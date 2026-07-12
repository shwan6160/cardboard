/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.block.SignSide;
import java.util.Arrays;
import java.util.List;

public class SignSideMap<T> {
    private T front;
    private T back;

    public SignSideMap() {
    }

    public SignSideMap(T front, T back) {
        this.front = front;
        this.back = back;
    }

    public T front() {
        return this.front;
    }

    public T back() {
        return this.back;
    }

    public T side(SignSide side) {
        return side == SignSide.BACK ? this.back : this.front;
    }

    public void set(T front, T back) {
        this.front = front;
        this.back = back;
    }

    public void setSide(SignSide side, T value) {
        if (side == SignSide.BACK) {
            this.back = value;
        } else {
            this.front = value;
        }
    }

    public void setFront(T front) {
        this.front = front;
    }

    public void setBack(T back) {
        this.back = back;
    }

    public List<T> both() {
        return Arrays.asList(this.front, this.back);
    }
}

