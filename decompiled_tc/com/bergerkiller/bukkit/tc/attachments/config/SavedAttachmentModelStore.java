/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.properties.SavedClaim;
import com.bergerkiller.bukkit.tc.utils.SetCallbackCollector;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.BasicModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationFile;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class SavedAttachmentModelStore
implements TrainCarts.Provider {
    protected final TrainCarts traincarts;
    protected final ModularConfigurationEntry.Container<SavedAttachmentModel> container;

    protected SavedAttachmentModelStore(TrainCarts traincarts, ModularConfigurationEntry.Container<SavedAttachmentModel> container) {
        this.traincarts = traincarts;
        this.container = container;
    }

    public static SavedAttachmentModelStore create(TrainCarts traincarts, String filename, String directoryName) {
        ModularConfig modularConfig = new ModularConfig(traincarts, filename, directoryName);
        return new DefaultStore(traincarts, modularConfig);
    }

    static SavedAttachmentModelStore createModule(ModularConfigurationModule<SavedAttachmentModel> module) {
        ModularConfig modularConfig = (ModularConfig)module.getMain();
        if (module == modularConfig.getDefaultModule()) {
            return modularConfig.traincarts.getSavedAttachmentModels();
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

    public abstract SavedAttachmentModelStore getModule(String var1);

    public String getModuleNameOfModel(String name) {
        ModularConfigurationEntry<SavedAttachmentModel> entry = this.container.getIfExists(name);
        return entry == null ? null : entry.getModule().getName();
    }

    public abstract void setModuleNameOfModel(String var1, String var2);

    public boolean hasPermission(CommandSender sender, String name) {
        SavedAttachmentModel savedProperties = this.getModel(name);
        return savedProperties == null || savedProperties.hasPermission(sender);
    }

    public Set<SavedClaim> getClaims(String name) {
        SavedAttachmentModel savedProperties = this.getModel(name);
        return savedProperties == null ? Collections.emptySet() : savedProperties.getClaims();
    }

    public void setClaim(String name, Player player) {
        this.setClaims(name, Collections.singleton(new SavedClaim((OfflinePlayer)player)));
    }

    public void setClaims(String name, Collection<SavedClaim> claims) {
        SavedAttachmentModel savedModel = this.getModel(name);
        if (savedModel != null) {
            savedModel.setClaims(claims);
        }
    }

    public boolean containsModel(String name) {
        return this.container.getIfExists(name) != null;
    }

    public abstract void save(boolean var1);

    public abstract void reload();

    public SavedAttachmentModel setDefaultConfigIfMissing(String name) throws IllegalNameException {
        return this.setDefaultConfigIfMissing(name, null);
    }

    public SavedAttachmentModel setDefaultConfigIfMissing(String name, CommandSender editingPlayer) throws IllegalNameException {
        SavedAttachmentModel existing = this.getModel(name);
        if (existing != null) {
            return existing;
        }
        ConfigurationNode config = new ConfigurationNode();
        AttachmentTypeRegistry.instance().toConfig(config, CartAttachmentItem.TYPE);
        config.set("item", (Object)new ItemStack(MaterialUtil.getMaterial((String)"LEGACY_WOOD")));
        return this.setConfigAsPlayer(name, config, editingPlayer);
    }

    public SavedAttachmentModel setConfig(String name, ConfigurationNode config) throws IllegalNameException {
        return this.setConfigAsPlayer(name, config, null);
    }

    public SavedAttachmentModel setConfigAsPlayer(String name, ConfigurationNode config, CommandSender editingPlayer) throws IllegalNameException {
        if (name == null || name.isEmpty()) {
            throw new IllegalNameException("Name is empty");
        }
        List<Object> claims = Collections.emptyList();
        ModularConfigurationEntry<SavedAttachmentModel> entry = this.container.getIfExists(name);
        if (entry == null && editingPlayer instanceof Player && TCConfig.claimNewSavedModels) {
            claims = Collections.singletonList(new SavedClaim((OfflinePlayer)((Player)editingPlayer)).toString());
        } else if (entry != null && entry.getConfig().contains("claims")) {
            claims = new ArrayList(entry.getConfig().getList("claims", String.class));
        }
        entry = this.container.add(name, config);
        entry.getWritableConfig().set("claims", claims);
        return entry.get();
    }

    public SavedAttachmentModel getModel(String name) {
        ModularConfigurationEntry<SavedAttachmentModel> entry = this.container.getIfExists(name);
        return entry == null ? null : entry.get();
    }

    public abstract SavedAttachmentModel getModelOrNone(String var1);

    public final SavedAttachmentModel getEditingInit(Player player) {
        return this.traincarts.getPlayer(player).getEditedModelInit();
    }

    public final SavedAttachmentModel getEditingInit(UUID playerUUID) {
        return this.traincarts.getPlayer(playerUUID).getEditedModelInit();
    }

    public final SavedAttachmentModel getEditing(Player player) {
        return this.traincarts.getPlayer(player).getEditedModel();
    }

    public final SavedAttachmentModel getEditing(UUID playerUUID) {
        return this.traincarts.getPlayer(playerUUID).getEditedModel();
    }

    public final void setEditing(Player player, SavedAttachmentModel model) {
        this.traincarts.getPlayer(player).editModel(model);
    }

    public final void setEditing(UUID playerUUID, SavedAttachmentModel model) {
        this.traincarts.getPlayer(playerUUID).editModel(model);
    }

    public ConfigurationNode getConfig(String name) {
        ModularConfigurationEntry<SavedAttachmentModel> entry = this.container.getIfExists(name);
        return entry == null ? null : entry.getConfig();
    }

    public String findName(String text) {
        String foundName = null;
        for (String name : this.getNames()) {
            if (!text.startsWith(name) || foundName != null && name.length() <= foundName.length()) continue;
            foundName = name;
        }
        return foundName;
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

    public List<String> getNames() {
        return this.container.getNames();
    }

    public List<SavedAttachmentModel> getAll() {
        return this.container.getAllValues();
    }

    public void findModelsUsedInConfiguration(AttachmentConfig attachmentConfig, SetCallbackCollector<SavedAttachmentModel> models) {
        String name;
        SavedAttachmentModel model;
        if (attachmentConfig instanceof AttachmentConfig.Model && models.acceptCheckAdded(model = this.getModelOrNone(name = ((AttachmentConfig.Model)attachmentConfig).modelName())) && !model.isNone()) {
            this.findModelsUsedInConfiguration(model.getRoot().get(), models);
        }
        for (AttachmentConfig child : attachmentConfig.children()) {
            this.findModelsUsedInConfiguration(child, models);
        }
    }

    public void findModelsUsedInConfiguration(ConfigurationNode attachmentConfig, SetCallbackCollector<SavedAttachmentModel> models) {
        SavedAttachmentModel model;
        String modelName;
        if ("MODEL".equals(attachmentConfig.getOrDefault("type", (Object)"EMPTY")) && !(modelName = (String)attachmentConfig.getOrDefault("modelName", (Object)"")).isEmpty() && models.acceptCheckAdded(model = this.traincarts.getSavedAttachmentModels().getModelOrNone(modelName)) && !model.isNone()) {
            this.findModelsUsedInConfiguration(model.getRoot().get(), models);
        }
        for (ConfigurationNode child : attachmentConfig.getNodeList("attachments")) {
            this.findModelsUsedInConfiguration(child, models);
        }
    }

    private static class ModularConfig
    extends BasicModularConfiguration<SavedAttachmentModel> {
        private final TrainCarts traincarts;

        public ModularConfig(TrainCarts plugin, String mainFilePath, String moduleDirectoryPath) {
            super((Plugin)plugin, mainFilePath, moduleDirectoryPath);
            this.traincarts = plugin;
            this.addResourcePack(TCConfig.resourcePack, "traincarts", "saved_models");
        }

        @Override
        protected void preProcessModuleConfiguration(ConfigurationNode moduleConfig) {
            this.storeSavedNameInConfig(moduleConfig);
        }

        @Override
        protected void postProcessEntryConfiguration(ModularConfigurationEntry<SavedAttachmentModel> entry) {
            ConfigurationNode config = entry.getWritableConfig();
            if (!config.contains("savedName") || !((String)config.get("savedName", (Object)"")).equals(entry.getName())) {
                config.set("savedName", (Object)entry.getName());
            }
        }

        @Override
        protected SavedAttachmentModel decodeConfig(ModularConfigurationEntry<SavedAttachmentModel> entry) {
            return new SavedAttachmentModel(entry);
        }

        private void storeSavedNameInConfig(ConfigurationNode savedModelsConfig) {
            boolean logSavedNameFieldWarning = false;
            for (ConfigurationNode config : savedModelsConfig.getNodes()) {
                if (!config.contains("savedName")) {
                    config.set("savedName", (Object)config.getName());
                    continue;
                }
                String setName = (String)config.get("savedName", (Object)config.getName());
                if (config.getName().equals(setName)) continue;
                this.logger.log(Level.WARNING, "Saved attachment model '" + config.getName() + "' has a different name set: '" + setName + "'");
                logSavedNameFieldWarning = true;
                config.set("savedName", (Object)config.getName());
            }
            if (logSavedNameFieldWarning) {
                this.logger.log(Level.WARNING, "If the intention was to rename the model, instead rename the key, not field 'savedName'");
            }
        }
    }

    private static class DefaultStore
    extends SavedAttachmentModelStore {
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
        public SavedAttachmentModelStore getModule(String moduleName) {
            ModularConfigurationFile<SavedAttachmentModel> module = this.modularConfig.MODULES.getFile(moduleName);
            return module == null ? null : DefaultStore.createModule(module);
        }

        @Override
        public void setModuleNameOfModel(String name, String module) {
            ModularConfigurationEntry entry = this.modularConfig.getIfExists(name);
            if (entry != null) {
                entry.setModule(this.modularConfig.createModule(module));
            }
        }

        @Override
        public SavedAttachmentModel getModelOrNone(String name) {
            return (SavedAttachmentModel)this.modularConfig.get(name).get();
        }
    }

    public static class ModuleStore
    extends SavedAttachmentModelStore {
        private final ModularConfigurationModule<SavedAttachmentModel> module;

        public ModuleStore(TrainCarts traincarts, ModularConfigurationModule<SavedAttachmentModel> module) {
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
        public SavedAttachmentModelStore getModule(String moduleName) {
            return null;
        }

        @Override
        public void setModuleNameOfModel(String name, String module) {
        }

        @Override
        public SavedAttachmentModel getModelOrNone(String name) {
            return this.module.getMain().get(name).get();
        }
    }

    public static interface ModelUsing {
        default public ConfigurationNode getUsedModelsAsExport() {
            Set<SavedAttachmentModel> models = this.getUsedModels();
            if (!models.isEmpty()) {
                ConfigurationNode result = new ConfigurationNode();
                for (SavedAttachmentModel model : models) {
                    if (model.isNone()) continue;
                    result.set(model.getName(), (Object)model.getConfig().clone());
                }
                if (!result.isEmpty()) {
                    return result;
                }
            }
            return null;
        }

        default public Set<SavedAttachmentModel> getUsedModels() {
            SetCallbackCollector<SavedAttachmentModel> models = new SetCallbackCollector<SavedAttachmentModel>();
            this.getUsedModels(models);
            return models.result();
        }

        public void getUsedModels(SetCallbackCollector<SavedAttachmentModel> var1);
    }
}

