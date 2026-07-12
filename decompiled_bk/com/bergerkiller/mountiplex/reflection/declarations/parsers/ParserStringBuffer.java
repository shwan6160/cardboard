/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;

public final class ParserStringBuffer {
    public static final char[] WHITESPACE_CHARACTERS = new char[]{' ', '\n', '\r'};
    public static final char[] INVALID_NAME_CHARACTERS = new char[]{' ', '\n', '\r', '<', '>', ',', '(', ')', '{', '}', ';', '='};
    private StringBuffer _text = null;

    public StringBuffer get() {
        return this._text;
    }

    public void set(StringBuffer text) {
        this._text = text;
    }

    public boolean isNull() {
        return this._text == null;
    }

    public String toString() {
        return this._text.toString();
    }

    public boolean startsWith(String text) {
        return this._text.startsWith(text);
    }

    public void trimWhitespace(int start) {
        if (this._text == null) {
            return;
        }
        for (int cidx = start; cidx < this._text.length(); ++cidx) {
            char c = this._text.charAt(cidx);
            if (MountiplexUtil.containsChar(c, WHITESPACE_CHARACTERS)) continue;
            this._text = this._text.substring(cidx);
            return;
        }
        this._text = StringBuffer.EMPTY;
    }

    public String trimLine() {
        if (this.isNull()) {
            return "";
        }
        int firstNewLineIdx = -1;
        for (int cidx = 0; cidx < this._text.length(); ++cidx) {
            char c = this._text.charAt(cidx);
            if (c == '\r' || c == '\n') {
                if (firstNewLineIdx != -1) continue;
                firstNewLineIdx = cidx;
                continue;
            }
            if (c == ' ' || firstNewLineIdx == -1) continue;
            String remainder = this._text.substringToString(0, firstNewLineIdx);
            this._text = this._text.substring(cidx);
            return remainder;
        }
        String remainder = this._text.toString();
        this._text = StringBuffer.EMPTY;
        return remainder;
    }

    public void trimGenericTypes() {
        StringBuffer postfix = this.get();
        if (postfix != null && postfix.length() > 0 && postfix.charAt(0) == '<') {
            boolean foundEnd = false;
            for (int cidx = 1; cidx < postfix.length(); ++cidx) {
                char c = postfix.charAt(cidx);
                if (c == '>') {
                    foundEnd = true;
                    continue;
                }
                if (!foundEnd || MountiplexUtil.containsChar(c, INVALID_NAME_CHARACTERS)) continue;
                this.set(postfix.substring(cidx));
                break;
            }
        }
    }

    public Declaration detectMemberDeclaration(ClassResolver resolver) {
        StringBuffer postfix = this.get();
        Declaration dec = Declaration.parseDeclaration(resolver, postfix);
        if (dec != null) {
            this.set(dec.getPostfix());
            this.trimLine();
            return dec;
        }
        return null;
    }
}

