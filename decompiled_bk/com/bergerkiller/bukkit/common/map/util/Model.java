/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.dep.gson.annotations.SerializedName;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.map.util.ModelInfo;
import com.bergerkiller.bukkit.common.map.util.Quad;
import com.bergerkiller.bukkit.common.map.util.SinglePixelTexture;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.bukkit.common.wrappers.RenderOptions;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class Model
extends ModelInfo {
    private int totalQuadCount = 0;
    protected transient BuiltinType builtinType = BuiltinType.DEFAULT;
    public boolean ambientocclusion = true;
    public Map<String, Display> display = new HashMap<String, Display>();
    public Map<String, String> textures = new HashMap<String, String>();
    public List<Element> elements = new ArrayList<Element>();

    public void loadParent(Model parentModel) {
        for (Map.Entry<String, String> entry : parentModel.textures.entrySet()) {
            if (this.textures.containsKey(entry.getKey())) continue;
            this.textures.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : parentModel.display.entrySet()) {
            if (this.display.containsKey(entry.getKey())) continue;
            this.display.put(entry.getKey(), ((Display)entry.getValue()).clone());
        }
        int elementIdx = 0;
        for (Element element : parentModel.elements) {
            this.elements.add(elementIdx++, element.clone());
        }
        this.builtinType = parentModel.builtinType;
    }

    public void build(MapResourcePack resourcePack, RenderOptions options) {
        boolean hasChanges;
        this.setName(options.lookupModelName());
        int loop_limit = 100;
        String loop_last_changed = null;
        do {
            hasChanges = false;
            for (Map.Entry<String, String> textureEntry : this.textures.entrySet()) {
                String texture;
                String oldTextureValue = textureEntry.getValue();
                if (!oldTextureValue.startsWith("#") || (texture = this.textures.get(oldTextureValue.substring(1))) == null || texture.equals(oldTextureValue)) continue;
                textureEntry.setValue(texture);
                loop_last_changed = texture;
                hasChanges = true;
            }
            if (--loop_limit > 0) continue;
            if (loop_last_changed == null) break;
            Logging.LOGGER_MAPDISPLAY.warning("Texture loop error for model " + this.getName() + " texture " + loop_last_changed);
            break;
        } while (hasChanges);
        if (this.builtinType == BuiltinType.GENERATED) {
            String layerKey;
            String layerTexturePath;
            this.elements.clear();
            MapTexture result = null;
            int i = 0;
            while ((layerTexturePath = this.textures.get(layerKey = "layer" + i)) != null) {
                MapTexture texture = resourcePack.getTexture(layerTexturePath);
                texture = Model.applyTint(texture, options.get(layerKey + "tint"));
                if (result == null) {
                    result = texture.clone();
                } else {
                    result.draw(texture, 0, 0);
                }
                ++i;
            }
            if (result != null) {
                if (result.getWidth() > 16 || result.getHeight() > 16) {
                    MapTexture newTexture = MapTexture.createEmpty(16, 16);
                    for (int x = 0; x < 16; ++x) {
                        for (int y = 0; y < 16; ++y) {
                            int px = x * result.getWidth() / 16;
                            int py = y * result.getHeight() / 16;
                            newTexture.writePixel(x, y, result.readPixel(px, py));
                        }
                    }
                    result = newTexture;
                }
                for (int y = 0; y < result.getHeight(); ++y) {
                    for (int x = 0; x < result.getWidth(); ++x) {
                        byte color = result.readPixel(x, y);
                        if (color == 0) continue;
                        Element element = new Element();
                        element.from = new Vector3(x, 0.0, y);
                        element.to = new Vector3(element.from.x + 1.0, element.from.y + 1.0, element.from.z + 1.0);
                        for (BlockFace bface : FaceUtil.BLOCK_SIDES) {
                            int y2;
                            int x2;
                            if (!FaceUtil.isVertical(bface) && result.readPixel(x2 = x + bface.getModX(), y2 = y + bface.getModZ()) != 0) continue;
                            Element.Face face = new Element.Face();
                            face.texture = SinglePixelTexture.get(color);
                            element.faces.put(bface, face);
                        }
                        this.elements.add(element);
                    }
                }
            }
        }
        for (Element element : this.elements) {
            element.build(resourcePack, this.textures);
        }
    }

    public void buildBlock(RenderOptions options) {
        for (Element element : this.elements) {
            for (Element.Face face : element.faces.values()) {
                face.buildBlock(options);
            }
        }
    }

    public void buildQuads() {
        for (Element element : this.elements) {
            element.buildQuads();
        }
    }

    public List<Quad> getQuads() {
        ArrayList<Quad> result = new ArrayList<Quad>(this.totalQuadCount);
        for (Element element : this.elements) {
            for (Element.Face face : element.faces.values()) {
                result.add(face.quad.clone());
            }
        }
        this.totalQuadCount = result.size();
        return result;
    }

    public Model clone() {
        Model clone = new Model();
        clone.setName(this.getName());
        clone.ambientocclusion = this.ambientocclusion;
        clone.textures.putAll(this.textures);
        for (Map.Entry<String, Display> displayEntry : this.display.entrySet()) {
            clone.display.put(displayEntry.getKey(), displayEntry.getValue().clone());
        }
        for (Element element : this.elements) {
            clone.elements.add(element.clone());
        }
        if (this.overrides != null && !this.overrides.isEmpty()) {
            clone.overrides.addAll(this.overrides);
        }
        return clone;
    }

    private static MapTexture applyTint(MapTexture input, String tint) {
        if (tint == null || input == null) {
            return input;
        }
        try {
            byte color = MapColorPalette.getColor(Color.decode(tint));
            MapTexture result = input.clone();
            byte[] buffer = result.getBuffer();
            MapBlendMode.MULTIPLY.process(color, buffer);
            return result;
        }
        catch (NumberFormatException numberFormatException) {
            return input;
        }
    }

    public static final Model createPlaceholderModel(RenderOptions renderOptions) {
        Model model = new Model();
        Element element = new Element();
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            element.faces.put(face, Model.createPlaceholderFace());
        }
        element.buildQuads();
        model.placeholder = true;
        model.elements.add(element);
        model.setName(renderOptions.lookupModelName());
        return model;
    }

    private static final Element.Face createPlaceholderFace() {
        Element.Face face = new Element.Face();
        face.texture = Model.createPlaceholderTexture();
        return face;
    }

    public static final MapTexture createPlaceholderTexture() {
        return Model.createPlaceholderTexture(16, 16);
    }

    public static final MapTexture createPlaceholderTexture(int width, int height) {
        int wd2 = width / 2;
        int hd2 = height / 2;
        MapTexture result = MapTexture.createEmpty(width, height);
        result.fill((byte)66);
        result.fillRectangle(0, 0, wd2, hd2, (byte)50);
        result.fillRectangle(wd2, hd2, width - wd2, height - hd2, (byte)50);
        return result;
    }

    public static enum BuiltinType {
        DEFAULT,
        GENERATED;

    }

    public static class Display {
        public Vector3 rotation = new Vector3();
        public Vector3 translation = new Vector3();
        public Vector3 scale = new Vector3(1.0, 1.0, 1.0);

        public void apply(Matrix4x4 transform) {
            transform.translate(8.0, 8.0, 8.0);
            transform.rotateZ(this.rotation.z);
            transform.rotateX(this.rotation.x - 90.0);
            transform.rotateY(this.rotation.y);
            transform.scale(this.scale);
            transform.translate(-8.0, -8.0, -8.0);
            transform.translate(this.translation);
        }

        public Display clone() {
            Display clone = new Display();
            clone.rotation = this.rotation.clone();
            clone.translation = this.translation.clone();
            clone.scale = this.scale.clone();
            return clone;
        }
    }

    public static class Element {
        public Vector3 from = new Vector3(0.0, 0.0, 0.0);
        public Vector3 to = new Vector3(16.0, 16.0, 16.0);
        public Rotation rotation = null;
        public Map<BlockFace, Face> faces = new EnumMap<BlockFace, Face>(BlockFace.class);
        public transient Matrix4x4 transform = null;

        public void build(MapResourcePack resourcePack, Map<String, String> textures) {
            for (Face face : this.faces.values()) {
                face.build(resourcePack, textures);
            }
            this.buildQuads();
        }

        public void buildQuads() {
            for (Map.Entry<BlockFace, Face> entry : this.faces.entrySet()) {
                Face face = entry.getValue();
                face.quad = new Quad(entry.getKey(), this.from.clone(), this.to.clone(), face.texture);
            }
            if (this.rotation != null) {
                Matrix4x4 transform = new Matrix4x4();
                transform.translate(this.rotation.origin);
                if (this.rotation.axis.equals("x")) {
                    transform.rotateX(this.rotation.angle);
                    if (this.rotation.rescale) {
                        transform.scale(1.0, 1.0 / transform.m21, 1.0 / transform.m22);
                    }
                } else if (this.rotation.axis.equals("y")) {
                    transform.rotateY(this.rotation.angle);
                    if (this.rotation.rescale) {
                        transform.scale(1.0 / transform.m00, 1.0, 1.0 / transform.m02);
                    }
                } else if (this.rotation.axis.equals("z")) {
                    transform.rotateZ(this.rotation.angle);
                    if (this.rotation.rescale) {
                        transform.scale(1.0 / transform.m10, 1.0 / transform.m11, 1.0);
                    }
                }
                transform.translate(this.rotation.origin.negate());
                for (Face face : this.faces.values()) {
                    transform.transformQuad(face.quad);
                }
            }
            if (this.transform != null) {
                for (Face face : this.faces.values()) {
                    this.transform.transformQuad(face.quad);
                }
            }
            for (Face face : this.faces.values()) {
                for (Face otherFace : this.faces.values()) {
                    if (face == otherFace) continue;
                    face.quad.mergePoints(otherFace.quad);
                }
            }
        }

        public Element clone() {
            Element clone = new Element();
            clone.from = this.from.clone();
            clone.to = this.to.clone();
            clone.transform = this.transform == null ? null : this.transform.clone();
            clone.rotation = this.rotation == null ? null : this.rotation.clone();
            for (Map.Entry<BlockFace, Face> face : this.faces.entrySet()) {
                clone.faces.put(face.getKey(), face.getValue().clone());
            }
            return clone;
        }

        public static class Rotation
        implements Cloneable {
            public Vector3 origin = new Vector3();
            public String axis = "y";
            public float angle = 0.0f;
            public boolean rescale = false;

            public Rotation clone() {
                Rotation rotation = new Rotation();
                rotation.origin = this.origin.clone();
                rotation.axis = this.axis;
                rotation.angle = this.angle;
                rotation.rescale = this.rescale;
                return rotation;
            }
        }

        public static class Face {
            @SerializedName(value="texture")
            private String textureName = "";
            private float[] uv = null;
            public transient MapTexture texture = null;
            public int tintindex = -1;
            public int rotation = 180;
            public BlockFace cullface;
            public transient Quad quad = null;

            public void build(MapResourcePack resourcePack, Map<String, String> textures) {
                String texture;
                if (this.textureName.startsWith("#") && (texture = textures.get(this.textureName.substring(1))) != null) {
                    this.textureName = texture;
                }
                if (!this.textureName.isEmpty()) {
                    this.texture = resourcePack.getTexture(this.textureName);
                }
                if (this.uv != null) {
                    int x1 = (int)((double)this.uv[0] * (double)this.texture.getWidth() / 16.0);
                    int x2 = (int)((double)this.uv[2] * (double)this.texture.getWidth() / 16.0);
                    int y1 = (int)((double)this.uv[1] * (double)this.texture.getHeight() / 16.0);
                    int y2 = (int)((double)this.uv[3] * (double)this.texture.getHeight() / 16.0);
                    if (x2 > x1) {
                        --x2;
                    } else if (x1 > x2) {
                        --x1;
                    }
                    if (y2 > y1) {
                        --y2;
                    } else if (y1 > y2) {
                        --y1;
                    }
                    if (x1 == x2 && y1 == y2) {
                        this.texture = SinglePixelTexture.get(this.texture.readPixel(x1, y1));
                    } else {
                        int dx = x2 - x1;
                        int dy = y2 - y1;
                        MapTexture texture_uv = MapTexture.createEmpty(Math.abs(dx) + 1, Math.abs(dy) + 1);
                        byte[] buffer = texture_uv.getBuffer();
                        int sx = dx >= 1 ? 1 : -1;
                        int sy = dy >= 1 ? 1 : -1;
                        int i = 0;
                        int y = y1 - sy;
                        do {
                            y += sy;
                            int x = x1 - sx;
                            do {
                                buffer[i++] = this.texture.readPixel(x += sx, y);
                            } while (x != x2);
                        } while (y != y2);
                        this.texture = texture_uv;
                    }
                }
                if (this.rotation != 180 && (this.texture.getWidth() > 1 || this.texture.getHeight() > 1)) {
                    this.texture = MapTexture.rotate(this.texture, this.rotation + 180);
                }
            }

            public void buildBlock(RenderOptions options) {
                if (this.tintindex != -1) {
                    this.texture = Model.applyTint(this.texture, options.get("tint"));
                }
            }

            public Face clone() {
                Face clone = new Face();
                clone.uv = this.uv == null ? null : (float[])this.uv.clone();
                clone.rotation = this.rotation;
                clone.textureName = this.textureName;
                clone.texture = this.texture.clone();
                clone.cullface = this.cullface;
                clone.tintindex = this.tintindex;
                clone.quad = this.quad.clone();
                return clone;
            }
        }
    }

    @Deprecated
    public static class ModelOverride {
        private final ItemModel.Overrides.OverriddenModel overriddenModel;
        public String model;

        public ModelOverride(ItemModel.Overrides.OverriddenModel overriddenModel) {
            this.overriddenModel = overriddenModel;
            ItemModel.MinecraftModel setModel = overriddenModel.models.isEmpty() ? ItemModel.MinecraftModel.NOT_SET : overriddenModel.models.get(0);
            this.model = setModel.model;
        }

        public ModelOverride clone() {
            ModelOverride clone = new ModelOverride(this.overriddenModel);
            clone.model = this.model;
            return clone;
        }

        public String toString() {
            return this.model + "[" + this.overriddenModel.toString() + "]";
        }

        public boolean matches(RenderOptions options) {
            return options instanceof ItemRenderOptions && this.overriddenModel.isMatching(((ItemRenderOptions)options).getCommonItem());
        }

        public ItemStack applyToItem(ItemStack item) {
            CommonItemStack copy = CommonItemStack.copyOf(item);
            for (ItemModel.Overrides.PredicateCondition<?> condition : this.overriddenModel.predicate) {
                copy = condition.tryMakeMatching(copy).orElse(copy);
            }
            return copy.toBukkit();
        }
    }
}

