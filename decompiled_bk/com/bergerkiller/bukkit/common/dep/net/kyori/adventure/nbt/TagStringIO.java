/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.BinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CharBuffer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.CompoundBinaryTag;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.StringTagParseException;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.TagStringReader;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.TagStringWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class TagStringIO {
    private static final TagStringIO INSTANCE = new TagStringIO(new Builder());
    private final boolean acceptLegacy;
    private final boolean emitLegacy;
    private final boolean acceptHeterogeneousLists;
    private final boolean emitHeterogeneousLists;
    private final String indent;

    @Deprecated
    @NotNull
    public static TagStringIO get() {
        return TagStringIO.tagStringIO();
    }

    @NotNull
    public static TagStringIO tagStringIO() {
        return INSTANCE;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    private TagStringIO(@NotNull Builder builder) {
        this.acceptLegacy = builder.acceptLegacy;
        this.emitLegacy = builder.emitLegacy;
        this.acceptHeterogeneousLists = builder.acceptHeterogeneousLists;
        this.emitHeterogeneousLists = builder.emitHeterogeneousLists;
        this.indent = builder.indent;
    }

    @NotNull
    public CompoundBinaryTag asCompound(@NotNull String input) throws IOException {
        Objects.requireNonNull(input, "input");
        try {
            CharBuffer buffer = new CharBuffer(input);
            TagStringReader parser = new TagStringReader(buffer);
            parser.legacy(this.acceptLegacy);
            parser.heterogeneousLists(this.acceptHeterogeneousLists);
            CompoundBinaryTag tag = parser.compound();
            if (buffer.skipWhitespace().hasMore()) {
                throw new IOException("Document had trailing content after first CompoundTag");
            }
            return tag;
        }
        catch (StringTagParseException ex) {
            throw new IOException(ex);
        }
    }

    @NotNull
    public BinaryTag asTag(@NotNull String input) throws IOException {
        Objects.requireNonNull(input, "input");
        try {
            CharBuffer buffer = new CharBuffer(input);
            TagStringReader parser = new TagStringReader(buffer);
            parser.legacy(this.acceptLegacy);
            parser.heterogeneousLists(this.acceptHeterogeneousLists);
            BinaryTag tag = parser.tag();
            if (buffer.skipWhitespace().hasMore()) {
                throw new IOException("Document had trailing content after first Tag");
            }
            return tag;
        }
        catch (StringTagParseException ex) {
            throw new IOException(ex);
        }
    }

    @NotNull
    public CompoundBinaryTag asCompound(@NotNull String input, @NotNull Appendable remainder) throws IOException {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(remainder, "remainder");
        try {
            CharBuffer buffer = new CharBuffer(input);
            TagStringReader parser = new TagStringReader(buffer);
            parser.legacy(this.acceptLegacy);
            parser.heterogeneousLists(this.acceptHeterogeneousLists);
            CompoundBinaryTag tag = parser.compound();
            remainder.append(buffer.takeRest());
            return tag;
        }
        catch (StringTagParseException ex) {
            throw new IOException(ex);
        }
    }

    @NotNull
    public BinaryTag asTag(@NotNull String input, @NotNull Appendable remainder) throws IOException {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(remainder, "remainder");
        try {
            CharBuffer buffer = new CharBuffer(input);
            TagStringReader parser = new TagStringReader(buffer);
            parser.legacy(this.acceptLegacy);
            parser.heterogeneousLists(this.acceptHeterogeneousLists);
            BinaryTag tag = parser.tag();
            remainder.append(buffer.takeRest());
            return tag;
        }
        catch (StringTagParseException ex) {
            throw new IOException(ex);
        }
    }

    @NotNull
    public String asString(@NotNull CompoundBinaryTag input) throws IOException {
        return this.asString((BinaryTag)input);
    }

    @NotNull
    public String asString(@NotNull BinaryTag input) throws IOException {
        Objects.requireNonNull(input, "input");
        StringBuilder sb = new StringBuilder();
        try (TagStringWriter emit = new TagStringWriter(sb, this.indent);){
            emit.legacy(this.emitLegacy);
            emit.heterogeneousLists(this.emitHeterogeneousLists);
            emit.writeTag(input);
        }
        return sb.toString();
    }

    public void toWriter(@NotNull CompoundBinaryTag input, @NotNull Writer dest) throws IOException {
        this.toWriter((BinaryTag)input, dest);
    }

    public void toWriter(@NotNull BinaryTag input, @NotNull Writer dest) throws IOException {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(dest, "dest");
        try (TagStringWriter emit = new TagStringWriter(dest, this.indent);){
            emit.legacy(this.emitLegacy);
            emit.heterogeneousLists(this.emitHeterogeneousLists);
            emit.writeTag(input);
        }
    }

    public static class Builder {
        private boolean acceptLegacy = true;
        private boolean emitLegacy = false;
        private boolean acceptHeterogeneousLists = false;
        private boolean emitHeterogeneousLists = false;
        private String indent = "";

        Builder() {
        }

        @NotNull
        public Builder indent(int spaces) {
            if (spaces == 0) {
                this.indent = "";
            } else if (!this.indent.isEmpty() && this.indent.charAt(0) != ' ' || spaces != this.indent.length()) {
                char[] indent = new char[spaces];
                Arrays.fill(indent, ' ');
                this.indent = String.copyValueOf(indent);
            }
            return this;
        }

        @NotNull
        public Builder indentTab(int tabs) {
            if (tabs == 0) {
                this.indent = "";
            } else if (!this.indent.isEmpty() && this.indent.charAt(0) != '\t' || tabs != this.indent.length()) {
                char[] indent = new char[tabs];
                Arrays.fill(indent, '\t');
                this.indent = String.copyValueOf(indent);
            }
            return this;
        }

        @NotNull
        public Builder acceptLegacy(boolean legacy) {
            this.acceptLegacy = legacy;
            return this;
        }

        @NotNull
        public Builder emitLegacy(boolean legacy) {
            this.emitLegacy = legacy;
            return this;
        }

        @NotNull
        public Builder acceptHeterogeneousLists(boolean heterogeneous) {
            this.acceptHeterogeneousLists = heterogeneous;
            return this;
        }

        @NotNull
        public Builder emitHeterogeneousLists(boolean heterogeneous) {
            this.emitHeterogeneousLists = heterogeneous;
            return this;
        }

        @NotNull
        public TagStringIO build() {
            return new TagStringIO(this);
        }
    }
}

