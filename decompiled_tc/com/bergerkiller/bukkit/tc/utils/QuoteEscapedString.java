/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.utils;

public class QuoteEscapedString {
    private final String unescaped;
    private String escaped;
    private final boolean isQuoteEscaped;

    public QuoteEscapedString(String unescaped) {
        this(unescaped, null, false);
    }

    public QuoteEscapedString(String unescaped, String escaped, boolean isQuoteEscaped) {
        this.unescaped = unescaped;
        this.escaped = escaped;
        this.isQuoteEscaped = isQuoteEscaped;
    }

    public String getUnescaped() {
        return this.unescaped;
    }

    public String getEscaped() {
        if (this.escaped == null) {
            this.escaped = QuoteEscapedString.escapeString(this.unescaped);
        }
        return this.escaped;
    }

    public boolean isQuoteEscaped() {
        return this.isQuoteEscaped;
    }

    public String toString() {
        return "QuoteEscapedString{" + this.unescaped + " QUOTED=" + this.isQuoteEscaped + "}";
    }

    public static QuoteEscapedString quoteEscape(String str) {
        return new QuoteEscapedString(str, QuoteEscapedString.escapeString(str), false);
    }

    public static QuoteEscapedString tryParseQuoted(String str) {
        int i;
        int len = str.length();
        if (len < 2) {
            return new QuoteEscapedString(str);
        }
        char quoteChar = str.charAt(0);
        if (quoteChar != '\"' && quoteChar != '\'') {
            return new QuoteEscapedString(str);
        }
        if (str.charAt(len - 1) != quoteChar) {
            return new QuoteEscapedString(str);
        }
        if (str.indexOf(92, 1) == -1 && str.indexOf(quoteChar, 1) == len - 1) {
            return new QuoteEscapedString(str.substring(1, len - 1), str, true);
        }
        StringBuilder newStr = new StringBuilder(len - 1);
        boolean escaped = false;
        for (i = 1; i < len; ++i) {
            char c = str.charAt(i);
            if (escaped) {
                escaped = false;
                newStr.append(c);
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == quoteChar) {
                ++i;
                break;
            }
            newStr.append(c);
        }
        if (escaped || i < len) {
            return new QuoteEscapedString(str);
        }
        return new QuoteEscapedString(newStr.toString(), str, true);
    }

    public static int unquotedIndexOf(String text, String token, int fromIndex) {
        int len = text.length();
        block0: while (fromIndex < len) {
            int matchIndex = text.indexOf(token, fromIndex);
            if (matchIndex == -1 || matchIndex == fromIndex) {
                return matchIndex;
            }
            boolean isQuotedString = false;
            char quoteChar = '\"';
            boolean escaped = false;
            while (fromIndex < len) {
                if (fromIndex == matchIndex && !isQuotedString) {
                    return matchIndex;
                }
                char c = text.charAt(fromIndex);
                if (escaped) {
                    escaped = false;
                } else if (c == '\"' || c == '\'') {
                    if (!isQuotedString) {
                        isQuotedString = true;
                        quoteChar = c;
                    } else if (c == quoteChar) {
                        isQuotedString = false;
                        if (fromIndex > matchIndex) {
                            continue block0;
                        }
                    }
                } else if (isQuotedString && c == '\\') {
                    escaped = true;
                }
                ++fromIndex;
            }
        }
        return -1;
    }

    private static String escapeString(String text) {
        int len = text.length();
        boolean allowed = true;
        for (int i = 0; i < len; ++i) {
            if (QuoteEscapedString.isAllowedInUnquotedString(text.charAt(i))) continue;
            allowed = false;
            break;
        }
        if (allowed) {
            return text;
        }
        StringBuilder escaped = new StringBuilder(len + 8);
        escaped.append('\"');
        for (int i = 0; i < len; ++i) {
            char c = text.charAt(i);
            if (c == '\\' || c == '\"') {
                escaped.append('\\');
            }
            escaped.append(c);
        }
        escaped.append('\"');
        return escaped.toString();
    }

    private static boolean isAllowedInUnquotedString(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+';
    }
}

