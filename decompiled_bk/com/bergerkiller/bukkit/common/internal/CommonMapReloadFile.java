/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.config.CompressedDataReader;
import com.bergerkiller.bukkit.common.config.CompressedDataWriter;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonMapReloadFile {
    private static final int FORMAT_VERSION = 1;
    public final List<Integer> staticReservedIds = new ArrayList<Integer>();
    public final List<DynamicMappedId> dynamicMappedIds = new ArrayList<DynamicMappedId>();
    public final List<ItemFrameDisplayUUID> itemFrameDisplayUUIDs = new ArrayList<ItemFrameDisplayUUID>();

    private CommonMapReloadFile() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void load(JavaPlugin plugin, Consumer<CommonMapReloadFile> callback) {
        File file = CommonMapReloadFile.getSaveFile(plugin);
        if (!file.exists()) {
            return;
        }
        try {
            if (MinecraftServerHandle.instance().getTicks() == 0) {
                return;
            }
            final AtomicBoolean isValid = new AtomicBoolean(true);
            final CommonMapReloadFile reloadFile = new CommonMapReloadFile();
            if (new CompressedDataReader(file){

                @Override
                public void read(DataInputStream stream) throws IOException {
                    int version = stream.readInt();
                    if (version != 1) {
                        isValid.set(false);
                        return;
                    }
                    if (stream.readInt() != MinecraftServerHandle.instance().getTicksSinceUnixEpoch()) {
                        isValid.set(false);
                        return;
                    }
                    int numStaticIds = stream.readInt();
                    for (int n = 0; n < numStaticIds; ++n) {
                        reloadFile.staticReservedIds.add(stream.readInt());
                    }
                    int numDynamicIds = stream.readInt();
                    for (int n = 0; n < numDynamicIds; ++n) {
                        reloadFile.dynamicMappedIds.add(DynamicMappedId.readFrom(stream));
                    }
                    int numItemFrames = stream.readInt();
                    for (int n = 0; n < numItemFrames; ++n) {
                        reloadFile.itemFrameDisplayUUIDs.add(ItemFrameDisplayUUID.readFrom(stream));
                    }
                }
            }.read() && isValid.get()) {
                callback.accept(reloadFile);
            }
        }
        finally {
            file.delete();
        }
    }

    public static void save(JavaPlugin plugin, final Consumer<CommonMapReloadFile> callback) {
        File file = CommonMapReloadFile.getSaveFile(plugin);
        if (CommonUtil.isShuttingDown()) {
            if (file.exists()) {
                file.delete();
            }
            return;
        }
        if (!new CompressedDataWriter(file){

            @Override
            public void write(DataOutputStream stream) throws IOException {
                CommonMapReloadFile reloadFile = new CommonMapReloadFile();
                callback.accept(reloadFile);
                stream.writeInt(1);
                stream.writeInt(MinecraftServerHandle.instance().getTicksSinceUnixEpoch());
                stream.writeInt(reloadFile.staticReservedIds.size());
                for (Integer id : reloadFile.staticReservedIds) {
                    stream.writeInt(id);
                }
                stream.writeInt(reloadFile.dynamicMappedIds.size());
                for (DynamicMappedId mapid : reloadFile.dynamicMappedIds) {
                    mapid.writeTo(stream);
                }
                stream.writeInt(reloadFile.itemFrameDisplayUUIDs.size());
                for (ItemFrameDisplayUUID displayUUID : reloadFile.itemFrameDisplayUUIDs) {
                    displayUUID.writeTo(stream);
                }
            }
        }.write()) {
            file.delete();
        }
    }

    private static File getSaveFile(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), "mapinfo.reload.dat");
    }

    public void addDynamicMapId(MapUUID uuid, int id) {
        this.dynamicMappedIds.add(new DynamicMappedId(uuid, id));
    }

    public void addItemFrameDisplayUUID(int entityId, MapUUID uuid) {
        this.itemFrameDisplayUUIDs.add(new ItemFrameDisplayUUID(uuid, entityId));
    }

    private static MapUUID readMapUUID(DataInputStream stream) throws IOException {
        long uuidMostSig = stream.readLong();
        long uuidLeastSig = stream.readLong();
        int tileX = stream.readInt();
        int tileY = stream.readInt();
        return new MapUUID(new UUID(uuidMostSig, uuidLeastSig), tileX, tileY);
    }

    private static void writeMapUUID(DataOutputStream stream, MapUUID uuid) throws IOException {
        stream.writeLong(uuid.getUUID().getMostSignificantBits());
        stream.writeLong(uuid.getUUID().getLeastSignificantBits());
        stream.writeInt(uuid.getTileX());
        stream.writeInt(uuid.getTileY());
    }

    public static class DynamicMappedId {
        public final MapUUID uuid;
        public final int id;

        public DynamicMappedId(MapUUID uuid, int id) {
            this.uuid = uuid;
            this.id = id;
        }

        public static DynamicMappedId readFrom(DataInputStream stream) throws IOException {
            MapUUID uuid = CommonMapReloadFile.readMapUUID(stream);
            int id = stream.readInt();
            return new DynamicMappedId(uuid, id);
        }

        public void writeTo(DataOutputStream stream) throws IOException {
            CommonMapReloadFile.writeMapUUID(stream, this.uuid);
            stream.writeInt(this.id);
        }
    }

    public static class ItemFrameDisplayUUID {
        public final MapUUID uuid;
        public final int entityId;

        public ItemFrameDisplayUUID(MapUUID uuid, int entityId) {
            this.uuid = uuid;
            this.entityId = entityId;
        }

        public static ItemFrameDisplayUUID readFrom(DataInputStream stream) throws IOException {
            MapUUID uuid = CommonMapReloadFile.readMapUUID(stream);
            int entityId = stream.readInt();
            return new ItemFrameDisplayUUID(uuid, entityId);
        }

        public void writeTo(DataOutputStream stream) throws IOException {
            CommonMapReloadFile.writeMapUUID(stream, this.uuid);
            stream.writeInt(this.entityId);
        }
    }
}

