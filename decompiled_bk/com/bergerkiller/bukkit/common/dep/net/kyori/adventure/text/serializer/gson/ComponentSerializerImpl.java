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
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.reflect.TypeToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BlockNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BuildableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ComponentLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.EntityNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.KeybindComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponentBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ObjectComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScoreComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.SelectorComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.StorageNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslatableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslationArgument;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.ObjectContents;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.object.SpriteObjectContents;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonHacks;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Buildable;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl
extends TypeAdapter<Component> {
    static final Type COMPONENT_LIST_TYPE = new TypeToken<List<Component>>(){}.getType();
    static final Type TRANSLATABLE_ARGUMENT_LIST_TYPE = new TypeToken<List<TranslationArgument>>(){}.getType();
    static final Type PROPERTY_LIST_TYPE = new TypeToken<List<PlayerHeadObjectContents.ProfileProperty>>(){}.getType();
    private final boolean emitCompactTextComponent;
    private final Gson gson;

    static TypeAdapter<Component> create(OptionState features, Gson gson) {
        return new ComponentSerializerImpl(features.value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT), gson).nullSafe();
    }

    private ComponentSerializerImpl(boolean emitCompactTextComponent, Gson gson) {
        this.emitCompactTextComponent = emitCompactTextComponent;
        this.gson = gson;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public BuildableComponent<?, ?> read(JsonReader in) throws IOException {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder;
        JsonToken token = in.peek();
        if (token == JsonToken.STRING || token == JsonToken.NUMBER || token == JsonToken.BOOLEAN) {
            return Component.text(GsonHacks.readString(in));
        }
        if (token == JsonToken.BEGIN_ARRAY) {
            void var3_4;
            Object var3_3 = null;
            in.beginArray();
            while (in.hasNext()) {
                Object child = this.read(in);
                if (var3_4 == null) {
                    Buildable.Builder builder2 = child.toBuilder();
                    continue;
                }
                var3_4.append((Component)child);
            }
            if (var3_4 == null) {
                throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
            }
            in.endArray();
            return var3_4.build();
        }
        if (token != JsonToken.BEGIN_OBJECT) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
        }
        JsonObject jsonObject = new JsonObject();
        List extra = Collections.emptyList();
        String text = null;
        String translate = null;
        String translateFallback = null;
        List translateWith = null;
        String scoreName = null;
        String scoreObjective = null;
        String scoreValue = null;
        String selector = null;
        String keybind = null;
        String nbt = null;
        boolean nbtInterpret = false;
        BlockNBTComponent.Pos nbtBlock = null;
        String nbtEntity = null;
        Key nbtStorage = null;
        Object separator = null;
        Key atlas = null;
        Key sprite = null;
        PlayerHeadObjectContents.Builder playerHeadContents = null;
        boolean playerHeadContentsHasProfile = false;
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if (fieldName.equals("text")) {
                text = GsonHacks.readString(in);
                continue;
            }
            if (fieldName.equals("translate")) {
                translate = in.nextString();
                continue;
            }
            if (fieldName.equals("fallback")) {
                translateFallback = in.nextString();
                continue;
            }
            if (fieldName.equals("with")) {
                translateWith = (List)this.gson.fromJson(in, TRANSLATABLE_ARGUMENT_LIST_TYPE);
                continue;
            }
            if (fieldName.equals("score")) {
                in.beginObject();
                while (in.hasNext()) {
                    String scoreFieldName = in.nextName();
                    if (scoreFieldName.equals("name")) {
                        scoreName = in.nextString();
                        continue;
                    }
                    if (scoreFieldName.equals("objective")) {
                        scoreObjective = in.nextString();
                        continue;
                    }
                    if (scoreFieldName.equals("value")) {
                        scoreValue = in.nextString();
                        continue;
                    }
                    in.skipValue();
                }
                if (scoreName == null || scoreObjective == null) {
                    throw new JsonParseException("A score component requires a name and objective");
                }
                in.endObject();
                continue;
            }
            if (fieldName.equals("selector")) {
                selector = in.nextString();
                continue;
            }
            if (fieldName.equals("keybind")) {
                keybind = in.nextString();
                continue;
            }
            if (fieldName.equals("nbt")) {
                nbt = in.nextString();
                continue;
            }
            if (fieldName.equals("interpret")) {
                nbtInterpret = in.nextBoolean();
                continue;
            }
            if (fieldName.equals("block")) {
                nbtBlock = (BlockNBTComponent.Pos)this.gson.fromJson(in, SerializerFactory.BLOCK_NBT_POS_TYPE);
                continue;
            }
            if (fieldName.equals("entity")) {
                nbtEntity = in.nextString();
                continue;
            }
            if (fieldName.equals("storage")) {
                nbtStorage = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("extra")) {
                extra = (List)this.gson.fromJson(in, COMPONENT_LIST_TYPE);
                continue;
            }
            if (fieldName.equals("separator")) {
                separator = this.read(in);
                continue;
            }
            if (fieldName.equals("atlas")) {
                atlas = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("sprite")) {
                sprite = (Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
                continue;
            }
            if (fieldName.equals("player")) {
                JsonToken playerToken;
                if (playerHeadContents == null) {
                    playerHeadContents = ObjectContents.playerHead();
                }
                if ((playerToken = in.peek()) == JsonToken.STRING) {
                    playerHeadContentsHasProfile = true;
                    playerHeadContents.name(in.nextString());
                    continue;
                }
                if (playerToken == JsonToken.BEGIN_OBJECT) {
                    playerHeadContentsHasProfile = true;
                    in.beginObject();
                    while (in.hasNext()) {
                        String playerHeadFieldName = in.nextName();
                        if (playerHeadFieldName.equals("name")) {
                            playerHeadContents.name(in.nextString());
                            continue;
                        }
                        if (playerHeadFieldName.equals("id")) {
                            playerHeadContents.id((UUID)this.gson.fromJson(in, SerializerFactory.UUID_TYPE));
                            continue;
                        }
                        if (playerHeadFieldName.equals("properties")) {
                            JsonToken propertyToken = in.peek();
                            if (propertyToken == JsonToken.BEGIN_ARRAY) {
                                playerHeadContents.profileProperties((Collection)this.gson.fromJson(in, PROPERTY_LIST_TYPE));
                                continue;
                            }
                            if (propertyToken == JsonToken.BEGIN_OBJECT) {
                                in.beginObject();
                                while (in.hasNext()) {
                                    String propertyName = in.nextName();
                                    in.beginArray();
                                    while (in.hasNext()) {
                                        playerHeadContents.profileProperty(PlayerHeadObjectContents.property(propertyName, in.nextString()));
                                    }
                                    in.endArray();
                                }
                                in.endObject();
                                continue;
                            }
                            in.skipValue();
                            continue;
                        }
                        if (playerHeadFieldName.equals("texture")) {
                            playerHeadContents.texture((Key)this.gson.fromJson(in, SerializerFactory.KEY_TYPE));
                            continue;
                        }
                        in.skipValue();
                    }
                    in.endObject();
                    continue;
                }
                in.skipValue();
                continue;
            }
            if (fieldName.equals("hat")) {
                if (playerHeadContents == null) {
                    playerHeadContents = ObjectContents.playerHead();
                }
                playerHeadContents.hat(in.nextBoolean());
                continue;
            }
            jsonObject.add(fieldName, (JsonElement)this.gson.fromJson(in, (Type)((Object)JsonElement.class)));
        }
        if (text != null) {
            builder = Component.text().content(text);
        } else if (translate != null) {
            builder = translateWith != null ? Component.translatable().key(translate).fallback(translateFallback).arguments(translateWith) : Component.translatable().key(translate).fallback(translateFallback);
        } else if (scoreName != null && scoreObjective != null) {
            builder = scoreValue == null ? Component.score().name(scoreName).objective(scoreObjective) : Component.score().name(scoreName).objective(scoreObjective).value(scoreValue);
        } else if (selector != null) {
            builder = Component.selector().pattern(selector).separator((ComponentLike)separator);
        } else if (keybind != null) {
            builder = Component.keybind().keybind(keybind);
        } else if (nbt != null) {
            if (nbtBlock != null) {
                builder = ComponentSerializerImpl.nbt(Component.blockNBT(), nbt, nbtInterpret, separator).pos(nbtBlock);
            } else if (nbtEntity != null) {
                builder = ComponentSerializerImpl.nbt(Component.entityNBT(), nbt, nbtInterpret, separator).selector(nbtEntity);
            } else {
                if (nbtStorage == null) throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
                builder = ComponentSerializerImpl.nbt(Component.storageNBT(), nbt, nbtInterpret, separator).storage(nbtStorage);
            }
        } else if (sprite != null) {
            builder = Component.object().contents(ObjectContents.sprite(atlas != null ? atlas : SpriteObjectContents.DEFAULT_ATLAS, sprite));
        } else {
            if (playerHeadContents == null || !playerHeadContentsHasProfile) throw ComponentSerializerImpl.notSureHowToDeserialize(in.getPath());
            builder = Component.object().contents(playerHeadContents.build());
        }
        builder.style(this.gson.fromJson((JsonElement)jsonObject, SerializerFactory.STYLE_TYPE)).append(extra);
        in.endObject();
        return builder.build();
    }

    private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
        return builder.nbtPath(nbt).interpret(interpret).separator(separator);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        JsonElement style;
        if (value instanceof TextComponent && value.children().isEmpty() && !value.hasStyling() && this.emitCompactTextComponent) {
            out.value(((TextComponent)value).content());
            return;
        }
        out.beginObject();
        if (value.hasStyling() && (style = this.gson.toJsonTree(value.style(), SerializerFactory.STYLE_TYPE)).isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : style.getAsJsonObject().entrySet()) {
                out.name(entry.getKey());
                this.gson.toJson(entry.getValue(), out);
            }
        }
        if (!value.children().isEmpty()) {
            out.name("extra");
            this.gson.toJson(value.children(), COMPONENT_LIST_TYPE, out);
        }
        if (value instanceof TextComponent) {
            out.name("text");
            out.value(((TextComponent)value).content());
        } else if (value instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent)value;
            out.name("translate");
            out.value(translatable.key());
            @Nullable String fallback = translatable.fallback();
            if (fallback != null) {
                out.name("fallback");
                out.value(fallback);
            }
            if (!translatable.arguments().isEmpty()) {
                out.name("with");
                this.gson.toJson(translatable.arguments(), TRANSLATABLE_ARGUMENT_LIST_TYPE, out);
            }
        } else if (value instanceof ScoreComponent) {
            ScoreComponent score = (ScoreComponent)value;
            out.name("score");
            out.beginObject();
            out.name("name");
            out.value(score.name());
            out.name("objective");
            out.value(score.objective());
            if (score.value() != null) {
                out.name("value");
                out.value(score.value());
            }
            out.endObject();
        } else if (value instanceof SelectorComponent) {
            SelectorComponent selector = (SelectorComponent)value;
            out.name("selector");
            out.value(selector.pattern());
            this.serializeSeparator(out, selector.separator());
        } else if (value instanceof KeybindComponent) {
            out.name("keybind");
            out.value(((KeybindComponent)value).keybind());
        } else if (value instanceof NBTComponent) {
            NBTComponent nbt = (NBTComponent)value;
            out.name("nbt");
            out.value(nbt.nbtPath());
            out.name("interpret");
            out.value(nbt.interpret());
            this.serializeSeparator(out, nbt.separator());
            if (value instanceof BlockNBTComponent) {
                out.name("block");
                this.gson.toJson((Object)((BlockNBTComponent)value).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, out);
            } else if (value instanceof EntityNBTComponent) {
                out.name("entity");
                out.value(((EntityNBTComponent)value).selector());
            } else {
                if (!(value instanceof StorageNBTComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
                out.name("storage");
                this.gson.toJson((Object)((StorageNBTComponent)value).storage(), SerializerFactory.KEY_TYPE, out);
            }
        } else {
            if (!(value instanceof ObjectComponent)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
            ObjectComponent objectComponent = (ObjectComponent)value;
            ObjectContents contents = objectComponent.contents();
            if (contents instanceof SpriteObjectContents) {
                SpriteObjectContents spriteContents = (SpriteObjectContents)contents;
                if (!spriteContents.atlas().equals(SpriteObjectContents.DEFAULT_ATLAS)) {
                    out.name("atlas");
                    this.gson.toJson((Object)spriteContents.atlas(), SerializerFactory.KEY_TYPE, out);
                }
                out.name("sprite");
                this.gson.toJson((Object)spriteContents.sprite(), SerializerFactory.KEY_TYPE, out);
            } else {
                if (!(contents instanceof PlayerHeadObjectContents)) throw ComponentSerializerImpl.notSureHowToSerialize(value);
                PlayerHeadObjectContents playerHeadContents = (PlayerHeadObjectContents)contents;
                out.name("hat");
                out.value(playerHeadContents.hat());
                String playerName = playerHeadContents.name();
                UUID playerId = playerHeadContents.id();
                List<PlayerHeadObjectContents.ProfileProperty> properties = playerHeadContents.profileProperties();
                Key texture = playerHeadContents.texture();
                out.name("player");
                if (playerName != null && playerId == null && properties.isEmpty() && texture == null) {
                    out.value(playerName);
                } else {
                    out.beginObject();
                    if (playerName != null) {
                        out.name("name");
                        out.value(playerName);
                    }
                    if (playerId != null) {
                        out.name("id");
                        this.gson.toJson((Object)playerId, SerializerFactory.UUID_TYPE, out);
                    }
                    if (!properties.isEmpty()) {
                        out.name("properties");
                        this.gson.toJson(properties, PROPERTY_LIST_TYPE, out);
                    }
                    if (texture != null) {
                        out.name("texture");
                        this.gson.toJson((Object)texture, SerializerFactory.KEY_TYPE, out);
                    }
                    out.endObject();
                }
            }
        }
        out.endObject();
    }

    private void serializeSeparator(JsonWriter out, @Nullable Component separator) throws IOException {
        if (separator != null) {
            out.name("separator");
            this.write(out, separator);
        }
    }

    static JsonParseException notSureHowToDeserialize(Object element) {
        return new JsonParseException("Don't know how to turn " + element + " into a Component");
    }

    private static IllegalArgumentException notSureHowToSerialize(Component component) {
        return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
    }
}

