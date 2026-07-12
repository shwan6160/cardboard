/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.logic;

import java.util.Comparator;

public final class TextValueSequence
implements Comparable<TextValueSequence> {
    public static final Comparator<String> STRING_COMPARATOR = (a, b) -> {
        if (a.equals(b)) {
            return 0;
        }
        if (TextValueSequence.evaluateText(a, ">", b)) {
            return 1;
        }
        return -1;
    };
    private final String fullText;
    private boolean number;
    private int value;
    private String text;
    private TextValueSequence next;

    public static TextValueSequence parse(String text) {
        return new TextValueSequence(text);
    }

    public static int compareText(String value1, String value2) {
        return TextValueSequence.parse(value1).compareTo(TextValueSequence.parse(value2));
    }

    public static boolean evaluateText(String value1, String operand, String value2) {
        return TextValueSequence.evaluate(TextValueSequence.parse(value1), operand, TextValueSequence.parse(value2));
    }

    public static boolean evaluate(TextValueSequence value1, String operand, TextValueSequence value2) {
        int len = operand.length();
        if (len == 0 || len > 2) {
            return false;
        }
        char first = operand.charAt(0);
        int second = len == 2 ? (int)operand.charAt(1) : 32;
        int comp = value1.compareTo(value2);
        if (first == '>') {
            if (second == 61) {
                return comp >= 0;
            }
            return comp > 0;
        }
        if (first == '<') {
            if (second == 61) {
                return comp <= 0;
            }
            return comp < 0;
        }
        if (first == '=' && second == 61) {
            return comp == 0;
        }
        if (first == '!' && second == 61) {
            return comp != 0;
        }
        return false;
    }

    private TextValueSequence(String text) {
        int cIdx;
        this.fullText = text;
        this.number = false;
        this.value = 0;
        for (cIdx = 0; cIdx < text.length() && Character.isDigit(text.charAt(cIdx)); ++cIdx) {
            this.number = true;
        }
        if (this.number) {
            this.text = text.substring(0, cIdx);
            try {
                this.value = Integer.parseInt(this.text);
            }
            catch (NumberFormatException ex) {
                this.number = false;
            }
        }
        if (!this.number) {
            while (cIdx < text.length() && !Character.isDigit(text.charAt(cIdx))) {
                ++cIdx;
            }
            this.text = text.substring(0, cIdx);
        }
        this.next = cIdx < text.length() ? new TextValueSequence(text.substring(cIdx)) : null;
    }

    public int hashCode() {
        return this.fullText.hashCode();
    }

    public String toString() {
        return this.fullText;
    }

    @Override
    public int compareTo(TextValueSequence o) {
        int result = this.number && o.number ? Integer.compare(this.value, o.value) : this.text.compareTo(o.text);
        if (result == 0) {
            if (this.next != null && o.next != null) {
                return this.next.compareTo(o.next);
            }
            if (this.next != null) {
                return 1;
            }
            if (o.next != null) {
                return -1;
            }
        }
        return result;
    }

    public boolean equals(Object value) {
        if (value instanceof TextValueSequence) {
            return this.fullText.equals(((TextValueSequence)value).fullText);
        }
        return false;
    }
}

