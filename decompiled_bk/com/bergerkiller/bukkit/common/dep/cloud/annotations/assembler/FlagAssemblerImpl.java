/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler.FlagAssembler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.annotations.*"})
public final class FlagAssemblerImpl
implements FlagAssembler {
    private final CommandManager<?> commandManager;

    public FlagAssemblerImpl(@NonNull CommandManager<?> commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public @NonNull CommandFlag<?> assembleFlag(@NonNull FlagDescriptor descriptor) {
        Description description = descriptor.description() == null ? Description.empty() : descriptor.description();
        Permission permission = descriptor.permission() == null ? Permission.empty() : descriptor.permission();
        CommandFlag.Builder<?, Void> builder = this.commandManager.flagBuilder(descriptor.name()).withDescription(description).withAliases(descriptor.aliases()).withPermission(permission);
        if (descriptor.repeatable()) {
            builder = builder.asRepeatable();
        }
        if (descriptor.parameter().getType().equals(Boolean.TYPE)) {
            return builder.build();
        }
        TypeToken<?> token = descriptor.repeatable() && Collection.class.isAssignableFrom(descriptor.parameter().getType()) ? TypeToken.get(GenericTypeReflector.getTypeParameter(descriptor.parameter().getParameterizedType(), Collection.class.getTypeParameters()[0])) : TypeToken.get(descriptor.parameter().getType());
        if (token.equals(TypeToken.get(Boolean.TYPE))) {
            return builder.build();
        }
        List<Annotation> annotations = Arrays.asList(descriptor.parameter().getAnnotations());
        ParserRegistry<?> registry = this.commandManager.parserRegistry();
        ArgumentParser parser = descriptor.parserName() == null ? (ArgumentParser)registry.createParser(token, registry.parseAnnotations(token, annotations)).orElse(null) : (ArgumentParser)registry.createParser(descriptor.parserName(), registry.parseAnnotations(token, annotations)).orElse(null);
        if (parser == null) {
            throw new IllegalArgumentException(String.format("Cannot find parser for type '%s' for flag '%s'", descriptor.parameter().getType().getCanonicalName(), descriptor.name()));
        }
        SuggestionProvider suggestionProvider = descriptor.suggestions() != null ? (SuggestionProvider)registry.getSuggestionProvider(descriptor.suggestions()).orElse(null) : null;
        CommandComponent.Builder componentBuilder = CommandComponent.builder();
        componentBuilder.commandManager(this.commandManager).name(descriptor.name()).valueType(descriptor.parameter().getType()).parser(parser);
        if (suggestionProvider != null) {
            componentBuilder.suggestionProvider(suggestionProvider);
        }
        return builder.withComponent(componentBuilder.build()).build();
    }
}

