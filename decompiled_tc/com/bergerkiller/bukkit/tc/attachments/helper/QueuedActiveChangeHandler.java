/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.helper;

import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.helper.ActiveChangeHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueuedActiveChangeHandler
implements ActiveChangeHandler {
    private final Queue<PendingChange> queue = new ConcurrentLinkedQueue<PendingChange>();

    @Override
    public void scheduleActiveChange(Attachment attachment, boolean active) {
        this.queue.offer(new PendingChange(attachment, active));
    }

    public void sync() {
        try {
            for (PendingChange pending : this.queue) {
                pending.attachment.setActive(pending.active);
            }
        }
        finally {
            this.queue.clear();
        }
    }

    private static final class PendingChange {
        public final Attachment attachment;
        public final boolean active;

        public PendingChange(Attachment attachment, boolean active) {
            this.attachment = attachment;
            this.active = active;
        }
    }
}

