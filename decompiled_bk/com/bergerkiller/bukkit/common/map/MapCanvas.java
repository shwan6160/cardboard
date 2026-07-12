/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.collections.CharacterIterable;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Quad;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.math.Vector3;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public abstract class MapCanvas {
    private int fontSpacing = 0;
    private MapFont.Alignment fontAlignment = MapFont.Alignment.LEFT;
    private MapBlendMode blendMode = MapBlendMode.OVERLAY;
    private short[] depthBuffer = null;
    private byte[] maskBuffer = null;
    private int mask_w;
    private int mask_h;
    private boolean maskRelative = false;
    private short currentDepthZ = 0;
    private boolean hasDepthHoles = false;
    private Matrix4x4 projMatrix = null;
    private Vector3 directionalLightVec = null;
    private float directionalLightFact = 0.0f;
    private float ambientLightFact = 1.0f;
    public static final int MAX_DEPTH = Short.MAX_VALUE;

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract byte[] getBuffer();

    public abstract byte readPixel(int var1, int var2);

    public abstract void writePixel(int var1, int var2, byte var3);

    public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
        if (x == 0 && y == 0 && w == this.getWidth() && h == this.getHeight()) {
            System.arraycopy(this.getBuffer(), 0, dst_buffer, 0, dst_buffer.length);
        } else {
            byte[] src_buffer = this.getBuffer();
            int src_w = this.getWidth();
            int src_h = this.getHeight();
            int src_y = y;
            for (int dst_y = 0; dst_y < h && src_y < src_h; ++src_y, ++dst_y) {
                if (src_y < 0) continue;
                int src_offset = src_y * src_w;
                int dst_offset = dst_y * w;
                int src_x = x;
                for (int dst_x = 0; dst_x < w && src_x < src_w; ++src_x, ++dst_x) {
                    if (src_x < 0) continue;
                    dst_buffer[dst_offset + dst_x] = src_buffer[src_offset + src_x];
                }
            }
        }
        return dst_buffer;
    }

    public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
        int w_end = x + w;
        int px = x;
        int py = y;
        int pixel_count = Math.min(colorData.length, w * h);
        for (int i = 0; i < pixel_count; ++i) {
            this.writePixel(px, py, colorData[i]);
            if (++px < w_end) continue;
            px = x;
            ++py;
        }
        return this;
    }

    public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
        for (int dx = 0; dx < w; ++dx) {
            for (int dy = 0; dy < h; ++dy) {
                this.writePixel(x + dx, y + dy, color);
            }
        }
        return this;
    }

    public final MapCanvas drawRawData(int x, int y, int w, int h, byte[] colorData) {
        return this.drawRawData(x, y, w, h, colorData, (byte)0);
    }

    public final MapCanvas drawRawData(int x, int y, int w, int h, byte[] colorData, byte colorFactor) {
        if (colorData == null && this.depthBuffer == null && this.depthBuffer == null && this.maskBuffer == null && !this.blendMode.inputColorUsesOutput(colorFactor)) {
            return this.writePixelsFill(x, y, w, h, colorFactor);
        }
        if (colorData == null) {
            if (w <= 0 || h <= 0) {
                return this;
            }
            colorData = new byte[w * h];
            Arrays.fill(colorData, colorFactor);
        } else if (colorFactor != 0) {
            colorData = (byte[])colorData.clone();
            MapBlendMode.MULTIPLY.process(colorFactor, colorData);
        }
        if (this.depthBuffer == null) {
            if (this.blendMode == MapBlendMode.NONE && this.maskBuffer == null) {
                return this.writePixels(x, y, w, h, colorData);
            }
            byte[] pixels = this.readPixels(x, y, w, h);
            if (this.maskBuffer != null) {
                byte[] pixels_old = (byte[])pixels.clone();
                this.blendMode.process(colorData, pixels);
                int colorIndex = 0;
                int maskIndex = 0;
                block0: for (int dy = 0; dy < h; ++dy) {
                    int maskEndIndex;
                    if (dy >= this.mask_h) {
                        System.arraycopy(pixels_old, colorIndex, pixels, colorIndex, w);
                        colorIndex += w;
                        continue;
                    }
                    if (this.maskRelative) {
                        maskIndex = dy * this.mask_w;
                        maskEndIndex = maskIndex + this.mask_w;
                    } else {
                        maskIndex = (y + dy) * this.mask_w;
                        maskEndIndex = maskIndex + this.mask_w;
                        maskIndex += x;
                    }
                    for (int dx = 0; dx < w; ++dx) {
                        if (maskIndex >= maskEndIndex) {
                            System.arraycopy(pixels_old, colorIndex, pixels, colorIndex, w - dx);
                            colorIndex += w - dx;
                            continue block0;
                        }
                        if (this.maskBuffer[maskIndex] == 0) {
                            pixels[colorIndex] = pixels_old[colorIndex];
                        }
                        ++maskIndex;
                        ++colorIndex;
                    }
                }
            } else {
                this.blendMode.process(colorData, pixels);
            }
            return this.writePixels(x, y, w, h, pixels);
        }
        int colorDataIdx = -1;
        for (int dy = 0; dy < h; ++dy) {
            for (int dx = 0; dx < w; ++dx) {
                int depthIndex;
                short depth;
                ++colorDataIdx;
                int px = x + dx;
                int py = y + dy;
                if (this.maskBuffer != null && (!this.maskRelative ? px >= this.mask_w || py >= this.mask_h || this.maskBuffer[py * this.mask_w + px] == 0 : dx >= this.mask_w || dy >= this.mask_h || this.maskBuffer[dy * this.mask_w + dx] == 0)) continue;
                if (px < 0 || py < 0 || px >= this.getWidth() || py >= this.getHeight() || this.currentDepthZ > (depth = this.depthBuffer[depthIndex = px + this.getWidth() * py])) continue;
                byte color = colorData[colorDataIdx];
                if (this.currentDepthZ == depth) {
                    if (color == 0) {
                        this.writePixel(px, py, (byte)0);
                        this.depthBuffer[depthIndex] = Short.MAX_VALUE;
                        this.hasDepthHoles = true;
                        continue;
                    }
                    this.writePixel(px, py, color);
                    continue;
                }
                if (color != 0) {
                    this.writePixel(px, py, color);
                    this.depthBuffer[depthIndex] = this.currentDepthZ;
                    continue;
                }
                if (depth != Short.MAX_VALUE) continue;
                this.hasDepthHoles = true;
            }
        }
        return this;
    }

    public final byte[] readPixels() {
        int w = this.getWidth();
        int h = this.getHeight();
        return this.readPixels(0, 0, w, h, new byte[w * h]);
    }

    public final byte[] readPixels(int x, int y, int w, int h) {
        return this.readPixels(x, y, w, h, new byte[w * h]);
    }

    public final MapCanvas drawCopy(MapCanvas canvas, int x, int y) {
        return this.writePixels(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer());
    }

    public final MapCanvas draw(MapCanvas canvas, int x, int y) {
        return this.drawRawData(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer());
    }

    public final MapCanvas draw(MapCanvas canvas, int x, int y, byte colorFactor) {
        return this.drawRawData(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer(), colorFactor);
    }

    public final MapCanvas remap(byte[] remappingColors) {
        byte[] pixels = this.readPixels();
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = remappingColors[pixels[i]];
        }
        return this.writePixels(0, 0, this.getWidth(), this.getHeight(), pixels);
    }

    public final MapCanvas drawMove(MapCanvas canvas, int old_x, int old_y, int new_x, int new_y) {
        this.clearRectangle(old_x, old_y, canvas.getWidth(), canvas.getHeight());
        return this.draw(canvas, new_x, new_y);
    }

    public final MapCanvas movePixels(int dx, int dy) {
        byte[] oldPixels = this.getBuffer();
        short[] oldDepthBuffer = this.depthBuffer;
        byte[] newPixels = new byte[oldPixels.length];
        short[] newDepthBuffer = null;
        if (this.depthBuffer != null) {
            newDepthBuffer = new short[this.depthBuffer.length];
            Arrays.fill(newDepthBuffer, (short)Short.MAX_VALUE);
        }
        int colorIndex = 0;
        int dstIndex = 0;
        for (int y = 0; y < this.getHeight(); ++y) {
            for (int x = 0; x < this.getWidth(); ++x) {
                int sx = x + dx;
                int sy = y + dy;
                if (sx < 0 || sy < 0 || sx >= this.getWidth() || sy >= this.getHeight()) {
                    ++colorIndex;
                    continue;
                }
                dstIndex = sy * this.getWidth() + sx;
                newPixels[dstIndex] = oldPixels[colorIndex];
                if (newDepthBuffer != null) {
                    newDepthBuffer[dstIndex] = oldDepthBuffer[colorIndex];
                }
                ++colorIndex;
            }
        }
        this.writePixels(0, 0, this.getWidth(), this.getHeight(), newPixels);
        this.depthBuffer = newDepthBuffer;
        return this;
    }

    public final MapCanvas clearRectangle(int x, int y, int w, int h) {
        MapBlendMode oldMode = this.blendMode;
        this.blendMode = MapBlendMode.NONE;
        this.fillRectangle(x, y, w, h, (byte)0);
        this.blendMode = oldMode;
        return this;
    }

    public final MapCanvas fillRectangle(int x, int y, int w, int h, byte color) {
        return this.drawRawData(x, y, w, h, null, color);
    }

    public final MapCanvas clear() {
        MapBlendMode oldMode = this.blendMode;
        this.blendMode = MapBlendMode.NONE;
        this.fill((byte)0);
        this.blendMode = oldMode;
        return this;
    }

    public final MapCanvas fill(byte color) {
        return this.fillRectangle(0, 0, this.getWidth(), this.getHeight(), color);
    }

    public final MapCanvas setLightOptions(float ambientLightFactor, float directionalLightFactor, Vector3 directionVector) {
        this.ambientLightFact = ambientLightFactor;
        this.directionalLightFact = directionVector == null ? 0.0f : directionalLightFactor;
        this.directionalLightVec = directionVector == null ? null : directionVector.normalize();
        return this;
    }

    public final MapCanvas setBrushMask(MapCanvas mask) {
        if (mask == null) {
            this.maskBuffer = null;
        } else {
            this.maskBuffer = mask.getBuffer();
            this.mask_w = mask.getWidth();
            this.mask_h = mask.getHeight();
            this.maskRelative = false;
        }
        return this;
    }

    public final MapCanvas setRelativeBrushMask(MapCanvas mask) {
        if (mask == null) {
            this.maskBuffer = null;
        } else {
            this.maskBuffer = mask.getBuffer();
            this.mask_w = mask.getWidth();
            this.mask_h = mask.getHeight();
            this.maskRelative = true;
        }
        return this;
    }

    public final MapCanvas clearDepthBuffer() {
        if (this.depthBuffer != null) {
            Arrays.fill(this.depthBuffer, (short)Short.MAX_VALUE);
        }
        return this;
    }

    public final MapCanvas clearDepthBuffer(int x, int y, int width, int height) {
        if (this.depthBuffer != null) {
            if (x >= this.getWidth() || y >= this.getHeight()) {
                return this;
            }
            if (x < 0) {
                width += x;
                x = 0;
            }
            if (y < 0) {
                height += y;
                y = 0;
            }
            int maxWidth = this.getWidth() - x;
            int maxHeight = this.getHeight() - y;
            if (width > maxWidth) {
                width = maxWidth;
            }
            if (height > maxHeight) {
                height = maxHeight;
            }
            if (width <= 0 || height <= 0) {
                return this;
            }
            int fromIndex = x + y * this.getWidth();
            for (int dy = 0; dy < height; ++dy) {
                Arrays.fill(this.depthBuffer, fromIndex, fromIndex + width, (short)Short.MAX_VALUE);
                fromIndex += this.getWidth();
            }
        }
        return this;
    }

    public final MapCanvas setDrawDepth(int depth) {
        this.currentDepthZ = (short)depth;
        this.hasDepthHoles = false;
        if (this.depthBuffer == null) {
            this.depthBuffer = new short[this.getWidth() * this.getHeight()];
            this.clearDepthBuffer();
        }
        return this;
    }

    public final boolean hasMoreDepth() {
        if (!this.hasDepthHoles) {
            for (short d : this.depthBuffer) {
                if (d != Short.MAX_VALUE) continue;
                this.hasDepthHoles = true;
                break;
            }
        }
        return this.hasDepthHoles;
    }

    public final int getDepth(int x, int y) {
        if (this.depthBuffer == null) {
            return Short.MAX_VALUE;
        }
        return this.depthBuffer[y * this.getWidth() + x];
    }

    public final short[] getDepthBuffer() {
        return this.depthBuffer;
    }

    public final boolean hasMoreDepth(int x, int y, int width, int height) {
        if (this.depthBuffer == null) {
            return true;
        }
        if (x >= this.getWidth() || y >= this.getHeight()) {
            return false;
        }
        if (x < 0) {
            width += x;
            x = 0;
        }
        if (y < 0) {
            height += y;
            y = 0;
        }
        if (width <= 0 || height <= 0) {
            return false;
        }
        int limWidth = this.getWidth() - x;
        int limHeight = this.getHeight() - y;
        if (width > limWidth) {
            width = limWidth;
        }
        if (height > limHeight) {
            height = limHeight;
        }
        int depthIndexBase = y * this.getWidth();
        for (int dy = 0; dy < height; ++dy) {
            int depthIndex = depthIndexBase + x;
            for (int dx = 0; dx < width; ++dx) {
                if (this.depthBuffer[depthIndex++] != Short.MAX_VALUE) continue;
                return true;
            }
            depthIndexBase += this.getWidth();
        }
        return false;
    }

    public final MapCanvas setSpacing(int spacing) {
        this.fontSpacing = spacing;
        return this;
    }

    public final MapCanvas setAlignment(MapFont.Alignment alignment) {
        this.fontAlignment = alignment;
        return this;
    }

    public final MapFont.Alignment getAlignment() {
        return this.fontAlignment;
    }

    public final MapCanvas setBlendMode(MapBlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }

    public final MapBlendMode getBlendMode() {
        return this.blendMode;
    }

    public final <T> MapCanvas drawRectangle(int x, int y, int width, int height, byte color) {
        this.drawLine(x, y, x, y + height - 1, color);
        this.drawLine(x, y, x + width - 1, y, color);
        this.drawLine(x + width - 1, y, x + width - 1, y + height - 1, color);
        this.drawLine(x, y + height - 1, x + width - 1, y + height - 1, color);
        return this;
    }

    public final MapCanvas drawPixel(int x, int y, byte color) {
        byte oldColor = this.readPixel(x, y);
        byte newColor = this.blendMode.process(color, oldColor);
        this.writePixel(x, y, newColor);
        return this;
    }

    public final MapCanvas drawContour(Collection<Point> points, byte color) {
        if (!points.isEmpty()) {
            Point first = null;
            Point last = null;
            for (Point point : points) {
                if (first == null) {
                    first = point;
                } else {
                    this.drawLine(last, point, color);
                }
                last = point;
            }
            this.drawLine(first, last, color);
        }
        return this;
    }

    public final MapCanvas drawLine(Point p1, Point p2, byte color) {
        return this.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY(), color);
    }

    public final MapCanvas drawLine(int x1, int y1, int x2, int y2, byte color) {
        int c;
        boolean steep;
        if (x1 == x2) {
            return this.fillRectangle(x1, Math.min(y1, y2), 1, Math.abs(y2 - y1) + 1, color);
        }
        if (y1 == y2) {
            return this.fillRectangle(Math.min(x1, x2), y1, Math.abs(x2 - x1) + 1, 1, color);
        }
        int dx = x2 - x1;
        int dy = y2 - y1;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        boolean bl = steep = ady > adx;
        if (steep) {
            c = x1;
            x1 = y1;
            y1 = c;
            c = x2;
            x2 = y2;
            y2 = c;
            c = adx;
            adx = ady;
            ady = c;
        }
        if (x1 > x2) {
            c = x1;
            x1 = x2;
            x2 = c;
            c = y1;
            y1 = y2;
            y2 = c;
        }
        int err = adx;
        int ystep = y1 < y2 ? 1 : -1;
        adx += adx;
        ady += ady;
        if (steep) {
            while (x1 <= x2) {
                this.drawPixel(y1, x1, color);
                if ((err -= ady) < 0) {
                    y1 += ystep;
                    err += adx;
                }
                ++x1;
            }
        } else {
            while (x1 <= x2) {
                this.drawPixel(x1, y1, color);
                if ((err -= ady) < 0) {
                    y1 += ystep;
                    err += adx;
                }
                ++x1;
            }
        }
        return this;
    }

    public final <T> Dimension calcFontSize(MapFont<T> font, T ... characters) {
        if (characters == null) {
            throw new IllegalArgumentException("Null characters array specified");
        }
        return this.calcFontSize(font, (Iterable<T>)Arrays.asList(characters));
    }

    public final Dimension calcFontSize(MapFont<Character> font, CharSequence characters) {
        if (characters == null) {
            throw new IllegalArgumentException("Null String specified");
        }
        return this.calcFontSize((MapFont<T>)font, new CharacterIterable(characters));
    }

    public final <T> Dimension calcFontSize(MapFont<T> font, Iterable<T> characters) {
        if (characters == null) {
            throw new IllegalArgumentException("Null Character Iterable specified");
        }
        int maxWidth = 0;
        int height = 0;
        int currWidth = 0;
        int totalHeight = 0;
        boolean first = false;
        for (T character : characters) {
            MapTexture sprite = font.getSprite(character);
            if (font.isNewline(character)) {
                height = Math.max(height, sprite.getHeight());
                totalHeight += height + this.fontSpacing;
                currWidth = 0;
                first = true;
                height = 0;
                continue;
            }
            if (first) {
                first = false;
            } else {
                currWidth += this.fontSpacing;
            }
            maxWidth = Math.max(maxWidth, currWidth += sprite.getWidth());
            height = Math.max(height, sprite.getHeight());
        }
        return new Dimension(maxWidth, totalHeight += height);
    }

    @SafeVarargs
    public final <T> MapCanvas draw(MapFont<T> font, int x, int y, T ... characters) {
        if (characters == null) {
            throw new IllegalArgumentException("Null characters array specified");
        }
        return this.draw(font, x, y, (byte)0, (Iterable<T>)Arrays.asList(characters));
    }

    public final MapCanvas draw(MapFont<Character> font, int x, int y, byte color, CharSequence characters) {
        if (characters == null) {
            throw new IllegalArgumentException("Null String characters specified");
        }
        return this.draw((MapFont<T>)font, x, y, color, new CharacterIterable(characters));
    }

    public final <T> MapCanvas draw(MapFont<T> font, int x, int y, byte color, Iterable<T> characters) {
        if (characters == null) {
            throw new IllegalArgumentException("Null characters Iterable specified");
        }
        if (this.fontAlignment == MapFont.Alignment.RIGHT) {
            x -= this.calcFontSize(font, characters).width;
        } else if (this.fontAlignment == MapFont.Alignment.MIDDLE) {
            x -= this.calcFontSize(font, characters).width / 2;
        }
        int height = 0;
        int start_x = x;
        for (T character : characters) {
            MapTexture sprite = font.getSprite(character);
            if (font.isNewline(character)) {
                height = Math.max(height, sprite.getHeight());
                y += height + this.fontSpacing;
                x = start_x;
                height = 0;
                continue;
            }
            this.draw(sprite, x, y, color);
            x += sprite.getWidth() + this.fontSpacing;
            height = Math.max(height, sprite.getHeight());
        }
        return this;
    }

    public final MapCanvas drawModel(Model model, float scale, int x, int y, float yaw, float pitch) {
        Matrix4x4 transform = new Matrix4x4();
        transform.translate(x, 0.0, y);
        transform.scale(scale);
        transform.rotateX(pitch);
        transform.rotateY(yaw);
        return this.drawModel(model, transform);
    }

    public final MapCanvas drawItem(MapResourcePack resourcePack, ItemStack item, int x, int y, int width, int height) {
        return this.draw(resourcePack.getItemTexture(item, width, height), x, y);
    }

    public final MapCanvas fillItem(MapResourcePack resourcePack, ItemStack item) {
        return this.drawItem(resourcePack, item, 0, 0, this.getWidth(), this.getHeight());
    }

    public final MapCanvas drawModel(Model model, Matrix4x4 transform) {
        if (model == null) {
            throw new IllegalArgumentException("Model is null");
        }
        List<Quad> quads = model.getQuads();
        for (Quad quad : quads) {
            transform.transformQuad(quad);
        }
        Collections.sort(quads);
        for (Quad quad : quads) {
            this.drawQuad(quad);
        }
        return this;
    }

    public final MapCanvas drawQuad(Quad quad) {
        return this.drawQuad(quad.texture, quad.p0, quad.p1, quad.p2, quad.p3);
    }

    public final MapCanvas drawQuad(MapCanvas canvas, Vector3 p0, Vector3 p1, Vector3 p2, Vector3 p3) {
        Vector3 fp3 = Vector3.add(p2, Vector3.subtract(p0, p1));
        if (fp3.distanceSquared(p3) > 1.0E-4) {
            this.drawQuad(canvas, p0, p1, p2, fp3, 1);
            Vector3 fp1 = Vector3.add(p2, Vector3.subtract(p0, p3));
            this.drawQuad(canvas, p0, fp1, p2, p3, -1);
        } else {
            this.drawQuad(canvas, p0, p1, p2, p3, 0);
        }
        return this;
    }

    public final MapCanvas drawQuad(MapCanvas canvas, Matrix4x4 projectionMatrix) {
        return this.drawQuad(canvas, projectionMatrix, 0);
    }

    private final MapCanvas drawQuad(MapCanvas canvas, Vector3 p0, Vector3 p1, Vector3 p2, Vector3 p3, int half) {
        Matrix4x4 m0;
        if (canvas.projMatrix == null) {
            Vector3 ip0 = new Vector3(0.0, 0.0, 0.0);
            Vector3 ip1 = new Vector3(0.0, 0.0, canvas.getHeight());
            Vector3 ip2 = new Vector3(canvas.getWidth(), 0.0, canvas.getHeight());
            Vector3 ip3 = new Vector3(canvas.getWidth(), 0.0, 0.0);
            canvas.projMatrix = Matrix4x4.computeProjectionMatrix(new Vector3[]{ip0, ip1, ip2, ip3});
            if (canvas.projMatrix == null) {
                return this;
            }
            canvas.projMatrix.invert();
        }
        if ((m0 = Matrix4x4.computeProjectionMatrix(new Vector3[]{p0, p1, p2, p3})) == null) {
            return this;
        }
        m0.multiply(canvas.projMatrix);
        return this.drawQuad(canvas, m0, half);
    }

    private final MapCanvas drawQuad(MapCanvas canvas, Matrix4x4 projectionMatrix, int half) {
        MapTexture temp;
        int minY;
        int minX;
        block12: {
            float light;
            int maxY;
            int maxX;
            Matrix4x4 mInv;
            block11: {
                Vector3[] corners;
                mInv = new Matrix4x4(projectionMatrix);
                mInv.invert();
                for (Vector3 corner : corners = new Vector3[]{new Vector3(0.0, 0.0, 0.0), new Vector3(0.0, 0.0, canvas.getHeight()), new Vector3(canvas.getWidth(), 0.0, canvas.getHeight()), new Vector3(canvas.getWidth(), 0.0, 0.0)}) {
                    projectionMatrix.transformPoint(corner);
                }
                Vector3 v1 = Vector3.subtract(corners[0], corners[1]);
                Vector3 v2 = Vector3.subtract(corners[2], corners[1]);
                Vector3 cross = Vector3.cross(v1, v2).normalize();
                if (cross.y < 0.0) {
                    cross = cross.negate();
                }
                minX = Integer.MAX_VALUE;
                minY = Integer.MAX_VALUE;
                maxX = Integer.MIN_VALUE;
                maxY = Integer.MIN_VALUE;
                for (Vector3 corner : corners) {
                    int cx = (int)corner.x;
                    int cy = (int)corner.z;
                    if (cx > maxX) {
                        maxX = cx;
                    }
                    if (cx < minX) {
                        minX = cx;
                    }
                    if (cy > maxY) {
                        maxY = cy;
                    }
                    if (cy >= minY) continue;
                    minY = cy;
                }
                temp = MapTexture.createEmpty(maxX - minX + 1, maxY - minY + 1);
                light = this.ambientLightFact;
                if (this.directionalLightVec != null) {
                    double dot = Vector3.dot(cross.normalize(), this.directionalLightVec);
                    dot = (dot + 1.0) / 2.0;
                    light = (float)((double)light + (double)this.directionalLightFact * dot);
                }
                if (canvas.getWidth() != 1 || canvas.getHeight() != 1) break block11;
                byte color = canvas.readPixel(0, 0);
                if (color == 0) break block12;
                color = MapColorPalette.getSpecular(color, light);
                Vector3 p = new Vector3();
                for (int y = minY; y <= maxY; ++y) {
                    for (int x = minX; x <= maxX; ++x) {
                        p.x = x;
                        p.z = y;
                        p.y = 1.0;
                        mInv.transformPoint(p);
                        double ax = p.x;
                        double ay = p.z;
                        if (half > 0 && ax > ay || half < 0 && ax <= ay || !(ax >= 0.0) || !(ay >= 0.0) || !(ax < 1.0) || !(ay < 1.0)) continue;
                        temp.writePixel(x - minX, y - minY, color);
                    }
                }
                break block12;
            }
            Vector3 p = new Vector3();
            for (int y = minY; y <= maxY; ++y) {
                for (int x = minX; x <= maxX; ++x) {
                    byte color;
                    p.x = x;
                    p.z = y;
                    p.y = 1.0;
                    mInv.transformPoint(p);
                    double ax = p.x;
                    double ay = p.z;
                    if (half > 0 && ax > ay || half < 0 && ax <= ay || !(ax >= 0.0) || !(ay >= 0.0) || !(ax <= (double)canvas.getWidth()) || !(ay <= (double)canvas.getHeight()) || (color = canvas.readPixel((int)ax, (int)ay)) == 0) continue;
                    color = MapColorPalette.getSpecular(color, light);
                    temp.writePixel(x - minX, y - minY, color);
                }
            }
        }
        return this.draw(temp, minX, minY);
    }

    public final MapCanvas getClip(int x, int y, int w, int h) {
        if (x <= 0 && y <= 0 && x + w >= this.getWidth() && y + h >= this.getHeight()) {
            return this;
        }
        return new Clip(this, x, y, w, h);
    }

    public MapCanvas getView(int x, int y, int w, int h) {
        if (x == 0 && y == 0 && w == this.getWidth() && h == this.getHeight()) {
            return this;
        }
        return new View(this, x, y, w, h);
    }

    public final MapCanvas getView(int offset_x, int offset_y) {
        return this.getView(offset_x, offset_y, this.getWidth() - offset_x, this.getHeight() - offset_y);
    }

    public int getViewAbsoluteX() {
        return 0;
    }

    public int getViewAbsoluteY() {
        return 0;
    }

    public boolean isSameView(MapCanvas otherView) {
        return otherView == this;
    }

    public final MapTexture clone() {
        return MapTexture.fromRawData(this.getWidth(), this.getHeight(), this.readPixels());
    }

    public final BufferedImage toJavaImage() {
        BufferedImage result = new BufferedImage(this.getWidth(), this.getHeight(), 2);
        int[] buffer = ((DataBufferInt)result.getRaster().getDataBuffer()).getData();
        byte[] data = this.readPixels();
        int index = 0;
        for (int y = 0; y < this.getHeight(); ++y) {
            for (int x = 0; x < this.getWidth(); ++x) {
                buffer[index] = MapColorPalette.getRealColor(data[index]).getRGB();
                ++index;
            }
        }
        return result;
    }

    public final BufferedImage toJavaImageIndexed() {
        byte[] buffer = this.readPixels();
        int width = this.getWidth();
        int height = this.getHeight();
        DataBufferByte data = new DataBufferByte(buffer, buffer.length);
        WritableRaster raster = Raster.createInterleavedRaster(data, width, height, width, 1, new int[]{0}, null);
        return new BufferedImage(MapColorPalette.getIndexColorModel(), raster, false, null);
    }

    private static final class Clip
    extends MapCanvas {
        private final MapCanvas parent;
        private final int x0;
        private final int y0;
        private final int x1;
        private final int y1;
        private final int xAbs;
        private final int yAbs;

        public Clip(MapCanvas parent, int x, int y, int w, int h) {
            this.parent = parent;
            this.x0 = x;
            this.y0 = y;
            this.x1 = x + w;
            this.y1 = y + h;
            this.xAbs = parent.getViewAbsoluteX();
            this.yAbs = parent.getViewAbsoluteY();
        }

        @Override
        public int getViewAbsoluteX() {
            return this.xAbs;
        }

        @Override
        public int getViewAbsoluteY() {
            return this.yAbs;
        }

        @Override
        public boolean isSameView(MapCanvas otherView) {
            if (otherView == this) {
                return true;
            }
            if (otherView instanceof Clip) {
                Clip c = (Clip)otherView;
                return this.parent.isSameView(c.parent) && this.x0 == c.x0 && this.y0 == c.y0 && this.x1 == c.x1 && this.y1 == c.y1;
            }
            return false;
        }

        @Override
        public int getWidth() {
            return this.parent.getWidth();
        }

        @Override
        public int getHeight() {
            return this.parent.getHeight();
        }

        @Override
        public byte[] getBuffer() {
            byte[] buffer = new byte[this.parent.getWidth() * this.parent.getHeight()];
            return this.readPixels(0, 0, this.parent.getWidth(), this.parent.getHeight(), buffer);
        }

        @Override
        public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
            if (x >= this.x0 && y >= this.y0 && x + w <= this.x1 && y + h <= this.y1) {
                return this.parent.readPixels(x, y, w, h, dst_buffer);
            }
            if (x >= this.x1 || y >= this.y1 || x + w <= this.x0 || y + h <= this.y0) {
                Arrays.fill(dst_buffer, 0, w * h, (byte)0);
                return dst_buffer;
            }
            int xp_end = x + w;
            int y_end = y + h;
            int index = 0;
            for (int yp = y; yp < y_end; ++yp) {
                if (yp < this.y0 || yp >= this.y1) {
                    int index_end = index + w;
                    Arrays.fill(dst_buffer, index, index_end, (byte)0);
                    index = index_end;
                    continue;
                }
                for (int xp = x; xp < xp_end; ++xp) {
                    dst_buffer[index++] = xp >= this.x0 && xp < this.x1 ? this.parent.readPixel(xp, yp) : (byte)0;
                }
            }
            return dst_buffer;
        }

        @Override
        public void writePixel(int x, int y, byte color) {
            if (x >= this.x0 && y >= this.y0 && x < this.x1 && y < this.y1) {
                this.parent.writePixel(x, y, color);
            }
        }

        @Override
        public byte readPixel(int x, int y) {
            if (x >= this.x0 && y >= this.y0 && x < this.x1 && y < this.y1) {
                return this.parent.readPixel(x, y);
            }
            return 0;
        }

        @Override
        public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
            if (x >= this.x0 && y >= this.y0 && x + w <= this.x1 && y + h <= this.y1) {
                return this.parent.writePixels(x, y, w, h, colorData);
            }
            return super.writePixels(x, y, w, h, colorData);
        }

        @Override
        public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
            if (x >= this.x0 && y >= this.y0 && x + w <= this.x1 && y + h <= this.y1) {
                return this.parent.writePixelsFill(x, y, w, h, color);
            }
            return super.writePixelsFill(x, y, w, h, color);
        }

        public String toString() {
            return "{clip x=" + this.x0 + ",y=" + this.y0 + ",w=" + (this.x1 - this.x0) + ",h=" + (this.y1 - this.y0) + "} of " + this.parent.toString();
        }
    }

    private static final class View
    extends MapCanvas {
        private final MapCanvas parent;
        private final int x;
        private final int y;
        private final int w;
        private final int h;
        private final int xAbs;
        private final int yAbs;

        public View(MapCanvas parent, int x, int y, int w, int h) {
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.xAbs = parent.getViewAbsoluteX() + x;
            this.yAbs = parent.getViewAbsoluteY() + y;
        }

        @Override
        public int getWidth() {
            return this.w;
        }

        @Override
        public int getHeight() {
            return this.h;
        }

        @Override
        public int getViewAbsoluteX() {
            return this.xAbs;
        }

        @Override
        public int getViewAbsoluteY() {
            return this.yAbs;
        }

        @Override
        public boolean isSameView(MapCanvas otherView) {
            if (otherView == this) {
                return true;
            }
            if (otherView instanceof View) {
                View v = (View)otherView;
                return this.parent.isSameView(v.parent) && this.x == v.x && this.y == v.y && this.w == v.w && this.h == v.h;
            }
            return false;
        }

        @Override
        public MapCanvas getView(int x, int y, int w, int h) {
            if (x >= 0 && y >= 0 && x + w <= this.getWidth() && y + h <= this.getHeight()) {
                if (x == 0 && y == 0 && w == this.getWidth() && h == this.getHeight()) {
                    return this;
                }
                return new View(this.parent, this.x + x, this.y + y, w, h);
            }
            return new View(this, x, y, w, h);
        }

        @Override
        public byte[] getBuffer() {
            return this.parent.readPixels(this.x, this.y, this.w, this.h);
        }

        @Override
        public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
            if (x >= 0 && y >= 0 && x + w <= this.w && y + h <= this.h) {
                return this.parent.readPixels(x + this.x, y + this.y, w, h, dst_buffer);
            }
            return super.readPixels(x, y, w, h, dst_buffer);
        }

        @Override
        public void writePixel(int x, int y, byte color) {
            if (x >= 0 && y >= 0 && x < this.w && y < this.h) {
                this.parent.writePixel(x + this.x, y + this.y, color);
            }
        }

        @Override
        public byte readPixel(int x, int y) {
            if (x >= 0 && y >= 0 && x < this.w && y < this.h) {
                return this.parent.readPixel(x + this.x, y + this.y);
            }
            return 0;
        }

        @Override
        public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
            if (x >= 0 && y >= 0 && x + w <= this.w && y + h <= this.h) {
                return this.parent.writePixels(x + this.x, y + this.y, w, h, colorData);
            }
            return super.writePixels(x, y, w, h, colorData);
        }

        @Override
        public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
            if (x >= 0 && y >= 0 && x + w <= this.w && y + h <= this.h) {
                return this.parent.writePixelsFill(x + this.x, y + this.y, w, h, color);
            }
            return super.writePixelsFill(x, y, w, h, color);
        }

        public String toString() {
            return "{view x=" + this.x + ",y=" + this.y + ",w=" + this.w + ",h=" + this.h + "} of " + this.parent.toString();
        }
    }
}

