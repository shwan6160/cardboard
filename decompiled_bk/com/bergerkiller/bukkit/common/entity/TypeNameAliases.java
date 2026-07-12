/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.common.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.EntityType;

class TypeNameAliases {
    private static final Map<String, List<String>> aliases = new HashMap<String, List<String>>();

    TypeNameAliases() {
    }

    public static List<String> getNames(EntityType entityType) {
        return aliases.getOrDefault(entityType.name(), Collections.singletonList(entityType.name()));
    }

    private static void register(String ... names) {
        List<String> namesList = Collections.unmodifiableList(Arrays.asList(names));
        for (String name : namesList) {
            aliases.put(name, namesList);
        }
    }

    static {
        TypeNameAliases.register("CHEST_MINECART", "MINECART_CHEST");
        TypeNameAliases.register("FURNACE_MINECART", "MINECART_FURNACE");
        TypeNameAliases.register("TNT_MINECART", "MINECART_TNT");
        TypeNameAliases.register("HOPPER_MINECART", "MINECART_HOPPER");
        TypeNameAliases.register("COMMAND_BLOCK_MINECART", "MINECART_COMMAND");
        TypeNameAliases.register("SPAWNER_MINECART", "MINECART_MOB_SPAWNER");
    }
}

