/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.api.Via
 *  com.viaversion.viaversion.api.connection.UserConnection
 *  com.viaversion.viaversion.api.protocol.Protocol
 *  com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType
 *  com.viaversion.viaversion.api.protocol.packet.PacketWrapper
 *  com.viaversion.viaversion.api.type.Type
 *  com.viaversion.viaversion.libs.gson.JsonElement
 *  com.viaversion.viaversion.libs.gson.JsonParser
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.viaversion;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.MessageType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetBase;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonParser;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViaFacet<V>
extends FacetBase<V>
implements Facet.Message<V, String> {
    private static final String PACKAGE = "com.viaversion.viaversion";
    private static final int SUPPORTED_VIA_MAJOR_VERSION = 4;
    private static final boolean SUPPORTED;
    private final Function<V, UserConnection> connectionFunction;
    private final int minProtocol;

    public ViaFacet(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction, int minProtocol) {
        super(viewerClass);
        this.connectionFunction = connectionFunction;
        this.minProtocol = minProtocol;
    }

    @Override
    public boolean isSupported() {
        return super.isSupported() && SUPPORTED && this.connectionFunction != null && this.minProtocol >= 0;
    }

    @Override
    public boolean isApplicable(@NotNull V viewer) {
        return super.isApplicable(viewer) && this.minProtocol > Via.getAPI().getServerVersion().lowestSupportedVersion() && this.findProtocol(viewer) >= this.minProtocol;
    }

    @Nullable
    public UserConnection findConnection(@NotNull V viewer) {
        return this.connectionFunction.apply(viewer);
    }

    public int findProtocol(@NotNull V viewer) {
        UserConnection connection = this.findConnection(viewer);
        if (connection != null) {
            return connection.getProtocolInfo().getProtocolVersion();
        }
        return -1;
    }

    @Override
    @NotNull
    public String createMessage(@NotNull V viewer, @NotNull Component message) {
        int protocol = this.findProtocol(viewer);
        if (protocol >= 713) {
            return (String)GsonComponentSerializer.gson().serialize(message);
        }
        return (String)GsonComponentSerializer.colorDownsamplingGson().serialize(message);
    }

    static {
        boolean supported = false;
        try {
            Class.forName("com.viaversion.viaversion.api.ViaAPI").getDeclaredMethod("majorVersion", new Class[0]);
            supported = Via.getAPI().majorVersion() == 4;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        SUPPORTED = supported && Knob.isEnabled("viaversion", true);
    }

    public static final class TabList<V>
    extends ProtocolBased<V>
    implements Facet.TabList<V, String> {
        public TabList(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> userConnection) {
            super("1_16", "1_15_2", 713, "TAB_LIST", viewerClass, userConnection);
        }

        @Override
        public void send(V viewer, @Nullable String header, @Nullable String footer) {
            PacketWrapper packet = this.createPacket(viewer);
            packet.write(Type.COMPONENT, (Object)this.parse(header));
            packet.write(Type.COMPONENT, (Object)this.parse(footer));
            this.sendPacket(packet);
        }
    }

    public static final class BossBar<V>
    extends ProtocolBased<V>
    implements Facet.BossBarPacket<V> {
        private final Set<V> viewers;
        private UUID id;
        private String title;
        private float health;
        private int color;
        private int overlay;
        private byte flags;

        private BossBar(@NotNull String fromProtocol, @NotNull String toProtocol, @NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction, Collection<V> viewers) {
            super(fromProtocol, toProtocol, 356, "BOSSBAR", viewerClass, connectionFunction);
            this.viewers = new CopyOnWriteArraySet<V>(viewers);
        }

        @Override
        public void bossBarInitialized(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar) {
            Facet.BossBarPacket.super.bossBarInitialized(bar);
            this.id = UUID.randomUUID();
            this.broadcastPacket(0);
        }

        @Override
        public void bossBarNameChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
            if (!this.viewers.isEmpty()) {
                this.title = this.createMessage((Object)this.viewers.iterator().next(), newName);
                this.broadcastPacket(3);
            }
        }

        @Override
        public void bossBarProgressChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, float oldPercent, float newPercent) {
            this.health = newPercent;
            this.broadcastPacket(2);
        }

        @Override
        public void bossBarColorChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar,  @NotNull BossBar.Color oldColor,  @NotNull BossBar.Color newColor) {
            this.color = this.createColor(newColor);
            this.broadcastPacket(4);
        }

        @Override
        public void bossBarOverlayChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar,  @NotNull BossBar.Overlay oldOverlay,  @NotNull BossBar.Overlay newOverlay) {
            this.overlay = this.createOverlay(newOverlay);
            this.broadcastPacket(4);
        }

        @Override
        public void bossBarFlagsChanged(@NotNull com.bergerkiller.bukkit.common.dep.net.kyori.adventure.bossbar.BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
            this.flags = this.createFlag(this.flags, flagsAdded, flagsRemoved);
            this.broadcastPacket(5);
        }

        public void sendPacket(@NotNull V viewer, int action) {
            PacketWrapper packet = this.createPacket(viewer);
            packet.write(Type.UUID, (Object)this.id);
            packet.write((Type)Type.VAR_INT, (Object)action);
            if (action == 0 || action == 3) {
                packet.write(Type.COMPONENT, (Object)this.parse(this.title));
            }
            if (action == 0 || action == 2) {
                packet.write((Type)Type.FLOAT, (Object)Float.valueOf(this.health));
            }
            if (action == 0 || action == 4) {
                packet.write((Type)Type.VAR_INT, (Object)this.color);
                packet.write((Type)Type.VAR_INT, (Object)this.overlay);
            }
            if (action == 0 || action == 5) {
                packet.write((Type)Type.BYTE, (Object)this.flags);
            }
            this.sendPacket(packet);
        }

        public void broadcastPacket(int action) {
            if (this.isEmpty()) {
                return;
            }
            for (V viewer : this.viewers) {
                this.sendPacket(viewer, action);
            }
        }

        @Override
        public void addViewer(@NotNull V viewer) {
            if (this.viewers.add(viewer)) {
                this.sendPacket(viewer, 0);
            }
        }

        @Override
        public void removeViewer(@NotNull V viewer) {
            if (this.viewers.remove(viewer)) {
                this.sendPacket(viewer, 1);
            }
        }

        @Override
        public boolean isEmpty() {
            return this.id == null || this.viewers.isEmpty();
        }

        @Override
        public void close() {
            this.broadcastPacket(1);
            this.viewers.clear();
        }

        public static class Builder1_9_To_1_15<V>
        extends ViaFacet<V>
        implements Facet.BossBar.Builder<V, Facet.BossBar<V>> {
            public Builder1_9_To_1_15(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
                super(viewerClass, connectionFunction, 356);
            }

            @Override
            public  @NotNull Facet.BossBar<V> createBossBar(@NotNull Collection<V> viewer) {
                return new BossBar("1_9", "1_8", this.viewerClass, this::findConnection, viewer);
            }
        }

        public static class Builder<V>
        extends ViaFacet<V>
        implements Facet.BossBar.Builder<V, Facet.BossBar<V>> {
            public Builder(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
                super(viewerClass, connectionFunction, 713);
            }

            @Override
            public  @NotNull Facet.BossBar<V> createBossBar(@NotNull Collection<V> viewer) {
                return new BossBar("1_16", "1_15_2", this.viewerClass, this::findConnection, viewer);
            }
        }
    }

    public static class Title<V>
    extends ProtocolBased<V>
    implements Facet.TitlePacket<V, String, List<Consumer<PacketWrapper>>, Consumer<V>> {
        protected Title(@NotNull String fromProtocol, @NotNull String toProtocol, int minProtocol, @NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
            super(fromProtocol, toProtocol, minProtocol, "TITLE", viewerClass, connectionFunction);
        }

        public Title(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
            this("1_16", "1_15_2", 713, viewerClass, connectionFunction);
        }

        @Override
        @NotNull
        public List<Consumer<PacketWrapper>> createTitleCollection() {
            return new ArrayList<Consumer<PacketWrapper>>();
        }

        @Override
        public void contributeTitle(@NotNull List<Consumer<PacketWrapper>> coll, @NotNull String title) {
            coll.add(packet -> {
                packet.write((Type)Type.VAR_INT, (Object)0);
                packet.write(Type.COMPONENT, (Object)this.parse(title));
            });
        }

        @Override
        public void contributeSubtitle(@NotNull List<Consumer<PacketWrapper>> coll, @NotNull String subtitle) {
            coll.add(packet -> {
                packet.write((Type)Type.VAR_INT, (Object)1);
                packet.write(Type.COMPONENT, (Object)this.parse(subtitle));
            });
        }

        @Override
        public void contributeTimes(@NotNull List<Consumer<PacketWrapper>> coll, int inTicks, int stayTicks, int outTicks) {
            coll.add(packet -> {
                packet.write((Type)Type.VAR_INT, (Object)3);
                packet.write((Type)Type.INT, (Object)inTicks);
                packet.write((Type)Type.INT, (Object)stayTicks);
                packet.write((Type)Type.INT, (Object)outTicks);
            });
        }

        @Override
        @Nullable
        public Consumer<V> completeTitle(@NotNull List<Consumer<PacketWrapper>> coll) {
            return v -> {
                int length = coll.size();
                for (int i = 0; i < length; ++i) {
                    PacketWrapper pkt = this.createPacket(v);
                    ((Consumer)coll.get(i)).accept(pkt);
                    this.sendPacket(pkt);
                }
            };
        }

        @Override
        public void showTitle(@NotNull V viewer, @NotNull Consumer<V> title) {
            title.accept(viewer);
        }

        @Override
        public void clearTitle(@NotNull V viewer) {
            PacketWrapper packet = this.createPacket(viewer);
            packet.write((Type)Type.VAR_INT, (Object)4);
            this.sendPacket(packet);
        }

        @Override
        public void resetTitle(@NotNull V viewer) {
            PacketWrapper packet = this.createPacket(viewer);
            packet.write((Type)Type.VAR_INT, (Object)5);
            this.sendPacket(packet);
        }
    }

    public static class ActionBarTitle<V>
    extends ProtocolBased<V>
    implements Facet.ActionBar<V, String> {
        public ActionBarTitle(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
            super("1_11", "1_10", 310, "TITLE", viewerClass, connectionFunction);
        }

        @Override
        public void sendMessage(@NotNull V viewer, @NotNull String message) {
            PacketWrapper packet = this.createPacket(viewer);
            packet.write((Type)Type.VAR_INT, (Object)2);
            packet.write(Type.COMPONENT, (Object)this.parse(message));
            this.sendPacket(packet);
        }
    }

    public static class ActionBar<V>
    extends Chat<V>
    implements Facet.ActionBar<V, String> {
        public ActionBar(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
            super(viewerClass, connectionFunction);
        }

        @Override
        public byte createMessageType(@NotNull MessageType type) {
            return 2;
        }

        @Override
        public void sendMessage(@NotNull V viewer, @NotNull String message) {
            this.sendMessage(viewer, Identity.nil(), message, (Object)MessageType.CHAT);
        }
    }

    public static class Chat<V>
    extends ProtocolBased<V>
    implements Facet.ChatPacket<V, String> {
        public Chat(@NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
            super("1_16", "1_15_2", 713, "CHAT_MESSAGE", viewerClass, connectionFunction);
        }

        @Override
        public void sendMessage(@NotNull V viewer, @NotNull Identity source, @NotNull String message, @NotNull Object type) {
            PacketWrapper packet = this.createPacket(viewer);
            packet.write(Type.COMPONENT, (Object)this.parse(message));
            packet.write((Type)Type.BYTE, (Object)this.createMessageType(type instanceof MessageType ? (MessageType)((Object)type) : MessageType.SYSTEM));
            packet.write(Type.UUID, (Object)source.uuid());
            this.sendPacket(packet);
        }
    }

    public static class ProtocolBased<V>
    extends ViaFacet<V> {
        private final Class<? extends Protocol<?, ?, ?, ?>> protocolClass;
        private final Class<? extends ClientboundPacketType> packetClass;
        private final int packetId;

        protected ProtocolBased(@NotNull String fromProtocol, @NotNull String toProtocol, int minProtocol, @NotNull String packetName, @NotNull Class<? extends V> viewerClass, @NotNull Function<V, UserConnection> connectionFunction) {
            super(viewerClass, connectionFunction, minProtocol);
            String protocolClassName = MessageFormat.format("{0}.protocols.protocol{1}to{2}.Protocol{1}To{2}", ViaFacet.PACKAGE, fromProtocol, toProtocol);
            String packetClassName = MessageFormat.format("{0}.protocols.protocol{1}to{2}.ClientboundPackets{1}", ViaFacet.PACKAGE, fromProtocol, toProtocol);
            Class<?> protocolClass = null;
            Class<?> packetClass = null;
            int packetId = -1;
            try {
                protocolClass = Class.forName(protocolClassName);
                packetClass = Class.forName(packetClassName);
                for (ClientboundPacketType type : (ClientboundPacketType[])packetClass.getEnumConstants()) {
                    if (!type.getName().equals(packetName)) continue;
                    packetId = type.getId();
                    break;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.protocolClass = protocolClass;
            this.packetClass = packetClass;
            this.packetId = packetId;
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && this.protocolClass != null && this.packetClass != null && this.packetId >= 0;
        }

        public PacketWrapper createPacket(@NotNull V viewer) {
            return PacketWrapper.create((int)this.packetId, null, (UserConnection)this.findConnection(viewer));
        }

        public void sendPacket(@NotNull PacketWrapper packet) {
            if (packet.user() == null) {
                return;
            }
            try {
                packet.scheduleSend(this.protocolClass);
            }
            catch (Throwable error) {
                Knob.logError(error, "Failed to send ViaVersion packet: %s %s", packet.user(), packet);
            }
        }

        @NotNull
        public JsonElement parse(@NotNull String message) {
            return JsonParser.parseString((String)message);
        }
    }
}

