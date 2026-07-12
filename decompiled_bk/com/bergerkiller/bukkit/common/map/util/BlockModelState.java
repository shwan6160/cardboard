/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.dep.gson.annotations.SerializedName;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockModelState {
    public List<Multipart> multipart;
    public Map<Condition, VariantList> variants;

    public VariantList findVariants(BlockRenderOptions options) {
        VariantList result;
        block3: {
            block2: {
                result = new VariantList();
                if (this.multipart == null) break block2;
                for (Multipart part : this.multipart) {
                    if (!part.when.has(options)) continue;
                    result.addAll(part.apply);
                }
                break block3;
            }
            if (this.variants == null) break block3;
            for (Map.Entry<Condition, VariantList> entry : this.variants.entrySet()) {
                if (!entry.getKey().has(options) || entry.getValue().isEmpty()) continue;
                result.add((Variant)entry.getValue().get(0));
            }
        }
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('{');
        if (this.multipart != null && !this.multipart.isEmpty()) {
            result.append("multipart=[");
            boolean first = true;
            for (Multipart part : this.multipart) {
                if (first) {
                    first = false;
                } else {
                    result.append(", ");
                }
                result.append(part);
            }
            result.append("]\n");
        }
        if (this.variants != null && !this.variants.isEmpty()) {
            result.append("variants={");
            for (Map.Entry<Condition, VariantList> entry : this.variants.entrySet()) {
                result.append(entry.getKey()).append(" -> ").append(entry.getValue());
                result.append('\n');
            }
            result.append("}\n");
        }
        result.append('}');
        return result.toString();
    }

    public static class VariantList
    extends ArrayList<Variant> {
        private static final long serialVersionUID = 1L;

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append('[');
            boolean first = true;
            for (Variant variant : this) {
                if (first) {
                    first = false;
                } else {
                    result.append(", ");
                }
                result.append("\n  ");
                result.append(variant.toString());
            }
            result.append("\n]");
            return result.toString();
        }
    }

    public static class Multipart {
        public Condition when = Condition.ALWAYS;
        public VariantList apply;
    }

    public static class Condition {
        public String key = null;
        public String value = null;
        public Mode mode = Mode.SELF;
        public List<Condition> conditions;
        public static final Condition ALWAYS = new Condition(){

            @Override
            public boolean has(Map<String, String> options) {
                return true;
            }
        };

        public boolean has(Map<String, String> options) {
            if (this.mode == Mode.AND) {
                if (this.conditions == null || this.conditions.isEmpty()) {
                    return false;
                }
                for (Condition condition : this.conditions) {
                    if (condition.has(options)) continue;
                    return false;
                }
                return true;
            }
            if (this.mode == Mode.OR) {
                if (this.conditions == null || this.conditions.isEmpty()) {
                    return false;
                }
                for (Condition condition : this.conditions) {
                    if (!condition.has(options)) continue;
                    return true;
                }
                return false;
            }
            if (this.key.equals("normal") && this.value.equals("")) {
                return true;
            }
            String option = options.get(this.key);
            if (option == null) {
                return false;
            }
            if (option.contains("|")) {
                for (String part : option.split("\\|")) {
                    if (!part.equals(this.value)) continue;
                    return true;
                }
                return false;
            }
            return option.equals(this.value);
        }

        public String toString() {
            if (this.mode == Mode.SELF) {
                return "{" + this.key + "=" + this.value + "}";
            }
            StringBuilder result = new StringBuilder();
            result.append("{");
            boolean first = true;
            for (Condition condition : this.conditions) {
                if (first) {
                    first = false;
                } else if (this.mode == Mode.OR) {
                    result.append(" OR ");
                } else {
                    result.append(" AND ");
                }
                result.append(condition.toString());
            }
            result.append("}");
            return result.toString();
        }

        public static enum Mode {
            SELF,
            AND,
            OR;

        }
    }

    public static class Variant {
        @SerializedName(value="model")
        public String modelName;
        @SerializedName(value="x")
        public float rotationX = 0.0f;
        @SerializedName(value="y")
        public float rotationY = 0.0f;
        @SerializedName(value="z")
        public float rotationZ = 0.0f;
        public boolean uvlock = false;

        public void update(Model model) {
            if (this.rotationX != 0.0f || this.rotationY != 0.0f || this.rotationZ != 0.0f) {
                for (Model.Element element : model.elements) {
                    if (element.transform == null) {
                        element.transform = new Matrix4x4();
                    }
                    element.transform.translate(8.0, 8.0, 8.0);
                    element.transform.rotateY(-this.rotationY);
                    element.transform.rotateX(-this.rotationX);
                    element.transform.rotateZ(-this.rotationZ);
                    element.transform.translate(-8.0, -8.0, -8.0);
                }
            }
        }

        public String toString() {
            return "{modelName=" + this.modelName + ", rotX=" + this.rotationX + ", rotY=" + this.rotationY + ", rotZ=" + this.rotationZ + ", uvlock=" + this.uvlock + "}";
        }
    }
}

