/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.AnnotatedMethodHandler;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.method.ParameterValue;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandExecutionException;
import com.bergerkiller.bukkit.common.dep.cloud.execution.CommandExecutionHandler;
import com.bergerkiller.bukkit.common.dep.cloud.injection.ParameterInjectorRegistry;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MethodCommandExecutionHandler<C>
extends AnnotatedMethodHandler<C>
implements CommandExecutionHandler.FutureCommandExecutionHandler<C> {
    private final CommandMethodContext<C> context;
    private final AnnotationParser<C> annotationParser;
    private final boolean returnsFuture;

    public MethodCommandExecutionHandler(@NonNull CommandMethodContext<C> context) {
        super(((CommandMethodContext)context).method, ((CommandMethodContext)context).instance, ((CommandMethodContext)context).annotationParser.manager().parameterInjectorRegistry());
        this.context = context;
        this.annotationParser = context.annotationParser();
        this.returnsFuture = context.method().getReturnType().equals(CompletableFuture.class);
    }

    public @NonNull CommandMethodContext<C> context() {
        return this.context;
    }

    @Override
    public CompletableFuture<Void> executeFuture(@NonNull CommandContext<C> commandContext) {
        try {
            Object result = this.methodHandle().invokeWithArguments(this.createParameterValues(commandContext).stream().map(ParameterValue::value).collect(Collectors.toList()));
            if (this.returnsFuture) {
                return (CompletableFuture)result;
            }
            return CompletableFuture.completedFuture(null);
        }
        catch (Error e) {
            throw e;
        }
        catch (Throwable throwable) {
            throw new CommandExecutionException(throwable, commandContext);
        }
    }

    @Override
    protected final @Nullable ParameterValue getParameterValue(@NonNull Parameter parameter, @NonNull CommandContext<C> context) {
        ArgumentDescriptor argumentDescriptor = ((CommandMethodContext)this.context).argumentDescriptors.stream().filter(descriptor -> descriptor.parameter().equals(parameter)).findFirst().orElse(null);
        if (argumentDescriptor != null) {
            String argumentName = argumentDescriptor.name().equals("__INFERRED_ARGUMENT_NAME__") ? parameter.getName() : this.annotationParser.processString(argumentDescriptor.name());
            CommandComponent commandComponent = (CommandComponent)((CommandMethodContext)this.context).commandComponents.get(argumentName);
            if (commandComponent.required()) {
                return ParameterValue.of(parameter, context.get(argumentName), argumentDescriptor);
            }
            Object optional = context.optional(argumentName).orElse(null);
            return ParameterValue.of(parameter, optional, argumentDescriptor);
        }
        FlagDescriptor flagDescriptor = ((CommandMethodContext)this.context).flagDescriptors.stream().filter(descriptor -> descriptor.parameter().equals(parameter)).findFirst().orElse(null);
        if (flagDescriptor != null) {
            if (parameter.getType().equals(Boolean.TYPE)) {
                return ParameterValue.of(parameter, context.flags().isPresent(flagDescriptor.name()), flagDescriptor);
            }
            if (flagDescriptor.repeatable() && parameter.getType().isAssignableFrom(List.class)) {
                return ParameterValue.of(parameter, context.flags().getAll(flagDescriptor.name()), flagDescriptor);
            }
            return ParameterValue.of(parameter, context.flags().getValue(flagDescriptor.name(), null), flagDescriptor);
        }
        return null;
    }

    public static class CommandMethodContext<C> {
        private final Object instance;
        private final Map<String, CommandComponent<C>> commandComponents;
        private final Method method;
        private final ParameterInjectorRegistry<C> injectorRegistry;
        private final AnnotationParser<C> annotationParser;
        private final Collection<@NonNull ArgumentDescriptor> argumentDescriptors;
        private final Collection<@NonNull FlagDescriptor> flagDescriptors;

        CommandMethodContext(@NonNull Object instance, @NonNull Map<@NonNull String, @NonNull CommandComponent<C>> commandComponents, @NonNull Collection<@NonNull ArgumentDescriptor> argumentDescriptors, @NonNull Collection<@NonNull FlagDescriptor> flagDescriptors, @NonNull Method method, @NonNull AnnotationParser<C> annotationParser) {
            this.instance = instance;
            this.commandComponents = commandComponents;
            this.method = method;
            this.method.setAccessible(true);
            this.injectorRegistry = annotationParser.manager().parameterInjectorRegistry();
            this.annotationParser = annotationParser;
            this.argumentDescriptors = argumentDescriptors;
            this.flagDescriptors = flagDescriptors;
        }

        public @NonNull Object instance() {
            return this.instance;
        }

        public final @NonNull Method method() {
            return this.method;
        }

        public final @NonNull Map<@NonNull String, @NonNull CommandComponent<C>> commandComponents() {
            return this.commandComponents;
        }

        public final @NonNull ParameterInjectorRegistry<C> injectorRegistry() {
            return this.injectorRegistry;
        }

        public @NonNull AnnotationParser<C> annotationParser() {
            return this.annotationParser;
        }

        @API(status=API.Status.STABLE)
        public @NonNull Collection<@NonNull ArgumentDescriptor> argumentDescriptors() {
            return Collections.unmodifiableCollection(this.argumentDescriptors);
        }

        @API(status=API.Status.STABLE)
        public @NonNull Collection<@NonNull FlagDescriptor> flagDescriptors() {
            return Collections.unmodifiableCollection(this.flagDescriptors);
        }
    }
}

