/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ByteBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CharBuffer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.DoubleBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.FloatBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.IntArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.IntBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ListBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.LongArrayBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.LongBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.NumberBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.ShortBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.StringBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.StringTagParseException;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.Tokens;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.jetbrains.annotations.Nullable;

final class TagStringReader {
    private static final int MAX_DEPTH = 512;
    private static final int HEX_RADIX = 16;
    private static final int BINARY_RADIX = 2;
    private static final int DECIMAL_RADIX = 10;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final long[] EMPTY_LONG_ARRAY = new long[0];
    private final CharBuffer buffer;
    private boolean acceptLegacy;
    private boolean acceptHeterogeneousLists;
    private int depth;

    TagStringReader(CharBuffer buffer) {
        this.buffer = buffer;
    }

    public CompoundBinaryTag compound() throws StringTagParseException {
        this.buffer.expect('{');
        if (this.buffer.takeIf('}')) {
            return CompoundBinaryTag.empty();
        }
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
        while (this.buffer.hasMore()) {
            builder.put(this.key(), this.tag());
            if (!this.separatorOrCompleteWith('}')) continue;
            return builder.build();
        }
        throw this.buffer.makeError("Unterminated compound tag!");
    }

    public ListBinaryTag list() throws StringTagParseException {
        boolean prefixedIndex;
        ListBinaryTag.Builder<BinaryTag> builder = this.acceptHeterogeneousLists ? ListBinaryTag.heterogeneousListBinaryTag() : ListBinaryTag.builder();
        this.buffer.expect('[');
        boolean bl = prefixedIndex = this.acceptLegacy && this.buffer.peek() == '0' && this.buffer.peek(1) == ':';
        if (!prefixedIndex && this.buffer.takeIf(']')) {
            return ListBinaryTag.empty();
        }
        while (this.buffer.hasMore()) {
            if (prefixedIndex) {
                this.buffer.takeUntil(':');
            }
            BinaryTag next = this.tag();
            builder.add(next);
            if (!this.separatorOrCompleteWith(']')) continue;
            return builder.build();
        }
        throw this.buffer.makeError("Reached end of file without end of list tag!");
    }

    public BinaryTag array(char elementType) throws StringTagParseException {
        this.buffer.expect('[').expect(elementType).expect(';');
        elementType = Character.toLowerCase(elementType);
        if (elementType == 'b') {
            return ByteArrayBinaryTag.byteArrayBinaryTag(this.byteArray());
        }
        if (elementType == 'i') {
            return IntArrayBinaryTag.intArrayBinaryTag(this.intArray());
        }
        if (elementType == 'l') {
            return LongArrayBinaryTag.longArrayBinaryTag(this.longArray());
        }
        throw this.buffer.makeError("Type " + elementType + " is not a valid element type in an array!");
    }

