/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.offline.train.format;

import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OfflineDataBlockSerializer {
    private final List<String> values = new ArrayList<String>();
    private final Map<String, Integer> valueToIndex = new HashMap<String, Integer>();
    private final OfflineDataBlock.DataBlockBuilder dataBlockBuilder = new OfflineDataBlock.DataBlockBuilder();

    public OfflineDataBlockSerializer() {
        this.reset();
    }

    public void reset() {
        this.values.clear();
        this.valueToIndex.clear();
        this.values.add("");
        this.valueToIndex.put("", 0);
    }

    public OfflineDataBlock readDataBlock(DataInputStream stream) throws IOException {
        OfflineDataBlock child;
        String name = this.readString(stream);
        if (name.isEmpty()) {
            return null;
        }
        byte[] data = Util.readByteArray(stream);
        OfflineDataBlock dataBlock = new OfflineDataBlock(this.dataBlockBuilder, name, data);
        while ((child = this.readDataBlock(stream)) != null) {
            dataBlock.addChild(child);
        }
        return dataBlock;
    }

    public void writeDataBlock(DataOutputStream stream, OfflineDataBlock dataBlock) throws IOException {
        this.writeString(stream, dataBlock.name);
        Util.writeByteArray(stream, dataBlock.data);
        for (OfflineDataBlock child : dataBlock.children) {
            this.writeDataBlock(stream, child);
        }
        this.writeEmptyString(stream);
    }

    public void writeString(DataOutputStream stream, String value) throws IOException {
        Integer index = this.valueToIndex.get(value);
        if (index == null) {
            index = this.values.size();
            this.values.add(value);
            this.valueToIndex.put(value, index);
            Util.writeVariableLengthInt(stream, index);
            stream.writeUTF(value);
        } else {
            Util.writeVariableLengthInt(stream, index);
        }
    }

    public void writeEmptyString(DataOutputStream stream) throws IOException {
        Util.writeVariableLengthInt(stream, 0);
    }

    public String readString(DataInputStream stream) throws IOException {
        int index = Util.readVariableLengthInt(stream);
        if (index == this.values.size()) {
            String value = stream.readUTF();
            this.values.add(value);
            this.valueToIndex.put(value, index);
            return value;
        }
        if (index < 0 || index > this.values.size()) {
            throw new IOException("String index out of range: " + index);
        }
        return this.values.get(index);
    }
}

