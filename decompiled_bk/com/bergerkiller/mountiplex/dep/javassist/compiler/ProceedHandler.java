/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.compiler;

import com.bergerkiller.mountiplex.dep.javassist.bytecode.Bytecode;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstCodeGen;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstTypeChecker;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTList;

public interface ProceedHandler {
    public void doit(JvstCodeGen var1, Bytecode var2, ASTList var3) throws CompileError;

    public void setReturnType(JvstTypeChecker var1, ASTList var2) throws CompileError;
}

