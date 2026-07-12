/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.AnnotatedConverter;
import com.bergerkiller.mountiplex.conversion.type.CastingConverter;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

public abstract class DuplexConverter<A, B>
extends Converter<A, B> {
    protected DuplexConverter<B, A> reverse;

    public DuplexConverter(Class<?> typeA, Class<?> typeB) {
        super(typeA, typeB);
        this.reverse = new ReverseDuplexConverter();
    }

    public DuplexConverter(TypeDeclaration typeA, TypeDeclaration typeB) {
        super(typeA, typeB);
        this.reverse = new ReverseDuplexConverter();
    }

    protected DuplexConverter(TypeDeclaration typeA, TypeDeclaration typeB, DuplexConverter<B, A> reverse) {
        super(typeA, typeB);
        this.reverse = reverse;
    }

    @Override
    public abstract B convertInput(A var1);

    public abstract A convertOutput(B var1);

    public final A convertReverse(Object value) {
        A result = null;
        if (value != null) {
            Class<?> outputType = this.output.type;
            if (this.output.isPrimitive) {
                outputType = BoxedType.getBoxedType(outputType);
            }
            if (outputType.isAssignableFrom(value.getClass())) {
                result = this.convertOutput(value);
            }
        } else if (this.acceptsNullOutput()) {
            result = this.convertOutput(null);
        }
        if (result == null && this.input.isPrimitive) {
            result = (A)BoxedType.getDefaultValue(this.input.type);
        }
        return result;
    }

    public final A convertReverse(Object value, A defaultValue) {
        A result = null;
        if (value != null) {
            Class<?> outputType = this.output.type;
            if (this.output.isPrimitive) {
                outputType = BoxedType.getBoxedType(outputType);
            }
            if (outputType.isAssignableFrom(value.getClass())) {
                result = this.convertOutput(value);
            }
        } else if (this.acceptsNullOutput()) {
            result = this.convertOutput(null);
        }
        if (result == null) {
            result = defaultValue;
        }
        if (result == null && this.input.isPrimitive) {
            result = (A)BoxedType.getDefaultValue(this.input.type);
        }
        return result;
    }

    public boolean acceptsNullOutput() {
        return false;
    }

    public Converter<A, B> getInputConverter() {
        return this;
    }

    public Converter<B, A> getOutputConverter() {
        return this.reverse;
    }

    public final DuplexConverter<B, A> reverse() {
        return this.reverse;
    }

    public static <A, B> DuplexConverter<A, B> pair(Converter<?, B> converter, Converter<?, A> reverse) {
        DuplexConverter dupl;
        if (converter == null || reverse == null) {
            return null;
        }
        if (!converter.output.isInstanceOf(reverse.input)) {
            throw new RuntimeException("Converter output of " + converter.toString() + " can not be assigned to the input of " + reverse.toString());
        }
        if (!reverse.output.isInstanceOf(converter.input)) {
            throw new RuntimeException("Reverse converter output of " + converter.toString() + " can not be assigned to the input of " + reverse.toString());
        }
        if (DuplexConverter.isNullConverter(converter)) {
            if (DuplexConverter.isNullConverter(reverse)) {
                return DuplexConverter.createNull(converter.output);
            }
            if (reverse instanceof CastingConverter) {
                return new DuplexCastingAdapter(reverse.output, converter.output).reverse();
            }
            return new DuplexHalfAdapter(reverse, converter.output).reverse();
        }
        if (DuplexConverter.isNullConverter(reverse)) {
            if (converter instanceof CastingConverter) {
                return new DuplexCastingAdapter(converter.output, reverse.output);
            }
            return new DuplexHalfAdapter(converter, reverse.output);
        }
        if (converter instanceof DuplexConverter && (dupl = (DuplexConverter)converter).reverse() == reverse) {
            return dupl;
        }
        if (converter instanceof AnnotatedConverter && reverse instanceof AnnotatedConverter) {
            AnnotatedConverter a = (AnnotatedConverter)converter;
            AnnotatedConverter b = (AnnotatedConverter)reverse;
            return new DuplexAnnotatedConverter(a, b);
        }
        return new DuplexAdapter<A, B>(converter, reverse);
    }

    public static <A, B> DuplexConverter<A, B> createNull(TypeDeclaration type) {
        return new DuplexNullConverter(type);
    }

    private static boolean isNullConverter(Converter<?, ?> converter) {
        return converter instanceof NullConverter || converter instanceof DuplexNullConverter;
    }

    private final class ReverseDuplexConverter
    extends DuplexConverter<B, A> {
        public ReverseDuplexConverter() {
            super(DuplexConverter.this.output, DuplexConverter.this.input, DuplexConverter.this);
        }

        @Override
        public A convertInput(B value) {
            return DuplexConverter.this.convertOutput(value);
        }

        @Override
        public B convertOutput(A value) {
            return DuplexConverter.this.convertInput(value);
        }

        @Override
        public boolean acceptsNullInput() {
            return DuplexConverter.this.acceptsNullOutput();
        }

        @Override
        public boolean acceptsNullOutput() {
            return DuplexConverter.this.acceptsNullInput();
        }

        @Override
        public Converter<B, A> getInputConverter() {
            return DuplexConverter.this.getOutputConverter();
        }

        @Override
        public Converter<A, B> getOutputConverter() {
            return DuplexConverter.this.getInputConverter();
        }
    }

    private static final class DuplexCastingAdapter<A, B>
    extends DuplexConverter<A, B> {
        public DuplexCastingAdapter(TypeDeclaration typeA, TypeDeclaration typeB) {
            super(typeA, typeB);
        }

        @Override
        public B convertInput(A value) {
            if (this.output.isAssignableFrom(value)) {
                return (B)value;
            }
            return null;
        }

        @Override
        public A convertOutput(B value) {
            return (A)value;
        }

        @Override
        public boolean acceptsNullInput() {
            return false;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }

        @Override
        public String toString() {
            return "DuplexCastingAdapter{" + this.input + " <> " + this.output.toString() + "}";
        }
    }

    private static final class DuplexHalfAdapter<A, B>
    extends DuplexConverter<A, B> {
        private final Converter<A, B> converter;
        private final TypeDeclaration reverseOutput;

        public DuplexHalfAdapter(Converter<?, B> converter, TypeDeclaration reverseOutput) {
            super(reverseOutput, converter.output);
            this.converter = converter;
            this.reverseOutput = reverseOutput;
        }

        @Override
        public B convertInput(A value) {
            return this.converter.convertInput(value);
        }

        @Override
        public A convertOutput(B value) {
            return (A)value;
        }

        @Override
        public boolean acceptsNullInput() {
            return this.converter.acceptsNullInput();
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }

        @Override
        public Converter<A, B> getInputConverter() {
            return this.converter;
        }

        @Override
        public Converter<B, A> getOutputConverter() {
            return new NullConverter(this.converter.output, this.reverseOutput);
        }

        @Override
        public String toString() {
            return "DuplexHalfAdapter{" + this.reverseOutput.toString() + " <> " + this.converter.output.toString() + "}";
        }
    }

    private static final class DuplexAnnotatedConverter<A, B>
    extends DuplexConverter<A, B> {
        private final Invoker<Object> converterInvoker;
        private final Invoker<Object> reverseInvoker;
        private final boolean converterAcceptNull;
        private final boolean reverseAcceptNull;

        public DuplexAnnotatedConverter(AnnotatedConverter converter, AnnotatedConverter reverse) {
            this(converter, reverse, null);
            this.reverse = new DuplexAnnotatedConverter<B, A>(reverse, converter, this);
        }

        public DuplexAnnotatedConverter(AnnotatedConverter converter, AnnotatedConverter reverse, DuplexAnnotatedConverter<B, A> reverseDupl) {
            super(reverse.output, converter.output, reverseDupl);
            this.converterInvoker = InitInvoker.proxy((Object)this, "converterInvoker", converter.invoker);
            this.reverseInvoker = InitInvoker.proxy((Object)this, "reverseInvoker", reverse.invoker);
            this.converterAcceptNull = converter.acceptsNullInput();
            this.reverseAcceptNull = reverse.acceptsNullInput();
        }

        @Override
        public B convertInput(A value) {
            return (B)this.converterInvoker.invoke(null, value);
        }

        @Override
        public A convertOutput(B value) {
            return (A)this.reverseInvoker.invoke(null, value);
        }

        @Override
        public boolean acceptsNullInput() {
            return this.converterAcceptNull;
        }

        @Override
        public boolean acceptsNullOutput() {
            return this.reverseAcceptNull;
        }
    }

    private static final class DuplexAdapter<A, B>
    extends DuplexConverter<A, B> {
        private final Converter<A, B> input_converter;
        private final Converter<B, A> output_converter;

        public DuplexAdapter(Converter<?, B> converter, Converter<?, A> reverse) {
            this(converter, reverse, null);
            this.reverse = new DuplexAdapter<B, A>(reverse, converter, this);
        }

        private DuplexAdapter(Converter<?, B> input_converter, Converter<?, A> output_converter, DuplexAdapter<B, A> reverseDuplex) {
            super(output_converter.output, input_converter.output, reverseDuplex);
            this.input_converter = input_converter;
            this.output_converter = output_converter;
        }

        @Override
        public B convertInput(A value) {
            return this.input_converter.convertInput(value);
        }

        @Override
        public A convertOutput(B value) {
            return this.output_converter.convertInput(value);
        }

        @Override
        public boolean acceptsNullInput() {
            return this.input_converter.acceptsNullInput();
        }

        @Override
        public boolean acceptsNullOutput() {
            return this.output_converter.acceptsNullInput();
        }

        @Override
        public Converter<A, B> getInputConverter() {
            return this.input_converter;
        }

        @Override
        public Converter<B, A> getOutputConverter() {
            return this.output_converter;
        }

        @Override
        public String toString() {
            return "DuplexAdapter{" + this.output_converter.output.toString() + " <> " + this.input_converter.output.toString() + "}";
        }
    }

    private static final class DuplexNullConverter<A>
    extends DuplexConverter<A, A> {
        public DuplexNullConverter(TypeDeclaration type) {
            super(type, type, null);
            this.reverse = this;
        }

        @Override
        public A convertInput(A value) {
            return value;
        }

        @Override
        public A convertOutput(A value) {
            return value;
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }

        @Override
        public String toString() {
            return "DuplexNullConverter{" + this.input + "}";
        }
    }
}

