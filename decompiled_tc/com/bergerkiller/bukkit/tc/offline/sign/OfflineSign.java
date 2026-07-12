/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.generated.org.bukkit.block.SignHandle
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignSide;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class OfflineSign {
    private static final String EMPTY_STR = "";
    private final OfflineSignSide side;
    private final String[] lines;

    protected OfflineSign(OfflineBlock block, boolean front, String[] lines) {
        this.side = OfflineSignSide.of(block, front);
        this.lines = new String[4];
        for (int i = 0; i < lines.length; ++i) {
            this.lines[i] = lines[i].isEmpty() ? EMPTY_STR : lines[i];
        }
    }

    public OfflineSignSide getSide() {
        return this.side;
    }

    public OfflineWorld getWorld() {
        return this.side.getWorld();
    }

    public OfflineBlock getBlock() {
        return this.side.getBlock();
    }

    public boolean isFrontText() {
        return this.side.isFrontText();
    }

    public UUID getWorldUUID() {
        return this.side.getWorldUUID();
    }

    public World getLoadedWorld() {
        return this.side.getLoadedWorld();
    }

    public IntVector3 getPosition() {
        return this.side.getPosition();
    }

    public Block getLoadedBlock() {
        return this.side.getLoadedBlock();
    }

    public String[] getLines() {
        return this.lines;
    }

    public String getLine(int index) {
        return this.lines[index];
    }

    public boolean verify(Sign sign) {
        SignHandle signHandle = SignHandle.createHandle((Object)sign);
        if (this.side.isFrontText()) {
            for (int n = 0; n < 4; ++n) {
                if (signHandle.getFrontLine(n).equals(this.lines[n])) continue;
                return false;
            }
        } else {
            for (int n = 0; n < 4; ++n) {
                if (signHandle.getBackLine(n).equals(this.lines[n])) continue;
                return false;
            }
        }
        return true;
    }

    public String toString() {
        OfflineBlock block = this.side.getBlock();
        World world = block.getLoadedWorld();
        return String.format("OfflineSign{world=%s, x=%d, y=%d, z=%d, side=%s, lines=[%s | %s | %s | %s]}", world != null ? world.getName() : "uuid_" + block.getWorldUUID(), block.getX(), block.getY(), block.getZ(), this.side.isFrontText() ? "front" : "back", this.lines[0], this.lines[1], this.lines[2], this.lines[3]);
    }

    public static OfflineSign readFrom(DataInputStream stream) throws IOException {
        OfflineBlock block = OfflineBlock.readFrom((DataInputStream)stream);
        boolean front = stream.readBoolean();
        String[] lines = new String[4];
        for (int n = 0; n < 4; ++n) {
            lines[n] = stream.readUTF();
        }
        return new OfflineSign(block, front, lines);
    }

    public static void writeTo(DataOutputStream stream, OfflineSign sign) throws IOException {
        OfflineBlock.writeTo((DataOutputStream)stream, (OfflineBlock)sign.getBlock());
        stream.writeBoolean(sign.isFrontText());
        for (String line : sign.getLines()) {
            stream.writeUTF(line);
        }
    }

    public static OfflineSign fromSign(Sign sign, boolean isFrontText) {
        OfflineBlock signBlock = OfflineWorld.of((World)sign.getWorld()).getBlockAt(sign.getX(), sign.getY(), sign.getZ());
        SignHandle signHandle = SignHandle.createHandle((Object)sign);
        return new OfflineSign(signBlock, isFrontText, isFrontText ? signHandle.getFrontLines() : signHandle.getBackLines());
    }
}

