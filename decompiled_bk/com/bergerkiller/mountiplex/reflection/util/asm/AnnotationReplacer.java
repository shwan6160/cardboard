/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm;

import com.bergerkiller.mountiplex.dep.org.objectweb.asm.AnnotationVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassReader;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.ClassWriter;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Type;

public class AnnotationReplacer {
    public static byte[] replace(byte[] originalBytecode, Transformer transformer) {
        ClassReader cr = new ClassReader(originalBytecode);
        ClassWriter cw = new ClassWriter(0);
        Adapter a = new Adapter(cw, transformer);
        cr.accept(a, 0);
        return cw.toByteArray();
    }

    private static class Adapter
    extends ClassVisitor {
        private final Transformer transformer;

        public Adapter(ClassVisitor cv, Transformer transformer) {
            super(589824, cv);
            this.transformer = transformer;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return new MethodVisitor(this.api, super.visitMethod(access, name, descriptor, signature, exceptions)){

                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return new AnnotationAdapter(super.visitAnnotation(descriptor, visible), descriptor);
                }
            };
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            return new FieldVisitor(this.api, super.visitField(access, name, descriptor, signature, value)){

                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    return new AnnotationAdapter(super.visitAnnotation(descriptor, visible), descriptor);
                }
            };
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return new AnnotationAdapter(super.visitAnnotation(descriptor, visible), descriptor);
        }

        private class AnnotationAdapter
        extends AnnotationVisitor {
            private final String annotationName;

            public AnnotationAdapter(AnnotationVisitor annotationVisitor, String annotationDescriptor) {
                super(Adapter.this.api, annotationVisitor);
                this.annotationName = Type.getType(annotationDescriptor).getClassName();
            }

            @Override
            public void visit(String name, Object value) {
                if (value instanceof String) {
                    super.visit(name, Adapter.this.transformer.transform(this.annotationName, (String)value));
                } else {
                    super.visit(name, value);
                }
            }

            @Override
            public AnnotationVisitor visitArray(String name) {
                return new AnnotationVisitor(Adapter.this.api, super.visitArray(name)){

                    @Override
                    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                        return new AnnotationAdapter(super.visitAnnotation(name, descriptor), descriptor);
                    }
                };
            }
        }
    }

    public static interface Transformer {
        public String transform(String var1, String var2);
    }
}

