/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;

public class SNBTDeserializer<TO> {
    private final Factory<TO, ?, ?> factory;
    private final String contents;
    private final int length;
    private int position = 0;

    public SNBTDeserializer(String contents, Factory<TO, ?, ?> factory) {
        this.contents = contents;
        this.length = contents.length();
        this.factory = factory;
    }

    public static Object parse(String snbtContent) {
        return new SNBTDeserializer<Object>(snbtContent, Factory.JAVA).next();
    }

    public static <T> T parse(String snbtContent, Factory<T, ?, ?> factory) {
        return new SNBTDeserializer<T>(snbtContent, factory).next();
    }

    public TO next() {
        this.consumeWhitespace();
        return this.nextValue(this.factory);
    }

    private <T, L extends T, M extends T> T nextValue(Factory<T, L, M> factory) {
        int length = this.length;
        if (this.tryConsume('{')) {
            M result = factory.createMap();
            while (true) {
                this.consumeWhitespace();
                if (this.tryConsume(',')) {
                    this.consumeWhitespace();
                    continue;
                }
                if (this.position >= length || this.tryConsume('}')) break;
                String key = this.consumeCompoundKey();
                this.consumeWhitespace();
                if (!this.tryConsume(':')) {
                    factory.addToMap(result, key, factory.wrap(null));
                    continue;
                }
                this.consumeWhitespace();
                T value = this.nextValue(factory);
                factory.addToMap(result, key, value);
            }
            return (T)result;
        }
        if (this.tryConsume('[')) {
            this.consumeWhitespace();
            NBTArrayFormat format = this.tryConsumeArrayFormat();
            if (format != null) {
                return factory.wrap(format.build(this.consumeList(Factory.JAVA)));
            }
            return (T)this.consumeList(factory);
        }
        if (this.tryConsume('\"')) {
            return factory.wrap(this.consumeString('\"'));
        }
        if (this.tryConsume('\'')) {
            return factory.wrap(this.consumeString('\''));
        }
        if (this.tryConsume("true")) {
            return factory.wrap((byte)1);
        }
        if (this.tryConsume("false")) {
            return factory.wrap((byte)0);
        }
        return factory.wrap(this.consumeNumber());
    }

    private <T, L extends T, M extends T> L consumeList(Factory<T, L, M> factory) {
        L list = factory.createList();
        while (this.position < this.length && !this.tryConsume(']')) {
            if (this.tryConsume(',')) {
                this.consumeWhitespace();
                continue;
            }
            factory.addToList(list, this.nextValue(factory));
            this.consumeWhitespace();
        }
        return list;
    }

