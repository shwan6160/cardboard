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

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.MultipleEntitySelector;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SelectorUtils;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apiguardian.api.API;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class MultipleEntitySelectorParser<C>
extends SelectorUtils.EntitySelectorParser<C, MultipleEntitySelector> {
    private final boolean allowEmpty;

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, MultipleEntitySelector> multipleEntitySelectorParser() {
        return MultipleEntitySelectorParser.multipleEntitySelectorParser(true);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, MultipleEntitySelector> multipleEntitySelectorParser(boolean allowEmpty) {
        return ParserDescriptor.of(new MultipleEntitySelectorParser<C>(allowEmpty), MultipleEntitySelector.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, MultipleEntitySelector> multipleEntitySelectorComponent() {
        return CommandComponent.builder().parser(MultipleEntitySelectorParser.multipleEntitySelectorParser());
    }

    @API(status=API.Status.STABLE, since="1.8.0")
    public MultipleEntitySelectorParser(boolean allowEmpty) {
        super(false);
        this.allowEmpty = allowEmpty;
    }

    public MultipleEntitySelectorParser() {
        this(true);
    }

    @Override
    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public MultipleEntitySelector mapResult(final @NonNull String input,  @NonNull SelectorUtils.EntitySelectorWrapper wrapper) {
        final List<Entity> entities = wrapper.entities();
        if (entities.isEmpty() && !this.allowEmpty) {
            new SelectorUtils.SelectorParser.Thrower(NO_ENTITIES_EXCEPTION_TYPE.get()).throwIt();
        }
        return new MultipleEntitySelector(){

            @Override
            public @NonNull String inputString() {
                return input;
            }

            @Override
            public @NonNull Collection<Entity> values() {
                return Collections.unmodifiableCollection(entities);
            }
        };
    }
}

