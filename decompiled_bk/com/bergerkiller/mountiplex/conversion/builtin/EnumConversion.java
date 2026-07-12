/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnumConversion {
    public static void register() {
        Conversion.registerProvider(new ConverterProvider(){

            @Override
            public void getConverters(TypeDeclaration outputType, List<Converter<?, ?>> converters) {
                if (!outputType.isInstanceOf(TypeDeclaration.ENUM)) {
                    return;
                }
                if (outputType.type != null && !outputType.type.isEnum() && outputType.type.getSuperclass().isEnum()) {
                    outputType = outputType.getSuperType();
                }
                final EnumStringCache cache = new EnumStringCache(outputType.type);
                converters.add(new Converter<Number, Enum>(this, TypeDeclaration.fromClass(Number.class), outputType){
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = this$0;
                        super(input, output);
                    }

                    @Override
                    public Enum convertInput(Number value) {
                        int idx = value.intValue();
                        if (idx >= 0 && idx < cache.getConstants().length) {
                            return cache.getConstants()[idx];
                        }
                        return null;
                    }
                });
                converters.add(new Converter<String, Enum>(this, TypeDeclaration.fromClass(String.class), outputType){
                    final /* synthetic */ 1 this$0;
                    {
                        this.this$0 = this$0;
                        super(input, output);
                    }

                    @Override
                    public Enum convertInput(String value) {
                        return cache.get(value);
                    }
                });
            }
        });
    }

    private static class EnumStringCache {
        private final Map<String, Enum> values = new HashMap<String, Enum>();
        private final Class<?> _type;
        private Enum<?>[] _constants;

        public EnumStringCache(Class<?> type) {
            this._type = type;
            this._constants = null;
        }

        public Enum<?>[] getConstants() {
            if (this._constants == null) {
                this._constants = (Enum[])this._type.getEnumConstants();
                if (this._constants == null) {
                    this._constants = (Enum[])MountiplexUtil.createArray(this._type, 0);
                }
                for (Enum<?> constant : this._constants) {
                    String name = constant.name();
                    this.values.put(name.toLowerCase(Locale.ENGLISH), constant);
                    this.values.put(name.toUpperCase(Locale.ENGLISH), constant);
                    this.values.put(name, constant);
                }
            }
            return this._constants;
        }

        public Enum get(String key) {
            Enum result = this.values.get(key);
            if (result == null && (result = (Enum)MountiplexUtil.parseArray(this.getConstants(), key, null)) != null) {
                this.values.put(key, result);
            }
            return result;
        }
    }
}

