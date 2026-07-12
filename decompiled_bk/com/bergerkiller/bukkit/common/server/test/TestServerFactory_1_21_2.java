/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_20_2;
import java.util.List;

class TestServerFactory_1_21_2
extends TestServerFactory_1_20_2 {
    TestServerFactory_1_21_2() {
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        super.init(env);
        TestServerFactory_1_21_2.setField(env.mc_server, "fuelValues", TestServerFactory_1_21_2.createFromCode(env.mc_server_type, "return net.minecraft.world.level.block.entity.FuelValues.vanillaBurnTimes(\n    arg0.compositeAccess(),\n    arg1\n);", env.registries, env.featureFlagSet));
    }

    @Override
    protected Object initRegistries(TestServerFactory.ServerEnvironment env) throws Throwable {
        Object registryAccess = TestServerFactory_1_21_2.createFromCode(env.mc_server_type, "return net.minecraft.server.RegistryLayer.createRegistryAccess();");
        env.tagDataPackRegistries = (List)TestServerFactory_1_21_2.createFromCode(env.mc_server_type, "net.minecraft.server.packs.resources.CloseableResourceManager ireloadableresourcemanager = arg0;\nnet.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\nreturn net.minecraft.tags.TagLoader.loadTagsForExistingRegistries(ireloadableresourcemanager, layeredregistryaccess.getLayer(net.minecraft.server.RegistryLayer.STATIC));", env.resourceManager, registryAccess);
        env.registries = TestServerFactory_1_21_2.createFromCode(env.mc_server_type, "net.minecraft.server.packs.resources.CloseableResourceManager ireloadableresourcemanager = arg0;\nnet.minecraft.core.LayeredRegistryAccess layeredregistryaccess = arg1;\njava.util.List list = arg2;\nnet.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension = layeredregistryaccess.getAccessForLoading(net.minecraft.server.RegistryLayer.WORLDGEN);\njava.util.List list1 = net.minecraft.tags.TagLoader.buildUpdatedLookups(iregistrycustom_dimension, list);\nnet.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension1 = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.ResourceManager) ireloadableresourcemanager, list1, net.minecraft.resources.RegistryDataLoader.WORLDGEN_REGISTRIES);\njava.util.List list2 = java.util.stream.Stream.concat(list1.stream(), iregistrycustom_dimension1.listRegistries()).toList();\nnet.minecraft.core.RegistryAccess$Frozen iregistrycustom_dimension2 = net.minecraft.resources.RegistryDataLoader.load((net.minecraft.server.packs.resources.ResourceManager) ireloadableresourcemanager, list2, net.minecraft.resources.RegistryDataLoader.DIMENSION_REGISTRIES);\n\nreturn layeredregistryaccess.replaceFrom(net.minecraft.server.RegistryLayer.WORLDGEN, java.util.Collections.singletonList(iregistrycustom_dimension1));", env.resourceManager, registryAccess, env.tagDataPackRegistries);
        return env.registries;
    }
}

