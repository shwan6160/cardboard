/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.CommandManager
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.Suggestions
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidCommandSenderException
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  net.milkbowl.vault.economy.Economy
 *  net.milkbowl.vault.economy.EconomyResponse
 *  net.milkbowl.vault.economy.EconomyResponse$ResponseType
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.suggestion.Suggestions;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.InvalidCommandSenderException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.Suggestion;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.exception.command.NoTicketSelectedException;
import com.bergerkiller.bukkit.tc.tickets.TCTicketDisplay;
import com.bergerkiller.bukkit.tc.tickets.Ticket;
import com.bergerkiller.bukkit.tc.tickets.TicketStore;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TicketCommands {
    @Suggestions(value="ticketNames")
    public List<String> getTicketNames(CommandContext<CommandSender> context, String input) {
        return TicketStore.getAll().stream().map(Ticket::getName).collect(Collectors.toList());
    }

    public void init(CommandManager<CommandSender> manager) {
        manager.parameterInjectorRegistry().registerInjector(Ticket.class, (context, annot) -> {
            if (!(context.sender() instanceof Player)) {
                throw new InvalidCommandSenderException(context.sender(), Player.class, Collections.emptyList(), context.command());
            }
            Ticket ticket = TicketStore.getEditing((Player)context.sender());
            if (ticket == null) {
                throw new NoTicketSelectedException();
            }
            return ticket;
        });
        manager.parserRegistry().registerParser(ParserDescriptor.of((ArgumentParser)new TicketParser().createParser(), Ticket.class));
    }

    @Command(value="train list tickets")
    @CommandDescription(value="Lists the names of all tickets that exist")
    private void commandTrainList(CommandSender sender) {
        this.commandList(sender);
    }

    @Command(value="train ticket list")
    @CommandDescription(value="Lists the names of all tickets that exist")
    private void commandList(CommandSender sender) {
        MessageBuilder builder = new MessageBuilder();
        builder.yellow(new Object[]{"The following tickets are available:"});
        builder.newLine().setSeparator(ChatColor.WHITE, " / ");
        for (Ticket ticket : TicketStore.getAll()) {
            builder.green(new Object[]{ticket.getName()});
        }
        builder.send(sender);
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket edit <name>")
    @CommandDescription(value="Edits a ticket by name")
    private void commandEdit(Player sender, @Quoted @Argument(value="name") Ticket ticket) {
        TicketStore.setEditing(sender, ticket);
        sender.sendMessage(ChatColor.GREEN + "You are now editing ticket " + ChatColor.YELLOW + ticket.getName());
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket create")
    @CommandDescription(value="Creates a new ticket with a unique random name")
    private void commandCreate(Player sender) {
        Ticket newTicket = TicketStore.createTicket(TicketStore.DEFAULT);
        sender.sendMessage(ChatColor.GREEN + "You have created a new ticket with the name " + ChatColor.YELLOW + newTicket.getName());
        TicketStore.setEditing(sender, newTicket);
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket create <name>")
    @CommandDescription(value="Creates a new ticket with a name as specified")
    private void commandCreateWithName(Player sender, @Quoted @Argument(value="name") String name) {
        Ticket newTicket = TicketStore.createTicket(TicketStore.DEFAULT, name);
        if (newTicket == null) {
            sender.sendMessage(ChatColor.RED + "Can not create this ticket: name '" + name + "' is already in use!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "You have created a new ticket with the name " + ChatColor.YELLOW + newTicket.getName());
            TicketStore.setEditing(sender, newTicket);
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket give <ticket> <players>")
    @CommandDescription(value="Gives a ticket by name to one or more players")
    private void commandGiveTicket(CommandSender sender, @Argument(value="ticket") Ticket ticket, @Argument(value="players", suggestions="targetplayer") String[] playerNames) {
        for (String playerName : playerNames) {
            Player player = Util.findPlayer(sender, playerName);
            if (player == null) continue;
            ItemStack item = ticket.createItem(player);
            player.getInventory().addItem(new ItemStack[]{item});
            sender.sendMessage(ChatColor.GREEN + "Ticket " + ChatColor.YELLOW + ticket.getName() + ChatColor.GREEN + " sold to player " + ChatColor.YELLOW + player.getName());
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket sell <ticket> <price> <players>")
    @CommandDescription(value="Sells a ticket by name to one or more players by charging them money")
    private void commandSellTicket(CommandSender sender, TrainCarts plugin, @Argument(value="ticket") Ticket ticket, @Argument(value="price") double price, @Argument(value="players", suggestions="targetplayer") String[] playerNames) {
        Economy econ = plugin.getEconomy();
        if (econ == null) {
            sender.sendMessage(ChatColor.RED + "No Vault-compatible economy plugin is installed!");
            for (String playerName : playerNames) {
                Player player = Util.findPlayer(sender, playerName);
                if (player == null || player == sender) continue;
                sender.sendMessage(ChatColor.RED + "Failed to buy ticket: no vault-compatible economy plugin is installed!");
            }
            return;
        }
        if (price < 0.0) {
            sender.sendMessage(ChatColor.RED + "Price must be positive");
            return;
        }
        for (String playerName : playerNames) {
            Player player = Util.findPlayer(sender, playerName);
            if (player == null) continue;
            if (!econ.has((OfflinePlayer)player, player.getWorld().getName(), price)) {
                Localization.TICKET_BUYFAIL.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(price)});
                continue;
            }
            EconomyResponse resp = econ.withdrawPlayer((OfflinePlayer)player, player.getWorld().getName(), price);
            if (resp.type != EconomyResponse.ResponseType.SUCCESS) {
                Localization.TICKET_BUYFAIL.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(price)});
                continue;
            }
            Localization.TICKET_BUY.message((CommandSender)player, new String[]{TrainCarts.getCurrencyText(price)});
            ItemStack item = ticket.createItem(player);
            player.getInventory().addItem(new ItemStack[]{item});
            sender.sendMessage(ChatColor.GREEN + "Ticket " + ChatColor.YELLOW + ticket.getName() + ChatColor.GREEN + " given to player " + ChatColor.YELLOW + player.getName());
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket clone")
    @CommandDescription(value="Clones the currently edited ticket with a random new name")
    private void commandCloneTicket(Player sender, Ticket ticket) {
        Ticket newTicket = TicketStore.createTicket(ticket);
        sender.sendMessage(ChatColor.GREEN + "You cloned the ticket, creating a new ticket with the name " + ChatColor.YELLOW + newTicket.getName());
        TicketStore.setEditing(sender, newTicket);
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket clone <newname>")
    @CommandDescription(value="Clones the currently edited ticket with the new name specified")
    private void commandCloneTicketWithNewName(Player sender, Ticket ticket, @Argument(value="newname") String newTicketName) {
        Ticket newTicket = TicketStore.createTicket(ticket, newTicketName);
        if (newTicket == null) {
            sender.sendMessage(ChatColor.RED + "Failed to clone ticket: a ticket with the name " + newTicketName + " already exists");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "You cloned the ticket, creating a new ticket with the name " + ChatColor.YELLOW + newTicket.getName());
        TicketStore.setEditing(sender, newTicket);
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket remove")
    @CommandDescription(value="Permanently removes a ticket")
    private void commandDeleteTicket(Player sender, Ticket ticket) {
        if (ticket.remove()) {
            sender.sendMessage(ChatColor.GREEN + "Ticket has been removed!");
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to remove ticket: not found!");
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket rename <newname>")
    @CommandDescription(value="Renames the currently edited ticket")
    private void commandRenameTicket(Player sender, Ticket ticket, @Quoted @Argument(value="newname") String newTicketName) {
        if (ticket.setName(newTicketName)) {
            sender.sendMessage(ChatColor.GREEN + "Ticket has been renamed to " + ChatColor.YELLOW + ticket.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "Failed to rename ticket to " + newTicketName + ": a ticket with this name already exists!");
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket realm <newrealm>")
    @CommandDescription(value="Changes the realm of the currently edited ticket")
    private void commandSetRealm(Player sender, Ticket ticket, @Argument(value="newrealm") String newRealm) {
        ticket.setRealm(newRealm);
        TicketStore.markChanged();
        sender.sendMessage(ChatColor.GREEN + "Ticket realm set to " + ChatColor.YELLOW + newRealm);
    }

    @Command(value="train ticket background|image")
    @CommandDescription(value="Reads what background image is configured for the currently edited ticket")
    private void commandSetBackground(Player sender, Ticket ticket) {
        if (ticket.getBackgroundImagePath().isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No background image is set for this ticket (default).");
            sender.sendMessage(ChatColor.YELLOW + "To set a background image, use /train ticket background [path]");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Background image is currently set to: " + ChatColor.WHITE + ticket.getBackgroundImagePath());
            sender.sendMessage(ChatColor.YELLOW + "To set a background image, use /train ticket background [path]");
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket background|image <newimage>")
    @CommandDescription(value="Configures a custom background image for the currently edited ticket")
    private void commandSetBackground(Player sender, Ticket ticket, @Quoted @Argument(value="newimage") String newImage) {
        ticket.setBackgroundImagePath(newImage);
        TicketStore.markChanged();
        if (newImage.isEmpty()) {
            sender.sendMessage(ChatColor.GREEN + "Ticket background image reset to the default image");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Ticket background image set to " + ChatColor.YELLOW + newImage);
        }
        for (TCTicketDisplay display : TCTicketDisplay.getAllDisplays(TCTicketDisplay.class)) {
            if (TicketStore.getTicketFromItem(display.getMapItem()) != ticket) continue;
            display.renderBackground();
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket maximumuses|maxuses|uselimit unlimited|infinite")
    @CommandDescription(value="Sets the number of uses for the currently edited ticket to unlimited")
    private void commandSetUnlimitedMaximumUses(Player sender, Ticket ticket) {
        this.commandSetMaximumUses(sender, ticket, -1);
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket maximumuses|maxuses|uselimit <newmaxuses>")
    @CommandDescription(value="Sets the number of uses for the currently edited ticket")
    private void commandSetMaximumUses(Player sender, Ticket ticket, @Argument(value="newmaxuses") int newMaximumUses) {
        ticket.setMaxNumberOfUses(newMaximumUses);
        TicketStore.markChanged();
        if (newMaximumUses >= 0) {
            sender.sendMessage(ChatColor.GREEN + "Ticket maximum number of uses set to " + ChatColor.YELLOW + newMaximumUses);
        } else {
            sender.sendMessage(ChatColor.GREEN + "Ticket now has unlimited number of uses");
        }
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket destination <newdestination>")
    @CommandDescription(value="Sets a destination to apply to the train when the currently edited ticket is used")
    private void commandSetDestination(Player sender, Ticket ticket, @Quoted @Argument(value="newdestination") String newDestination) {
        ticket.getProperties().set("destination", (Object)newDestination);
        sender.sendMessage(ChatColor.GREEN + "Ticket destination set to " + ChatColor.YELLOW + newDestination);
    }

    @CommandRequiresPermission(value=Permission.TICKET_MANAGE)
    @Command(value="train ticket tags [newtags]")
    @CommandDescription(value="Sets tags to apply to the train when the currently edited ticket is used")
    private void commandSetTags(Player sender, Ticket ticket, @Argument(value="newtags") String[] newTags) {
        if (newTags == null || newTags.length == 0) {
            ticket.getProperties().set("tags", (Object)new String[0]);
            sender.sendMessage(ChatColor.GREEN + "All ticket tags have been cleared");
        } else {
            ticket.getProperties().set("tags", (Object)newTags);
            sender.sendMessage(ChatColor.GREEN + "Ticket tags set: " + ChatColor.YELLOW + StringUtil.combineNames((String[])newTags));
        }
    }

    private static class TicketParser
    implements QuotedArgumentParser<CommandSender, Ticket>,
    SuggestionProvider<CommandSender> {
        private TicketParser() {
        }

        public ArgumentParseResult<Ticket> parseQuotedString(CommandContext<CommandSender> commandContext, String inputString) {
            Ticket ticket = TicketStore.getTicket(inputString);
            if (ticket == null) {
                return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_TICKET_NOTFOUND, new String[]{inputString}));
            }
            return ArgumentParseResult.success((Object)ticket);
        }

        public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<CommandSender> context, CommandInput input) {
            return CompletableFuture.completedFuture(TicketStore.getAll().stream().map(Ticket::getName).map(Suggestion::suggestion).collect(Collectors.toList()));
        }
    }
}

