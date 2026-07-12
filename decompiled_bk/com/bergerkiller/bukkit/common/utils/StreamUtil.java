/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.UnmodifiableListCollector;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class StreamUtil {
    public static UUID readUUID(DataInputStream stream) throws IOException {
        return new UUID(stream.readLong(), stream.readLong());
    }

    public static void writeUUID(DataOutputStream stream, UUID uuid) throws IOException {
        stream.writeLong(uuid.getMostSignificantBits());
        stream.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeIndent(BufferedWriter writer, int indent) throws IOException {
        for (int i = 0; i < indent; ++i) {
            writer.write(32);
        }
    }

    public static Iterable<File> listFiles(File directory) {
        return Arrays.asList(directory.listFiles());
    }

    public static long getFileSize(File file) {
        if (!file.exists()) {
            return 0L;
        }
        if (file.isDirectory()) {
            long size = 0L;
            for (File subfile : StreamUtil.listFiles(file)) {
                size += StreamUtil.getFileSize(subfile);
            }
            return size;
        }
        return file.length();
    }

    public static List<File> deleteFile(File file) {
        if (file.isDirectory()) {
            ArrayList<File> failFiles = new ArrayList<File>();
            StreamUtil.deleteFileList(file, failFiles);
            return Collections.unmodifiableList(failFiles);
        }
        if (file.delete()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(file));
    }

    private static boolean deleteFileList(File file, List<File> failFiles) {
        if (file.isDirectory()) {
            boolean success = true;
            for (File subFile : StreamUtil.listFiles(file)) {
                success &= StreamUtil.deleteFileList(subFile, failFiles);
            }
            if (success) {
                file.delete();
            }
            return success;
        }
        if (file.delete()) {
            return true;
        }
        failFiles.add(file);
        return false;
    }

    public static FileOutputStream createOutputStream(File file) throws IOException, SecurityException {
        return StreamUtil.createOutputStream(file, false);
    }

    public static FileOutputStream createOutputStream(File file, boolean append) throws IOException, SecurityException {
        File directory = file.getAbsoluteFile().getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create the parent directory of the file");
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create the new file to write to");
        }
        return new FileOutputStream(file, append);
    }

    public static boolean tryCopyFile(File sourceLocation, File targetLocation) {
        try {
            StreamUtil.copyFile(sourceLocation, targetLocation);
            return true;
        }
        catch (IOException ex) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to copy file " + sourceLocation + " to " + targetLocation, ex);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copyFile(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }
            for (File subFile : StreamUtil.listFiles(sourceLocation)) {
                String subFileName = subFile.getName();
                StreamUtil.copyFile(new File(sourceLocation, subFileName), new File(targetLocation, subFileName));
            }
        } else {
            FileInputStream input = null;
            FileOutputStream output = null;
            AbstractInterruptibleChannel inputChannel = null;
            AbstractInterruptibleChannel outputChannel = null;
            try {
                input = new FileInputStream(sourceLocation);
                inputChannel = input.getChannel();
                output = StreamUtil.createOutputStream(targetLocation);
                outputChannel = output.getChannel();
                long transfered = 0L;
                long bytes = ((FileChannel)inputChannel).size();
                while (transfered < bytes) {
                    ((FileChannel)outputChannel).position(transfered += ((FileChannel)outputChannel).transferFrom((ReadableByteChannel)((Object)inputChannel), 0L, ((FileChannel)inputChannel).size()));
                }
            }
            finally {
                if (inputChannel != null) {
                    inputChannel.close();
                } else if (input != null) {
                    input.close();
                }
                if (outputChannel != null) {
                    outputChannel.close();
                } else if (output != null) {
                    output.close();
                }
            }
        }
    }

    public static File getFileIgnoreCase(File parent, String child) {
        if (LogicUtil.nullOrEmpty(child)) {
            return parent;
        }
        File childFile = new File(parent, child);
        if (!childFile.exists() && parent.exists()) {
            int firstFolderIdx = child.indexOf(File.separator);
            if (firstFolderIdx != -1) {
                File newParent = StreamUtil.getFileIgnoreCase(parent, child.substring(0, firstFolderIdx));
                String newChild = child.substring(firstFolderIdx + 1);
                return StreamUtil.getFileIgnoreCase(newParent, newChild);
            }
            for (String childName : parent.list()) {
                if (!childName.equalsIgnoreCase(child)) continue;
                return new File(parent, childName);
            }
        }
        return childFile;
    }

    public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
        return UnmodifiableListCollector.INSTANCE;
    }

    public static DeflaterOutputStream createDeflaterOutputStreamWithCompressionLevel(OutputStream stream, int deflaterCompressionLevel) throws IOException {
        return new DeflaterOutputStream(stream, new Deflater(deflaterCompressionLevel), 512, false);
    }

    public static void atomicReplace(File fromFile, File toFile) throws IOException {
        try {
            Files.move(fromFile.toPath(), toFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        catch (UnsupportedOperationException | AtomicMoveNotSupportedException exception) {
            if (toFile.delete() && fromFile.renameTo(toFile)) {
                return;
            }
            if (StreamUtil.tryCopyFile(fromFile, toFile)) {
                fromFile.delete();
                return;
            }
            throw new IOException("Failed to move " + fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath());
        }
    }
}

