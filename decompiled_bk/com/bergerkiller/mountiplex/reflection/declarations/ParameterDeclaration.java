/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.NameDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.lang.reflect.Type;

public class ParameterDeclaration
extends Declaration {
    public final TypeDeclaration type;
    public final NameDeclaration name;

    public ParameterDeclaration(ClassResolver resolver, TypeDeclaration type, NameDeclaration name) {
        super(resolver);
        this.type = type;
        this.name = name;
    }

    public ParameterDeclaration(ClassResolver resolver, Type type, String name) {
        super(resolver);
        this.type = TypeDeclaration.fromType(resolver, type);
        this.name = new NameDeclaration(resolver, name, null);
    }

    @Deprecated
    public ParameterDeclaration(ClassResolver resolver, String declaration, int paramIdx) {
        this(resolver, StringBuffer.of(declaration), paramIdx);
    }

    public ParameterDeclaration(ClassResolver resolver, StringBuffer declaration, int paramIdx) {
        super(resolver, declaration);
        this.type = this.nextType();
        this.name = this.nextName(paramIdx);
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof ParameterDeclaration)) {
            return 0.0;
        }
        return this.type.similarity(((ParameterDeclaration)other).type);
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof ParameterDeclaration) {
            return this.type.match(((ParameterDeclaration)declaration).type);
        }
        return false;
    }

    @Override
    public String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        return this.type.toString(identity) + " " + this.name.toString(identity);
    }

    @Override
    public boolean isResolved() {
        return this.type.isResolved() && this.name.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Parameter {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.name.debugString(str, indent + "  ");
        this.type.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }
}

