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
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.bukkit.Bukkit;

class TestServerFactory_1_19_3
extends TestServerFactory {
    TestServerFactory_1_19_3() {
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        Object worldSettings;
        Class<?> sharedConstantsClass = TestServerFactory_1_19_3.resolveClass("net.minecraft.SharedConstants");
        Method initSharedConstantsMethod = Resolver.resolveAndGetDeclaredMethod(sharedConstantsClass, "tryDetectVersion", new Class[0]);
        initSharedConstantsMethod.invoke(null, new Object[0]);
        try (BackgroundWorkerDefuser defuser = BackgroundWorkerDefuser.start(TestServerFactory_1_19_3.resolveClass("net.minecraft.util.Util"));){
            TestServerFactory_1_19_3.resolveClass("net.minecraft.util.datafix.DataFixers", true);
        }
        Class<?> dispenserRegistryClass = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.Bootstrap");
        Method dispenserRegistryBootstrapMethod = Resolver.resolveAndGetDeclaredMethod(dispenserRegistryClass, "bootStrap", new Class[0]);
        dispenserRegistryBootstrapMethod.invoke(null, new Object[0]);
        ClassTemplate<? extends Bukkit> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
        Bukkit server = server_t.newInstanceNull();
        env.mc_server_type = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.MinecraftServer");
        Class<?> dedicatedType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.dedicated.DedicatedServer");
        ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
        Object mc_server = mc_server_t.newInstanceNull();
        env.mc_server = mc_server;
        Class<?> iAsyncTaskHandlerClass = TestServerFactory_1_19_3.resolveClass("net.minecraft.util.thread.BlockableEventLoop");
        TestServerFactory_1_19_3.setField(mc_server, iAsyncTaskHandlerClass, "name", "Server");
        TestServerFactory_1_19_3.setField(mc_server, iAsyncTaskHandlerClass, "pendingRunnables", TestServerFactory_1_19_3.createFromCode(env.mc_server_type, "return com.google.common.collect.Queues.newConcurrentLinkedQueue();"));
        TestServerFactory_1_19_3.setField(server, "logger", MountiplexUtil.LOGGER);
        TestServerFactory_1_19_3.setField(server, "console", mc_server);
        TestServerFactory_1_19_3.setField(mc_server, "serverThread", Thread.currentThread());
        TestServerFactory_1_19_3.setField(mc_server, "levels", Collections.emptyMap());
        try {
            TestServerFactory_1_19_3.setField(server, "registries", new HashMap());
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        this.initVanillaResourceManager(env);
        env.featureFlagSet = TestServerFactory_1_19_3.createFromCode(TestServerFactory_1_19_3.resolveClass("net.minecraft.world.level.WorldDataConfiguration"), "return net.minecraft.world.level.WorldDataConfiguration.DEFAULT.enabledFeatures();");
        Object registries = this.initRegistries(env);
        TestServerFactory_1_19_3.setField(mc_server, "registries", registries);
        Field bkServerField = Bukkit.class.getDeclaredField("server");
        bkServerField.setAccessible(true);
        bkServerField.set(null, server);
        String propertyManager = ClassTemplate.create("net.minecraft.server.dedicated.DedicatedServerSettings").newInstanceNull();
        TestServerFactory_1_19_3.setField(mc_server, "settings", propertyManager);
        TestServerFactory_1_19_3.setField(propertyManager, "properties", TestServerFactory_1_19_3.createFromCode(TestServerFactory_1_19_3.resolveClass("net.minecraft.server.dedicated.DedicatedServerProperties"), "return new net.minecraft.server.dedicated.DedicatedServerProperties(    new java.util.Properties(),    new joptsimple.OptionParser().parse(new String[0]));"));
        Class<?> dataConverterRegistryClass = TestServerFactory_1_19_3.resolveClass("net.minecraft.util.datafix.DataFixers");
        Method dataConverterRegistryInitMethod = Resolver.resolveAndGetDeclaredMethod(dataConverterRegistryClass, "getDataFixer", new Class[0]);
        Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null, new Object[0]);
        TestServerFactory_1_19_3.setField(mc_server, "fixerUpper", dataConverterManager);
        TestServerFactory_1_19_3.setField(mc_server, "executor", TestServerFactory_1_19_3.createFromCode(env.mc_server_type, "return net.minecraft.util.Util.backgroundExecutor();"));
        this.initDataPack(env, env.mc_server_type, mc_server, registries);
        Object worldDataConfiguration = TestServerFactory_1_19_3.createFromCode(TestServerFactory_1_19_3.resolveClass("net.minecraft.world.level.WorldDataConfiguration"), "return net.minecraft.world.level.WorldDataConfiguration.DEFAULT;");
        if (CommonBootstrap.evaluateMCVersion(">=", "26.1")) {
            worldSettings = TestServerFactory_1_19_3.createFromCode(TestServerFactory_1_19_3.resolveClass("net.minecraft.world.level.LevelSettings"), "return new net.minecraft.world.level.LevelSettings(\n    \"\",\n    net.minecraft.world.level.GameType.SURVIVAL,\n    LevelSettings$DifficultySettings.DEFAULT,\n    true,\n    arg0\n);", worldDataConfiguration);
        } else {
            worldSettings = NullInstantiator.of(TestServerFactory_1_19_3.resolveClass("net.minecraft.world.level.LevelSettings")).create();
            TestServerFactory_1_19_3.setField(worldSettings, "dataConfiguration", worldDataConfiguration);
        }
        Object worldData = NullInstantiator.of(TestServerFactory_1_19_3.resolveClass("net.minecraft.world.level.storage.PrimaryLevelData")).create();
        TestServerFactory_1_19_3.setField(worldData, "settings", worldSettings);
        TestServerFactory_1_19_3.setField(mc_server, "worldData", worldData);
    }

    protected Object initCustomRegistryDimension(Class<?> minecraftServerType) {
        return TestServerFactory_1_19_3.createFromCode(minecraftServerType, "return net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);");
    }

    protected Object createVanillaResourcePackRepository() throws Throwable {
        Class<?> resourcePackRepositoryType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.repository.PackRepository");
        ?[] resourcePackSources = LogicUtil.createArray(TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.repository.RepositorySource"), 1);
        resourcePackSources[0] = TestServerFactory_1_19_3.construct(TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.repository.ServerPacksSource"), new Object[0]);
        return TestServerFactory_1_19_3.construct(resourcePackRepositoryType, new Object[]{resourcePackSources});
    }

    protected void initVanillaResourceManager(TestServerFactory.ServerEnvironment env) throws Throwable {
        Class<?> resourcePackRepositoryType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.repository.PackRepository");
        Class<?> enumSourcePackTypeClass = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.PackType");
        Object resourcepacktype = TestServerFactory_1_19_3.getStaticField(enumSourcePackTypeClass, "SERVER_DATA");
        Object resourcepackrepository = this.createVanillaResourcePackRepository();
        TestServerFactory_1_19_3.createFromCode(resourcepackrepository.getClass(), "arg0.reload();\narg0.setSelected(java.util.Collections.singleton(\"vanilla\"));\nreturn null;", resourcepackrepository);
        List packs = (List)Resolver.resolveAndGetDeclaredMethod(resourcePackRepositoryType, "openAllSelected", new Class[0]).invoke(resourcepackrepository, new Object[0]);
        Object resourcemanager = TestServerFactory_1_19_3.construct(TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.resources.MultiPackResourceManager"), resourcepacktype, packs);
        env.resourcePackRepository = resourcepackrepository;
        env.resourceManager = resourcemanager;
    }

    protected Object initRegistries(TestServerFactory.ServerEnvironment env) throws Throwable {
        Object registryAccess = TestServerFactory_1_19_3.createFromCode(env.mc_server_type, "return net.minecraft.server.RegistryLayer.createRegistryAccess();");
        env.registries = TestServerFactory_1_19_3.createFromCode(env.mc_server_type, "return net.minecraft.server.WorldLoader.loadAndReplaceLayer(\n    arg0, arg1,\n    net.minecraft.server.RegistryLayer.WORLDGEN,\n    net.minecraft.resources.RegistryDataLoader.WORLDGEN_REGISTRIES\n);", env.resourceManager, registryAccess);
        TestServerFactory_1_19_3.createFromCode(env.mc_server_type, "return net.minecraft.resources.RegistryDataLoader.load(arg0,\n            arg1.getAccessForLoading(net.minecraft.server.RegistryLayer.DIMENSIONS),\n            net.minecraft.resources.RegistryDataLoader.DIMENSION_REGISTRIES);", env.resourceManager, env.registries);
        return env.registries;
    }

    protected void initDataPack(TestServerFactory.ServerEnvironment env, Class<?> minecraftServerType, Object mc_server, Object registries) throws Throwable {
        Object datapackresources;
        Object customRegistryDimension = TestServerFactory_1_19_3.createFromCode(TestServerFactory_1_19_3.resolveClass("net.minecraft.core.LayeredRegistryAccess"), "return arg0.getAccessForLoading(net.minecraft.server.RegistryLayer.RELOADABLE);", registries);
        Class<?> serverTypeType = TestServerFactory_1_19_3.resolveClass("net.minecraft.commands.Commands$CommandSelection");
        Object serverType = TestServerFactory_1_19_3.getStaticField(serverTypeType, "DEDICATED");
        int functionPermissionLevel = 2;
        Executor executor1 = CommonBootstrap.evaluateMCVersion(">=", "1.19.4") ? TestServerFactory_1_19_3.newThreadExecutor() : (Executor)Resolver.resolveAndGetDeclaredMethod(TestServerFactory_1_19_3.resolveClass("net.minecraft.util.Util"), "bootstrapExecutor", new Class[0]).invoke(null, new Object[0]);
        Executor executor2 = TestServerFactory_1_19_3.newThreadExecutor();
        Class<?> dataPackResourcesType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources");
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.11")) {
            Class<?> permissionSetType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.permissions.PermissionSet");
            Object allPermissions = TestServerFactory_1_19_3.createFromCode(permissionSetType, "return net.minecraft.server.permissions.PermissionSet.ALL_PERMISSIONS;");
            Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources", TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.resources.ResourceManager"), TestServerFactory_1_19_3.resolveClass("net.minecraft.core.LayeredRegistryAccess"), List.class, TestServerFactory_1_19_3.resolveClass("net.minecraft.world.flag.FeatureFlagSet"), serverTypeType, permissionSetType, Executor.class, Executor.class);
            CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, env.resourceManager, registries, env.tagDataPackRegistries, env.featureFlagSet, serverType, allPermissions, executor1, executor2);
            datapackresources = futureDPLoaded.get();
            if (CommonBootstrap.evaluateMCVersion(">=", "26.1")) {
                Class<?> datapackresourceType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources");
                Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateComponentsAndStaticRegistryTags", new Class[0]).invoke(datapackresources, new Object[0]);
            } else {
                Class<?> datapackresourceType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources");
                Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateStaticRegistryTags", new Class[0]).invoke(datapackresources, new Object[0]);
            }
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.21.2")) {
            Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources", TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.resources.ResourceManager"), TestServerFactory_1_19_3.resolveClass("net.minecraft.core.LayeredRegistryAccess"), List.class, TestServerFactory_1_19_3.resolveClass("net.minecraft.world.flag.FeatureFlagSet"), serverTypeType, Integer.TYPE, Executor.class, Executor.class);
            CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, env.resourceManager, registries, env.tagDataPackRegistries, env.featureFlagSet, serverType, functionPermissionLevel, executor1, executor2);
            datapackresources = futureDPLoaded.get();
            Class<?> datapackresourceType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources");
            Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateStaticRegistryTags", new Class[0]).invoke(datapackresources, new Object[0]);
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
            Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources", TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.resources.ResourceManager"), TestServerFactory_1_19_3.resolveClass("net.minecraft.core.LayeredRegistryAccess"), TestServerFactory_1_19_3.resolveClass("net.minecraft.world.flag.FeatureFlagSet"), serverTypeType, Integer.TYPE, Executor.class, Executor.class);
            CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, env.resourceManager, registries, env.featureFlagSet, serverType, functionPermissionLevel, executor1, executor2);
            datapackresources = futureDPLoaded.get();
            Class<?> datapackresourceType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources");
            Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateRegistryTags", new Class[0]).invoke(datapackresources, new Object[0]);
        } else {
            Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources", TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.resources.ResourceManager"), TestServerFactory_1_19_3.resolveClass("net.minecraft.core.RegistryAccess$Frozen"), TestServerFactory_1_19_3.resolveClass("net.minecraft.world.flag.FeatureFlagSet"), serverTypeType, Integer.TYPE, Executor.class, Executor.class);
            CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, env.resourceManager, customRegistryDimension, env.featureFlagSet, serverType, functionPermissionLevel, executor1, executor2);
            datapackresources = futureDPLoaded.get();
            Class<?> datapackresourceType = TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources");
            Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateRegistryTags", TestServerFactory_1_19_3.resolveClass("net.minecraft.core.RegistryAccess")).invoke(datapackresources, customRegistryDimension);
        }
        TestServerFactory_1_19_3.setField(mc_server, "packRepository", env.resourcePackRepository);
        String resourcesFieldName = Resolver.resolveFieldName(minecraftServerType, "resources");
        Field field = minecraftServerType.getDeclaredField(resourcesFieldName);
        field.setAccessible(true);
        Constructor<?> constr = field.getType().getConstructor(TestServerFactory_1_19_3.resolveClass("net.minecraft.server.packs.resources.CloseableResourceManager"), TestServerFactory_1_19_3.resolveClass("net.minecraft.server.ReloadableServerResources"));
        constr.setAccessible(true);
        Object managerWithResources = constr.newInstance(env.resourceManager, datapackresources);
        field.set(mc_server, managerWithResources);
    }
}

