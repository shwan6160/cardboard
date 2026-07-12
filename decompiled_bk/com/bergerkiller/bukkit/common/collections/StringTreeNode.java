/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.CharArrayBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringTreeNode {
    private StringTreeNode _parent = null;
    private List<StringTreeNode> _children = Collections.emptyList();
    private final TreeNodeBuffer _buffer;
    private int _lengthTotal;
    private int _countTotal;
    private boolean _changed;

    public StringTreeNode() {
        this._buffer = new TreeNodeBuffer();
        this._lengthTotal = 0;
        this._countTotal = 1;
        this._changed = false;
    }

    public StringTreeNode(String value) {
        this._buffer = new TreeNodeBuffer(value);
        this._lengthTotal = this._buffer.length();
        this._countTotal = 1;
        this._changed = false;
    }

    public StringTreeNode parent() {
        return this._parent;
    }

    public int index() {
        if (this._parent == null) {
            return -1;
        }
        return this._parent._children.indexOf(this);
    }

    public int size() {
        return this._children.size();
    }

    public boolean setIndex(int index) {
        int oldIndex;
        if (this._parent == null) {
            return false;
        }
        if (index < 0 || index >= this._parent._children.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of range");
        }
        if (this._parent.get(index) != this && (oldIndex = this._parent._children.indexOf(this)) != -1 && oldIndex != index) {
            this._parent._children.remove(oldIndex);
            this._parent._children.add(index, this);
            this._parent.markChildrenChanged();
            return true;
        }
        return false;
    }

    public String getValue() {
        return this._buffer.toString();
    }

    public CharSequence getValueSequence() {
        return this._buffer;
    }

    public void setValue(String value) {
        this.markLengthChanged(this._buffer.update(value));
    }

    public void setValueSequence(CharSequence value) {
        this.markLengthChanged(this._buffer.update(value));
    }

    public void remove() {
        if (this._parent == null) {
            throw new IllegalStateException("This is a root node and cannot be removed");
        }
        StringTreeNode parent = this._parent;
        this._parent = null;
        if (parent._children.size() == 1) {
            parent._children = Collections.emptyList();
        } else {
            parent._children.remove(this);
        }
        parent.markChanged(-this._lengthTotal, -this._countTotal);
    }

    public StringTreeNode get(int index) {
        return this._children.get(index);
    }

    public StringTreeNode add() {
        return this.add(new StringTreeNode());
    }

    public StringTreeNode add(StringTreeNode node) {
        return this.insert(this._children.size(), node);
    }

    public StringTreeNode insert(int index) {
        return this.insert(index, new StringTreeNode());
    }

    public StringTreeNode insert(int index, StringTreeNode node) {
        if (node._parent != null) {
            throw new IllegalStateException("The node already has a parent. Clone or remove the node first.");
        }
        if (index < 0 || index > this._children.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds (0 <= index <= " + this._children.size() + ")");
        }
        if (this._children.isEmpty()) {
            this._children = Collections.singletonList(node);
        } else {
            if (this._children.size() == 1) {
                this._children = new ArrayList<StringTreeNode>(this._children);
            }
            this._children.add(index, node);
        }
        node._parent = this;
        this.markChanged(node._lengthTotal, node._countTotal);
        return node;
    }

    public StringTreeNode clone() {
        return this.clone(this.toCharArray(), 0);
    }

    private StringTreeNode clone(char[] buffer, int position) {
        StringTreeNode clone = new StringTreeNode();
        clone._lengthTotal = this._lengthTotal;
        clone._countTotal = this._countTotal;
        clone._buffer.assign(buffer, position, this._buffer.length());
        position += this._buffer.length();
        int child_count = this._children.size();
        if (child_count == 1) {
            StringTreeNode childClone = this._children.get(0).clone(buffer, position);
            childClone._parent = clone;
            clone._children = Collections.singletonList(childClone);
            position += childClone._lengthTotal;
        } else if (child_count > 0) {
            clone._children = new ArrayList<StringTreeNode>(child_count);
            for (StringTreeNode child : this._children) {
                StringTreeNode childClone = child.clone(buffer, position);
                childClone._parent = clone;
                clone._children.add(childClone);
                position += childClone._lengthTotal;
            }
        }
        return clone;
    }

    public CharBuffer toCharBuffer() {
        return CharBuffer.wrap(this.toCharArray());
    }

    public char[] toCharArray() {
        if (this._changed) {
            BufferBuilder builder = new BufferBuilder(this._lengthTotal, this._countTotal);
            this.toCharArray(builder);
            return builder.buffer;
        }
        char[] result = new char[this._lengthTotal];
        this._buffer.copyTo(result, 0, this._lengthTotal);
        return result;
    }

    public String toString() {
        if (this._changed) {
            BufferBuilder builder = new BufferBuilder(this._lengthTotal, this._countTotal);
            this.toCharArray(builder);
            return new String(builder.buffer);
        }
        return this._buffer.copyToString(this._lengthTotal);
    }

    private void toCharArray(BufferBuilder builder) {
        int flatTotal = this._countTotal;
        if (flatTotal == 1) {
            this._changed = false;
            this._buffer.moveToBufferAssignFlat(builder);
            return;
        }
        if (!this._changed) {
            this._buffer.copyAllTo(builder, this._lengthTotal, this._countTotal);
            TreeNodeBuffer[] flat = builder.flatBuffers;
            int flatIndex = builder.flatPosition;
            int flatEnd = flatIndex + this._countTotal;
            do {
                TreeNodeBuffer buffer = flat[flatIndex];
                builder.position = buffer.swapBuffer(builder.buffer, builder.position);
                TreeNodeBuffer.access$002(buffer, builder.flatBuffers);
                buffer._flatOffset = flatIndex;
            } while (++flatIndex < flatEnd);
            builder.flatPosition = flatEnd;
            return;
        }
        this._changed = false;
        TreeNodeBuffer[] nodeFlat = this._buffer._flatChildren;
        if (nodeFlat != null) {
            int flatIndex = builder.flatPosition;
            int flatEnd = flatIndex + flatTotal;
            System.arraycopy(nodeFlat, this._buffer._flatOffset, builder.flatBuffers, flatIndex, flatTotal);
            TreeNodeBuffer[] flat = builder.flatBuffers;
            do {
                flat[flatIndex].moveToBuffer(builder, flatIndex);
            } while (++flatIndex < flatEnd);
            builder.flatPosition = flatEnd;
        } else {
            this._buffer.moveToBufferAssignFlat(builder);
            for (StringTreeNode child : this._children) {
                child.toCharArray(builder);
            }
        }
    }

    private void markChanged(int length_change, int count_change) {
        if (count_change == 0) {
            this.markLengthChanged(length_change);
        } else {
            StringTreeNode node = this;
            do {
                node._lengthTotal += length_change;
                node._countTotal += count_change;
                TreeNodeBuffer.access$002(node._buffer, null);
                node._changed = true;
            } while ((node = node._parent) != null);
        }
    }

    private void markLengthChanged(int length_change) {
        if (length_change != 0) {
            StringTreeNode node = this;
            do {
                node._lengthTotal += length_change;
                node._changed = true;
            } while ((node = node._parent) != null);
        }
    }

    private void markChildrenChanged() {
        TreeNodeBuffer buffer;
        StringTreeNode node = this;
        while ((buffer = node._buffer)._flatChildren != null) {
            TreeNodeBuffer.access$002(buffer, null);
            node._changed = true;
            node = node._parent;
            if (node != null) continue;
        }
    }

    private static final class TreeNodeBuffer
    extends CharArrayBuffer {
        private TreeNodeBuffer[] _flatChildren = null;
        private int _flatOffset = 0;

        public TreeNodeBuffer() {
        }

        public TreeNodeBuffer(String value) {
            super(value);
        }

        public void moveToBuffer(BufferBuilder builder, int flatOffset) {
            builder.position = super.moveToBuffer(builder.buffer, builder.position);
            this._flatChildren = builder.flatBuffers;
            this._flatOffset = flatOffset;
        }

        public void moveToBufferAssignFlat(BufferBuilder builder) {
            int flatOffset = builder.flatPosition++;
            this.moveToBuffer(builder, flatOffset);
            builder.flatBuffers[flatOffset] = this;
        }

        public void copyAllTo(BufferBuilder builder, int lengthTotal, int countTotal) {
            super.copyTo(builder.buffer, builder.position, lengthTotal);
            System.arraycopy(this._flatChildren, this._flatOffset, builder.flatBuffers, builder.flatPosition, countTotal);
        }

        static /* synthetic */ TreeNodeBuffer[] access$002(TreeNodeBuffer x0, TreeNodeBuffer[] x1) {
            x0._flatChildren = x1;
            return x1;
        }
    }

    private static final class BufferBuilder {
        public final char[] buffer;
        public final TreeNodeBuffer[] flatBuffers;
        public int position;
        public int flatPosition;

        public BufferBuilder(int lengthTotal, int countTotal) {
            this.buffer = new char[lengthTotal];
            this.flatBuffers = new TreeNodeBuffer[countTotal];
            this.position = 0;
            this.flatPosition = 0;
        }
    }
}

