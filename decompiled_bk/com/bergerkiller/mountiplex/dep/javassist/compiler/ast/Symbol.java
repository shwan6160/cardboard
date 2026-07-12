/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.compiler.ast;

import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTree;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Visitor;

public class Symbol
extends ASTree {
    private static final long serialVersionUID = 1L;
    protected String identifier;

    public Symbol(String sym) {
        this.identifier = sym;
    }

    public String get() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return this.identifier;
    }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atSymbol(this);
    }
}

