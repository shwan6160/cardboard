/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.config.yaml.YamlEntry;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;

public class YamlNode
extends YamlNodeAbstract<YamlNode> {
    public YamlNode() {
    }

    protected YamlNode(YamlEntry entry) {
        super(entry);
    }

    @Override
    protected YamlNode createNode(YamlEntry entry) {
        return new YamlNode(entry);
    }
}