    private byte[] byteArray() throws StringTagParseException {
        if (this.buffer.takeIf(']')) {
            return EMPTY_BYTE_ARRAY;
        }
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        while (this.buffer.hasMore()) {
            CharSequence value = this.buffer.skipWhitespace().takeUntil('b');
            try {
                bytes.add(Byte.valueOf(value.toString()));
            }
            catch (NumberFormatException ex) {
                throw this.buffer.makeError("All elements of a byte array must be bytes!");
            }
            if (!this.separatorOrCompleteWith(']')) continue;
            byte[] result = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); ++i) {
                result[i] = (Byte)bytes.get(i);
            }
            return result;
        }
        throw this.buffer.makeError("Reached end of document without array close");
    }

    private int[] intArray() throws StringTagParseException {
        if (this.buffer.takeIf(']')) {
            return EMPTY_INT_ARRAY;
        }
        IntStream.Builder builder = IntStream.builder();
        while (this.buffer.hasMore()) {
            BinaryTag value = this.tag();
            if (!(value instanceof IntBinaryTag)) {
                throw this.buffer.makeError("All elements of an int array must be ints!");
            }
            builder.add(((IntBinaryTag)value).intValue());
            if (!this.separatorOrCompleteWith(']')) continue;
            return builder.build().toArray();
        }
        throw this.buffer.makeError("Reached end of document without array close");
    }

    private long[] longArray() throws StringTagParseException {
        if (this.buffer.takeIf(']')) {
            return EMPTY_LONG_ARRAY;
        }
        LongStream.Builder longs = LongStream.builder();
        while (this.buffer.hasMore()) {
            CharSequence value = this.buffer.skipWhitespace().takeUntil('l');
            try {
                longs.add(Long.parseLong(value.toString()));
            }
            catch (NumberFormatException ex) {
                throw this.buffer.makeError("All elements of a long array must be longs!");
            }
            if (!this.separatorOrCompleteWith(']')) continue;
            return longs.build().toArray();
        }
        throw this.buffer.makeError("Reached end of document without array close");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String key() throws StringTagParseException {
        this.buffer.skipWhitespace();
        char starChar = this.buffer.peek();
        try {
            if (starChar == '\'' || starChar == '\"') {
                String string = TagStringReader.unescape(this.buffer.takeUntil(this.buffer.take()).toString());
                return string;
            }
            StringBuilder builder = new StringBuilder();
            while (this.buffer.hasMore()) {
                char peek = this.buffer.peek();
                if (!Tokens.id(peek)) {
                    if (!this.acceptLegacy) break;
                    if (peek == '\\') {
                        this.buffer.take();
                        continue;
                    }
                    if (peek == ':') break;
                    builder.append(this.buffer.take());
                    continue;
                }
                builder.append(this.buffer.take());
            }
            String string = builder.toString();
            return string;
        }
        finally {
            this.buffer.expect(':');
        }
    }

    public BinaryTag tag() throws StringTagParseException {
        if (this.depth++ > 512) {
            throw this.buffer.makeError("Exceeded maximum allowed depth of 512 when reading tag");
        }
        try {
            char startToken = this.buffer.skipWhitespace().peek();
            switch (startToken) {
                case '{': {
                    CompoundBinaryTag compoundBinaryTag = this.compound();
                    return compoundBinaryTag;
                }
                case '[': {
                    if (this.buffer.hasMore(2) && this.buffer.peek(2) == ';') {
                        BinaryTag binaryTag = this.array(this.buffer.peek(1));
                        return binaryTag;
                    }
                    ListBinaryTag listBinaryTag = this.list();
                    return listBinaryTag;
                }
                case '\"': 
                case '\'': {
                    this.buffer.advance();
                    StringBinaryTag stringBinaryTag = StringBinaryTag.stringBinaryTag(TagStringReader.unescape(this.buffer.takeUntil(startToken).toString()));
                    return stringBinaryTag;
                }
            }
            BinaryTag binaryTag = this.scalar();
            return binaryTag;
        }
        finally {
            --this.depth;
        }
    }

    private BinaryTag scalar() throws StringTagParseException {
        String original;
        block19: {
            char signChar;
            boolean signed;
            StringBuilder builder = new StringBuilder();
            while (this.buffer.hasMore()) {
                char current = this.buffer.peek();
                if (current == '\\') {
                    this.buffer.advance();
                    current = this.buffer.take();
                } else {
                    if (!Tokens.id(current)) break;
                    this.buffer.advance();
                }
                builder.append(current);
            }
            if (builder.length() == 0) {
                throw this.buffer.makeError("Expected a value but got nothing");
            }
            original = builder.toString();
            int radix = this.extractRadix(builder, original);
            if (builder.length() == 0) {
                throw this.buffer.makeError("Input is a radix, not a number");
            }
            char last = builder.charAt(builder.length() - 1);
            boolean hasSignToken = false;
            boolean bl = signed = radix != 16;
            if (builder.length() > 2 && ((signChar = builder.charAt(builder.length() - 2)) == 's' || signChar == 'u')) {
                hasSignToken = true;
                signed = signChar == 's';
                builder.deleteCharAt(builder.length() - 2);
            }
            boolean hasTypeToken = false;
            char typeToken = 'i';
            if (Tokens.numericType(last) && (hasSignToken || radix != 16)) {
                hasTypeToken = true;
                typeToken = Character.toLowerCase(last);
                builder.deleteCharAt(builder.length() - 1);
            }
            if (!(signed || typeToken != 'f' && typeToken != 'd')) {
                throw this.buffer.makeError("Cannot create unsigned floating point numbers");
            }
            String strippedString = builder.toString().replace("_", "");
            if (hasTypeToken) {
                try {
                    NumberBinaryTag tag = this.parseNumberTag(strippedString, typeToken, radix, signed);
                    if (tag != null) {
                        return tag;
                    }
                }
                catch (NumberFormatException tag) {}
            } else {
                try {
                    return IntBinaryTag.intBinaryTag(this.parseInt(strippedString, radix, signed));
                }
                catch (NumberFormatException ex) {
                    if (strippedString.indexOf(46) == -1) break block19;
                    try {
                        return DoubleBinaryTag.doubleBinaryTag(Double.parseDouble(strippedString));
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
            }
        }
        if (original.equalsIgnoreCase("true")) {
            return ByteBinaryTag.ONE;
        }
        if (original.equalsIgnoreCase("false")) {
            return ByteBinaryTag.ZERO;
        }
        return StringBinaryTag.stringBinaryTag(original);
    }

    private int extractRadix(StringBuilder builder, String original) {
        int radixPrefixOffset = 0;
        char first = builder.charAt(0);
        if (first == '+' || first == '-') {
            radixPrefixOffset = 1;
        }
        int radixEndIndex = 2 + radixPrefixOffset;
        int radix = original.length() > radixEndIndex && (original.startsWith("0b", radixPrefixOffset) || original.startsWith("0B", radixPrefixOffset)) ? 2 : (original.startsWith("0x", radixPrefixOffset) || original.startsWith("0X", radixPrefixOffset) ? 16 : 10);
        if (radix != 10) {
            builder.delete(radixPrefixOffset, radixEndIndex);
        }
        return radix;
    }

    @Nullable
    private NumberBinaryTag parseNumberTag(String s, char typeToken, int radix, boolean signed) {
        switch (typeToken) {
            case 'b': {
                return ByteBinaryTag.byteBinaryTag(this.parseByte(s, radix, signed));
            }
            case 's': {
                return ShortBinaryTag.shortBinaryTag(this.parseShort(s, radix, signed));
            }
            case 'i': {
                return IntBinaryTag.intBinaryTag(this.parseInt(s, radix, signed));
            }
            case 'l': {
                return LongBinaryTag.longBinaryTag(this.parseLong(s, radix, signed));
            }
            case 'f': {
                float floatValue = Float.parseFloat(s);
                if (!Float.isFinite(floatValue)) break;
                return FloatBinaryTag.floatBinaryTag(floatValue);
            }
            case 'd': {
                double doubleValue = Double.parseDouble(s);
                if (!Double.isFinite(doubleValue)) break;
                return DoubleBinaryTag.doubleBinaryTag(doubleValue);
            }
        }
        return null;
    }

    private byte parseByte(String s, int radix, boolean signed) {
        if (signed) {
            return Byte.parseByte(s, radix);
        }
        int parsedInt = Integer.parseInt(s, radix);
        if (parsedInt >> 8 == 0) {
            return (byte)parsedInt;
        }
        throw new NumberFormatException();
    }

    private short parseShort(String s, int radix, boolean signed) {
        if (signed) {
            return Short.parseShort(s, radix);
        }
        int parsedInt = Integer.parseInt(s, radix);
        if (parsedInt >> 16 == 0) {
            return (short)parsedInt;
        }
        throw new NumberFormatException();
    }

    private int parseInt(String s, int radix, boolean signed) {
        return signed ? Integer.parseInt(s, radix) : Integer.parseUnsignedInt(s, radix);
    }

    private long parseLong(String s, int radix, boolean signed) {
        return signed ? Long.parseLong(s, radix) : Long.parseUnsignedLong(s, radix);
    }

    private boolean separatorOrCompleteWith(char endCharacter) throws StringTagParseException {
        if (this.buffer.takeIf(endCharacter)) {
            return true;
        }
        this.buffer.expect(',');
        return this.buffer.takeIf(endCharacter);
    }

    private static String unescape(String withEscapes) {
        int escapeIdx = withEscapes.indexOf(92);
        if (escapeIdx == -1) {
            return withEscapes;
        }
        int lastEscape = 0;
        StringBuilder output = new StringBuilder(withEscapes.length());
        do {
            output.append(withEscapes, lastEscape, escapeIdx);
        } while ((escapeIdx = withEscapes.indexOf(92, (lastEscape = escapeIdx + 1) + 1)) != -1);
        output.append(withEscapes.substring(lastEscape));
        return output.toString();
    }

    public void legacy(boolean acceptLegacy) {
        this.acceptLegacy = acceptLegacy;
    }

    public void heterogeneousLists(boolean acceptHeterogeneousLists) {
        this.acceptHeterogeneousLists = acceptHeterogeneousLists;
    }
}

