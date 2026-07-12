/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class BlockDataWrapperHook_Impl_Default
extends BlockDataWrapperHook {
    private final FastMethod<Object> getValues = new FastMethod();
    private final FastField<Object> values = new FastField();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField();
    private final FastField<BlockData> hookBlockDataField = new FastField();

    BlockDataWrapperHook_Impl_Default() {
    }

    @Override
    protected void baseEnable() throws Throwable {
        Method getValuesMethod;
        Field valuesField;
        Class<?> immutableMapType;
        Class<?> stateHolderType = this.getClassVerify("net.minecraft.world.level.block.state.StateHolder");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
            immutableMapType = this.getClassVerify("it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap");
            valuesField = Resolver.resolveAndGetDeclaredField(stateHolderType, "values");
        } else {
            immutableMapType = this.getClassVerify("com.google.common.collect.ImmutableMap");
            valuesField = CommonBootstrap.evaluateMCVersion(">=", "1.16") ? (CommonBootstrap.evaluateMCVersion(">=", "1.18") ? Resolver.resolveAndGetDeclaredField(stateHolderType, "values") : (CommonBootstrap.evaluateMCVersion(">=", "1.17") ? Resolver.resolveAndGetDeclaredField(stateHolderType, "values") : Resolver.resolveAndGetDeclaredField(stateHolderType, "b"))) : (CommonBootstrap.evaluateMCVersion(">=", "1.13") ? (CommonBootstrap.evaluateMCVersion(">=", "1.14") ? Resolver.resolveAndGetDeclaredField(stateHolderType, "d") : Resolver.resolveAndGetDeclaredField(stateHolderType, "c")) : (SafeField.contains(stateHolderType, "bAsImmutableMap", immutableMapType) ? stateHolderType.getDeclaredField("bAsImmutableMap") : Resolver.resolveAndGetDeclaredField(stateHolderType, "b")));
        }
        if (valuesField.getType() != immutableMapType) {
            throw new UnsupportedOperationException("Values field is of type " + valuesField.getType() + ", expected " + immutableMapType);
        }
        this.values.init(valuesField);
        this.values.forceInitialization();
        if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
            getValuesMethod = Resolver.resolveAndGetDeclaredMethod(stateHolderType, "getValues", new Class[0]);
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
            getValuesMethod = Resolver.resolveAndGetDeclaredMethod(stateHolderType, "getValues", new Class[0]);
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            getValuesMethod = Resolver.resolveAndGetDeclaredMethod(stateHolderType, "getStateMap", new Class[0]);
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.13.2")) {
            try {
                getValuesMethod = Resolver.resolveAndGetDeclaredMethod(stateHolderType, "getStateMap", new Class[0]);
            }
            catch (NoSuchMethodException ex) {
                getValuesMethod = Resolver.resolveAndGetDeclaredMethod(stateHolderType, "b", new Class[0]);
            }
        } else {
            getValuesMethod = CommonBootstrap.evaluateMCVersion(">=", "1.13") ? Resolver.resolveAndGetDeclaredMethod(stateHolderType, "b", new Class[0]) : (CommonBootstrap.evaluateMCVersion(">=", "1.12") ? Resolver.resolveAndGetDeclaredMethod(stateHolderType, "t", new Class[0]) : (CommonBootstrap.evaluateMCVersion(">=", "1.11") ? Resolver.resolveAndGetDeclaredMethod(stateHolderType, "u", new Class[0]) : (CommonBootstrap.evaluateMCVersion(">=", "1.9") ? Resolver.resolveAndGetDeclaredMethod(stateHolderType, "s", new Class[0]) : Resolver.resolveAndGetDeclaredMethod(stateHolderType, "b", new Class[0]))));
        }
        this.getValues.init(getValuesMethod);
        this.getValues.forceInitialization();
        ExtendedClassWriter cw = ExtendedClassWriter.builder(immutableMapType).addInterface(BlockDataWrapperHook.Accessor.class).setFlags(1).setClassLoader(BlockDataWrapperHook_Impl_Default.class.getClassLoader()).build();
        String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        String immutableMapDesc = MPLType.getDescriptor(immutableMapType);
        FieldVisitor fv = cw.visitField(18, "base", immutableMapDesc, null, null);
        fv.visitEnd();
        fv = cw.visitField(18, "blockData", blockDataDesc, null, null);
        fv.visitEnd();
        MethodVisitor mv = cw.visitMethod(1, "bkcGetOriginalValue", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, cw.getInternalName(), "base", immutableMapDesc);
        mv.visitInsn(176);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(1, "bkcGetBlockData", "()" + blockDataDesc, null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, cw.getInternalName(), "blockData", blockDataDesc);
        mv.visitInsn(176);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        ReflectionUtil.getAllMethods(immutableMapType).filter(m -> {
            if (m.getName().equals("finalize") && m.getParameterCount() == 0) {
                return false;
            }
            if (m.getName().equals("clone") && m.getParameterCount() == 0) {
                return false;
            }
            int modifiers = m.getModifiers();
            return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isPrivate(modifiers);
        }).forEachOrdered(m -> {
            MethodVisitor mv = cw.visitMethod(1, MPLType.getName(m), MPLType.getMethodDescriptor(m), null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, cw.getInternalName(), "base", immutableMapDesc);
            int registerInitial = 1;
            for (Class<?> param : m.getParameterTypes()) {
                registerInitial = MPLType.visitVarILoad(mv, registerInitial, param);
            }
            ExtendedClassWriter.visitInvoke(mv, immutableMapType, m);
            mv.visitInsn(MPLType.getReturnType(m).getOpcode(172));
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        });
        Class type = cw.generate();
        this.hookBuilder = NullInstantiator.of(type);
        this.hookBaseField.init(type.getDeclaredField("base"));
        this.hookBlockDataField.init(type.getDeclaredField("blockData"));
    }

    private Class<?> getClassVerify(String name) {
        Class<?> type = CommonUtil.getClass(name);
        if (type == null) {
            throw new UnsupportedOperationException("Class not found: " + name);
        }
        return type;
    }

    @Override
    public Object getAccessor(Object nmsIBlockData) {
        return this.getValues.invoke(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        this.values.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = this.hookBuilder.create();
        this.hookBaseField.set(hook, accessor);
        this.hookBlockDataField.set(hook, blockData);
        return hook;
    }
}

