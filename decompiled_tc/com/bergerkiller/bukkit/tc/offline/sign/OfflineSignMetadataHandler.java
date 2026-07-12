/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.offline.sign;

import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface OfflineSignMetadataHandler<T> {
    default public int getMetadataVersion() {
        return 0;
    }

    default public DataMigrationDecoder<T> getMigrationDecoder(OfflineSign sign, int dataVersion) {
        throw new UnsupportedOperationException("Not supported");
    }

    public void onUpdated(OfflineSignStore var1, OfflineSign var2, T var3, T var4);

    public void onAdded(OfflineSignStore var1, OfflineSign var2, T var3);

    public void onRemoved(OfflineSignStore var1, OfflineSign var2, T var3);

    default public void onLoaded(OfflineSignStore store, OfflineSign sign, T metadata) {
        this.onAdded(store, sign, metadata);
    }

    default public void onUnloaded(OfflineSignStore store, OfflineSign sign, T metadata) {
        this.onRemoved(store, sign, metadata);
    }

    default public T onSignChanged(OfflineSignStore store, OfflineSign oldSign, OfflineSign newSign, T metadata) {
        return null;
    }

    public void onEncode(DataOutputStream var1, OfflineSign var2, T var3) throws IOException;

    public T onDecode(DataInputStream var1, OfflineSign var2) throws IOException;

    default public boolean isUnloadedWorldsIgnored() {
        return true;
    }

    public static final class InvalidMetadataException
    extends RuntimeException {
        private static final long serialVersionUID = 1301135081987007765L;
    }

    public static interface DataMigrationDecoder<T> {
        public T onDecode(DataInputStream var1, OfflineSign var2, int var3) throws IOException;
    }
}

