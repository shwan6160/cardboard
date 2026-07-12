/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.entity.PlayerInstancePhase;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapClip;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapSessionMode;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class MapSession {
    public final HashSet<UUID> owners = new HashSet();
    public final ArrayList<Owner> onlineOwners = new ArrayList();
    public final MapDisplay display;
    public boolean hasHolders = false;
    public boolean hasViewers = false;
    public boolean hasNewViewers = false;
    public boolean refreshResolutionRequested = false;
    public MapSessionMode mode = MapSessionMode.ONLINE;
    public List<MapDisplayTile> tiles = new ArrayList<MapDisplayTile>();

    public MapSession(MapDisplay display) {
        this.display = display;
    }

    public boolean update() {
        MapDisplayInfo info = this.display.getMapInfo();
        if (info == null) {
            return false;
        }
        this.hasHolders = false;
        this.hasViewers = false;
        this.hasNewViewers = false;
        if (!this.onlineOwners.isEmpty()) {
            Iterator<Owner> onlineIter = this.onlineOwners.iterator();
            while (onlineIter.hasNext()) {
                Owner owner = onlineIter.next();
                owner.input.handleDisplayUpdate(this.display, owner.interceptInput);
                PlayerInstancePhase playerPhase = PlayerInstancePhase.of(owner.player);
                if (!playerPhase.isConnected()) {
                    if (this.mode != MapSessionMode.FOREVER) {
                        this.owners.remove(owner.player.getUniqueId());
                    }
                    this.onOwnerRemoved(owner);
                    onlineIter.remove();
                    continue;
                }
                if (playerPhase.isAliveOnWorld()) {
                    owner.controlling = info.isMap(HumanHand.getItemInMainHand((HumanEntity)owner.player));
                    boolean bl = owner.holding = owner.controlling || info.isMap(HumanHand.getItemInOffHand((HumanEntity)owner.player));
                    if (!owner.holding && this.mode == MapSessionMode.HOLDING) {
                        this.owners.remove(owner.player.getUniqueId());
                        this.onOwnerRemoved(owner);
                        onlineIter.remove();
                        continue;
                    }
                } else {
                    owner.controlling = false;
                    owner.holding = false;
                }
                owner.wasViewing = owner.viewing;
                boolean bl = owner.viewing = owner.holding || info.getViewers().contains(owner.player);
                if (!owner.viewing && this.mode == MapSessionMode.VIEWING) {
                    this.owners.remove(owner.player.getUniqueId());
                    this.onOwnerRemoved(owner);
                    onlineIter.remove();
                    continue;
                }
                this.hasHolders |= owner.holding;
                this.hasViewers |= owner.viewing;
                this.hasNewViewers |= owner.isNewViewer();
            }
        }
        switch (this.mode) {
            case FOREVER: {
                return true;
            }
            case ONLINE: {
                return !this.onlineOwners.isEmpty();
            }
            case VIEWING: {
                return this.hasViewers;
            }
            case HOLDING: {
                return this.hasHolders;
            }
        }
        return true;
    }

    public void addOwner(Player ownerPlayer) {
        if (this.owners.add(ownerPlayer.getUniqueId())) {
            Owner owner = new Owner(ownerPlayer, this.display);
            this.onlineOwners.add(owner);
            this.onOwnerAdded(owner);
        }
    }

    public boolean removeOwner(Player ownerPlayer) {
        if (this.owners.remove(ownerPlayer.getUniqueId())) {
            Iterator<Owner> iter = this.onlineOwners.iterator();
            while (iter.hasNext()) {
                Owner owner = iter.next();
                if (owner.player != ownerPlayer) continue;
                this.onOwnerRemoved(owner);
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public void initOwners() {
        for (Owner owner : this.onlineOwners) {
            this.onOwnerAdded(owner);
        }
    }

    private void onOwnerAdded(Owner owner) {
        MapDisplayInfo info = this.display.getMapInfo();
        if (info != null) {
            MapDisplayInfo.ViewStack views = info.getOrCreateViewStack(owner.player);
            if (views.stack.isEmpty()) {
                views.stack.addLast(this.display);
            } else {
                MapDisplay previousDisplay = views.stack.getLast();
                if (previousDisplay != this.display) {
                    previousDisplay.removeOwner(owner.player);
                    views.stack.addLast(previousDisplay);
                    views.stack.addLast(this.display);
                }
            }
        }
    }

    private void onOwnerRemoved(Owner owner) {
        MapDisplayInfo info = this.display.getMapInfo();
        if (info != null) {
            MapDisplayInfo.ViewStack views = info.getOrCreateViewStack(owner.player);
            if (!views.stack.isEmpty() && views.stack.getLast() == this.display) {
                owner.interceptInput = false;
                owner.input.handleDisplayUpdate(this.display, false);
                views.stack.removeLast();
                if (!views.stack.isEmpty()) {
                    views.stack.getLast().addOwner(owner.player);
                }
            } else {
                views.stack.remove(this.display);
            }
        }
    }

    public void updatePlayerOnline(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (this.mode == MapSessionMode.FOREVER && this.owners.contains(playerUUID)) {
            boolean ownerFound = false;
            for (Owner owner : this.onlineOwners) {
                if (!owner.playerUUID.equals(playerUUID)) continue;
                owner.wasViewing = false;
                owner.player = player;
                owner.clip.markEverythingDirty();
                ownerFound = true;
                break;
            }
            if (!ownerFound) {
                Owner owner = new Owner(player, this.display);
                this.onlineOwners.add(owner);
                this.onOwnerAdded(owner);
            }
        }
    }

    public static class Owner {
        public final UUID playerUUID;
        public final MapDisplay display;
        public final MapClip clip = new MapClip();
        public final MapPlayerInput input;
        public Player player;
        public boolean interceptInput = false;
        public boolean wasViewing;
        public boolean viewing;
        public boolean holding;
        public boolean controlling;

        public Owner(Player player, MapDisplay display) {
            this.playerUUID = player.getUniqueId();
            this.display = display;
            this.controlling = false;
            this.viewing = false;
            this.holding = false;
            this.wasViewing = false;
            this.player = player;
            this.input = CommonPlugin.getInstance().getMapController().getPlayerInput(this.player);
            this.clip.markEverythingDirty();
        }

        public boolean isNewViewer() {
            return this.viewing && !this.wasViewing;
        }

        public void updateMap(List<MapDisplayTile.Update> updates) {
            this.clip.clearDirty();
            for (MapDisplayTile.Update mapUpdate : updates) {
                this.sendUpdate(mapUpdate);
            }
        }

        public void sendDirtyTile(MapDisplayTile tile) {
            int x = tile.tileX << 7;
            int y = tile.tileY << 7;
            int w = 128;
            int h = 128;
            if (!this.viewing) {
                this.clip.markDirty(x, y, w, h);
                return;
            }
            MapClip clip = this.clip.getArea(x, y, w, h);
            if (!clip.isDirty()) {
                MapDisplayTile.Update update = tile.getTileUpdate(this.display, this.player, null);
                if (update != null) {
                    this.sendUpdate(update);
                }
            } else if (!clip.isEverythingDirty()) {
                this.clip.markDirty(x, y, w, h);
            }
        }

        private void sendUpdate(MapDisplayTile.Update mapUpdate) {
            PacketUtil.sendPacket(this.player, mapUpdate.packet.create(), false);
            this.display.getMarkerManager().setMarkersSynchronized(this.player, mapUpdate);
        }
    }
}

