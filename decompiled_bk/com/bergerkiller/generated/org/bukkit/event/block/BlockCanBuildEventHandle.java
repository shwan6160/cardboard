/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.event.block.BlockCanBuildEvent
 */
package com.bergerkiller.generated.org.bukkit.event.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockCanBuildEvent;

@Template.InstanceType(value="org.bukkit.event.block.BlockCanBuildEvent")
public abstract class BlockCanBuildEventHandle
extends Template.Handle {
    public static final BlockCanBuildEventClass T = Template.Class.create(BlockCanBuildEventClass.class, Common.TEMPLATE_RESOLVER);

    public static BlockCanBuildEventHandle createHandle(Object handleInstance) {
        return (BlockCanBuildEventHandle)T.createHandle(handleInstance);
    }

    public static BlockCanBuildEvent create(Block block, BlockData data, boolean canBuild) {
        return (BlockCanBuildEvent)BlockCanBuildEventHandle.T.create.invoker.invoke(null, block, data, canBuild);
    }

    public static final class BlockCanBuildEventClass
    extends Template.Class<BlockCanBuildEventHandle> {
        public final Template.StaticMethod<BlockCanBuildEvent> create = new Template.StaticMethod();
    }
}

