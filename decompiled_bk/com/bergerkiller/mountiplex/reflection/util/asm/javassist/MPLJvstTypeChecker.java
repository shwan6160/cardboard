/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm.javassist;

import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstCodeGen;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstTypeChecker;
import com.bergerkiller.mountiplex.dep.javassist.compiler.MemberResolver;
import com.bergerkiller.mountiplex.dep.javassist.compiler.NoFieldException;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTList;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTree;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.CallExpr;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Expr;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Keyword;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Symbol;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLMemberResolver;

public class MPLJvstTypeChecker
extends JvstTypeChecker {
    public MPLJvstTypeChecker(CtClass cc, ClassPool cp, JvstCodeGen gen) {
        super(cc, cp, gen);
    }

    @Override
    public void atCallExpr(CallExpr expr) throws CompileError {
        if (MPLJvstTypeChecker.mustSuppressResolvingOfCallExpr(expr)) {
            this.atCallExprSuppressLookup(expr);
        } else {
            super.atCallExpr(expr);
        }
    }

    public static boolean mustSuppressResolvingOfCallExpr(CallExpr expr) {
        ASTree right;
        Expr e;
        ASTree method = expr.oprand1();
        if (!(method instanceof Expr) || ((Expr)method).getOperator() != 46) {
            return false;
        }
        ASTree target = ((Expr)method).oprand1();
        return !(target instanceof Expr) || (e = (Expr)target).getOperator() != 46 || !((right = e.oprand2()) instanceof Keyword) || ((Keyword)right).get() != 336 || ((Symbol)e.oprand1()).get() == null;
    }

    private void atCallExprSuppressLookup(CallExpr expr) throws CompileError {
        Expr e = (Expr)expr.oprand1();
        String mname = null;
        CtClass targetClass = null;
        ASTList args = (ASTList)expr.oprand2();
        mname = ((Symbol)e.oprand2()).get();
        ASTree target = e.oprand1();
        boolean resolveClassName = false;
        try {
            target.accept(this);
        }
        catch (NoFieldException nfe) {
            if (nfe.getExpr() != target) {
                throw nfe;
            }
            resolveClassName = true;
            this.exprType = 307;
            this.arrayDim = 0;
            this.className = nfe.getField();
            e.setOperator(35);
            e.setOprand1(new Symbol(MemberResolver.jvmToJavaName(this.className)));
        }
        MPLMemberResolver resolver = (MPLMemberResolver)this.resolver;
        if (this.arrayDim > 0) {
            targetClass = resolver.lookupClass("java.lang.Object", true);
        } else if (this.exprType == 307) {
            targetClass = resolveClassName ? resolver.lookupClassByJvmName(this.className) : resolver.lookupClassByJvmNameIgnoreResolver(this.className);
        } else {
            throw new CompileError("bad method");
        }
        MemberResolver.Method minfo = this.atMethodCallCore(targetClass, mname, args);
        expr.setMethod(minfo);
    }
}

