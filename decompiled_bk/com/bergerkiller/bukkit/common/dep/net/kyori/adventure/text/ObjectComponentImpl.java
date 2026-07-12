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
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ObjectComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.Style;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.ObjectContents;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ObjectComponentImpl
extends AbstractComponent
implements ObjectComponent {
    private final ObjectContents contents;

    private ObjectComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull ObjectContents contents) {
        super(children, style);
        this.contents = contents;
    }

    @Override
    @NotNull
    public ObjectContents contents() {
        return this.contents;
    }

    @Override
    @NotNull
    public ObjectComponent contents(@NotNull ObjectContents contents) {
        return ObjectComponentImpl.create(this.children, this.style, contents);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ObjectComponent)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        ObjectComponentImpl that = (ObjectComponentImpl)other;
        return Objects.equals(this.contents, that.contents());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.contents.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }

    @Override
    @NotNull
    public ObjectComponent.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    @NotNull
    static ObjectComponentImpl create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull ObjectContents objectContents) {
        return new ObjectComponentImpl(ComponentLike.asComponents(children, IS_NOT_EMPTY), Objects.requireNonNull(style, "style"), Objects.requireNonNull(objectContents, "contents"));
    }

    @Override
    @NotNull
    public ObjectComponent children(@NotNull List<? extends ComponentLike> children) {
        return ObjectComponentImpl.create(children, this.style, this.contents);
    }

    @Override
    @NotNull
    public ObjectComponent style(@NotNull Style style) {
        return ObjectComponentImpl.create(this.children, style, this.contents);
    }

    static final class BuilderImpl
    extends AbstractComponentBuilder<ObjectComponent, ObjectComponent.Builder>
    implements ObjectComponent.Builder {
        private ObjectContents objectContents;

        BuilderImpl() {
        }

        BuilderImpl(@NotNull ObjectComponent component) {
            super(component);
            this.objectContents = component.contents();
        }

        @Override
        @NotNull
        public ObjectComponent.Builder contents(@NotNull ObjectContents objectContents) {
            this.objectContents = Objects.requireNonNull(objectContents, "contents");
            return this;
        }

        @Override
        @NotNull
        public ObjectComponent build() {
            if (this.objectContents == null) {
                throw new IllegalStateException("contents must be set");
            }
            return ObjectComponentImpl.create(this.children, this.buildStyle(), this.objectContents);
        }
    }
}

