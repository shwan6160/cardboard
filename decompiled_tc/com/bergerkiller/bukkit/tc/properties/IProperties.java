/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.properties.IParsable;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyInputContext;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.bukkit.entity.Player;

public interface IProperties
extends IParsable,
TrainCarts.Provider {
    public boolean isRemoved();

    public <T> T get(IProperty<T> var1);

    public <T> void set(IProperty<T> var1, T var2);

    default public <T> T update(IProperty<T> property, Function<T, T> operation) {
        T new_value;
        T old_value = this.get(property);
        if (old_value != (new_value = operation.apply(old_value))) {
            this.set(property, new_value);
        }
        return new_value;
    }

    @Override
    default public PropertyParseResult<?> parseAndSet(String name, String input) {
        return IPropertyRegistry.instance().parseAndSet(this, name, input);
    }

    default public PropertyParseResult<?> parseAndSet(String name, PropertyInputContext inputContext) {
        return IPropertyRegistry.instance().parseAndSet(this, name, inputContext);
    }

    public ConfigurationNode getConfig();

    public void load(ConfigurationNode var1);

    public void save(ConfigurationNode var1);

    public String getTypeName();

    public boolean matchTag(String var1);

    public boolean hasTags();

    public void clearTags();

    public void removeTags(String ... var1);

    public void addTags(String ... var1);

    public boolean hasOwnership(Player var1);

    public boolean hasOwners();

    public Set<String> getOwners();

    public void setOwners(Set<String> var1);

    public void addOwners(Collection<String> var1);

    public void removeOwners(Collection<String> var1);

    public boolean hasOwnerPermissions();

    public Set<String> getOwnerPermissions();

    public void setOwnerPermissions(Set<String> var1);

    public void clearOwners();

    public void clearOwnerPermissions();

    public boolean isOwnedByEveryone();

    public Collection<String> getTags();

    public void setTags(String ... var1);

    public boolean isOwner(Player var1);

    public void setPickup(boolean var1);

    public boolean getCanOnlyOwnersEnter();

    public void setCanOnlyOwnersEnter(boolean var1);

    public boolean getPlayersEnter();

    public void setPlayersEnter(boolean var1);

    public boolean getPlayersExit();

    public void setPlayersExit(boolean var1);

    public boolean isInvincible();

    public void setInvincible(boolean var1);

    public boolean getSpawnItemDrops();

    public void setSpawnItemDrops(boolean var1);

    public void clearDestination();

    public boolean hasDestination();

    public String getLastPathNode();

    public void setLastPathNode(String var1);

    public String getDestination();

    public void setDestination(String var1);

    public List<String> getDestinationRoute();

    public void setDestinationRoute(List<String> var1);

    public void clearDestinationRoute();

    public void addDestinationToRoute(String var1);

    public void removeDestinationFromRoute(String var1);

    public int getCurrentRouteDestinationIndex();

    default public String getNextDestinationOnRoute() {
        return this.getNextDestinationOnRoute(this.getDestination());
    }

    public String getNextDestinationOnRoute(String var1);

    public void setEnterMessage(String var1);

    public BlockLocation getLocation();

    public IPropertiesHolder getHolder();

    public CompletableFuture<Boolean> restore();

    public boolean hasHolder();
}

