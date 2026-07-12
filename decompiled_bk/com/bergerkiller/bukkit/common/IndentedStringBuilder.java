/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IndentedStringBuilder {
    public final StringBuilder builder;
    private final int indent;
    private final int indentStep;

    public static IndentedStringBuilder create() {
        return IndentedStringBuilder.create(2);
    }

    public static IndentedStringBuilder create(int indentStep) {
        return new IndentedStringBuilder(indentStep);
    }

    public static String toString(AppendableToString appendable) {
        IndentedStringBuilder str = IndentedStringBuilder.create();
        str.append(appendable);
        return str.toString();
    }

    private IndentedStringBuilder(int indentStep) {
        this(new StringBuilder(), indentStep, 0);
    }

    private IndentedStringBuilder(StringBuilder builder, int indentStep, int indent) {
        this.builder = builder;
        this.indent = indent;
        this.indentStep = indentStep;
    }

    public IndentedStringBuilder indent() {
        return this.indent(1);
    }

    public IndentedStringBuilder indent(int indentIncrease) {
        return new IndentedStringBuilder(this.builder, this.indentStep, this.indent + this.indentStep * indentIncrease);
    }

    public IndentedStringBuilder appendWithIndent(Consumer<IndentedStringBuilder> callback) {
        callback.accept(this.indent());
        return this;
    }

    public IndentedStringBuilder appendWithIndent(Consumer<IndentedStringBuilder> callback, int indentIncrease) {
        callback.accept(this.indent(indentIncrease));
        return this;
    }

    public IndentedStringBuilder appendLines(Iterable<?> lineItems) {
        for (Object item : lineItems) {
            if (item instanceof AppendableToString) {
                ((AppendableToString)item).toString(this.append("\n"));
                continue;
            }
            this.append("\n").append(item);
        }
        return this;
    }

    public <T> IndentedStringBuilder appendLines(Iterable<T> lineItems, BiConsumer<IndentedStringBuilder, T> toString) {
        for (T item : lineItems) {
            toString.accept(this.append("\n"), (IndentedStringBuilder)item);
        }
        return this;
    }

    public IndentedStringBuilder append(AppendableToString appendable) {
        if (appendable != null) {
            appendable.toString(this);
        } else {
            this.builder.append("<null>");
        }
        return this;
    }

    public IndentedStringBuilder append(float value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(double value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(byte value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(short value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(int value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(long value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(boolean value) {
        this.builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(Object obj) {
        if (obj instanceof AppendableToString) {
            ((AppendableToString)obj).toString(this);
            return this;
        }
        return this.append(obj == null ? "<null>" : obj.toString());
    }

    public IndentedStringBuilder append(String str) {
        if (str == null) {
            this.builder.append("<null>");
        } else if (this.indent > 0) {
            int startChar = 0;
            int newlineChar = -1;
            while ((newlineChar = str.indexOf(10, startChar)) != -1) {
                this.builder.append(str, startChar, newlineChar + 1);
                for (int i = 0; i < this.indent; ++i) {
                    this.builder.append(' ');
                }
                startChar = newlineChar + 1;
            }
            this.builder.append(str, startChar, str.length());
        } else {
            this.builder.append(str);
        }
        return this;
    }

    public IndentedStringBuilder append(char ch) {
        if (ch == '\n' && this.indent > 0) {
            this.builder.append('\n');
            for (int i = 0; i < this.indent; ++i) {
                this.builder.append(' ');
            }
        } else {
            this.builder.append(ch);
        }
        return this;
    }

    public String toString() {
        return this.builder.toString();
    }

    public static interface AppendableToString {
        public void toString(IndentedStringBuilder var1);
    }
}

