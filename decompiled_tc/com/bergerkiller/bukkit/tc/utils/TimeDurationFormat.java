/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeDurationFormat {
    private final TimeZone timeZone;
    private final SimpleDateFormat sdf;

    public TimeDurationFormat(String format) {
        if (format == null) {
            throw new IllegalArgumentException("Input format should not be null");
        }
        this.timeZone = TimeZone.getTimeZone("GMT+0");
        this.sdf = new SimpleDateFormat(format, Locale.getDefault());
        this.sdf.setTimeZone(this.timeZone);
    }

    public String format(long durationMillis) {
        return this.sdf.format(new Date(durationMillis - (long)this.timeZone.getRawOffset()));
    }
}

