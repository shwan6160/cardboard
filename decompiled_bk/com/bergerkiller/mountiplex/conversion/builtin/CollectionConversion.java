/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingCollection;
import com.bergerkiller.mountiplex.conversion.util.ConvertingIterable;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.conversion.util.ConvertingQueue;
import com.bergerkiller.mountiplex.conversion.util.ConvertingSet;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class CollectionConversion {
    public static void register() {
        Conversion.registerProvider(new ConverterProvider(){

            @Override
            public void getConverters(TypeDeclaration output, List<Converter<?, ?>> converters) {
                if (output.type.equals(Iterable.class)) {
                    converters.add(new CollectionConverter<Iterable<?>>(output){

                        @Override
                        protected boolean isOneWay() {
                            return true;
                        }

                        @Override
                        protected Iterable<?> change(Iterable<?> original) {
                            return original;
                        }

                        @Override
                        protected Iterable<?> create(Iterable<?> original, DuplexConverter<Object, Object> elementConverter) {
                            return new ConvertingIterable<Object>(original, elementConverter.getInputConverter());
                        }
                    });
                }
                if (output.type.equals(Collection.class)) {
                    converters.add(new CollectionConverter<Collection<?>>(output){

                        @Override
                        protected Collection<?> change(Iterable<?> original) {
                            if (original instanceof Collection) {
                                return (Collection)original;
                            }
                            ArrayList result = new ArrayList();
                            for (Object o : original) {
                                result.add(o);
                            }
                            return result;
                        }

                        @Override
                        protected Collection<?> create(Collection<?> original, DuplexConverter<Object, Object> elementConverter) {
                            return new ConvertingCollection<Object>(original, elementConverter);
                        }
                    });
                }
                if (output.type.equals(Queue.class)) {
                    converters.add(new CollectionConverter<Queue<?>>(output){

                        @Override
                        protected Queue<?> change(Iterable<?> original) {
                            if (original instanceof Collection) {
                                return new LinkedList((Collection)original);
                            }
                            LinkedList result = new LinkedList();
                            for (Object o : original) {
                                result.add(o);
                            }
                            return result;
                        }

                        @Override
                        protected Queue<?> create(Queue<?> original, DuplexConverter<Object, Object> elementConverter) {
                            return new ConvertingQueue<Object>(original, elementConverter);
                        }
                    });
                }
                if (output.type.equals(List.class)) {
                    converters.add(new CollectionConverter<List<?>>(output){

                        @Override
                        protected List<?> change(Iterable<?> original) {
                            if (original instanceof Collection) {
                                return new ArrayList((Collection)original);
                            }
                            ArrayList result = new ArrayList();
                            for (Object o : original) {
                                result.add(o);
                            }
                            return result;
                        }

                        @Override
                        protected List<?> create(List<?> original, DuplexConverter<Object, Object> elementConverter) {
                            return new ConvertingList<Object>(original, elementConverter);
                        }
                    });
                }
                if (output.type.equals(Set.class)) {
                    converters.add(new CollectionConverter<Set<?>>(output){

                        @Override
                        protected Set<?> change(Iterable<?> original) {
                            if (original instanceof Collection) {
                                return new HashSet((Collection)original);
                            }
                            HashSet result = new HashSet();
                            for (Object o : original) {
                                result.add(o);
                            }
                            return result;
                        }

                        @Override
                        protected Set<?> create(Set<?> original, DuplexConverter<Object, Object> elementConverter) {
                            return new ConvertingSet<Object>(original, elementConverter);
                        }
                    });
                }
            }
        });
    }

    private static abstract class CollectionConverter<T extends Iterable<?>>
    extends InputConverter<T> {
        public CollectionConverter(TypeDeclaration output) {
            super(TypeDeclaration.fromClass(Iterable.class), output);
        }

        protected abstract T change(Iterable<?> var1);

        protected abstract T create(T var1, DuplexConverter<Object, Object> var2);

        protected boolean isOneWay() {
            return false;
        }

        @Override
        public int getCost() {
            return 2;
        }

        @Override
        public final Converter<?, T> getConverter(TypeDeclaration input) {
            DuplexConverter<Object, Object> elementConverter;
            TypeDeclaration collectionType = input.castAsType(Iterable.class);
            TypeDeclaration inputElementType = collectionType == null ? TypeDeclaration.OBJECT : collectionType.getGenericType(0);
            TypeDeclaration outputElementType = this.output.getGenericType(0);
            if (this.isOneWay()) {
                NullConverter converter;
                if (outputElementType.isAssignableFrom(inputElementType)) {
                    if (input.type.equals(this.output.type)) {
                        return new NullConverter(input, this.output);
                    }
                    converter = new NullConverter(inputElementType, outputElementType);
                } else {
                    converter = Conversion.find(inputElementType, outputElementType);
                    if (converter == null) {
                        return null;
                    }
                }
                elementConverter = DuplexConverter.pair(converter, new Converter<Object, Object>(outputElementType, inputElementType){

                    @Override
                    public Object convertInput(Object value) {
                        throw new UnsupportedOperationException("This collection converter only works one-way!");
                    }
                });
            } else if (inputElementType.equals(outputElementType)) {
                elementConverter = DuplexConverter.createNull(inputElementType);
            } else {
                elementConverter = Conversion.findDuplex(inputElementType, outputElementType);
                if (elementConverter == null) {
                    return null;
                }
            }
            return new ElementConverter(input, this.output, elementConverter);
        }

        private final class ElementConverter
        extends DuplexConverter<T, T> {
            private final DuplexConverter<Object, Object> elementConverter;
            private final boolean convertCollection;

            public ElementConverter(TypeDeclaration input, TypeDeclaration output, DuplexConverter<Object, Object> elementConverter) {
                super(input, output);
                this.elementConverter = elementConverter;
                this.convertCollection = !output.type.isAssignableFrom(input.type);
            }

            @Override
            public final T convertInput(T value) {
                if (this.convertCollection) {
                    if (value instanceof Iterable) {
                        value = CollectionConverter.this.change((Iterable<?>)value);
                    } else {
                        return null;
                    }
                }
                return CollectionConverter.this.create(value, this.elementConverter);
            }

            @Override
            public final T convertOutput(T value) {
                Object result = CollectionConverter.this.create(value, this.elementConverter.reverse());
                if (this.convertCollection) {
                    if (result instanceof Collection) {
                        result = CollectionConverter.this.change((Collection)result);
                    } else {
                        return null;
                    }
                }
                return result;
            }
        }
    }
}

