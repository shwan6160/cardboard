/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.mountiplex.reflection.util.FastMethod
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatChangeEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PaperPlayerViewDistanceProperty
implements ICartProperty<Integer>,
Listener {
    public static final PaperPlayerViewDistanceProperty INSTANCE = new PaperPlayerViewDistanceProperty();
    private final Map<Player, PreviousViewSettings> previousViewSettings = new HashMap<Player, PreviousViewSettings>();
    private final FastMethod<Integer> getViewDistance = new FastMethod();
    private final FastMethod<Void> setViewDistance = new FastMethod();
    private final FastMethod<Integer> getSimulationDistance = new FastMethod();
    private final FastMethod<Void> setSimulationDistance = new FastMethod();
    private final FastMethod<Integer> getChunkSendDistance = new FastMethod();
    private final FastMethod<Void> setChunkSendDistance = new FastMethod();

    @CommandTargetTrain
    @PropertyCheckPermission(value="viewdistance")
    @Command(value="train viewdistance reset")
    @CommandDescription(value="Resets the view distance players inside the train have to the defaults")
    private void resetProperty(CommandSender sender, TrainProperties properties) {
        this.setProperty(sender, properties, -1);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="viewdistance")
    @Command(value="train viewdistance <num_chunks>")
    @CommandDescription(value="Sets the view distance players inside the train have")
    private void setProperty(CommandSender sender, TrainProperties properties, @Argument(value="num_chunks") int distance) {
        properties.set(this, distance);
        this.getProperty(sender, properties);
    }

    @Command(value="train viewdistance")
    @CommandDescription(value="Displays the view distance players inside the train have")
    private void getProperty(CommandSender sender, TrainProperties properties) {
        int distance = properties.get(this);
        if (distance >= 0) {
            sender.sendMessage(ChatColor.YELLOW + "View distance of players in the train: " + ChatColor.WHITE + distance + " chunks");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "View distance of players in the train: " + ChatColor.RED + "Default (not set)");
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="viewdistance")
    @Command(value="cart viewdistance reset")
    @CommandDescription(value="Resets the view distance players inside the cart have to the defaults")
    private void resetProperty(CommandSender sender, CartProperties properties) {
        this.setProperty(sender, properties, -1);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="viewdistance")
    @Command(value="cart viewdistance <num_chunks>")
    @CommandDescription(value="Sets the view distance players inside the cart have")
    private void setProperty(CommandSender sender, CartProperties properties, @Argument(value="num_chunks") int distance) {
        properties.set(this, distance);
        this.getProperty(sender, properties);
    }

    @Command(value="cart viewdistance")
    @CommandDescription(value="Displays the view distance players inside the cart have")
    private void getProperty(CommandSender sender, CartProperties properties) {
        int distance = properties.get(this);
        if (distance >= 0) {
            sender.sendMessage(ChatColor.YELLOW + "View distance of players in the cart: " + ChatColor.WHITE + distance + " chunks");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "View distance of players in the cart: " + ChatColor.RED + "Default (not set)");
        }
    }

    @PropertyParser(value="viewdistance|playerviewdistance")
    public int parseViewDistance(PropertyParseContext<Integer> context) {
        return context.inputInteger();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_VIEW_DISTANCE.has(sender);
    }

    @Override
    public Integer getDefault() {
        return -1;
    }

    @Override
    public Optional<Integer> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "paperPlayerViewDistance", Integer.TYPE);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Integer> value) {
        Util.setConfigOptional(config, "paperPlayerViewDistance", value);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        MinecartMember<?> member = MinecartMemberStore.getFromEntity(event.getPlayer().getVehicle());
        if (member != null && member.getProperties().get(this) >= 0) {
            this.restore(event.getPlayer());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onMemberSeatExit(MemberSeatExitEvent event) {
        if (!event.isPlayer()) {
            return;
        }
        if (!event.isMemberVehicleChange()) {
            return;
        }
        if (event.isSeatChange()) {
            MinecartMember<?> newMember = ((MemberSeatChangeEvent)event).getEnteredMember();
            int newViewDistance = newMember.getProperties().get(this);
            if (newViewDistance >= 0) {
                this.apply((Player)event.getEntity(), newViewDistance);
            } else if (event.getMember().getProperties().get(this) >= 0) {
                this.restore((Player)event.getEntity());
            }
            return;
        }
        if (event.getMember().getProperties().get(this) >= 0) {
            this.restore((Player)event.getEntity());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onMemberSeatEnter(MemberSeatEnterEvent event) {
        if (!event.isPlayer()) {
            return;
        }
        if (event.wasSeatChange()) {
            return;
        }
        int viewDistance = event.getMember().getProperties().get(this);
        if (viewDistance >= 0) {
            this.apply((Player)event.getEntity(), viewDistance);
        }
    }

    @Override
    public void set(CartProperties properties, Integer value) {
        block2: {
            IPropertiesHolder member;
            block3: {
                boolean hadViewDistance = (Integer)this.get(properties) >= 0;
                ICartProperty.super.set(properties, value);
                member = properties.getHolder();
                if (member == null || ((MinecartMember)member).isUnloaded()) break block2;
                if (!hadViewDistance || value >= 0) break block3;
                for (Player player : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
                    this.restore(player);
                }
                break block2;
            }
            if (value < 0) break block2;
            for (Player player : ((CommonMinecart)member.getEntity()).getPlayerPassengers()) {
                this.apply(player, value);
            }
        }
    }

    public void enable(TrainCarts plugin) throws Throwable {
        this.getViewDistance.init(Player.class.getMethod("getViewDistance", new Class[0]));
        this.setViewDistance.init(Player.class.getMethod("setViewDistance", Integer.TYPE));
        this.getSimulationDistance.init(Player.class.getMethod("getNoTickViewDistance", new Class[0]));
        this.setSimulationDistance.init(Player.class.getMethod("setNoTickViewDistance", Integer.TYPE));
        this.getChunkSendDistance.init(Player.class.getMethod("getSendViewDistance", new Class[0]));
        this.setChunkSendDistance.init(Player.class.getMethod("setSendViewDistance", Integer.TYPE));
        this.getViewDistance.forceInitialization();
        this.setViewDistance.forceInitialization();
        this.getSimulationDistance.forceInitialization();
        this.setSimulationDistance.forceInitialization();
        this.getChunkSendDistance.forceInitialization();
        this.setChunkSendDistance.forceInitialization();
        plugin.register(this);
    }

    public void disable(TrainCarts plugin) {
        for (Map.Entry<Player, PreviousViewSettings> e : this.previousViewSettings.entrySet()) {
            e.getValue().restore(e.getKey());
        }
    }

    private void restore(Player player) {
        PreviousViewSettings prevViewSettings = this.previousViewSettings.remove(player);
        if (prevViewSettings != null) {
            prevViewSettings.restore(player);
        }
    }

    private void apply(Player player, int viewDistance) {
        this.previousViewSettings.computeIfAbsent(player, x$0 -> new PreviousViewSettings((Player)x$0));
        viewDistance = MathUtil.clamp((int)viewDistance, (int)2, (int)31);
        this.setSimulationDistance.invoke((Object)player, (Object)(viewDistance + 1));
        this.setViewDistance.invoke((Object)player, (Object)(viewDistance + 1));
        this.setChunkSendDistance.invoke((Object)player, (Object)viewDistance);
    }

    private class PreviousViewSettings {
        public final int viewDistance;
        public final int simulationDistance;
        public final int chunkSendDistance;

        public PreviousViewSettings(Player player) {
            this.viewDistance = (Integer)PaperPlayerViewDistanceProperty.this.getViewDistance.invoke((Object)player);
            this.simulationDistance = (Integer)PaperPlayerViewDistanceProperty.this.getSimulationDistance.invoke((Object)player);
            this.chunkSendDistance = (Integer)PaperPlayerViewDistanceProperty.this.getChunkSendDistance.invoke((Object)player);
        }

        public void restore(Player player) {
            PaperPlayerViewDistanceProperty.this.setSimulationDistance.invoke((Object)player, (Object)this.simulationDistance);
            PaperPlayerViewDistanceProperty.this.setChunkSendDistance.invoke((Object)player, (Object)this.chunkSendDistance);
            PaperPlayerViewDistanceProperty.this.setViewDistance.invoke((Object)player, (Object)this.viewDistance);
        }

        public String toString() {
            return "View{distance=" + this.viewDistance + ", simulation=" + this.simulationDistance + ", chunk=" + this.chunkSendDistance + "}";
        }
    }
}

