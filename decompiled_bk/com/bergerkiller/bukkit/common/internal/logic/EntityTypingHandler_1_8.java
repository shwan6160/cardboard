/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

class EntityTypingHandler_1_8
extends EntityTypingHandler {
    private final ServerLevelHandle dummyTrackerWorld = (ServerLevelHandle)ServerLevelHandle.T.newHandleNull();
    private final EntityTrackerHandle dummyTracker;
    private final IntHashMap<Object> entriesMap;
    private final Collection<Object> entries;
    private final FastMethod<Object> fallbackConstructor;

    public EntityTypingHandler_1_8() {
        SafeField.create(LevelHandle.T.getType(), "players", List.class).set(this.dummyTrackerWorld.getRaw(), Collections.emptyList());
        this.entriesMap = new IntHashMap();
        this.dummyTracker = (EntityTrackerHandle)EntityTrackerHandle.T.newHandleNull();
        this.entries = EntityTypingHandler_1_8.tryInitEntries(this.dummyTracker.getRaw());
        ((Template.Method)EntityTrackerHandle.T.setWorld.raw).invoke(this.dummyTracker.getRaw(), this.dummyTrackerWorld.getRaw());
        SafeField.set(this.dummyTracker.getRaw(), "trackedEntities", this.entriesMap.getRawHandle());
        this.fallbackConstructor = new FastMethod();
        ClassResolver resolver = new ClassResolver();
        resolver.setPackage("net.minecraft.server.level");
        resolver.setDeclaredClass(EntityTrackerEntryStateHandle.T.getType());
        MethodDeclaration fallbackConstructorMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static EntityTrackerEntry createNew(Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile) {\n#if exists net.minecraft.server.level.EntityTrackerEntry public EntityTrackerEntry(net.minecraft.world.entity.Entity entity, int viewDistance, int playerViewDistance, int updateInterval, boolean isMobile);\n    return new EntityTrackerEntry(entity, viewDistance, playerViewDistance, updateInterval, isMobile);\n#else\n    return new EntityTrackerEntry(entity, viewDistance, updateInterval, isMobile);\n#endif\n}"));
        this.fallbackConstructor.init(fallbackConstructorMethod);
    }

    private static Collection<Object> tryInitEntries(Object instance) {
        try {
            Collection<Object> result;
            Field entriesField = EntityTrackerHandle.T.getType().getDeclaredField("c");
            entriesField.setAccessible(true);
            if (entriesField.getType().equals(Set.class) || entriesField.getType().equals(HashSet.class)) {
                result = new HashSet<Object>();
            } else if (entriesField.getType().equals(List.class)) {
                result = new ArrayList();
            } else if (entriesField.getType().getName().equals("me.rastrian.dev.utils.IndexedLinkedHashSet")) {
                result = (Collection)entriesField.getType().getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                throw new UnsupportedOperationException("Unsupported entries set type: " + entriesField.getType());
            }
            entriesField.set(instance, result);
            return result;
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find EntityTracker flat entries 'c' set", t);
            return new ArrayList<Object>();
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public Class<?> getClassFromEntityTypes(Object nmsEntityTypesInstance) {
        return null;
    }

    @Override
    public EntityTrackerEntryHandle createEntityTrackerEntry(EntityTracker entityTracker, Entity entity) {
        Template.Handle createdEntry = null;
        try {
            this.entries.clear();
            this.entriesMap.clear();
            this.dummyTracker.setTrackingDistance((Bukkit.getViewDistance() - 1) * 16);
            this.dummyTracker.trackEntity(entity);
            List<IntHashMap.Entry<Object>> entries = this.entriesMap.entries();
            if (!entries.isEmpty()) {
                createdEntry = EntityTrackerEntryHandle.createHandle(entries.get(0).getValue());
            } else {
                Logging.LOGGER_REFLECTION.once(Level.WARNING, "No dummy entry created for " + entity.getName() + ", resolving to defaults");
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.once(Level.SEVERE, "Failed to create dummy entry", t);
        }
        if (createdEntry == null) {
            Object nmsEntity = HandleConversion.toEntityHandle(entity);
            createdEntry = EntityTrackerEntryHandle.createHandle(this.fallbackConstructor.invoke(null, nmsEntity, 80, (Bukkit.getViewDistance() - 1) * 16, 3, true));
        }
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            EntityTrackerEntryStateHandle.T.opt_passengers.set(createdEntry.getRaw(), new ExtendedEntity<Entity>(entity).getPassengers());
        }
        if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            EntityTrackerEntryStateHandle.T.opt_vehicle.set(createdEntry.getRaw(), entity.getVehicle());
        }
        return createdEntry;
    }

    @Override
    public EntityTrackerEntryHook getEntityTrackerEntryHook(Object entityTrackerEntryHandle) {
        return EntityTrackerEntryHook_1_8_to_1_13_2.get(entityTrackerEntryHandle, EntityTrackerEntryHook_1_8_to_1_13_2.class);
    }

    @Override
    public Object hookEntityTrackerEntry(Object entityTrackerEntryHandle) {
        return new EntityTrackerEntryHook_1_8_to_1_13_2().hook(entityTrackerEntryHandle);
    }
}

