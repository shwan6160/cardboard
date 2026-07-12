/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandException
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.server.RemoteServerCommandEvent
 *  org.bukkit.event.server.ServerCommandEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.commands.selector;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorException;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorHandler;
import com.bergerkiller.bukkit.tc.utils.QuoteEscapedString;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SelectorHandlerRegistry
implements Listener,
LibraryComponent {
    private static final Pattern CONDITIONS_PATTERN = Pattern.compile("^\\[([\\w\\d\\s\\-\\+=,\\*\\.\\!\\\"\\'\\\\]+)\\](?:\\s|$)");
    private final Map<String, SelectorHandler> handlers = new HashMap<String, SelectorHandler>();
    private final JavaPlugin plugin;

    public SelectorHandlerRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }

    public void disable() {
    }

    public synchronized void registerMultiple(List<String> selectorNames, SelectorHandler handler) {
        selectorNames.forEach(selectorName -> this.register((String)selectorName, handler));
    }

    public synchronized void register(String selectorName, SelectorHandler handler) {
        this.handlers.put(selectorName.toLowerCase(Locale.ENGLISH), handler);
    }

    public synchronized SelectorHandler find(String selectorName) {
        return this.handlers.get(selectorName.toLowerCase(Locale.ENGLISH));
    }

    public synchronized List<String> expandCommands(CommandSender sender, String command) throws SelectorException {
        int commandLength = command.length();
        int maxSelectorValues = 0;
        ArrayList<StringBuilder> resultBuilders = null;
        int postLastSelectorStart = 0;
        int postSelectorCommandStart = 0;
        int lastChar = 32;
        Matcher conditionsMatcher = null;
        int searchIndex = 0;
        block0: while (searchIndex < commandLength) {
            Collection<String> values;
            int valuesCount;
            List<SelectorCondition> conditions;
            String conditionsString;
            int priorEnd;
            int nameEndIndex;
            int ch = command.charAt(searchIndex);
            if (ch != 64 || !Character.isWhitespace((char)lastChar) && lastChar != 33 && lastChar != 61 && lastChar != 34) {
                lastChar = ch;
                ++searchIndex;
                continue;
            }
            boolean hasConditions = false;
            for (nameEndIndex = searchIndex + 1; nameEndIndex < commandLength; ++nameEndIndex) {
                char ch2 = command.charAt(nameEndIndex);
                if (Character.isAlphabetic(ch2) || Character.isDigit(ch2)) continue;
                if (ch2 == '[') {
                    hasConditions = true;
                    break;
                }
                if (Character.isWhitespace(ch2)) break;
                searchIndex = nameEndIndex;
                continue block0;
            }
            int selectorStartIndex = searchIndex;
            searchIndex = nameEndIndex;
            lastChar = 93;
            int replaceStartIndex = selectorStartIndex;
            String selector = command.substring(selectorStartIndex + 1, nameEndIndex);
            SelectorHandler handler = this.handlers.get(selector.toLowerCase(Locale.ENGLISH));
            if (handler == null) continue;
            for (priorEnd = selectorStartIndex - 1; priorEnd > 0 && command.charAt(priorEnd) == ' '; --priorEnd) {
            }
            if (!handler.isCommandHandled(command.substring(0, priorEnd + 1))) continue;
            postSelectorCommandStart = selectorStartIndex + selector.length() + 1;
            if (hasConditions) {
                if (conditionsMatcher != null) {
                    conditionsMatcher.reset(command.subSequence(nameEndIndex, commandLength));
                } else {
                    conditionsMatcher = CONDITIONS_PATTERN.matcher(command.subSequence(nameEndIndex, commandLength));
                }
                if (!conditionsMatcher.lookingAt()) continue;
                conditionsString = conditionsMatcher.group(1);
                searchIndex += conditionsString.length() + 2;
                postSelectorCommandStart += conditionsString.length() + 2;
            } else {
                conditionsString = null;
            }
            if (conditionsString == null) {
                conditions = Collections.emptyList();
            } else {
                conditions = SelectorCondition.parseAll(conditionsString);
                if (conditions == null) {
                    if (this.plugin == null) continue;
                    Localization.COMMAND_INPUT_SELECTOR_INVALID.message(sender, new String[]{conditionsString});
                    continue;
                }
            }
            if (maxSelectorValues == 0) {
                if (sender == null || Permission.COMMAND_UNLIMITED_SELECTORS.has(sender)) {
                    maxSelectorValues = Integer.MAX_VALUE;
                } else if (Permission.COMMAND_USE_SELECTORS.has(sender)) {
                    maxSelectorValues = TCConfig.maxCommandSelectorValues;
                }
                if (maxSelectorValues <= 0) {
                    Localization.COMMAND_INPUT_SELECTOR_NOPERM.message(sender, new String[0]);
                    return Collections.emptyList();
                }
            }
            if ((valuesCount = (values = handler.handle(sender, selector, conditions)).size()) == 0) {
                return Collections.emptyList();
            }
            if (valuesCount * (resultBuilders == null ? 1 : resultBuilders.size()) > maxSelectorValues) {
                Localization.COMMAND_INPUT_SELECTOR_EXCEEDEDLIMIT.message(sender, new String[0]);
                return Collections.emptyList();
            }
            if (resultBuilders == null) {
                StringBuilder builder = new StringBuilder(command.length());
                builder.append(command, 0, replaceStartIndex);
                resultBuilders = new ArrayList<StringBuilder>(values.size());
                resultBuilders.add(builder);
            } else {
                String inbetween = command.substring(postLastSelectorStart, replaceStartIndex);
                for (StringBuilder builder : resultBuilders) {
                    builder.append(inbetween);
                }
            }
            if (valuesCount > 1) {
                int numResults = resultBuilders.size();
                for (int num = 1; num < valuesCount; ++num) {
                    for (int i = 0; i < numResults; ++i) {
                        resultBuilders.add(new StringBuilder((CharSequence)resultBuilders.get(i)));
                    }
                }
                Iterator builderIter = resultBuilders.iterator();
                for (String value : values) {
                    for (int i = 0; i < numResults; ++i) {
                        ((StringBuilder)builderIter.next()).append(QuoteEscapedString.quoteEscape(value).getEscaped());
                    }
                }
            } else {
                String value = values.iterator().next();
                for (StringBuilder builder : resultBuilders) {
                    builder.append(QuoteEscapedString.quoteEscape(value).getEscaped());
                }
            }
            postLastSelectorStart = postSelectorCommandStart;
        }
        if (resultBuilders != null) {
            ArrayList<String> results = new ArrayList<String>(resultBuilders.size());
            for (StringBuilder builder : resultBuilders) {
                builder.append(command, postSelectorCommandStart, commandLength);
                results.add(builder.toString());
            }
            return results;
        }
        return Collections.singletonList(command);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/") && event.getMessage().length() > 1) {
            String command = event.getMessage().substring(1);
            ServerCommandEvent wrapped = new ServerCommandEvent((CommandSender)event.getPlayer(), command);
            this.onServerCommand(wrapped);
            if (SelectorHandlerRegistry.isCancelled(wrapped) || wrapped.getCommand().isEmpty()) {
                event.setCancelled(true);
            } else if (!command.equals(wrapped.getCommand())) {
                event.setMessage("/" + wrapped.getCommand());
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onRemoteServerCommand(RemoteServerCommandEvent event) {
        this.onServerCommandBase((ServerCommandEvent)event);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onServerCommand(ServerCommandEvent event) {
        if (!(event instanceof RemoteServerCommandEvent)) {
            this.onServerCommandBase(event);
        }
    }

    private void onServerCommandBase(ServerCommandEvent event) {
        List<String> commands;
        CommandSender sender = event.getSender();
        String inputCommand = event.getCommand();
        try {
            commands = this.expandCommands(sender, inputCommand);
        }
        catch (SelectorException ex) {
            sender.sendMessage(ChatColor.RED + "[TrainCarts] " + ex.getMessage());
            SelectorHandlerRegistry.cancelCommand(event);
            return;
        }
        if (commands.size() == 1) {
            String replacement = commands.iterator().next();
            if (!replacement.equals(inputCommand)) {
                event.setCommand(replacement);
            }
        } else if (commands.isEmpty()) {
            SelectorHandlerRegistry.cancelCommand(event);
        } else {
            Iterator<String> iter = commands.iterator();
            event.setCommand(iter.next());
            while (iter.hasNext()) {
                try {
                    Bukkit.getServer().dispatchCommand(sender, iter.next());
                }
                catch (CommandException ex) {
                    sender.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
                    Logger.getLogger(ServerGamePacketListenerImplHandle.T.getType().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static boolean isCancelled(ServerCommandEvent event) {
        if (event instanceof Cancellable) {
            return event.isCancelled();
        }
        return event.getCommand().isEmpty();
    }

    private static void cancelCommand(ServerCommandEvent event) {
        if (event instanceof Cancellable) {
            event.setCancelled(true);
        } else {
            event.setCommand("");
        }
    }
}

