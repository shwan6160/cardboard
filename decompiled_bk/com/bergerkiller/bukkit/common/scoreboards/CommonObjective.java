/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.scoreboard.DisplaySlot
 */
package com.bergerkiller.bukkit.common.scoreboards;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.scoreboards.CommonScore;
import com.bergerkiller.bukkit.common.scoreboards.CommonScoreboard;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.scoreboard.DisplaySlot;

public class CommonObjective {
    private DisplaySlot display;
    private CommonScoreboard scoreboard;
    private String name;
    private String displayName;
    private boolean displayed;
    private Map<String, CommonScore> scores = new HashMap<String, CommonScore>();

    protected CommonObjective(CommonScoreboard scoreboard, DisplaySlot display) {
        this.display = display;
        this.scoreboard = scoreboard;
        this.name = display.name();
        this.displayName = display.name();
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public DisplaySlot getDisplay() {
        return this.display;
    }

    public void setDisplayName(String value) {
        this.displayName = value;
        this.update();
    }

    public Collection<CommonScore> getScores() {
        return this.scores.values();
    }

    public CommonScore getScore(String name) {
        return this.scores.get(name);
    }

    public void addScore(String name, CommonScore score) {
        CommonScore old = this.getScore(name);
        if (old != null) {
            old.remove();
        }
        score.create();
        this.scores.put(name, score);
    }

    public void removeScore(String name) {
        CommonScore score = this.getScore(name);
        if (score != null) {
            score.remove();
        }
        this.scores.remove(name);
    }

    public CommonScore createScore(String name, String displayName, int value) {
        CommonScore score = new CommonScore(this.scoreboard, displayName, this.name);
        score.setValue(value);
        this.addScore(name, score);
        return score;
    }

    public void clearScores() {
        Iterator<String> it = this.scores.keySet().iterator();
        while (it.hasNext()) {
            CommonScore score = this.getScore(it.next());
            score.remove();
            it.remove();
        }
    }

    public void update() {
        if (!this.displayed) {
            return;
        }
        this.handle(2);
    }

    public void show() {
        if (!this.displayed) {
            this.handle(0);
        }
        this.display();
        this.displayed = true;
    }

    public void hide() {
        if (!this.displayed) {
            return;
        }
        this.handle(1);
        this.displayed = false;
    }

    private void handle(int type) {
        CommonPacket packet = new CommonPacket(PacketType.OUT_SCOREBOARD_OBJECTIVE);
        packet.write(PacketType.OUT_SCOREBOARD_OBJECTIVE.name, this.name);
        packet.write(PacketType.OUT_SCOREBOARD_OBJECTIVE.displayName, ChatText.fromMessage(this.displayName));
        packet.write(PacketType.OUT_SCOREBOARD_OBJECTIVE.action, type);
        PacketUtil.sendPacket(this.scoreboard.getPlayer(), packet);
    }

    private void display() {
        CommonPacket packet = new CommonPacket(PacketType.OUT_SCOREBOARD_DISPLAY_OBJECTIVE);
        packet.write(PacketType.OUT_SCOREBOARD_DISPLAY_OBJECTIVE.name, this.name);
        packet.write(PacketType.OUT_SCOREBOARD_DISPLAY_OBJECTIVE.display, this.display);
        PacketUtil.sendPacket(this.scoreboard.getPlayer(), packet);
    }

    protected static CommonObjective copyFrom(CommonScoreboard board, CommonObjective objective) {
        CommonObjective obj = new CommonObjective(board, objective.display);
        for (Map.Entry<String, CommonScore> entry : objective.scores.entrySet()) {
            String id = entry.getKey();
            CommonScore score = entry.getValue();
            CommonScore newScore = CommonScore.copyFrom(board, score);
            obj.addScore(id, newScore);
        }
        return obj;
    }
}

