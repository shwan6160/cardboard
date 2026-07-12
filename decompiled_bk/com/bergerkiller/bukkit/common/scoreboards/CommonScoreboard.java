/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.DisplaySlot
 */
package com.bergerkiller.bukkit.common.scoreboards;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.scoreboards.CommonObjective;
import com.bergerkiller.bukkit.common.scoreboards.CommonTeam;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class CommonScoreboard {
    private static Map<Player, CommonScoreboard> boards = new WeakHashMap<Player, CommonScoreboard>();
    private static Map<String, CommonTeam> teams = new HashMap<String, CommonTeam>();
    public static final CommonTeam dummyTeam = new CommonTeam("dummy"){
        private static final long serialVersionUID = 2284488822613734842L;

        @Override
        public void addPlayer(OfflinePlayer player) {
        }

        @Override
        public void removePlayer(OfflinePlayer player) {
        }

        @Override
        public void show() {
        }

        @Override
        public void hide() {
        }

        @Override
        public void setDisplayName(String displayName) {
        }

        @Override
        public void setPrefix(String prefix) {
        }

        @Override
        public void setSuffix(String suffic) {
        }

        @Override
        public void setFriendlyFire(CommonTeam.FriendlyFireType friendlyFire) {
        }

        @Override
        public void send(Player player) {
        }

        @Override
        public void setSendToAll(boolean sendToAll) {
        }
    };
    private CommonTeam team;
    private CommonObjective[] objectives = new CommonObjective[DisplaySlot.values().length];
    private final WeakReference<Player> player;

    private CommonScoreboard(Player player) {
        this.team = dummyTeam;
        this.player = new WeakReference<Player>(player);
        for (int i = 0; i < this.objectives.length; ++i) {
            DisplaySlot display = DisplaySlot.values()[i];
            this.objectives[i] = new CommonObjective(this, display);
        }
    }

    public Player getPlayer() {
        Player player = (Player)this.player.get();
        if (player == null) {
            throw new RuntimeException("The Player referenced by this Scoreboard is no longer online/available");
        }
        return player;
    }

    public CommonObjective getObjective(DisplaySlot display) {
        return this.objectives[display.ordinal()];
    }

    public CommonTeam getTeam() {
        return this.team;
    }

    public void setTeam(CommonTeam team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null!");
        }
        this.team = team;
    }

    public static CommonTeam newTeam(String name) {
        CommonTeam team = new CommonTeam(name);
        teams.put(name, team);
        return team;
    }

    public static CommonTeam loadTeam(String name) {
        CommonTeam team = null;
        try {
            File dir = CommonPlugin.getInstance().getDataFolder();
            dir.mkdir();
            dir = new File(dir, "teams");
            dir.mkdir();
            File file = new File(dir, name + ".bin");
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                team = (CommonTeam)ois.readObject();
                ois.close();
            }
        }
        catch (Exception e) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to load team from disk", e);
        }
        if (team == null) {
            team = new CommonTeam(name);
        }
        teams.put(name, team);
        return team;
    }

    public static void saveTeam(CommonTeam team) {
        try {
            File dir = CommonPlugin.getInstance().getDataFolder();
            dir.mkdir();
            dir = new File(dir, "teams");
            dir.mkdir();
            File file = new File(dir, team.getName() + ".bin");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(team);
            oos.flush();
            oos.close();
        }
        catch (Exception e) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to save team to disk", e);
        }
    }

    public static CommonTeam[] getTeams() {
        return teams.values().toArray(new CommonTeam[0]);
    }

    public static CommonTeam getTeam(String name) {
        return teams.get(name);
    }

    public static CommonScoreboard copyFrom(Player player, CommonScoreboard from) {
        CommonScoreboard board = new CommonScoreboard(player);
        for (int i = 0; i < board.objectives.length; ++i) {
            DisplaySlot display = DisplaySlot.values()[i];
            board.objectives[i] = CommonObjective.copyFrom(board, from.getObjective(display));
        }
        return board;
    }

    public static void removePlayer(Player player) {
        CommonScoreboard board = boards.remove(player);
        if (board != null) {
            board.player.clear();
        }
    }

    public static CommonScoreboard get(Player player) {
        CommonScoreboard board = boards.get(player);
        if (board == null) {
            board = new CommonScoreboard(player);
            boards.put(player, board);
        }
        return board;
    }
}

