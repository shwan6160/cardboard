/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.IgnoredFieldAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

public abstract class SafeDirectField<T>
implements FieldAccessor<T> {
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public T transfer(Object from, Object to) {
        Object old = this.get(to);
        this.set(to, this.get(from));
        return old;
    }

    @Override
    public <K> TranslatorFieldAccessor<K> translate(DuplexConverter<?, K> converterPair) {
        return new TranslatorFieldAccessor<K>(this, converterPair);
    }

    @Override
    public FieldAccessor<T> ignoreInvalid(T defaultValue) {
        if (this.isValid()) {
            return this;
        }
        return new IgnoredFieldAccessor<T>(defaultValue);
    }
}

