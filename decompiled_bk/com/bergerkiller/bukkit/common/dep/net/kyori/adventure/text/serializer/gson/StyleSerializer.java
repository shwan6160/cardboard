/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonObject;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.JsonPrimitive;
import com.bergerkiller.bukkit.common.dep.gson.JsonSyntaxException;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.HoverEvent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.ShadowColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.Style;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextDecoration;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.ComponentSerializerImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonHacks;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.TextColorWrapper;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.ARGBLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Codec;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

final class StyleSerializer
extends TypeAdapter<Style> {
    private static final TextDecoration[] DECORATIONS = new TextDecoration[]{TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED, TextDecoration.STRIKETHROUGH, TextDecoration.OBFUSCATED};
    private static final String FALLBACK_URL_PROTOCOL = "https://";
    private final LegacyHoverEventSerializer legacyHover;
    private final boolean emitValueFieldHover;
    private final boolean emitCamelCaseHover;
    private final boolean emitSnakeCaseHover;
    private final boolean emitCamelCaseClick;
    private final boolean emitSnakeCaseClick;
    private final boolean strictEventValues;
    private final boolean emitShadowColor;
    private final boolean emitStringPage;
    private final boolean emitClickUrlHttps;
    private final Gson gson;

    static TypeAdapter<Style> create(@Nullable LegacyHoverEventSerializer legacyHover, OptionState features, Gson gson) {
        JSONOptions.HoverEventValueMode hoverMode = features.value(JSONOptions.EMIT_HOVER_EVENT_TYPE);
        JSONOptions.ClickEventValueMode clickMode = features.value(JSONOptions.EMIT_CLICK_EVENT_TYPE);
        return new StyleSerializer(legacyHover, hoverMode == JSONOptions.HoverEventValueMode.VALUE_FIELD || hoverMode == JSONOptions.HoverEventValueMode.ALL, hoverMode == JSONOptions.HoverEventValueMode.CAMEL_CASE || hoverMode == JSONOptions.HoverEventValueMode.ALL, hoverMode == JSONOptions.HoverEventValueMode.SNAKE_CASE || hoverMode == JSONOptions.HoverEventValueMode.ALL, clickMode == JSONOptions.ClickEventValueMode.CAMEL_CASE || clickMode == JSONOptions.ClickEventValueMode.BOTH, clickMode == JSONOptions.ClickEventValueMode.SNAKE_CASE || clickMode == JSONOptions.ClickEventValueMode.BOTH, features.value(JSONOptions.VALIDATE_STRICT_EVENTS), features.value(JSONOptions.SHADOW_COLOR_MODE) != JSONOptions.ShadowColorEmitMode.NONE, features.value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING), features.value(JSONOptions.EMIT_CLICK_URL_HTTPS), gson).nullSafe();
    }

    private StyleSerializer(@Nullable LegacyHoverEventSerializer legacyHover, boolean emitValueFieldHover, boolean emitCamelCaseHover, boolean emitSnakeCaseHover, boolean emitCamelCaseClick, boolean emitSnakeCaseClick, boolean strictEventValues, boolean emitShadowColor, boolean emitStringPage, boolean emitClickUrlHttps, Gson gson) {
        this.legacyHover = legacyHover;
        this.emitValueFieldHover = emitValueFieldHover;
        this.emitCamelCaseHover = emitCamelCaseHover;
        this.emitSnakeCaseHover = emitSnakeCaseHover;
        this.emitCamelCaseClick = emitCamelCaseClick;
        this.emitSnakeCaseClick = emitSnakeCaseClick;
        this.strictEventValues = strictEventValues;
        this.emitShadowColor = emitShadowColor;
        this.emitStringPage = emitStringPage;
        this.emitClickUrlHttps = emitClickUrlHttps;
        this.gson = gson;
    }

    @Override
    public Style read(JsonReader in) throws IOException {
        in.beginObject();
        Style.Builder style = Style.style();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals("font")) {
                style.font((Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE));
                continue;
            }
            if (fieldName.equals("color")) {
                TextColorWrapper color = (TextColorWrapper)this.gson.fromJson(in, SerializerFactory.COLOR_WRAPPER_TYPE);
                if (color.color != null) {
                    style.color(color.color);
                    continue;
                }
                if (color.decoration == null) continue;
                style.decoration(color.decoration, TextDecoration.State.TRUE);
                continue;
            }
            if (fieldName.equals("shadow_color")) {
                style.shadowColor((ARGBLike)this.gson.fromJson(in, SerializerFactory.SHADOW_COLOR_TYPE));
                continue;
            }
            if (TextDecoration.NAMES.keys().contains(fieldName)) {
                style.decoration(TextDecoration.NAMES.value(fieldName), GsonHacks.readBoolean(in));
                continue;
            }
            if (fieldName.equals("insertion")) {
                style.insertion(in.nextString());
                continue;
            }
            if (fieldName.equals("click_event") || fieldName.equals("clickEvent")) {
                in.beginObject();
                ClickEvent.Action action = null;
                String value = null;
                Key key = null;
                Integer page = null;
                while (in.hasNext()) {
                    String clickEventField = in.nextName();
                    if (clickEventField.equals("action")) {
                        action = (ClickEvent.Action)((Object)this.gson.fromJson(in, SerializerFactory.CLICK_ACTION_TYPE));
                        continue;
                    }
                    if (clickEventField.equals("page")) {
                        if (in.peek() == JsonToken.NUMBER) {
                            page = in.nextInt();
                            continue;
                        }
                        if (in.peek() == JsonToken.STRING) {
                            page = Integer.parseInt(in.nextString());
                            continue;
                        }
                        if (in.peek() == JsonToken.NULL) {
                            throw ComponentSerializerImpl.notSureHowToDeserialize(clickEventField);
                        }
                        in.skipValue();
                        continue;
                    }
                    if (clickEventField.equals("value") || clickEventField.equals("url") || clickEventField.equals("path") || clickEventField.equals("command") || clickEventField.equals("payload")) {
                        if (in.peek() == JsonToken.NULL) {
                            if (this.strictEventValues) {
                                throw ComponentSerializerImpl.notSureHowToDeserialize(clickEventField);
                            }
                            in.nextNull();
                            continue;
                        }
                        value = in.nextString();
                        continue;
                    }
                    if (clickEventField.equals("id")) {
                        key = Key.key(in.nextString());
                        continue;
                    }
                    in.skipValue();
                }
                if (action != null && action.readable()) {
                    switch (action) {
                        case OPEN_URL: {
                            if (value == null) break;
                            style.clickEvent(ClickEvent.openUrl(value));
                            break;
                        }
                        case RUN_COMMAND: {
                            if (value == null) break;
                            style.clickEvent(ClickEvent.runCommand(value));
                            break;
                        }
                        case SUGGEST_COMMAND: {
                            if (value == null) break;
                            style.clickEvent(ClickEvent.suggestCommand(value));
                            break;
                        }
                        case CHANGE_PAGE: {
                            if (page == null) break;
                            style.clickEvent(ClickEvent.changePage(page));
                            break;
                        }
                        case COPY_TO_CLIPBOARD: {
                            if (value == null) break;
                            style.clickEvent(ClickEvent.copyToClipboard(value));
                            break;
                        }
                        case CUSTOM: {
                            if (key == null || value == null) break;
                            style.clickEvent(ClickEvent.custom(key, value));
                            break;
                        }
                    }
                }
                in.endObject();
                continue;
            }
            if (fieldName.equals("hover_event") || fieldName.equals("hoverEvent")) {
                Object value;
                HoverEvent.Action action;
                JsonPrimitive serializedAction;
                JsonObject hoverEventObject = (JsonObject)this.gson.fromJson(in, (Type)((Object)JsonObject.class));
                if (hoverEventObject == null || (serializedAction = hoverEventObject.getAsJsonPrimitive("action")) == null || !(action = this.gson.fromJson((JsonElement)serializedAction, SerializerFactory.HOVER_ACTION_TYPE)).readable()) continue;
                Class actionType = action.type();
                if (hoverEventObject.has("contents")) {
                    @Nullable JsonElement rawValue = hoverEventObject.get("contents");
                    if (GsonHacks.isNullOrEmpty(rawValue)) {
                        if (this.strictEventValues) {
                            throw ComponentSerializerImpl.notSureHowToDeserialize(rawValue);
                        }
                        value = null;
                    } else {
                        value = SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson(rawValue, SerializerFactory.COMPONENT_TYPE) : (SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson(rawValue, SerializerFactory.SHOW_ITEM_TYPE) : (SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson(rawValue, SerializerFactory.SHOW_ENTITY_TYPE) : null));
                    }
                } else if (hoverEventObject.has("value")) {
                    JsonElement element = hoverEventObject.get("value");
                    if (GsonHacks.isNullOrEmpty(element)) {
                        if (this.strictEventValues) {
                            throw ComponentSerializerImpl.notSureHowToDeserialize(element);
                        }
                        value = null;
                    } else if (SerializerFactory.COMPONENT_TYPE.isAssignableFrom(actionType)) {
                        Component rawValue = this.gson.fromJson(element, SerializerFactory.COMPONENT_TYPE);
                        value = this.legacyHoverEventContents(action, rawValue);
                    } else {
                        value = SerializerFactory.STRING_TYPE.isAssignableFrom(actionType) ? this.gson.fromJson(element, SerializerFactory.STRING_TYPE) : null;
                    }
                } else if (SerializerFactory.SHOW_ITEM_TYPE.isAssignableFrom(actionType)) {
                    value = this.gson.fromJson((JsonElement)hoverEventObject, SerializerFactory.SHOW_ITEM_TYPE);
                } else if (SerializerFactory.SHOW_ENTITY_TYPE.isAssignableFrom(actionType)) {
                    value = this.gson.fromJson((JsonElement)hoverEventObject, SerializerFactory.SHOW_ENTITY_TYPE);
                } else {
                    if (this.strictEventValues) {
                        throw ComponentSerializerImpl.notSureHowToDeserialize(hoverEventObject);
                    }
                    value = null;
                }
                if (value == null) continue;
                style.hoverEvent(HoverEvent.hoverEvent(action, value));
                continue;
            }
            in.skipValue();
        }
        in.endObject();
        return style.build();
    }

    private Object legacyHoverEventContents(HoverEvent.Action<?> action, Component rawValue) {
        if (action == HoverEvent.Action.SHOW_TEXT) {
            return rawValue;
        }
        if (this.legacyHover != null) {
            try {
                if (action == HoverEvent.Action.SHOW_ENTITY) {
                    return this.legacyHover.deserializeShowEntity(rawValue, this.decoder());
                }
                if (action == HoverEvent.Action.SHOW_ITEM) {
                    return this.legacyHover.deserializeShowItem(rawValue);
                }
            }
            catch (IOException ex) {
                throw new JsonParseException(ex);
            }
        }
        throw new UnsupportedOperationException();
    }

    private Codec.Decoder<Component, String, JsonParseException> decoder() {
        return string -> this.gson.fromJson((String)string, SerializerFactory.COMPONENT_TYPE);
    }

    private Codec.Encoder<Component, String, JsonParseException> encoder() {
        return component -> this.gson.toJson(component, SerializerFactory.COMPONENT_TYPE);
    }

    @Override
    public void write(JsonWriter out, Style value) throws IOException {
        Key font;
        HoverEvent<?> hoverEvent;
        ClickEvent clickEvent;
        String insertion;
        ShadowColor shadowColor;
        out.beginObject();
        for (TextDecoration decoration : DECORATIONS) {
            TextDecoration.State state = value.decoration(decoration);
            if (state == TextDecoration.State.NOT_SET) continue;
            String name = TextDecoration.NAMES.key(decoration);
            assert (name != null);
            out.name(name);
            out.value(state == TextDecoration.State.TRUE);
        }
        @Nullable TextColor color = value.color();
        if (color != null) {
            out.name("color");
            this.gson.toJson((Object)color, SerializerFactory.COLOR_TYPE, out);
        }
        if ((shadowColor = value.shadowColor()) != null && this.emitShadowColor) {
            out.name("shadow_color");
            this.gson.toJson((Object)shadowColor, SerializerFactory.SHADOW_COLOR_TYPE, out);
        }
        if ((insertion = value.insertion()) != null) {
            out.name("insertion");
            out.value(insertion);
        }
        if ((clickEvent = value.clickEvent()) != null) {
            ClickEvent.Action action = clickEvent.action();
            if (this.emitSnakeCaseClick) {
                out.name("click_event");
                out.beginObject();
                out.name("action");
                this.gson.toJson((Object)action, SerializerFactory.CLICK_ACTION_TYPE, out);
                if (action.readable()) {
                    ClickEvent.Payload payload = clickEvent.payload();
                    if (payload instanceof ClickEvent.Payload.Text) {
                        switch (action) {
                            case OPEN_URL: {
                                out.name("url");
                                break;
                            }
                            case RUN_COMMAND: 
                            case SUGGEST_COMMAND: {
                                out.name("command");
                                break;
                            }
                            case COPY_TO_CLIPBOARD: {
                                out.name("value");
                            }
                        }
                        String payloadValue = ((ClickEvent.Payload.Text)payload).value();
                        if (action == ClickEvent.Action.OPEN_URL && this.emitClickUrlHttps && !StyleSerializer.isValidUrlScheme(payloadValue)) {
                            payloadValue = FALLBACK_URL_PROTOCOL + payloadValue;
                        }
                        out.value(payloadValue);
                    } else if (payload instanceof ClickEvent.Payload.Custom) {
                        ClickEvent.Payload.Custom customPayload = (ClickEvent.Payload.Custom)payload;
                        out.name("id");
                        this.gson.toJson((Object)customPayload.key(), SerializerFactory.KEY_TYPE, out);
                        out.name("payload");
                        out.value(customPayload.data());
                    } else if (payload instanceof ClickEvent.Payload.Int) {
                        ClickEvent.Payload.Int intPayload = (ClickEvent.Payload.Int)payload;
                        out.name("page");
                        if (this.emitStringPage) {
                            out.value(String.valueOf(intPayload.integer()));
                        } else {
                            out.value(intPayload.integer());
                        }
                    }
                }
                out.endObject();
            }
            if (this.emitCamelCaseClick && action.payloadType() == ClickEvent.Payload.Text.class) {
                out.name("clickEvent");
                out.beginObject();
                out.name("action");
                this.gson.toJson((Object)action, SerializerFactory.CLICK_ACTION_TYPE, out);
                out.name("value");
                String payloadValue = clickEvent.value();
                if (action == ClickEvent.Action.OPEN_URL && this.emitClickUrlHttps && !StyleSerializer.isValidUrlScheme(payloadValue)) {
                    payloadValue = FALLBACK_URL_PROTOCOL + payloadValue;
                }
                out.value(payloadValue);
                out.endObject();
            }
        }
        if ((hoverEvent = value.hoverEvent()) != null && ((this.emitSnakeCaseHover || this.emitCamelCaseHover) && hoverEvent.action() != HoverEvent.Action.SHOW_ACHIEVEMENT || this.emitValueFieldHover)) {
            HoverEvent.Action<?> action = hoverEvent.action();
            if (this.emitSnakeCaseHover && action != HoverEvent.Action.SHOW_ACHIEVEMENT) {
                out.name("hover_event");
                out.beginObject();
                out.name("action");
                this.gson.toJson(action, SerializerFactory.HOVER_ACTION_TYPE, out);
                if (action == HoverEvent.Action.SHOW_ITEM) {
                    for (Map.Entry<String, JsonElement> entry : this.gson.toJsonTree(hoverEvent.value(), SerializerFactory.SHOW_ITEM_TYPE).getAsJsonObject().entrySet()) {
                        out.name(entry.getKey());
                        this.gson.toJson(entry.getValue(), out);
                    }
                } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                    for (Map.Entry<String, JsonElement> entry : this.gson.toJsonTree(hoverEvent.value(), SerializerFactory.SHOW_ENTITY_TYPE).getAsJsonObject().entrySet()) {
                        out.name(entry.getKey());
                        this.gson.toJson(entry.getValue(), out);
                    }
                } else if (action == HoverEvent.Action.SHOW_TEXT) {
                    out.name("value");
                    this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
                } else {
                    throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
                }
                out.endObject();
            }
            if (this.emitCamelCaseHover || this.emitValueFieldHover) {
                out.name("hoverEvent");
                out.beginObject();
                out.name("action");
                this.gson.toJson(action, SerializerFactory.HOVER_ACTION_TYPE, out);
                if (this.emitCamelCaseHover && action != HoverEvent.Action.SHOW_ACHIEVEMENT) {
                    out.name("contents");
                    if (action == HoverEvent.Action.SHOW_ITEM) {
                        this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ITEM_TYPE, out);
                    } else if (action == HoverEvent.Action.SHOW_ENTITY) {
                        this.gson.toJson(hoverEvent.value(), SerializerFactory.SHOW_ENTITY_TYPE, out);
                    } else if (action == HoverEvent.Action.SHOW_TEXT) {
                        this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
                    } else {
                        throw new JsonParseException("Don't know how to serialize " + hoverEvent.value());
                    }
                }
                if (this.emitValueFieldHover) {
                    out.name("value");
                    this.serializeLegacyHoverEvent(hoverEvent, out);
                }
                out.endObject();
            }
        }
        if ((font = value.font()) != null) {
            out.name("font");
            this.gson.toJson((Object)font, SerializerFactory.KEY_TYPE, out);
        }
        out.endObject();
    }

    private void serializeLegacyHoverEvent(HoverEvent<?> hoverEvent, JsonWriter out) throws IOException {
        if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
            this.gson.toJson(hoverEvent.value(), SerializerFactory.COMPONENT_TYPE, out);
        } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
            this.gson.toJson(hoverEvent.value(), (Type)((Object)String.class), out);
        } else if (this.legacyHover != null) {
            Component serialized = null;
            try {
                if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
                    serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity)hoverEvent.value(), this.encoder());
                } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
                    serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem)hoverEvent.value());
                }
            }
            catch (IOException ex) {
                throw new JsonSyntaxException(ex);
            }
            if (serialized != null) {
                this.gson.toJson((Object)serialized, SerializerFactory.COMPONENT_TYPE, out);
            } else {
                out.nullValue();
            }
        } else {
            out.nullValue();
        }
    }

    private static boolean isValidUrlScheme(String url) {
        return url.startsWith("http://") || url.startsWith(FALLBACK_URL_PROTOCOL);
    }

    static {
        EnumSet<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);
        for (TextDecoration decoration : DECORATIONS) {
            knownDecorations.remove(decoration);
        }
        if (!knownDecorations.isEmpty()) {
            throw new IllegalStateException("Gson serializer is missing some text decorations: " + knownDecorations);
        }
    }
}

