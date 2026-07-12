/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Damageable
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Wither
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagIO;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTagTypes;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.StringBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitEmitter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.CraftBukkitAccess;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.PaperFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetBase;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.sound.Sound;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CraftBukkitFacet<V extends CommandSender>
extends FacetBase<V> {
    private static final Class<?> CLASS_NMS_ENTITY = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("Entity"), MinecraftReflection.findMcClassName("world.entity.Entity"));
    private static final Class<?> CLASS_CRAFT_ENTITY = MinecraftReflection.findCraftClass("entity.CraftEntity");
    private static final MethodHandle CRAFT_ENTITY_GET_HANDLE = MinecraftReflection.findMethod(CLASS_CRAFT_ENTITY, "getHandle", CLASS_NMS_ENTITY, new Class[0]);
    @Nullable
    static final Class<? extends Player> CLASS_CRAFT_PLAYER = MinecraftReflection.findCraftClass("entity.CraftPlayer", Player.class);
    @Nullable
    static final MethodHandle CRAFT_PLAYER_GET_HANDLE;
    @Nullable
    private static final MethodHandle ENTITY_PLAYER_GET_CONNECTION;
    @Nullable
    private static final MethodHandle PLAYER_CONNECTION_SEND_PACKET;
    private static final boolean SUPPORTED;
    @Nullable
    private static final Class<?> CLASS_CHAT_COMPONENT;
    @Nullable
    private static final Class<?> CLASS_MESSAGE_TYPE;
    @Nullable
    private static final Object MESSAGE_TYPE_CHAT;
    @Nullable
    private static final Object MESSAGE_TYPE_SYSTEM;
    @Nullable
    private static final Object MESSAGE_TYPE_ACTIONBAR;
    @Nullable
    private static final MethodHandle LEGACY_CHAT_PACKET_CONSTRUCTOR;
    @Nullable
    private static final MethodHandle CHAT_PACKET_CONSTRUCTOR;
    @Nullable
    private static final Class<?> CLASS_TITLE_PACKET;
    @Nullable
    private static final Class<?> CLASS_TITLE_ACTION;
    private static final MethodHandle CONSTRUCTOR_TITLE_MESSAGE;
    @Nullable
    private static final MethodHandle CONSTRUCTOR_TITLE_TIMES;
    @Nullable
    private static final Object TITLE_ACTION_TITLE;
    @Nullable
    private static final Object TITLE_ACTION_SUBTITLE;
    @Nullable
    private static final Object TITLE_ACTION_ACTIONBAR;
    @Nullable
    private static final Object TITLE_ACTION_CLEAR;
    @Nullable
    private static final Object TITLE_ACTION_RESET;

    protected CraftBukkitFacet(@Nullable Class<? extends V> viewerClass) {
        super(viewerClass);
    }

    @Override
    public boolean isSupported() {
        return super.isSupported() && SUPPORTED;
    }

    static {
        Class<?> craftPlayerClass = MinecraftReflection.findCraftClass("entity.CraftPlayer");
        Class<?> packetClass = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("Packet"), MinecraftReflection.findMcClassName("network.protocol.Packet"));
        MethodHandle craftPlayerGetHandle = null;
        MethodHandle entityPlayerGetConnection = null;
        MethodHandle playerConnectionSendPacket = null;
        if (craftPlayerClass != null && packetClass != null) {
            try {
                Method getHandleMethod = craftPlayerClass.getMethod("getHandle", new Class[0]);
                Class<?> entityPlayerClass = getHandleMethod.getReturnType();
                craftPlayerGetHandle = MinecraftReflection.lookup().unreflect(getHandleMethod);
                Field playerConnectionField = MinecraftReflection.findField(entityPlayerClass, "playerConnection", "connection");
                Class<?> playerConnectionClass = null;
                if (playerConnectionField != null) {
                    entityPlayerGetConnection = MinecraftReflection.lookup().unreflectGetter(playerConnectionField);
                    playerConnectionClass = playerConnectionField.getType();
                } else {
                    Class<?> serverGamePacketListenerImpl = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PlayerConnection"), MinecraftReflection.findMcClassName("server.network.PlayerConnection"), MinecraftReflection.findMcClassName("server.network.ServerGamePacketListenerImpl"));
                    for (Field field : entityPlayerClass.getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (!Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers) || serverGamePacketListenerImpl != null && !field.getType().equals(serverGamePacketListenerImpl)) continue;
                        entityPlayerGetConnection = MinecraftReflection.lookup().unreflectGetter(field);
                        playerConnectionClass = field.getType();
                    }
                }
                Class<?> serverCommonPacketListenerImpl = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("server.network.ServerCommonPacketListenerImpl"));
                if (serverCommonPacketListenerImpl != null) {
                    playerConnectionClass = serverCommonPacketListenerImpl;
                }
                playerConnectionSendPacket = MinecraftReflection.searchMethod(playerConnectionClass, (Integer)1, new String[]{"sendPacket", "send"}, Void.TYPE, packetClass);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to initialize CraftBukkit sendPacket", new Object[0]);
            }
        }
        CRAFT_PLAYER_GET_HANDLE = craftPlayerGetHandle;
        ENTITY_PLAYER_GET_CONNECTION = entityPlayerGetConnection;
        PLAYER_CONNECTION_SEND_PACKET = playerConnectionSendPacket;
        SUPPORTED = Knob.isEnabled("craftbukkit", true) && MinecraftComponentSerializer.isSupported() && CRAFT_PLAYER_GET_HANDLE != null && ENTITY_PLAYER_GET_CONNECTION != null && PLAYER_CONNECTION_SEND_PACKET != null;
        CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.Component"));
        CLASS_MESSAGE_TYPE = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("ChatMessageType"), MinecraftReflection.findMcClassName("network.chat.ChatMessageType"), MinecraftReflection.findMcClassName("network.chat.ChatType"));
        if (CLASS_MESSAGE_TYPE != null && !CLASS_MESSAGE_TYPE.isEnum()) {
            MESSAGE_TYPE_CHAT = 0;
            MESSAGE_TYPE_SYSTEM = 1;
            MESSAGE_TYPE_ACTIONBAR = 2;
        } else {
            MESSAGE_TYPE_CHAT = MinecraftReflection.findEnum(CLASS_MESSAGE_TYPE, "CHAT", 0);
            MESSAGE_TYPE_SYSTEM = MinecraftReflection.findEnum(CLASS_MESSAGE_TYPE, "SYSTEM", 1);
            MESSAGE_TYPE_ACTIONBAR = MinecraftReflection.findEnum(CLASS_MESSAGE_TYPE, "GAME_INFO", 2);
        }
        MethodHandle legacyChatPacketConstructor = null;
        MethodHandle chatPacketConstructor = null;
        try {
            if (CLASS_CHAT_COMPONENT != null) {
                Class<?> chatPacketClass = MinecraftReflection.needClass(MinecraftReflection.findNmsClassName("PacketPlayOutChat"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutChat"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundChatPacket"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundSystemChatPacket"));
                if (MESSAGE_TYPE_CHAT == Integer.valueOf(0)) {
                    chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, CLASS_CHAT_COMPONENT, Boolean.TYPE);
                }
                if (chatPacketConstructor == null) {
                    chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, CLASS_CHAT_COMPONENT, Integer.TYPE);
                }
                if (chatPacketConstructor == null) {
                    chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, CLASS_CHAT_COMPONENT);
                }
                if (chatPacketConstructor == null) {
                    if (CLASS_MESSAGE_TYPE != null) {
                        chatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, CLASS_CHAT_COMPONENT, CLASS_MESSAGE_TYPE, UUID.class);
                    }
                } else if (MESSAGE_TYPE_CHAT == Integer.valueOf(0)) {
                    if (chatPacketConstructor.type().parameterType(1).equals(Boolean.TYPE)) {
                        chatPacketConstructor = MethodHandles.insertArguments(chatPacketConstructor, 1, Boolean.FALSE);
                        chatPacketConstructor = MethodHandles.dropArguments(chatPacketConstructor, 1, new Class[]{Integer.class, UUID.class});
                    } else {
                        chatPacketConstructor = MethodHandles.dropArguments(chatPacketConstructor, 2, new Class[]{UUID.class});
                    }
                } else {
                    chatPacketConstructor = MethodHandles.dropArguments(chatPacketConstructor, 1, new Class[]{CLASS_MESSAGE_TYPE == null ? Object.class : CLASS_MESSAGE_TYPE, UUID.class});
                }
                if ((legacyChatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, CLASS_CHAT_COMPONENT, Byte.TYPE)) == null) {
                    legacyChatPacketConstructor = MinecraftReflection.findConstructor(chatPacketClass, CLASS_CHAT_COMPONENT, Integer.TYPE);
                }
            }
        }
        catch (Throwable error) {
            Knob.logError(error, "Failed to initialize ClientboundChatPacket constructor", new Object[0]);
        }
        CHAT_PACKET_CONSTRUCTOR = chatPacketConstructor;
        LEGACY_CHAT_PACKET_CONSTRUCTOR = legacyChatPacketConstructor;
        CLASS_TITLE_PACKET = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutTitle"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutTitle"));
        CLASS_TITLE_ACTION = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutTitle$EnumTitleAction"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutTitle$EnumTitleAction"));
        CONSTRUCTOR_TITLE_MESSAGE = MinecraftReflection.findConstructor(CLASS_TITLE_PACKET, CLASS_TITLE_ACTION, CLASS_CHAT_COMPONENT);
        CONSTRUCTOR_TITLE_TIMES = MinecraftReflection.findConstructor(CLASS_TITLE_PACKET, Integer.TYPE, Integer.TYPE, Integer.TYPE);
        TITLE_ACTION_TITLE = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "TITLE", 0);
        TITLE_ACTION_SUBTITLE = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "SUBTITLE", 1);
        TITLE_ACTION_ACTIONBAR = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "ACTIONBAR");
        TITLE_ACTION_CLEAR = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "CLEAR");
        TITLE_ACTION_RESET = MinecraftReflection.findEnum(CLASS_TITLE_ACTION, "RESET");
    }

    static final class Translator
    extends FacetBase<Server>
    implements FacetComponentFlattener.Translator<Server> {
        private static final Class<?> CLASS_LANGUAGE = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("LocaleLanguage"), MinecraftReflection.findMcClassName("locale.LocaleLanguage"), MinecraftReflection.findMcClassName("locale.Language"));
        private static final MethodHandle LANGUAGE_GET_INSTANCE;
        private static final MethodHandle LANGUAGE_GET_OR_DEFAULT;

        private static MethodHandle unreflectUnchecked(Method m) {
            try {
                m.setAccessible(true);
                return MinecraftReflection.lookup().unreflect(m);
            }
            catch (IllegalAccessException ex) {
                return null;
            }
        }

        Translator() {
            super(Server.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && LANGUAGE_GET_INSTANCE != null && LANGUAGE_GET_OR_DEFAULT != null;
        }

        @Override
        @NotNull
        public String valueOrDefault(@NotNull Server game, @NotNull String key) {
            try {
                return LANGUAGE_GET_OR_DEFAULT.invoke(LANGUAGE_GET_INSTANCE.invoke(), key);
            }
            catch (Throwable ex) {
                Knob.logError(ex, "Failed to transate key '%s'", key);
                return key;
            }
        }

        static {
            if (CLASS_LANGUAGE == null) {
                LANGUAGE_GET_INSTANCE = null;
                LANGUAGE_GET_OR_DEFAULT = null;
            } else {
                LANGUAGE_GET_INSTANCE = Arrays.stream(CLASS_LANGUAGE.getDeclaredMethods()).filter(m -> Modifier.isStatic(m.getModifiers()) && !Modifier.isPrivate(m.getModifiers()) && m.getReturnType().equals(CLASS_LANGUAGE) && m.getParameterCount() == 0).findFirst().map(Translator::unreflectUnchecked).orElse(null);
                LANGUAGE_GET_OR_DEFAULT = Arrays.stream(CLASS_LANGUAGE.getDeclaredMethods()).filter(m -> !Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 1 && m.getParameterTypes()[0] == String.class && m.getReturnType().equals(String.class)).findFirst().map(Translator::unreflectUnchecked).orElse(null);
            }
        }
    }

    static class TabList
    extends PacketFacet<Player>
    implements Facet.TabList<Player, Object> {
        private static final Class<?> CLIENTBOUND_TAB_LIST_PACKET = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutPlayerListHeaderFooter"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutPlayerListHeaderFooter"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundTabListPacket"));
        @Nullable
        private static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_CTOR_PRE_1_17 = MinecraftReflection.findConstructor(CLIENTBOUND_TAB_LIST_PACKET, new Class[0]);
        @Nullable
        protected static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_CTOR = MinecraftReflection.findConstructor(CLIENTBOUND_TAB_LIST_PACKET, CraftBukkitFacet.access$500(), CraftBukkitFacet.access$500());
        @Nullable
        private static final Field CRAFT_PLAYER_TAB_LIST_HEADER = MinecraftReflection.findField(CLASS_CRAFT_PLAYER, "playerListHeader");
        @Nullable
        private static final Field CRAFT_PLAYER_TAB_LIST_FOOTER = MinecraftReflection.findField(CLASS_CRAFT_PLAYER, "playerListFooter");
        @Nullable
        protected static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER = TabList.first(MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, PaperFacet.NATIVE_COMPONENT_CLASS, "adventure$header")), MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, CraftBukkitFacet.access$500(), "header", "a")));
        @Nullable
        protected static final MethodHandle CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER = TabList.first(MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, PaperFacet.NATIVE_COMPONENT_CLASS, "adventure$footer")), MinecraftReflection.findSetterOf(MinecraftReflection.findField(CLIENTBOUND_TAB_LIST_PACKET, CraftBukkitFacet.access$500(), "footer", "b")));

        TabList() {
        }

        private static MethodHandle first(MethodHandle ... handles) {
            for (int i = 0; i < handles.length; ++i) {
                MethodHandle handle = handles[i];
                if (handle == null) continue;
                return handle;
            }
            return null;
        }

        @Override
        public boolean isSupported() {
            return (CLIENTBOUND_TAB_LIST_PACKET_CTOR != null || CLIENTBOUND_TAB_LIST_PACKET_CTOR_PRE_1_17 != null && CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER != null && CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER != null) && super.isSupported();
        }

        protected Object create117Packet(Player viewer, @Nullable Object header, @Nullable Object footer) throws Throwable {
            return CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(header == null ? this.createMessage(viewer, (Component)Component.empty()) : header, footer == null ? this.createMessage(viewer, (Component)Component.empty()) : footer);
        }

        @Override
        public void send(Player viewer, @Nullable Object header, @Nullable Object footer) {
            try {
                Object packet;
                if (CRAFT_PLAYER_TAB_LIST_HEADER != null && CRAFT_PLAYER_TAB_LIST_FOOTER != null) {
                    if (header == null) {
                        header = CRAFT_PLAYER_TAB_LIST_HEADER.get(viewer);
                    } else {
                        CRAFT_PLAYER_TAB_LIST_HEADER.set(viewer, header);
                    }
                    if (footer == null) {
                        footer = CRAFT_PLAYER_TAB_LIST_FOOTER.get(viewer);
                    } else {
                        CRAFT_PLAYER_TAB_LIST_FOOTER.set(viewer, footer);
                    }
                }
                if (CLIENTBOUND_TAB_LIST_PACKET_CTOR != null) {
                    packet = this.create117Packet(viewer, header, footer);
                } else {
                    packet = CLIENTBOUND_TAB_LIST_PACKET_CTOR_PRE_1_17.invoke();
                    CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER.invoke(packet, header == null ? this.createMessage(viewer, (Component)Component.empty()) : header);
                    CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER.invoke(packet, footer == null ? this.createMessage(viewer, (Component)Component.empty()) : footer);
                }
                this.sendPacket(viewer, packet);
            }
            catch (Throwable thr) {
                Knob.logError(thr, "Failed to send tab list header and footer to %s", viewer);
            }
        }
    }

    static final class BossBarWither
    extends FakeEntity<Wither>
    implements Facet.BossBarEntity<Player, Location> {
        private volatile boolean initialized = false;

        private BossBarWither(@NotNull Collection<Player> viewers) {
            super(Wither.class, viewers.iterator().next().getWorld().getSpawnLocation());
            this.invisible(true);
            this.metadata(20, 890);
        }

        @Override
        public void bossBarInitialized(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar) {
            Facet.BossBarEntity.super.bossBarInitialized(bar);
            this.initialized = true;
        }

        @Override
        @NotNull
        public Location createPosition(@NotNull Player viewer) {
            Location position = super.createPosition(viewer);
            position.setPitch(position.getPitch() - 30.0f);
            position.setYaw(position.getYaw() + 0.0f);
            position.add(position.getDirection().multiply(40));
            return position;
        }

        @Override
        public boolean isEmpty() {
            return !this.initialized || this.viewers.isEmpty();
        }

        public static class Builder
        extends CraftBukkitFacet<Player>
        implements Facet.BossBar.Builder<Player, BossBarWither> {
            protected Builder() {
                super(Player.class);
            }

            @Override
            @NotNull
            public BossBarWither createBossBar(@NotNull Collection<Player> viewers) {
                return new BossBarWither(viewers);
            }
        }
    }

    static class FakeEntity<E extends Entity>
    extends PacketFacet<Player>
    implements Facet.FakeEntity<Player, Location>,
    Listener {
        private static final Class<? extends World> CLASS_CRAFT_WORLD = MinecraftReflection.findCraftClass("CraftWorld", World.class);
        private static final Class<?> CLASS_NMS_LIVING_ENTITY = MinecraftReflection.findNmsClass("EntityLiving");
        private static final Class<?> CLASS_DATA_WATCHER = MinecraftReflection.findNmsClass("DataWatcher");
        private static final MethodHandle CRAFT_WORLD_CREATE_ENTITY = MinecraftReflection.findMethod(CLASS_CRAFT_WORLD, "createEntity", CraftBukkitFacet.access$1100(), Location.class, Class.class);
        private static final MethodHandle NMS_ENTITY_GET_BUKKIT_ENTITY = MinecraftReflection.findMethod(CraftBukkitFacet.access$1100(), "getBukkitEntity", CraftBukkitFacet.access$900(), new Class[0]);
        private static final MethodHandle NMS_ENTITY_GET_DATA_WATCHER = MinecraftReflection.findMethod(CraftBukkitFacet.access$1100(), "getDataWatcher", CLASS_DATA_WATCHER, new Class[0]);
        private static final MethodHandle NMS_ENTITY_SET_LOCATION = MinecraftReflection.findMethod(CraftBukkitFacet.access$1100(), "setLocation", Void.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
        private static final MethodHandle NMS_ENTITY_SET_INVISIBLE = MinecraftReflection.findMethod(CraftBukkitFacet.access$1100(), "setInvisible", Void.TYPE, Boolean.TYPE);
        private static final MethodHandle DATA_WATCHER_WATCH = MinecraftReflection.findMethod(CLASS_DATA_WATCHER, "watch", Void.TYPE, Integer.TYPE, Object.class);
        private static final Class<?> CLASS_SPAWN_LIVING_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutSpawnEntityLiving");
        private static final MethodHandle NEW_SPAWN_LIVING_PACKET = MinecraftReflection.findConstructor(CLASS_SPAWN_LIVING_PACKET, CLASS_NMS_LIVING_ENTITY);
        private static final Class<?> CLASS_ENTITY_DESTROY_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutEntityDestroy");
        private static final MethodHandle NEW_ENTITY_DESTROY_PACKET = MinecraftReflection.findConstructor(CLASS_ENTITY_DESTROY_PACKET, int[].class);
        private static final Class<?> CLASS_ENTITY_METADATA_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutEntityMetadata");
        private static final MethodHandle NEW_ENTITY_METADATA_PACKET = MinecraftReflection.findConstructor(CLASS_ENTITY_METADATA_PACKET, Integer.TYPE, CLASS_DATA_WATCHER, Boolean.TYPE);
        private static final Class<?> CLASS_ENTITY_TELEPORT_PACKET = MinecraftReflection.findNmsClass("PacketPlayOutEntityTeleport");
        private static final MethodHandle NEW_ENTITY_TELEPORT_PACKET = MinecraftReflection.findConstructor(CLASS_ENTITY_TELEPORT_PACKET, CraftBukkitFacet.access$1100());
        private static final Class<?> CLASS_ENTITY_WITHER = MinecraftReflection.findNmsClass("EntityWither");
        private static final Class<?> CLASS_WORLD = MinecraftReflection.findNmsClass("World");
        private static final Class<?> CLASS_WORLD_SERVER = MinecraftReflection.findNmsClass("WorldServer");
        private static final MethodHandle CRAFT_WORLD_GET_HANDLE = MinecraftReflection.findMethod(CLASS_CRAFT_WORLD, "getHandle", CLASS_WORLD_SERVER, new Class[0]);
        private static final MethodHandle NEW_ENTITY_WITHER = MinecraftReflection.findConstructor(CLASS_ENTITY_WITHER, CLASS_WORLD);
        private static final boolean SUPPORTED = (CRAFT_WORLD_CREATE_ENTITY != null || NEW_ENTITY_WITHER != null && CRAFT_WORLD_GET_HANDLE != null) && CraftBukkitFacet.access$1000() != null && NMS_ENTITY_GET_BUKKIT_ENTITY != null && NMS_ENTITY_GET_DATA_WATCHER != null;
        private final E entity;
        private final Object entityHandle;
        protected final Set<Player> viewers;

        protected FakeEntity(@NotNull Class<E> entityClass, @NotNull Location location) {
            this(BukkitAudience.PLUGIN.get(), entityClass, location);
        }

        protected FakeEntity(@NotNull Plugin plugin, @NotNull Class<E> entityClass, @NotNull Location location) {
            Entity entity = null;
            Object handle = null;
            if (SUPPORTED) {
                try {
                    if (CRAFT_WORLD_CREATE_ENTITY != null) {
                        Object nmsEntity = CRAFT_WORLD_CREATE_ENTITY.invoke(location.getWorld(), location, entityClass);
                        entity = NMS_ENTITY_GET_BUKKIT_ENTITY.invoke(nmsEntity);
                    } else if (Wither.class.isAssignableFrom(entityClass) && NEW_ENTITY_WITHER != null) {
                        Object nmsEntity = NEW_ENTITY_WITHER.invoke(CRAFT_WORLD_GET_HANDLE.invoke(location.getWorld()));
                        entity = NMS_ENTITY_GET_BUKKIT_ENTITY.invoke(nmsEntity);
                    }
                    if (CLASS_CRAFT_ENTITY.isInstance(entity)) {
                        handle = CRAFT_ENTITY_GET_HANDLE.invoke(entity);
                    }
                }
                catch (Throwable error) {
                    Knob.logError(error, "Failed to create fake entity: %s", entityClass.getSimpleName());
                }
            }
            this.entity = entity;
            this.entityHandle = handle;
            this.viewers = new HashSet<Player>();
            if (this.isSupported()) {
                plugin.getServer().getPluginManager().registerEvents((Listener)this, plugin);
            }
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && this.entity != null && this.entityHandle != null;
        }

        @EventHandler(ignoreCancelled=false, priority=EventPriority.MONITOR)
        public void onPlayerMove(PlayerMoveEvent event) {
            Player viewer = event.getPlayer();
            if (this.viewers.contains(viewer)) {
                this.teleport(viewer, this.createPosition(viewer));
            }
        }

        @Nullable
        public Object createSpawnPacket() {
            if (this.entity instanceof LivingEntity) {
                try {
                    return NEW_SPAWN_LIVING_PACKET.invoke(this.entityHandle);
                }
                catch (Throwable error) {
                    Knob.logError(error, "Failed to create spawn packet: %s", this.entity);
                }
            }
            return null;
        }

        @Nullable
        public Object createDespawnPacket() {
            try {
                return NEW_ENTITY_DESTROY_PACKET.invoke(this.entity.getEntityId());
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to create despawn packet: %s", this.entity);
                return null;
            }
        }

        @Nullable
        public Object createMetadataPacket() {
            try {
                Object dataWatcher = NMS_ENTITY_GET_DATA_WATCHER.invoke(this.entityHandle);
                return NEW_ENTITY_METADATA_PACKET.invoke(this.entity.getEntityId(), dataWatcher, false);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to create update metadata packet: %s", this.entity);
                return null;
            }
        }

        @Nullable
        public Object createLocationPacket() {
            try {
                return NEW_ENTITY_TELEPORT_PACKET.invoke(this.entityHandle);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to create teleport packet: %s", this.entity);
                return null;
            }
        }

        public void broadcastPacket(@Nullable Object packet) {
            for (Player viewer : this.viewers) {
                this.sendPacket(viewer, packet);
            }
        }

        @Override
        @NotNull
        public Location createPosition(@NotNull Player viewer) {
            return viewer.getLocation();
        }

        @Override
        @NotNull
        public Location createPosition(double x, double y, double z) {
            return new Location(null, x, y, z);
        }

        @Override
        public void teleport(@NotNull Player viewer, @Nullable Location position) {
            if (position == null) {
                this.viewers.remove(viewer);
                this.sendPacket(viewer, this.createDespawnPacket());
                return;
            }
            if (!this.viewers.contains(viewer)) {
                this.sendPacket(viewer, this.createSpawnPacket());
                this.viewers.add(viewer);
            }
            try {
                NMS_ENTITY_SET_LOCATION.invoke(this.entityHandle, position.getX(), position.getY(), position.getZ(), position.getPitch(), position.getYaw());
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to set entity location: %s %s", this.entity, position);
            }
            this.sendPacket(viewer, this.createLocationPacket());
        }

        @Override
        public void metadata(int position, @NotNull Object data) {
            if (DATA_WATCHER_WATCH != null) {
                try {
                    Object dataWatcher = NMS_ENTITY_GET_DATA_WATCHER.invoke(this.entityHandle);
                    DATA_WATCHER_WATCH.invoke(dataWatcher, position, data);
                }
                catch (Throwable error) {
                    Knob.logError(error, "Failed to set entity metadata: %s %s=%s", this.entity, position, data);
                }
                this.broadcastPacket(this.createMetadataPacket());
            }
        }

        @Override
        public void invisible(boolean invisible) {
            if (NMS_ENTITY_SET_INVISIBLE != null) {
                try {
                    NMS_ENTITY_SET_INVISIBLE.invoke(this.entityHandle, invisible);
                }
                catch (Throwable error) {
                    Knob.logError(error, "Failed to change entity visibility: %s", this.entity);
                }
            }
        }

        @Override
        @Deprecated
        public void health(float health) {
            if (this.entity instanceof Damageable) {
                Damageable entity = (Damageable)this.entity;
                entity.setHealth((double)health * (entity.getMaxHealth() - (double)0.1f) + (double)0.1f);
                this.broadcastPacket(this.createMetadataPacket());
            }
        }

        @Override
        public void name(@NotNull Component name) {
            this.entity.setCustomName(BukkitComponentSerializer.legacy().serialize(name));
            this.broadcastPacket(this.createMetadataPacket());
        }

        @Override
        public void close() {
            HandlerList.unregisterAll((Listener)this);
            for (Player viewer : new LinkedList<Player>(this.viewers)) {
                this.teleport(viewer, null);
            }
        }
    }

    static final class BossBar
    extends BukkitFacet.BossBar {
        private static final Class<?> CLASS_CRAFT_BOSS_BAR = MinecraftReflection.findCraftClass("boss.CraftBossBar");
        private static final Class<?> CLASS_BOSS_BAR_ACTION;
        private static final Object BOSS_BAR_ACTION_TITLE;
        private static final MethodHandle CRAFT_BOSS_BAR_HANDLE;
        private static final MethodHandle NMS_BOSS_BATTLE_SET_NAME;
        private static final MethodHandle NMS_BOSS_BATTLE_SEND_UPDATE;

        private BossBar(@NotNull Collection<Player> viewers) {
            super(viewers);
        }

        @Override
        public void bossBarNameChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            try {
                Object handle = CRAFT_BOSS_BAR_HANDLE.invoke(this.bar);
                Object text = MinecraftComponentSerializer.get().serialize(newName);
                NMS_BOSS_BATTLE_SET_NAME.invoke(handle, text);
                NMS_BOSS_BATTLE_SEND_UPDATE.invoke(handle, BOSS_BAR_ACTION_TITLE);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to set CraftBossBar name: %s %s", this.bar, newName);
                super.bossBarNameChanged(bar, oldName, newName);
            }
        }

        static {
            Class<Object> classBossBarAction = null;
            Object bossBarActionTitle = null;
            classBossBarAction = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutBoss$Action"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutBoss$Action"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundBossEventPacket$Operation"));
            if (classBossBarAction == null || !classBossBarAction.isEnum()) {
                classBossBarAction = null;
                Class<?> packetClass = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutBoss"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutBoss"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundBossEventPacket"));
                Class<?> bossEventClass = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("BossBattle"), MinecraftReflection.findMcClassName("world.BossBattle"), MinecraftReflection.findMcClassName("world.BossEvent"));
                if (packetClass != null && bossEventClass != null) {
                    try {
                        String methodName;
                        MethodType methodType = MethodType.methodType(packetClass, bossEventClass);
                        try {
                            packetClass.getDeclaredMethod("createUpdateNamePacket", bossEventClass);
                            methodName = "createUpdateNamePacket";
                        }
                        catch (NoSuchMethodException ignored) {
                            methodName = "c";
                        }
                        MethodHandle factoryMethod = MinecraftReflection.lookup().findStatic(packetClass, methodName, methodType);
                        bossBarActionTitle = LambdaMetafactory.metafactory(MinecraftReflection.lookup(), "apply", MethodType.methodType(Function.class), methodType.generic(), factoryMethod, methodType).getTarget().invoke();
                        classBossBarAction = Function.class;
                    }
                    catch (Throwable error) {
                        Knob.logError(error, "Failed to initialize CraftBossBar constructor", new Object[0]);
                    }
                }
            } else {
                bossBarActionTitle = MinecraftReflection.findEnum(classBossBarAction, "UPDATE_NAME", 3);
            }
            CLASS_BOSS_BAR_ACTION = classBossBarAction;
            BOSS_BAR_ACTION_TITLE = bossBarActionTitle;
            MethodHandle craftBossBarHandle = null;
            MethodHandle nmsBossBattleSetName = null;
            MethodHandle nmsBossBattleSendUpdate = null;
            if (CLASS_CRAFT_BOSS_BAR != null && CLASS_CHAT_COMPONENT != null && BOSS_BAR_ACTION_TITLE != null) {
                try {
                    Field craftBossBarHandleField = MinecraftReflection.needField(CLASS_CRAFT_BOSS_BAR, "handle");
                    craftBossBarHandle = MinecraftReflection.lookup().unreflectGetter(craftBossBarHandleField);
                    Class<?> nmsBossBattleType = craftBossBarHandleField.getType();
                    for (Field field : nmsBossBattleType.getFields()) {
                        if (!field.getType().equals(CLASS_CHAT_COMPONENT)) continue;
                        nmsBossBattleSetName = MinecraftReflection.lookup().unreflectSetter(field);
                        break;
                    }
                    nmsBossBattleSendUpdate = MinecraftReflection.findMethod(nmsBossBattleType, new String[]{"sendUpdate", "a", "broadcast"}, Void.TYPE, CLASS_BOSS_BAR_ACTION);
                }
                catch (Throwable error) {
                    Knob.logError(error, "Failed to initialize CraftBossBar constructor", new Object[0]);
                }
            }
            CRAFT_BOSS_BAR_HANDLE = craftBossBarHandle;
            NMS_BOSS_BATTLE_SET_NAME = nmsBossBattleSetName;
            NMS_BOSS_BATTLE_SEND_UPDATE = nmsBossBattleSendUpdate;
        }

        public static class Builder
        extends CraftBukkitFacet<Player>
        implements Facet.BossBar.Builder<Player, BossBar> {
            protected Builder() {
                super(Player.class);
            }

            @Override
            public boolean isSupported() {
                return super.isSupported() && CLASS_CRAFT_BOSS_BAR != null && CRAFT_BOSS_BAR_HANDLE != null && NMS_BOSS_BATTLE_SET_NAME != null && NMS_BOSS_BATTLE_SEND_UPDATE != null;
            }

            @Override
            public @NotNull BossBar createBossBar(@NotNull Collection<Player> viewers) {
                return new BossBar(viewers);
            }
        }
    }

    static final class BookPre1_13
    extends AbstractBook {
        private static final String PACKET_TYPE_BOOK_OPEN = "MC|BOpen";
        private static final Class<?> CLASS_BYTE_BUF = MinecraftReflection.findClass("io.netty.buffer.ByteBuf");
        private static final Class<?> CLASS_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findNmsClass("PacketPlayOutCustomPayload");
        private static final Class<?> CLASS_PACKET_DATA_SERIALIZER = MinecraftReflection.findNmsClass("PacketDataSerializer");
        private static final MethodHandle NEW_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findConstructor(CLASS_PACKET_CUSTOM_PAYLOAD, String.class, CLASS_PACKET_DATA_SERIALIZER);
        private static final MethodHandle NEW_PACKET_BYTE_BUF = MinecraftReflection.findConstructor(CLASS_PACKET_DATA_SERIALIZER, CLASS_BYTE_BUF);

        BookPre1_13() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CLASS_BYTE_BUF != null && CLASS_PACKET_CUSTOM_PAYLOAD != null && NEW_PACKET_CUSTOM_PAYLOAD != null;
        }

        @Override
        protected void sendOpenPacket(@NotNull Player viewer) throws Throwable {
            ByteBuf data = Unpooled.buffer();
            data.writeByte(0);
            Object packetByteBuf = NEW_PACKET_BYTE_BUF.invoke(data);
            this.sendMessage(viewer, NEW_PACKET_CUSTOM_PAYLOAD.invoke(PACKET_TYPE_BOOK_OPEN, packetByteBuf));
        }
    }

    static final class Book1_13
    extends AbstractBook {
        private static final Class<?> CLASS_BYTE_BUF = MinecraftReflection.findClass("io.netty.buffer.ByteBuf");
        private static final Class<?> CLASS_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findNmsClass("PacketPlayOutCustomPayload");
        private static final Class<?> CLASS_FRIENDLY_BYTE_BUF = MinecraftReflection.findNmsClass("PacketDataSerializer");
        private static final Class<?> CLASS_RESOURCE_LOCATION = MinecraftReflection.findNmsClass("MinecraftKey");
        private static final Object PACKET_TYPE_BOOK_OPEN;
        private static final MethodHandle NEW_PACKET_CUSTOM_PAYLOAD;
        private static final MethodHandle NEW_FRIENDLY_BYTE_BUF;

        Book1_13() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CLASS_BYTE_BUF != null && NEW_PACKET_CUSTOM_PAYLOAD != null && PACKET_TYPE_BOOK_OPEN != null;
        }

        @Override
        protected void sendOpenPacket(@NotNull Player viewer) throws Throwable {
            ByteBuf data = Unpooled.buffer();
            data.writeByte(0);
            Object packetByteBuf = NEW_FRIENDLY_BYTE_BUF.invoke(data);
            this.sendMessage(viewer, NEW_PACKET_CUSTOM_PAYLOAD.invoke(PACKET_TYPE_BOOK_OPEN, packetByteBuf));
        }

        static {
            NEW_PACKET_CUSTOM_PAYLOAD = MinecraftReflection.findConstructor(CLASS_PACKET_CUSTOM_PAYLOAD, CLASS_RESOURCE_LOCATION, CLASS_FRIENDLY_BYTE_BUF);
            NEW_FRIENDLY_BYTE_BUF = MinecraftReflection.findConstructor(CLASS_FRIENDLY_BYTE_BUF, CLASS_BYTE_BUF);
            Object packetType = null;
            if (CLASS_RESOURCE_LOCATION != null) {
                try {
                    packetType = CLASS_RESOURCE_LOCATION.getConstructor(String.class).newInstance("minecraft:book_open");
                }
                catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException reflectiveOperationException) {
                    // empty catch block
                }
            }
            PACKET_TYPE_BOOK_OPEN = packetType;
        }
    }

    static final class BookPost1_13
    extends AbstractBook {
        private static final Class<?> CLASS_ENUM_HAND = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("EnumHand"), MinecraftReflection.findMcClassName("world.EnumHand"), MinecraftReflection.findMcClassName("world.InteractionHand"));
        private static final Object HAND_MAIN = MinecraftReflection.findEnum(CLASS_ENUM_HAND, "MAIN_HAND", 0);
        private static final Class<?> PACKET_OPEN_BOOK = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutOpenBook"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutOpenBook"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundOpenBookPacket"));
        private static final MethodHandle NEW_PACKET_OPEN_BOOK = MinecraftReflection.findConstructor(PACKET_OPEN_BOOK, CLASS_ENUM_HAND);

        BookPost1_13() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && HAND_MAIN != null && NEW_PACKET_OPEN_BOOK != null;
        }

        @Override
        protected void sendOpenPacket(@NotNull Player viewer) throws Throwable {
            this.sendMessage(viewer, NEW_PACKET_OPEN_BOOK.invoke(HAND_MAIN));
        }
    }

    protected static abstract class AbstractBook
    extends PacketFacet<Player>
    implements Facet.Book<Player, Object, ItemStack> {
        protected static final int HAND_MAIN = 0;
        private static final Material BOOK_TYPE = (Material)MinecraftReflection.findEnum(Material.class, "WRITTEN_BOOK");
        private static final ItemStack BOOK_STACK = BOOK_TYPE == null ? null : new ItemStack(BOOK_TYPE);
        private static final String BOOK_TITLE = "title";
        private static final String BOOK_AUTHOR = "author";
        private static final String BOOK_PAGES = "pages";
        private static final String BOOK_RESOLVED = "resolved";
        private static final Class<?> CLASS_NBT_TAG_COMPOUND = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("NBTTagCompound"), MinecraftReflection.findMcClassName("nbt.CompoundTag"), MinecraftReflection.findMcClassName("nbt.NBTTagCompound"));
        private static final Class<?> CLASS_NBT_IO = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("NBTCompressedStreamTools"), MinecraftReflection.findMcClassName("nbt.NbtIo"), MinecraftReflection.findMcClassName("nbt.NBTCompressedStreamTools"));
        private static final MethodHandle NBT_IO_DESERIALIZE;
        private static final Class<?> CLASS_CRAFT_ITEMSTACK;
        private static final Class<?> CLASS_MC_ITEMSTACK;
        private static final MethodHandle MC_ITEMSTACK_SET_TAG;
        private static final MethodHandle CRAFT_ITEMSTACK_NMS_COPY;
        private static final MethodHandle CRAFT_ITEMSTACK_CRAFT_MIRROR;

        protected AbstractBook() {
        }

        protected abstract void sendOpenPacket(@NotNull Player var1) throws Throwable;

        @Override
        public boolean isSupported() {
            return super.isSupported() && NBT_IO_DESERIALIZE != null && MC_ITEMSTACK_SET_TAG != null && CRAFT_ITEMSTACK_CRAFT_MIRROR != null && CRAFT_ITEMSTACK_NMS_COPY != null && BOOK_STACK != null;
        }

        @Override
        @NotNull
        public String createMessage(@NotNull Player viewer, @NotNull Component message) {
            return (String)BukkitComponentSerializer.gson().serialize(message);
        }

        @Override
        @NotNull
        public ItemStack createBook(@NotNull String title, @NotNull String author, @NotNull Iterable<Object> pages) {
            return this.applyTag(BOOK_STACK, AbstractBook.tagFor(title, author, pages));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void openBook(@NotNull Player viewer, @NotNull ItemStack book) {
            PlayerInventory inventory = viewer.getInventory();
            ItemStack current = inventory.getItemInHand();
            try {
                inventory.setItemInHand(book);
                this.sendOpenPacket(viewer);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to send openBook packet: %s", book);
            }
            finally {
                inventory.setItemInHand(current);
            }
        }

        private static CompoundBinaryTag tagFor(@NotNull String title, @NotNull String author, @NotNull Iterable<Object> pages) {
            ListBinaryTag.Builder<StringBinaryTag> builder = ListBinaryTag.builder(BinaryTagTypes.STRING);
            for (Object page : pages) {
                builder.add(StringBinaryTag.of((String)page));
            }
            return ((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString(BOOK_TITLE, title)).putString(BOOK_AUTHOR, author)).put(BOOK_PAGES, builder.build())).putByte(BOOK_RESOLVED, (byte)1)).build();
        }

        @NotNull
        private Object createTag(@NotNull CompoundBinaryTag tag) throws IOException {
            Object object;
            TrustedByteArrayOutputStream output = new TrustedByteArrayOutputStream();
            BinaryTagIO.writer().write(tag, output);
            DataInputStream dis = new DataInputStream(output.toInputStream());
            try {
                object = NBT_IO_DESERIALIZE.invoke(dis);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        dis.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (Throwable err) {
                    throw new IOException(err);
                }
            }
            dis.close();
            return object;
        }

        private ItemStack applyTag(@NotNull ItemStack input, CompoundBinaryTag binTag) {
            if (CRAFT_ITEMSTACK_NMS_COPY == null || MC_ITEMSTACK_SET_TAG == null || CRAFT_ITEMSTACK_CRAFT_MIRROR == null) {
                return input;
            }
            try {
                Object stack = CRAFT_ITEMSTACK_NMS_COPY.invoke(input);
                Object tag = this.createTag(binTag);
                MC_ITEMSTACK_SET_TAG.invoke(stack, tag);
                return CRAFT_ITEMSTACK_CRAFT_MIRROR.invoke(stack);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to apply NBT tag to ItemStack: %s %s", input, binTag);
                return input;
            }
        }

        static {
            MethodHandle nbtIoDeserialize = null;
            if (CLASS_NBT_IO != null) {
                for (Method method : CLASS_NBT_IO.getDeclaredMethods()) {
                    Class<?> firstParam;
                    if (!Modifier.isStatic(method.getModifiers()) || !method.getReturnType().equals(CLASS_NBT_TAG_COMPOUND) || method.getParameterCount() != 1 || !(firstParam = method.getParameterTypes()[0]).equals(DataInputStream.class) && !firstParam.equals(DataInput.class)) continue;
                    try {
                        nbtIoDeserialize = MinecraftReflection.lookup().unreflect(method);
                    }
                    catch (IllegalAccessException illegalAccessException) {}
                    break;
                }
            }
            NBT_IO_DESERIALIZE = nbtIoDeserialize;
            CLASS_CRAFT_ITEMSTACK = MinecraftReflection.findCraftClass("inventory.CraftItemStack");
            CLASS_MC_ITEMSTACK = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("ItemStack"), MinecraftReflection.findMcClassName("world.item.ItemStack"));
            MC_ITEMSTACK_SET_TAG = MinecraftReflection.searchMethod(CLASS_MC_ITEMSTACK, (Integer)1, "setTag", Void.TYPE, CLASS_NBT_TAG_COMPOUND);
            CRAFT_ITEMSTACK_NMS_COPY = MinecraftReflection.findStaticMethod(CLASS_CRAFT_ITEMSTACK, "asNMSCopy", CLASS_MC_ITEMSTACK, ItemStack.class);
            CRAFT_ITEMSTACK_CRAFT_MIRROR = MinecraftReflection.findStaticMethod(CLASS_CRAFT_ITEMSTACK, "asCraftMirror", CLASS_CRAFT_ITEMSTACK, CLASS_MC_ITEMSTACK);
        }

        private static final class TrustedByteArrayOutputStream
        extends ByteArrayOutputStream {
            private TrustedByteArrayOutputStream() {
            }

            public InputStream toInputStream() {
                return new ByteArrayInputStream(this.buf, 0, this.count);
            }
        }
    }

    static final class Book_1_20_5
    extends PacketFacet<Player>
    implements Facet.Book<Player, Object, ItemStack> {
        Book_1_20_5() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CraftBukkitAccess.Book_1_20_5.isSupported();
        }

        @Override
        @Nullable
        public ItemStack createBook(@NotNull String title, @NotNull String author, @NotNull Iterable<Object> pages) {
            try {
                ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
                ArrayList<Object> pageList = new ArrayList<Object>();
                for (Object page : pages) {
                    pageList.add(CraftBukkitAccess.Book_1_20_5.CREATE_FILTERABLE.invoke(page));
                }
                Object bookContent = CraftBukkitAccess.Book_1_20_5.NEW_BOOK_CONTENT.invoke(CraftBukkitAccess.Book_1_20_5.CREATE_FILTERABLE.invoke(title), author, 0, pageList, true);
                Object stack = CraftBukkitAccess.Book_1_20_5.CRAFT_ITEMSTACK_NMS_COPY.invoke(item);
                CraftBukkitAccess.Book_1_20_5.MC_ITEMSTACK_SET.invoke(stack, CraftBukkitAccess.Book_1_20_5.WRITTEN_BOOK_COMPONENT_TYPE, bookContent);
                return CraftBukkitAccess.Book_1_20_5.CRAFT_ITEMSTACK_CRAFT_MIRROR.invoke(stack);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to apply written_book_content component to ItemStack", new Object[0]);
                return null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void openBook(@NotNull Player viewer, @NotNull ItemStack book) {
            PlayerInventory inventory = viewer.getInventory();
            ItemStack current = inventory.getItemInHand();
            try {
                inventory.setItemInHand(book);
                this.sendMessage(viewer, CraftBukkitAccess.Book_1_20_5.NEW_PACKET_OPEN_BOOK.invoke(CraftBukkitAccess.Book_1_20_5.HAND_MAIN));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to send openBook packet: %s", book);
            }
            finally {
                inventory.setItemInHand(current);
            }
        }
    }

    static class Title
    extends PacketFacet<Player>
    implements Facet.Title<Player, Object, List<Object>, List<?>> {
        Title() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CONSTRUCTOR_TITLE_MESSAGE != null && CONSTRUCTOR_TITLE_TIMES != null;
        }

        @Override
        @NotNull
        public List<Object> createTitleCollection() {
            return new ArrayList<Object>();
        }

        @Override
        public void contributeTitle(@NotNull List<Object> coll, @NotNull Object title) {
            try {
                coll.add(CONSTRUCTOR_TITLE_MESSAGE.invoke(TITLE_ACTION_TITLE, title));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke title packet constructor", new Object[0]);
            }
        }

        @Override
        public void contributeSubtitle(@NotNull List<Object> coll, @NotNull Object subtitle) {
            try {
                coll.add(CONSTRUCTOR_TITLE_MESSAGE.invoke(TITLE_ACTION_SUBTITLE, subtitle));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke subtitle packet constructor", new Object[0]);
            }
        }

        @Override
        public void contributeTimes(@NotNull List<Object> coll, int inTicks, int stayTicks, int outTicks) {
            try {
                coll.add(CONSTRUCTOR_TITLE_TIMES.invoke(inTicks, stayTicks, outTicks));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke title animations packet constructor", new Object[0]);
            }
        }

        @Override
        @Nullable
        public List<?> completeTitle(@NotNull List<Object> coll) {
            return coll;
        }

        @Override
        public void showTitle(@NotNull Player viewer, @NotNull List<?> packets) {
            for (Object packet : packets) {
                this.sendMessage(viewer, packet);
            }
        }

        @Override
        public void clearTitle(@NotNull Player viewer) {
            try {
                if (TITLE_ACTION_CLEAR != null) {
                    this.sendPacket(viewer, CONSTRUCTOR_TITLE_MESSAGE.invoke(TITLE_ACTION_CLEAR, null));
                } else {
                    viewer.sendTitle("", "", -1, -1, -1);
                }
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to clear title", new Object[0]);
            }
        }

        @Override
        public void resetTitle(@NotNull Player viewer) {
            try {
                if (TITLE_ACTION_RESET != null) {
                    this.sendPacket(viewer, CONSTRUCTOR_TITLE_MESSAGE.invoke(TITLE_ACTION_RESET, null));
                } else {
                    viewer.resetTitle();
                }
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to clear title", new Object[0]);
            }
        }
    }

    static class Title_1_17
    extends PacketFacet<Player>
    implements Facet.Title<Player, Object, List<Object>, List<?>> {
        private static final Class<?> PACKET_SET_TITLE = MinecraftReflection.findMcClass("network.protocol.game.ClientboundSetTitleTextPacket");
        private static final Class<?> PACKET_SET_SUBTITLE = MinecraftReflection.findMcClass("network.protocol.game.ClientboundSetSubtitleTextPacket");
        private static final Class<?> PACKET_SET_TITLE_ANIMATION = MinecraftReflection.findMcClass("network.protocol.game.ClientboundSetTitlesAnimationPacket");
        private static final Class<?> PACKET_CLEAR_TITLES = MinecraftReflection.findMcClass("network.protocol.game.ClientboundClearTitlesPacket");
        private static final MethodHandle CONSTRUCTOR_SET_TITLE = MinecraftReflection.findConstructor(PACKET_SET_TITLE, CraftBukkitFacet.access$500());
        private static final MethodHandle CONSTRUCTOR_SET_SUBTITLE = MinecraftReflection.findConstructor(PACKET_SET_SUBTITLE, CraftBukkitFacet.access$500());
        private static final MethodHandle CONSTRUCTOR_SET_TITLE_ANIMATION = MinecraftReflection.findConstructor(PACKET_SET_TITLE_ANIMATION, Integer.TYPE, Integer.TYPE, Integer.TYPE);
        private static final MethodHandle CONSTRUCTOR_CLEAR_TITLES = MinecraftReflection.findConstructor(PACKET_CLEAR_TITLES, Boolean.TYPE);

        Title_1_17() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CONSTRUCTOR_SET_TITLE != null && CONSTRUCTOR_SET_SUBTITLE != null && CONSTRUCTOR_SET_TITLE_ANIMATION != null && CONSTRUCTOR_CLEAR_TITLES != null;
        }

        @Override
        @NotNull
        public List<Object> createTitleCollection() {
            return new ArrayList<Object>();
        }

        @Override
        public void contributeTitle(@NotNull List<Object> coll, @NotNull Object title) {
            try {
                coll.add(CONSTRUCTOR_SET_TITLE.invoke(title));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke title packet constructor", new Object[0]);
            }
        }

        @Override
        public void contributeSubtitle(@NotNull List<Object> coll, @NotNull Object subtitle) {
            try {
                coll.add(CONSTRUCTOR_SET_SUBTITLE.invoke(subtitle));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke subtitle packet constructor", new Object[0]);
            }
        }

        @Override
        public void contributeTimes(@NotNull List<Object> coll, int inTicks, int stayTicks, int outTicks) {
            try {
                coll.add(CONSTRUCTOR_SET_TITLE_ANIMATION.invoke(inTicks, stayTicks, outTicks));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke title animations packet constructor", new Object[0]);
            }
        }

        @Override
        @Nullable
        public List<?> completeTitle(@NotNull List<Object> coll) {
            return coll;
        }

        @Override
        public void showTitle(@NotNull Player viewer, @NotNull List<?> packets) {
            for (Object packet : packets) {
                this.sendMessage(viewer, packet);
            }
        }

        @Override
        public void clearTitle(@NotNull Player viewer) {
            try {
                if (CONSTRUCTOR_CLEAR_TITLES != null) {
                    this.sendPacket(viewer, CONSTRUCTOR_CLEAR_TITLES.invoke(false));
                } else {
                    viewer.sendTitle("", "", -1, -1, -1);
                }
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to clear title", new Object[0]);
            }
        }

        @Override
        public void resetTitle(@NotNull Player viewer) {
            try {
                if (CONSTRUCTOR_CLEAR_TITLES != null) {
                    this.sendPacket(viewer, CONSTRUCTOR_CLEAR_TITLES.invoke(true));
                } else {
                    viewer.resetTitle();
                }
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to clear title", new Object[0]);
            }
        }
    }

    static class EntitySound
    extends PacketFacet<Player>
    implements PartialEntitySound {
        private static final Class<?> CLASS_CLIENTBOUND_CUSTOM_SOUND = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("PacketPlayOutCustomSoundEffect"), MinecraftReflection.findMcClassName("network.protocol.game.ClientboundCustomSoundPacket"), MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutCustomSoundEffect"));
        private static final Class<?> CLASS_VEC3 = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("Vec3D"), MinecraftReflection.findMcClassName("world.phys.Vec3D"), MinecraftReflection.findMcClassName("world.phys.Vec3"));
        private static final MethodHandle NEW_CLIENTBOUND_ENTITY_SOUND;
        private static final MethodHandle NEW_CLIENTBOUND_CUSTOM_SOUND;
        private static final MethodHandle NEW_VEC3;
        private static final MethodHandle NEW_RESOURCE_LOCATION;
        private static final MethodHandle REGISTRY_GET_OPTIONAL;
        private static final Object REGISTRY_SOUND_EVENT;

        EntitySound() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && NEW_CLIENTBOUND_ENTITY_SOUND != null && NEW_RESOURCE_LOCATION != null && REGISTRY_SOUND_EVENT != null && REGISTRY_GET_OPTIONAL != null && CRAFT_ENTITY_GET_HANDLE != null && CraftBukkitAccess.EntitySound.isSupported();
        }

        @Override
        public Object createForEntity(Sound sound, Entity entity) {
            try {
                Object nmsEntity = this.toNativeEntity(entity);
                if (nmsEntity == null) {
                    return null;
                }
                Object soundCategory = this.toVanilla(sound.source());
                if (soundCategory == null) {
                    return null;
                }
                Object nameRl = NEW_RESOURCE_LOCATION.invoke(sound.name().namespace(), sound.name().value());
                Optional event = REGISTRY_GET_OPTIONAL.invoke(REGISTRY_SOUND_EVENT, nameRl);
                long seed = sound.seed().orElseGet(() -> ThreadLocalRandom.current().nextLong());
                if (event.isPresent()) {
                    return NEW_CLIENTBOUND_ENTITY_SOUND.invoke(event.get(), soundCategory, nmsEntity, sound.volume(), sound.pitch(), seed);
                }
                if (NEW_CLIENTBOUND_CUSTOM_SOUND != null && NEW_VEC3 != null) {
                    Location loc = entity.getLocation();
                    return NEW_CLIENTBOUND_CUSTOM_SOUND.invoke(nameRl, soundCategory, NEW_VEC3.invoke(loc.getX(), loc.getY(), loc.getZ()), sound.volume(), sound.pitch(), seed);
                }
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to send sound tracking an entity", new Object[0]);
            }
            return null;
        }

        @Override
        public void playSound(@NotNull Player viewer, Object message) {
            this.sendPacket(viewer, message);
        }

        static {
            NEW_VEC3 = MinecraftReflection.findConstructor(CLASS_VEC3, Double.TYPE, Double.TYPE, Double.TYPE);
            NEW_RESOURCE_LOCATION = MinecraftReflection.findConstructor(CraftBukkitAccess.CLASS_RESOURCE_LOCATION, String.class, String.class);
            REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, (Integer)1, "getOptional", Optional.class, CraftBukkitAccess.CLASS_RESOURCE_LOCATION);
            MethodHandle entitySoundPacketConstructor = MinecraftReflection.findConstructor(CraftBukkitAccess.EntitySound.CLASS_CLIENTBOUND_ENTITY_SOUND, CraftBukkitAccess.EntitySound.CLASS_SOUND_EVENT, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CLASS_NMS_ENTITY, Float.TYPE, Float.TYPE, Long.TYPE);
            if (entitySoundPacketConstructor == null && (entitySoundPacketConstructor = MinecraftReflection.findConstructor(CraftBukkitAccess.EntitySound.CLASS_CLIENTBOUND_ENTITY_SOUND, CraftBukkitAccess.EntitySound.CLASS_SOUND_EVENT, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CLASS_NMS_ENTITY, Float.TYPE, Float.TYPE)) != null) {
                entitySoundPacketConstructor = MethodHandles.dropArguments(entitySoundPacketConstructor, 5, new Class[]{Long.TYPE});
            }
            NEW_CLIENTBOUND_ENTITY_SOUND = entitySoundPacketConstructor;
            MethodHandle customSoundPacketConstructor = MinecraftReflection.findConstructor(CLASS_CLIENTBOUND_CUSTOM_SOUND, CraftBukkitAccess.CLASS_RESOURCE_LOCATION, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CLASS_VEC3, Float.TYPE, Float.TYPE, Long.TYPE);
            if (customSoundPacketConstructor == null && (customSoundPacketConstructor = MinecraftReflection.findConstructor(CLASS_CLIENTBOUND_CUSTOM_SOUND, CraftBukkitAccess.CLASS_RESOURCE_LOCATION, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CLASS_VEC3, Float.TYPE, Float.TYPE)) != null) {
                customSoundPacketConstructor = MethodHandles.dropArguments(customSoundPacketConstructor, 5, new Class[]{Long.TYPE});
            }
            NEW_CLIENTBOUND_CUSTOM_SOUND = customSoundPacketConstructor;
            Object registrySoundEvent = null;
            if (CraftBukkitAccess.CLASS_REGISTRY != null) {
                try {
                    Field soundEventField = MinecraftReflection.findField(CraftBukkitAccess.CLASS_REGISTRY, "SOUND_EVENT");
                    if (soundEventField != null) {
                        registrySoundEvent = soundEventField.get(null);
                    } else {
                        Object rootRegistry = null;
                        for (Field field : CraftBukkitAccess.CLASS_REGISTRY.getDeclaredFields()) {
                            int mask = 28;
                            if ((field.getModifiers() & 0x1C) != 28 || !field.getType().equals(CraftBukkitAccess.CLASS_WRITABLE_REGISTRY)) continue;
                            field.setAccessible(true);
                            rootRegistry = field.get(null);
                            break;
                        }
                        if (rootRegistry != null) {
                            registrySoundEvent = REGISTRY_GET_OPTIONAL.invoke(rootRegistry, NEW_RESOURCE_LOCATION.invoke("minecraft", "sound_event")).orElse(null);
                        }
                    }
                }
                catch (Throwable thr) {
                    Knob.logError(thr, "Failed to initialize EntitySound CraftBukkit facet", new Object[0]);
                }
            }
            REGISTRY_SOUND_EVENT = registrySoundEvent;
        }
    }

    static class EntitySound_1_19_3
    extends PacketFacet<Player>
    implements PartialEntitySound {
        EntitySound_1_19_3() {
        }

        @Override
        public boolean isSupported() {
            return CraftBukkitAccess.EntitySound_1_19_3.isSupported() && super.isSupported();
        }

        @Override
        public Object createForEntity(Sound sound, Entity entity) {
            try {
                Object resLoc = CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke(sound.name().namespace(), sound.name().value());
                Optional possibleSoundEvent = CraftBukkitAccess.EntitySound_1_19_3.REGISTRY_GET_OPTIONAL.invoke(CraftBukkitAccess.EntitySound_1_19_3.SOUND_EVENT_REGISTRY, resLoc);
                Object soundEvent = possibleSoundEvent.isPresent() ? possibleSoundEvent.get() : CraftBukkitAccess.EntitySound_1_19_3.SOUND_EVENT_CREATE_VARIABLE_RANGE.invoke(resLoc);
                Object soundEventHolder = CraftBukkitAccess.EntitySound_1_19_3.REGISTRY_WRAP_AS_HOLDER.invoke(CraftBukkitAccess.EntitySound_1_19_3.SOUND_EVENT_REGISTRY, soundEvent);
                long seed = sound.seed().orElseGet(() -> ThreadLocalRandom.current().nextLong());
                return CraftBukkitAccess.EntitySound_1_19_3.NEW_CLIENTBOUND_ENTITY_SOUND.invoke(soundEventHolder, this.toVanilla(sound.source()), this.toNativeEntity(entity), sound.volume(), sound.pitch(), seed);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to send sound tracking an entity", new Object[0]);
                return null;
            }
        }

        @Override
        public void playSound(@NotNull Player viewer, Object packet) {
            this.sendPacket(viewer, packet);
        }
    }

    private static interface PartialEntitySound
    extends Facet.EntitySound<Player, Object> {
        public static final Map<String, Object> MC_SOUND_SOURCE_BY_NAME = new ConcurrentHashMap<String, Object>();

        @Override
        default public Object createForSelf(Player viewer, @NotNull Sound sound) {
            return this.createForEntity(sound, (Entity)viewer);
        }

        @Override
        default public Object createForEmitter(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
            Entity entity;
            if (emitter instanceof BukkitEmitter) {
                entity = ((BukkitEmitter)emitter).entity;
            } else if (emitter instanceof Entity) {
                entity = (Entity)emitter;
            } else {
                return null;
            }
            return this.createForEntity(sound, entity);
        }

        default public Object toNativeEntity(Entity entity) throws Throwable {
            if (!CLASS_CRAFT_ENTITY.isInstance(entity)) {
                return null;
            }
            return CRAFT_ENTITY_GET_HANDLE.invoke(entity);
        }

        default public Object toVanilla(Sound.Source source) throws Throwable {
            if (MC_SOUND_SOURCE_BY_NAME.isEmpty()) {
                for (Object enumConstant : CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE.getEnumConstants()) {
                    MC_SOUND_SOURCE_BY_NAME.put(CraftBukkitAccess.EntitySound.SOUND_SOURCE_GET_NAME.invoke(enumConstant), enumConstant);
                }
            }
            return MC_SOUND_SOURCE_BY_NAME.get(Sound.Source.NAMES.key(source));
        }

        public Object createForEntity(Sound var1, Entity var2);
    }

    static class ActionBarLegacy
    extends PacketFacet<Player>
    implements Facet.ActionBar<Player, Object> {
        ActionBarLegacy() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && LEGACY_CHAT_PACKET_CONSTRUCTOR != null;
        }

        @Override
        @Nullable
        public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
            TextComponent legacyMessage = Component.text(BukkitComponentSerializer.legacy().serialize(message));
            try {
                return LEGACY_CHAT_PACKET_CONSTRUCTOR.invoke(super.createMessage(viewer, (Component)legacyMessage), (byte)2);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke PacketPlayOutChat constructor: %s", legacyMessage);
                return null;
            }
        }
    }

    static class ActionBar
    extends PacketFacet<Player>
    implements Facet.ActionBar<Player, Object> {
        ActionBar() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && TITLE_ACTION_ACTIONBAR != null;
        }

        @Override
        @Nullable
        public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
            try {
                return CONSTRUCTOR_TITLE_MESSAGE.invoke(TITLE_ACTION_ACTIONBAR, super.createMessage(viewer, message));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke PacketPlayOutTitle constructor: %s", message);
                return null;
            }
        }
    }

    static class ActionBar_1_17
    extends PacketFacet<Player>
    implements Facet.ActionBar<Player, Object> {
        @Nullable
        private static final Class<?> CLASS_SET_ACTION_BAR_TEXT_PACKET = MinecraftReflection.findMcClass("network.protocol.game.ClientboundSetActionBarTextPacket");
        @Nullable
        private static final MethodHandle CONSTRUCTOR_ACTION_BAR = MinecraftReflection.findConstructor(CLASS_SET_ACTION_BAR_TEXT_PACKET, CraftBukkitFacet.access$500());

        ActionBar_1_17() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CONSTRUCTOR_ACTION_BAR != null;
        }

        @Override
        @Nullable
        public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
            try {
                return CONSTRUCTOR_ACTION_BAR.invoke(super.createMessage(viewer, message));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke PacketPlayOutTitle constructor: %s", message);
                return null;
            }
        }
    }

    static class Chat
    extends PacketFacet<CommandSender>
    implements Facet.Chat<CommandSender, Object> {
        Chat() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CHAT_PACKET_CONSTRUCTOR != null;
        }

        @Override
        public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, @NotNull Object message, @NotNull Object type) {
            Object messageType = type == MessageType.CHAT ? MESSAGE_TYPE_CHAT : MESSAGE_TYPE_SYSTEM;
            try {
                this.sendMessage(viewer, CHAT_PACKET_CONSTRUCTOR.invoke(message, messageType, source.uuid()));
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke PacketPlayOutChat constructor: %s %s", message, messageType);
            }
        }
    }

    static class Chat1_19_3
    extends Chat {
        Chat1_19_3() {
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && CraftBukkitAccess.Chat1_19_3.isSupported();
        }

        @Override
        public void sendMessage(@NotNull CommandSender viewer, @NotNull Identity source, @NotNull Object message, @NotNull Object type) {
            if (!(type instanceof ChatType.Bound)) {
                super.sendMessage(viewer, source, message, type);
            } else {
                ChatType.Bound bound = (ChatType.Bound)type;
                try {
                    Object boundNetwork;
                    Object nameComponent = this.createMessage(viewer, bound.name());
                    Object targetComponent = bound.target() != null ? this.createMessage(viewer, bound.target()) : null;
                    Object registryAccess = CraftBukkitAccess.Chat1_19_3.ACTUAL_GET_REGISTRY_ACCESS.invoke(CraftBukkitAccess.Chat1_19_3.SERVER_PLAYER_GET_LEVEL.invoke(CRAFT_PLAYER_GET_HANDLE.invoke(viewer)));
                    Object chatTypeRegistry = CraftBukkitAccess.Chat1_19_3.REGISTRY_ACCESS_GET_REGISTRY_OPTIONAL.invoke(registryAccess, CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_RESOURCE_KEY).orElseThrow(NoSuchElementException::new);
                    Object typeResourceLocation = CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke(bound.type().key().namespace(), bound.type().key().value());
                    if (CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR != null) {
                        Object chatTypeObject = CraftBukkitAccess.Chat1_19_3.REGISTRY_GET_OPTIONAL.invoke(chatTypeRegistry, typeResourceLocation).orElseThrow(NoSuchElementException::new);
                        int networkId = CraftBukkitAccess.Chat1_19_3.REGISTRY_GET_ID.invoke(chatTypeRegistry, chatTypeObject);
                        if (networkId < 0) {
                            throw new IllegalArgumentException("Could not get a valid network id from " + type);
                        }
                        boundNetwork = CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR.invoke(networkId, nameComponent, targetComponent);
                    } else {
                        Object chatTypeHolder = CraftBukkitAccess.Chat1_19_3.REGISTRY_GET_HOLDER.invoke(chatTypeRegistry, typeResourceLocation).orElseThrow(NoSuchElementException::new);
                        boundNetwork = CraftBukkitAccess.Chat1_19_3.CHAT_TYPE_BOUND_CONSTRUCTOR.invoke(chatTypeHolder, nameComponent, Optional.ofNullable(targetComponent));
                    }
                    this.sendMessage(viewer, CraftBukkitAccess.Chat1_19_3.DISGUISED_CHAT_PACKET_CONSTRUCTOR.invoke(message, boundNetwork));
                }
                catch (Throwable error) {
                    Knob.logError(error, "Failed to send a 1.19.3+ message: %s %s", message, type);
                }
            }
        }
    }

    static class PacketFacet<V extends CommandSender>
    extends CraftBukkitFacet<V>
    implements Facet.Message<V, Object> {
        protected PacketFacet() {
            super(CLASS_CRAFT_PLAYER);
        }

        public void sendPacket(@NotNull Player player, @Nullable Object packet) {
            if (packet == null) {
                return;
            }
            try {
                PLAYER_CONNECTION_SEND_PACKET.invoke(ENTITY_PLAYER_GET_CONNECTION.invoke(CRAFT_PLAYER_GET_HANDLE.invoke(player)), packet);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to invoke CraftBukkit sendPacket: %s", packet);
            }
        }

        public void sendMessage(@NotNull V player, @Nullable Object packet) {
            this.sendPacket((Player)player, packet);
        }

        @Override
        @Nullable
        public Object createMessage(@NotNull V viewer, @NotNull Component message) {
            try {
                return MinecraftComponentSerializer.get().serialize(message);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to serialize net.minecraft.server IChatBaseComponent: %s", message);
                return null;
            }
        }
    }
}

