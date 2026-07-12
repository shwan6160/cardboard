/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.ProtocolManager
 *  com.comphenix.protocol.events.ListenerOptions
 *  com.comphenix.protocol.events.ListenerPriority
 *  com.comphenix.protocol.events.ListeningWhitelist
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.events.PacketListener
 *  com.comphenix.protocol.events.ScheduledPacket
 *  com.comphenix.protocol.injector.GamePhase
 *  com.comphenix.protocol.injector.packet.PacketRegistry
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.internal.network.SilentPacketQueue;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.fast.InvalidArgumentCountException;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class ProtocolLibPacketHandler
implements PacketHandler {
    public static final String LIB_ROOT = "com.comphenix.protocol.";
    private final List<CommonPacketMonitor> monitors = new ArrayList<CommonPacketMonitor>();
    private final List<CommonPacketListener> listeners = new ArrayList<CommonPacketListener>();
    private final SilentPacketQueue silentPacketQueueFallback = new SilentPacketQueue();
    private final SilentQueueCleanupTask silentQueueCleanupTask = new SilentQueueCleanupTask();
    private final FastMethod<Void> receivePacketMethod = new FastMethod();
    private final ThreadLocal<PacketEvent> currentHandledEvent = new ThreadLocal();
    private Class<?> loggedOutPlayerExceptionType = String.class;
    private boolean useSilentPacketQueue = false;
    private boolean isSendSilentPacketBroken = false;

    public static boolean isBundlePacketWorking() {
        if (!CommonCapabilities.HAS_BUNDLE_PACKET) {
            return true;
        }
        Plugin p = CommonUtil.getPlugin("ProtocolLib");
        if (p != null) {
            String ver = p.getDescription().getVersion();
            int dashIdx = ver.indexOf(45);
            if (dashIdx != -1) {
                ver = ver.substring(0, dashIdx);
            }
            if (TextValueSequence.evaluateText(ver, ">", "5.0.0")) {
                return true;
            }
        }
        try {
            PacketEvent.class.getMethod("getBundle", new Class[0]);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
    }

    @Override
    public boolean onEnable() {
        Class<?> manager = CommonUtil.getClass("com.comphenix.protocol.ProtocolManager");
        Class<?> packetContainer = CommonUtil.getClass("com.comphenix.protocol.events.PacketContainer");
        if (manager == null || packetContainer == null) {
            return false;
        }
        CommonUtil.loadClass(InvalidArgumentCountException.class);
        this.loggedOutPlayerExceptionType = CommonUtil.getClass("com.comphenix.protocol.injector.PlayerLoggedOutException");
        if (this.loggedOutPlayerExceptionType == null) {
            this.loggedOutPlayerExceptionType = String.class;
        }
        try {
            this.receivePacketMethod.init(manager.getMethod("receiveClientPacket", Player.class, packetContainer));
            this.receivePacketMethod.forceInitialization();
        }
        catch (Throwable t1) {
            try {
                this.receivePacketMethod.init(manager.getMethod("recieveClientPacket", Player.class, packetContainer));
                this.receivePacketMethod.forceInitialization();
            }
            catch (Throwable t2) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to initialize receiveClientPacket method for ProtocolLib", t1);
            }
        }
        if (ProtocolLibPacketHandler.containsSendSilentPacketBug()) {
            this.useSilentPacketQueue = true;
            this.isSendSilentPacketBroken = true;
        } else {
            this.useSilentPacketQueue = false;
            this.isSendSilentPacketBroken = false;
        }
        return true;
    }

    @Override
    public boolean onDisable() {
        ProtocolManager protocolManager = ProtocolLibPacketHandler.getProtocolManager();
        for (CommonPacketMonitor monitor : this.monitors) {
            protocolManager.removePacketListener((com.comphenix.protocol.events.PacketListener)monitor);
        }
        for (CommonPacketListener listener : this.listeners) {
            protocolManager.removePacketListener((com.comphenix.protocol.events.PacketListener)listener);
        }
        this.monitors.clear();
        this.listeners.clear();
        this.silentQueueCleanupTask.disable();
        if (!CommonUtil.isShuttingDown()) {
            Logging.LOGGER_NETWORK.warning("Reload detected! ProtocolLib does not officially support reloading the server!");
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                Logging.LOGGER_NETWORK.warning("Players are logged in. This is known to cause complete lock-out of the server!");
                Logging.LOGGER_NETWORK.warning("If you must absolutely reload, do so from the server terminal with no players logged in");
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "the ProtocolLib library";
    }

    @Override
    public Collection<Plugin> getListening(PacketType packetType) {
        HashSet<Plugin> plugins = new HashSet<Plugin>();
        boolean outGoing = packetType.isOutGoing();
        com.comphenix.protocol.PacketType comType = ProtocolLibPacketHandler.getPacketType(packetType);
        for (com.comphenix.protocol.events.PacketListener listener : ProtocolLibPacketHandler.getProtocolManager().getPacketListeners()) {
            ListeningWhitelist whitelist = outGoing ? listener.getSendingWhitelist() : listener.getReceivingWhitelist();
            if (whitelist.getPriority() == ListenerPriority.MONITOR || !whitelist.getTypes().contains(comType)) continue;
            plugins.add(listener.getPlugin());
        }
        return plugins;
    }

    private PacketContainer toContainer(Object rawPacket) {
        return new PacketContainer(ProtocolLibPacketHandler.getPacketType(rawPacket.getClass()), rawPacket);
    }

    @Override
    public void receivePacket(Player player, PacketType type, Object packet) {
        if (ServerGamePacketListenerImplHandle.forPlayer(player) == null) {
            return;
        }
        type.preprocess(packet);
        PacketContainer toReceive = this.toContainer(packet);
        try {
            this.receivePacketMethod.invoke(ProtocolLibPacketHandler.getProtocolManager(), player, toReceive);
        }
        catch (RuntimeException ex) {
            if (this.loggedOutPlayerExceptionType.isInstance(ex)) {
                return;
            }
            throw ex;
        }
        catch (Exception e) {
            throw new RuntimeException("Error while receiving packet:", e);
        }
    }

    @Override
    public void sendPacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        ServerGamePacketListenerImplHandle connection = ServerGamePacketListenerImplHandle.forPlayer(player);
        if (connection == null) {
            return;
        }
        type.preprocess(packet);
        if (throughListeners) {
            connection.sendPacket(packet);
            return;
        }
        if (this.useSilentPacketQueue) {
            this.silentPacketQueueFallback.add(player, packet);
            this.silentQueueCleanupTask.kick();
        }
        if (this.isSendSilentPacketBroken) {
            try {
                connection.sendPacket(packet);
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Encountered an error during sendPacket()", t);
                try {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Packet: " + packet);
                }
                catch (Throwable throwable) {}
            }
        } else {
            try {
                PacketContainer toSend = this.toContainer(packet);
                ProtocolLibPacketHandler.getProtocolManager().sendServerPacket(player, toSend, null, false);
            }
            catch (RuntimeException ex) {
                if (this.loggedOutPlayerExceptionType.isInstance(ex)) {
                    return;
                }
                throw ex;
            }
            catch (LinkageError err) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "A severe error occurred inside ProtocolLib while trying to send a packet");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Please look for an update of ProtocolLib to get this issue resolved");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error that occurred", err);
                this.silentPacketQueueFallback.take(player, packet);
                this.isSendSilentPacketBroken = true;
                this.useSilentPacketQueue = true;
                this.sendPacket(player, type, packet, throughListeners);
            }
            catch (Throwable t) {
                throw new RuntimeException("Error while sending packet:", t);
            }
        }
    }

    @Override
    public void queuePacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        PacketEvent currentEvent = this.currentHandledEvent.get();
        if (currentEvent != null) {
            type.preprocess(packet);
            if (!throughListeners && this.useSilentPacketQueue) {
                this.silentPacketQueueFallback.add(player, packet);
            }
            try {
                PacketContainer toSend = new PacketContainer(ProtocolLibPacketHandler.getPacketType(packet.getClass()), packet);
                ScheduledPacket scheduled = new ScheduledPacket(toSend, player, throughListeners);
                currentEvent.schedule(scheduled);
            }
            catch (RuntimeException ex) {
                if (this.loggedOutPlayerExceptionType.isInstance(ex)) {
                    return;
                }
                throw ex;
            }
            catch (LinkageError err) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "A severe error occurred inside ProtocolLib while trying to schedule a packet");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Please look for an update of ProtocolLib to get this issue resolved");
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error that occurred", err);
            }
            catch (Throwable t) {
                throw new RuntimeException("Error while sending packet:", t);
            }
            return;
        }
        this.sendPacket(player, type, packet, throughListeners);
    }

    @Override
    public void removePacketListeners(Plugin plugin) {
        ProtocolLibPacketHandler.getProtocolManager().removePacketListeners(plugin);
        Iterator<CommonPacketListener> list_iter = this.listeners.iterator();
        while (list_iter.hasNext()) {
            if (list_iter.next().getPlugin() != plugin) continue;
            list_iter.remove();
        }
        Iterator<CommonPacketMonitor> mon_iter = this.monitors.iterator();
        while (mon_iter.hasNext()) {
            if (mon_iter.next().getPlugin() != plugin) continue;
            mon_iter.remove();
        }
    }

    @Override
    public void removePacketListener(PacketListener listener) {
        Iterator<CommonPacketListener> iter = this.listeners.iterator();
        while (iter.hasNext()) {
            CommonPacketListener cpl = iter.next();
            if (cpl.listener != listener) continue;
            ProtocolLibPacketHandler.getProtocolManager().removePacketListener((com.comphenix.protocol.events.PacketListener)cpl);
            iter.remove();
        }
    }

    @Override
    public void removePacketMonitor(PacketMonitor monitor) {
        Iterator<CommonPacketMonitor> iter = this.monitors.iterator();
        while (iter.hasNext()) {
            CommonPacketMonitor cpm = iter.next();
            if (cpm.monitor != monitor) continue;
            ProtocolLibPacketHandler.getProtocolManager().removePacketListener((com.comphenix.protocol.events.PacketListener)cpm);
            iter.remove();
        }
    }

    @Override
    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        CommonPacketListener commonListener = new CommonPacketListener(plugin, listener, types);
        ProtocolLibPacketHandler.getProtocolManager().addPacketListener((com.comphenix.protocol.events.PacketListener)commonListener);
        this.listeners.add(commonListener);
    }

    @Override
    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        CommonPacketMonitor commonMonitor = new CommonPacketMonitor(plugin, monitor, types);
        ProtocolLibPacketHandler.getProtocolManager().addPacketListener((com.comphenix.protocol.events.PacketListener)commonMonitor);
        this.monitors.add(commonMonitor);
    }

    @Override
    public void transfer(PacketHandler to) {
        for (CommonPacketListener listener : this.listeners) {
            to.addPacketListener(listener.getPlugin(), listener.listener, listener.types);
        }
        for (CommonPacketMonitor monitor : this.monitors) {
            to.addPacketMonitor(monitor.getPlugin(), monitor.monitor, monitor.types);
        }
    }

    private static boolean containsSendSilentPacketBug() {
        if (!Common.IS_PAPERSPIGOT_SERVER) {
            return false;
        }
        String protocolLibVersion = Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
        int protocolLibVersionEnd = protocolLibVersion.indexOf(45);
        if (protocolLibVersionEnd != -1) {
            protocolLibVersion = protocolLibVersion.substring(0, protocolLibVersionEnd);
        }
        if (protocolLibVersion.equals("4.5.1")) {
            try {
                Class.forName("com.comphenix.protocol.injector.netty.PacketFilterQueue");
                return false;
            }
            catch (ClassNotFoundException ex) {
                return true;
            }
        }
        return TextValueSequence.evaluateText(protocolLibVersion, "<", "4.5.1");
    }

    private static com.comphenix.protocol.PacketType getPacketType(PacketType commonType) {
        return ProtocolLibPacketHandler.getPacketType(commonType.getType());
    }

    private static com.comphenix.protocol.PacketType getPacketType(Class<?> packetClass) {
        return PacketRegistry.getPacketType(packetClass);
    }

    static ProtocolManager getProtocolManager() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        if (manager == null) {
            PluginDescriptionFile pfile;
            Logging.LOGGER.log(Level.SEVERE, "Unexpected NPE: ProtocolLibrary.getProtocolManager() is returning null!");
            Logging.LOGGER.log(Level.SEVERE, "This is caused by other plugins shading in ProtocolLib");
            Plugin plugin = CommonUtil.getPluginByClass(ProtocolManager.class);
            if (plugin != null && (pfile = plugin.getDescription()) != null) {
                Logging.LOGGER.log(Level.SEVERE, "Please tell the developer(s) of " + pfile.getName() + " (" + StringUtil.combineNames(pfile.getAuthors()) + ") to fix this problem, or remove the plugin");
                Logging.LOGGER.log(Level.SEVERE, "Send the developer(s) the following information:");
                Logging.LOGGER.log(Level.SEVERE, "Add <scope>provided</scope> to the protocollib dependency in the plugin's pom.xml");
            }
            Logging.LOGGER.log(Level.SEVERE, "Please read: https://www.spigotmc.org/threads/protocollibrary-getprotocolmanager-returns-null.507594/#post-4171682");
            throw new NullPointerException("ProtocolLibrary.getProtocolManager() is null");
        }
        return manager;
    }

    static ListeningWhitelist getWhiteList(ListenerPriority priority, PacketType[] types, boolean receiving) {
        ArrayList<com.comphenix.protocol.PacketType> comTypes = new ArrayList<com.comphenix.protocol.PacketType>();
        for (PacketType type : types) {
            com.comphenix.protocol.PacketType comType;
            if (!type.isOutGoing() != receiving || type.getType() == null || (comType = ProtocolLibPacketHandler.getPacketType(type)) == null) continue;
            comTypes.add(comType);
        }
        return ListeningWhitelist.newBuilder().priority(priority).types(comTypes).gamePhase(GamePhase.PLAYING).options(new ListenerOptions[]{ListenerOptions.ASYNC}).build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean handleSendDuring(PacketEvent event, BooleanSupplier sendAction) {
        ThreadLocal<PacketEvent> tCurrentEvent = this.currentHandledEvent;
        try {
            tCurrentEvent.set(event);
            boolean bl = sendAction.getAsBoolean();
            return bl;
        }
        finally {
            tCurrentEvent.set(null);
        }
    }

    private final class SilentQueueCleanupTask
    extends Task {
        private boolean _isScheduled;

        public SilentQueueCleanupTask() {
            super(CommonPlugin.getInstance());
            this._isScheduled = false;
        }

        @Override
        public synchronized void run() {
            if (ProtocolLibPacketHandler.this.silentPacketQueueFallback.cleanup()) {
                this._isScheduled = false;
                this.stop();
            }
        }

        public synchronized void disable() {
            if (this._isScheduled) {
                this._isScheduled = false;
                this.stop();
            }
        }

        public synchronized void kick() {
            if (!this._isScheduled) {
                this.start(20L, 20L);
                this._isScheduled = true;
            }
        }
    }

    private class CommonPacketMonitor
    extends CommonPacketAdapter {
        public final PacketMonitor monitor;

        public CommonPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
            super(plugin, ListenerPriority.MONITOR, types);
            this.monitor = monitor;
        }

        public void onPacketReceiving(PacketEvent event) {
            if (this.isTemporary(event)) {
                return;
            }
            this.monitor.onMonitorPacketReceive(new CommonPacket(event.getPacket().getHandle()), event.getPlayer());
        }

        public void onPacketSending(PacketEvent event) {
            if (this.isTemporary(event)) {
                return;
            }
            ThreadLocal tCurrentEvent = ProtocolLibPacketHandler.this.currentHandledEvent;
            try {
                tCurrentEvent.set(event);
                this.monitor.onMonitorPacketSend(new CommonPacket(event.getPacket().getHandle()), event.getPlayer());
            }
            finally {
                tCurrentEvent.set(null);
            }
        }
    }

    private class CommonPacketListener
    extends CommonPacketAdapter {
        public final PacketListener listener;

        public CommonPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
            super(plugin, ListenerPriority.NORMAL, types);
            this.listener = listener;
        }

        private void swapPacket(PacketEvent event, CommonPacket commonpacket) {
            PacketContainer container = ProtocolLibPacketHandler.this.toContainer(commonpacket.getHandle());
            event.setPacket(container);
        }

        public void onPacketReceiving(PacketEvent event) {
            if (this.isTemporary(event)) {
                return;
            }
            CommonPacket packet = new CommonPacket(event.getPacket().getHandle());
            PacketReceiveEvent receiveEvent = new PacketReceiveEvent(event.getPlayer(), packet);
            receiveEvent.setCancelled(event.isCancelled());
            this.listener.onPacketReceive(receiveEvent);
            event.setCancelled(receiveEvent.isCancelled());
            if (receiveEvent.getPacket() != packet) {
                this.swapPacket(event, receiveEvent.getPacket());
            }
        }

        public void onPacketSending(PacketEvent event) {
            Object raw_packet = event.getPacket().getHandle();
            if (ProtocolLibPacketHandler.this.useSilentPacketQueue && ProtocolLibPacketHandler.this.silentPacketQueueFallback.take(event.getPlayer(), raw_packet)) {
                return;
            }
            if (this.isTemporary(event)) {
                return;
            }
            CommonPacket packet = new CommonPacket(raw_packet);
            PacketSendEvent sendEvent = new PacketSendEvent(event.getPlayer(), packet);
            sendEvent.setCancelled(event.isCancelled());
            event.setCancelled(ProtocolLibPacketHandler.this.handleSendDuring(event, () -> {
                this.listener.onPacketSend(sendEvent);
                return sendEvent.isCancelled();
            }));
            if (sendEvent.getPacket() != packet) {
                this.swapPacket(event, sendEvent.getPacket());
            }
        }
    }

    private static abstract class CommonPacketAdapter
    implements com.comphenix.protocol.events.PacketListener {
        private final Plugin plugin;
        public final PacketType[] types;
        private final ListeningWhitelist receiving;
        private final ListeningWhitelist sending;
        private final boolean eventHasIsTemporaryPlayerMethod;
        private final Class<?> temporaryPlayerClass;

        public CommonPacketAdapter(Plugin plugin, ListenerPriority priority, PacketType[] types) {
            this.plugin = plugin;
            this.types = types;
            this.receiving = ProtocolLibPacketHandler.getWhiteList(priority, types, true);
            this.sending = ProtocolLibPacketHandler.getWhiteList(priority, types, false);
            boolean hasIsTemporaryPlayerMethod = false;
            Class tempPlayerClass = String.class;
            try {
                PacketEvent.class.getDeclaredMethod("isPlayerTemporary", new Class[0]);
                hasIsTemporaryPlayerMethod = true;
            }
            catch (NoSuchMethodException ex) {
                try {
                    tempPlayerClass = Class.forName("com.comphenix.protocol.injector.temporary.TemporaryPlayer");
                }
                catch (Throwable t1) {
                    try {
                        tempPlayerClass = Class.forName("com.comphenix.protocol.injector.server.TemporaryPlayer");
                    }
                    catch (Throwable t2) {
                        Logging.LOGGER_NETWORK.warning("Failed to find ProtocolLib TemporaryPlayer class!");
                    }
                }
            }
            this.eventHasIsTemporaryPlayerMethod = hasIsTemporaryPlayerMethod;
            this.temporaryPlayerClass = tempPlayerClass;
        }

        protected boolean isTemporary(PacketEvent event) {
            if (this.eventHasIsTemporaryPlayerMethod) {
                return CommonPacketAdapter.isEventPlayerTemporary(event);
            }
            Player player = event.getPlayer();
            return player == null || this.temporaryPlayerClass.isAssignableFrom(player.getClass());
        }

        private static boolean isEventPlayerTemporary(PacketEvent event) {
            return event.isPlayerTemporary();
        }

        public Plugin getPlugin() {
            return this.plugin;
        }

        public ListeningWhitelist getReceivingWhitelist() {
            return this.receiving;
        }

        public ListeningWhitelist getSendingWhitelist() {
            return this.sending;
        }
    }
}

