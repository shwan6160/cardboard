/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;

class YamlChangeListenerRelative
implements YamlChangeListener {
    private final YamlPath.Supplier rootPathSupplier;
    private final YamlChangeListener listener;

    private YamlChangeListenerRelative(YamlPath.Supplier rootPathSupplier, YamlChangeListener listener) {
        this.rootPathSupplier = rootPathSupplier;
        this.listener = listener;
    }

    @Override
    public void onNodeChanged(YamlPath path) {
        YamlPath rootPath = this.rootPathSupplier.getYamlPath();
        if (rootPath.isRoot()) {
            this.listener.onNodeChanged(path);
            return;
        }
        YamlPath relativePath = path.makeRelative(rootPath);
        if (relativePath != null) {
            this.listener.onNodeChanged(relativePath);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof YamlChangeListenerRelative) {
            YamlChangeListenerRelative other = (YamlChangeListenerRelative)o;
            return this.listener.equals(other.listener);
        }
        return false;
    }

    public static YamlChangeListener create(YamlPath.Supplier rootPathSupplier, YamlChangeListener listener) {
        return new YamlChangeListenerRelative(rootPathSupplier, listener);
    }
}

