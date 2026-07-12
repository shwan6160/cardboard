/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityPositionApplier;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.wrappers.DamageSource;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class EntityController<T extends CommonEntity<?>>
extends CommonEntityController<T> {
    private EntityHook hook = null;
    private final EntityMoveHandler moveHandler = EntityMoveHandler.create(this);

    public final void bind(CommonEntity<?> entity, boolean handleAttachment) {
        if (entity == null && this.hook == null) {
            throw new RuntimeException("WTF");
        }
        if (entity != null && entity.getWorld() == null) {
            throw new RuntimeException("Can not bind to an Entity that has no world set");
        }
        if (this.entity != null) {
            this.onDetached();
            this.markEntityChunkDirty();
        }
        this.entity = entity;
        if (this.entity != null) {
            this.hook = EntityHook.get(this.entity.getHandle(), EntityHook.class);
            if (this.hook == null) {
                this.hook = new EntityHook();
                this.hook.mock(this.entity.getHandle());
            }
            this.hook.setController(this);
            if (handleAttachment) {
                this.onAttached();
            }
            if (!(this instanceof DefaultEntityController)) {
                this.markEntityChunkDirty();
            }
        }
    }

    private void markEntityChunkDirty() {
        Chunk chunk = this.entity.getChunk();
        if (chunk != null) {
            LevelChunkHandle.fromBukkit(chunk).markEntitiesDirty();
        }
    }

    public void onDie(boolean killed) {
        this.hook.onBaseDeath(killed);
    }

    @Override
    public void onTick() {
        ((EntityHook)this.hook.base).onTick();
    }

    public InteractionResult onInteractBy(HumanEntity interacter, HumanHand hand, Vector location) {
        return this.hook.base_onInteractBy(interacter, hand, location);
    }

    public boolean onDamage(DamageSource damageSource, double damage) {
        return this.hook.baseDamageEntity(damageSource.getRawHandle(), (float)damage);
    }

    public boolean onEntityCollision(Entity e) {
        return true;
    }

    public void onEntityBump(Entity e) {
        ((EntityHook)this.hook.base).collide(HandleConversion.toEntityHandle(e));
    }

    public boolean onBlockCollision(Block block, BlockFace hitFace) {
        return true;
    }

    public String getLocalizedName() {
        return this.hook.getStringUUID_base(((ExtendedEntity)this.getEntity()).getHandle());
    }

    public void onPush(double dx, double dy, double dz) {
        this.hook.baseOnPush(dx, dy, dz);
    }

    public void onMove(MoveType moveType, double dx, double dy, double dz) {
        this.moveHandler.move(this.entity.getWrappedHandle(), moveType, dx, dy, dz);
    }

    public void onItemSet(int index, ItemStack item) {
        ((EntityHook)this.hook.base).setInventoryItem(index, HandleConversion.toItemStackHandle(item));
    }

    public void onPositionPassenger(Entity passenger, EntityPositionApplier applier) {
        this.hook.basePositionRider(passenger, applier);
    }

    public boolean isPlayerTakeable() {
        return true;
    }

    public void setBlockCollisionEnabled(boolean enabled) {
        this.moveHandler.setBlockCollisionEnabled(enabled);
    }

    public void setBlockActivationEnabled(boolean enabled) {
        this.moveHandler.setBlockActivationEnabled(enabled);
    }

    public void setEntityCollisionEnabled(boolean enabled) {
        this.moveHandler.setEntityCollisionEnabled(enabled);
    }

    public void setBlockCollisionBounds(Vector bounds) {
        this.moveHandler.setCustomBlockCollisionBounds(bounds);
    }
}