    private Object consumeNumber() {
        int numberStart;
        NBTNumberFormat format = NBTNumberFormat.INTEGER;
        int numberEnd = numberStart = this.position;
        while (true) {
            if (numberEnd >= this.length) {
                this.position = this.length;
                break;
            }
            char c = this.contents.charAt(numberEnd);
            if (c == ',' || c == ']' || c == '}' || c == ' ') {
                this.position = numberEnd;
                break;
            }
            if (c == '.' || c == 'E') {
                format = NBTNumberFormat.DOUBLE;
                ++numberEnd;
                continue;
            }
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                NBTNumberFormat matchedFormat = NBTNumberFormat.find(c);
                if (matchedFormat != null) {
                    format = matchedFormat;
                    this.position = numberEnd + 1;
                    break;
                }
                this.position = numberEnd;
                break;
            }
            ++numberEnd;
        }
        if (numberStart == numberEnd) {
            return null;
        }
        return format.parse(this.contents.substring(numberStart, numberEnd));
    }

    private String consumeCompoundKey() {
        char c;
        int position;
        if (this.tryConsume('\"')) {
            return this.consumeString('\"');
        }
        if (this.tryConsume('\'')) {
            return this.consumeString('\'');
        }
        String contents = this.contents;
        int length = this.length;
        for (position = this.position; position < length && ((c = contents.charAt(position)) >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.' || c == '+'); ++position) {
        }
        if (this.position == position) {
            while (position < length && contents.charAt(position) != ':') {
                ++position;
            }
        }
        String key = contents.substring(this.position, position);
        this.position = position;
        return key;
    }

    private String consumeString(char delimiterQuoteChar) {
        String result;
        int stringEnd = this.contents.indexOf(delimiterQuoteChar, this.position);
        if (stringEnd != -1 && (stringEnd == this.position || this.contents.charAt(stringEnd - 1) != '\\') && (result = this.contents.substring(this.position, stringEnd)).indexOf(92) == -1) {
            this.position = stringEnd + 1;
            return result;
        }
        StringBuilder str = new StringBuilder();
        boolean escaped = false;
        int position = this.position;
        while (position < this.length) {
            char c = this.contents.charAt(position++);
            if (escaped) {
                escaped = false;
                str.append(c);
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == delimiterQuoteChar) break;
            str.append(c);
        }
        this.position = position;
        return str.toString();
    }

    private NBTArrayFormat tryConsumeArrayFormat() {
        NBTArrayFormat format;
        int position = this.position;
        int length = this.length;
        String contents = this.contents;
        if (length - position >= 2 && contents.charAt(position + 1) == ';' && (format = NBTArrayFormat.parse(contents.charAt(position))) != null) {
            this.position = position + 2;
            return format;
        }
        return null;
    }

    private void consumeWhitespace() {
        int position;
        int length = this.length;
        for (position = this.position; position < length && this.contents.charAt(position) == ' '; ++position) {
        }
        this.position = position;
    }

    private boolean tryConsume(char c) {
        int position = this.position;
        if (position < this.length && this.contents.charAt(position) == c) {
            this.position = position + 1;
            return true;
        }
        return false;
    }

    private boolean tryConsume(String s) {
        int position = this.position;
        int s_len = s.length();
        if (s_len > this.length - position) {
            return false;
        }
        for (int i = 0; i < s_len; ++i) {
            if (this.contents.charAt(position + i) == s.charAt(i)) continue;
            return false;
        }
        this.position = position + s_len;
        return true;
    }

    public static interface Factory<T, L extends T, M extends T> {
        public static final Factory<Object, List<Object>, Map<String, Object>> JAVA = new Factory<Object, List<Object>, Map<String, Object>>(){

            @Override
            public Object wrap(Object data) {
                return data;
            }

            @Override
            public List<Object> createList() {
                return new ArrayList<Object>();
            }

            @Override
            public Map<String, Object> createMap() {
                return new LinkedHashMap<String, Object>();
            }

            @Override
            public void addToList(List<Object> list, Object item) {
                list.add(item);
            }

            @Override
            public void addToMap(Map<String, Object> map, String key, Object value) {
                map.put(key, value);
            }
        };
        public static final Factory<CommonTag, CommonTagList, CommonTagCompound> NBT = new Factory<CommonTag, CommonTagList, CommonTagCompound>(){

            @Override
            public CommonTag wrap(Object data) {
                if (data == null) {
                    return CommonTag.createForData("");
                }
                return CommonTag.createForData(data);
            }

            @Override
            public CommonTagList createList() {
                return new CommonTagList();
            }

            @Override
            public CommonTagCompound createMap() {
                return new CommonTagCompound();
            }

            @Override
            public void addToList(CommonTagList list, CommonTag item) {
                list.add(item);
            }

            @Override
            public void addToMap(CommonTagCompound map, String key, CommonTag value) {
                map.put(key, value);
            }
        };

        public T wrap(Object var1);

        public L createList();

        public M createMap();

        public void addToList(L var1, T var2);

        public void addToMap(M var1, String var2, T var3);
    }

    private static enum NBTArrayFormat {
        BYTE_ARRAY('B', byte[]::new, (array, index, value) -> {
            if (value instanceof Number) {
                array[index] = ((Number)value).byteValue();
            }
        }),
        INT_ARRAY('I', int[]::new, (array, index, value) -> {
            if (value instanceof Number) {
                array[index] = ((Number)value).intValue();
            }
        }),
        LONG_ARRAY('L', long[]::new, (array, index, value) -> {
            if (value instanceof Number) {
                array[index] = ((Number)value).longValue();
            }
        });

        private static final NBTArrayFormat[] FORMATS;
        private final char c;
        private final Function<List<?>, Object> arrayBuilder;

        private <T> NBTArrayFormat(char c, IntFunction<T> create, ArraySetter<T> setter) {
            this.c = c;
            this.arrayBuilder = list -> {
                int length = list.size();
                Object array = create.apply(length);
                for (int i = 0; i < length; ++i) {
                    setter.set(array, i, list.get(i));
                }
                return array;
            };
        }

        public static NBTArrayFormat parse(char identifier) {
            for (NBTArrayFormat f : FORMATS) {
                if (f.c != identifier) continue;
                return f;
            }
            return null;
        }

        public Object build(List<?> values) {
            return this.arrayBuilder.apply(values);
        }

        static {
            FORMATS = NBTArrayFormat.values();
        }

        private static interface ArraySetter<T> {
            public void set(T var1, int var2, Object var3);
        }
    }

    private static enum NBTNumberFormat {
        FLOAT('f', Float::parseFloat),
        DOUBLE('d', Double::parseDouble),
        BYTE('b', Byte::parseByte),
        SHORT('s', Short::parseShort),
        LONG('l', Long::parseLong),
        INTEGER('i', Integer::parseInt);

        private final char c1;
        private final char c2;
        private final Function<String, Object> parser;
        private static final NBTNumberFormat[] FORMATS;

        private NBTNumberFormat(char c, Function<String, Object> parser) {
            this.c1 = c;
            this.c2 = Character.toUpperCase(c);
            this.parser = parser;
        }

        public static NBTNumberFormat find(char c) {
            for (NBTNumberFormat format : FORMATS) {
                if (format.c1 != c && format.c2 != c) continue;
                return format;
            }
            return null;
        }

        public Object parse(String input) {
            try {
                return this.parser.apply(input);
            }
            catch (NumberFormatException ex) {
                return input;
            }
        }

        static {
            FORMATS = NBTNumberFormat.values();
        }
    }
}

