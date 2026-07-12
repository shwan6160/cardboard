/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.index.qual.NonNegative
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Default
 *  org.immutables.value.Value$Derived
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.help.CommandPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandler;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpQuery;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.CommandEntry;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.HelpQueryResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.IndexCommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.MultipleCommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.help.result.VerboseCommandResult;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.AudienceProvider;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.ComponentHelper;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.HelpColorsImpl;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.ImmutableMinecraftHelp;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.Pagination;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.RichDescription;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.bergerkiller.bukkit.common.dep.cloud.util.StringUtils;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.LinearComponents;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEventSource;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.NamedTextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@Value.Immutable
public abstract class MinecraftHelp<C> {
    public static final int DEFAULT_HEADER_FOOTER_LENGTH = 46;
    public static final int DEFAULT_MAX_RESULTS_PER_PAGE = 6;
    public static final HelpColors DEFAULT_HELP_COLORS = MinecraftHelp.helpColors(NamedTextColor.GOLD, NamedTextColor.GREEN, NamedTextColor.YELLOW, NamedTextColor.GRAY, NamedTextColor.DARK_GRAY);
    public static final String MESSAGE_HELP_TITLE = "help";
    public static final String MESSAGE_COMMAND = "command";
    public static final String MESSAGE_DESCRIPTION = "description";
    public static final String MESSAGE_NO_DESCRIPTION = "no_description";
    public static final String MESSAGE_ARGUMENTS = "arguments";
    public static final String MESSAGE_OPTIONAL = "optional";
    public static final String MESSAGE_SHOWING_RESULTS_FOR_QUERY = "showing_results_for_query";
    public static final String MESSAGE_NO_RESULTS_FOR_QUERY = "no_results_for_query";
    public static final String MESSAGE_AVAILABLE_COMMANDS = "available_commands";
    public static final String MESSAGE_CLICK_TO_SHOW_HELP = "click_to_show_help";
    public static final String MESSAGE_PAGE_OUT_OF_RANGE = "page_out_of_range";
    public static final String MESSAGE_CLICK_FOR_NEXT_PAGE = "click_for_next_page";
    public static final String MESSAGE_CLICK_FOR_PREVIOUS_PAGE = "click_for_previous_page";
    private static final Pattern STRING_PLACEHOLDER_PATTERN = Pattern.compile("<([a-z_]+)>");
    private static final Map<String, String> DEFAULT_MESSAGES = new HashMap<String, String>();

    public static <C> CaptionProvider<C> defaultCaptionsProvider() {
        return CaptionProvider.constantProvider().putAllCaptions(DEFAULT_MESSAGES.entrySet().stream().map(e -> Pair.of(Caption.of("help.minecraft." + (String)e.getKey()), (String)e.getValue())).collect(Collectors.toMap(Pair::first, Pair::second))).build();
    }

