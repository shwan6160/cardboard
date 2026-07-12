/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.IgnoredFieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

public interface FieldAccessor<T> {
    public T get(Object var1);

    public boolean set(Object var1, T var2);

    default public boolean isValid() {
        return true;
    }

    default public T transfer(Object from, Object to) {
        T to_previous = this.get(to);
        this.set(to, this.get(from));
        return to_previous;
    }

    default public <K> TranslatorFieldAccessor<K> translate(DuplexConverter<?, K> converterPair) {
        return new TranslatorFieldAccessor<K>(this, converterPair);
    }

    default public FieldAccessor<T> ignoreInvalid(T defaultValue) {
        if (this.isValid()) {
            return this;
        }
        return new IgnoredFieldAccessor<T>(defaultValue);
    }

    public static <T> FieldAccessor<T> wrapOptionalMethods(final Template.AbstractMethod<T> getter, final Template.AbstractMethod<Void> setter, final T defaultValue) {
        if (getter.isAvailable() && setter.isAvailable()) {
            return FieldAccessor.wrapMethods(getter, setter);
        }
        if (!getter.isAvailable() && !setter.isAvailable()) {
            return new SafeDirectField<T>(){

                @Override
                public T get(Object instance) {
                    return defaultValue;
                }

                @Override
                public boolean set(Object instance, T value) {
                    return false;
                }
            };
        }
        return new SafeDirectField<T>(){

            @Override
            public T get(Object instance) {
                if (getter.isAvailable()) {
                    return getter.invoker.invoke(instance);
                }
                return defaultValue;
            }

            @Override
            public boolean set(Object instance, T value) {
                if (setter.isAvailable()) {
                    setter.invoker.invoke(instance, value);
                    return true;
                }
                return false;
            }
        };
    }

    public static <T> FieldAccessor<T> wrapMethods(final Template.AbstractMethod<T> getter, final Template.AbstractMethod<Void> setter) {
        final boolean valid = getter.isAvailable() && setter.isAvailable();
        return new SafeDirectField<T>(){

            @Override
            public T get(Object instance) {
                return getter.invoker.invoke(instance);
            }

            @Override
            public boolean set(Object instance, T value) {
                setter.invoker.invoke(instance, value);
                return true;
            }

            @Override
            public boolean isValid() {
                return valid;
            }
        };
    }

    public static <T> FieldAccessor<T> wrapMethods(final Template.Method.Converted<T> getter, final Template.Method.Converted<Void> setter) {
        final boolean valid = getter.isAvailable() && setter.isAvailable();
        return new SafeDirectField<T>(){

            @Override
            public T get(Object instance) {
                return getter.invoke(instance);
            }

            @Override
            public boolean set(Object instance, T value) {
                setter.invoke(instance, value);
                return true;
            }

            @Override
            public boolean isValid() {
                return valid;
            }
        };
    }

    public static <T> FieldAccessor<T> wrapMethods(final MethodAccessor<T> getter, final MethodAccessor<Void> setter) {
        final boolean valid = getter.isValid() && setter.isValid();
        return new SafeDirectField<T>(){

            @Override
            public T get(Object instance) {
                return getter.invoke(instance, new Object[0]);
            }

            @Override
            public boolean set(Object instance, T value) {
                setter.invoke(instance, value);
                return true;
            }

            @Override
            public boolean isValid() {
                return valid;
            }
        };
    }
}

