/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackCallback;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackInfo;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackInfoLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackRequest;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.MonkeyBars;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ResourcePackRequestImpl
implements ResourcePackRequest {
    private final List<ResourcePackInfo> packs;
    private final ResourcePackCallback cb;
    private final boolean replace;
    private final boolean required;
    @Nullable
    private final Component prompt;

    ResourcePackRequestImpl(List<ResourcePackInfo> packs, ResourcePackCallback cb, boolean replace, boolean required, @Nullable Component prompt) {
        this.packs = packs;
        this.cb = cb;
        this.replace = replace;
        this.required = required;
        this.prompt = prompt;
    }

    @Override
    @NotNull
    public List<ResourcePackInfo> packs() {
        return this.packs;
    }

    @Override
    @NotNull
    public ResourcePackRequest packs(@NotNull Iterable<? extends ResourcePackInfoLike> packs) {
        if (this.packs.equals(packs)) {
            return this;
        }
        return new ResourcePackRequestImpl(MonkeyBars.toUnmodifiableList(ResourcePackInfoLike::asResourcePackInfo, packs), this.cb, this.replace, this.required, this.prompt);
    }

    @Override
    @NotNull
    public ResourcePackCallback callback() {
        return this.cb;
    }

    @Override
    @NotNull
    public ResourcePackRequest callback(@NotNull ResourcePackCallback cb) {
        if (cb == this.cb) {
            return this;
        }
        return new ResourcePackRequestImpl(this.packs, Objects.requireNonNull(cb, "cb"), this.replace, this.required, this.prompt);
    }

    @Override
    public boolean replace() {
        return this.replace;
    }

    @Override
    public boolean required() {
        return this.required;
    }

    @Override
    @Nullable
    public Component prompt() {
        return this.prompt;
    }

    @Override
    @NotNull
    public ResourcePackRequest replace(boolean replace) {
        if (replace == this.replace) {
            return this;
        }
        return new ResourcePackRequestImpl(this.packs, this.cb, replace, this.required, this.prompt);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ResourcePackRequestImpl that = (ResourcePackRequestImpl)other;
        return this.replace == that.replace && Objects.equals(this.packs, that.packs) && Objects.equals(this.cb, that.cb) && this.required == that.required && Objects.equals(this.prompt, that.prompt);
    }

    public int hashCode() {
        return Objects.hash(this.packs, this.cb, this.replace, this.required, this.prompt);
    }

    @NotNull
    public String toString() {
        return Internals.toString(this);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("packs", this.packs), ExaminableProperty.of("callback", this.cb), ExaminableProperty.of("replace", this.replace), ExaminableProperty.of("required", this.required), ExaminableProperty.of("prompt", this.prompt));
    }

    static final class BuilderImpl
    implements ResourcePackRequest.Builder {
        private List<ResourcePackInfo> packs;
        private ResourcePackCallback cb;
        private boolean replace;
        private boolean required;
        @Nullable
        private Component prompt;

        BuilderImpl() {
            this.packs = Collections.emptyList();
            this.cb = ResourcePackCallback.noOp();
            this.replace = false;
        }

        BuilderImpl(@NotNull ResourcePackRequest req) {
            this.packs = req.packs();
            this.cb = req.callback();
            this.replace = req.replace();
            this.required = req.required();
            this.prompt = req.prompt();
        }

        @Override
        @NotNull
        public ResourcePackRequest.Builder packs(@NotNull ResourcePackInfoLike first, ResourcePackInfoLike ... others) {
            this.packs = MonkeyBars.nonEmptyArrayToList(ResourcePackInfoLike::asResourcePackInfo, first, others);
            return this;
        }

        @Override
        @NotNull
        public ResourcePackRequest.Builder packs(@NotNull Iterable<? extends ResourcePackInfoLike> packs) {
            this.packs = MonkeyBars.toUnmodifiableList(ResourcePackInfoLike::asResourcePackInfo, packs);
            return this;
        }

        @Override
        @NotNull
        public ResourcePackRequest.Builder callback(@NotNull ResourcePackCallback cb) {
            this.cb = Objects.requireNonNull(cb, "cb");
            return this;
        }

        @Override
        @NotNull
        public ResourcePackRequest.Builder replace(boolean replace) {
            this.replace = replace;
            return this;
        }

        @Override
        @NotNull
        public ResourcePackRequest.Builder required(boolean required) {
            this.required = required;
            return this;
        }

        @Override
        @NotNull
        public ResourcePackRequest.Builder prompt(@Nullable Component prompt) {
            this.prompt = prompt;
            return this;
        }

        @Override
        @NotNull
        public ResourcePackRequest build() {
            return new ResourcePackRequestImpl(this.packs, this.cb, this.replace, this.required, this.prompt);
        }
    }
}

