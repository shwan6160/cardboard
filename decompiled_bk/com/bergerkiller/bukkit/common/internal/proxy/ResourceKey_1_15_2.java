/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ResourceKey_1_15_2<T> {
    private static final Map<String, ResourceKey_1_15_2> cache = Collections.synchronizedMap(new IdentityHashMap());
    private static final Object root_category = IdentifierHandle.createNew("root").getRaw();
    public final Object category;
    public final Object name;
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_TYPE_OVERWORLD = ResourceKey_1_15_2.createNamed("dimension_type", "overworld");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_TYPE_THE_NETHER = ResourceKey_1_15_2.createNamed("dimension_type", "the_nether");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_TYPE_THE_END = ResourceKey_1_15_2.createNamed("dimension_type", "the_end");
    public static final ResourceKey_1_15_2<Object> CATEGORY_WORLD_DIMENSION = ResourceKey_1_15_2.createCategory(IdentifierHandle.createNew("dimension").getRaw());
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_OVERWORLD = ResourceKey_1_15_2.createNamed("dimension", "overworld");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_THE_NETHER = ResourceKey_1_15_2.createNamed("dimension", "the_nether");
    public static final ResourceKey_1_15_2<Object> WORLD_DIMENSION_THE_END = ResourceKey_1_15_2.createNamed("dimension", "the_end");

    public static <T> ResourceKey_1_15_2<T> createCategory(Object categoryName) {
        return ResourceKey_1_15_2.fromCache(root_category, categoryName);
    }

    public static <T> ResourceKey_1_15_2<T> create(ResourceKey_1_15_2<T> category, Object key) {
        return ResourceKey_1_15_2.fromCache(category.name, key);
    }

    public static <T> ResourceKey_1_15_2<T> createNamed(String categoryName, String name) {
        return ResourceKey_1_15_2.fromCache(IdentifierHandle.createNew(categoryName).getRaw(), IdentifierHandle.createNew(name).getRaw());
    }

    private static <T> ResourceKey_1_15_2<T> fromCache(Object category, Object name) {
        String key = (category + ":" + name).intern();
        return cache.computeIfAbsent(key, unused -> new ResourceKey_1_15_2(category, name));
    }

    private ResourceKey_1_15_2(Object category, Object key) {
        this.category = category;
        this.name = key;
    }

    public String toString() {
        return "ResourceKey[" + this.category + " / " + this.name + ']';
    }
}

