/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 */
package com.bergerkiller.bukkit.tc.attachments.helper;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.helper.QueuedActiveChangeHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public abstract class AttachmentUpdateTransformHelper {
    protected final QueuedActiveChangeHandler activeChangeHandler = new QueuedActiveChangeHandler();

    public static AttachmentUpdateTransformHelper createSimple() {
        return new AttachmentUpdateHelperSingleThreaded();
    }

    public static AttachmentUpdateTransformHelper create(int parallelism) {
        if (parallelism <= 0) {
            parallelism = Runtime.getRuntime().availableProcessors();
        }
        if (parallelism > 1) {
            return new AttachmentUpdateHelperMultiThreaded(parallelism);
        }
        return new AttachmentUpdateHelperSingleThreaded();
    }

    protected AttachmentUpdateTransformHelper() {
    }

    public final void startAndFinish(Attachment attachment, Matrix4x4 initialTransform) {
        try {
            this.start(attachment, initialTransform);
        }
        finally {
            this.finish();
        }
    }

    public abstract void start(Attachment var1, Matrix4x4 var2);

    public abstract void finish();

    private static final class AttachmentUpdateHelperSingleThreaded
    extends AttachmentUpdateTransformHelper {
        private final ArrayList<Attachment> pendingUpdates = new ArrayList();

        private AttachmentUpdateHelperSingleThreaded() {
        }

        @Override
        public void start(Attachment attachment, Matrix4x4 initialTransform) {
            attachment.getInternalState().updateTransform(attachment, initialTransform, this.activeChangeHandler);
            this.pendingUpdates.addAll(attachment.getChildren());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void finish() {
            try {
                int endIndex;
                int startIndex = 0;
                while (startIndex < (endIndex = this.pendingUpdates.size())) {
                    for (int index = startIndex; index < endIndex; ++index) {
                        Attachment attachment = this.pendingUpdates.get(index);
                        attachment.getInternalState().updateTransform(attachment, attachment.getParent().getTransform(), this.activeChangeHandler);
                        this.pendingUpdates.addAll(attachment.getChildren());
                    }
                    startIndex = endIndex;
                }
            }
            finally {
                this.pendingUpdates.clear();
                this.activeChangeHandler.sync();
            }
        }
    }

    private static final class AttachmentUpdateHelperMultiThreaded
    extends AttachmentUpdateTransformHelper {
        private final List<ForkJoinTask<Void>> pendingTasks = new ArrayList<ForkJoinTask<Void>>();
        private final ForkJoinPool pool;

        public AttachmentUpdateHelperMultiThreaded(int parallelism) {
            this.pool = new ForkJoinPool(parallelism, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, false);
        }

        @Override
        public void start(Attachment attachment, Matrix4x4 initialTransform) {
            ForkJoinTask<Void> task = attachment.getInternalState().updateTransformRecurseAsync(attachment, initialTransform, this.activeChangeHandler);
            this.pendingTasks.add(task);
            this.pool.execute(task);
        }

        @Override
        public void finish() {
            try {
                for (int i = this.pendingTasks.size() - 1; i >= 0; --i) {
                    this.pendingTasks.get(i).join();
                }
            }
            finally {
                this.pendingTasks.clear();
                this.activeChangeHandler.sync();
            }
        }
    }
}

