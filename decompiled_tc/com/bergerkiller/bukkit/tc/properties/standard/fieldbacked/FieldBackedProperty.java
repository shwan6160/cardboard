/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.tc.properties.standard.fieldbacked;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedCombinedTrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.AttachmentModelBoundToCart;
import com.bergerkiller.bukkit.tc.properties.standard.type.BankingOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.ChunkLoadOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.SignSkipOptions;
import com.bergerkiller.bukkit.tc.properties.standard.type.SlowdownMode;
import com.bergerkiller.bukkit.tc.properties.standard.type.WaitOptions;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;

public abstract class FieldBackedProperty<T>
implements IProperty<T> {

    protected static final class TrainInternalData {
        public double speedLimit;
        public double gravity;
        public double friction;
        public double cartGap;
        public CollisionOptions collision;
        public Set<SlowdownMode> slowdown;
        public SignSkipOptions signSkipOptionsData;
        public WaitOptions waitOptionsData;
        public BankingOptions bankingOptionsData;
        public boolean soundEnabled;
        public ChunkLoadOptions chunkLoadOptions;
        public boolean allowPlayerManualMovement;
        public boolean allowMobManualMovement;
        public boolean realtimePhysics;
        public List<String> activeSavedTrainSpawnLimits;
        public final FieldBackedCombinedTrainProperty<String> tags = new FieldBackedCombinedTrainProperty();
        public final FieldBackedCombinedTrainProperty<String> owners = new FieldBackedCombinedTrainProperty();
        public final FieldBackedCombinedTrainProperty<String> ownerPermissions = new FieldBackedCombinedTrainProperty();

        protected TrainInternalData() {
        }

        public static TrainInternalData get(TrainProperties properties) {
            return properties.getStandardPropertiesHolder().data;
        }
    }

    protected static class CartInternalData {
        public SignSkipOptions signSkipOptionsData;
        public Set<String> tags;
        public Set<String> owners;
        public Set<String> ownerPermissions;
        public Set<Material> blockBreakTypes;
        public boolean pickUpItems;
        public boolean canOnlyOwnersEnter;
        public AttachmentModelBoundToCart model = null;

        protected CartInternalData() {
        }

        public static CartInternalData get(CartProperties properties) {
            return properties.getStandardPropertiesHolder().data;
        }
    }

    public static final class TrainInternalDataHolder {
        protected final TrainInternalData data = new TrainInternalData();
    }

    public static final class CartInternalDataHolder {
        protected final CartInternalData data = new CartInternalData();
    }
}

