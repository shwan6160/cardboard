/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.commands.argument.AttachmentsByName;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IPropertiesHolder;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AttachmentByNameParser<T extends Attachment>
implements QuotedArgumentParser<CommandSender, AttachmentsByName<T>>,
BlockingSuggestionProvider.Strings<CommandSender> {
    private final boolean forTrain;
    private final Predicate<Attachment> filter;
    private final Localization emptyMessage;

    public static <T extends Attachment> AttachmentByNameParser<T> all(Class<? extends Attachment> attachmentType, boolean forTrain) {
        return new AttachmentByNameParser<T>(forTrain, attachmentType::isInstance, Localization.COMMAND_INPUT_ATTACHMENTS_NO_OTHER);
    }

    public static <T extends Attachment> AttachmentByNameParser<T> seats(boolean forTrain) {
        return new AttachmentByNameParser<T>(forTrain, a -> a instanceof CartAttachmentSeat, Localization.COMMAND_INPUT_ATTACHMENTS_NO_SEATS);
    }

    public static <T extends Attachment> AttachmentByNameParser<T> effects(boolean forTrain) {
        return new AttachmentByNameParser<T>(forTrain, a -> a instanceof Attachment.EffectAttachment, Localization.COMMAND_INPUT_ATTACHMENTS_NO_EFFECTS);
    }

    public AttachmentByNameParser(boolean forTrain, Predicate<Attachment> filter, Localization emptyMessage) {
        this.forTrain = forTrain;
        this.filter = filter;
        this.emptyMessage = emptyMessage;
    }

    public List<T> parse(CommandContext<CommandSender> context, String name) {
        List<Attachment> result = this.lookup(context).get(name, this.filter);
        if (result.isEmpty()) {
            throw new CloudLocalizedException(context, (ILocalizationEnum)this.emptyMessage, new String[]{name});
        }
        return result;
    }

    private List<String> names(MinecartMember<?> member) {
        return member.getAttachments().getRootAttachment().getNameLookup().names(this.filter);
    }

    private AttachmentNameLookup lookup(CommandContext<CommandSender> context) {
        try {
            if (this.forTrain) {
                TrainProperties properties = (TrainProperties)context.inject(TrainProperties.class).get();
                MinecartGroup group = properties.getHolder();
                if (group == null) {
                    return AttachmentNameLookup.EMPTY;
                }
                return group.getAttachments().getNameLookup();
            }
            CartProperties properties = (CartProperties)context.inject(CartProperties.class).get();
            IPropertiesHolder member = properties.getHolder();
            if (member == null) {
                return AttachmentNameLookup.EMPTY;
            }
            return ((MinecartMember)member).getAttachments().getNameLookup();
        }
        catch (RuntimeException ex) {
            return AttachmentNameLookup.EMPTY;
        }
    }

    public @NonNull ArgumentParseResult<@NonNull AttachmentsByName<T>> parseQuotedString(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull String name) {
        return ArgumentParseResult.success(new AttachmentsByName(name, this, commandContext));
    }

    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput input) {
        IPropertiesHolder member;
        if (!(commandContext.sender() instanceof Player)) {
            return Collections.emptyList();
        }
        CartProperties props = ((TrainCarts)((Object)commandContext.inject(TrainCarts.class).get())).getPlayer((Player)commandContext.sender()).getEditedCart();
        if (props == null || (member = props.getHolder()) == null) {
            return Collections.emptyList();
        }
        AttachmentNameLookup lookup = this.forTrain ? ((MinecartMember)member).getGroup().getAttachments().getNameLookup() : ((MinecartMember)member).getAttachments().getNameLookup();
        return lookup.names(this.filter);
    }
}

