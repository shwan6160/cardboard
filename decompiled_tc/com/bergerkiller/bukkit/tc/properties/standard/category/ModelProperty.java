/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardCartProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.AttachmentModelBoundToCart;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public final class ModelProperty
extends FieldBackedStandardCartProperty<AttachmentModel> {
    @Override
    public String getPermissionName() {
        return "model (attachment editor)";
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.COMMAND_GIVE_EDITOR.has(sender);
    }

    @Override
    public AttachmentModel getDefault() {
        return null;
    }

    @Override
    public void onConfigurationChanged(CartProperties properties) {
        FieldBackedProperty.CartInternalData data = FieldBackedProperty.CartInternalData.get(properties);
        if (data.model != null) {
            data.model.sync();
        }
    }

    @Override
    public AttachmentModel get(CartProperties properties) {
        FieldBackedProperty.CartInternalData data = FieldBackedProperty.CartInternalData.get(properties);
        if (data.model == null) {
            data.model = new AttachmentModelBoundToCart(properties);
        }
        return data.model;
    }

    @Override
    public void set(CartProperties properties, AttachmentModel value) {
        FieldBackedProperty.CartInternalData data = FieldBackedProperty.CartInternalData.get(properties);
        if (value == null || value.isDefault()) {
            if (data.model != null) {
                data.model.resetToDefaults();
            } else {
                properties.getConfig().remove("model");
            }
        } else if (data.model == null) {
            properties.getConfig().set("model", (Object)value.getConfig().clone());
        } else if (data.model != value) {
            data.model.update(value.getConfig());
        }
    }

    @Override
    public AttachmentModel get(TrainProperties properties) {
        return properties.isEmpty() ? this.getDefault() : this.get(properties.get(0));
    }

    @Override
    public void set(TrainProperties properties, AttachmentModel value) {
        for (CartProperties cProp : properties) {
            this.set(cProp, value);
        }
    }

    @Override
    public AttachmentModel getData(FieldBackedProperty.CartInternalData data) {
        return data.model;
    }

    @Override
    public void setData(FieldBackedProperty.CartInternalData data, AttachmentModel value) {
        if (value == null || value.isDefault()) {
            if (data.model != null) {
                data.model.resetToDefaults();
            }
        } else if (data.model != null && data.model != value) {
            data.model.update(value.getConfig());
        }
    }

    @Override
    public Optional<AttachmentModel> readFromConfig(ConfigurationNode config) {
        if (config.isNode("model")) {
            return Optional.of(new AttachmentModel(config.getNode("model")));
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<AttachmentModel> value) {
        if (value.isPresent()) {
            config.set("model", (Object)value.get().getConfig().clone());
        } else {
            config.remove("model");
        }
    }
}

