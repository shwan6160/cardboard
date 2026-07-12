/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacketHandle
 *  com.bergerkiller.mountiplex.reflection.util.UniqueHash
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.GlowColorTeamProvider;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacketHandle;
import com.bergerkiller.mountiplex.reflection.util.UniqueHash;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamProvider
implements LibraryComponent {
    private final TrainCarts plugin;
    private final UniqueHash teamIdHash = new UniqueHash();
    private final Map<Player, ViewerState> viewerStates = new HashMap<Player, ViewerState>();
    private final Set<ViewerState.ViewedTeam> pendingTeamUpdates = new HashSet<ViewerState.ViewedTeam>();
    private final Task updateTask;
    private final Team disabledTeam = new Team(){

        @Override
        public void join(Player viewer, Iterable<UUID> entityUUIDs) {
            TeamProvider.this.reset(viewer, entityUUIDs);
        }

        @Override
        public void join(AttachmentViewer viewer, Iterable<UUID> entityUUIDs) {
            TeamProvider.this.reset(viewer, entityUUIDs);
        }

        @Override
        public void join(Player viewer, UUID entityUUID) {
            TeamProvider.this.reset(viewer, entityUUID);
        }

        @Override
        public void join(AttachmentViewer viewer, UUID entityUUID) {
            TeamProvider.this.reset(viewer, entityUUID);
        }
    };
    private final Team noCollisionTeam = this.buildTeam().visibility(false).collision(false).rememberEntities(false).build();
    private final GlowColorTeamProvider glowColors;

    public TeamProvider(TrainCarts plugin) {
        this.plugin = plugin;
        this.updateTask = new Task((JavaPlugin)plugin){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                TeamProvider teamProvider = TeamProvider.this;
                synchronized (teamProvider) {
                    Iterator iter = TeamProvider.this.pendingTeamUpdates.iterator();
                    while (iter.hasNext()) {
                        if (((ViewerState.ViewedTeam)iter.next()).update()) continue;
                        iter.remove();
                    }
                    TeamProvider.this.pendingTeamUpdates.forEach(ViewerState.ViewedTeam::assignEntities);
                    TeamProvider.this.pendingTeamUpdates.clear();
                }
            }
        };
        this.glowColors = new GlowColorTeamProvider(this);
    }

    public GlowColorTeamProvider glowColors() {
        return this.glowColors;
    }

    public Team disabledTeam() {
        return this.disabledTeam;
    }

    public Team noCollisionTeam() {
        return this.noCollisionTeam;
    }

    public synchronized void enable() {
    }

    public synchronized void disable() {
        if (!this.pendingTeamUpdates.isEmpty()) {
            this.pendingTeamUpdates.clear();
            this.updateTask.stop();
        }
        for (ViewerState state : this.viewerStates.values()) {
            state.teams.forEach(ViewerState.ViewedTeam::reset);
        }
        this.viewerStates.clear();
    }

    private void schedule(ViewerState.ViewedTeam viewedTeam) {
        if (this.pendingTeamUpdates.isEmpty() && this.updateTask.getPlugin().isEnabled()) {
            this.updateTask.start();
        }
        this.pendingTeamUpdates.add(viewedTeam);
    }

    public TeamBuilder buildTeam() {
        return new TeamBuilder("ZZTCTeam" + this.teamIdHash.nextHex());
    }

    public void reset(AttachmentViewer viewer, Iterable<UUID> entityUUIDs) {
        this.reset(viewer.getPlayer(), entityUUIDs);
    }

    public synchronized void reset(Player viewer, Iterable<UUID> entityUUIDs) {
        ViewerState state = this.viewerStates.get(viewer);
        if (state != null) {
            for (ViewerState.ViewedTeam team : state.teams) {
                for (UUID entityUUID : entityUUIDs) {
                    team.removeEntity(entityUUID);
                }
            }
        }
    }

    public void reset(AttachmentViewer viewer, UUID entityUUID) {
        this.reset(viewer.getPlayer(), entityUUID);
    }

    public synchronized void reset(Player viewer, UUID entityUUID) {
        block1: {
            ViewerState.ViewedTeam team;
            ViewerState state = this.viewerStates.get(viewer);
            if (state == null) break block1;
            Iterator iterator = state.teams.iterator();
            while (iterator.hasNext() && !(team = (ViewerState.ViewedTeam)iterator.next()).removeEntity(entityUUID)) {
            }
        }
    }

    public synchronized void reset(Player viewer) {
        ViewerState state = this.viewerStates.remove(viewer);
        if (state != null) {
            state.teams.forEach(ViewerState.ViewedTeam::reset);
        }
    }

    private final class ViewerState {
        private final AttachmentViewer viewer;
        private final ArrayList<ViewedTeam> teams;

        public ViewerState(Player viewer) {
            this(teamProvider.plugin.getAttachmentViewer(viewer));
        }

        public ViewerState(AttachmentViewer viewer) {
            this.viewer = viewer;
            this.teams = new ArrayList();
        }

        public void assignTeamEntities(Team team, Iterable<UUID> entityUUIDs) {
            ViewedTeam foundViewedTeam = null;
            for (ViewedTeam viewedTeam : this.teams) {
                if (viewedTeam.team == team) {
                    UUID eid;
                    boolean hasPendingRemove;
                    Iterator<UUID> iter = entityUUIDs.iterator();
                    if (!iter.hasNext()) {
                        return;
                    }
                    boolean bl = hasPendingRemove = !viewedTeam.pendingRemove.isEmpty();
                    while (viewedTeam.entities.contains(eid = iter.next())) {
                        if (hasPendingRemove) {
                            viewedTeam.pendingRemove.remove(eid.toString());
                        }
                        if (iter.hasNext()) continue;
                        return;
                    }
                    foundViewedTeam = viewedTeam;
                    continue;
                }
                for (UUID uuid : entityUUIDs) {
                    viewedTeam.removeEntity(uuid);
                }
            }
            Iterator<UUID> iter = entityUUIDs.iterator();
            if (!iter.hasNext()) {
                return;
            }
            if (foundViewedTeam == null) {
                foundViewedTeam = new ViewedTeam(team);
                this.teams.add(foundViewedTeam);
            }
            do {
                foundViewedTeam.addEntity(iter.next());
            } while (iter.hasNext());
        }

        public void assignTeamEntity(Team team, UUID entityUUID) {
            ViewedTeam foundViewedTeam = null;
            for (ViewedTeam viewedTeam : this.teams) {
                if (viewedTeam.team == team) {
                    if (viewedTeam.entities.contains(entityUUID)) {
                        if (!viewedTeam.pendingRemove.isEmpty()) {
                            viewedTeam.pendingRemove.remove(entityUUID.toString());
                        }
                        return;
                    }
                    foundViewedTeam = viewedTeam;
                    continue;
                }
                viewedTeam.removeEntity(entityUUID);
            }
            if (foundViewedTeam == null) {
                foundViewedTeam = new ViewedTeam(team);
                this.teams.add(foundViewedTeam);
            }
            foundViewedTeam.addEntity(entityUUID);
        }

        public final class ViewedTeam {
            public final Team team;
            public final Set<UUID> entities = new HashSet<UUID>();
            private Set<String> pendingAdd = Collections.emptySet();
            private Set<String> pendingRemove = Collections.emptySet();
            private boolean teamCreated;

            public ViewedTeam(Team team) {
                this.team = team;
                this.teamCreated = false;
            }

            public boolean addEntity(UUID entityUUID) {
                if (this.entities.add(entityUUID)) {
                    String entityUUIDStr = entityUUID.toString();
                    if (!this.pendingRemove.isEmpty() && this.pendingRemove.remove(entityUUIDStr)) {
                        return true;
                    }
                    if (this.pendingAdd.isEmpty()) {
                        this.pendingAdd = new HashSet<String>();
                    }
                    this.pendingAdd.add(entityUUIDStr);
                    TeamProvider.this.schedule(this);
                    return true;
                }
                return false;
            }

            public boolean removeEntity(UUID entityUUID) {
                if (this.entities.remove(entityUUID)) {
                    String entityUUIDStr = entityUUID.toString();
                    if (!this.pendingAdd.isEmpty() && this.pendingAdd.remove(entityUUIDStr)) {
                        return true;
                    }
                    if (this.pendingRemove.isEmpty()) {
                        this.pendingRemove = new HashSet<String>();
                    }
                    this.pendingRemove.add(entityUUIDStr);
                    TeamProvider.this.schedule(this);
                    return true;
                }
                return false;
            }

            public void reset() {
                if (this.teamCreated) {
                    this.teamCreated = false;
                    this.pendingRemove = Collections.emptySet();
                    this.pendingAdd = Collections.emptySet();
                    this.entities.clear();
                    ViewerState.this.viewer.send((PacketHandle)this.team.createPacket(1));
                }
            }

            public boolean update() {
                if (!this.team.rememberEntities) {
                    this.entities.clear();
                }
                if (this.team.rememberEntities && this.entities.isEmpty()) {
                    this.reset();
                } else if (!this.pendingRemove.isEmpty()) {
                    ClientboundSetPlayerTeamPacketHandle packet = this.team.createPacket(4);
                    packet.setPlayers(this.pendingRemove);
                    this.pendingRemove = Collections.emptySet();
                    ViewerState.this.viewer.send((PacketHandle)packet);
                }
                if (this.pendingAdd.isEmpty()) {
                    this.pendingAdd = Collections.emptySet();
                    return false;
                }
                return true;
            }

            public void assignEntities() {
                if (!this.teamCreated) {
                    this.teamCreated = true;
                    ClientboundSetPlayerTeamPacketHandle packet = this.team.createPacket(0);
                    packet.setPlayers(this.pendingAdd);
                    this.pendingAdd = Collections.emptySet();
                    ViewerState.this.viewer.send((PacketHandle)packet);
                } else {
                    ClientboundSetPlayerTeamPacketHandle packet = this.team.createPacket(3);
                    packet.setPlayers(this.pendingAdd);
                    this.pendingAdd = Collections.emptySet();
                    ViewerState.this.viewer.send((PacketHandle)packet);
                }
            }
        }
    }

    public class Team {
        private final String name;
        private final ChatText displayName;
        private final ChatText prefix;
        private final ChatText suffix;
        private final ChatColor color;
        private final String visibility;
        private final String collision;
        private final boolean rememberEntities;

        private Team() {
            this.name = null;
            this.displayName = null;
            this.prefix = null;
            this.suffix = null;
            this.color = null;
            this.visibility = null;
            this.collision = null;
            this.rememberEntities = false;
        }

        private Team(TeamBuilder opts) {
            this.name = opts.name;
            this.displayName = ChatText.fromMessage((String)opts.name);
            this.prefix = opts.prefix;
            this.suffix = opts.suffix;
            this.color = opts.color;
            this.visibility = opts.visibility;
            this.collision = opts.collision;
            this.rememberEntities = opts.rememberEntities;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void join(Player viewer, Iterable<UUID> entityUUIDs) {
            TeamProvider teamProvider = TeamProvider.this;
            synchronized (teamProvider) {
                ViewerState state = TeamProvider.this.viewerStates.computeIfAbsent(viewer.getPlayer(), x$0 -> new ViewerState((Player)x$0));
                state.assignTeamEntities(this, entityUUIDs);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void join(AttachmentViewer viewer, Iterable<UUID> entityUUIDs) {
            TeamProvider teamProvider = TeamProvider.this;
            synchronized (teamProvider) {
                ViewerState state = TeamProvider.this.viewerStates.computeIfAbsent(viewer.getPlayer(), p -> new ViewerState(viewer));
                state.assignTeamEntities(this, entityUUIDs);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void join(Player viewer, UUID entityUUID) {
            TeamProvider teamProvider = TeamProvider.this;
            synchronized (teamProvider) {
                ViewerState state = TeamProvider.this.viewerStates.computeIfAbsent(viewer.getPlayer(), x$0 -> new ViewerState((Player)x$0));
                state.assignTeamEntity(this, entityUUID);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void join(AttachmentViewer viewer, UUID entityUUID) {
            TeamProvider teamProvider = TeamProvider.this;
            synchronized (teamProvider) {
                ViewerState state = TeamProvider.this.viewerStates.computeIfAbsent(viewer.getPlayer(), p -> new ViewerState(viewer));
                state.assignTeamEntity(this, entityUUID);
            }
        }

        private ClientboundSetPlayerTeamPacketHandle createPacket(int method) {
            ClientboundSetPlayerTeamPacketHandle packet = ClientboundSetPlayerTeamPacketHandle.createNew();
            packet.setName(this.name);
            packet.setDisplayName(this.displayName);
            packet.setColor(this.color);
            packet.setPrefix(this.prefix);
            packet.setSuffix(this.suffix);
            packet.setMethod(method);
            packet.setVisibility(this.visibility);
            packet.setCollisionRule(this.collision);
            if (method == 0) {
                packet.setTeamOptionFlags(3);
            }
            return packet;
        }
    }

    public class TeamBuilder {
        private final String name;
        private ChatText prefix = ChatText.empty();
        private ChatText suffix = ChatText.empty();
        private ChatColor color = ChatColor.BLACK;
        private String visibility = "always";
        private String collision = "always";
        private boolean rememberEntities = true;

        private TeamBuilder(String name) {
            this.name = name;
        }

        public TeamBuilder prefix(ChatText prefix) {
            this.prefix = prefix;
            return this;
        }

        public TeamBuilder suffix(ChatText suffix) {
            this.suffix = suffix;
            return this;
        }

        public TeamBuilder color(ChatColor color) {
            this.color = color;
            return this;
        }

        public TeamBuilder visibility(boolean visible) {
            this.visibility = visible ? "always" : "never";
            return this;
        }

        public TeamBuilder collision(boolean enabled) {
            this.collision = enabled ? "always" : "never";
            return this;
        }

        public TeamBuilder rememberEntities(boolean remember) {
            this.rememberEntities = remember;
            return this;
        }

        public Team build() {
            return new Team(this);
        }
    }
}

