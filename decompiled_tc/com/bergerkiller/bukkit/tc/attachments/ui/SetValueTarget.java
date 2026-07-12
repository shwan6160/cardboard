/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public interface SetValueTarget {
    public String getAcceptedPropertyName();

    public boolean acceptTextValue(String var1);

    default public boolean acceptTextValue(Operation operation, String value) {
        return operation == Operation.SET && this.acceptTextValue(value);
    }

    public static enum Operation {
        SET,
        ADD,
        SUBTRACT;


        public boolean perform(IntSupplier getter, IntConsumer setter, String value) {
            int parsed = ParseUtil.parseInt((String)value, (int)Integer.MAX_VALUE);
            if (parsed == Integer.MAX_VALUE && (double)parsed != ParseUtil.parseDouble((String)value, (double)Double.NaN)) {
                return false;
            }
            switch (this.ordinal()) {
                case 0: {
                    setter.accept(parsed);
                    return true;
                }
                case 1: {
                    setter.accept(getter.getAsInt() + parsed);
                    return true;
                }
                case 2: {
                    setter.accept(getter.getAsInt() - parsed);
                    return true;
                }
            }
            return false;
        }

        public boolean perform(DoubleSupplier getter, DoubleConsumer setter, String value) {
            double parsed = ParseUtil.parseDouble((String)value, (double)Double.NaN);
            if (Double.isNaN(parsed)) {
                return false;
            }
            switch (this.ordinal()) {
                case 0: {
                    setter.accept(parsed);
                    return true;
                }
                case 1: {
                    setter.accept(getter.getAsDouble() + parsed);
                    return true;
                }
                case 2: {
                    setter.accept(getter.getAsDouble() - parsed);
                    return true;
                }
            }
            return false;
        }
    }
}

