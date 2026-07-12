/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.CommandDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ImmutableCommandDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.CommandExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.util.annotation.AnnotationAccessor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.annotations.*"})
public final class CommandExtractorImpl
implements CommandExtractor {
    private final AnnotationParser<?> annotationParser;

    public CommandExtractorImpl(@NonNull AnnotationParser<?> annotationParser) {
        this.annotationParser = annotationParser;
    }

    @Override
    public @NonNull Collection<@NonNull CommandDescriptor> extractCommands(@NonNull Object instance) {
        AnnotationAccessor classAnnotations = AnnotationAccessor.of(instance.getClass());
        Command classCommand = classAnnotations.annotation(Command.class);
        String syntaxPrefix = classCommand == null ? "" : this.annotationParser.processString(classCommand.value()) + " ";
        Method[] methods = instance.getClass().getDeclaredMethods();
        ArrayList<CommandDescriptor> commandDescriptors = new ArrayList<CommandDescriptor>();
        for (Method method : methods) {
            Command[] commands = (Command[])method.getAnnotationsByType(Command.class);
            if (commands.length == 0) continue;
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            if (Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException(String.format("@Command annotated method '%s' is static! @Command annotated methods should not be static.", method.getName()));
            }
            for (Command command : commands) {
                String syntax = syntaxPrefix + this.annotationParser.processString(command.value());
                commandDescriptors.add(ImmutableCommandDescriptor.builder().method(method).syntax(this.annotationParser.syntaxParser().parseSyntax(method, syntax)).commandToken(syntax.split(" ")[0].split("\\|")[0]).requiredSender(command.requiredSender()).build());
            }
        }
        return commandDescriptors;
    }
}

