/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.TileEntityTypesSerializedIds_1_8_to_1_17_1;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class BlockStateChange {
    public abstract BlockStateType getType();

    public abstract IntVector3 getPosition();

    public abstract CommonTagCompound getMetadata();

    public abstract boolean hasMetadata();

    public abstract CommonTagCompound serialize();

    public String toString() {
        return "BlockStateChange{type=" + this.getType() + ", pos=" + this.getPosition() + ", meta=" + (this.hasMetadata() ? this.getMetadata().toString() : "none") + "}";
    }

    public int hashCode() {
        return this.getPosition().hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BlockStateChange) {
            BlockStateChange other = (BlockStateChange)o;
            if (this.getType() != other.getType() || !this.getPosition().equals(other.getPosition())) {
                return false;
            }
            if (this.hasMetadata()) {
                return other.hasMetadata() && this.getMetadata().equals(other.getMetadata());
            }
            return !other.hasMetadata() || other.getMetadata().isEmpty();
        }
        return false;
    }

    public static BlockStateChange fromMetadataPacked(CommonTagCompound metadata) {
        return new BlockStateChangeMetadataPacked(metadata);
    }

    public static BlockStateChange deferred(IntVector3 position, BlockStateType type, Supplier<CommonTagCompound> metadataSupplier, BooleanSupplier hasMetadataSupplier) {
        return new BlockStateChangeMetadataDeferred(position, type, metadataSupplier, hasMetadataSupplier);
    }

    private static final class BlockStateChangeMetadataPacked
    extends BlockStateChange {
        private final CommonTagCompound metadata;

        public BlockStateChangeMetadataPacked(CommonTagCompound metadata) {
            this.metadata = metadata;
        }

        @Override
        public BlockStateType getType() {
            String id = (String)((Object)this.metadata.getValue("id", String.class));
            if (CommonCapabilities.TILE_ENTITY_LEGACY_NAMES) {
                IdentifierHandle key = TileEntityTypesSerializedIds_1_8_to_1_17_1.toMinecraftKeyFromLegacyName(id);
                return BlockStateType.byKey(key);
            }
            return id == null ? null : BlockStateType.byName(id);
        }

        @Override
        public IntVector3 getPosition() {
            Integer x = (Integer)((Object)this.metadata.getValue("x", Integer.class));
            Integer y = (Integer)((Object)this.metadata.getValue("y", Integer.class));
            Integer z = (Integer)((Object)this.metadata.getValue("z", Integer.class));
            if (x != null && y != null && z != null) {
                return new IntVector3(x, y, z);
            }
            return null;
        }

        @Override
        public CommonTagCompound getMetadata() {
            return this.metadata;
        }

        @Override
        public CommonTagCompound serialize() {
            return this.metadata;
        }

        @Override
        public boolean hasMetadata() {
            return true;
        }
    }

    private static final class BlockStateChangeMetadataDeferred
    extends BlockStateChange {
        private final IntVector3 position;
        private final BlockStateType type;
        private final Supplier<CommonTagCompound> metadataSupplier;
        private final BooleanSupplier hasMetadataSupplier;

        public BlockStateChangeMetadataDeferred(IntVector3 position, BlockStateType type, Supplier<CommonTagCompound> metadataSupplier, BooleanSupplier hasMetadataSupplier) {
            this.position = position;
            this.type = type;
            this.metadataSupplier = metadataSupplier;
            this.hasMetadataSupplier = hasMetadataSupplier;
        }

        @Override
        public BlockStateType getType() {
            return this.type;
        }

        @Override
        public IntVector3 getPosition() {
            return this.position;
        }

        @Override
        public boolean hasMetadata() {
            return this.hasMetadataSupplier.getAsBoolean();
        }

        @Override
        public CommonTagCompound getMetadata() {
            return this.metadataSupplier.get();
        }

        @Override
        public CommonTagCompound serialize() {
            CommonTagCompound serialized;
            CommonTagCompound commonTagCompound = serialized = this.hasMetadataSupplier.getAsBoolean() ? this.metadataSupplier.get().clone() : new CommonTagCompound();
            if (CommonCapabilities.TILE_ENTITY_LEGACY_NAMES) {
                serialized.putValue("id", TileEntityTypesSerializedIds_1_8_to_1_17_1.getLegacyName(this.type.getKey()));
            } else {
                serialized.putValue("id", this.type.getKey().toString());
            }
            serialized.putValue("x", this.position.x);
            serialized.putValue("y", this.position.y);
            serialized.putValue("z", this.position.z);
            return serialized;
        }
    }
}

