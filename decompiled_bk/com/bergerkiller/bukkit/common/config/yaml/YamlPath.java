/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config.yaml;

public class YamlPath {
    public static final YamlPath ROOT = new YamlPath();
    private final YamlPath parent;
    private final String name;
    private final int depth;
    private final int hashcode;

    private YamlPath() {
        this.parent = null;
        this.name = "";
        this.depth = 0;
        this.hashcode = 0;
    }

    private YamlPath(YamlPath parent, String name) {
        this.parent = parent;
        this.name = name;
        this.depth = parent.depth + 1;
        this.hashcode = this.name.hashCode() + 31 * this.parent.hashcode;
    }

    public static YamlPath create(String path) {
        return YamlPath.createChild(ROOT, path);
    }

    private static YamlPath createChild(YamlPath parent, String path) {
        if (path.isEmpty()) {
            return parent;
        }
        int startIndex = 0;
        block0: while (startIndex < path.length()) {
            int i;
            char c = path.charAt(startIndex);
            if (c == '.') {
                ++startIndex;
                continue;
            }
            if (c == '[' && path.length() - startIndex >= 3) {
                i = startIndex;
                int listIndex = 0;
                boolean foundDigit = false;
                int listEndIndex = -1;
                while (++i < path.length()) {
                    char digit = path.charAt(i);
                    if (digit >= '0' && digit <= '9') {
                        foundDigit = true;
                        listIndex *= 10;
                        listIndex += digit - 48;
                        continue;
                    }
                    if (digit != ']') break;
                    listEndIndex = i + 1;
                    break;
                }
                if (foundDigit && listEndIndex != -1) {
                    parent = new YamlPathListElement(parent, listIndex);
                    startIndex = listEndIndex;
                    continue;
                }
            }
            i = startIndex;
            do {
                if (++i < path.length()) continue;
                parent = new YamlPath(parent, path.substring(startIndex));
                startIndex = path.length();
                continue block0;
            } while ((c = path.charAt(i)) != '.' && c != '[');
            parent = new YamlPath(parent, path.substring(startIndex, i));
            startIndex = i;
        }
        return parent;
    }

    public boolean isRoot() {
        return this == ROOT;
    }

    public boolean isListElement() {
        return false;
    }

    public int listIndex() {
        return -1;
    }

    public YamlPath parent() {
        return this.parent;
    }

    public YamlPath child(String childPath) {
        return YamlPath.createChild(this, childPath);
    }

    public YamlPath child(YamlPath childPath) {
        if (childPath.isRoot()) {
            return this;
        }
        return this.child(childPath.parent()).child(childPath.name());
    }

    public YamlPath childWithName(YamlPath nameOfPath) {
        return nameOfPath.appendNameTo(this);
    }

    public YamlPath childWithName(String name) {
        return new YamlPath(this, name);
    }

    public YamlPath makeRelative(YamlPath absolutePath) {
        int depthDiff = this.depth() - absolutePath.depth();
        if (depthDiff < 0) {
            return null;
        }
        if (depthDiff == 0) {
            return this.equals(absolutePath) ? ROOT : null;
        }
        YamlPath[] relativeParts = new YamlPath[depthDiff];
        YamlPath commonParent = this;
        do {
            relativeParts[--depthDiff] = commonParent;
            commonParent = commonParent.parent;
        } while (depthDiff > 0);
        if (!commonParent.equals(absolutePath)) {
            return null;
        }
        YamlPath result = ROOT;
        for (YamlPath relativePart : relativeParts) {
            result = relativePart.appendNameTo(result);
        }
        return result;
    }

    public static YamlPath join(YamlPath firstPath, YamlPath secondPath) {
        if (firstPath.isRoot()) {
            return secondPath;
        }
        if (secondPath.isRoot()) {
            return firstPath;
        }
        int depth = secondPath.depth;
        YamlPath[] parts = new YamlPath[depth];
        YamlPath p = secondPath;
        while (--depth >= 0) {
            parts[depth] = p;
            p = p.parent;
        }
        YamlPath result = firstPath;
        for (YamlPath p2 : parts) {
            result = result.childWithName(p2);
        }
        return result;
    }

    public YamlPath listChild(int listIndex) {
        return new YamlPathListElement(this, listIndex);
    }

    public String name() {
        return this.name;
    }

    protected YamlPath appendNameTo(YamlPath parent) {
        return new YamlPath(parent, this.name);
    }

    public int depth() {
        return this.depth;
    }

    public int hashCode() {
        return this.hashcode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof YamlPath) {
            YamlPath p1 = this;
            YamlPath p2 = (YamlPath)o;
            while (p1.name.equals(p2.name)) {
                if ((p1 = p1.parent()) == (p2 = p2.parent())) {
                    return true;
                }
                if (p1 != null && p2 != null) continue;
                return false;
            }
        }
        return false;
    }

    public String toString() {
        if (this.isRoot()) {
            return "";
        }
        StringBuilder str = new StringBuilder(64);
        YamlPath path = this;
        do {
            path.insertBeginning(str);
        } while (!(path = path.parent).isRoot());
        return str.toString();
    }

    protected void insertBeginning(StringBuilder str) {
        str.insert(0, this.name);
        if (!this.parent.isRoot()) {
            str.insert(0, '.');
        }
    }

    private static final class YamlPathListElement
    extends YamlPath {
        private final int index;

        public YamlPathListElement(YamlPath parent, int index) {
            super(parent, Integer.toString(index));
            this.index = index;
        }

        private YamlPathListElement(YamlPath parent, String name, int index) {
            super(parent, name);
            this.index = index;
        }

        @Override
        public int listIndex() {
            return this.index;
        }

        @Override
        public boolean isListElement() {
            return true;
        }

        @Override
        protected YamlPathListElement appendNameTo(YamlPath parent) {
            return new YamlPathListElement(parent, this.name(), this.index);
        }

        @Override
        protected void insertBeginning(StringBuilder str) {
            str.insert(0, ']');
            str.insert(0, this.name());
            str.insert(0, '[');
        }
    }

    @FunctionalInterface
    public static interface Supplier {
        public YamlPath getYamlPath();
    }
}

