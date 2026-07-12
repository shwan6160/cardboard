/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class FileOverlayView {
    private DirectoryEntry root = new DirectoryEntry("/");
    private final Set<String> absoluteFilePaths = new HashSet<String>();
    private final Map<String, DirectoryEntry> directories = new HashMap<String, DirectoryEntry>();
    private final List<String> overlayDirectories = new ArrayList<String>();

    FileOverlayView() {
    }

    public List<String> listFiles(String directoryPath, boolean deep) {
        DirectoryEntry directory = this.directories.get(directoryPath);
        if (directory != null) {
            return deep ? directory.deepNameList : directory.nameList;
        }
        return Collections.emptyList();
    }

    public boolean hasAbsoluteFile(String absoluteFilePath) {
        return this.absoluteFilePaths.contains(absoluteFilePath);
    }

    public String getAbsoluteFilePath(String filePath) {
        String name;
        DirectoryEntry directory;
        if (this.overlayDirectories.isEmpty()) {
            return filePath;
        }
        int directoryEnd = filePath.lastIndexOf(47);
        if (directoryEnd == filePath.length() - 1) {
            return filePath;
        }
        if (directoryEnd == -1) {
            directory = this.root;
            name = filePath;
        } else {
            directory = this.directories.get(filePath.substring(0, directoryEnd + 1));
            if (directory == null) {
                return filePath;
            }
            name = filePath.substring(directoryEnd + 1);
        }
        Entry e = directory.entriesWithOverlays.get(name);
        if (e instanceof FileEntry) {
            return ((FileEntry)e).absolutePath;
        }
        return filePath;
    }

    public void clear() {
        this.root = new DirectoryEntry("/");
        this.directories.clear();
        this.absoluteFilePaths.clear();
    }

    public void load(FileLoaderFunc loaderFunc) throws IOException {
        DirectoryTreeBuilder tree = new DirectoryTreeBuilder(new DirectoryEntry("/"));
        tree.load(loaderFunc);
        this.root = tree.root;
        this.directories.clear();
        this.absoluteFilePaths.clear();
        this.absoluteFilePaths.addAll(tree.absoluteFilePaths);
        this.applyOverlays();
        this.loadDirectoryTree();
    }

    public void addOverlay(String directory) {
        this.addOverlays(Collections.singletonList(directory));
    }

    public void addOverlays(Iterable<String> directories) {
        boolean changed = false;
        for (String directory : directories) {
            if (!directory.endsWith("/")) {
                directory = directory + "/";
            }
            if (directory.startsWith("/")) {
                directory = directory.substring(1);
            }
            if (directory.equals("/")) continue;
            this.overlayDirectories.remove(directory);
            this.overlayDirectories.add(directory);
            changed = true;
        }
        if (!changed) {
            return;
        }
        this.root.resetRecurse();
        this.directories.clear();
        this.applyOverlays();
        this.loadDirectoryTree();
    }

    private void applyOverlays() {
        for (String directoryPath : this.overlayDirectories) {
            DirectoryEntry directory = this.root.findDirectory(directoryPath);
            if (directory == null) continue;
            this.root = this.root.mergeWith(directory);
        }
    }

    private void loadDirectoryTree() {
        this.root.initDeepNameLists(true);
        this.directories.clear();
        this.directories.put("/", this.root);
        this.root.loadIntoDirectoriesMap(this.directories, "");
    }

    private static class DirectoryEntry
    extends Entry {
        public List<Entry> entries = new ArrayList<Entry>();
        public Map<String, Entry> entriesWithOverlays = null;
        public List<String> nameList = null;
        public List<String> deepNameList = null;

        public DirectoryEntry(String name) {
            super(name);
        }

        @Override
        public DirectoryEntry mergeWith(Entry other) {
            if (!(other instanceof DirectoryEntry)) {
                return this;
            }
            for (Entry subEntry : ((DirectoryEntry)other).entries) {
                this.entriesWithOverlays.compute(subEntry.name, (name, existingSubEntry) -> {
                    if (existingSubEntry != null) {
                        return existingSubEntry.mergeWith(subEntry);
                    }
                    return subEntry;
                });
            }
            return this;
        }

        public void makeReadOnly() {
            if (this.entries instanceof ArrayList) {
                ((ArrayList)this.entries).trimToSize();
                this.entries = Collections.unmodifiableList(this.entries);
            }
        }

        public void reset() {
            this.entriesWithOverlays = new HashMap<String, Entry>();
            this.entries.forEach(e -> this.entriesWithOverlays.put(e.name, (Entry)e));
            this.nameList = null;
            this.deepNameList = null;
        }

        public void resetRecurse() {
            this.reset();
            for (Entry e : this.entries) {
                if (!(e instanceof DirectoryEntry)) continue;
                ((DirectoryEntry)e).resetRecurse();
            }
        }

        public void loadIntoDirectoriesMap(Map<String, DirectoryEntry> directories, String currentPath) {
            for (Entry e : this.entriesWithOverlays.values()) {
                if (!(e instanceof DirectoryEntry)) continue;
                DirectoryEntry de = (DirectoryEntry)e;
                String dePath = currentPath + de.name;
                directories.put(dePath, de);
                de.loadIntoDirectoriesMap(directories, dePath);
            }
        }

        public List<String> initDeepNameLists(boolean isRoot) {
            if (this.deepNameList != null) {
                return this.deepNameList;
            }
            ArrayList nameList = new ArrayList(this.entriesWithOverlays.size());
            ArrayList deepNameList = new ArrayList(this.entriesWithOverlays.size());
            this.entriesWithOverlays.values().stream().sorted().forEachOrdered(e -> {
                nameList.add(e.name);
                deepNameList.add(e.name);
                if (e instanceof DirectoryEntry) {
                    DirectoryEntry subDir = (DirectoryEntry)e;
                    if (isRoot) {
                        deepNameList.addAll(subDir.initDeepNameLists(false));
                    } else {
                        for (String deepName : subDir.initDeepNameLists(false)) {
                            deepNameList.add(subDir.name + deepName);
                        }
                    }
                }
            });
            nameList.trimToSize();
            deepNameList.trimToSize();
            this.nameList = Collections.unmodifiableList(nameList);
            this.deepNameList = Collections.unmodifiableList(deepNameList);
            return this.deepNameList;
        }

        public DirectoryEntry findDirectory(String directory) {
            DirectorySplitter splitter = new DirectorySplitter(directory);
            DirectoryEntry curr = this;
            while (splitter.next()) {
                String name = splitter.currentDirectoryName();
                boolean found = false;
                for (Entry e : curr.entries) {
                    if (!(e instanceof DirectoryEntry) || !name.equals(e.name)) continue;
                    curr = (DirectoryEntry)e;
                    found = true;
                    break;
                }
                if (found) continue;
                return null;
            }
            return curr;
        }

        public DirectoryEntry addDirectory(String name) {
            DirectoryEntry e = new DirectoryEntry(name);
            this.entries.add(e);
            return e;
        }

        public void addFile(String directory, String name) {
            this.entries.add(new FileEntry(name, directory + name));
        }

        @Override
        public int compareTo(Entry o) {
            if (o instanceof DirectoryEntry) {
                return super.compareTo(o);
            }
            return -1;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Dir{").append(this.name).append("}: [").appendWithIndent(predicatesStr -> predicatesStr.appendLines(this.entriesWithOverlays.values())).append("\n]");
        }
    }

    private static abstract class Entry
    implements Comparable<Entry>,
    IndentedStringBuilder.AppendableToString {
        public final String name;

        public Entry(String name) {
            this.name = name;
        }

        public abstract Entry mergeWith(Entry var1);

        public final String toString() {
            return IndentedStringBuilder.toString(this);
        }

        @Override
        public int compareTo(Entry o) {
            return this.name.compareTo(o.name);
        }
    }

    private static class FileEntry
    extends Entry {
        public final String absolutePath;

        public FileEntry(String name, String absolutePath) {
            super(name);
            this.absolutePath = absolutePath;
        }

        @Override
        public Entry mergeWith(Entry other) {
            return other;
        }

        @Override
        public int compareTo(Entry o) {
            if (o instanceof FileEntry) {
                return super.compareTo(o);
            }
            return 1;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("File{").append(this.name).append(" -> ").append(this.absolutePath).append("}");
        }
    }

    private static final class DirectoryTreeBuilder {
        public final Map<String, DirectoryEntry> entries = new HashMap<String, DirectoryEntry>();
        public final Set<String> absoluteFilePaths = new HashSet<String>();
        public final DirectoryEntry root;

        public DirectoryTreeBuilder(DirectoryEntry root) {
            this.entries.put("/", root);
            this.root = root;
        }

        public DirectoryEntry add(String directory) {
            DirectoryEntry existing = this.entries.get(directory);
            if (existing != null) {
                return existing;
            }
            DirectorySplitter splitter = new DirectorySplitter(directory);
            DirectoryEntry entry = this.root;
            while (splitter.next()) {
                DirectoryEntry parentEntry = entry;
                entry = this.entries.computeIfAbsent(splitter.currentDirectoryPath(), subDirAbsolutePath -> parentEntry.addDirectory(splitter.currentDirectoryName()));
            }
            return entry;
        }

        public void load(FileLoaderFunc loaderFunc) throws IOException {
            loaderFunc.load((String path) -> {
                int directoryIdx = path.lastIndexOf(47);
                if (directoryIdx == path.length() - 1) {
                    this.add(path);
                } else if (directoryIdx == -1) {
                    this.absoluteFilePaths.add(path);
                    this.root.addFile("/", path);
                } else {
                    this.absoluteFilePaths.add(path);
                    String directory = path.substring(0, directoryIdx + 1);
                    this.add(directory).addFile(directory, path.substring(directoryIdx + 1));
                }
            });
            this.entries.values().forEach(DirectoryEntry::makeReadOnly);
            this.entries.values().forEach(DirectoryEntry::reset);
        }
    }

    @FunctionalInterface
    public static interface FileLoaderFunc {
        public void load(FileLoader var1) throws IOException;
    }

    @FunctionalInterface
    public static interface FileLoader {
        public void add(String var1);
    }

    private static class DirectorySplitter {
        private final String directory;
        private int currIdx = 0;
        private int lastIdx = 0;

        public DirectorySplitter(String directory) {
            this.directory = directory;
        }

        public boolean next() {
            this.lastIdx = this.currIdx;
            this.currIdx = this.directory.indexOf(47, this.lastIdx) + 1;
            return this.currIdx != 0;
        }

        public String currentDirectoryPath() {
            return this.directory.substring(0, this.currIdx);
        }

        public String currentDirectoryName() {
            return this.directory.substring(this.lastIdx, this.currIdx);
        }
    }
}

