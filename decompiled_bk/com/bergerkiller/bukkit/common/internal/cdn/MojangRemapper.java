/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.LinkedListMultimap
 *  com.google.common.collect.ListMultimap
 */
package com.bergerkiller.bukkit.common.internal.cdn;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangMappings;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.google.common.collect.BiMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class MojangRemapper {
    private static final ClassRemapper[] NO_REMAPPERS = new ClassRemapper[0];
    private final Map<Class<?>, ClassRemapper> remappersByDeclaringClassName = new IdentityHashMap();
    private Map<Class<?>, ClassRemapper[]> recurseRemappersByDeclaringClassName = new IdentityHashMap();
    private MojangMappings mappings = null;
    private final LazyClassLookup classLookup = new LazyClassLookup();

    public String remapFieldName(Class<?> declaringClass, String fieldName, String defaultName) {
        for (ClassRemapper remapper : this.remappersFor(declaringClass)) {
            String remapped = (String)remapper.fields_name_to_obfuscated.get((Object)fieldName);
            if (remapped == null) continue;
            return remapped;
        }
        return defaultName;
    }

    public String remapFieldNameReverse(Class<?> declaringClass, String fieldName, String defaultName) {
        for (ClassRemapper remapper : this.remappersFor(declaringClass)) {
            String remapped = (String)remapper.fields_obfuscated_to_name.get((Object)fieldName);
            if (remapped == null) continue;
            return remapped;
        }
        return defaultName;
    }

    public String remapMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, String defaultName) {
        for (ClassRemapper remapper : this.remappersFor(declaringClass)) {
            for (MethodDetails method : remapper.methods_by_name.get((Object)methodName)) {
                if (!method.canAcceptParameters(parameterTypes)) continue;
                return method.name_obfuscated;
            }
        }
        return defaultName;
    }

    public String remapMethodNameReverse(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, String defaultName) {
        for (ClassRemapper remapper : this.remappersFor(declaringClass)) {
            for (MethodDetails method : remapper.methods_by_obfuscated.get((Object)methodName)) {
                if (!method.canAcceptParameters(parameterTypes)) continue;
                return method.name;
            }
        }
        return defaultName;
    }

    public void removeMethodMapping(Class<?> declaringClass, String methodName, Class<?> ... parameterTypes) {
        for (ClassRemapper remapper : this.remappersFor(declaringClass)) {
            remapper.removeMethodMapping(methodName, parameterTypes);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ClassRemapper[] remappersFor(Class<?> declaringClass) {
        ClassRemapper[] remappers = this.recurseRemappersByDeclaringClassName.get(declaringClass);
        if (remappers != null) {
            return remappers;
        }
        ClassRemapper remapper = this.initRemapper(declaringClass);
        if (remapper == null) {
            return NO_REMAPPERS;
        }
        ClassRemapper[] result = remapper.recurse(this::initRemapper);
        MojangRemapper mojangRemapper = this;
        synchronized (mojangRemapper) {
            IdentityHashMap newRecurseMap = new IdentityHashMap(this.recurseRemappersByDeclaringClassName);
            newRecurseMap.put(declaringClass, result);
            this.recurseRemappersByDeclaringClassName = newRecurseMap;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ClassRemapper initRemapper(Class<?> declaringClass) {
        ClassRemapper remapper;
        MojangMappings.ClassMappings mappings = this.mappings.forClassIfExists(declaringClass.getName());
        if (mappings == null) {
            return null;
        }
        Map<Class<?>, ClassRemapper> map = this.remappersByDeclaringClassName;
        synchronized (map) {
            remapper = this.remappersByDeclaringClassName.get(declaringClass);
        }
        if (remapper == null) {
            remapper = ClassRemapper.create(mappings, this.classLookup);
            map = this.remappersByDeclaringClassName;
            synchronized (map) {
                ClassRemapper prev = this.remappersByDeclaringClassName.putIfAbsent(declaringClass, remapper);
                if (prev != null) {
                    remapper = prev;
                }
            }
        }
        return remapper;
    }

    protected synchronized void loadMappings(MojangMappings mappings) {
        this.remappersByDeclaringClassName.clear();
        this.recurseRemappersByDeclaringClassName = new IdentityHashMap();
        this.classLookup.reset();
        for (MojangMappings.ClassMappings classMappings : mappings.classes()) {
            this.classLookup.add(classMappings.name);
            for (MojangMappings.MethodSignature method : classMappings.methods) {
                this.classLookup.add(method.returnType.name);
                for (MojangMappings.ClassSignature sig : method.parameterTypes) {
                    this.classLookup.add(sig.name);
                }
            }
        }
        this.mappings = mappings;
    }

    public static MojangRemapper load(String minecraftVersion, ClassPathResolver classPathResolver) {
        MojangMappings mojangMappings = MojangMappings.fromCacheOrDownload(minecraftVersion);
        return MojangRemapper.create(mojangMappings, classPathResolver);
    }

    public static MojangRemapper create(MojangMappings mojangMappings, ClassPathResolver classPathResolver) {
        MojangMappings mappings = mojangMappings.translateClassNames(classPathResolver::resolveClassPath);
        MojangRemapper remapper = new MojangRemapper();
        remapper.loadMappings(mappings);
        return remapper;
    }

    private static final class LazyClassLookup {
        private final Map<String, Supplier<Class<?>>> map = new ConcurrentHashMap();

        private LazyClassLookup() {
        }

        public void reset() {
            this.map.clear();
            for (Class<?> type : BoxedType.getUnboxedTypes()) {
                this.map.put(type.getSimpleName(), LogicUtil.constantSupplier(type));
                if (type == Void.TYPE) continue;
                String nameTmp = type.getSimpleName();
                Class<?> typeTmp = type;
                for (int dims = 0; dims < 10; ++dims) {
                    nameTmp = nameTmp + "[]";
                    typeTmp = LogicUtil.getArrayType(typeTmp);
                    this.map.put(nameTmp, LogicUtil.constantSupplier(typeTmp));
                }
            }
            for (Class<?> type : BoxedType.getBoxedTypes()) {
                this.map.put(type.getName(), LogicUtil.constantSupplier(type));
            }
            this.map.put(String.class.getName(), LogicUtil.constantSupplier(String.class));
            this.map.put(List.class.getName(), LogicUtil.constantSupplier(List.class));
            this.map.put(Map.class.getName(), LogicUtil.constantSupplier(Map.class));
        }

        public Class<?> get(String name) {
            return (Class)this.map.getOrDefault(name, LogicUtil.nullSupplier()).get();
        }

        public void add(String name) {
            this.map.computeIfAbsent(name, this::createNewLookup);
        }

        private GeneratingLookup createNewLookup(String name) {
            if (name.endsWith("[]")) {
                int numDims = 0;
                String tmp = name;
                do {
                    tmp = tmp.substring(0, tmp.length() - 2);
                    ++numDims;
                } while (tmp.endsWith("[]"));
                return new GeneratingArrayTypeLookup(this.map, name, numDims);
            }
            return new GeneratingLookup(this.map, name);
        }

        private static final class GeneratingArrayTypeLookup
        extends GeneratingLookup {
            private final int numDims;

            public GeneratingArrayTypeLookup(Map<String, Supplier<Class<?>>> map, String name, int numDims) {
                super(map, name);
                this.numDims = numDims;
            }

            @Override
            public Class<?> load(String className) {
                Class<?> foundClass = super.load(className = className.substring(0, className.length() - this.numDims * 2));
                if (foundClass != null) {
                    foundClass = LogicUtil.getArrayType(foundClass, this.numDims);
                }
                return foundClass;
            }
        }

        private static class GeneratingLookup
        implements Supplier<Class<?>> {
            private final Map<String, Supplier<Class<?>>> map;
            private final String name;

            public GeneratingLookup(Map<String, Supplier<Class<?>>> map, String name) {
                this.map = map;
                this.name = name;
            }

            public Class<?> load(String className) {
                try {
                    return MPLType.getClassByName(className, false, this.getClass().getClassLoader());
                }
                catch (ClassNotFoundException e) {
                    return null;
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "An error occurred loading server class " + className, t);
                    return null;
                }
            }

            @Override
            public final Class<?> get() {
                Class<?> foundClass = this.load(this.name);
                if (foundClass != null) {
                    this.map.put(this.name, LogicUtil.constantSupplier(foundClass));
                } else {
                    this.map.remove(this.name);
                }
                return foundClass;
            }
        }
    }

    private static class ClassRemapper {
        public final Class<?> type;
        public final BiMap<String, String> fields_obfuscated_to_name;
        public final BiMap<String, String> fields_name_to_obfuscated;
        public final ListMultimap<String, MethodDetails> methods_by_name = LinkedListMultimap.create((int)64);
        public final ListMultimap<String, MethodDetails> methods_by_obfuscated = LinkedListMultimap.create((int)64);

        private ClassRemapper(MojangMappings.ClassMappings mappings, Class<?> type) {
            this.type = type;
            this.fields_name_to_obfuscated = mappings.fields_name_to_obfuscated;
            this.fields_obfuscated_to_name = mappings.fields_obfuscated_to_name;
        }

        public ClassRemapper[] recurse(Function<Class<?>, ClassRemapper> remapperLookup) {
            return (ClassRemapper[])ReflectionUtil.getAllClassesAndInterfaces(this.type).map(remapperLookup).filter(Objects::nonNull).toArray(ClassRemapper[]::new);
        }

        public static ClassRemapper create(MojangMappings.ClassMappings mappings, LazyClassLookup classLookup) {
            Class<?> type = classLookup.get(mappings.name);
            if (type != null) {
                ClassRemapper remapper = new ClassRemapper(mappings, type);
                for (MojangMappings.MethodSignature sig : mappings.methods) {
                    MethodDetails details = MethodDetails.create(sig, classLookup);
                    if (details == null) continue;
                    remapper.methods_by_name.put((Object)details.name, (Object)details);
                    remapper.methods_by_obfuscated.put((Object)details.name_obfuscated, (Object)details);
                }
                return remapper;
            }
            return null;
        }

        public void removeMethodMapping(String methodName, Class<?>[] parameterTypes) {
            Iterator iter = this.methods_by_name.get((Object)methodName).iterator();
            while (iter.hasNext()) {
                MethodDetails details = (MethodDetails)iter.next();
                if (!details.canAcceptParameters(parameterTypes)) continue;
                iter.remove();
                this.methods_by_obfuscated.remove((Object)details.name_obfuscated, (Object)details);
            }
        }
    }

    private static class MethodDetails {
        private static final Class<?>[] NO_ARGS = new Class[0];
        public final String name;
        public final String name_obfuscated;
        public final Class<?> returnType;
        public final Class<?>[] parameterTypes;

        public MethodDetails(MojangMappings.MethodSignature sig, Class<?> returnType, Class<?>[] parameterTypes) {
            this.name = sig.name;
            this.name_obfuscated = sig.name_obfuscated;
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        public boolean canAcceptParameters(Class<?>[] parameterTypes) {
            int count = parameterTypes.length;
            if (this.parameterTypes.length != count) {
                return false;
            }
            for (int i = 0; i < count; ++i) {
                if (this.parameterTypes[i].isAssignableFrom(parameterTypes[i])) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.returnType.getName()).append(' ');
            str.append(this.name).append(':').append(this.name_obfuscated);
            str.append("(");
            boolean first = true;
            for (Class<?> type : this.parameterTypes) {
                if (first) {
                    first = false;
                } else {
                    str.append(", ");
                }
                str.append(type.getName());
            }
            str.append(')');
            return str.toString();
        }

        public static MethodDetails create(MojangMappings.MethodSignature sig, LazyClassLookup classLookup) {
            Class<?> returnType = classLookup.get(sig.returnType.name);
            if (returnType == null) {
                return null;
            }
            if (sig.parameterTypes.isEmpty()) {
                return new MethodDetails(sig, returnType, NO_ARGS);
            }
            Class[] arguments = new Class[sig.parameterTypes.size()];
            for (int i = 0; i < arguments.length; ++i) {
                Class<?> argType = classLookup.get(sig.parameterTypes.get((int)i).name);
                if (argType == null) {
                    return null;
                }
                arguments[i] = argType;
            }
            return new MethodDetails(sig, returnType, arguments);
        }
    }
}

