/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.InputTypeMap;
import com.bergerkiller.mountiplex.reflection.util.signature.FieldSignature;
import com.bergerkiller.mountiplex.reflection.util.signature.MethodSignature;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Remapping {
    public final Class<?> declaringClass;

    public static Lookup createLookup() {
        return new Lookup(new LookupTable());
    }

    public Remapping(Class<?> declaringClass) {
        this.declaringClass = declaringClass;
    }

    public final Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    public abstract Object getKey();

    public static class Lookup
    implements Cloneable {
        private LookupTable table;

        private Lookup(LookupTable table) {
            this.table = table;
        }

        private void makeMutable() {
            if (this.table.isReadOnly()) {
                this.table = this.table.copy(false);
            }
        }

        public void assign(Lookup lookup) {
            this.table = lookup.table;
        }

        public void addRemapping(Remapping remapping) {
            this.makeMutable();
            this.table.addRemapping(remapping);
        }

        public FieldRemapping find(FieldDeclaration declaration) {
            return this.find(declaration.getResolver().getDeclaredClass(), new FieldSignature(declaration.name.value()));
        }

        public FieldRemapping find(Class<?> type, FieldSignature signature) {
            if (type == null) {
                return null;
            }
            for (ClassRemapping remapping : this.table.getAll(type)) {
                FieldRemapping field = remapping.find(signature);
                if (field == null) continue;
                return field;
            }
            return null;
        }

        public MethodRemapping find(MethodDeclaration declaration) {
            return this.find(declaration.getDeclaringClass(), new MethodSignature(declaration.name.value(), declaration.parameters.toParamArray()));
        }

        public MethodRemapping find(Class<?> type, MethodSignature signature) {
            if (type == null) {
                return null;
            }
            for (ClassRemapping remapping : this.table.getAll(type)) {
                MethodRemapping method = remapping.find(signature);
                if (method == null) continue;
                return method;
            }
            return null;
        }

        public Collection<Remapping> getAllStoredRemappings() {
            return this.table.getAllStoredRemappings();
        }

        public Lookup clone() {
            return new Lookup(this.table.copy(true));
        }
    }

    private static final class LookupTable {
        private final Object lock;
        private final boolean readOnly;
        private final InputTypeMap<ClassRemapping> remappingsByDeclaringClass;

        public LookupTable() {
            this.lock = new Object();
            this.readOnly = true;
            this.remappingsByDeclaringClass = new InputTypeMap();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private LookupTable(LookupTable original, boolean readOnly) {
            this.readOnly = readOnly;
            if (original.readOnly && readOnly) {
                this.lock = original.lock;
                this.remappingsByDeclaringClass = original.remappingsByDeclaringClass;
            } else {
                this.lock = new Object();
                Object object = original.lock;
                synchronized (object) {
                    this.remappingsByDeclaringClass = original.remappingsByDeclaringClass.clone();
                }
            }
        }

        public boolean isReadOnly() {
            return this.readOnly;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Collection<ClassRemapping> getAll(Class<?> type) {
            Object object = this.lock;
            synchronized (object) {
                return this.remappingsByDeclaringClass.getAll(type);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Collection<Remapping> getAllStoredRemappings() {
            Object object = this.lock;
            synchronized (object) {
                return this.remappingsByDeclaringClass.values().stream().flatMap(a -> a.getAll().stream()).collect(Collectors.toList());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addRemapping(Remapping remapping) {
            Object object = this.lock;
            synchronized (object) {
                ClassRemapping classRemapping = (ClassRemapping)this.remappingsByDeclaringClass.get(remapping.getDeclaringClass());
                if (classRemapping == null) {
                    classRemapping = new ClassRemapping(remapping.getDeclaringClass());
                }
                classRemapping = classRemapping.withRemapping(remapping);
                this.remappingsByDeclaringClass.put(classRemapping.getDeclaringClass(), classRemapping);
            }
        }

        public LookupTable copy(boolean readOnly) {
            return new LookupTable(this, readOnly);
        }
    }

    public static class MethodRemapping
    extends Remapping {
        public final MethodSignature signature;
        public final Method method;
        public final MethodDeclaration declaration;

        public MethodRemapping(MethodDeclaration declaration) {
            super(declaration.getResolver().getDeclaredClass());
            this.signature = new MethodSignature(declaration.name.firstReal(), declaration.parameters.toParamArray());
            this.method = declaration.method;
            this.declaration = declaration;
            if (this.method == null) {
                throw new IllegalStateException("Method for remapping is not resolved");
            }
        }

        @Override
        public Object getKey() {
            return this.signature;
        }

        public String toString() {
            return "MethodRemapping { " + this.declaration.toString() + " }";
        }
    }

    public static class FieldRemapping
    extends Remapping {
        public final FieldSignature signature;
        public final Field field;
        public final FieldDeclaration declaration;

        public FieldRemapping(FieldDeclaration declaration) {
            super(declaration.getResolver().getDeclaredClass());
            this.signature = new FieldSignature(declaration.name.firstReal());
            this.field = declaration.field;
            this.declaration = declaration;
            if (this.field == null) {
                throw new IllegalStateException("Field for remapping is not resolved");
            }
        }

        @Override
        public Object getKey() {
            return this.signature;
        }

        public String toString() {
            return "FieldRemapping { " + this.declaration.toString() + " }";
        }
    }

    public static class ClassRemapping
    extends Remapping {
        private final Map<Object, Remapping> remappings;

        public ClassRemapping(Class<?> declaringClass) {
            super(declaringClass);
            this.remappings = Collections.emptyMap();
        }

        private ClassRemapping(ClassRemapping base, Remapping newRemapping) {
            super(base.getDeclaringClass());
            if (base.remappings.isEmpty()) {
                this.remappings = Collections.singletonMap(newRemapping.getKey(), newRemapping);
            } else {
                this.remappings = new HashMap<Object, Remapping>(base.remappings);
                this.remappings.put(newRemapping.getKey(), newRemapping);
            }
        }

        public ClassRemapping withRemapping(Remapping newRemapping) {
            return new ClassRemapping(this, newRemapping);
        }

        @Override
        public Object getKey() {
            return this.getDeclaringClass();
        }

        public FieldRemapping find(FieldSignature signature) {
            return (FieldRemapping)this.remappings.get(signature);
        }

        public MethodRemapping find(MethodSignature signature) {
            return (MethodRemapping)this.remappings.get(signature);
        }

        public Collection<Remapping> getAll() {
            return this.remappings.values();
        }
    }
}

