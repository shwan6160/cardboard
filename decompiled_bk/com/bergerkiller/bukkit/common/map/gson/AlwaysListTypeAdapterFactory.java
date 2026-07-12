/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapter;
import com.bergerkiller.bukkit.common.dep.gson.TypeAdapterFactory;
import com.bergerkiller.bukkit.common.dep.gson.reflect.TypeToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonReader;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonToken;
import com.bergerkiller.bukkit.common.dep.gson.stream.JsonWriter;
import com.bergerkiller.bukkit.common.dep.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class AlwaysListTypeAdapterFactory<E>
implements TypeAdapterFactory {
    private AlwaysListTypeAdapterFactory() {
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (!List.class.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }
        Type elementType = AlwaysListTypeAdapterFactory.resolveTypeArgument(typeToken.getType());
        TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
        TypeAdapter alwaysListTypeAdapter = new AlwaysListTypeAdapter(elementTypeAdapter).nullSafe();
        return alwaysListTypeAdapter;
    }

    private static Type resolveTypeArgument(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return Object.class;
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        return parameterizedType.getActualTypeArguments()[0];
    }

    private static final class AlwaysListTypeAdapter<E>
    extends TypeAdapter<List<E>> {
        private final TypeAdapter<E> elementTypeAdapter;

        private AlwaysListTypeAdapter(TypeAdapter<E> elementTypeAdapter) {
            this.elementTypeAdapter = elementTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, List<E> list) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<E> read(JsonReader in) throws IOException {
            ArrayList<E> list = new ArrayList<E>();
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY: {
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(this.elementTypeAdapter.read(in));
                    }
                    in.endArray();
                    break;
                }
                case BEGIN_OBJECT: 
                case STRING: 
                case NUMBER: 
                case BOOLEAN: {
                    list.add(this.elementTypeAdapter.read(in));
                    break;
                }
                case NULL: {
                    throw new AssertionError((Object)"Must never happen: check if the type adapter configured with .nullSafe()");
                }
                case NAME: 
                case END_ARRAY: 
                case END_OBJECT: 
                case END_DOCUMENT: {
                    throw new MalformedJsonException("Unexpected token: " + (Object)((Object)token));
                }
                default: {
                    throw new AssertionError((Object)("Must never happen: " + (Object)((Object)token)));
                }
            }
            return list;
        }
    }
}

