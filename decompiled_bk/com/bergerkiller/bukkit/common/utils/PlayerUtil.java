/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCustomSoundPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelParticlesPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.InventoryHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.SlotHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftPlayer;
import java.util.Collection;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerUtil
extends EntityUtil {
    public static void showEndCredits(Player player) {
        PortalHandler.INSTANCE.showEndCredits(player);
    }

    public static VehicleMountController getVehicleMountController(Player player) {
        return CommonPlugin.getInstance().getVehicleMountManager().get(player);
    }

    public static boolean isDisconnected(Player player) {
        return ServerGamePacketListenerImplHandle.forPlayer(player) == null;
    }

    public static void queueChunkSend(Player player, Chunk chunk) {
        PlayerUtil.queueChunkSend(player, chunk.getX(), chunk.getZ());
    }

    public static List<Player> getNearbyPlayers(Player player, double radius) {
        ServerPlayerHandle handle = CommonNMS.getHandle(player);
        List<?> nearbyPlayerHandles = handle.getWorld().getRawEntitiesOfType(ServerPlayerHandle.T.getType(), handle.getBoundingBox().grow(radius, radius, radius));
        return new ConvertingList<Player>(nearbyPlayerHandles, DuplexConversion.player);
    }

    @Deprecated
    public static void queueChunkSend(Player player, IntVector2 coordinates) {
        PlayerUtil.queueChunkSend(player, coordinates.x, coordinates.z);
    }

    @Deprecated
    public static void queueChunkSend(Player player, int chunkX, int chunkZ) {
        throw new RuntimeException("Queueing a chunk send for individual players is broken. Use WorldUtil.queueChunkSend instead.");
    }

    public static void setFirstPlayed(Player player, long firstPlayed) {
        CBCraftPlayer.setFirstPlayed.invoke(player, firstPlayed);
    }

    public static void setHasPlayedBefore(Player player, boolean playedBefore) {
        CBCraftPlayer.hasPlayedBefore.set(player, playedBefore);
    }

    public static int getPing(Player player) {
        return CommonNMS.getHandle(player).getPing();
    }

    public static GameProfileHandle getGameProfile(Player player) {
        return PlayerHandle.T.gameProfile.get(HandleConversion.toEntityHandle((Entity)player));
    }

    public static boolean isChunkVisible(Player player, Chunk chunk) {
        return player.getWorld() == chunk.getWorld() && PlayerUtil.isChunkVisible(player, chunk.getX(), chunk.getZ());
    }

    public static boolean isChunkVisible(Player player, int chunkX, int chunkZ) {
        ServerPlayerHandle ep = CommonNMS.getHandle(player);
        return ep.getWorldServer().getPlayerChunkMap().isChunkEntered(ep, chunkX, chunkZ);
    }

    @Deprecated
    public static boolean isChunkEntered(Player player, int chunkX, int chunkZ) {
        return PlayerUtil.isChunkVisible(player, chunkX, chunkZ);
    }

    @Deprecated
    public static boolean isChunkEntered(Player player, Chunk chunk) {
        return PlayerUtil.isChunkVisible(player, chunk);
    }

    public static Collection<Integer> getEntityRemoveQueue(Player player) {
        return CommonPlugin.getInstance().getPlayerMeta(player).getRemoveQueue();
    }

    public static ItemStack getItemInHand(Player player, HumanHand hand) {
        return HumanHand.getHeldItem((HumanEntity)player, hand);
    }

    public static int getInventorySlotIndex(int playerInventoryIndex) {
        int index = playerInventoryIndex;
        if (index < InventoryHandle.getHotbarSize()) {
            index += 36;
        } else if (index > 39 && CommonCapabilities.PLAYER_OFF_HAND) {
            index += 5;
        } else if (index > 35) {
            index = 8 - (index - 36);
        }
        return index;
    }

    public static void setItemSilently(Player player, int index, ItemStack item) {
        Object rawContainer = CommonNMS.getHandle(player).getActiveContainer().getRaw();
        List rawOldItems = (List)((Template.Field)AbstractContainerMenuHandle.T.oldItems.raw).get(rawContainer);
        List rawSlots = (List)((Template.Field)AbstractContainerMenuHandle.T.slots.raw).get(rawContainer);
        int convertedIndex = PlayerUtil.getInventorySlotIndex(index);
        if (convertedIndex >= 0 && convertedIndex < rawOldItems.size() && convertedIndex < rawSlots.size()) {
            Object oldItem = ((Template.Method)SlotHandle.T.getItem.raw).invoke(rawSlots.get(convertedIndex));
            if (oldItem != null && oldItem != ItemStackHandle.EMPTY_ITEM.getRaw()) {
                ItemStackHandle.T.copy(HandleConversion.toItemStackHandle(item), oldItem);
            } else {
                player.getInventory().setItem(index, item);
            }
        }
        PlayerUtil.markItemUnchanged(player, index);
    }

    public static void markItemUnchanged(Player player, int index) {
        Object rawContainer = CommonNMS.getHandle(player).getActiveContainer().getRaw();
        List rawOldItems = (List)((Template.Field)AbstractContainerMenuHandle.T.oldItems.raw).get(rawContainer);
        List rawSlots = (List)((Template.Field)AbstractContainerMenuHandle.T.slots.raw).get(rawContainer);
        if ((index = PlayerUtil.getInventorySlotIndex(index)) >= 0 && index < rawOldItems.size() && index < rawSlots.size()) {
            Object oldItem = ((Template.Method)SlotHandle.T.getItem.raw).invoke(rawSlots.get(index));
            oldItem = CommonNMS.isItemEmpty(oldItem) ? (CommonCapabilities.ITEMSTACK_EMPTY_STATE ? ItemStackHandle.EMPTY_ITEM.getRaw() : null) : ((Template.Method)ItemStackHandle.T.cloneItemStack.raw).invoke(oldItem);
            rawOldItems.set(index, oldItem);
        }
    }

    public static void spawnDustParticles(Player player, Vector position, Color color) {
        ClientboundLevelParticlesPacketHandle packet = ClientboundLevelParticlesPacketHandle.createNew();
        packet.setParticle(ParticleType.DUST, ParticleType.DustOptions.create(color, 1.0f));
        packet.setPos(position);
        PacketUtil.sendPacket(player, packet);
    }

    public static void playSound(Player player, ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        PlayerUtil.playSound(player, player.getEyeLocation(), soundKey, "master", volume, pitch);
    }

    public static void playSound(Player player, ResourceKey<SoundEffect> soundKey, String category, float volume, float pitch) {
        PlayerUtil.playSound(player, player.getEyeLocation(), soundKey, category, volume, pitch);
    }

    public static void playSound(Player player, Location location, ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        PlayerUtil.playSound(player, location, soundKey, "master", volume, pitch);
    }

    public static void playSound(Player player, Location location, ResourceKey<SoundEffect> soundKey, String category, float volume, float pitch) {
        if (soundKey != null) {
            PacketUtil.sendPacket(player, ClientboundCustomSoundPacketHandle.createNew(soundKey, category, location, volume, pitch));
        }
    }

    public static DimensionType getPlayerDimension(Player player) {
        if (CBCraftPlayer.T.isAssignableFrom(player.getClass())) {
            return WorldUtil.getDimensionType(player.getWorld());
        }
        return null;
    }

    public static void sendMessage(Player player, ChatText text) {
        ServerPlayerHandle.fromBukkit(player).sendMessage(text);
    }

    public static String getGameVersion(Player player) {
        return PlayerGameInfo.of(player).version();
    }

    public static boolean evaluateGameVersion(Player player, String operand, String rightSide) {
        return PlayerGameInfo.of(player).evaluateVersion(operand, rightSide);
    }
}

