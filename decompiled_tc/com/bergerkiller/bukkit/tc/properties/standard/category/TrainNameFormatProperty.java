/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorConditionList;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class TrainNameFormatProperty
implements ITrainProperty<TrainNameFormat> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="name")
    @Command(value="train rename|setname|name <new_name>")
    @CommandDescription(value="Renames a train")
    private void trainSetNameFormat(CommandSender sender, TrainProperties properties, @Argument(value="new_name") TrainNameFormat newName, @Flag(value="generate", description="Append a number to make the name unique, if needed") boolean generate) {
        if (!newName.hasOptionalNumber() && !generate && TrainPropertiesStore.exists(newName.toString()) && !newName.matches(properties.getTrainName())) {
            sender.sendMessage(ChatColor.RED + "This name is already taken! Some suggestions:");
            sender.sendMessage(ChatColor.RED + "- Include # somewhere in the name to insert a random number");
            sender.sendMessage(ChatColor.RED + "- Pass --generate to append on if needed");
            return;
        }
        properties.set(this, newName);
        MessageBuilder builder = new MessageBuilder();
        this.appendNameInfo(builder, properties, "Train renamed to: ");
        builder.send(sender);
    }

    @Command(value="train rename|setname|name")
    @CommandDescription(value="Displays the current name of the train being edited")
    private void trainGetNameFormat(CommandSender sender, TrainProperties properties) {
        MessageBuilder builder = new MessageBuilder();
        this.appendNameInfo(builder, properties, "Train name: ");
        builder.send(sender);
    }

    public void appendNameInfo(MessageBuilder builder, TrainProperties properties, String prefix) {
        TrainNameFormat format = properties.get(this);
        if (TrainNameFormat.DEFAULT.equals(format)) {
            builder.yellow(new Object[]{prefix}).white(new Object[]{properties.getTrainName()});
            builder.red(new Object[]{" (Default)"});
        } else if (format.toString().equals(properties.getTrainName())) {
            builder.yellow(new Object[]{prefix}).white(new Object[]{properties.getTrainName()});
        } else {
            builder.yellow(new Object[]{prefix}).white(new Object[]{properties.getTrainName()});
            builder.yellow(new Object[]{" (Format: "}).blue(new Object[]{format}).yellow(new Object[]{")"});
        }
    }

    @PropertyParser(value="name|rename|setname|settrainname")
    public TrainNameFormat parseRename(String nameFormat) {
        TrainNameFormat name = TrainNameFormat.parse(nameFormat);
        TrainNameFormat.VerifyResult verify = name.verify();
        if (verify != TrainNameFormat.VerifyResult.OK) {
            throw new PropertyInvalidInputException(verify.getMessage().get(nameFormat));
        }
        return name;
    }

    @PropertySelectorConditionList(value={@PropertySelectorCondition(value="name"), @PropertySelectorCondition(value="train")})
    public String getSelectorMatchedTrainName(TrainProperties properties) {
        return StringUtil.stripChatStyle((String)properties.getTrainName());
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_NAME.has(sender);
    }

    @Override
    public TrainNameFormat getDefault() {
        return TrainNameFormat.DEFAULT;
    }

    @Override
    public boolean isAppliedAsDefault() {
        return true;
    }

    @Override
    public Optional<TrainNameFormat> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "name", String.class).map(TrainNameFormat::parse);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<TrainNameFormat> value) {
        Util.setConfigOptional(config, "name", value.map(TrainNameFormat::toString));
    }

    @Override
    public TrainNameFormat get(TrainProperties properties) {
        Optional<TrainNameFormat> fromConfig = this.readFromConfig(properties.getConfig());
        if (fromConfig.isPresent()) {
            return fromConfig.get();
        }
        if (TrainNameFormat.DEFAULT.matches(properties.getTrainName())) {
            return TrainNameFormat.DEFAULT;
        }
        return TrainNameFormat.guess(properties.getTrainName());
    }

    @Override
    public void set(TrainProperties properties, TrainNameFormat value) {
        ITrainProperty.super.set(properties, value);
        if (!value.matches(properties.getTrainName())) {
            properties.setTrainName(value.search(TrainPropertiesStore::isUseableName));
        }
    }
}

