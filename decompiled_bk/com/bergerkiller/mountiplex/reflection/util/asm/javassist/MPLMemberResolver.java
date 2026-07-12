/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.asm.javassist;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.CtClass;
import com.bergerkiller.mountiplex.dep.javassist.CtField;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.dep.javassist.bytecode.MethodInfo;
import com.bergerkiller.mountiplex.dep.javassist.compiler.CompileError;
import com.bergerkiller.mountiplex.dep.javassist.compiler.MemberResolver;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.ASTree;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Member;
import com.bergerkiller.mountiplex.dep.javassist.compiler.ast.Symbol;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.resolver.ResolvedClassPool;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ArrayHelper;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedCodeInvoker;
import com.bergerkiller.mountiplex.reflection.util.signature.FieldSignature;
import com.bergerkiller.mountiplex.reflection.util.signature.MethodSignature;
import java.lang.reflect.Method;
import java.util.Map;

public final class MPLMemberResolver
extends MemberResolver {
    public static final String IGNORE_PREFIX = "MPL_NOREMAP$";
    private static final String GENERATED_CODE_INVOKER_NAME = GeneratedCodeInvoker.class.getName();
    private final ResolvedClassPool resolvedClassPool;
    private FailedLocalFieldLookup lastFailedLocalFieldLookup = null;
    private static final String INVALID;
    private static final Method getInvalidNamesMethod;

    public MPLMemberResolver(ResolvedClassPool cp) {
        super(cp);
        this.resolvedClassPool = cp;
    }

    @Override
    public ResolvedClassPool getClassPool() {
        return this.resolvedClassPool;
    }

    @Override
    public MemberResolver.Method lookupMethod(CtClass clazz, CtClass currentClass, MethodInfo current, String methodName, int[] argTypes, int[] argDims, String[] argClassNames) throws CompileError {
        String actualMethodName = this.preprocessMethodName(clazz, methodName, argTypes, argDims, argClassNames);
        try (ResolvedClassPool.IgnoreToken t = this.resolvedClassPool.ignoreResolver();){
            MemberResolver.Method method = super.lookupMethod(clazz, currentClass, current, actualMethodName, argTypes, argDims, argClassNames);
            return method;
        }
    }

    @Override
    public CtField lookupField(String className, Symbol fieldNameSymbol) throws CompileError {
        CtClass cc;
        try (ResolvedClassPool.IgnoreToken t = this.resolvedClassPool.ignoreResolver();){
            cc = this.lookupClass(className, false);
        }
        String fieldName = fieldNameSymbol.get();
        if (fieldNameSymbol instanceof Member) {
            fieldName = this.preprocessFieldName(cc, fieldName);
        }
        ResolvedClassPool.IgnoreToken t = this.resolvedClassPool.ignoreResolver();
        try {
            CtField ctField = cc.getField(fieldName);
            if (t != null) {
                t.close();
            }
            return ctField;
        }
        catch (Throwable throwable) {
            try {
                if (t != null) {
                    try {
                        t.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (NotFoundException notFoundException) {
                this.lastFailedLocalFieldLookup = new FailedLocalFieldLookup(cc.getName(), fieldNameSymbol.get());
                throw new CompileError("no such field: " + cc.getName() + " -> " + fieldNameSymbol.get());
            }
        }
    }

    @Override
    public CtField lookupFieldByJvmName2(String jvmClassName, Symbol fieldSym, ASTree expr) throws com.bergerkiller.mountiplex.dep.javassist.compiler.NoFieldException {
        String field = fieldSym.get();
        CtClass cc = null;
        String javaName = MPLMemberResolver.jvmToJavaName(jvmClassName);
        try {
            cc = this.lookupClass(javaName, true);
        }
        catch (CompileError e) {
            if (field.startsWith(IGNORE_PREFIX)) {
                field = field.substring(IGNORE_PREFIX.length());
            }
            if (this.lastFailedLocalFieldLookup != null && this.lastFailedLocalFieldLookup.fieldName.equals(field)) {
                MountiplexUtil.LOGGER.severe("Local field lookup also failed: " + this.lastFailedLocalFieldLookup.className + " -> " + this.lastFailedLocalFieldLookup.fieldName);
                this.lastFailedLocalFieldLookup = null;
            }
            throw new NoFieldException(jvmClassName + "/" + field, "??" + javaName + "??." + field, expr);
        }
        field = this.preprocessFieldName(cc, field);
        try {
            return cc.getField(field);
        }
        catch (NotFoundException e) {
            jvmClassName = MPLMemberResolver.javaToJvmName(cc.getName());
            throw new NoFieldException(jvmClassName + "$" + field, cc.getName() + "." + field, expr);
        }
    }

    @Override
    public CtClass lookupClass(String name, boolean notCheckInner) throws CompileError {
        Map cache;
        try {
            cache = (Map)getInvalidNamesMethod.invoke((Object)this, new Object[0]);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
        String found = (String)cache.get(name);
        if (found == INVALID) {
            throw new CompileError("no such class: " + name);
        }
        if (found != null) {
            try {
                return this.resolvedClassPool.getWithoutResolving(found);
            }
            catch (NotFoundException e) {
                cache.remove(name);
            }
        }
        return super.lookupClass(name, notCheckInner);
    }

    public CtClass lookupClassByJvmNameIgnoreResolver(String jvmName) throws CompileError {
        String name = MPLMemberResolver.jvmToJavaName(jvmName);
        if (!name.startsWith(IGNORE_PREFIX) && !Resolver.resolveClassPath(name).equals(name)) {
            name = IGNORE_PREFIX + name;
        }
        return this.lookupClass(name, false);
    }

    @Override
    public CtClass lookupClassByJvmName(String jvmName) throws CompileError {
        return this.lookupClass(MPLMemberResolver.jvmToJavaName(jvmName), false);
    }

    @Override
    public CtClass lookupClass(int type, int dim, String classname) throws CompileError {
        if (type == 307 && dim > 0) {
            CtClass clazz = this.lookupClassByJvmName(classname);
            String cname = clazz.getName();
            while (dim-- > 0) {
                cname = cname + "[]";
            }
            return this.lookupClass(IGNORE_PREFIX + cname, false);
        }
        return super.lookupClass(type, dim, classname);
    }

    private String preprocessFieldName(CtClass clazz, String fieldName) {
        Class<?> declaringClass;
        if (fieldName.startsWith(IGNORE_PREFIX)) {
            return fieldName.substring(IGNORE_PREFIX.length());
        }
        try {
            CtClass superClazz;
            try (ResolvedClassPool.IgnoreToken t = this.resolvedClassPool.ignoreResolver();){
                superClazz = clazz.getSuperclass();
            }
            if (superClazz != null && superClazz.getName().equals(GENERATED_CODE_INVOKER_NAME)) {
                return fieldName;
            }
        }
        catch (NotFoundException superClazz) {
            // empty catch block
        }
        try {
            declaringClass = Resolver.getClassByExactName(clazz.getName());
        }
        catch (ClassNotFoundException e) {
            return fieldName;
        }
        if (IgnoresRemapping.class.isAssignableFrom(declaringClass)) {
            return fieldName;
        }
        Remapping.FieldRemapping remappedField = this.getClassPool().getRemappings().find(declaringClass, new FieldSignature(fieldName));
        if (remappedField != null) {
            return MPLType.getName(remappedField.field);
        }
        String newName = Resolver.resolveFieldName(declaringClass, fieldName);
        return newName != null ? newName : fieldName;
    }

    private String preprocessMethodName(CtClass clazz, String methodName, int[] argTypes, int[] argDims, String[] argClassNames) {
        Class<?> declaringClass;
        if (methodName.startsWith(IGNORE_PREFIX)) {
            return methodName.substring(IGNORE_PREFIX.length());
        }
        if (methodName.equals("<init>")) {
            return methodName;
        }
        if (clazz.getName().startsWith("java.")) {
            return methodName;
        }
        try {
            CtClass superClazz = clazz.getSuperclass();
            if (superClazz != null && superClazz.getName().equals(GENERATED_CODE_INVOKER_NAME)) {
                return methodName;
            }
        }
        catch (NotFoundException superClazz) {
            // empty catch block
        }
        try {
            declaringClass = Resolver.getClassByExactName(clazz.getName());
        }
        catch (ClassNotFoundException e) {
            return methodName;
        }
        if (IgnoresRemapping.class.isAssignableFrom(declaringClass)) {
            return methodName;
        }
        Class[] parameterTypes = new Class[argTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            parameterTypes[i] = MPLMemberResolver.translateType(argTypes[i], argClassNames[i]);
            if (parameterTypes[i] == null) {
                return methodName;
            }
            parameterTypes[i] = ArrayHelper.getArrayType(parameterTypes[i], argDims[i]);
        }
        Remapping.MethodRemapping remappedMethod = this.getClassPool().getRemappings().find(declaringClass, new MethodSignature(methodName, parameterTypes));
        if (remappedMethod != null) {
            return MPLType.getName(remappedMethod.method);
        }
        return Resolver.resolveMethodName(declaringClass, methodName, parameterTypes);
    }

    private static Class<?> translateType(int type, String altName) {
        switch (type) {
            case 301: {
                return Boolean.TYPE;
            }
            case 306: {
                return Character.TYPE;
            }
            case 303: {
                return Byte.TYPE;
            }
            case 334: {
                return Short.TYPE;
            }
            case 324: {
                return Integer.TYPE;
            }
            case 326: {
                return Long.TYPE;
            }
            case 317: {
                return Float.TYPE;
            }
            case 312: {
                return Double.TYPE;
            }
            case 307: {
                try {
                    return Resolver.getClassByExactName(altName.replace('/', '.'));
                }
                catch (ClassNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }

    static {
        try {
            INVALID = SafeField.get(MemberResolver.class, "INVALID", String.class);
            getInvalidNamesMethod = MemberResolver.class.getDeclaredMethod("getInvalidNames", new Class[0]);
            getInvalidNamesMethod.setAccessible(true);
        }
        catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private static class FailedLocalFieldLookup {
        public final String className;
        public final String fieldName;

        public FailedLocalFieldLookup(String className, String fieldName) {
            this.className = className;
            this.fieldName = fieldName;
        }
    }

    public static class NoFieldException
    extends com.bergerkiller.mountiplex.dep.javassist.compiler.NoFieldException {
        private static final long serialVersionUID = -3755584273114392309L;
        private final String replFieldName;

        public NoFieldException(String symbol, String printed, ASTree e) {
            super(printed, e);
            this.replFieldName = symbol;
        }

        @Override
        public String getField() {
            return this.replFieldName;
        }
    }
}

