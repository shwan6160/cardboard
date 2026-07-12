/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.Option;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionSchema;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import org.jetbrains.annotations.NotNull;

public final class JSONOptions {
    private static final int VERSION_INITIAL = 0;
    private static final int VERSION_1_16 = 2526;
    private static final int VERSION_1_20_3 = 3679;
    private static final int VERSION_1_20_5 = 3819;
    private static final int VERSION_1_21_4 = 4174;
    private static final int VERSION_1_21_5 = 4298;
    private static final int VERSION_1_21_6 = 4422;
    private static final OptionSchema.Mutable UNSAFE_SCHEMA = OptionSchema.globalSchema();
    public static final Option<Boolean> EMIT_RGB = Option.booleanOption(JSONOptions.key("emit/rgb"), true);
    public static final Option<HoverEventValueMode> EMIT_HOVER_EVENT_TYPE = UNSAFE_SCHEMA.enumOption(JSONOptions.key("emit/hover_value_mode"), HoverEventValueMode.class, HoverEventValueMode.SNAKE_CASE);
    public static final Option<ClickEventValueMode> EMIT_CLICK_EVENT_TYPE = Option.enumOption(JSONOptions.key("emit/click_value_mode"), ClickEventValueMode.class, ClickEventValueMode.SNAKE_CASE);
    public static final Option<Boolean> EMIT_COMPACT_TEXT_COMPONENT = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("emit/compact_text_component"), true);
    public static final Option<Boolean> EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("emit/hover_show_entity_id_as_int_array"), true);
    public static final Option<Boolean> EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("emit/hover_show_entity_key_as_type_and_uuid_as_id"), false);
    public static final Option<Boolean> VALIDATE_STRICT_EVENTS = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("validate/strict_events"), true);
    public static final Option<Boolean> EMIT_DEFAULT_ITEM_HOVER_QUANTITY = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("emit/default_item_hover_quantity"), true);
    public static final Option<ShowItemHoverDataMode> SHOW_ITEM_HOVER_DATA_MODE = UNSAFE_SCHEMA.enumOption(JSONOptions.key("emit/show_item_hover_data"), ShowItemHoverDataMode.class, ShowItemHoverDataMode.EMIT_EITHER);
    public static final Option<ShadowColorEmitMode> SHADOW_COLOR_MODE = UNSAFE_SCHEMA.enumOption(JSONOptions.key("emit/shadow_color"), ShadowColorEmitMode.class, ShadowColorEmitMode.EMIT_INTEGER);
    public static final Option<Boolean> EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("emit/change_page_click_event_page_as_string"), false);
    public static final Option<Boolean> EMIT_CLICK_URL_HTTPS = UNSAFE_SCHEMA.booleanOption(JSONOptions.key("emit/click_url_https"), false);
    private static final OptionSchema SCHEMA = OptionSchema.childSchema(UNSAFE_SCHEMA).frozenView();
    private static final OptionState.Versioned BY_DATA_VERSION = SCHEMA.versionedStateBuilder().version(0, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.VALUE_FIELD).value(EMIT_CLICK_EVENT_TYPE, ClickEventValueMode.CAMEL_CASE).value(EMIT_RGB, false).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, false).value(EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true).value(VALIDATE_STRICT_EVENTS, false).value(EMIT_DEFAULT_ITEM_HOVER_QUANTITY, false).value(SHOW_ITEM_HOVER_DATA_MODE, ShowItemHoverDataMode.EMIT_LEGACY_NBT).value(SHADOW_COLOR_MODE, ShadowColorEmitMode.NONE).value(EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, true)).version(2526, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.CAMEL_CASE).value(EMIT_RGB, true)).version(3679, b -> b.value(EMIT_COMPACT_TEXT_COMPONENT, true).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, true).value(VALIDATE_STRICT_EVENTS, true)).version(3819, b -> b.value(EMIT_DEFAULT_ITEM_HOVER_QUANTITY, true).value(SHOW_ITEM_HOVER_DATA_MODE, ShowItemHoverDataMode.EMIT_DATA_COMPONENTS)).version(4174, b -> b.value(SHADOW_COLOR_MODE, ShadowColorEmitMode.EMIT_INTEGER)).version(4298, b -> b.value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.SNAKE_CASE).value(EMIT_CLICK_EVENT_TYPE, ClickEventValueMode.SNAKE_CASE).value(EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, false).value(EMIT_CLICK_URL_HTTPS, true)).version(4422, b -> b.value(EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, false)).build();
    private static final OptionState MOST_COMPATIBLE = SCHEMA.stateBuilder().value(EMIT_HOVER_EVENT_TYPE, HoverEventValueMode.ALL).value(EMIT_CLICK_EVENT_TYPE, ClickEventValueMode.BOTH).value(EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, false).value(EMIT_COMPACT_TEXT_COMPONENT, false).value(VALIDATE_STRICT_EVENTS, false).value(SHOW_ITEM_HOVER_DATA_MODE, ShowItemHoverDataMode.EMIT_EITHER).value(SHADOW_COLOR_MODE, ShadowColorEmitMode.EMIT_INTEGER).value(EMIT_CLICK_URL_HTTPS, true).build();

    private JSONOptions() {
    }

    private static String key(String value) {
        return "adventure:json/" + value;
    }

    @NotNull
    public static OptionSchema schema() {
        return SCHEMA;
    }

    public static @NotNull OptionState.Versioned byDataVersion() {
        return BY_DATA_VERSION;
    }

    @NotNull
    public static OptionState compatibility() {
        return MOST_COMPATIBLE;
    }

    public static enum HoverEventValueMode {
        SNAKE_CASE,
        CAMEL_CASE,
        VALUE_FIELD,
        ALL;

        @Deprecated
        public static final HoverEventValueMode MODERN_ONLY;
        @Deprecated
        public static final HoverEventValueMode LEGACY_ONLY;
        @Deprecated
        public static final HoverEventValueMode BOTH;

        static {
            MODERN_ONLY = CAMEL_CASE;
            LEGACY_ONLY = VALUE_FIELD;
            BOTH = ALL;
        }
    }

    public static enum ClickEventValueMode {
        SNAKE_CASE,
        CAMEL_CASE,
        BOTH;

    }

    public static enum ShadowColorEmitMode {
        NONE,
        EMIT_INTEGER,
        EMIT_ARRAY;

    }

    public static enum ShowItemHoverDataMode {
        EMIT_LEGACY_NBT,
        EMIT_DATA_COMPONENTS,
        EMIT_EITHER;

    }
}

