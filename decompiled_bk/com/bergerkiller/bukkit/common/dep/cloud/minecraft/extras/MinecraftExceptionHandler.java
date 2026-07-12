/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.exception.ArgumentParseException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.CommandExecutionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidCommandSenderException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidSyntaxException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.NoPermissionException;
import com.bergerkiller.bukkit.common.dep.cloud.exception.handling.ExceptionContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.AudienceProvider;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.ComponentHelper;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.RichVariable;
import com.bergerkiller.bukkit.common.dep.cloud.util.TypeUtils;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.JoinConfiguration;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.NamedTextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextDecoration;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.ComponentMessageThrowable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.returnsreceiver.qual.This;

public final class MinecraftExceptionHandler<C> {
    private static final Component NULL = Component.text("null");
    private final Map<Class<? extends Throwable>, MessageFactory<C, ?>> componentBuilders = new HashMap();
    private final AudienceProvider<C> audienceProvider;
    private Decorator<C> decorator = (formatter, ctx, msg) -> msg;
    private ComponentCaptionFormatter<C> captionFormatter = ComponentCaptionFormatter.placeholderReplacing();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MessageFactory<C, InvalidSyntaxException> createDefaultInvalidSyntaxHandler() {
        return (formatter, ctx) -> ((TextComponent.Builder)Component.text().color(NamedTextColor.RED)).append((Component)ctx.context().formatCaption(formatter, StandardCaptionKeys.EXCEPTION_INVALID_SYNTAX, RichVariable.of("syntax", ComponentHelper.highlight(Component.text(String.format("/%s", ((InvalidSyntaxException)ctx.exception()).correctSyntax()), (TextColor)NamedTextColor.GRAY), NamedTextColor.WHITE))));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MessageFactory<C, InvalidCommandSenderException> createDefaultInvalidSenderHandler() {
        return (formatter, ctx) -> {
            boolean multiple = ((InvalidCommandSenderException)ctx.exception()).requiredSenderTypes().size() > 1;
            TextComponent expected = multiple ? Component.join(JoinConfiguration.commas(true), (Iterable<? extends ComponentLike>)((InvalidCommandSenderException)ctx.exception()).requiredSenderTypes().stream().map(TypeUtils::simpleName).map(name -> Component.text(name, (TextColor)NamedTextColor.GRAY)).collect(Collectors.toList())) : Component.text(TypeUtils.simpleName(((InvalidCommandSenderException)ctx.exception()).requiredSenderTypes().iterator().next()), (TextColor)NamedTextColor.GRAY);
            return ((TextComponent.Builder)Component.text().color(NamedTextColor.RED)).append((Component)ctx.context().formatCaption(formatter, multiple ? StandardCaptionKeys.EXCEPTION_INVALID_SENDER_LIST : StandardCaptionKeys.EXCEPTION_INVALID_SENDER, RichVariable.of("actual", Component.text(TypeUtils.simpleName(ctx.context().sender().getClass()), (TextColor)NamedTextColor.GRAY)), RichVariable.of("expected", expected)));
        };
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MessageFactory<C, NoPermissionException> createDefaultNoPermissionHandler() {
        return (formatter, ctx) -> ((TextComponent.Builder)Component.text().color(NamedTextColor.RED)).append((Component)ctx.context().formatCaption(formatter, StandardCaptionKeys.EXCEPTION_NO_PERMISSION, CaptionVariable.of("permission", ((NoPermissionException)ctx.exception()).permissionResult().permission().permissionString())));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MessageFactory<C, ArgumentParseException> createDefaultArgumentParsingHandler() {
        return (formatter, ctx) -> ((TextComponent.Builder)Component.text().color(NamedTextColor.RED)).append((Component)ctx.context().formatCaption(formatter, StandardCaptionKeys.EXCEPTION_INVALID_ARGUMENT, RichVariable.of("cause", MinecraftExceptionHandler.getMessage(formatter, ((ArgumentParseException)ctx.exception()).getCause()).colorIfAbsent(NamedTextColor.GRAY))));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> Consumer<ExceptionContext<C, CommandExecutionException>> createDefaultCommandExecutionLogger() {
        return ctx -> ((CommandExecutionException)ctx.exception()).getCause().printStackTrace();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MessageFactory<C, CommandExecutionException> createDefaultCommandExecutionHandler() {
        return MinecraftExceptionHandler.createDefaultCommandExecutionHandler(MinecraftExceptionHandler.createDefaultCommandExecutionLogger());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MessageFactory<C, CommandExecutionException> createDefaultCommandExecutionHandler(Consumer<ExceptionContext<C, CommandExecutionException>> logger) {
        return (formatter, ctx) -> {
            logger.accept(ctx);
            Throwable cause = ((CommandExecutionException)ctx.exception()).getCause();
            StringWriter writer = new StringWriter();
            cause.printStackTrace(new PrintWriter(writer));
            String stackTrace = writer.toString().replaceAll("\t", "    ");
            HoverEvent<Component> hover = HoverEvent.showText(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(MinecraftExceptionHandler.getMessage(formatter, cause))).append((Component)Component.newline())).append((Component)Component.text(stackTrace))).append((Component)Component.newline())).append((Component)Component.text("    Click to copy", (TextColor)NamedTextColor.GRAY, TextDecoration.ITALIC)));
            ClickEvent click = ClickEvent.copyToClipboard(stackTrace);
            return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)ctx.context().formatCaption(formatter, StandardCaptionKeys.EXCEPTION_UNEXPECTED, new CaptionVariable[0]))).color(NamedTextColor.RED)).hoverEvent(hover)).clickEvent(click);
        };
    }

    private MinecraftExceptionHandler(AudienceProvider<C> audienceProvider) {
        this.audienceProvider = audienceProvider;
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> MinecraftExceptionHandler<C> create(AudienceProvider<C> audienceProvider) {
        return new MinecraftExceptionHandler<C>(audienceProvider);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C extends Audience> MinecraftExceptionHandler<C> createNative() {
        return MinecraftExceptionHandler.create(AudienceProvider.nativeAudience());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultInvalidSyntaxHandler() {
        return this.handler(InvalidSyntaxException.class, MinecraftExceptionHandler.createDefaultInvalidSyntaxHandler());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultInvalidSenderHandler() {
        return this.handler(InvalidCommandSenderException.class, MinecraftExceptionHandler.createDefaultInvalidSenderHandler());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultNoPermissionHandler() {
        return this.handler(NoPermissionException.class, MinecraftExceptionHandler.createDefaultNoPermissionHandler());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultArgumentParsingHandler() {
        return this.handler(ArgumentParseException.class, MinecraftExceptionHandler.createDefaultArgumentParsingHandler());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultCommandExecutionHandler() {
        return this.defaultCommandExecutionHandler(MinecraftExceptionHandler.createDefaultCommandExecutionLogger());
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultCommandExecutionHandler(@NonNull Consumer<ExceptionContext<C, CommandExecutionException>> logger) {
        return this.handler(CommandExecutionException.class, MinecraftExceptionHandler.createDefaultCommandExecutionHandler(logger));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> defaultHandlers() {
        return this.defaultArgumentParsingHandler().defaultInvalidSenderHandler().defaultInvalidSyntaxHandler().defaultNoPermissionHandler().defaultCommandExecutionHandler();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public <T extends Throwable> @This @NonNull MinecraftExceptionHandler<C> handler(@NonNull Class<T> type, @NonNull MessageFactory<C, T> componentFactory) {
        this.componentBuilders.put(type, componentFactory);
        return this;
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> decorator(@NonNull Decorator<C> decorator) {
        this.decorator = decorator;
        return this;
    }

    public @This @NonNull MinecraftExceptionHandler<C> captionFormatter(@NonNull ComponentCaptionFormatter<C> captionFormatter) {
        this.captionFormatter = Objects.requireNonNull(captionFormatter, "captionFormatter");
        return this;
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public @This @NonNull MinecraftExceptionHandler<C> decorator(@NonNull Function<@NonNull Component, @NonNull ComponentLike> decorator) {
        return this.decorator((ComponentCaptionFormatter<C> formatter, ExceptionContext<C, ?> ctx, Component message) -> (ComponentLike)decorator.apply(message));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public void registerTo(@NonNull CommandManager<C> manager) {
        this.componentBuilders.forEach((type, formatter) -> manager.exceptionController().registerHandler(type, ctx -> {
            @Nullable ComponentLike message = formatter.message(this.captionFormatter, ctx);
            if (message != null) {
                this.audienceProvider.apply(ctx.context().sender()).sendMessage(this.decorator.decorate(this.captionFormatter, ctx, message.asComponent()));
            }
        }));
    }

    private static <C> Component getMessage(ComponentCaptionFormatter<C> formatter, Throwable throwable) {
        if (throwable instanceof ParserException) {
            return (Component)((ParserException)throwable).formatCaption(formatter);
        }
        Component msg = ComponentMessageThrowable.getOrConvertMessage(ComponentMessageThrowableConverterHolder.instance.maybeConvert(throwable));
        return msg == null ? NULL : msg;
    }

    @FunctionalInterface
    public static interface MessageFactory<C, T extends Throwable> {
        public @Nullable ComponentLike message(@NonNull ComponentCaptionFormatter<C> var1, @NonNull ExceptionContext<C, T> var2);
    }

    @FunctionalInterface
    public static interface Decorator<C> {
        public @NonNull ComponentLike decorate(@NonNull ComponentCaptionFormatter<C> var1, @NonNull ExceptionContext<C, ?> var2, @NonNull Component var3);
    }

    @API(status=API.Status.INTERNAL)
    public static final class ComponentMessageThrowableConverterHolder
    implements ComponentMessageThrowableConverter {
        private static ComponentMessageThrowableConverter instance = new ComponentMessageThrowableConverterHolder();

        public static void converter(ComponentMessageThrowableConverter converter) {
            instance = Objects.requireNonNull(converter, "converter");
        }

        private ComponentMessageThrowableConverterHolder() {
        }

        @Override
        public Throwable maybeConvert(Throwable thr) {
            return thr;
        }
    }

    @API(status=API.Status.INTERNAL)
    @FunctionalInterface
    public static interface ComponentMessageThrowableConverter {
        public Throwable maybeConvert(Throwable var1);
    }
}

