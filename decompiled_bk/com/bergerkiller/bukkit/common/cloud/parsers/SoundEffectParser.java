/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.cloud.parsers;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.cloud.captions.BKCommonLibCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMappingBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.LambdaBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SoundEffectParser<C>
implements ArgumentParser<C, ResourceKey<SoundEffect>>,
BlockingSuggestionProvider.Strings<C> {
    public static <C> @NonNull ParserDescriptor<C, ResourceKey<SoundEffect>> soundEffectParser() {
        return ParserDescriptor.of(new SoundEffectParser<C>(), new TypeToken<ResourceKey<SoundEffect>>(){});
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ResourceKey<SoundEffect>> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.peekString();
        ResourceKey<SoundEffect> result = SoundEffect.fromName(input);
        if (result == null) {
            return ArgumentParseResult.failure(new SoundEffectParseException(input, commandContext));
        }
        commandInput.readString();
        return ArgumentParseResult.success(result);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        Collection<IdentifierHandle> keys = SoundEventHandle.getSoundNames();
        ArrayList<String> suggestions = new ArrayList<String>(keys.size());
        for (IdentifierHandle key : keys) {
            suggestions.add(key.toString());
        }
        return suggestions;
    }

    public static void registerBrigadier(CloudBrigadierManager<?, ?> brig) throws Exception {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.commands.synchronization.SuggestionProviders");
        resolver.addImport("net.minecraft.commands.arguments.IdentifierArgument");
        resolver.addImport("com.mojang.brigadier.suggestion.SuggestionProvider");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);
        MethodDeclaration createIdentifierArgumentMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static IdentifierArgument createIdentifierArgument() {\n#if version >= 1.18\n    return IdentifierArgument.id();\n#else\n    return IdentifierArgument.a();\n#endif\n}", resolver));
        FastMethod createIdentifierArgument = new FastMethod(createIdentifierArgumentMethod);
        createIdentifierArgument.forceInitialization();
        MethodDeclaration createSuggestionProviderMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static SuggestionProvider getSoundSuggestions() {\n#if version >= 1.17\n    return SuggestionProviders.AVAILABLE_SOUNDS;\n#else\n    return SuggestionProviders.c;\n#endif\n}", resolver));
        FastMethod createSuggestionProvider = new FastMethod(createSuggestionProviderMethod);
        Object soundSuggestionProvider = createSuggestionProvider.invoke(null);
        BrigadierMappingBuilder.SuggestionProviderSupplier suggestionProvider = (BrigadierMappingBuilder.SuggestionProviderSupplier)LambdaBuilder.of(BrigadierMappingBuilder.SuggestionProviderSupplier.class).createConstant(soundSuggestionProvider);
        FastMethod registerMapping = new FastMethod(CloudBrigadierManager.class.getMethod("registerMapping", TypeToken.class, Consumer.class));
        FastMethod builderTo = new FastMethod(BrigadierMappingBuilder.class.getMethod("to", Function.class));
        registerMapping.forceInitialization();
        builderTo.forceInitialization();
        registerMapping.invoke(brig, new TypeToken<SoundEffectParser<CommandSender>>(){}, builder -> {
            builderTo.invoke(builder, o -> createIdentifierArgument.invoke(null));
            builder.suggestedBy((BrigadierMappingBuilder.SuggestionProviderSupplier)LogicUtil.unsafeCast(suggestionProvider));
        });
    }

    public static final class SoundEffectParseException
    extends ParserException {
        private static final long serialVersionUID = 1615554107385965610L;
        private final String input;

        public SoundEffectParseException(String input, CommandContext<?> context) {
            super(SoundEffectParser.class, context, BKCommonLibCaptionKeys.ARGUMENT_PARSE_FAILURE_SOUNDEFFECT, CaptionVariable.of("input", input));
            this.input = input;
        }

        public String getInput() {
            return this.input;
        }
    }
}

