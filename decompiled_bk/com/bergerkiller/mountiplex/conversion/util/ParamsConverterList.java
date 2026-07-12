/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.function.Function;

public class ParamsConverterList<T> {
    public Function<Object, T> result = null;
    public Function<Object, ?>[] args = null;
    public Function<Object, ?> arg0 = null;
    public Function<Object, ?> arg1 = null;
    public Function<Object, ?> arg2 = null;
    public Function<Object, ?> arg3 = null;
    public Function<Object, ?> arg4 = null;
    public boolean valid = true;
    private static final Supplier UNINITIALIZED_SUPPLIER = new Supplier(){

        public ParamsConverterList get() {
            throw new UnsupportedOperationException("Converters have not been initialized");
        }

        @Override
        public boolean available() {
            return false;
        }
    };

    public ParamsConverterList(String name, TypeDeclaration returnType, ParameterListDeclaration parameters) {
        if (returnType.cast != null) {
            this.result = Conversion.find(returnType, returnType.cast);
            if (this.result == null) {
                this.valid = false;
                MountiplexUtil.LOGGER.warning("Converter for " + name + " return type not found: " + returnType.toString());
            } else if (this.result instanceof NullConverter) {
                this.result = null;
            }
        }
        ParameterDeclaration[] params = parameters.parameters;
        this.args = new Function[params.length];
        boolean hasArgumentConversion = false;
        for (int i = 0; i < params.length; ++i) {
            if (params[i].type.cast != null) {
                this.args[i] = Conversion.find(params[i].type.cast, params[i].type);
                if (this.args[i] == null) {
                    this.valid = false;
                    MountiplexUtil.LOGGER.warning("Converter for " + name + " argument " + params[i].name.toString() + " not found: " + params[i].type.toString());
                    continue;
                }
                if (this.args[i] instanceof NullConverter) {
                    this.args[i] = Function.identity();
                    continue;
                }
                hasArgumentConversion = true;
                continue;
            }
            this.args[i] = Function.identity();
        }
        if (hasArgumentConversion) {
            if (params.length >= 1) {
                this.arg0 = this.args[0];
            }
            if (params.length >= 2) {
                this.arg1 = this.args[1];
            }
            if (params.length >= 3) {
                this.arg2 = this.args[2];
            }
            if (params.length >= 4) {
                this.arg3 = this.args[3];
            }
            if (params.length >= 5) {
                this.arg4 = this.args[4];
            }
        } else {
            this.args = null;
        }
    }

    public final T convertResult(Object result) {
        if (this.result != null) {
            return this.result.apply(result);
        }
        return (T)result;
    }

    public final Object[] convertArgs(Object[] arguments) {
        if (this.args != null) {
            Object[] convertedArgs = new Object[arguments.length];
            for (int i = 0; i < convertedArgs.length; ++i) {
                convertedArgs[i] = this.args[i].apply(arguments[i]);
            }
            return convertedArgs;
        }
        return arguments;
    }

    public static interface Supplier<T> {
        public ParamsConverterList<T> get();

        public boolean available();

        public static <T> Supplier<T> uninitialized() {
            return UNINITIALIZED_SUPPLIER;
        }

        public static <T> Supplier<T> unsupported(final String message) {
            return new Supplier<T>(){

                @Override
                public ParamsConverterList<T> get() {
                    throw new UnsupportedOperationException(message);
                }

                @Override
                public boolean available() {
                    return false;
                }
            };
        }

        public static <T> Supplier<T> of(final ParamsConverterList<T> value) {
            return new Supplier<T>(){

                @Override
                public ParamsConverterList<T> get() {
                    return value;
                }

                @Override
                public boolean available() {
                    return true;
                }
            };
        }

        public static <T> Supplier<T> lazy(final java.util.function.Supplier<Supplier<T>> supplierOfSupplier) {
            return new Supplier<T>(){

                @Override
                public ParamsConverterList<T> get() {
                    return ((Supplier)supplierOfSupplier.get()).get();
                }

                @Override
                public boolean available() {
                    return ((Supplier)supplierOfSupplier.get()).available();
                }
            };
        }
    }
}

