/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.world.level.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.world.level.block.Block")
public abstract class BlockHandle
extends Template.Handle {
    public static final BlockClass T = Template.Class.create(BlockClass.class, Common.TEMPLATE_RESOLVER);
    public static final Iterable REGISTRY_ID = BlockHandle.T.REGISTRY_ID.getSafe();

    public static BlockHandle createHandle(Object handleInstance) {
        return (BlockHandle)T.createHandle(handleInstance);
    }

    public static Iterable<?> getRegistry() {
        return (Iterable)BlockHandle.T.getRegistry.invoker.invoke(null);
    }

    public static BlockStateHandle getByCombinedId(int combinedId) {
        return BlockHandle.T.getByCombinedId.invoke(combinedId);
    }

    public static int getCombinedId(BlockStateHandle iblockdata) {
        return BlockHandle.T.getCombinedId.invoke(iblockdata);
    }

    public abstract String getTitle();

    public abstract boolean isFaceOpaque(BlockStateHandle var1, World var2, int var3, int var4, int var5, BlockFace var6);

    public abstract int getOpacity(BlockStateHandle var1, World var2, int var3, int var4, int var5);

    public abstract int getEmission(BlockStateHandle var1, World var2, int var3, int var4, int var5);

    public abstract boolean isOccluding_at(BlockStateHandle var1, World var2, int var3, int var4, int var5);

    public abstract boolean isOccluding(BlockStateHandle var1, Block var2);

    public abstract boolean canSupportOnFace(BlockStateHandle var1, Block var2, BlockFace var3);

    public abstract float getDamageResillience();

    public abstract void dropNaturally(BlockStateHandle var1, World var2, IntVector3 var3, float var4, int var5);

    public abstract void stepOn(World var1, IntVector3 var2, BlockStateHandle var3, Entity var4);

    public abstract BlockStateHandle updateState(BlockStateHandle var1, World var2, IntVector3 var3);

    public abstract BlockStateHandle getBlockData();

    public static final class BlockClass
    extends Template.Class<BlockHandle> {
        public final Template.StaticField.Converted<Iterable> REGISTRY_ID = new Template.StaticField.Converted();
        public final Template.StaticMethod<Iterable<?>> getRegistry = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<BlockStateHandle> getByCombinedId = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<Integer> getCombinedId = new Template.StaticMethod.Converted();
        public final Template.Method<String> getTitle = new Template.Method();
        public final Template.Method.Converted<Boolean> isFaceOpaque = new Template.Method.Converted();
        public final Template.Method.Converted<Integer> getOpacity = new Template.Method.Converted();
        public final Template.Method.Converted<Integer> getEmission = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isOccluding_at = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> isOccluding = new Template.Method.Converted();
        public final Template.Method.Converted<Boolean> canSupportOnFace = new Template.Method.Converted();
        public final Template.Method<Float> getDamageResillience = new Template.Method();
        public final Template.Method.Converted<Void> dropNaturally = new Template.Method.Converted();
        public final Template.Method.Converted<Void> stepOn = new Template.Method.Converted();
        public final Template.Method.Converted<BlockStateHandle> updateState = new Template.Method.Converted();
        public final Template.Method.Converted<BlockStateHandle> getBlockData = new Template.Method.Converted();
    }
}

