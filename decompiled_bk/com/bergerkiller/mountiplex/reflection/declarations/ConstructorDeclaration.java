/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.ModifierDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.lang.reflect.Constructor;

public class ConstructorDeclaration
extends Declaration {
    public final ModifierDeclaration modifiers;
    public final TypeDeclaration type;
    public final ParameterListDeclaration parameters;
    public Constructor<?> constructor;

    public ConstructorDeclaration(ClassResolver resolver, Constructor<?> constructor) {
        super(resolver);
        this.constructor = constructor;
        this.modifiers = new ModifierDeclaration(resolver, constructor.getModifiers());
        this.type = TypeDeclaration.fromType(resolver, constructor.getDeclaringClass());
        this.parameters = new ParameterListDeclaration(resolver, constructor.getGenericParameterTypes());
    }

    @Deprecated
    public ConstructorDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    public ConstructorDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver, declaration);
        this.constructor = null;
        this.modifiers = this.nextModifier();
        this.type = this.nextType();
        this.parameters = this.nextParameterList();
    }

    public final String getName() {
        String name = "constr";
        for (ParameterDeclaration param : this.parameters.parameters) {
            name = name + "_" + param.name.real();
        }
        return name;
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof ConstructorDeclaration) {
            ConstructorDeclaration constr = (ConstructorDeclaration)declaration;
            return this.type.match(constr.type) && this.parameters.match(constr.parameters);
        }
        return false;
    }

    @Override
    public String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        String m = this.modifiers.toString(identity);
        if (m.length() > 0) {
            return m + " " + this.type.toString(identity) + this.parameters.toString(identity) + ";";
        }
        return this.type.toString() + this.parameters.toString(identity) + ";";
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof ConstructorDeclaration)) {
            return 0.0;
        }
        ConstructorDeclaration c = (ConstructorDeclaration)other;
        return 0.1 * this.modifiers.similarity(c.modifiers) + 0.9 * this.parameters.similarity(c.parameters);
    }

    @Override
    public boolean isResolved() {
        return this.type.isResolved() && this.parameters.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Constructor {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.modifiers.debugString(str, indent + "  ");
        this.type.debugString(str, indent + "  ");
        this.parameters.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }

    @Override
    public ConstructorDeclaration discover() {
        if (!this.isValid() || !this.isResolved()) {
            return null;
        }
        try {
            Constructor<?> constructor = this.getResolver().getDeclaredClass().getDeclaredConstructor(this.parameters.toParamArray());
            if (constructor != null) {
                this.constructor = constructor;
                return this;
            }
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
        return null;
    }
}

