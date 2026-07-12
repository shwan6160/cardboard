/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm.javassist;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.ByteArrayClassPath;
import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.ClassPool;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.CtConstructor;
import com.bergerkiller.mountiplex.dep.javassist.CtField;
import com.bergerkiller.mountiplex.dep.javassist.CtMethod;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.Bytecode;
import com.bergerkiller.mountiplex.dep.javassist.compiler.Javac;
import com.bergerkiller.mountiplex.dep.javassist.compiler.SymbolTable;
import com.bergerkiller.mountiplex.reflection.resolver.ResolvedClassPool;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLJvstCodeGen;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class MPLJavac {
    private static final NullInstantiator<Javac> javac_instantiator = NullInstantiator.of(Javac.class);
    private static final Field javac_gen;
    private static final Field javac_stable;
    private static final Field javac_bytecode;

    public static Javac create(CtClass thisClass) {
        ClassPool classPool = thisClass.getClassPool();
        if (!(classPool instanceof ResolvedClassPool)) {
            throw new IllegalArgumentException("Class " + thisClass.getName() + " does not use a resolved class pool");
        }
        try {
            Javac compiler = javac_instantiator.create();
            Bytecode bytecode = new Bytecode(thisClass.getClassFile2().getConstPool(), 0, 0);
            javac_gen.set(compiler, new MPLJvstCodeGen(bytecode, thisClass, (ResolvedClassPool)classPool));
            javac_stable.set(compiler, new SymbolTable());
            javac_bytecode.set(compiler, bytecode);
            return compiler;
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    public static CtClass makeNewClass(ClassPool classPool, byte[] byteCode) throws CannotCompileException, NotFoundException {
        try {
            return classPool.makeClass(new ByteArrayInputStream(byteCode));
        }
        catch (IOException e) {
            throw new IllegalStateException("Unexpected IO Exception", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CtClass makeNewClassWithDefaultConstructor(ClassPool classPool, String name, byte[] byteCode) throws CannotCompileException, NotFoundException {
        ByteArrayClassPath selfClassPath = new ByteArrayClassPath(name, byteCode);
        try {
            classPool.insertClassPath(selfClassPath);
            CtClass fromBytecode = classPool.get(name);
            CtClass ctClass = MPLJavac.asNewClass(fromBytecode);
            return ctClass;
        }
        finally {
            classPool.removeClassPath(selfClassPath);
        }
    }

    public static CtClass asNewClass(CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtClass newClass = ctClass.getClassPool().makeClass(ctClass.getName(), ctClass.getSuperclass());
        for (CtClass ctInterface : ctClass.getInterfaces()) {
            newClass.addInterface(ctInterface);
        }
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            newClass.addMethod(new CtMethod(method, newClass, null));
        }
        for (CtField field : ctClass.getDeclaredFields()) {
            newClass.addField(new CtField(field, newClass));
        }
        for (CtConstructor constr : ctClass.getDeclaredConstructors()) {
            newClass.addConstructor(new CtConstructor(constr, newClass, null));
        }
        CtConstructor initializer = ctClass.getClassInitializer();
        if (initializer != null) {
            newClass.makeClassInitializer().setBody(initializer, null);
        }
        return newClass;
    }

    static {
        try {
            javac_gen = Javac.class.getDeclaredField("gen");
            javac_gen.setAccessible(true);
            javac_stable = Javac.class.getDeclaredField("stable");
            javac_stable.setAccessible(true);
            javac_bytecode = Javac.class.getDeclaredField("bytecode");
            javac_bytecode.setAccessible(true);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
}

