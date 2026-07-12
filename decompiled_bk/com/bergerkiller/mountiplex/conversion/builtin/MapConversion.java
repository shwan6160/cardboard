/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.List;
import java.util.Map;

public class MapConversion {
    public static void register() {
        Conversion.registerProvider(new ConverterProvider(){

            @Override
            public void getConverters(TypeDeclaration output, List<Converter<?, ?>> converters) {
                if (output.type.equals(Map.class)) {
                    converters.add(new MapConverter(output));
                }
            }
        });
    }

    private static final class MapConverter
    extends InputConverter<Map<?, ?>> {
        private final TypeDeclaration outputKeyType;
        private final TypeDeclaration outputValueType;

        public MapConverter(TypeDeclaration output) {
            super(TypeDeclaration.fromClass(Map.class), output);
            TypeDeclaration mapOutput = output.castAsType(Map.class);
            this.outputKeyType = mapOutput.getGenericType(0);
            this.outputValueType = mapOutput.getGenericType(1);
        }

        @Override
        public final Converter<?, Map<?, ?>> getConverter(TypeDeclaration input) {
            boolean hasValueConverter;
            TypeDeclaration mapInput = input.castAsType(Map.class);
            TypeDeclaration inputKeyType = mapInput.getGenericType(0);
            TypeDeclaration inputValueType = mapInput.getGenericType(1);
            boolean hasKeyConverter = !inputKeyType.equals(this.outputKeyType);
            boolean bl = hasValueConverter = !inputValueType.equals(this.outputValueType);
            if (!hasKeyConverter && !hasValueConverter) {
                return null;
            }
            DuplexConverter<Object, Object> keyConverter = hasKeyConverter ? Conversion.findDuplex(inputKeyType, this.outputKeyType) : DuplexConverter.createNull(this.outputKeyType);
            DuplexConverter<Object, Object> valueConverter = hasValueConverter ? Conversion.findDuplex(inputValueType, this.outputValueType) : DuplexConverter.createNull(this.outputValueType);
            if (keyConverter != null && valueConverter != null) {
                return new ElementConverter(input, this.output, keyConverter, valueConverter);
            }
            return null;
        }

        private final class ElementConverter
        extends DuplexConverter<Map<?, ?>, Map<?, ?>> {
            private final DuplexConverter<Object, Object> keyConverter;
            private final DuplexConverter<Object, Object> valueConverter;

            public ElementConverter(TypeDeclaration input, TypeDeclaration output, DuplexConverter<Object, Object> keyConverter, DuplexConverter<Object, Object> valueConverter) {
                super(input, output);
                this.keyConverter = keyConverter;
                this.valueConverter = valueConverter;
            }

            @Override
            public final Map<?, ?> convertInput(Map<?, ?> value) {
                return new ConvertingMap<Object, Object>(value, this.keyConverter, this.valueConverter);
            }

            @Override
            public final Map<?, ?> convertOutput(Map<?, ?> value) {
                return new ConvertingMap<Object, Object>(value, this.keyConverter.reverse(), this.valueConverter.reverse());
            }
        }
    }
}

