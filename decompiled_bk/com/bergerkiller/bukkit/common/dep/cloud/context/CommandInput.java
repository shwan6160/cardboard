/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.index.qual.NonNegative
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 *  org.checkerframework.dataflow.qual.Pure
 *  org.checkerframework.dataflow.qual.SideEffectFree
 */
package com.bergerkiller.bukkit.common.dep.cloud.context;

import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInputImpl;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ByteRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.DoubleRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.FloatRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.IntRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.LongRange;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.ShortRange;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apiguardian.api.API;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;

@API(status=API.Status.EXPERIMENTAL)
public interface CommandInput {
    public static final List<String> BOOLEAN_STRICT = Collections.unmodifiableList(Arrays.asList("TRUE", "FALSE"));
    public static final List<String> BOOLEAN_LIBERAL = Collections.unmodifiableList(Arrays.asList("TRUE", "YES", "ON", "FALSE", "NO", "OFF"));
    public static final List<String> BOOLEAN_LIBERAL_TRUE = Collections.unmodifiableList(Arrays.asList("TRUE", "YES", "ON"));

    public static @NonNull CommandInput of(@NonNull String input) {
        return new CommandInputImpl(input);
    }

    public static @NonNull CommandInput of(@NonNull Iterable<String> input) {
        return new CommandInputImpl(String.join((CharSequence)" ", input));
    }

    public static @NonNull CommandInput empty() {
        return new CommandInputImpl("");
    }

    @Pure
    public @NonNull String input();

    @SideEffectFree
    public @NonNegative int cursor();

    @Pure
    default public @NonNegative int length() {
        return this.input().length();
    }

    @SideEffectFree
    default public @NonNegative int remainingLength() {
        return this.length() - this.cursor();
    }

    @SideEffectFree
    default public @NonNegative int remainingTokens() {
        int count = new StringTokenizer(this.remainingInput(), " ").countTokens();
        if (this.remainingInput().endsWith(" ")) {
            return count + 1;
        }
        return count;
    }

    @SideEffectFree
    default public @NonNull String remainingInput() {
        return this.input().substring(this.cursor());
    }

    @SideEffectFree
    default public @NonNull String readInput() {
        return this.input().substring(0, this.cursor());
    }

    public @NonNull CommandInput appendString(@NonNull String var1);

    @SideEffectFree
    default public boolean hasRemainingInput() {
        return this.cursor() < this.length();
    }

    @SideEffectFree
    default public boolean isEmpty() {
        return this.isEmpty(false);
    }

    @SideEffectFree
    default public boolean isEmpty(boolean ignoreWhitespace) {
        return !this.hasRemainingInput(ignoreWhitespace);
    }

    @SideEffectFree
    default public boolean hasRemainingInput(boolean ignoreWhitespace) {
        if (!this.hasRemainingInput()) {
            return false;
        }
        if (ignoreWhitespace) {
            return this.hasNonWhitespace();
        }
        return true;
    }

    public void moveCursor(int var1);

    public @This @NonNull CommandInput cursor(@NonNegative int var1);

    @SideEffectFree
    default public @NonNull String peekString(@NonNegative int chars) {
        String remainingInput = this.remainingInput();
        if (chars > remainingInput.length()) {
            throw new CursorOutOfBoundsException(this.cursor() + chars, this.length());
        }
        return remainingInput.substring(0, chars);
    }

    default public @NonNull String read(@NonNegative int chars) {
        String readString = this.peekString(chars);
        this.moveCursor(chars);
        return readString;
    }

    @SideEffectFree
    default public char peek() {
        if (this.cursor() >= this.input().length()) {
            throw new CursorOutOfBoundsException(this.cursor(), this.length());
        }
        return this.input().charAt(this.cursor());
    }

    default public char read() {
        char readChar = this.peek();
        this.moveCursor(1);
        return readChar;
    }

    default public @NonNull String peekString() {
        if (!this.hasRemainingInput()) {
            return "";
        }
        String remainingInput = this.remainingInput();
        int indexOfWhitespace = remainingInput.indexOf(32);
        if (indexOfWhitespace == -1) {
            return remainingInput;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < remainingInput.length(); ++i) {
            char currentChar = remainingInput.charAt(i);
            if (Character.isWhitespace(currentChar)) {
                if (builder.length() != 0) break;
                continue;
            }
            builder.append(currentChar);
        }
        return builder.toString();
    }

    default public @NonNull String readStringSkipWhitespace(boolean preserveSingleSpace) {
        String readString = this.readString();
        this.skipWhitespace(preserveSingleSpace);
        return readString;
    }

    default public @NonNull String readStringSkipWhitespace() {
        return this.readStringSkipWhitespace(true);
    }

    default public @NonNull String readString() {
        return this.skipWhitespace().readUntil(' ');
    }

    default public @NonNull String readUntil(char separator) {
        if (!this.hasRemainingInput()) {
            return "";
        }
        String remainingInput = this.remainingInput();
        int indexOfWhitespace = remainingInput.indexOf(separator);
        if (indexOfWhitespace == -1) {
            this.moveCursor(this.remainingLength());
            return remainingInput;
        }
        return this.read(indexOfWhitespace);
    }

    default public @NonNull String readUntilAndSkip(char separator) {
        String readString = this.readUntil(separator);
        if (readString.isEmpty() || !this.hasRemainingInput()) {
            return readString;
        }
        char readChar = this.read();
        if (readChar != separator) {
            this.moveCursor(-1);
        }
        return readString;
    }

