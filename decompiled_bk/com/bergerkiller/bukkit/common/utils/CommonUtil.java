/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.RegisteredListener
 *  org.bukkit.plugin.SimplePluginManager
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.StackTraceFilter;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonNextTickExecutor;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.reflection.org.bukkit.BHandlerList;
import com.bergerkiller.reflection.org.bukkit.BSimplePluginManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;

public class CommonUtil {
    public static final int VIEW;
    public static final int VIEWWIDTH;
    public static final int CHUNKAREA;
    public static final int BLOCKVIEW;
    private static final boolean IS_PAPER_PLUGIN_MANAGER;
    public static final Thread MAIN_THREAD;
    private static final Map<String, Class<?>> classMap;

    @Deprecated
    public static void bootstrap() {
        CommonBootstrap.initCommonServerAssertCompatibility();
    }

    public static boolean isShuttingDown() {
        return MinecraftServerHandle.instance().isHasStopped();
    }

    public static int getServerTicks() {
        return MinecraftServerHandle.instance().getTicks();
    }

    public static CommandMap getCommandMap() {
        return (CommandMap)CraftServerHandle.T.getCommandMap.invoke(Bukkit.getServer());
    }

    public static void sendMessage(Object sender, Object message) {
        String msg;
        if (message != null && (msg = message.toString()).length() > 0 && sender instanceof CommandSender) {
            if (!(sender instanceof Player)) {
                message = ChatColor.stripColor((String)msg);
            }
            for (String line : msg.split("\n", -1)) {
                ((CommandSender)sender).sendMessage(line);
            }
        }
    }

    public static void sendListMessage(Object sender, String delimiter, Object[] items) {
        String msgpart = null;
        for (Object oitem : items) {
            String item = oitem.toString();
            if (msgpart == null || msgpart.length() + item.length() < 70) {
                if (msgpart == null) {
                    msgpart = item;
                    continue;
                }
                msgpart = msgpart + ChatColor.WHITE + delimiter + item;
                continue;
            }
            CommonUtil.sendMessage(sender, msgpart);
            msgpart = item;
        }
        CommonUtil.sendMessage(sender, msgpart);
    }

    public static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static boolean hasHandlers(HandlerList handlerList) {
        return handlerList.getRegisteredListeners().length > 0;
    }

    public static void savePlayer(HumanEntity human) {
        if (human instanceof Player) {
            CommonNMS.getPlayerList().savePlayerFile((Player)human);
        }
    }

    public static void savePlayers() {
        CommonNMS.getPlayerList().savePlayers();
    }

    public static <T> void shuffle(T[] array) {
        for (int i = 1; i < array.length; ++i) {
            int random = (int)(Math.random() * (double)i);
            T temp = array[i - 1];
            array[i - 1] = array[random];
            array[random] = temp;
        }
    }

    public static Set addAllToSet(Set to, Set from) {
        for (Object i : from) {
            to.add(i);
        }
        return to;
    }

    public static Collection<Player> getOnlinePlayers() {
        return (Collection)LogicUtil.unsafeCast(Bukkit.getOnlinePlayers());
    }

    public static Collection<Player> getOnlinePlayers(Predicate<Player> filter) {
        Collection allPlayers = Bukkit.getOnlinePlayers();
        ArrayList<Player> players = new ArrayList<Player>(allPlayers.size());
        for (Player player : allPlayers) {
            if (!filter.test(player)) continue;
            players.add(player);
        }
        return Collections.unmodifiableCollection(players);
    }

    public static boolean hasPermission(CommandSender sender, String[] permissionNode) {
        return CommonPlugin.getInstance().getPermissionHandler().hasPermission(sender, permissionNode);
    }

    public static GameProfileHandle getGameProfile(String name) {
        Player player = Bukkit.getPlayer((String)name);
        if (player != null) {
            return PlayerUtil.getGameProfile(player);
        }
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        return GameProfileHandle.createNew(uuid, name);
    }

    public static boolean hasPermission(CommandSender sender, String permissionNode) {
        return CommonPlugin.getInstance().getPermissionHandler().hasPermission(sender, permissionNode);
    }

    public static void printFilteredStackTrace(Throwable error) {
        StackTraceFilter.SERVER.print(error);
    }

    public static void printFilteredStackTrace(Throwable error, Level level) {
        StackTraceFilter.SERVER.print(error, level);
    }

