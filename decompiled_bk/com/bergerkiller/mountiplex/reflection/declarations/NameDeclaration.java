/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class NameDeclaration
extends Declaration {
    private final String _name;
    private final String _alias;

    public NameDeclaration(ClassResolver resolver, String name, String alias) {
        super(resolver);
        this._name = name;
        this._alias = alias;
    }

    @Deprecated
    public NameDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    @Deprecated
    public NameDeclaration(ClassResolver resolver, String declaration, int optionalIdx) {
        this(resolver, StringBuffer.of(declaration), optionalIdx);
    }

    public NameDeclaration(ClassResolver resolver, StringBuffer declaration) {
        this(resolver, declaration, -1);
    }

    public NameDeclaration(ClassResolver resolver, StringBuffer declaration, int optionalIdx) {
        super(resolver, declaration);
        int alias_idx;
        if (declaration == null) {
            this._name = "";
            this._alias = null;
            this.setInvalid();
            return;
        }
        int startIdx = -1;
        StringBuffer name = null;
        StringBuffer alias = null;
        for (int cidx = 0; cidx < declaration.length(); ++cidx) {
            boolean validNameChar;
            char c = declaration.charAt(cidx);
            if (startIdx == -1 && c == ' ') continue;
            if (c == '<') {
                validNameChar = declaration.substringEquals(cidx, cidx + 6, "<init>") || declaration.substringEquals(cidx, cidx + 16, "<record_changer>");
            } else if (c == '>') {
                validNameChar = declaration.substringEquals(cidx - 5, cidx + 1, "<init>") || declaration.substringEquals(cidx - 15, cidx + 1, "<record_changer>");
            } else {
                boolean bl = validNameChar = !MountiplexUtil.containsChar(c, ParserStringBuffer.INVALID_NAME_CHARACTERS);
            }
            if (startIdx == -1) {
                if (!validNameChar) break;
                startIdx = cidx;
            }
            if (!validNameChar && name == null) {
                name = declaration.substring(startIdx, cidx);
            }
            if (name == null || c == ' ') continue;
            this.setPostfix(declaration.substring(cidx));
            break;
        }
        if (startIdx == -1) {
            if (optionalIdx != -1) {
                this._name = "arg" + optionalIdx;
                this._alias = null;
            } else {
                this._name = "";
                this._alias = null;
                this.setInvalid();
            }
            return;
        }
        if (name == null) {
            name = declaration.substring(startIdx);
            this.setPostfix(StringBuffer.EMPTY);
        }
        if ((alias_idx = name.indexOf(':')) != -1) {
            alias = name.substring(0, alias_idx);
            name = name.substring(alias_idx + 1);
        }
        this._name = name == null ? null : name.toString();
        this._alias = alias == null ? null : alias.toString();
    }

    public final String value() {
        return this._name;
    }

    public final String alias() {
        return this._alias;
    }

    public final String real() {
        return this._alias != null ? this._alias : this._name;
    }

    public final String firstReal() {
        if (this._alias != null) {
            int index = this._alias.indexOf(58);
            if (index != -1) {
                return this._alias.substring(0, index);
            }
            return this._alias;
        }
        return this._name;
    }

    public final boolean hasAlias() {
        return this._alias != null;
    }

    public final boolean isObfuscated() {
        return this._name.length() <= 2;
    }

    public final boolean isAliasOnly() {
        if (!this.hasAlias()) {
            return false;
        }
        for (int cidx = 0; cidx < this._name.length(); ++cidx) {
            if (this._name.charAt(cidx) == '?') continue;
            return false;
        }
        return true;
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof NameDeclaration)) {
            return 0.0;
        }
        NameDeclaration n = (NameDeclaration)other;
        if (n._name.equals(this._name)) {
            return 1.0;
        }
        if (n.isObfuscated() && this.isObfuscated()) {
            return 0.9;
        }
        if (n.isObfuscated() || this.isObfuscated()) {
            return 0.1;
        }
        return MountiplexUtil.similarity(n._name, this._name);
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof NameDeclaration) {
            NameDeclaration other = (NameDeclaration)declaration;
            if (this.isAliasOnly()) {
                return other.matchAlias(this._alias);
            }
            if (other.isAliasOnly()) {
                return this.matchAlias(other._alias);
            }
            return other._name.equals(this._name);
        }
        return false;
    }

    private boolean matchAlias(String otherAlias) {
        String self_name = this.real();
        int top_alias_end = self_name.indexOf(58);
        if (top_alias_end != -1) {
            self_name = self_name.substring(0, top_alias_end);
        }
        return self_name.equals(otherAlias);
    }

    @Override
    public String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        if (this._alias == null || identity) {
            return this._name;
        }
        return this._alias + ":" + this._name;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Name {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        str.append(indent).append("  name=").append(this._name).append('\n');
        str.append(indent).append("  alias=").append(this._alias).append('\n');
        str.append(indent).append("}\n");
    }

    public NameDeclaration rename(NameDeclaration newName) {
        if (!newName.hasAlias()) {
            return this.rename(newName.value());
        }
        ArrayList<String> aliases = new ArrayList<String>(Arrays.asList(newName.alias().split(":")));
        if (((String)aliases.get(0)).equals(this.value())) {
            aliases.remove(0);
        }
        if (aliases.isEmpty()) {
            return this.rename(newName.value());
        }
        if (newName.value().equals(this.value())) {
            return this;
        }
        aliases.add(0, this.value());
        return new NameDeclaration(this.getResolver(), newName.value(), String.join((CharSequence)":", aliases));
    }

    public NameDeclaration rename(String newName) {
        if (newName.equals(this.value())) {
            return this;
        }
        if (this.hasAlias()) {
            return new NameDeclaration(this.getResolver(), newName, this.alias() + ":" + this.value());
        }
        return new NameDeclaration(this.getResolver(), newName, this.value());
    }
}

