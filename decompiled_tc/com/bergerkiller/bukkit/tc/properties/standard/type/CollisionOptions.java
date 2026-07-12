/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import com.bergerkiller.bukkit.tc.CollisionMode;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionMobCategory;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class CollisionOptions {
    private static final EnumMap<CollisionMobCategory, CollisionMode> NO_MOB_MODES = new EnumMap(CollisionMobCategory.class);
    public static final CollisionOptions DEFAULT = new CollisionOptions(NO_MOB_MODES, CollisionMode.DEFAULT, CollisionMode.PUSH, CollisionMode.LINK, CollisionMode.DEFAULT);
    public static final CollisionOptions CANCEL = new CollisionOptions(NO_MOB_MODES, CollisionMode.CANCEL, CollisionMode.CANCEL, CollisionMode.CANCEL, CollisionMode.CANCEL);
    private final EnumMap<CollisionMobCategory, CollisionMode> mobModes;
    private final CollisionMode playerMode;
    private final CollisionMode miscMode;
    private final CollisionMode trainMode;
    private final CollisionMode blockMode;

    private CollisionOptions(EnumMap<CollisionMobCategory, CollisionMode> mobModes, CollisionMode playerMode, CollisionMode miscMode, CollisionMode trainMode, CollisionMode blockMode) {
        this.mobModes = mobModes;
        this.playerMode = playerMode;
        this.miscMode = miscMode;
        this.trainMode = trainMode;
        this.blockMode = blockMode;
    }

    public Map<CollisionMobCategory, CollisionMode> mobModes() {
        return Collections.unmodifiableMap(this.mobModes);
    }

    public CollisionMode mobMode(CollisionMobCategory category) {
        return this.mobModes.get((Object)category);
    }

    public CollisionMode forEntity(Entity entity) {
        if (entity instanceof Player) {
            return this.playerMode;
        }
        for (CollisionMobCategory collisionConfigObject : CollisionMobCategory.values()) {
            CollisionMode collisionMode = this.mobMode(collisionConfigObject);
            if (collisionMode == null || !collisionConfigObject.isMobType(entity)) continue;
            return collisionMode;
        }
        return this.miscMode;
    }

    public CollisionMode playerMode() {
        return this.playerMode;
    }

    public CollisionMode miscMode() {
        return this.miscMode;
    }

    public CollisionMode trainMode() {
        return this.trainMode;
    }

    public CollisionMode blockMode() {
        return this.blockMode;
    }

    public boolean collidesWithEntities() {
        if (this.playerMode != CollisionMode.CANCEL || this.trainMode != CollisionMode.CANCEL || this.miscMode != CollisionMode.CANCEL) {
            return true;
        }
        for (Map.Entry<CollisionMobCategory, CollisionMode> entry : this.mobModes.entrySet()) {
            if (entry.getValue() == CollisionMode.CANCEL) continue;
            return true;
        }
        return false;
    }

    public CollisionOptions cloneAndSetPlayerMode(CollisionMode mode) {
        if (this.playerMode == mode) {
            return this;
        }
        return new CollisionOptions(this.mobModes, mode, this.miscMode, this.trainMode, this.blockMode);
    }

    public CollisionOptions cloneAndSetMiscMode(CollisionMode mode) {
        if (this.miscMode == mode) {
            return this;
        }
        return new CollisionOptions(this.mobModes, this.playerMode, mode, this.trainMode, this.blockMode);
    }

    public CollisionOptions cloneAndSetTrainMode(CollisionMode mode) {
        if (this.trainMode == mode) {
            return this;
        }
        return new CollisionOptions(this.mobModes, this.playerMode, this.miscMode, mode, this.blockMode);
    }

    public CollisionOptions cloneAndSetBlockMode(CollisionMode mode) {
        if (this.blockMode == mode) {
            return this;
        }
        return new CollisionOptions(this.mobModes, this.playerMode, this.miscMode, this.trainMode, mode);
    }

    public CollisionOptions cloneCompareAndSetForAllMobs(CollisionMode expected, CollisionMode newModeIfExpected) {
        Object modes = this.mobModes.clone();
        if (newModeIfExpected == null) {
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                if (!category.isMobCategory() || ((EnumMap)modes).get((Object)category) != expected) continue;
                ((EnumMap)modes).remove((Object)category);
            }
        } else {
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                if (!category.isMobCategory() || ((EnumMap)modes).get((Object)category) != expected) continue;
                ((EnumMap)modes).put(category, newModeIfExpected);
            }
        }
        return new CollisionOptions((EnumMap<CollisionMobCategory, CollisionMode>)modes, this.playerMode, this.miscMode, this.trainMode, this.blockMode);
    }

    public CollisionOptions cloneAndSetForAllMobs(CollisionMode mode) {
        Object modes = this.mobModes.clone();
        if (mode == null) {
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                if (!category.isMobCategory()) continue;
                ((EnumMap)modes).remove((Object)category);
            }
        } else {
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                if (!category.isMobCategory()) continue;
                ((EnumMap)modes).put(category, mode);
            }
        }
        return new CollisionOptions((EnumMap<CollisionMobCategory, CollisionMode>)modes, this.playerMode, this.miscMode, this.trainMode, this.blockMode);
    }

    public CollisionOptions cloneAndSetMobMode(CollisionMobCategory category, CollisionMode mode) {
        EnumMap<CollisionMobCategory, CollisionMode> modes;
        if (category == null) {
            throw new IllegalArgumentException("Collision mob category can not be null");
        }
        if (this.mobModes.get((Object)category) == mode) {
            return this;
        }
        if (mode == null && this.mobModes.size() == 1 && this.mobModes.containsKey((Object)category)) {
            modes = CollisionOptions.DEFAULT.mobModes;
        } else {
            modes = this.mobModes.clone();
            if (mode == null) {
                modes.remove((Object)category);
            } else {
                modes.put(category, mode);
            }
        }
        return new CollisionOptions(modes, this.playerMode, this.miscMode, this.trainMode, this.blockMode);
    }

    public int hashCode() {
        return this.playerMode.ordinal();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CollisionOptions) {
            CollisionOptions other = (CollisionOptions)o;
            return this.playerMode == other.playerMode && this.miscMode == other.miscMode && this.trainMode == other.trainMode && this.blockMode == other.blockMode && this.mobModes.equals((Object)other.mobModes);
        }
        return false;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("CollisionConfig{");
        str.append("player=").append(this.playerMode.name());
        str.append(",misc=").append(this.miscMode.name());
        str.append(",train=").append(this.trainMode.name());
        str.append(",block=").append(this.blockMode.name());
        for (Map.Entry<CollisionMobCategory, CollisionMode> entry : this.mobModes.entrySet()) {
            str.append(',').append(entry.getKey().getMobType());
            str.append('=').append(entry.getValue().name());
        }
        str.append('}');
        return str.toString();
    }

    public static Builder builder() {
        return new Builder(DEFAULT);
    }

    public static Builder builder(CollisionOptions initial) {
        return new Builder(initial);
    }

    public static final class Builder {
        private final EnumMap<CollisionMobCategory, CollisionMode> mobModes = new EnumMap(CollisionMobCategory.class);
        private CollisionMode playerMode;
        private CollisionMode miscMode;
        private CollisionMode trainMode;
        private CollisionMode blockMode;

        private Builder(CollisionOptions initial) {
            this.mobModes.putAll(initial.mobModes());
            this.playerMode = initial.playerMode();
            this.miscMode = initial.miscMode();
            this.trainMode = initial.trainMode();
            this.blockMode = initial.blockMode();
        }

        public Builder setPlayerMode(CollisionMode mode) {
            this.playerMode = mode;
            return this;
        }

        public Builder setMiscMode(CollisionMode mode) {
            this.miscMode = mode;
            return this;
        }

        public Builder setTrainMode(CollisionMode mode) {
            this.trainMode = mode;
            return this;
        }

        public Builder setBlockMode(CollisionMode mode) {
            this.blockMode = mode;
            return this;
        }

        public Builder setMobMode(CollisionMobCategory category, CollisionMode mode) {
            if (category == null) {
                throw new IllegalArgumentException("Collision mob category cannot be null");
            }
            if (mode == null) {
                this.mobModes.remove((Object)category);
            } else {
                this.mobModes.put(category, mode);
            }
            return this;
        }

        public Builder setModeForAllMobs(CollisionMode mode) {
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                if (!category.isMobCategory()) continue;
                this.setMobMode(category, mode);
            }
            return this;
        }

        public CollisionOptions build() {
            return new CollisionOptions(this.mobModes.isEmpty() ? NO_MOB_MODES : this.mobModes, this.playerMode, this.miscMode, this.trainMode, this.blockMode);
        }
    }
}

