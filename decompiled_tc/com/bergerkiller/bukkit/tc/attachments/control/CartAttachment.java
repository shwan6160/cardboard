/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.generated.net.minecraft.world.entity.monster.ShulkerHandle
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentInternalState;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentBlock;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentEmpty;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentEntity;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentHitBox;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentPlatform;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSchematic;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSequencer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSound;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentText;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import com.bergerkiller.generated.net.minecraft.world.entity.monster.ShulkerHandle;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class CartAttachment
implements Attachment {
    private final AttachmentInternalState state = new AttachmentInternalState();

    @Override
    public AttachmentInternalState getInternalState() {
        return this.state;
    }

    @Override
    public void onAttached() {
    }

    @Override
    public void onDetached() {
    }

    @Override
    public void onLoad(ConfigurationNode config) {
    }

    @Override
    public Collection<Player> getViewers() {
        return this.getManager().getViewers();
    }

    @Override
    public Collection<AttachmentViewer> getAttachmentViewers() {
        return this.getManager().getAttachmentViewers();
    }

    public boolean hasController() {
        return this.getManager() instanceof AttachmentControllerMember;
    }

    public AttachmentControllerMember getController() {
        return (AttachmentControllerMember)this.getManager();
    }

    public MinecartMember<?> getMember() {
        return this.getController().getMember();
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return false;
    }

    public int getMountEntityId() {
        return -1;
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
    }

    @Deprecated
    protected void updateGlowColorFor(UUID entityUUID, ChatColor color, Player viewer) {
        this.updateGlowColorFor(entityUUID, color, this.getManager().asAttachmentViewer(viewer));
    }

    @Deprecated
    protected void updateGlowColorFor(UUID entityUUID, ChatColor color, AttachmentViewer viewer) {
        viewer.updateGlowColor(entityUUID, color);
    }

    protected void updateGlowColor(UUID entityUUID, ChatColor color) {
        for (AttachmentViewer viewer : this.getAttachmentViewers()) {
            viewer.updateGlowColor(entityUUID, color);
        }
    }

    public static void registerDefaultAttachments() {
        AttachmentTypeRegistry.instance().register(CartAttachmentEmpty.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentEntity.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentItem.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentModel.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentSeat.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentText.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentHitBox.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentSound.TYPE);
        AttachmentTypeRegistry.instance().register(CartAttachmentSequencer.TYPE);
        if (ShulkerHandle.T.isAvailable()) {
            AttachmentTypeRegistry.instance().register(CartAttachmentPlatform.TYPE);
        }
        if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
            AttachmentTypeRegistry.instance().register(CartAttachmentBlock.TYPE);
            if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
                AttachmentTypeRegistry.instance().register(CartAttachmentSchematic.TYPE);
            }
        }
    }
}

