/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.archive.FileOverlayView;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MapResourcePackDirectoryArchive
implements MapResourcePackArchive {
    private final File directory;
    private final FileOverlayView overlayView = new FileOverlayView();

    public MapResourcePackDirectoryArchive(File directory) {
        this.directory = directory;
    }

    @Override
    public String name() {
        return "Directory: " + this.directory.getName();
    }

    @Override
    public void load(boolean lazy) {
        this.overlayView.clear();
        if (this.directory.exists()) {
            try {
                this.overlayView.load(loader -> {
                    final Path rootPath = this.directory.toPath();
                    Files.walkFileTree(rootPath, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){
                        final /* synthetic */ MapResourcePackDirectoryArchive this$0;
                        {
                            this.this$0 = this$0;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            loader.add(rootPath.relativize(file).toString());
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            if (!dir.equals(rootPath)) {
                                loader.add(rootPath.relativize(dir) + "/");
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                });
            }
            catch (IOException | SecurityException ex) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack folder " + this.directory, ex);
            }
        }
    }

    @Override
    public MapResourcePackArchive.ArchiveResource openResource(String path) {
        String absoluteFilePath = this.overlayView.getAbsoluteFilePath(path);
        if (!this.overlayView.hasAbsoluteFile(absoluteFilePath)) {
            return null;
        }
        return () -> {
            File sub = new File(this.directory, absoluteFilePath);
            if (!sub.isFile()) {
                throw new IOException("Unexpected resource not found: " + path);
            }
            return new FileInputStream(sub);
        };
    }

    @Override
    public void configure(MapResourcePack.Metadata metadata) {
        this.overlayView.addOverlays(metadata.getUsedOverlays().stream().map(o -> o.directory).collect(Collectors.toList()));
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        return this.overlayView.listFiles(folder, deep);
    }
}

