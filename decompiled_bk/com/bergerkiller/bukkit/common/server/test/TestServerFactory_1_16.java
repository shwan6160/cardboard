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
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import org.bukkit.Bukkit;

class TestServerFactory_1_16
extends TestServerFactory {
    TestServerFactory_1_16() {
    }

    @Override
    protected String detectNMSRoot() throws Throwable {
        String cb_root = this.detectCBRoot();
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf(46));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = TestServerFactory_1_16.getPackagePath(f.getType());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        nms_root = nms_root + ".";
        return nms_root;
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        Class<?> dispenserRegistryClass = TestServerFactory_1_16.resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("init", new Class[0]);
        dispenserRegistryBootstrapMethod.invoke(null, new Object[0]);
        ClassTemplate<? extends Bukkit> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Bukkit server = server_t.newInstanceNull();
        Class<?> minecraftServerType = TestServerFactory_1_16.resolveClass("net.minecraft.server.MinecraftServer");
        Class<?> dedicatedType = TestServerFactory_1_16.resolveClass("net.minecraft.server.dedicated.DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();
        env.mc_server = mc_server;
        Class<?> iAsyncTaskHandlerClass = TestServerFactory_1_16.resolveClass("net.minecraft.util.thread.BlockableEventLoop");
        TestServerFactory_1_16.setField(mc_server, iAsyncTaskHandlerClass, "b", "Server");
        TestServerFactory_1_16.setField(mc_server, iAsyncTaskHandlerClass, "d", TestServerFactory_1_16.createFromCode(minecraftServerType, "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));
        Class<?> gameProfileRepositoryType = TestServerFactory_1_16.resolveClass("com.mojang.authlib.GameProfileRepository");
        TestServerFactory_1_16.setField(mc_server, minecraftServerType, "gameProfileRepository", Proxy.newProxyInstance(TestServerFactory.class.getClassLoader(), new Class[]{gameProfileRepositoryType}, (proxy, method, args) -> null));
        TestServerFactory_1_16.setField(mc_server, minecraftServerType, "processQueue", new ConcurrentLinkedQueue());
        TestServerFactory_1_16.setField(server, "logger", MountiplexUtil.LOGGER);
        TestServerFactory_1_16.setField(server, "console", mc_server);
        TestServerFactory_1_16.setField(mc_server, "serverThread", Thread.currentThread());
        TestServerFactory_1_16.setField(mc_server, "worldServer", Collections.emptyMap());
        Object customRegistry = TestServerFactory_1_16.createFromCode(minecraftServerType, "return net.minecraft.core.RegistryAccess.b();");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16.3")) {
            TestServerFactory_1_16.setField(mc_server, "customRegistry", customRegistry);
        } else {
            TestServerFactory_1_16.setField(mc_server, "f", customRegistry);
        }
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);
        String propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
        TestServerFactory_1_16.setField(mc_server, "propertyManager", propertyManager);
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16.2")) {
            TestServerFactory_1_16.setField(propertyManager, "properties", TestServerFactory_1_16.createFromCode(TestServerFactory_1_16.resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "return new net.minecraft.server.dedicated.DedicatedServerProperties(    new java.util.Properties(),    arg0,    new joptsimple.OptionParser().parse(new String[0]));", customRegistry));
        } else {
            TestServerFactory_1_16.setField(propertyManager, "properties", TestServerFactory_1_16.createFromCode(TestServerFactory_1_16.resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "return new net.minecraft.server.dedicated.DedicatedServerProperties(    new java.util.Properties(),    new joptsimple.OptionParser().parse(new String[0]));"));
        }
        Class<?> dataConverterRegistryClass = TestServerFactory_1_16.resolveClass("net.minecraft.util.datafix.DataFixers");
        Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a", new Class[0]);
        Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null, new Object[0]);
        TestServerFactory_1_16.setField(mc_server, "dataConverterManager", dataConverterManager);
        minecraftServerType.getDeclaredMethod("getCraftingManager", new Class[0]);
        TestServerFactory_1_16.setField(mc_server, "executorService", TestServerFactory_1_16.createFromCode(minecraftServerType, "return net.minecraft.util.Util.e();"));
        final Class<?> resourcePackLoaderType = TestServerFactory_1_16.resolveClass("net.minecraft.server.packs.repository.Pack");
        ClassInterceptor interceptor = new ClassInterceptor(this){
            final /* synthetic */ TestServerFactory_1_16 this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("create")) {
                    return (instance, args) -> TestServerFactory.construct(resourcePackLoaderType, args);
                }
                return null;
            }
        };
        Object resourcePackLoaderNew = interceptor.createInstance(TestServerFactory_1_16.resolveClass("net.minecraft.server.packs.repository.Pack$PackConstructor"));
        ?[] resourcePackSources = LogicUtil.createArray(TestServerFactory_1_16.resolveClass("net.minecraft.server.packs.repository.RepositorySource"), 1);
        resourcePackSources[0] = TestServerFactory_1_16.construct(TestServerFactory_1_16.resolveClass("net.minecraft.server.packs.repository.ServerPacksSource"), new Object[0]);
        Object resourcepackrepository = TestServerFactory_1_16.construct(TestServerFactory_1_16.resolveClass("net.minecraft.server.packs.repository.PackRepository"), resourcePackLoaderNew, resourcePackSources);
        Object defaultDPConfig = TestServerFactory_1_16.getStaticField(TestServerFactory_1_16.resolveClass("net.minecraft.world.level.DataPackConfig"), "a");
        Method createDPConfig = minecraftServerType.getDeclaredMethod("a", TestServerFactory_1_16.resolveClass("net.minecraft.server.packs.repository.PackRepository"), TestServerFactory_1_16.resolveClass("net.minecraft.world.level.DataPackConfig"), Boolean.TYPE);
        Object datapackconfiguration = createDPConfig.invoke(null, resourcepackrepository, defaultDPConfig, true);
        List packs = (List)resourcepackrepository.getClass().getMethod("f", new Class[0]).invoke(resourcepackrepository, new Object[0]);
        Class<?> serverTypeType = TestServerFactory_1_16.resolveClass("net.minecraft.commands.Commands$CommandSelection");
        Object serverType = TestServerFactory_1_16.getStaticField(serverTypeType, "DEDICATED");
        int functionPermissionLevel = 2;
        Executor executor1 = (Executor)TestServerFactory_1_16.resolveClass("net.minecraft.util.Util").getMethod("f", new Class[0]).invoke(null, new Object[0]);
        Executor executor2 = TestServerFactory_1_16.newThreadExecutor();
        Method startLoadingMethod = TestServerFactory_1_16.resolveClass("net.minecraft.server.ServerResources").getDeclaredMethod("a", List.class, serverTypeType, Integer.TYPE, Executor.class, Executor.class);
        CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, packs, serverType, functionPermissionLevel, executor1, executor2);
        Object datapackresources = futureDPLoaded.get();
        Class<?> datapackresourceType = TestServerFactory_1_16.resolveClass("net.minecraft.server.ServerResources");
        datapackresourceType.getMethod("i", new Class[0]).invoke(datapackresources, new Object[0]);
        TestServerFactory_1_16.setField(mc_server, "resourcePackRepository", resourcepackrepository);
        TestServerFactory_1_16.setField(mc_server, "datapackconfiguration", datapackconfiguration);
        TestServerFactory_1_16.setField(mc_server, "dataPackResources", datapackresources);
    }
}

