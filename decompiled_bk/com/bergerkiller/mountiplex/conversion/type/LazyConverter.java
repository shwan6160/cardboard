/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.FailingConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import java.util.ArrayList;
import java.util.List;

public final class LazyConverter<I, O>
extends Converter<I, O>
implements LazyInitializedObject {
    public Converter<I, O> converter;
    private static final LazyConverter UNINITIALIZED = LazyConverter.of(FailingConverter.uninitialized());

    private LazyConverter(Converter<I, O> constant) {
        super(constant.input, constant.output);
        this.converter = constant;
    }

    private LazyConverter(TypeDeclaration input, TypeDeclaration output) {
        super(input, output);
        this.converter = new InitConverter(input, output);
    }

    public boolean isAvailable() {
        Converter<I, O> c = this.converter;
        if (c instanceof LazyInitializedObject) {
            ((LazyInitializedObject)((Object)c)).forceInitialization();
        }
        return !(this.converter instanceof FailingConverter);
    }

    public void whenFailing(FailCallback consumer) {
        Converter<I, O> c = this.converter;
        if (c instanceof InitConverter) {
            ((InitConverter)c).failConsumers.add(consumer);
        } else if (c instanceof FailingConverter) {
            consumer.failed(c.input, c.output);
        }
    }

    @Override
    public O convertInput(I value) {
        return this.converter.convertInput(value);
    }

    @Override
    public boolean acceptsNullInput() {
        return this.converter.acceptsNullInput();
    }

    @Override
    public void forceInitialization() {
        Converter<I, O> c = this.converter;
        if (c instanceof LazyInitializedObject) {
            ((LazyInitializedObject)((Object)c)).forceInitialization();
        }
        if ((c = this.converter) instanceof FailingConverter) {
            throw new UnsupportedOperationException(((FailingConverter)c).getMessage());
        }
    }

    public static <I, O> LazyConverter<I, O> uninitialized() {
        return UNINITIALIZED;
    }

    public static <I, O> LazyConverter<I, O> of(Converter<I, O> converter) {
        return new LazyConverter<I, O>(converter);
    }

    public static <I, O> LazyConverter<I, O> create(Class<?> input, Class<?> output) {
        return new LazyConverter<I, O>(TypeDeclaration.fromClass(input), TypeDeclaration.fromClass(output));
    }

    public static <I, O> LazyConverter<I, O> create(TypeDeclaration input, TypeDeclaration output) {
        return new LazyConverter<I, O>(input, output);
    }

    private final class InitConverter
    extends Converter<I, O>
    implements LazyInitializedObject {
        private final List<FailCallback> failConsumers;

        public InitConverter(TypeDeclaration input, TypeDeclaration output) {
            super(input, output);
            this.failConsumers = new ArrayList<FailCallback>();
        }

        @Override
        public void forceInitialization() {
            this.init();
        }

        private Converter<I, O> init() {
            Converter<Object, Object> result = Conversion.find(this.input, this.output);
            if (result != null) {
                LazyConverter.this.converter = result;
            } else {
                FailingConverter f = FailingConverter.create(this.input, this.output, "Converter not found: " + this.input.toString(true) + " -> " + this.output.toString(true));
                LazyConverter.this.converter = f;
                for (FailCallback consumer : this.failConsumers) {
                    consumer.failed(this.input, this.output);
                }
            }
            return LazyConverter.this.converter;
        }

        @Override
        public O convertInput(I value) {
            return this.init().convertInput(value);
        }

        @Override
        public boolean acceptsNullInput() {
            return this.init().acceptsNullInput();
        }
    }

    public static interface FailCallback {
        public void failed(TypeDeclaration var1, TypeDeclaration var2);
    }
}

