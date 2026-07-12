/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.utils.LaunchFunction;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Predicate;

public class LauncherConfig
implements Cloneable {
    private String _asString = null;
    private double _distance;
    private int _duration;
    private double _acceleration;
    private boolean _launchFunctionIsDefault = true;
    private Class<? extends LaunchFunction> _launchFunction = LaunchFunction.Bezier.class;

    public boolean isValid() {
        return this.hasDistance() || this.hasDuration() || this.hasAcceleration();
    }

    public boolean hasDistance() {
        return this._distance >= 0.0;
    }

    public boolean hasDuration() {
        return this._duration >= 0;
    }

    public boolean hasAcceleration() {
        return this._acceleration > 0.0;
    }

    public Mode getMode() {
        for (Mode mode : Mode.values()) {
            if (!mode.predicate.test(this)) continue;
            return mode;
        }
        return Mode.INVALID;
    }

    public int getDuration() {
        return this._duration;
    }

    public void setDuration(int duration) {
        this._asString = null;
        this._duration = duration;
        if (this._duration >= 0) {
            this._distance = -1.0;
            this._acceleration = -1.0;
        }
    }

    public double getDistance() {
        return this._distance;
    }

    public void setDistance(double distance) {
        this._asString = null;
        this._distance = distance;
        if (distance >= 0.0) {
            this._duration = -1;
            this._acceleration = -1.0;
        }
    }

    public double getAcceleration() {
        return this._acceleration;
    }

    public void setAcceleration(double acceleration) {
        this._asString = null;
        this._acceleration = acceleration;
        if (acceleration >= 0.0) {
            this._duration = -1;
            this._distance = -1.0;
        }
    }

    public Class<? extends LaunchFunction> getFunction() {
        return this._launchFunction;
    }

    public void setFunction(Class<? extends LaunchFunction> function) {
        this._launchFunction = function;
    }

    public LauncherConfig clone() {
        LauncherConfig clone = new LauncherConfig();
        clone._asString = this._asString;
        clone._distance = this._distance;
        clone._duration = this._duration;
        clone._acceleration = this._acceleration;
        clone._launchFunction = this._launchFunction;
        clone._launchFunctionIsDefault = this._launchFunctionIsDefault;
        return clone;
    }

    public String toString() {
        if (this._asString == null) {
            StringBuilder result = new StringBuilder();
            if (this.hasDistance()) {
                result.append(this.getDistance());
            } else if (this.hasDuration()) {
                result.append(this.getDuration()).append('t');
            } else if (this.hasAcceleration()) {
                result.append(this.getAcceleration()).append("/tt");
            } else {
                return "";
            }
            if (!this._launchFunctionIsDefault) {
                if (this._launchFunction == LaunchFunction.Bezier.class) {
                    result.append('b');
                } else if (this._launchFunction == LaunchFunction.Linear.class) {
                    result.append('l');
                }
            }
            this._asString = result.toString();
        }
        return this._asString;
    }

    public static LauncherConfig parse(String text) {
        LauncherConfig config = LauncherConfig.createDefault();
        config._asString = text;
        String textFilt = text;
        int idx = 0;
        boolean is_acceleration_in_g = false;
        while (idx < textFilt.length()) {
            char c = textFilt.charAt(idx);
            if (c == 'b') {
                config._launchFunction = LaunchFunction.Bezier.class;
                config._launchFunctionIsDefault = false;
            } else if (c == 'l') {
                config._launchFunction = LaunchFunction.Linear.class;
                config._launchFunctionIsDefault = false;
            } else if (c == 'g' || c == 'G') {
                is_acceleration_in_g = true;
            } else {
                ++idx;
                continue;
            }
            textFilt = textFilt.substring(0, idx) + textFilt.substring(idx + 1);
        }
        int accelerationStart = textFilt.indexOf(47);
        if (accelerationStart != -1) {
            config._duration = -1;
            config._distance = -1.0;
            config._acceleration = Util.parseAcceleration(textFilt, -1.0);
            if (!config._launchFunctionIsDefault) {
                config._launchFunction = LaunchFunction.Linear.class;
            }
        } else if (is_acceleration_in_g) {
            config._duration = -1;
            config._distance = -1.0;
            config._acceleration = 0.024525 * ParseUtil.parseDouble((String)textFilt, (double)-1.0);
        } else {
            config._acceleration = -1.0;
            config._duration = Util.parseTimeTicks(textFilt);
            if (config._duration < 0) {
                config._distance = ParseUtil.parseDouble((String)textFilt, (double)-1.0);
            }
        }
        return config;
    }

    public static LauncherConfig createDefault() {
        LauncherConfig config = new LauncherConfig();
        config._launchFunction = TCConfig.launchFunctionType.equalsIgnoreCase("linear") ? LaunchFunction.Linear.class : (TCConfig.launchFunctionType.equalsIgnoreCase("bezier") ? LaunchFunction.Bezier.class : LaunchFunction.Bezier.class);
        config._launchFunctionIsDefault = true;
        config._duration = -1;
        config._distance = -1.0;
        config._acceleration = -1.0;
        config._asString = "";
        return config;
    }

    public static LauncherConfig readFrom(DataInputStream stream) throws IOException {
        int modeOrd = Util.readVariableLengthInt(stream);
        Mode[] modes = Mode.values();
        Mode mode = modeOrd >= 0 && modeOrd < modes.length ? modes[modeOrd] : Mode.INVALID;
        int launchFunctionId = Util.readVariableLengthInt(stream);
        Class launchFunction = launchFunctionId == 1 ? LaunchFunction.Linear.class : LaunchFunction.Bezier.class;
        LauncherConfig config = new LauncherConfig();
        config.setFunction(launchFunction);
        mode.read.accept(stream, config);
        return config;
    }

    public void writeTo(DataOutputStream stream) throws IOException {
        Mode mode = this.getMode();
        Util.writeVariableLengthInt(stream, mode.ordinal());
        Util.writeVariableLengthInt(stream, this._launchFunction == LaunchFunction.Linear.class ? 1 : 0);
        mode.write.accept(stream, this);
    }

    public static enum Mode {
        DURATION(LauncherConfig::hasDuration, (stream, config) -> config.setDuration(stream.readInt()), (stream, config) -> stream.writeInt(config.getDuration())),
        DISTANCE(LauncherConfig::hasDistance, (stream, config) -> config.setDistance(stream.readDouble()), (stream, config) -> stream.writeDouble(config.getDistance())),
        ACCELERATION(LauncherConfig::hasAcceleration, (stream, config) -> config.setAcceleration(stream.readDouble()), (stream, config) -> stream.writeDouble(config.getAcceleration())),
        INVALID(l -> !l.isValid(), (stream, config) -> {}, (stream, config) -> {});

        private final Predicate<LauncherConfig> predicate;
        private final IOBiFunction<DataInputStream, LauncherConfig> read;
        private final IOBiFunction<DataOutputStream, LauncherConfig> write;

        private Mode(Predicate<LauncherConfig> predicate, IOBiFunction<DataInputStream, LauncherConfig> read, IOBiFunction<DataOutputStream, LauncherConfig> write) {
            this.predicate = predicate;
            this.read = read;
            this.write = write;
        }

        @FunctionalInterface
        private static interface IOBiFunction<A, B> {
            public void accept(A var1, B var2) throws IOException;
        }
    }
}

