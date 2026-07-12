/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

@Deprecated
public class AsyncTextWriter_nio {
    private final AsynchronousFileChannel _file;
    private final CharsetEncoder _encoder;
    private final CharBuffer _inputData;
    private final ByteBuffer _writeBufferA;
    private final ByteBuffer _writeBufferB;
    private final CompletableFuture<Void> _future;
    private FileLock _lock;
    private volatile long _position;
    private volatile boolean _finishedReading;
    private volatile boolean _finishedEncoding;
    private static final AsyncTextWriterStep<FileLock> LOCK_FILE = new AsyncTextWriterStep<FileLock>(){

        @Override
        public void start(AsyncTextWriter_nio writer) {
            writer._file.lock(writer, this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void completed(FileLock result, AsyncTextWriter_nio writer) {
            AsyncTextWriter_nio asyncTextWriter_nio = writer;
            synchronized (asyncTextWriter_nio) {
                writer._lock = result;
                writer.encode(writer.getBufferA());
                writer.start(writer._finishedEncoding ? FINISH_BUFFER_A : WRITE_BUFFER_A);
            }
        }
    };
    private static final AsyncTestWriterStepWriter WRITE_BUFFER_A = new AsyncTestWriterStepWriter(AsyncTextWriter_nio::getBufferA){

        @Override
        public void start(AsyncTextWriter_nio writer) {
            super.start(writer);
            writer.encode(writer.getBufferB());
        }

        @Override
        public void next(AsyncTextWriter_nio writer) {
            writer.start(writer._finishedEncoding ? FINISH_BUFFER_B : WRITE_BUFFER_B);
        }
    };
    private static final AsyncTestWriterStepWriter WRITE_BUFFER_B = new AsyncTestWriterStepWriter(AsyncTextWriter_nio::getBufferB){

        @Override
        public void start(AsyncTextWriter_nio writer) {
            super.start(writer);
            writer.encode(writer.getBufferA());
        }

        @Override
        public void next(AsyncTextWriter_nio writer) {
            writer.start(writer._finishedEncoding ? FINISH_BUFFER_A : WRITE_BUFFER_A);
        }
    };
    private static final AsyncTestWriterStepWriter FINISH_BUFFER_A = new AsyncTestWriterStepWriter(AsyncTextWriter_nio::getBufferA){

        @Override
        public void next(AsyncTextWriter_nio writer) {
            writer.close();
            writer._future.complete(null);
        }
    };
    private static final AsyncTestWriterStepWriter FINISH_BUFFER_B = new AsyncTestWriterStepWriter(AsyncTextWriter_nio::getBufferB){

        @Override
        public void next(AsyncTextWriter_nio writer) {
            writer.close();
            writer._future.complete(null);
        }
    };

    private AsyncTextWriter_nio(AsynchronousFileChannel file, CharBuffer inputData, int bufferSize) {
        this._file = file;
        this._encoder = StandardCharsets.UTF_8.newEncoder();
        this._inputData = inputData;
        this._writeBufferA = ByteBuffer.allocateDirect(bufferSize);
        this._writeBufferB = ByteBuffer.allocateDirect(bufferSize);
        this._future = new CompletableFuture();
        this._lock = null;
        this._position = 0L;
        this._finishedReading = false;
        this._finishedEncoding = false;
    }

    private synchronized void fail(Throwable ex) {
        if (!this._future.isCompletedExceptionally()) {
            this._future.completeExceptionally(ex);
            this.close();
        }
    }

    private void close() {
        try {
            if (this._lock != null) {
                this._lock.release();
                this._lock = null;
            }
        }
        catch (Throwable t) {
            this.fail(t);
        }
        try {
            this._file.force(true);
            this._file.close();
        }
        catch (Throwable t) {
            this.fail(t);
        }
    }

    private ByteBuffer getBufferA() {
        return this._writeBufferA;
    }

    private ByteBuffer getBufferB() {
        return this._writeBufferB;
    }

    private void start(AsyncTextWriterStep<?> step) {
        step.start(this);
    }

    private void encode(ByteBuffer buffer) {
        CoderResult result;
        ((Buffer)buffer).clear();
        if (this._finishedReading) {
            result = this._encoder.flush(buffer);
        } else {
            result = this._encoder.encode(this._inputData, buffer, true);
            if (result.isUnderflow()) {
                this._finishedReading = true;
                result = this._encoder.flush(buffer);
            }
        }
        ((Buffer)buffer).flip();
        if (result.isError()) {
            this._finishedEncoding = true;
            try {
                result.throwException();
            }
            catch (Throwable t) {
                this._future.completeExceptionally(t);
            }
        } else if (result.isUnderflow()) {
            this._finishedEncoding = true;
        }
    }

    public static CompletableFuture<Void> writeSafe(File file, String inputData) {
        return AsyncTextWriter_nio.writeSafe(file, CharBuffer.wrap(inputData));
    }

    public static CompletableFuture<Void> writeSafe(final File file, CharBuffer inputData) {
        final File tempFile = new File(file.getAbsoluteFile().getParentFile(), file.getName() + "." + System.currentTimeMillis() + ".tmp");
        CompletableFuture<Void> tempWrite = AsyncTextWriter_nio.write(tempFile, inputData);
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
                    Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    future.complete(null);
                    return;
                }
                catch (UnsupportedOperationException | AtomicMoveNotSupportedException exception) {
                }
                catch (Throwable t) {
                    future.completeExceptionally(t);
                    return;
                }
                if (file.delete() && tempFile.renameTo(file)) {
                    future.complete(null);
                    return;
                }
                if (StreamUtil.tryCopyFile(tempFile, file)) {
                    tempFile.delete();
                    future.complete(null);
                    return;
                }
                future.completeExceptionally(new IOException("Failed to move " + tempFile.getAbsolutePath() + " to " + file.getAbsolutePath()));
            }
        });
        return future;
    }

    public static CompletableFuture<Void> write(File file, String inputData) {
        return AsyncTextWriter_nio.write(file, CharBuffer.wrap(inputData));
    }

    public static CompletableFuture<Void> write(File file, CharBuffer inputData) {
        try {
            int bufferSize;
            file.getParentFile().mkdirs();
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
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            int len = inputData.length();
            for (bufferSize = 512; len > bufferSize && bufferSize < 16384; bufferSize <<= 1) {
            }
            AsyncTextWriter_nio writer = new AsyncTextWriter_nio(fileChannel, inputData, bufferSize);
            writer.start(LOCK_FILE);
            return writer._future;
        }
        catch (Throwable t) {
            CompletableFuture<Void> future = new CompletableFuture<Void>();
            future.completeExceptionally(t);
            return future;
        }
    }

    private static abstract class AsyncTestWriterStepWriter
    extends AsyncTextWriterStep<Integer> {
        private final Function<AsyncTextWriter_nio, ByteBuffer> _getBufferFunction;

        public AsyncTestWriterStepWriter(Function<AsyncTextWriter_nio, ByteBuffer> getBufferFunction) {
            this._getBufferFunction = getBufferFunction;
        }

        protected abstract void next(AsyncTextWriter_nio var1);

        @Override
        public void start(AsyncTextWriter_nio writer) {
            writer._file.write(this._getBufferFunction.apply(writer), writer._position, writer, this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void completed(Integer result, AsyncTextWriter_nio writer) {
            int num = result;
            if (num <= 0) {
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException interruptedException) {}
            } else {
                writer._position += num;
            }
            AsyncTextWriter_nio asyncTextWriter_nio = writer;
            synchronized (asyncTextWriter_nio) {
                ByteBuffer buffer = this._getBufferFunction.apply(writer);
                if (buffer.remaining() > 0) {
                    writer._file.write(buffer, writer._position, writer, this);
                } else {
                    this.next(writer);
                }
            }
        }
    }

    private static abstract class AsyncTextWriterStep<T>
    implements CompletionHandler<T, AsyncTextWriter_nio> {
        private AsyncTextWriterStep() {
        }

        public abstract void start(AsyncTextWriter_nio var1);

        @Override
        public final void failed(Throwable exc, AsyncTextWriter_nio writer) {
            writer.fail(exc);
        }
    }
}

