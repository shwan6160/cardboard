/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.block.SignLineAccessor;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.SignBlockEntityHandle;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockHandle;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class SignChangeTracker
implements Cloneable,
SignLineAccessor {
    private final Block block;
    private Sign state;
    private BlockData blockData;
    private SignBlockEntityHandle tileEntity;
    private Object[] lastRawFrontLines;
    private Object[] lastRawBackLines;
    private String[] lastMessageFrontLines;
    private String[] lastMessageBackLines;
    private static final Function<Block, SignChangeTracker> constructor;

    protected SignChangeTracker(Block block) {
        this.block = block;
        this.state = null;
        this.resetTileEntity();
    }

    private void initState(Sign state) {
        this.state = state;
        this.loadTileEntity(SignBlockEntityHandle.fromBukkit(state));
    }

    private void loadTileEntity(SignBlockEntityHandle tile) {
        if (tile == null) {
            this.resetTileEntity();
        } else {
            this.tileEntity = tile;
            this.lastRawFrontLines = (Object[])tile.getRawFrontLines().clone();
            this.lastRawBackLines = CommonCapabilities.HAS_SIGN_BACK_TEXT ? (Object[])tile.getRawBackLines().clone() : null;
            this.lastMessageFrontLines = null;
            this.lastMessageBackLines = null;
            this.blockData = this.isTileRemoved(tile) ? WorldUtil.getBlockData(this.block) : tile.getBlockData();
        }
    }

    private void resetTileEntity() {
        this.tileEntity = null;
        this.lastRawFrontLines = null;
        this.lastRawBackLines = null;
        this.lastMessageFrontLines = null;
        this.lastMessageBackLines = null;
        this.blockData = null;
    }

    public World getWorld() {
        return this.block.getWorld();
    }

    public int getX() {
        return this.block.getX();
    }

    public int getY() {
        return this.block.getY();
    }

    public int getZ() {
        return this.block.getZ();
    }

    @Override
    public String getLine(SignSide side, int index) {
        return side.getLine(this, index);
    }

    @Override
    public void setLine(SignSide side, int index, String text) {
        side.setLine(this, index, text);
    }

    @Override
    public String[] getLines(SignSide side) {
        return side.getLines(this);
    }

    @Override
    public String getFrontLine(int index) {
        return this.getFrontLines()[index];
    }

    @Override
    public void setFrontLine(int index, String text) {
        this.checkRemoved();
        if (this.lastMessageFrontLines != null) {
            this.lastMessageFrontLines[index] = text;
        }
        SignHandle.T.setFrontLine.invoke(this.state, index, text);
        this.state.update(true);
    }

    @Override
    public String[] getFrontLines() {
        this.checkRemoved();
        String[] lines = this.lastMessageFrontLines;
        if (lines == null) {
            lines = this.tileEntity.getMessageFrontLines();
            this.lastMessageFrontLines = lines;
        }
        return lines;
    }

    @Override
    public ChatText getFormattedFrontLine(int index) {
        this.checkRemoved();
        return ChatText.fromComponent(this.tileEntity.getRawFrontLines()[index]);
    }

    @Override
    public void setFormattedFrontLine(int index, ChatText text) {
        this.checkRemoved();
        this.tileEntity.setFormattedFrontLine(index, text);
        this.lastMessageFrontLines = null;
        this.state = this.tileEntity.toBukkit();
    }

    @Override
    public ChatText[] getFormattedFrontLines() {
        this.checkRemoved();
        return LogicUtil.mapArray(this.tileEntity.getRawFrontLines(), ChatText.class, ChatText::fromComponent);
    }

    @Override
    public String getBackLine(int index) {
        return this.getBackLines()[index];
    }

    @Override
    public void setBackLine(int index, String text) {
        this.checkRemoved();
        if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
            if (this.lastMessageBackLines != null) {
                this.lastMessageBackLines[index] = text;
            }
            SignHandle.T.setBackLine.invoke(this.state, index, text);
            this.state.update(true);
        }
    }

    @Override
    public String[] getBackLines() {
        this.checkRemoved();
        String[] lines = this.lastMessageBackLines;
        if (lines == null) {
            lines = this.tileEntity.getMessageBackLines();
            this.lastMessageBackLines = lines;
        }
        return lines;
    }

    @Override
    public ChatText getFormattedBackLine(int index) {
        this.checkRemoved();
        return ChatText.fromComponent(this.tileEntity.getRawBackLines()[index]);
    }

    @Override
    public void setFormattedBackLine(int index, ChatText text) {
        this.checkRemoved();
        if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
            this.tileEntity.setFormattedBackLine(index, text);
            this.lastMessageBackLines = null;
            this.state = this.tileEntity.toBukkit();
        }
    }

    @Override
    public ChatText[] getFormattedBackLines() {
        this.checkRemoved();
        return LogicUtil.mapArray(this.tileEntity.getRawBackLines(), ChatText.class, ChatText::fromComponent);
    }

    public SignLineAccessor liveUpdatingAccessor() {
        return new SignLineAccessor(){

            @Override
            public String getLine(SignSide side, int index) {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getLine(side, index);
            }

            @Override
            public void setLine(SignSide side, int index, String text) {
                SignChangeTracker.this.setLine(side, index, text);
            }

            @Override
            public String[] getLines(SignSide side) {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getLines(side);
            }

            @Override
            public String getFrontLine(int index) {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getFrontLine(index);
            }

            @Override
            public void setFrontLine(int index, String text) {
                SignChangeTracker.this.setFrontLine(index, text);
            }

            @Override
            public String[] getFrontLines() {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getFrontLines();
            }

            @Override
            public ChatText getFormattedFrontLine(int index) {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getFormattedFrontLine(index);
            }

            @Override
            public void setFormattedFrontLine(int index, ChatText text) {
                SignChangeTracker.this.setFormattedFrontLine(index, text);
            }

            @Override
            public ChatText[] getFormattedFrontLines() {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getFormattedFrontLines();
            }

            @Override
            public String getBackLine(int index) {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getBackLine(index);
            }

            @Override
            public void setBackLine(int index, String text) {
                SignChangeTracker.this.setBackLine(index, text);
            }

            @Override
            public String[] getBackLines() {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getBackLines();
            }

            @Override
            public ChatText getFormattedBackLine(int index) {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getFormattedBackLine(index);
            }

            @Override
            public void setFormattedBackLine(int index, ChatText text) {
                SignChangeTracker.this.setFormattedBackLine(index, text);
            }

            @Override
            public ChatText[] getFormattedBackLines() {
                SignChangeTracker.this.update();
                return SignChangeTracker.this.getFormattedBackLines();
            }
        };
    }

    public BlockFace getAttachedFace() {
        this.checkRemoved();
        return this.blockData.getAttachedFace();
    }

    public BlockFace getFacing() {
        this.checkRemoved();
        return this.blockData.getFacingDirection();
    }

    public boolean isAttachedTo(Block block) {
        Block signBlock = this.getBlock();
        BlockFace attachedFace = this.getAttachedFace();
        return block.getX() - signBlock.getX() == attachedFace.getModX() && block.getY() - signBlock.getY() == attachedFace.getModY() && block.getZ() - signBlock.getZ() == attachedFace.getModZ();
    }

    public boolean isRemoved() {
        return this.state == null;
    }

    public Block getBlock() {
        return this.block;
    }

    public BlockData getBlockData() {
        return this.blockData;
    }

    public Sign getSign() {
        return this.state;
    }

    public boolean update() {
        SignBlockEntityHandle tileEntity = this.tileEntity;
        if (tileEntity == null) {
            return this.tryLoadFromWorld();
        }
        if (this.isTileRemoved(tileEntity)) {
            if (this.tryLoadFromWorld()) {
                return true;
            }
            this.state = null;
            this.resetTileEntity();
            return true;
        }
        boolean blockDataChanged = false;
        Object newBlockDataRaw = tileEntity.getRawBlockData();
        if (newBlockDataRaw != this.blockData.getData()) {
            this.blockData = BlockData.fromBlockData(newBlockDataRaw);
            blockDataChanged = true;
        }
        return this.detectChangedLines(tileEntity) || blockDataChanged;
    }

    private void checkRemoved() {
        if (this.isRemoved()) {
            if (this.block == null) {
                throw new IllegalStateException("Sign is removed");
            }
            throw new IllegalStateException("Sign at world=" + this.block.getWorld().getName() + " x=" + this.block.getX() + " y=" + this.block.getY() + " z=" + this.block.getZ() + " is removed");
        }
    }

    protected boolean isTileRemoved(SignBlockEntityHandle tileEntity) {
        return tileEntity.isRemoved();
    }

    private boolean detectChangedLines(SignBlockEntityHandle tileEntity) {
        Object[] oldRawFrontLines = this.lastRawFrontLines;
        Object[] newRawFrontLines = tileEntity.getRawFrontLines();
        Object[] oldRawBackLines = this.lastRawBackLines;
        Object[] newRawBackLines = tileEntity.getRawBackLines();
        if (oldRawFrontLines.length != newRawFrontLines.length || CommonCapabilities.HAS_SIGN_BACK_TEXT && oldRawBackLines.length != newRawBackLines.length) {
            this.lastRawFrontLines = (Object[])newRawFrontLines.clone();
            if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
                this.lastRawBackLines = (Object[])newRawBackLines.clone();
            }
        } else if (!SignChangeTracker.copyLinesCheckChanges(oldRawFrontLines, newRawFrontLines)) {
            if (!CommonCapabilities.HAS_SIGN_BACK_TEXT || !SignChangeTracker.copyLinesCheckChanges(oldRawBackLines, newRawBackLines)) {
                return false;
            }
        } else if (CommonCapabilities.HAS_SIGN_BACK_TEXT) {
            System.arraycopy(oldRawBackLines, 0, newRawBackLines, 0, newRawBackLines.length);
        }
        this.lastMessageFrontLines = null;
        this.lastMessageBackLines = null;
        this.state = this.tileEntity.toBukkit();
        return true;
    }

    private static boolean copyLinesCheckChanges(Object[] oldRawLines, Object[] newRawLines) {
        int numLines = newRawLines.length;
        for (int line = 0; line < numLines; ++line) {
            Object newLine = newRawLines[line];
            if (oldRawLines[line] == newLine) continue;
            oldRawLines[line] = newLine;
            while (++line < numLines) {
                oldRawLines[line] = newRawLines[line];
            }
            return true;
        }
        return false;
    }

    private boolean tryLoadFromWorld() {
        Object rawTileEntity;
        Block block = this.block;
        if (((Boolean)MaterialUtil.ISSIGN.get(block)).booleanValue() && (rawTileEntity = CraftBlockHandle.getBlockTileEntity(block)) != null && SignBlockEntityHandle.T.isAssignableFrom(rawTileEntity)) {
            SignBlockEntityHandle tileEntity = SignBlockEntityHandle.createHandle(rawTileEntity);
            this.state = tileEntity.toBukkit();
            this.loadTileEntity(tileEntity);
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("SignChangeTracker{block=");
        if (this.block == null) {
            str.append("null");
        } else {
            str.append("{world=").append(this.block.getWorld().getName()).append(", x=").append(this.block.getX()).append(", y=").append(this.block.getY()).append(", z=").append(this.block.getZ()).append('}');
        }
        str.append(", lines=");
        Sign sign = this.getSign();
        if (sign == null) {
            str.append("<REMOVED>");
        } else {
            str.append('[');
            for (int i = 0; i < 4; ++i) {
                if (i > 0) {
                    str.append(", ");
                }
                str.append(sign.getLine(i));
            }
            str.append(']');
        }
        str.append('}');
        return str.toString();
    }

    public SignChangeTracker clone() {
        SignChangeTracker clone = new SignChangeTracker(this.block);
        clone.state = this.state;
        clone.blockData = this.blockData;
        clone.tileEntity = this.tileEntity;
        clone.lastRawFrontLines = this.lastRawFrontLines;
        clone.lastRawBackLines = this.lastRawBackLines;
        clone.lastMessageFrontLines = this.lastMessageFrontLines;
        clone.lastMessageBackLines = this.lastMessageBackLines;
        return clone;
    }

    public static SignChangeTracker track(Sign sign) {
        if (sign == null) {
            throw new IllegalArgumentException("Sign is null");
        }
        SignChangeTracker tracker = constructor.apply(sign.getBlock());
        tracker.initState(sign);
        return tracker;
    }

    public static SignChangeTracker track(Block signBlock) {
        SignChangeTracker tracker = constructor.apply(signBlock);
        Sign state = BlockUtil.getSign(signBlock);
        if (state != null) {
            tracker.initState(state);
        }
        return tracker;
    }

    static {
        Function<Block, SignChangeTracker> constr = SignChangeTracker::new;
        if (Common.evaluateMCVersion("<=", "1.12.2") && Common.SERVER.isForgeServer()) {
            try {
                FastField tileEntityListField = new FastField();
                tileEntityListField.init(Resolver.resolveAndGetDeclaredField(LevelHandle.T.getType(), "tileEntityList"));
                constr = block -> {
                    List worldTileEntities = (List)tileEntityListField.get(HandleConversion.toWorldHandle(block.getWorld()));
                    return new SignChangeTrackerMohistLegacy((Block)block, worldTileEntities);
                };
            }
            catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "[Mohist Compat] Failed to find World tileEntityList field", t);
            }
        }
        constructor = constr;
    }

    private static class SignChangeTrackerMohistLegacy
    extends SignChangeTracker {
        private final List<Object> worldTileEntities;
        private int lastIndex = -1;

        protected SignChangeTrackerMohistLegacy(Block block, List<Object> worldTileEntities) {
            super(block);
            this.worldTileEntities = worldTileEntities;
        }

        @Override
        protected boolean isTileRemoved(SignBlockEntityHandle tileEntity) {
            List<Object> list = this.worldTileEntities;
            Object rawTileEntity = tileEntity.getRaw();
            if (this.lastIndex >= 0 && this.lastIndex < list.size() && list.get(this.lastIndex) == rawTileEntity) {
                return false;
            }
            this.lastIndex = list.indexOf(rawTileEntity);
            return this.lastIndex == -1;
        }
    }
}

