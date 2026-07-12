/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponentImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.VirtualComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.VirtualComponentRenderer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.Style;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class VirtualComponentImpl<C>
extends TextComponentImpl
implements VirtualComponent {
    private final Class<C> contextType;
    private final VirtualComponentRenderer<C> renderer;

    static <C> VirtualComponent createVirtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer) {
        return VirtualComponentImpl.createVirtual(contextType, renderer, Collections.emptyList(), Style.empty());
    }

    static <C> VirtualComponent createVirtual(@NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer, List<? extends ComponentLike> children, Style style) {
        List<Component> filteredChildren = ComponentLike.asComponents(children, IS_NOT_EMPTY);
        return new VirtualComponentImpl<C>(filteredChildren, style, "", contextType, renderer);
    }

    private VirtualComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String content, @NotNull Class<C> contextType, @NotNull VirtualComponentRenderer<C> renderer) {
        super(children, style, content);
        this.contextType = contextType;
        this.renderer = renderer;
    }

    @Override
    VirtualComponent create0(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String content) {
        return new VirtualComponentImpl<C>(ComponentLike.asComponents(children, IS_NOT_EMPTY), style, content, this.contextType, this.renderer);
    }

    @NotNull
    public Class<C> contextType() {
        return this.contextType;
    }

    @NotNull
    public VirtualComponentRenderer<C> renderer() {
        return this.renderer;
    }

    @Override
    @NotNull
    public String content() {
        return this.renderer.fallbackString();
    }

    @Override
    @NotNull
    public TextComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof VirtualComponentImpl)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        VirtualComponentImpl that = (VirtualComponentImpl)other;
        return Objects.equals(this.contextType, that.contextType) && Objects.equals(this.renderer, that.renderer);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.contextType.hashCode();
        result = 31 * result + this.renderer.hashCode();
        return result;
    }

    static final class BuilderImpl<C>
    extends TextComponentImpl.BuilderImpl {
        private final Class<C> contextType;
        private final VirtualComponentRenderer<C> renderer;

        BuilderImpl(VirtualComponentImpl<C> other) {
            super(other);
            this.contextType = other.contextType();
            this.renderer = other.renderer();
        }

        @Override
        @NotNull
        public TextComponent build() {
            return VirtualComponentImpl.createVirtual(this.contextType, this.renderer, this.children, this.buildStyle());
        }
    }
}

