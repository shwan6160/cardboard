/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.builtin;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingEntry;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.Map;

public class EntryConverter<K, V>
extends DuplexConverter<Map.Entry<?, ?>, Map.Entry<K, V>> {
    private final DuplexConverter<Object, K> keyConverter;
    private final DuplexConverter<Object, V> valueConverter;

    protected EntryConverter(DuplexConverter<?, K> keyConverter, DuplexConverter<?, V> valueConverter) {
        super(EntryConverter.entryType(keyConverter.input, valueConverter.input), EntryConverter.entryType(valueConverter.output, valueConverter.output));
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public Map.Entry<K, V> convertInput(Map.Entry<?, ?> value) {
        return new ConvertingEntry<K, V>(value, this.keyConverter, this.valueConverter);
    }

    @Override
    public Map.Entry<?, ?> convertOutput(Map.Entry<K, V> value) {
        return new ConvertingEntry<Object, Object>(value, this.keyConverter.reverse(), this.valueConverter.reverse());
    }

    private static TypeDeclaration entryType(TypeDeclaration keyType, TypeDeclaration valueType) {
        return TypeDeclaration.fromClass(Map.Entry.class).setGenericTypes(keyType, valueType);
    }

    public static <K, V> EntryConverter<K, V> create(DuplexConverter<?, K> keyConverter, DuplexConverter<?, V> valueConverter) {
        return new EntryConverter<K, V>(keyConverter, valueConverter);
    }
}

