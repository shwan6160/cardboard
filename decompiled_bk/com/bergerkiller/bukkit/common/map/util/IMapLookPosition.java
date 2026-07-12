/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ItemFrame
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import org.bukkit.entity.ItemFrame;

public interface IMapLookPosition {
    public ItemFrameInfo getItemFrameInfo();

    public ItemFrame getItemFrame();

    public int getX();

    public int getY();

    public double getDoubleX();

    public double getDoubleY();

    public double getDistance();

    public boolean isWithinBounds();

    public double getEdgeDistance();
}

