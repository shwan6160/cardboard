/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_19_3;

class TestServerFactory_1_20_2
extends TestServerFactory_1_19_3 {
    TestServerFactory_1_20_2() {
    }

    @Override
    protected void init(TestServerFactory.ServerEnvironment env) throws Throwable {
        super.init(env);
        TestServerFactory_1_20_2.createFromCode(TestServerFactory_1_20_2.resolveClass("org.bukkit.craftbukkit.CraftServer"), "org.bukkit.craftbukkit.CraftRegistry.setMinecraftRegistry(arg0.registryAccess());", env.mc_server);
    }

    @Override
    protected Object createVanillaResourcePackRepository() throws Throwable {
        Class<?> resourcePackRepositoryType = TestServerFactory_1_20_2.resolveClass("net.minecraft.server.packs.repository.ServerPacksSource");
        return TestServerFactory_1_20_2.createFromCode(resourcePackRepositoryType, "return net.minecraft.server.packs.repository.ServerPacksSource.createVanillaTrustedRepository();");
    }
}

