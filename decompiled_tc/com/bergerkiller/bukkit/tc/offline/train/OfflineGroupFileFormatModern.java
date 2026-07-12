/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 */
package com.bergerkiller.bukkit.tc.offline.train;

import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroup;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupFileFormatLegacy;
import com.bergerkiller.bukkit.tc.offline.train.OfflineGroupWorld;
import com.bergerkiller.bukkit.tc.offline.train.OfflineMember;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OfflineGroupFileFormatModern {
    private static void bootstrap(Class<?> ... classNames) {
        for (Class<?> clazz : classNames) {
            CommonUtil.loadClass(clazz);
        }
    }

    public static void writeAll(DataOutputStream stream, Data data) throws IOException {
        stream.writeInt(1);
        StreamUtil.writeUUID((DataOutputStream)stream, (UUID)new UUID(0L, 0L));
        stream.writeInt(0);
        for (OfflineGroupWorld world : data.worlds) {
            OfflineGroupFileFormatModern.writeWorldGroups(data.root, world);
        }
        data.root.writeTo(stream);
    }

    public static Data readAll(DataInputStream stream) throws IOException {
        List<OfflineDataBlock> worldDataList;
        int worldCount = stream.readInt();
        if (worldCount == 0) {
            return new Data(Collections.emptyList());
        }
        UUID firstWorldUUID = StreamUtil.readUUID((DataInputStream)stream);
        int firstWorldGroupCount = stream.readInt();
        if (worldCount != 1 || firstWorldUUID.getMostSignificantBits() != 0L || firstWorldUUID.getLeastSignificantBits() != 0L || firstWorldGroupCount != 0) {
            ArrayList<OfflineGroupWorld> worlds = new ArrayList<OfflineGroupWorld>(worldCount);
            worlds.add(OfflineGroupFileFormatLegacy.readWorld(stream, firstWorldUUID, firstWorldGroupCount));
            for (int worldIdx = 1; worldIdx < worldCount; ++worldIdx) {
                worlds.add(OfflineGroupFileFormatLegacy.readWorld(stream));
            }
            return new Data(Collections.unmodifiableList(worlds));
        }
        OfflineDataBlock root = OfflineDataBlock.read(stream);
        List<OfflineDataBlock> list = worldDataList = root == null ? Collections.emptyList() : root.findChildren("world");
        if (worldDataList.isEmpty()) {
            return new Data(Collections.emptyList(), root);
        }
        ArrayList<OfflineGroupWorld> worlds = new ArrayList<OfflineGroupWorld>(worldDataList.size());
        for (OfflineDataBlock worldData : worldDataList) {
            worlds.add(OfflineGroupFileFormatModern.readWorldGroups(worldData));
        }
        return new Data(Collections.unmodifiableList(worlds), root);
    }

    public static void writeWorldGroups(OfflineDataBlock root, OfflineGroupWorld world) throws IOException {
        OfflineDataBlock worldData = root.addChild("world", s -> StreamUtil.writeUUID((DataOutputStream)s, (UUID)world.getWorld().getUniqueId()));
        for (OfflineGroup group : world.getGroups()) {
            OfflineGroupFileFormatModern.writeGroup(worldData, group);
        }
    }

    public static OfflineGroupWorld readWorldGroups(OfflineDataBlock worldGroupData) throws IOException {
        OfflineWorld world;
        try (DataInputStream stream = worldGroupData.readData();){
            world = OfflineWorld.of((UUID)StreamUtil.readUUID((DataInputStream)stream));
        }
        List<OfflineDataBlock> groupListData = worldGroupData.findChildren("group");
        ArrayList<OfflineGroup> groups = new ArrayList<OfflineGroup>(groupListData.size());
        for (OfflineDataBlock groupData : groupListData) {
            OfflineGroup group = OfflineGroupFileFormatModern.readGroup(groupData, world);
            if (group == null) continue;
            groups.add(group);
        }
        return OfflineGroupWorld.snapshot(world, groups);
    }

    public static void writeGroup(OfflineDataBlock root, OfflineGroup group) throws IOException {
        OfflineDataBlock groupData = root.addChild("group", s -> s.writeUTF(group.name));
        groupData.children.addAll(group.actions);
        groupData.children.addAll(group.skippedSigns);
        for (OfflineMember member : group.members) {
            OfflineGroupFileFormatModern.writeMember(groupData, member);
        }
    }

    public static OfflineGroup readGroup(OfflineDataBlock groupData, OfflineWorld world) throws IOException {
        String name;
        try (DataInputStream stream = groupData.readData();){
            name = stream.readUTF();
        }
        List<OfflineDataBlock> members = groupData.findChildren("member");
        if (members.isEmpty()) {
            return null;
        }
        return new OfflineGroup(name, world, groupData.findChildren("action"), groupData.findChildren("skipped-sign"), members, OfflineGroupFileFormatModern::readMember);
    }

    public static void writeMember(OfflineDataBlock root, OfflineMember member) throws IOException {
        OfflineDataBlock memberData = root.addChild("member", s -> {
            StreamUtil.writeUUID((DataOutputStream)s, (UUID)member.entityUID);
            s.writeInt(member.cx);
            s.writeInt(member.cz);
            s.writeDouble(member.motX);
            s.writeDouble(member.motY);
            s.writeDouble(member.motZ);
        });
        memberData.children.addAll(member.actions);
        memberData.children.addAll(member.activeSigns);
        memberData.children.addAll(member.skippedSigns);
    }

    private static OfflineMember readMember(OfflineGroup group, OfflineDataBlock memberData) throws IOException {
        double motZ;
        double motY;
        double motX;
        int cz;
        int cx;
        UUID entityUID;
        try (DataInputStream stream = memberData.readData();){
            entityUID = StreamUtil.readUUID((DataInputStream)stream);
            cx = stream.readInt();
            cz = stream.readInt();
            motX = stream.readDouble();
            motY = stream.readDouble();
            motZ = stream.readDouble();
        }
        return new OfflineMember(group, entityUID, cx, cz, motX, motY, motZ, memberData.findChildren("action"), memberData.findChildren("sign"), memberData.findChildren("skipped-sign"));
    }

    static {
        OfflineGroupFileFormatModern.bootstrap(DataInputStream.class, DataOutputStream.class, Data.class, OfflineWorld.class, StreamUtil.class, OfflineDataBlock.class, IOException.class, ArrayList.class, Collections.class, List.class, UUID.class);
    }

    public static final class Data {
        public final List<OfflineGroupWorld> worlds;
        public final OfflineDataBlock root;

        public Data(List<OfflineGroupWorld> worlds) {
            this.worlds = worlds;
            this.root = OfflineDataBlock.create("root");
        }

        public Data(List<OfflineGroupWorld> worlds, OfflineDataBlock root) {
            this.worlds = worlds;
            this.root = root;
        }
    }
}

