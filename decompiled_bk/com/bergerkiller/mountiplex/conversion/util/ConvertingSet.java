/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.util;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingCollection;
import java.util.Set;

public class ConvertingSet<T>
extends ConvertingCollection<T>
implements Set<T> {
    public ConvertingSet(Set<?> collection, DuplexConverter<?, T> converterPair) {
        super(collection, converterPair);
    }
}

