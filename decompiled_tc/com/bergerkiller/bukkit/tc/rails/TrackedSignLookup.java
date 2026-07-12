/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.block.SignChangeTracker
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.rails;

import com.bergerkiller.bukkit.common.block.SignChangeTracker;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.utils.ListCallbackCollector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.block.Block;

public final class TrackedSignLookup
implements TrainCarts.Provider {
    private final TrainCarts plugin;
    private final List<SignSupplier> suppliers = new ArrayList<SignSupplier>();
    private final Map<String, RegisteredKeySerializer> serializersById = new HashMap<String, RegisteredKeySerializer>();
    private final WeakHashMap<Class<?>, RegisteredKeySerializer> serializersByType = new WeakHashMap();
    private static final RegisteredKeySerializer MISSING_SERIALIZER = new RegisteredKeySerializer(null, null){

        @Override
        public byte[] serialize(TrainCarts plugin, Object uniqueKey) {
            return null;
        }
    };

    public TrackedSignLookup(TrainCarts plugin) {
        this.plugin = plugin;
        this.serializersByType.put(UnknownSignKey.class, new RegisteredKeySerializer(null, null){

            @Override
            public byte[] serialize(TrainCarts plugin, Object uniqueKey) {
                return ((UnknownSignKey)uniqueKey).data;
            }
        });
        this.registerSerializer("tc-realsign", new RealSignKeySerializer());
        this.registerSerializer("tc-uuid", new KeySerializer<UUID>(){

            @Override
            public Class<UUID> getKeyType() {
                return UUID.class;
            }

            @Override
            public UUID read(DataInputStream input) throws IOException {
                return StreamUtil.readUUID((DataInputStream)input);
            }

            @Override
            public void write(DataOutputStream output, UUID value) throws IOException {
                StreamUtil.writeUUID((DataOutputStream)output, (UUID)value);
            }
        });
        this.registerSerializer("tc-string", new KeySerializer<String>(){

            @Override
            public Class<String> getKeyType() {
                return String.class;
            }

            @Override
            public String read(DataInputStream input) throws IOException {
                return input.readUTF();
            }

            @Override
            public void write(DataOutputStream output, String value) throws IOException {
                output.writeUTF(value);
            }
        });
        this.register(new RealSignSupplier());
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.plugin;
    }

    public RailLookup.TrackedSign getTrackedSign(Object uniqueKey) {
        for (SignSupplier supplier : this.suppliers) {
            RailLookup.TrackedSign sign = supplier.getTrackedSign(uniqueKey);
            if (sign == null) continue;
            return sign;
        }
        return null;
    }

    public List<RailLookup.TrackedSign> getOutputtingTrackedSigns(Block block) {
        List results = Collections.emptyList();
        for (SignSupplier supplier : this.suppliers) {
            results = LogicUtil.combineUnmodifiableLists(results, supplier.getOutputtingTrackedSigns(this.plugin, block));
        }
        return results;
    }

    public <T> List<OfflineDataBlock> serializeUniqueKeys(Collection<T> items, String name, Function<T, Object> uniqueKeyGetter) {
        return this.serializeUniqueKeys(items, name, uniqueKeyGetter, (item, data) -> {});
    }

    public <T> List<OfflineDataBlock> serializeUniqueKeys(Collection<T> items, String name, Function<T, Object> uniqueKeyGetter, BiConsumer<T, OfflineDataBlock> extraMetaApplier) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<OfflineDataBlock> dataBlocks = new ArrayList<OfflineDataBlock>(items.size());
        for (T item : items) {
            byte[] data = this.serializeUniqueKey(uniqueKeyGetter.apply(item));
            if (data == null) continue;
            OfflineDataBlock dataBlock = OfflineDataBlock.createWithData(name, data);
            extraMetaApplier.accept(item, dataBlock);
            dataBlocks.add(dataBlock);
        }
        return Collections.unmodifiableList(dataBlocks);
    }

    public byte[] serializeUniqueKey(Object uniqueKey) {
        if (uniqueKey == null) {
            return null;
        }
        RegisteredKeySerializer registered = this.serializersByType.get(uniqueKey.getClass());
        if (registered == null) {
            registered = MISSING_SERIALIZER;
            Class<?> type = uniqueKey.getClass();
            for (Map.Entry<Class<?>, RegisteredKeySerializer> mapped : this.serializersByType.entrySet()) {
                if (!mapped.getKey().isAssignableFrom(type)) continue;
                registered = mapped.getValue();
                break;
            }
            this.serializersByType.put(type, registered);
        }
        return registered.serialize(this.plugin, uniqueKey);
    }

    public List<Object> deserializeUniqueKeys(List<OfflineDataBlock> dataBlocks) {
        if (dataBlocks.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> uniqueKeys = new ArrayList<Object>(dataBlocks.size());
        for (OfflineDataBlock dataBlock : dataBlocks) {
            Object uniqueKey = this.deserializeUniqueKey(dataBlock.data);
            if (uniqueKey == null) continue;
            uniqueKeys.add(uniqueKey);
        }
        return Collections.unmodifiableList(uniqueKeys);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public Object deserializeUniqueKey(byte[] data) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data);){
            RegisteredKeySerializer registered;
            DataInputStream dataStream;
            block14: {
                UnknownSignKey unknownSignKey;
                dataStream = new DataInputStream(stream);
                try {
                    String id = dataStream.readUTF();
                    registered = this.serializersById.get(id);
                    if (registered != null) break block14;
                    unknownSignKey = new UnknownSignKey(id, data);
                }
                catch (Throwable throwable) {
                    try {
                        dataStream.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                dataStream.close();
                return unknownSignKey;
            }
            Object object = registered.serializer.read(dataStream);
            dataStream.close();
            return object;
        }
        catch (Throwable t) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to deserialize unique sign key", t);
            return null;
        }
    }

    public void register(SignSupplier supplier) {
        if (!this.suppliers.contains(supplier)) {
            this.suppliers.add(supplier);
        }
    }

    public void unregister(SignSupplier supplier) {
        this.suppliers.remove(supplier);
    }

    public void registerSerializer(String id, KeySerializer<?> serializer) {
        Class<?> keyType = serializer.getKeyType();
        RegisteredKeySerializer registered = new RegisteredKeySerializer(id, serializer);
        this.serializersById.put(id, registered);
        this.serializersByType.put(keyType, registered);
        this.serializersByType.values().removeIf(s -> s == MISSING_SERIALIZER);
    }

    public void unregisterSerializer(String id) {
        this.serializersById.remove(id);
    }

    private static class UnknownSignKey {
        public final String id;
        public final byte[] data;

        public UnknownSignKey(String id, byte[] data) {
            this.id = id;
            this.data = data;
        }

        public int hashCode() {
            return this.id.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof UnknownSignKey) {
                return Arrays.equals(this.data, ((UnknownSignKey)o).data);
            }
            return false;
        }

        public String toString() {
            return "UnknownSignKey{" + this.id + "}@" + System.identityHashCode(this);
        }
    }

    public static interface KeySerializer<T> {
        public Class<T> getKeyType();

        public T read(DataInputStream var1) throws IOException;

        public void write(DataOutputStream var1, T var2) throws IOException;
    }

    private static class RealSignKeySerializer
    implements KeySerializer<RealSignKey> {
        private RealSignKeySerializer() {
        }

        @Override
        public Class<RealSignKey> getKeyType() {
            return RealSignKey.class;
        }

        @Override
        public RealSignKey read(DataInputStream input) throws IOException {
            byte version = input.readByte();
            if (version == 1) {
                OfflineBlock block = OfflineBlock.readFrom((DataInputStream)input);
                boolean front = input.readBoolean();
                return new RealSignKey(block, front);
            }
            return null;
        }

        @Override
        public void write(DataOutputStream output, RealSignKey value) throws IOException {
            output.writeByte(1);
            OfflineBlock.writeTo((DataOutputStream)output, (OfflineBlock)value.block);
            output.writeBoolean(value.front);
        }
    }

    private static class RealSignSupplier
    implements SignSupplier {
        private RealSignSupplier() {
        }

        @Override
        public RailLookup.TrackedSign getTrackedSign(Object uniqueKey) {
            if (uniqueKey instanceof RealSignKey) {
                return ((RealSignKey)uniqueKey).findRealSign();
            }
            return null;
        }

        @Override
        public List<RailLookup.TrackedSign> getOutputtingTrackedSigns(TrainCarts trainCarts, Block block) {
            ListCallbackCollector signs = new ListCallbackCollector();
            trainCarts.getSignController().forWorld(block.getWorld()).forEachNearbyVerify(block, true, entry -> {
                if (entry.sign.isAttachedTo(block)) {
                    if (entry.front.hasSignAction()) {
                        signs.accept(entry.front.createTrackedSign(null));
                    }
                    if (entry.back.hasSignAction()) {
                        signs.accept(entry.back.createTrackedSign(null));
                    }
                }
            });
            return signs.result();
        }
    }

    @FunctionalInterface
    public static interface SignSupplier {
        public RailLookup.TrackedSign getTrackedSign(Object var1);

        default public List<RailLookup.TrackedSign> getOutputtingTrackedSigns(TrainCarts trainCarts, Block block) {
            return Collections.emptyList();
        }
    }

    private static class RegisteredKeySerializer {
        public final String id;
        public final KeySerializer<Object> serializer;

        public RegisteredKeySerializer(String id, KeySerializer<?> serializer) {
            this.id = id;
            this.serializer = serializer;
        }

        public byte[] serialize(TrainCarts plugin, Object uniqueKey) {
            byte[] byArray;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                try (DataOutputStream dataStream = new DataOutputStream(stream);){
                    dataStream.writeUTF(this.id);
                    this.serializer.write(dataStream, uniqueKey);
                }
                byArray = stream.toByteArray();
            }
            catch (Throwable throwable) {
                try {
                    try {
                        stream.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (Throwable t) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to serialize unique sign key " + uniqueKey.getClass().getName(), t);
                    return null;
                }
            }
            stream.close();
            return byArray;
        }
    }

    protected static final class RealSignKey {
        public final OfflineBlock block;
        public final boolean front;
        private final int hashCode;

        public RealSignKey(OfflineBlock block, boolean front) {
            this.block = block;
            this.front = front;
            this.hashCode = block.hashCode();
        }

        public RailLookup.TrackedSign findRealSign() {
            Block loaded = this.block.getLoadedBlock();
            if (loaded == null) {
                return null;
            }
            SignChangeTracker tracker = SignChangeTracker.track((Block)loaded);
            if (tracker.isRemoved()) {
                return null;
            }
            RailLookup.TrackedSign sign = RailLookup.TrackedSign.forRealSign(tracker, this.front, RailPiece.NONE);
            sign.rail = null;
            return sign;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof RealSignKey) {
                RealSignKey other = (RealSignKey)o;
                return this.block.equals((Object)other.block) && this.front == other.front;
            }
            return false;
        }

        public String toString() {
            return "RealSign{block=" + this.block + " side=" + (this.front ? "front" : "back") + "}";
        }
    }
}

