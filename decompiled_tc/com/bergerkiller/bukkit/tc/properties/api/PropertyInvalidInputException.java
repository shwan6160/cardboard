/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.localization.LocalizationEnum
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.common.localization.LocalizationEnum;

public class PropertyInvalidInputException
extends RuntimeException {
    private static final long serialVersionUID = -8618056967820261214L;

    public PropertyInvalidInputException(String message) {
        super(message);
    }

    public PropertyInvalidInputException(LocalizationEnum localization, String ... arguments) {
        super(localization.get(arguments));
    }
}

