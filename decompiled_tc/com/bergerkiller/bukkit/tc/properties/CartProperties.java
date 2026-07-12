/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.ExitOffset;
import com.bergerkiller.bukkit.tc.properties.standard.type.SignSkipOptions;
import com.bergerkiller.bukkit.tc.utils.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CartProperties
extends CartPropertiesStore
implements IProperties {
    private final TrainCarts traincarts;
    private SoftReference<MinecartMember<?>> member = new SoftReference();
    protected TrainProperties group = null;
    private final FieldBackedProperty.CartInternalDataHolder standardProperties = new FieldBackedProperty.CartInternalDataHolder();
    private ConfigurationNode config;
    private final UUID uuid;
    protected boolean removed;

    protected CartProperties(TrainCarts traincarts, TrainProperties group, ConfigurationNode config, UUID uuid) {
        this.traincarts = traincarts;
        this.uuid = uuid;
        this.group = group;
        this.config = config;
        this.removed = false;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    @Override
    public boolean isRemoved() {
        return this.removed;
    }

    protected void reassign(TrainProperties group, ConfigurationNode config) {
        if (this.group != null && this.group != group) {
            this.group.remove(this);
        }
        this.group = group;
        this.config = config;
    }

    public static boolean hasGlobalOwnership(Player player) {
        return Permission.COMMAND_GLOBALPROPERTIES.has((CommandSender)player);
    }

    public TrainProperties getTrainProperties() {
        return this.group;
    }

    @Override
    public String getTypeName() {
        return "cart";
    }

    @Override
    public final ConfigurationNode getConfig() {
        return this.config;
    }

    @Override
    public final <T> T get(IProperty<T> property) {
        return property.get(this);
    }

    @Override
    public final <T> void set(IProperty<T> property, T value) {
        property.set(this, value);
    }

    public FieldBackedProperty.CartInternalDataHolder getStandardPropertiesHolder() {
        return this.standardProperties;
    }

    protected void setHolder(MinecartMember<?> holder) {
        this.member.set(holder);
    }

    @Override
    public MinecartMember<?> getHolder() {
        MinecartMember<?> member = this.member.get();
        if (member == null || member.getEntity() == null || !((CommonMinecart)member.getEntity()).getUniqueId().equals(this.uuid)) {
            return this.member.set(MinecartMemberStore.getFromUID(this.uuid));
        }
        return member;
    }

    @Override
    public boolean hasHolder() {
        return this.getHolder() != null;
    }

    @Override
    public CompletableFuture<Boolean> restore() {
        return this.getTrainProperties().restore();
    }

    public MinecartGroup getGroup() {
        IPropertiesHolder member = this.getHolder();
        if (member == null) {
            return this.group == null ? null : this.group.getHolder();
        }
        return ((MinecartMember)member).getGroup();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void tryUpdate() {
        IPropertiesHolder m = this.getHolder();
        if (m != null) {
            ((MinecartMember)m).onPropertiesChanged();
        }
    }

    public Collection<UUID> getEditing() {
        List<TrainCartsPlayer> players = this.traincarts.getPlayerStore().find(p -> p.getEditedCart() == this);
        return players.stream().map(TrainCartsPlayer::getUniqueId).collect(Collectors.toList());
    }

    public Collection<Player> getEditingPlayers() {
        Collection<UUID> uuids = this.getEditing();
        ArrayList<Player> players = new ArrayList<Player>(uuids.size());
        for (UUID uuid : uuids) {
            Player p = Bukkit.getServer().getPlayer(uuid);
            if (p == null) continue;
            players.add(p);
        }
        return players;
    }

    public boolean canBreak(Block block) {
        Set<Material> types = this.get(StandardProperties.BLOCK_BREAK_TYPES);
        return !types.isEmpty() && types.contains(block.getType());
    }

    @Override
    public boolean hasOwnership(Player player) {
        if (CartProperties.hasGlobalOwnership(player) || this.isOwnedByEveryone() || this.isOwner(player)) {
            return true;
        }
        for (String ownerPermission : this.getOwnerPermissions()) {
            if (!CommonUtil.hasPermission((CommandSender)player, (String)ownerPermission)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isOwner(Player player) {
        return this.isOwner(player.getName());
    }

    public boolean isOwner(String player) {
        return this.get(StandardProperties.OWNERS).contains(player.toLowerCase());
    }

    public void setOwner(String player) {
        this.setOwner(player, true);
    }

    public void setOwner(String player, boolean owner) {
        this.update(StandardProperties.OWNERS, curr_owners -> {
            String player_lc = player.toLowerCase();
            if (curr_owners.contains(player_lc) == owner) {
                return curr_owners;
            }
            HashSet new_owners = new HashSet(curr_owners);
            LogicUtil.addOrRemove(new_owners, (Object)player_lc, (boolean)owner);
            return new_owners;
        });
    }

    public void setOwner(Player player) {
        this.setOwner(player, true);
    }

    public void setOwner(Player player, boolean owner) {
        if (player == null) {
            return;
        }
        this.setOwner(player.getName(), owner);
    }

    @Override
    public boolean isOwnedByEveryone() {
        return !this.hasOwners() && !this.hasOwnerPermissions();
    }

    @Override
    public Set<String> getOwnerPermissions() {
        return this.get(StandardProperties.OWNER_PERMISSIONS);
    }

    @Override
    public void setOwnerPermissions(Set<String> newOwnerPermissions) {
        this.set(StandardProperties.OWNER_PERMISSIONS, newOwnerPermissions);
    }

    public void addOwnerPermission(String permission) {
        this.update(StandardProperties.OWNER_PERMISSIONS, curr_perms -> {
            if (curr_perms.contains(permission)) {
                return curr_perms;
            }
            HashSet<String> new_perms = new HashSet<String>((Collection<String>)curr_perms);
            new_perms.add(permission);
            return new_perms;
        });
    }

    public void removeOwnerPermission(String permission) {
        this.update(StandardProperties.OWNER_PERMISSIONS, curr_perms -> {
            if (!curr_perms.contains(permission)) {
                return curr_perms;
            }
            HashSet new_perms = new HashSet(curr_perms);
            new_perms.remove(permission);
            return new_perms;
        });
    }

    @Override
    public void clearOwnerPermissions() {
        this.set(StandardProperties.OWNER_PERMISSIONS, Collections.emptySet());
    }

    @Override
    public boolean hasOwnerPermissions() {
        return !this.get(StandardProperties.OWNER_PERMISSIONS).isEmpty();
    }

    @Override
    public Set<String> getOwners() {
        return this.get(StandardProperties.OWNERS);
    }

    @Override
    public void setOwners(Set<String> newOwners) {
        this.set(StandardProperties.OWNERS, newOwners);
    }

    @Override
    public void clearOwners() {
        this.set(StandardProperties.OWNERS, Collections.emptySet());
    }

    @Override
    public void addOwners(Collection<String> ownersToAdd) {
        this.update(StandardProperties.OWNERS, curr_owners -> {
            HashSet newOwners = new HashSet(curr_owners);
            newOwners.addAll(curr_owners);
            return newOwners;
        });
    }

    @Override
    public void removeOwners(Collection<String> ownersToRemove) {
        this.update(StandardProperties.OWNERS, curr_owners -> {
            HashSet newOwners = new HashSet(curr_owners);
            newOwners.removeAll((Collection<?>)curr_owners);
            return newOwners;
        });
    }

    @Override
    public boolean hasOwners() {
        return !this.get(StandardProperties.OWNERS).isEmpty();
    }

    public ExitOffset getExitOffset() {
        return this.get(StandardProperties.EXIT_OFFSET);
    }

    public void setExitOffset(ExitOffset new_offset) {
        this.set(StandardProperties.EXIT_OFFSET, new_offset);
    }

    public boolean canPickup() {
        return this.get(StandardProperties.PICK_UP_ITEMS);
    }

    @Override
    public void setPickup(boolean pickup) {
        this.set(StandardProperties.PICK_UP_ITEMS, pickup);
    }

    @Override
    public boolean getCanOnlyOwnersEnter() {
        return this.get(StandardProperties.ONLY_OWNERS_CAN_ENTER);
    }

    @Override
    public void setCanOnlyOwnersEnter(boolean state) {
        this.set(StandardProperties.ONLY_OWNERS_CAN_ENTER, state);
    }

    @Override
    public boolean matchTag(String tag) {
        return Util.matchText(this.getTags(), tag);
    }

    @Override
    public boolean hasTags() {
        return !this.getTags().isEmpty();
    }

    @Override
    public void clearTags() {
        this.set(StandardProperties.TAGS, Collections.emptySet());
    }

    @Override
    public void addTags(String ... tags) {
        this.update(StandardProperties.TAGS, curr_tags -> {
            HashSet<String> new_tags = new HashSet<String>((Collection<String>)curr_tags);
            new_tags.addAll(Arrays.asList(tags));
            return new_tags;
        });
    }

    @Override
    public void removeTags(String ... tags) {
        this.update(StandardProperties.TAGS, curr_tags -> {
            HashSet new_tags = new HashSet(curr_tags);
            new_tags.removeAll(Arrays.asList(tags));
            return new_tags;
        });
    }

    public Set<String> getTags() {
        return this.get(StandardProperties.TAGS);
    }

    @Override
    public void setTags(String ... tags) {
        this.set(StandardProperties.TAGS, new HashSet<String>(Arrays.asList(tags)));
    }

    @Override
    public boolean getSpawnItemDrops() {
        return this.get(StandardProperties.SPAWN_ITEM_DROPS);
    }

    @Override
    public void setSpawnItemDrops(boolean spawnDrops) {
        this.set(StandardProperties.SPAWN_ITEM_DROPS, spawnDrops);
    }

    @Override
    public BlockLocation getLocation() {
        IPropertiesHolder member = this.getHolder();
        if (member != null) {
            return new BlockLocation(((CommonMinecart)member.getEntity()).getLocation().getBlock());
        }
        OfflineMember omember = this.getTrainCarts().getOfflineGroups().findMember(this.getTrainProperties().getTrainName(), this.getUUID());
        if (omember == null) {
            return null;
        }
        World world = omember.group.world.getLoadedWorld();
        if (world == null) {
            return new BlockLocation("Unknown", omember.cx << 4, 0, omember.cz << 4);
        }
        return new BlockLocation(world, omember.cx << 4, 0, omember.cz << 4);
    }

    public boolean hasBlockBreakTypes() {
        return !this.get(StandardProperties.BLOCK_BREAK_TYPES).isEmpty();
    }

    public void clearBlockBreakTypes() {
        this.set(StandardProperties.BLOCK_BREAK_TYPES, Collections.emptySet());
    }

    public Collection<Material> getBlockBreakTypes() {
        return this.get(StandardProperties.BLOCK_BREAK_TYPES);
    }

    public String getEnterMessage() {
        return this.get(StandardProperties.ENTER_MESSAGE);
    }

    @Override
    public void setEnterMessage(String message) {
        this.set(StandardProperties.ENTER_MESSAGE, message);
    }

    public boolean hasEnterMessage() {
        return !this.get(StandardProperties.ENTER_MESSAGE).isEmpty();
    }

    public void showEnterMessage(Player player) {
        String message = this.getEnterMessage();
        if (!message.isEmpty()) {
            TrainCarts.sendMessage(player, ChatColor.YELLOW + TrainCarts.getMessage(message));
        }
    }

    @Override
    public void clearDestination() {
        this.set(StandardProperties.DESTINATION, "");
    }

    @Override
    public boolean hasDestination() {
        return !this.get(StandardProperties.DESTINATION).isEmpty();
    }

    @Override
    public String getDestination() {
        return this.get(StandardProperties.DESTINATION);
    }

    @Override
    public void setDestination(String destination) {
        this.set(StandardProperties.DESTINATION, destination);
    }

    @Override
    public List<String> getDestinationRoute() {
        return this.get(StandardProperties.DESTINATION_ROUTE);
    }

    @Override
    public void setDestinationRoute(List<String> route) {
        this.set(StandardProperties.DESTINATION_ROUTE, route);
    }

    @Override
    public void clearDestinationRoute() {
        this.set(StandardProperties.DESTINATION_ROUTE, Collections.emptyList());
    }

    @Override
    public void addDestinationToRoute(String destination) {
        if (destination != null && !destination.isEmpty()) {
            this.update(StandardProperties.DESTINATION_ROUTE, curr_route -> {
                ArrayList<String> new_route = new ArrayList<String>((Collection<String>)curr_route);
                new_route.add(destination);
                return new_route;
            });
        }
    }

    @Override
    public void removeDestinationFromRoute(String destination) {
        if (destination != null && !destination.isEmpty()) {
            this.update(StandardProperties.DESTINATION_ROUTE, curr_route -> {
                ArrayList new_route = new ArrayList(curr_route);
                while (new_route.remove(destination)) {
                }
                return new_route;
            });
        }
    }

    @Override
    public int getCurrentRouteDestinationIndex() {
        List<String> destinationRoute = this.getDestinationRoute();
        String destination = this.getDestination();
        if (destinationRoute.isEmpty() || destination.isEmpty()) {
            return -1;
        }
        int destinationRouteIndex = this.get(StandardProperties.DESTINATION_ROUTE_INDEX);
        if (destinationRouteIndex < 0 || destinationRouteIndex >= destinationRoute.size()) {
            return destinationRoute.indexOf(destination);
        }
        if (destination.equals(destinationRoute.get(destinationRouteIndex))) {
            return destinationRouteIndex;
        }
        return destinationRoute.indexOf(destination);
    }

    @Override
    public String getNextDestinationOnRoute(String currentDestination) {
        int index;
        List<String> destinationRoute = this.getDestinationRoute();
        if (destinationRoute.isEmpty()) {
            return "";
        }
        int destinationRouteIndex = this.get(StandardProperties.DESTINATION_ROUTE_INDEX);
        if (destinationRouteIndex < 0 || destinationRouteIndex >= destinationRoute.size()) {
            this.set(StandardProperties.DESTINATION_ROUTE_INDEX, 0);
            destinationRouteIndex = 0;
        }
        if (currentDestination == null || currentDestination.isEmpty()) {
            return destinationRoute.get(destinationRouteIndex);
        }
        if (currentDestination.equals(destinationRoute.get(destinationRouteIndex))) {
            index = destinationRouteIndex;
        } else {
            index = destinationRoute.indexOf(currentDestination);
            if (index == -1) {
                return "";
            }
        }
        return destinationRoute.get((index + 1) % destinationRoute.size());
    }

    @Override
    public String getLastPathNode() {
        return this.get(StandardProperties.DESTINATION_LAST_PATH_NODE);
    }

    @Override
    public void setLastPathNode(String nodeName) {
        this.set(StandardProperties.DESTINATION_LAST_PATH_NODE, nodeName);
    }

    public AttachmentModel getModel() {
        return this.get(StandardProperties.MODEL);
    }

    public void resetModel() {
        this.set(StandardProperties.MODEL, null);
    }

    @Override
    public boolean parseSet(String key, String arg) {
        return this.parseAndSet(key, arg).getReason() != PropertyParseResult.Reason.PROPERTY_NOT_FOUND;
    }

    public void load(CartProperties source) {
        this.load(source.getConfig());
    }

    @Override
    public void load(ConfigurationNode node) {
        this.config.clear();
        node.cloneInto(this.config);
        this.onConfigurationChanged();
    }

    @Override
    public void save(ConfigurationNode node) {
        this.getConfig().cloneInto(node);
    }

    protected void onConfigurationChanged() {
        for (IProperty<Object> property : IPropertyRegistry.instance().all()) {
            property.onConfigurationChanged(this);
        }
    }

    @Deprecated
    public ConfigurationNode saveToConfig() {
        return this.config;
    }

    @Override
    public boolean isInvincible() {
        return this.get(StandardProperties.INVINCIBLE);
    }

    @Override
    public void setInvincible(boolean invincible) {
        this.set(StandardProperties.INVINCIBLE, invincible);
    }

    @Override
    public boolean getPlayersEnter() {
        return this.get(StandardProperties.ALLOW_PLAYER_ENTER);
    }

    @Override
    public void setPlayersEnter(boolean state) {
        this.set(StandardProperties.ALLOW_PLAYER_ENTER, state);
    }

    @Override
    public boolean getPlayersExit() {
        return this.get(StandardProperties.ALLOW_PLAYER_EXIT);
    }

    @Override
    public void setPlayersExit(boolean state) {
        this.set(StandardProperties.ALLOW_PLAYER_EXIT, state);
    }

    public SignSkipOptions getSkipOptions() {
        return this.get(StandardProperties.SIGN_SKIP);
    }

    public void setSkipOptions(SignSkipOptions options) {
        Set<BlockLocation> old_skipped_signs = this.get(StandardProperties.SIGN_SKIP).skippedSigns();
        if (old_skipped_signs.equals(options.skippedSigns())) {
            this.set(StandardProperties.SIGN_SKIP, options);
        } else {
            this.set(StandardProperties.SIGN_SKIP, SignSkipOptions.create(options.ignoreCounter(), options.skipCounter(), options.filter(), old_skipped_signs));
        }
    }

    public String getDriveSound() {
        return this.get(StandardProperties.DRIVE_SOUND);
    }

    public void setDriveSound(String driveSound) {
        this.set(StandardProperties.DRIVE_SOUND, driveSound);
    }
}

