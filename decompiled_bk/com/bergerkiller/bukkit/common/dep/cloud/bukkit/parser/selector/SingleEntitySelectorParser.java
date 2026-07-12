/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.entity.Entity
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.SingleEntitySelector;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SelectorUtils;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import org.apiguardian.api.API;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class SingleEntitySelectorParser<C>
extends SelectorUtils.EntitySelectorParser<C, SingleEntitySelector> {
    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, SingleEntitySelector> singleEntitySelectorParser() {
        return ParserDescriptor.of(new SingleEntitySelectorParser<C>(), SingleEntitySelector.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, SingleEntitySelector> singleEntitySelectorComponent() {
        return CommandComponent.builder().parser(SingleEntitySelectorParser.singleEntitySelectorParser());
    }

    public SingleEntitySelectorParser() {
        super(true);
    }

    @Override
    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public SingleEntitySelector mapResult(final @NonNull String input, @NonNull SelectorUtils.EntitySelectorWrapper wrapper) {
        final Entity entity = wrapper.singleEntity();
        return new SingleEntitySelector(){

            @Override
            public @NonNull Entity single() {
                return entity;
            }

            @Override
            public @NonNull String inputString() {
                return input;
            }
        };
    }
}

