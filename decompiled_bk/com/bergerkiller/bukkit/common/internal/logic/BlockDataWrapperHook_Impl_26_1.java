/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Method;

class BlockDataWrapperHook_Impl_26_1
extends BlockDataWrapperHook {
    private final FastField<Object> statePredicateField = new FastField();
    private NullInstantiator<Object> hookBuilder = null;
    private final FastField<Object> hookBaseField = new FastField();
    private final FastField<BlockData> hookBlockDataField = new FastField();

    BlockDataWrapperHook_Impl_26_1() {
    }

    @Override
    protected void baseEnable() throws Throwable {
        Class<?> blockStateBaseType = this.getClassVerify("net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase");
        Class<?> statePredicateType = this.getClassVerify("net.minecraft.world.level.block.state.BlockBehaviour$StatePredicate");
        Class<?> nmsBlockStateType = this.getClassVerify("net.minecraft.world.level.block.state.BlockState");
        Class<?> nmsBlockGetterType = this.getClassVerify("net.minecraft.world.level.BlockGetter");
        Class<?> nmsBlockPosType = this.getClassVerify("net.minecraft.core.BlockPos");
        this.statePredicateField.init(Resolver.resolveAndGetDeclaredField(blockStateBaseType, "isSuffocating"));
        this.statePredicateField.forceInitialization();
        Method testMethod = Resolver.resolveAndGetDeclaredMethod(statePredicateType, "test", nmsBlockStateType, nmsBlockGetterType, nmsBlockPosType);
        ExtendedClassWriter cw = ExtendedClassWriter.builder(statePredicateType).addInterface(BlockDataWrapperHook.Accessor.class).setFlags(1).setClassLoader(BlockDataWrapperHook_Impl_26_1.class.getClassLoader()).build();
        String blockDataDesc = MPLType.getDescriptor(BlockData.class);
        String statePredicateDesc = MPLType.getDescriptor(statePredicateType);
        FieldVisitor fv = cw.visitField(18, "base", statePredicateDesc, null, null);
        fv.visitEnd();
        fv = cw.visitField(18, "blockData", blockDataDesc, null, null);
        fv.visitEnd();
        MethodVisitor mv = cw.visitMethod(1, "bkcGetOriginalValue", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, cw.getInternalName(), "base", statePredicateDesc);
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
        String testMethodName = MPLType.getName(testMethod);
        String testMethodDesc = MPLType.getMethodDescriptor(testMethod);
        MethodVisitor mv2 = cw.visitMethod(1, testMethodName, testMethodDesc, null, null);
        mv2.visitCode();
        mv2.visitVarInsn(25, 0);
        mv2.visitFieldInsn(180, cw.getInternalName(), "base", statePredicateDesc);
        mv2.visitVarInsn(25, 1);
        mv2.visitVarInsn(25, 2);
        mv2.visitVarInsn(25, 3);
        mv2.visitMethodInsn(185, MPLType.getInternalName(statePredicateType), testMethodName, testMethodDesc, true);
        mv2.visitInsn(172);
        mv2.visitMaxs(4, 4);
        mv2.visitEnd();
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
        return this.statePredicateField.get(nmsIBlockData);
    }

    @Override
    protected void setAccessor(Object nmsIBlockdata, Object accessor) {
        this.statePredicateField.set(nmsIBlockdata, accessor);
    }

    @Override
    protected Object hook(Object accessor, BlockData blockData) {
        Object hook = this.hookBuilder.create();
        this.hookBaseField.set(hook, accessor);
        this.hookBlockDataField.set(hook, blockData);
        return hook;
    }
}

