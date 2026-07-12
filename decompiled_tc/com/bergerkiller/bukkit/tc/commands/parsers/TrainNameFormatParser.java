/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import org.bukkit.command.CommandSender;

public class TrainNameFormatParser
implements QuotedArgumentParser<CommandSender, TrainNameFormat> {
    public static ParserDescriptor<CommandSender, TrainNameFormat> trainNameFormatParser() {
        return new TrainNameFormatParser().createDescriptor(TrainNameFormat.class);
    }

    public ArgumentParseResult<TrainNameFormat> parseQuotedString(CommandContext<CommandSender> commandContext, String inputName) {
        TrainNameFormat name = TrainNameFormat.parse(inputName);
        TrainNameFormat.VerifyResult verify = name.verify();
        if (verify != TrainNameFormat.VerifyResult.OK) {
            return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)verify.getMessage(), new String[]{inputName}));
        }
        return ArgumentParseResult.success((Object)name);
    }
}

