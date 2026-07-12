/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.BlockStateBase
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.generated.org.bukkit.block.SignHandle
 *  com.bergerkiller.mountiplex.MountiplexUtil
 *  com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor
 *  com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label
 *  com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor
 *  com.bergerkiller.mountiplex.dep.org.objectweb.asm.Opcodes
 *  com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter
 *  com.bergerkiller.mountiplex.reflection.util.asm.MPLType
 *  org.bukkit.DyeColor
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataHolder
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.bases.BlockStateBase;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Label;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.Opcodes;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Constructor;
import java.util.function.Function;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

public abstract class FakeSign
extends BlockStateBase
implements Sign {
    private static final Function<Block, FakeSign> constructor = (Function)LogicUtil.tryCreate(FakeSign::createFakeSignConstructor, err -> block -> {
        throw new IllegalStateException("Failed to create FakeSign implementation", (Throwable)err);
    });
    public Handler handler = null;

    protected FakeSign(Block block) {
        super(block);
    }

    public static FakeSign create(Block signBlock) {
        if (signBlock == null) {
            throw new IllegalArgumentException("Sign block is null");
        }
        return constructor.apply(signBlock);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return this.handler;
    }

    @Deprecated
    public String[] getLines() {
        String[] lines = new String[4];
        for (int i = 0; i < 4; ++i) {
            lines[i] = this.handler.getFrontLine(i);
        }
        return lines;
    }

    @Deprecated
    public String getLine(int index) {
        return this.handler.getFrontLine(index);
    }

    @Deprecated
    public void setLine(int index, String text) {
        this.handler.setFrontLine(index, text);
    }

    public PersistentDataContainer getPersistentDataContainer() {
        BlockState state = this.getBlock().getState();
        if (state instanceof PersistentDataHolder) {
            return ((PersistentDataHolder)state).getPersistentDataContainer();
        }
        return null;
    }

    public boolean isEditable() {
        return false;
    }

    public void setEditable(boolean editable) {
    }

    public DyeColor getColor() {
        return null;
    }

    public void setColor(DyeColor arg0) {
    }

    public boolean isGlowingText() {
        return false;
    }

    public void setGlowingText(boolean arg0) {
    }

    public boolean update() {
        return this.handler.update(false, true);
    }

    public boolean update(boolean force) {
        return this.handler.update(force, true);
    }

    public boolean update(boolean force, boolean applyPhysics) {
        return this.handler.update(force, applyPhysics);
    }

    private static Function<Block, FakeSign> createFakeSignConstructor() {
        Class<?> backSideClass;
        Class<?> frontSideClass;
        if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
            frontSideClass = FakeSign.generateFakeSignSide("Front");
            backSideClass = FakeSign.generateFakeSignSide("Back");
        } else {
            backSideClass = null;
            frontSideClass = null;
        }
        ExtendedClassWriter classWriter = ExtendedClassWriter.builder(FakeSign.class).setExactName(FakeSign.class.getName() + "$Impl").build();
        String ctorDesc = "(" + MPLType.getDescriptor(Block.class) + ")V";
        if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
            Class signSideType = CommonUtil.getClass((String)"org.bukkit.block.sign.SignSide");
            String signSideDesc = MPLType.getDescriptor((Class)signSideType);
            String fakeSignDesc = MPLType.getDescriptor(FakeSign.class);
            FieldVisitor fieldVisitor = classWriter.visitField(18, "front", signSideDesc, null, null);
            fieldVisitor.visitEnd();
            fieldVisitor = classWriter.visitField(18, "back", signSideDesc, null, null);
            fieldVisitor.visitEnd();
            MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", ctorDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitMethodInsn(183, MPLType.getInternalName(FakeSign.class), "<init>", ctorDesc, false);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitTypeInsn(187, MPLType.getInternalName(frontSideClass));
            methodVisitor.visitInsn(89);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(183, MPLType.getInternalName(frontSideClass), "<init>", "(" + fakeSignDesc + ")V", false);
            methodVisitor.visitFieldInsn(181, classWriter.getInternalName(), "front", signSideDesc);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitTypeInsn(187, MPLType.getInternalName(backSideClass));
            methodVisitor.visitInsn(89);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitMethodInsn(183, MPLType.getInternalName(backSideClass), "<init>", "(" + fakeSignDesc + ")V", false);
            methodVisitor.visitFieldInsn(181, classWriter.getInternalName(), "back", signSideDesc);
            methodVisitor.visitInsn(177);
            methodVisitor.visitMaxs(4, 2);
            methodVisitor.visitEnd();
            Class sideType = CommonUtil.getClass((String)"org.bukkit.block.sign.Side");
            String sideDesc = MPLType.getDescriptor((Class)sideType);
            methodVisitor = classWriter.visitMethod(1, "getSide", "(" + sideDesc + ")" + signSideDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitFieldInsn(178, MPLType.getInternalName((Class)sideType), "FRONT", sideDesc);
            Label label0 = new Label();
            methodVisitor.visitJumpInsn(166, label0);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitFieldInsn(180, classWriter.getInternalName(), "front", signSideDesc);
            Label label1 = new Label();
            methodVisitor.visitJumpInsn(167, label1);
            methodVisitor.visitLabel(label0);
            methodVisitor.visitFrame(3, 0, null, 0, null);
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitFieldInsn(180, classWriter.getInternalName(), "back", signSideDesc);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitFrame(4, 0, null, 1, new Object[]{MPLType.getInternalName((Class)signSideType)});
            methodVisitor.visitInsn(176);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        } else {
            MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", ctorDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(25, 0);
            methodVisitor.visitVarInsn(25, 1);
            methodVisitor.visitMethodInsn(183, MPLType.getInternalName(FakeSign.class), "<init>", ctorDesc, false);
            methodVisitor.visitInsn(177);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        Constructor ctor = classWriter.generateConstructor(new Class[]{Block.class});
        return block -> {
            try {
                return (FakeSign)((Object)((Object)ctor.newInstance(block)));
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow((Throwable)t);
            }
        };
    }

    private static Class<?> generateFakeSignSide(String sideName) {
        Class signSideType = CommonUtil.getClass((String)"org.bukkit.block.sign.SignSide");
        ExtendedClassWriter classWriter = ExtendedClassWriter.builder((Class)signSideType).setExactName(FakeSign.class.getName() + "$FakeSignSide" + sideName).build();
        String fakeSignName = MPLType.getInternalName(FakeSign.class);
        String fakeSignDesc = MPLType.getDescriptor(FakeSign.class);
        FieldVisitor fieldVisitor = classWriter.visitField(18, "fakeSign", fakeSignDesc, null, null);
        fieldVisitor.visitEnd();
        MethodVisitor methodVisitor = classWriter.visitMethod(1, "<init>", "(" + fakeSignDesc + ")V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitFieldInsn(181, classWriter.getInternalName(), "fakeSign", fakeSignDesc);
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "getLines", "()[Ljava/lang/String;", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitInsn(7);
        methodVisitor.visitTypeInsn(189, "java/lang/String");
        methodVisitor.visitVarInsn(58, 1);
        methodVisitor.visitInsn(3);
        methodVisitor.visitVarInsn(54, 2);
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitFrame(1, 2, new Object[]{"[Ljava/lang/String;", Opcodes.INTEGER}, 0, null);
        methodVisitor.visitVarInsn(21, 2);
        methodVisitor.visitInsn(7);
        Label label1 = new Label();
        methodVisitor.visitJumpInsn(162, label1);
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitVarInsn(21, 2);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(21, 2);
        methodVisitor.visitMethodInsn(182, classWriter.getInternalName(), "getLine", "(I)Ljava/lang/String;", false);
        methodVisitor.visitInsn(83);
        methodVisitor.visitIincInsn(2, 1);
        methodVisitor.visitJumpInsn(167, label0);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitFrame(2, 1, null, 0, null);
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(4, 3);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "getLine", "(I)Ljava/lang/String;", null, new String[]{"java/lang/IndexOutOfBoundsException"});
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitFieldInsn(180, classWriter.getInternalName(), "fakeSign", fakeSignDesc);
        methodVisitor.visitMethodInsn(182, fakeSignName, "getHandler", "()" + MPLType.getDescriptor(Handler.class), false);
        methodVisitor.visitVarInsn(21, 1);
        methodVisitor.visitMethodInsn(185, MPLType.getInternalName(Handler.class), "get" + sideName + "Line", "(I)Ljava/lang/String;", true);
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "setLine", "(ILjava/lang/String;)V", null, new String[]{"java/lang/IndexOutOfBoundsException"});
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitFieldInsn(180, classWriter.getInternalName(), "fakeSign", fakeSignDesc);
        methodVisitor.visitMethodInsn(182, fakeSignName, "getHandler", "()" + MPLType.getDescriptor(Handler.class), false);
        methodVisitor.visitVarInsn(21, 1);
        methodVisitor.visitVarInsn(25, 2);
        methodVisitor.visitMethodInsn(185, MPLType.getInternalName(Handler.class), "set" + sideName + "Line", "(ILjava/lang/String;)V", true);
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(3, 3);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "isGlowingText", "()Z", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitInsn(3);
        methodVisitor.visitInsn(172);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "setGlowingText", "(Z)V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(0, 2);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "getColor", "()" + MPLType.getDescriptor(DyeColor.class), null, null);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(178, MPLType.getInternalName(DyeColor.class), "BLACK", MPLType.getDescriptor(DyeColor.class));
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        methodVisitor = classWriter.visitMethod(1, "setColor", "(" + MPLType.getDescriptor(DyeColor.class) + ")V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(0, 2);
        methodVisitor.visitEnd();
        classWriter.visitEnd();
        return classWriter.generate();
    }

    public static interface Handler {
        public String getFrontLine(int var1);

        public void setFrontLine(int var1, String var2);

        public String getBackLine(int var1);

        public void setBackLine(int var1, String var2);

        default public boolean update(boolean force, boolean applyPhysics) {
            return true;
        }
    }

    public static class HandlerSignFallback
    implements Handler {
        private final Block signBlock;

        public HandlerSignFallback(Block signBlock) {
            this.signBlock = signBlock;
        }

        private SignHandle accessSign() {
            SignHandle sign = SignHandle.createHandle((Object)BlockUtil.getSign((Block)this.signBlock));
            if (sign == null) {
                throw new IllegalStateException("No sign is set at " + this.signBlock);
            }
            return sign;
        }

        @Override
        public String getFrontLine(int index) {
            return this.accessSign().getFrontLine(index);
        }

        @Override
        public void setFrontLine(int index, String text) {
            SignHandle sign = this.accessSign();
            sign.setFrontLine(index, text);
            ((Sign)sign.getRaw()).update(true);
        }

        @Override
        public String getBackLine(int index) {
            return this.accessSign().getBackLine(index);
        }

        @Override
        public void setBackLine(int index, String text) {
            SignHandle sign = this.accessSign();
            sign.setBackLine(index, text);
            ((Sign)sign.getRaw()).update(true);
        }
    }
}

