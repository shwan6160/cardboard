/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.renderer;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BlockNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BuildableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.EntityNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.KeybindComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScoreComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.SelectorComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.StorageNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslatableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgument;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.VirtualComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.Style;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.renderer.AbstractComponentRenderer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.Translator;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.TriState;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TranslatableComponentRenderer<C>
extends AbstractComponentRenderer<C> {
    private static final Set<Style.Merge> MERGES;

    @NotNull
    public static TranslatableComponentRenderer<Locale> usingTranslationSource(final @NotNull Translator source) {
        Objects.requireNonNull(source, "source");
        return new TranslatableComponentRenderer<Locale>(){

            @Override
            @Nullable
            protected MessageFormat translate(@NotNull String key, @NotNull Locale context) {
                return source.translate(key, context);
            }

            @Override
            @NotNull
            protected Component renderTranslatableInner(@NotNull TranslatableComponent component, @NotNull Locale context) {
                TriState anyTranslations = source.hasAnyTranslations();
                if (anyTranslations == TriState.FALSE) {
                    return component;
                }
                Component translated = source.canTranslate(component.key(), context) ? source.translate(component, context) : null;
                return translated != null ? this.render(translated, context) : super.renderTranslatableInner(component, context);
            }
        };
    }

    @Nullable
    protected MessageFormat translate(@NotNull String key, @NotNull C context) {
        return null;
    }

    @Nullable
    protected MessageFormat translate(@NotNull String key, @Nullable String fallback, @NotNull C context) {
        return this.translate(key, context);
    }

    @Override
    @NotNull
    protected Component renderBlockNbt(@NotNull BlockNBTComponent component, @NotNull C context) {
        BlockNBTComponent.Builder builder = this.nbt(context, Component.blockNBT(), component).pos(component.pos());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderEntityNbt(@NotNull EntityNBTComponent component, @NotNull C context) {
        EntityNBTComponent.Builder builder = this.nbt(context, Component.entityNBT(), component).selector(component.selector());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderStorageNbt(@NotNull StorageNBTComponent component, @NotNull C context) {
        StorageNBTComponent.Builder builder = this.nbt(context, Component.storageNBT(), component).storage(component.storage());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    protected <O extends NBTComponent<O, B>, B extends NBTComponentBuilder<O, B>> B nbt(@NotNull C context, B builder, O oldComponent) {
        builder.nbtPath(oldComponent.nbtPath()).interpret(oldComponent.interpret());
        @Nullable Component separator = oldComponent.separator();
        if (separator != null) {
            builder.separator(this.render(separator, context));
        }
        return builder;
    }

    @Override
    @NotNull
    protected Component renderKeybind(@NotNull KeybindComponent component, @NotNull C context) {
        KeybindComponent.Builder builder = Component.keybind().keybind(component.keybind());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderScore(@NotNull ScoreComponent component, @NotNull C context) {
        ScoreComponent.Builder builder = Component.score().name(component.name()).objective(component.objective()).value(component.value());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderSelector(@NotNull SelectorComponent component, @NotNull C context) {
        SelectorComponent.Builder builder = Component.selector().pattern(component.pattern());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderText(@NotNull TextComponent component, @NotNull C context) {
        TextComponent.Builder builder = Component.text().content(component.content());
        return this.mergeStyleAndOptionallyDeepRender(component, builder, context);
    }

    @Override
    @NotNull
    protected Component renderTranslatable(@NotNull TranslatableComponent component, @NotNull C context) {
        List<TranslationArgument> arguments = component.arguments();
        List<Component> children = component.children();
        if (!arguments.isEmpty() || !children.isEmpty()) {
            TranslatableComponent.Builder builder = (TranslatableComponent.Builder)component.toBuilder();
            if (!arguments.isEmpty()) {
                ArrayList<TranslationArgument> translatedArguments = new ArrayList<TranslationArgument>(arguments);
                for (int i = 0; i < translatedArguments.size(); ++i) {
                    TranslationArgument arg = (TranslationArgument)translatedArguments.get(i);
                    if (!(arg.value() instanceof Component) || arg.value() instanceof VirtualComponent) continue;
                    translatedArguments.set(i, TranslationArgument.component(this.render((Component)arg.value(), context)));
                }
                builder.arguments(translatedArguments);
            }
            component = (TranslatableComponent)builder.build();
        }
        return this.renderTranslatableInner(component, context);
    }

    @NotNull
    protected Component renderTranslatableInner(@NotNull TranslatableComponent component, @NotNull C context) {
        @Nullable MessageFormat format = this.translate(component.key(), component.fallback(), context);
        if (format == null) {
            return this.optionallyRenderChildrenAndStyle(component, context);
        }
        List<TranslationArgument> args = component.arguments();
        TextComponent.Builder builder = Component.text();
        this.mergeStyle(component, builder, context);
        if (args.isEmpty()) {
            builder.content(format.format(null, new StringBuffer(), null).toString());
            return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
        }
        Object[] nulls = new Object[args.size()];
        StringBuffer sb = format.format(nulls, new StringBuffer(), (FieldPosition)null);
        AttributedCharacterIterator it = format.formatToCharacterIterator(nulls);
        while (it.getIndex() < it.getEndIndex()) {
            int end = it.getRunLimit();
            Integer index = (Integer)it.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index != null) {
                TranslationArgument arg = args.get(index);
                builder.append(arg.asComponent());
            } else {
                builder.append((Component)Component.text(sb.substring(it.getIndex(), end)));
            }
            it.setIndex(end);
        }
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }

    protected Component optionallyRenderChildrenAndStyle(Component component, C context) {
        List<Component> children;
        @Nullable HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
            component = component.hoverEvent(hoverEvent.withRenderedValue(this, context));
        }
        if ((children = component.children()).isEmpty()) {
            return component;
        }
        ArrayList rendered = new ArrayList(children.size());
        children.forEach(child -> rendered.add(this.render((Component)child, context)));
        return component.children(rendered);
    }

    protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(Component component, B builder, C context) {
        this.mergeStyle(component, builder, context);
        return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
    }

    protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(List<Component> children, B builder, C context) {
        if (!children.isEmpty()) {
            children.forEach(child -> builder.append(this.render((Component)child, context)));
        }
        return (O)builder.build();
    }

    protected <B extends ComponentBuilder<?, ?>> void mergeStyle(Component component, B builder, C context) {
        builder.mergeStyle(component, MERGES);
        builder.clickEvent(component.clickEvent());
        @Nullable HoverEvent<?> hoverEvent = component.hoverEvent();
        if (hoverEvent != null) {
            builder.hoverEvent(hoverEvent.withRenderedValue(this, context));
        }
    }

    static {
        EnumSet<Style.Merge> merges = EnumSet.allOf(Style.Merge.class);
        merges.remove((Object)Style.Merge.EVENTS);
        MERGES = Collections.unmodifiableSet(merges);
    }
}

