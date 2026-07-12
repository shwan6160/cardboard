/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.StringMap
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.collections.StringMap;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Effect {
    private static final StringMap<Integer> DIR_NAMES = new StringMap();
    private static final StringMap<Integer> DISK_NAMES = new StringMap();
    public final List<String> effects = new ArrayList<String>();
    public float pitch = 1.0f;
    public float volume = 1.0f;
    public int range;

    public void parseEffect(String text) {
        text = text.toUpperCase(Locale.ENGLISH).replace(' ', '_');
        if ((text = text.replace("MUSIC", "RECORD")).equals("LINK")) {
            this.effects.add("SMOKE");
            this.effects.add("EXTINGUISH");
            return;
        }
        this.effects.add(text);
    }

    private String trimSpace(String text) {
        return text.substring(StringUtil.getSuccessiveCharCount((String)text, (char)'_'));
    }

    public void play(Player player) {
        this.play(player.getEyeLocation(), player);
    }

    public void play(Location location) {
        this.play(location, null);
    }

    public void play(Location location, Player player) {
        for (String name : this.effects) {
            Integer data;
            if (name.startsWith("SMOKE")) {
                name = this.trimSpace(name.substring(5));
                data = null;
                if (name.length() >= 2) {
                    data = (Integer)DIR_NAMES.get((Object)name.substring(0, 2));
                }
                if (data == null && name.length() >= 1) {
                    data = (Integer)DIR_NAMES.get((Object)name.substring(0, 1));
                }
                if (data == null) {
                    try {
                        data = ParseUtil.parseInt((String)name, null);
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                if (data == null) {
                    data = 0;
                }
                if (data == 4) {
                    Effect.playEffect(location.clone().add(0.0, 0.5, 0.0), player, org.bukkit.Effect.SMOKE, data);
                    continue;
                }
                Effect.playEffect(location, player, org.bukkit.Effect.SMOKE, data);
                continue;
            }
            if (name.startsWith("RECORD")) {
                if ((name = this.trimSpace(name.substring(6))).startsWith("PLAY")) {
                    name = this.trimSpace(name.substring(4));
                }
                if ((data = (Integer)DISK_NAMES.get((Object)name)) == null) {
                    try {
                        data = ParseUtil.parseInt((String)name, null);
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                if (data == null) {
                    data = 2257;
                }
                Effect.playEffect(location, player, org.bukkit.Effect.RECORD_PLAY, data);
                continue;
            }
            org.bukkit.Effect effect = (org.bukkit.Effect)ParseUtil.parseEnum(org.bukkit.Effect.class, (String)name, null);
            if (effect != null && !effect.toString().toUpperCase(Locale.ENGLISH).endsWith("_BREAK")) {
                Effect.playEffect(location, player, effect, 0);
                continue;
            }
            Sound sound = (Sound)ParseUtil.parseEnum(Sound.class, (String)name, null);
            if (sound != null) {
                Effect.playSound(location, player, sound, this.volume, this.pitch);
                continue;
            }
            Effect.playSound(location, player, name, this.volume, this.pitch);
        }
    }

    private static void playEffect(Location location, Player player, org.bukkit.Effect effect, int data) {
        try {
            if (player != null) {
                player.playEffect(location, effect, data);
            } else {
                location.getWorld().playEffect(location, effect, data);
            }
            location.getWorld().playEffect(location, effect, data);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void playSound(Location location, Player player, Sound sound, float volume, float pitch) {
        try {
            if (player != null) {
                player.playSound(location, sound, volume, pitch);
            } else {
                location.getWorld().playSound(location, sound, volume, pitch);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void playSound(Location location, Player player, String name, float volume, float pitch) {
        try {
            if (player != null) {
                PlayerUtil.playSound((Player)player, (Location)location, (ResourceKey)SoundEffect.fromName((String)name), (float)volume, (float)pitch);
            } else {
                WorldUtil.playSound((Location)location, (ResourceKey)SoundEffect.fromName((String)name), (float)volume, (float)pitch);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static {
        DIR_NAMES.putUpper("U", (Object)4);
        DIR_NAMES.putUpper("M", (Object)4);
        DIR_NAMES.putUpper("N", (Object)7);
        DIR_NAMES.putUpper("E", (Object)3);
        DIR_NAMES.putUpper("S", (Object)1);
        DIR_NAMES.putUpper("W", (Object)5);
        DIR_NAMES.putUpper("NE", (Object)6);
        DIR_NAMES.putUpper("SE", (Object)0);
        DIR_NAMES.putUpper("NW", (Object)8);
        DIR_NAMES.putUpper("SW", (Object)2);
        DISK_NAMES.putUpper("NONE", (Object)0);
        DISK_NAMES.putUpper("13", (Object)2256);
        DISK_NAMES.putUpper("CAT", (Object)2257);
        DISK_NAMES.putUpper("BLOCKS", (Object)2258);
        DISK_NAMES.putUpper("CHIRP", (Object)2259);
        DISK_NAMES.putUpper("FAR", (Object)2260);
        DISK_NAMES.putUpper("MALL", (Object)2261);
        DISK_NAMES.putUpper("MELLOHI", (Object)2262);
        DISK_NAMES.putUpper("STAL", (Object)2263);
        DISK_NAMES.putUpper("STRAD", (Object)2264);
        DISK_NAMES.putUpper("WARD", (Object)2265);
        DISK_NAMES.putUpper("11", (Object)2266);
        DISK_NAMES.putUpper("WAIT", (Object)2267);
    }
}

