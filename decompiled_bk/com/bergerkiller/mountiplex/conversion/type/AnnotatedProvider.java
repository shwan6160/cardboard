/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;

public class AnnotatedProvider
implements ConverterProvider {
    public final Method method;

    public AnnotatedProvider(Method method) {
        this.method = method;
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Converter provider method is not static");
        }
        Class<?>[] types = method.getParameterTypes();
        if (types.length != 2 || !types[0].equals(TypeDeclaration.class) || !types[1].equals(List.class)) {
            throw new IllegalArgumentException("Converter provider method does not have the expected method signature");
        }
    }

    @Override
    public void getConverters(TypeDeclaration output, List<Converter<?, ?>> converters) {
        try {
            this.method.invoke(null, output, converters);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to get suitable converters for " + output, t);
        }
    }
}

