package org.bukkit.craftbukkit.block.impl;

import com.google.common.base.Preconditions;
import io.papermc.paper.annotation.GeneratedClass;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
@GeneratedClass
public class CraftPointedDripstone extends CraftBlockData implements PointedDripstone {
    private static final EnumProperty<SpeleothemThickness> THICKNESS = SpeleothemBlock.THICKNESS;

    private static final EnumProperty<Direction> TIP_DIRECTION = SpeleothemBlock.TIP_DIRECTION;

    private static final BooleanProperty WATERLOGGED = SpeleothemBlock.WATERLOGGED;

    public CraftPointedDripstone(BlockState state) {
        super(state);
    }

    @Override
    public Thickness getThickness() {
        return this.get(THICKNESS, Thickness.class);
    }

    @Override
    public void setThickness(final Thickness thickness) {
        Preconditions.checkArgument(thickness != null, "thickness cannot be null!");
        this.set(THICKNESS, thickness);
    }

    @Override
    public BlockFace getVerticalDirection() {
        return this.get(TIP_DIRECTION, BlockFace.class);
    }

    @Override
    public void setVerticalDirection(final BlockFace blockFace) {
        Preconditions.checkArgument(blockFace != null, "blockFace cannot be null!");
        Preconditions.checkArgument(blockFace.getModY() != 0, "Invalid face, only vertical face are allowed for this property!");
        this.set(TIP_DIRECTION, blockFace);
    }

    @Override
    public Set<BlockFace> getVerticalDirections() {
        return this.getValues(TIP_DIRECTION, BlockFace.class);
    }

    @Override
    public boolean isWaterlogged() {
        return this.get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(final boolean waterlogged) {
        this.set(WATERLOGGED, waterlogged);
    }
}
