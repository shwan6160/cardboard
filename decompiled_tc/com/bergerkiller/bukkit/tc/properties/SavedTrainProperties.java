/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModelStore;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.properties.SavedClaim;
import com.bergerkiller.bukkit.tc.properties.SavedTrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.CartLockOrientation;
import com.bergerkiller.bukkit.tc.utils.SetCallbackCollector;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bukkit.command.CommandSender;

public class SavedTrainProperties
implements TrainCarts.Provider,
SavedAttachmentModelStore.ModelUsing {
    private final TrainCarts traincarts;
    private final ModularConfigurationEntry<SavedTrainProperties> entry;

    SavedTrainProperties(TrainCarts traincarts, ModularConfigurationEntry<SavedTrainProperties> entry) {
        this.traincarts = traincarts;
        this.entry = entry;
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    public SavedTrainPropertiesStore getModule() {
        if (this.entry.isRemoved()) {
            return null;
        }
        return SavedTrainPropertiesStore.createModule(this.entry.getModule());
    }

    public boolean isNone() {
        return this.entry.isRemoved();
    }

    public String getName() {
        return this.entry.getName();
    }

    public ConfigurationNode getConfig() {
        return this.entry.getConfig();
    }

    public ConfigurationNode getExportedConfig() {
        ConfigurationNode exportedConfig = this.getConfig().clone();
        exportedConfig.remove("claims");
        exportedConfig.set("usedModels", (Object)this.getUsedModelsAsExport());
        return exportedConfig;
    }

    @Override
    public void getUsedModels(SetCallbackCollector<SavedAttachmentModel> collector) {
        for (ConfigurationNode cart : this.getCarts()) {
            ConfigurationNode modelConfig = cart.getNodeIfExists("model");
            if (modelConfig == null) continue;
            this.traincarts.getSavedAttachmentModels().findModelsUsedInConfiguration(modelConfig, collector);
        }
    }

    public boolean isEmpty() {
        if (this.entry.isRemoved()) {
            return true;
        }
        ConfigurationNode config = this.entry.getConfig();
        return !config.contains("carts") && !config.contains("spawnPattern");
    }

    public boolean hasSpawnPattern() {
        return !this.entry.isRemoved() && this.entry.getConfig().contains("spawnPattern");
    }

    public String getSpawnPattern() {
        return this.entry.isRemoved() ? null : (String)this.entry.getConfig().getOrDefault("spawnPattern", String.class, null);
    }

    public void reverse() {
        if (this.isEmpty()) {
            return;
        }
        List carts = this.entry.getWritableConfig().getNodeList("carts");
        if (carts.isEmpty() && this.hasSpawnPattern()) {
            this.entry.getWritableConfig().set("flipped", (Object)((Boolean)this.entry.getWritableConfig().get("flipped", (Object)false) == false ? 1 : 0));
            return;
        }
        carts.forEach(StandardProperties::reverseSavedCart);
        Collections.reverse(carts);
        this.entry.getWritableConfig().setNodeList("carts", carts);
    }

    public void setOrientationLocked(boolean locked) {
        if (this.isEmpty()) {
            return;
        }
        List carts = this.entry.getWritableConfig().getNodeList("carts");
        for (ConfigurationNode cart : carts) {
            if (locked) {
                StandardProperties.LOCK_ORIENTATION_FLIPPED.writeToConfig(cart, Optional.of(CartLockOrientation.locked((Boolean)cart.get("flipped", (Object)false))));
                continue;
            }
            StandardProperties.LOCK_ORIENTATION_FLIPPED.writeToConfig(cart, Optional.empty());
        }
        this.entry.getWritableConfig().setNodeList("carts", carts);
    }

    public Set<SavedClaim> getClaims() {
        return this.entry.isRemoved() ? Collections.emptySet() : SavedClaim.loadClaims(this.entry.getConfig());
    }

    public void setClaims(Collection<SavedClaim> claims) {
        if (!this.entry.isRemoved()) {
            SavedClaim.saveClaims(this.entry.getWritableConfig(), claims);
        }
    }

    public boolean hasPermission(CommandSender sender) {
        return this.entry.isRemoved() || SavedClaim.hasPermission(this.entry.getConfig(), sender);
    }

    public SpawnableGroup toSpawnableGroup() {
        return SpawnableGroup.fromConfig(this);
    }

    public List<ConfigurationNode> getCarts() {
        if (this.entry.getConfig().isNode("carts")) {
            return this.entry.getConfig().getNodeList("carts");
        }
        return Collections.emptyList();
    }

    public int getNumberOfCarts() {
        return this.getCarts().size();
    }

    public int getNumberOfSeats() {
        int count = 0;
        for (ConfigurationNode cart : this.getCarts()) {
            if (!cart.isNode("model")) continue;
            count += SavedTrainProperties.getNumberOfSeatAttachmentsRecurse(cart.getNode("model"));
        }
        return count;
    }

    public double getTotalTrainLength() {
        double totalLength = 0.0;
        List<ConfigurationNode> carts = this.getCarts();
        if (!carts.isEmpty()) {
            double prevCartCouplerLength = 0.0;
            boolean first = true;
            for (ConfigurationNode cart : carts) {
                double cartCouplerLength = (Double)cart.getOrDefault("model.physical.cartCouplerLength", (Object)(0.5 * TCConfig.cartDistanceGap));
                if (first) {
                    first = false;
                } else {
                    totalLength += prevCartCouplerLength + cartCouplerLength;
                }
                prevCartCouplerLength = cartCouplerLength;
                totalLength += ((Double)cart.getOrDefault("model.physical.cartLength", (Object)0.98f)).doubleValue();
            }
        }
        return totalLength;
    }

    public int getSpawnLimit() {
        return this.entry.isRemoved() ? -1 : (Integer)this.entry.getConfig().getOrDefault("spawnLimit", (Object)-1);
    }

    public void setSpawnLimit(int limit) {
        if (!this.entry.isRemoved()) {
            if (limit >= 0) {
                this.entry.getWritableConfig().set("spawnLimit", (Object)limit);
            } else {
                this.entry.getWritableConfig().remove("spawnLimit");
            }
        }
    }

    public int getSpawnLimitCurrentCount() {
        if (this.entry.isRemoved()) {
            return 0;
        }
        int count = 0;
        for (TrainProperties properties : TrainPropertiesStore.getAll()) {
            if (!properties.get(StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS).contains(this.getName())) continue;
            ++count;
        }
        return count;
    }

    private static int getNumberOfSeatAttachmentsRecurse(ConfigurationNode attachmentConfig) {
        int count = 0;
        if (AttachmentTypeRegistry.instance().fromConfig(attachmentConfig) == CartAttachmentSeat.TYPE) {
            count = 1;
        }
        if (attachmentConfig.isNode("attachments")) {
            for (ConfigurationNode childAttachment : attachmentConfig.getNodeList("attachments")) {
                count += SavedTrainProperties.getNumberOfSeatAttachmentsRecurse(childAttachment);
            }
        }
        return count;
    }
}

