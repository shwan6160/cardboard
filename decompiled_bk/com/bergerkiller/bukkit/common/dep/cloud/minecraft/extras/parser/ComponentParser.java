/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.parser;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.StringParser;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.ComponentSerializer;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ComponentParser<C>
implements ArgumentParser<C, Component> {
    private static final StringParser.StringMode DEFAULT_STRING_MODE = StringParser.StringMode.QUOTED;
    private final Function<String, ? extends Component> componentDecoder;
    private final StringParser<C> stringParser;

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Component> miniMessageParser() {
        return ComponentParser.miniMessageParser(DEFAULT_STRING_MODE);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Component> miniMessageParser(@NonNull StringParser.StringMode stringMode) {
        return ComponentParser.componentParser(MiniMessage.miniMessage(), stringMode);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Component> componentParser(@NonNull ComponentSerializer<?, ? extends Component, String> componentSerializer) {
        return ComponentParser.componentParser(componentSerializer::deserialize);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Component> componentParser(@NonNull Function<String, ? extends Component> componentDecoder) {
        return ComponentParser.componentParser(componentDecoder, DEFAULT_STRING_MODE);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Component> componentParser(@NonNull ComponentSerializer<?, ? extends Component, String> componentSerializer, @NonNull StringParser.StringMode stringMode) {
        return ComponentParser.componentParser(componentSerializer::deserialize, stringMode);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Component> componentParser(@NonNull Function<String, ? extends Component> componentDecoder, @NonNull StringParser.StringMode stringMode) {
        return ParserDescriptor.of(new ComponentParser<C>(componentDecoder, stringMode), Component.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, Component> componentComponent(@NonNull Function<String, ? extends Component> componentDecoder, @NonNull StringParser.StringMode stringMode) {
        return CommandComponent.builder().parser(ComponentParser.componentParser(componentDecoder, stringMode));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public ComponentParser(@NonNull Function<String, ? extends Component> componentDecoder, @NonNull StringParser.StringMode stringMode) {
        this.componentDecoder = componentDecoder;
        this.stringParser = new StringParser(stringMode);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Component> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        ArgumentParseResult<String> result = this.stringParser.parse(commandContext, commandInput);
        if (result.failure().isPresent()) {
            return ArgumentParseResult.failure(result.failure().get());
        }
        try {
            return ArgumentParseResult.success(this.componentDecoder.apply(result.parsedValue().get()));
        }
        catch (Exception exception) {
            return ArgumentParseResult.failure(exception);
        }
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @NonNull StringParser.StringMode stringMode() {
        return this.stringParser.stringMode();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @NonNull Function<String, ? extends Component> componentDecoder() {
        return this.componentDecoder;
    }
}

