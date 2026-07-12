/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.ConstructorDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.fast.Constructor;
import com.bergerkiller.mountiplex.reflection.util.fast.ReflectionConstructor;

public class FastConstructor<T>
implements Constructor<T>,
LazyInitializedObject,
IgnoresRemapping {
    private Constructor<T> constructor;
    private ConstructorDeclaration constructorDec;
    private String missingInfo = "!!UNKNOWN!!";

    public FastConstructor() {
        this.constructorDec = null;
        this.constructor = new FastConstructorInitProxy();
    }

    public FastConstructor(java.lang.reflect.Constructor<?> constructor) {
        this.constructorDec = new ConstructorDeclaration(ClassResolver.DEFAULT, constructor);
        this.constructor = new FastConstructorInitProxy();
    }

    public final void init(java.lang.reflect.Constructor<?> constructor) {
        this.constructorDec = new ConstructorDeclaration(ClassResolver.DEFAULT, constructor);
        this.constructor = new FastConstructorInitProxy();
    }

    public final void init(ConstructorDeclaration constructorDeclaration) {
        if (constructorDeclaration != null && constructorDeclaration.constructor == null) {
            this.constructorDec = null;
            this.constructor = new FastConstructorInitProxy();
        } else {
            this.constructorDec = constructorDeclaration;
            this.constructor = new FastConstructorInitProxy();
        }
    }

    public final void initUnavailable(String missingInfo) {
        this.constructorDec = null;
        this.constructor = new FastConstructorInitProxy();
        this.missingInfo = missingInfo;
    }

    public final void checkInit() {
        if (this.constructorDec == null) {
            throw new UnsupportedOperationException("Constructor " + this.missingInfo + " is not available");
        }
    }

    public final boolean isAvailable() {
        return this.constructorDec != null;
    }

    public final java.lang.reflect.Constructor<?> getConstructor() {
        return this.constructorDec == null ? null : this.constructorDec.constructor;
    }

    public final boolean isConstructor(java.lang.reflect.Constructor<?> constructor) {
        return this.constructorDec != null && this.constructorDec.constructor != null && this.constructorDec.constructor.equals(constructor);
    }

    public final String getName() {
        if (this.constructorDec == null) {
            return "null";
        }
        StringBuilder name = new StringBuilder();
        name.append("constr");
        for (ParameterDeclaration param : this.constructorDec.parameters.parameters) {
            name.append(param.name.real());
        }
        return name.toString();
    }

    @Override
    public void forceInitialization() {
        if (this.constructor instanceof FastConstructorInitProxy) {
            ((FastConstructorInitProxy)this.constructor).init();
        }
    }

    @Override
    public T newInstanceVA(Object ... args) {
        return this.constructor.newInstanceVA(args);
    }

    @Override
    public T newInstance() {
        return this.constructor.newInstance();
    }

    @Override
    public T newInstance(Object arg0) {
        return this.constructor.newInstance(arg0);
    }

    @Override
    public T newInstance(Object arg0, Object arg1) {
        return this.constructor.newInstance(arg0, arg1);
    }

    @Override
    public T newInstance(Object arg0, Object arg1, Object arg2) {
        return this.constructor.newInstance(arg0, arg1, arg2);
    }

    @Override
    public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3) {
        return this.constructor.newInstance(arg0, arg1, arg2, arg3);
    }

    @Override
    public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
        return this.constructor.newInstance(arg0, arg1, arg2, arg3, arg4);
    }

    private final class FastConstructorInitProxy
    implements Constructor<T> {
        private FastConstructorInitProxy() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private final Constructor<T> init() {
            if (FastConstructor.this.constructor == this) {
                FastConstructor fastConstructor = FastConstructor.this;
                synchronized (fastConstructor) {
                    if (FastConstructor.this.constructor == this) {
                        FastConstructor.this.checkInit();
                        ((FastConstructor)FastConstructor.this).constructorDec.constructor.setAccessible(true);
                        FastConstructor.this.constructor = ReflectionConstructor.create(((FastConstructor)FastConstructor.this).constructorDec.constructor);
                    }
                }
            }
            return FastConstructor.this.constructor;
        }

        @Override
        public T newInstanceVA(Object ... args) {
            return this.init().newInstanceVA(args);
        }

        @Override
        public T newInstance() {
            return this.init().newInstance();
        }

        @Override
        public T newInstance(Object arg0) {
            return this.init().newInstance(arg0);
        }

        @Override
        public T newInstance(Object arg0, Object arg1) {
            return this.init().newInstance(arg0, arg1);
        }

        @Override
        public T newInstance(Object arg0, Object arg1, Object arg2) {
            return this.init().newInstance(arg0, arg1, arg2);
        }

        @Override
        public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3) {
            return this.init().newInstance(arg0, arg1, arg2, arg3);
        }

        @Override
        public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
            return this.init().newInstance(arg0, arg1, arg2, arg3, arg4);
        }
    }
}