    @API(status=API.Status.STABLE, since="1.5.0")
    public static <C extends Audience> @NonNull MinecraftHelp<C> createNative(@NonNull String commandPrefix, @NonNull CommandManager<C> commandManager) {
        return ImmutableMinecraftHelp.builder().commandManager(commandManager).audienceProvider(AudienceProvider.nativeAudience()).commandPrefix(commandPrefix).build();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull MinecraftHelp<C> create(@NonNull String commandPrefix, @NonNull CommandManager<C> commandManager, @NonNull AudienceProvider<C> audienceProvider) {
        return ImmutableMinecraftHelp.builder().commandManager(commandManager).audienceProvider(audienceProvider).commandPrefix(commandPrefix).build();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ImmutableMinecraftHelp.CommandManagerBuildStage<C> builder() {
        return ImmutableMinecraftHelp.builder();
    }

    MinecraftHelp() {
    }

    public abstract @NonNull CommandManager<C> commandManager();

    public abstract @NonNull AudienceProvider<C> audienceProvider();

    @API(status=API.Status.STABLE, since="2.0.0")
    public abstract @NonNull String commandPrefix();

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Derived
    public @NonNull HelpHandler<C> helpHandler() {
        return this.commandManager().createHelpHandler(this.commandFilter());
    }

    public @NonNull Audience audience(@NonNull C sender) {
        return this.audienceProvider().apply((Object)sender);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Default
    public @NonNull CommandPredicate<C> commandFilter() {
        return CommandPredicate.acceptAll();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Default
    public @NonNull DescriptionDecorator<C> descriptionDecorator() {
        return DescriptionDecorator.text();
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public abstract @NonNull Map<@NonNull String, @NonNull String> messages();

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Default
    public @NonNull MessageProvider<C> messageProvider() {
        return (sender, key, args) -> {
            String message = this.messageOrDefault(key);
            if (args.isEmpty()) {
                return Component.text(message);
            }
            return Component.text(StringUtils.replaceAll(message, STRING_PLACEHOLDER_PATTERN, matchResult -> (String)args.get(matchResult.group(1))));
        };
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Default
    public @NonNull HelpColors colors() {
        return DEFAULT_HELP_COLORS;
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Default
    public @NonNegative int headerFooterLength() {
        return 46;
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    @Value.Default
    public @NonNegative int maxResultsPerPage() {
        return 6;
    }

    protected final @NonNull String messageOrDefault(@NonNull String key) {
        return this.messages().getOrDefault(key, DEFAULT_MESSAGES.get(key));
    }

    public void queryCommands(@NonNull String rawQuery, @NonNull C recipient) {
        String query;
        int page;
        String[] splitQuery = rawQuery.split(" ");
        try {
            String pageText = splitQuery[splitQuery.length - 1];
            page = Integer.parseInt(pageText);
            query = rawQuery.substring(0, Math.max(rawQuery.lastIndexOf(pageText) - 1, 0));
        }
        catch (NumberFormatException e) {
            page = 1;
            query = rawQuery;
        }
        Audience audience = this.audience(recipient);
        this.printTopic(recipient, query, page, this.helpHandler().query(HelpQuery.of(recipient, query)));
    }

    private void printTopic(@NonNull C sender, @NonNull String query, int page, @NonNull HelpQueryResult<C> helpTopic) {
        if (helpTopic instanceof IndexCommandResult) {
            this.printIndexHelpTopic(sender, query, page, (IndexCommandResult)helpTopic);
        } else if (helpTopic instanceof MultipleCommandResult) {
            this.printMultiHelpTopic(sender, query, page, (MultipleCommandResult)helpTopic);
        } else if (helpTopic instanceof VerboseCommandResult) {
            this.printVerboseHelpTopic(sender, query, (VerboseCommandResult)helpTopic);
        } else {
            throw new IllegalArgumentException("Unknown help topic type");
        }
    }

    private void printNoResults(@NonNull C sender, @NonNull String query) {
        Audience audience = this.audience(sender);
        audience.sendMessage(this.basicHeader(sender));
        audience.sendMessage(LinearComponents.linear(this.messageProvider().provide(sender, MESSAGE_NO_RESULTS_FOR_QUERY).color(this.colors().text()), Component.text(": \"", this.colors().text()), this.highlight(Component.text("/" + query, this.colors().highlight())), Component.text("\"", this.colors().text())));
        audience.sendMessage(this.footer(sender));
    }

    private void printIndexHelpTopic(@NonNull C sender, @NonNull String query, int page, @NonNull IndexCommandResult<C> helpTopic) {
        if (helpTopic.isEmpty()) {
            this.printNoResults(sender, query);
            return;
        }
        Audience audience = this.audience(sender);
        new Pagination<CommandEntry>((currentPage, maxPages) -> {
            ArrayList<Object> header = new ArrayList<Object>();
            header.add(this.paginatedHeader(sender, (int)currentPage, (int)maxPages));
            header.add(this.showingResults(sender, query));
            header.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(this.lastBranch())).append((Component)Component.space())).append(this.messageProvider().provide(sender, MESSAGE_AVAILABLE_COMMANDS).color(this.colors().text()))).append((Component)Component.text(":", this.colors().text()))).build());
            return header;
        }, (helpEntry, isLastOfPage) -> {
            CommandDescription commandDescription = helpEntry.command().commandDescription();
            Component description = commandDescription.description() instanceof RichDescription ? ((RichDescription)commandDescription.description()).contents() : (commandDescription.isEmpty() ? this.messageProvider().provide(sender, MESSAGE_CLICK_TO_SHOW_HELP) : this.descriptionDecorator().decorate(sender, commandDescription.description().textDescription()));
            boolean lastBranch = isLastOfPage != false || helpTopic.entries().indexOf(helpEntry) == helpTopic.entries().size() - 1;
            return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("   "))).append(lastBranch ? this.lastBranch() : this.branch())).append(this.highlight(Component.text(String.format(" /%s", helpEntry.syntax()), this.colors().highlight())).hoverEvent((HoverEventSource)description.color(this.colors().text())).clickEvent(ClickEvent.runCommand(this.commandPrefix() + " " + helpEntry.syntax())))).build();
        }, (currentPage, maxPages) -> this.paginatedFooter(sender, (int)currentPage, (int)maxPages, query), (attemptedPage, maxPages) -> this.pageOutOfRange(sender, (int)attemptedPage, (int)maxPages)).render(helpTopic.entries(), page, this.maxResultsPerPage()).forEach(audience::sendMessage);
    }

    private void printMultiHelpTopic(@NonNull C sender, @NonNull String query, int page, @NonNull MultipleCommandResult<C> helpTopic) {
        if (helpTopic.childSuggestions().isEmpty()) {
            this.printNoResults(sender, query);
            return;
        }
        Audience audience = this.audience(sender);
        int headerIndentation = helpTopic.longestPath().length();
        new Pagination<String>((currentPage, maxPages) -> {
            ArrayList<Component> header = new ArrayList<Component>();
            header.add(this.paginatedHeader(sender, (int)currentPage, (int)maxPages));
            header.add(this.showingResults(sender, query));
            header.add(this.lastBranch().append(this.highlight(Component.text(" /" + helpTopic.longestPath(), this.colors().highlight()))));
            return header;
        }, (suggestion, isLastOfPage) -> {
            boolean lastBranch = isLastOfPage != false || helpTopic.childSuggestions().indexOf(suggestion) == helpTopic.childSuggestions().size() - 1;
            return ComponentHelper.repeat(Component.space(), headerIndentation).append(lastBranch ? this.lastBranch() : this.branch()).append(this.highlight(Component.text(" /" + suggestion, this.colors().highlight())).hoverEvent((HoverEventSource)this.messageProvider().provide(sender, MESSAGE_CLICK_TO_SHOW_HELP).color(this.colors().text())).clickEvent(ClickEvent.runCommand(this.commandPrefix() + " " + suggestion)));
        }, (currentPage, maxPages) -> this.paginatedFooter(sender, (int)currentPage, (int)maxPages, query), (attemptedPage, maxPages) -> this.pageOutOfRange(sender, (int)attemptedPage, (int)maxPages)).render(helpTopic.childSuggestions(), page, this.maxResultsPerPage()).forEach(audience::sendMessage);
    }

    private void printVerboseHelpTopic(@NonNull C sender, @NonNull String query, @NonNull VerboseCommandResult<C> helpTopic) {
        Audience audience = this.audience(sender);
        audience.sendMessage(this.basicHeader(sender));
        audience.sendMessage(this.showingResults(sender, query));
        String command = this.commandManager().commandSyntaxFormatter().apply(sender, helpTopic.entry().command().components(), null);
        audience.sendMessage((ComponentLike)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(this.lastBranch())).append((Component)Component.space())).append(this.messageProvider().provide(sender, MESSAGE_COMMAND).color(this.colors().primary()))).append((Component)Component.text(": ", this.colors().primary()))).append(this.highlight(Component.text("/" + command, this.colors().highlight()))));
        Description commandDescription = helpTopic.entry().command().commandDescription().verboseDescription();
        Component topicDescription = commandDescription instanceof RichDescription ? ((RichDescription)commandDescription).contents() : (commandDescription.isEmpty() ? this.messageProvider().provide(sender, MESSAGE_NO_DESCRIPTION) : this.descriptionDecorator().decorate(sender, commandDescription.textDescription()));
        boolean hasArguments = helpTopic.entry().command().components().size() > 1;
        audience.sendMessage((ComponentLike)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("   "))).append(hasArguments ? this.branch() : this.lastBranch())).append((Component)Component.space())).append(this.messageProvider().provide(sender, MESSAGE_DESCRIPTION).color(this.colors().primary()))).append((Component)Component.text(": ", this.colors().primary()))).append(topicDescription.color(this.colors().text())));
        if (hasArguments) {
            audience.sendMessage((ComponentLike)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("   "))).append(this.lastBranch())).append((Component)Component.space())).append(this.messageProvider().provide(sender, MESSAGE_ARGUMENTS).color(this.colors().primary()))).append((Component)Component.text(":", this.colors().primary())));
            Iterator<CommandComponent<C>> iterator = helpTopic.entry().command().components().iterator();
            iterator.next();
            while (iterator.hasNext()) {
                Description description;
                CommandComponent<C> component = iterator.next();
                String syntax = this.commandManager().commandSyntaxFormatter().apply(sender, Collections.singletonList(component), null);
                TextComponent.Builder textComponent = (TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.text("       "))).append(iterator.hasNext() ? this.branch() : this.lastBranch())).append(this.highlight(Component.text(" " + syntax, this.colors().highlight())));
                if (component.optional()) {
                    textComponent.append((Component)Component.text(" (", this.colors().alternateHighlight()));
                    textComponent.append(this.messageProvider().provide(sender, MESSAGE_OPTIONAL).color(this.colors().alternateHighlight()));
                    textComponent.append((Component)Component.text(")", this.colors().alternateHighlight()));
                }
                if (!(description = component.description()).isEmpty()) {
                    textComponent.append((Component)Component.text(" - ", this.colors().accent()));
                    textComponent.append(this.formatDescription(sender, description).colorIfAbsent(this.colors().text()));
                }
                audience.sendMessage(textComponent);
            }
        }
        audience.sendMessage(this.footer(sender));
    }

    private Component formatDescription(C sender, Description description) {
        if (description instanceof RichDescription) {
            return ((RichDescription)description).contents();
        }
        return this.descriptionDecorator().decorate(sender, description.textDescription());
    }

    private @NonNull Component showingResults(@NonNull C sender, @NonNull String query) {
        return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(this.messageProvider().provide(sender, MESSAGE_SHOWING_RESULTS_FOR_QUERY).color(this.colors().text()))).append((Component)Component.text(": \"", this.colors().text()))).append(this.highlight(Component.text("/" + query, this.colors().highlight())))).append((Component)Component.text("\"", this.colors().text()))).build();
    }

    private @NonNull Component button(char icon, @NonNull String command, @NonNull Component hoverText) {
        return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.space())).append((Component)Component.text('[', this.colors().accent()))).append((Component)Component.text(icon, this.colors().alternateHighlight()))).append((Component)Component.text(']', this.colors().accent()))).append((Component)Component.space())).clickEvent(ClickEvent.runCommand(command))).hoverEvent((HoverEventSource)hoverText)).build();
    }

    private @NonNull Component footer(@NonNull C sender) {
        return this.paginatedFooter(sender, 1, 1, "");
    }

    private @NonNull Component paginatedFooter(@NonNull C sender, int currentPage, int maxPages, @NonNull String query) {
        boolean lastPage;
        boolean firstPage = currentPage == 1;
        boolean bl = lastPage = currentPage == maxPages;
        if (firstPage && lastPage) {
            return this.line(this.headerFooterLength());
        }
        Component nextPageButton = this.button('\u2192', this.pageCommand(query, currentPage + 1), this.messageProvider().provide(sender, MESSAGE_CLICK_FOR_NEXT_PAGE).color(this.colors().text()));
        if (firstPage) {
            return this.header(sender, nextPageButton);
        }
        Component previousPageButton = this.button('\u2190', this.pageCommand(query, currentPage - 1), this.messageProvider().provide(sender, MESSAGE_CLICK_FOR_PREVIOUS_PAGE).color(this.colors().text()));
        if (lastPage) {
            return this.header(sender, previousPageButton);
        }
        Object buttons = ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(previousPageButton)).append(this.line(3))).append(nextPageButton)).build();
        return this.header(sender, (Component)buttons);
    }

    private String pageCommand(String query, int page) {
        if (query.isEmpty()) {
            return String.format("%s %s", this.commandPrefix(), page);
        }
        return String.format("%s %s %s", this.commandPrefix(), query, page);
    }

    private @NonNull Component header(@NonNull C sender, @NonNull Component title) {
        int sideLength = (this.headerFooterLength() - ComponentHelper.length(title)) / 2;
        return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(this.line(sideLength))).append(title)).append(this.line(sideLength))).build();
    }

    private @NonNull Component basicHeader(@NonNull C sender) {
        return this.header(sender, LinearComponents.linear(Component.space(), this.messageProvider().provide(sender, MESSAGE_HELP_TITLE).color(this.colors().highlight()), Component.space()));
    }

    private @NonNull Component paginatedHeader(@NonNull C sender, int currentPage, int pages) {
        return this.header(sender, (Component)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append((Component)Component.space())).append(this.messageProvider().provide(sender, MESSAGE_HELP_TITLE).color(this.colors().highlight()))).append((Component)Component.space())).append((Component)Component.text("(", this.colors().alternateHighlight()))).append((Component)Component.text(currentPage, this.colors().text()))).append((Component)Component.text("/", this.colors().alternateHighlight()))).append((Component)Component.text(pages, this.colors().text()))).append((Component)Component.text(")", this.colors().alternateHighlight()))).append((Component)Component.space())).build());
    }

    private @NonNull Component line(int length) {
        return ComponentHelper.repeat(Component.text("-", this.colors().primary(), TextDecoration.STRIKETHROUGH), length);
    }

    private @NonNull Component branch() {
        return Component.text("\u251c\u2500", this.colors().accent());
    }

    private @NonNull Component lastBranch() {
        return Component.text("\u2514\u2500", this.colors().accent());
    }

    private @NonNull Component highlight(@NonNull Component component) {
        return ComponentHelper.highlight(component, this.colors().alternateHighlight());
    }

    private @NonNull Component pageOutOfRange(@NonNull C sender, int attemptedPage, int maxPages) {
        LinkedHashMap<String, String> args = new LinkedHashMap<String, String>();
        args.put("page", String.valueOf(attemptedPage));
        args.put("max_pages", String.valueOf(maxPages));
        return this.highlight(this.messageProvider().provide(sender, MESSAGE_PAGE_OUT_OF_RANGE, args).color(this.colors().text()));
    }

    public static <C> MessageProvider<C> captionMessageProvider(CaptionRegistry<C> registry, ComponentCaptionFormatter<C> formatter) {
        return (sender, key, args) -> {
            Caption caption = Caption.of("help.minecraft." + key);
            String resolved = registry.caption(caption, sender);
            return (Component)formatter.formatCaption(caption, sender, resolved, args.entrySet().stream().map(e -> CaptionVariable.of((String)e.getKey(), (String)e.getValue())).collect(Collectors.toList()));
        };
    }

    public static @NonNull HelpColors helpColors(@NonNull TextColor primary, @NonNull TextColor highlight, @NonNull TextColor alternateHighlight, @NonNull TextColor text, @NonNull TextColor accent) {
        return HelpColorsImpl.of(primary, highlight, alternateHighlight, text, accent);
    }

    static {
        DEFAULT_MESSAGES.put(MESSAGE_HELP_TITLE, "Help");
        DEFAULT_MESSAGES.put(MESSAGE_COMMAND, "Command");
        DEFAULT_MESSAGES.put(MESSAGE_DESCRIPTION, "Description");
        DEFAULT_MESSAGES.put(MESSAGE_NO_DESCRIPTION, "No description");
        DEFAULT_MESSAGES.put(MESSAGE_ARGUMENTS, "Arguments");
        DEFAULT_MESSAGES.put(MESSAGE_OPTIONAL, "Optional");
        DEFAULT_MESSAGES.put(MESSAGE_SHOWING_RESULTS_FOR_QUERY, "Showing search results for query");
        DEFAULT_MESSAGES.put(MESSAGE_NO_RESULTS_FOR_QUERY, "No results for query");
        DEFAULT_MESSAGES.put(MESSAGE_AVAILABLE_COMMANDS, "Available Commands");
        DEFAULT_MESSAGES.put(MESSAGE_CLICK_TO_SHOW_HELP, "Click to show help for this command");
        DEFAULT_MESSAGES.put(MESSAGE_PAGE_OUT_OF_RANGE, "Error: Page <page> is not in range. Must be in range [1, <max_pages>]");
        DEFAULT_MESSAGES.put(MESSAGE_CLICK_FOR_NEXT_PAGE, "Click for next page");
        DEFAULT_MESSAGES.put(MESSAGE_CLICK_FOR_PREVIOUS_PAGE, "Click for previous page");
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    @FunctionalInterface
    public static interface DescriptionDecorator<C> {
        public static <C> @NonNull DescriptionDecorator<C> wrap(@NonNull Function<String, Component> function) {
            return (sender, description) -> (Component)function.apply(description);
        }

        public static <C> @NonNull DescriptionDecorator<C> text() {
            return (sender, description) -> Component.text(description);
        }

        public @NonNull Component decorate(@NonNull C var1, @NonNull String var2);
    }

    @FunctionalInterface
    @API(status=API.Status.STABLE, since="2.0.0")
    public static interface MessageProvider<C> {
        public @NonNull Component provide(@NonNull C var1, @NonNull String var2, @NonNull Map<String, String> var3);

        default public @NonNull Component provide(@NonNull C sender, @NonNull String key) {
            return this.provide(sender, key, Collections.emptyMap());
        }
    }

    @Value.Immutable
    public static interface HelpColors {
        public @NonNull TextColor primary();

        public @NonNull TextColor highlight();

        public @NonNull TextColor alternateHighlight();

        public @NonNull TextColor text();

        public @NonNull TextColor accent();
    }
}

