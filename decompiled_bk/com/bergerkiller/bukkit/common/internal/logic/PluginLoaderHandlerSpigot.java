/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

class PluginLoaderHandlerSpigot
extends PluginLoaderHandler {
    private final List<String> classDependPlugins;
    private static final boolean hasProvidesMethod;
    private static Function<Plugin, List<String>> providesMethod;

    public PluginLoaderHandlerSpigot(Plugin plugin, String pluginConfigText) {
        super(plugin, pluginConfigText);
        List classDepend = this.pluginConfig.getStringList("classdepend");
        if (classDepend == null) {
            classDepend = Collections.emptyList();
        }
        this.classDependPlugins = classDepend;
    }

    @Override
    public void bootstrap() {
        this.silenceClassLoadWarningsFromProvides();
        this.addAccessToClassloaders(this.classDependPlugins);
    }

    @Override
    public void onPluginLoaded(Plugin plugin) {
    }

    @Override
    public void addAccessToClassloader(Plugin plugin) {
        this.addAccessToClassloaders(Collections.singletonList(plugin.getName()));
    }

    @Override
    public void addAccessToClassloaders(List<String> pluginNames) {
        try {
            Field seenIllegalAccessField = this.getClassLoader().getClass().getDeclaredField("seenIllegalAccess");
            if (!seenIllegalAccessField.getType().equals(Set.class)) {
                return;
            }
            seenIllegalAccessField.setAccessible(true);
            Set seenIllegalAccess = (Set)seenIllegalAccessField.get(this.getClassLoader());
            seenIllegalAccess.addAll(pluginNames);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void silenceClassLoadWarningsFromProvides() {
        if (!hasProvidesMethod) {
            return;
        }
        try {
            Field seenIllegalAccessField = this.getClassLoader().getClass().getDeclaredField("seenIllegalAccess");
            if (!seenIllegalAccessField.getType().equals(Set.class)) {
                return;
            }
            seenIllegalAccessField.setAccessible(true);
            final Set seenIllegalAccess = (Set)seenIllegalAccessField.get(this.getClassLoader());
            AbstractSet<String> hook = new AbstractSet<String>(this){
                final /* synthetic */ PluginLoaderHandlerSpigot this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean contains(Object o) {
                    Plugin pluginByName;
                    if (seenIllegalAccess.contains(o)) {
                        return true;
                    }
                    if (o instanceof String && (pluginByName = Bukkit.getPluginManager().getPlugin((String)o)) != null) {
                        for (String provides : (List)providesMethod.apply(pluginByName)) {
                            if (!seenIllegalAccess.contains(provides)) continue;
                            seenIllegalAccess.add((String)o);
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean add(String value) {
                    return seenIllegalAccess.add(value);
                }

                @Override
                public boolean remove(Object value) {
                    return seenIllegalAccess.remove(value);
                }

                @Override
                public Iterator<String> iterator() {
                    return seenIllegalAccess.iterator();
                }

                @Override
                public int size() {
                    return seenIllegalAccess.size();
                }

                @Override
                public void clear() {
                    seenIllegalAccess.clear();
                }
            };
            seenIllegalAccessField.set(this.getClassLoader(), hook);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static {
        Method getProvidesMethodTmp = null;
        try {
            getProvidesMethodTmp = PluginDescriptionFile.class.getMethod("getProvides", new Class[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        boolean bl = hasProvidesMethod = getProvidesMethodTmp != null;
        if (hasProvidesMethod) {
            Method getProvidesMethod = getProvidesMethodTmp;
            providesMethod = p -> {
                try {
                    return (List)getProvidesMethod.invoke((Object)p.getDescription(), new Object[0]);
                }
                catch (Throwable t) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error checking provides list", t);
                    return Collections.emptyList();
                }
            };
        } else {
            providesMethod = p -> Collections.emptyList();
        }
    }
}

