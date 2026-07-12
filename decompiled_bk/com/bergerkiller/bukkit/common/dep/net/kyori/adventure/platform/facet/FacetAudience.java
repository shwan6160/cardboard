/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.ApiStatus$OverrideOnly
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.inventory.Book;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetAudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetBossBarListener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.SoundStop;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.flattener.ComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart;
import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class FacetAudience<V>
implements Audience,
Closeable {
    @NotNull
    protected final FacetAudienceProvider<V, FacetAudience<V>> provider;
    @NotNull
    private final Set<V> viewers;
    @Nullable
    private V viewer;
    private volatile Pointers pointers;
    private final @Nullable Facet.Chat<V, Object> chat;
    private final @Nullable Facet.ActionBar<V, Object> actionBar;
    private final @Nullable Facet.Title<V, Object, Object, Object> title;
    private final @Nullable Facet.Sound<V, Object> sound;
    private final @Nullable Facet.EntitySound<V, Object> entitySound;
    private final @Nullable Facet.Book<V, Object, Object> book;
    private final @Nullable Facet.BossBar.Builder<V, Facet.BossBar<V>> bossBar;
    @Nullable
    private final Map<BossBar, Facet.BossBar<V>> bossBars;
    private final @Nullable Facet.TabList<V, Object> tabList;
    @NotNull
    private final Collection<? extends Facet.Pointers<V>> pointerProviders;

    public FacetAudience(@NotNull FacetAudienceProvider provider, @NotNull Collection<? extends V> viewers, @Nullable Collection<? extends Facet.Chat> chat, @Nullable Collection<? extends Facet.ActionBar> actionBar, @Nullable Collection<? extends Facet.Title> title, @Nullable Collection<? extends Facet.Sound> sound, @Nullable Collection<? extends Facet.EntitySound> entitySound, @Nullable Collection<? extends Facet.Book> book, @Nullable Collection<? extends Facet.BossBar.Builder> bossBar, @Nullable Collection<? extends Facet.TabList> tabList, @Nullable Collection<? extends Facet.Pointers> pointerProviders) {
        this.provider = Objects.requireNonNull(provider, "audience provider");
        this.viewers = new CopyOnWriteArraySet<V>();
        for (V viewer : Objects.requireNonNull(viewers, "viewers")) {
            this.addViewer(viewer);
        }
        this.refresh();
        this.chat = Facet.of(chat, this.viewer);
        this.actionBar = Facet.of(actionBar, this.viewer);
        this.title = Facet.of(title, this.viewer);
        this.sound = Facet.of(sound, this.viewer);
        this.entitySound = Facet.of(entitySound, this.viewer);
        this.book = Facet.of(book, this.viewer);
        this.bossBar = Facet.of(bossBar, this.viewer);
        this.bossBars = this.bossBar == null ? null : Collections.synchronizedMap(new IdentityHashMap(4));
        this.tabList = Facet.of(tabList, this.viewer);
        this.pointerProviders = pointerProviders == null ? Collections.emptyList() : pointerProviders;
    }

    public void addViewer(@NotNull V viewer) {
        if (this.viewers.add(viewer) && this.viewer == null) {
            this.viewer = viewer;
            this.refresh();
        }
    }

    public void removeViewer(@NotNull V viewer) {
        if (this.viewers.remove(viewer) && this.viewer == viewer) {
            this.viewer = this.viewers.isEmpty() ? null : this.viewers.iterator().next();
            this.refresh();
        }
        if (this.bossBars == null) {
            return;
        }
        for (Facet.BossBar<V> listener : this.bossBars.values()) {
            listener.removeViewer(viewer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refresh() {
        FacetAudience facetAudience = this;
        synchronized (facetAudience) {
            this.pointers = null;
        }
        if (this.bossBars == null) {
            return;
        }
        for (Map.Entry entry : this.bossBars.entrySet()) {
            BossBar bar = (BossBar)entry.getKey();
            Facet.BossBar listener = (Facet.BossBar)entry.getValue();
            listener.bossBarNameChanged(bar, bar.name(), bar.name());
        }
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component original, @NotNull MessageType type) {
        if (this.chat == null) {
            return;
        }
        Object message = this.createMessage(original, this.chat);
        if (message == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.chat.sendMessage(viewer, source, message, (Object)type);
        }
    }

    @Override
    public void sendMessage(@NotNull Component original,  @NotNull ChatType.Bound boundChatType) {
        if (this.chat == null) {
            return;
        }
        Object message = this.createMessage(original, this.chat);
        if (message == null) {
            return;
        }
        Component name = this.provider.componentRenderer.render(boundChatType.name(), this);
        Component target = null;
        if (boundChatType.target() != null) {
            target = this.provider.componentRenderer.render(boundChatType.target(), this);
        }
        ChatType.Bound renderedType = boundChatType.type().bind(name, target);
        for (V viewer : this.viewers) {
            this.chat.sendMessage(viewer, Identity.nil(), message, renderedType);
        }
    }

    @Override
    public void sendMessage(@NotNull SignedMessage signedMessage,  @NotNull ChatType.Bound boundChatType) {
        if (signedMessage.isSystem()) {
            Component content = signedMessage.unsignedContent() != null ? signedMessage.unsignedContent() : Component.text(signedMessage.message());
            this.sendMessage(content, boundChatType);
        } else {
            Audience.super.sendMessage(signedMessage, boundChatType);
        }
    }

    @Override
    public void sendActionBar(@NotNull Component original) {
        if (this.actionBar == null) {
            return;
        }
        Object message = this.createMessage(original, this.actionBar);
        if (message == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.actionBar.sendMessage(viewer, message);
        }
    }

    @Override
    public void playSound(@NotNull Sound original) {
        if (this.sound == null) {
            return;
        }
        for (V viewer : this.viewers) {
            Object position = this.sound.createPosition(viewer);
            if (position == null) continue;
            this.sound.playSound(viewer, original, position);
        }
    }

    @Override
    public void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
        if (this.entitySound == null) {
            return;
        }
        if (emitter == Sound.Emitter.self()) {
            for (V viewer : this.viewers) {
                Object message = this.entitySound.createForSelf(viewer, sound);
                if (message == null) continue;
                this.entitySound.playSound(viewer, message);
            }
        } else {
            Object message = this.entitySound.createForEmitter(sound, emitter);
            if (message == null) {
                return;
            }
            for (V viewer : this.viewers) {
                this.entitySound.playSound(viewer, message);
            }
        }
    }

    @Override
    public void playSound(@NotNull Sound original, double x, double y, double z) {
        if (this.sound == null) {
            return;
        }
        Object position = this.sound.createPosition(x, y, z);
        for (V viewer : this.viewers) {
            this.sound.playSound(viewer, original, position);
        }
    }

    @Override
    public void stopSound(@NotNull SoundStop original) {
        if (this.sound == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.sound.stopSound(viewer, original);
        }
    }

    @Override
    public void openBook(@NotNull Book original) {
        if (this.book == null) {
            return;
        }
        String title = this.toPlain(original.title());
        String author = this.toPlain(original.author());
        LinkedList<Object> pages = new LinkedList<Object>();
        for (Component originalPage : original.pages()) {
            Object page = this.createMessage(originalPage, this.book);
            if (page == null) continue;
            pages.add(page);
        }
        if (title == null || author == null || pages.isEmpty()) {
            return;
        }
        Object book = this.book.createBook(title, author, pages);
        if (book == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.book.openBook(viewer, book);
        }
    }

    private String toPlain(Component comp) {
        if (comp == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        ComponentFlattener.basic().flatten(this.provider.componentRenderer.render(comp, this), builder::append);
        return builder.toString();
    }

    @Override
    public void showTitle(@NotNull Title original) {
        if (this.title == null) {
            return;
        }
        Object mainTitle = this.createMessage(original.title(), this.title);
        Object subTitle = this.createMessage(original.subtitle(), this.title);
        @Nullable Title.Times times = original.times();
        int inTicks = times == null ? -1 : this.title.toTicks(times.fadeIn());
        int stayTicks = times == null ? -1 : this.title.toTicks(times.stay());
        int outTicks = times == null ? -1 : this.title.toTicks(times.fadeOut());
        Object collection = this.title.createTitleCollection();
        if (inTicks != -1 || stayTicks != -1 || outTicks != -1) {
            this.title.contributeTimes(collection, inTicks, stayTicks, outTicks);
        }
        this.title.contributeSubtitle(collection, subTitle);
        this.title.contributeTitle(collection, mainTitle);
        Object title = this.title.completeTitle(collection);
        if (title == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.title.showTitle(viewer, title);
        }
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        Object message;
        if (this.title == null) {
            return;
        }
        Objects.requireNonNull(value, "value");
        Object collection = this.title.createTitleCollection();
        if (part == TitlePart.TITLE) {
            message = this.createMessage((Component)value, this.title);
            if (message != null) {
                this.title.contributeTitle(collection, message);
            }
        } else if (part == TitlePart.SUBTITLE) {
            message = this.createMessage((Component)value, this.title);
            if (message != null) {
                this.title.contributeSubtitle(collection, message);
            }
        } else if (part == TitlePart.TIMES) {
            Title.Times times = (Title.Times)value;
            int inTicks = this.title.toTicks(times.fadeIn());
            int stayTicks = this.title.toTicks(times.stay());
            int outTicks = this.title.toTicks(times.fadeOut());
            if (inTicks != -1 || stayTicks != -1 || outTicks != -1) {
                this.title.contributeTimes(collection, inTicks, stayTicks, outTicks);
            }
        } else {
            throw new IllegalArgumentException("Unknown TitlePart '" + part + "'");
        }
        Object title = this.title.completeTitle(collection);
        if (title == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.title.showTitle(viewer, title);
        }
    }

    @Override
    public void clearTitle() {
        if (this.title == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.title.clearTitle(viewer);
        }
    }

    @Override
    public void resetTitle() {
        if (this.title == null) {
            return;
        }
        for (V viewer : this.viewers) {
            this.title.resetTitle(viewer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void showBossBar(@NotNull BossBar bar) {
        Facet.BossBar listener;
        if (this.bossBar == null || this.bossBars == null) {
            return;
        }
        Map<BossBar, Facet.BossBar<V>> map = this.bossBars;
        synchronized (map) {
            listener = this.bossBars.get(bar);
            if (listener == null) {
                listener = new FacetBossBarListener<V>(this.bossBar.createBossBar(this.viewers), message -> this.provider.componentRenderer.render((Component)message, this));
                this.bossBars.put(bar, listener);
            }
        }
        if (listener.isEmpty()) {
            listener.bossBarInitialized(bar);
            bar.addListener(listener);
        }
        for (Object viewer : this.viewers) {
            listener.addViewer(viewer);
        }
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        if (this.bossBars == null) {
            return;
        }
        Facet.BossBar<V> listener = this.bossBars.get(bar);
        if (listener == null) {
            return;
        }
        for (V viewer : this.viewers) {
            listener.removeViewer(viewer);
        }
        if (listener.isEmpty() && this.bossBars.remove(bar) != null) {
            bar.removeListener(listener);
            listener.close();
        }
    }

    @Override
    public void sendPlayerListHeader(@NotNull Component header) {
        if (this.tabList != null) {
            Object headerFormatted = this.createMessage(header, this.tabList);
            if (headerFormatted == null) {
                return;
            }
            for (V viewer : this.viewers) {
                this.tabList.send(viewer, headerFormatted, null);
            }
        }
    }

    @Override
    public void sendPlayerListFooter(@NotNull Component footer) {
        if (this.tabList != null) {
            Object footerFormatted = this.createMessage(footer, this.tabList);
            if (footerFormatted == null) {
                return;
            }
            for (V viewer : this.viewers) {
                this.tabList.send(viewer, null, footerFormatted);
            }
        }
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        if (this.tabList != null) {
            Object headerFormatted = this.createMessage(header, this.tabList);
            Object footerFormatted = this.createMessage(footer, this.tabList);
            if (headerFormatted == null || footerFormatted == null) {
                return;
            }
            for (V viewer : this.viewers) {
                this.tabList.send(viewer, headerFormatted, footerFormatted);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @NotNull
    public Pointers pointers() {
        if (this.pointers == null) {
            FacetAudience facetAudience = this;
            synchronized (facetAudience) {
                if (this.pointers == null) {
                    V viewer = this.viewer;
                    if (viewer == null) {
                        return Pointers.empty();
                    }
                    Pointers.Builder builder = Pointers.builder();
                    this.contributePointers(builder);
                    for (Facet.Pointers<V> provider : this.pointerProviders) {
                        if (!provider.isApplicable(viewer)) continue;
                        provider.contributePointers(viewer, builder);
                    }
                    this.pointers = (Pointers)builder.build();
                    return this.pointers;
                }
            }
        }
        return this.pointers;
    }

    @ApiStatus.OverrideOnly
    protected void contributePointers(Pointers.Builder builder) {
    }

    @Override
    public void close() {
        if (this.bossBars != null) {
            for (BossBar bar : new LinkedList<BossBar>(this.bossBars.keySet())) {
                this.hideBossBar(bar);
            }
            this.bossBars.clear();
        }
        for (Object viewer : this.viewers) {
            this.removeViewer(viewer);
        }
        this.viewers.clear();
    }

    @Nullable
    private Object createMessage(@NotNull Component original, @NotNull Facet.Message<V, Object> facet) {
        Component message = this.provider.componentRenderer.render(original, this);
        V viewer = this.viewer;
        return viewer == null ? null : facet.createMessage(viewer, message);
    }
}

