/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Logging;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class DebugLogger {
    private static final Map<String, DebugLogger> _loggers = new HashMap<String, DebugLogger>();
    private final String _name;
    private int[] _columnSpacing = new int[0];
    private final StringBuilder _buffer = new StringBuilder();
    private final List<String> _backBuffer = new ArrayList<String>();
    private boolean _backBufferActive = false;
    private int _col = 0;

    private DebugLogger(String name) {
        this._name = name;
        this._buffer.append(this._name);
    }

    public DebugLogger bufferStart() {
        this._backBuffer.clear();
        this._backBufferActive = true;
        return this;
    }

    public DebugLogger bufferDone() {
        this._backBufferActive = false;
        for (String message : this._backBuffer) {
            Logging.LOGGER.info(message);
        }
        this._backBuffer.clear();
        return this;
    }

    public DebugLogger addBlock(Block block) {
        if (block == null) {
            return this.add("null", 0, 0, 0);
        }
        return this.add(block.getWorld().getName(), Integer.toString(block.getX()), Integer.toString(block.getY()), Integer.toString(block.getZ()));
    }

    public DebugLogger addVec(int decimals, Vector vec) {
        return this.addNum(decimals, vec.getX(), vec.getY(), vec.getZ());
    }

    public DebugLogger addNum(int decimals, double ... values) {
        double p = Math.pow(10.0, decimals);
        Object[] output = new Object[values.length];
        for (int i = 0; i < values.length; ++i) {
            output[i] = Double.toString((double)Math.round(values[i] * p) / p);
        }
        return this.add(output);
    }

    public DebugLogger add(int ... values) {
        Object[] output = new Object[values.length];
        for (int i = 0; i < values.length; ++i) {
            output[i] = Integer.toString(values[i]);
        }
        return this.add(output);
    }

    public DebugLogger add(Object ... values) {
        this.cache(values.length);
        for (Object value : values) {
            String s = value == null ? "null" : value.toString();
            int spacing = this._columnSpacing[this._col];
            if (s.length() > spacing) {
                this._columnSpacing[this._col] = spacing = s.length();
            }
            this._buffer.append("  ");
            this._buffer.append(s);
            for (int j = s.length(); j < spacing; ++j) {
                this._buffer.append(' ');
            }
            ++this._col;
        }
        return this;
    }

    public DebugLogger pad(int n) {
        this.cache(1);
        int spacing = this._columnSpacing[this._col];
        if (n > spacing) {
            this._columnSpacing[this._col] = spacing = n;
        }
        for (int i = 0; i < spacing; ++i) {
            this._buffer.append(' ');
        }
        ++this._col;
        return this;
    }

    public DebugLogger log(Object ... values) {
        return this.add(values).log();
    }

    public DebugLogger log() {
        if (this._backBufferActive) {
            this._backBuffer.add(this._buffer.toString());
        } else {
            Logging.LOGGER.info(this._buffer.toString());
        }
        this._buffer.setLength(this._name.length());
        this._col = 0;
        return this;
    }

    private void cache(int n) {
        if (this._col + n > this._columnSpacing.length) {
            this._columnSpacing = Arrays.copyOf(this._columnSpacing, this._col + n);
        }
    }

    public static synchronized DebugLogger get(String name) {
        return _loggers.computeIfAbsent(name, DebugLogger::new);
    }
}

