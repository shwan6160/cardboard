/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.config.transform;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ArmorStandItemTransformType;
import org.bukkit.util.Vector;

public enum HybridItemTransformType {
    ARMORSTAND_HEAD("head \u24b6", 0.625, ArmorStandItemTransformType.HEAD, ItemDisplayMode.HEAD){

        @Override
        public Matrix4x4 transformDisplay(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(0.0, 0.25, 0.0);
            return tmp;
        }
    }
    ,
    ARMORSTAND_HEAD_SMALL("head \u24b6\u24ae", 0.4375, ArmorStandItemTransformType.SMALL_HEAD, ItemDisplayMode.HEAD){

        @Override
        public Matrix4x4 transformDisplay(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(0.0, 0.175, 0.0);
            return tmp;
        }
    }
    ,
    ARMORSTAND_RIGHT_HAND("right hand \u24b6", 1.0, ArmorStandItemTransformType.RIGHT_HAND, ItemDisplayMode.THIRD_PERSON_RIGHT_HAND){

        @Override
        public Matrix4x4 transformDisplay(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(-0.0625, 0.12575, 0.625);
            return tmp;
        }
    }
    ,
    ARMORSTAND_RIGHT_HAND_SMALL("right hand \u24b6\u24ae", 0.5, ArmorStandItemTransformType.SMALL_RIGHT_HAND, ItemDisplayMode.THIRD_PERSON_RIGHT_HAND){

        @Override
        public Matrix4x4 transformDisplay(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(-0.0315, 0.06275, 0.31225);
            tmp.worldTranslate(-0.03625, 0.19625, 0.0);
            return tmp;
        }
    }
    ,
    DISPLAY_HEAD("head \u24b9", 0.625, ArmorStandItemTransformType.HEAD, ItemDisplayMode.HEAD){

        @Override
        public Matrix4x4 transformArmorStand(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(0.0, -0.25, 0.0);
            return tmp;
        }
    }
    ,
    DISPLAY_HEAD_SMALL("head \u24b9\u24ae", 0.4375, ArmorStandItemTransformType.SMALL_HEAD, ItemDisplayMode.HEAD){

        @Override
        public Matrix4x4 transformArmorStand(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(0.0, -0.175, 0.0);
            return tmp;
        }
    }
    ,
    DISPLAY_RIGHT_HAND("right hand \u24b9", 1.0, ArmorStandItemTransformType.RIGHT_HAND, ItemDisplayMode.THIRD_PERSON_RIGHT_HAND){

        @Override
        public Matrix4x4 transformArmorStand(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.translate(-0.0625, -0.12575, 0.625);
            return tmp;
        }
    }
    ,
    DISPLAY_RIGHT_HAND_SMALL("right hand \u24b9\u24ae", 0.5, ArmorStandItemTransformType.SMALL_RIGHT_HAND, ItemDisplayMode.THIRD_PERSON_RIGHT_HAND){

        @Override
        public Matrix4x4 transformArmorStand(Matrix4x4 tmp, Matrix4x4 transform) {
            tmp.set(transform);
            tmp.worldTranslate(0.03625, -0.19625, 0.0);
            tmp.translate(-0.0315, -0.06275, 0.31225);
            return tmp;
        }
    };

    private final String name;
    private final Vector displayScale;
    private final ArmorStandItemTransformType armorStandTransform;
    private final ItemDisplayMode displayMode;

    private HybridItemTransformType(String name, double displayScale, ArmorStandItemTransformType armorStandTransform, ItemDisplayMode displayMode) {
        this.name = name;
        this.displayScale = new Vector(displayScale, displayScale, displayScale);
        this.armorStandTransform = armorStandTransform;
        this.displayMode = displayMode;
    }

    public Vector displayScale() {
        return this.displayScale;
    }

    public ItemDisplayMode displayMode() {
        return this.displayMode;
    }

    public ArmorStandItemTransformType armorStandTransform() {
        return this.armorStandTransform;
    }

    public Matrix4x4 transformArmorStand(Matrix4x4 tmp, Matrix4x4 transform) {
        return transform;
    }

    public Matrix4x4 transformDisplay(Matrix4x4 tmp, Matrix4x4 transform) {
        return transform;
    }

    public String toString() {
        return this.name;
    }
}

