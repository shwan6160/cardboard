/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.map.color.MCSDBubbleFormat;
import com.bergerkiller.bukkit.common.map.color.MCSDGenBukkit;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;
import com.bergerkiller.bukkit.common.map.util.RGBColorToIntConversion;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

public class MapColorPalette {
    private static final MapColorSpaceData COLOR_MAP_DATA;
    private static final IndexColorModel COLOR_INDEX_MODEL;
    public static final byte[] COLOR_MAP_AVERAGE;
    public static final byte[] COLOR_MAP_ADD;
    public static final byte[] COLOR_MAP_SUBTRACT;
    public static final byte[] COLOR_MAP_MULTIPLY;
    public static final byte[] COLOR_MAP_SPECULAR;
    public static final byte COLOR_TRANSPARENT = 0;
    public static final byte COLOR_BLACK = 119;
    public static final byte COLOR_WHITE = 34;
    public static final byte COLOR_RED = 18;
    public static final byte COLOR_GREEN = 30;
    public static final byte COLOR_BLUE = 50;
    public static final byte COLOR_CYAN = 126;
    public static final byte COLOR_YELLOW = 122;
    public static final byte COLOR_ORANGE = 62;
    public static final byte COLOR_BROWN = 42;
    public static final byte COLOR_PURPLE = 66;
    public static final byte COLOR_PINK = 82;

    private static void initTable(byte color1, byte color2) {
        int index = MapColorPalette.getMapIndex(color1, color2);
        if (MapColorPalette.isTransparent(color1) || MapColorPalette.isTransparent(color2)) {
            MapColorPalette.initTransparent(index, color1, color2);
        } else {
            Color c1 = MapColorPalette.getRealColor(color1);
            Color c2 = MapColorPalette.getRealColor(color2);
            MapColorPalette.initColor(index, c1.getRed(), c1.getGreen(), c1.getBlue(), c2.getRed(), c2.getGreen(), c2.getBlue());
        }
    }

    private static void initTransparent(int index, byte color1, byte color2) {
        MapColorPalette.COLOR_MAP_AVERAGE[index] = color2;
        MapColorPalette.COLOR_MAP_ADD[index] = color2;
        MapColorPalette.COLOR_MAP_SUBTRACT[index] = color2;
        MapColorPalette.COLOR_MAP_MULTIPLY[index] = 0;
    }

    private static void initColor(int index, int r1, int g1, int b1, int r2, int g2, int b2) {
        MapColorPalette.initArray(COLOR_MAP_AVERAGE, index, r1 + r2 >> 1, g1 + g2 >> 1, b1 + b2 >> 1);
        MapColorPalette.initArray(COLOR_MAP_ADD, index, r1 + r2, g1 + g2, b1 + b2);
        MapColorPalette.initArray(COLOR_MAP_SUBTRACT, index, r2 - r1, g2 - g1, b2 - b1);
        MapColorPalette.initArray(COLOR_MAP_MULTIPLY, index, r1 * r2 / 255, g1 * g2 / 255, b1 * b2 / 255);
    }

    private static void initArray(byte[] array, int index, int r, int g, int b) {
        if (r < 0) {
            r = 0;
        }
        if (r > 255) {
            r = 255;
        }
        if (g < 0) {
            g = 0;
        }
        if (g > 255) {
            g = 255;
        }
        if (b < 0) {
            b = 0;
        }
        if (b > 255) {
            b = 255;
        }
        array[index] = MapColorPalette.getColor(r, g, b);
    }

    public static byte remapColor(byte inputA, byte inputB, byte[] remapArray) {
        return remapArray[MapColorPalette.getMapIndex(inputA, inputB) & 0xFFFF];
    }

    public static void remapColors(byte input, byte[] output, byte[] remapArray) {
        for (int i = 0; i < output.length; ++i) {
            output[i] = remapArray[MapColorPalette.getMapIndex(input, output[i]) & 0xFFFF];
        }
    }

    public static void remapColors(byte[] input, byte[] output, byte[] remapArray) {
        for (int i = 0; i < output.length; ++i) {
            output[i] = remapArray[MapColorPalette.getMapIndex(input[i], output[i]) & 0xFFFF];
        }
    }

    public static int getColorCount() {
        return COLOR_MAP_DATA.getColorCount();
    }

    public static boolean isTransparent(byte color) {
        return (color & 0xFF) < 4;
    }

