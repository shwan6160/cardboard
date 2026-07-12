/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.type.RawConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayConversion {
    public static void register() {
        for (Class<?> unboxedType : BoxedType.getUnboxedTypes()) {
            if (unboxedType.equals(Void.TYPE)) continue;
            Class<?> boxedType = BoxedType.getBoxedType(unboxedType);
            Class<?> unboxedArray = MountiplexUtil.getArrayType(unboxedType);
            Class<?> boxedArray = MountiplexUtil.getArrayType(boxedType);
            Conversion.registerConverter(new ArrayConverter(unboxedArray, boxedArray));
            Conversion.registerConverter(new ArrayConverter(boxedArray, unboxedArray));
        }
        Conversion.registerProvider(new ConverterProvider(){

            @Override
            public void getConverters(TypeDeclaration output, List<Converter<?, ?>> converters) {
                TypeDeclaration input;
                if (output.type.equals(List.class)) {
                    final TypeDeclaration outputElementType = output.getGenericType(0);
                    input = TypeDeclaration.fromClass(MountiplexUtil.getArrayType(outputElementType.type));
                    input = input.setGenericTypes(outputElementType.genericTypes);
                    converters.add(new Converter<Object[], List<?>>(input, output){

                        @Override
                        public List<?> convertInput(Object[] value) {
                            return Arrays.asList(value);
                        }
                    });
                    converters.add(new InputConverter<List<?>>(this, output){
                        final /* synthetic */ 1 this$0;
                        {
                            this.this$0 = this$0;
                            super(output);
                        }

                        @Override
                        public Converter<?, List<?>> getConverter(TypeDeclaration input) {
                            Converter<Object, Object> reverseConverter;
                            if (!input.isArray()) {
                                return null;
                            }
                            TypeDeclaration inputElementType = input.getComponentType();
                            final Converter<Object, Object> elementConverter = Conversion.find(inputElementType, outputElementType);
                            if (elementConverter == null) {
                                return null;
                            }
                            if (!inputElementType.isPrimitive && (reverseConverter = Conversion.find(outputElementType, inputElementType)) != null) {
                                final DuplexConverter<Object, Object> duplexConverter = DuplexConverter.pair(elementConverter, reverseConverter);
                                return new Converter<Object, List<?>>(this, input, this.output){
                                    final /* synthetic */ 2 this$1;
                                    {
                                        this.this$1 = this$1;
                                        super(input, output);
                                    }

                                    @Override
                                    public List<?> convertInput(Object value) {
                                        return new ConvertingList(Arrays.asList((Object[])value), duplexConverter);
                                    }
                                };
                            }
                            return new Converter<Object, List<?>>(this, input, this.output){
                                final /* synthetic */ 2 this$1;
                                {
                                    this.this$1 = this$1;
                                    super(input, output);
                                }

                                @Override
                                public List<?> convertInput(Object value) {
                                    int arrLen = Array.getLength(value);
                                    ArrayList result = new ArrayList(arrLen);
                                    for (int i = 0; i < arrLen; ++i) {
                                        result.add(elementConverter.convert(Array.get(value, i)));
                                    }
                                    return result;
                                }
                            };
                        }

                        @Override
                        public boolean isLazy() {
                            return true;
                        }
                    });
                }
                if (output.type.isArray()) {
                    if (output.type.isArray() && !output.type.getComponentType().isPrimitive()) {
                        TypeDeclaration elementType = TypeDeclaration.fromClass(output.type.getComponentType());
                        elementType = elementType.setGenericTypes(output.genericTypes);
                        input = TypeDeclaration.fromClass(List.class);
                        input = input.setGenericTypes(elementType);
                        converters.add(new Converter<List<?>, Object[]>(input, output){

                            @Override
                            public Object[] convertInput(List<?> value) {
                                Object[] result = (Object[])Array.newInstance(this.output.type.getComponentType(), value.size());
                                return value.toArray(result);
                            }
                        });
                    }
                    TypeDeclaration collIn = TypeDeclaration.fromClass(Collection.class);
                    final TypeDeclaration outputElementType = output.getComponentType();
                    converters.add(new InputConverter<Object>(this, collIn, output){
                        final /* synthetic */ 1 this$0;
                        {
                            this.this$0 = this$0;
                            super(input, output);
                        }

                        @Override
                        public Converter<?, Object> getConverter(TypeDeclaration input) {
                            if (!Collection.class.isAssignableFrom(input.type)) {
                                return null;
                            }
                            final TypeDeclaration inputElementType = input.getGenericType(0);
                            final Converter<Object, Object> elementConverter = Conversion.find(inputElementType, outputElementType);
                            if (elementConverter == null) {
                                return null;
                            }
                            return new Converter<Collection<?>, Object>(this, input, this.output){
                                final /* synthetic */ 4 this$1;
                                {
                                    this.this$1 = this$1;
                                    super(input, output);
                                }

                                @Override
                                public Object convertInput(Collection<?> value) {
                                    ?[] inArray = value.toArray(MountiplexUtil.createArray(inputElementType.type, value.size()));
                                    int arrSize = inArray.length;
                                    Object result = Array.newInstance(outputElementType.type, arrSize);
                                    for (int i = 0; i < inArray.length; ++i) {
                                        Array.set(result, i, elementConverter.convert(inArray[i]));
                                    }
                                    return result;
                                }
                            };
                        }
                    });
                    TypeDeclaration input2 = TypeDeclaration.parse("Object[]");
                    final TypeDeclaration elementOutput = output.getComponentType();
                    converters.add(new InputConverter<Object>(this, input2, output){
                        final /* synthetic */ 1 this$0;
                        {
                            this.this$0 = this$0;
                            super(input, output);
                        }

                        @Override
                        public Converter<?, Object> getConverter(TypeDeclaration input) {
                            if (!input.type.isArray()) {
                                return null;
                            }
                            TypeDeclaration elementInput = input.getComponentType();
                            final Converter<Object, Object> elementConverter = Conversion.find(elementInput, elementOutput);
                            if (elementConverter == null) {
                                return null;
                            }
                            return new Converter<Object, Object>(this, input, this.output){
                                final /* synthetic */ 5 this$1;
                                {
                                    this.this$1 = this$1;
                                    super(input, output);
                                }

                                @Override
                                public Object convertInput(Object value) {
                                    int arrSize = Array.getLength(value);
                                    Object result = Array.newInstance(elementOutput.type, arrSize);
                                    for (int i = 0; i < arrSize; ++i) {
                                        Array.set(result, i, elementConverter.convert(Array.get(value, i)));
                                    }
                                    return result;
                                }
                            };
                        }
                    });
                }
            }
        });
    }

    private static final class ArrayConverter
    extends RawConverter {
        private final Converter<Object, Object> componentConverter;

        public ArrayConverter(Class<?> input, Class<?> output) {
            super(input, output);
            this.componentConverter = Conversion.find(input.getComponentType(), output.getComponentType());
        }

        @Override
        public Object convertInput(Object value) {
            int len = Array.getLength(value);
            Object result = Array.newInstance(this.output.type.getComponentType(), len);
            for (int i = 0; i < len; ++i) {
                Array.set(result, i, this.componentConverter.convert(Array.get(value, i)));
            }
            return result;
        }
    }
}

