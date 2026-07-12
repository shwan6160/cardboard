/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class BlockDataWrapperHook_Impl_Paper_1_21_2
extends BlockDataWrapperHook {
    private final FastField<Object> propertiesCodec = new FastField();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField();
    private final FastField<BlockData> hookBlockDataField = new FastField();

    BlockDataWrapperHook_Impl_Paper_1_21_2() {
    }

    @Override
    protected void baseEnable() throws Throwable {
        Class<?> mapCodecType = this.getClassVerify("com.mojang.serialization.MapCodec");
        Class<?> iBlockDataHolderType = this.getClassVerify("net.minecraft.world.level.block.state.StateHolder");
        Field propertiesCodecField = Resolver.resolveAndGetDeclaredField(iBlockDataHolderType, "propertiesCodec");
        if (propertiesCodecField.getType() != mapCodecType) {
            throw new UnsupportedOperationException("Values field is of type " + propertiesCodecField.getType() + ", expected " + mapCodecType);
        }
        this.propertiesCodec.init(propertiesCodecField);
        this.propertiesCodec.forceInitialization();
        ExtendedClassWriter cw = ExtendedClassWriter.builder(mapCodecType).addInterface(BlockDataWrapperHook.Accessor.class).setFlags(1).setClassLoader(BlockDataWrapperHook_Impl_Paper_1_21_2.class.getClassLoader()).build();
        String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        String immutableMapDesc = MPLType.getDescriptor(mapCodecType);
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
        ReflectionUtil.getAllMethods(mapCodecType).filter(m -> {
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
            ExtendedClassWriter.visitInvoke(mv, mapCodecType, m);
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
        return this.propertiesCodec.get(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        this.propertiesCodec.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = this.hookBuilder.create();
        this.hookBaseField.set(hook, accessor);
        this.hookBlockDataField.set(hook, blockData);
        return hook;
    }
}

