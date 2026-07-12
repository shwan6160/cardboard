/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.AnnotationVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Attribute;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassReader;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassWriter;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Type;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

public class ASMUtil {
    private static final int ASM_VERSION = 589824;

    public static byte[] removeClassMethods(byte[] classData, final Set<String> methodsSignatures) {
        ClassReader cr = new ClassReader(classData);
        final ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(new ClassVisitor(589824){

            @Override
            public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                return cw.visitAnnotation(arg0, arg1);
            }

            @Override
            public void visitAttribute(Attribute arg0) {
                cw.visitAttribute(arg0);
            }

            @Override
            public void visitEnd() {
                cw.visitEnd();
            }

            @Override
            public FieldVisitor visitField(int arg0, String arg1, String arg2, String arg3, Object arg4) {
                return cw.visitField(arg0, arg1, arg2, arg3, arg4);
            }

            @Override
            public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
                cw.visitInnerClass(arg0, arg1, arg2, arg3);
            }

            @Override
            public MethodVisitor visitMethod(int arg0, String name, String signature, String arg3, String[] arg4) {
                if (methodsSignatures.contains(name + signature)) {
                    return null;
                }
                return cw.visitMethod(arg0, name, signature, arg3, arg4);
            }

            @Override
            public void visitOuterClass(String arg0, String arg1, String arg2) {
                cw.visitOuterClass(arg0, arg1, arg2);
            }

            @Override
            public void visitSource(String arg0, String arg1) {
                cw.visitSource(arg0, arg1);
            }

            @Override
            public void visit(int arg0, int arg1, String arg2, String arg3, String arg4, String[] arg5) {
                cw.visit(arg0, arg1, arg2, arg3, arg4, arg5);
            }
        }, 0);
        return cw.toByteArray();
    }

    public static final Collection<Class<?>> findUsedTypes(Class<?> type) {
        final FoundTypeSet result = new FoundTypeSet(type.getClassLoader());
        try {
            InputStream classStream = type.getResourceAsStream(type.getSimpleName() + ".class");
            if (classStream == null) {
                return result;
            }
            ClassReader reader = new ClassReader(classStream);
            reader.accept(new ClassVisitor(589824){

                @Override
                public void visit(int ver, int acc, String name, String sig, String supername, String[] interfaces) {
                    result.add(Type.getObjectType(name));
                    if (supername != null) {
                        result.add(Type.getObjectType(supername));
                    }
                    if (interfaces != null) {
                        for (String iif : interfaces) {
                            result.add(Type.getObjectType(iif));
                        }
                    }
                }

                @Override
                public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                    return null;
                }

                @Override
                public void visitAttribute(Attribute arg0) {
                }

                @Override
                public void visitEnd() {
                }

                @Override
                public FieldVisitor visitField(int arg0, String name, String desc, String sig, Object value) {
                    result.add(Type.getType(desc));
                    return null;
                }

                @Override
                public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
                }

                @Override
                public MethodVisitor visitMethod(int arg0, String name, String desc, String sig, String[] exceptions) {
                    for (Type t : Type.getArgumentTypes(desc)) {
                        result.add(t);
                    }
                    result.add(Type.getReturnType(desc));
                    return new MethodVisitor(this, 589824){
                        final /* synthetic */ 2 this$0;
                        {
                            this.this$0 = this$0;
                            super(arg0);
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                            return null;
                        }

                        @Override
                        public AnnotationVisitor visitAnnotationDefault() {
                            return null;
                        }

                        @Override
                        public void visitAttribute(Attribute arg0) {
                        }

                        @Override
                        public void visitCode() {
                        }

                        @Override
                        public void visitEnd() {
                        }

                        @Override
                        public void visitFieldInsn(int arg0, String owner, String name, String desc) {
                            result.add(Type.getObjectType(owner));
                            result.add(Type.getType(desc));
                        }

                        @Override
                        public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
                        }

                        @Override
                        public void visitIincInsn(int arg0, int arg1) {
                        }

                        @Override
                        public void visitInsn(int arg0) {
                        }

                        @Override
                        public void visitIntInsn(int arg0, int arg1) {
                        }

                        @Override
                        public void visitJumpInsn(int arg0, Label arg1) {
                        }

                        @Override
                        public void visitLabel(Label arg0) {
                        }

                        @Override
                        public void visitLdcInsn(Object arg0) {
                            if (arg0 instanceof Type) {
                                result.add((Type)arg0);
                            }
                        }

                        @Override
                        public void visitLineNumber(int arg0, Label arg1) {
                        }

                        @Override
                        public void visitLocalVariable(String name, String desc, String sig, Label arg3, Label arg4, int arg5) {
                            result.add(Type.getType(desc));
                        }

                        @Override
                        public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
                        }

                        @Override
                        public void visitMaxs(int arg0, int arg1) {
                        }

                        @Override
                        public void visitMethodInsn(int arg0, String owner, String name, String desc) {
                            result.add(Type.getObjectType(owner));
                            for (Type t : Type.getArgumentTypes(desc)) {
                                result.add(t);
                            }
                            result.add(Type.getReturnType(desc));
                        }

                        @Override
                        public void visitMultiANewArrayInsn(String arg0, int arg1) {
                            result.add(Type.getType(arg0));
                        }

                        @Override
                        public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
                            return null;
                        }

                        @Override
                        public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label ... arg3) {
                        }

                        @Override
                        public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
                        }

                        @Override
                        public void visitTypeInsn(int arg0, String arg1) {
                            result.add(Type.getObjectType(arg1));
                        }

                        @Override
                        public void visitVarInsn(int arg0, int arg1) {
                        }
                    };
                }

                @Override
                public void visitOuterClass(String arg0, String arg1, String arg2) {
                }

                @Override
                public void visitSource(String arg0, String arg1) {
                }
            }, 0);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Unhandled error finding used types of " + type, t);
        }
        return result;
    }

    public static String findStringConstantReturnedByMethod(Method method) {
        final FoundConstantInfo found = new FoundConstantInfo();
        final String method_name = method.getName();
        final String method_desc = MPLType.getMethodDescriptor(method);
        try {
            Class<?> type = method.getDeclaringClass();
            InputStream classStream = type.getResourceAsStream(type.getSimpleName() + ".class");
            if (classStream == null) {
                return null;
            }
            ClassReader reader = new ClassReader(classStream);
            reader.accept(new ClassVisitor(589824){

                @Override
                public MethodVisitor visitMethod(int arg0, String name, String desc, String sig, String[] exceptions) {
                    if (!method_name.equals(name) || !method_desc.equals(desc)) {
                        return null;
                    }
                    return new MethodVisitor(this, 589824){
                        final /* synthetic */ 3 this$0;
                        {
                            this.this$0 = this$0;
                            super(arg0);
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                            return null;
                        }

                        @Override
                        public AnnotationVisitor visitAnnotationDefault() {
                            return null;
                        }

                        @Override
                        public void visitAttribute(Attribute arg0) {
                        }

                        @Override
                        public void visitCode() {
                            found.isValidSig = true;
                        }

                        @Override
                        public void visitFieldInsn(int arg0, String owner, String name, String desc) {
                            found.invalid();
                        }

                        @Override
                        public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
                            found.invalid();
                        }

                        @Override
                        public void visitIincInsn(int arg0, int arg1) {
                            found.invalid();
                        }

                        @Override
                        public void visitInsn(int arg0) {
                            if (arg0 != 176 || found.result == null) {
                                found.invalid();
                            }
                        }

                        @Override
                        public void visitIntInsn(int arg0, int arg1) {
                            found.invalid();
                        }

                        @Override
                        public void visitJumpInsn(int arg0, Label arg1) {
                            found.invalid();
                        }

                        @Override
                        public void visitLabel(Label arg0) {
                        }

                        @Override
                        public void visitLdcInsn(Object arg0) {
                            if (found.isValidSig && arg0 instanceof String) {
                                found.result = (String)arg0;
                            } else {
                                found.invalid();
                            }
                        }

                        @Override
                        public void visitLineNumber(int arg0, Label arg1) {
                        }

                        @Override
                        public void visitLocalVariable(String name, String desc, String sig, Label arg3, Label arg4, int arg5) {
                        }

                        @Override
                        public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
                            found.invalid();
                        }

                        @Override
                        public void visitMaxs(int arg0, int arg1) {
                        }

                        @Override
                        public void visitMethodInsn(int arg0, String owner, String name, String desc) {
                            found.invalid();
                        }

                        @Override
                        public void visitMultiANewArrayInsn(String arg0, int arg1) {
                            found.invalid();
                        }

                        @Override
                        public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
                            return null;
                        }

                        @Override
                        public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label ... arg3) {
                            found.invalid();
                        }

                        @Override
                        public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
                            found.invalid();
                        }

                        @Override
                        public void visitTypeInsn(int arg0, String arg1) {
                            found.invalid();
                        }

                        @Override
                        public void visitVarInsn(int arg0, int arg1) {
                            found.invalid();
                        }
                    };
                }
            }, 0);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return found.isValidSig ? found.result : null;
    }

    public static StackTraceElement findMethodDetails(Method method) {
        final MethodInfo info = new MethodInfo(method);
        Class<?> declaringClass = method.getDeclaringClass();
        try {
            InputStream classStream = declaringClass.getResourceAsStream(declaringClass.getSimpleName() + ".class");
            if (classStream == null) {
                return info.toTrace();
            }
            ClassReader reader = new ClassReader(classStream);
            reader.accept(new ClassVisitor(589824){

                @Override
                public void visit(int ver, int acc, String name, String sig, String supername, String[] interfaces) {
                }

                @Override
                public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                    return null;
                }

                @Override
                public void visitAttribute(Attribute arg0) {
                }

                @Override
                public void visitEnd() {
                }

                @Override
                public FieldVisitor visitField(int arg0, String name, String desc, String sig, Object value) {
                    return null;
                }

                @Override
                public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
                }

                @Override
                public MethodVisitor visitMethod(int arg0, String name, String desc, String sig, String[] exceptions) {
                    if (!name.equals(info.method.getName())) {
                        return null;
                    }
                    return new MethodVisitor(this, 589824){
                        final /* synthetic */ 4 this$0;
                        {
                            this.this$0 = this$0;
                            super(arg0);
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                            return null;
                        }

                        @Override
                        public AnnotationVisitor visitAnnotationDefault() {
                            return null;
                        }

                        @Override
                        public void visitAttribute(Attribute arg0) {
                        }

                        @Override
                        public void visitCode() {
                        }

                        @Override
                        public void visitEnd() {
                        }

                        @Override
                        public void visitFieldInsn(int arg0, String owner, String name, String desc) {
                        }

                        @Override
                        public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
                        }

                        @Override
                        public void visitIincInsn(int arg0, int arg1) {
                        }

                        @Override
                        public void visitInsn(int arg0) {
                        }

                        @Override
                        public void visitIntInsn(int arg0, int arg1) {
                        }

                        @Override
                        public void visitJumpInsn(int arg0, Label arg1) {
                        }

                        @Override
                        public void visitLabel(Label arg0) {
                        }

                        @Override
                        public void visitLdcInsn(Object arg0) {
                        }

                        @Override
                        public void visitLineNumber(int arg0, Label arg1) {
                            if (info.lineNumber == -1) {
                                info.lineNumber = arg0 - 1;
                            }
                        }

                        @Override
                        public void visitLocalVariable(String name, String desc, String sig, Label arg3, Label arg4, int arg5) {
                        }

                        @Override
                        public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
                        }

                        @Override
                        public void visitMaxs(int arg0, int arg1) {
                        }

                        @Override
                        public void visitMethodInsn(int arg0, String owner, String name, String desc) {
                        }

                        @Override
                        public void visitMultiANewArrayInsn(String arg0, int arg1) {
                        }

                        @Override
                        public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
                            return null;
                        }

                        @Override
                        public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label ... arg3) {
                        }

                        @Override
                        public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
                        }

                        @Override
                        public void visitTypeInsn(int arg0, String arg1) {
                        }

                        @Override
                        public void visitVarInsn(int arg0, int arg1) {
                        }
                    };
                }

                @Override
                public void visitOuterClass(String arg0, String arg1, String arg2) {
                }

                @Override
                public void visitSource(String arg0, String arg1) {
                    info.source = arg0;
                }
            }, 0);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Unhandled error finding method details of " + method, t);
        }
        return info.toTrace();
    }

    private static class FoundTypeSet
    extends TreeSet<Class<?>> {
        private static final long serialVersionUID = 1L;
        private final ClassLoader loader;

        public FoundTypeSet(ClassLoader loader) {
            super(new Comparator<Class<?>>(){

                @Override
                public int compare(Class<?> o1, Class<?> o2) {
                    if (o1.isPrimitive() != o2.isPrimitive()) {
                        if (o1.isPrimitive()) {
                            return -1;
                        }
                        return 1;
                    }
                    return MPLType.getName(o1).compareTo(MPLType.getName(o2));
                }
            });
            this.loader = loader;
        }

        public void add(Type type) {
            String name = type.getClassName();
            while (name.endsWith("[]")) {
                name = name.substring(0, name.length() - 2);
            }
            try {
                Class<?> primType = BoxedType.getUnboxedType(name);
                if (primType != null) {
                    this.add(primType);
                } else {
                    this.add(Class.forName(name, false, this.loader));
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private static class FoundConstantInfo {
        public String result = null;
        public boolean isValidSig = false;

        private FoundConstantInfo() {
        }

        public void invalid() {
            if (this.isValidSig) {
                this.isValidSig = false;
            }
        }
    }

    private static class MethodInfo {
        public String source = null;
        public int lineNumber = -1;
        public Method method;

        public MethodInfo(Method method) {
            this.method = method;
        }

        public StackTraceElement toTrace() {
            return new StackTraceElement(MPLType.getName(this.method.getDeclaringClass()), MPLType.getName(this.method), this.source, this.lineNumber);
        }
    }
}

