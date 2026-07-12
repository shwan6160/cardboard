/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class EmptyBlockGetterInit {
    public static final String CLASS_NAME = "com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetter";
    public static final String FIELD_CODE = "com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetter.INSTANCE";
    public static volatile boolean is_initialized = false;

    public static synchronized void initialize() {
        if (is_initialized) {
            return;
        }
        is_initialized = true;
        if (CommonBootstrap.evaluateMCVersion("<", "1.13") || CommonBootstrap.evaluateMCVersion(">", "1.14")) {
            return;
        }
        try {
            Resolver.getClassByExactName(CLASS_NAME);
            return;
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                Class<?> blockGetterType = Resolver.loadClass("net.minecraft.world.level.BlockGetter", false);
                if (blockGetterType == null) {
                    throw new IllegalStateException("BlockGetter class not found in server");
                }
                ExtendedClassWriter cw = ExtendedClassWriter.builder(blockGetterType).setExactName(CLASS_NAME).build();
                FieldVisitor fv = cw.visitField(9, "INSTANCE", cw.getTypeDescriptor(), null, null);
                fv.visitEnd();
                ReflectionUtil.getAllMethods(blockGetterType).filter(m -> !m.isDefault()).filter(m -> {
                    int modifiers = m.getModifiers();
                    return !Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers);
                }).forEach(m -> cw.visitMethodReturnConstant((Method)m, BoxedType.getDefaultValue(m.getReturnType())));
                Object instance = cw.generateInstanceNull();
                Field f = instance.getClass().getDeclaredField("INSTANCE");
                f.set(null, instance);
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize empty block getter", t);
            }
            return;
        }
    }
}

