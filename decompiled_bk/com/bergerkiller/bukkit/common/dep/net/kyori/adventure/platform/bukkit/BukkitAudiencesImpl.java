/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.graph.MutableGraph
 *  org.bukkit.Bukkit
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.ProxiedCommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitAudiences;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetAudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointered;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.flattener.ComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.renderer.ComponentRenderer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.GlobalTranslator;
import com.google.common.collect.ImmutableList;
import com.google.common.graph.MutableGraph;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

final class BukkitAudiencesImpl
extends FacetAudienceProvider<CommandSender, BukkitAudience>
implements BukkitAudiences,
Listener {
    private static final Map<String, BukkitAudiences> INSTANCES;
    private final Plugin plugin;

    static Builder builder(@NotNull Plugin plugin) {
        return new Builder(plugin);
    }

    static BukkitAudiences instanceFor(@NotNull Plugin plugin) {
        return BukkitAudiencesImpl.builder(plugin).build();
    }

    BukkitAudiencesImpl(@NotNull Plugin plugin, @NotNull ComponentRenderer<Pointered> componentRenderer) {
        super(componentRenderer);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        ConsoleCommandSender console = this.plugin.getServer().getConsoleSender();
        this.addViewer(console);
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.addViewer(player);
        }
        this.registerEvent(PlayerJoinEvent.class, EventPriority.LOWEST, event -> this.addViewer(event.getPlayer()));
        this.registerEvent(PlayerQuitEvent.class, EventPriority.MONITOR, event -> this.removeViewer(event.getPlayer()));
    }

    @Override
    @NotNull
    public Audience sender(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return this.player((Player)sender);
        }
        if (sender instanceof ConsoleCommandSender) {
            return this.console();
        }
        if (sender instanceof ProxiedCommandSender) {
            return this.sender(((ProxiedCommandSender)sender).getCallee());
        }
        if (sender instanceof Entity || sender instanceof Block) {
            return Audience.empty();
        }
        return this.createAudience(Collections.singletonList(sender));
    }

    @Override
    @NotNull
    public Audience player(@NotNull Player player) {
        return super.player(player.getUniqueId());
    }

    @Override
    @NotNull
    protected BukkitAudience createAudience(@NotNull Collection<CommandSender> viewers) {
        return new BukkitAudience(this.plugin, this, viewers);
    }

    @Override
    public void close() {
        INSTANCES.remove(this.plugin.getName());
        super.close();
    }

    @Override
    @NotNull
    public ComponentFlattener flattener() {
        return BukkitComponentSerializer.FLATTENER;
    }

    private <T extends Event> void registerEvent(@NotNull Class<T> type, @NotNull EventPriority priority, @NotNull Consumer<T> callback) {
        Objects.requireNonNull(callback, "callback");
        this.plugin.getServer().getPluginManager().registerEvent(type, (Listener)this, priority, (listener, event) -> callback.accept(event), this.plugin, true);
    }

    static {
        Knob.OUT = message -> Bukkit.getLogger().log(Level.INFO, (String)message);
        Knob.ERR = (message, error) -> Bukkit.getLogger().log(Level.WARNING, (String)message, (Throwable)error);
        INSTANCES = Collections.synchronizedMap(new HashMap(4));
    }

    static final class Builder
    implements BukkitAudiences.Builder {
        @NotNull
        private final Plugin plugin;
        private ComponentRenderer<Pointered> componentRenderer;

        Builder(@NotNull Plugin plugin) {
            this.plugin = Objects.requireNonNull(plugin, "plugin");
            this.componentRenderer(ptr -> ptr.getOrDefault(Identity.LOCALE, DEFAULT_LOCALE), GlobalTranslator.renderer());
        }

        @Override
        @NotNull
        public Builder componentRenderer(@NotNull ComponentRenderer<Pointered> componentRenderer) {
            this.componentRenderer = Objects.requireNonNull(componentRenderer, "component renderer");
            return this;
        }

        @Override
        public @NotNull BukkitAudiences.Builder partition(@NotNull Function<Pointered, ?> partitionFunction) {
            Objects.requireNonNull(partitionFunction, "partitionFunction");
            return this;
        }

        @Override
        @NotNull
        public BukkitAudiences build() {
            return INSTANCES.computeIfAbsent(this.plugin.getName(), name -> {
                this.softDepend("ViaVersion");
                return new BukkitAudiencesImpl(this.plugin, this.componentRenderer);
            });
        }

        private void softDepend(@NotNull String pluginName) {
            PluginDescriptionFile file = this.plugin.getDescription();
            if (file.getName().equals(pluginName)) {
                return;
            }
            try {
                Field softDepend = MinecraftReflection.needField(file.getClass(), "softDepend");
                List dependencies = (List)softDepend.get(file);
                if (!dependencies.contains(pluginName)) {
                    ImmutableList newList = ImmutableList.builder().addAll((Iterable)dependencies).add((Object)pluginName).build();
                    softDepend.set(file, newList);
                }
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to inject softDepend in plugin.yml: %s %s", this.plugin, pluginName);
            }
            try {
                PluginManager manager = this.plugin.getServer().getPluginManager();
                Field dependencyGraphField = MinecraftReflection.needField(manager.getClass(), "dependencyGraph");
                MutableGraph graph = (MutableGraph)dependencyGraphField.get(manager);
                graph.putEdge((Object)file.getName(), (Object)pluginName);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

