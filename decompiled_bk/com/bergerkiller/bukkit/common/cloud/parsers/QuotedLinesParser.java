/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.cloud.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QuotedLinesParser {
    private final List<String> lines = new ArrayList<String>();

    public static QuotedLinesParser create() {
        return new QuotedLinesParser();
    }

    public boolean hasLines() {
        return !this.lines.isEmpty();
    }

    public List<String> getLines() {
        return this.lines;
    }

    public String[] getLinesArray() {
        return this.lines.toArray(new String[0]);
    }

    public QuotedLinesParser addLine(String line) {
        this.lines.add(line);
        return this;
    }

    public QuotedLinesParser addLines(String[] lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    public QuotedLinesParser addLines(Collection<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public QuotedLinesParser parse(String input) {
        if (input != null) {
            this.parse(input, 0, input.length());
        }
        return this;
    }

    public QuotedLinesParser parse(String input, int startIndex, int endIndex) {
        if (input != null) {
            int currStartIndex = startIndex;
            while ((currStartIndex = this.parseNext(input, currStartIndex, endIndex)) != -1) {
            }
        }
        return this;
    }

    private int parseNext(String input, int startIndex, int endIndex) {
        int space;
        while (startIndex < endIndex && input.charAt(startIndex) == ' ') {
            ++startIndex;
        }
        if (startIndex < endIndex && input.charAt(startIndex) == '\"') {
            int nextQuote;
            StringBuilder unescaped = null;
            int partStartIndex = startIndex + 1;
            while ((nextQuote = input.indexOf(34, partStartIndex)) != -1 && nextQuote < endIndex) {
                if (input.charAt(nextQuote - 1) == '\\') {
                    if (unescaped == null) {
                        unescaped = new StringBuilder(input.length() - startIndex);
                    }
                    unescaped.append(input, partStartIndex, nextQuote);
                    unescaped.append('\"');
                    partStartIndex = nextQuote + 1;
                    continue;
                }
                if (unescaped != null) {
                    unescaped.append(input, partStartIndex, nextQuote);
                    this.addLine(unescaped.toString());
                } else {
                    this.addLine(input.substring(startIndex + 1, nextQuote));
                }
                return nextQuote + 1;
            }
        }
        if ((space = input.indexOf(32, startIndex)) != -1) {
            this.addLine(input.substring(startIndex, space));
            return space + 1;
        }
        if (startIndex < endIndex) {
            this.addLine(input.substring(startIndex, endIndex));
        }
        return -1;
    }
}

