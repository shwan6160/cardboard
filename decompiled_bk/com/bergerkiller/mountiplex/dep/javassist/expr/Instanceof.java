/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.dep.javassist.expr;

import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.CtBehavior;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.BadBytecode;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.Bytecode;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.CodeAttribute;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.CodeIterator;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.ConstPool;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.MethodInfo;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.Javac;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstCodeGen;
import com.bergerkiller.mountiplex.dep.javassist.compiler.JvstTypeChecker;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ProceedHandler;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTList;
import com.bergerkiller.mountiplex.dep.javassist.expr.Expr;

public class Instanceof
extends Expr {
    protected Instanceof(int pos, CodeIterator i, CtClass declaring, MethodInfo m) {
        super(pos, i, declaring, m);
    }

    @Override
    public CtBehavior where() {
        return super.where();
    }

    @Override
    public int getLineNumber() {
        return super.getLineNumber();
    }

    @Override
    public String getFileName() {
        return super.getFileName();
    }

    public CtClass getType() throws NotFoundException {
        ConstPool cp = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        String name = cp.getClassInfo(index);
        return this.thisClass.getClassPool().getCtClass(name);
    }

    @Override
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    @Override
    public void replace(String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        ConstPool constPool = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        Javac jc = new Javac(this.thisClass);
        ClassPool cp = this.thisClass.getClassPool();
        CodeAttribute ca = this.iterator.get();
        try {
            CtClass[] params = new CtClass[]{cp.get("java.lang.Object")};
            CtClass retType = CtClass.booleanType;
            int paramVar = ca.getMaxLocals();
            jc.recordParams("java.lang.Object", params, true, paramVar, this.withinStatic());
            int retVar = jc.recordReturnType(retType, true);
            jc.recordProceed(new ProceedForInstanceof(index));
            jc.recordType(this.getType());
            Instanceof.checkResultValue(retType, statement);
            Bytecode bytecode = jc.getBytecode();
            Instanceof.storeStack(params, true, paramVar, bytecode);
            jc.recordLocalVariables(ca, pos);
            bytecode.addConstZero(retType);
            bytecode.addStore(retVar, retType);
            jc.compileStmnt(statement);
            bytecode.addLoad(retVar, retType);
            this.replace0(pos, bytecode, 3);
        }
        catch (CompileError e) {
            throw new CannotCompileException(e);
        }
        catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }
        catch (BadBytecode e) {
            throw new CannotCompileException("broken method");
        }
    }

    static class ProceedForInstanceof
    implements ProceedHandler {
        int index;

        ProceedForInstanceof(int i) {
            this.index = i;
        }

        @Override
        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
            if (gen.getMethodArgsLength(args) != 1) {
                throw new CompileError("$proceed() cannot take more than one parameter for instanceof");
            }
            gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
            bytecode.addOpcode(193);
            bytecode.addIndex(this.index);
            gen.setType(CtClass.booleanType);
        }

        @Override
        public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
            c.atMethodArgs(args, new int[1], new int[1], new String[1]);
            c.setType(CtClass.booleanType);
        }
    }
}

