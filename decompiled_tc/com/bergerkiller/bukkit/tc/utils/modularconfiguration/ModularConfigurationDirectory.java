/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils.modularconfiguration;

import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationBlock;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationFile;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ModularConfigurationDirectory<T>
implements ModularConfigurationBlock<T> {
    private final ModularConfiguration<T> main;
    private List<ModularConfigurationFile<T>> files;
    private List<String> names;
    private final Map<String, ModularConfigurationFile<T>> filesByName;
    private final File directory;

    ModularConfigurationDirectory(ModularConfiguration<T> main, File directory) {
        this.main = main;
        this.files = Collections.emptyList();
        this.names = Collections.emptyList();
        this.filesByName = new HashMap<String, ModularConfigurationFile<T>>();
        this.directory = directory;
        this.loadFiles();
    }

    public File getDirectory() {
        return this.directory;
    }

    public void clear() {
        if (!this.files.isEmpty()) {
            List<ModularConfigurationFile<ModularConfigurationFile>> filesOrig = this.files;
            this.files = Collections.emptyList();
            this.names = Collections.emptyList();
            this.filesByName.clear();
            filesOrig.forEach(this.main::onModuleRemoved);
        }
    }

    private void loadFiles() {
        if (!this.directory.exists()) {
            this.directory.mkdir();
            this.files = Collections.emptyList();
            this.names = Collections.emptyList();
            this.filesByName.clear();
            return;
        }
        File[] directoryFiles = this.directory.listFiles();
        if (directoryFiles == null) {
            this.files = Collections.emptyList();
            this.names = Collections.emptyList();
            this.filesByName.clear();
            return;
        }
        this.files = Arrays.stream(directoryFiles).filter(file -> {
            String ext = file.getName().toLowerCase(Locale.ENGLISH);
            if (ext.endsWith(".zip")) {
                this.main.logger.warning("Zip files are not read, please extract '" + file.getAbsolutePath() + "'!");
                return false;
            }
            return ext.endsWith(".yml") || ext.endsWith(".yaml");
        }).map(file -> new ModularConfigurationFile<T>(this.main, (File)file)).filter(m -> !m.isEmpty()).sorted().collect(Collectors.toList());
        this.filesByName.clear();
        this.files.forEach(f -> this.filesByName.put(f.name, (ModularConfigurationFile<T>)f));
        this.regenNames();
    }

    public ModularConfigurationFile<T> getFile(String name) {
        return this.filesByName.get(name.toLowerCase(Locale.ENGLISH));
    }

    public ModularConfigurationFile<T> createFile(String name) {
        File file = new File(this.directory, name + ".yml");
        String fixedName = ModularConfigurationFile.decodeModuleNameFromFile(file);
        ModularConfigurationFile<T> fileModule = this.filesByName.get(fixedName);
        if (fileModule == null) {
            try {
                file.createNewFile();
            }
            catch (IOException ex) {
                this.main.logger.log(Level.WARNING, "Failed to write to " + file.getAbsolutePath(), ex);
            }
            fileModule = new ModularConfigurationFile<T>(this.main, fixedName, file, false);
            ArrayList<ModularConfigurationFile<T>> newFiles = new ArrayList<ModularConfigurationFile<T>>(this.files);
            int index = Collections.binarySearch(newFiles, fileModule);
            if (index < 0) {
                index = -index - 1;
            }
            newFiles.add(index, fileModule);
            this.files = newFiles;
            this.filesByName.put(fixedName, fileModule);
            this.regenNames();
        }
        return fileModule;
    }

    private void regenNames() {
        ArrayList<String> names = new ArrayList<String>(this.files.size());
        for (ModularConfigurationFile<T> file : this.files) {
            names.add(file.getName());
        }
        this.names = Collections.unmodifiableList(names);
    }

    @Override
    public ModularConfiguration<T> getMain() {
        return this.main;
    }

    @Override
    public List<ModularConfigurationFile<T>> getFiles() {
        return this.files;
    }

    public List<String> getFileNames() {
        return this.names;
    }

    @Override
    public void reload() {
        this.saveChanges();
        this.clear();
        this.loadFiles();
        this.files.forEach(this.main::onModuleAdded);
    }

    @Override
    public void saveChanges() {
        this.files.forEach(ModularConfigurationModule::saveChanges);
    }

    @Override
    public void save() {
        this.files.forEach(ModularConfigurationModule::save);
    }
}

