/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.Block;

@Template.InstanceType(value="org.bukkit.craftbukkit.block.CraftBlock")
public abstract class CraftBlockHandle
extends Template.Handle {
    public static final CraftBlockClass T = Template.Class.create(CraftBlockClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftBlockHandle createHandle(Object handleInstance) {
        return (CraftBlockHandle)T.createHandle(handleInstance);
    }

    public static Object getBlockTileEntity(Block block) {
        return CraftBlockHandle.T.getBlockTileEntity.invoker.invoke(null, block);
    }

    public static Object getBlockPosition(Block block) {
        return CraftBlockHandle.T.getBlockPosition.invoker.invoke(null, block);
    }

    public static Block createBlockAtTileEntity(Object nmsTileEntity) {
        return (Block)CraftBlockHandle.T.createBlockAtTileEntity.invoker.invoke(null, nmsTileEntity);
    }

    public abstract BlockData getBlockData();

    public static final class CraftBlockClass
    extends Template.Class<CraftBlockHandle> {
        public final Template.StaticMethod<Object> getBlockTileEntity = new Template.StaticMethod();
        public final Template.StaticMethod<Object> getBlockPosition = new Template.StaticMethod();
        public final Template.StaticMethod<Block> createBlockAtTileEntity = new Template.StaticMethod();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted();
    }
}

