/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TickTracker;
import com.bergerkiller.bukkit.common.controller.Tickable;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddMobPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerPositionPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MapPlayerInput
implements Tickable {
    private int last_dx;
    private int last_dy;
    private int last_dz;
    private int curr_dx;
    private int curr_dy;
    private int curr_dz;
    private int recv_dx;
    private int recv_dy;
    private int recv_dz;
    private int key_repeat_timer;
    private int ticks_without_input = 0;
    private boolean has_input;
    private int _fakeMountId = -1;
    private Vector _fakeMountLastPos = new Vector();
    private boolean _fakeMountShown = false;
    private boolean _isIntercepting = false;
    private boolean _newInterceptState = false;
    private final TickTracker _inputTickTracker = new TickTracker();
    public final Player player;

    public MapPlayerInput(Player player) {
        this.player = player;
        this._inputTickTracker.setRunnable(() -> {
            this._isIntercepting = this._newInterceptState;
            this._newInterceptState = false;
            this.updateInterception(this._isIntercepting);
            if (!this.player.isInsideVehicle() && !this._fakeMountShown) {
                this.receiveInput(0, 0, 0);
            }
            this.last_dx = this.curr_dx;
            this.last_dy = this.curr_dy;
            this.last_dz = this.curr_dz;
            this.curr_dx = this.recv_dx;
            this.curr_dy = this.recv_dy;
            this.curr_dz = this.recv_dz;
            this.key_repeat_timer = this.curr_dx != 0 || this.curr_dy != 0 || this.curr_dz != 0 ? ++this.key_repeat_timer : 0;
        });
        this.reset();
    }

    public final boolean isFakeMountShown() {
        return this._fakeMountShown;
    }

    public boolean hasInput() {
        return this.has_input;
    }

    public boolean hasChanges() {
        return this.curr_dx != this.last_dx || this.curr_dy != this.last_dy || this.curr_dz != this.last_dz;
    }

    public int getRepeat() {
        return this.key_repeat_timer;
    }

    public boolean isRepeating() {
        return this.key_repeat_timer > 15;
    }

    public boolean isPressed(Key key) {
        switch (key.ordinal()) {
            case 0: {
                return this.curr_dx < 0;
            }
            case 1: {
                return this.curr_dx > 0;
            }
            case 3: {
                return this.curr_dy > 0;
            }
            case 2: {
                return this.curr_dy < 0;
            }
            case 4: {
                return this.curr_dz > 0;
            }
            case 5: {
                return this.curr_dz < 0;
            }
        }
        return false;
    }

    public boolean wasPressed(Key key) {
        switch (key.ordinal()) {
            case 0: {
                return this.last_dx < 0;
            }
            case 1: {
                return this.last_dx > 0;
            }
            case 3: {
                return this.last_dy > 0;
            }
            case 2: {
                return this.last_dy < 0;
            }
            case 4: {
                return this.last_dz > 0;
            }
            case 5: {
                return this.last_dz < 0;
            }
        }
        return false;
    }

    public int getLeftRight() {
        return this.curr_dx;
    }

    public int getUpDown() {
        return this.curr_dy;
    }

    public int getBackEnter() {
        return this.curr_dz;
    }

    public boolean leftPressed() {
        return this.curr_dx < 0 && this.last_dx >= 0;
    }

    public boolean leftReleased() {
        return this.last_dx < 0 && this.curr_dx >= 0;
    }

    public boolean left() {
        return this.curr_dx < 0;
    }

    public boolean rightPressed() {
        return this.curr_dx > 0 && this.last_dx <= 0;
    }

    public boolean rightReleased() {
        return this.last_dx > 0 && this.curr_dx <= 0;
    }

    public boolean right() {
        return this.curr_dx > 0;
    }

    public boolean upPressed() {
        return this.curr_dy < 0 && this.last_dy >= 0;
    }

    public boolean upReleased() {
        return this.last_dy < 0 && this.curr_dy >= 0;
    }

    public boolean up() {
        return this.curr_dy < 0;
    }

    public boolean downPressed() {
        return this.curr_dy > 0 && this.last_dy <= 0;
    }

    public boolean downReleased() {
        return this.last_dy > 0 && this.curr_dy <= 0;
    }

    public boolean down() {
        return this.curr_dy > 0;
    }

    public boolean enterPressed() {
        return this.curr_dz > 0 && this.last_dz <= 0;
    }

    public boolean enterReleased() {
        return this.last_dz > 0 && this.curr_dz <= 0;
    }

    public boolean enter() {
        return this.curr_dz > 0;
    }

    public boolean backPressed() {
        return this.curr_dz < 0 && this.last_dz >= 0;
    }

    public boolean backReleased() {
        return this.last_dz < 0 && this.curr_dz >= 0;
    }

    public boolean back() {
        return this.curr_dz < 0;
    }

    public void reset() {
        this.updateInputInterception(false);
        this.curr_dz = 0;
        this.curr_dy = 0;
        this.curr_dx = 0;
        this.last_dz = 0;
        this.last_dy = 0;
        this.last_dx = 0;
        this.recv_dz = 0;
        this.recv_dy = 0;
        this.recv_dx = 0;
        this.key_repeat_timer = 0;
        this.has_input = false;
    }

    public void onDisconnected() {
        this.reset();
    }

    @Override
    public void onTick() {
        this._inputTickTracker.update();
        if (++this.ticks_without_input == 40) {
            if (this.curr_dx != 0 || this.curr_dy != 0 || this.curr_dz != 0) {
                this.receiveInput(0, 0, 0);
            }
            this.ticks_without_input = 500;
        }
    }

    public void handleDisplayUpdate(MapDisplay display, boolean interceptInput) {
        MapKeyEvent event;
        boolean press_b;
        boolean press_a;
        this._inputTickTracker.update();
        this._newInterceptState |= interceptInput;
        if (!interceptInput) {
            return;
        }
        if (this.isRepeating()) {
            for (Key key : Key.values()) {
                press_a = this.wasPressed(key);
                press_b = this.isPressed(key);
                if (press_b) {
                    event = new MapKeyEvent(display, this, key);
                    display.onKeyPressed(event);
                    display.onKey(event);
                    display.getRootWidget().onKeyPressed(event);
                    display.getRootWidget().onKey(event);
                    continue;
                }
                if (!press_a) continue;
                event = new MapKeyEvent(display, this, key);
                display.onKeyReleased(event);
                display.getRootWidget().onKeyReleased(event);
            }
        }
        if (this.hasChanges()) {
            for (Key key : Key.values()) {
                press_a = this.wasPressed(key);
                press_b = this.isPressed(key);
                if (!press_a && press_b) {
                    event = new MapKeyEvent(display, this, key);
                    display.onKeyPressed(event);
                    display.getRootWidget().onKeyPressed(event);
                } else if (press_a && !press_b) {
                    event = new MapKeyEvent(display, this, key);
                    display.onKeyReleased(event);
                    display.getRootWidget().onKeyReleased(event);
                }
                if (!press_b) continue;
                event = new MapKeyEvent(display, this, key);
                display.onKey(event);
                display.getRootWidget().onKey(event);
            }
        } else {
            for (Key key : Key.values()) {
                if (!this.isPressed(key)) continue;
                MapKeyEvent event2 = new MapKeyEvent(display, this, key);
                display.onKey(event2);
                display.getRootWidget().onKey(event2);
            }
        }
    }

    public boolean receiveInput(int dx, int dy, int dz) {
        this.recv_dx = dx;
        this.recv_dy = dy;
        this.recv_dz = dz;
        this.has_input = true;
        if (dx == 0 && dy == 0 && dz == 0) {
            this.key_repeat_timer = 0;
        }
        this.keepAliveInput();
        if (!CommonCapabilities.VEHICLE_EXIT_CANCELLABLE && this._fakeMountShown && dz < 0) {
            new Task(CommonPlugin.getInstance()){

                @Override
                public void run() {
                    MapPlayerInput.this.sendMountPacket();
                }
            }.start(4L);
        }
        return this._isIntercepting;
    }

    public void keepAliveInput() {
        this.ticks_without_input = 0;
    }

    private void updateInterception(boolean intercept) {
        if (!intercept) {
            this.updateInputInterception(false);
            return;
        }
        if (this.player.isInsideVehicle()) {
            this.updateInputInterception(false);
            return;
        }
        if (!CommonCapabilities.VEHICLE_EXIT_CANCELLABLE && this.player.isSneaking()) {
            this.updateInputInterception(false);
            return;
        }
        if (!this.player.isFlying() && !this.player.isOnGround()) {
            EntityHandle playerHandle = EntityHandle.fromBukkit((Entity)this.player);
            double half_width = 0.5 * (double)playerHandle.getWidth();
            double below = 0.1;
            AABBHandle below_bounds = AABBHandle.createNew(playerHandle.getLocX() - half_width, playerHandle.getLocY() - below, playerHandle.getLocZ() - half_width, playerHandle.getLocX() + half_width, playerHandle.getLocY(), playerHandle.getLocZ() + half_width);
            if (LevelHandle.fromBukkit(this.player.getWorld()).isNotCollidingWithBlocks(playerHandle, below_bounds)) {
                this.updateInputInterception(false);
                return;
            }
        }
        this.updateInputInterception(true);
    }

    private void updateInputInterception(boolean intercept) {
        if (!intercept && this._fakeMountShown) {
            this._fakeMountShown = false;
            PacketUtil.sendPacket(this.player, PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(this._fakeMountId));
            Location loc = this.player.getLocation();
            ClientboundPlayerPositionPacketHandle positionPacket = ClientboundPlayerPositionPacketHandle.createAbsolute(loc);
            PacketUtil.sendPacket(this.player, positionPacket);
            return;
        }
        if (intercept) {
            Vector pos = this.player.getLocation().toVector();
            pos.setZ(pos.getZ() + 0.1);
            pos.setY(pos.getY() + 0.002);
            if (!this._fakeMountShown) {
                this._fakeMountShown = true;
                if (this._fakeMountId == -1) {
                    this._fakeMountId = EntityUtil.getUniqueEntityId();
                }
                this._fakeMountLastPos = pos;
                DataWatcher data = new DataWatcher();
                data.set(EntityHandle.DATA_FLAGS, (byte)32);
                data.set(LivingEntityHandle.DATA_HEALTH, Float.valueOf(10.0f));
                ClientboundAddMobPacketHandle packet = ClientboundAddMobPacketHandle.createNew();
                packet.setEntityId(this._fakeMountId);
                packet.setEntityUUID(UUID.randomUUID());
                packet.setEntityType(EntityType.CHICKEN);
                packet.setPosX(pos.getX());
                packet.setPosY(pos.getY());
                packet.setPosZ(pos.getZ());
                PacketUtil.sendEntityLivingSpawnPacket(this.player, packet, data);
                PacketUtil.sendPacket(this.player, ClientboundUpdateAttributesPacketHandle.createZeroMaxHealth(this._fakeMountId));
                this.sendMountPacket();
            }
            if (this._fakeMountId != -1 && !pos.equals((Object)this._fakeMountLastPos)) {
                this._fakeMountLastPos = pos;
                ClientboundEntityPositionSyncPacketHandle tp_packet = ClientboundEntityPositionSyncPacketHandle.createNew(this._fakeMountId, pos.getX(), pos.getY(), pos.getZ(), 0.0f, 0.0f, false);
                PacketUtil.sendPacket(this.player, tp_packet);
            }
        }
    }

    public void sendMountPacket() {
        if (this._fakeMountShown) {
            if (PacketType.OUT_MOUNT.getType() != null) {
                CommonPacket packet = PacketType.OUT_MOUNT.newInstance();
                packet.write(PacketType.OUT_MOUNT.entityId, this._fakeMountId);
                packet.write(PacketType.OUT_MOUNT.mountedEntityIds, new int[]{this.player.getEntityId()});
                PacketUtil.sendPacket(this.player, packet);
            } else {
                CommonPacket packet = PacketType.OUT_ENTITY_ATTACH.newInstance();
                packet.write(PacketType.OUT_ENTITY_ATTACH.vehicleId, this._fakeMountId);
                packet.write(PacketType.OUT_ENTITY_ATTACH.passengerId, this.player.getEntityId());
                PacketUtil.sendPacket(this.player, packet);
            }
        }
    }

    public static enum Key {
        LEFT(-1, 0, 0),
        RIGHT(1, 0, 0),
        UP(0, -1, 0),
        DOWN(0, 1, 0),
        ENTER(0, 0, 1),
        BACK(0, 0, -1);

        private final int _dx;
        private final int _dy;
        private final int _dz;

        private Key(int dx, int dy, int dz) {
            this._dx = dx;
            this._dy = dy;
            this._dz = dz;
        }

        public int dx() {
            return this._dx;
        }

        public int dy() {
            return this._dy;
        }

        public int dz() {
            return this._dz;
        }
    }
}

