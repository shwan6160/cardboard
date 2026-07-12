/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle$ItemDisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.ArmorStandHandle;
import org.bukkit.inventory.ItemStack;

public class VirtualDisplayItemEntity
extends VirtualDisplayEntity
implements VirtualSpawnableObject.ItemDisplay {
    private static final DataWatcher MOUNT_METADATA = new DataWatcher();
    public static final DataWatcher.Prototype ITEM_DISPLAY_METADATA;
    public static final boolean IS_YAW_FLIPPED;
    private ItemDisplayMode mode = ItemDisplayMode.HEAD;
    private ItemStack item = null;
    private double clip = 0.0;
    private boolean appliedClip = false;

    public VirtualDisplayItemEntity(AttachmentManager manager) {
        super(manager, ITEM_DISPLAY_ENTITY_TYPE, ITEM_DISPLAY_METADATA.create());
    }

    public ItemDisplayMode getMode() {
        return this.mode;
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public void setItem(ItemStack item) {
        this.setItem(this.mode, item);
    }

    public void setItem(ItemDisplayMode mode, ItemStack item) {
        if (mode == null) {
            throw new IllegalArgumentException("Null dispay mode specified. Invalid transform type?");
        }
        if (!LogicUtil.bothNullOrEqual((Object)item, (Object)this.item) || this.mode != mode) {
            this.item = item;
            this.mode = mode;
            this.metadata.set(DisplayHandle.ItemDisplayHandle.DATA_ITEM_STACK, (Object)item);
            this.metadata.set(DisplayHandle.ItemDisplayHandle.DATA_ITEM_DISPLAY_MODE, (Object)mode);
            this.syncMeta();
        }
    }

    @Override
    protected void onScaleUpdated() {
        super.onScaleUpdated();
        this.applyClip();
    }

    @Override
    protected void onRotationUpdated(Quaternion rotation) {
        if (IS_YAW_FLIPPED) {
            rotation.rotateYFlip();
        }
    }

    public void setClip(double clip) {
        if (this.clip != clip) {
            this.clip = clip;
            this.applyClip();
        }
    }

    private void applyClip() {
        if (this.clip != 0.0) {
            this.appliedClip = true;
            float f = (float)(this.clip * 1.41421356274619 * Util.absMaxAxis(this.scale));
            this.metadata.set(DisplayHandle.ItemDisplayHandle.DATA_WIDTH, (Object)Float.valueOf(f));
            this.metadata.set(DisplayHandle.ItemDisplayHandle.DATA_HEIGHT, (Object)Float.valueOf(f));
        } else if (this.appliedClip) {
            this.metadata.set(DisplayHandle.ItemDisplayHandle.DATA_WIDTH, (Object)Float.valueOf(0.0f));
            this.metadata.set(DisplayHandle.ItemDisplayHandle.DATA_HEIGHT, (Object)Float.valueOf(0.0f));
        }
    }

    static {
        MOUNT_METADATA.set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        MOUNT_METADATA.set(EntityHandle.DATA_FLAGS, (Object)-96);
        MOUNT_METADATA.set(ArmorStandHandle.DATA_ARMORSTAND_FLAGS, (Object)25);
        ITEM_DISPLAY_METADATA = BASE_DISPLAY_METADATA.modify().setClientDefault(DisplayHandle.ItemDisplayHandle.DATA_ITEM_DISPLAY_MODE, (Object)ItemDisplayMode.NONE).set(DisplayHandle.ItemDisplayHandle.DATA_ITEM_DISPLAY_MODE, (Object)ItemDisplayMode.HEAD).setClientDefault(DisplayHandle.ItemDisplayHandle.DATA_ITEM_STACK, null).create();
        IS_YAW_FLIPPED = Common.evaluateMCVersion((String)"<=", (String)"1.19.4");
    }
}

