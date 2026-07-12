/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.cloud.captions;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public final class BKCommonLibCaptionKeys {
    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<Caption>();
    public static final Caption ARGUMENT_PARSE_FAILURE_SOUNDEFFECT = BKCommonLibCaptionKeys.of("argument.parse.failure.bkcommonlib.soundeffect");

    private BKCommonLibCaptionKeys() {
    }

    private static Caption of(String key) {
        Caption caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    public static Collection<Caption> getBukkitCaptionKeys() {
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}

