/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.regionflagtracker;

public final class RegionFlag<T> {
    private final String name;
    private final Type type;

    public static RegionFlag<State> ofState(String name) {
        return RegionFlag.create(name, Type.STATE);
    }

    public static RegionFlag<Boolean> ofBoolean(String name) {
        return RegionFlag.create(name, Type.BOOLEAN);
    }

    public static RegionFlag<Integer> ofInteger(String name) {
        return RegionFlag.create(name, Type.INTEGER);
    }

    public static RegionFlag<Double> ofDouble(String name) {
        return RegionFlag.create(name, Type.DOUBLE);
    }

    public static RegionFlag<String> ofString(String name) {
        return RegionFlag.create(name, Type.STRING);
    }

    private RegionFlag(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return this.name;
    }

    public Type type() {
        return this.type;
    }

    public String toString() {
        return "RegionFlag{name=" + this.name + ", type=" + this.type.name() + "}";
    }

    private static <T> RegionFlag<T> create(String name, Type type) {
        return new RegionFlag<T>(name, type);
    }

    public static enum Type {
        STATE,
        BOOLEAN,
        INTEGER,
        DOUBLE,
        STRING;

    }

    public static enum State {
        ALLOW,
        DENY;

    }
}

