/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm.javassist;

import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.CtMember;
import com.bergerkiller.mountiplex.dep.javassist.CtMethod;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.Javac;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLJavac;

public class MPLCtNewMethod {
    public static CtMethod make(String src, CtClass declaring) throws CannotCompileException {
        Javac compiler = MPLJavac.create(declaring);
        try {
            CtMember obj = compiler.compile(src);
            if (obj instanceof CtMethod) {
                return (CtMethod)obj;
            }
        }
        catch (CompileError e) {
            throw new CannotCompileException(e);
        }
        throw new CannotCompileException("not a method");
    }
}

