/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.index.qual.NonNegative
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.help.CommandPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.help.HelpHandler;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.AudienceProvider;
import com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.MinecraftHelp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="MinecraftHelp", generator="Immutables")
public final class ImmutableMinecraftHelp<C>
extends MinecraftHelp<C> {
    private final @NonNull CommandManager<C> commandManager;
    private final @NonNull AudienceProvider<C> audienceProvider;
    private final @NonNull String commandPrefix;
    private final transient @NonNull HelpHandler<C> helpHandler;
    private final @NonNull CommandPredicate<C> commandFilter;
    private final @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator;
    private final @NonNull Map<String, String> messages;
    private final @NonNull MinecraftHelp.MessageProvider<C> messageProvider;
    private final @NonNull MinecraftHelp.HelpColors colors;
    private final @NonNegative int headerFooterLength;
    private final @NonNegative int maxResultsPerPage;
    private static final byte STAGE_INITIALIZING = -1;
    private static final byte STAGE_UNINITIALIZED = 0;
    private static final byte STAGE_INITIALIZED = 1;
    private volatile transient InitShim initShim = new InitShim();

    private ImmutableMinecraftHelp(@NonNull CommandManager<C> commandManager, @NonNull AudienceProvider<C> audienceProvider, @NonNull String commandPrefix, @NonNull CommandPredicate<C> commandFilter, @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator, Map<String, ? extends String> messages, @NonNull MinecraftHelp.MessageProvider<C> messageProvider, @NonNull MinecraftHelp.HelpColors colors, @NonNegative int headerFooterLength, @NonNegative int maxResultsPerPage) {
        this.commandManager = Objects.requireNonNull(commandManager, "commandManager");
        this.audienceProvider = Objects.requireNonNull(audienceProvider, "audienceProvider");
        this.commandPrefix = Objects.requireNonNull(commandPrefix, "commandPrefix");
        this.initShim.commandFilter(Objects.requireNonNull(commandFilter, "commandFilter"));
        this.initShim.descriptionDecorator(Objects.requireNonNull(descriptionDecorator, "descriptionDecorator"));
        this.messages = ImmutableMinecraftHelp.createUnmodifiableMap(true, false, messages);
        this.initShim.messageProvider(Objects.requireNonNull(messageProvider, "messageProvider"));
        this.initShim.colors(Objects.requireNonNull(colors, "colors"));
        this.initShim.headerFooterLength(headerFooterLength);
        this.initShim.maxResultsPerPage(maxResultsPerPage);
        this.helpHandler = this.initShim.helpHandler();
        this.commandFilter = this.initShim.commandFilter();
        this.descriptionDecorator = this.initShim.descriptionDecorator();
        this.messageProvider = this.initShim.messageProvider();
        this.colors = this.initShim.colors();
        this.headerFooterLength = this.initShim.headerFooterLength();
        this.maxResultsPerPage = this.initShim.maxResultsPerPage();
        this.initShim = null;
    }

    private ImmutableMinecraftHelp(Builder<C> builder) {
        this.commandManager = ((Builder)builder).commandManager;
        this.audienceProvider = ((Builder)builder).audienceProvider;
        this.commandPrefix = ((Builder)builder).commandPrefix;
        this.messages = ImmutableMinecraftHelp.createUnmodifiableMap(false, false, ((Builder)builder).messages);
        if (((Builder)builder).commandFilterIsSet()) {
            this.initShim.commandFilter(((Builder)builder).commandFilter);
        }
        if (((Builder)builder).descriptionDecoratorIsSet()) {
            this.initShim.descriptionDecorator(((Builder)builder).descriptionDecorator);
        }
        if (((Builder)builder).messageProviderIsSet()) {
            this.initShim.messageProvider(((Builder)builder).messageProvider);
        }
        if (((Builder)builder).colorsIsSet()) {
            this.initShim.colors(((Builder)builder).colors);
        }
        if (((Builder)builder).headerFooterLengthIsSet()) {
            this.initShim.headerFooterLength(((Builder)builder).headerFooterLength);
        }
        if (((Builder)builder).maxResultsPerPageIsSet()) {
            this.initShim.maxResultsPerPage(((Builder)builder).maxResultsPerPage);
        }
        this.helpHandler = this.initShim.helpHandler();
        this.commandFilter = this.initShim.commandFilter();
        this.descriptionDecorator = this.initShim.descriptionDecorator();
        this.messageProvider = this.initShim.messageProvider();
        this.colors = this.initShim.colors();
        this.headerFooterLength = this.initShim.headerFooterLength();
        this.maxResultsPerPage = this.initShim.maxResultsPerPage();
        this.initShim = null;
    }

    private ImmutableMinecraftHelp(ImmutableMinecraftHelp<C> original, @NonNull CommandManager<C> commandManager, @NonNull AudienceProvider<C> audienceProvider, @NonNull String commandPrefix, @NonNull CommandPredicate<C> commandFilter, @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator, @NonNull Map<String, String> messages, @NonNull MinecraftHelp.MessageProvider<C> messageProvider, @NonNull MinecraftHelp.HelpColors colors, @NonNegative int headerFooterLength, @NonNegative int maxResultsPerPage) {
        this.commandManager = commandManager;
        this.audienceProvider = audienceProvider;
        this.commandPrefix = commandPrefix;
        this.initShim.commandFilter(commandFilter);
        this.initShim.descriptionDecorator(descriptionDecorator);
        this.messages = messages;
        this.initShim.messageProvider(messageProvider);
        this.initShim.colors(colors);
        this.initShim.headerFooterLength(headerFooterLength);
        this.initShim.maxResultsPerPage(maxResultsPerPage);
        this.helpHandler = this.initShim.helpHandler();
        this.commandFilter = this.initShim.commandFilter();
        this.descriptionDecorator = this.initShim.descriptionDecorator();
        this.messageProvider = this.initShim.messageProvider();
        this.colors = this.initShim.colors();
        this.headerFooterLength = this.initShim.headerFooterLength();
        this.maxResultsPerPage = this.initShim.maxResultsPerPage();
        this.initShim = null;
    }

    @Override
    public @NonNull CommandManager<C> commandManager() {
        return this.commandManager;
    }

    @Override
    public @NonNull AudienceProvider<C> audienceProvider() {
        return this.audienceProvider;
    }

    @Override
    public @NonNull String commandPrefix() {
        return this.commandPrefix;
    }

    @Override
    public @NonNull HelpHandler<C> helpHandler() {
        InitShim shim = this.initShim;
        return shim != null ? shim.helpHandler() : this.helpHandler;
    }

    @Override
    public @NonNull CommandPredicate<C> commandFilter() {
        InitShim shim = this.initShim;
        return shim != null ? shim.commandFilter() : this.commandFilter;
    }

    @Override
    public @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator() {
        InitShim shim = this.initShim;
        return shim != null ? shim.descriptionDecorator() : this.descriptionDecorator;
    }

    @Override
    public @NonNull Map<String, String> messages() {
        return this.messages;
    }

    @Override
    public @NonNull MinecraftHelp.MessageProvider<C> messageProvider() {
        InitShim shim = this.initShim;
        return shim != null ? shim.messageProvider() : this.messageProvider;
    }

    @Override
    public @NonNull MinecraftHelp.HelpColors colors() {
        InitShim shim = this.initShim;
        return shim != null ? shim.colors() : this.colors;
    }

    @Override
    public @NonNegative int headerFooterLength() {
        InitShim shim = this.initShim;
        return shim != null ? shim.headerFooterLength() : this.headerFooterLength;
    }

    @Override
    public @NonNegative int maxResultsPerPage() {
        InitShim shim = this.initShim;
        return shim != null ? shim.maxResultsPerPage() : this.maxResultsPerPage;
    }

    public final ImmutableMinecraftHelp<C> withCommandManager(@NonNull CommandManager<C> value) {
        if (this.commandManager == value) {
            return this;
        }
        @NonNull CommandManager<C> newValue = Objects.requireNonNull(value, "commandManager");
        return new ImmutableMinecraftHelp<C>(this, newValue, this.audienceProvider, this.commandPrefix, this.commandFilter, this.descriptionDecorator, this.messages, this.messageProvider, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withAudienceProvider(@NonNull AudienceProvider<C> value) {
        if (this.audienceProvider == value) {
            return this;
        }
        @NonNull AudienceProvider<C> newValue = Objects.requireNonNull(value, "audienceProvider");
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, newValue, this.commandPrefix, this.commandFilter, this.descriptionDecorator, this.messages, this.messageProvider, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withCommandPrefix(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "commandPrefix");
        if (this.commandPrefix.equals(newValue)) {
            return this;
        }
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, newValue, this.commandFilter, this.descriptionDecorator, this.messages, this.messageProvider, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withCommandFilter(@NonNull CommandPredicate<C> value) {
        if (this.commandFilter == value) {
            return this;
        }
        @NonNull CommandPredicate<C> newValue = Objects.requireNonNull(value, "commandFilter");
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, newValue, this.descriptionDecorator, this.messages, this.messageProvider, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withDescriptionDecorator(@NonNull MinecraftHelp.DescriptionDecorator<C> value) {
        if (this.descriptionDecorator == value) {
            return this;
        }
        @NonNull MinecraftHelp.DescriptionDecorator<C> newValue = Objects.requireNonNull(value, "descriptionDecorator");
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, this.commandFilter, newValue, this.messages, this.messageProvider, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withMessages(Map<String, ? extends String> entries) {
        if (this.messages == entries) {
            return this;
        }
        @NonNull Map<String, String> newValue = ImmutableMinecraftHelp.createUnmodifiableMap(true, false, entries);
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, this.commandFilter, this.descriptionDecorator, newValue, this.messageProvider, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withMessageProvider(@NonNull MinecraftHelp.MessageProvider<C> value) {
        if (this.messageProvider == value) {
            return this;
        }
        @NonNull MinecraftHelp.MessageProvider<C> newValue = Objects.requireNonNull(value, "messageProvider");
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, this.commandFilter, this.descriptionDecorator, this.messages, newValue, this.colors, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withColors(@NonNull MinecraftHelp.HelpColors value) {
        if (this.colors == value) {
            return this;
        }
        @NonNull MinecraftHelp.HelpColors newValue = Objects.requireNonNull(value, "colors");
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, this.commandFilter, this.descriptionDecorator, this.messages, this.messageProvider, newValue, this.headerFooterLength, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withHeaderFooterLength(@NonNegative int value) {
        if (this.headerFooterLength == value) {
            return this;
        }
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, this.commandFilter, this.descriptionDecorator, this.messages, this.messageProvider, this.colors, value, this.maxResultsPerPage);
    }

    public final ImmutableMinecraftHelp<C> withMaxResultsPerPage(@NonNegative int value) {
        if (this.maxResultsPerPage == value) {
            return this;
        }
        return new ImmutableMinecraftHelp<C>(this, this.commandManager, this.audienceProvider, this.commandPrefix, this.commandFilter, this.descriptionDecorator, this.messages, this.messageProvider, this.colors, this.headerFooterLength, value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableMinecraftHelp && this.equalsByValue((ImmutableMinecraftHelp)another);
    }

    private boolean equalsByValue(ImmutableMinecraftHelp<?> another) {
        return this.commandManager.equals(another.commandManager) && this.audienceProvider.equals(another.audienceProvider) && this.commandPrefix.equals(another.commandPrefix) && this.helpHandler.equals(another.helpHandler) && this.commandFilter.equals(another.commandFilter) && this.descriptionDecorator.equals(another.descriptionDecorator) && this.messages.equals(another.messages) && this.messageProvider.equals(another.messageProvider) && this.colors.equals(another.colors) && this.headerFooterLength == another.headerFooterLength && this.maxResultsPerPage == another.maxResultsPerPage;
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.commandManager.hashCode();
        h += (h << 5) + this.audienceProvider.hashCode();
        h += (h << 5) + this.commandPrefix.hashCode();
        h += (h << 5) + this.helpHandler.hashCode();
        h += (h << 5) + this.commandFilter.hashCode();
        h += (h << 5) + this.descriptionDecorator.hashCode();
        h += (h << 5) + this.messages.hashCode();
        h += (h << 5) + this.messageProvider.hashCode();
        h += (h << 5) + this.colors.hashCode();
        h += (h << 5) + this.headerFooterLength;
        h += (h << 5) + this.maxResultsPerPage;
        return h;
    }

    public String toString() {
        return "MinecraftHelp{commandManager=" + this.commandManager + ", audienceProvider=" + this.audienceProvider + ", commandPrefix=" + this.commandPrefix + ", helpHandler=" + this.helpHandler + ", commandFilter=" + this.commandFilter + ", descriptionDecorator=" + this.descriptionDecorator + ", messages=" + this.messages + ", messageProvider=" + this.messageProvider + ", colors=" + this.colors + ", headerFooterLength=" + this.headerFooterLength + ", maxResultsPerPage=" + this.maxResultsPerPage + "}";
    }

    public static <C> ImmutableMinecraftHelp<C> of(@NonNull CommandManager<C> commandManager, @NonNull AudienceProvider<C> audienceProvider, @NonNull String commandPrefix, @NonNull CommandPredicate<C> commandFilter, @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator, Map<String, ? extends String> messages, @NonNull MinecraftHelp.MessageProvider<C> messageProvider, @NonNull MinecraftHelp.HelpColors colors, @NonNegative int headerFooterLength, @NonNegative int maxResultsPerPage) {
        return new ImmutableMinecraftHelp<C>(commandManager, audienceProvider, commandPrefix, commandFilter, descriptionDecorator, messages, messageProvider, colors, headerFooterLength, maxResultsPerPage);
    }

    public static <C> ImmutableMinecraftHelp<C> copyOf(MinecraftHelp<C> instance) {
        if (instance instanceof ImmutableMinecraftHelp) {
            return (ImmutableMinecraftHelp)instance;
        }
        return ((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)((Builder)ImmutableMinecraftHelp.builder()).commandManager((CommandManager)instance.commandManager())).audienceProvider((AudienceProvider)instance.audienceProvider())).commandPrefix(instance.commandPrefix())).commandFilter((CommandPredicate)instance.commandFilter())).descriptionDecorator((MinecraftHelp.DescriptionDecorator)instance.descriptionDecorator())).messages(instance.messages())).messageProvider((MinecraftHelp.MessageProvider)instance.messageProvider())).colors(instance.colors())).headerFooterLength(instance.headerFooterLength())).maxResultsPerPage(instance.maxResultsPerPage())).build();
    }

    public static <C> CommandManagerBuildStage<C> builder() {
        return new Builder();
    }

    private static <K, V> Map<K, V> createUnmodifiableMap(boolean checkNulls, boolean skipNulls, Map<? extends K, ? extends V> map) {
        switch (map.size()) {
            case 0: {
                return Collections.emptyMap();
            }
            case 1: {
                Map.Entry<K, V> e = map.entrySet().iterator().next();
                K k = e.getKey();
                V v = e.getValue();
                if (checkNulls) {
                    Objects.requireNonNull(k, "key");
                    Objects.requireNonNull(v, v == null ? "value for key: " + k : null);
                }
                if (skipNulls && (k == null || v == null)) {
                    return Collections.emptyMap();
                }
                return Collections.singletonMap(k, v);
            }
        }
        LinkedHashMap<K, V> linkedMap = new LinkedHashMap<K, V>(map.size() * 4 / 3 + 1);
        if (skipNulls || checkNulls) {
            for (Map.Entry<K, V> e : map.entrySet()) {
                K k = e.getKey();
                V v = e.getValue();
                if (skipNulls) {
                    if (k == null || v == null) {
                        continue;
                    }
                } else if (checkNulls) {
                    Objects.requireNonNull(k, "key");
                    Objects.requireNonNull(v, v == null ? "value for key: " + k : null);
                }
                linkedMap.put(k, v);
            }
        } else {
            linkedMap.putAll(map);
        }
        return Collections.unmodifiableMap(linkedMap);
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    public static final class Builder<C>
    implements CommandManagerBuildStage<C>,
    AudienceProviderBuildStage<C>,
    CommandPrefixBuildStage<C>,
    BuildFinal<C> {
        private static final long INIT_BIT_COMMAND_MANAGER = 1L;
        private static final long INIT_BIT_AUDIENCE_PROVIDER = 2L;
        private static final long INIT_BIT_COMMAND_PREFIX = 4L;
        private static final long OPT_BIT_COMMAND_FILTER = 1L;
        private static final long OPT_BIT_DESCRIPTION_DECORATOR = 2L;
        private static final long OPT_BIT_MESSAGE_PROVIDER = 4L;
        private static final long OPT_BIT_COLORS = 8L;
        private static final long OPT_BIT_HEADER_FOOTER_LENGTH = 16L;
        private static final long OPT_BIT_MAX_RESULTS_PER_PAGE = 32L;
        private long initBits = 7L;
        private long optBits;
        private @NonNull CommandManager<C> commandManager;
        private @NonNull AudienceProvider<C> audienceProvider;
        private @NonNull String commandPrefix;
        private @NonNull CommandPredicate<C> commandFilter;
        private @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator;
        private final Map<String, String> messages = new LinkedHashMap<String, String>();
        private @NonNull MinecraftHelp.MessageProvider<C> messageProvider;
        private @NonNull MinecraftHelp.HelpColors colors;
        private @NonNegative int headerFooterLength;
        private @NonNegative int maxResultsPerPage;

        private Builder() {
        }

        @Override
        public final Builder<C> commandManager(@NonNull CommandManager<C> commandManager) {
            Builder.checkNotIsSet(this.commandManagerIsSet(), "commandManager");
            this.commandManager = Objects.requireNonNull(commandManager, "commandManager");
            this.initBits &= 0xFFFFFFFFFFFFFFFEL;
            return this;
        }

        @Override
        public final Builder<C> audienceProvider(@NonNull AudienceProvider<C> audienceProvider) {
            Builder.checkNotIsSet(this.audienceProviderIsSet(), "audienceProvider");
            this.audienceProvider = Objects.requireNonNull(audienceProvider, "audienceProvider");
            this.initBits &= 0xFFFFFFFFFFFFFFFDL;
            return this;
        }

        @Override
        public final Builder<C> commandPrefix(@NonNull String commandPrefix) {
            Builder.checkNotIsSet(this.commandPrefixIsSet(), "commandPrefix");
            this.commandPrefix = Objects.requireNonNull(commandPrefix, "commandPrefix");
            this.initBits &= 0xFFFFFFFFFFFFFFFBL;
            return this;
        }

        @Override
        public final Builder<C> commandFilter(@NonNull CommandPredicate<C> commandFilter) {
            Builder.checkNotIsSet(this.commandFilterIsSet(), "commandFilter");
            this.commandFilter = Objects.requireNonNull(commandFilter, "commandFilter");
            this.optBits |= 1L;
            return this;
        }

        @Override
        public final Builder<C> descriptionDecorator(@NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator) {
            Builder.checkNotIsSet(this.descriptionDecoratorIsSet(), "descriptionDecorator");
            this.descriptionDecorator = Objects.requireNonNull(descriptionDecorator, "descriptionDecorator");
            this.optBits |= 2L;
            return this;
        }

        @Override
        public final Builder<C> messages(String key, String value) {
            this.messages.put(Objects.requireNonNull(key, "messages key"), Objects.requireNonNull(value, value == null ? "messages value for key: " + key : null));
            return this;
        }

        @Override
        public final Builder<C> messages(Map.Entry<String, ? extends String> entry) {
            String v;
            String k = entry.getKey();
            this.messages.put(Objects.requireNonNull(k, "messages key"), Objects.requireNonNull(v, (v = entry.getValue()) == null ? "messages value for key: " + k : null));
            return this;
        }

        @Override
        public final Builder<C> messages(Map<String, ? extends String> entries) {
            for (Map.Entry<String, ? extends String> e : entries.entrySet()) {
                String v;
                String k = e.getKey();
                this.messages.put(Objects.requireNonNull(k, "messages key"), Objects.requireNonNull(v, (v = e.getValue()) == null ? "messages value for key: " + k : null));
            }
            return this;
        }

        @Override
        public final Builder<C> messageProvider(@NonNull MinecraftHelp.MessageProvider<C> messageProvider) {
            Builder.checkNotIsSet(this.messageProviderIsSet(), "messageProvider");
            this.messageProvider = Objects.requireNonNull(messageProvider, "messageProvider");
            this.optBits |= 4L;
            return this;
        }

        @Override
        public final Builder<C> colors(@NonNull MinecraftHelp.HelpColors colors) {
            Builder.checkNotIsSet(this.colorsIsSet(), "colors");
            this.colors = Objects.requireNonNull(colors, "colors");
            this.optBits |= 8L;
            return this;
        }

        @Override
        public final Builder<C> headerFooterLength(@NonNegative int headerFooterLength) {
            Builder.checkNotIsSet(this.headerFooterLengthIsSet(), "headerFooterLength");
            this.headerFooterLength = headerFooterLength;
            this.optBits |= 0x10L;
            return this;
        }

        @Override
        public final Builder<C> maxResultsPerPage(@NonNegative int maxResultsPerPage) {
            Builder.checkNotIsSet(this.maxResultsPerPageIsSet(), "maxResultsPerPage");
            this.maxResultsPerPage = maxResultsPerPage;
            this.optBits |= 0x20L;
            return this;
        }

        @Override
        public ImmutableMinecraftHelp<C> build() {
            this.checkRequiredAttributes();
            return new ImmutableMinecraftHelp(this);
        }

        private boolean commandFilterIsSet() {
            return (this.optBits & 1L) != 0L;
        }

        private boolean descriptionDecoratorIsSet() {
            return (this.optBits & 2L) != 0L;
        }

        private boolean messageProviderIsSet() {
            return (this.optBits & 4L) != 0L;
        }

        private boolean colorsIsSet() {
            return (this.optBits & 8L) != 0L;
        }

        private boolean headerFooterLengthIsSet() {
            return (this.optBits & 0x10L) != 0L;
        }

        private boolean maxResultsPerPageIsSet() {
            return (this.optBits & 0x20L) != 0L;
        }

        private boolean commandManagerIsSet() {
            return (this.initBits & 1L) == 0L;
        }

        private boolean audienceProviderIsSet() {
            return (this.initBits & 2L) == 0L;
        }

        private boolean commandPrefixIsSet() {
            return (this.initBits & 4L) == 0L;
        }

        private static void checkNotIsSet(boolean isSet, String name) {
            if (isSet) {
                throw new IllegalStateException("Builder of MinecraftHelp is strict, attribute is already set: ".concat(name));
            }
        }

        private void checkRequiredAttributes() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            }
        }

        private String formatRequiredAttributesMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if (!this.commandManagerIsSet()) {
                attributes.add("commandManager");
            }
            if (!this.audienceProviderIsSet()) {
                attributes.add("audienceProvider");
            }
            if (!this.commandPrefixIsSet()) {
                attributes.add("commandPrefix");
            }
            return "Cannot build MinecraftHelp, some of required attributes are not set " + attributes;
        }
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    private final class InitShim {
        private byte helpHandlerBuildStage = 0;
        private @NonNull HelpHandler<C> helpHandler;
        private byte commandFilterBuildStage = 0;
        private @NonNull CommandPredicate<C> commandFilter;
        private byte descriptionDecoratorBuildStage = 0;
        private @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator;
        private byte messageProviderBuildStage = 0;
        private @NonNull MinecraftHelp.MessageProvider<C> messageProvider;
        private byte colorsBuildStage = 0;
        private @NonNull MinecraftHelp.HelpColors colors;
        private byte headerFooterLengthBuildStage = 0;
        private @NonNegative int headerFooterLength;
        private byte maxResultsPerPageBuildStage = 0;
        private @NonNegative int maxResultsPerPage;

        private InitShim() {
        }

        @NonNull HelpHandler<C> helpHandler() {
            if (this.helpHandlerBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.helpHandlerBuildStage == 0) {
                this.helpHandlerBuildStage = (byte)-1;
                @NonNull HelpHandler computedValue = ImmutableMinecraftHelp.super.helpHandler();
                this.helpHandler = Objects.requireNonNull(computedValue, "helpHandler");
                this.helpHandlerBuildStage = 1;
            }
            return this.helpHandler;
        }

        @NonNull CommandPredicate<C> commandFilter() {
            if (this.commandFilterBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.commandFilterBuildStage == 0) {
                this.commandFilterBuildStage = (byte)-1;
                @NonNull CommandPredicate computedValue = ImmutableMinecraftHelp.super.commandFilter();
                this.commandFilter = Objects.requireNonNull(computedValue, "commandFilter");
                this.commandFilterBuildStage = 1;
            }
            return this.commandFilter;
        }

        void commandFilter(@NonNull CommandPredicate<C> commandFilter) {
            this.commandFilter = commandFilter;
            this.commandFilterBuildStage = 1;
        }

        @NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator() {
            if (this.descriptionDecoratorBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.descriptionDecoratorBuildStage == 0) {
                this.descriptionDecoratorBuildStage = (byte)-1;
                @NonNull MinecraftHelp.DescriptionDecorator computedValue = ImmutableMinecraftHelp.super.descriptionDecorator();
                this.descriptionDecorator = Objects.requireNonNull(computedValue, "descriptionDecorator");
                this.descriptionDecoratorBuildStage = 1;
            }
            return this.descriptionDecorator;
        }

        void descriptionDecorator(@NonNull MinecraftHelp.DescriptionDecorator<C> descriptionDecorator) {
            this.descriptionDecorator = descriptionDecorator;
            this.descriptionDecoratorBuildStage = 1;
        }

        @NonNull MinecraftHelp.MessageProvider<C> messageProvider() {
            if (this.messageProviderBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.messageProviderBuildStage == 0) {
                this.messageProviderBuildStage = (byte)-1;
                @NonNull MinecraftHelp.MessageProvider computedValue = ImmutableMinecraftHelp.super.messageProvider();
                this.messageProvider = Objects.requireNonNull(computedValue, "messageProvider");
                this.messageProviderBuildStage = 1;
            }
            return this.messageProvider;
        }

        void messageProvider(@NonNull MinecraftHelp.MessageProvider<C> messageProvider) {
            this.messageProvider = messageProvider;
            this.messageProviderBuildStage = 1;
        }

        @NonNull MinecraftHelp.HelpColors colors() {
            if (this.colorsBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.colorsBuildStage == 0) {
                this.colorsBuildStage = (byte)-1;
                @NonNull MinecraftHelp.HelpColors computedValue = ImmutableMinecraftHelp.super.colors();
                this.colors = Objects.requireNonNull(computedValue, "colors");
                this.colorsBuildStage = 1;
            }
            return this.colors;
        }

        void colors(@NonNull MinecraftHelp.HelpColors colors) {
            this.colors = colors;
            this.colorsBuildStage = 1;
        }

        @NonNegative int headerFooterLength() {
            if (this.headerFooterLengthBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.headerFooterLengthBuildStage == 0) {
                int computedValue;
                this.headerFooterLengthBuildStage = (byte)-1;
                this.headerFooterLength = computedValue = ImmutableMinecraftHelp.super.headerFooterLength();
                this.headerFooterLengthBuildStage = 1;
            }
            return this.headerFooterLength;
        }

        void headerFooterLength(@NonNegative int headerFooterLength) {
            this.headerFooterLength = headerFooterLength;
            this.headerFooterLengthBuildStage = 1;
        }

        @NonNegative int maxResultsPerPage() {
            if (this.maxResultsPerPageBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.maxResultsPerPageBuildStage == 0) {
                int computedValue;
                this.maxResultsPerPageBuildStage = (byte)-1;
                this.maxResultsPerPage = computedValue = ImmutableMinecraftHelp.super.maxResultsPerPage();
                this.maxResultsPerPageBuildStage = 1;
            }
            return this.maxResultsPerPage;
        }

        void maxResultsPerPage(@NonNegative int maxResultsPerPage) {
            this.maxResultsPerPage = maxResultsPerPage;
            this.maxResultsPerPageBuildStage = 1;
        }

        private String formatInitCycleMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if (this.helpHandlerBuildStage == -1) {
                attributes.add("helpHandler");
            }
            if (this.commandFilterBuildStage == -1) {
                attributes.add("commandFilter");
            }
            if (this.descriptionDecoratorBuildStage == -1) {
                attributes.add("descriptionDecorator");
            }
            if (this.messageProviderBuildStage == -1) {
                attributes.add("messageProvider");
            }
            if (this.colorsBuildStage == -1) {
                attributes.add("colors");
            }
            if (this.headerFooterLengthBuildStage == -1) {
                attributes.add("headerFooterLength");
            }
            if (this.maxResultsPerPageBuildStage == -1) {
                attributes.add("maxResultsPerPage");
            }
            return "Cannot build MinecraftHelp, attribute initializers form cycle " + attributes;
        }
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    public static interface CommandManagerBuildStage<C>
    extends BuildStart<C> {
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    public static interface BuildFinal<C> {
        public BuildFinal<C> commandFilter(@NonNull CommandPredicate<C> var1);

        public BuildFinal<C> descriptionDecorator(@NonNull MinecraftHelp.DescriptionDecorator<C> var1);

        public BuildFinal<C> messages(String var1, String var2);

        public BuildFinal<C> messages(Map.Entry<String, ? extends String> var1);

        public BuildFinal<C> messages(Map<String, ? extends String> var1);

        public BuildFinal<C> messageProvider(@NonNull MinecraftHelp.MessageProvider<C> var1);

        public BuildFinal<C> colors(@NonNull MinecraftHelp.HelpColors var1);

        public BuildFinal<C> headerFooterLength(@NonNegative int var1);

        public BuildFinal<C> maxResultsPerPage(@NonNegative int var1);

        public ImmutableMinecraftHelp<C> build();
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    public static interface CommandPrefixBuildStage<C> {
        public BuildFinal<C> commandPrefix(@NonNull String var1);
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    public static interface AudienceProviderBuildStage<C> {
        public CommandPrefixBuildStage<C> audienceProvider(@NonNull AudienceProvider<C> var1);
    }

    @Generated(from="MinecraftHelp", generator="Immutables")
    public static interface BuildStart<C> {
        public AudienceProviderBuildStage<C> commandManager(@NonNull CommandManager<C> var1);
    }
}

