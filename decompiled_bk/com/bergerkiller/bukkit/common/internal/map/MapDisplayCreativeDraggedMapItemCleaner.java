/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CreativeDraggedMapItem;
import java.util.Iterator;
import org.bukkit.plugin.java.JavaPlugin;

class MapDisplayCreativeDraggedMapItemCleaner
extends Task {
    private final CommonMapController controller;

    public MapDisplayCreativeDraggedMapItemCleaner(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        CommonMapController commonMapController = this.controller;
        synchronized (commonMapController) {
            if (!this.controller.creativeDraggedMapItems.isEmpty()) {
                Iterator<CreativeDraggedMapItem> iter = this.controller.creativeDraggedMapItems.values().iterator();
                while (iter.hasNext()) {
                    if ((iter.next().life -= 60) > 0) continue;
                    iter.remove();
                }
            }
        }
    }
}

