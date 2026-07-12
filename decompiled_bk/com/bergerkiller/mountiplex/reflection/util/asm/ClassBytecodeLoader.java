/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.ClassClassPath;
import com.bergerkiller.mountiplex.dep.javassist.ClassPath;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassReader;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassWriter;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ClassBytecodeLoader {
    public static final ClassPath CLASSPATH = new CBLObjectClassPath();
    private static final ClassPath SYSTEM = new ClassClassPath(Object.class);

    public static InputStream getResourceAsStream(Class<?> clazz) {
        String filename = '/' + MPLType.getInternalName(clazz) + ".class";
        InputStream stream = ClassBytecodeLoader.cleanBytecode(() -> ClassBytecodeLoader.class.getResourceAsStream(filename));
        if (stream == null) {
            return new ByteArrayInputStream(ClassBytecodeLoader.generateMockByteCode(clazz));
        }
        return stream;
    }

    public static URL getResource(Class<?> clazz) {
        String filename = '/' + MPLType.getInternalName(clazz) + ".class";
        URL url = ClassBytecodeLoader.class.getResource(filename);
        return url != null ? url : ClassBytecodeLoader.generatedURL(clazz, filename);
    }

    public static URL getResourceGenerated(Class<?> clazz) {
        String filename = '/' + MPLType.getInternalName(clazz) + ".class";
        return ClassBytecodeLoader.generatedURL(clazz, filename);
    }

    private static URL generatedURL(final Class<?> clazz, String filename) {
        try {
            return new URL("bytecode", "mountiplex", 0, filename, new URLStreamHandler(){

                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(this, u){
                        final /* synthetic */ 1 this$0;
                        {
                            this.this$0 = this$0;
                            super(arg0);
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new ByteArrayInputStream(ClassBytecodeLoader.generateMockByteCode(clazz));
                        }

                        @Override
                        public void connect() throws IOException {
                        }
                    };
                }
            });
        }
        catch (MalformedURLException e) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Unhandled error parsing generated url for " + clazz, e);
            return null;
        }
    }

    private static byte[] generateMockByteCode(Class<?> clazz) {
        MethodVisitor mv;
        ClassWriter cw = new ClassWriter(0);
        Class<Object> superClass = clazz.getSuperclass();
        if (superClass == null) {
            superClass = Object.class;
        }
        if (clazz.isAssignableFrom(superClass)) {
            throw new IllegalStateException("Super class '" + MPLType.getName(superClass) + "' of '" + MPLType.getName(clazz) + "' implements itself!");
        }
        cw.visit(52, clazz.getModifiers(), MPLType.getInternalName(clazz), null, MPLType.getInternalName(superClass), MPLType.getInternalNames(clazz.getInterfaces()));
        for (Field field : clazz.getDeclaredFields()) {
            FieldVisitor fv = cw.visitField(field.getModifiers(), MPLType.getName(field), MPLType.getDescriptor(field.getType()), null, null);
            fv.visitEnd();
        }
        for (AccessibleObject accessibleObject : clazz.getDeclaredConstructors()) {
            mv = cw.visitMethod(((Constructor)accessibleObject).getModifiers(), "<init>", MPLType.getConstructorDescriptor(accessibleObject), null, MPLType.getInternalNames(((Constructor)accessibleObject).getExceptionTypes()));
            mv.visitEnd();
        }
        for (AccessibleObject accessibleObject : clazz.getDeclaredMethods()) {
            mv = cw.visitMethod(((Method)accessibleObject).getModifiers() & 0xFFFFFFBF, MPLType.getName((Method)accessibleObject), MPLType.getMethodDescriptor((Method)accessibleObject), null, MPLType.getInternalNames(((Method)accessibleObject).getExceptionTypes()));
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static InputStream cleanBytecode(Supplier<InputStream> streamSupplier) {
        try (InputStream stream = streamSupplier.get();){
            if (stream == null) {
                InputStream inputStream = null;
                return inputStream;
            }
            ClassReader reader = new ClassReader(stream);
            ClassWriter writer = new ClassWriter(0);
            reader.accept(new ClassVisitor(589824, writer){

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    return super.visitMethod(access & 0xFFFFFFBF, name, descriptor, signature, exceptions);
                }
            }, 0);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(writer.toByteArray());
            return byteArrayInputStream;
        }
        catch (IOException | IllegalArgumentException exception) {
            return streamSupplier.get();
        }
    }

    private static final class CBLObjectClassPath
    implements ClassPath {
        private final ClassLoader mountiplexClassLoader = ClassBytecodeLoader.class.getClassLoader();
        private final ClassLoader fallbackClassLoader = Thread.currentThread().getContextClassLoader();
        private final boolean fallbackIsMountiplex = this.mountiplexClassLoader == this.fallbackClassLoader;

        private CBLObjectClassPath() {
        }

        @Override
        public InputStream openClassfile(String classname) throws NotFoundException {
            Class<?> clazz;
            InputStream result;
            if (classname.startsWith("java.") && (result = SYSTEM.openClassfile(classname)) != null) {
                return result;
            }
            if (Resolver.canLoadClassPath(classname)) {
                String filename = classname.replace('.', '/') + ".class";
                result = ClassBytecodeLoader.cleanBytecode(() -> this.fallbackClassLoader.getResourceAsStream(filename));
                if (result != null) {
                    return result;
                }
                if (!this.fallbackIsMountiplex && (result = ClassBytecodeLoader.cleanBytecode(() -> this.mountiplexClassLoader.getResourceAsStream(filename))) != null) {
                    return result;
                }
                Class<?> generated = GeneratorClassLoader.findGeneratedClass(classname);
                if (generated != null) {
                    return new ByteArrayInputStream(ClassBytecodeLoader.generateMockByteCode(generated));
                }
                result = SYSTEM.openClassfile(classname);
                if (result != null) {
                    return result;
                }
            }
            try {
                clazz = Resolver.getClassByExactName(classname);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
            return new ByteArrayInputStream(ClassBytecodeLoader.generateMockByteCode(clazz));
        }

        @Override
        public URL find(String classname) {
            URL result;
            if (classname.startsWith("java.") && (result = SYSTEM.find(classname)) != null) {
                return result;
            }
            String filename = classname.replace('.', '/') + ".class";
            if (Resolver.canLoadClassPath(classname)) {
                result = this.fallbackClassLoader.getResource(filename);
                if (result != null) {
                    return result;
                }
                if (!this.fallbackIsMountiplex && (result = this.mountiplexClassLoader.getResource(filename)) != null) {
                    return result;
                }
                Class<?> generated = GeneratorClassLoader.findGeneratedClass(classname);
                if (generated != null) {
                    return ClassBytecodeLoader.getResourceGenerated(generated);
                }
                result = SYSTEM.find(classname);
                if (result != null) {
                    return result;
                }
            }
            try {
                Class<?> clazz = Resolver.getClassByExactName(classname);
                return ClassBytecodeLoader.generatedURL(clazz, filename);
            }
            catch (Throwable throwable) {
                return null;
            }
        }
    }
}

