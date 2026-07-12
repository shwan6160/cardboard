/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.defaults;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public class DefaultProperties {
    private final ConfigurationNode config;
    private final List<DefaultProperty<?>> properties;
    private final List<DefaultProperty<?>> propertiesWithValues;

    public static DefaultProperties of(ConfigurationNode defaultConfig) {
        return new DefaultProperties(defaultConfig);
    }

    private DefaultProperties(ConfigurationNode config) {
        Collection<IProperty<Object>> registeredProperties = IPropertyRegistry.instance().all();
        this.config = config;
        this.properties = new ArrayList(registeredProperties.size());
        this.propertiesWithValues = new ArrayList(registeredProperties.size());
        for (IProperty<Object> property : registeredProperties) {
            if (!property.isAppliedAsDefault()) continue;
            DefaultProperty defaultProperty = property instanceof ICartProperty ? new DefaultCartProperty<Object>(property, config) : new DefaultTrainProperty<Object>(property, config);
            this.properties.add(defaultProperty);
            if (!defaultProperty.set) continue;
            this.propertiesWithValues.add(defaultProperty);
        }
    }

    public ConfigurationNode getConfig() {
        return this.config;
    }

    public void applyTo(TrainProperties properties) {
        for (DefaultProperty<?> defaultProperty : this.propertiesWithValues) {
            defaultProperty.applyTo(properties);
        }
        properties.tryUpdate();
        for (CartProperties prop : properties) {
            prop.tryUpdate();
        }
    }

    public void applyTo(CartProperties properties) {
        for (DefaultProperty<?> defaultProperty : this.propertiesWithValues) {
            defaultProperty.applyTo(properties);
        }
        properties.tryUpdate();
    }

    public boolean checkSavedTrainPermissions(CommandSender sender, SpawnableGroup spawnableGroup) {
        ArrayList<ConfigurationNode> cartConfigs = new ArrayList<ConfigurationNode>(spawnableGroup.getMembers().size());
        for (SpawnableMember member : spawnableGroup.getMembers()) {
            cartConfigs.add(member.getConfig());
        }
        return this.checkSavedTrainPermissions(sender, spawnableGroup.getConfig(), cartConfigs);
    }

    public boolean checkSavedTrainPermissions(CommandSender player, ConfigurationNode trainConfig) {
        List cartConfigs = trainConfig.getNodeList("carts");
        return this.checkSavedTrainPermissions(player, trainConfig, cartConfigs);
    }

    private boolean checkSavedTrainPermissions(CommandSender sender, ConfigurationNode trainConfig, List<ConfigurationNode> cartConfigs) {
        boolean canChangeProperties = Permission.COMMAND_PROPERTIES.has(sender) || Permission.COMMAND_GLOBALPROPERTIES.has(sender);
        for (DefaultProperty<?> property : this.properties) {
            if (property.isEqual(trainConfig, cartConfigs)) continue;
            if (!canChangeProperties) {
                Localization.PROPERTY_NOPERM.message(sender, new String[]{property.permissionName});
                Localization.PROPERTY_NOPERM_ANY.message(sender, new String[0]);
                return false;
            }
            if (property.property.hasPermission(sender, property.permissionName)) continue;
            Localization.PROPERTY_NOPERM.message(sender, new String[]{property.permissionName});
            return false;
        }
        return true;
    }

    private static class DefaultCartProperty<T>
    extends DefaultProperty<T> {
        public DefaultCartProperty(IProperty<T> property, ConfigurationNode config) {
            super(property, config);
        }

        @Override
        public boolean isEqual(ConfigurationNode trainConfig, List<ConfigurationNode> cartConfigs) {
            for (ConfigurationNode cartConfig : cartConfigs) {
                if (LogicUtil.bothNullOrEqual(this.property.readFromConfig(cartConfig).orElse(this.property.getDefault()), (Object)this.value)) continue;
                return false;
            }
            return true;
        }
    }

    private static class DefaultTrainProperty<T>
    extends DefaultProperty<T> {
        public DefaultTrainProperty(IProperty<T> property, ConfigurationNode config) {
            super(property, config);
        }

        @Override
        public boolean isEqual(ConfigurationNode trainConfig, List<ConfigurationNode> cartConfigs) {
            return LogicUtil.bothNullOrEqual(this.property.readFromConfig(trainConfig).orElse(this.property.getDefault()), (Object)this.value);
        }
    }

    private static abstract class DefaultProperty<T> {
        public final IProperty<T> property;
        public final String permissionName;
        public final boolean set;
        public final T value;

        public DefaultProperty(IProperty<T> property, ConfigurationNode config) {
            this.property = property;
            this.permissionName = property.getPermissionName();
            Optional<T> valueOpt = property.readFromConfig(config);
            this.set = valueOpt.isPresent();
            this.value = valueOpt.orElse(property.getDefault());
        }

        public void applyTo(IProperties properties) {
            properties.set(this.property, this.value);
        }

        public abstract boolean isEqual(ConfigurationNode var1, List<ConfigurationNode> var2);
    }
}

