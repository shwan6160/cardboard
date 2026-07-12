/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.function.Function;

public class ParameterListDeclaration
extends Declaration {
    public final ParameterDeclaration[] parameters;

    public ParameterListDeclaration(ClassResolver resolver, Type[] parameters) {
        super(resolver);
        this.parameters = new ParameterDeclaration[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            this.parameters[i] = new ParameterDeclaration(resolver, parameters[i], "arg" + i);
        }
    }

    public ParameterListDeclaration(ClassResolver resolver, Declaration previous) {
        this(resolver, previous.getPostfix());
    }

    @Deprecated
    public ParameterListDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    public ParameterListDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver);
        if (declaration == null) {
            this.parameters = new ParameterDeclaration[0];
            this.setInvalid();
            return;
        }
        boolean foundStart = false;
        boolean foundEnd = false;
        for (int cidx = 0; cidx < declaration.length(); ++cidx) {
            char c = declaration.charAt(cidx);
            if (!foundStart && c == '(') {
                this.setPostfix(declaration.substring(cidx));
                foundStart = true;
                continue;
            }
            if (c == ')') {
                if (foundEnd) {
                    foundStart = false;
                    break;
                }
                foundEnd = true;
                continue;
            }
            if (c == ' ') continue;
            if (!foundEnd) break;
            this.setPostfix(declaration.substring(cidx));
            break;
        }
        if (!foundStart) {
            this.parameters = new ParameterDeclaration[0];
            this.setInvalid();
            return;
        }
        if (foundEnd) {
            this.parameters = new ParameterDeclaration[0];
            return;
        }
        LinkedList<ParameterDeclaration> params = new LinkedList<ParameterDeclaration>();
        StringBuffer postfix = this.getPostfix();
        do {
            this.setPostfix(postfix.substring(1));
            params.add(this.nextParameter(params.size()));
        } while ((postfix = this.getPostfix()) != null && postfix.length() > 0 && postfix.charAt(0) == ',');
        if (postfix != null && postfix.charAt(0) == ')') {
            this.setPostfix(postfix.substring(1));
        }
        this.parameters = params.toArray(new ParameterDeclaration[params.size()]);
    }

    private ParameterListDeclaration(ParameterListDeclaration source, Function<ParameterDeclaration, String> nameMapper) {
        super(source.getResolver());
        this.parameters = new ParameterDeclaration[source.parameters.length];
        for (int i = 0; i < this.parameters.length; ++i) {
            ParameterDeclaration sourceParam = source.parameters[i];
            this.parameters[i] = new ParameterDeclaration(this.getResolver(), sourceParam.type, sourceParam.name.rename(nameMapper.apply(sourceParam)));
        }
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof ParameterListDeclaration)) {
            return 0.0;
        }
        ParameterListDeclaration pl = (ParameterListDeclaration)other;
        if (pl.parameters.length == 0 && this.parameters.length == 0) {
            return 1.0;
        }
        if (pl.parameters.length == this.parameters.length) {
            double similarity = 0.0;
            for (int i = 0; i < this.parameters.length; ++i) {
                similarity += this.parameters[i].similarity(pl.parameters[i]);
            }
            return similarity /= (double)this.parameters.length;
        }
        int minCnt = Math.min(pl.parameters.length, this.parameters.length);
        double similarity = 0.0;
        for (int i = 0; i < minCnt; ++i) {
            similarity += this.parameters[i].similarity(pl.parameters[i]);
        }
        return similarity /= (double)Math.max(pl.parameters.length, this.parameters.length);
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof ParameterListDeclaration) {
            ParameterListDeclaration param = (ParameterListDeclaration)declaration;
            if (this.parameters.length == param.parameters.length) {
                for (int i = 0; i < this.parameters.length; ++i) {
                    if (this.parameters[i].match(param.parameters[i])) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        String str = "(";
        for (int i = 0; i < this.parameters.length; ++i) {
            if (i > 0) {
                str = str + ", ";
            }
            str = str + this.parameters[i].toString(identity);
        }
        return str + ")";
    }

    @Override
    public boolean isResolved() {
        for (int i = 0; i < this.parameters.length; ++i) {
            if (this.parameters[i].isResolved()) continue;
            return false;
        }
        return true;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("ParameterList {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        for (ParameterDeclaration p : this.parameters) {
            p.debugString(str, indent + "  ");
        }
        str.append(indent).append("}\n");
    }

    public Class<?>[] toParamArray() {
        Class[] params = new Class[this.parameters.length];
        for (int i = 0; i < params.length; ++i) {
            params[i] = this.parameters[i].type.type;
        }
        return params;
    }

    public ParameterListDeclaration renameParameters(Function<ParameterDeclaration, String> nameMapper) {
        return new ParameterListDeclaration(this, nameMapper);
    }
}