    public static StackTraceElement[] filterStackTrace(StackTraceElement[] elements, String className, String methodName) {
        StackTraceElement elem;
        if (elements == null || elements.length == 0) {
            return new StackTraceElement[0];
        }
        ArrayList<StackTraceElement> rval = new ArrayList<StackTraceElement>(elements.length - 1);
        StackTraceElement[] stackTraceElementArray = elements;
        int n = stackTraceElementArray.length;
        for (int i = 0; !(i >= n || (elem = stackTraceElementArray[i]).getClassName().equals(className) && elem.getMethodName().equals(methodName)); ++i) {
            rval.add(elem);
        }
        return rval.toArray(new StackTraceElement[0]);
    }

    public static boolean isPluginInDirectory(String name) {
        File dir = new File("plugins");
        if (!dir.exists()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            try {
                BasicConfiguration config = CommonUtil.getPluginConfiguration(file, "plugin.yml");
                String pluginName = (String)((Object)config.get("name", String.class));
                if (!name.equals(pluginName)) continue;
                return true;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BasicConfiguration getPluginConfiguration(File pluginJarFile, String configResourcePath) throws IOException {
        try (InputStream stream = CommonUtil.getPluginResource(pluginJarFile, configResourcePath);){
            BasicConfiguration config = new BasicConfiguration();
            try {
                config.loadFromStream(stream);
            }
            catch (Throwable t) {
                throw new IOException("Error in YAML format", t);
            }
            BasicConfiguration basicConfiguration = config;
            return basicConfiguration;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static InputStream getPluginResource(File pluginJarFile, String resourcePath) throws IOException {
        PluginManager pluginManager = Bukkit.getPluginManager();
        synchronized (pluginManager) {
            for (Plugin plugin : CommonUtil.getPluginsUnsafe()) {
                File file = CommonUtil.getPluginJarFile(plugin);
                if (!pluginJarFile.equals(file)) continue;
                InputStream stream = plugin.getResource(resourcePath);
                if (stream == null) {
                    throw new IOException("Resource not found: " + resourcePath);
                }
                return stream;
            }
        }
        JarFile jarFile = new JarFile(pluginJarFile);
        ZipEntry entry = jarFile.getEntry(resourcePath);
        if (entry == null) {
            jarFile.close();
            throw new IOException("Resource not found: " + resourcePath);
        }
        return jarFile.getInputStream(entry);
    }

    public static File getPluginJarFile(Plugin plugin) {
        Class pluginClass = plugin.getClass();
        try {
            URI uri = pluginClass.getProtectionDomain().getCodeSource().getLocation().toURI();
            File file = new File(uri);
            if (file.exists()) {
                return file;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static File getPluginDataFolder(Plugin plugin) {
        File folder = plugin.getDataFolder();
        if (folder == null) {
            File jarFile = CommonUtil.getPluginJarFile(plugin);
            if (jarFile == null) {
                throw new RuntimeException("Plugin data folder can not be obtained: Not a valid JAR plugin");
            }
            folder = new File(jarFile.getAbsoluteFile().getParentFile(), plugin.getName());
        }
        return folder;
    }

    public static <T extends Throwable> T filterStackTrace(T error, String className, String methodName) {
        error.setStackTrace(CommonUtil.filterStackTrace(error.getStackTrace(), className, methodName));
        return error;
    }

    public static StackTraceElement[] filterStackTrace(StackTraceElement[] elements) {
        StackTraceElement[] currStack = Thread.currentThread().getStackTrace();
        for (int i = 1; i < currStack.length; ++i) {
            if (currStack[i].getClassName().equals(CommonUtil.class.getName()) && currStack[i].getMethodName().equals("filterStackTrace")) continue;
            return CommonUtil.filterStackTrace(elements, currStack[i + 1].getClassName(), currStack[i + 1].getMethodName());
        }
        return elements;
    }

    public static <T extends Throwable> T filterStackTrace(T error) {
        error.setStackTrace(CommonUtil.filterStackTrace(error.getStackTrace()));
        return error;
    }

    @Deprecated
    public static <T> T unsafeCast(Object value) {
        return (T)value;
    }

    @Deprecated
    public static <T> T tryCast(Object object, Class<T> type) {
        return CommonUtil.tryCast(object, type, null);
    }

    @Deprecated
    public static <T> T tryCast(Object object, Class<T> type, T def) {
        if (type.isInstance(object)) {
            return type.cast(object);
        }
        return def;
    }

    public static void syncTick(Runnable runnable) {
        try {
            CommonUtil.runAsyncMainThread(runnable).get();
        }
        catch (InterruptedException interruptedException) {
        }
        catch (ExecutionException e) {
            throw MountiplexUtil.uncheckedRethrow(e.getCause());
        }
    }

    public static void nextTick(Runnable runnable) {
        CommonNextTickExecutor.INSTANCE.execute(runnable);
    }

    public static Executor getNextTickExecutor() {
        return CommonNextTickExecutor.INSTANCE;
    }

    public static Executor getMainThreadExecutor() {
        return CommonNextTickExecutor.MAIN_THREAD;
    }

    public static Executor getPluginExecutor(Plugin plugin) {
        return task -> {
            if (!plugin.isEnabled()) {
                Logging.LOGGER.warning("Failed to execute task for plugin " + plugin.getName() + " because plugin is disabled");
            } else if (plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task) == -1) {
                Logging.LOGGER.warning("Failed to execute task for plugin " + plugin.getName() + " because scheduling failed");
            }
        };
    }

    public static Executor getPluginAsyncExecutor(Plugin plugin) {
        return task -> {
            if (!plugin.isEnabled()) {
                Logging.LOGGER.warning("Failed to execute asynchronous task for plugin " + plugin.getName() + " because plugin is disabled");
            } else {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
            }
        };
    }

    public static CompletableFuture<Void> runAsyncMainThread(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, CommonNextTickExecutor.MAIN_THREAD);
    }

    public static CompletableFuture<Void> runCheckedAsync(CheckedRunnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
                future.complete(null);
            }
            catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public static CompletableFuture<Void> runCheckedAsync(CheckedRunnable runnable, Executor executor) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
                future.complete(null);
            }
            catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }, executor);
        return future;
    }

    public static void broadcast(Object message) {
        if (message != null) {
            for (Player player : CommonUtil.getOnlinePlayers()) {
                player.sendMessage(message.toString());
            }
        }
    }

    public static Collection<Plugin> getPluginsUnsafe() {
        PluginManager man = Bukkit.getPluginManager();
        if (!IS_PAPER_PLUGIN_MANAGER && man instanceof SimplePluginManager) {
            return BSimplePluginManager.plugins.get(man);
        }
        return Arrays.asList(man.getPlugins());
    }

    public static Plugin[] getPlugins() {
        return Bukkit.getPluginManager().getPlugins();
    }

    public static boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    public static Plugin getPlugin(String name) {
        if (CommonBootstrap.isTestMode()) {
            return null;
        }
        return Bukkit.getPluginManager().getPlugin(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Plugin getPluginByClass(Class<?> clazz) {
        if (CommonBootstrap.isTestMode()) {
            return null;
        }
        ClassLoader loader = clazz.getClassLoader();
        if (IS_PAPER_PLUGIN_MANAGER) {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin.getClass().getClassLoader() != loader) continue;
                return plugin;
            }
        } else {
            PluginManager pluginManager = Bukkit.getServer().getPluginManager();
            synchronized (pluginManager) {
                for (Plugin plugin : CommonUtil.getPluginsUnsafe()) {
                    if (plugin.getClass().getClassLoader() != loader) continue;
                    return plugin;
                }
            }
        }
        return null;
    }

    public static Plugin getPluginByClass(String classPath) {
        try {
            return CommonUtil.getPluginByClass(Class.forName(classPath));
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static String getPackagePath(String classPath) {
        int idx = classPath.lastIndexOf(46);
        return idx == -1 ? "" : classPath.substring(0, idx);
    }

    public static Plugin[] findPlugins(StackTraceElement[] stackTrace) {
        return CommonUtil.findPlugins(Arrays.asList(stackTrace));
    }

    public static Plugin[] findPlugins(List<StackTraceElement> stackTrace) {
        LinkedHashSet<Plugin> found = new LinkedHashSet<Plugin>(3);
        for (StackTraceElement elem : stackTrace) {
            Plugin plugin = CommonUtil.getPluginByClass(elem.getClassName());
            if (plugin == null) continue;
            found.add(plugin);
        }
        return found.toArray(new Plugin[0]);
    }

    public static void loadClass(Class<?> type) {
        CommonUtil.getClass(type.getName(), true);
        for (Class<?> superClass : type.getDeclaredClasses()) {
            CommonUtil.getClass(superClass.getName(), true);
        }
    }

    public static Class<?> getClass(String path) {
        return CommonUtil.getClass(path, true);
    }

    public static Class<?> getClass(String path, boolean initialize) {
        return Resolver.loadClass(path, initialize);
    }

    public static boolean isSoftDepending(Plugin plugin, Plugin depending) {
        List dep = plugin.getDescription().getSoftDepend();
        return !LogicUtil.nullOrEmpty(dep) && dep.contains(depending.getName());
    }

    public static boolean isDepending(Plugin plugin, Plugin depending) {
        List dep = plugin.getDescription().getDepend();
        return !LogicUtil.nullOrEmpty(dep) && dep.contains(depending.getName());
    }

    public static <T> T[] getClassConstants(Class<T> theClass) {
        if (theClass.isEnum()) {
            return CommonUtil.getEnumClassConstants(theClass, theClass);
        }
        Class<T> superType = theClass.getSuperclass();
        if (superType != null && superType.isEnum()) {
            return CommonUtil.getEnumClassConstants(superType, superType);
        }
        return CommonUtil.getStaticFieldConstants(theClass, theClass);
    }

    public static <T> T[] getClassConstants(Class<?> theClass, Class<T> type) {
        Class<?> superType;
        if (theClass.isEnum() && type.isAssignableFrom(theClass)) {
            return CommonUtil.getEnumClassConstants(theClass, type);
        }
        if (!theClass.isEnum() && (superType = theClass.getSuperclass()) != null && superType.isEnum() && type.isAssignableFrom(superType)) {
            return CommonUtil.getEnumClassConstants(superType, type);
        }
        return CommonUtil.getStaticFieldConstants(theClass, type);
    }

    private static <T> T[] getEnumClassConstants(Class<?> enumClass, Class<T> typeToReturn) {
        if (enumClass.equals(typeToReturn)) {
            return enumClass.getEnumConstants();
        }
        ?[] constants = enumClass.getEnumConstants();
        T[] result = LogicUtil.createArray(typeToReturn, constants.length);
        System.arraycopy(constants, 0, result, 0, constants.length);
        return result;
    }

    private static <T> T[] getStaticFieldConstants(Class<?> theClass, Class<T> typeToReturn) {
        try {
            Field[] declaredFields = theClass.getDeclaredFields();
            ArrayList<Object> constants = new ArrayList<Object>(declaredFields.length);
            for (Field field : declaredFields) {
                Object constant;
                if (!Modifier.isStatic(field.getModifiers()) || !typeToReturn.isAssignableFrom(field.getType()) || (constant = field.get(null)) == null) continue;
                constants.add(constant);
            }
            return LogicUtil.toArray(constants, typeToReturn);
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.WARNING, "Failed to find class constants of " + theClass, t);
            return LogicUtil.createArray(typeToReturn, 0);
        }
    }

    public static boolean isInstance(Object value, Class<?> ... types) {
        for (Class<?> type : types) {
            if (!type.isInstance(value)) continue;
            return true;
        }
        return false;
    }

    public static boolean isServerStarted() {
        return CommonPlugin.hasInstance() && CommonPlugin.getInstance().isServerStarted();
    }

    public static HandlerList getEventHandlerList(Class<?> eventClass) {
        for (Class<?> classWithHandlerList = eventClass; classWithHandlerList != null && Event.class.isAssignableFrom(classWithHandlerList); classWithHandlerList = classWithHandlerList.getSuperclass()) {
            try {
                return (HandlerList)classWithHandlerList.getDeclaredMethod("getHandlerList", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Throwable throwable) {
                continue;
            }
        }
        throw new RuntimeException("Class '" + eventClass.getName() + "' does not have a handler list");
    }

    public static void unregisterListener(Listener listener) {
        HashSet eventTypes = new HashSet();
        HashSet<Method> methods = new HashSet<Method>();
        methods.addAll(Arrays.asList(listener.getClass().getMethods()));
        methods.addAll(Arrays.asList(listener.getClass().getDeclaredMethods()));
        for (Method method : methods) {
            Class<?>[] params;
            if (method.getAnnotation(EventHandler.class) == null || (params = method.getParameterTypes()).length != 1 || !Event.class.isAssignableFrom(params[0])) continue;
            eventTypes.add(params[0]);
        }
        for (Class clazz : eventTypes) {
            CommonUtil.getEventHandlerList(clazz).unregister(listener);
        }
    }

    public static void queueListenerLast(Listener listener, Class<?> eventClass) {
        CommonUtil.setListenerOrder(listener, eventClass, false);
    }

    public static void queueListenerFirst(Listener listener, Class<?> eventClass) {
        CommonUtil.setListenerOrder(listener, eventClass, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void setListenerOrder(Listener listener, Class<?> eventClass, boolean first) {
        HandlerList list = CommonUtil.getEventHandlerList(eventClass);
        EventPriority prio = first ? EventPriority.LOWEST : EventPriority.MONITOR;
        HandlerList handlerList = list;
        synchronized (handlerList) {
            EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerSlots = BHandlerList.handlerslots.get(list);
            ArrayList<RegisteredListener> registeredListenerList = handlerSlots.get(prio);
            int requestedIndex = first ? 0 : registeredListenerList.size() - 1;
            for (int i = 0; i < registeredListenerList.size(); ++i) {
                RegisteredListener registeredListener = registeredListenerList.get(i);
                if (registeredListener.getListener() != listener) continue;
                if (i == requestedIndex) {
                    return;
                }
                RegisteredListener[] allListeners = list.getRegisteredListeners();
                registeredListenerList.remove(i);
                registeredListenerList.add(requestedIndex, registeredListener);
                ArrayList<RegisteredListener> newListeners = new ArrayList<RegisteredListener>(allListeners.length);
                if (first) {
                    newListeners.add(registeredListener);
                    for (RegisteredListener otherListener : allListeners) {
                        if (otherListener == registeredListener) continue;
                        newListeners.add(otherListener);
                    }
                } else {
                    for (RegisteredListener otherListener : allListeners) {
                        if (otherListener == registeredListener) continue;
                        newListeners.add(otherListener);
                    }
                    newListeners.add(registeredListener);
                }
                if (newListeners.toArray(allListeners) != allListeners) {
                    list.bake();
                }
                return;
            }
        }
    }

    public static boolean isMethodOverrided(Class<?> baseClass, Object typeInstance, String methodName, Class<?> ... parameterTypes) {
        return CommonUtil.isMethodOverrided(baseClass, typeInstance.getClass(), methodName, parameterTypes);
    }

    public static boolean isMethodOverrided(Class<?> baseClass, Class<?> type, String methodName, Class<?> ... parameterTypes) {
        if (type == null || !baseClass.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Type " + type + " is not a class extending base " + baseClass);
        }
        SafeMethod method = new SafeMethod(baseClass, methodName, parameterTypes);
        while (type != baseClass && type != null) {
            if (method.isOverridedIn(type)) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    public static boolean isMainThread() {
        Class<?> threadClass;
        if (MinecraftServerHandle.instance().isMainThread()) {
            return true;
        }
        return CommonBootstrap.isSpigotServer() && (threadClass = Thread.currentThread().getClass()).getName().equals("org.spigotmc.WatchdogThread");
    }

    public static String getServerProperty(String key, String defaultValue) {
        return MinecraftServerHandle.instance().getProperty(key, defaultValue);
    }

    public static void handlePostDisable(Plugin plugin) {
        Server server = Bukkit.getServer();
        try {
            server.getScheduler().cancelTasks(plugin);
        }
        catch (Throwable ex) {
            server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
        try {
            server.getServicesManager().unregisterAll(plugin);
        }
        catch (Throwable ex) {
            server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
        try {
            HandlerList.unregisterAll((Plugin)plugin);
        }
        catch (Throwable ex) {
            server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
        try {
            server.getMessenger().unregisterIncomingPluginChannel(plugin);
            server.getMessenger().unregisterOutgoingPluginChannel(plugin);
        }
        catch (Throwable ex) {
            server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
        Method removePluginChunkTicketsMethod = null;
        try {
            removePluginChunkTicketsMethod = World.class.getMethod("removePluginChunkTickets", Plugin.class);
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
        if (removePluginChunkTicketsMethod != null) {
            try {
                for (World world : server.getWorlds()) {
                    removePluginChunkTicketsMethod.invoke((Object)world, plugin);
                }
            }
            catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while removing chunk tickets for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
        }
    }

    static {
        MAIN_THREAD = Thread.currentThread();
        classMap = new HashMap();
        VIEW = Bukkit.getServer() == null ? 5 : Bukkit.getServer().getViewDistance();
        VIEWWIDTH = VIEW + VIEW + 1;
        CHUNKAREA = VIEWWIDTH * VIEWWIDTH;
        BLOCKVIEW = 32 + (VIEW << 4);
        classMap.put("double", Double.TYPE);
        boolean isPaperPluginManager = false;
        try {
            SimplePluginManager.class.getDeclaredField("paperPluginManager");
            isPaperPluginManager = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        IS_PAPER_PLUGIN_MANAGER = isPaperPluginManager;
    }
}

