/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigListener;
import com.bergerkiller.bukkit.tc.utils.ListCallbackCollector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface AttachmentConfig {
    public AttachmentConfig parent();

    default public boolean isRoot() {
        return this.parent() == null;
    }

    public List<AttachmentConfig> children();

    default public AttachmentConfig child(int childIndex) {
        List<AttachmentConfig> children = this.children();
        if (childIndex < 0 || childIndex >= children.size()) {
            return null;
        }
        return children.get(childIndex);
    }

    default public AttachmentConfig child(int[] childPath) {
        AttachmentConfig p = this;
        int len = childPath.length;
        if (len > 0) {
            int i = 0;
            do {
                p = p.child(childPath[i]);
            } while (++i < len && p != null);
        }
        return p;
    }

    default public AttachmentConfig child(YamlPath path) {
        boolean found;
        if (path.isRoot()) {
            return this;
        }
        AttachmentConfig currentAttachment = this;
        YamlPath resultPath = path;
        YamlPath searchPath = YamlPath.join((YamlPath)this.path(), (YamlPath)path);
        block0: do {
            found = false;
            for (AttachmentConfig child : currentAttachment.children()) {
                YamlPath childRelativePath = searchPath.makeRelative(child.path());
                if (childRelativePath == null) continue;
                currentAttachment = child;
                resultPath = childRelativePath;
                found = true;
                continue block0;
            }
        } while (found);
        if (!resultPath.isRoot()) {
            while (!resultPath.parent().isRoot()) {
                resultPath = resultPath.parent();
            }
            if (resultPath.name().equals("attachments")) {
                return null;
            }
        }
        return currentAttachment;
    }

    default public AttachmentConfig removeChild(int childIndex) {
        AttachmentConfig child = this.child(childIndex);
        if (child == null) {
            throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
        }
        child.remove();
        return child;
    }

    default public AttachmentConfig addChild(ConfigurationNode config) {
        return this.addChild(this.children().size(), config);
    }

    public AttachmentConfig addChild(int var1, ConfigurationNode var2);

    public boolean isRemoved();

    public void remove();

    public int childIndex();

    default public int[] childPath() {
        ArrayList<AttachmentConfig> parents = new ArrayList<AttachmentConfig>(10);
        AttachmentConfig a = this;
        while (a.parent() != null) {
            parents.add(a);
            a = a.parent();
        }
        int[] path = new int[parents.size()];
        int i = 0;
        int j = path.length - 1;
        while (j >= 0) {
            path[i] = ((AttachmentConfig)parents.get(j)).childIndex();
            --j;
            ++i;
        }
        return path;
    }

    public YamlPath path();

    public String typeId();

    public ConfigurationNode config();

    public boolean isEmptyConfig();

    default public void setConfig(ConfigurationNode config) {
        this.config().setToExcept((YamlNodeAbstract)config, Collections.singletonList("attachments"));
    }

    public void runAction(Consumer<Attachment> var1);

    default public List<Attachment> liveAttachments() {
        ListCallbackCollector<Attachment> collector = new ListCallbackCollector<Attachment>();
        this.runAction(collector);
        return collector.result();
    }

    default public <T extends Attachment> List<T> liveAttachmentsOfType(Class<T> type) {
        ListCallbackCollector collector = new ListCallbackCollector();
        this.runAction(attachment -> {
            if (type.isInstance(attachment)) {
                collector.accept(attachment);
            }
        });
        return collector.result();
    }

    public static final class RootReference {
        public static final RootReference NONE = new RootReference(null, () -> false);
        private AttachmentConfig root;
        private ValidChecker validChecker;

        RootReference(AttachmentConfig root, ValidChecker validChecker) {
            this.root = root;
            this.validChecker = validChecker;
        }

        public AttachmentConfig get() {
            if (this.valid()) {
                return this.root;
            }
            throw new IllegalStateException("This root reference is no longer valid");
        }

        public boolean valid() {
            ValidChecker checker = this.validChecker;
            if (checker != null) {
                if (checker.valid()) {
                    return true;
                }
                this.invalidate();
            }
            return false;
        }

        public void invalidate() {
            ValidChecker checker = this.validChecker;
            this.root = null;
            this.validChecker = null;
            if (checker != null) {
                checker.close();
            }
        }

        ValidChecker getValidChecker() {
            ValidChecker checker = this.validChecker;
            return checker != null ? checker : () -> false;
        }

        @FunctionalInterface
        static interface ValidChecker {
            public boolean valid();

            default public void close() {
            }
        }
    }

    public static enum ChangeType {
        ADDED(AttachmentConfigListener::onAttachmentAdded),
        REMOVED(AttachmentConfigListener::onAttachmentRemoved),
        CHANGED(AttachmentConfigListener::onAttachmentChanged),
        SYNCHRONIZED(AttachmentConfigListener::onSynchronized);

        private final BiConsumer<AttachmentConfigListener, AttachmentConfig> callback;

        private ChangeType(BiConsumer<AttachmentConfigListener, AttachmentConfig> callback) {
            this.callback = callback;
        }

        public BiConsumer<AttachmentConfigListener, AttachmentConfig> callback() {
            return this.callback;
        }
    }

    public static final class Change {
        private final ChangeType changeType;
        private final AttachmentConfig attachment;

        public Change(ChangeType changeType, AttachmentConfig attachment) {
            this.changeType = changeType;
            this.attachment = attachment;
        }

        public AttachmentConfig attachment() {
            return this.attachment;
        }

        public ChangeType changeType() {
            return this.changeType;
        }

        public String toString() {
            return "{" + this.changeType.name() + " " + this.attachment.path() + "}";
        }
    }

    public static interface Model
    extends AttachmentConfig {
        public static final String MODEL_NAME_CONFIG_KEY = "modelName";

        public String modelName();
    }
}

