/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.DyeColor
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkers;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapDecorationTypeHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.DyeColor;

public final class MapMarker {
    private final MapDisplayMarkers owner;
    private final String id;
    private Type type;
    private double x;
    private double y;
    private double rotation;
    private boolean visible;
    private ChatText caption;

    MapMarker(MapDisplayMarkers owner, String id) {
        this.owner = owner;
        this.id = id;
        this.type = Type.RED_MARKER;
        this.x = 0.0;
        this.y = 0.0;
        this.rotation = 0.0;
        this.visible = true;
        this.caption = null;
    }

    public String getId() {
        return this.id;
    }

    public boolean remove() {
        return this.owner.remove(this);
    }

    public Type getType() {
        return this.type;
    }

    public MapMarker setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        if (this.type != type) {
            this.type = type;
            this.owner.update(this);
        }
        return this;
    }

    public double getPositionX() {
        return this.x;
    }

    public double getPositionY() {
        return this.y;
    }

    public MapMarker setPositionX(double x) {
        return this.setPosition(x, this.y);
    }

    public MapMarker setPositionY(double y) {
        return this.setPosition(this.x, y);
    }

    public MapMarker setPosition(double x, double y) {
        if (this.x != x || this.y != y) {
            this.owner.move(this, x, y);
            this.x = x;
            this.y = y;
        }
        return this;
    }

    public double getRotation() {
        return this.rotation;
    }

    public MapMarker setRotation(double rotation) {
        boolean changed = MapMarker.isRotationChanged(this.rotation, rotation);
        this.rotation = rotation;
        if (changed) {
            this.owner.update(this);
        }
        return this;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public MapMarker setVisible(boolean visible) {
        this.owner.updateVisible(this, visible);
        this.visible = visible;
        return this;
    }

    public String getCaption() {
        return this.caption == null ? null : this.caption.getMessage();
    }

    public MapMarker setCaption(String caption) {
        if (this.caption == null) {
            if (caption != null) {
                this.caption = ChatText.fromMessage(caption);
                this.owner.update(this);
            }
        } else if (caption == null) {
            this.caption = null;
            this.owner.update(this);
        } else if (!this.caption.getMessage().equals(caption)) {
            this.caption.setMessage(caption);
            this.owner.update(this);
        }
        return this;
    }

    public ChatText getFormattedCaption() {
        return this.caption;
    }

    public MapMarker setFormattedCaption(ChatText caption) {
        if (this.caption == null) {
            if (caption != null) {
                this.caption = caption.clone();
                this.owner.update(this);
            }
        } else if (caption == null) {
            this.caption = null;
            this.owner.update(this);
        } else if (!this.caption.equals(caption)) {
            this.caption.copy(caption);
            this.owner.update(this);
        }
        return this;
    }

    private static boolean isRotationChanged(double oldAngle, double newAngle) {
        return oldAngle != newAngle;
    }

    public static final class Type {
        private final boolean available;
        private final boolean visibleOnItemFrames;
        private final TypeInfo info;
        private final Holder<MapDecorationTypeHandle> handle;
        private static final Map<Holder<MapDecorationTypeHandle>, Type> typesByHolder = new HashMap<Holder<MapDecorationTypeHandle>, Type>();
        private static final Map<IdentifierHandle, Type> existingTypesByKey = new HashMap<IdentifierHandle, Type>();
        private static final Map<Byte, Type> existingTypesByLegacyId = new HashMap<Byte, Type>();
        public static final Type WHITE_POINTER;
        public static final Type GREEN_POINTER;
        public static final Type RED_POINTER;
        public static final Type BLUE_POINTER;
        public static final Type WHITE_CROSS;
        public static final Type RED_MARKER;
        public static final Type WHITE_CIRCLE;
        public static final Type SMALL_WHITE_CIRCLE;
        public static final Type MANSION;
        public static final Type TEMPLE;
        public static final Type BANNER_WHITE;
        public static final Type BANNER_ORANGE;
        public static final Type BANNER_MAGENTA;
        public static final Type BANNER_LIGHT_BLUE;
        public static final Type BANNER_YELLOW;
        public static final Type BANNER_LIME;
        public static final Type BANNER_PINK;
        public static final Type BANNER_GRAY;
        public static final Type BANNER_LIGHT_GRAY;
        public static final Type BANNER_CYAN;
        public static final Type BANNER_PURPLE;
        public static final Type BANNER_BLUE;
        public static final Type BANNER_BROWN;
        public static final Type BANNER_GREEN;
        public static final Type BANNER_RED;
        public static final Type BANNER_BLACK;
        public static final Type RED_X;
        private static final EnumMap<Color, Type> pointerByColor;
        private static final EnumMap<Color, Type> bannerByColor;
        private static final List<Type> all_values;
        private static final List<Type> all_values_including_unavailable;
        private static final Map<String, Type> by_name;

        public static List<Type> values() {
            return all_values;
        }

        public static List<Type> values_including_unavailable() {
            return all_values_including_unavailable;
        }

        public static Type byName(String name) {
            return by_name.get(name);
        }

        public static Type getPointer(Color color) {
            return pointerByColor.get((Object)color);
        }

        public static Type getBanner(Color color) {
            return bannerByColor.get((Object)color);
        }

        public static Type fromHandle(Holder<MapDecorationTypeHandle> handle) {
            return LogicUtil.getOrComputeDefault(typesByHolder, handle, Type::new);
        }

        @Deprecated
        public static Type fromLegacyId(byte id) {
            return existingTypesByLegacyId.getOrDefault(id, WHITE_POINTER);
        }

        private static Type getTypeOrFail(String registryName) {
            return LogicUtil.getOrSupplyDefault(existingTypesByKey, IdentifierHandle.createNew("minecraft", registryName), () -> {
                throw new IllegalStateException("Marker type does not exist: " + registryName);
            });
        }

        private static Type getTypeOrFallback(String registryName, Type fallback) {
            return LogicUtil.getOrComputeDefault(existingTypesByKey, IdentifierHandle.createNew("minecraft", registryName), key -> new Type((IdentifierHandle)key, fallback));
        }

        private Type(Holder<MapDecorationTypeHandle> handle) {
            this.available = true;
            this.handle = handle;
            this.info = TypeInfo.get(handle.value().getName());
            this.visibleOnItemFrames = handle.value().isShownOnItemFrame();
        }

        private Type(IdentifierHandle key, Type fallback) {
            this.available = false;
            this.handle = fallback.getHandle();
            this.info = TypeInfo.get(key);
            this.visibleOnItemFrames = fallback.isVisibleOnItemFrames();
        }

        public String name() {
            return this.info.name;
        }

        public String displayName() {
            return this.info.displayName;
        }

        public Color color() {
            return this.info.color;
        }

        @Deprecated
        public byte id() {
            return this.handle.value().getId();
        }

        public Holder<MapDecorationTypeHandle> getHandle() {
            return this.handle;
        }

        public boolean isAvailable() {
            return this.available;
        }

        public boolean isVisibleOnItemFrames() {
            return this.visibleOnItemFrames;
        }

        public String toString() {
            return this.name();
        }

        private static EnumMap<Color, Type> byColors(Type ... types) {
            EnumMap<Color, Type> result = new EnumMap<Color, Type>(Color.class);
            for (Type type : types) {
                result.put(type.color(), type);
            }
            return result;
        }

        private static Type getTypeConstant(Field f) {
            try {
                return (Type)f.get(null);
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        static {
            for (Holder<MapDecorationTypeHandle> holder : MapDecorationTypeHandle.getValues()) {
                Type type = new Type(holder);
                typesByHolder.put(holder, type);
                existingTypesByKey.put(type.info.key, type);
                existingTypesByLegacyId.put(type.id(), type);
            }
            WHITE_POINTER = Type.getTypeOrFail("player");
            GREEN_POINTER = Type.getTypeOrFail("frame");
            RED_POINTER = Type.getTypeOrFail("red_marker");
            BLUE_POINTER = Type.getTypeOrFail("blue_marker");
            WHITE_CROSS = Type.getTypeOrFail("target_x");
            RED_MARKER = Type.getTypeOrFail("target_point");
            WHITE_CIRCLE = Type.getTypeOrFail("player_off_map");
            SMALL_WHITE_CIRCLE = Type.getTypeOrFallback("player_off_limits", WHITE_CIRCLE);
            MANSION = Type.getTypeOrFallback("mansion", RED_MARKER);
            TEMPLE = Type.getTypeOrFallback("monument", RED_MARKER);
            BANNER_WHITE = Type.getTypeOrFallback("banner_white", WHITE_POINTER);
            BANNER_ORANGE = Type.getTypeOrFallback("banner_orange", GREEN_POINTER);
            BANNER_MAGENTA = Type.getTypeOrFallback("banner_magenta", RED_POINTER);
            BANNER_LIGHT_BLUE = Type.getTypeOrFallback("banner_light_blue", BLUE_POINTER);
            BANNER_YELLOW = Type.getTypeOrFallback("banner_yellow", GREEN_POINTER);
            BANNER_LIME = Type.getTypeOrFallback("banner_lime", GREEN_POINTER);
            BANNER_PINK = Type.getTypeOrFallback("banner_pink", RED_POINTER);
            BANNER_GRAY = Type.getTypeOrFallback("banner_gray", WHITE_POINTER);
            BANNER_LIGHT_GRAY = Type.getTypeOrFallback("banner_light_gray", WHITE_POINTER);
            BANNER_CYAN = Type.getTypeOrFallback("banner_cyan", BLUE_POINTER);
            BANNER_PURPLE = Type.getTypeOrFallback("banner_purple", RED_POINTER);
            BANNER_BLUE = Type.getTypeOrFallback("banner_blue", BLUE_POINTER);
            BANNER_BROWN = Type.getTypeOrFallback("banner_brown", BLUE_POINTER);
            BANNER_GREEN = Type.getTypeOrFallback("banner_green", GREEN_POINTER);
            BANNER_RED = Type.getTypeOrFallback("banner_red", RED_POINTER);
            BANNER_BLACK = Type.getTypeOrFallback("banner_black", WHITE_POINTER);
            RED_X = Type.getTypeOrFallback("red_x", WHITE_CROSS);
            pointerByColor = Type.byColors(WHITE_POINTER, GREEN_POINTER, RED_POINTER, BLUE_POINTER);
            bannerByColor = Type.byColors(BANNER_WHITE, BANNER_ORANGE, BANNER_MAGENTA, BANNER_LIGHT_BLUE, BANNER_YELLOW, BANNER_LIME, BANNER_PINK, BANNER_GRAY, BANNER_LIGHT_GRAY, BANNER_CYAN, BANNER_PURPLE, BANNER_BLUE, BANNER_BROWN, BANNER_GREEN, BANNER_RED, BANNER_BLACK);
            all_values = typesByHolder.values().stream().collect(StreamUtil.toUnmodifiableList());
            all_values_including_unavailable = Stream.concat(all_values.stream(), Stream.of(Type.class.getDeclaredFields()).filter(f -> f.getType() == Type.class && Modifier.isStatic(f.getModifiers())).map(field -> {
                try {
                    return (Type)field.get(null);
                }
                catch (Throwable t) {
                    throw MountiplexUtil.uncheckedRethrow(t);
                }
            })).distinct().collect(StreamUtil.toUnmodifiableList());
            by_name = all_values_including_unavailable.stream().collect(Collectors.toMap(Type::name, Function.identity()));
        }
    }

    private static class TypeInfo {
        private static final Map<IdentifierHandle, TypeInfo> byKey = new HashMap<IdentifierHandle, TypeInfo>();
        public final IdentifierHandle key;
        public final String name;
        public final String displayName;
        public final Color color;

        private static void register(String registryName, String name, String displayName, Color color) {
            IdentifierHandle key = IdentifierHandle.createNew("minecraft", registryName);
            byKey.put(key, new TypeInfo(key, name, displayName, color));
        }

        public static TypeInfo get(IdentifierHandle key) {
            return LogicUtil.getOrComputeDefault(byKey, key, TypeInfo::new);
        }

        private TypeInfo(IdentifierHandle key, String name, String displayName, Color color) {
            this.key = key;
            this.name = name;
            this.displayName = displayName;
            this.color = color;
        }

        private TypeInfo(IdentifierHandle key) {
            this.key = key;
            if (key.getNamespace().equals("minecraft")) {
                this.name = key.getName().toUpperCase(Locale.ENGLISH);
                this.displayName = key.getName().replace('_', ' ');
            } else {
                this.name = key.toString().toUpperCase(Locale.ENGLISH).replace('.', '_');
                this.displayName = key.toString().replace('_', ' ');
            }
            this.color = Color.WHITE;
        }

        static {
            TypeInfo.register("player", "WHITE_POINTER", "pointer (white)", Color.WHITE);
            TypeInfo.register("frame", "GREEN_POINTER", "pointer (green)", Color.GREEN);
            TypeInfo.register("red_marker", "RED_POINTER", "pointer (red)", Color.RED);
            TypeInfo.register("blue_marker", "BLUE_POINTER", "pointer (blue)", Color.BLUE);
            TypeInfo.register("target_x", "WHITE_CROSS", "cross (white)", Color.WHITE);
            TypeInfo.register("target_point", "RED_MARKER", "marker (red)", Color.RED);
            TypeInfo.register("player_off_map", "WHITE_CIRCLE", "ball (white)", Color.WHITE);
            TypeInfo.register("player_off_limits", "SMALL_WHITE_CIRCLE", "dot (white)", Color.WHITE);
            TypeInfo.register("mansion", "MANSION", "mansion", Color.CYAN);
            TypeInfo.register("monument", "TEMPLE", "temple", Color.BROWN);
            TypeInfo.register("banner_white", "BANNER_WHITE", "banner (white)", Color.WHITE);
            TypeInfo.register("banner_orange", "BANNER_ORANGE", "banner (orange)", Color.ORANGE);
            TypeInfo.register("banner_magenta", "BANNER_MAGENTA", "banner (magenta)", Color.MAGENTA);
            TypeInfo.register("banner_light_blue", "BANNER_LIGHT_BLUE", "banner (light blue)", Color.LIGHT_BLUE);
            TypeInfo.register("banner_yellow", "BANNER_YELLOW", "banner (yellow)", Color.YELLOW);
            TypeInfo.register("banner_lime", "BANNER_LIME", "banner (lime)", Color.LIME);
            TypeInfo.register("banner_pink", "BANNER_PINK", "banner (pink)", Color.PINK);
            TypeInfo.register("banner_gray", "BANNER_GRAY", "banner (gray)", Color.GRAY);
            TypeInfo.register("banner_light_gray", "BANNER_LIGHT_GRAY", "banner (light gray)", Color.LIGHT_GRAY);
            TypeInfo.register("banner_cyan", "BANNER_CYAN", "banner (cyan)", Color.CYAN);
            TypeInfo.register("banner_purple", "BANNER_PURPLE", "banner (purple)", Color.PURPLE);
            TypeInfo.register("banner_blue", "BANNER_BLUE", "banner (blue)", Color.BLUE);
            TypeInfo.register("banner_brown", "BANNER_BROWN", "banner (brown)", Color.BROWN);
            TypeInfo.register("banner_green", "BANNER_GREEN", "banner (green)", Color.GREEN);
            TypeInfo.register("banner_red", "BANNER_RED", "banner (red)", Color.RED);
            TypeInfo.register("banner_black", "BANNER_BLACK", "banner (black)", Color.BLACK);
            TypeInfo.register("red_x", "RED_X", "cross (red)", Color.RED);
        }
    }

    public static enum Color {
        WHITE,
        ORANGE,
        MAGENTA,
        LIGHT_BLUE,
        YELLOW,
        LIME,
        PINK,
        GRAY,
        LIGHT_GRAY,
        CYAN,
        PURPLE,
        BLUE,
        BROWN,
        GREEN,
        RED,
        BLACK;

        private final DyeColor _dyeColor = DyeColor.values()[this.ordinal()];
        private static final Color[] _cached_values;

        public org.bukkit.Color toColor() {
            return this._dyeColor.getColor();
        }

        public DyeColor toDyeColor() {
            return this._dyeColor;
        }

        public static Color byDyeColor(DyeColor dyeColor) {
            return dyeColor == null ? null : _cached_values[dyeColor.ordinal()];
        }

        public static Color byOrdinal(int ordinal) {
            return _cached_values[ordinal & 0xF];
        }

        static {
            _cached_values = Color.values();
        }
    }
}

