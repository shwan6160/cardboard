/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion;

import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import java.util.function.Function;

public abstract class Converter<I, O>
implements Function<Object, O> {
    public final TypeDeclaration input;
    public final TypeDeclaration output;

    public Converter(Class<?> input, Class<?> output) {
        this(TypeDeclaration.fromClass(input), TypeDeclaration.fromClass(output));
    }

    public Converter(TypeDeclaration input, TypeDeclaration output) {
        this.input = input;
        this.output = output;
    }

    public abstract O convertInput(I var1);

    public final O convert(Object value) {
        O result = null;
        if (value != null) {
            Class<?> inputType = this.input.type;
            if (inputType.isPrimitive()) {
                inputType = BoxedType.getBoxedType(inputType);
            }
            if (inputType.isAssignableFrom(value.getClass())) {
                result = this.convertInput(value);
            }
        } else if (this.acceptsNullInput()) {
            result = this.convertInput(null);
        }
        if (result == null && this.output.isPrimitive) {
            result = (O)BoxedType.getDefaultValue(this.output.type);
        }
        return result;
    }

    @Override
    public final O apply(Object value) {
        O result = null;
        if (value != null) {
            Class<?> inputType = this.input.type;
            if (inputType.isPrimitive()) {
                inputType = BoxedType.getBoxedType(inputType);
            }
            if (inputType.isAssignableFrom(value.getClass())) {
                result = this.convertInput(value);
            }
        } else if (this.acceptsNullInput()) {
            result = this.convertInput(null);
        }
        if (result == null && this.output.isPrimitive) {
            result = (O)BoxedType.getDefaultValue(this.output.type);
        }
        return result;
    }

    public final O convert(Object value, O defaultValue) {
        O result = null;
        if (value != null) {
            Class<?> inputType = this.input.type;
            if (inputType.isPrimitive()) {
                inputType = BoxedType.getBoxedType(inputType);
            }
            if (inputType.isAssignableFrom(value.getClass())) {
                result = this.convertInput(value);
            }
        } else if (this.acceptsNullInput()) {
            result = this.convertInput(null);
        }
        if (result == null && (result = (O)defaultValue) == null && this.output.isPrimitive) {
            result = (O)BoxedType.getDefaultValue(this.output.type);
        }
        return result;
    }

    public boolean isLazy() {
        return false;
    }

    public int getCost() {
        return this.isLazy() ? 100 : 1;
    }

    public boolean acceptsNullInput() {
        return false;
    }

    public String toString() {
        return this.getClass().getName() + "[cost=" + this.getCost() + ", " + this.input.toString() + " -> " + this.output.toString() + "]";
    }
}

