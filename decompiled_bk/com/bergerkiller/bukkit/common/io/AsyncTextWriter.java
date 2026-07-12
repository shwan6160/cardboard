/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class AsyncTextWriter {
    public static CompletableFuture<Void> writeSafe(File file, String inputData) {
        return AsyncTextWriter.writeSafe(file, CharBuffer.wrap(inputData));
    }

    public static CompletableFuture<Void> writeSafe(final File file, CharBuffer inputData) {
        final File tempFile = new File(file.getAbsoluteFile().getParentFile(), file.getName() + "." + System.currentTimeMillis() + ".tmp");
        CompletableFuture<Void> tempWrite = AsyncTextWriter.write(tempFile, inputData);
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        tempWrite.handle(new BiFunction<Void, Throwable, Void>(){

            @Override
            public Void apply(Void ignored, Throwable baseThrown) {
                if (baseThrown != null) {
                    future.completeExceptionally(baseThrown);
                    return null;
                }
                this.move();
                return null;
            }

            public void move() {
                try {
                    StreamUtil.atomicReplace(tempFile, file);
                    future.complete(null);
                }
                catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        return future;
    }

    public static CompletableFuture<Void> write(File file, String inputData) {
        return AsyncTextWriter.write(file, CharBuffer.wrap(inputData));
    }

    public static CompletableFuture<Void> write(File file, CharBuffer inputData) {
        try {
            if (CommonPlugin.hasInstance() && CommonPlugin.getInstance().forceSynchronousSaving()) {
                try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);){
                    int remaining;
                    char[] tmp = new char[4096];
                    while ((remaining = Math.min(tmp.length, inputData.remaining())) > 0) {
                        inputData.get(tmp, 0, remaining);
                        writer.write(tmp, 0, remaining);
                    }
                }
                return CompletableFuture.completedFuture(null);
            }
            return CommonPlugin.runIOTaskAsync(() -> {
                int bufferSize;
                int len = inputData.length();
                for (bufferSize = 512; len > bufferSize && bufferSize < 16384; bufferSize <<= 1) {
                }
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
                CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
                file.getAbsoluteFile().getParentFile().mkdirs();
                try (FileOutputStream stream = new FileOutputStream(file, false);
                     FileChannel channel = stream.getChannel();){
                    boolean finished = false;
                    while (true) {
                        ((Buffer)byteBuffer).clear();
                        CoderResult result = finished ? encoder.flush(byteBuffer) : encoder.encode(inputData, byteBuffer, true);
                        if (result.isError()) {
                            result.throwException();
                        }
                        ((Buffer)byteBuffer).flip();
                        while (byteBuffer.hasRemaining()) {
                            if (channel.write(byteBuffer) > 0) continue;
                            try {
                                Thread.sleep(50L);
                            }
                            catch (InterruptedException interruptedException) {}
                        }
                        if (finished) break;
                        if (!result.isUnderflow()) continue;
                        finished = true;
                    }
                    channel.force(true);
                }
            });
        }
        catch (Throwable t) {
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            future.completeExceptionally(t);
            return future;
        }
    }
}

