/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayHelper {
    private static int[][] dimensions_cache = new int[][]{{0}};

    public static Class<?> getArrayType(Class<?> componentType, int num_dimensions) {
        int[] dimensions;
        int[][] cache;
        int len;
        if (num_dimensions == 0) {
            return componentType;
        }
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
            ++num_dimensions;
        }
        if (num_dimensions == 1 && !componentType.isPrimitive()) {
            try {
                return MPLType.getClassByName("[L" + MPLType.getName(componentType) + ";", false, componentType.getClassLoader());
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        if (num_dimensions >= (len = (cache = dimensions_cache).length)) {
            int[][] new_cache = (int[][])Arrays.copyOf(cache, num_dimensions + 1);
            for (int i = len; i <= num_dimensions; ++i) {
                new_cache[i] = new int[i];
            }
            dimensions_cache = new_cache;
            dimensions = new_cache[num_dimensions];
        } else {
            dimensions = cache[num_dimensions];
        }
        return Array.newInstance(componentType, dimensions).getClass();
    }
}

