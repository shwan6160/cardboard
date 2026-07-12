/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.material.Sign
 */
package com.bergerkiller.bukkit.common.internal.legacy;

import org.bukkit.Material;
import org.bukkit.material.Sign;

@Deprecated
public class CommonSignDataFix
extends Sign {
    private final boolean _isWallSign;

    public CommonSignDataFix(Material legacy_data_type, byte legacy_data_value, boolean isWallSign) {
        super(legacy_data_type, legacy_data_value);
        this._isWallSign = isWallSign;
    }

    public boolean isWallSign() {
        return this._isWallSign;
    }

    public CommonSignDataFix clone() {
        return (CommonSignDataFix)super.clone();
    }
}

