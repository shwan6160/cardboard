/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class RenderOptions
implements Map<String, String>,
Cloneable {
    protected String optionsToken;
    protected Map<String, String> optionsMap;

    public RenderOptions() {
        this.optionsToken = "";
        this.optionsMap = null;
    }

    public RenderOptions(String optionsToken) {
        this.optionsToken = optionsToken;
        this.optionsMap = null;
    }

    public RenderOptions(Map<String, String> optionsMap) {
        this.optionsMap = optionsMap;
        this.optionsToken = null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final Map<String, String> map(boolean write) {
        if (this.optionsMap != null) return this.optionsMap;
        if (this.optionsToken.isEmpty()) {
            if (!write) return Collections.emptyMap();
            this.optionsMap = new HashMap<String, String>(1);
            this.optionsToken = null;
            return this.optionsMap;
        } else {
            this.optionsMap = new HashMap<String, String>(2);
            int index = 0;
            do {
                String pair;
                int endIndex;
                if ((endIndex = this.optionsToken.indexOf(44, index)) == -1) {
                    pair = this.optionsToken.substring(index);
                    index = -1;
                } else {
                    pair = this.optionsToken.substring(index, endIndex);
                    index = endIndex + 1;
                }
                int pairSep = pair.indexOf(61);
                if (pairSep != -1) {
                    this.optionsMap.put(pair.substring(0, pairSep), pair.substring(pairSep + 1));
                    continue;
                }
                this.optionsMap.put(pair, "");
            } while (index != -1);
            if (!write) return this.optionsMap;
            this.optionsToken = null;
        }
        return this.optionsMap;
    }

    public final String getOptionsString() {
        if (this.optionsToken == null) {
            if (this.optionsMap.isEmpty()) {
                this.optionsToken = "";
            } else {
                boolean first = true;
                StringBuilder result = new StringBuilder(this.optionsMap.size() * 10);
                for (Map.Entry<String, String> option : this.optionsMap.entrySet()) {
                    if (first) {
                        first = false;
                    } else {
                        result.append(',');
                    }
                    result.append(option.getKey());
                    if (option.getValue().isEmpty()) continue;
                    result.append('=').append(option.getValue());
                }
                this.optionsToken = result.toString();
            }
        }
        return this.optionsToken;
    }

    @Override
    public final int size() {
        return this.map(false).size();
    }

    @Override
    public final boolean isEmpty() {
        return this.map(false).isEmpty();
    }

    @Override
    public final boolean containsKey(Object key) {
        return this.map(false).containsKey(key);
    }

    @Override
    public final boolean containsValue(Object value) {
        return this.map(false).containsValue(value);
    }

    @Override
    public final String get(Object key) {
        return this.map(false).get(key);
    }

    @Override
    public final String put(String key, String value) {
        return this.map(true).put(key, value);
    }

    @Override
    public final String remove(Object key) {
        return this.map(true).remove(key);
    }

    @Override
    public final void putAll(Map<? extends String, ? extends String> m) {
        this.map(true).putAll(m);
    }

    @Override
    public final void clear() {
        this.map(true).clear();
    }

    @Override
    public final Set<String> keySet() {
        return this.map(false).keySet();
    }

    @Override
    public final Collection<String> values() {
        return this.map(false).values();
    }

    @Override
    public final Set<Map.Entry<String, String>> entrySet() {
        return this.map(false).entrySet();
    }

    @Override
    public int hashCode() {
        return this.map(false).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RenderOptions) {
            return ((RenderOptions)o).map(false).equals(this.map(false));
        }
        return false;
    }

    public String toString() {
        return "[" + this.getOptionsString() + "]";
    }

    public abstract RenderOptions clone();

    public abstract String lookupModelName();

    public static BlockRenderOptions fromString(BlockData blockData, String token) {
        return new BlockRenderOptions(blockData, token);
    }

    public static BlockRenderOptions fromBlock(Block block) {
        return WorldUtil.getBlockData(block).getRenderOptions(block);
    }

    public static BlockRenderOptions fromBlock(World world, int x, int y, int z) {
        return WorldUtil.getBlockData(world, x, y, z).getRenderOptions(world, x, y, z);
    }
}

