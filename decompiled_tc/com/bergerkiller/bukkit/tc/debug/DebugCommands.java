/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.bases.IntVector2
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.math.OrientedBoundingBox
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.DebugUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode
 *  com.bergerkiller.bukkit.common.wrappers.RelativeFlags
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.vehicle.VehicleEnterEvent
 *  org.bukkit.event.vehicle.VehicleExitEvent
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.debug;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.OrientedBoundingBox;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayItemEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualBoundingBox;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.global.SignControllerWorld;
import com.bergerkiller.bukkit.tc.debug.DebugTool;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTypeListDestinations;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTypeRails;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTypeTrackDistance;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupManager;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.utils.EventListenerHook;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class DebugCommands {
    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug event vehicle_enter [enabled]")
    @CommandDescription(value="Broadcasts a message when a vehicle enter is cancelled by a plugin")
    private void commandDebugEventVehicleEnter(CommandSender sender, @Argument(value="enabled") boolean enabled) {
        sender.sendMessage(ChatColor.RED + "Vehicle enter debug mode: " + Localization.boolStr(enabled));
        if (enabled) {
            EventListenerHook.hook(VehicleEnterEvent.class, (listener, callEvent, event) -> {
                boolean wasCancelled = event.isCancelled();
                callEvent.accept(event);
                if (!wasCancelled && event.isCancelled() && MinecartMemberStore.getFromEntity((Entity)event.getVehicle()) != null) {
                    Bukkit.broadcastMessage((String)("[TrainCarts] Vehicle enter by " + event.getEntered().getName() + " was cancelled by plugin " + listener.getPlugin().getName()));
                }
            });
            sender.sendMessage(ChatColor.YELLOW + "A message will be broadcast when entering a traincarts minecart is cancelled by a plugin, with details");
            sender.sendMessage(ChatColor.YELLOW + "Use /train debug vehicle_enter false to turn off again");
        } else {
            EventListenerHook.unhook(VehicleEnterEvent.class);
        }
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug event vehicle_exit [enabled]")
    @CommandDescription(value="Broadcasts a message when a vehicle exit is cancelled by a plugin")
    private void commandDebugEventVehicleExit(CommandSender sender, @Argument(value="enabled") boolean enabled) {
        sender.sendMessage(ChatColor.RED + "Vehicle exit debug mode: " + Localization.boolStr(enabled));
        if (enabled) {
            EventListenerHook.hook(VehicleExitEvent.class, (listener, callEvent, event) -> {
                boolean wasCancelled = event.isCancelled();
                callEvent.accept(event);
                if (!wasCancelled && event.isCancelled() && MinecartMemberStore.getFromEntity((Entity)event.getVehicle()) != null) {
                    Bukkit.broadcastMessage((String)("[TrainCarts] Vehicle exit by " + event.getExited().getName() + " was cancelled by plugin " + listener.getPlugin().getName()));
                }
            });
            sender.sendMessage(ChatColor.YELLOW + "A message will be broadcast when exiting a traincarts minecart is cancelled by a plugin, with details");
            sender.sendMessage(ChatColor.YELLOW + "Use /train debug vehicle_exit false to turn off again");
        } else {
            EventListenerHook.unhook(VehicleExitEvent.class);
        }
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug rails")
    @CommandDescription(value="Get a debug stick item to visually display what path tracks use")
    private void commandDebugRails(Player player) {
        new DebugToolTypeRails().giveToPlayer(player);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug distance")
    @CommandDescription(value="Get a debug stick item to display the track distance between two points")
    private void commandDebugTrackDistance(Player player) {
        new DebugToolTypeTrackDistance().giveToPlayer(player);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug destinations")
    @CommandDescription(value="Get a debug stick item to visually display the possible path finding routes")
    private void commandDebugDestinationAll(Player player, @Flag(value="max-destinations") Integer maxDestinations) {
        new DebugToolTypeListDestinations().setMaxDestinations(maxDestinations != null ? maxDestinations : 5).giveToPlayer(player);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug destination")
    @CommandDescription(value="Get a debug stick item to visually display the possible path finding routes")
    private void commandDebugDestinationAll(Player player) {
        new DebugToolTypeListDestinations().giveToPlayer(player);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug destination <destination>")
    @CommandDescription(value="Get a debug stick item to visually display the route towards a destination")
    private void commandDebugDestinationName(Player player, @Quoted @Argument(value="destination", suggestions="destinations") String destination) {
        new DebugToolTypeListDestinations(destination).giveToPlayer(player);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug destination <destination> teleport")
    @CommandDescription(value="Get a debug stick item to visually display the route towards a destination")
    private void commandDebugTeleportToDestination(Player player, TrainCarts plugin, @Quoted @Argument(value="destination", suggestions="destinations") String destination) {
        PathNode node = plugin.getPathProvider().getWorld(player.getWorld()).getNodeByName(destination);
        if (node == null && (node = (PathNode)plugin.getPathProvider().getWorlds().stream().map(w -> w.getNodeByName(destination)).findFirst().orElse(null)) == null) {
            player.sendMessage(ChatColor.RED + "Destination with name '" + destination + "' not found");
            return;
        }
        RailPiece rail = RailPiece.create(node.location.getBlock());
        if (rail.isNone()) {
            player.sendMessage(ChatColor.RED + "There are no rails at this destination! (No longer exists?)");
            return;
        }
        RailState spawnState = RailState.getSpawnState(rail);
        player.teleport(spawnState.positionLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to destination '" + ChatColor.YELLOW + destination + ChatColor.GREEN + "'!");
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug mutex")
    @CommandDescription(value="Displays the area of effect of all nearby mutex signs")
    private void commandDebugMutex(Player player, TrainCarts traincarts) {
        DebugTool.showMutexZones(traincarts, player);
        player.sendMessage(ChatColor.GREEN + "Displaying mutex zones near your position");
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug railtracker <enabled>")
    @CommandDescription(value="Sets whether the rail tracker debugging is currently enabled")
    private void commandDebugSetRailTracker(CommandSender sender, @Argument(value="enabled") boolean enabled) {
        TCConfig.railTrackerDebugEnabled = enabled;
        this.commandDebugCheckRailTracker(sender);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug railtracker")
    @CommandDescription(value="Checks whether the rail tracker debugging is currently enabled")
    private void commandDebugCheckRailTracker(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Displaying tracked rail positions: " + (TCConfig.railTrackerDebugEnabled ? "ENABLED" : ChatColor.RED + "DISABLED"));
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug wheeltracker <enabled>")
    @CommandDescription(value="Sets whether the rail tracker debugging is currently enabled")
    private void commandDebugSetWheelTracker(CommandSender sender, @Argument(value="enabled") boolean enabled) {
        TCConfig.wheelTrackerDebugEnabled = enabled;
        this.commandDebugCheckWheelTracker(sender);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug wheeltracker")
    @CommandDescription(value="Checks whether the wheel tracker debugging is currently enabled")
    private void commandDebugCheckWheelTracker(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Displaying tracked wheel positions: " + (TCConfig.wheelTrackerDebugEnabled ? "ENABLED" : ChatColor.RED + "DISABLED"));
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug splitting <enabled>")
    @CommandDescription(value="Sets whether messages are logged when trains split apart")
    private void commandDebugSetSplitDebugEnabled(CommandSender sender, @Argument(value="enabled") boolean enabled) {
        TCConfig.logTrainSplitting = enabled;
        this.commandDebugCheckSplitDebugEnabled(sender);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug splitting")
    @CommandDescription(value="Checks whether messages are logged when trains split apart")
    private void commandDebugCheckSplitDebugEnabled(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Logging messages when trains split apart: " + (TCConfig.logTrainSplitting ? "ENABLED" : ChatColor.RED + "DISABLED"));
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug fix signs")
    @CommandDescription(value="Forcibly recalculates all cached sign information near the player")
    private void commandDebugFixSigns(Player player, TrainCarts plugin, @Flag(value="redetect_actions") boolean redetectSignActions) {
        if (!TCConfig.enableVanillaActionSigns) {
            player.sendMessage(ChatColor.RED + "Vanilla action signs are disabled in TrainCarts config.yml!");
            return;
        }
        int radius = Bukkit.getViewDistance() - 1;
        IntVector2 mid = IntVector3.blockOf((Location)player.getLocation()).toChunkCoordinates();
        SignControllerWorld controller = plugin.getSignController().forWorld(player.getWorld());
        SignControllerWorld.RefreshResult result = SignControllerWorld.RefreshResult.NONE;
        for (int cx = -radius; cx <= radius; ++cx) {
            for (int cz = -radius; cz <= radius; ++cz) {
                Chunk chunk = WorldUtil.getChunk((World)player.getWorld(), (int)(mid.x + cx), (int)(mid.z + cz));
                if (chunk == null) continue;
                result = result.add(controller.refreshInChunk(chunk));
            }
        }
        if (result.numAdded == 0 && result.numRemoved == 0) {
            player.sendMessage(ChatColor.GREEN + "All signs are correctly cached");
        } else {
            if (result.numRemoved > 0) {
                player.sendMessage(ChatColor.RED.toString() + result.numRemoved + " signs were removed from the cache because they were incorrect!");
            }
            if (result.numAdded > 0) {
                player.sendMessage(ChatColor.YELLOW.toString() + result.numAdded + " signs were missing and have been added to the cache!");
            }
        }
        if (redetectSignActions) {
            plugin.redetectSignActions();
            player.sendMessage(ChatColor.GREEN + "Recalculated the registered sign action for all signs on the server");
        }
    }

    @CommandRequiresPermission(value=Permission.COMMAND_FIXBUGGED)
    @Command(value="train debug fix buggedminecarts")
    @CommandDescription(value="Forcibly removes minecarts and trackers that have glitched out")
    private void commandFixBugged(CommandSender sender) {
        for (World world : WorldUtil.getWorlds()) {
            OfflineGroupManager.removeBuggedMinecarts(world);
        }
        sender.sendMessage(ChatColor.YELLOW + "Bugged minecarts have been forcibly removed.");
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug railcache export")
    @CommandDescription(value="Exports the rail block coordinates inside the current player world's rail cache")
    private void commandDebugRailCacheExport(Player player, TrainCarts plugin) {
        Collection<IntVector3> blocks = RailLookup.forWorld(player.getWorld()).getBlockIndex();
        StringBuffer buffer = new StringBuffer(blocks.size() * 20);
        for (IntVector3 block : blocks) {
            buffer.append(block.x).append(' ').append(block.y).append(' ').append(block.z).append("\r\n");
        }
        TCConfig.hastebin.upload(buffer.toString()).thenAccept(t -> {
            if (t.success()) {
                player.sendMessage(ChatColor.GREEN + "Rail cache block index exported: " + ChatColor.WHITE + ChatColor.UNDERLINE + t.url());
            } else {
                player.sendMessage(ChatColor.RED + "Failed to export rail cache block coordinates: " + t.error());
            }
        });
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug loading unload")
    @CommandDescription(value="Forces the targeted train to unload, even if it otherwise wouldn't")
    private void commandDebugUnloadTrain(CommandSender sender, MinecartGroup group) {
        String name = group.getProperties().getTrainName();
        group.unload();
        sender.sendMessage(ChatColor.YELLOW + "Train '" + ChatColor.WHITE + name + ChatColor.YELLOW + "' unloaded!");
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug loading refresh")
    @CommandDescription(value="Forcibly checks all unloaded trains if they can be loaded, and loads them in")
    private void commandDebugForceLoadTrains(CommandSender sender, TrainCarts trainCarts) {
        int loadedBefore = MinecartGroupStore.getGroups().size();
        trainCarts.getOfflineGroups().refresh();
        int loadedAfter = MinecartGroupStore.getGroups().size();
        if (loadedBefore == loadedAfter) {
            sender.sendMessage(ChatColor.YELLOW + "Forcibly refreshed trains on all worlds, no trains loaded");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Forcibly refreshed trains on all worlds, " + ChatColor.WHITE + (loadedAfter - loadedBefore) + ChatColor.YELLOW + " trains loaded");
        }
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug pvc fly")
    private void commandTestFlight(final Player player, final TrainCarts plugin, final @Flag(value="stop_on_collide") boolean stopOnBlockCollision) {
        new Task(this, (JavaPlugin)plugin){
            Quaternion rotation;
            AttachmentViewer.MovementController controller;
            Location loc;
            int ctr;
            final int duration = 200000;
            double rotX;
            double rotY;
            double radius;
            double speed;
            final double incr = 0.5;
            Vector lastMotion;
            final /* synthetic */ DebugCommands this$0;
            {
                this.this$0 = this$0;
                super(arg0);
                this.rotation = new Quaternion();
                this.controller = plugin.getAttachmentViewer(player).controlMovement();
                this.loc = player.getLocation();
                this.ctr = 0;
                this.duration = 200000;
                this.rotX = 0.0;
                this.rotY = 0.0;
                this.radius = 1.0;
                this.speed = 0.0;
                this.incr = 0.5;
                this.lastMotion = new Vector();
            }

            public void run() {
                ++this.ctr;
                if (this.ctr > 200005) {
                    this.controller.stop();
                    this.stop();
                }
                if (this.ctr > 200002) {
                    return;
                }
                if (player.isSneaking()) {
                    this.controller.stop();
                    this.stop();
                    return;
                }
                if (!player.isValid()) {
                    this.stop();
                    this.controller.stop();
                    return;
                }
                if (!this.controller.update(this.loc.toVector(), stopOnBlockCollision)) {
                    this.stop();
                    return;
                }
                this.speed *= 0.9;
                AttachmentViewer.Input input = this.controller.getInput();
                if (input.hasWalkInput() || input.jumping() || input.sneaking()) {
                    this.lastMotion = new Vector();
                    this.speed += 0.2;
                } else if (this.speed < 0.01) {
                    this.speed = 0.0;
                }
                Quaternion q = Quaternion.fromLookDirection((Vector)player.getEyeLocation().getDirection(), (Vector)new Vector(0, 1, 0));
                if (input.jumping()) {
                    this.lastMotion.add(q.upVector());
                }
                if (input.forwards()) {
                    this.lastMotion.add(q.forwardVector());
                } else if (input.backwards()) {
                    this.lastMotion.add(q.forwardVector().multiply(-1.0));
                }
                if (input.left()) {
                    this.lastMotion.add(q.rightVector());
                } else if (input.right()) {
                    this.lastMotion.add(q.rightVector().multiply(-1.0));
                }
                double fixedSpeed = this.speed;
                if (input.sprinting()) {
                    fixedSpeed *= 2.0;
                }
                this.loc.add(this.lastMotion.clone().multiply(fixedSpeed));
            }
        }.start(5L, 1L);
        player.sendMessage("Started");
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug pvc swing")
    private void commandTestSwing(final Player player, final TrainCarts plugin, final @Flag(value="no_horizontal") boolean no_horizontal, final @Flag(value="no_vertical") boolean no_vertical) {
        double radius = 10.0;
        final Vector center = new Vector(-175.5, 14.1, 354.5);
        new Task(this, (JavaPlugin)plugin){
            Quaternion rotation;
            AttachmentViewer.MovementController controller;
            int ctr;
            final int duration = 200000;
            final /* synthetic */ DebugCommands this$0;
            {
                this.this$0 = this$0;
                super(arg0);
                this.rotation = new Quaternion();
                this.controller = plugin.getAttachmentViewer(player).controlMovement();
                this.ctr = 0;
                this.duration = 200000;
            }

            public void run() {
                ++this.ctr;
                if (this.ctr > 200005) {
                    this.controller.stop();
                    this.stop();
                }
                if (this.ctr > 200002) {
                    return;
                }
                if (player.isSneaking()) {
                    player.teleport(center.toLocation(player.getWorld()));
                    this.controller.stop();
                    this.stop();
                    return;
                }
                if (!player.isValid()) {
                    this.stop();
                    this.controller.stop();
                    return;
                }
                Vector pos = center.clone().add(this.rotation.forwardVector().multiply(10.0));
                if (no_horizontal) {
                    pos.setX(center.getX());
                    pos.setZ(center.getZ());
                }
                if (no_vertical) {
                    pos.setY(center.getY());
                }
                if (this.ctr <= 1) {
                    Location loc = player.getLocation();
                    loc.setX(pos.getX());
                    loc.setY(pos.getY());
                    loc.setZ(pos.getZ());
                    player.setFlying(true);
                    player.teleport(loc);
                    player.setFlying(true);
                }
                this.controller.update(pos);
                if (this.ctr > 0) {
                    this.rotation.rotateX(4.0);
                }
            }
        }.start(5L, 1L);
        player.sendMessage("Started");
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug display")
    private void commandDebugDisplayEntity(Player player, TrainCarts plugin) {
        Location location = player.getEyeLocation();
        final Matrix4x4 transform = Matrix4x4.fromLocation((Location)location);
        final VirtualDisplayItemEntity entity = new VirtualDisplayItemEntity(null);
        entity.setItem(ItemDisplayMode.HEAD, CommonItemStack.create((Material)MaterialUtil.getFirst((String[])new String[]{"JACK_O_LANTERN", "LEGACY_JACK_O_LANTERN"}), (int)1).toBukkit());
        AttachmentViewer viewer = plugin.getAttachmentViewer(player);
        entity.updatePosition(transform);
        entity.syncPosition(true);
        entity.spawn(viewer, new Vector());
        new Task(this, (JavaPlugin)plugin){
            double movement;
            double fx;
            final /* synthetic */ DebugCommands this$0;
            {
                this.this$0 = this$0;
                super(arg0);
                this.movement = 0.0;
                this.fx = 0.1;
            }

            public void run() {
                boolean changed = false;
                double x = DebugUtil.getDoubleValue((String)"x", (double)0.0);
                double y = DebugUtil.getDoubleValue((String)"y", (double)0.0);
                double z = DebugUtil.getDoubleValue((String)"z", (double)0.0);
                if (x != 0.0) {
                    transform.rotateX(x);
                    changed = true;
                }
                if (y != 0.0) {
                    transform.rotateY(y);
                    changed = true;
                }
                if (z != 0.0) {
                    transform.rotateZ(z);
                    changed = true;
                }
                if (changed) {
                    Quaternion quaternion = transform.getRotation();
                }
                transform.worldTranslate(this.fx, 0.0, 0.0);
                this.movement += this.fx;
                if (Math.abs(this.movement) > 5.0) {
                    this.fx = -this.fx;
                }
                entity.updatePosition(transform);
                entity.syncPosition(true);
            }
        }.start(1L, 1L);
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug sendpos")
    private void commandDebugSendPositionPacket(Player player, TrainCarts plugin, @Flag(value="x") Double x, @Flag(value="y") Double y, @Flag(value="z") Double z, @Flag(value="yaw") Float yaw, @Flag(value="pitch") Float pitch, @Flag(value="dx") Double dx, @Flag(value="dy") Double dy, @Flag(value="dz") Double dz, @Flag(value="relpos") boolean relpos, @Flag(value="reldeltapos") boolean reldeltapos, @Flag(value="relrot") boolean relrot, @Flag(value="look") boolean look) {
        double p_x = (Double)LogicUtil.fixNull((Object)x, (Object)0.0);
        double p_y = (Double)LogicUtil.fixNull((Object)y, (Object)0.0);
        double p_z = (Double)LogicUtil.fixNull((Object)z, (Object)0.0);
        float p_yaw = ((Float)LogicUtil.fixNull((Object)yaw, (Object)Float.valueOf(0.0f))).floatValue();
        float p_pitch = ((Float)LogicUtil.fixNull((Object)pitch, (Object)Float.valueOf(0.0f))).floatValue();
        double p_dx = (Double)LogicUtil.fixNull((Object)dx, (Object)0.0);
        double p_dy = (Double)LogicUtil.fixNull((Object)dy, (Object)0.0);
        double p_dz = (Double)LogicUtil.fixNull((Object)dz, (Object)0.0);
        RelativeFlags flags = RelativeFlags.fromRawRelativeFlags(Collections.emptySet());
        if (relpos) {
            flags = flags.withRelativeX().withRelativeY().withRelativeZ();
        }
        if (reldeltapos) {
            flags = flags.withRelativeDeltaX().withRelativeDeltaY().withRelativeDeltaZ();
        }
        if (look || relrot) {
            flags = flags.withRelativeRotation();
        }
        PacketUtil.sendPacket((Player)player, (PacketHandle)ClientboundPlayerPositionPacketHandle.createNew((double)p_x, (double)p_y, (double)p_z, (float)(look ? 0.0f : p_yaw), (float)(look ? 0.0f : p_pitch), (double)p_dx, (double)p_dy, (double)p_dz, (RelativeFlags)flags));
        if (look) {
            // empty if block
        }
    }

    @CommandRequiresPermission(value=Permission.DEBUG_COMMAND_DEBUG)
    @Command(value="train debug surface")
    private void commandDebugShulkerSurface(TrainCarts plugin, Player player, @Flag(value="width") Double widthFlag, @Flag(value="height") Double heightFlag, @Flag(value="behind") boolean behind) {
        AttachmentViewer viewer = plugin.getAttachmentViewer(player);
        Quaternion orientation = Quaternion.fromLookDirection((Vector)player.getEyeLocation().getDirection(), (Vector)new Vector(0, 1, 0));
        OrientedBoundingBox bbox = new OrientedBoundingBox();
        Location eyeLoc = viewer.getPlayer().getEyeLocation();
        bbox.setPosition(eyeLoc.add(eyeLoc.getDirection().setY(0.0).normalize().multiply(behind ? -10.0 : 10.0)).toVector());
        bbox.setSize(((Double)LogicUtil.fixNull((Object)widthFlag, (Object)5.0)).doubleValue(), 0.0, ((Double)LogicUtil.fixNull((Object)heightFlag, (Object)5.0)).doubleValue());
        bbox.setOrientation(orientation);
        VirtualBoundingBox particle = VirtualBoundingBox.createPlane(null, MaterialUtil.getFirst((String[])new String[]{"ICE", "LEGACY_ICE"}));
        particle.update(bbox);
        particle.spawn(viewer, new Vector());
        viewer.createCollisionSurface(Integer.MAX_VALUE).setShape(bbox);
    }
}

