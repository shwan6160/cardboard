/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.ImplicitlySharedList
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.collections.ImplicitlySharedList;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigListener;
import com.bergerkiller.bukkit.tc.utils.ListCallbackCollector;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AttachmentConfigTrackerBase {
    private final ImplicitlySharedList<RemovableListener> listeners;
    private WeakRootReference cachedRoot;
    protected final Logger logger;

    public AttachmentConfigTrackerBase(Logger logger) {
        this.logger = logger;
        this.listeners = new ImplicitlySharedList();
        this.cachedRoot = new WeakRootReference();
    }

    protected abstract void startTracking();

    protected abstract void stopTracking();

    protected abstract AttachmentConfig.RootReference createRootReference();

    public abstract void sync();

    public final AttachmentConfig.RootReference getRoot() {
        AttachmentConfig.RootReference root = this.cachedRoot.getIfValid();
        if (root == null) {
            this.sync();
            root = this.createRootReference();
            this.cachedRoot = new WeakRootReference(root);
        }
        return root;
    }

    public AttachmentConfig startTracking(AttachmentConfigListener listener) {
        RemovableListener removableListener = new RemovableListener(listener);
        if (this.listeners.isEmpty()) {
            this.startTracking();
            this.listeners.add((Object)removableListener);
            AttachmentConfig.RootReference ref = this.createRootReference();
            this.cachedRoot.close();
            this.cachedRoot = new WeakRootReference(ref);
            return ref.get();
        }
        if (this.listeners.contains((Object)removableListener)) {
            throw new IllegalStateException("Listener already added");
        }
        this.sync();
        this.listeners.add((Object)removableListener);
        return this.getRoot().get();
    }

    public void stopTracking(AttachmentConfigListener listener) {
        Iterator iter = this.listeners.iterator();
        while (iter.hasNext()) {
            RemovableListener rl = (RemovableListener)iter.next();
            if (!rl.listener.equals(listener)) continue;
            rl.removed = true;
            iter.remove();
            if (this.listeners.isEmpty()) {
                this.stopTracking();
            }
            this.cachedRoot.close();
            this.cachedRoot = new WeakRootReference();
            break;
        }
    }

    public boolean isTracking() {
        return !this.listeners.isEmpty();
    }

    protected void notifyChanges(Collection<AttachmentConfig.Change> changes) {
        try (ImplicitlySharedList listeners = this.listeners.clone();){
            block7: for (RemovableListener removableListener : listeners) {
                for (AttachmentConfig.Change change : changes) {
                    if (removableListener.removed) continue block7;
                    try {
                        removableListener.listener.onChange(change);
                    }
                    catch (Throwable t) {
                        this.logger.log(Level.SEVERE, "Failed to notify an attachment was " + (Object)((Object)change.changeType()), t);
                    }
                }
            }
        }
    }

    protected void notifyChange(AttachmentConfig.ChangeType changeType, AttachmentConfig attachment) {
        this.notifyChanges(Collections.singleton(new AttachmentConfig.Change(changeType, attachment)));
    }

    protected void runAttachmentAction(AttachmentConfig attachment, Consumer<Attachment> action) {
        if (this.listeners.isEmpty()) {
            return;
        }
        try (ImplicitlySharedList listeners = this.listeners.clone();){
            for (RemovableListener removableListener : listeners) {
                if (removableListener.removed) continue;
                try {
                    removableListener.listener.onAttachmentAction(attachment, action);
                }
                catch (Throwable t) {
                    this.logger.log(Level.SEVERE, "Failed to run attachment action", t);
                }
            }
        }
    }

    public void runAction(int[] childPath, Consumer<Attachment> action) {
        if (!this.listeners.isEmpty()) {
            AttachmentConfig config;
            this.sync();
            if (!this.listeners.isEmpty() && (config = this.getRoot().get().child(childPath)) != null) {
                config.runAction(action);
            }
        }
    }

    public void runAction(YamlPath relativePath, Consumer<Attachment> action) {
        if (!this.listeners.isEmpty()) {
            AttachmentConfig config;
            this.sync();
            if (!this.listeners.isEmpty() && (config = this.getRoot().get().child(relativePath)) != null) {
                config.runAction(action);
            }
        }
    }

    public List<Attachment> liveAttachments(int[] childPath) {
        ListCallbackCollector<Attachment> collector = new ListCallbackCollector<Attachment>();
        this.runAction(childPath, collector);
        return collector.result();
    }

    public List<Attachment> liveAttachments(YamlPath relativePath) {
        ListCallbackCollector<Attachment> collector = new ListCallbackCollector<Attachment>();
        this.runAction(relativePath, collector);
        return collector.result();
    }

    private static class WeakRootReference {
        private final WeakReference<AttachmentConfig.RootReference> reference;
        private final AttachmentConfig.RootReference.ValidChecker checker;

        public WeakRootReference() {
            this.reference = LogicUtil.nullWeakReference();
            this.checker = () -> false;
        }

        public WeakRootReference(AttachmentConfig.RootReference ref) {
            this.reference = new WeakReference<AttachmentConfig.RootReference>(ref);
            this.checker = ref.getValidChecker();
        }

        public AttachmentConfig.RootReference getIfValid() {
            AttachmentConfig.RootReference ref = (AttachmentConfig.RootReference)this.reference.get();
            if (ref == null) {
                this.checker.close();
                return null;
            }
            return ref.valid() ? ref : null;
        }

        public void close() {
            AttachmentConfig.RootReference ref = (AttachmentConfig.RootReference)this.reference.get();
            if (ref != null) {
                ref.invalidate();
            } else {
                this.checker.close();
            }
        }
    }

    private static class RemovableListener {
        public final AttachmentConfigListener listener;
        public boolean removed;

        public RemovableListener(AttachmentConfigListener listener) {
            this.listener = listener;
            this.removed = false;
        }

        public boolean equals(Object o) {
            return this.listener.equals(((RemovableListener)o).listener);
        }
    }
}

