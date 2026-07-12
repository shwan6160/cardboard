/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigListener;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigTracker;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigTrackerBase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public abstract class AttachmentConfigModelTracker
extends AttachmentConfigTrackerBase {
    private DeepAttachmentConfig root = null;
    private final DeepAttachmentTrackerProxy proxy;
    private int numProxiesSynchronizing = 0;
    private int modificationCount = 0;

    public AttachmentConfigModelTracker(AttachmentConfigTracker tracker) {
        this(tracker, null);
    }

    public AttachmentConfigModelTracker(AttachmentConfigTracker tracker, Plugin plugin) {
        super(plugin == null ? Logger.getGlobal() : plugin.getLogger());
        this.proxy = new DeepAttachmentTrackerProxy(tracker, true){

            @Override
            public DeepAttachmentConfig getRoot() {
                return AttachmentConfigModelTracker.this.root;
            }

            @Override
            public void setRoot(AttachmentConfig baseRoot) {
                AttachmentConfigModelTracker.this.root = baseRoot == null ? null : AttachmentConfigModelTracker.this.createNewRoot(baseRoot, null);
            }
        };
    }

    public abstract AttachmentConfigTracker findModelConfig(String var1);

    @Override
    protected void startTracking() {
        this.proxy.start();
        ++this.modificationCount;
    }

    @Override
    protected void stopTracking() {
        this.root.onRemoved();
        this.proxy.stop();
        this.root = null;
        ++this.modificationCount;
    }

    @Override
    protected AttachmentConfig.RootReference createRootReference() {
        if (this.isTracking()) {
            int currModCount = this.modificationCount;
            return new AttachmentConfig.RootReference(this.root, () -> this.modificationCount == currModCount);
        }
        AttachmentConfig.RootReference mainBaseRootRef = this.proxy.tracker.createRootReference();
        ArrayList<AttachmentConfig.RootReference> allRootsUsed = new ArrayList<AttachmentConfig.RootReference>();
        allRootsUsed.add(mainBaseRootRef);
        DeepAttachmentConfig tempRoot = this.createNewRoot(mainBaseRootRef.get(), allRootsUsed);
        if (allRootsUsed.size() == 1) {
            return new AttachmentConfig.RootReference(tempRoot, mainBaseRootRef.getValidChecker());
        }
        return new AttachmentConfig.RootReference(tempRoot, () -> {
            for (AttachmentConfig.RootReference ref : allRootsUsed) {
                if (ref.valid()) continue;
                return false;
            }
            return true;
        });
    }

    @Override
    public void sync() {
    }

    private DeepAttachmentConfig createNewRoot(AttachmentConfig base, List<AttachmentConfig.RootReference> modelRoots) {
        return this.createAttachmentConfig(PositionAccess.DEFAULT, null, base, modelRoots);
    }

    private DeepAttachmentConfig createAttachmentConfig(PositionAccess position, DeepAttachmentConfig parent, AttachmentConfig base, List<AttachmentConfig.RootReference> modelRoots) {
        if (base == null) {
            throw new IllegalArgumentException("Base attachment configuration cannot be null");
        }
        DeepAttachmentConfig config = base instanceof AttachmentConfig.Model ? new DeepModelAttachmentConfig(position, parent, (AttachmentConfig.Model)base, modelRoots) : new DeepAttachmentConfig(position, parent, base);
        config.initChildren(modelRoots);
        return config;
    }

    private static interface PositionAccess {
        public static final PositionAccess DEFAULT = new PositionAccess(){

            @Override
            public int childIndex(AttachmentConfig base) {
                return base.childIndex();
            }

            @Override
            public YamlPath path(AttachmentConfig base) {
                return base.path();
            }

            @Override
            public PositionAccess forChildren() {
                return this;
            }
        };

        public int childIndex(AttachmentConfig var1);

        public YamlPath path(AttachmentConfig var1);

        public PositionAccess forChildren();

        default public boolean isRemoved() {
            return false;
        }

        default public PositionAccess removed(AttachmentConfig base) {
            return new PositionAccessRemoved(this.childIndex(base), this.path(base));
        }
    }

    private class DeepAttachmentConfig
    implements AttachmentConfig {
        protected PositionAccess position;
        private final DeepAttachmentConfig parent;
        private final AttachmentConfig base;
        protected final ArrayList<DeepAttachmentConfig> children;

        public DeepAttachmentConfig(PositionAccess position, DeepAttachmentConfig parent, AttachmentConfig base) {
            this.position = position;
            this.parent = parent;
            this.base = base;
            this.children = new ArrayList(base.children().size() + 1);
        }

        public void initChildren(List<AttachmentConfig.RootReference> modelRoots) {
            for (AttachmentConfig baseChild : this.base.children()) {
                this.children.add(AttachmentConfigModelTracker.this.createAttachmentConfig(this.position.forChildren(), this, baseChild, modelRoots));
            }
        }

        @Override
        public AttachmentConfig parent() {
            return this.parent;
        }

        @Override
        public List<AttachmentConfig> children() {
            return Collections.unmodifiableList(this.children);
        }

        @Override
        public DeepAttachmentConfig child(int childIndex) {
            ArrayList<DeepAttachmentConfig> children = this.children;
            if (childIndex < 0 || childIndex >= children.size()) {
                return null;
            }
            return (DeepAttachmentConfig)children.get(childIndex);
        }

        public DeepAttachmentConfig nonModelChild(int childIndex) {
            return this.child(childIndex);
        }

        public final DeepAttachmentConfig nonModelChild(int[] childPath) {
            DeepAttachmentConfig p = this;
            int len = childPath.length;
            if (len > 0) {
                int i = 0;
                do {
                    p = p.nonModelChild(childPath[i]);
                } while (++i < len && p != null);
            }
            return p;
        }

        @Override
        public AttachmentConfig addChild(int childIndex, ConfigurationNode config) {
            throw new UnsupportedOperationException("Model attachment configurations cannot be added");
        }

        @Override
        public boolean isRemoved() {
            return this.position.isRemoved();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Model attachment configurations cannot be removed");
        }

        @Override
        public int childIndex() {
            return this.position.childIndex(this.base);
        }

        @Override
        public YamlPath path() {
            return this.position.path(this.base);
        }

        @Override
        public String typeId() {
            return this.base.typeId();
        }

        @Override
        public ConfigurationNode config() {
            return this.base.config();
        }

        @Override
        public boolean isEmptyConfig() {
            return this.base.isEmptyConfig();
        }

        @Override
        public void runAction(Consumer<Attachment> action) {
            if (!this.position.isRemoved()) {
                AttachmentConfigModelTracker.this.runAttachmentAction(this, action);
            }
        }

        public String toString() {
            return "Attachment{" + this.typeId() + " at " + Arrays.toString(this.childPath()) + "}";
        }

        public final boolean isModelUsed(String name) {
            DeepAttachmentConfig att = this;
            while (att != null) {
                if (att.isModelUsedSelf(name)) {
                    return true;
                }
                att = att.parent;
            }
            return false;
        }

        protected boolean isModelUsedSelf(String name) {
            return false;
        }

        public void addDeepChildAtPath(int[] path, AttachmentConfig base) {
            this.runActionForChild(path, (parent, childIndex) -> parent.addDeepChild(childIndex, base));
        }

        protected void addDeepChild(int childIndex, AttachmentConfig baseConfig) {
            if (childIndex < 0 || childIndex > this.children.size()) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            }
            DeepAttachmentConfig deepConfig = AttachmentConfigModelTracker.this.createAttachmentConfig(this.position.forChildren(), this, baseConfig, null);
            this.children.add(childIndex, deepConfig);
            AttachmentConfigModelTracker.this.notifyChange(AttachmentConfig.ChangeType.ADDED, deepConfig);
        }

        public void removeDeepChildAtPath(int[] childPath) {
            this.runActionForChild(childPath, DeepAttachmentConfig::removeDeepChild);
        }

        protected void removeDeepChild(int childIndex) {
            DeepAttachmentConfig deepConfig = this.children.get(childIndex);
            deepConfig.onRemoved();
            this.children.remove(childIndex);
            AttachmentConfigModelTracker.this.notifyChange(AttachmentConfig.ChangeType.REMOVED, deepConfig);
        }

        private void runActionForChild(int[] path, ChildActionConsumer action) {
            int i;
            DeepAttachmentConfig parent = this;
            int limit = path.length - 1;
            for (i = 0; i < limit; ++i) {
                parent = parent.children.get(path[i]);
            }
            action.accept(parent, path[i]);
        }

        protected void onRemoved() {
            this.position = this.position.removed(this.base);
            for (DeepAttachmentConfig child : this.children) {
                child.onRemoved();
            }
        }
    }

    private abstract class DeepAttachmentTrackerProxy
    implements AttachmentConfigListener {
        private final AttachmentConfigTracker tracker;
        private final boolean allowEmptyRootConfig;
        private boolean isSynchronizing = false;

        public abstract DeepAttachmentConfig getRoot();

        public abstract void setRoot(AttachmentConfig var1);

        public DeepAttachmentTrackerProxy(AttachmentConfigTracker tracker, boolean allowEmptyRootConfig) {
            this.tracker = tracker;
            this.allowEmptyRootConfig = allowEmptyRootConfig;
        }

        public final void start() {
            AttachmentConfig newRoot = this.tracker.startTracking(this);
            this.setRoot(this.allowEmptyRootConfig || !newRoot.isEmptyConfig() ? newRoot : null);
        }

        public final void stop() {
            this.tracker.stopTracking(this);
            this.notifyDoneSynchronizing();
        }

        private void notifyStartSynchronizing() {
            if (!this.isSynchronizing) {
                this.isSynchronizing = true;
                AttachmentConfigModelTracker.this.numProxiesSynchronizing++;
            }
        }

        private void notifyDoneSynchronizing() {
            if (this.isSynchronizing) {
                this.isSynchronizing = false;
                int numNowSynchronizing = --AttachmentConfigModelTracker.this.numProxiesSynchronizing;
                if (numNowSynchronizing < 0) {
                    AttachmentConfigModelTracker.this.numProxiesSynchronizing = 0;
                    throw new IllegalStateException("Number of trackers synchronizing went negative");
                }
                if (numNowSynchronizing == 0 && AttachmentConfigModelTracker.this.root != null) {
                    AttachmentConfigModelTracker.this.notifyChange(AttachmentConfig.ChangeType.SYNCHRONIZED, AttachmentConfigModelTracker.this.root);
                }
            }
        }

        @Override
        public void onChange(AttachmentConfig.Change change) {
            AttachmentConfigModelTracker.this.modificationCount++;
            if (change.changeType() == AttachmentConfig.ChangeType.SYNCHRONIZED) {
                this.notifyDoneSynchronizing();
            } else {
                this.notifyStartSynchronizing();
            }
            change.changeType().callback().accept(this, change.attachment());
        }

        @Override
        public void onAttachmentAdded(AttachmentConfig attachment) {
            int[] path = attachment.childPath();
            if (path.length == 0) {
                if (this.getRoot() != null) {
                    throw new IllegalStateException("Root being re-added while one already exists");
                }
                AttachmentConfig newRoot = this.allowEmptyRootConfig || !attachment.isEmptyConfig() ? attachment : null;
                this.setRoot(newRoot);
                if (newRoot != null) {
                    AttachmentConfigModelTracker.this.notifyChange(AttachmentConfig.ChangeType.ADDED, this.getRoot());
                }
            } else {
                DeepAttachmentConfig root = this.getRoot();
                if (root == null) {
                    throw new IllegalStateException("Root child being added while root was removed");
                }
                root.addDeepChildAtPath(path, attachment);
            }
        }

        @Override
        public void onAttachmentRemoved(AttachmentConfig attachment) {
            int[] path = attachment.childPath();
            DeepAttachmentConfig root = this.getRoot();
            if (path.length == 0) {
                if (root == null) {
                    if (this.allowEmptyRootConfig || !attachment.isEmptyConfig()) {
                        throw new IllegalStateException("Root being removed, but root was already removed");
                    }
                    return;
                }
                root.onRemoved();
                this.setRoot(null);
                AttachmentConfigModelTracker.this.notifyChange(AttachmentConfig.ChangeType.REMOVED, root);
            } else {
                if (root == null) {
                    throw new IllegalStateException("Root child being removed, but root was already removed");
                }
                root.removeDeepChildAtPath(path);
            }
        }

        @Override
        public void onAttachmentChanged(AttachmentConfig attachment) {
            DeepAttachmentConfig root = this.getRoot();
            if (root == null) {
                if (!this.allowEmptyRootConfig && attachment.isRoot()) {
                    if (!attachment.isEmptyConfig()) {
                        this.onAttachmentAdded(attachment);
                    }
                    return;
                }
                throw new IllegalStateException("Root changed, but root was already removed");
            }
            if (attachment.isRoot() && !this.allowEmptyRootConfig && attachment.isEmptyConfig()) {
                this.onAttachmentRemoved(attachment);
                return;
            }
            DeepAttachmentConfig child = root.nonModelChild(attachment.childPath());
            if (child == null) {
                throw new IllegalStateException("An attachment changed that did not exist in this tracker");
            }
            AttachmentConfigModelTracker.this.notifyChange(AttachmentConfig.ChangeType.CHANGED, child);
        }

        @Override
        public void onAttachmentAction(AttachmentConfig attachment, Consumer<Attachment> action) {
            DeepAttachmentConfig root = this.getRoot();
            if (root == null) {
                if (!this.allowEmptyRootConfig && attachment.isEmptyConfig() && attachment.childPath().length == 0) {
                    return;
                }
                throw new IllegalStateException("Action on an attachment of root, but root was already removed");
            }
            DeepAttachmentConfig child = root.nonModelChild(attachment.childPath());
            if (child == null) {
                throw new IllegalStateException("Action on an attachment that did not exist in this tracker");
            }
            AttachmentConfigModelTracker.this.runAttachmentAction(child, action);
        }
    }

    private class DeepModelAttachmentConfig
    extends DeepAttachmentConfig
    implements AttachmentConfig.Model {
        private final AttachmentConfig.Model baseModel;
        private final AttachmentConfigTracker modelTracker;
        private final DeepAttachmentTrackerProxy proxy;
        private DeepAttachmentConfig modelChild;

        public DeepModelAttachmentConfig(PositionAccess position, DeepAttachmentConfig parent, AttachmentConfig.Model base, List<AttachmentConfig.RootReference> modelRoots) {
            super(position, parent, base);
            this.baseModel = base;
            if (parent != null && parent.isModelUsed(base.modelName())) {
                this.modelChild = null;
                this.proxy = null;
                this.modelTracker = null;
                return;
            }
            this.modelTracker = AttachmentConfigModelTracker.this.findModelConfig(base.modelName());
            if (modelRoots != null) {
                this.proxy = null;
                return;
            }
            this.proxy = new DeepAttachmentTrackerProxy(this.modelTracker, false){

                @Override
                public DeepAttachmentConfig getRoot() {
                    return DeepModelAttachmentConfig.this.modelChild;
                }

                @Override
                public void setRoot(AttachmentConfig baseRoot) {
                    if (baseRoot == null) {
                        if (!DeepModelAttachmentConfig.this.children.isEmpty() && DeepModelAttachmentConfig.this.children.get(DeepModelAttachmentConfig.this.children.size() - 1) == DeepModelAttachmentConfig.this.modelChild) {
                            DeepModelAttachmentConfig.this.children.remove(DeepModelAttachmentConfig.this.children.size() - 1);
                        }
                        DeepModelAttachmentConfig.this.modelChild = null;
                    } else {
                        DeepModelAttachmentConfig.this.modelChild = AttachmentConfigModelTracker.this.createAttachmentConfig(new PositionAccessModelChild(DeepModelAttachmentConfig.this), DeepModelAttachmentConfig.this, baseRoot, null);
                        DeepModelAttachmentConfig.this.children.add(DeepModelAttachmentConfig.this.modelChild);
                    }
                }
            };
        }

        @Override
        public void initChildren(List<AttachmentConfig.RootReference> modelRoots) {
            if (this.modelTracker == null) {
                return;
            }
            super.initChildren(modelRoots);
            if (modelRoots != null) {
                AttachmentConfig.RootReference modelRoot = this.modelTracker.getRoot();
                modelRoots.add(modelRoot);
                if (modelRoot.get().isEmptyConfig()) {
                    this.modelChild = null;
                    return;
                }
                this.modelChild = AttachmentConfigModelTracker.this.createAttachmentConfig(new PositionAccessModelChild(this), this, modelRoot.get(), modelRoots);
                this.children.add(this.modelChild);
            }
            if (this.proxy != null) {
                this.proxy.start();
            }
        }

        @Override
        protected boolean isModelUsedSelf(String name) {
            return this.baseModel.modelName().equals(name);
        }

        @Override
        protected void onRemoved() {
            super.onRemoved();
            if (this.proxy != null) {
                this.proxy.stop();
                this.modelChild = null;
            }
        }

        public int getModelChildIndex() {
            int index = this.children.size() - 1;
            if (this.modelChild == null || index == -1) {
                throw new IllegalStateException("Model configuration does not store a model attachment");
            }
            return index;
        }

        public YamlPath getModelPath() {
            int index = this.getModelChildIndex();
            if (index == 0) {
                return this.path().child("attachments").child("0");
            }
            YamlPath siblingPath = ((DeepAttachmentConfig)this.children.get(0)).path();
            if (siblingPath.isListElement()) {
                return siblingPath.parent().listChild(index);
            }
            return siblingPath.parent().child(Integer.toString(index));
        }

        @Override
        protected void addDeepChild(int childIndex, AttachmentConfig baseConfig) {
            if (this.modelChild != null && childIndex == this.children.size()) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            }
            super.addDeepChild(childIndex, baseConfig);
        }

        @Override
        protected void removeDeepChild(int childIndex) {
            if (this.modelChild != null && childIndex == this.children.size() - 1) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            }
            super.removeDeepChild(childIndex);
        }

        @Override
        public DeepAttachmentConfig nonModelChild(int childIndex) {
            ArrayList children = this.children;
            if (childIndex < 0 || childIndex >= (this.modelChild == null ? children.size() : children.size() - 1)) {
                return null;
            }
            return (DeepAttachmentConfig)children.get(childIndex);
        }

        @Override
        public String modelName() {
            return this.baseModel.modelName();
        }
    }

    private static class PositionAccessModelChild
    implements PositionAccess {
        private final DeepModelAttachmentConfig parent;
        private final PositionAccess forChildren;

        public PositionAccessModelChild(DeepModelAttachmentConfig parent) {
            this.parent = parent;
            this.forChildren = new PositionAccess(){

                @Override
                public int childIndex(AttachmentConfig base) {
                    return base.childIndex();
                }

                @Override
                public YamlPath path(AttachmentConfig base) {
                    YamlPath parentPath = parent.getModelPath();
                    return YamlPath.join((YamlPath)parentPath, (YamlPath)base.path());
                }

                @Override
                public PositionAccess forChildren() {
                    return this;
                }
            };
        }

        @Override
        public int childIndex(AttachmentConfig base) {
            return this.parent.getModelChildIndex();
        }

        @Override
        public YamlPath path(AttachmentConfig base) {
            return this.parent.getModelPath();
        }

        @Override
        public PositionAccess forChildren() {
            return this.forChildren;
        }
    }

    private static class PositionAccessRemoved
    implements PositionAccess {
        private final int childIndex;
        private final YamlPath path;

        public PositionAccessRemoved(int childIndex, YamlPath path) {
            this.childIndex = childIndex;
            this.path = path;
        }

        @Override
        public int childIndex(AttachmentConfig base) {
            return this.childIndex;
        }

        @Override
        public YamlPath path(AttachmentConfig base) {
            return this.path;
        }

        @Override
        public PositionAccess forChildren() {
            throw new UnsupportedOperationException("Can't add children to removed attachments");
        }

        @Override
        public boolean isRemoved() {
            return true;
        }

        @Override
        public PositionAccess removed(AttachmentConfig base) {
            return this;
        }
    }

    @FunctionalInterface
    private static interface ChildActionConsumer {
        public void accept(DeepAttachmentConfig var1, int var2);
    }
}

