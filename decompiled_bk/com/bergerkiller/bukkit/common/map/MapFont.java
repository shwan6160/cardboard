/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapFont
 *  org.bukkit.map.MinecraftFont
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.MapTinyFont;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.map.MinecraftFont;

public abstract class MapFont<K> {
    private final HashMap<K, MapTexture> sprites = new HashMap();
    public static final MapFont<Character> MINECRAFT = MapFont.fromBukkitFont((org.bukkit.map.MapFont)MinecraftFont.Font);
    public static final MapFont<Character> TINY = new MapTinyFont();

    protected abstract MapTexture loadSprite(K var1);

    protected MapTexture loadNullSprite() {
        return MapTexture.createEmpty();
    }

    public final MapTexture getSprite(K key) {
        MapTexture sprite = this.sprites.get(key);
        if (sprite == null) {
            try {
                sprite = key != null ? this.loadSprite(key) : this.loadNullSprite();
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to load font sprite for character '" + key + "'", t);
            }
            if (sprite == null) {
                sprite = MapTexture.createEmpty();
            }
            this.sprites.put(key, sprite);
        }
        return sprite;
    }

    public boolean isNewline(K key) {
        return false;
    }

    public void reload() {
        this.sprites.clear();
    }

    public static MapFont<Character> fromBukkitFont(org.bukkit.map.MapFont font) {
        return new BukkitFont(font);
    }

    public static MapFont<Character> fromJavaFont(Font font) {
        return new JavaFont(font);
    }

    public static MapFont<Character> fromJavaFont(String name, int style, int size) {
        return new JavaFont(new Font(name, style, size));
    }

    public static MapFont<Character> fromJavaFont(String name, int size) {
        return new JavaFont(new Font(name, 0, size));
    }

    private static class BukkitFont
    extends MapFont<Character> {
        private final org.bukkit.map.MapFont font;

        public BukkitFont(org.bukkit.map.MapFont font) {
            this.font = font;
        }

        @Override
        protected MapTexture loadSprite(Character key) {
            return MapTexture.fromBukkitSprite(this.font.getChar(key.charValue()));
        }

        @Override
        public boolean isNewline(Character key) {
            return key != null && key.charValue() == '\n';
        }
    }

    private static class JavaFont
    extends MapFont<Character> {
        private final Font font;
        private final FontMetrics metrics;

        public JavaFont(Font font) {
            this.font = font;
            BufferedImage img = new BufferedImage(1, 1, 2);
            Graphics2D g2d = img.createGraphics();
            g2d.setFont(this.font);
            this.metrics = g2d.getFontMetrics();
        }

        @Override
        protected MapTexture loadSprite(Character key) {
            TextLayout layout = new TextLayout(key.toString(), this.metrics.getFont(), this.metrics.getFontRenderContext());
            int width = MathUtil.ceil(layout.getAdvance());
            int ascent = MathUtil.ceil(layout.getAscent());
            int height = MathUtil.ceil(layout.getDescent()) + ascent;
            if (width <= 0 || height <= 0) {
                return MapTexture.createEmpty();
            }
            BufferedImage image = new BufferedImage(width + 1, height + 1, 6);
            Graphics g = image.getGraphics();
            g.setColor(Color.WHITE);
            g.setFont(this.metrics.getFont());
            g.drawString(key.toString(), 0, ascent);
            return MapTexture.fromImage(image);
        }

        @Override
        public boolean isNewline(Character key) {
            return key != null && key.charValue() == '\n';
        }
    }

    public static enum Alignment {
        LEFT,
        MIDDLE,
        RIGHT;

    }
}

