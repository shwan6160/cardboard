/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.util.BaseABGRToInt;
import com.bergerkiller.bukkit.common.map.util.BaseARGBToInt;
import com.bergerkiller.bukkit.common.map.util.BaseBGRToInt;
import com.bergerkiller.bukkit.common.map.util.BaseRGBToInt;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public interface RGBColorToIntConversion {
    public static final RGBColorToIntConversion BGR = SIMDLoader.access$000(BaseBGRToInt::new, "bgr");
    public static final RGBColorToIntConversion RGB = SIMDLoader.access$000(BaseRGBToInt::new, "rgb");
    public static final RGBColorToIntConversion ABGR = SIMDLoader.access$000(BaseABGRToInt::new, "abgr");
    public static final RGBColorToIntConversion ARGB = SIMDLoader.access$000(BaseARGBToInt::new, "argb");

    public boolean hasTransparency();

    default public boolean isUsingSIMD() {
        return false;
    }

    default public RGBColorToIntConversion noSIMD() {
        return this;
    }

    public int singleBytesInputLength();

    public int singleBytesToInt(byte[] var1, int var2);

    public int singleIntToInt(int var1);

    default public int byteBlockInputLength() {
        return 32 * this.singleBytesInputLength();
    }

    default public int byteBlockInputMinimumLength() {
        return 32 * this.singleBytesInputLength();
    }

    default public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
        int len = this.singleBytesInputLength();
        for (int i = 0; i < 32; ++i) {
            buffer[i] = this.singleBytesToInt(input, inputOffset);
            inputOffset += len;
        }
        return inputOffset;
    }

    default public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
        for (int i = 0; i < 32; ++i) {
            buffer[i] = this.singleIntToInt(input[inputOffset++]);
        }
        return inputOffset;
    }

    default public void decode(int[] data, int pixelCount, final RGBColorConsumer consumer) {
        new Decoder(this){

            @Override
            public void onPixel(int index, int rgba) {
                consumer.onPixel(index, rgba);
            }
        }.decode(data, pixelCount);
    }

    default public void decode(byte[] data, int pixelCount, final RGBColorConsumer consumer) {
        new Decoder(this){

            @Override
            public void onPixel(int index, int rgba) {
                consumer.onPixel(index, rgba);
            }
        }.decode(data, pixelCount);
    }

    @FunctionalInterface
    public static interface RGBColorConsumer {
        public void onPixel(int var1, int var2);
    }

    public static class SIMDLoader {
        private static Throwable simdError = null;

        public static Throwable getError() {
            ARGB.hasTransparency();
            return simdError;
        }

        private static RGBColorToIntConversion tryCreateSIMD(Supplier<RGBColorToIntConversion> base, String simdFactoryName) {
            String simdName = RGBColorToIntConversion.class.getName();
            simdName = simdName.substring(0, simdName.lastIndexOf(46)) + ".SIMDColorConversion";
            try {
                if (!SIMDLoader.isVectorModulePresent()) {
                    throw new UnsupportedOperationException("Incubator vector module is not loaded");
                }
                Class<?> simdColorConversionType = Class.forName(simdName);
                Method factoryMethod = simdColorConversionType.getMethod(simdFactoryName, new Class[0]);
                factoryMethod.setAccessible(true);
                return (RGBColorToIntConversion)factoryMethod.invoke(null, new Object[0]);
            }
            catch (Throwable t) {
                simdError = t;
                return base.get();
            }
        }

        private static boolean isVectorModulePresent() {
            try {
                ModuleLayer layer = ModuleLayer.boot();
                Optional<Module> module = layer.findModule("jdk.incubator.vector");
                return module.isPresent();
            }
            catch (Throwable t) {
                return false;
            }
        }

        static /* synthetic */ RGBColorToIntConversion access$000(Supplier x0, String x1) {
            return SIMDLoader.tryCreateSIMD(x0, x1);
        }
    }

    public static abstract class Decoder
    implements RGBColorConsumer {
        private final RGBColorToIntConversion converter;
        private int parallelism;

        public Decoder(RGBColorToIntConversion converter) {
            this.converter = converter;
            this.parallelism = Runtime.getRuntime().availableProcessors();
        }

        public Decoder parallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        private int computeParallelism(int pixelCount) {
            return Math.min(this.parallelism, pixelCount / 128);
        }

        public void decode(byte[] data, int pixelCount) {
            int bytePosition = 0;
            int pixelPosition = 0;
            int totalBytes = pixelCount * this.converter.singleBytesInputLength() - this.converter.byteBlockInputMinimumLength();
            int parallelism = this.computeParallelism(pixelCount);
            if (parallelism > 1) {
                int blocksPerThread = totalBytes / (parallelism * this.converter.byteBlockInputLength());
                int bytesPerThread = this.converter.byteBlockInputLength() * blocksPerThread;
                int pixelsPerThread = 32 * blocksPerThread;
                IntStream.range(0, parallelism).parallel().forEach(threadId -> {
                    int threadBytePosition = threadId * bytesPerThread;
                    int threadEndPosition = threadBytePosition + bytesPerThread;
                    int threadPixelPosition = threadId * pixelsPerThread;
                    int[] buff = new int[32];
                    while (threadBytePosition < threadEndPosition) {
                        threadBytePosition = this.converter.byteBlockConvert32Pixels(data, threadBytePosition, buff);
                        for (int i = 0; i < 32; ++i) {
                            this.onPixel(threadPixelPosition++, buff[i]);
                        }
                    }
                });
                bytePosition += parallelism * bytesPerThread;
                pixelPosition += parallelism * pixelsPerThread;
            } else {
                int[] buff = new int[32];
                while (bytePosition < totalBytes) {
                    bytePosition = this.converter.byteBlockConvert32Pixels(data, bytePosition, buff);
                    for (int i = 0; i < 32; ++i) {
                        this.onPixel(pixelPosition++, buff[i]);
                    }
                }
            }
            int step = this.converter.singleBytesInputLength();
            int limit = pixelCount * step;
            while (bytePosition < limit) {
                this.onPixel(pixelPosition++, this.converter.singleBytesToInt(data, bytePosition));
                bytePosition += step;
            }
        }

        public void decode(int[] data, int pixelCount) {
            int intPosition = 0;
            int pixelPosition = 0;
            int totalIntegers = pixelCount - 32;
            int parallelism = this.computeParallelism(pixelCount);
            if (parallelism > 1) {
                int blocksPerThread = totalIntegers / (parallelism * 32);
                int pixelsPerThread = 32 * blocksPerThread;
                IntStream.range(0, parallelism).parallel().forEach(threadId -> {
                    int threadIntPosition = threadId * pixelsPerThread;
                    int threadEndPosition = threadIntPosition + pixelsPerThread;
                    int threadPixelPosition = threadId * pixelsPerThread;
                    int[] buff = new int[32];
                    while (threadIntPosition < threadEndPosition) {
                        threadIntPosition = this.converter.intBlockConvert32Pixels(data, threadIntPosition, buff);
                        for (int i = 0; i < 32; ++i) {
                            this.onPixel(threadPixelPosition++, buff[i]);
                        }
                    }
                });
                intPosition += parallelism * pixelsPerThread;
                pixelPosition += parallelism * pixelsPerThread;
            } else {
                int[] buff = new int[32];
                while (intPosition < totalIntegers) {
                    intPosition = this.converter.intBlockConvert32Pixels(data, intPosition, buff);
                    for (int i = 0; i < 32; ++i) {
                        this.onPixel(pixelPosition++, buff[i]);
                    }
                }
            }
            while (intPosition < pixelCount) {
                this.onPixel(pixelPosition++, this.converter.singleIntToInt(data[intPosition]));
                ++intPosition;
            }
        }
    }
}

