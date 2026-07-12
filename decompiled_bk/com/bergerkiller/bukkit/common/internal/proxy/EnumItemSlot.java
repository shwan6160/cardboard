/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

public enum EnumItemSlot {
    MAINHAND(Function.HAND, 0, 0, "mainhand"),
    FEET(Function.ARMOR, 0, 1, "feet"),
    LEGS(Function.ARMOR, 1, 2, "legs"),
    CHEST(Function.ARMOR, 2, 3, "chest"),
    HEAD(Function.ARMOR, 3, 4, "head");

    private final Function g;
    private final int h;
    private final int i;
    private final String j;
    private static final EnumItemSlot[] BY_PLAYER_SLOT_INDEX;
    private static final EnumItemSlot[] BY_NONPLAYER_SLOT_INDEX;

    private EnumItemSlot(Function enumitemslot_function, int i, int j, String s) {
        this.g = enumitemslot_function;
        this.h = i;
        this.i = j;
        this.j = s;
    }

    public Function a() {
        return this.g;
    }

    public int b() {
        return this.h;
    }

    public int c() {
        return this.i;
    }

    public String d() {
        return this.j;
    }

    public static EnumItemSlot fromPlayerSlotIndex(int index) {
        int len = BY_PLAYER_SLOT_INDEX.length;
        if (index < 0 || index >= len) {
            return HEAD;
        }
        return BY_PLAYER_SLOT_INDEX[index];
    }

    public static EnumItemSlot fromNonPlayerSlotIndex(int index) {
        int len = BY_NONPLAYER_SLOT_INDEX.length;
        if (index < 0 || index >= len) {
            return HEAD;
        }
        return BY_NONPLAYER_SLOT_INDEX[index];
    }

    public static EnumItemSlot a(String s) {
        for (EnumItemSlot enumitemslot : EnumItemSlot.values()) {
            if (!enumitemslot.d().equals(s)) continue;
            return enumitemslot;
        }
        throw new IllegalArgumentException("Invalid slot '" + s + "'");
    }

    static {
        BY_PLAYER_SLOT_INDEX = new EnumItemSlot[4];
        EnumItemSlot.BY_PLAYER_SLOT_INDEX[EnumItemSlot.FEET.b()] = FEET;
        EnumItemSlot.BY_PLAYER_SLOT_INDEX[EnumItemSlot.LEGS.b()] = LEGS;
        EnumItemSlot.BY_PLAYER_SLOT_INDEX[EnumItemSlot.CHEST.b()] = CHEST;
        EnumItemSlot.BY_PLAYER_SLOT_INDEX[EnumItemSlot.HEAD.b()] = HEAD;
        BY_NONPLAYER_SLOT_INDEX = EnumItemSlot.values();
    }

    public static enum Function {
        HAND,
        ARMOR;

    }
}

