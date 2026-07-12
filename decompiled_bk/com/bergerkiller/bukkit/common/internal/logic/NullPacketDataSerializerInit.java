/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class NullPacketDataSerializerInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer";
    public static final String FIELD_CODE = "com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializer.INSTANCE";
    public static volatile boolean is_initialized = false;

    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        }
        is_initialized = true;
        if (CommonBootstrap.evaluateMCVersion("<", "1.17")) {
            return;
        }
        try {
            Resolver.getClassByExactName(CLASS_NAME);
            return;
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                Class<?> dataSerializerType = Resolver.loadClass("net.minecraft.network.RegistryFriendlyByteBuf", false);
                if (dataSerializerType == null) {
                    dataSerializerType = Resolver.loadClass("net.minecraft.network.FriendlyByteBuf", false);
                }
                if (dataSerializerType == null) {
                    throw new IllegalStateException("FriendlyByteBuf class not found in server");
                }
                ExtendedClassWriter cw = ExtendedClassWriter.builder(dataSerializerType).setExactName(CLASS_NAME).build();
                FieldVisitor fv = cw.visitField(9, "INSTANCE", cw.getTypeDescriptor(), null, null);
                fv.visitEnd();
                Class<?> customRegistryType = CommonBootstrap.evaluateMCVersion(">=", "1.20.5") ? CommonUtil.getClass("net.minecraft.core.RegistryAccess") : null;
                ReflectionUtil.getAllMethods(dataSerializerType).filter(m -> {
                    int modifiers = m.getModifiers();
                    return !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers);
                }).forEach(m -> {
                    if (m.getReturnType().equals(customRegistryType)) {
                        MethodVisitor mv = cw.visitMethod(1, MPLType.getName(m), MPLType.getMethodDescriptor(m), null, null);
                        mv.visitCode();
                        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.5")) {
                            Class<?> craftRegistryClass = CommonUtil.getClass("org.bukkit.craftbukkit.CraftRegistry", false);
                            String methodName = Resolver.resolveMethodName(craftRegistryClass, "getMinecraftRegistry", new Class[0]);
                            mv.visitMethodInsn(184, MPLType.getInternalName(craftRegistryClass), methodName, "()" + MPLType.getDescriptor(customRegistryType), false);
                        } else {
                            Class<?> minecraftServerClass = CommonUtil.getClass("net.minecraft.server.MinecraftServer");
                            String methodName = Resolver.resolveMethodName(minecraftServerClass, "getDefaultRegistryAccess", new Class[0]);
                            mv.visitMethodInsn(184, MPLType.getInternalName(minecraftServerClass), methodName, "()" + MPLType.getDescriptor(customRegistryType), false);
                        }
                        mv.visitInsn(176);
                        mv.visitMaxs(MPLType.getType(customRegistryType).getSize(), 1);
                        mv.visitEnd();
                    } else {
                        cw.visitMethodReturnConstant((Method)m, BoxedType.getDefaultValue(m.getReturnType()));
                    }
                });
                Object instance = cw.generateInstanceNull();
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

