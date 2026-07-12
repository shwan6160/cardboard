/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParserImpl;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateResultMapper;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public class AggregateParserBuilder<C> {
    private final List<CommandComponent<C>> components;

    AggregateParserBuilder(@NonNull List<? extends CommandComponent<C>> components) {
        this.components = Collections.unmodifiableList(components);
    }

    AggregateParserBuilder() {
        this.components = Collections.emptyList();
    }

    public final <O> @NonNull MappedAggregateParserBuilder<C, O> withMapper(@NonNull TypeToken<O> valueType, @NonNull AggregateResultMapper<C, O> mapper) {
        return new MappedAggregateParserBuilder<C, O>(this.components(), valueType, mapper);
    }

    public final <O> @NonNull MappedAggregateParserBuilder<C, O> withMapper(@NonNull Class<O> valueType, @NonNull AggregateResultMapper<C, O> mapper) {
        return new MappedAggregateParserBuilder<C, O>(this.components(), TypeToken.get(valueType), mapper);
    }

    public final <O> @NonNull MappedAggregateParserBuilder<C, O> withDirectMapper(@NonNull Class<O> valueType, @NonNull AggregateResultMapper.DirectSuccessMapper<C, O> mapper) {
        return new MappedAggregateParserBuilder<C, O>(this.components(), TypeToken.get(valueType), mapper);
    }

    public final <O> @NonNull MappedAggregateParserBuilder<C, O> withDirectMapper(@NonNull TypeToken<O> valueType, @NonNull AggregateResultMapper.DirectSuccessMapper<C, O> mapper) {
        return new MappedAggregateParserBuilder<C, O>(this.components(), valueType, mapper);
    }

    public @NonNull AggregateParserBuilder<C> withComponent(@NonNull CommandComponent<C> component) {
        ArrayList<CommandComponent<C>> components = new ArrayList<CommandComponent<C>>(this.components());
        components.add(component);
        return new AggregateParserBuilder<C>(components);
    }

    public <T> @NonNull AggregateParserBuilder<C> withComponent(@NonNull String name, @NonNull ParserDescriptor<C, T> parserDescriptor) {
        return this.withComponent(CommandComponent.builder().name(name).parser(parserDescriptor).build());
    }

    public <T> @NonNull AggregateParserBuilder<C> withComponent(@NonNull String name, @NonNull ParserDescriptor<C, T> parserDescriptor, @NonNull SuggestionProvider<C> suggestionProvider) {
        return this.withComponent(CommandComponent.builder().name(name).parser(parserDescriptor).suggestionProvider(suggestionProvider).build());
    }

    public <T> @NonNull AggregateParserBuilder<C> withComponent(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<C, T> parserDescriptor) {
        return this.withComponent(CommandComponent.builder().key(name).parser(parserDescriptor).build());
    }

    public <T> @NonNull AggregateParserBuilder<C> withComponent(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<C, T> parserDescriptor, @NonNull SuggestionProvider<C> suggestionProvider) {
        return this.withComponent(CommandComponent.builder().key(name).parser(parserDescriptor).suggestionProvider(suggestionProvider).build());
    }

    final @NonNull List<@NonNull CommandComponent<C>> components() {
        return this.components;
    }

    public static final class MappedAggregateParserBuilder<C, O>
    extends AggregateParserBuilder<C> {
        private final AggregateResultMapper<C, O> mapper;
        private final TypeToken<O> valueType;

        MappedAggregateParserBuilder(@NonNull List<CommandComponent<C>> components, @NonNull TypeToken<O> valueType, @NonNull AggregateResultMapper<C, O> mapper) {
            super(components);
            this.valueType = valueType;
            this.mapper = mapper;
        }

        public @NonNull MappedAggregateParserBuilder<C, O> withComponent(@NonNull CommandComponent<C> component) {
            ArrayList components = new ArrayList(this.components());
            components.add(component);
            return new MappedAggregateParserBuilder(components, this.valueType, this.mapper);
        }

        public <T> @NonNull MappedAggregateParserBuilder<C, O> withComponent(@NonNull String name, @NonNull ParserDescriptor<C, T> parserDescriptor) {
            return this.withComponent((CommandComponent)CommandComponent.builder().name(name).parser(parserDescriptor).build());
        }

        public <T> @NonNull MappedAggregateParserBuilder<C, O> withComponent(@NonNull String name, @NonNull ParserDescriptor<C, T> parserDescriptor, @NonNull SuggestionProvider<C> suggestionProvider) {
            return this.withComponent((CommandComponent)CommandComponent.builder().name(name).parser(parserDescriptor).suggestionProvider(suggestionProvider).build());
        }

        public <T> @NonNull MappedAggregateParserBuilder<C, O> withComponent(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<C, T> parserDescriptor) {
            return this.withComponent((CommandComponent)CommandComponent.builder().key(name).parser(parserDescriptor).build());
        }

        public <T> @NonNull MappedAggregateParserBuilder<C, O> withComponent(@NonNull CloudKey<T> name, @NonNull ParserDescriptor<C, T> parserDescriptor, @NonNull SuggestionProvider<C> suggestionProvider) {
            return this.withComponent((CommandComponent)CommandComponent.builder().key(name).parser(parserDescriptor).suggestionProvider(suggestionProvider).build());
        }

        public @NonNull AggregateParser<C, O> build() {
            return new AggregateParserImpl(this.components(), this.valueType, this.mapper);
        }
    }
}

