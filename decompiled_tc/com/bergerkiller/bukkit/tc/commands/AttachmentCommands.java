/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.ProtoItemStack
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.ProtoItemStack;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.commands.argument.AttachmentsByName;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AttachmentCommands {
    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="train attachments")
    @CommandDescription(value="Gives an attachment editor map item to the player")
    private void commandGiveAttachmentEditor(Player sender) {
        CommonItemStack item = CommonItemStack.of((ItemStack)MapDisplay.createMapItem(AttachmentEditor.class)).setCustomNameMessage("Traincarts Attachments Editor").setFilledMapColor(0xFF0000);
        sender.getInventory().addItem(new ItemStack[]{item.toBukkit()});
        sender.sendMessage(ChatColor.GREEN + "Given a Traincarts attachments editor");
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="train attachments update item <attachment_name> <item> ")
    @CommandDescription(value="Updates the displayed item of an item attachment")
    private void trainCommandSetAttachmentItem(CommandSender sender, @Argument(value="attachment_name", parserName="trainItemDisplayAttachments") AttachmentsByName<Attachment.ItemDisplayAttachment> itemAttachments, @Argument(value="item") ProtoItemStack protoItem) {
        itemAttachments.validate();
        ItemStack item = protoItem.createItemStack(1);
        itemAttachments.attachments().forEach(a -> a.setDisplayedItem(item));
        sender.sendMessage(ChatColor.YELLOW + "Item updated for " + itemAttachments.attachments().size() + " attachment(s)");
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="cart attachments update item <attachment_name> <item> ")
    @CommandDescription(value="Updates the displayed item of an item attachment")
    private void cartCommandSetAttachmentItem(CommandSender sender, @Argument(value="attachment_name", parserName="cartItemDisplayAttachments") AttachmentsByName<Attachment.ItemDisplayAttachment> itemAttachments, @Argument(value="item") ProtoItemStack protoItem) {
        itemAttachments.validate();
        ItemStack item = protoItem.createItemStack(1);
        itemAttachments.attachments().forEach(a -> a.setDisplayedItem(item));
        sender.sendMessage(ChatColor.YELLOW + "Item updated for " + itemAttachments.attachments().size() + " attachment(s)");
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="train attachments update text <attachment_name> <text> ")
    @CommandDescription(value="Updates the displayed item of an item attachment")
    private void trainCommandSetAttachmentText(CommandSender sender, @Argument(value="attachment_name", parserName="trainTextDisplayAttachments") AttachmentsByName<Attachment.TextDisplayAttachment> textAttachments, @Quoted @Argument(value="text") String text) {
        textAttachments.validate();
        ChatText chatText = ChatText.fromMessage((String)text);
        textAttachments.attachments().forEach(a -> a.setDisplayedText(chatText));
        sender.sendMessage(ChatColor.YELLOW + "Text updated for " + textAttachments.attachments().size() + " attachment(s)");
    }

    @CommandTargetTrain
    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="cart attachments update text <attachment_name> <text> ")
    @CommandDescription(value="Updates the displayed item of an item attachment")
    private void cartCommandSetAttachmentText(CommandSender sender, @Argument(value="attachment_name", parserName="cartTextDisplayAttachments") AttachmentsByName<Attachment.TextDisplayAttachment> textAttachments, @Quoted @Argument(value="text") String text) {
        textAttachments.validate();
        ChatText chatText = ChatText.fromMessage((String)text);
        textAttachments.attachments().forEach(a -> a.setDisplayedText(chatText));
        sender.sendMessage(ChatColor.YELLOW + "Text updated for " + textAttachments.attachments().size() + " attachment(s)");
    }
}

