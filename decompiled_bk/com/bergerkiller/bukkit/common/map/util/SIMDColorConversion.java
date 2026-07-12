/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jdk.incubator.vector.ByteVector
 *  jdk.incubator.vector.IntVector
 *  jdk.incubator.vector.VectorShuffle
 *  jdk.incubator.vector.VectorSpecies
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.util.BaseABGRToInt;
import com.bergerkiller.bukkit.common.map.util.BaseARGBToInt;
import com.bergerkiller.bukkit.common.map.util.BaseBGRToInt;
import com.bergerkiller.bukkit.common.map.util.BaseRGBToInt;
import com.bergerkiller.bukkit.common.map.util.RGBColorToIntConversion;
import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorShuffle;
import jdk.incubator.vector.VectorSpecies;

abstract class SIMDColorConversion
implements RGBColorToIntConversion {
    private final boolean hasTransparency;
    protected final RGBColorToIntConversion base;
    protected final VectorSpecies<Byte> byteSpecies;
    protected final VectorSpecies<Integer> intSpecies;
    protected final VectorShuffle<Byte> shuffle;
    protected final VectorShuffle<Byte> intShuffle;

    public static RGBColorToIntConversion bgr() {
        return SIMDColorConversion.opaque(new BaseBGRToInt(), new int[]{2, 1, 0}, new int[]{0, 1, 2, 3});
    }

    public static RGBColorToIntConversion rgb() {
        return SIMDColorConversion.opaque(new BaseRGBToInt(), new int[]{0, 1, 2}, new int[]{2, 1, 0, 3});
    }

    public static RGBColorToIntConversion abgr() {
        return SIMDColorConversion.transparent(new BaseABGRToInt(), new int[]{3, 2, 1, 0}, new int[]{0, 1, 2, 3});
    }

    public static RGBColorToIntConversion argb() {
        return SIMDColorConversion.transparent(new BaseARGBToInt(), new int[]{1, 2, 3, 0}, new int[]{2, 1, 0, 3});
    }

    private static RGBColorToIntConversion transparent(RGBColorToIntConversion base, int[] byte_rgb, int[] int_rgb) {
        int byteVectorLength = ByteVector.SPECIES_PREFERRED.length();
        if (byteVectorLength == 8) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 16; ++i) {
                        ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 2);
                        inputOffset += 8;
                    }
                    return inputOffset;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 16; ++i) {
                        IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 2);
                        inputOffset += 2;
                    }
                    return inputOffset;
                }
            };
        }
        if (byteVectorLength == 16) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 8; ++i) {
                        ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 4);
                        inputOffset += 16;
                    }
                    return inputOffset;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 8; ++i) {
                        IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 4);
                        inputOffset += 4;
                    }
                    return inputOffset;
                }
            };
        }
        if (byteVectorLength == 64) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 2; ++i) {
                        ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 16);
                        inputOffset += 64;
                    }
                    return inputOffset;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 2; ++i) {
                        IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 16);
                        inputOffset += 16;
                    }
                    return inputOffset;
                }
            };
        }
        if (byteVectorLength == 128) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, 0);
                    return inputOffset + 128;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, 0);
                    return inputOffset + 32;
                }
            };
        }
        return new SIMDColorConversion(ByteVector.SPECIES_256, base, byte_rgb, int_rgb){

            @Override
            public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                for (int i = 0; i < 4; ++i) {
                    ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 8);
                    inputOffset += 32;
                }
                return inputOffset;
            }

            @Override
            public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                for (int i = 0; i < 4; ++i) {
                    IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 8);
                    inputOffset += 8;
                }
                return inputOffset;
            }
        };
    }

    private static RGBColorToIntConversion opaque(RGBColorToIntConversion base, int[] byte_rgb, int[] int_rgb) {
        int byteVectorLength = ByteVector.SPECIES_PREFERRED.length();
        if (byteVectorLength == 8) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 16; ++i) {
                        ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).withLane(7, (byte)0).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 2);
                        inputOffset += 6;
                    }
                    return inputOffset;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 16; ++i) {
                        IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().withLane(7, (byte)0).rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 2);
                        inputOffset += 2;
                    }
                    return inputOffset;
                }
            };
        }
        if (byteVectorLength == 16) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 8; ++i) {
                        ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).withLane(15, (byte)0).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 4);
                        inputOffset += 12;
                    }
                    return inputOffset;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 8; ++i) {
                        IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().withLane(15, (byte)0).rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 4);
                        inputOffset += 4;
                    }
                    return inputOffset;
                }
            };
        }
        if (byteVectorLength == 64) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 2; ++i) {
                        ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).withLane(63, (byte)0).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 16);
                        inputOffset += 48;
                    }
                    return inputOffset;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    for (int i = 0; i < 2; ++i) {
                        IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().withLane(63, (byte)0).rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 16);
                        inputOffset += 16;
                    }
                    return inputOffset;
                }
            };
        }
        if (byteVectorLength == 128) {
            return new SIMDColorConversion(ByteVector.SPECIES_PREFERRED, base, byte_rgb, int_rgb){

                @Override
                public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                    ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).withLane(127, (byte)0).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, 0);
                    return inputOffset + 96;
                }

                @Override
                public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                    IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().withLane(127, (byte)0).rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, 0);
                    return inputOffset + 32;
                }
            };
        }
        return new SIMDColorConversion(ByteVector.SPECIES_256, base, byte_rgb, int_rgb){

            @Override
            public int byteBlockConvert32Pixels(byte[] input, int inputOffset, int[] buffer) {
                for (int i = 0; i < 4; ++i) {
                    ByteVector.fromArray((VectorSpecies)this.byteSpecies, (byte[])input, (int)inputOffset).withLane(31, (byte)0).rearrange(this.shuffle).reinterpretAsInts().intoArray(buffer, i * 8);
                    inputOffset += 24;
                }
                return inputOffset;
            }

            @Override
            public int intBlockConvert32Pixels(int[] input, int inputOffset, int[] buffer) {
                for (int i = 0; i < 4; ++i) {
                    IntVector.fromArray((VectorSpecies)this.intSpecies, (int[])input, (int)inputOffset).reinterpretAsBytes().withLane(31, (byte)0).rearrange(this.intShuffle).reinterpretAsInts().intoArray(buffer, i * 8);
                    inputOffset += 8;
                }
                return inputOffset;
            }
        };
    }

    protected SIMDColorConversion(VectorSpecies<Byte> species, RGBColorToIntConversion base, int[] byte_rgb, int[] int_rgb) {
        this.hasTransparency = byte_rgb.length == 4;
        this.byteSpecies = species;
        this.intSpecies = species.withLanes(Integer.TYPE);
        this.base = base;
        int length = species.length();
        int[] shuffleInts = new int[length];
        int i = 0;
        int ctr = 0;
        while (i < length) {
            shuffleInts[i++] = ctr + byte_rgb[0];
            shuffleInts[i++] = ctr + byte_rgb[1];
            shuffleInts[i++] = ctr + byte_rgb[2];
            if (this.hasTransparency) {
                shuffleInts[i++] = ctr + byte_rgb[3];
                ctr += 4;
                continue;
            }
            shuffleInts[i++] = length - 1;
            ctr += 3;
        }
        this.shuffle = VectorShuffle.fromArray(species, (int[])shuffleInts, (int)0);
        length = species.length();
        shuffleInts = new int[length];
        i = 0;
        ctr = 0;
        while (i < length) {
            shuffleInts[i++] = ctr + int_rgb[0];
            shuffleInts[i++] = ctr + int_rgb[1];
            shuffleInts[i++] = ctr + int_rgb[2];
            shuffleInts[i++] = this.hasTransparency ? ctr + int_rgb[3] : length - 1;
            ctr += 4;
        }
        this.intShuffle = VectorShuffle.fromArray(species, (int[])shuffleInts, (int)0);
    }

    @Override
    public boolean isUsingSIMD() {
        return true;
    }

    @Override
    public RGBColorToIntConversion noSIMD() {
        return this.base;
    }

    @Override
    public boolean hasTransparency() {
        return this.hasTransparency;
    }

    @Override
    public int singleBytesInputLength() {
        return this.hasTransparency ? 4 : 3;
    }

    @Override
    public int singleBytesToInt(byte[] input, int inputOffset) {
        return this.base.singleBytesToInt(input, inputOffset);
    }

    @Override
    public int singleIntToInt(int input) {
        return this.base.singleIntToInt(input);
    }

    @Override
    public int byteBlockInputMinimumLength() {
        return 128;
    }

    @Override
    public abstract int byteBlockConvert32Pixels(byte[] var1, int var2, int[] var3);

    @Override
    public abstract int intBlockConvert32Pixels(int[] var1, int var2, int[] var3);
}

