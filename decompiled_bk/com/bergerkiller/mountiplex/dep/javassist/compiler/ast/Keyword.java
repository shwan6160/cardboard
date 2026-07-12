/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.compiler.ast;

import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTree;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Visitor;

public class Keyword
extends ASTree {
    private static final long serialVersionUID = 1L;
    protected int tokenId;

    public Keyword(int token) {
        this.tokenId = token;
    }

    public int get() {
        return this.tokenId;
    }

    @Override
    public String toString() {
        return "id:" + this.tokenId;
    }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atKeyword(this);
    }
}

