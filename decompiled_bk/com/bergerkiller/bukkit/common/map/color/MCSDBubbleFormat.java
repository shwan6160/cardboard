/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.color;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.io.BitInputStream;
import com.bergerkiller.bukkit.common.io.BitOutputStream;
import com.bergerkiller.bukkit.common.io.BitPacket;
import com.bergerkiller.bukkit.common.map.color.MCSDWebbingCodec;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.InflaterInputStream;

public class MCSDBubbleFormat
extends MapColorSpaceData {
    public final boolean[][] strands = new boolean[256][65536];
    public final ArrayList<Bubble> bubbles = new ArrayList();
    private MapColorSpaceData input_colors = null;
    private int max_iterations = 1000;

    public void setMaxIterations(int maxIterations) {
        this.max_iterations = maxIterations;
    }

    public boolean getStrand(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= 256 || y >= 256 || z >= 256) {
            return true;
        }
        return this.strands[z][x | y << 8];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void readFrom(InputStream stream) throws IOException {
        try (BitInputStream bitStream = new BitInputStream(new BufferedInputStream(new InflaterInputStream(stream)));){
            for (int i = 0; i < 256; ++i) {
                int r = bitStream.read();
                int g = bitStream.read();
                int b = bitStream.read();
                int a = bitStream.read();
                this.setColor((byte)i, new Color(r, g, b, a));
            }
            while (true) {
                Bubble bubble = new Bubble();
                bubble.color = (byte)bitStream.read();
                if (bubble.color == 0) break;
                bubble.x = bitStream.read();
                bubble.y = bitStream.read();
                bubble.z_min = bitStream.read();
                bubble.z_max = bubble.z_min + bitStream.read();
                this.bubbles.add(bubble);
            }
            MCSDWebbingCodec codec = new MCSDWebbingCodec();
            for (int z = 0; z < 256; ++z) {
                Arrays.fill(this.strands[z], false);
                codec.reset(this.strands[z], false);
                while (codec.readNext(bitStream)) {
                }
            }
            this.initColors();
            for (int i = 0; i < 0x1000000; ++i) {
                if (this.get(i) != 0) continue;
                if (bitStream.readBits(1) == 0) {
                    this.set(i, this.get(i - 1));
                    continue;
                }
                int mode = bitStream.readBits(2);
                if (mode == 0) {
                    this.set(i, this.get(i - 256));
                    continue;
                }
                if (mode == 1) {
                    this.set(i, this.get(i + 1));
                    continue;
                }
                if (mode == 2) {
                    this.set(i, this.get(i + 256));
                    continue;
                }
                this.set(i, (byte)bitStream.readBits(8));
            }
        }
    }

    public void writeTo(OutputStream stream) throws IOException {
        try (BitOutputStream bitStream = new BitOutputStream(StreamUtil.createDeflaterOutputStreamWithCompressionLevel(stream, 9));){
            int index;
            boolean[] strands;
            int z2;
            this.input_colors = new MapColorSpaceData();
            this.input_colors.readFrom(this);
            Logging.LOGGER_MAPDISPLAY.info("Loading bubble boundaries...");
            for (z2 = 0; z2 < 256; ++z2) {
                strands = this.strands[z2];
                int index_end = z2 + 1 << 16;
                for (index = z2 << 16; index < index_end; ++index) {
                    int x = index & 0xFF;
                    int y = index >> 8 & 0xFF;
                    byte color = this.get(index);
                    strands[index & strands.length - 1] = x < 255 && color != this.get(index + 1) || y < 255 && color != this.get(index + 256);
                }
            }
            Logging.LOGGER_MAPDISPLAY.info("Generating bubble spatial information...");
            for (z2 = 0; z2 < 256; ++z2) {
                strands = this.strands[z2];
                int index_offset = z2 << 16;
                for (index = 0; index < 65536; ++index) {
                    byte color;
                    if (strands[index] || (color = this.get(index_offset + index)) == 0) continue;
                    this.set(index_offset + index, (byte)0);
                    Bubble bubble = new Bubble();
                    bubble.x = index & 0xFF;
                    bubble.y = index >> 8 & 0xFF;
                    bubble.z_min = z2;
                    bubble.z_max = z2;
                    bubble.color = color;
                    bubble.pixels.add(new IntVector2(bubble.x, bubble.y));
                    this.spread(bubble);
                    this.bubbles.add(bubble);
                }
            }
            this.readFrom(this.input_colors);
            Logging.LOGGER_MAPDISPLAY.info("Connecting bubbles in the z-axis...");
            for (int bubbleidx = 0; bubbleidx < this.bubbles.size(); ++bubbleidx) {
                Bubble bubble = this.bubbles.get(bubbleidx);
                Iterator<Bubble> iter = this.bubbles.iterator();
                while (iter.hasNext()) {
                    Bubble otherBubble = iter.next();
                    if (bubble == otherBubble || bubble.color != otherBubble.color || bubble.z_min != otherBubble.z_max + 1 && bubble.z_max != otherBubble.z_min - 1) continue;
                    boolean sharesPixels = false;
                    for (IntVector2 p : otherBubble.pixels) {
                        if (!bubble.pixels.contains(p)) continue;
                        sharesPixels = true;
                        break;
                    }
                    if (!sharesPixels) continue;
                    bubble.pixels.retainAll(otherBubble.pixels);
                    if (otherBubble.z_min < bubble.z_min) {
                        bubble.z_min = otherBubble.z_min;
                    }
                    if (otherBubble.z_max > bubble.z_max) {
                        bubble.z_max = otherBubble.z_max;
                    }
                    iter.remove();
                }
            }
            Logging.LOGGER_MAPDISPLAY.info("Calculating bubble positions...");
            for (Bubble bubble : this.bubbles) {
                int avg_x = 0;
                int avg_y = 0;
                for (IntVector2 pixel : bubble.pixels) {
                    avg_x += pixel.x;
                    avg_y += pixel.z;
                }
                avg_x /= bubble.pixels.size();
                avg_y /= bubble.pixels.size();
                IntVector2 closest = null;
                int minDistSq = Integer.MAX_VALUE;
                for (IntVector2 pixel : bubble.pixels) {
                    int dx = pixel.x - avg_x;
                    int dy = pixel.z - avg_y;
                    int distSq = dx * dx + dy * dy;
                    if (distSq >= minDistSq) continue;
                    minDistSq = distSq;
                    closest = pixel;
                }
                bubble.x = closest.x;
                bubble.y = closest.z;
            }
            for (int i = 0; i < 256; ++i) {
                Color color = this.getColor((byte)i);
                bitStream.write(color.getRed());
                bitStream.write(color.getGreen());
                bitStream.write(color.getBlue());
                bitStream.write(color.getAlpha());
            }
            for (Bubble bubble : this.bubbles) {
                bitStream.write(bubble.color & 0xFF);
                bitStream.write(bubble.x);
                bitStream.write(bubble.y);
                bitStream.write(bubble.z_min);
                bitStream.write(bubble.z_max - bubble.z_min);
            }
            bitStream.write(0);
            Logging.LOGGER_MAPDISPLAY.info("Initializing color information for " + this.bubbles.size() + " bubbles...");
            this.initColors();
            Logging.LOGGER_MAPDISPLAY.info("Writing bubble boundary information...");
            ((Stream)IntStream.range(0, 256).mapToObj(z -> this.generateSlice(z)).parallel()).forEachOrdered(codec -> {
                try {
                    codec.writePackets(bitStream);
                }
                catch (IOException ex) {
                    Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "IO Exception while writing packets", ex);
                }
            });
            Logging.LOGGER_MAPDISPLAY.info("Correcting missing color information...");
            ArrayList<BitPacket> colorCodes = new ArrayList<BitPacket>();
            for (int i = 0; i < 0x1000000; ++i) {
                if (this.get(i) != 0) continue;
                int x = i & 0xFF;
                int y = i >> 8 & 0xFF;
                BitPacket code = new BitPacket();
                byte color = this.input_colors.get(i);
                if (x > 0 && this.get(i - 1) == color) {
                    code.write(0, 1);
                } else {
                    code.write(1, 1);
                    if (y > 0 && this.get(i - 256) == color) {
                        code.write(0, 2);
                    } else if (x < 255 && this.get(i + 1) == color) {
                        code.write(1, 2);
                    } else if (y < 255 && this.get(i + 256) == color) {
                        code.write(2, 2);
                    } else {
                        code.write(3, 2);
                        code.write(color & 0xFF, 8);
                    }
                }
                colorCodes.add(code);
                this.set(i, color);
            }
            for (BitPacket code : colorCodes) {
                bitStream.writeBits(code.data, code.bits);
            }
        }
    }

    private void spread(Bubble cell) {
        boolean hasChanges;
        do {
            hasChanges = false;
            for (int i = 0; i < cell.pixels.size(); ++i) {
                IntVector2 pixel = (IntVector2)cell.pixels.get(i);
                for (int dx = -1; dx <= 1; dx += 2) {
                    for (int dy = -1; dy <= 1; dy += 2) {
                        int x = pixel.x + dx;
                        int y = pixel.z + dy;
                        if (x < 0 || y < 0 || x >= 256 || y >= 256 || this.get(x, y, cell.z_min) != cell.color || this.getStrand(x, y, cell.z_min)) continue;
                        this.set(x, y, cell.z_min, (byte)0);
                        cell.pixels.add(new IntVector2(x, y));
                        hasChanges = true;
                    }
                }
            }
        } while (hasChanges);
    }

    private void initColors() {
        this.clearRGBData();
        for (Bubble cell : this.bubbles) {
            for (int z = cell.z_min; z <= cell.z_max; ++z) {
                this.set(cell.x, cell.y, z, cell.color);
            }
        }
        this.spreadColors();
    }

    private void spreadColors() {
        int[] buffer = new int[0x1000000];
        int count = -1;
        for (int z = 0; z < 256; ++z) {
            boolean[] layerStrands = this.strands[z];
            int indexOffset = z << 16;
            for (int i = 0; i < 65536; ++i) {
                if (layerStrands[i]) continue;
                buffer[++count] = indexOffset + i;
            }
        }
        StrandBuffer buf = new StrandBuffer(buffer, ++count);
        buf.process(index -> {
            boolean row;
            boolean col = (index & 0xFF) < 255;
            boolean bl = row = (index & 0xFF00) < 65280;
            if (col && row) {
                byte color = this.get(index);
                if (color != 0) {
                    this.set(index + 1, color);
                    this.set(index + 256, color);
                    return true;
                }
                color = this.get(index + 1);
                if (color != 0) {
                    this.set(index, color);
                    this.set(index + 256, color);
                    return true;
                }
                color = this.get(index + 256);
                if (color != 0) {
                    this.set(index, color);
                    this.set(index + 1, color);
                    return true;
                }
            } else if (col) {
                byte color = this.get(index);
                if (color != 0) {
                    this.set(index + 1, color);
                    return true;
                }
                color = this.get(index + 1);
                if (color != 0) {
                    this.set(index, color);
                    return true;
                }
            } else if (row) {
                byte color = this.get(index);
                if (color != 0) {
                    this.set(index + 256, color);
                    return true;
                }
                color = this.get(index + 256);
                if (color != 0) {
                    this.set(index, color);
                    return true;
                }
            }
            return false;
        });
    }

    private MCSDWebbingCodec generateSlice(int z) {
        boolean[] cells = this.strands[z];
        ArrayList<IntVector2> edges = new ArrayList<IntVector2>();
        ArrayList<IntVector2> intersects = new ArrayList<IntVector2>();
        ArrayList<IntVector2> lines = new ArrayList<IntVector2>();
        boolean[] data3x3 = new boolean[9];
        int index = 0;
        for (int y = 0; y < 256; ++y) {
            for (int x = 0; x < 256; ++x) {
                if (!cells[index++]) continue;
                IntVector2 coord = new IntVector2(x, y);
                int n = 0;
                for (int dy = -1; dy <= 1; ++dy) {
                    for (int dx = -1; dx <= 1; ++dx) {
                        int mx = coord.x + dx;
                        int my = coord.z + dy;
                        data3x3[n++] = mx < 0 || my < 0 || mx >= 256 || my >= 256 ? false : cells[mx | my << 8];
                    }
                }
                if (MCSDWebbingCodec.EDGE_PATTERN.matches(data3x3)) {
                    edges.add(coord);
                    continue;
                }
                if (MCSDWebbingCodec.LINE_PATTERN.matches(data3x3)) {
                    lines.add(coord);
                    continue;
                }
                intersects.add(coord);
            }
        }
        Logging.LOGGER_MAPDISPLAY.info("Processing z=" + z + ", " + edges.size() + " edges, " + intersects.size() + " intersects, " + lines.size() + " lines");
        MCSDWebbingCodec codec = new MCSDWebbingCodec();
        codec.reset(cells, true);
        codec.processBest(edges, this.max_iterations);
        codec.processBest(intersects, this.max_iterations);
        codec.processBest(lines, this.max_iterations);
        return codec;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MCSDBubbleFormat) {
            int i;
            MCSDBubbleFormat other = (MCSDBubbleFormat)o;
            for (i = 0; i < this.strands.length; ++i) {
                if (other.strands[i] == this.strands[i]) continue;
                return false;
            }
            if (this.bubbles.size() != other.bubbles.size()) {
                return false;
            }
            for (i = 0; i < this.bubbles.size(); ++i) {
                if (this.bubbles.get(i).equals(other.bubbles.get(i))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public static class Bubble {
        public int x;
        public int y;
        public int z_min;
        public int z_max;
        public byte color;
        private final ArrayList<IntVector2> pixels = new ArrayList();

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Bubble) {
                Bubble other = (Bubble)o;
                return other.x == this.x && other.y == this.y && other.z_min == this.z_min && other.z_max == this.z_max && other.color == this.color;
            }
            return false;
        }

        public String toString() {
            return "cell{x=" + this.x + ", y=" + this.y + ", zmin=" + this.z_min + ", zmax=" + this.z_max + ", color=" + (this.color & 0xFF) + "}";
        }
    }

    private static class StrandBuffer {
        private final int[] buf;
        private int start;
        private int end;

        public StrandBuffer(int[] buffer, int count) {
            this.buf = buffer;
            this.start = 0;
            this.end = count - 1;
        }

        public void process(IntPredicate strandIndexProc) {
            while (this.forward(strandIndexProc) && this.reverse(strandIndexProc)) {
            }
        }

        public boolean forward(IntPredicate strandIndexProc) {
            int[] buf = this.buf;
            int writeIdx = this.start - 1;
            int endIdx = this.end;
            boolean changed = false;
            for (int i = this.start; i <= endIdx; ++i) {
                int strandIndex = buf[i];
                if (strandIndexProc.test(strandIndex)) {
                    changed = true;
                    continue;
                }
                buf[++writeIdx] = strandIndex;
            }
            this.end = writeIdx;
            return changed;
        }

        public boolean reverse(IntPredicate strandIndexProc) {
            int[] buf = this.buf;
            int writeIdx = this.end + 1;
            int startIdx = this.start;
            boolean changed = false;
            for (int i = this.end; i >= startIdx; --i) {
                int strandIndex = buf[i];
                if (strandIndexProc.test(strandIndex)) {
                    changed = true;
                    continue;
                }
                buf[--writeIdx] = strandIndex;
            }
            this.start = writeIdx;
            return changed;
        }
    }
}

