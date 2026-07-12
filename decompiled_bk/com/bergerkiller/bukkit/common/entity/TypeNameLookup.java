/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.entity.EntityType;

class TypeNameLookup {
    private static final NamePair[] values;
    private static final Object byNameLock;
    private static Map<String, NamePair> byNameCache;

    TypeNameLookup() {
    }

    public static NamePair lookupByName(String name) {
        return LogicUtil.synchronizeCopyOnWrite(byNameLock, () -> byNameCache, name, Map::get, (currByNameCache, theName) -> {
            NamePair computed = MountiplexUtil.parseArray(values, theName, null);
            if (computed != null) {
                HashMap<String, NamePair> updatedCache = new HashMap<String, NamePair>((Map<String, NamePair>)currByNameCache);
                updatedCache.put((String)theName, computed);
                byNameCache = updatedCache;
            }
            return computed;
        });
    }

    static {
        byNameLock = new Object();
        ArrayList<NamePair> namePairValues = new ArrayList<NamePair>();
        HashMap<String, NamePair> namePairByNameCache = new HashMap<String, NamePair>();
        for (EntityType entityType : EntityType.values()) {
            CommonEntityType commonEntityType = CommonEntityType.byEntityType(entityType);
            if (commonEntityType == CommonEntityType.UNKNOWN) {
                namePairValues.add(new NamePair(entityType.name(), entityType, commonEntityType));
                continue;
            }
            for (String name : commonEntityType.entityTypeNames) {
                namePairValues.add(new NamePair(name, entityType, commonEntityType));
            }
        }
        for (NamePair pair : namePairValues) {
            namePairByNameCache.put(pair.name, pair);
        }
        for (NamePair pair : namePairValues) {
            namePairByNameCache.putIfAbsent(pair.name.toLowerCase(Locale.ENGLISH), pair);
            namePairByNameCache.putIfAbsent(pair.name.toUpperCase(Locale.ENGLISH), pair);
        }
        values = namePairValues.toArray(new NamePair[0]);
        byNameCache = namePairByNameCache;
    }

    public static class NamePair {
        public final String name;
        public final EntityType entityType;
        public final CommonEntityType commonEntityType;

        public NamePair(String name, EntityType entityType, CommonEntityType commonEntityType) {
            this.name = name;
            this.entityType = entityType;
            this.commonEntityType = commonEntityType;
        }

        public String toString() {
            return this.name;
        }
    }
}

