/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardTrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.type.BankingOptions;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class BankingOptionsProperty
extends FieldBackedStandardTrainProperty<BankingOptions> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="banking")
    @Command(value="train banking <strength> <smoothness>")
    @CommandDescription(value="Sets a new train banking strength and smoothness")
    private void trainSetBanking(CommandSender sender, TrainProperties properties, @Argument(value="strength") double strength, @Argument(value="smoothness") double smoothness) {
        properties.setBanking(strength, smoothness);
        this.trainGetBankingInfo(sender, properties);
    }

    @Command(value="train banking")
    @CommandDescription(value="Displays the current train banking settings")
    private void trainGetBankingInfo(CommandSender sender, TrainProperties properties) {
        if (properties.getBankingStrength() == 0.0) {
            sender.sendMessage(ChatColor.YELLOW + "Train banking " + ChatColor.RED + "is inactive. " + ChatColor.YELLOW + "Change strength to enable.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Train banking " + ChatColor.BLUE + "strength " + ChatColor.WHITE + properties.getBankingStrength() + ChatColor.BLUE + " smoothness " + ChatColor.WHITE + properties.getBankingSmoothness());
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="banking")
    @Command(value="train banking strength <strength>")
    @CommandDescription(value="Sets a new train banking strength")
    private void trainSetBankingStrength(CommandSender sender, TrainProperties properties, @Argument(value="strength") double strength) {
        properties.setBankingStrength(strength);
        this.trainGetBankingStrength(sender, properties);
    }

    @Command(value="train banking strength")
    @CommandDescription(value="Displays the currently configured train banking strength")
    private void trainGetBankingStrength(CommandSender sender, TrainProperties properties) {
        if (properties.getBankingStrength() == 0.0) {
            sender.sendMessage(ChatColor.YELLOW + "Train banking strength: " + ChatColor.WHITE + "0 " + ChatColor.RED + "(Inactive)");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Train banking strength: " + ChatColor.WHITE + properties.getBankingStrength());
        }
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="banking")
    @Command(value="train banking smoothness <strength>")
    @CommandDescription(value="Sets a new train banking smoothness")
    private void trainSetBankingSmoothness(CommandSender sender, TrainProperties properties, @Argument(value="strength") double strength) {
        properties.setBankingSmoothness(strength);
        this.trainGetBankingSmoothness(sender, properties);
    }

    @Command(value="train banking smoothness")
    @CommandDescription(value="Displays the currently configured train banking smoothness")
    private void trainGetBankingSmoothness(CommandSender sender, TrainProperties properties) {
        sender.sendMessage(ChatColor.YELLOW + "Train banking smoothness: " + ChatColor.WHITE + properties.getBankingSmoothness());
    }

    @PropertyParser(value="banking")
    public BankingOptions parseBanking(PropertyParseContext<BankingOptions> context) {
        double newSmoothness;
        double newStrength;
        String[] args = context.input().trim().split(" ");
        if (args.length >= 2) {
            newStrength = ParseUtil.parseDouble((String)args[0], (double)Double.NaN);
            newSmoothness = ParseUtil.parseDouble((String)args[1], (double)Double.NaN);
        } else {
            newStrength = ParseUtil.parseDouble((String)context.input(), (double)Double.NaN);
            newSmoothness = context.current().smoothness();
        }
        if (Double.isNaN(newStrength)) {
            throw new PropertyInvalidInputException("Banking strength is not a number");
        }
        if (Double.isNaN(newSmoothness)) {
            throw new PropertyInvalidInputException("Banking smoothness is not a number");
        }
        return BankingOptions.create(newStrength, newSmoothness);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_BANKING.has(sender);
    }

    @Override
    public BankingOptions getDefault() {
        return BankingOptions.DEFAULT;
    }

    @Override
    public BankingOptions getData(FieldBackedProperty.TrainInternalData data) {
        return data.bankingOptionsData;
    }

    @Override
    public void setData(FieldBackedProperty.TrainInternalData data, BankingOptions value) {
        data.bankingOptionsData = value;
    }

    @Override
    public Optional<BankingOptions> readFromConfig(ConfigurationNode config) {
        if (!config.isNode("banking")) {
            return Optional.empty();
        }
        ConfigurationNode banking = config.getNode("banking");
        return Optional.of(BankingOptions.create((Double)banking.get("strength", (Object)0.0), (Double)banking.get("smoothness", (Object)0.0)));
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<BankingOptions> value) {
        if (value.isPresent()) {
            BankingOptions data = value.get();
            ConfigurationNode banking = config.getNode("banking");
            banking.set("strength", (Object)data.strength());
            banking.set("smoothness", (Object)data.smoothness());
        } else {
            config.remove("banking");
        }
    }
}

