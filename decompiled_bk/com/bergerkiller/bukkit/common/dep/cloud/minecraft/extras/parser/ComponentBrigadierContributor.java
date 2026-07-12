/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.parser;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMappingContributor;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.parser.ComponentParser;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import com.mojang.brigadier.arguments.StringArgumentType;
import org.apiguardian.api.API;

@API(status=API.Status.INTERNAL)
public final class ComponentBrigadierContributor
implements BrigadierMappingContributor {
    @Override
    public <C, S> void contribute(CommandManager<C> manager, CloudBrigadierManager<C, S> brigadierManager) {
        brigadierManager.registerMapping(new TypeToken<ComponentParser<C>>(){}, builder -> builder.cloudSuggestions().to(argument -> {
            switch (argument.stringMode()) {
                case QUOTED: {
                    return StringArgumentType.string();
                }
                case GREEDY: 
                case GREEDY_FLAG_YIELDING: {
                    return StringArgumentType.greedyString();
                }
            }
            return StringArgumentType.word();
        }));
    }
}

