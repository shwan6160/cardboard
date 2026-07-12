/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlDeserializer;

public class BasicConfiguration
extends ConfigurationNode {
    private int indent = 2;

    @Override
    public String getPath() {
        return "";
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getIndent() {
        return this.indent;
    }

    @Override
    public void loadDeserializerOutput(YamlDeserializer.Output output) {
        this.indent = output.indent;
        super.loadDeserializerOutput(output);
    }
}

