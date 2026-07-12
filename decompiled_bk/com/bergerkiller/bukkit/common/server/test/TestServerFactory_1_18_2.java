/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_18;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class TestServerFactory_1_18_2
extends TestServerFactory_1_18 {
    TestServerFactory_1_18_2() {
    }

    @Override
    protected Object initCustomRegistryDimension(Class<?> minecraftServerType) {
        return TestServerFactory_1_18_2.createFromCode(minecraftServerType, "return net.minecraft.core.RegistryAccess.builtinCopy().freeze();");
    }

    @Override
    protected void initDataPack(Class<?> minecraftServerType, Object mc_server, Object customRegistryDimension) throws Throwable {
        Class<?> resourcePackRepositoryType = TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.repository.PackRepository");
        Class<?> enumSourcePackTypeClass = TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.PackType");
        Object resourcepacktype = TestServerFactory_1_18_2.getStaticField(enumSourcePackTypeClass, "SERVER_DATA");
        ?[] resourcePackSources = LogicUtil.createArray(TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.repository.RepositorySource"), 1);
        resourcePackSources[0] = TestServerFactory_1_18_2.construct(TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.repository.ServerPacksSource"), new Object[0]);
        Object resourcepackrepository = TestServerFactory_1_18_2.construct(resourcePackRepositoryType, resourcepacktype, resourcePackSources);
        Object defaultDPConfig = TestServerFactory_1_18_2.getStaticField(TestServerFactory_1_18_2.resolveClass("net.minecraft.world.level.DataPackConfig"), "DEFAULT");
        Method createDPConfig = Resolver.resolveAndGetDeclaredMethod(minecraftServerType, "configurePackRepository", resourcePackRepositoryType, TestServerFactory_1_18_2.resolveClass("net.minecraft.world.level.DataPackConfig"), Boolean.TYPE);
        Object datapackconfiguration = createDPConfig.invoke(null, resourcepackrepository, defaultDPConfig, true);
        List packs = (List)Resolver.resolveAndGetDeclaredMethod(resourcePackRepositoryType, "openAllSelected", new Class[0]).invoke(resourcepackrepository, new Object[0]);
        Object resourcemanager = TestServerFactory_1_18_2.construct(TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.resources.MultiPackResourceManager"), resourcepacktype, packs);
        Class<?> serverTypeType = TestServerFactory_1_18_2.resolveClass("net.minecraft.commands.Commands$CommandSelection");
        Object serverType = TestServerFactory_1_18_2.getStaticField(serverTypeType, "DEDICATED");
        int functionPermissionLevel = 2;
        Executor executor1 = (Executor)Resolver.resolveAndGetDeclaredMethod(TestServerFactory_1_18_2.resolveClass("net.minecraft.util.Util"), "bootstrapExecutor", new Class[0]).invoke(null, new Object[0]);
        Executor executor2 = TestServerFactory_1_18_2.newThreadExecutor();
        Class<?> dataPackResourcesType = TestServerFactory_1_18_2.resolveClass("net.minecraft.server.ReloadableServerResources");
        Method startLoadingMethod = Resolver.resolveAndGetDeclaredMethod(dataPackResourcesType, "loadResources", TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.resources.ResourceManager"), TestServerFactory_1_18_2.resolveClass("net.minecraft.core.RegistryAccess$Frozen"), serverTypeType, Integer.TYPE, Executor.class, Executor.class);
        CompletableFuture futureDPLoaded = (CompletableFuture)startLoadingMethod.invoke(null, resourcemanager, customRegistryDimension, serverType, functionPermissionLevel, executor1, executor2);
        Object datapackresources = futureDPLoaded.get();
        Class<?> datapackresourceType = TestServerFactory_1_18_2.resolveClass("net.minecraft.server.ReloadableServerResources");
        Resolver.resolveAndGetDeclaredMethod(datapackresourceType, "updateRegistryTags", TestServerFactory_1_18_2.resolveClass("net.minecraft.core.RegistryAccess")).invoke(datapackresources, customRegistryDimension);
        TestServerFactory_1_18_2.setField(mc_server, "packRepository", resourcepackrepository);
        TestServerFactory_1_18_2.setField(mc_server, "datapackconfiguration", datapackconfiguration);
        String resourcesFieldName = Resolver.resolveFieldName(minecraftServerType, "resources");
        Field field = minecraftServerType.getDeclaredField(resourcesFieldName);
        field.setAccessible(true);
        Constructor<?> constr = field.getType().getConstructor(TestServerFactory_1_18_2.resolveClass("net.minecraft.server.packs.resources.CloseableResourceManager"), TestServerFactory_1_18_2.resolveClass("net.minecraft.server.ReloadableServerResources"));
        constr.setAccessible(true);
        Object managerWithResources = constr.newInstance(resourcemanager, datapackresources);
        field.set(mc_server, managerWithResources);
    }
}

