/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItemException;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class UnsetDataWatcherItemInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItem";
    public static final String FIELD_CODE = "com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItem.INSTANCE";
    public static volatile boolean is_initialized = false;
    public static final Object UNSET_MARKER_VALUE = new Object();

    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        }
        is_initialized = true;
        if (CommonBootstrap.evaluateMCVersion("<", "1.20.5")) {
            return;
        }
        try {
            Class.forName(CLASS_NAME);
            return;
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                Class<?> dataWatcherItemType = Resolver.loadClass("net.minecraft.network.syncher.SynchedEntityData$DataItem", false);
                if (dataWatcherItemType == null) {
                    throw new IllegalStateException("SynchedEntityData.DataItem class not found in server");
                }
                Class<?> dataWatcherObjectType = Resolver.loadClass("net.minecraft.network.syncher.EntityDataAccessor", false);
                if (dataWatcherObjectType == null) {
                    throw new IllegalStateException("DataWatcherObject class not found in server");
                }
                ExtendedClassWriter cw = ExtendedClassWriter.builder(dataWatcherItemType).setExactName(CLASS_NAME).build();
                FieldVisitor fv = cw.visitField(9, "INSTANCE", cw.getTypeDescriptor(), null, null);
                fv.visitEnd();
                MethodVisitor mv = cw.visitMethod(1, "<init>", "(" + MPLType.getDescriptor(dataWatcherObjectType) + "Ljava/lang/Object;)V", null, null);
                mv.visitCode();
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, 1);
                mv.visitVarInsn(25, 2);
                mv.visitMethodInsn(183, MPLType.getInternalName(dataWatcherItemType), "<init>", "(" + MPLType.getDescriptor(dataWatcherObjectType) + "Ljava/lang/Object;)V", false);
                mv.visitInsn(177);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
                Method setValueMethod = Resolver.resolveAndGetDeclaredMethod(dataWatcherItemType, "setValue", Object.class);
                ReflectionUtil.getAllMethods(dataWatcherItemType).filter(m -> {
                    int modifiers = m.getModifiers();
                    return !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers);
                }).forEach(m -> {
                    if (m.equals(setValueMethod)) {
                        MethodVisitor mv = cw.visitMethod(1, MPLType.getName(setValueMethod), MPLType.getMethodDescriptor(setValueMethod), "(TT;)V", null);
                        mv.visitCode();
                        mv.visitFieldInsn(178, MPLType.getInternalName(UnsetDataWatcherItemException.class), "INSTANCE", MPLType.getDescriptor(UnsetDataWatcherItemException.class));
                        mv.visitInsn(191);
                        mv.visitMaxs(1, 2);
                        mv.visitEnd();
                    }
                });
                Object instance = cw.generateInstance(new Class[]{dataWatcherObjectType, Object.class}, new Object[]{null, UNSET_MARKER_VALUE});
                Field f = instance.getClass().getDeclaredField("INSTANCE");
                f.set(null, instance);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize null packet data serializer", t);
            }
            return;
        }
    }
}

