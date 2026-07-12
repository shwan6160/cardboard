/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapFont$CharacterSprite
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.bukkit.map.MapFont;
import org.bukkit.plugin.java.JavaPlugin;

public final class MapTexture
extends MapCanvas {
    private final byte[] buffer;
    private final int width;
    private final int height;

    private MapTexture(int width, int height, byte[] buffer) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public byte[] getBuffer() {
        return this.buffer;
    }

    @Override
    public byte readPixel(int x, int y) {
        int index;
        if (x >= 0 && x < this.width && (index = x + y * this.width) >= 0 && index < this.buffer.length) {
            return this.buffer[index];
        }
        return 0;
    }

    @Override
    public void writePixel(int x, int y, byte color) {
        int index;
        if (x >= 0 && x < this.width && (index = x + y * this.width) >= 0 && index < this.buffer.length) {
            this.buffer[index] = color;
        }
    }

    @Override
    public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
        if (x == 0 && y == 0 && w == this.width && h == this.height) {
            System.arraycopy(colorData, 0, this.buffer, 0, w * h);
            return this;
        }
        return super.writePixels(x, y, w, h, colorData);
    }

    @Override
    public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
        int index;
        x = Math.max(x, 0);
        y = Math.max(y, 0);
        w = Math.min(w, this.width - x);
        h = Math.min(h, this.height - y);
        int res = this.width * this.height;
        int max_index = Math.min(res, index + h * this.width + w);
        if (index == 0 && max_index == res) {
            Arrays.fill(this.buffer, 0, res, color);
        } else {
            for (index = y * this.width + x; index < max_index; index += this.width) {
                Arrays.fill(this.buffer, index, index + w, color);
            }
        }
        return this;
    }

    public static MapTexture createEmpty(int width, int height) {
        return new MapTexture(width, height, new byte[width * height]);
    }

    public static MapTexture createEmpty() {
        return new MapTexture(0, 0, new byte[0]);
    }

    public static MapTexture fromRawData(int width, int height, byte[] buffer) {
        return new MapTexture(width, height, buffer);
    }

    public static MapTexture loadPluginResource(JavaPlugin plugin, String filename) throws TextureLoadException {
        MapTexture mapTexture;
        block10: {
            if (plugin == null) {
                throw new IllegalArgumentException("plugin is null");
            }
            InputStream stream = plugin.getResource(filename);
            try {
                if (stream == null) {
                    throw new TextureLoadException("Resource not found in " + plugin.getName() + ": " + filename);
                }
                mapTexture = MapTexture.fromStream(stream);
                if (stream == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new TextureLoadException("Failed to open image resource stream", e);
                }
            }
            stream.close();
        }
        return mapTexture;
    }

    public static MapTexture loadResource(Class<?> ownerClass, String filename) throws TextureLoadException {
        MapTexture mapTexture;
        block10: {
            if (ownerClass == null) {
                throw new IllegalArgumentException("ownerClass is null");
            }
            InputStream stream = ownerClass.getResourceAsStream(filename);
            try {
                if (stream == null) {
                    throw new TextureLoadException("Resource not found: " + filename);
                }
                mapTexture = MapTexture.fromStream(stream);
                if (stream == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new TextureLoadException("Failed to open image resource stream", e);
                }
            }
            stream.close();
        }
        return mapTexture;
    }

    public static MapTexture loadResource(URL imageResourceURL) throws TextureLoadException {
        MapTexture mapTexture;
        block9: {
            InputStream stream = imageResourceURL.openStream();
            try {
                if (stream == null) {
                    throw new TextureLoadException("Resource not found: " + imageResourceURL.toString());
                }
                mapTexture = MapTexture.fromStream(stream);
                if (stream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new TextureLoadException("Failed to open image resource stream", e);
                }
            }
            stream.close();
        }
        return mapTexture;
    }

    public static MapTexture fromImageFile(String filePath) throws TextureLoadException {
        MapTexture mapTexture;
        FileInputStream stream = new FileInputStream(filePath);
        try {
            mapTexture = MapTexture.fromStream(stream);
        }
        catch (Throwable throwable) {
            try {
                try {
                    stream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new TextureLoadException("Failed to open image file", e);
            }
        }
        stream.close();
        return mapTexture;
    }

    public static MapTexture fromStream(InputStream imageStream) throws TextureLoadException {
        BufferedImage image = ImageIO.read(imageStream);
        try {
            MapTexture mapTexture = MapTexture.fromImage(image);
            image.flush();
            return mapTexture;
        }
        catch (Throwable throwable) {
            try {
                image.flush();
                throw throwable;
            }
            catch (IOException e) {
                throw new TextureLoadException("Failed to load image from stream", e);
            }
        }
    }

    public static MapTexture fromImage(Image image) {
        return new MapTexture(image.getWidth(null), image.getHeight(null), MapColorPalette.convertImage(image));
    }

    public static MapTexture fromBukkitSprite(MapFont.CharacterSprite sprite) {
        if (sprite == null) {
            return MapTexture.createEmpty();
        }
        MapTexture texture = MapTexture.createEmpty(sprite.getWidth() + 1, sprite.getHeight());
        for (int dx = 0; dx < sprite.getWidth(); ++dx) {
            for (int dy = 0; dy < sprite.getHeight(); ++dy) {
                texture.writePixel(dx, dy, sprite.get(dy, dx) ? (byte)34 : 0);
            }
        }
        return texture;
    }

    public static MapTexture flipH(MapCanvas input) {
        MapTexture result = MapTexture.createEmpty(input.getWidth(), input.getHeight());
        for (int x = 0; x < result.getWidth(); ++x) {
            for (int y = 0; y < result.getHeight(); ++y) {
                result.writePixel(x, y, input.readPixel(result.getWidth() - x - 1, y));
            }
        }
        return result;
    }

    public static MapTexture flipV(MapCanvas input) {
        MapTexture result = MapTexture.createEmpty(input.getWidth(), input.getHeight());
        for (int x = 0; x < result.getWidth(); ++x) {
            for (int y = 0; y < result.getHeight(); ++y) {
                result.writePixel(x, y, input.readPixel(x, result.getHeight() - y - 1));
            }
        }
        return result;
    }

    public static MapTexture rotate(MapCanvas input, int angle) {
        MapTexture result;
        if (MathUtil.getAngleDifference(angle, 90) <= 45) {
            result = MapTexture.createEmpty(input.getHeight(), input.getWidth());
            for (int x = 0; x < result.getWidth(); ++x) {
                for (int y = 0; y < result.getHeight(); ++y) {
                    result.writePixel(x, result.getHeight() - y - 1, input.readPixel(y, x));
                }
            }
        } else if (MathUtil.getAngleDifference(angle, 180) <= 45) {
            result = MapTexture.createEmpty(input.getWidth(), input.getHeight());
            for (int x = 0; x < result.getWidth(); ++x) {
                for (int y = 0; y < result.getHeight(); ++y) {
                    result.writePixel(x, y, input.readPixel(result.getWidth() - x - 1, result.getHeight() - y - 1));
                }
            }
        } else if (MathUtil.getAngleDifference(angle, 270) <= 45) {
            result = MapTexture.createEmpty(input.getHeight(), input.getWidth());
            for (int x = 0; x < result.getWidth(); ++x) {
                for (int y = 0; y < result.getHeight(); ++y) {
                    result.writePixel(result.getWidth() - x - 1, y, input.readPixel(y, x));
                }
            }
        } else {
            result = input.clone();
        }
        return result;
    }

    public static MapTexture resize(MapCanvas input, int newWidth, int newHeight) {
        MapTexture result = MapTexture.createEmpty(newWidth, newHeight);
        MapColorPalette.resizeCopy(input.getBuffer(), input.getWidth(), input.getHeight(), result.getBuffer(), newWidth, newHeight);
        return result;
    }

    public static final class TextureLoadException
    extends RuntimeException {
        private static final long serialVersionUID = 7530009132906543361L;

        public TextureLoadException(String message) {
            super(message);
        }

        public TextureLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

