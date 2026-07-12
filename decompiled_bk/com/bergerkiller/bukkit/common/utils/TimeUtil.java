/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.collections.StringMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {
    private static final StringMap<Integer> times = new StringMap();

    public static long getTime(String timeName) {
        try {
            String[] bits = timeName.split(":");
            if (bits.length == 2) {
                long hours = 1000L * (Long.parseLong(bits[0]) - 8L);
                long minutes = 1000L * Long.parseLong(bits[1]) / 60L;
                return hours + minutes;
            }
            return (long)((Double.parseDouble(timeName) - 8.0) * 1000.0);
        }
        catch (Exception ex) {
            Integer time = times.getLower(timeName);
            if (time != null) {
                return time.longValue();
            }
            return -1L;
        }
    }

    public static String getTimeString(long time) {
        int hours = (int)((time / 1000L + 8L) % 24L);
        int minutes = (int)(60L * (time % 1000L) / 1000L);
        return String.format("%02d:%02d (%d:%02d %s)", hours, minutes, hours % 12 == 0 ? 12 : hours % 12, minutes, hours < 12 ? "am" : "pm");
    }

    public static String now(String dateformat) {
        return TimeUtil.now(new SimpleDateFormat(dateformat));
    }

    public static String now(SimpleDateFormat format) {
        return format.format(Calendar.getInstance().getTime()).trim();
    }

    static {
        times.put("dawn", 22000);
        times.put("sunrise", 23000);
        times.put("morning", 24000);
        times.put("day", 24000);
        times.put("midday", 28000);
        times.put("noon", 28000);
        times.put("afternoon", 30000);
        times.put("evening", 32000);
        times.put("sunset", 37000);
        times.put("dusk", 37500);
        times.put("night", 38000);
        times.put("midnight", 16000);
    }
}

