/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModelStore;
import com.bergerkiller.bukkit.tc.attachments.helper.AttachmentUpdateTransformHelper;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import com.bergerkiller.bukkit.tc.utils.SetCallbackCollector;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;

public class AttachmentControllerGroup
implements SavedAttachmentModelStore.ModelUsing,
AttachmentNameLookup.Supplier {
    public static final int ABSOLUTE_UPDATE_INTERVAL = 200;
    public static final int MOVEMENT_UPDATE_INTERVAL = 3;
    private final MinecartGroup group;
    private int movementCounter;
    private int ticksSinceLocationSync = 0;
    private SoftReference<AttachmentNameLookup> cachedByNameLookup = new SoftReference<Object>(null);

    public AttachmentControllerGroup(MinecartGroup group) {
        this.group = group;
    }

    public MinecartGroup getGroup() {
        return this.group;
    }

    public void syncPrePositionUpdate(AttachmentUpdateTransformHelper updater) {
        for (MinecartMember<?> member : this.group) {
            member.getAttachments().syncPrePositionUpdate(updater);
        }
    }

    public void syncPositionAbsolute() {
        this.ticksSinceLocationSync = 0;
        for (MinecartMember<?> member : this.group) {
            member.getAttachments().syncMovement(true);
        }
    }

    public void syncPostPositionUpdate() {
        block7: {
            boolean isUpdateTick;
            block6: {
                for (Object member : this.group) {
                    ((MinecartMember)member).getAttachments().syncPostPositionUpdate();
                }
                isUpdateTick = false;
                if (++this.movementCounter >= 3) {
                    this.movementCounter = 0;
                    isUpdateTick = true;
                }
                if (++this.ticksSinceLocationSync <= 200) break block6;
                this.ticksSinceLocationSync = 0;
                for (MinecartMember member : this.group) {
                    member.getAttachments().syncMovement(true);
                }
                break block7;
            }
            boolean needsSync = isUpdateTick;
            if (!needsSync) {
                for (MinecartMember<?> member : this.group) {
                    if (member.isUnloaded() || !((CommonMinecart)member.getEntity()).isPositionChanged() && !((CommonMinecart)member.getEntity()).getDataWatcher().isChanged()) continue;
                    needsSync = true;
                    break;
                }
            }
            if (!needsSync) break block7;
            for (MinecartMember<?> member : this.group) {
                member.getAttachments().syncMovement(false);
            }
        }
    }

    public void syncRespawn() {
        ArrayList<RespawnedMember> members = new ArrayList<RespawnedMember>(this.group.size());
        for (MinecartMember<?> member : this.group) {
            members.add(new RespawnedMember(member));
        }
        members.forEach(RespawnedMember::hide);
        this.group.getTrainCarts().getTrainUpdateController().syncPositions(Collections.singletonList(this.group));
        members.forEach(RespawnedMember::show);
    }

    @Override
    public void getUsedModels(SetCallbackCollector<SavedAttachmentModel> collector) {
        for (MinecartMember<?> member : this.group) {
            member.getAttachments().getUsedModels(collector);
        }
    }

    @Override
    public AttachmentNameLookup getNameLookup() {
        AttachmentNameLookup cached = this.cachedByNameLookup.get();
        if (cached == null || !cached.isValid()) {
            ArrayList<AttachmentNameLookup> components = new ArrayList<AttachmentNameLookup>(this.group.size());
            for (MinecartMember<?> member : this.group) {
                components.add(member.getAttachments().getNameLookup());
            }
            cached = AttachmentNameLookup.merge(components);
            this.cachedByNameLookup = new SoftReference<AttachmentNameLookup>(cached);
        }
        return cached;
    }

    public void notifyGroupCompositionChanged() {
        AttachmentNameLookup cached = this.cachedByNameLookup.get();
        if (cached != null) {
            cached.invalidate();
        }
    }

    private static class RespawnedMember {
        public final MinecartMember<?> member;
        private List<Player> players;

        public RespawnedMember(MinecartMember<?> member) {
            this.member = member;
            this.players = Collections.emptyList();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void hide() {
            AttachmentControllerMember attachmentControllerMember = this.member.getAttachments();
            synchronized (attachmentControllerMember) {
                this.players = new ArrayList<Player>(this.member.getAttachments().getViewers());
                this.member.getAttachments().makeHiddenForAll();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void show() {
            AttachmentControllerMember attachmentControllerMember = this.member.getAttachments();
            synchronized (attachmentControllerMember) {
                for (Player viewer : this.players) {
                    if (this.member.getAttachments().isViewer(viewer)) continue;
                    this.member.getAttachments().makeVisible(viewer);
                }
            }
        }
    }
}

