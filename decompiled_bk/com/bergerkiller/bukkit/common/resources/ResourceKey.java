/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.resources.ResourceKeyHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ResourceKey<T>
extends BasicWrapper<ResourceKeyHandle> {
    private static final Map<Object, ResourceKey> cache = Collections.synchronizedMap(new IdentityHashMap());

    private ResourceKey(Object keyHandle) {
        this.setHandle(ResourceKeyHandle.createHandle(keyHandle));
    }

    public ResourceCategory<T> getCategory() {
        return ResourceCategory.create(((ResourceKeyHandle)this.handle).getCategory());
    }

    public IdentifierHandle getName() {
        return ((ResourceKeyHandle)this.handle).getName();
    }

    public String getPath() {
        return ((ResourceKeyHandle)this.handle).getName().toString();
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ResourceKey) {
            ResourceKey other = (ResourceKey)o;
            return this.getCategory().equals(other.getCategory()) && this.getName().equals(other.getName());
        }
        return false;
    }

    public static <T> ResourceKey<T> fromResourceKeyHandle(Object nmsResourceKeyHandle) {
        if (nmsResourceKeyHandle == null) {
            return null;
        }
        return cache.computeIfAbsent(nmsResourceKeyHandle, ResourceKey::new);
    }

    public static <T> ResourceKey<T> fromMinecraftKey(ResourceCategory<T> category, IdentifierHandle minecraftKey) {
        if (minecraftKey != null) {
            Object resourceKeyHandle = ((Template.StaticMethod)ResourceKeyHandle.T.create.raw).invoke(category.getCategoryKey().getRaw(), minecraftKey.getRaw());
            return ResourceKey.fromResourceKeyHandle(resourceKeyHandle);
        }
        return null;
    }

    public static <T> ResourceKey<T> fromPath(ResourceCategory<T> category, String key) {
        return ResourceKey.fromMinecraftKey(category, IdentifierHandle.createNew(key));
    }

    public static <T> ResourceKey<T> fromPath(ResourceCategory<T> category, String namespace, String name) {
        return ResourceKey.fromMinecraftKey(category, IdentifierHandle.createNew(namespace, name));
    }
}

