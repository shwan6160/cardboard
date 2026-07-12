/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.tc.signactions.SignActionType;

public enum SignRedstoneMode {
    ON("", true, false, false, false),
    OFF("!", true, true, false, false),
    ALWAYS("+", false, false, false, false),
    NEVER("-", false, false, false, false),
    PULSE_ON("/", true, false, true, false),
    PULSE_OFF("\\", true, false, false, true),
    PULSE_ALWAYS("/\\", true, false, true, true),
    INVERTED_PULSE_ON("!/", true, true, true, false),
    INVERTED_PULSE_OFF("!\\", true, true, false, true),
    INVERTED_PULSE_ALWAYS("!/\\", true, true, true, true);

    private final String pattern;
    private final boolean respondsToRedstone;
    private final boolean isInverted;
    private final boolean isRisingPulse;
    private final boolean isFallingPulse;

    private SignRedstoneMode(String pattern, boolean respondsToRedstone, boolean isInverted, boolean isRisingPulse, boolean isFallingPulse) {
        this.pattern = pattern;
        this.respondsToRedstone = respondsToRedstone;
        this.isInverted = isInverted;
        this.isRisingPulse = isRisingPulse;
        this.isFallingPulse = isFallingPulse;
    }

    public String getPattern() {
        return this.pattern;
    }

    public boolean isRespondingToRedstone() {
        return this.respondsToRedstone;
    }

    public boolean isRisingPulse() {
        return this.isRisingPulse;
    }

    public boolean isFallingPulse() {
        return this.isFallingPulse;
    }

    public boolean isInverted() {
        return this.isInverted;
    }

    public SignActionType getRedstoneAction(boolean newPowerState) {
        switch (this.ordinal()) {
            case 2: 
            case 3: {
                return SignActionType.NONE;
            }
            case 6: {
                return SignActionType.REDSTONE_ON;
            }
            case 4: {
                return newPowerState ? SignActionType.REDSTONE_ON : SignActionType.NONE;
            }
            case 5: {
                return newPowerState ? SignActionType.NONE : SignActionType.REDSTONE_ON;
            }
            case 9: {
                return SignActionType.REDSTONE_OFF;
            }
            case 7: {
                return newPowerState ? SignActionType.REDSTONE_OFF : SignActionType.NONE;
            }
            case 8: {
                return newPowerState ? SignActionType.NONE : SignActionType.REDSTONE_OFF;
            }
            case 1: {
                return newPowerState ? SignActionType.REDSTONE_OFF : SignActionType.REDSTONE_ON;
            }
        }
        return newPowerState ? SignActionType.REDSTONE_ON : SignActionType.REDSTONE_OFF;
    }

    public static ParseResult parse(String input, int startIndex) {
        int idx;
        boolean power_inverted = false;
        boolean power_always_on = false;
        boolean power_always_off = false;
        boolean power_rising = false;
        boolean power_falling = false;
        int len = input.length();
        for (idx = startIndex; idx < len; ++idx) {
            char c = input.charAt(idx);
            if (c == '!') {
                power_inverted = true;
                continue;
            }
            if (c == '+') {
                power_always_on = true;
                continue;
            }
            if (c == '-') {
                power_always_off = true;
                continue;
            }
            if (c == '/') {
                power_rising = true;
                continue;
            }
            if (c != '\\') break;
            power_falling = true;
        }
        if (power_always_on) {
            return new ParseResult(idx, ALWAYS);
        }
        if (power_always_off) {
            return new ParseResult(idx, NEVER);
        }
        if (power_rising && power_falling) {
            return new ParseResult(idx, PULSE_ALWAYS);
        }
        if (power_rising) {
            return new ParseResult(idx, power_inverted ? INVERTED_PULSE_ON : PULSE_ON);
        }
        if (power_falling) {
            return new ParseResult(idx, power_inverted ? INVERTED_PULSE_OFF : PULSE_OFF);
        }
        if (power_inverted) {
            return new ParseResult(idx, OFF);
        }
        return new ParseResult(idx, ON);
    }

    public static class ParseResult {
        public final int endIndex;
        public final SignRedstoneMode mode;

        public ParseResult(int endIndex, SignRedstoneMode mode) {
            this.endIndex = endIndex;
            this.mode = mode;
        }
    }
}

