/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

@Template.InstanceType(value="org.bukkit.craftbukkit.block.CraftBlockState")
public abstract class CraftBlockStateHandle
extends Template.Handle {
    public static final CraftBlockStateClass T = Template.Class.create(CraftBlockStateClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftBlockStateHandle createHandle(Object handleInstance) {
        return (CraftBlockStateHandle)T.createHandle(handleInstance);
    }

    public static final BlockState createNew(Block block) {
        return CraftBlockStateHandle.T.constr_block.newInstance(block);
    }

    public abstract int getFlag();

    public abstract void setFlag(int var1);

    public static final class CraftBlockStateClass
    extends Template.Class<CraftBlockStateHandle> {
        public final Template.Constructor.Converted<BlockState> constr_block = new Template.Constructor.Converted();
        public final Template.Field.Integer flag = new Template.Field.Integer();
        @Template.Optional
        public final Template.Method<Void> init = new Template.Method();
    }
}

