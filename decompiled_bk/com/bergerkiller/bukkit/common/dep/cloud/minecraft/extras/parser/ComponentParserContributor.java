/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.parser;

import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.MinecraftExtrasParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.annotation.specifier.Decoder;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.parser.ComponentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserContributor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.StandardParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.StringParser;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.minimessage.MiniMessage;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.function.Function;
import org.apiguardian.api.API;

@API(status=API.Status.INTERNAL)
public final class ComponentParserContributor
implements ParserContributor {
    @Override
    public <C> void contribute(ParserRegistry<C> registry) {
        try {
            registry.registerAnnotationMapper(Decoder.MiniMessage.class, (annotation, parsedType) -> ParserParameters.single(MinecraftExtrasParserParameters.COMPONENT_DECODER, arg_0 -> MiniMessage.miniMessage().deserialize(arg_0)));
            registry.registerAnnotationMapper(Decoder.Legacy.class, (annotation, parsedType) -> {
                char character = annotation.value();
                return ParserParameters.single(MinecraftExtrasParserParameters.COMPONENT_DECODER, string -> LegacyComponentSerializer.legacy(character).deserialize((String)string));
            });
            registry.registerAnnotationMapper(Decoder.Json.class, (annotation, parsedType) -> {
                boolean downsampleColors = annotation.downsampleColors();
                Function<String, Component> decoder = downsampleColors ? GsonComponentSerializer.colorDownsamplingGson()::deserialize : GsonComponentSerializer.gson()::deserialize;
                return ParserParameters.single(MinecraftExtrasParserParameters.COMPONENT_DECODER, decoder);
            });
            registry.registerAnnotationMapper(Decoder.class, (annotation, parsedType) -> {
                Function<String, ? extends Component> decoder;
                try {
                    decoder = annotation.value().getConstructor(new Class[0]).newInstance(new Object[0]).decoder(parsedType);
                }
                catch (ReflectiveOperationException exception) {
                    throw new IllegalArgumentException("Could not create decoder for " + annotation.value(), exception);
                }
                return ParserParameters.single(MinecraftExtrasParserParameters.COMPONENT_DECODER, decoder);
            });
            registry.registerParserSupplier(TypeToken.get(Component.class), options -> {
                boolean greedy = options.get(StandardParameters.GREEDY, false);
                boolean greedyFlagAware = options.get(StandardParameters.FLAG_YIELDING, false);
                boolean quoted = options.get(StandardParameters.QUOTED, false);
                if (greedyFlagAware && quoted) {
                    throw new IllegalArgumentException("Don't know whether to create GREEDY_FLAG_YIELDING or QUOTED StringArgument.StringParser, both specified.");
                }
                if (greedy && quoted) {
                    throw new IllegalArgumentException("Don't know whether to create GREEDY or QUOTED StringArgument.StringParser, both specified.");
                }
                StringParser.StringMode stringMode = greedyFlagAware ? StringParser.StringMode.GREEDY_FLAG_YIELDING : (greedy ? StringParser.StringMode.GREEDY : (quoted ? StringParser.StringMode.QUOTED : StringParser.StringMode.SINGLE));
                Function<String, Component> decoder = options.get(MinecraftExtrasParserParameters.COMPONENT_DECODER, arg_0 -> MiniMessage.miniMessage().deserialize(arg_0));
                return new ComponentParser(decoder, stringMode);
            });
        }
        catch (Exception | LinkageError throwable) {
            // empty catch block
        }
    }
}

