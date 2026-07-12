/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.protocol.CommonPacket
 *  com.bergerkiller.bukkit.common.protocol.PacketType
 *  com.bergerkiller.bukkit.common.utils.EntityUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher
 *  com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle
 *  com.bergerkiller.generated.com.mojang.authlib.properties.PropertyHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacketHandle$ActionHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacketHandle$EntryHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacketHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.com.mojang.authlib.properties.PropertyHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundAddPlayerPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public enum FakePlayerSpawner {
    NORMAL(null, null, false),
    NO_NAMETAG("DinnerBone", "BoredTCRiders", true),
    NO_NAMETAG_RANDOM("DinnerBone", "BoredTCRandoms", true),
    NO_NAMETAG_SECONDARY("DinnarBone", "BoredTCRiders2", true),
    NO_NAMETAG_TERTIARY("DinnarBone", "BoredTCRiders3", true),
    UPSIDEDOWN("Dinnerbone", "DizzyTCRiders", true);

    private static final Map<UUID, ProfileState> _dummyProfileStates;
    private static final Map<UUID, Map<UUID, ProfileState>> _profileStates;
    private static final int TAB_LIST_CLEANUP_DELAY = 5;
    private final String _playerName;
    private final ChatText _teamName;
    private final boolean _hideNametag;
    private final Set<UUID> _teamSentPlayers = new HashSet<UUID>();

    private FakePlayerSpawner(String playerName, String teamName, boolean hideNametag) {
        this._playerName = playerName;
        this._teamName = ChatText.fromMessage((String)teamName);
        this._hideNametag = hideNametag;
    }

    public ChatText getPlayerName() {
        return ChatText.fromMessage((String)this._playerName);
    }

    public void spawnPlayer(Player viewer, Player player, int entityId, FakePlayerPosition position, Consumer<DataWatcher> metaFunction) {
        this.spawnPlayer(AttachmentViewer.fallback(viewer), player, entityId, position, metaFunction);
    }

    public void spawnPlayer(AttachmentViewer viewer, Player player, int entityId, FakePlayerPosition position, Consumer<DataWatcher> metaFunction) {
        this.spawnPlayerSimple(viewer, player, entityId, (ClientboundAddPlayerPacketHandle fakePlayerSpawnPacket) -> {
            fakePlayerSpawnPacket.setPosX(position.getX());
            fakePlayerSpawnPacket.setPosY(position.getY());
            fakePlayerSpawnPacket.setPosZ(position.getZ());
            fakePlayerSpawnPacket.setYaw(position.getYaw());
            fakePlayerSpawnPacket.setPitch(position.getPitch());
        }, metaFunction);
        CommonPacket headPacket = PacketType.OUT_ENTITY_HEAD_ROTATION.newInstance();
        headPacket.write(PacketType.OUT_ENTITY_HEAD_ROTATION.entityId, (Object)entityId);
        headPacket.write(PacketType.OUT_ENTITY_HEAD_ROTATION.headYaw, (Object)Float.valueOf(position.getHeadYaw()));
        viewer.send(headPacket);
    }

    public void spawnPlayerSimple(Player viewer, Player player, int entityId, Consumer<ClientboundAddPlayerPacketHandle> applier, Consumer<DataWatcher> metaFunction) {
        this.spawnPlayerSimple(AttachmentViewer.fallback(viewer), player, entityId, applier, metaFunction);
    }

    public void spawnPlayerSimple(AttachmentViewer viewer, Player player, int entityId, Consumer<ClientboundAddPlayerPacketHandle> applier, Consumer<DataWatcher> metaFunction) {
        ProfileState state = this.getProfileState(player, viewer.getPlayer());
        UUID uuidOfFakePlayer = this.sendPlayerProfileInfo(viewer, player, state);
        ClientboundAddPlayerPacketHandle fakePlayerSpawnPacket = ClientboundAddPlayerPacketHandle.createNew();
        fakePlayerSpawnPacket.setEntityId(entityId);
        fakePlayerSpawnPacket.setEntityUUID(uuidOfFakePlayer);
        applier.accept(fakePlayerSpawnPacket);
        DataWatcher metaData = player == null ? new DataWatcher() : EntityUtil.getDataWatcher((Entity)player).clone();
        metaFunction.accept(metaData);
        viewer.sendNamedEntitySpawnPacket(fakePlayerSpawnPacket, metaData);
    }

    private UUID sendPlayerProfileInfo(AttachmentViewer viewer, Player player, ProfileState state) {
        ChatText playerListName;
        GameProfileHandle newFakeGameProfile;
        if (this == NORMAL && player != null) {
            return player.getUniqueId();
        }
        UUID uuid = state.getUUID(this);
        state.runAndClearCleanupTasksFor(viewer, uuid);
        if (player == null) {
            newFakeGameProfile = FakePlayerSpawner.createDummyPlayerProfile(uuid, this._playerName);
            playerListName = ChatText.fromMessage((String)"Dummy");
        } else {
            newFakeGameProfile = GameProfileHandle.createNew((UUID)uuid, (String)this._playerName).withPropertiesOf(GameProfileHandle.getForPlayer((HumanEntity)player));
            playerListName = ChatText.fromMessage((String)player.getPlayerListName());
        }
        ClientboundPlayerInfoUpdatePacketHandle newInfoPacket = ClientboundPlayerInfoUpdatePacketHandle.createNew();
        newInfoPacket.setAction(ClientboundPlayerInfoUpdatePacketHandle.ActionHandle.ADD_PLAYER);
        ClientboundPlayerInfoUpdatePacketHandle.EntryHandle playerInfo = ClientboundPlayerInfoUpdatePacketHandle.EntryHandle.createNew((ClientboundPlayerInfoUpdatePacketHandle)newInfoPacket, (GameProfileHandle)newFakeGameProfile, (int)50, (GameMode)GameMode.CREATIVE, (ChatText)playerListName, (boolean)false);
        newInfoPacket.getPlayers().add(playerInfo);
        viewer.send((PacketHandle)newInfoPacket);
        if (this._hideNametag && this._teamName != null && this._teamSentPlayers.add(viewer.getPlayer().getUniqueId())) {
            ClientboundSetPlayerTeamPacketHandle teamPacket = ClientboundSetPlayerTeamPacketHandle.createNew();
            teamPacket.setMethod(0);
            teamPacket.setName(this._teamName.getMessage());
            teamPacket.setDisplayName(this._teamName);
            teamPacket.setPrefix(ChatText.fromMessage((String)""));
            teamPacket.setSuffix(ChatText.fromMessage((String)""));
            teamPacket.setVisibility("never");
            teamPacket.setCollisionRule("never");
            teamPacket.setTeamOptionFlags(3);
            teamPacket.setPlayers(new ArrayList<String>(Collections.singleton(this._playerName)));
            teamPacket.setColor(ChatColor.RESET);
            viewer.send((PacketHandle)teamPacket);
        }
        state.scheduleCleanupTask(viewer, uuid);
        return uuid;
    }

    private final ProfileState getProfileState(Player player, Player viewer) {
        if (player == null) {
            return _dummyProfileStates.computeIfAbsent(viewer.getUniqueId(), uuid -> new ProfileState(true));
        }
        return _profileStates.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap(1)).computeIfAbsent(viewer.getUniqueId(), uuid -> new ProfileState(false));
    }

    public static void onViewerQuit(Player viewer) {
        _profileStates.remove(viewer.getUniqueId());
        for (FakePlayerSpawner modifier : FakePlayerSpawner.values()) {
            modifier._teamSentPlayers.remove(viewer.getUniqueId());
        }
    }

    public static void runAndClearCleanupTasks() {
        _profileStates.values().stream().flatMap(e -> e.values().stream()).forEach(ProfileState::runAndClearCleanupTasks);
        _dummyProfileStates.values().forEach(ProfileState::runAndClearCleanupTasks);
    }

    private static UUID generateNPCUUID() {
        UUID uuid = UUID.randomUUID();
        return new UUID(uuid.getMostSignificantBits() & 0xFFFFFFFFFFFF0FFFL | 0x2000L, uuid.getLeastSignificantBits());
    }

    public static GameProfileHandle createDummyPlayerProfile() {
        return FakePlayerSpawner.createDummyPlayerProfile(FakePlayerSpawner.generateNPCUUID(), "Dummy");
    }

    public static GameProfileHandle createDummyPlayerProfile(UUID uuid, String playerName) {
        return GameProfileHandle.createNew((UUID)uuid, (String)playerName).withPropertyPut("textures", PropertyHandle.createNew((String)"textures", (String)"ewogICJ0aW1lc3RhbXAiIDogMTY0Mjc4NTAwMzQ3NywKICAicHJvZmlsZUlkIiA6ICIwNjNhMTc2Y2RkMTU0ODRiYjU1MjRhNjQyMGM1YjdhNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkYXZpcGF0dXJ5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2YzMzBlNjk1OTc4ZTgyZDE1M2IxZmRhMWM1NjE2OTA3NGUyNzZlNTMzODY2ZGE3OWFkZDQzZDIwMTczNzUxYWUiCiAgICB9CiAgfQp9", (String)"s1s8RzOgcymEF56ow13Vvw0UfvhJG1PY7Qh5A5kpBi5uscbwaI/ib3QfK5wll4Gge06JreHAGbHIiTx1jAX17ciJQHhWvSF3/VnnnZaLEyXo3xWaOwFEIedGgUeqv9RigMJbJKvLqA0hQ1ezhwTGylQCLhz5Pxrsqtj+x6sozqRmL6YvLm+xTwAH2r5bj5luRrakgRYpG5kOh2ykYGwL4PEgU1yaZB7pcpnRfwOX2a/qm2e0l9RGDAW1X36fJ9w/kUzPVZSD9yXMu4XX6NVXn1fmhFeezfqVEtbQTozCVoEbLh3828rY+P7U5b8GfdHWM9hs5Ukc7dcLfzcwPU2bRTfvT0t95BdKI5P9bDlchqBGQFNQ49ii9dwZ4+JxLBTWQT/7/X5XsfpNKl96GVnPfVZ49hczx6O923XdD3j7MknDC11ZA8KGo03nNmz2cPqLKUfhyqmSCvQA70A9DBKP4Ys35I3HkiS3Qxrd6bdNtrixys7oJmGA8MUf1tyDW2w9tq3S5+nHLUnMahhqSzToznIt3cu+OVEbjVbaM9LGj5VK3H7M3brkb1C4jRRYL3Pia3cck9BtLEvx42gjCfA2rqXR6YxOMcq3GuunwtC1oRfrocRzW73qg0gCDVHCAdcfazmKWwCh3h6dvxOy7GaXKQaNKKdF1rxfsvYT+8zLwOQ="));
    }

    static {
        _dummyProfileStates = new HashMap<UUID, ProfileState>();
        _profileStates = new HashMap<UUID, Map<UUID, ProfileState>>();
    }

    public static class FakePlayerPosition {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;
        private final float headyaw;

        private FakePlayerPosition(double x, double y, double z, float yaw, float pitch, float headyaw) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.headyaw = headyaw;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }

        public float getYaw() {
            return this.yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        public float getHeadYaw() {
            return this.headyaw;
        }

        public FakePlayerPosition atOppositePitchBoundary() {
            return FakePlayerPosition.create(this.x, this.y, this.z, this.yaw, Util.atOppositeRotationGlitchBoundary(this.pitch), this.headyaw);
        }

        public static FakePlayerPosition ofPlayer(Player player) {
            EntityHandle playerHandle = EntityHandle.fromBukkit((Entity)player);
            return new FakePlayerPosition(playerHandle.getLocX(), playerHandle.getLocY(), playerHandle.getLocZ(), playerHandle.getYaw(), playerHandle.getPitch(), playerHandle.getHeadRotation());
        }

        public static FakePlayerPosition ofPlayer(double x, double y, double z, Player player) {
            EntityHandle playerHandle = EntityHandle.fromBukkit((Entity)player);
            return new FakePlayerPosition(x, y, z, playerHandle.getYaw(), playerHandle.getPitch(), playerHandle.getHeadRotation());
        }

        public static FakePlayerPosition ofPlayerUpsideDown(double x, double y, double z, Player player) {
            EntityHandle playerHandle = EntityHandle.fromBukkit((Entity)player);
            float yaw = playerHandle.getYaw();
            return new FakePlayerPosition(x, y, z, yaw, -playerHandle.getPitch(), -playerHandle.getHeadRotation() + 2.0f * yaw);
        }

        public static FakePlayerPosition create(double x, double y, double z, float yaw, float pitch, float headyaw) {
            return new FakePlayerPosition(x, y, z, yaw, pitch, headyaw);
        }
    }

    private static class ProfileState {
        private final UUID npcUUID;
        private final UUID npcUUID2;
        private final UUID npcUUID3;
        public final List<CleanupPlayerListEntryTask> pendingCleanup;

        public ProfileState(boolean dummy) {
            this.npcUUID = dummy ? null : FakePlayerSpawner.generateNPCUUID();
            this.npcUUID2 = dummy ? null : FakePlayerSpawner.generateNPCUUID();
            this.npcUUID3 = dummy ? null : FakePlayerSpawner.generateNPCUUID();
            this.pendingCleanup = new ArrayList<CleanupPlayerListEntryTask>();
        }

        public UUID getUUID(FakePlayerSpawner type) {
            return type == NO_NAMETAG_RANDOM || this.npcUUID == null ? FakePlayerSpawner.generateNPCUUID() : (type == NO_NAMETAG_TERTIARY ? this.npcUUID3 : (type == NO_NAMETAG_SECONDARY ? this.npcUUID2 : this.npcUUID));
        }

        public void scheduleCleanupTask(AttachmentViewer viewer, UUID playerUUID) {
            CleanupPlayerListEntryTask task;
            Iterator<CleanupPlayerListEntryTask> iter = this.pendingCleanup.iterator();
            while (iter.hasNext()) {
                task = iter.next();
                if (!task.playerUUID.equals(playerUUID)) continue;
                task.stop();
                iter.remove();
            }
            task = new CleanupPlayerListEntryTask((JavaPlugin)TrainCarts.plugin, this, viewer, playerUUID);
            this.pendingCleanup.add(task);
            task.start(5L, 1L);
        }

        public void runAndClearCleanupTasksFor(AttachmentViewer viewer, UUID uuid) {
            Iterator<CleanupPlayerListEntryTask> iter = this.pendingCleanup.iterator();
            while (iter.hasNext()) {
                CleanupPlayerListEntryTask task = iter.next();
                if (!task.viewer.equals(viewer) || !uuid.equals(task.playerUUID)) continue;
                iter.remove();
                task.finish();
            }
        }

        public void runAndClearCleanupTasks() {
            if (!this.pendingCleanup.isEmpty()) {
                ArrayList<CleanupPlayerListEntryTask> all = new ArrayList<CleanupPlayerListEntryTask>(this.pendingCleanup);
                this.pendingCleanup.clear();
                for (CleanupPlayerListEntryTask task : all) {
                    task.finish();
                }
            }
        }
    }

    private static class CleanupPlayerListEntryTask
    extends Task {
        private final ProfileState state;
        private final AttachmentViewer viewer;
        private final long runWhen;
        public final UUID playerUUID;

        public CleanupPlayerListEntryTask(JavaPlugin plugin, ProfileState state, AttachmentViewer viewer, UUID playerUUID) {
            super(plugin);
            this.state = state;
            this.viewer = viewer;
            this.playerUUID = playerUUID;
            this.runWhen = System.currentTimeMillis() + 250L;
        }

        public void run() {
            if (System.currentTimeMillis() >= this.runWhen) {
                this.finish();
            }
        }

        public void finish() {
            try {
                if (this.viewer.isConnected()) {
                    this.viewer.send((PacketHandle)ClientboundPlayerInfoRemovePacketHandle.createNew(Collections.singletonList(this.playerUUID)));
                }
            }
            finally {
                this.stop();
                this.state.pendingCleanup.remove((Object)this);
            }
        }
    }
}

