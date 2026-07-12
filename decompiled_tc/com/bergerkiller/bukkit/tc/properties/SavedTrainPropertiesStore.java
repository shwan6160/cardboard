/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.TrainSpawnPattern;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.properties.SavedClaim;
import com.bergerkiller.bukkit.tc.properties.SavedTrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.BasicModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationFile;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class SavedTrainPropertiesStore
implements TrainCarts.Provider {
    protected final TrainCarts traincarts;
    protected final ModularConfigurationEntry.Container<SavedTrainProperties> container;

    protected SavedTrainPropertiesStore(TrainCarts traincarts, ModularConfigurationEntry.Container<SavedTrainProperties> container) {
        this.traincarts = traincarts;
        this.container = container;
    }

    public static SavedTrainPropertiesStore create(TrainCarts traincarts, String filename, String directoryName) {
        ModularConfig modularConfig = new ModularConfig(traincarts, filename, directoryName);
        return new DefaultStore(traincarts, modularConfig);
    }

    static SavedTrainPropertiesStore createModule(ModularConfigurationModule<SavedTrainProperties> module) {
        ModularConfig modularConfig = (ModularConfig)module.getMain();
        if (module == modularConfig.getDefaultModule()) {
            return modularConfig.traincarts.getSavedTrains();
        }
        return new ModuleStore(modularConfig.traincarts, module);
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.traincarts;
    }

    public String getName() {
        return this.container.getName();
    }

    public abstract boolean isDefault();

    public abstract List<String> getModuleNames();

    public abstract SavedTrainPropertiesStore getModule(String var1);

    public abstract SavedTrainPropertiesStore getDefaultModule();

    public String getModuleNameOfTrain(String name) {
        ModularConfigurationEntry<SavedTrainProperties> entry = this.container.getIfExists(name);
        return entry == null ? null : entry.getModule().getName();
    }

    public abstract void setModuleNameOfTrain(String var1, String var2);

    public boolean hasPermission(CommandSender sender, String name) {
        SavedTrainProperties savedProperties = this.getProperties(name);
        return savedProperties == null || savedProperties.hasPermission(sender);
    }

    public Set<SavedClaim> getClaims(String name) {
        SavedTrainProperties savedProperties = this.getProperties(name);
        return savedProperties == null ? Collections.emptySet() : savedProperties.getClaims();
    }

    public void setClaim(String name, Player player) {
        this.setClaims(name, Collections.singleton(new SavedClaim((OfflinePlayer)player)));
    }

    public void setClaims(String name, Set<SavedClaim> claims) {
        SavedTrainProperties savedProperties = this.getProperties(name);
        if (savedProperties != null) {
            savedProperties.setClaims(claims);
        }
    }

    public boolean containsTrain(String name) {
        return this.container.getIfExists(name) != null;
    }

    public abstract void save(boolean var1);

    public abstract void reload();

    @Deprecated
    public void save(MinecartGroup group, String name, String module) throws IllegalNameException {
        this.saveGroup(name, group);
        this.setModuleNameOfTrain(name, module);
    }

    public void saveGroup(String name, MinecartGroup group) throws IllegalNameException {
        this.setConfig(name, group.saveConfig());
    }

    public SavedTrainProperties setConfig(String name, ConfigurationNode config) throws IllegalNameException {
        if (name == null || name.isEmpty()) {
            throw new IllegalNameException("Name is empty");
        }
        if (Character.isDigit(name.charAt(0))) {
            throw new IllegalNameException("Name starts with a digit");
        }
        List claims = Collections.emptyList();
        int spawnLimit = -1;
        ModularConfigurationEntry<SavedTrainProperties> entry = this.container.getIfExists(name);
        if (entry != null) {
            if (entry.getConfig().contains("claims")) {
                claims = new ArrayList(entry.getConfig().getList("claims", String.class));
            }
            spawnLimit = (Integer)entry.getConfig().getOrDefault("spawnLimit", (Object)-1);
        }
        entry = this.container.add(name, config);
        entry.getWritableConfig().set("claims", claims);
        entry.getWritableConfig().set("spawnLimit", (Object)(spawnLimit >= 0 ? Integer.valueOf(spawnLimit) : null));
        return entry.get();
    }

    public SavedTrainProperties getProperties(String name) {
        ModularConfigurationEntry<SavedTrainProperties> entry = this.container.getIfExists(name);
        return entry == null ? null : entry.get();
    }

    public abstract SavedTrainProperties getPropertiesOrNone(String var1);

    public ConfigurationNode getConfig(String name) {
        ModularConfigurationEntry<SavedTrainProperties> entry = this.container.getIfExists(name);
        return entry == null ? null : entry.getConfig();
    }

    public String findName(String text) {
        return TrainSpawnPattern.findNameInSortedList(this.getNames(), text);
    }

    public boolean remove(String name) {
        try {
            return this.container.remove(name) != null;
        }
        catch (ReadOnlyModuleException ex) {
            return false;
        }
    }

    public boolean rename(String name, String newName) {
        return this.container.rename(name, newName);
    }

    public boolean reverse(String name) {
        SavedTrainProperties properties = this.getProperties(name);
        if (properties != null) {
            properties.reverse();
            return true;
        }
        return false;
    }

    public List<String> getNames() {
        return this.container.getNames();
    }

    private static class ModularConfig
    extends BasicModularConfiguration<SavedTrainProperties> {
        private final TrainCarts traincarts;

        public ModularConfig(TrainCarts plugin, String mainFilePath, String moduleDirectoryPath) {
            super((Plugin)plugin, mainFilePath, moduleDirectoryPath);
            this.traincarts = plugin;
            this.addResourcePack(TCConfig.resourcePack, "traincarts", "saved_train_properties");
        }

        @Override
        protected void preProcessModuleConfiguration(ConfigurationNode moduleConfig) {
            this.renameTrainsBeginningWithDigits(moduleConfig);
            this.storeSavedNameInConfig(moduleConfig);
        }

        @Override
        protected void postProcessEntryConfiguration(ModularConfigurationEntry<SavedTrainProperties> entry) {
            ConfigurationNode config = entry.getWritableConfig();
            if (!config.contains("savedName") || !((String)config.get("savedName", (Object)"")).equals(entry.getName())) {
                config.set("savedName", (Object)entry.getName());
            }
            StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS.writeToConfig(config, Optional.empty());
        }

        @Override
        protected SavedTrainProperties decodeConfig(ModularConfigurationEntry<SavedTrainProperties> entry) {
            return new SavedTrainProperties(this.traincarts, entry);
        }

        private void renameTrainsBeginningWithDigits(ConfigurationNode savedTrainsConfig) {
            for (ConfigurationNode config : new ArrayList(savedTrainsConfig.getNodes())) {
                String name = config.getName();
                if (!name.isEmpty() && !Character.isDigit(name.charAt(0))) continue;
                String new_name = "t" + name;
                int i = 1;
                while (savedTrainsConfig.contains(new_name)) {
                    new_name = "t" + name + i;
                    ++i;
                }
                this.logger.log(Level.WARNING, "Train name '" + name + "' starts with a digit, renamed to " + new_name);
                config.set("savedName", (Object)new_name);
                config.remove();
                savedTrainsConfig.set(new_name, (Object)config);
            }
        }

        private void storeSavedNameInConfig(ConfigurationNode savedTrainsConfig) {
            boolean logSavedNameFieldWarning = false;
            for (ConfigurationNode config : savedTrainsConfig.getNodes()) {
                if (!config.contains("savedName")) {
                    config.set("savedName", (Object)config.getName());
                    continue;
                }
                String setName = (String)config.get("savedName", (Object)config.getName());
                if (config.getName().equals(setName)) continue;
                this.logger.log(Level.WARNING, "Saved train '" + config.getName() + "' has a different name set: '" + setName + "'");
                logSavedNameFieldWarning = true;
                config.set("savedName", (Object)config.getName());
            }
            if (logSavedNameFieldWarning) {
                this.logger.log(Level.WARNING, "If the intention was to rename the train, instead rename the key, not field 'savedName'");
            }
        }
    }

    private static class DefaultStore
    extends SavedTrainPropertiesStore {
        private final ModularConfig modularConfig;

        public DefaultStore(TrainCarts traincarts, ModularConfig modularConfig) {
            super(traincarts, modularConfig);
            this.modularConfig = modularConfig;
        }

        @Override
        public void save(boolean autosave) {
            if (autosave) {
                this.modularConfig.saveChanges();
            } else {
                this.modularConfig.save();
            }
        }

        @Override
        public void reload() {
            this.modularConfig.reload();
        }

        @Override
        public boolean isDefault() {
            return true;
        }

        @Override
        public List<String> getModuleNames() {
            return this.modularConfig.MODULES.getFileNames();
        }

        @Override
        public SavedTrainPropertiesStore getModule(String moduleName) {
            ModularConfigurationFile<SavedTrainProperties> module = this.modularConfig.MODULES.getFile(moduleName);
            return module == null ? null : DefaultStore.createModule(module);
        }

        @Override
        public SavedTrainPropertiesStore getDefaultModule() {
            return new ModuleStore(this.modularConfig.traincarts, this.modularConfig.getDefaultModule());
        }

        @Override
        public void setModuleNameOfTrain(String name, String module) {
            ModularConfigurationEntry entry = this.modularConfig.getIfExists(name);
            if (entry != null) {
                entry.setModule(this.modularConfig.createModule(module));
            }
        }

        @Override
        public SavedTrainProperties getPropertiesOrNone(String name) {
            return (SavedTrainProperties)this.modularConfig.get(name).get();
        }
    }

    public static class ModuleStore
    extends SavedTrainPropertiesStore {
        private final ModularConfigurationModule<SavedTrainProperties> module;

        public ModuleStore(TrainCarts traincarts, ModularConfigurationModule<SavedTrainProperties> module) {
            super(traincarts, module);
            this.module = module;
        }

        @Override
        public void save(boolean autosave) {
            if (autosave) {
                this.module.saveChanges();
            } else {
                this.module.save();
            }
        }

        @Override
        public void reload() {
            this.module.reload();
        }

        @Override
        public boolean isDefault() {
            return false;
        }

        @Override
        public List<String> getModuleNames() {
            return Collections.emptyList();
        }

        @Override
        public SavedTrainPropertiesStore getModule(String moduleName) {
            return null;
        }

        @Override
        public SavedTrainPropertiesStore getDefaultModule() {
            return this;
        }

        @Override
        public void setModuleNameOfTrain(String name, String module) {
        }

        @Override
        public SavedTrainProperties getPropertiesOrNone(String name) {
            return this.module.getMain().get(name).get();
        }
    }
}

