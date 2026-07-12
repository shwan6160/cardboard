/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import java.util.List;

public interface ModularConfigurationBlock<T> {
    public ModularConfiguration<T> getMain();

    public List<? extends ModularConfigurationModule<T>> getFiles();

    public void reload();

    public void saveChanges();

    public void save();
}

