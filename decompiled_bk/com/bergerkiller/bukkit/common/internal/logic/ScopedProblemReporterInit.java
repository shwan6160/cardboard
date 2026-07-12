/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.util.logging.Level;

public class ScopedProblemReporterInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.ScopedProblemReporter";
    public static volatile boolean is_initialized = false;

    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        }
        is_initialized = true;
        if (CommonBootstrap.evaluateMCVersion("<", "1.21.6")) {
            return;
        }
        try {
            Resolver.getClassByExactName(CLASS_NAME);
            return;
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                Class<?> problemReporterBaseType = Resolver.loadClass("net.minecraft.util.ProblemReporter$Collector", false);
                ExtendedClassWriter cw = ExtendedClassWriter.builder(problemReporterBaseType).addInterface(AutoCloseable.class).setExactName(CLASS_NAME).build();
                FieldVisitor fv = cw.visitField(18, "logger", "Ljava/util/logging/Logger;", null, null);
                fv.visitEnd();
                MethodVisitor mv = cw.visitMethod(1, "<init>", "(Ljava/util/logging/Logger;)V", null, null);
                mv.visitCode();
                mv.visitVarInsn(25, 0);
                mv.visitMethodInsn(183, MPLType.getInternalName(problemReporterBaseType), "<init>", "()V", false);
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, 1);
                mv.visitFieldInsn(181, cw.getInternalName(), "logger", "Ljava/util/logging/Logger;");
                mv.visitInsn(177);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
                String isEmptyMethodName = Resolver.resolveMethodName(problemReporterBaseType, "isEmpty", new Class[0]);
                String getTreeReportMethodName = Resolver.resolveMethodName(problemReporterBaseType, "getTreeReport", new Class[0]);
                MethodVisitor mv2 = cw.visitMethod(1, "close", "()V", null, null);
                mv2.visitCode();
                mv2.visitVarInsn(25, 0);
                mv2.visitMethodInsn(182, cw.getInternalName(), isEmptyMethodName, "()Z", false);
                Label label0 = new Label();
                mv2.visitJumpInsn(154, label0);
                mv2.visitVarInsn(25, 0);
                mv2.visitFieldInsn(180, cw.getInternalName(), "logger", "Ljava/util/logging/Logger;");
                mv2.visitTypeInsn(187, "java/lang/StringBuilder");
                mv2.visitInsn(89);
                mv2.visitMethodInsn(183, "java/lang/StringBuilder", "<init>", "()V", false);
                mv2.visitLdcInsn("Serialization errors\n: ");
                mv2.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv2.visitVarInsn(25, 0);
                mv2.visitMethodInsn(182, cw.getInternalName(), getTreeReportMethodName, "()Ljava/lang/String;", false);
                mv2.visitMethodInsn(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv2.visitMethodInsn(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv2.visitMethodInsn(182, "java/util/logging/Logger", "warning", "(Ljava/lang/String;)V", false);
                mv2.visitLabel(label0);
                mv2.visitFrame(3, 0, null, 0, null);
                mv2.visitInsn(177);
                mv2.visitMaxs(3, 1);
                mv2.visitEnd();
                cw.generate();
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize scoped problem reporter", t);
            }
            return;
        }
    }
}

