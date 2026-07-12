/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ConfigurationNode
extends YamlNodeAbstract<ConfigurationNode> {
    public ConfigurationNode() {
    }

    protected ConfigurationNode(YamlEntry entry) {
        super(entry);
    }

    @Override
    protected ConfigurationNode createNode(YamlEntry entry) {
        return new ConfigurationNode(entry);
    }

    @Override
    public void setNodeList(String path, List<ConfigurationNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            this.remove(path);
        } else {
            super.setNodeList(path, nodes);
        }
    }

    @Override
    public void set(String path, Object value) {
        if (value == null) {
            super.remove(path);
        } else {
            super.set(path, value);
        }
    }

    @Override
    public ConfigurationNode getParent() {
        YamlNodeAbstract<?> parent;
        for (parent = this.getYamlParent(); parent != null && !(parent instanceof ConfigurationNode); parent = parent.getYamlParent()) {
        }
        return (ConfigurationNode)parent;
    }

    @Override
    public ConfigurationNode getNode(String path) {
        return (ConfigurationNode)super.getNode(path);
    }

    @Override
    public ConfigurationNode getNode(YamlPath relativePath) {
        return (ConfigurationNode)super.getNode(relativePath);
    }

    @Override
    public ConfigurationNode getNodeIfExists(String path) {
        return (ConfigurationNode)super.getNodeIfExists(path);
    }

    @Override
    public ConfigurationNode getNodeIfExists(YamlPath relativePath) {
        return (ConfigurationNode)super.getNodeIfExists(relativePath);
    }

    @Override
    public ConfigurationNode clone() {
        return (ConfigurationNode)super.clone();
    }

    @Override
    public void cloneInto(ConfigurationNode target) {
        super.cloneInto(target);
    }

    @Override
    public void cloneInto(ConfigurationNode target, Predicate<YamlPath> filter) {
        super.cloneInto(target, filter);
    }

    @Override
    public void cloneIntoExcept(ConfigurationNode target, Collection<String> excludedPaths) {
        super.cloneIntoExcept(target, excludedPaths);
    }

    @Deprecated
    public void setRead(String path) {
    }

    @Deprecated
    public void setRead() {
    }

    @Deprecated
    public void trim() {
    }

    @Deprecated
    public boolean isRead(String path) {
        return true;
    }

    @Deprecated
    public boolean isRead() {
        return true;
    }
}

