/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.wrappers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum EntityPose {
    STANDING(0),
    FALL_FLYING(1),
    SLEEPING(2),
    SWIMMING(3),
    SPIN_ATTACK(4),
    CROUCHING(5),
    LONG_JUMPING(6),
    DYING(7),
    CROAKING(8),
    USING_TONGUE(9),
    SITTING(10),
    ROARING(11),
    SNIFFING(12),
    EMERGING(13),
    DIGGING(14),
    SLIDING(15),
    SHOOTING(16),
    INHALING(17),
    UNKNOWN(-1);

    private final int id;
    private static final EntityPose[] BY_ID;
    private static final Map<String, EntityPose> BY_NAME;

    private EntityPose(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static EntityPose fromId(int id) {
        EntityPose[] byIdLocal = BY_ID;
        if (id >= 0 && id < byIdLocal.length) {
            return byIdLocal[id];
        }
        return UNKNOWN;
    }

    public static EntityPose fromName(String name) {
        return BY_NAME.getOrDefault(name, UNKNOWN);
    }

    static {
        BY_NAME = new HashMap<String, EntityPose>();
        int max = Stream.of(EntityPose.values()).filter(p -> p != UNKNOWN).mapToInt(EntityPose::getId).max().orElse(0);
        BY_ID = new EntityPose[max + 1];
        for (EntityPose pose : EntityPose.values()) {
            if (pose == UNKNOWN) continue;
            EntityPose.BY_ID[pose.getId()] = pose;
            BY_NAME.put(pose.name(), pose);
        }
        BY_NAME.put("SNEAKING", CROUCHING);
    }
}