    default public @This @NonNull CommandInput skipWhitespace(int maxSpaces, boolean preserveSingleSpace) {
        if (preserveSingleSpace && this.remainingLength() == 1 && this.peek() == ' ') {
            return this;
        }
        for (int i = 0; i < maxSpaces && this.hasRemainingInput() && Character.isWhitespace(this.peek()); ++i) {
            this.read();
        }
        return this;
    }

    default public @This @NonNull CommandInput skipWhitespace(int maxSpaces) {
        return this.skipWhitespace(maxSpaces, false);
    }

    default public @This @NonNull CommandInput skipWhitespace(boolean preserveSingleSpace) {
        return this.skipWhitespace(Integer.MAX_VALUE, preserveSingleSpace);
    }

    default public @This @NonNull CommandInput skipWhitespace() {
        return this.skipWhitespace(false);
    }

    default public boolean hasNonWhitespace() {
        return this.remainingInput().chars().anyMatch(c -> !Character.isWhitespace(c));
    }

    @SideEffectFree
    default public boolean isValidByte(byte min, byte max) {
        try {
            byte parsedByte = Byte.parseByte(this.peekString());
            return parsedByte >= min && parsedByte <= max;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SideEffectFree
    default public boolean isValidByte(@NonNull ByteRange range) {
        return this.isValidByte(range.minByte(), range.maxByte());
    }

    default public byte readByte() {
        return Byte.parseByte(this.readString());
    }

    @SideEffectFree
    default public boolean isValidShort(short min, short max) {
        try {
            short parsedShort = Short.parseShort(this.peekString());
            return parsedShort >= min && parsedShort <= max;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SideEffectFree
    default public boolean isValidShort(@NonNull ShortRange range) {
        return this.isValidShort(range.minShort(), range.maxShort());
    }

    default public short readShort() {
        return Short.parseShort(this.readString());
    }

    @SideEffectFree
    default public boolean isValidInteger(int min, int max) {
        try {
            int parsedInteger = Integer.parseInt(this.peekString());
            return parsedInteger >= min && parsedInteger <= max;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SideEffectFree
    default public boolean isValidInteger(@NonNull IntRange range) {
        return this.isValidInteger(range.minInt(), range.maxInt());
    }

    default public int readInteger() {
        return Integer.parseInt(this.readString());
    }

    default public int readInteger(int radix) {
        return Integer.parseInt(this.readString(), radix);
    }

    @SideEffectFree
    default public boolean isValidLong(long min, long max) {
        try {
            long parsedLong = Long.parseLong(this.peekString());
            return parsedLong >= min && parsedLong <= max;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SideEffectFree
    default public boolean isValidLong(@NonNull LongRange range) {
        return this.isValidLong(range.minLong(), range.maxLong());
    }

    default public long readLong() {
        return Long.parseLong(this.readString());
    }

    @SideEffectFree
    default public boolean isValidDouble(double min, double max) {
        try {
            double parsedDouble = Double.parseDouble(this.peekString());
            return parsedDouble >= min && parsedDouble <= max;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SideEffectFree
    default public boolean isValidDouble(@NonNull DoubleRange range) {
        return this.isValidDouble(range.minDouble(), range.maxDouble());
    }

    default public double readDouble() {
        return Double.parseDouble(this.readString());
    }

    @SideEffectFree
    default public boolean isValidFloat(float min, float max) {
        try {
            float parsedFloat = Float.parseFloat(this.peekString());
            return parsedFloat >= min && parsedFloat <= max;
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    @SideEffectFree
    default public boolean isValidFloat(@NonNull FloatRange range) {
        return this.isValidFloat(range.minFloat(), range.maxFloat());
    }

    default public float readFloat() {
        return Float.parseFloat(this.readString());
    }

    @SideEffectFree
    default public boolean isValidBoolean(boolean liberal) {
        if (liberal) {
            return BOOLEAN_LIBERAL.contains(this.peekString().toUpperCase(Locale.ROOT));
        }
        return BOOLEAN_STRICT.contains(this.peekString().toUpperCase(Locale.ROOT));
    }

    default public boolean readBoolean() {
        return BOOLEAN_LIBERAL_TRUE.contains(this.readString().toUpperCase(Locale.ROOT));
    }

    default public @NonNull String lastRemainingToken() {
        String remainingInput = this.remainingInput();
        if (remainingInput.isEmpty() || remainingInput.endsWith(" ")) {
            return "";
        }
        int lastSpace = remainingInput.lastIndexOf(32);
        if (lastSpace == -1) {
            return remainingInput;
        }
        return remainingInput.substring(lastSpace + 1);
    }

    default public char lastRemainingCharacter() {
        String lastToken = this.lastRemainingToken();
        if (lastToken.isEmpty()) {
            throw new CursorOutOfBoundsException(this.cursor(), this.length());
        }
        return lastToken.charAt(lastToken.length() - 1);
    }

    public @NonNull CommandInput copy();

    default public @NonNull String difference(@NonNull CommandInput that, boolean includeTrailingWhitespace) {
        if (!this.input().equals(that.input())) {
            return this.input();
        }
        String difference = this.input().substring(this.cursor(), that.cursor());
        if (!includeTrailingWhitespace && difference.endsWith(" ")) {
            return difference.substring(0, difference.length() - 1);
        }
        return difference;
    }

    default public @NonNull String difference(@NonNull CommandInput that) {
        return this.difference(that, false);
    }

    @API(status=API.Status.STABLE)
    public static class CursorOutOfBoundsException
    extends NoSuchElementException {
        CursorOutOfBoundsException(@NonNegative int cursor, @NonNegative int length) {
            super(String.format("Cursor exceeds input length (%d > %d)", cursor, length - 1));
        }
    }
}

