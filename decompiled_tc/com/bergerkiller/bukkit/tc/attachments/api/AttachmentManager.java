/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentInternalState;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentWorldFeatures;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface AttachmentManager {
    public World getWorld();

    public AttachmentWorldFeatures getWorldFeatures();

    public Collection<Player> getViewers();

    public Collection<AttachmentViewer> getAttachmentViewers();

    public AttachmentViewer asAttachmentViewer(Player var1);

    default public AttachmentTypeRegistry getTypeRegistry() {
        return AttachmentTypeRegistry.instance();
    }

    default public Attachment createAttachment(AttachmentConfig attachmentConfig) {
        AttachmentType attachmentType = this.getTypeRegistry().findOrEmpty(attachmentConfig.typeId());
        ConfigurationNode config = attachmentConfig.config();
        Attachment attachment = attachmentType.createController(config);
        AttachmentInternalState state = attachment.getInternalState();
        state.manager = this;
        state.rootParent = attachment;
        state.onLoad(this.getClass(), attachmentType, config);
        for (AttachmentConfig childAttachmentConfig : attachmentConfig.children()) {
            attachment.addChild(this.createAttachment(childAttachmentConfig));
        }
        return attachment;
    }

    public AttachmentNameLookup getNameLookup(Attachment var1);
}

