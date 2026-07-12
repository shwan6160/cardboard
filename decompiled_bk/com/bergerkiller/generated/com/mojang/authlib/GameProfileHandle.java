/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Multimap
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 */
package com.bergerkiller.generated.com.mojang.authlib;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.com.mojang.authlib.properties.PropertyHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

@Template.InstanceType(value="com.mojang.authlib.GameProfile")
public abstract class GameProfileHandle
extends Template.Handle {
    public static final GameProfileClass T = Template.Class.create(GameProfileClass.class, Common.TEMPLATE_RESOLVER);

    public static GameProfileHandle createHandle(Object handleInstance) {
        return (GameProfileHandle)T.createHandle(handleInstance);
    }

    public static GameProfileHandle createNew(UUID uuid, String name, Multimap<String, PropertyHandle> properties) {
        return GameProfileHandle.T.createNew.invoke(uuid, name, properties);
    }

    public abstract UUID getId();

    public abstract String getName();

    public abstract Set<String> getPropertyKeys();

    public abstract Collection<PropertyHandle> getProperties(String var1);

    public abstract Multimap<String, PropertyHandle> getMutableProperties();

    public static GameProfileHandle createNew(UUID uuid, String name) {
        return GameProfileHandle.createNew(uuid, name, (Multimap<String, PropertyHandle>)ImmutableMultimap.of());
    }

    public GameProfileHandle withProperties(Multimap<String, PropertyHandle> properties) {
        return GameProfileHandle.createNew(this.getId(), this.getName(), properties);
    }

    public GameProfileHandle withPropertiesOf(GameProfileHandle profile) {
        return this.withProperties(profile.getMutableProperties());
    }

    public GameProfileHandle withPropertiesChanged(Consumer<Multimap<String, PropertyHandle>> mutator) {
        Multimap<String, PropertyHandle> properties = this.getMutableProperties();
        mutator.accept(properties);
        return this.withProperties(properties);
    }

    public GameProfileHandle withPropertyPut(String key, PropertyHandle property) {
        return this.withPropertiesChanged(p -> p.put((Object)key, (Object)property));
    }

    public static GameProfileHandle getForPlayer(HumanEntity player) {
        Object handle = HandleConversion.toEntityHandle((Entity)player);
        return PlayerHandle.T.gameProfile.get(handle);
    }

    public static final class GameProfileClass
    extends Template.Class<GameProfileHandle> {
        public final Template.StaticMethod.Converted<GameProfileHandle> createNew = new Template.StaticMethod.Converted();
        public final Template.Method<UUID> getId = new Template.Method();
        public final Template.Method<String> getName = new Template.Method();
        public final Template.Method<Set<String>> getPropertyKeys = new Template.Method();
        public final Template.Method.Converted<Collection<PropertyHandle>> getProperties = new Template.Method.Converted();
        public final Template.Method<Multimap<String, PropertyHandle>> getMutableProperties = new Template.Method();
    }
}

