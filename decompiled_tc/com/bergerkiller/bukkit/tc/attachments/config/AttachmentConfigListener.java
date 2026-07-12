/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import java.util.function.Consumer;

public interface AttachmentConfigListener {
    default public void onChange(AttachmentConfig.Change change) {
        change.changeType().callback().accept(this, change.attachment());
    }

    default public void onAttachmentAdded(AttachmentConfig attachmentConfig) {
    }

    default public void onAttachmentRemoved(AttachmentConfig attachmentConfig) {
    }

    default public void onAttachmentChanged(AttachmentConfig attachmentConfig) {
    }

    default public void onSynchronized(AttachmentConfig rootAttachmentConfig) {
    }

    default public void onAttachmentAction(AttachmentConfig attachmentConfig, Consumer<Attachment> action) {
    }
}

