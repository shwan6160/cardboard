/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassWriter;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Type;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLTypeHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class MPLType {
    private static final MPLTypeHelper REMAPPING_DISABLED_HELPER = MPLType.generateNoRemappingHelper();
    private static final MPLTypeHelper REMAPPING_ENABLED_HELPER = new DefaultMPLTypeHelper();
    private static MPLTypeHelper helper = REMAPPING_DISABLED_HELPER;

    public static void setRemappingEnabled(boolean enabled) {
        helper = enabled ? REMAPPING_ENABLED_HELPER : REMAPPING_DISABLED_HELPER;
    }

    public static String getInternalName(Class<?> clazz) {
        return helper.getClassName(clazz).replace('.', '/');
    }

    public static String[] getInternalNames(Class<?>[] types) {
        if (types == null || types.length == 0) {
            return null;
        }
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; ++i) {
            names[i] = MPLType.getInternalName(types[i]);
        }
        return names;
    }

    public static String getDescriptor(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder();
        MPLType.appendDescriptor(clazz, stringBuilder);
        return stringBuilder.toString();
    }

    public static String getConstructorDescriptor(Constructor<?> constructor) {
        Class<?>[] parameters;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for (Class<?> parameter : parameters = constructor.getParameterTypes()) {
            MPLType.appendDescriptor(parameter, stringBuilder);
        }
        return stringBuilder.append(")V").toString();
    }

    public static String getMethodDescriptor(Method method) {
        return MPLType.getMethodDescriptor(method.getReturnType(), method.getParameterTypes());
    }

    public static String getInternalMethodDescriptor(MethodDeclaration dec) {
        Class<?> returnType = dec.returnType.type;
        Class[] paramTypes = new Class[dec.parameters.parameters.length];
        for (int i = 0; i < paramTypes.length; ++i) {
            paramTypes[i] = dec.parameters.parameters[i].type.type;
        }
        return MPLType.getMethodDescriptor(returnType, paramTypes);
    }

    public static String getMethodDescriptor(Class<?> returnType, Class<?>[] parameterTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for (Class<?> parameter : parameterTypes) {
            MPLType.appendDescriptor(parameter, stringBuilder);
        }
        stringBuilder.append(')');
        MPLType.appendDescriptor(returnType, stringBuilder);
        return stringBuilder.toString();
    }

    private static void appendDescriptor(Class<?> clazz, StringBuilder stringBuilder) {
        Class<?> currentClass = clazz;
        while (currentClass.isArray()) {
            stringBuilder.append('[');
            currentClass = currentClass.getComponentType();
        }
        if (currentClass.isPrimitive()) {
            int descriptor;
            if (currentClass == Integer.TYPE) {
                descriptor = 73;
            } else if (currentClass == Void.TYPE) {
                descriptor = 86;
            } else if (currentClass == Boolean.TYPE) {
                descriptor = 90;
            } else if (currentClass == Byte.TYPE) {
                descriptor = 66;
            } else if (currentClass == Character.TYPE) {
                descriptor = 67;
            } else if (currentClass == Short.TYPE) {
                descriptor = 83;
            } else if (currentClass == Double.TYPE) {
                descriptor = 68;
            } else if (currentClass == Float.TYPE) {
                descriptor = 70;
            } else if (currentClass == Long.TYPE) {
                descriptor = 74;
            } else {
                throw new AssertionError();
            }
            stringBuilder.append((char)descriptor);
        } else {
            stringBuilder.append('L');
            String name = helper.getClassName(currentClass);
            int nameLength = name.length();
            for (int i = 0; i < nameLength; ++i) {
                char car = name.charAt(i);
                stringBuilder.append(car == '.' ? (char)'/' : (char)car);
            }
            stringBuilder.append(';');
        }
    }

    public static int getOpcode(Class<?> clazz, int opcode) {
        return MPLType.getType(clazz).getOpcode(opcode);
    }

    public static void visitBoxVariable(MethodVisitor mv, Class<?> primType) {
        Class<?> boxedType;
        if (primType == Void.TYPE) {
            mv.visitInsn(1);
        } else if (primType.isPrimitive() && (boxedType = BoxedType.getBoxedType(primType)) != null) {
            mv.visitMethodInsn(184, MPLType.getInternalName(boxedType), "valueOf", "(" + MPLType.getDescriptor(primType) + ")" + MPLType.getDescriptor(boxedType), false);
        }
    }

    public static void visitUnboxVariable(MethodVisitor mv, Class<?> primType) {
        if (primType == Void.TYPE) {
            mv.visitInsn(1);
        } else if (primType.isPrimitive()) {
            mv.visitMethodInsn(182, MPLType.getInternalName(BoxedType.getBoxedType(primType)), primType.getSimpleName() + "Value", "()" + MPLType.getDescriptor(primType), false);
        }
    }

    public static int visitVarILoad(MethodVisitor mv, int registerInitial, Class<?> type) {
        Type asm_type = Type.getType(type);
        mv.visitVarInsn(asm_type.getOpcode(21), registerInitial);
        return registerInitial + asm_type.getSize();
    }

    public static int visitVarILoad(MethodVisitor mv, int registerInitial, Class<?> ... types) {
        int register = registerInitial;
        for (Class<?> type : types) {
            register = MPLType.visitVarILoad(mv, register, type);
        }
        return register;
    }

    public static int visitVarILoad(MethodVisitor mv, int registerInitial, ParameterListDeclaration parameters) {
        int register = registerInitial;
        for (ParameterDeclaration param : parameters.parameters) {
            register = MPLType.visitVarILoad(mv, register, param.type.type);
        }
        return register;
    }

    public static int visitVarILoadAndBox(MethodVisitor mv, int registerInitial, Class<?> type) {
        int register = MPLType.visitVarILoad(mv, registerInitial, type);
        MPLType.visitBoxVariable(mv, type);
        return register;
    }

    public static int visitVarIStore(MethodVisitor mv, int registerInitial, Class<?> type) {
        Type asm_type = Type.getType(type);
        mv.visitVarInsn(asm_type.getOpcode(54), registerInitial);
        return registerInitial + asm_type.getSize();
    }

    public static int visitVarIStore(MethodVisitor mv, int registerInitial, Class<?> ... types) {
        int register = registerInitial;
        for (Class<?> type : types) {
            register = MPLType.visitVarIStore(mv, register, type);
        }
        return register;
    }

    public static Type getReturnType(Method method) {
        return MPLType.getType(method.getReturnType());
    }

    public static Type[] getArgumentTypes(Method method) {
        Class<?>[] classes = method.getParameterTypes();
        Type[] types = new Type[classes.length];
        for (int i = classes.length - 1; i >= 0; --i) {
            types[i] = MPLType.getType(classes[i]);
        }
        return types;
    }

    public static Type[] getTypes(Class<?> ... classes) {
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; ++i) {
            types[i] = MPLType.getType(classes[i]);
        }
        return types;
    }

    public static Type getType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return Type.INT_TYPE;
            }
            if (clazz == Void.TYPE) {
                return Type.VOID_TYPE;
            }
            if (clazz == Boolean.TYPE) {
                return Type.BOOLEAN_TYPE;
            }
            if (clazz == Byte.TYPE) {
                return Type.BYTE_TYPE;
            }
            if (clazz == Character.TYPE) {
                return Type.CHAR_TYPE;
            }
            if (clazz == Short.TYPE) {
                return Type.SHORT_TYPE;
            }
            if (clazz == Double.TYPE) {
                return Type.DOUBLE_TYPE;
            }
            if (clazz == Float.TYPE) {
                return Type.FLOAT_TYPE;
            }
            if (clazz == Long.TYPE) {
                return Type.LONG_TYPE;
            }
            throw new AssertionError();
        }
        return Type.getType(MPLType.getDescriptor(clazz));
    }

    public static String getName(Class<?> clazz) {
        return helper.getClassName(clazz);
    }

    public static String getName(Method method) {
        return Resolver.resolveCompiledMethodName(method.getDeclaringClass(), helper.getMethodName(method), method.getParameterTypes());
    }

    public static String getName(Field field) {
        return Resolver.resolveCompiledFieldName(field.getDeclaringClass(), helper.getFieldName(field));
    }

    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?> ... parameterTypes) throws NoSuchMethodException, SecurityException {
        return helper.getDeclaredMethod(clazz, name, parameterTypes);
    }

    public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
        return helper.getDeclaredField(clazz, name);
    }

    public static Class<?> getClassByName(String name, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            try {
                return helper.getClassByName(name, initialize, classLoader);
            }
            catch (IllegalStateException is_ex) {
                if ("zip file closed".equals(is_ex.getMessage()) && classLoader instanceof URLClassLoader) {
                    throw new LoaderClosedException();
                }
                throw new ClassNotFoundException("Failed to load class " + name, is_ex);
            }
        }
        catch (ClassNotFoundException ex) {
            Class<?> generated = GeneratorClassLoader.findGeneratedClass(name);
            if (generated != null) {
                return generated;
            }
            throw ex;
        }
    }

    public static Class<?> getClassByName(String name) throws ClassNotFoundException {
        try {
            try {
                return helper.getClassByName(name, false, MPLType.class.getClassLoader());
            }
            catch (IllegalStateException is_ex) {
                if ("zip file closed".equals(is_ex.getMessage())) {
                    throw new LoaderClosedException();
                }
                throw new ClassNotFoundException("Failed to load class " + name, is_ex);
            }
        }
        catch (ClassNotFoundException ex) {
            Class<?> generated = GeneratorClassLoader.findGeneratedClass(name);
            if (generated != null) {
                return generated;
            }
            throw ex;
        }
    }

    private static MPLTypeHelper generateNoRemappingHelper() {
        String interfaceName = MPLTypeHelper.class.getName().replace('.', '/');
        String internalName = MPLType.class.getName().replace('.', '/') + "$HelperImpl";
        String signature = "Ljava/lang/Object;L" + interfaceName + ";";
        ClassWriter cw = new ClassWriter(0);
        cw.visit(52, 9, internalName, signature, "java/lang/Object", new String[]{interfaceName});
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(1, "getClassName", "(Ljava/lang/Class;)Ljava/lang/String;", "(Ljava/lang/Class<*>;)Ljava/lang/String;", null);
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(182, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        mv.visitInsn(176);
        mv.visitMaxs(1, 2);
        mv.visitEnd();
        mv = cw.visitMethod(1, "getMethodName", "(Ljava/lang/reflect/Method;)Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(182, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
        mv.visitInsn(176);
        mv.visitMaxs(1, 2);
        mv.visitEnd();
        mv = cw.visitMethod(1, "getFieldName", "(Ljava/lang/reflect/Field;)Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(182, "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
        mv.visitInsn(176);
        mv.visitMaxs(1, 2);
        mv.visitEnd();
        mv = cw.visitMethod(129, "getDeclaredMethod", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", "(Ljava/lang/Class<*>;Ljava/lang/String;[Ljava/lang/Class<*>;)Ljava/lang/reflect/Method;", new String[]{"java/lang/NoSuchMethodException", "java/lang/SecurityException"});
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(25, 2);
        mv.visitVarInsn(25, 3);
        mv.visitMethodInsn(182, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        mv.visitInsn(176);
        mv.visitMaxs(3, 4);
        mv.visitEnd();
        mv = cw.visitMethod(1, "getDeclaredField", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", "(Ljava/lang/Class<*>;Ljava/lang/String;)Ljava/lang/reflect/Field;", new String[]{"java/lang/NoSuchFieldException", "java/lang/SecurityException"});
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(25, 2);
        mv.visitMethodInsn(182, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
        mv.visitInsn(176);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
        mv = cw.visitMethod(1, "getClassByName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class<*>;", new String[]{"java/lang/ClassNotFoundException"});
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(21, 2);
        mv.visitVarInsn(25, 3);
        mv.visitMethodInsn(184, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
        mv.visitInsn(176);
        mv.visitMaxs(3, 4);
        mv.visitEnd();
        cw.visitEnd();
        GeneratorClassLoader loader = GeneratorClassLoader.get(MPLType.class.getClassLoader());
        Class<?> helperImplType = loader.createClassFromBytecode(MPLType.class.getName() + "$HelperImpl", cw.toByteArray(), null, false);
        try {
            return (MPLTypeHelper)helperImplType.newInstance();
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    public static final class LoaderClosedException
    extends ClassNotFoundException {
        private static final long serialVersionUID = -2465209759941212720L;

        public LoaderClosedException() {
            super("This ClassLoader is closed");
        }
    }

    private static final class DefaultMPLTypeHelper
    implements MPLTypeHelper {
        private DefaultMPLTypeHelper() {
        }

        @Override
        public String getClassName(Class<?> clazz) {
            return clazz.getName();
        }

        @Override
        public String getMethodName(Method method) {
            return method.getName();
        }

        @Override
        public String getFieldName(Field field) {
            return field.getName();
        }

        @Override
        public Method getDeclaredMethod(Class<?> clazz, String name, Class<?> ... parameterTypes) throws NoSuchMethodException, SecurityException {
            return clazz.getDeclaredMethod(name, parameterTypes);
        }

        @Override
        public Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException, SecurityException {
            return clazz.getDeclaredField(name);
        }

        @Override
        public Class<?> getClassByName(String name, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException {
            return Class.forName(name, initialize, classLoader);
        }
    }
}

