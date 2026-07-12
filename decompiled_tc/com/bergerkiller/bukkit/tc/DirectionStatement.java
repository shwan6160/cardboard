/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.statements.Statement;
import java.util.Locale;

public class DirectionStatement {
    public String directionFrom;
    public String direction;
    public String text;
    public Counter counter;

    public DirectionStatement(String text, String defaultDirection) {
        int idx = text.indexOf(58);
        if (idx == -1) {
            this.text = text;
            this.direction = defaultDirection;
        } else {
            this.text = text.substring(idx + 1);
            this.direction = text.substring(0, idx);
        }
        if (this.text.isEmpty()) {
            this.text = "default";
        }
        if ((idx = this.direction.indexOf(45)) == -1) {
            this.directionFrom = "self";
        } else {
            this.directionFrom = this.direction.substring(0, idx);
            this.direction = this.direction.substring(idx + 1);
        }
        if (DirectionStatement.startsWithDigit(this.text)) {
            if (this.text.endsWith("%")) {
                String value = this.text.substring(0, this.text.length() - 1);
                try {
                    this.counter = new CounterPercentage(Double.parseDouble(value));
                }
                catch (NumberFormatException ex) {
                    this.counter = null;
                }
            } else {
                try {
                    this.counter = new CounterAbsolute(Integer.parseInt(this.text));
                }
                catch (NumberFormatException ex) {
                    this.counter = null;
                }
            }
        } else {
            this.counter = null;
        }
    }

    public boolean has(SignActionEvent event, MinecartMember<?> member) {
        return Statement.has(member, this.text, event);
    }

    public boolean has(SignActionEvent event, MinecartGroup group) {
        return Statement.has(group, this.text, event);
    }

    public boolean isSwitchedFromSelf() {
        return this.directionFrom.equals("self");
    }

    public boolean hasCounter() {
        return this.counter != null;
    }

    public boolean isDefault() {
        String str = this.text.toLowerCase(Locale.ENGLISH);
        return str.equals("def") || str.equals("default");
    }

    public String toString() {
        if (this.hasCounter()) {
            return "{from=" + this.directionFrom + " to=" + this.direction + " every " + this.counter + "}";
        }
        return "{from=" + this.directionFrom + " to=" + this.direction + " when " + this.text + "}";
    }

    private static boolean startsWithDigit(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if (Character.isDigit(ch)) {
                return true;
            }
            if (ch == ' ' || ch == '\t' || ch == '-' || ch == '+') continue;
            return false;
        }
        return false;
    }

    private static final class CounterPercentage
    implements Counter {
        private final double theta;

        public CounterPercentage(double percentage) {
            this.theta = percentage / 100.0;
        }

        @Override
        public int get(int trainSize) {
            return MathUtil.ceil((double)(this.theta * (double)trainSize));
        }

        public String toString() {
            return this.theta * 100.0 + "%";
        }
    }

    public static interface Counter {
        public int get(int var1);
    }

    private static final class CounterAbsolute
    implements Counter {
        private final int value;

        public CounterAbsolute(int value) {
            this.value = value;
        }

        @Override
        public int get(int trainSize) {
            return this.value;
        }

        public String toString() {
            return Integer.toString(this.value);
        }
    }
}

