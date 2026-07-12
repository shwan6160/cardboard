/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

class ImageFrameIdZeroApplier {
    private static boolean applied = false;

    ImageFrameIdZeroApplier() {
    }

    public static void apply() {
        Object zeroMapIdIntRange;
        Collection exemptIdSet;
        if (applied) {
            return;
        }
        applied = true;
        Plugin imageFramePlugin = Bukkit.getPluginManager().getPlugin("ImageFrame");
        if (imageFramePlugin == null) {
            return;
        }
        Class imageFrameClass = imageFramePlugin.getClass();
        try {
            Field exemptField = imageFrameClass.getField("exemptMapIdsFromDeletion");
            exemptField.setAccessible(true);
            Object exemptIdSetObj = exemptField.get(null);
            if (!(exemptIdSetObj instanceof Collection)) {
                return;
            }
            exemptIdSet = (Collection)LogicUtil.unsafeCast(exemptIdSetObj);
        }
        catch (Throwable t) {
            return;
        }
        try {
            Method satisfiedMethod = exemptIdSet.getClass().getMethod("satisfies", Integer.TYPE);
            if (satisfiedMethod.getReturnType() != Boolean.TYPE) {
                return;
            }
            if (satisfiedMethod.invoke((Object)exemptIdSet, 0).equals(Boolean.TRUE)) {
                return;
            }
        }
        catch (Throwable t) {
            return;
        }
        try {
            Class<?> intRangeType = Class.forName("com.loohp.imageframe.objectholders.IntRange");
            zeroMapIdIntRange = intRangeType.getMethod("of", Integer.TYPE).invoke(null, 0);
            if (zeroMapIdIntRange == null) {
                return;
            }
        }
        catch (Throwable t) {
            return;
        }
        try {
            exemptIdSet.add(zeroMapIdIntRange);
        }
        catch (Throwable t) {
            return;
        }
        Logging.LOGGER_MAPDISPLAY.info("Added Map ID #0 to ImageFrame's 'exempt from deletion' set to avoid maps going empty");
    }
}

