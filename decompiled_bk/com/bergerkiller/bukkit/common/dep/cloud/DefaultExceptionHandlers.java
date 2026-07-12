/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.ArgumentParseException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandExecutionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidCommandSenderException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidSyntaxException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.NoPermissionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.NoSuchCommandException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionController;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Triplet;
import com.bergerkiller.bukkit.common.dep.cloud.util.TypeUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
final class DefaultExceptionHandlers<C> {
    private final Consumer<Triplet<CommandContext<C>, Caption, List<@NonNull CaptionVariable>>> messageSender;
    private final Consumer<Pair<String, Throwable>> logger;
    private final ExceptionController<C> exceptionController;

    DefaultExceptionHandlers(@NonNull Consumer<Triplet<CommandContext<C>, Caption, List<@NonNull CaptionVariable>>> messageSender, @NonNull Consumer<Pair<String, Throwable>> logger, @NonNull ExceptionController<C> exceptionController) {
        this.messageSender = Objects.requireNonNull(messageSender, "messageSender");
        this.logger = Objects.requireNonNull(logger, "logger");
        this.exceptionController = Objects.requireNonNull(exceptionController, "exceptionController");
    }

    void register() {
        this.exceptionController.registerHandler(Throwable.class, context -> {
            this.sendMessage(context, StandardCaptionKeys.EXCEPTION_UNEXPECTED, new CaptionVariable[0]);
            this.log("An unhandled exception was thrown during command execution", (Throwable)context.exception());
        });
        this.exceptionController.registerHandler(CommandExecutionException.class, context -> {
            this.sendMessage(context, StandardCaptionKeys.EXCEPTION_UNEXPECTED, new CaptionVariable[0]);
            this.log("Exception executing command handler", ((CommandExecutionException)context.exception()).getCause());
        });
        this.exceptionController.registerHandler(ArgumentParseException.class, context -> this.sendMessage(context, StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT, CaptionVariable.of("cause", ((ArgumentParseException)context.exception()).getCause().getMessage())));
        this.exceptionController.registerHandler(NoSuchCommandException.class, context -> this.sendMessage(context, StandardCaptionKeys.EXCEPTION_NO_SUCH_COMMAND, CaptionVariable.of("command", ((NoSuchCommandException)context.exception()).suppliedCommand())));
        this.exceptionController.registerHandler(NoPermissionException.class, context -> this.sendMessage(context, StandardCaptionKeys.EXCEPTION_NO_PERMISSION, CaptionVariable.of("permission", ((NoPermissionException)context.exception()).permissionResult().permission().permissionString())));
        this.exceptionController.registerHandler(InvalidCommandSenderException.class, context -> {
            boolean multiple = ((InvalidCommandSenderException)context.exception()).requiredSenderTypes().size() != 1;
            String expected = multiple ? ((InvalidCommandSenderException)context.exception()).requiredSenderTypes().stream().map(TypeUtils::simpleName).collect(Collectors.joining(", ")) : TypeUtils.simpleName(((InvalidCommandSenderException)context.exception()).requiredSenderTypes().iterator().next());
            this.sendMessage(context, multiple ? StandardCaptionKeys.EXCEPTION_INVALID_SENDER_LIST : StandardCaptionKeys.EXCEPTION_INVALID_SENDER, CaptionVariable.of("actual", context.context().sender().getClass().getSimpleName()), CaptionVariable.of("expected", expected));
        });
        this.exceptionController.registerHandler(InvalidSyntaxException.class, context -> this.sendMessage(context, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, CaptionVariable.of("syntax", ((InvalidSyntaxException)context.exception()).correctSyntax())));
    }

    private void sendMessage(@NonNull ExceptionContext<C, ?> context, @NonNull Caption caption, CaptionVariable ... variables) {
        this.sendMessage(context.context(), caption, variables);
    }

    private void sendMessage(@NonNull CommandContext<C> context, @NonNull Caption caption, CaptionVariable ... variables) {
        this.messageSender.accept(Triplet.of(context, caption, Arrays.asList(variables)));
    }

    private void log(@NonNull String message, @NonNull Throwable exception) {
        this.logger.accept(Pair.of(message, exception));
    }
}

