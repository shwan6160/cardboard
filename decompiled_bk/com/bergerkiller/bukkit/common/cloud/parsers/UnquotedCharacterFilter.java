/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.cloud.parsers;

import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParserProxy;

public interface UnquotedCharacterFilter {
    public static final UnquotedCharacterFilter PERMISSIVE = c -> c != ' ' && c != '\"';
    public static final UnquotedCharacterFilter STRICT = c -> c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+';

    public boolean isAllowed(char var1);

    default public boolean isStringAllowed(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            if (this.isAllowed(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    default public String escapeStringIfNeeded(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (this.isAllowed(c)) continue;
            StringBuilder newStr = new StringBuilder(len + 10);
            newStr.append('\"');
            newStr.append(str, 0, i);
            QuotedArgumentParserProxy.appendEscapedCharToBuilder(newStr, c);
            QuotedArgumentParserProxy.appendEscapedStringToBuilder(newStr, str, i + 1, len);
            newStr.append('\"');
            return newStr.toString();
        }
        return str;
    }

    public static String escapeString(String str) {
        StringBuilder newStr = new StringBuilder(str.length() + 10);
        newStr.append('\"');
        QuotedArgumentParserProxy.appendEscapedStringToBuilder(newStr, str, 0, str.length());
        newStr.append('\"');
        return newStr.toString();
    }

    public static String unescapeString(String str) {
        int len = str.length();
        if (len == 0 || str.charAt(0) != '\"') {
            return str;
        }
        StringBuilder newStr = new StringBuilder(len - 1);
        boolean escaped = false;
        for (int i = 1; i < len; ++i) {
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
            if (c == '\"') break;
            newStr.append(c);
        }
        return newStr.toString();
    }
}

