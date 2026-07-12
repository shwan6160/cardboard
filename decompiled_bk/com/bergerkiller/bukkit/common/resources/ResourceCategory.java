/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.resources.ResourceKeyHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import org.bukkit.World;

public final class ResourceCategory<T> {
    private static final Map<Object, ResourceCategory> cache = Collections.synchronizedMap(new IdentityHashMap());
    public static final ResourceCategory<SoundEffect> sound_effect = ResourceCategory.create("sound_event");
    public static final ResourceCategory<DimensionType> dimension_type = ResourceCategory.create("dimension_type");
    public static final ResourceCategory<World> dimension = ResourceCategory.create("dimension");
    private final ResourceKeyHandle categoryKey;

    private ResourceCategory(Object keyHandle) {
        this.categoryKey = ResourceKeyHandle.createHandle(keyHandle);
    }

    private static <T> ResourceCategory<T> create(String name) {
        return ResourceCategory.create(IdentifierHandle.createNew(name));
    }

    protected static <T> ResourceCategory<T> create(IdentifierHandle name) {
        Object resourceKeyHandle = ((Template.StaticMethod)ResourceKeyHandle.T.createCategory.raw).invoke(name.getRaw());
        return cache.computeIfAbsent(resourceKeyHandle, ResourceCategory::new);
    }

    public ResourceKey<T> createKey(IdentifierHandle name) {
        return ResourceKey.fromMinecraftKey(this, name);
    }

    public ResourceKey<T> createKey(String name) {
        return ResourceKey.fromPath(this, name);
    }

    public ResourceKey<T> createKey(String namespace, String name) {
        return ResourceKey.fromPath(this, namespace, name);
    }

    public IdentifierHandle getName() {
        return this.categoryKey.getName();
    }

    public ResourceKeyHandle getCategoryKey() {
        return this.categoryKey;
    }

    public String toString() {
        return this.categoryKey.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ResourceCategory) {
            return ((ResourceCategory)o).getName().equals(this.getName());
        }
        return false;
    }
}

