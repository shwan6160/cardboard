/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatMessageType
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.chat.TranslationRegistry
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.BookMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetBase;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.TranslationRegistry;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SpigotFacet<V extends CommandSender>
extends FacetBase<V> {
    private static final boolean SUPPORTED = Knob.isEnabled("spigot", true) && BungeeComponentSerializer.isNative();
    private static final Class<?> BUNGEE_CHAT_MESSAGE_TYPE = MinecraftReflection.findClass("net.md_5.bungee.api.ChatMessageType");
    static final Class<?> BUNGEE_COMPONENT_TYPE = MinecraftReflection.findClass("net.md_5.bungee.api.chat.BaseComponent");

    protected SpigotFacet(@Nullable Class<? extends V> viewerClass) {
        super(viewerClass);
    }

    @Override
    public boolean isSupported() {
        return super.isSupported() && SUPPORTED;
    }

    static /* synthetic */ Class access$000() {
        return BUNGEE_CHAT_MESSAGE_TYPE;
    }

    static class Translator
    extends FacetBase<Server>
    implements FacetComponentFlattener.Translator<Server> {
        private static final boolean SUPPORTED = MinecraftReflection.hasClass("net.md_5.bungee.chat.TranslationRegistry");

        Translator() {
            super(Server.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        @NotNull
        public String valueOrDefault(@NotNull Server game, @NotNull String key) {
            return TranslationRegistry.INSTANCE.translate(key);
        }
    }

    static final class Book
    extends Message<Player>
    implements Facet.Book<Player, BaseComponent[], ItemStack> {
        private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "openBook", ItemStack.class);

        protected Book() {
            super(Player.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        @NotNull
        public ItemStack createBook(@NotNull String title, @NotNull String author, @NotNull Iterable<BaseComponent[]> pages) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            ItemMeta meta = book.getItemMeta();
            if (meta instanceof BookMeta) {
                BookMeta spigot = (BookMeta)meta;
                for (BaseComponent[] page : pages) {
                    spigot.spigot().addPage((BaseComponent[][])new BaseComponent[][]{page});
                }
                spigot.setTitle(title);
                spigot.setAuthor(author);
                book.setItemMeta((ItemMeta)spigot);
            }
            return book;
        }

        @Override
        public void openBook(@NotNull Player viewer, @NotNull ItemStack book) {
            viewer.openBook(book);
        }
    }

    static final class ActionBar
    extends ChatWithType
    implements Facet.ActionBar<Player, BaseComponent[]> {
        ActionBar() {
        }

        @Override
        public void sendMessage(@NotNull Player viewer, BaseComponent @NotNull [] message) {
            viewer.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        }
    }

    static class ChatWithType
    extends Message<Player>
    implements Facet.Chat<Player, BaseComponent[]> {
        private static final Class<?> PLAYER_CLASS = MinecraftReflection.findClass("org.bukkit.entity.Player$Spigot");
        private static final boolean SUPPORTED = MinecraftReflection.hasMethod(PLAYER_CLASS, "sendMessage", SpigotFacet.access$000(), BUNGEE_COMPONENT_TYPE);

        protected ChatWithType() {
            super(Player.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Nullable
        private ChatMessageType createType(@NotNull MessageType type) {
            if (type == MessageType.CHAT) {
                return ChatMessageType.CHAT;
            }
            if (type == MessageType.SYSTEM) {
                return ChatMessageType.SYSTEM;
            }
            Knob.logUnsupported(this, (Object)type);
            return null;
        }

        @Override
        public void sendMessage(@NotNull Player viewer, @NotNull Identity source, BaseComponent @NotNull [] message, @NotNull Object type) {
            ChatMessageType chat;
            ChatMessageType chatMessageType = chat = type instanceof MessageType ? this.createType((MessageType)((Object)type)) : ChatMessageType.SYSTEM;
            if (chat != null) {
                viewer.spigot().sendMessage(chat, message);
            }
        }
    }

    static final class Chat
    extends Message<CommandSender>
    implements Facet.Chat<CommandSender, BaseComponent[]> {
        private static final boolean SUPPORTED = MinecraftReflection.hasClass("org.bukkit.command.CommandSender$Spigot");

        protected Chat() {
            super(CommandSender.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, BaseComponent @NotNull [] message, @NotNull Object type) {
            viewer.spigot().sendMessage(message);
        }
    }

    static class Message<V extends CommandSender>
    extends SpigotFacet<V>
    implements Facet.Message<V, BaseComponent[]> {
        private static final BungeeComponentSerializer SERIALIZER = BungeeComponentSerializer.of(BukkitComponentSerializer.gson(), BukkitComponentSerializer.legacy());

        protected Message(@Nullable Class<? extends V> viewerClass) {
            super(viewerClass);
        }

        @Override
        @NotNull
        public @NotNull BaseComponent @NotNull [] createMessage(@NotNull V viewer, @NotNull Component message) {
            return SERIALIZER.serialize(message);
        }
    }
}

