/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.cloud.captions;

import com.bergerkiller.bukkit.common.cloud.captions.BKCommonLibCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;

public class BKCommonLibCaptionRegistry {
    public static final String ARGUMENT_PARSE_FAILURE_SOUNDEFFECT = "'{input}' is not a valid sound effect name";

    public static <C> CaptionProvider<C> provider() {
        return CaptionProvider.forCaption(BKCommonLibCaptionKeys.ARGUMENT_PARSE_FAILURE_SOUNDEFFECT, r -> ARGUMENT_PARSE_FAILURE_SOUNDEFFECT);
    }
}

