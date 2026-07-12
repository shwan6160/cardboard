/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.CollisionMode;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionMobCategory;
import com.bergerkiller.bukkit.tc.properties.standard.type.CollisionOptions;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class CollisionProperty
extends FieldBackedStandardTrainProperty<CollisionOptions> {
    public void appendCollisionInfo(MessageBuilder builder, TrainProperties properties) {
        CollisionOptions opt = properties.getCollision();
        builder.yellow(new Object[]{"Collision rules for the train:"});
        this.appendCollisionMode(builder, opt.blockMode(), "blocks");
        if (TCConfig.collisionIgnoreGlobalOwners) {
            this.appendCollisionMode(builder, CollisionMode.DEFAULT, "administrators");
            builder.newLine().white(new Object[]{"      collision.ignoreGlobalOwners = true "}).blue(new Object[]{"[config.yml]"});
        }
        if (TCConfig.collisionIgnoreOwners) {
            this.appendCollisionMode(builder, CollisionMode.DEFAULT, "owners of this train");
            builder.newLine().white(new Object[]{"      collision.ignoreOwners = true "}).blue(new Object[]{"[config.yml]"});
        }
        this.appendCollisionMode(builder, opt.playerMode(), TCConfig.collisionIgnoreGlobalOwners || TCConfig.collisionIgnoreOwners ? "other players" : "players");
        this.appendCollisionMode(builder, opt.trainMode(), "other trains");
        for (Map.Entry<CollisionMobCategory, CollisionMode> entry : opt.mobModes().entrySet()) {
            this.appendCollisionMode(builder, entry.getValue(), entry.getKey().getPluralMobType());
        }
        this.appendCollisionMode(builder, opt.miscMode(), "miscellaneous entities");
    }

    private void appendCollisionMode(MessageBuilder builder, CollisionMode mode, String who) {
        builder.newLine().yellow(new Object[]{" - "}).red(new Object[]{mode.getOperationName()}).yellow(new Object[]{" "}).blue(new Object[]{who});
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="collision")
    @Command(value="train collision default|true")
    @CommandDescription(value="Configures the default collision settings")
    private void trainSetCollisionDefault(CommandSender sender, TrainProperties properties) {
        properties.setCollision(CollisionOptions.DEFAULT);
        this.trainGetCollisionInfo(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="collision")
    @Command(value="train collision none|false")
    @CommandDescription(value="Disables collision with all entities and blocks")
    private void trainSetCollisionNone(CommandSender sender, TrainProperties properties) {
        properties.setCollision(CollisionOptions.CANCEL);
        this.trainGetCollisionInfo(sender, properties);
    }

    @Command(value="train collision")
    @CommandDescription(value="Gets all collision rules configured for a train")
    private void trainGetCollisionInfo(CommandSender sender, TrainProperties properties) {
        MessageBuilder builder = new MessageBuilder();
        this.appendCollisionInfo(builder, properties);
        builder.send(sender);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="mobcollision")
    @Command(value="train collision <mobcategory> <mode>")
    @CommandDescription(value="Sets new behavior when colliding with a given mob category")
    private void trainSetMobCollision(CommandSender sender, TrainProperties properties, @Argument(value="mobcategory") CollisionMobCategory category, @Argument(value="mode") CollisionMode mode) {
        properties.setCollisionMode(category, mode);
        this.trainGetMobCollision(sender, properties, category);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="mobcollision")
    @Command(value="train collision mobs|mob <mode>")
    @CommandDescription(value="Sets new behavior when colliding with all types of mob")
    private void trainSetAllMobCollision(CommandSender sender, TrainProperties properties, @Argument(value="mode") CollisionMode mode) {
        properties.setCollisionModeForMobs(mode);
        CollisionProperty.showMode(sender, "mobs", mode);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="mobcollision")
    @Command(value="train collision <mobcategory> none")
    @CommandDescription(value="Resets behavior when colliding with a given mob category")
    private void trainResetMobCollision(CommandSender sender, TrainProperties properties, @Argument(value="mobcategory") CollisionMobCategory category) {
        properties.setCollisionMode(category, null);
        this.trainGetMobCollision(sender, properties, category);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="mobcollision")
    @Command(value="train collision mobs|mob none")
    @CommandDescription(value="Resets behavior when colliding with any type of mob")
    private void trainResetAllMobsCollision(CommandSender sender, TrainProperties properties) {
        properties.setCollisionModeForMobs(null);
        sender.sendMessage(ChatColor.YELLOW + "Reset collision rules for all mob types. Will default to misc.");
    }

    @Command(value="train collision <mobcategory>")
    @CommandDescription(value="Gets the current behavior when colliding with a given mob category")
    private void trainGetMobCollision(CommandSender sender, TrainProperties properties, @Argument(value="mobcategory") CollisionMobCategory category) {
        CollisionMode mode = properties.getCollision().mobMode(category);
        if (mode == null) {
            sender.sendMessage(ChatColor.YELLOW + "The train has no specific mob collision mode set");
            sender.sendMessage(ChatColor.YELLOW + "Other mob collision rules might be set. If none are, behavior defaults to what is set for misc: ");
            CollisionProperty.showMode(sender, category.getPluralMobType(), properties.getCollision().miscMode());
        } else {
            CollisionProperty.showMode(sender, category.getPluralMobType(), mode);
        }
    }

    @Command(value="train collision mobs|mob")
    @CommandDescription(value="Gets the current behavior when colliding with a given mob category")
    private void trainGetAllMobCollision(CommandSender sender, TrainProperties properties) {
        CollisionOptions options = properties.getCollision();
        if (options.mobModes().isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "The train has no specific mob collision modes set");
            sender.sendMessage(ChatColor.YELLOW + "Behavior will default to what is set for misc: ");
            CollisionProperty.showMode(sender, "mobs", options.miscMode());
            return;
        }
        CollisionMode foundMode = null;
        boolean hasNonMobModes = false;
        for (CollisionMobCategory category : CollisionMobCategory.values()) {
            CollisionMode modeForMob = options.mobMode(category);
            if (category.isMobCategory()) {
                if (modeForMob == null || foundMode != null && foundMode != modeForMob) {
                    foundMode = null;
                    break;
                }
                if (foundMode != null) continue;
                foundMode = modeForMob;
                continue;
            }
            if (modeForMob == null) continue;
            hasNonMobModes = true;
            CollisionProperty.showMode(sender, category.getPluralMobType(), modeForMob);
        }
        if (foundMode != null) {
            CollisionProperty.showMode(sender, hasNonMobModes ? "other mobs" : "mobs", foundMode);
            return;
        }
        for (Map.Entry<CollisionMobCategory, CollisionMode> mode : options.mobModes().entrySet()) {
            if (!mode.getKey().isMobCategory()) continue;
            CollisionProperty.showMode(sender, mode.getKey().getPluralMobType(), mode.getValue());
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="blockcollision")
    @Command(value="train collision block <mode>")
    @CommandDescription(value="Sets the behavior of the train when colliding with blocks")
    private void trainSetBlockCollision(CommandSender sender, TrainProperties properties, @Argument(value="mode") CollisionMode mode) {
        properties.setCollision(properties.getCollision().cloneAndSetBlockMode(mode));
        this.trainGetBlockCollision(sender, properties);
    }

    @Command(value="train collision block")
    @CommandDescription(value="Gets the behavior of the train when colliding with blocks")
    private void trainGetBlockCollision(CommandSender sender, TrainProperties properties) {
        CollisionProperty.showMode(sender, "blocks", properties.getCollision().blockMode());
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="playercollision")
    @Command(value="train collision player <mode>")
    @CommandDescription(value="Sets the behavior of the train when colliding with players")
    private void trainSetPlayerCollision(CommandSender sender, TrainProperties properties, @Argument(value="mode") CollisionMode mode) {
        properties.setCollision(properties.getCollision().cloneAndSetPlayerMode(mode));
        this.trainGetPlayerCollision(sender, properties);
    }

    @Command(value="train collision player")
    @CommandDescription(value="Gets the behavior of the train when colliding with players")
    private void trainGetPlayerCollision(CommandSender sender, TrainProperties properties) {
        CollisionProperty.showMode(sender, "players", properties.getCollision().playerMode());
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="traincollision")
    @Command(value="train collision train <mode>")
    @CommandDescription(value="Sets the behavior of the train when colliding with other trains")
    private void trainSetTrainCollision(CommandSender sender, TrainProperties properties, @Argument(value="mode") CollisionMode mode) {
        properties.setCollision(properties.getCollision().cloneAndSetTrainMode(mode));
        this.trainGetTrainCollision(sender, properties);
    }

    @Command(value="train collision train")
    @CommandDescription(value="Gets the behavior of the train when colliding with other trains")
    private void trainGetTrainCollision(CommandSender sender, TrainProperties properties) {
        CollisionProperty.showMode(sender, "other trains", properties.getCollision().trainMode());
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="misccollision")
    @Command(value="train collision misc <mode>")
    @CommandDescription(value="Sets the behavior of the train when colliding with miscellaneous mobs and entities")
    private void trainSetMiscCollision(CommandSender sender, TrainProperties properties, @Argument(value="mode") CollisionMode mode) {
        properties.setCollision(properties.getCollision().cloneAndSetMiscMode(mode));
        this.trainGetMiscCollision(sender, properties);
    }

    @Command(value="train collision misc")
    @CommandDescription(value="Gets the behavior of the train when colliding with miscellaneous mobs and entities")
    private void trainGetMiscCollision(CommandSender sender, TrainProperties properties) {
        CollisionProperty.showMode(sender, "miscellaneous mobs and entities", properties.getCollision().miscMode());
    }

    private static void showMode(CommandSender sender, String category, CollisionMode mode) {
        MessageBuilder builder = new MessageBuilder();
        builder.yellow(new Object[]{"The train "}).red(new Object[]{mode.getOperationName()});
        builder.yellow(new Object[]{" "}).blue(new Object[]{category}).yellow(new Object[]{" when colliding"});
        builder.send(sender);
    }

    @PropertyParser(value="playercollision")
    public CollisionOptions parsePlayerCollisionMode(PropertyParseContext<CollisionOptions> context) {
        return context.current().cloneAndSetPlayerMode(this.parseMode(context));
    }

    @PropertyParser(value="misccollision")
    public CollisionOptions parseMiscCollisionMode(PropertyParseContext<CollisionOptions> context) {
        return context.current().cloneAndSetMiscMode(this.parseMode(context));
    }

    @PropertyParser(value="traincollision")
    public CollisionOptions parseTrainCollisionMode(PropertyParseContext<CollisionOptions> context) {
        return context.current().cloneAndSetTrainMode(this.parseMode(context));
    }

    @PropertyParser(value="blockcollision")
    public CollisionOptions parseBlockCollisionMode(PropertyParseContext<CollisionOptions> context) {
        return context.current().cloneAndSetBlockMode(this.parseMode(context));
    }

    @PropertyParser(value="([a-z]+)collision")
    public CollisionOptions parseCollisionMobsType(PropertyParseContext<CollisionOptions> context) {
        return this.parseUpdateForMobs(context, context.nameGroup(1), this.parseModeOrReset(context));
    }

    @PropertyParser(value="linking|link")
    public CollisionOptions parseLinkingMode(PropertyParseContext<CollisionOptions> context) {
        if (context.inputBoolean()) {
            return context.current().cloneAndSetTrainMode(CollisionMode.LINK);
        }
        if (context.current().trainMode() == CollisionMode.LINK) {
            return context.current().cloneAndSetTrainMode(CollisionMode.DEFAULT);
        }
        return context.current();
    }

    @PropertyParser(value="pushplayers")
    public CollisionOptions parsePushPlayers(PropertyParseContext<CollisionOptions> context) {
        return context.current().cloneAndSetPlayerMode(CollisionMode.fromPushing(context.inputBoolean()));
    }

    @PropertyParser(value="pushmisc")
    public CollisionOptions parsePushMisc(PropertyParseContext<CollisionOptions> context) {
        return context.current().cloneAndSetMiscMode(CollisionMode.fromPushing(context.inputBoolean()));
    }

    @PropertyParser(value="push([a-z]+)")
    public CollisionOptions parsePushingMobsType(PropertyParseContext<CollisionOptions> context) {
        CollisionMode mode = ParseUtil.isBool((String)context.input()) ? CollisionMode.fromPushing(context.inputBoolean()) : this.parseModeOrReset(context);
        return this.parseUpdateForMobs(context, context.nameGroup(1), mode);
    }

    @PropertyParser(value="push|pushing")
    public CollisionOptions parsePushing(PropertyParseContext<CollisionOptions> context) {
        CollisionMode mode = CollisionMode.fromPushing(context.inputBoolean());
        return context.current().cloneAndSetPlayerMode(mode).cloneAndSetMiscMode(mode).cloneAndSetForAllMobs(mode);
    }

    @PropertyParser(value="mobenter|mobsenter")
    public CollisionOptions parseMobsEnter(PropertyParseContext<CollisionOptions> context) {
        if (context.inputBoolean()) {
            return context.current().cloneAndSetForAllMobs(CollisionMode.ENTER);
        }
        return context.current().cloneCompareAndSetForAllMobs(CollisionMode.ENTER, CollisionMode.DEFAULT);
    }

    @PropertyParser(value="collision|collide")
    public CollisionOptions parseDefaultOrNoCollision(PropertyParseContext<CollisionOptions> context) {
        return context.inputBoolean() ? CollisionOptions.DEFAULT : CollisionOptions.CANCEL;
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_COLLISION.has(sender);
    }

    @Override
    public CollisionOptions getDefault() {
        return CollisionOptions.DEFAULT;
    }

    @Override
    public CollisionOptions getData(FieldBackedProperty.TrainInternalData data) {
        return data.collision;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, CollisionOptions value) {
        data.collision = value;
    }

    @Override
    public Optional<CollisionOptions> readFromConfig(ConfigurationNode config) {
        if (config.contains("trainCollision") && !((Boolean)config.get("trainCollision", (Object)true)).booleanValue()) {
            CollisionOptions collision = CollisionOptions.CANCEL;
            if (config.contains("collision.block")) {
                collision = collision.cloneAndSetBlockMode((CollisionMode)((Object)config.get("collision.block", (Object)CollisionMode.DEFAULT)));
            }
            return Optional.of(collision);
        }
        if (config.isNode("collision")) {
            ConfigurationNode collisionConfig = config.getNode("collision");
            CollisionOptions.Builder builder = CollisionOptions.builder();
            if (collisionConfig.contains("players")) {
                builder.setPlayerMode(this.readMode(collisionConfig, "players", CollisionOptions.DEFAULT.playerMode()));
            }
            if (collisionConfig.contains("misc")) {
                builder.setMiscMode(this.readMode(collisionConfig, "misc", CollisionOptions.DEFAULT.miscMode()));
            }
            if (collisionConfig.contains("train")) {
                builder.setTrainMode(this.readMode(collisionConfig, "train", CollisionOptions.DEFAULT.trainMode()));
            }
            if (collisionConfig.contains("block")) {
                builder.setBlockMode(this.readMode(collisionConfig, "block", CollisionOptions.DEFAULT.blockMode()));
            }
            if (collisionConfig.contains("mobs")) {
                builder.setModeForAllMobs(this.readMode(collisionConfig, "mobs", null));
            } else if (collisionConfig.contains("mob")) {
                builder.setModeForAllMobs(this.readMode(collisionConfig, "mob", null));
            }
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                CollisionMode mode;
                if (collisionConfig.contains(category.getMobType())) {
                    mode = this.readMode(collisionConfig, category.getMobType(), null);
                } else {
                    if (!collisionConfig.contains(category.getPluralMobType())) continue;
                    mode = this.readMode(collisionConfig, category.getPluralMobType(), null);
                }
                if (mode == null) continue;
                builder.setMobMode(category, mode);
            }
            return Optional.of(builder.build());
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<CollisionOptions> value) {
        config.remove("trainCollision");
        if (value.isPresent()) {
            boolean hasMobsMode;
            ConfigurationNode collisionConfig = config.getNode("collision");
            CollisionOptions data = value.get();
            List mobCollisionModes = Stream.of(CollisionMobCategory.values()).filter(CollisionMobCategory::isMobCategory).map(data::mobMode).distinct().collect(Collectors.toList());
            boolean bl = hasMobsMode = mobCollisionModes.size() == 1 && mobCollisionModes.get(0) != null;
            if (hasMobsMode) {
                collisionConfig.set("mobs", mobCollisionModes.get(0));
            } else {
                collisionConfig.remove("mobs");
            }
            collisionConfig.remove("mob");
            for (CollisionMobCategory category : CollisionMobCategory.values()) {
                CollisionMode mode = hasMobsMode && category.isMobCategory() ? null : data.mobMode(category);
                if (mode != null) {
                    collisionConfig.set(category.getMobType(), (Object)mode);
                } else {
                    collisionConfig.remove(category.getMobType());
                }
                collisionConfig.remove(category.getPluralMobType());
            }
            collisionConfig.set("players", (Object)data.playerMode());
            collisionConfig.set("misc", (Object)data.miscMode());
            collisionConfig.set("train", (Object)data.trainMode());
            collisionConfig.set("block", (Object)data.blockMode());
        } else {
            config.remove("collision");
        }
    }

    private CollisionMode readMode(ConfigurationNode config, String key, CollisionMode defValue) {
        CollisionMode parsed;
        String name = (String)config.get(key, String.class, null);
        if (name != null && (parsed = CollisionMode.parse(name)) != null) {
            return parsed;
        }
        return null;
    }

    private CollisionMode parseMode(PropertyParseContext<CollisionOptions> context) {
        CollisionMode mode = CollisionMode.parse(context.input());
        if (mode == null) {
            throw new PropertyInvalidInputException("Not a valid collision mode");
        }
        return mode;
    }

    private CollisionMode parseModeOrReset(PropertyParseContext<CollisionOptions> context) {
        if (context.input().equalsIgnoreCase("reset")) {
            return null;
        }
        return this.parseMode(context);
    }

    private CollisionOptions parseUpdateForMobs(PropertyParseContext<CollisionOptions> context, String mobType, CollisionMode newMode) {
        if (mobType.equals("mob") || mobType.equals("mobs")) {
            return context.current().cloneAndSetForAllMobs(newMode);
        }
        boolean matchedMode = false;
        CollisionOptions newCollision = context.current();
        for (CollisionMobCategory mobCategory : CollisionMobCategory.values()) {
            if (!mobType.equals(mobCategory.getMobType()) && !mobType.equals(mobCategory.getPluralMobType())) continue;
            newCollision = newCollision.cloneAndSetMobMode(mobCategory, newMode);
            matchedMode = true;
        }
        if (!matchedMode) {
            throw new PropertyInvalidInputException("Invalid collision category: " + mobType);
        }
        return newCollision;
    }
}