    public static byte getColor(Color color) {
        if ((color.getAlpha() & 0x80) == 0) {
            return 0;
        }
        return COLOR_MAP_DATA.get(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static byte getColor(byte r, byte g, byte b) {
        return COLOR_MAP_DATA.get(r, g, b);
    }

    public static byte getColor(int r, int g, int b) {
        if (r < 0) {
            r = 0;
        } else if (r > 255) {
            r = 255;
        }
        if (g < 0) {
            g = 0;
        } else if (g > 255) {
            g = 255;
        }
        if (b < 0) {
            b = 0;
        } else if (b > 255) {
            b = 255;
        }
        return COLOR_MAP_DATA.get(r, g, b);
    }

    public static byte[] convertImage(Image image) {
        BufferedImage imageBuf;
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (image instanceof BufferedImage) {
            imageBuf = (BufferedImage)image;
        } else {
            imageBuf = new BufferedImage(width, height, 2);
            Graphics2D graphics = imageBuf.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
        }
        int[] intPixels = null;
        byte[] bytePixels = null;
        DataBuffer dataBuffer = imageBuf.getRaster().getDataBuffer();
        if (dataBuffer instanceof DataBufferInt) {
            intPixels = ((DataBufferInt)dataBuffer).getData();
        } else if (dataBuffer instanceof DataBufferByte) {
            bytePixels = ((DataBufferByte)dataBuffer).getData();
        }
        int type = imageBuf.getType();
        RGBColorToIntConversion converterType = null;
        if (type == 1) {
            converterType = RGBColorToIntConversion.RGB;
        } else if (type == 2) {
            converterType = RGBColorToIntConversion.ARGB;
        } else if (type == 4) {
            converterType = RGBColorToIntConversion.BGR;
        } else if (type == 5) {
            converterType = RGBColorToIntConversion.BGR;
        } else if (type == 6) {
            converterType = RGBColorToIntConversion.ABGR;
        }
        if (intPixels == null && bytePixels == null || converterType == null) {
            intPixels = new int[width * height];
            bytePixels = null;
            imageBuf.getRGB(0, 0, width, height, intPixels, 0, width);
            converterType = RGBColorToIntConversion.ARGB;
        }
        final byte[] result = new byte[width * height];
        RGBColorToIntConversion.Decoder decoder = converterType.hasTransparency() ? new RGBColorToIntConversion.Decoder(converterType){

            @Override
            public void onPixel(int index, int rgba) {
                result[index] = (rgba & Integer.MIN_VALUE) != 0 ? COLOR_MAP_DATA.get(rgba & 0xFFFFFF) : (byte)0;
            }
        } : new RGBColorToIntConversion.Decoder(converterType){

            @Override
            public void onPixel(int index, int rgba) {
                result[index] = COLOR_MAP_DATA.get(rgba);
            }
        };
        if (bytePixels != null) {
            decoder.decode(bytePixels, width * height);
        } else {
            decoder.decode(intPixels, width * height);
        }
        return result;
    }

    public static void resizeCopy(byte[] input, int inputWidth, int inputHeight, final byte[] output, int outputWidth, int outputHeight) {
        if (inputWidth == outputWidth && inputHeight == outputHeight) {
            System.arraycopy(input, 0, output, 0, inputWidth * inputHeight);
            return;
        }
        int outputLength = outputWidth * outputHeight;
        int pixelStepX = inputWidth / outputWidth;
        int pixelStepY = inputHeight / outputHeight;
        if (outputWidth * pixelStepX == inputWidth && outputHeight * pixelStepY == inputHeight) {
            int pixelStepBlock = pixelStepX * pixelStepY;
            int pixelStepBlockHalf = pixelStepBlock / 2;
            int inputLineOffset = inputWidth - pixelStepX;
            int inputLineStep = (pixelStepY - 1) * inputWidth;
            int inputStartIndex = 0;
            int inputLineEndIndex = inputWidth;
            for (int i = 0; i < outputLength; ++i) {
                int numTransparent = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int inputIndex = inputStartIndex;
                for (int dy = 0; dy < pixelStepY; ++dy) {
                    for (int dx = 0; dx < pixelStepX; ++dx) {
                        byte pixel;
                        if (MapColorPalette.isTransparent(pixel = input[inputIndex++])) {
                            ++numTransparent;
                            continue;
                        }
                        Color c = MapColorPalette.getRealColor(pixel);
                        r += c.getRed();
                        g += c.getGreen();
                        b += c.getBlue();
                    }
                    inputIndex += inputLineOffset;
                }
                if (numTransparent >= pixelStepBlockHalf) {
                    output[i] = 0;
                } else {
                    int fact = pixelStepBlock - numTransparent;
                    output[i] = COLOR_MAP_DATA.get(r /= fact, g /= fact, b /= fact);
                }
                if ((inputStartIndex += pixelStepX) < inputLineEndIndex) continue;
                inputLineEndIndex = (inputStartIndex += inputLineStep) + inputWidth;
            }
            return;
        }
        DataBufferByte data = new DataBufferByte(input, input.length);
        WritableRaster raster = Raster.createInterleavedRaster(data, inputWidth, inputHeight, inputWidth, 1, new int[]{0}, null);
        BufferedImage inputImage = new BufferedImage(MapColorPalette.getIndexColorModel(), raster, false, null);
        BufferedImage outputImage = new BufferedImage(outputWidth, outputHeight, 2);
        if (outputLength > inputWidth * inputHeight) {
            AffineTransform at = new AffineTransform();
            at.scale((double)outputWidth / (double)inputWidth, (double)outputHeight / (double)inputHeight);
            AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
            outputImage = scaleOp.filter(inputImage, outputImage);
        } else {
            Image tmp = inputImage.getScaledInstance(outputWidth, outputHeight, 16);
            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics2D.drawImage(tmp, 0, 0, outputWidth, outputHeight, null);
            graphics2D.dispose();
        }
        new RGBColorToIntConversion.Decoder(RGBColorToIntConversion.ARGB){

            @Override
            public void onPixel(int index, int rgba) {
                output[index] = (rgba & Integer.MIN_VALUE) != 0 ? COLOR_MAP_DATA.get(rgba & 0xFFFFFF) : (byte)0;
            }
        }.decode(((DataBufferInt)outputImage.getRaster().getDataBuffer()).getData(), outputLength);
    }

    public static final int getMapIndex(byte color_a, byte color_b) {
        return color_a & 0xFF | (color_b & 0xFF) << 8;
    }

    public static final Color getRealColor(byte color) {
        return COLOR_MAP_DATA.getColor(color);
    }

    public static final Color getRealColor(int index) {
        return COLOR_MAP_DATA.getColor(index);
    }

    public static byte getSpecular(byte color, float lightness) {
        int index = (int)(128.0f * lightness);
        if (index < 0) {
            return 119;
        }
        if (index >= 256) {
            return COLOR_MAP_SPECULAR[((color & 0xFF) << 8) + 255];
        }
        return COLOR_MAP_SPECULAR[((color & 0xFF) << 8) + index];
    }

    public static IndexColorModel getIndexColorModel() {
        return COLOR_INDEX_MODEL;
    }

    static {
        COLOR_MAP_AVERAGE = new byte[65536];
        COLOR_MAP_ADD = new byte[65536];
        COLOR_MAP_SUBTRACT = new byte[65536];
        COLOR_MAP_MULTIPLY = new byte[65536];
        COLOR_MAP_SPECULAR = new byte[65536];
        if (CommonBootstrap.isHeadlessJDK()) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "The Java AWT runtime library is not available");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "This is usually because a headless JVM is used for the server");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Please install and configure a non-headless JVM to have Map Displays work");
            throw new UnsupportedOperationException("Map Displays require a non-headless JVM (Uses AWT)");
        }
        COLOR_MAP_DATA = new MapColorSpaceData();
        boolean success = false;
        MCSDBubbleFormat bubbleData = new MCSDBubbleFormat();
        try {
            String bub_path;
            InputStream input;
            String bub_path_postfix = "map_1_8_8.bub";
            for (String ver : new String[]{"1.17", "1.16", "1.12"}) {
                if (!CommonBootstrap.evaluateMCVersion(">=", ver)) continue;
                bub_path_postfix = "map_" + ver.replace('.', '_') + ".bub";
                break;
            }
            if ((input = MapColorPalette.class.getResourceAsStream(bub_path = "/com/bergerkiller/bukkit/common/internal/resources/map/" + bub_path_postfix)) == null) {
                Logging.LOGGER_MAPDISPLAY.severe("Bubble data at " + bub_path + " not found!");
            } else {
                bubbleData.readFrom(input);
                success = true;
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Unhandled map color format initialization error", t);
        }
        if (success) {
            COLOR_MAP_DATA.readFrom(bubbleData);
        } else {
            Logging.LOGGER_MAPDISPLAY.warning("Bubble colormap data could not be loaded, it will be generated instead");
            MCSDGenBukkit bukkitGen = new MCSDGenBukkit();
            bukkitGen.generate();
            COLOR_MAP_DATA.readFrom(bukkitGen);
        }
        COLOR_INDEX_MODEL = COLOR_MAP_DATA.toIndexColorModel();
        for (int a = 0; a < 256; ++a) {
            int index = a * 256;
            Color color_a = MapColorPalette.getRealColor((byte)a);
            if (color_a.getAlpha() < 128) {
                Arrays.fill(COLOR_MAP_SPECULAR, index, index + 256, (byte)0);
                continue;
            }
            for (int b = 0; b < 256; ++b) {
                float f = (float)b / 128.0f;
                int sr = (int)((float)color_a.getRed() * f);
                int sg = (int)((float)color_a.getGreen() * f);
                int sb = (int)((float)color_a.getBlue() * f);
                MapColorPalette.COLOR_MAP_SPECULAR[index++] = MapColorPalette.getColor(sr, sg, sb);
            }
        }
        for (int c1 = 0; c1 < 256; ++c1) {
            for (int c2 = 0; c2 < 256; ++c2) {
                MapColorPalette.initTable((byte)c1, (byte)c2);
            }
        }
    }
}

