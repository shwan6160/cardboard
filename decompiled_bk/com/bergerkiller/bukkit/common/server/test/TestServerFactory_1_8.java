/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Properties;
import org.bukkit.Bukkit;

class TestServerFactory_1_8
extends TestServerFactory {
    TestServerFactory_1_8() {
    }

    @Override
    protected String detectNMSRoot() throws Throwable {
        String cb_root = this.detectCBRoot();
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf(46));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = TestServerFactory_1_8.getPackagePath(f.getType());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        nms_root = nms_root + ".";
        return nms_root;
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        try {
            Class<?> spigotConfigClass = Class.forName("org.spigotmc.SpigotConfig");
            Field f = spigotConfigClass.getDeclaredField("disabledAdvancements");
            f.setAccessible(true);
            f.set(null, Collections.emptyList());
        }
        catch (Throwable spigotConfigClass) {
            // empty catch block
        }
        Class<?> dispenserRegistryClass = TestServerFactory_1_8.resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("c", new Class[0]);
        dispenserRegistryBootstrapMethod.invoke(null, new Object[0]);
        Class<?> dataConverterRegistryClass = null;
        Object dataConverterManager = null;
        try {
            dataConverterRegistryClass = TestServerFactory_1_8.resolveClass("net.minecraft.util.datafix.DataFixers");
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        if (dataConverterRegistryClass != null) {
            Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a", new Class[0]);
            dataConverterManager = dataConverterRegistryInitMethod.invoke(null, new Object[0]);
        }
        ClassTemplate<? extends Bukkit> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Bukkit server = server_t.newInstanceNull();
        Class<?> minecraftServerType = TestServerFactory_1_8.resolveClass("net.minecraft.server.MinecraftServer");
        Class<?> dedicatedType = TestServerFactory_1_8.resolveClass("net.minecraft.server.dedicated.DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();
        env.mc_server = mc_server;
        TestServerFactory_1_8.setField(server, "logger", MountiplexUtil.LOGGER);
        TestServerFactory_1_8.setField(server, "console", mc_server);
        TestServerFactory_1_8.setField(mc_server, "primaryThread", Thread.currentThread());
        TestServerFactory_1_8.setField(mc_server, "serverThread", Thread.currentThread());
        if (CommonBootstrap.evaluateMCVersion(">=", "1.13.1")) {
            TestServerFactory_1_8.setField(mc_server, "worldServer", Collections.emptyMap());
        } else {
            Class<?> worldServerType = TestServerFactory_1_8.resolveClass("net.minecraft.server.level.ServerLevel", false);
            TestServerFactory_1_8.setField(mc_server, "worldServer", LogicUtil.createArray(worldServerType, 0));
            TestServerFactory_1_8.setField(mc_server, "worlds", Collections.emptyList());
        }
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);
        String propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.Settings").newInstanceNull();
        TestServerFactory_1_8.setField(mc_server, "propertyManager", propertyManager);
        TestServerFactory_1_8.setField(propertyManager, "properties", new Properties());
        if (dataConverterManager != null) {
            TestServerFactory_1_8.setField(mc_server, "dataConverterManager", dataConverterManager);
        }
        boolean hasLocalCraftingManager = false;
        try {
            minecraftServerType.getDeclaredMethod("getCraftingManager", new Class[0]);
            hasLocalCraftingManager = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (hasLocalCraftingManager) {
            TestServerFactory_1_8.setField(mc_server, "ac", TestServerFactory_1_8.createFromCode(minecraftServerType, "return new ResourceManager(net.minecraft.server.packs.PackType.SERVER_DATA);"));
            FastMethod<Object> loaderCreator = TestServerFactory_1_8.compileCode(minecraftServerType, "public static Object create(Object args_t) {  Object[] args = (Object[]) args_t;  return new ResourcePackLoader(     (String) args[0],     ((Boolean) args[1]).booleanValue(),      (java.util.function.Supplier) args[2],      (IResourcePack) args[3],      (ResourcePackInfo) args[4],      (ResourcePackLoader$Position) args[5]);}");
            Class<?> resourcePackLoaderFuncType = TestServerFactory_1_8.resolveClass("net.minecraft.server.packs.repository.Pack$PackConstructor");
            Object resourcePackLoaderFunc = Proxy.newProxyInstance(TestServerFactory.class.getClassLoader(), new Class[]{resourcePackLoaderFuncType}, (proxy, method, args) -> loaderCreator.invoke(null, args));
            Class<?> resourcePackRepositoryType = TestServerFactory_1_8.resolveClass("net.minecraft.server.packs.repository.PackRepository");
            TestServerFactory_1_8.setField(mc_server, "resourcePackRepository", TestServerFactory_1_8.construct(resourcePackRepositoryType, resourcePackLoaderFunc));
            Class<?> craftingManagerType = TestServerFactory_1_8.resolveClass("net.minecraft.world.item.crafting.RecipeManager");
            TestServerFactory_1_8.setField(mc_server, "ag", craftingManagerType.newInstance());
            craftingManagerType = TestServerFactory_1_8.resolveClass("net.minecraft.tags.TagManager");
            TestServerFactory_1_8.setField(mc_server, "ah", craftingManagerType.newInstance());
            TestServerFactory_1_8.compileCode(minecraftServerType, "public void register() {  instance.getResourceManager().a(instance.getTagRegistry());}").invoke(mc_server);
            TestServerFactory_1_8.compileCode(minecraftServerType, "public void register() {  instance.getResourceManager().a(instance.getCraftingManager());}").invoke(mc_server);
            File serverDir = new File(System.getProperty("user.dir"), "target");
            Class<?> worldDataType = TestServerFactory_1_8.resolveClass("net.minecraft.world.level.storage.LevelData");
            Constructor<?> con = worldDataType.getDeclaredConstructor(new Class[0]);
            con.setAccessible(true);
            Object worldData = con.newInstance(new Object[0]);
            Method m = minecraftServerType.getDeclaredMethod("a", File.class, worldDataType);
            m.setAccessible(true);
            m.invoke(mc_server, serverDir, worldData);
        }
    }
}

