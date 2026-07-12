/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import java.util.function.Function;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public enum SignSide {
    FRONT(SignHandle::getFrontLines, SignHandle::getFrontLine, SignHandle::setFrontLine, SignChangeTracker::getFrontLines, SignChangeTracker::getFrontLine, SignChangeTracker::setFrontLine, true),
    BACK(SignHandle::getBackLines, SignHandle::getBackLine, SignHandle::setBackLine, SignChangeTracker::getBackLines, SignChangeTracker::getBackLine, SignChangeTracker::setBackLine, CommonCapabilities.HAS_SIGN_BACK_TEXT);

    private final Function<SignHandle, String[]> bukkitAllLinesGetter;
    private final BukkitLineGetterFunc bukkitLineGetter;
    private final BukkitLineSetterFunc bukkitLineSetter;
    private final Function<SignChangeTracker, String[]> trackerAllLinesGetter;
    private final SignTrackerLineGetterFunc trackerLineGetter;
    private final SignTrackerLineSetterFunc trackerLineSetter;
    private final boolean supported;
    private SignSide opposite;

    private SignSide(Function<SignHandle, String[]> bukkitAllLinesGetter, BukkitLineGetterFunc bukkitLineGetter, BukkitLineSetterFunc bukkitLineSetter, Function<SignChangeTracker, String[]> trackerAllLinesGetter, SignTrackerLineGetterFunc trackerLineGetter, SignTrackerLineSetterFunc trackerLineSetter, boolean supported) {
        this.bukkitAllLinesGetter = bukkitAllLinesGetter;
        this.bukkitLineGetter = bukkitLineGetter;
        this.bukkitLineSetter = bukkitLineSetter;
        this.trackerAllLinesGetter = trackerAllLinesGetter;
        this.trackerLineGetter = trackerLineGetter;
        this.trackerLineSetter = trackerLineSetter;
        this.supported = supported;
    }

    public SignSide opposite() {
        return this.opposite;
    }

    public boolean isSupported() {
        return this.supported;
    }

    public boolean isFront() {
        return this == FRONT;
    }

    public BlockFace getFacing(Block signBlock) {
        BlockFace facing = BlockUtil.getFacing(signBlock);
        if (this == BACK) {
            facing = facing.getOppositeFace();
        }
        return facing;
    }

    public BlockFace getFacing(BlockData signBlockData) {
        BlockFace facing = signBlockData.getFacingDirection();
        if (this == BACK) {
            facing = facing.getOppositeFace();
        }
        return facing;
    }

    public <T> T selectFront(T selfSideValue, T oppositeSideValue) {
        return this == FRONT ? selfSideValue : oppositeSideValue;
    }

    public <T> T selectBack(T selfSideValue, T oppositeSideValue) {
        return this == BACK ? selfSideValue : oppositeSideValue;
    }

    public String[] getLines(SignChangeTracker signChangeTracker) {
        return this.trackerAllLinesGetter.apply(signChangeTracker);
    }

    public String getLine(SignChangeTracker signChangeTracker, int index) {
        return this.trackerLineGetter.get(signChangeTracker, index);
    }

    public void setLine(SignChangeTracker signChangeTracker, int index, String text) {
        this.trackerLineSetter.set(signChangeTracker, index, text);
    }

    @Deprecated
    public String[] getLines(Sign sign) {
        return this.bukkitAllLinesGetter.apply(SignHandle.createHandle(sign));
    }

    @Deprecated
    public String getLine(Sign sign, int index) {
        return this.bukkitLineGetter.get(SignHandle.createHandle(sign), index);
    }

    @Deprecated
    public void setLine(Sign sign, int index, String text) {
        this.bukkitLineSetter.set(SignHandle.createHandle(sign), index, text);
    }

    @Deprecated
    public String[] getLines(SignHandle sign) {
        return this.bukkitAllLinesGetter.apply(sign);
    }

    @Deprecated
    public String getLine(SignHandle sign, int index) {
        return this.bukkitLineGetter.get(sign, index);
    }

    @Deprecated
    public void setLine(SignHandle sign, int index, String text) {
        this.bukkitLineSetter.set(sign, index, text);
    }

    public static SignSide sideChanged(SignChangeEvent event) {
        return SignHandle.isChangingFrontLines(event) ? FRONT : BACK;
    }

    public static SignSide byFront(boolean front) {
        return front ? FRONT : BACK;
    }

    static {
        SignSide.FRONT.opposite = BACK;
        SignSide.BACK.opposite = FRONT;
    }

    @FunctionalInterface
    private static interface BukkitLineGetterFunc {
        public String get(SignHandle var1, int var2);
    }

    @FunctionalInterface
    private static interface BukkitLineSetterFunc {
        public void set(SignHandle var1, int var2, String var3);
    }

    @FunctionalInterface
    private static interface SignTrackerLineGetterFunc {
        public String get(SignChangeTracker var1, int var2);
    }

    @FunctionalInterface
    private static interface SignTrackerLineSetterFunc {
        public void set(SignChangeTracker var1, int var2, String var3);
    }
}

