/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.bukkit.common.server.test.BackgroundWorkerDefuser;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.bukkit.Bukkit;

class TestServerFactory_1_18
extends TestServerFactory {
    TestServerFactory_1_18() {
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        Class<?> sharedConstantsClass = TestServerFactory_1_18.resolveClass("net.minecraft.SharedConstants");
        Method initSharedConstantsMethod = Resolver.resolveAndGetDeclaredMethod(sharedConstantsClass, "tryDetectVersion", new Class[0]);
        initSharedConstantsMethod.invoke(null, new Object[0]);
        try (BackgroundWorkerDefuser defuser = BackgroundWorkerDefuser.start(TestServerFactory_1_18.resolveClass("net.minecraft.util.Util"));){
            TestServerFactory_1_18.resolveClass("net.minecraft.util.datafix.DataFixers", true);
        }
        Class<?> dispenserRegistryClass = TestServerFactory_1_18.resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = Resolver.resolveAndGetDeclaredMethod(dispenserRegistryClass, "bootStrap", new Class[0]);
        dispenserRegistryBootstrapMethod.invoke(null, new Object[0]);
        ClassTemplate<? extends Bukkit> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Bukkit server = server_t.newInstanceNull();
        Class<?> minecraftServerType = TestServerFactory_1_18.resolveClass("net.minecraft.server.MinecraftServer");
        Class<?> dedicatedType = TestServerFactory_1_18.resolveClass("net.minecraft.server.dedicated.DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();
        env.mc_server = mc_server;
        Class<?> iAsyncTaskHandlerClass = TestServerFactory_1_18.resolveClass("net.minecraft.util.thread.BlockableEventLoop");
        TestServerFactory_1_18.setField(mc_server, iAsyncTaskHandlerClass, "name", "Server");
        TestServerFactory_1_18.setField(mc_server, iAsyncTaskHandlerClass, "pendingRunnables", TestServerFactory_1_18.createFromCode(minecraftServerType, "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));
        TestServerFactory_1_18.setField(server, "logger", MountiplexUtil.LOGGER);
        TestServerFactory_1_18.setField(server, "console", mc_server);
        TestServerFactory_1_18.setField(mc_server, "serverThread", Thread.currentThread());
        TestServerFactory_1_18.setField(mc_server, "levels", Collections.emptyMap());
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.1")) {
            try {
                TestServerFactory_1_18.setField(server, "registries", new HashMap());
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        Object customRegistryDimension = this.initCustomRegistryDimension(minecraftServerType);
        TestServerFactory_1_18.setField(mc_server, "registryHolder", customRegistryDimension);
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);
        String propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
        TestServerFactory_1_18.setField(mc_server, "settings", propertyManager);
        TestServerFactory_1_18.setField(propertyManager, "properties", TestServerFactory_1_18.createFromCode(TestServerFactory_1_18.resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "return new net.minecraft.server.dedicated.DedicatedServerProperties(    new java.util.Properties(),    new joptsimple.OptionParser().parse(new String[0]));"));
        Class<?> dataConverterRegistryClass = TestServerFactory_1_18.resolveClass("net.minecraft.util.datafix.DataFixers");
        Method dataConverterRegistryInitMethod = Resolver.resolveAndGetDeclaredMethod(dataConverterRegistryClass, "getDataFixer", new Class[0]);
        Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null, new Object[0]);
        TestServerFactory_1_18.setField(mc_server, "fixerUpper", dataConverterManager);
        TestServerFactory_1_18.setField(mc_server, "executor", TestServerFactory_1_18.createFromCode(minecraftServerType, "return net.minecraft.util.Util.backgroundExecutor();"));
        this.initDataPack(minecraftServerType, mc_server, customRegistryDimension);
    }

    protected Object initCustomRegistryDimension(Class<?> minecraftServerType) {
        return TestServerFactory_1_18.createFromCode(minecraftServerType, "return net.minecraft.core.RegistryAccess.builtin();");
    }

    protected void initDataPack(Class<?> minecraftServerType, Object mc_server, Object customRegistryDimension) throws Throwable {
        Class<?> enumSourcePackTypeClass = TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.PackType");
        final Object packTypeServerData = TestServerFactory_1_18.getStaticField(enumSourcePackTypeClass, "SERVER_DATA");
        final Class<?> resourcePackLoaderType = TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.repository.Pack");
        ClassInterceptor interceptor = new ClassInterceptor(this){
            final /* synthetic */ TestServerFactory_1_18 this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getReturnType().equals(resourcePackLoaderType)) {
                    return (instance, args) -> TestServerFactory.construct(resourcePackLoaderType, args[0], args[1], args[2], args[3], args[4], packTypeServerData, args[5], args[6]);
                }
                return null;
            }
        };
        Object resourcePackLoaderNew = interceptor.createInstance(TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.repository.Pack$PackConstructor"));
        ?[] resourcePackSources = LogicUtil.createArray(TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.repository.RepositorySource"), 1);
        resourcePackSources[0] = TestServerFactory_1_18.construct(TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.repository.ServerPacksSource"), new Object[0]);
        Object resourcepackrepository = TestServerFactory_1_18.construct(TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.repository.PackRepository"), resourcePackLoaderNew, resourcePackSources);
        Object defaultDPConfig = TestServerFactory_1_18.getStaticField(TestServerFactory_1_18.resolveClass("net.minecraft.world.level.DataPackConfig"), "a");
        Method createDPConfig = Resolver.resolveAndGetDeclaredMethod(minecraftServerType, "configurePackRepository", TestServerFactory_1_18.resolveClass("net.minecraft.server.packs.repository.PackRepository"), TestServerFactory_1_18.resolveClass("net.minecraft.world.level.DataPackConfig"), Boolean.TYPE);
        Object datapackconfiguration = createDPConfig.invoke(null, resourcepackrepository, defaultDPConfig, true);
        List packs = (List)resourcepackrepository.getClass().getMethod("f", new Class[0]).invoke(resourcepackrepository, new Object[0]);
        Class<?> serverTypeType = TestServerFactory_1_18.resolveClass("net.minecraft.commands.Commands$CommandSelection");
        Object serverType = TestServerFactory_1_18.getStaticField(serverTypeType, "DEDICATED");
        int functionPermissionLevel = 2;
        Executor executor1 = (Executor)TestServerFactory_1_18.resolveClass("net.minecraft.util.Util").getMethod("f", new Class[0]).invoke(null, new Object[0]);
        Executor executor2 = TestServerFactory_1_18.newThreadExecutor();
        Class<?> dataPackResourcesType = TestServerFactory_1_18.resolveClass("net.minecraft.server.ServerResources");
        Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources", List.class, TestServerFactory_1_18.resolveClass("net.minecraft.core.RegistryAccess"), serverTypeType, Integer.TYPE, Executor.class, Executor.class);
        CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, packs, customRegistryDimension, serverType, functionPermissionLevel, executor1, executor2);
        Object datapackresources = futureDPLoaded.get();
        Class<?> datapackresourceType = TestServerFactory_1_18.resolveClass("net.minecraft.server.ServerResources");
        Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateGlobals", new Class[0]).invoke(datapackresources, new Object[0]);
        TestServerFactory_1_18.setField(mc_server, "packRepository", resourcepackrepository);
        TestServerFactory_1_18.setField(mc_server, "datapackconfiguration", datapackconfiguration);
        TestServerFactory_1_18.setField(mc_server, "resources", datapackresources);
    }
}

