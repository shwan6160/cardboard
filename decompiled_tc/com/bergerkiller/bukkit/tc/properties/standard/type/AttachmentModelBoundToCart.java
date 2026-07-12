/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.EntityType
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlChangeListener;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentEntity;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import java.util.Arrays;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class AttachmentModelBoundToCart
extends AttachmentModel {
    private final ModelConfigSupplier configSupplier;

    public AttachmentModelBoundToCart(CartProperties properties) {
        this(new ModelConfigSupplier(properties));
    }

    private AttachmentModelBoundToCart(ModelConfigSupplier configSupplier) {
        super(configSupplier);
        this.configSupplier = configSupplier;
    }

    @Override
    public boolean isDefault() {
        return this.configSupplier.isDefault();
    }

    @Override
    public void resetToDefaults() {
        this.configSupplier.makeDefaults();
        this.sync();
    }

    public static ConfigurationNode createDefaults(AttachmentTypeRegistry typeRegistry, EntityType entityType) {
        ConfigurationNode config = new ConfigurationNode();
        typeRegistry.toConfig(config, CartAttachmentEntity.TYPE);
        config.set("entityType", (Object)entityType);
        if (entityType == EntityType.MINECART) {
            ConfigurationNode seatNode = new ConfigurationNode();
            typeRegistry.toConfig(seatNode, CartAttachmentSeat.TYPE);
            config.setNodeList("attachments", Arrays.asList(seatNode));
        }
        return config;
    }

    private static class ModelConfigSupplier
    implements Supplier<ConfigurationNode> {
        private final CartProperties properties;
        private EntityType cartEntityType;
        private EntityType defaultConfigEntityType;
        private ConfigurationNode defaultConfig;

        public ModelConfigSupplier(CartProperties properties) {
            this.properties = properties;
            this.cartEntityType = null;
            this.defaultConfigEntityType = null;
            this.defaultConfig = null;
        }

        public boolean isDefault() {
            return !this.properties.getConfig().isNode("model");
        }

        @Override
        public ConfigurationNode get() {
            EntityType entityType;
            IPropertiesHolder member;
            ConfigurationNode config = this.properties.getConfig();
            if (config.isNode("model")) {
                this.defaultConfig = null;
                this.defaultConfigEntityType = null;
                return config.getNode("model");
            }
            if (this.cartEntityType == null && (member = this.properties.getHolder()) != null && member.getEntity() != null) {
                this.cartEntityType = ((CommonMinecart)member.getEntity()).getType();
            }
            EntityType entityType2 = entityType = this.cartEntityType == null ? EntityType.MINECART : this.cartEntityType;
            if (entityType != this.defaultConfigEntityType) {
                this.defaultConfigEntityType = entityType;
                final ConfigurationNode currConfig = this.defaultConfig = AttachmentModelBoundToCart.createDefaults(AttachmentTypeRegistry.instance(), entityType);
                currConfig.addChangeListener(new YamlChangeListener(){
                    final /* synthetic */ ModelConfigSupplier this$0;
                    {
                        this.this$0 = this$0;
                    }

                    public void onNodeChanged(YamlPath yamlPath) {
                        currConfig.removeChangeListener((YamlChangeListener)this);
                        if (this.this$0.defaultConfig != currConfig) {
                            return;
                        }
                        if (!this.this$0.properties.getConfig().isNode("model")) {
                            Runnable assignTask = () -> {
                                ConfigurationNode currCartConfig = this.this$0.properties.getConfig();
                                if (!currCartConfig.isNode("model")) {
                                    currCartConfig.set("model", (Object)currConfig);
                                    this.this$0.defaultConfig = null;
                                    this.this$0.defaultConfigEntityType = null;
                                }
                            };
                            if (this.this$0.properties.getTrainCarts().isEnabled()) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.this$0.properties.getTrainCarts(), assignTask);
                            } else {
                                assignTask.run();
                            }
                        }
                    }
                });
            }
            return this.defaultConfig;
        }

        public void makeDefaults() {
            ConfigurationNode config = this.properties.getConfig();
            config.remove("model");
        }
    }
}

