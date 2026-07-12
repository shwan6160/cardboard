/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.Brightness
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle$BlockDisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.debug.particles;

import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.debug.particles.DebugParticles;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddEntityPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEntityDataPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class DebugParticlesDisplay
extends DebugParticles {
    private static final int DURATION = 100;
    private static final int BRIGHTNESS_RANGE = 4;
    private static final int BRIGHTNESS_STEPS = 2;
    private final List<DisplayTask> displayTasks = new ArrayList<DisplayTask>();
    private static final DataWatcher.Prototype LINE_METADATA = VirtualDisplayEntity.BASE_DISPLAY_METADATA.modify().setByte(EntityHandle.DATA_FLAGS, 64).set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)0).set(DisplayHandle.DATA_BRIGHTNESS_OVERRIDE, (Object)Brightness.blockLight((int)15)).setClientDefault(DisplayHandle.DATA_GLOW_COLOR_OVERRIDE, (Object)-1).setClientDefault(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.AIR).create();
    private static final DataWatcher.Prototype POINT_METADATA;

    protected DebugParticlesDisplay(Player player) {
        super(player);
    }

    @Override
    public void cube(Color color, double x1, double y1, double z1, double x2, double y2, double z2) {
        double minSize = 0.02 * Util.absMinAxis(new Vector(x2 - x1, y2 - y1, z2 - z1));
        double lineThickness = Math.min(0.3, minSize);
        this.cube(color, ConcretePalette.getConcrete(color), x1, y1, z1, x2, y2, z2, lineThickness);
    }

    @Override
    public void face(Color color, double x1, double y1, double z1, double x2, double y2, double z2) {
        double dist = MathUtil.distance((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
        double minSize = 0.02 * dist;
        double lineThickness = Math.min(0.3, minSize);
        this.face(color, ConcretePalette.getConcrete(color), x1, y1, z1, x2, y2, z2, lineThickness);
    }

    @Override
    public void line(Color color, double x1, double y1, double z1, double x2, double y2, double z2) {
        double dist = MathUtil.distance((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
        double minSize = 0.02 * dist;
        double lineThickness = Math.min(0.3, minSize);
        this.line(color, ConcretePalette.getConcrete(color), x1, y1, z1, x2, y2, z2, lineThickness);
    }

    private void cube(Color color, BlockData concrete, double x1, double y1, double z1, double x2, double y2, double z2, double lineThickness) {
        this.face(color, concrete, x1, y1, z1, x2, y1, z2, lineThickness);
        this.face(color, concrete, x1, y2, z1, x2, y2, z2, lineThickness);
        this.line(color, concrete, x1, y1, z1, x1, y2, z1, lineThickness);
        this.line(color, concrete, x2, y1, z1, x2, y2, z1, lineThickness);
        this.line(color, concrete, x1, y1, z2, x1, y2, z2, lineThickness);
        this.line(color, concrete, x2, y1, z2, x2, y2, z2, lineThickness);
    }

    private void face(Color color, BlockData concrete, double x1, double y1, double z1, double x2, double y2, double z2, double lineThickness) {
        this.line(color, concrete, x1, y1, z1, x2, y1, z1, lineThickness);
        this.line(color, concrete, x1, y1, z1, x1, y2, z1, lineThickness);
        this.line(color, concrete, x1, y1, z1, x1, y1, z2, lineThickness);
        this.line(color, concrete, x1, y2, z2, x2, y2, z2, lineThickness);
        this.line(color, concrete, x2, y1, z2, x2, y2, z2, lineThickness);
        this.line(color, concrete, x2, y2, z1, x2, y2, z2, lineThickness);
    }

    private void line(Color color, BlockData concrete, double x1, double y1, double z1, double x2, double y2, double z2, double lineThickness) {
        double dist = MathUtil.distance((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
        if (dist <= 1.0E-6) {
            return;
        }
        Quaternion rotation = Quaternion.fromLookDirection((Vector)new Vector(x2 - x1, y2 - y1, z2 - z1), (Vector)new Vector(0.0, 1.0, 0.0));
        int entityId = EntityUtil.getUniqueEntityId();
        UUID entityUUID = UUID.randomUUID();
        DataWatcher metadata = LINE_METADATA.create();
        if (!TCConfig.debugMutexGlow) {
            metadata.setFlag(EntityHandle.DATA_FLAGS, 64, false);
        }
        Vector translation = new Vector(-0.5 * lineThickness, -0.5 * lineThickness, -0.5 * dist);
        rotation.transformPoint(translation);
        metadata.set(DisplayHandle.DATA_TRANSLATION, (Object)translation);
        metadata.set(DisplayHandle.DATA_LEFT_ROTATION, (Object)rotation);
        metadata.set(DisplayHandle.DATA_SCALE, (Object)new Vector(lineThickness, lineThickness, dist));
        metadata.set(DisplayHandle.DATA_GLOW_COLOR_OVERRIDE, (Object)color.asRGB());
        metadata.set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)concrete);
        DisplayTask task = new DisplayTask(entityId, color, metadata);
        task.applyBrightness();
        ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(entityId);
        spawnPacket.setEntityUUID(entityUUID);
        spawnPacket.setEntityType(VirtualDisplayEntity.BLOCK_DISPLAY_ENTITY_TYPE);
        spawnPacket.setPosX(0.5 * (x1 + x2));
        spawnPacket.setPosY(0.5 * (y1 + y2));
        spawnPacket.setPosZ(0.5 * (z1 + z2));
        spawnPacket.setMotX(0.0);
        spawnPacket.setMotY(0.0);
        spawnPacket.setMotZ(0.0);
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        PacketUtil.sendPacket((Player)this.player, (PacketHandle)spawnPacket);
        PacketUtil.sendPacket((Player)this.player, (PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)entityId, (DataWatcher)metadata, (boolean)true));
        this.displayTasks.add(task);
        this.startUpdating();
    }

    @Override
    public void point(Color color, double x, double y, double z) {
        int entityId = EntityUtil.getUniqueEntityId();
        UUID entityUUID = UUID.randomUUID();
        DataWatcher metadata = POINT_METADATA.create();
        if (!TCConfig.debugMutexGlow) {
            metadata.setFlag(EntityHandle.DATA_FLAGS, 64, false);
        }
        metadata.set(DisplayHandle.DATA_GLOW_COLOR_OVERRIDE, (Object)color.asRGB());
        metadata.set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)ConcretePalette.getConcrete(color));
        DisplayTask task = new DisplayTask(entityId, color, metadata);
        task.applyBrightness();
        ClientboundAddEntityPacketHandle spawnPacket = ClientboundAddEntityPacketHandle.createNew();
        spawnPacket.setEntityId(entityId);
        spawnPacket.setEntityUUID(entityUUID);
        spawnPacket.setEntityType(VirtualDisplayEntity.BLOCK_DISPLAY_ENTITY_TYPE);
        spawnPacket.setPosX(x);
        spawnPacket.setPosY(y);
        spawnPacket.setPosZ(z);
        spawnPacket.setMotX(0.0);
        spawnPacket.setMotY(0.0);
        spawnPacket.setMotZ(0.0);
        spawnPacket.setYaw(0.0f);
        spawnPacket.setPitch(0.0f);
        PacketUtil.sendPacket((Player)this.player, (PacketHandle)spawnPacket);
        PacketUtil.sendPacket((Player)this.player, (PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)entityId, (DataWatcher)metadata, (boolean)true));
        this.displayTasks.add(task);
        this.startUpdating();
    }

    @Override
    protected boolean update() {
        this.displayTasks.removeIf(d -> d.update(this.player));
        return this.displayTasks.isEmpty();
    }

    static {
        double scale = 0.1;
        POINT_METADATA = VirtualDisplayEntity.BASE_DISPLAY_METADATA.modify().set(DisplayHandle.DATA_TRANSLATION, (Object)new Vector(-0.5 * scale, -0.5 * scale, -0.5 * scale)).set(DisplayHandle.DATA_SCALE, (Object)new Vector(scale, scale, scale)).setByte(EntityHandle.DATA_FLAGS, 64).set(DisplayHandle.DATA_INTERPOLATION_DURATION, (Object)0).set(DisplayHandle.DATA_BRIGHTNESS_OVERRIDE, (Object)Brightness.blockLight((int)15)).setClientDefault(DisplayHandle.DATA_GLOW_COLOR_OVERRIDE, (Object)-1).setClientDefault(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, (Object)BlockData.AIR).create();
    }

    private static class ConcretePalette {
        public static final List<Entry> entries = Arrays.asList(new Entry("WHITE_CONCRETE", 255, 255, 255), new Entry("ORANGE_CONCRETE", 220, 95, 0), new Entry("MAGENTA_CONCRETE", 168, 49, 158), new Entry("LIGHT_BLUE_CONCRETE", 35, 134, 196), new Entry("YELLOW_CONCRETE", 237, 172, 21), new Entry("LIME_CONCRETE", 92, 165, 24), new Entry("PINK_CONCRETE", 211, 100, 141), new Entry("GRAY_CONCRETE", 53, 56, 60), new Entry("LIGHT_GRAY_CONCRETE", 125, 125, 115), new Entry("CYAN_CONCRETE", 21, 117, 133), new Entry("PURPLE_CONCRETE", 99, 31, 154), new Entry("BLUE_CONCRETE", 44, 46, 142), new Entry("BROWN_CONCRETE", 96, 59, 32), new Entry("GREEN_CONCRETE", 73, 91, 37), new Entry("RED_CONCRETE", 141, 35, 35), new Entry("BLACK_CONCRETE", 0, 0, 0));

        private ConcretePalette() {
        }

        public static BlockData getConcrete(Color color) {
            BlockData best = ConcretePalette.entries.get((int)0).data;
            long bestDistSq = Long.MAX_VALUE;
            for (Entry e : entries) {
                long dist = ConcretePalette.calcColourDistanceSq(e.color, color);
                if (dist >= bestDistSq) continue;
                bestDistSq = dist;
                best = e.data;
            }
            return best;
        }

        private static long calcColourDistanceSq(Color c1, Color c2) {
            long rmean = ((long)c1.getRed() + (long)c2.getRed()) / 2L;
            long r = (long)c1.getRed() - (long)c2.getRed();
            long g = (long)c1.getGreen() - (long)c2.getGreen();
            long b = (long)c1.getBlue() - (long)c2.getBlue();
            return ((512L + rmean) * r * r >> 8) + 4L * g * g + ((767L - rmean) * b * b >> 8);
        }

        private static class Entry {
            public final BlockData data;
            public final Color color;

            public Entry(String name, int r, int g, int b) {
                this.data = BlockData.fromMaterial((Material)MaterialUtil.getMaterial((String)name));
                this.color = Color.fromRGB((int)r, (int)g, (int)b);
            }
        }
    }

    private static class DisplayTask {
        public final int entityId;
        public final Color color;
        public final DataWatcher metadata;
        public int age;
        public int brightness;

        public DisplayTask(int entityId, Color color, DataWatcher metadata) {
            this.entityId = entityId;
            this.color = color;
            this.metadata = metadata;
            this.age = 0;
        }

        public void applyBrightness() {
            int brightness = this.age % 8;
            if (brightness > 4) {
                brightness = 8 - brightness;
            }
            brightness *= 2;
            this.metadata.set(DisplayHandle.DATA_BRIGHTNESS_OVERRIDE, (Object)Brightness.blockLight((int)(brightness += 7)));
            this.metadata.set(DisplayHandle.DATA_GLOW_COLOR_OVERRIDE, (Object)Color.fromRGB((int)(this.color.getRed() * brightness / 15), (int)(this.color.getGreen() * brightness / 15), (int)(this.color.getBlue() * brightness / 15)).asRGB());
        }

        public boolean update(Player viewer) {
            if (++this.age >= 100) {
                PacketUtil.sendPacket((Player)viewer, (PacketHandle)ClientboundRemoveEntitiesPacketHandle.createNewSingle((int)this.entityId));
                return true;
            }
            this.applyBrightness();
            PacketUtil.sendPacket((Player)viewer, (PacketHandle)ClientboundSetEntityDataPacketHandle.createNew((int)this.entityId, (DataWatcher)this.metadata, (boolean)false));
            return false;
        }
    }
}

