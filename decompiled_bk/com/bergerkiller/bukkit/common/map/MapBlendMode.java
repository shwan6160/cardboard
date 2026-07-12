/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import java.util.Arrays;

public enum MapBlendMode {
    NONE{

        @Override
        public byte process(byte inputA, byte inputB) {
            return inputA;
        }

        @Override
        public void process(byte input, byte[] output) {
            Arrays.fill(output, input);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            System.arraycopy(input, 0, output, 0, output.length);
        }

        @Override
        public boolean inputColorUsesOutput(byte input) {
            return false;
        }
    }
    ,
    OVERLAY{

        @Override
        public byte process(byte inputA, byte inputB) {
            return inputA == 0 ? inputB : inputA;
        }

        @Override
        public void process(byte input, byte[] output) {
            if (input != 0) {
                Arrays.fill(output, input);
            }
        }

        @Override
        public void process(byte[] input, byte[] output) {
            for (int i = 0; i < output.length; ++i) {
                if (input[i] == 0) continue;
                output[i] = input[i];
            }
        }

        @Override
        public boolean inputColorUsesOutput(byte input) {
            return input == 0;
        }
    }
    ,
    AVERAGE{

        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_AVERAGE);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_AVERAGE);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_AVERAGE);
        }
    }
    ,
    ADD{

        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_ADD);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_ADD);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_ADD);
        }
    }
    ,
    SUBTRACT{

        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_SUBTRACT);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_SUBTRACT);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_SUBTRACT);
        }
    }
    ,
    MULTIPLY{

        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_MULTIPLY);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_MULTIPLY);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_MULTIPLY);
        }
    };


    public abstract byte process(byte var1, byte var2);

    public abstract void process(byte var1, byte[] var2);

    public abstract void process(byte[] var1, byte[] var2);

    public boolean inputColorUsesOutput(byte input) {
        return true;
    }
}

