/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audiences;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.EmptyAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.ForwardingAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.ForwardingAudienceOverrideNotRequired;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.SignedMessage;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.dialog.DialogLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identified;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.inventory.Book;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointered;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackInfo;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackInfoLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackRequest;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource.ResourcePackRequestLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.SoundStop;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.TitlePart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface Audience
extends Pointered {
    @NotNull
    public static Audience empty() {
        return EmptyAudience.INSTANCE;
    }

    @NotNull
    public static Audience audience(Audience ... audiences) {
        int length = audiences.length;
        if (length == 0) {
            return Audience.empty();
        }
        if (length == 1) {
            return audiences[0];
        }
        return Audience.audience(Arrays.asList(audiences));
    }

    @NotNull
    public static ForwardingAudience audience(@NotNull Iterable<? extends Audience> audiences) {
        return () -> audiences;
    }

    @NotNull
    public static Collector<? super Audience, ?, ForwardingAudience> toAudience() {
        return Audiences.COLLECTOR;
    }

    @NotNull
    default public Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
        return filter.test(this) ? this : Audience.empty();
    }

    default public void forEachAudience(@NotNull Consumer<? super Audience> action) {
        action.accept(this);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull ComponentLike message) {
        this.sendMessage(message.asComponent());
    }

    default public void sendMessage(@NotNull Component message) {
        this.sendMessage(message, MessageType.SYSTEM);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
        this.sendMessage(message.asComponent(), type);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Component message, @NotNull MessageType type) {
        this.sendMessage(Identity.nil(), message, type);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
        this.sendMessage(source, message.asComponent());
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
        this.sendMessage(source, message.asComponent());
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identified source, @NotNull Component message) {
        this.sendMessage(source, message, MessageType.CHAT);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull Identity source, @NotNull Component message) {
        this.sendMessage(source, message, MessageType.CHAT);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
        this.sendMessage(source, message.asComponent(), type);
    }

    @Deprecated
    @ForwardingAudienceOverrideNotRequired
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
        this.sendMessage(source, message.asComponent(), type);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
        this.sendMessage(source.identity(), message, type);
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    default public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
    }

    default public void sendMessage(@NotNull Component message,  @NotNull ChatType.Bound boundChatType) {
        this.sendMessage(message, MessageType.CHAT);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendMessage(@NotNull ComponentLike message,  @NotNull ChatType.Bound boundChatType) {
        this.sendMessage(message.asComponent(), boundChatType);
    }

    default public void sendMessage(@NotNull SignedMessage signedMessage,  @NotNull ChatType.Bound boundChatType) {
        Component content;
        Component component = content = signedMessage.unsignedContent() != null ? signedMessage.unsignedContent() : Component.text(signedMessage.message());
        if (signedMessage.isSystem()) {
            this.sendMessage(content);
        } else {
            this.sendMessage(signedMessage.identity(), content, MessageType.CHAT);
        }
    }

    @ForwardingAudienceOverrideNotRequired
    default public void deleteMessage(@NotNull SignedMessage signedMessage) {
        if (signedMessage.canDelete()) {
            this.deleteMessage(Objects.requireNonNull(signedMessage.signature()));
        }
    }

    default public void deleteMessage(@NotNull SignedMessage.Signature signature) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendActionBar(@NotNull ComponentLike message) {
        this.sendActionBar(message.asComponent());
    }

    default public void sendActionBar(@NotNull Component message) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendPlayerListHeader(@NotNull ComponentLike header) {
        this.sendPlayerListHeader(header.asComponent());
    }

    default public void sendPlayerListHeader(@NotNull Component header) {
        this.sendPlayerListHeaderAndFooter(header, Component.empty());
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendPlayerListFooter(@NotNull ComponentLike footer) {
        this.sendPlayerListFooter(footer.asComponent());
    }

    default public void sendPlayerListFooter(@NotNull Component footer) {
        this.sendPlayerListHeaderAndFooter(Component.empty(), footer);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
        this.sendPlayerListHeaderAndFooter(header.asComponent(), footer.asComponent());
    }

    default public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void showTitle(@NotNull Title title) {
        Title.Times times = title.times();
        if (times != null) {
            this.sendTitlePart(TitlePart.TIMES, times);
        }
        this.sendTitlePart(TitlePart.SUBTITLE, title.subtitle());
        this.sendTitlePart(TitlePart.TITLE, title.title());
    }

    default public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
    }

    default public void clearTitle() {
    }

    default public void resetTitle() {
    }

    default public void showBossBar(@NotNull BossBar bar) {
    }

    default public void hideBossBar(@NotNull BossBar bar) {
    }

    default public void playSound(@NotNull Sound sound) {
    }

    default public void playSound(@NotNull Sound sound, double x, double y, double z) {
    }

    default public void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void stopSound(@NotNull Sound sound) {
        this.stopSound(Objects.requireNonNull(sound, "sound").asStop());
    }

    default public void stopSound(@NotNull SoundStop stop) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void openBook(@NotNull Book.Builder book) {
        this.openBook(book.build());
    }

    default public void openBook(@NotNull Book book) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendResourcePacks(@NotNull ResourcePackInfoLike first, ResourcePackInfoLike ... others) {
        this.sendResourcePacks(ResourcePackRequest.addingRequest(first, others));
    }

    @ForwardingAudienceOverrideNotRequired
    default public void sendResourcePacks(@NotNull ResourcePackRequestLike request) {
        this.sendResourcePacks(request.asResourcePackRequest());
    }

    default public void sendResourcePacks(@NotNull ResourcePackRequest request) {
    }

    @ForwardingAudienceOverrideNotRequired
    default public void removeResourcePacks(@NotNull ResourcePackRequestLike request) {
        this.removeResourcePacks(request.asResourcePackRequest());
    }

    @ForwardingAudienceOverrideNotRequired
    default public void removeResourcePacks(@NotNull ResourcePackRequest request) {
        List<ResourcePackInfo> infos = request.packs();
        if (infos.size() == 1) {
            this.removeResourcePacks(infos.get(0).id(), new UUID[0]);
        } else if (infos.isEmpty()) {
            return;
        }
        UUID[] otherReqs = new UUID[infos.size() - 1];
        for (int i = 0; i < otherReqs.length; ++i) {
            otherReqs[i] = infos.get(i + 1).id();
        }
        this.removeResourcePacks(infos.get(0).id(), otherReqs);
    }

    @ForwardingAudienceOverrideNotRequired
    default public void removeResourcePacks(@NotNull ResourcePackInfoLike request, ResourcePackInfoLike ... others) {
        UUID[] otherReqs = new UUID[others.length];
        for (int i = 0; i < others.length; ++i) {
            otherReqs[i] = others[i].asResourcePackInfo().id();
        }
        this.removeResourcePacks(request.asResourcePackInfo().id(), otherReqs);
    }

    default public void removeResourcePacks(@NotNull Iterable<UUID> ids) {
        UUID[] others;
        Iterator<UUID> it = ids.iterator();
        if (!it.hasNext()) {
            return;
        }
        UUID id = it.next();
        if (!it.hasNext()) {
            others = new UUID[]{};
        } else if (ids instanceof Collection) {
            others = new UUID[((Collection)ids).size() - 1];
            for (int i = 0; i < others.length; ++i) {
                others[i] = it.next();
            }
        } else {
            ArrayList<UUID> othersList = new ArrayList<UUID>();
            while (it.hasNext()) {
                othersList.add(it.next());
            }
            others = othersList.toArray(new UUID[0]);
        }
        this.removeResourcePacks(id, others);
    }

    default public void removeResourcePacks(@NotNull UUID id, UUID ... others) {
    }

    default public void clearResourcePacks() {
    }

    default public void showDialog(@NotNull DialogLike dialog) {
    }

    default public void closeDialog() {
    }
}

