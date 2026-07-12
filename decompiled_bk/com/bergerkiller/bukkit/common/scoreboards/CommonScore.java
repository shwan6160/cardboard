/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.scoreboards;

import com.bergerkiller.bukkit.common.scoreboards.CommonScoreboard;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundResetScorePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetScorePacketHandle;

public class CommonScore {
    private CommonScoreboard scoreboard;
    private String name;
    private String objName;
    private int value;
    private boolean created;

    protected CommonScore(CommonScoreboard scoreboard, String name, String objName) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.objName = objName;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void update() {
        if (!this.created) {
            return;
        }
        PacketUtil.sendPacket(this.scoreboard.getPlayer(), ClientboundSetScorePacketHandle.createNew(this.name, this.objName, this.value));
    }

    protected void create() {
        if (this.created) {
            return;
        }
        this.created = true;
        this.update();
    }

    protected void remove() {
        if (!this.created) {
            return;
        }
        PacketUtil.sendPacket(this.scoreboard.getPlayer(), ClientboundResetScorePacketHandle.createNew(this.name, this.objName));
        this.created = false;
    }

    protected static CommonScore copyFrom(CommonScoreboard board, CommonScore from) {
        CommonScore to = new CommonScore(board, from.name, from.objName);
        to.setValue(from.getValue());
        return to;
    }
}

