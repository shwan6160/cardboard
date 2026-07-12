/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.function.IntPredicate;
import org.bukkit.plugin.Plugin;

class InteractiveBoardMapIDFilter
implements IntPredicate {
    private final Plugin plugin;
    private final InteractiveBoardLogicHandle handle;

    public InteractiveBoardMapIDFilter(Plugin plugin) {
        this.plugin = plugin;
        this.handle = Template.Class.create(InteractiveBoardLogicHandle.class);
        this.handle.forceInitialization();
    }

    @Override
    public boolean test(int mapId) {
        return this.handle.isMapFiltered(this.plugin, mapId);
    }

    @Template.Optional
    public static abstract class InteractiveBoardLogicHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isMapFiltered(com.interactiveboard.InteractiveBoard plugin, int mapId) {\n    return plugin.getBoardDisplayManager().isBoardMap(mapId);\n}")
        public abstract boolean isMapFiltered(Object var1, int var2);
    }
}

