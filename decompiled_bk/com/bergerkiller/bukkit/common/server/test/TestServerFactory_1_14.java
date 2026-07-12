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
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Bukkit;

class TestServerFactory_1_14
extends TestServerFactory {
    TestServerFactory_1_14() {
    }

    @Override
    protected String detectNMSRoot() throws Throwable {
        String cb_root = this.detectCBRoot();
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf(46));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = TestServerFactory_1_14.getPackagePath(f.getType());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        nms_root = nms_root + ".";
        return nms_root;
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        Class<?> dispenserRegistryClass = TestServerFactory_1_14.resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("init", new Class[0]);
        dispenserRegistryBootstrapMethod.invoke(null, new Object[0]);
        ClassTemplate<? extends Bukkit> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Bukkit server = server_t.newInstanceNull();
        Class<?> minecraftServerType = TestServerFactory_1_14.resolveClass("net.minecraft.server.MinecraftServer");
        Class<?> dedicatedType = TestServerFactory_1_14.resolveClass("net.minecraft.server.dedicated.DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();
        env.mc_server = mc_server;
        Class<?> iAsyncTaskHandlerClass = TestServerFactory_1_14.resolveClass("net.minecraft.util.thread.BlockableEventLoop");
        TestServerFactory_1_14.setField(mc_server, iAsyncTaskHandlerClass, "b", "Server");
        TestServerFactory_1_14.setField(mc_server, iAsyncTaskHandlerClass, "d", TestServerFactory_1_14.createFromCode(minecraftServerType, "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));
        Class<?> gameProfileRepositoryType = TestServerFactory_1_14.resolveClass("com.mojang.authlib.GameProfileRepository");
        TestServerFactory_1_14.setField(mc_server, minecraftServerType, "gameProfileRepository", Proxy.newProxyInstance(TestServerFactory.class.getClassLoader(), new Class[]{gameProfileRepositoryType}, (proxy, method, args) -> null));
        TestServerFactory_1_14.setField(mc_server, minecraftServerType, "processQueue", new ConcurrentLinkedQueue());
        TestServerFactory_1_14.setField(server, "logger", MountiplexUtil.LOGGER);
        TestServerFactory_1_14.setField(server, "console", mc_server);
        TestServerFactory_1_14.setField(mc_server, "serverThread", Thread.currentThread());
        TestServerFactory_1_14.setField(mc_server, "worldServer", Collections.emptyMap());
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);
        String propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
        TestServerFactory_1_14.setField(mc_server, "propertyManager", propertyManager);
        TestServerFactory_1_14.setField(propertyManager, "properties", TestServerFactory_1_14.createFromCode(TestServerFactory_1_14.resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "return new net.minecraft.server.dedicated.DedicatedServerProperties(    new java.util.Properties(),    new joptsimple.OptionParser().parse(new String[0]));"));
        Class<?> dataConverterRegistryClass = TestServerFactory_1_14.resolveClass("net.minecraft.util.datafix.DataFixers");
        Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a", new Class[0]);
        Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null, new Object[0]);
        TestServerFactory_1_14.setField(mc_server, "dataConverterManager", dataConverterManager);
        minecraftServerType.getDeclaredMethod("getCraftingManager", new Class[0]);
        TestServerFactory_1_14.setField(mc_server, "executorService", TestServerFactory_1_14.createFromCode(minecraftServerType, "return net.minecraft.util.Util.e();"));
        String fieldname = CommonBootstrap.evaluateMCVersion(">=", "1.14.4") ? "ae" : (CommonBootstrap.evaluateMCVersion(">=", "1.14.3") ? "ad" : "ae");
        TestServerFactory_1_14.setField(mc_server, fieldname, TestServerFactory_1_14.createFromCode(minecraftServerType, "return new net.minecraft.server.packs.resources.SimpleReloadableResourceManager(  net.minecraft.server.packs.PackType.SERVER_DATA,  java.lang.Thread.currentThread());"));
        FastMethod<Object> loaderCreator = TestServerFactory_1_14.compileCode(minecraftServerType, "public static Object create(Object args_t) {  Object[] args = (Object[]) args_t;  return new net.minecraft.server.packs.repository.Pack(     (String) args[0],     ((Boolean) args[1]).booleanValue(),      (java.util.function.Supplier) args[2],      (net.minecraft.server.packs.PackResources) args[3],      (net.minecraft.server.packs.metadata.pack.PackMetadataSection) args[4],      (net.minecraft.server.packs.repository.Pack$Position) args[5]);}");
        Class<?> resourcePackLoaderFuncType = TestServerFactory_1_14.resolveClass("net.minecraft.server.packs.repository.Pack$PackConstructor");
        Object resourcePackLoaderFunc = Proxy.newProxyInstance(TestServerFactory.class.getClassLoader(), new Class[]{resourcePackLoaderFuncType}, (proxy, method, args) -> loaderCreator.invoke(null, args));
        Class<?> resourcePackRepositoryType = TestServerFactory_1_14.resolveClass("net.minecraft.server.packs.repository.PackRepository");
        TestServerFactory_1_14.setField(mc_server, "resourcePackRepository", TestServerFactory_1_14.construct(resourcePackRepositoryType, resourcePackLoaderFunc));
        Class<?> recipeManagerType = TestServerFactory_1_14.resolveClass("net.minecraft.world.item.crafting.RecipeManager");
        Class<?> tagManagerType = TestServerFactory_1_14.resolveClass("net.minecraft.tags.TagManager");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
            TestServerFactory_1_14.setField(mc_server, "craftingManager", recipeManagerType.newInstance());
            TestServerFactory_1_14.setField(mc_server, "tagRegistry", tagManagerType.newInstance());
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.4")) {
            TestServerFactory_1_14.setField(mc_server, "ai", recipeManagerType.newInstance());
            TestServerFactory_1_14.setField(mc_server, "aj", tagManagerType.newInstance());
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.14.3")) {
            TestServerFactory_1_14.setField(mc_server, "ah", recipeManagerType.newInstance());
            TestServerFactory_1_14.setField(mc_server, "ai", tagManagerType.newInstance());
        } else {
            TestServerFactory_1_14.setField(mc_server, "ai", recipeManagerType.newInstance());
            TestServerFactory_1_14.setField(mc_server, "aj", tagManagerType.newInstance());
        }
        TestServerFactory_1_14.compileCode(minecraftServerType, "public void register() {  instance.getResourceManager().a(instance.getTagRegistry());}").invoke(mc_server);
        TestServerFactory_1_14.compileCode(minecraftServerType, "public void register() {  instance.getResourceManager().a(instance.getCraftingManager());}").invoke(mc_server);
        File serverDir = new File(System.getProperty("user.dir"), "target");
        Class<?> worldDataType = TestServerFactory_1_14.resolveClass("net.minecraft.world.level.storage.LevelData");
        Constructor<?> con = worldDataType.getDeclaredConstructor(new Class[0]);
        con.setAccessible(true);
        Object worldData = con.newInstance(new Object[0]);
        Method m = minecraftServerType.getDeclaredMethod("a", File.class, worldDataType);
        m.setAccessible(true);
        m.invoke(mc_server, serverDir, worldData);
    }
}

