/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.entity.EntityType
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfigTracker;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModelStore;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.properties.standard.type.AttachmentModelBoundToCart;
import com.bergerkiller.bukkit.tc.utils.SetCallbackCollector;
import java.util.Collections;
import java.util.function.Supplier;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class AttachmentModel
implements SavedAttachmentModelStore.ModelUsing {
    public static final float DEFAULT_CART_LENGTH = 0.98f;
    private final AttachmentTypeRegistry registry = AttachmentTypeRegistry.instance();
    private final AttachmentConfigTracker tracker;
    private final SavedAttachmentModelStore modelStore;
    private AttachmentModelMeta _meta;

    public AttachmentModel(ConfigurationNode config) {
        this(LogicUtil.constantSupplier((Object)config));
    }

    public AttachmentModel(Supplier<ConfigurationNode> configSupplier) {
        this.tracker = new AttachmentConfigTracker(configSupplier, (Plugin)TrainCarts.plugin);
        this.modelStore = TrainCarts.plugin.getSavedAttachmentModels();
        this._meta = new AttachmentModelMeta();
    }

    public ConfigurationNode getConfig() {
        return this.tracker.getConfig();
    }

    public ConfigurationNode getNodeConfig(int[] targetPath) {
        AttachmentConfig child = this.tracker.getRoot().get().child(targetPath);
        return child == null ? null : child.config();
    }

    public AttachmentConfig.RootReference getRoot() {
        return this.tracker.getRoot();
    }

    public AttachmentConfigTracker getConfigTracker() {
        return this.tracker;
    }

    public int getSeatCount() {
        return this.calcMeta().seatCount;
    }

    public float getCartLength() {
        return this.calcMeta().cartLength;
    }

    public double getCartCouplerLength() {
        return this.calcMeta().cartCouplerLength;
    }

    public double getWheelDistance() {
        return this.calcMeta().wheelDistance;
    }

    public double getWheelCenter() {
        return this.calcMeta().wheelCenter;
    }

    public boolean isDefault() {
        return false;
    }

    public void resetToDefaults() {
        this.update(AttachmentModelBoundToCart.createDefaults(this.registry, EntityType.MINECART));
    }

    public void resetToName(String modelName) {
        ConfigurationNode config = new ConfigurationNode();
        this.registry.toConfig(config, CartAttachmentModel.TYPE);
        config.set("model", (Object)modelName);
        this.update(config, false);
    }

    public void update(ConfigurationNode newConfig) {
        if (this.getConfig() != newConfig) {
            this.getConfig().clear();
            this.getConfig().setToExcept((YamlNodeAbstract)newConfig, Collections.singleton("savedName"));
        }
        this.sync();
    }

    @Deprecated
    public void update(ConfigurationNode newConfig, boolean notify) {
        this.update(newConfig);
    }

    public void sync() {
        this.tracker.sync();
    }

    public void updateNode(int[] targetPath, ConfigurationNode newConfig) {
        AttachmentConfig child = this.tracker.getRoot().get().child(targetPath);
        if (child != null) {
            child.setConfig(newConfig);
            this.sync();
        }
    }

    public void removeNode(int[] targetPath) {
        AttachmentConfig child = this.tracker.getRoot().get().child(targetPath);
        if (child != null) {
            child.config().remove();
            this.sync();
        }
    }

    @Deprecated
    public void updateNode(int[] targetPath, ConfigurationNode newConfig, boolean notify) {
        this.updateNode(targetPath, newConfig);
    }

    @Override
    public void getUsedModels(SetCallbackCollector<SavedAttachmentModel> collector) {
        this.modelStore.findModelsUsedInConfiguration(this.getRoot().get(), collector);
    }

    private AttachmentModelMeta calcMeta() {
        AttachmentModelMeta meta = this._meta;
        if (meta.valid()) {
            return meta;
        }
        meta = this._meta = new AttachmentModelMeta(this.registry, this.tracker.getRoot());
        return this._meta;
    }

    public static AttachmentModel getDefaultModel(EntityType minecartType) {
        return new AttachmentModel(AttachmentModelBoundToCart.createDefaults(AttachmentTypeRegistry.instance(), minecartType));
    }

    private static class AttachmentModelMeta {
        public final AttachmentConfig.RootReference root;
        public final int seatCount;
        public final float cartLength;
        public final double cartCouplerLength;
        public final double wheelCenter;
        public final double wheelDistance;

        public AttachmentModelMeta() {
            this.root = AttachmentConfig.RootReference.NONE;
            this.seatCount = 0;
            this.cartLength = 0.98f;
            this.cartCouplerLength = 0.5 * TCConfig.cartDistanceGap;
            this.wheelCenter = 0.0;
            this.wheelDistance = 0.0;
        }

        public AttachmentModelMeta(AttachmentTypeRegistry registry, AttachmentConfig.RootReference root) {
            this.root = root;
            AttachmentConfig rootAtt = root.get();
            this.seatCount = AttachmentModelMeta.calcSeatCount(registry, rootAtt);
            if (rootAtt.config().isNode("physical")) {
                ConfigurationNode physical = rootAtt.config().getNode("physical");
                this.cartLength = ((Float)physical.get("cartLength", (Object)Float.valueOf(0.98f))).floatValue();
                this.cartCouplerLength = (Double)physical.getOrDefault("cartCouplerLength", (Object)(0.5 * TCConfig.cartDistanceGap));
                this.wheelCenter = (Double)physical.get("wheelCenter", (Object)0.0);
                this.wheelDistance = (Double)physical.get("wheelDistance", (Object)0.0);
            } else {
                this.cartLength = 0.98f;
                this.cartCouplerLength = 0.5 * TCConfig.cartDistanceGap;
                this.wheelCenter = 0.0;
                this.wheelDistance = 0.0;
            }
        }

        public boolean valid() {
            return this.root.valid();
        }

        private static int calcSeatCount(AttachmentTypeRegistry registry, AttachmentConfig attachment) {
            int count = 0;
            if (registry.find(attachment.typeId()) == CartAttachmentSeat.TYPE) {
                count = 1;
            }
            for (AttachmentConfig child : attachment.children()) {
                count += AttachmentModelMeta.calcSeatCount(registry, child);
            }
            return count;
        }
    }
}

