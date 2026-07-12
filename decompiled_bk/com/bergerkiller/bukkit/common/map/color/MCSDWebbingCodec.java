/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.color;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.io.BitInputStream;
import com.bergerkiller.bukkit.common.io.BitOutputStream;
import com.bergerkiller.bukkit.common.io.BitPacket;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class MCSDWebbingCodec {
    private int written_cells;
    private int last_x;
    private int last_y;
    private int last_dx;
    private int last_dy;
    public boolean[] strands = new boolean[65536];
    private BitPacket[] packets = new BitPacket[1024];
    private int packets_count = 0;
    public static final Pattern EDGE_PATTERN = new Pattern(new int[][]{{0, 0, 0, 0, 1, 0, 0, 1, 0}, {0, 0, 0, 0, 1, 0, 0, 1, 1}, {0, 0, 0, 0, 1, 0, 0, 0, 1}, {0, 0, 0, 0, 1, 0, 0, 0, 0}});
    public static final Pattern LINE_PATTERN = new Pattern(new int[][]{{0, 1, 0, 0, 1, 0, 0, 1, 0}, {0, 1, 0, 0, 1, 0, 0, 1, 1}, {1, 1, 0, 0, 1, 0, 0, 1, 1}, {0, 1, 0, 0, 1, 0, 0, 0, 1}, {0, 1, 0, 0, 1, 1, 0, 0, 1}, {1, 1, 0, 0, 1, 1, 0, 0, 1}, {1, 0, 0, 0, 1, 0, 0, 0, 1}});

    public MCSDWebbingCodec() {
        for (int i = 0; i < this.packets.length; ++i) {
            this.packets[i] = new BitPacket();
        }
    }

    public void reset(MCSDWebbingCodec codec) {
        this.written_cells = codec.written_cells;
        this.last_x = codec.last_x;
        this.last_y = codec.last_y;
        this.last_dx = codec.last_dx;
        this.last_dy = codec.last_dy;
        System.arraycopy(codec.strands, 0, this.strands, 0, this.strands.length);
        this.packets_count = 0;
        for (BitPacket packet2 : codec.packets) {
            BitPacket packet = this.addPacket();
            packet.bits = packet2.bits;
            packet.data = packet2.data;
        }
        this.packets_count = codec.packets_count;
    }

    public void reset(boolean[] cells, boolean copyCells) {
        if (copyCells) {
            System.arraycopy(cells, 0, this.strands, 0, cells.length);
        } else {
            this.strands = cells;
        }
        this.written_cells = 0;
        this.last_x = -1000;
        this.last_y = -1000;
        this.last_dx = 1;
        this.last_dy = 1;
        this.packets_count = 0;
    }

    public boolean writeNext(int x, int y) {
        if (x < 0 || y < 0 || x >= 256 || y >= 256) {
            return false;
        }
        int index = x | y << 8;
        if (!this.strands[index]) {
            return false;
        }
        this.strands[index] = false;
        ++this.written_cells;
        int dx = x - this.last_x;
        int dy = y - this.last_y;
        this.last_x = x;
        this.last_y = y;
        if (dx == 0 && dy == 0) {
            return false;
        }
        if (dx > 1 || dx < -1 || dy > 1 || dy < -1) {
            BitPacket code = this.addPacket();
            code.write(3, 2);
            code.write(0, 1);
            code.write(0, 1);
            code.write(x, 8);
            code.write(y, 8);
        } else {
            BitPacket code;
            if (dx > 0 && this.last_dx < 0 || dx < 0 && this.last_dx > 0) {
                code = this.addPacket();
                code.write(3, 2);
                code.write(1, 1);
                code.write(dx > 0 ? 1 : 0, 2);
                this.last_dx = dx;
            }
            if (dy > 0 && this.last_dy < 0 || dy < 0 && this.last_dy > 0) {
                code = this.addPacket();
                code.write(3, 2);
                code.write(1, 1);
                code.write(dy > 0 ? 3 : 2, 2);
                this.last_dy = dy;
            }
            code = this.addPacket();
            if (dy == 0) {
                code.write(0, 2);
            } else if (dx == 0) {
                code.write(1, 2);
            } else {
                code.write(2, 2);
            }
        }
        return true;
    }

    private BitPacket addPacket() {
        if (this.packets_count >= this.packets.length) {
            BitPacket[] new_packets = new BitPacket[this.packets.length * 2];
            System.arraycopy(this.packets, 0, new_packets, 0, this.packets.length);
            for (int i = this.packets.length; i < new_packets.length; ++i) {
                new_packets[i] = new BitPacket();
            }
            this.packets = new_packets;
        }
        BitPacket result = this.packets[this.packets_count++];
        result.bits = 0;
        result.data = 0;
        return result;
    }

    public boolean readNext(BitInputStream stream) throws IOException {
        int op = stream.readBits(2);
        if (op == 3) {
            if (stream.readBits(1) == 1) {
                int sub = stream.readBits(2);
                if (sub == 0) {
                    this.last_dx = -1;
                } else if (sub == 1) {
                    this.last_dx = 1;
                } else if (sub == 2) {
                    this.last_dy = -1;
                } else if (sub == 3) {
                    this.last_dy = 1;
                }
            } else {
                if (stream.readBits(1) == 1) {
                    return false;
                }
                this.last_x = stream.readBits(8);
                this.last_y = stream.readBits(8);
                this.strands[this.last_x | this.last_y << 8] = true;
            }
        } else {
            if (op == 0) {
                this.last_x += this.last_dx;
            } else if (op == 1) {
                this.last_y += this.last_dy;
            } else if (op == 2) {
                this.last_x += this.last_dx;
                this.last_y += this.last_dy;
            } else if (op == -1) {
                return false;
            }
            this.strands[this.last_x | this.last_y << 8] = true;
        }
        return true;
    }

    public boolean writeFrom(int x, int y) {
        if (!this.writeNext(x, y)) {
            return false;
        }
        while (true) {
            if (this.writeNext(x, y + this.last_dy)) {
                y += this.last_dy;
                continue;
            }
            if (this.writeNext(x + this.last_dx, y)) {
                x += this.last_dx;
                continue;
            }
            if (this.writeNext(x + this.last_dx, y + this.last_dy)) {
                x += this.last_dx;
                y += this.last_dy;
                continue;
            }
            if (this.writeNext(x - this.last_dx, y)) {
                x -= this.last_dx;
                continue;
            }
            if (this.writeNext(x, y - this.last_dy)) {
                y -= this.last_dy;
                continue;
            }
            if (this.writeNext(x - this.last_dx, y + this.last_dy)) {
                x -= this.last_dx;
                y += this.last_dy;
                continue;
            }
            if (this.writeNext(x + this.last_dx, y - this.last_dy)) {
                x += this.last_dx;
                y -= this.last_dy;
                continue;
            }
            if (!this.writeNext(x - this.last_dx, y - this.last_dy)) break;
            x -= this.last_dx;
            y -= this.last_dy;
        }
        return true;
    }

    public int calculateWritten() {
        return this.written_cells;
    }

    public int calculateCompressedSize() {
        int n;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        BitOutputStream bitStream = new BitOutputStream(StreamUtil.createDeflaterOutputStreamWithCompressionLevel(bs, 9));
        try {
            for (int i = 0; i < this.packets_count; ++i) {
                bitStream.writePacket(this.packets[i]);
            }
            bitStream.close();
            n = bs.size();
        }
        catch (Throwable throwable) {
            try {
                try {
                    bitStream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException ex) {
                Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "IO Exception while writing data", ex);
                return 0;
            }
        }
        bitStream.close();
        return n;
    }

    public int calculateSize() {
        int total_size = 0;
        for (int i = 0; i < this.packets_count; ++i) {
            total_size += this.packets[i].bits;
        }
        return total_size;
    }

    public double calculateCost() {
        return (double)this.calculateSize() / (double)this.calculateWritten();
    }

    public void writePackets(BitOutputStream stream) throws IOException {
        for (int i = 0; i < this.packets_count; ++i) {
            stream.writePacket(this.packets[i]);
        }
        BitPacket closingCode = new BitPacket();
        closingCode.write(3, 2);
        closingCode.write(0, 1);
        closingCode.write(1, 1);
        stream.writePacket(closingCode);
    }

    public void processBest(List<IntVector2> points, int max_iterations) {
        ArrayList<StartPoint> best_coords = new ArrayList<StartPoint>(points.size());
        for (IntVector2 point : points) {
            if (!this.strands[point.x | point.z << 8]) continue;
            StartPoint startPoint = new StartPoint();
            startPoint.x = point.x;
            startPoint.y = point.z;
            startPoint.active = true;
            best_coords.add(startPoint);
        }
        if (best_coords.isEmpty()) {
            return;
        }
        MCSDWebbingCodec codec_start = new MCSDWebbingCodec();
        codec_start.reset(this);
        Random random = new Random();
        MCSDWebbingCodec tempCodec = new MCSDWebbingCodec();
        int start_size = 0;
        int start_count = 0;
        double start_cost = Double.MAX_VALUE;
        int best_size = 0;
        int best_count = 0;
        double best_cost = Double.MAX_VALUE;
        int iterations = 0;
        while (++iterations < max_iterations) {
            int index_b;
            int index_a;
            while ((index_a = random.nextInt(best_coords.size())) == (index_b = random.nextInt(best_coords.size())) || !((StartPoint)best_coords.get((int)index_a)).active && !((StartPoint)best_coords.get((int)index_b)).active) {
            }
            Collections.swap(best_coords, index_a, index_b);
            tempCodec.reset(codec_start);
            for (StartPoint startPoint : best_coords) {
                startPoint.active = tempCodec.writeFrom(startPoint.x, startPoint.y);
            }
            double cost = tempCodec.calculateCost();
            if (cost < best_cost) {
                this.reset(tempCodec);
                iterations = 0;
                best_cost = cost;
                best_count = tempCodec.calculateWritten();
                best_size = tempCodec.calculateSize();
                if (start_cost != Double.MAX_VALUE) continue;
                start_cost = best_cost;
                start_count = best_count;
                start_size = best_size;
                continue;
            }
            Collections.swap(best_coords, index_a, index_b);
        }
        Logging.LOGGER_MAPDISPLAY.info("[" + MathUtil.round(start_cost, 3) + " c/p, " + start_count + " pixels, " + start_size + " bits] => [" + MathUtil.round(best_cost, 3) + " c/p, " + best_count + " pixels, " + best_size + " bits] [compressed=" + this.calculateCompressedSize() + "]");
    }

    private static class StartPoint {
        int x;
        int y;
        boolean active;

        private StartPoint() {
        }
    }

    public static class Pattern {
        private final boolean[][] patterns;

        public Pattern(int[][] patternData) {
            int num_transforms = 8;
            this.patterns = new boolean[patternData.length * 8][9];
            int patternIndex = 0;
            for (int[] intPattern : patternData) {
                boolean[] boolPattern = new boolean[intPattern.length];
                for (int i = 0; i < intPattern.length; ++i) {
                    boolPattern[i] = intPattern[i] > 0;
                }
                for (int mode = 0; mode < 8; ++mode) {
                    boolean[] pattern = this.patterns[patternIndex++];
                    int idx = 0;
                    for (int y = 0; y < 3; ++y) {
                        for (int x = 0; x < 3; ++x) {
                            int sx = x;
                            int sy = y;
                            if ((mode & 1) == 1) {
                                sx = y;
                                sy = x;
                            }
                            if ((mode & 2) == 2) {
                                sx = 2 - sx;
                            }
                            if ((mode & 4) == 4) {
                                sy = 2 - sy;
                            }
                            pattern[idx++] = boolPattern[sy * 3 + sx];
                        }
                    }
                }
            }
        }

        public boolean matches(boolean[] data) {
            for (boolean[] pattern : this.patterns) {
                if (!Arrays.equals(data, pattern)) continue;
                return true;
            }
            return false;
        }
    }
}

