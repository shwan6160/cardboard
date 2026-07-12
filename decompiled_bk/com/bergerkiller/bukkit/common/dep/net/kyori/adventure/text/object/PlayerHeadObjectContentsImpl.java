/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PlayerHeadObjectContentsImpl
implements PlayerHeadObjectContents {
    @Nullable
    private final String name;
    @Nullable
    private final UUID id;
    private final List<PlayerHeadObjectContents.ProfileProperty> properties;
    private final boolean hat;
    @Nullable
    private final Key texture;

    PlayerHeadObjectContentsImpl(@Nullable String name, @Nullable UUID id, @NotNull List<PlayerHeadObjectContents.ProfileProperty> properties, boolean hat, @Nullable Key texture) {
        this.name = name;
        this.id = id;
        this.properties = properties.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList(Objects.requireNonNull(properties, "properties")));
        this.hat = hat;
        this.texture = texture;
    }

    @Override
    @Nullable
    public String name() {
        return this.name;
    }

    @Override
    @Nullable
    public UUID id() {
        return this.id;
    }

    @Override
    @NotNull
    public List<PlayerHeadObjectContents.ProfileProperty> profileProperties() {
        return this.properties;
    }

    @Override
    public boolean hat() {
        return this.hat;
    }

    @Override
    @Nullable
    public Key texture() {
        return this.texture;
    }

    @Override
    @NotNull
    public PlayerHeadObjectContents.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlayerHeadObjectContents)) {
            return false;
        }
        PlayerHeadObjectContentsImpl that = (PlayerHeadObjectContentsImpl)other;
        return Objects.equals(this.name, that.name) && Objects.equals(this.id, that.id) && Objects.equals(this.properties, that.properties) && this.hat == that.hat && Objects.equals(this.texture, that.texture);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.id, this.properties, this.hat, this.texture);
    }

    public String toString() {
        return Internals.toString(this);
    }

    static final class BuilderImpl
    implements PlayerHeadObjectContents.Builder {
        @Nullable
        private String name;
        @Nullable
        private UUID id;
        private final List<PlayerHeadObjectContents.ProfileProperty> properties = new ArrayList<PlayerHeadObjectContents.ProfileProperty>();
        private boolean hat = true;
        @Nullable
        private Key texture;

        BuilderImpl() {
        }

        BuilderImpl(@NotNull PlayerHeadObjectContentsImpl playerHeadObjectContents) {
            this.name = playerHeadObjectContents.name;
            this.id = playerHeadObjectContents.id;
            this.properties.addAll(playerHeadObjectContents.properties);
            this.hat = playerHeadObjectContents.hat;
            this.texture = playerHeadObjectContents.texture;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder id(@Nullable UUID id) {
            this.id = id;
            return this;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder profileProperty(@NotNull PlayerHeadObjectContents.ProfileProperty property) {
            this.properties.add(Objects.requireNonNull(property, "property"));
            return this;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder profileProperties(@NotNull Collection<PlayerHeadObjectContents.ProfileProperty> properties) {
            for (PlayerHeadObjectContents.ProfileProperty property : Objects.requireNonNull(properties, "properties")) {
                this.profileProperty(property);
            }
            return this;
        }

        private void clearProfile() {
            this.name = null;
            this.id = null;
            this.properties.clear();
            this.texture = null;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder skin(@NotNull PlayerHeadObjectContents.SkinSource skinSource) {
            this.clearProfile();
            Objects.requireNonNull(skinSource, "skinSource").applySkinToPlayerHeadContents(this);
            return this;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder hat(boolean hat) {
            this.hat = hat;
            return this;
        }

        @Override
        public @NotNull PlayerHeadObjectContents.Builder texture(@Nullable Key texture) {
            this.texture = texture;
            return this;
        }

        @Override
        @NotNull
        public PlayerHeadObjectContents build() {
            return new PlayerHeadObjectContentsImpl(this.name, this.id, this.properties, this.hat, this.texture);
        }
    }

    static final class ProfilePropertyImpl
    implements PlayerHeadObjectContents.ProfileProperty {
        private final String name;
        private final String value;
        @Nullable
        private final String signature;

        ProfilePropertyImpl(@NotNull String name, @NotNull String value, @Nullable String signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }

        @Override
        @NotNull
        public String name() {
            return this.name;
        }

        @Override
        @NotNull
        public String value() {
            return this.value;
        }

        @Override
        @Nullable
        public String signature() {
            return this.signature;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ProfilePropertyImpl)) {
                return false;
            }
            ProfilePropertyImpl that = (ProfilePropertyImpl)other;
            return Objects.equals(this.name, that.name) && Objects.equals(this.value, that.value) && Objects.equals(this.signature, that.signature);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.value, this.signature);
        }

        public String toString() {
            return Internals.toString(this);
        }
    }
}

