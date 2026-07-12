/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import java.util.Optional;
import org.bukkit.plugin.java.JavaPlugin;

public final class AttachmentSelector<T> {
    private final SearchStrategy strategy;
    private final Optional<String> nameFilter;
    private final Class<T> typeFilter;
    private final boolean excludeSelf;
    private static final MapTexture SEARCH_STRATEGY_ICONS = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/search_strategies.png");

    public static AttachmentSelector<Attachment> none() {
        return SearchStrategy.NONE.selectAll();
    }

    public static <T> AttachmentSelector<T> none(Class<T> typeFilter) {
        return new AttachmentSelector<T>(SearchStrategy.NONE, Optional.empty(), typeFilter, false);
    }

    public static <T> AttachmentSelector<T> all(Class<T> typeFilter) {
        return new AttachmentSelector<T>(SearchStrategy.ROOT_CHILDREN, Optional.empty(), typeFilter, false);
    }

    public static AttachmentSelector<Attachment> all(SearchStrategy strategy) {
        return strategy.selectAll();
    }

    public static AttachmentSelector<Attachment> named(SearchStrategy strategy, String nameFilter) {
        return strategy.selectNamed(nameFilter);
    }

    private AttachmentSelector(SearchStrategy strategy, Optional<String> nameFilter, Class<T> typeFilter, boolean excludeSelf) {
        if (strategy == null) {
            throw new IllegalArgumentException("Search Strategy is null");
        }
        if (typeFilter == null) {
            throw new IllegalArgumentException("Type Filter is null");
        }
        this.strategy = strategy;
        this.nameFilter = nameFilter;
        this.typeFilter = typeFilter;
        this.excludeSelf = excludeSelf;
    }

    public SearchStrategy strategy() {
        return this.strategy;
    }

    public Optional<String> nameFilter() {
        return this.nameFilter;
    }

    public Class<T> typeFilter() {
        return this.typeFilter;
    }

    public boolean usesTypeFilter() {
        return this.typeFilter != Attachment.class;
    }

    public boolean isExcludingSelf() {
        return this.excludeSelf;
    }

    public AttachmentSelector<T> withSelectAll() {
        return new AttachmentSelector<T>(this.strategy, Optional.empty(), this.typeFilter, this.excludeSelf);
    }

    public AttachmentSelector<T> withName(String name) {
        if (name == null || name.isEmpty()) {
            return new AttachmentSelector<T>(SearchStrategy.NONE, Optional.empty(), this.typeFilter, this.excludeSelf);
        }
        return new AttachmentSelector<T>(this.strategy, Optional.of(name), this.typeFilter, this.excludeSelf);
    }

    public AttachmentSelector<T> withStrategy(SearchStrategy strategy) {
        return new AttachmentSelector<T>(strategy, this.nameFilter, this.typeFilter, this.excludeSelf);
    }

    public <A> AttachmentSelector<A> withType(Class<A> typeFilter) {
        return new AttachmentSelector<A>(this.strategy, this.nameFilter, typeFilter, this.excludeSelf);
    }

    public AttachmentSelector<T> excludingSelf() {
        return this.excludingSelf(true);
    }

    public AttachmentSelector<T> includingSelf() {
        return this.excludingSelf(false);
    }

    public AttachmentSelector<T> excludingSelf(boolean exclude) {
        return new AttachmentSelector<T>(this.strategy, this.nameFilter, this.typeFilter, exclude);
    }

    public int hashCode() {
        if (this.nameFilter.isPresent()) {
            return this.nameFilter.get().hashCode();
        }
        return this.typeFilter.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof AttachmentSelector) {
            AttachmentSelector other = (AttachmentSelector)o;
            return this.strategy == other.strategy && this.nameFilter.equals(other.nameFilter) && this.typeFilter.equals(other.typeFilter) && this.excludeSelf == other.excludeSelf;
        }
        return false;
    }

    public String toString() {
        return "AttachmentSelector{type=" + this.typeFilter.getSimpleName() + ", strategy=" + (Object)((Object)this.strategy) + ", name=" + this.nameFilter.orElse("<any>") + ", excludeSelf=" + this.excludeSelf + "}";
    }

    public boolean matches(Attachment attachment) {
        return this.matchesExceptName(attachment) && (!this.nameFilter.isPresent() || attachment.getNames().contains(this.nameFilter.get()));
    }

    public boolean matchesExceptName(Attachment attachment) {
        return this.typeFilter.isInstance(attachment);
    }

    public void writeToConfig(ConfigurationNode config, String key) {
        if (this.strategy == SearchStrategy.NONE) {
            config.remove(key);
        } else if (this.strategy == SearchStrategy.ROOT_CHILDREN && this.nameFilter.isPresent()) {
            config.set(key, (Object)this.nameFilter.get());
        } else {
            ConfigurationNode block = config.getNode(key);
            block.set("strategy", (Object)this.strategy);
            if (this.nameFilter.isPresent()) {
                block.set("name", (Object)this.nameFilter.get());
            } else {
                block.remove("name");
            }
        }
    }

    public static AttachmentSelector<Attachment> readFromConfig(ConfigurationNode config, String key) {
        ConfigurationNode block = config.getNodeIfExists(key);
        if (block != null) {
            SearchStrategy strategy = (SearchStrategy)((Object)block.getOrDefault("strategy", (Object)SearchStrategy.CHILDREN));
            String nameFilter = (String)block.getOrDefault("name", String.class, null);
            if (nameFilter != null) {
                return strategy.selectNamed(nameFilter);
            }
            return strategy.selectAll();
        }
        String nameFilter = (String)config.getOrDefault(key, String.class, null);
        if (nameFilter != null) {
            return SearchStrategy.ROOT_CHILDREN.selectNamed(nameFilter);
        }
        return AttachmentSelector.none();
    }

    public static enum SearchStrategy {
        NONE("Disabled"),
        ROOT_CHILDREN("All of cart"),
        CHILDREN("Children"),
        PARENTS("Parents");

        private final String caption;
        private final AttachmentSelector<Attachment> all;
        private final MapTexture iconDefault;
        private final MapTexture iconFocused;

        private SearchStrategy(String caption) {
            this.caption = caption;
            this.all = new AttachmentSelector(this, Optional.empty(), Attachment.class, false);
            this.iconDefault = SEARCH_STRATEGY_ICONS.getView(this.ordinal() * 11, 0, 11, 7).clone();
            this.iconFocused = SEARCH_STRATEGY_ICONS.getView(this.ordinal() * 11, 7, 11, 7).clone();
        }

        public String getCaption() {
            return this.caption;
        }

        public MapTexture getIcon(boolean focused) {
            return focused ? this.iconFocused : this.iconDefault;
        }

        public AttachmentSelector<Attachment> selectAll() {
            return this.all;
        }

        public AttachmentSelector<Attachment> selectNamed(String nameFilter) {
            if (nameFilter == null || nameFilter.isEmpty()) {
                return NONE.selectAll();
            }
            return new AttachmentSelector<Attachment>(this, Optional.of(nameFilter), Attachment.class, false);
        }
    }
}

