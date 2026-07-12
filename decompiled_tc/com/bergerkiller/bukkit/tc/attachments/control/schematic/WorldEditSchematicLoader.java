/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.extent.clipboard.Clipboard
 *  com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
 *  com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
 *  com.sk89q.worldedit.extent.clipboard.io.ClipboardReader
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.world.block.BlockStateHolder
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control.schematic;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldEditSchematicLoader
implements LibraryComponent {
    private static final long SCHEMATIC_EXPIRE_TIME_MS = 1800000L;
    private static final long SCHEMATIC_EXPIRE_TASK_INTERVAL = 1200L;
    private final TrainCarts plugin;
    private final Path tcSchematicsPath;
    private final Object lock = new Object();
    private final Map<Path, Schematic> loadedSchematicsByFile = new HashMap<Path, Schematic>();
    private final Map<String, Schematic> loadedSchematics = new HashMap<String, Schematic>();
    private final Map<String, List<SchematicReader>> pendingSchematics = new LinkedHashMap<String, List<SchematicReader>>();
    private volatile boolean isShuttingDown = false;
    private volatile LoaderThread loaderThread = null;
    private volatile Task unloaderTask = null;
    private static final ReaderState WAITING_STATE = new ReaderState(){

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean hasError() {
            return false;
        }
    };
    private static final ReaderState ABORTED_STATE = new ReaderState(){

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public boolean hasError() {
            return true;
        }
    };

    public WorldEditSchematicLoader(TrainCarts plugin) {
        this.plugin = plugin;
        this.tcSchematicsPath = plugin.getDataFile(new String[]{"schematics"}).toPath().toAbsolutePath();
        this.isShuttingDown = Bukkit.getPluginManager().getPlugin("WorldEdit") == null;
    }

    public boolean isEnabled() {
        return !this.isShuttingDown;
    }

    public void enable() {
        if (!TCConfig.allowSchematicAttachment || !CommonCapabilities.HAS_DISPLAY_ENTITY) {
            this.isShuttingDown = true;
            this.unloadAllCurrentSchematics();
            return;
        }
        Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null || !worldEdit.isEnabled()) {
            this.isShuttingDown = true;
            this.unloadAllCurrentSchematics();
            return;
        }
        try {
            Files.createDirectories(this.tcSchematicsPath, new FileAttribute[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.isShuttingDown = false;
        if (this.loaderThread == null) {
            this.loaderThread = new LoaderThread();
            this.loaderThread.start();
        }
        this.unloaderTask = new Task((JavaPlugin)this.plugin){
            List<Schematic> schematicsToUnload;
            {
                this.schematicsToUnload = new ArrayList<Schematic>();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                try {
                    long time = System.currentTimeMillis();
                    Object object = WorldEditSchematicLoader.this.lock;
                    synchronized (object) {
                        for (Schematic s : WorldEditSchematicLoader.this.loadedSchematicsByFile.values()) {
                            if (!s.canUnload(time)) continue;
                            this.schematicsToUnload.add(s);
                        }
                        for (Schematic s : this.schematicsToUnload) {
                            s.remove(true);
                        }
                    }
                }
                finally {
                    this.schematicsToUnload.clear();
                }
            }
        }.start(1200L, 1200L);
    }

    public void disable() {
        this.isShuttingDown = true;
        if (this.loaderThread != null) {
            try {
                this.loaderThread.join(500L);
            }
            catch (Throwable t1) {
                this.plugin.log(Level.WARNING, "Schematic loader is still busy. Waiting for 15s...");
                try {
                    this.loaderThread.join(15000L);
                }
                catch (Throwable t2) {
                    this.plugin.log(Level.SEVERE, "Schematic loader is stuck! Resuming shutdown anyway...");
                }
            }
            this.loaderThread = null;
        }
        this.unloadAllCurrentSchematics();
        Task.stop((Task)this.unloaderTask);
        this.unloaderTask = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void unloadAllCurrentSchematics() {
        Object object = this.lock;
        synchronized (object) {
            this.pendingSchematics.values().forEach(l -> l.forEach(r -> {
                r.state = ABORTED_STATE;
            }));
            this.pendingSchematics.clear();
            this.loadedSchematicsByFile.values().forEach(s -> {
                s.activeReaders.forEach(r -> {
                    r.state = ABORTED_STATE;
                });
                s.activeReaders.clear();
            });
            this.loadedSchematicsByFile.clear();
            this.loadedSchematics.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SchematicReader startReading(String fileName) {
        if (this.isShuttingDown || fileName.isEmpty()) {
            return new SchematicReader(fileName, ABORTED_STATE);
        }
        Object object = this.lock;
        synchronized (object) {
            Schematic loaded = this.loadedSchematics.get(fileName);
            if (loaded != null && !loaded.wasModifiedSinceLoading()) {
                return loaded.addReader(new SchematicReader(fileName, new ReaderStateBusy(loaded)));
            }
            SchematicReader reader = new SchematicReader(fileName, WAITING_STATE);
            this.pendingSchematics.computeIfAbsent(fileName, n -> new ArrayList()).add(reader);
            this.lock.notifyAll();
            return reader;
        }
    }

    private static interface ReaderState {
        public boolean isDone();

        public boolean hasError();

        default public void abort(SchematicReader reader) {
        }

        default public SchematicBlock next() {
            return null;
        }
    }

    private class LoaderThread
    extends Thread {
        public LoaderThread() {
            this.setName("TrainCarts schematic loader thread");
            this.setDaemon(true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            while (true) {
                List readers;
                Path schematicFilePath;
                String inputFileName;
                Object object = WorldEditSchematicLoader.this.lock;
                synchronized (object) {
                    if (WorldEditSchematicLoader.this.isShuttingDown) {
                        WorldEditSchematicLoader.this.pendingSchematics.values().forEach(readerList -> readerList.forEach(SchematicReader::abort));
                        WorldEditSchematicLoader.this.pendingSchematics.clear();
                        return;
                    }
                    Iterator iter = WorldEditSchematicLoader.this.pendingSchematics.entrySet().iterator();
                    if (!iter.hasNext()) {
                        try {
                            WorldEditSchematicLoader.this.lock.wait(10000L);
                        }
                        catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                        continue;
                    }
                    Map.Entry loadEntry = iter.next();
                    inputFileName = (String)loadEntry.getKey();
                    try {
                        schematicFilePath = Paths.get(inputFileName, new String[0]);
                    }
                    catch (InvalidPathException ex) {
                        schematicFilePath = null;
                    }
                    readers = (List)loadEntry.getValue();
                }
                List<Path> searchPaths = Stream.of(WorldEdit.getInstance().getWorkingDirectoryPath("schematics").toAbsolutePath(), WorldEditSchematicLoader.this.tcSchematicsPath).filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).collect(Collectors.toList());
                if (schematicFilePath == null) {
                    searchPaths = Collections.emptyList();
                } else if (schematicFilePath.getParent() != null) {
                    searchPaths = this.applyToSearchPaths(searchPaths, schematicFilePath.getParent());
                    schematicFilePath = schematicFilePath.getFileName();
                }
                schematicFilePath = this.findFile(searchPaths, schematicFilePath);
                FileTime lastModifiedTime = null;
                if (schematicFilePath != null) {
                    try {
                        lastModifiedTime = Files.getLastModifiedTime(schematicFilePath, new LinkOption[0]);
                    }
                    catch (Throwable t) {
                        WorldEditSchematicLoader.this.plugin.getLogger().log(Level.WARNING, "Failed to read last modified date of " + schematicFilePath, t);
                    }
                }
                if (schematicFilePath != null && lastModifiedTime != null) {
                    this.loadSchematic(inputFileName, schematicFilePath, lastModifiedTime, readers);
                    continue;
                }
                Object object2 = WorldEditSchematicLoader.this.lock;
                synchronized (object2) {
                    WorldEditSchematicLoader.this.pendingSchematics.remove(inputFileName);
                    readers.forEach(SchematicReader::abort);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        private void loadSchematic(String inputFileName, Path schematicFilePath, FileTime lastModifiedTime, List<SchematicReader> readers) {
            Object object = WorldEditSchematicLoader.this.lock;
            // MONITORENTER : object
            Schematic loadedSchematic = (Schematic)WorldEditSchematicLoader.this.loadedSchematicsByFile.get(schematicFilePath);
            if (loadedSchematic != null) {
                if (loadedSchematic.lastModified.equals(lastModifiedTime)) {
                    WorldEditSchematicLoader.this.pendingSchematics.remove(inputFileName);
                    WorldEditSchematicLoader.this.loadedSchematics.put(inputFileName, loadedSchematic);
                    readers.forEach(loadedSchematic::addReader);
                    // MONITOREXIT : object
                    return;
                }
                loadedSchematic.remove(false);
            }
            // MONITOREXIT : object
            Clipboard clipboard = null;
            try {
                ClipboardFormat format = ClipboardFormats.findByFile((File)schematicFilePath.toFile());
                try (ClipboardReader reader = format.getReader(Files.newInputStream(schematicFilePath, new OpenOption[0]));){
                    clipboard = reader.read();
                }
                BlockVector3 dims = clipboard.getDimensions();
                loadedSchematic = new Schematic(schematicFilePath, lastModifiedTime, new IntVector3(dims.getX(), dims.getY(), dims.getZ()));
            }
            catch (Throwable t) {
                WorldEditSchematicLoader.this.plugin.getLogger().log(Level.SEVERE, "Failed to load schematic " + schematicFilePath, t);
                clipboard = null;
                loadedSchematic = new Schematic(schematicFilePath, lastModifiedTime, IntVector3.ZERO);
                loadedSchematic.done = true;
                loadedSchematic.error = true;
            }
            Object t = WorldEditSchematicLoader.this.lock;
            // MONITORENTER : t
            loadedSchematic.fileNames.add(inputFileName);
            WorldEditSchematicLoader.this.pendingSchematics.remove(inputFileName);
            WorldEditSchematicLoader.this.loadedSchematics.put(inputFileName, loadedSchematic);
            WorldEditSchematicLoader.this.loadedSchematicsByFile.put(schematicFilePath, loadedSchematic);
            readers.forEach(loadedSchematic::addReader);
            if (!loadedSchematic.hasActiveReaders()) {
                loadedSchematic.remove(true);
                // MONITOREXIT : t
                return;
            }
            // MONITOREXIT : t
            if (clipboard == null) return;
            BlockVector3 min = clipboard.getMinimumPoint();
            try {
                BlockIterator iter = new BlockIterator(loadedSchematic.dimensions);
                int checkReadersCounter = 0;
                while (!iter.done) {
                    BlockData blockData;
                    loadedSchematic.blockData[iter.index] = blockData = BlockData.fromBukkit((Object)BukkitAdapter.adapt((BlockStateHolder)clipboard.getBlock(min.add(iter.x, iter.y, iter.z))));
                    iter.advance();
                    if (++checkReadersCounter != 100) continue;
                    Object object2 = WorldEditSchematicLoader.this.lock;
                    // MONITORENTER : object2
                    if (WorldEditSchematicLoader.this.isShuttingDown) {
                        loadedSchematic.error = true;
                        // MONITOREXIT : object2
                        break;
                    }
                    if (!loadedSchematic.hasActiveReaders()) {
                        loadedSchematic.error = true;
                        loadedSchematic.remove(true);
                        // MONITOREXIT : object2
                        break;
                    }
                    // MONITOREXIT : object2
                }
            }
            catch (Throwable t2) {
                WorldEditSchematicLoader.this.plugin.getLogger().log(Level.SEVERE, "Failed to load schematic " + schematicFilePath, t2);
                loadedSchematic.error = true;
            }
            loadedSchematic.done = true;
        }

        private Path findFile(List<Path> searchPaths, Path fileName) {
            if (searchPaths.isEmpty()) {
                return null;
            }
            for (Path searchPath : searchPaths) {
                Path foundFile = searchPath.resolve(fileName);
                if (!Files.isRegularFile(foundFile, new LinkOption[0])) continue;
                return foundFile;
            }
            String baseNameToFind = this.findBaseName(fileName);
            for (Path searchPath : searchPaths) {
                try {
                    Optional<Path> result = Files.list(searchPath).filter(p -> this.findBaseName((Path)p).equals(baseNameToFind)).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).findFirst();
                    if (!result.isPresent()) continue;
                    return result.get().toAbsolutePath();
                }
                catch (Throwable t) {
                    WorldEditSchematicLoader.this.plugin.getLogger().log(Level.WARNING, "Failed to list schematics in " + searchPath, t);
                }
            }
            return null;
        }

        private List<Path> applyToSearchPaths(List<Path> searchPaths, Path subDir) {
            if (subDir.isAbsolute()) {
                boolean isAllowed = false;
                for (Path search : searchPaths) {
                    if (!subDir.startsWith(search)) continue;
                    isAllowed = true;
                    break;
                }
                if (isAllowed && Files.isDirectory(subDir, new LinkOption[0])) {
                    return Collections.singletonList(subDir);
                }
                return Collections.emptyList();
            }
            return searchPaths.stream().map(s -> {
                Path sub = s.resolve(subDir).toAbsolutePath();
                return sub.startsWith((Path)s) ? sub : null;
            }).filter(Objects::nonNull).filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).collect(Collectors.toList());
        }

        private String findBaseName(Path path) {
            String name = path.getFileName().toString().toLowerCase(Locale.ENGLISH);
            if (name.endsWith(".schem")) {
                return name.substring(0, name.length() - 6);
            }
            if (name.endsWith(".schematic")) {
                return name.substring(0, name.length() - 10);
            }
            if (name.endsWith(".")) {
                return name.substring(0, name.length() - 1);
            }
            return name;
        }
    }

    public class SchematicReader {
        private final String fileName;
        protected volatile ReaderState state;

        private SchematicReader(String fileName, ReaderState state) {
            this.fileName = fileName;
            this.state = state;
        }

        public String fileName() {
            return this.fileName;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void abort() {
            Object object = WorldEditSchematicLoader.this.lock;
            synchronized (object) {
                this.state.abort(this);
                this.state = ABORTED_STATE;
            }
        }

        public boolean isDone() {
            return this.state.isDone();
        }

        public boolean hasError() {
            return this.state.hasError();
        }

        public SchematicBlock next() {
            return this.state.next();
        }
    }

    public class Schematic {
        public final IntVector3 dimensions;
        public final Path schematicFilePath;
        protected final FileTime lastModified;
        protected long lastModifiedLastChecked;
        protected boolean wasModified;
        protected final BlockData[] blockData;
        protected boolean error = false;
        protected boolean done = false;
        protected long lastAccessed;
        protected final Set<SchematicReader> activeReaders = new HashSet<SchematicReader>();
        protected final List<String> fileNames = new ArrayList<String>();

        private Schematic(Path schematicFilePath, FileTime lastModified, IntVector3 dimensions) {
            int numOfBlocks = dimensions.x * dimensions.y * dimensions.z;
            if (numOfBlocks > 1000000) {
                throw new IllegalArgumentException("Schematic is too big (>1 million blocks): " + dimensions);
            }
            this.dimensions = dimensions;
            this.schematicFilePath = schematicFilePath;
            this.lastModified = lastModified;
            this.wasModified = false;
            this.lastModifiedLastChecked = this.lastAccessed = System.currentTimeMillis();
            this.blockData = new BlockData[numOfBlocks];
        }

        public boolean isDone() {
            return this.done;
        }

        public boolean hasError() {
            return this.error;
        }

        protected boolean wasModifiedSinceLoading() {
            if (this.wasModified) {
                return true;
            }
            long timeNow = System.currentTimeMillis();
            if (timeNow - this.lastModifiedLastChecked > 1000L) {
                try {
                    FileTime currTime = Files.getLastModifiedTime(this.schematicFilePath, new LinkOption[0]);
                    if (!currTime.equals(this.lastModified)) {
                        this.wasModified = true;
                        return true;
                    }
                    this.lastModifiedLastChecked = timeNow;
                }
                catch (Throwable t) {
                    this.wasModified = true;
                    return true;
                }
            }
            return false;
        }

        protected boolean canUnload(long currentTime) {
            return this.activeReaders.isEmpty() && currentTime - this.lastAccessed > 1800000L;
        }

        protected boolean hasActiveReaders() {
            return !this.activeReaders.isEmpty();
        }

        protected SchematicReader addReader(SchematicReader reader) {
            if (reader.state != ABORTED_STATE) {
                reader.state = new ReaderStateBusy(this);
                this.activeReaders.add(reader);
                this.lastAccessed = System.currentTimeMillis();
            }
            return reader;
        }

        protected void remove(boolean abortReaders) {
            for (String fileName : this.fileNames) {
                WorldEditSchematicLoader.this.loadedSchematics.remove(fileName, this);
            }
            WorldEditSchematicLoader.this.loadedSchematicsByFile.remove(this.schematicFilePath, this);
            this.fileNames.clear();
            if (abortReaders) {
                for (SchematicReader reader : this.activeReaders) {
                    reader.state = ABORTED_STATE;
                }
                this.activeReaders.clear();
            }
        }
    }

    private class ReaderStateBusy
    extends BlockIterator
    implements ReaderState {
        public final Schematic schematic;
        public boolean error;

        public ReaderStateBusy(Schematic schematic) {
            super(schematic.dimensions);
            this.schematic = schematic;
            this.error = false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public boolean hasError() {
            return this.error;
        }

        @Override
        public void abort(SchematicReader reader) {
            this.schematic.activeReaders.remove(reader);
            this.schematic.lastAccessed = System.currentTimeMillis();
        }

        @Override
        public SchematicBlock next() {
            if (this.done) {
                return null;
            }
            BlockData data = this.schematic.blockData[this.index];
            if (data != null) {
                SchematicBlock block = new SchematicBlock(this.schematic, this.x, this.y, this.z, data);
                this.advance();
                return block;
            }
            this.done = this.schematic.isDone();
            this.error = this.schematic.hasError();
            return null;
        }
    }

    private static class BlockIterator {
        public final int x_max;
        public final int y_max;
        public final int z_max;
        public int x;
        public int y;
        public int z;
        public int index;
        public boolean done;

        public BlockIterator(IntVector3 dimensions) {
            this.x_max = dimensions.x;
            this.y_max = dimensions.y;
            this.z_max = dimensions.z;
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.index = 0;
            this.done = this.x_max == 0 || this.y_max == 0 || this.z_max == 0;
        }

        public void advance() {
            ++this.index;
            if (++this.x == this.x_max) {
                this.x = 0;
                if (++this.z == this.z_max) {
                    this.z = 0;
                    if (++this.y == this.y_max) {
                        this.done = true;
                    }
                }
            }
        }
    }

    public static class SchematicBlock {
        public final Schematic schematic;
        public final int x;
        public final int y;
        public final int z;
        public final BlockData blockData;

        public SchematicBlock(Schematic schematic, int x, int y, int z, BlockData blockData) {
            this.schematic = schematic;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockData = blockData;
        }
    }
}

