/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm.javassist;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.Bytecode;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CodeGen;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstCodeGen;
import com.bergerkiller.mountiplex.dep.javassist.compiler.MemberCodeGen;
import com.bergerkiller.mountiplex.dep.javassist.compiler.MemberResolver;
import com.bergerkiller.mountiplex.dep.javassist.compiler.NoFieldException;
import com.bergerkiller.mountiplex.dep.javassist.compiler.TypeChecker;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTList;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTree;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.CallExpr;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Expr;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Keyword;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Symbol;
import com.bergerkiller.mountiplex.reflection.resolver.ResolvedClassPool;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLJvstTypeChecker;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLMemberResolver;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class MPLJvstCodeGen
extends JvstCodeGen {
    private static final Method membercodegen_atMethodCallCore2;
    private static final Field codegen_typeChecker;
    private static final Field typechecker_resolver;
    private static final Method argTypesToString;

    public MPLJvstCodeGen(Bytecode b, CtClass cc, ResolvedClassPool cp) {
        super(b, cc, cp);
        this.setTypeChecker(new MPLJvstTypeChecker(cc, cp, this));
        this.resolver = new MPLMemberResolver(cp);
        try {
            Object typeChecker = codegen_typeChecker.get(this);
            typechecker_resolver.set(typeChecker, new MPLMemberResolver(cp));
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    @Override
    public void atCallExpr(CallExpr expr) throws CompileError {
        if (MPLJvstTypeChecker.mustSuppressResolvingOfCallExpr(expr)) {
            this.atCallExprSuppressLookup(expr);
        } else {
            super.atCallExpr(expr);
        }
    }

    private void atCallExprSuppressLookup(CallExpr expr) throws CompileError {
        String mname = null;
        CtClass targetClass = null;
        Expr e = (Expr)expr.oprand1();
        ASTList args = (ASTList)expr.oprand2();
        boolean isStatic = false;
        boolean isSpecial = false;
        int aload0pos = -1;
        MemberResolver.Method cached = expr.getMethod();
        mname = ((Symbol)e.oprand2()).get();
        ASTree target = e.oprand1();
        if (target instanceof Keyword && ((Keyword)target).get() == 336) {
            isSpecial = true;
        }
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
            isStatic = true;
        }
        MPLMemberResolver resolver = (MPLMemberResolver)this.resolver;
        if (this.arrayDim > 0) {
            targetClass = resolver.lookupClass("java.lang.Object", true);
        } else if (this.exprType == 307) {
            targetClass = resolveClassName ? resolver.lookupClassByJvmName(this.className) : resolver.lookupClassByJvmNameIgnoreResolver(this.className);
        } else {
            throw new CompileError("bad method");
        }
        this.atMethodCallCore(targetClass, mname, args, isStatic, isSpecial, aload0pos, cached);
    }

    @Override
    public void atMethodCallCore(CtClass targetClass, String mname, ASTList args, boolean isStatic, boolean isSpecial, int aload0pos, MemberResolver.Method found) throws CompileError {
        int nargs = this.getMethodArgsLength(args);
        int[] types = new int[nargs];
        int[] dims = new int[nargs];
        String[] cnames = new String[nargs];
        if (!isStatic && found != null && found.isStatic()) {
            this.bytecode.addOpcode(87);
            isStatic = true;
        }
        int stack = this.bytecode.getStackDepth();
        this.atMethodArgs(args, types, dims, cnames);
        if (found == null) {
            found = this.resolver.lookupMethod(targetClass, this.thisClass, this.thisMethod, mname, types, dims, cnames);
        }
        if (found == null) {
            String signature;
            String clazz = targetClass.getName();
            try {
                signature = (String)argTypesToString.invoke(null, types, dims, cnames);
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
            String msg = mname.equals("<init>") ? "cannot find constructor " + clazz + signature : mname + signature + " not found in " + clazz;
            throw new CompileError(msg);
        }
        try {
            membercodegen_atMethodCallCore2.invoke((Object)this, targetClass, found.info.getName(), isStatic, isSpecial, aload0pos, found);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    static {
        try {
            membercodegen_atMethodCallCore2 = MemberCodeGen.class.getDeclaredMethod("atMethodCallCore2", CtClass.class, String.class, Boolean.TYPE, Boolean.TYPE, Integer.TYPE, MemberResolver.Method.class);
            membercodegen_atMethodCallCore2.setAccessible(true);
            codegen_typeChecker = CodeGen.class.getDeclaredField("typeChecker");
            codegen_typeChecker.setAccessible(true);
            typechecker_resolver = TypeChecker.class.getDeclaredField("resolver");
            typechecker_resolver.setAccessible(true);
            argTypesToString = TypeChecker.class.getDeclaredMethod("argTypesToString", int[].class, int[].class, String[].class);
            argTypesToString.setAccessible(true);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
}

