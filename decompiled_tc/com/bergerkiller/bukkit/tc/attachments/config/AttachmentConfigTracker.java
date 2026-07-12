/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.RunOnceTask
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.RunOnceTask;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigTrackerBase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public class AttachmentConfigTracker
extends AttachmentConfigTrackerBase
implements YamlChangeListener {
    private final Supplier<ConfigurationNode> completeConfigSupplier;
    private ConfigurationNode completeConfig;
    private final SyncTask syncTask;
    private final Map<ConfigurationNode, TrackedAttachmentConfig> byConfig;
    private final List<AttachmentConfig.Change> pendingChanges;
    private final Set<TrackedAttachmentConfig> attachmentsWithChanges = new LinkedHashSet<TrackedAttachmentConfig>();
    private boolean isSynchronizing = false;
    private TrackedAttachmentConfig root;
    private int modificationCount = 0;

    public AttachmentConfigTracker(ConfigurationNode completeConfig) {
        this(completeConfig, null);
    }

    public AttachmentConfigTracker(ConfigurationNode completeConfig, Plugin plugin) {
        this(LogicUtil.constantSupplier((Object)completeConfig), plugin);
    }

    public AttachmentConfigTracker(Supplier<ConfigurationNode> completeConfigSupplier) {
        this(completeConfigSupplier, null);
    }

    public AttachmentConfigTracker(Supplier<ConfigurationNode> completeConfigSupplier, Plugin plugin) {
        super(plugin == null ? Logger.getGlobal() : plugin.getLogger());
        this.completeConfigSupplier = completeConfigSupplier;
        this.completeConfig = null;
        this.syncTask = plugin == null ? null : new SyncTask(plugin);
        this.byConfig = new IdentityHashMap<ConfigurationNode, TrackedAttachmentConfig>();
        this.pendingChanges = new ArrayList<AttachmentConfig.Change>();
        this.root = null;
    }

    @Override
    protected void startTracking() {
        this.completeConfig = this.completeConfigSupplier.get();
        if (this.completeConfig == null) {
            this.completeConfig = new ConfigurationNode();
        }
        this.root = this.createNewRoot(this.completeConfig);
        this.root.addToTracker();
        this.completeConfig.addChangeListener((YamlChangeListener)this);
        this.resetChanges();
        ++this.modificationCount;
    }

    @Override
    protected void stopTracking() {
        ++this.modificationCount;
        this.completeConfig.removeChangeListener((YamlChangeListener)this);
        this.completeConfig = null;
        this.resetChanges();
        this.root = null;
        if (this.syncTask != null) {
            this.syncTask.cancel();
        }
    }

    private void resetChanges() {
        this.pendingChanges.clear();
        this.attachmentsWithChanges.clear();
    }

    @Override
    protected AttachmentConfig.RootReference createRootReference() {
        if (this.isTracking()) {
            int currModCount = this.modificationCount;
            return new AttachmentConfig.RootReference(this.root, () -> this.modificationCount == currModCount);
        }
        ConfigurationNode configSnapshot = this.completeConfigSupplier.get();
        if (configSnapshot == null) {
            configSnapshot = new ConfigurationNode();
        }
        TrackedAttachmentConfig tempRoot = this.createNewRoot(configSnapshot);
        return new AttachmentConfig.RootReference(tempRoot, new ConfigInvalidChecker(configSnapshot));
    }

    public ConfigurationNode getConfig() {
        ConfigurationNode config = this.completeConfig;
        return config != null ? config : this.completeConfigSupplier.get();
    }

    @Override
    public void sync() {
        if (this.syncTask != null) {
            this.syncTask.cancel();
        }
        this.handleSync();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleSync() {
        List<AttachmentConfig.Change> pendingChanges = this.pendingChanges;
        if (!this.isTracking() || this.isSynchronizing) {
            return;
        }
        this.isSynchronizing = true;
        try {
            this.processYamlChanges();
            if (!this.attachmentsWithChanges.isEmpty()) {
                for (TrackedAttachmentConfig config : this.attachmentsWithChanges) {
                    if (config.isRemoved()) continue;
                    this.addChange(AttachmentConfig.ChangeType.CHANGED, config);
                }
                this.attachmentsWithChanges.clear();
            }
            if (!pendingChanges.isEmpty()) {
                pendingChanges.add(new AttachmentConfig.Change(AttachmentConfig.ChangeType.SYNCHRONIZED, this.root));
                this.notifyChanges(pendingChanges);
            }
        }
        finally {
            this.isSynchronizing = false;
            this.resetChanges();
        }
    }

    private void processYamlChanges() {
        ConfigurationNode config = this.completeConfigSupplier.get();
        if (config != this.completeConfig) {
            this.resetChanges();
            this.completeConfig.removeChangeListener((YamlChangeListener)this);
            this.completeConfig = config;
            this.root.swap(this.createNewRoot(config));
            this.completeConfig.addChangeListener((YamlChangeListener)this);
            return;
        }
        this.root.sync(this.completeConfig.getYamlPath());
    }

    public void onNodeChanged(YamlPath yamlPath) {
        if (yamlPath.name().equals("attachments")) {
            YamlPath attachmentPath = yamlPath.parent();
            TrackedAttachmentConfig attachment = this.findAttachment(attachmentPath);
            if (attachment != null && !attachment.childrenRefreshNeeded) {
                attachment.childrenRefreshNeeded = true;
                attachment.markChanged();
            }
        } else {
            TrackedAttachmentConfig attachment;
            YamlPath attachmentPath = AttachmentConfigTracker.getAttachmentPath(yamlPath);
            if (yamlPath != attachmentPath) {
                YamlPath tmp = yamlPath;
                int depthDiff = tmp.depth() - attachmentPath.depth();
                while (--depthDiff > 0) {
                    tmp = tmp.parent();
                }
                if (tmp.name().equals("editor")) {
                    return;
                }
            }
            if ((attachment = this.findAttachment(attachmentPath)) != null && !attachment.configChanged) {
                attachment.configChanged = true;
                attachment.markChanged();
            }
        }
        ++this.modificationCount;
        if (this.syncTask != null && this.syncTask.getPlugin().isEnabled()) {
            this.syncTask.start();
        }
    }

    private void addChange(AttachmentConfig.ChangeType changeType, TrackedAttachmentConfig attachment) {
        this.pendingChanges.add(new AttachmentConfig.Change(changeType, attachment));
    }

    private TrackedAttachmentConfig findAttachment(YamlPath path) {
        return this.byConfig.get(this.completeConfig.getNodeIfExists(path));
    }

    private static YamlPath getAttachmentPath(YamlPath path) {
        YamlPath parent;
        while (!path.isRoot() && !(parent = path.parent()).name().equals("attachments")) {
            path = parent;
        }
        return path;
    }

    private static boolean isEmptyConfiguration(ConfigurationNode config) {
        return !config.contains("type") && config.getNodeList("attachments").isEmpty();
    }

    private static String readAttachmentTypeId(ConfigurationNode config, String defaultType) {
        Object typeIdObj = config.get("type");
        return typeIdObj == null ? defaultType : typeIdObj.toString();
    }

    private static String readModelName(ConfigurationNode config) {
        Object modelNameObj = config.get("modelName");
        return modelNameObj == null ? "" : modelNameObj.toString();
    }

    private TrackedAttachmentConfig createNewRoot(ConfigurationNode config) {
        return this.createNewConfig(null, config.getYamlPath(), config, 0);
    }

    private TrackedAttachmentConfig createNewConfig(TrackedAttachmentConfig parent, YamlPath rootPath, ConfigurationNode config, int childIndex) {
        String typeId = AttachmentConfigTracker.readAttachmentTypeId(config, null);
        if (typeId == null) {
            typeId = "EMPTY";
            if (config.getNodeList("attachments").isEmpty()) {
                return new TrackedEmptyAttachmentConfig(parent, rootPath, config, typeId, childIndex);
            }
        }
        if (typeId.equals("MODEL")) {
            String modelName = AttachmentConfigTracker.readModelName(config);
            if (modelName.isEmpty()) {
                return new TrackedEmptyModelAttachmentConfig(parent, rootPath, config, typeId, childIndex);
            }
            return new TrackedModelAttachmentConfig(parent, rootPath, config, typeId, modelName, childIndex);
        }
        return new TrackedAttachmentConfig(parent, rootPath, config, typeId, childIndex);
    }

    private class TrackedAttachmentConfig
    implements AttachmentConfig {
        private final TrackedAttachmentConfig parent;
        private final List<TrackedAttachmentConfig> children;
        private YamlPath path;
        private final ConfigurationNode config;
        private final String typeId;
        private int childIndex;
        private boolean changed;
        private boolean configChanged;
        private boolean childrenRefreshNeeded;
        private boolean removed;

        private TrackedAttachmentConfig(TrackedAttachmentConfig parent, YamlPath rootPath, ConfigurationNode config, String typeId, int childIndex) {
            this.parent = parent;
            this.children = new ArrayList<TrackedAttachmentConfig>();
            this.path = config.getYamlPath().makeRelative(rootPath);
            this.config = config;
            this.typeId = typeId;
            this.childIndex = childIndex;
            this.changed = false;
            this.configChanged = false;
            this.childrenRefreshNeeded = false;
            this.removed = true;
            int index = -1;
            for (ConfigurationNode childNode : config.getNodeList("attachments")) {
                this.children.add(AttachmentConfigTracker.this.createNewConfig(this, rootPath, childNode, ++index));
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
        public AttachmentConfig addChild(int childIndex, ConfigurationNode config) {
            if (!this.removed && this.childrenRefreshNeeded) {
                AttachmentConfigTracker.this.processYamlChanges();
            }
            if (this.removed) {
                throw new UnsupportedOperationException("Cannot add a child because the parent attachment has already been removed");
            }
            if (childIndex < 0 || childIndex > this.children.size()) {
                throw new IndexOutOfBoundsException("Child add index out of bounds: " + childIndex);
            }
            this.config.getNodeList("attachments").add(childIndex, config);
            TrackedAttachmentConfig added = this.addTrackedChild(AttachmentConfigTracker.this.completeConfig.getYamlPath(), childIndex, config);
            for (int i = childIndex + 1; i < this.children.size(); ++i) {
                this.children.get((int)i).childIndex = i;
            }
            return added;
        }

        private TrackedAttachmentConfig addTrackedChild(YamlPath rootPath, int childIndex, ConfigurationNode childConfig) {
            TrackedAttachmentConfig attachment = AttachmentConfigTracker.this.createNewConfig(this, rootPath, childConfig, childIndex);
            this.children.add(childIndex, attachment);
            attachment.addToTracker();
            AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.ADDED, attachment);
            return attachment;
        }

        @Override
        public boolean isRemoved() {
            return this.removed;
        }

        @Override
        public void remove() {
            if (this.removed) {
                return;
            }
            if (this.parent == null) {
                throw new UnsupportedOperationException("Cannot remove a root attachment");
            }
            this.config.remove();
            this.parent.children.remove(this);
            this.removeFromTracker();
            int size = this.parent.children.size();
            for (int i = 0; i < size; ++i) {
                this.parent.children.get((int)i).childIndex = i;
            }
            if (AttachmentConfigTracker.this.isTracking()) {
                AttachmentConfigTracker.this.modificationCount++;
                AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.REMOVED, this);
            }
        }

        @Override
        public int childIndex() {
            return this.childIndex;
        }

        @Override
        public YamlPath path() {
            return this.path;
        }

        @Override
        public String typeId() {
            return this.typeId;
        }

        @Override
        public ConfigurationNode config() {
            return this.config;
        }

        @Override
        public boolean isEmptyConfig() {
            return false;
        }

        @Override
        public void runAction(Consumer<Attachment> action) {
            if (!this.removed) {
                AttachmentConfigTracker.this.runAttachmentAction(this, action);
            }
        }

        public String toString() {
            return "Attachment{" + this.typeId() + " at " + Arrays.toString(this.childPath()) + "}";
        }

        private void swap(TrackedAttachmentConfig replacement) {
            AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.REMOVED, this);
            if (this.parent == null) {
                AttachmentConfigTracker.this.byConfig.clear();
                this.markRemovedRecurse();
                AttachmentConfigTracker.this.root = replacement;
            } else {
                if (this.parent.children.get(this.childIndex) != this) {
                    throw new IllegalStateException("Self not found as child in parent");
                }
                this.removeFromTracker();
                this.parent.children.set(this.childIndex, replacement);
            }
            replacement.addToTracker();
            AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.ADDED, replacement);
        }

        private void markRemovedRecurse() {
            this.childrenRefreshNeeded = false;
            this.removed = true;
            for (TrackedAttachmentConfig child : this.children) {
                child.markRemovedRecurse();
            }
        }

        private void sync(YamlPath rootPath) {
            this.updatePath(rootPath);
            if (this.changed) {
                this.changed = false;
                if (this.configChanged) {
                    this.configChanged = false;
                    if (this.handleLoad()) {
                        AttachmentConfigTracker.this.attachmentsWithChanges.add(this);
                        if (!this.childrenRefreshNeeded && this.children.isEmpty() == this.config.contains("attachments")) {
                            this.childrenRefreshNeeded = true;
                        }
                    } else {
                        this.swap(AttachmentConfigTracker.this.createNewConfig(this.parent, rootPath, this.config, this.childIndex));
                    }
                }
                if (this.childrenRefreshNeeded) {
                    this.childrenRefreshNeeded = false;
                    this.updateChildren(rootPath);
                }
                if (!this.removed) {
                    for (TrackedAttachmentConfig child : this.children) {
                        child.sync(rootPath);
                    }
                }
            }
        }

        private void updateChildren(YamlPath rootPath) {
            List currChildNodes = this.config.getNodeList("attachments");
            int childIndex = 0;
            EfficientListContainsChecker checker = new EfficientListContainsChecker(currChildNodes);
            Iterator<TrackedAttachmentConfig> iter = this.children.iterator();
            while (iter.hasNext()) {
                TrackedAttachmentConfig child = iter.next();
                child.childIndex = childIndex++;
                if (checker.test(child.config)) continue;
                iter.remove();
                child.removeFromTracker();
                AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.REMOVED, child);
            }
            childIndex = 0;
            for (ConfigurationNode childConfig : currChildNodes) {
                if (childIndex < this.children.size()) {
                    TrackedAttachmentConfig attachment = this.children.get(childIndex);
                    if (attachment.config == childConfig) {
                        attachment.childIndex = childIndex++;
                        continue;
                    }
                    for (int i = childIndex + 1; i < this.children.size(); ++i) {
                        attachment = this.children.get(i);
                        if (attachment.config != childConfig) continue;
                        attachment.childIndex = i;
                        this.children.remove(i);
                        attachment.removeFromTracker();
                        AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.REMOVED, attachment);
                        break;
                    }
                }
                this.addTrackedChild(rootPath, childIndex, childConfig);
                ++childIndex;
            }
            while (childIndex < this.children.size()) {
                TrackedAttachmentConfig attachment = this.children.remove(childIndex);
                attachment.childIndex = childIndex;
                attachment.removeFromTracker();
                AttachmentConfigTracker.this.addChange(AttachmentConfig.ChangeType.REMOVED, attachment);
            }
        }

        private void updatePath(YamlPath rootPath) {
            if (this.parent != null) {
                this.path = this.config.getYamlPath().makeRelative(rootPath);
            }
        }

        protected boolean handleLoad() {
            if (AttachmentConfigTracker.isEmptyConfiguration(this.config)) {
                return false;
            }
            return this.typeId.equals(AttachmentConfigTracker.readAttachmentTypeId(this.config, "EMPTY"));
        }

        private void addToTracker() {
            AttachmentConfigTracker.this.byConfig.put(this.config, this);
            this.removed = false;
            for (TrackedAttachmentConfig child : this.children) {
                child.addToTracker();
            }
        }

        private void removeFromTracker() {
            this.removed = true;
            this.childrenRefreshNeeded = false;
            AttachmentConfigTracker.this.byConfig.remove(this.config, this);
            for (TrackedAttachmentConfig child : this.children) {
                child.removeFromTracker();
            }
        }

        private void markChanged() {
            TrackedAttachmentConfig att = this;
            while (att != null && !att.changed) {
                att.changed = true;
                att = att.parent;
            }
        }
    }

    private class SyncTask
    extends RunOnceTask {
        public SyncTask(Plugin plugin) {
            super(plugin);
        }

        public void run() {
            AttachmentConfigTracker.this.handleSync();
        }
    }

    private class ConfigInvalidChecker
    implements AttachmentConfig.RootReference.ValidChecker,
    YamlChangeListener {
        private final ConfigurationNode configSnapshot;
        private final int modCountSnapshot;
        private boolean valid;

        public ConfigInvalidChecker(ConfigurationNode configSnapshot) {
            this.configSnapshot = configSnapshot;
            this.configSnapshot.addChangeListener((YamlChangeListener)this);
            this.modCountSnapshot = AttachmentConfigTracker.this.modificationCount;
            this.valid = true;
        }

        @Override
        public boolean valid() {
            if (!this.valid) {
                return false;
            }
            if (AttachmentConfigTracker.this.isTracking() || this.modCountSnapshot != AttachmentConfigTracker.this.modificationCount || this.configSnapshot != AttachmentConfigTracker.this.completeConfigSupplier.get()) {
                this.close();
                return false;
            }
            return true;
        }

        @Override
        public void close() {
            if (this.valid) {
                this.valid = false;
                this.configSnapshot.removeChangeListener((YamlChangeListener)this);
            }
        }

        public void onNodeChanged(YamlPath yamlPath) {
            this.close();
        }
    }

    private class TrackedEmptyAttachmentConfig
    extends TrackedAttachmentConfig {
        public TrackedEmptyAttachmentConfig(TrackedAttachmentConfig parent, YamlPath rootPath, ConfigurationNode config, String typeId, int childIndex) {
            super(parent, rootPath, config, typeId, childIndex);
        }

        @Override
        public boolean isEmptyConfig() {
            return true;
        }

        @Override
        protected boolean handleLoad() {
            return super.handleLoad() && AttachmentConfigTracker.isEmptyConfiguration(this.config());
        }
    }

    private class TrackedEmptyModelAttachmentConfig
    extends TrackedAttachmentConfig {
        public TrackedEmptyModelAttachmentConfig(TrackedAttachmentConfig parent, YamlPath rootPath, ConfigurationNode config, String typeId, int childIndex) {
            super(parent, rootPath, config, typeId, childIndex);
        }

        @Override
        protected boolean handleLoad() {
            return super.handleLoad() && AttachmentConfigTracker.readModelName(this.config()).isEmpty();
        }
    }

    private class TrackedModelAttachmentConfig
    extends TrackedAttachmentConfig
    implements AttachmentConfig.Model {
        private final String modelName;

        private TrackedModelAttachmentConfig(TrackedAttachmentConfig parent, YamlPath rootPath, ConfigurationNode config, String typeId, String modelName, int childIndex) {
            super(parent, rootPath, config, typeId, childIndex);
            this.modelName = modelName;
        }

        @Override
        protected boolean handleLoad() {
            return super.handleLoad() && this.modelName.equals(AttachmentConfigTracker.readModelName(this.config()));
        }

        @Override
        public String modelName() {
            return this.modelName;
        }
    }

    private static class EfficientListContainsChecker<T>
    implements Predicate<T> {
        private final List<T> list;
        private int currentIndex;

        private EfficientListContainsChecker(List<T> list) {
            this.list = list;
            this.currentIndex = 0;
        }

        @Override
        public boolean test(T t) {
            int size = this.list.size();
            if (size == 0) {
                return false;
            }
            int i = this.currentIndex;
            do {
                if (this.list.get(i) == t) {
                    this.currentIndex = (i + 1) % size;
                    return true;
                }
                ++i;
            } while ((i %= size) != this.currentIndex);
            return false;
        }
    }
}

