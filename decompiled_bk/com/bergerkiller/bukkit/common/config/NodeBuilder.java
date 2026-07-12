/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import java.util.LinkedList;

public class NodeBuilder
implements YamlPath.Supplier {
    private LinkedList<String> nodes = new LinkedList();
    private int indent;

    public NodeBuilder(int indent) {
        this.indent = indent;
    }

    public void reset(int indent) {
        this.nodes.clear();
        this.indent = indent;
    }

    public int getIndent() {
        return this.indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public boolean handle(String line, int preceedingSpaces) {
        int nodeIndex;
        if (line.startsWith("#")) {
            return false;
        }
        if (this.indent == -1) {
            if (preceedingSpaces > 0) {
                this.indent = preceedingSpaces;
                nodeIndex = 1;
            } else {
                nodeIndex = 0;
            }
        } else {
            nodeIndex = preceedingSpaces / this.indent;
        }
        String nodeName = StringUtil.getLastBefore(line, ":");
        if (!nodeName.isEmpty()) {
            while (this.nodes.size() >= nodeIndex + 1) {
                this.nodes.pollLast();
            }
            this.nodes.offerLast(nodeName);
            return true;
        }
        return false;
    }

    public boolean handle(StringBuilder line, int contentStart, int contentEnd) {
        int nodeIndex;
        if (contentStart == contentEnd || line.charAt(contentStart) == '#') {
            return false;
        }
        if (this.indent == -1) {
            if (contentStart > 0) {
                this.indent = contentStart;
                nodeIndex = 1;
            } else {
                nodeIndex = 0;
            }
        } else {
            nodeIndex = contentStart / this.indent;
        }
        int nodeNameEndIndex = line.lastIndexOf(":", contentEnd);
        if (nodeNameEndIndex == -1 || nodeNameEndIndex <= contentStart) {
            return false;
        }
        String nodeName = line.substring(contentStart, nodeNameEndIndex);
        while (this.nodes.size() >= nodeIndex + 1) {
            this.nodes.pollLast();
        }
        this.nodes.offerLast(nodeName);
        return true;
    }

    public String getName() {
        return this.nodes.peekLast();
    }

    public int getDepth() {
        return this.nodes.size();
    }

    public String getPath() {
        return StringUtil.join(".", this.nodes);
    }

    @Override
    public YamlPath getYamlPath() {
        YamlPath p = YamlPath.ROOT;
        for (String name : this.nodes) {
            p = p.child(name);
        }
        return p;
    }
}

