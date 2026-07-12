/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.PluginIdentifiableCommand
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommand;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudCommodoreManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitHelper;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.setting.ManagerSetting;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public class BukkitPluginRegistrationHandler<C>
implements CommandRegistrationHandler<C> {
    private final Map<CommandComponent<C>, RegisteredCommandData<C>> registeredCommands = new HashMap<CommandComponent<C>, RegisteredCommandData<C>>();
    private final Set<String> recognizedAliases = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    private Map<String, org.bukkit.command.Command> bukkitCommands;
    private BukkitCommandManager<C> bukkitCommandManager;
    private CommandMap commandMap;

    protected BukkitPluginRegistrationHandler() {
    }

    final void initialize(@NonNull BukkitCommandManager<C> bukkitCommandManager) throws ReflectiveOperationException {
        Map bukkitCommands;
        Method getCommandMap = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap", new Class[0]);
        getCommandMap.setAccessible(true);
        this.commandMap = (CommandMap)getCommandMap.invoke((Object)Bukkit.getServer(), new Object[0]);
        Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommands.setAccessible(true);
        this.bukkitCommands = bukkitCommands = (Map)knownCommands.get(this.commandMap);
        this.bukkitCommandManager = bukkitCommandManager;
    }

    @Override
    public final boolean registerCommand(@NonNull Command<C> command) {
        CommandComponent<C> component = command.rootComponent();
        if (!(this.bukkitCommandManager.commandRegistrationHandler() instanceof CloudCommodoreManager) && this.registeredCommands.containsKey(component)) {
            return false;
        }
        String label = component.name();
        String namespacedLabel = BukkitHelper.namespacedLabel(this.bukkitCommandManager, label);
        ArrayList<String> aliases = new ArrayList<String>(component.alternativeAliases());
        BukkitCommand<C> bukkitCommand = new BukkitCommand<C>(label, aliases, command, component, this.bukkitCommandManager);
        if (this.bukkitCommandManager.settings().get(ManagerSetting.OVERRIDE_EXISTING_COMMANDS)) {
            this.bukkitCommands.remove(label);
            aliases.forEach(this.bukkitCommands::remove);
        }
        HashSet<String> newAliases = new HashSet<String>();
        for (String alias2 : aliases) {
            String namespacedAlias = BukkitHelper.namespacedLabel(this.bukkitCommandManager, alias2);
            newAliases.add(namespacedAlias);
            if (this.bukkitCommandOrAliasExists(alias2)) continue;
            newAliases.add(alias2);
        }
        if (!this.bukkitCommandExists(label)) {
            newAliases.add(label);
        }
        newAliases.add(namespacedLabel);
        this.commandMap.register(label, this.bukkitCommandManager.owningPlugin().getName().toLowerCase(Locale.ROOT), bukkitCommand);
        this.recognizedAliases.addAll(newAliases);
        if (this.bukkitCommandManager.splitAliases()) {
            newAliases.forEach(alias -> this.registerExternal((String)alias, command, bukkitCommand));
        }
        this.registeredCommands.put(component, new RegisteredCommandData(bukkitCommand, newAliases));
        return true;
    }

    @Override
    public final void unregisterRootCommand(@NonNull CommandComponent<C> component) {
        RegisteredCommandData<C> registeredCommand = this.registeredCommands.get(component);
        if (registeredCommand == null) {
            return;
        }
        ((RegisteredCommandData)registeredCommand).bukkit.disable();
        Set registeredAliases = ((RegisteredCommandData)registeredCommand).recognizedAliases;
        for (String alias : registeredAliases) {
            this.bukkitCommands.remove(alias);
        }
        this.recognizedAliases.removeAll(registeredAliases);
        if (this.bukkitCommandManager.splitAliases()) {
            registeredAliases.forEach(this::unregisterExternal);
        }
        this.registeredCommands.remove(component);
        if (this.bukkitCommandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
        }
    }

    public boolean isRecognized(@NonNull String alias) {
        return this.recognizedAliases.contains(alias);
    }

    protected void registerExternal(@NonNull String label, @NonNull Command<?> command, @NonNull BukkitCommand<C> bukkitCommand) {
    }

    @API(status=API.Status.STABLE, since="1.7.0")
    protected void unregisterExternal(@NonNull String label) {
    }

    private boolean bukkitCommandExists(String commandLabel) {
        org.bukkit.command.Command existingCommand = this.bukkitCommands.get(commandLabel);
        if (existingCommand == null) {
            return false;
        }
        if (existingCommand instanceof PluginIdentifiableCommand) {
            return existingCommand.getLabel().equals(commandLabel) && !((PluginIdentifiableCommand)existingCommand).getPlugin().getName().equalsIgnoreCase(this.bukkitCommandManager.owningPlugin().getName());
        }
        return existingCommand.getLabel().equals(commandLabel);
    }

    private boolean bukkitCommandOrAliasExists(String commandLabel) {
        org.bukkit.command.Command command = this.bukkitCommands.get(commandLabel);
        if (command instanceof PluginIdentifiableCommand) {
            return !((PluginIdentifiableCommand)command).getPlugin().getName().equalsIgnoreCase(this.bukkitCommandManager.owningPlugin().getName());
        }
        return command != null;
    }

    private static final class RegisteredCommandData<C> {
        private final BukkitCommand<C> bukkit;
        private final Set<String> recognizedAliases;

        private RegisteredCommandData(BukkitCommand<C> bukkit, Set<String> recognizedAliases) {
            this.bukkit = bukkit;
            TreeSet<String> treeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            treeSet.addAll(recognizedAliases);
            this.recognizedAliases = Collections.unmodifiableSet(treeSet);
        }
    }
}

