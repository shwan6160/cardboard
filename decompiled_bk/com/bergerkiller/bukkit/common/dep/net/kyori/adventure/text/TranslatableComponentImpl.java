/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.AbstractComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.AbstractComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslatableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgument;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgumentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.Style;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class TranslatableComponentImpl
extends AbstractComponent
implements TranslatableComponent {
    private final String key;
    @Nullable
    private final String fallback;
    private final List<TranslationArgument> args;

    static TranslatableComponent create(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull @NotNull ComponentLike @NotNull [] args) {
        Objects.requireNonNull(args, "args");
        return TranslatableComponentImpl.create(children, style, key, fallback, Arrays.asList(args));
    }

    static TranslatableComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull List<? extends ComponentLike> args) {
        return new TranslatableComponentImpl(ComponentLike.asComponents(children, IS_NOT_EMPTY), Objects.requireNonNull(style, "style"), Objects.requireNonNull(key, "key"), fallback, TranslatableComponentImpl.asArguments(args));
    }

    TranslatableComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String key, @Nullable String fallback, @NotNull List<TranslationArgument> args) {
        super(children, style);
        this.key = key;
        this.fallback = fallback;
        this.args = args;
    }

    @Override
    @NotNull
    public String key() {
        return this.key;
    }

    @Override
    @NotNull
    public TranslatableComponent key(@NotNull String key) {
        if (Objects.equals(this.key, key)) {
            return this;
        }
        return TranslatableComponentImpl.create(this.children, this.style, key, this.fallback, this.args);
    }

    @Override
    @Deprecated
    @NotNull
    public List<Component> args() {
        return ComponentLike.asComponents(this.args);
    }

    @Override
    @NotNull
    public List<TranslationArgument> arguments() {
        return this.args;
    }

    @Override
    @NotNull
    public TranslatableComponent arguments(ComponentLike ... args) {
        return TranslatableComponentImpl.create(this.children, this.style, this.key, this.fallback, args);
    }

    @Override
    @NotNull
    public TranslatableComponent arguments(@NotNull List<? extends ComponentLike> args) {
        return TranslatableComponentImpl.create(this.children, this.style, this.key, this.fallback, args);
    }

    @Override
    @Nullable
    public String fallback() {
        return this.fallback;
    }

    @Override
    @NotNull
    public TranslatableComponent fallback(@Nullable String fallback) {
        return TranslatableComponentImpl.create(this.children, this.style, this.key, fallback, this.args);
    }

    @Override
    @NotNull
    public TranslatableComponent children(@NotNull List<? extends ComponentLike> children) {
        return TranslatableComponentImpl.create(children, this.style, this.key, this.fallback, this.args);
    }

    @Override
    @NotNull
    public TranslatableComponent style(@NotNull Style style) {
        return TranslatableComponentImpl.create(this.children, style, this.key, this.fallback, this.args);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TranslatableComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TranslatableComponent that = (TranslatableComponent)other;
        return Objects.equals(this.key, that.key()) && Objects.equals(this.fallback, that.fallback()) && Objects.equals(this.args, that.arguments());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.key.hashCode();
        result = 31 * result + Objects.hashCode(this.fallback);
        result = 31 * result + this.args.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }

    @Override
    @NotNull
    public TranslatableComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static List<TranslationArgument> asArguments(@NotNull List<? extends ComponentLike> likes) {
        if (likes.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<TranslationArgument> ret = new ArrayList<TranslationArgument>(likes.size());
        for (int i = 0; i < likes.size(); ++i) {
            ComponentLike like = likes.get(i);
            if (like == null) {
                throw new NullPointerException("likes[" + i + "]");
            }
            if (like instanceof TranslationArgument) {
                ret.add((TranslationArgument)like);
                continue;
            }
            if (like instanceof TranslationArgumentLike) {
                ret.add(Objects.requireNonNull(((TranslationArgumentLike)like).asTranslationArgument(), "likes[" + i + "].asTranslationArgument()"));
                continue;
            }
            ret.add(TranslationArgument.component(like));
        }
        return Collections.unmodifiableList(ret);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<TranslatableComponent, TranslatableComponent.Builder>
    implements TranslatableComponent.Builder {
        @Nullable
        private String key;
        @Nullable
        private String fallback;
        private List<TranslationArgument> args = Collections.emptyList();

        BuilderImpl() {
        }

        BuilderImpl(@NotNull TranslatableComponent component) {
            super(component);
            this.key = component.key();
            this.args = component.arguments();
            this.fallback = component.fallback();
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder key(@NotNull String key) {
            this.key = key;
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder arguments(ComponentLike ... args) {
            Objects.requireNonNull(args, "args");
            if (args.length == 0) {
                return this.arguments(Collections.emptyList());
            }
            return this.arguments(Arrays.asList(args));
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder arguments(@NotNull List<? extends ComponentLike> args) {
            this.args = TranslatableComponentImpl.asArguments(Objects.requireNonNull(args, "args"));
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponent.Builder fallback(@Nullable String fallback) {
            this.fallback = fallback;
            return this;
        }

        @Override
        @NotNull
        public TranslatableComponent build() {
            if (this.key == null) {
                throw new IllegalStateException("key must be set");
            }
            return TranslatableComponentImpl.create(this.children, this.buildStyle(), this.key, this.fallback, this.args);
        }
    }
}

