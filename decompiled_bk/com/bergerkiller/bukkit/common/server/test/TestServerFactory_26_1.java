/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_21_2;
import java.util.List;
import java.util.concurrent.Executor;

class TestServerFactory_26_1
extends TestServerFactory_1_21_2 {
    TestServerFactory_26_1() {
    }

    @Override
    protected Object initRegistries(TestServerFactory.ServerEnvironment env) throws Throwable {
        Object registryAccess = TestServerFactory_26_1.createFromCode(env.mc_server_type, "return net.minecraft.server.RegistryLayer.createRegistryAccess();");
        env.tagDataPackRegistries = (List)TestServerFactory_26_1.createFromCode(env.mc_server_type, "net.minecraft.server.packs.resources.CloseableResourceManager ireloadableresourcemanager = arg0;\nnet.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\nreturn net.minecraft.tags.TagLoader.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(net.minecraft.server.RegistryLayer.STATIC));", env.resourceManager, registryAccess);
        SyncExecutor executor = new SyncExecutor();
        env.registries = TestServerFactory_26_1.createFromCode(env.mc_server_type, "net.minecraft.server.packs.resources.CloseableResourceManager ireloadableresourcemanager = arg0;\nnet.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\njava.util.List list = arg2;\njava.util.concurrent.Executor executor = arg3;\nnet.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension = layeredregistryaccess.getAccessForLoading(net.minecraft.server.RegistryLayer.WORLDGEN);\njava.util.List list1 = net.minecraft.tags.TagLoader.buildUpdatedLookups(iregistrycustom_dimension, list);\njava.util.concurrent.CompletableFuture iregistrycustom_dimension1_future = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.ResourceManager) ireloadableresourcemanager, list1, net.minecraft.resources.RegistryDataLoader.WORLDGEN_REGISTRIES, executor);\nnet.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension1 = (net.minecraft.core.RegistryAccess$Frozen) iregistrycustom_dimension1_future.get();\njava.util.List list2 = java.util.stream.Stream.concat(list1.stream(), iregistrycustom_dimension1.listRegistries()).toList();\njava.util.concurrent.CompletableFuture iregistrycustom_dimension2_future = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.ResourceManager) ireloadableresourcemanager, list2, net.minecraft.resources.RegistryDataLoader.DIMENSION_REGISTRIES, executor);\nnet.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension2 = (net.minecraft.core.RegistryAccess$Frozen) iregistrycustom_dimension2_future.get();\n\nreturn layeredregistryaccess.replaceFrom(net.minecraft.server.RegistryLayer.WORLDGEN, java.util.Collections.singletonList(iregistrycustom_dimension1));", env.resourceManager, registryAccess, env.tagDataPackRegistries, executor);
        return env.registries;
    }

    public static class SyncExecutor
    implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}

