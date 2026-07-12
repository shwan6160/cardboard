/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider
 *  com.bergerkiller.bukkit.common.chunk.ForcedChunk
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.GameMode
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.chunk.ChunkFutureProvider;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.CollisionMode;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.defaults.DefaultProperties;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.BankingOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.ChunkLoadOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionMobCategory;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.SignSkipOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.SlowdownMode;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import com.bergerkiller.bukkit.tc.properties.standard.type.WaitOptions;
import com.bergerkiller.bukkit.tc.utils.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TrainProperties
extends TrainPropertiesStore
implements IProperties {
    private static final long serialVersionUID = 1L;
    private final TrainCarts traincarts;
    private final SoftReference<MinecartGroup> group = new SoftReference();
    private final FieldBackedProperty.TrainInternalDataHolder standardProperties = new FieldBackedProperty.TrainInternalDataHolder();
    private final ConfigurationNode config;
    protected String trainname;
    protected boolean removed;

    protected TrainProperties(TrainCarts traincarts, String trainname, ConfigurationNode config) {
        this.traincarts = traincarts;
        this.trainname = trainname;
        this.config = config;
        this.removed = true;
        if (config.isNode("carts")) {
            for (ConfigurationNode cartConfig : config.getNode("carts").getNodes()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(cartConfig.getName());
                }
                catch (IllegalArgumentException ex) {
                    traincarts.getLogger().log(Level.WARNING, "Invalid UUID for cart: " + cartConfig.getName());
                    continue;
                }
                CartProperties cProp = CartPropertiesStore.createNew(this, cartConfig, uuid);
                cProp.group = this;
                super.add(CartPropertiesStore.createNew(this, cartConfig, uuid));
            }
        }
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    @Override
    public String getTypeName() {
        return "train";
    }

    @Override
    public final ConfigurationNode getConfig() {
        return this.config;
    }

    @Override
    public boolean isRemoved() {
        return this.removed;
    }

    @Override
    public final <T> T get(IProperty<T> property) {
        return property.get(this);
    }

    @Override
    public final <T> void set(IProperty<T> property, T value) {
        property.set(this, value);
    }

    public FieldBackedProperty.TrainInternalDataHolder getStandardPropertiesHolder() {
        return this.standardProperties;
    }

    @Override
    public MinecartGroup getHolder() {
        MinecartGroup group = this.group.get();
        if (group == null || group.isRemoved()) {
            return null;
        }
        return group;
    }

    protected void updateHolder(MinecartGroup holder, boolean set) {
        if (set) {
            if (this.group.get() != holder) {
                this.group.set(holder);
                this.onConfigurationChanged(true);
            }
        } else if (this.group.get() == holder) {
            this.group.set(null);
        }
    }

    @Override
    public boolean hasHolder() {
        return this.getHolder() != null;
    }

    @Override
    public CompletableFuture<Boolean> restore() {
        if (this.isLoaded()) {
            return CompletableFuture.completedFuture(true);
        }
        OfflineGroup group = this.getTrainCarts().getOfflineGroups().findGroup(this.trainname);
        if (group == null) {
            return CompletableFuture.completedFuture(false);
        }
        ArrayList<ForcedChunk> chunksOfTrain = new ArrayList<ForcedChunk>();
        World world = group.world.getLoadedWorld();
        if (world != null) {
            group.forAllChunks((cx, cz) -> chunksOfTrain.add(WorldUtil.forceChunkLoaded((World)world, (int)cx, (int)cz)));
        }
        CompletableFuture<Void> whenAllChunkEntitiesLoaded = TrainProperties.loadChunkFutureWithFutureProvider(this.traincarts, chunksOfTrain);
        CompletableFuture<Boolean> result = new CompletableFuture<Boolean>();
        ((CompletableFuture)whenAllChunkEntitiesLoaded.thenAccept(unused -> {
            result.complete(this.hasHolder());
            chunksOfTrain.forEach(ForcedChunk::close);
        })).exceptionally(err -> {
            this.traincarts.getLogger().log(Level.SEVERE, "Failed to load chunks of train", (Throwable)err);
            result.complete(false);
            chunksOfTrain.forEach(ForcedChunk::close);
            return null;
        });
        return result;
    }

    private static CompletableFuture<Void> loadChunkFutureWithFutureProvider(TrainCarts traincarts, List<ForcedChunk> chunks) {
        ChunkFutureProvider provider = ChunkFutureProvider.of((Plugin)traincarts);
        return CompletableFuture.allOf((CompletableFuture[])chunks.stream().map(c -> provider.whenEntitiesLoaded(c.getWorld(), c.getX(), c.getZ())).toArray(CompletableFuture[]::new));
    }

    public double getWaitDistance() {
        return this.get(StandardProperties.WAIT).distance();
    }

    public void setWaitDistance(double waitDistance) {
        this.update(StandardProperties.WAIT, opt -> WaitOptions.create(waitDistance, opt.delay(), opt.acceleration(), opt.deceleration(), opt.predict()));
    }

    public double getWaitDelay() {
        return this.get(StandardProperties.WAIT).delay();
    }

    public void setWaitDelay(double delay) {
        this.update(StandardProperties.WAIT, opt -> WaitOptions.create(opt.distance(), delay, opt.acceleration(), opt.deceleration(), opt.predict()));
    }

    public double getWaitAcceleration() {
        return this.get(StandardProperties.WAIT).acceleration();
    }

    public double getWaitDeceleration() {
        return this.get(StandardProperties.WAIT).deceleration();
    }

    public void setWaitAcceleration(double acceleration) {
        this.setWaitAcceleration(acceleration, acceleration);
    }

    public void setWaitAcceleration(double acceleration, double deceleration) {
        this.update(StandardProperties.WAIT, opt -> WaitOptions.create(opt.distance(), opt.delay(), acceleration, deceleration, opt.predict()));
    }

    public boolean isWaitPredicted() {
        return this.get(StandardProperties.WAIT).predict();
    }

    public void setWaitPredicted(boolean use) {
        this.update(StandardProperties.WAIT, opt -> WaitOptions.create(opt.distance(), opt.delay(), opt.acceleration(), opt.deceleration(), use));
    }

    public double getSpeedLimit() {
        return StandardProperties.SPEEDLIMIT.getDouble(this);
    }

    public void setSpeedLimit(double limit) {
        this.set(StandardProperties.SPEEDLIMIT, limit);
    }

    public double getGravity() {
        return StandardProperties.GRAVITY.getDouble(this);
    }

    public void setGravity(double gravity) {
        this.set(StandardProperties.GRAVITY, gravity);
    }

    public double getFriction() {
        return StandardProperties.FRICTION.getDouble(this);
    }

    public void setFriction(double friction) {
        this.set(StandardProperties.FRICTION, friction);
    }

    @Deprecated
    public boolean isSlowingDown() {
        return !this.get(StandardProperties.SLOWDOWN).isEmpty();
    }

    public boolean isSlowingDownAll() {
        return this.get(StandardProperties.SLOWDOWN).equals(EnumSet.allOf(SlowdownMode.class));
    }

    public boolean isSlowingDownNone() {
        return this.get(StandardProperties.SLOWDOWN).isEmpty();
    }

    public void setSlowingDown(boolean slowingDown) {
        if (slowingDown) {
            this.set(StandardProperties.SLOWDOWN, EnumSet.allOf(SlowdownMode.class));
        } else {
            this.set(StandardProperties.SLOWDOWN, Collections.emptySet());
        }
    }

    public boolean isSlowingDown(SlowdownMode mode) {
        return this.get(StandardProperties.SLOWDOWN).contains((Object)mode);
    }

    public void setSlowingDown(SlowdownMode mode, boolean slowingDown) {
        this.update(StandardProperties.SLOWDOWN, curr_modes -> {
            if (slowingDown == curr_modes.contains((Object)mode)) {
                return curr_modes;
            }
            EnumSet<SlowdownMode> new_modes = EnumSet.noneOf(SlowdownMode.class);
            new_modes.addAll((Collection<SlowdownMode>)curr_modes);
            LogicUtil.addOrRemove(new_modes, (Object)((Object)mode), (boolean)slowingDown);
            return new_modes;
        });
    }

    public String getDisplayName() {
        String name = this.get(StandardProperties.DISPLAY_NAME);
        return name.isEmpty() ? this.getTrainName() : name;
    }

    public String getDisplayNameOrEmpty() {
        return this.get(StandardProperties.DISPLAY_NAME);
    }

    public void setDisplayName(String displayName) {
        this.set(StandardProperties.DISPLAY_NAME, displayName);
    }

    public boolean isKeepingChunksLoaded() {
        return this.get(StandardProperties.CHUNK_LOAD_OPTIONS).keepLoaded();
    }

    public void setKeepChunksLoaded(boolean state) {
        this.setChunkLoadOptions(this.getChunkLoadOptions().withMode(state ? ChunkLoadOptions.Mode.FULL : ChunkLoadOptions.Mode.DISABLED));
    }

    public ChunkLoadOptions getChunkLoadOptions() {
        return this.get(StandardProperties.CHUNK_LOAD_OPTIONS);
    }

    public void setChunkLoadOptions(ChunkLoadOptions options) {
        this.set(StandardProperties.CHUNK_LOAD_OPTIONS, options);
    }

    public boolean isSoundEnabled() {
        return this.get(StandardProperties.SOUND_ENABLED);
    }

    public void setSoundEnabled(boolean enabled) {
        this.set(StandardProperties.SOUND_ENABLED, enabled);
    }

    @Override
    public boolean add(CartProperties properties) {
        if (properties.group != null && properties.group != this) {
            properties.group.remove(properties);
        }
        properties.group = this;
        if (!super.add(properties)) {
            return false;
        }
        this.config.getNode("carts").set(properties.getUUID().toString(), (Object)properties.getConfig());
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof MinecartMember) {
            o = ((MinecartMember)o).getProperties();
        }
        if (!super.remove(o)) {
            return false;
        }
        if (o instanceof CartProperties && this.config.isNode("carts")) {
            this.config.getNode("carts").remove(((CartProperties)o).getUUID().toString());
        }
        return true;
    }

    public CartProperties get(int index) {
        int i = 0;
        for (CartProperties prop : this) {
            if (i++ != index) continue;
            return prop;
        }
        throw new IndexOutOfBoundsException("No cart properties found at index " + index);
    }

    @Override
    public void setPickup(boolean pickup) {
        for (CartProperties prop : this) {
            prop.setPickup(pickup);
        }
    }

    @Override
    public boolean isOwnedByEveryone() {
        return !this.hasOwners() && !this.hasOwnerPermissions();
    }

    @Override
    public boolean hasOwners() {
        for (CartProperties prop : this) {
            if (!prop.hasOwners()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasOwnership(Player player) {
        return CartProperties.hasGlobalOwnership(player) || this.isOwnedByEveryone() || this.isOwner(player);
    }

    @Override
    public boolean isOwner(Player player) {
        for (CartProperties prop : this) {
            if (!prop.isOwner(player)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasOwnerPermissions() {
        for (CartProperties prop : this) {
            if (!prop.hasOwnerPermissions()) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<String> getOwnerPermissions() {
        HashSet<String> rval = new HashSet<String>();
        for (CartProperties cprop : this) {
            rval.addAll(cprop.getOwnerPermissions());
        }
        return rval;
    }

    @Override
    public void setOwnerPermissions(Set<String> newOwnerPermissions) {
        for (CartProperties cprop : this) {
            cprop.setOwnerPermissions(newOwnerPermissions);
        }
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
        for (CartProperties cprop : this) {
            cprop.addOwners(ownersToAdd);
        }
    }

    @Override
    public void removeOwners(Collection<String> ownersToRemove) {
        for (CartProperties cprop : this) {
            cprop.removeOwners(ownersToRemove);
        }
    }

    @Override
    public void clearOwnerPermissions() {
        for (CartProperties prop : this) {
            prop.clearOwnerPermissions();
        }
    }

    public void addOwnerPermission(String permission) {
        for (CartProperties prop : this) {
            prop.addOwnerPermission(permission);
        }
    }

    public void removeOwnerPermission(String permission) {
        for (CartProperties prop : this) {
            prop.removeOwnerPermission(permission);
        }
    }

    public void setOwner(String player, boolean owner) {
        for (CartProperties cProp : this) {
            cProp.setOwner(player, owner);
        }
    }

    public boolean isPlayerTakeable() {
        return this.get(StandardProperties.ALLOW_PLAYER_TAKE);
    }

    public void setPlayerTakeable(boolean takeable) {
        this.set(StandardProperties.ALLOW_PLAYER_TAKE, takeable);
    }

    public double getBankingStrength() {
        return this.get(StandardProperties.BANKING).strength();
    }

    public double getBankingSmoothness() {
        return this.get(StandardProperties.BANKING).smoothness();
    }

    public void setBanking(double strength, double smoothness) {
        this.set(StandardProperties.BANKING, BankingOptions.create(strength, smoothness));
    }

    public void setBankingStrength(double strength) {
        this.update(StandardProperties.BANKING, opt -> BankingOptions.create(strength, opt.smoothness()));
    }

    public void setBankingSmoothness(double smoothness) {
        this.update(StandardProperties.BANKING, opt -> BankingOptions.create(opt.strength(), smoothness));
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
    public void setEnterMessage(String message) {
        for (CartProperties prop : this) {
            prop.setEnterMessage(message);
        }
    }

    @Override
    public boolean matchTag(String tag) {
        for (CartProperties prop : this) {
            if (!prop.matchTag(tag)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTags() {
        for (CartProperties prop : this) {
            if (!prop.hasTags()) continue;
            return true;
        }
        return false;
    }

    @Override
    public Collection<String> getTags() {
        return this.get(StandardProperties.TAGS);
    }

    @Override
    public void setTags(String ... tags) {
        this.set(StandardProperties.TAGS, new HashSet<String>(Arrays.asList(tags)));
    }

    @Override
    public void clearTags() {
        this.set(StandardProperties.TAGS, Collections.emptySet());
    }

    @Override
    public void addTags(String ... tags) {
        for (CartProperties prop : this) {
            prop.addTags(tags);
        }
    }

    @Override
    public void removeTags(String ... tags) {
        for (CartProperties prop : this) {
            prop.removeTags(tags);
        }
    }

    @Override
    public boolean getPlayersEnter() {
        for (CartProperties prop : this) {
            if (!prop.getPlayersEnter()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setPlayersEnter(boolean state) {
        for (CartProperties prop : this) {
            prop.setPlayersEnter(state);
        }
    }

    @Override
    public boolean getPlayersExit() {
        for (CartProperties prop : this) {
            if (!prop.getPlayersExit()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setPlayersExit(boolean state) {
        for (CartProperties prop : this) {
            prop.setPlayersExit(state);
        }
    }

    @Override
    public boolean isInvincible() {
        for (CartProperties prop : this) {
            if (!prop.isInvincible()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setInvincible(boolean enabled) {
        for (CartProperties prop : this) {
            prop.setInvincible(enabled);
        }
    }

    @Override
    public boolean getSpawnItemDrops() {
        for (CartProperties prop : this) {
            if (!prop.getSpawnItemDrops()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setSpawnItemDrops(boolean spawnDrops) {
        for (CartProperties prop : this) {
            prop.setSpawnItemDrops(spawnDrops);
        }
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
        for (CartProperties prop : this) {
            prop.addDestinationToRoute(destination);
        }
    }

    @Override
    public void removeDestinationFromRoute(String destination) {
        for (CartProperties prop : this) {
            prop.removeDestinationFromRoute(destination);
        }
    }

    @Override
    public int getCurrentRouteDestinationIndex() {
        for (CartProperties prop : this) {
            if (prop.getDestinationRoute().isEmpty()) continue;
            return prop.getCurrentRouteDestinationIndex();
        }
        return -1;
    }

    @Override
    public String getNextDestinationOnRoute() {
        for (CartProperties prop : this) {
            if (prop.getDestinationRoute().isEmpty()) continue;
            return prop.getNextDestinationOnRoute();
        }
        return "";
    }

    @Override
    public String getNextDestinationOnRoute(String currentDestination) {
        for (CartProperties prop : this) {
            if (prop.getDestinationRoute().isEmpty()) continue;
            return prop.getNextDestinationOnRoute(currentDestination);
        }
        return "";
    }

    @Override
    public void clearDestination() {
        for (CartProperties prop : this) {
            prop.clearDestination();
        }
    }

    @Override
    public String getLastPathNode() {
        return this.isEmpty() ? "" : this.get(0).getLastPathNode();
    }

    @Override
    public void setLastPathNode(String nodeName) {
        for (CartProperties cprop : this) {
            cprop.setLastPathNode(nodeName);
        }
    }

    public boolean isPoweredMinecartRequired() {
        return this.get(StandardProperties.REQUIRE_POWERED_MINECART);
    }

    public void setPoweredMinecartRequired(boolean required) {
        this.set(StandardProperties.REQUIRE_POWERED_MINECART, required);
    }

    public double getCollisionDamage() {
        return this.get(StandardProperties.COLLISION_DAMAGE);
    }

    public void setCollisionDamage(double collisionDamage) {
        this.set(StandardProperties.COLLISION_DAMAGE, collisionDamage);
    }

    public CollisionMode getCollisionMode(Entity entity) {
        if (entity.isDead()) {
            return CollisionMode.CANCEL;
        }
        MinecartMember<?> member = MinecartMemberStore.getFromEntity(entity);
        CollisionOptions collision = this.getCollision();
        if (member != null) {
            if (collision.trainMode() == CollisionMode.LINK) {
                if (member.getGroup().getProperties().getCollision().trainMode() == CollisionMode.LINK) {
                    return CollisionMode.LINK;
                }
                return CollisionMode.CANCEL;
            }
            return collision.trainMode();
        }
        if (entity instanceof Player) {
            GameMode playerGameMode = ((Player)entity).getGameMode();
            if (playerGameMode == GameMode.SPECTATOR) {
                return CollisionMode.CANCEL;
            }
            if (TCConfig.collisionIgnoreOwners && collision.playerMode() != CollisionMode.DEFAULT) {
                if (TCConfig.collisionIgnoreGlobalOwners && CartProperties.hasGlobalOwnership((Player)entity)) {
                    return CollisionMode.DEFAULT;
                }
                if (this.hasOwnership((Player)entity)) {
                    return CollisionMode.DEFAULT;
                }
            }
            if (playerGameMode == GameMode.CREATIVE && (collision.playerMode() == CollisionMode.KILL || collision.playerMode() == CollisionMode.KILLNODROPS || collision.playerMode() == CollisionMode.DAMAGE || collision.playerMode() == CollisionMode.DAMAGENODROPS)) {
                return CollisionMode.PUSH;
            }
            return collision.playerMode();
        }
        return collision.forEntity(entity);
    }

    public String getTrainName() {
        return this.trainname;
    }

    public void setTrainName(String newTrainName) {
        TrainProperties.rename(this, newTrainName);
    }

    @Deprecated
    public TrainProperties setName(String newtrainname) {
        this.setTrainName(newtrainname);
        return this;
    }

    public boolean hasSuffocation() {
        return this.get(StandardProperties.SUFFOCATION);
    }

    public void setSuffocation(boolean suffocation) {
        this.set(StandardProperties.SUFFOCATION, suffocation);
    }

    public boolean isManualMovementAllowed() {
        return this.get(StandardProperties.ALLOW_PLAYER_MANUAL_MOVEMENT);
    }

    public void setManualMovementAllowed(boolean allow) {
        this.set(StandardProperties.ALLOW_PLAYER_MANUAL_MOVEMENT, allow);
    }

    public boolean isMobManualMovementAllowed() {
        return this.get(StandardProperties.ALLOW_MOB_MANUAL_MOVEMENT);
    }

    public void setMobManualMovementAllowed(boolean allow) {
        this.set(StandardProperties.ALLOW_MOB_MANUAL_MOVEMENT, allow);
    }

    public boolean hasRealtimePhysics() {
        return this.get(StandardProperties.REALTIME_PHYSICS);
    }

    public void setRealtimePhysics(boolean realtime) {
        this.set(StandardProperties.REALTIME_PHYSICS, realtime);
    }

    public Set<String> getTickets() {
        return this.get(StandardProperties.TICKETS);
    }

    public void addTicket(String ticketName) {
        this.update(StandardProperties.TICKETS, tickets -> {
            if (tickets.contains(ticketName)) {
                return tickets;
            }
            HashSet<String> new_tickets = new HashSet<String>((Collection<String>)tickets);
            new_tickets.add(ticketName);
            return new_tickets;
        });
    }

    public void removeTicket(String ticketName) {
        this.update(StandardProperties.TICKETS, tickets -> {
            if (!tickets.contains(ticketName)) {
                return tickets;
            }
            HashSet new_tickets = new HashSet(tickets);
            new_tickets.remove(ticketName);
            return new_tickets;
        });
    }

    public void clearTickets() {
        this.set(StandardProperties.TICKETS, Collections.emptySet());
    }

    public SignSkipOptions getSkipOptions() {
        return this.get(StandardProperties.SIGN_SKIP);
    }

    public void setSkipOptions(SignSkipOptions options) {
        this.set(StandardProperties.SIGN_SKIP, options);
    }

    public String getKillMessage() {
        return this.get(StandardProperties.KILL_MESSAGE);
    }

    public void setKillMessage(String killMessage) {
        this.set(StandardProperties.KILL_MESSAGE, killMessage);
    }

    public boolean isTrainRenamed() {
        return !TrainNameFormat.DEFAULT.matches(this.getTrainName());
    }

    public boolean isLoaded() {
        return this.hasHolder();
    }

    public boolean matchName(String expression) {
        return Util.matchText(this.getTrainName(), expression);
    }

    public boolean matchName(String[] expressionElements, boolean firstAny, boolean lastAny) {
        return Util.matchText(this.getTrainName(), expressionElements, firstAny, lastAny);
    }

    @Override
    public BlockLocation getLocation() {
        Iterator iterator = this.iterator();
        if (iterator.hasNext()) {
            CartProperties prop = (CartProperties)iterator.next();
            return prop.getLocation();
        }
        return null;
    }

    public void setDefault() {
        this.setDefault("default");
    }

    public void setDefault(String key) {
        DefaultProperties defaults = TrainProperties.getDefaultsByName(key);
        if (defaults != null) {
            this.apply(defaults);
        }
    }

    public void setDefault(Player player) {
        if (player == null) {
            this.setDefault();
        } else {
            this.apply(TrainProperties.getDefaultsByPlayer(player));
        }
    }

    @Deprecated
    public void setDefault(ConfigurationNode node) {
        this.apply(node);
    }

    public void tryUpdate() {
        MinecartGroup g = this.getHolder();
        if (g != null) {
            g.onPropertiesChanged();
        }
    }

    @Override
    public boolean parseSet(String key, String arg) {
        return this.parseAndSet(key, arg).getReason() != PropertyParseResult.Reason.PROPERTY_NOT_FOUND;
    }

    public CollisionOptions getCollision() {
        return this.get(StandardProperties.COLLISION);
    }

    public void setCollision(CollisionOptions collisionConfig) {
        this.set(StandardProperties.COLLISION, collisionConfig);
    }

    public void setCollisionMode(CollisionMobCategory mobCategory, CollisionMode mode) {
        this.update(StandardProperties.COLLISION, opt -> opt.cloneAndSetMobMode(mobCategory, mode));
    }

    public boolean setCollisionMode(String key, String value) {
        key = key.toLowerCase(Locale.ENGLISH);
        value = value.toLowerCase(Locale.ENGLISH);
        if (key.startsWith("push") && key.length() > 4) {
            String mobType = key.substring(4);
            CollisionMode mode = ParseUtil.isBool((String)value) ? CollisionMode.fromPushing(ParseUtil.parseBool((String)value)) : CollisionMode.parse(value);
            return this.updateCollisionProperties(mobType, mode);
        }
        if (key.endsWith("collision") && key.length() > 9) {
            String mobType = key.substring(0, key.length() - 9);
            CollisionMode mode = CollisionMode.parse(value);
            return this.updateCollisionProperties(mobType, mode);
        }
        return false;
    }

    public boolean updateCollisionProperties(String mobType, CollisionMode mode) {
        if (mode == null) {
            return false;
        }
        if (mobType.equals("mob") || mobType.equals("mobs")) {
            this.setCollisionModeForMobs(mode);
            return true;
        }
        for (CollisionMobCategory mobCategory : CollisionMobCategory.values()) {
            if (!mobType.equals(mobCategory.getMobType()) && !mobType.equals(mobCategory.getPluralMobType())) continue;
            this.setCollisionMode(mobCategory, mode);
            return true;
        }
        return false;
    }

    public void setCollisionModeForMobs(CollisionMode mode) {
        this.setCollision(this.getCollision().cloneAndSetForAllMobs(mode));
    }

    public void setCollisionModeIfModeForMobs(CollisionMode expected, CollisionMode mode) {
        this.setCollision(this.getCollision().cloneCompareAndSetForAllMobs(expected, mode));
    }

    public void setLinking(boolean linking) {
        this.update(StandardProperties.COLLISION, opt -> {
            if (linking) {
                return opt.cloneAndSetTrainMode(CollisionMode.LINK);
            }
            if (opt.trainMode() == CollisionMode.LINK) {
                return opt.cloneAndSetTrainMode(CollisionMode.DEFAULT);
            }
            return opt;
        });
    }

    public double getIdealTotalTrainLength() {
        if (this.isEmpty()) {
            return 0.0;
        }
        boolean first = true;
        double totalLength = 0.0;
        double previousCartCouplerLength = 0.0;
        for (CartProperties prop : this) {
            AttachmentModel model = prop.getModel();
            if (first) {
                first = false;
            } else {
                totalLength += previousCartCouplerLength + model.getCartCouplerLength();
            }
            previousCartCouplerLength = model.getCartCouplerLength();
            totalLength += (double)model.getCartLength();
        }
        return totalLength;
    }

    public void load(TrainProperties source) {
        this.load(source.getConfig());
    }

    @Override
    public void load(ConfigurationNode node) {
        for (String key : new ArrayList(this.config.getKeys())) {
            if ("carts".equals(key)) continue;
            this.config.remove(key);
        }
        node.cloneIntoExcept(this.config, Collections.singleton("carts"));
        this.onConfigurationChanged(false);
    }

    @Override
    public void save(ConfigurationNode node) {
        this.getConfig().cloneInto(node);
    }

    protected void onConfigurationChanged(boolean cartsChanged) {
        for (IProperty<Object> property : IPropertyRegistry.instance().all()) {
            property.onConfigurationChanged(this);
        }
        if (cartsChanged) {
            for (CartProperties cart : this) {
                cart.onConfigurationChanged();
            }
        }
    }

    @Deprecated
    public ConfigurationNode saveToConfig() {
        for (CartProperties cProp : this) {
            cProp.saveToConfig();
        }
        return this.config;
    }

    public void apply(ConfigurationNode node) {
        if (node != null) {
            DefaultProperties.of(node).applyTo(this);
        }
    }

    public void apply(DefaultProperties defaultProperties) {
        defaultProperties.applyTo(this);
    }
}

