/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationBlock;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationDirectory;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationFile;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ModularConfigurationBlockList<T>
implements ModularConfigurationBlock<T> {
    protected final List<ModularConfigurationBlock<T>> blocks = new ArrayList<ModularConfigurationBlock<T>>();

    @Override
    public List<? extends ModularConfigurationModule<T>> getFiles() {
        if (this.blocks.isEmpty()) {
            return Collections.emptyList();
        }
        if (this.blocks.size() == 1) {
            return this.blocks.get(0).getFiles();
        }
        ArrayList allFiles = new ArrayList();
        this.blocks.forEach(b -> allFiles.addAll(b.getFiles()));
        return allFiles;
    }

    @Override
    public void reload() {
        this.blocks.forEach(ModularConfigurationBlock::reload);
    }

    @Override
    public void saveChanges() {
        this.blocks.forEach(ModularConfigurationBlock::saveChanges);
    }

    @Override
    public void save() {
        this.blocks.forEach(ModularConfigurationBlock::save);
    }

    public ModularConfigurationDirectory<T> addDirectoryModule(File directory) {
        return this.addDirectoryModule(directory, false);
    }

    public ModularConfigurationDirectory<T> addDirectoryModule(File directory, boolean priority) {
        return this.addBlock(new ModularConfigurationDirectory(this.getMain(), directory), priority);
    }

    public ModularConfigurationFile<T> addFileModule(String name, File file, boolean readOnly) {
        return this.addFileModule(name, file, readOnly, false);
    }

    public ModularConfigurationFile<T> addFileModule(String name, File file, boolean readOnly, boolean priority) {
        return this.addBlock(new ModularConfigurationFile(this.getMain(), name, file, readOnly), priority);
    }

    public <B extends ModularConfigurationBlock<T>> B addBlock(B block, boolean priority) {
        if (priority) {
            this.blocks.add(0, block);
        } else {
            this.blocks.add(block);
        }
        block.getFiles().forEach(this.getMain()::onModuleAdded);
        return block;
    }

    public void clear() {
        ArrayList<ModularConfigurationBlock<T>> copy = new ArrayList<ModularConfigurationBlock<T>>(this.blocks);
        this.blocks.clear();
        copy.stream().flatMap(b -> b.getFiles().stream()).forEachOrdered(this.getMain()::onModuleRemoved);
    }
}

