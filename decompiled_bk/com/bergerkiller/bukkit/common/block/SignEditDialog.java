/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBlockUpdatePacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public abstract class SignEditDialog {
    private static Handler handler = null;
    private Material signType = MaterialUtil.getFirst("OAK_WALL_SIGN", "LEGACY_WALL_SIGN");

    public void onOpened(Player player, String[] lines) {
    }

    public abstract void onClosed(Player var1, String[] var2);

    public void onClosedWithoutLines(Player player) {
        this.onAborted(player);
    }

    public void onAborted(Player player) {
    }

    public final Material getSignType() {
        return this.signType;
    }

    public final SignEditDialog setSignType(Material signType) {
        this.signType = signType;
        return this;
    }

    public final boolean open(Player player) {
        return this.open(player, new String[]{"", "", "", ""});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final boolean open(Player player, String[] initialLines) {
        if (!CommonPlugin.hasInstance()) {
            return false;
        }
        Handler handler = SignEditDialog.handler;
        if (handler != null && handler.getMetadata(player) != null) {
            return false;
        }
        Location eyeLocation = player.getEyeLocation();
        BlockFace lookDirection = FaceUtil.vectorToBlockFace(eyeLocation.getDirection(), false);
        BlockFace backDirection = FaceUtil.yawToFace(eyeLocation.getYaw() - 90.0f, false).getOppositeFace();
        Block signBlock = eyeLocation.getBlock().getRelative(backDirection);
        if (lookDirection != BlockFace.DOWN) {
            signBlock = signBlock.getRelative(BlockFace.DOWN);
        }
        Block curr = signBlock;
        for (int k = 0; k < 10; ++k) {
            Block airBlock = SignEditDialog.findAirBlockAbove(curr);
            if (airBlock != null) {
                signBlock = airBlock;
                break;
            }
            curr = curr.getRelative(backDirection);
        }
        PlayerMetadata metadata = new PlayerMetadata(this, player, signBlock);
        if (handler == null) {
            Handler h = new Handler();
            h.players = Collections.singletonMap(player, metadata);
            h.enable();
            SignEditDialog.handler = h;
        } else {
            Handler handler2 = handler;
            synchronized (handler2) {
                if (handler.players.containsKey(player)) {
                    return false;
                }
                HashMap<Player, PlayerMetadata> newPlayers = new HashMap<Player, PlayerMetadata>(handler.players);
                newPlayers.put(player, metadata);
                handler.players = newPlayers;
            }
        }
        this.handleOpen(metadata, backDirection, initialLines);
        return true;
    }

    private static Block findAirBlockAbove(Block startBlock) {
        for (int n = 0; n < 10; ++n) {
            if (((Boolean)MaterialUtil.ISAIR.get(startBlock)).booleanValue()) {
                return startBlock;
            }
            startBlock = startBlock.getRelative(BlockFace.UP);
        }
        return null;
    }

    private final void handleOpen(PlayerMetadata metadata, BlockFace backDirection, String[] lines) {
        ServerPlayerHandle entityPlayer = ServerPlayerHandle.fromBukkit(metadata.player);
        IntVector3 coordinates = IntVector3.coordinatesOf(metadata.signBlock);
        metadata.sendBlock(BlockData.fromMaterial(this.signType).setProperty("facing", (Object)backDirection));
        metadata.player.sendSignChange(metadata.signBlock.getLocation(), lines);
        entityPlayer.openSignEditWindow(coordinates);
        this.onOpened(metadata.player, lines);
    }

    private final void handleAbort(PlayerMetadata metadata) {
        ServerPlayerHandle.fromBukkit(metadata.player).closeSignEditWindow();
        metadata.sendOriginalBlock();
        this.onAborted(metadata.player);
    }

    private final void handleClosed(PlayerMetadata metadata, String[] lines) {
        boolean isAllEmpty = true;
        for (String line : lines) {
            if (line.isEmpty()) continue;
            isAllEmpty = false;
            break;
        }
        metadata.sendOriginalBlock();
        if (isAllEmpty) {
            this.onClosedWithoutLines(metadata.player);
        } else {
            this.onClosed(metadata.player, lines);
        }
    }

    public final void abort() {
        SignEditDialog.abort(m -> m.dialog == this);
    }

    public final void abort(Player player) {
        SignEditDialog.abort(m -> m.dialog == this && m.player == player);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void abort(Predicate<PlayerMetadata> filter) {
        Handler handler = SignEditDialog.handler;
        if (handler != null) {
            ArrayList<PlayerMetadata> removed = new ArrayList<PlayerMetadata>();
            Handler handler2 = handler;
            synchronized (handler2) {
                HashMap newPlayers = new HashMap(handler.players);
                Iterator iter = newPlayers.values().iterator();
                while (iter.hasNext()) {
                    PlayerMetadata meta = (PlayerMetadata)iter.next();
                    if (!filter.test(meta)) continue;
                    removed.add(meta);
                    iter.remove();
                }
                handler.players = newPlayers;
            }
            removed.forEach(m -> m.dialog.handleAbort((PlayerMetadata)m));
            handler.disableIfNoPlayers();
        }
    }

    private static class PlayerMetadata {
        public final SignEditDialog dialog;
        public final Player player;
        public final Block signBlock;
        public final IntVector3 coordinates;

        public PlayerMetadata(SignEditDialog dialog, Player player, Block signBlock) {
            this.dialog = dialog;
            this.player = player;
            this.signBlock = signBlock;
            this.coordinates = IntVector3.coordinatesOf(signBlock);
        }

        public void sendOriginalBlock() {
            CommonPacket packet;
            this.sendBlock(WorldUtil.getBlockData(this.signBlock));
            BlockEntityHandle tileEntity = LevelHandle.fromBukkit(this.signBlock.getWorld()).getTileEntity(this.coordinates);
            if (tileEntity != null && (packet = tileEntity.getUpdatePacket()) != null) {
                PacketUtil.sendPacket(this.player, packet);
            }
        }

        public void sendBlock(BlockData blockData) {
            PacketUtil.sendPacket(this.player, ClientboundBlockUpdatePacketHandle.createNew(this.coordinates, blockData));
        }
    }

    private static class Handler
    implements Listener,
    PacketListener {
        private Map<Player, PlayerMetadata> players = Collections.emptyMap();

        private Handler() {
        }

        public void disableIfNoPlayers() {
            if (this.players.isEmpty() && this == handler) {
                if (CommonPlugin.hasInstance()) {
                    this.disable();
                }
                handler = null;
            }
        }

        public void disable() {
            CommonPlugin.getInstance().unregister(this);
            CommonUtil.unregisterListener(this);
        }

        public void enable() {
            CommonPlugin.getInstance().register(this, PacketType.IN_UPDATE_SIGN);
            CommonPlugin.getInstance().register(this);
        }

        public PlayerMetadata getMetadata(Player player) {
            return this.getOrRemoveMetadata(player, false);
        }

        public PlayerMetadata removeMetadata(Player player) {
            return this.getOrRemoveMetadata(player, true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private PlayerMetadata getOrRemoveMetadata(Player player, boolean remove) {
            PlayerMetadata metadata = this.players.get(player);
            if (metadata == null) {
                return null;
            }
            Handler handler = this;
            synchronized (handler) {
                metadata = this.players.get(player);
                if (metadata == null) {
                    return null;
                }
                if (remove) {
                    HashMap<Player, PlayerMetadata> newPlayers = new HashMap<Player, PlayerMetadata>(this.players);
                    newPlayers.remove(player);
                    this.players = newPlayers;
                }
                return metadata;
            }
        }

        @Override
        public void onPacketReceive(PacketReceiveEvent event) {
            if (event.getType() == PacketType.IN_UPDATE_SIGN) {
                PlayerMetadata metadata = this.removeMetadata(event.getPlayer());
                if (metadata == null) {
                    return;
                }
                IntVector3 position = event.getPacket().read(PacketType.IN_UPDATE_SIGN.position);
                if (position == null || !metadata.coordinates.equals(position)) {
                    CommonUtil.nextTick(() -> {
                        metadata.dialog.handleAbort(metadata);
                        this.disableIfNoPlayers();
                    });
                    return;
                }
                event.setCancelled(true);
                ChatText[] lines_chattext = event.getPacket().read(PacketType.IN_UPDATE_SIGN.lines);
                if (lines_chattext == null || lines_chattext.length != 4) {
                    CommonUtil.nextTick(() -> {
                        metadata.dialog.handleAbort(metadata);
                        this.disableIfNoPlayers();
                    });
                    return;
                }
                String[] lines = new String[lines_chattext.length];
                for (int i = 0; i < lines.length; ++i) {
                    ChatText ct = lines_chattext[i];
                    lines[i] = ct == null ? "" : ct.getMessage();
                }
                CommonUtil.nextTick(() -> {
                    metadata.dialog.handleClosed(metadata, lines);
                    this.disableIfNoPlayers();
                });
            }
        }

        @Override
        public void onPacketSend(PacketSendEvent event) {
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            PlayerMetadata metadata = this.removeMetadata(event.getPlayer());
            if (metadata != null) {
                metadata.dialog.onAborted(metadata.player);
                this.disableIfNoPlayers();
            }
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            PlayerMetadata metadata = this.removeMetadata(event.getPlayer());
            if (metadata != null) {
                metadata.dialog.handleAbort(metadata);
                this.disableIfNoPlayers();
            }
        }
    }
}

