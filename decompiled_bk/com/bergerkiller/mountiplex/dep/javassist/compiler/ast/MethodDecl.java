/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.compiler.ast;

import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTList;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTree;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Declarator;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Stmnt;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Symbol;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Visitor;

public class MethodDecl
extends ASTList {
    private static final long serialVersionUID = 1L;
    public static final String initName = "<init>";

    public MethodDecl(ASTree _head, ASTList _tail) {
        super(_head, _tail);
    }

    public boolean isConstructor() {
        Symbol sym = this.getReturn().getVariable();
        return sym != null && initName.equals(sym.get());
    }

    public ASTList getModifiers() {
        return (ASTList)this.getLeft();
    }

    public Declarator getReturn() {
        return (Declarator)this.tail().head();
    }

    public ASTList getParams() {
        return (ASTList)this.sublist(2).head();
    }

    public ASTList getThrows() {
        return (ASTList)this.sublist(3).head();
    }

    public Stmnt getBody() {
        return (Stmnt)this.sublist(4).head();
    }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atMethodDecl(this);
    }
}

