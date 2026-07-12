/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorld;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OfflineGroupFileFormatLegacy {
    public static void writeAllWorlds(DataOutputStream stream, List<OfflineGroupWorld> worlds) throws IOException {
        stream.writeInt(worlds.size());
        for (OfflineGroupWorld world : worlds) {
            StreamUtil.writeUUID((DataOutputStream)stream, (UUID)world.getWorld().getUniqueId());
            stream.writeInt(world.totalGroupCount());
            for (OfflineGroup wg : world) {
                OfflineGroupFileFormatLegacy.writeGroup(stream, wg);
            }
        }
    }

    public static List<OfflineGroupWorld> readAllWorlds(DataInputStream stream) throws IOException {
        int worldCount = stream.readInt();
        ArrayList<OfflineGroupWorld> worlds = new ArrayList<OfflineGroupWorld>(worldCount);
        for (int worldIdx = 0; worldIdx < worldCount; ++worldIdx) {
            worlds.add(OfflineGroupFileFormatLegacy.readWorld(stream));
        }
        return Collections.unmodifiableList(worlds);
    }

    public static OfflineGroupWorld readWorld(DataInputStream stream) throws IOException {
        UUID worldUUID = StreamUtil.readUUID((DataInputStream)stream);
        int groupCount = stream.readInt();
        return OfflineGroupFileFormatLegacy.readWorld(stream, worldUUID, groupCount);
    }

    public static OfflineGroupWorld readWorld(DataInputStream stream, UUID worldUUID, int groupCount) throws IOException {
        OfflineWorld world = OfflineWorld.of((UUID)worldUUID);
        ArrayList<OfflineGroup> groups = new ArrayList<OfflineGroup>(groupCount);
        for (int groupIdx = 0; groupIdx < groupCount; ++groupIdx) {
            groups.add(OfflineGroupFileFormatLegacy.readGroup(stream, world));
        }
        return OfflineGroupWorld.snapshot(world, groups);
    }

    public static void writeGroup(DataOutputStream stream, OfflineGroup group) throws IOException {
        stream.writeInt(group.members.length);
        for (OfflineMember member : group.members) {
            OfflineGroupFileFormatLegacy.writeMember(stream, member);
        }
        stream.writeUTF(group.name);
    }

    public static void writeMember(DataOutputStream stream, OfflineMember member) throws IOException {
        stream.writeLong(member.entityUID.getMostSignificantBits());
        stream.writeLong(member.entityUID.getLeastSignificantBits());
        stream.writeDouble(member.motX);
        stream.writeDouble(member.motZ);
        stream.writeInt(member.cx);
        stream.writeInt(member.cz);
    }

    public static OfflineGroup readGroup(DataInputStream stream, OfflineWorld world) throws IOException {
        LegacyOfflineMemberData[] members = new LegacyOfflineMemberData[stream.readInt()];
        for (int i = 0; i < members.length; ++i) {
            members[i] = LegacyOfflineMemberData.read(stream);
        }
        String name = stream.readUTF();
        return new OfflineGroup(name, world, Collections.emptyList(), Collections.emptyList(), Arrays.asList(members), (offlineGroup, legacyMember) -> legacyMember.toOfflineMember(offlineGroup));
    }

    private static class LegacyOfflineMemberData {
        public final UUID entityUID;
        public final int cx;
        public final int cz;
        public final double motX;
        public final double motZ;

        public static LegacyOfflineMemberData read(DataInputStream stream) throws IOException {
            return new LegacyOfflineMemberData(stream);
        }

        private LegacyOfflineMemberData(DataInputStream stream) throws IOException {
            this.entityUID = new UUID(stream.readLong(), stream.readLong());
            this.motX = stream.readDouble();
            this.motZ = stream.readDouble();
            this.cx = stream.readInt();
            this.cz = stream.readInt();
        }

        public OfflineMember toOfflineMember(OfflineGroup offlineGroup) {
            return new OfflineMember(offlineGroup, this.entityUID, this.cx, this.cz, this.motX, 0.0, this.motZ, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        }
    }
}

