/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

@Template.InstanceType(value="net.minecraft.world.level.block.entity.SignBlockEntity")
public abstract class SignBlockEntityHandle
extends BlockEntityHandle {
    public static final SignBlockEntityClass T = Template.Class.create(SignBlockEntityClass.class, Common.TEMPLATE_RESOLVER);
    public static final Object[] ALL_EMPTY_RAW_LINES;
    public static final String[] ALL_EMPTY_STRING_LINES;

    public static SignBlockEntityHandle createHandle(Object handleInstance) {
        return (SignBlockEntityHandle)T.createHandle(handleInstance);
    }

    public abstract Object[] getRawFrontLines();

    public abstract String[] getMessageFrontLines();

    public abstract Object[] getRawBackLines();

    public abstract String[] getMessageBackLines();

    public abstract void setFormattedFrontLine(int var1, ChatText var2);

    public abstract void setFormattedBackLine(int var1, ChatText var2);

    public Sign toBukkit() {
        return (Sign)super.toBukkit();
    }

    public static SignBlockEntityHandle fromBukkit(Sign sign) {
        return SignBlockEntityHandle.createHandle(BlockStateConversion.INSTANCE.blockStateToTileEntity((BlockState)sign));
    }

    static {
        Object raw_empty = ChatText.empty().getRawHandle();
        ALL_EMPTY_RAW_LINES = new Object[]{raw_empty, raw_empty, raw_empty, raw_empty};
        ALL_EMPTY_STRING_LINES = new String[]{"", "", "", ""};
    }

    public static final class SignBlockEntityClass
    extends Template.Class<SignBlockEntityHandle> {
        public final Template.Method<Object[]> getRawFrontLines = new Template.Method();
        public final Template.Method<String[]> getMessageFrontLines = new Template.Method();
        public final Template.Method<Object[]> getRawBackLines = new Template.Method();
        public final Template.Method<String[]> getMessageBackLines = new Template.Method();
        public final Template.Method.Converted<Void> setFormattedFrontLine = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setFormattedBackLine = new Template.Method.Converted();
    }
}

