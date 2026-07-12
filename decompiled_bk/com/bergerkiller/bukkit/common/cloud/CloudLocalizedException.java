/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;

public class CloudLocalizedException
extends IllegalArgumentException {
    private static final long serialVersionUID = -5070196948773497905L;
    private final CommandContext<?> context;
    private final ILocalizationEnum message;
    private final String[] input;

    public CloudLocalizedException(CommandContext<?> context, ILocalizationEnum message, String ... input) {
        this.context = context;
        this.message = message;
        this.input = input;
    }

    @Override
    public final String getMessage() {
        return this.message.get(this.input);
    }

    public final CommandContext<?> getContext() {
        return this.context;
    }
}

