/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

public class MatrixMath {
    public static boolean luDecomposition(double[] matrix0, int[] row_perm) {
        double[] row_scale = new double[4];
        int ptr = 0;
        int rs = 0;
        int i = 4;
        while (i-- != 0) {
            double big = 0.0;
            int j = 4;
            while (j-- != 0) {
                double temp = matrix0[ptr++];
                if (!((temp = Math.abs(temp)) > big)) continue;
                big = temp;
            }
            if (big == 0.0) {
                return false;
            }
            row_scale[rs++] = 1.0 / big;
        }
        int mtx = 0;
        for (int j = 0; j < 4; ++j) {
            double temp;
            int p1;
            int k;
            int p2;
            double sum;
            int target;
            int i2;
            for (i2 = 0; i2 < j; ++i2) {
                target = mtx + 4 * i2 + j;
                sum = matrix0[target];
                int k2 = i2;
                int p12 = mtx + 4 * i2;
                p2 = mtx + j;
                while (k2-- != 0) {
                    sum -= matrix0[p12] * matrix0[p2];
                    ++p12;
                    p2 += 4;
                }
                matrix0[target] = sum;
            }
            double big = 0.0;
            int imax = -1;
            for (i2 = j; i2 < 4; ++i2) {
                double d;
                target = mtx + 4 * i2 + j;
                sum = matrix0[target];
                k = j;
                p1 = mtx + 4 * i2;
                p2 = mtx + j;
                while (k-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += 4;
                }
                matrix0[target] = sum;
                temp = row_scale[i2] * Math.abs(sum);
                if (!(d >= big)) continue;
                big = temp;
                imax = i2;
            }
            if (imax < 0) {
                return false;
            }
            if (j != imax) {
                k = 4;
                p1 = mtx + 4 * imax;
                p2 = mtx + 4 * j;
                while (k-- != 0) {
                    temp = matrix0[p1];
                    matrix0[p1++] = matrix0[p2];
                    matrix0[p2++] = temp;
                }
                row_scale[imax] = row_scale[j];
            }
            row_perm[j] = imax;
            double SINGULAR_EP = 1.0E-5;
            double v = matrix0[mtx + 4 * j + j];
            if (v >= -1.0E-5 && v <= 1.0E-5) {
                return false;
            }
            if (j == 3) continue;
            temp = 1.0 / matrix0[mtx + 4 * j + j];
            target = mtx + 4 * (j + 1) + j;
            i2 = 3 - j;
            while (i2-- != 0) {
                int n = target;
                matrix0[n] = matrix0[n] * temp;
                target += 4;
            }
        }
        return true;
    }

    public static void luBacksubstitution(double[] matrix1, int[] row_perm, double[] matrix2) {
        int rp = 0;
        for (int k = 0; k < 4; ++k) {
            int rv;
            int cv = k;
            int ii = -1;
            for (int i = 0; i < 4; ++i) {
                int ip = row_perm[rp + i];
                double sum = matrix2[cv + 4 * ip];
                matrix2[cv + 4 * ip] = matrix2[cv + 4 * i];
                if (ii >= 0) {
                    rv = i * 4;
                    for (int j = ii; j <= i - 1; ++j) {
                        sum -= matrix1[rv + j] * matrix2[cv + 4 * j];
                    }
                } else if (sum != 0.0) {
                    ii = i;
                }
                matrix2[cv + 4 * i] = sum;
            }
            rv = 12;
            int n = cv + 12;
            matrix2[n] = matrix2[n] / matrix1[rv + 3];
            matrix2[cv + 8] = (matrix2[cv + 8] - matrix1[(rv -= 4) + 3] * matrix2[cv + 12]) / matrix1[rv + 2];
            matrix2[cv + 4] = (matrix2[cv + 4] - matrix1[(rv -= 4) + 2] * matrix2[cv + 8] - matrix1[rv + 3] * matrix2[cv + 12]) / matrix1[rv + 1];
            matrix2[cv + 0] = (matrix2[cv + 0] - matrix1[(rv -= 4) + 1] * matrix2[cv + 4] - matrix1[rv + 2] * matrix2[cv + 8] - matrix1[rv + 3] * matrix2[cv + 12]) / matrix1[rv + 0];
        }
    }
}

