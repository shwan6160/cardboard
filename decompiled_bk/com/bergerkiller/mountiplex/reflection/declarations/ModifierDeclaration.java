/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class ModifierDeclaration
extends Declaration {
    private static final HashMap<StringBuffer, Integer> _tokens;
    private static final int _token_mask = 207;
    private final int _modifiers;
    private final String _modifiersStr;
    private final boolean _final;
    private final boolean _unknown;
    private final boolean _optional;
    private final boolean _readonly;
    private final boolean _rawtype;

    public ModifierDeclaration(ClassResolver resolver, int modifiers) {
        super(resolver);
        this._modifiers = modifiers;
        this._modifiersStr = Modifier.toString(modifiers);
        this._final = Modifier.isFinal(this._modifiers);
        this._unknown = false;
        this._optional = false;
        this._rawtype = false;
        this._readonly = false;
    }

    @Deprecated
    public ModifierDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    public ModifierDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver, declaration);
        int startIdx;
        if (declaration == null) {
            this._modifiers = 0;
            this._modifiersStr = "";
            this._final = false;
            this._unknown = false;
            this._optional = false;
            this._rawtype = false;
            this._readonly = false;
            this.setInvalid();
            return;
        }
        boolean isUnknown = false;
        boolean isOptional = false;
        boolean isReadonly = false;
        boolean isRawtype = false;
        int modifiers = 0;
        String modifiersStr = "";
        StringBuffer postfix = declaration;
        while (true) {
            for (startIdx = 0; startIdx < postfix.length() && postfix.charAt(startIdx) == ' '; ++startIdx) {
            }
            int spaceIdx = postfix.indexOf(' ', startIdx);
            if (spaceIdx == -1) break;
            StringBuffer token = postfix.substring(startIdx, spaceIdx);
            Integer m = _tokens.get(token);
            boolean validToken = false;
            if (m != null) {
                modifiers |= m.intValue();
                validToken = true;
            } else if (token.equals("unknown")) {
                isUnknown = true;
                validToken = true;
            } else if (token.equals("optional")) {
                isOptional = true;
                validToken = true;
            } else if (token.equals("readonly")) {
                isReadonly = true;
                validToken = true;
            } else if (token.equals("rawtype")) {
                isRawtype = true;
                validToken = true;
            }
            if (!validToken) break;
            if (m != null) {
                if (modifiersStr.length() > 0) {
                    modifiersStr = modifiersStr + " ";
                }
                modifiersStr = modifiersStr + token;
            }
            postfix = postfix.substring(spaceIdx + 1);
        }
        postfix = postfix.substring(startIdx);
        this._final = Modifier.isFinal(modifiers);
        this._modifiers = modifiers;
        this._modifiersStr = modifiersStr;
        this._unknown = isUnknown;
        this._optional = isOptional;
        this._readonly = isReadonly;
        this._rawtype = isRawtype;
        this.setPostfix(postfix);
    }

    public final boolean isUnknown() {
        return this._unknown;
    }

    public final boolean isReadonly() {
        return this._readonly;
    }

    public final boolean isOptional() {
        return this._optional;
    }

    public final boolean isRawtype() {
        return this._rawtype;
    }

    public final boolean isFinal() {
        return this._final;
    }

    public final boolean isStatic() {
        return Modifier.isStatic(this._modifiers);
    }

    public final boolean isPublic() {
        return Modifier.isPublic(this._modifiers);
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof ModifierDeclaration)) {
            return 0.0;
        }
        ModifierDeclaration m = (ModifierDeclaration)other;
        int allModifiers = (m._modifiers | this._modifiers) & 0xCF;
        int numberTotal = 0;
        int numberMatched = 0;
        for (int b = 0; b < 31; ++b) {
            int mask = 1 << b;
            if ((allModifiers & mask) == 0) continue;
            ++numberTotal;
            if ((m._modifiers & mask) != (this._modifiers & mask)) continue;
            ++numberMatched;
        }
        if (numberTotal == 0) {
            return 1.0;
        }
        return (double)numberMatched / (double)numberTotal;
    }

    @Override
    public final boolean match(Declaration modifier) {
        if (!(modifier instanceof ModifierDeclaration)) {
            return false;
        }
        ModifierDeclaration other = (ModifierDeclaration)modifier;
        return (this._modifiers & 0xCF) == (other._modifiers & 0xCF);
    }

    public int getProtectionLevel() {
        if (Modifier.isPublic(this._modifiers)) {
            return 0;
        }
        if (Modifier.isProtected(this._modifiers)) {
            return 1;
        }
        if (!Modifier.isPrivate(this._modifiers)) {
            return 2;
        }
        return 3;
    }

    @Override
    public final String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        return this._modifiersStr;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Modifier {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        str.append(indent).append("  modifiersStr=").append(this._modifiersStr).append('\n');
        str.append(indent).append("  modifiers=").append(this._modifiers).append('\n');
        str.append(indent).append("}\n");
    }

    static {
        int[] modifiers;
        _tokens = new HashMap();
        for (int modifier : modifiers = new int[]{1024, 16, 256, 2, 4, 1, 8, 2048, 32, 128, 64}) {
            _tokens.put(StringBuffer.of(Modifier.toString(modifier)), modifier);
        }
    }
}

