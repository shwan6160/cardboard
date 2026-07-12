/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion.type;

import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.conversion.type.RawConverter;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class AnnotatedConverter
extends RawConverter {
    public Invoker<Object> invoker;
    public final boolean isUpcast;
    private final boolean nullInput;
    private final int cost;

    public AnnotatedConverter(MethodDeclaration method, TypeDeclaration input, TypeDeclaration output) {
        this(method, null, input, output, false);
    }

    public AnnotatedConverter(MethodDeclaration method, TypeDeclaration input, TypeDeclaration output, boolean isUpcast) {
        this(method, null, input, output, isUpcast);
    }

    private AnnotatedConverter(MethodDeclaration method, Invoker<Object> invoker, TypeDeclaration input, TypeDeclaration output, boolean isUpcast) {
        super(input, output);
        this.invoker = invoker == null ? InitInvoker.forMethod((Object)this, "invoker", method) : InitInvoker.proxy((Object)this, "invoker", invoker);
        this.isUpcast = isUpcast;
        ConverterMethod annot = method.method == null ? null : method.method.getAnnotation(ConverterMethod.class);
        this.nullInput = annot != null && annot.acceptsNull();
        this.cost = annot == null ? 1 : annot.cost();
    }

    @Override
    public Object convertInput(Object value) {
        Object result = this.invoker.invoke(null, value);
        if (!this.isUpcast || this.output.isAssignableFrom(result)) {
            return result;
        }
        return null;
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public boolean acceptsNullInput() {
        return this.nullInput;
    }

    public static TypeDeclaration parseType(Method method, boolean input) {
        String typeStr;
        ClassResolver resolver = ClassResolver.DEFAULT;
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Method is not static");
        }
        if (method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException("Method has no return type");
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalArgumentException("Method does not have one parameter");
        }
        method.setAccessible(true);
        ConverterMethod annot = method.getAnnotation(ConverterMethod.class);
        String string = annot == null ? "" : (typeStr = input ? annot.input() : annot.output());
        if (typeStr.length() > 0) {
            TypeDeclaration type = TypeDeclaration.parse(resolver, typeStr);
            if (!type.isValid()) {
                throw new IllegalArgumentException("Type is invalid: " + type.toString());
            }
            if (!type.isResolved()) {
                if (annot.optional()) {
                    return null;
                }
                throw new IllegalArgumentException("Type could not be resolved: " + type.toString());
            }
            return type;
        }
        return TypeDeclaration.fromType(resolver, input ? method.getGenericParameterTypes()[0] : method.getGenericReturnType());
    }

    public static class GenericProvider
    implements ConverterProvider {
        public final TypeDeclaration input;
        public final TypeDeclaration output;
        public final MethodDeclaration method;
        public Invoker<Object> invoker;

        public GenericProvider(MethodDeclaration method, TypeDeclaration input, TypeDeclaration output) {
            this.input = input;
            this.output = output;
            this.method = method;
            this.invoker = InitInvoker.forMethod((Object)this, "invoker", method);
        }

        @Override
        public void getConverters(TypeDeclaration output, List<Converter<?, ?>> converters) {
            TypeDeclaration[] inTypes;
            TypeDeclaration relOutput;
            boolean isUpcast = false;
            if (this.output.variableName != null || this.output.isWildcard) {
                isUpcast = true;
                if (!this.output.type.isAssignableFrom(output.type)) {
                    return;
                }
                relOutput = output.castAsType(this.output.type);
            } else {
                if (!output.type.equals(this.output.type)) {
                    return;
                }
                relOutput = output;
            }
            if (relOutput.genericTypes.length == 0) {
                inTypes = new TypeDeclaration[]{};
            } else if (relOutput.isInstanceOf(this.output) && this.output.genericTypes.length == relOutput.genericTypes.length) {
                inTypes = (TypeDeclaration[])this.input.genericTypes.clone();
                for (int i = 0; i < inTypes.length; ++i) {
                    String typeName = inTypes[i].variableName;
                    if (typeName == null) continue;
                    boolean found = false;
                    for (int j = 0; j < this.output.genericTypes.length; ++j) {
                        if (!typeName.equals(this.output.genericTypes[i].variableName)) continue;
                        inTypes[i] = relOutput.genericTypes[i];
                        found = true;
                        break;
                    }
                    if (found) continue;
                    return;
                }
            } else {
                return;
            }
            converters.add(new AnnotatedConverter(this.method, this.invoker, this.input.setGenericTypes(inTypes), output, isUpcast));
        }
    }
}

