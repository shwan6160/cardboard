/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DisabledConverter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.type.LazyConverter;
import com.bergerkiller.mountiplex.conversion.util.ParamsConverterList;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.IgnoredFieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ConstructorDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.NameDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TemplateClassBuilder;
import com.bergerkiller.mountiplex.reflection.declarations.TemplateHandleBuilder;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.NullInstantiator;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.ClassFieldCopier;
import com.bergerkiller.mountiplex.reflection.util.fast.InitInvoker;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.logging.Level;

public class Template {

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface ImportList {
        public Import[] value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Repeatable(value=ImportList.class)
    public static @interface Import {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Package {
        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Readonly {
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Optional {
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    public static @interface RequirementsList {
        public Require[] value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.TYPE})
    @Repeatable(value=RequirementsList.class)
    public static @interface Require {
        public String declaring() default "";

        public String value();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Generated {
        public String value() default "";
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface InstanceType {
        public String value();
    }

    public static class Field<T>
    extends AbstractField<T> {
        public final T get(Object instance) {
            return this.field.get(instance);
        }

        public final void set(Object instance, T value) {
            this.field.set(instance, value);
        }

        public void copy(Object instanceFrom, Object instanceTo) {
            this.field.copy(instanceFrom, instanceTo);
        }

        public static final class Boolean
        extends Field<java.lang.Boolean> {
            public final boolean getBoolean(Object instance) {
                return this.field.getBoolean(instance);
            }

            public final void setBoolean(Object instance, boolean value) {
                this.field.setBoolean(instance, value);
            }
        }

        public static final class Character
        extends Field<java.lang.Character> {
            public final char getCharacter(Object instance) {
                return this.field.getCharacter(instance);
            }

            public final void setCharacter(Object instance, char value) {
                this.field.setCharacter(instance, value);
            }
        }

        public static final class Long
        extends Field<java.lang.Long> {
            public final long getLong(Object instance) {
                return this.field.getLong(instance);
            }

            public final void setLong(Object instance, long value) {
                this.field.setLong(instance, value);
            }
        }

        public static final class Integer
        extends Field<java.lang.Integer> {
            public final int getInteger(Object instance) {
                return this.field.getInteger(instance);
            }

            public final void setInteger(Object instance, int value) {
                this.field.setInteger(instance, value);
            }
        }

        public static final class Short
        extends Field<java.lang.Short> {
            public final short getShort(Object instance) {
                return this.field.getShort(instance);
            }

            public final void setShort(Object instance, short value) {
                this.field.setShort(instance, value);
            }
        }

        public static final class Byte
        extends Field<java.lang.Byte> {
            public final byte getByte(Object instance) {
                return this.field.getByte(instance);
            }

            public final void setByte(Object instance, byte value) {
                this.field.setByte(instance, value);
            }
        }

        public static final class Float
        extends Field<java.lang.Float> {
            public final float getFloat(Object instance) {
                return this.field.getFloat(instance);
            }

            public final void setFloat(Object instance, float value) {
                this.field.setFloat(instance, value);
            }
        }

        public static final class Double
        extends Field<java.lang.Double> {
            public final double getDouble(Object instance) {
                return this.field.getDouble(instance);
            }

            public final void setDouble(Object instance, double value) {
                this.field.setDouble(instance, value);
            }
        }

        public static final class Converted<T>
        extends AbstractFieldConverter<Field<Object>, T> {
            public Converted() {
                super(new Field());
            }

            public final T get(Object instance) {
                Object rawValue = ((Field)this.raw).get(instance);
                return (T)this.converter.converter.convert(rawValue);
            }

            public final void set(Object instance, T value) {
                Object rawValue = this.reverse.converter.convert(value);
                ((Field)this.raw).set(instance, rawValue);
            }

            public final void copy(Object instanceFrom, Object instanceTo) {
                ((Field)this.raw).copy(instanceFrom, instanceTo);
            }

            @Override
            public final Field<Object> raw() {
                return (Field)this.raw;
            }
        }
    }

    public static class StaticField<T>
    extends AbstractField<T> {
        public final T getSafe() {
            if (!this._hasClass) {
                return null;
            }
            try {
                return this.get();
            }
            catch (RuntimeException ex) {
                return this.failGetSafe(ex, null);
            }
        }

        public final T get() {
            return this.field.get(null);
        }

        public final void set(T value) {
            this.field.set(null, value);
        }

        public static final class Boolean
        extends StaticField<java.lang.Boolean> {
            public final boolean getBooleanSafe() {
                if (!this._hasClass) {
                    return false;
                }
                try {
                    return this.getBoolean();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, false);
                }
            }

            public final boolean getBoolean() {
                return this.field.getBoolean(null);
            }

            public final void setBoolean(boolean value) {
                this.field.setBoolean(null, value);
            }
        }

        public static final class Character
        extends StaticField<java.lang.Character> {
            public final char getCharacterSafe() {
                if (!this._hasClass) {
                    return '\u0000';
                }
                try {
                    return this.getCharacter();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, java.lang.Character.valueOf('\u0000')).charValue();
                }
            }

            public final char getCharacter() {
                return this.field.getCharacter(null);
            }

            public final void setCharacter(char value) {
                this.field.setCharacter(null, value);
            }
        }

        public static final class Long
        extends StaticField<java.lang.Long> {
            public final long getLongSafe() {
                if (!this._hasClass) {
                    return 0L;
                }
                try {
                    return this.getLong();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, 0L);
                }
            }

            public final long getLong() {
                return this.field.getLong(null);
            }

            public final void setLong(long value) {
                this.field.setLong(null, value);
            }
        }

        public static final class Integer
        extends StaticField<java.lang.Integer> {
            public final int getIntegerSafe() {
                if (!this._hasClass) {
                    return 0;
                }
                try {
                    return this.getInteger();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, 0);
                }
            }

            public final int getInteger() {
                return this.field.getInteger(null);
            }

            public final void setInteger(int value) {
                this.field.setInteger(null, value);
            }
        }

        public static final class Short
        extends StaticField<java.lang.Short> {
            public final short getShortSafe() {
                if (!this._hasClass) {
                    return 0;
                }
                try {
                    return this.getShort();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, (short)0);
                }
            }

            public final short getShort() {
                return this.field.getShort(null);
            }

            public final void setShort(short value) {
                this.field.setShort(null, value);
            }
        }

        public static final class Byte
        extends StaticField<java.lang.Byte> {
            public final byte getByteSafe() {
                if (!this._hasClass) {
                    return 0;
                }
                try {
                    return this.getByte();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, (byte)0);
                }
            }

            public final byte getByte() {
                return this.field.getByte(null);
            }

            public final void setByte(byte value) {
                this.field.setByte(null, value);
            }
        }

        public static final class Float
        extends StaticField<java.lang.Float> {
            public final float getFloatSafe() {
                if (!this._hasClass) {
                    return 0.0f;
                }
                try {
                    return this.getFloat();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, java.lang.Float.valueOf(0.0f)).floatValue();
                }
            }

            public final float getFloat() {
                return this.field.getFloat(null);
            }

            public final void setFloat(float value) {
                this.field.setFloat(null, value);
            }
        }

        public static final class Double
        extends StaticField<java.lang.Double> {
            public final double getDoubleSafe() {
                if (!this._hasClass) {
                    return 0.0;
                }
                try {
                    return this.getDouble();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, 0.0);
                }
            }

            public final double getDouble() {
                return this.field.getDouble(null);
            }

            public final void setDouble(double value) {
                this.field.setDouble(null, value);
            }
        }

        public static final class Converted<T>
        extends AbstractFieldConverter<StaticField<Object>, T> {
            public Converted() {
                super(new StaticField());
            }

            public final T getSafe() {
                if (!this._hasClass) {
                    return null;
                }
                try {
                    return this.get();
                }
                catch (RuntimeException ex) {
                    return ((StaticField)this.raw).failGetSafe(ex, null);
                }
            }

            public final T get() {
                Object value = ((StaticField)this.raw).get();
                return (T)this.converter.converter.convert(value);
            }

            public final void set(T value) {
                Object rawValue = this.reverse.converter.convert(value);
                ((StaticField)this.raw).set(rawValue);
            }

            @Override
            public final StaticField<Object> raw() {
                return (StaticField)this.raw;
            }
        }
    }

    public static class EnumConstant<T extends Enum<?>>
    extends TemplateElement<FieldDeclaration> {
        protected T constant;
        private String constantName = null;

        public final T getSafe() {
            if (!this._hasClass) {
                return null;
            }
            try {
                return this.get();
            }
            catch (RuntimeException ex) {
                return (T)((Enum)this.failGetSafe(ex, null));
            }
        }

        public final T get() {
            if (this.constant == null) {
                if (this.constantName == null) {
                    throw new UnsupportedOperationException("Enumeration constant not initialized");
                }
                throw new UnsupportedOperationException("Enumeration constant " + this.constantName + " is not available");
            }
            return this.constant;
        }

        @Override
        protected FieldDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
            if (dec == null) {
                throw new IllegalArgumentException("ClassDeclaration is null");
            }
            this.constantName = name;
            for (FieldDeclaration fDec : dec.fields) {
                if (!fDec.isEnum || !fDec.name.real().equals(name)) continue;
                if (dec.type.type == null) {
                    this.initFail("Enumeration constant " + name + " in class " + dec.type + " not initialized: class not found");
                    return null;
                }
                for (Object enumConstant : dec.type.type.getEnumConstants()) {
                    if (!((Enum)enumConstant).name().equals(fDec.name.value())) continue;
                    this.constant = (Enum)enumConstant;
                    return fDec;
                }
                this.initFail("Enumeration constant " + name + " missing in class " + dec.type);
                return null;
            }
            this.initFail("Failed to find enumeration field " + name + " in class " + dec.type);
            return null;
        }

        @Override
        public void forceInitialization() {
            this.get();
        }

        @Override
        public boolean isAvailable() {
            return this.constant != null;
        }

        public static final class Converted<T>
        extends TemplateElement<FieldDeclaration> {
            public final EnumConstant<Enum<?>> raw = new EnumConstant();
            protected DuplexConverter<?, T> converter = null;

            public final T get() {
                Enum<?> value = this.raw.get();
                try {
                    return (T)this.converter.convert(value);
                }
                catch (RuntimeException ex) {
                    this.failNoConverter();
                    throw ex;
                }
            }

            public final T getSafe() {
                if (!this._hasClass) {
                    return null;
                }
                try {
                    return this.get();
                }
                catch (RuntimeException ex) {
                    return this.failGetSafe(ex, null);
                }
            }

            protected final void failNoConverter() {
                if (this.converter == null) {
                    throw new UnsupportedOperationException("Enum constant converter was not found");
                }
            }

            @Override
            public void forceInitialization() {
                this.raw.forceInitialization();
                this.failNoConverter();
            }

            @Override
            protected void initElementName(String elementName) {
                super.initElementName(elementName);
                this.raw.initElementName(elementName);
            }

            @Override
            protected void initNoClass() {
                super.initNoClass();
                this.raw.initNoClass();
            }

            @Override
            protected FieldDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
                if (dec == null) {
                    throw new IllegalArgumentException("ClassDeclaration is null");
                }
                Declaration fDec = this.raw.init((Class)owner, dec, name);
                if (fDec != null) {
                    DuplexConverter<Object, ?> ownerConverter = owner.getHandleConverter();
                    if (((FieldDeclaration)fDec).type.equals(ownerConverter.input) && ((FieldDeclaration)fDec).type.cast.equals(ownerConverter.output)) {
                        this.converter = ownerConverter;
                    } else {
                        this.converter = Conversion.findDuplex(((FieldDeclaration)fDec).type, ((FieldDeclaration)fDec).type.cast);
                        if (this.converter == null) {
                            MountiplexUtil.LOGGER.warning("Converter for enum constant " + ((FieldDeclaration)fDec).name.toString() + " not found: " + ((FieldDeclaration)fDec).type.toString());
                        }
                    }
                }
                return fDec;
            }

            @Override
            protected void setOptional() {
                super.setOptional();
                this.raw.setOptional();
            }

            @Override
            public boolean isAvailable() {
                return this.raw.isAvailable();
            }
        }
    }

    public static final class Method<T>
    extends AbstractMethod<T> {
        public T invokeVA(Object instance, Object ... arguments) {
            return this.invoker.invokeVA(instance, arguments);
        }

        public T invoke(Object instance) {
            return this.invoker.invoke(instance);
        }

        public T invoke(Object instance, Object arg0) {
            return this.invoker.invoke(instance, arg0);
        }

        public T invoke(Object instance, Object arg0, Object arg1) {
            return this.invoker.invoke(instance, arg0, arg1);
        }

        public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
            return this.invoker.invoke(instance, arg0, arg1, arg2);
        }

        public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
            return this.invoker.invoke(instance, arg0, arg1, arg2, arg3);
        }

        public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
            return this.invoker.invoke(instance, arg0, arg1, arg2, arg3, arg4);
        }

        public static final class Converted<T>
        extends AbstractMethodConverter<Method<Object>, T> {
            public Converted() {
                super(new Method());
            }

            public final T invokeVA(Object instance, Object ... arguments) {
                ParamsConverterList converters = this.prepare(arguments.length);
                Object[] convertedArgs = converters.convertArgs(arguments);
                Object rawResult = ((Method)this.raw).invoker.invokeVA(instance, convertedArgs);
                return converters.convertResult(rawResult);
            }

            public T invoke(Object instance) {
                ParamsConverterList converters = this.prepare(0);
                return converters.convertResult(((Method)this.raw).invoker.invoke(instance));
            }

            public T invoke(Object instance, Object arg0) {
                ParamsConverterList converters = this.prepare(1);
                if (converters.args == null) {
                    return converters.convertResult(((Method)this.raw).invoker.invoke(instance, arg0));
                }
                return converters.convertResult(((Method)this.raw).invoker.invoke(instance, converters.arg0.apply(arg0)));
            }

            public T invoke(Object instance, Object arg0, Object arg1) {
                ParamsConverterList converters = this.prepare(2);
                if (converters.args == null) {
                    return converters.convertResult(((Method)this.raw).invoker.invoke(instance, arg0, arg1));
                }
                return converters.convertResult(((Method)this.raw).invoker.invoke(instance, converters.arg0.apply(arg0), converters.arg1.apply(arg1)));
            }

            public T invoke(Object instance, Object arg0, Object arg1, Object arg2) {
                ParamsConverterList converters = this.prepare(3);
                if (converters.args == null) {
                    return converters.convertResult(((Method)this.raw).invoker.invoke(instance, arg0, arg1, arg2));
                }
                return converters.convertResult(((Method)this.raw).invoker.invoke(instance, converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2)));
            }

            public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3) {
                ParamsConverterList converters = this.prepare(4);
                if (converters.args == null) {
                    return converters.convertResult(((Method)this.raw).invoker.invoke(instance, arg0, arg1, arg2, arg3));
                }
                return converters.convertResult(((Method)this.raw).invoker.invoke(instance, converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2), converters.arg3.apply(arg3)));
            }

            public T invoke(Object instance, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
                ParamsConverterList converters = this.prepare(5);
                if (converters.args == null) {
                    return converters.convertResult(((Method)this.raw).invoker.invoke(instance, arg0, arg1, arg2, arg3, arg4));
                }
                return converters.convertResult(((Method)this.raw).invoker.invoke(instance, converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2), converters.arg3.apply(arg3), converters.arg4.apply(arg4)));
            }

            @Override
            public final Method<Object> raw() {
                return (Method)this.raw;
            }
        }
    }

    public static final class StaticMethod<T>
    extends AbstractMethod<T> {
        public T invokeVA(Object ... arguments) {
            return this.invoker.invokeVA(null, arguments);
        }

        public T invoke() {
            return this.invoker.invoke(null);
        }

        public T invoke(Object arg0) {
            return this.invoker.invoke(null, arg0);
        }

        public T invoke(Object arg0, Object arg1) {
            return this.invoker.invoke(null, arg0, arg1);
        }

        public T invoke(Object arg0, Object arg1, Object arg2) {
            return this.invoker.invoke(null, arg0, arg1, arg2);
        }

        public T invoke(Object arg0, Object arg1, Object arg2, Object arg3) {
            return this.invoker.invoke(null, arg0, arg1, arg2, arg3);
        }

        public T invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
            return this.invoker.invoke(null, arg0, arg1, arg2, arg3, arg4);
        }

        public static final class Converted<T>
        extends AbstractMethodConverter<StaticMethod<Object>, T> {
            public Converted() {
                super(new StaticMethod());
            }

            public final T invokeVA(Object ... arguments) {
                ParamsConverterList converters = this.prepare(arguments.length);
                Object[] convertedArgs = converters.convertArgs(arguments);
                Object rawResult = ((StaticMethod)this.raw).invoker.invokeVA(null, convertedArgs);
                return converters.convertResult(rawResult);
            }

            public final T invoke() {
                ParamsConverterList converters = this.prepare(0);
                return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null));
            }

            public final T invoke(Object arg0) {
                ParamsConverterList converters = this.prepare(1);
                if (converters.args == null) {
                    return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, arg0));
                }
                ((StaticMethod)this.raw).invoker.initializeInvoker();
                return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, converters.arg0.apply(arg0)));
            }

            public final T invoke(Object arg0, Object arg1) {
                ParamsConverterList converters = this.prepare(2);
                if (converters.args == null) {
                    return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, arg0, arg1));
                }
                return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, converters.arg0.apply(arg0), converters.arg1.apply(arg1)));
            }

            public final T invoke(Object arg0, Object arg1, Object arg2) {
                ParamsConverterList converters = this.prepare(3);
                if (converters.args == null) {
                    return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, arg0, arg1, arg2));
                }
                return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2)));
            }

            public final T invoke(Object arg0, Object arg1, Object arg2, Object arg3) {
                ParamsConverterList converters = this.prepare(4);
                if (converters.args == null) {
                    return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, arg0, arg1, arg2, arg3));
                }
                return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2), converters.arg3.apply(arg3)));
            }

            public final T invoke(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
                ParamsConverterList converters = this.prepare(5);
                if (converters.args == null) {
                    return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, arg0, arg1, arg2, arg3, arg4));
                }
                return converters.convertResult(((StaticMethod)this.raw).invoker.invoke(null, converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2), converters.arg3.apply(arg3), converters.arg4.apply(arg4)));
            }

            @Override
            public final StaticMethod<Object> raw() {
                return (StaticMethod)this.raw;
            }
        }
    }

    public static final class Constructor<T>
    extends TemplateElement<ConstructorDeclaration> {
        protected final FastConstructor<T> constructor = new FastConstructor();

        public T newInstanceVA(Object ... arguments) {
            return this.constructor.newInstanceVA(arguments);
        }

        public T newInstance() {
            return this.constructor.newInstance();
        }

        public T newInstance(Object arg0) {
            return this.constructor.newInstance(arg0);
        }

        public T newInstance(Object arg0, Object arg1) {
            return this.constructor.newInstance(arg0, arg1);
        }

        public T newInstance(Object arg0, Object arg1, Object arg2) {
            return this.constructor.newInstance(arg0, arg1, arg2);
        }

        public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3) {
            return this.constructor.newInstance(arg0, arg1, arg2, arg3);
        }

        public T newInstance(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
            return this.constructor.newInstance(arg0, arg1, arg2, arg3, arg4);
        }

        @Override
        public void forceInitialization() {
            this.constructor.forceInitialization();
        }

        @Override
        public boolean isAvailable() {
            return this.constructor.isAvailable();
        }

        @Override
        protected final void failNotFound() {
            if (!this.constructor.isAvailable()) {
                throw new RuntimeException("Constructor not found");
            }
        }

        protected final void failInvalidArgs(Object[] arguments) {
            this.failInvalidArgs(this.constructor.getConstructor().getParameterTypes(), arguments);
        }

        @Override
        protected ConstructorDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
            if (dec == null) {
                throw new IllegalArgumentException("ClassDeclaration is null");
            }
            for (ConstructorDeclaration cDec : dec.constructors) {
                if (cDec.constructor == null || !cDec.getName().equals(name)) continue;
                try {
                    this.constructor.init(cDec);
                    return cDec;
                }
                catch (Throwable t) {
                    MountiplexUtil.LOGGER.warning("Method '" + name + "' in template for " + dec.type.typePath + " not accessible");
                    return null;
                }
            }
            this.initFail("Constructor '" + name + "' not found in template for " + dec.type.typePath);
            return null;
        }

        public static final class Converted<T>
        extends AbstractParamsConverter<Constructor<Object>, T, ConstructorDeclaration> {
            public Converted() {
                super(new Constructor());
            }

            @Override
            protected ConstructorDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
                if (dec == null) {
                    throw new IllegalArgumentException("ClassDeclaration is null");
                }
                Declaration cDec = ((Constructor)this.raw).init((Class)owner, dec, name);
                if (cDec != null) {
                    this.initConverters("constructor " + ((ConstructorDeclaration)cDec).parameters.toString(), ((ConstructorDeclaration)cDec).type, ((ConstructorDeclaration)cDec).parameters);
                }
                return cDec;
            }

            public final T newInstanceVA(Object ... arguments) {
                ParamsConverterList converters = this.prepare(arguments.length);
                Object[] convertedArgs = converters.convertArgs(arguments);
                Object rawResult = ((Constructor)this.raw).newInstanceVA(convertedArgs);
                return converters.convertResult(rawResult);
            }

            public final T newInstance() {
                ParamsConverterList converters = this.prepare(0);
                return converters.convertResult(((Constructor)this.raw).newInstance());
            }

            public final T newInstance(Object arg0) {
                ParamsConverterList converters = this.prepare(1);
                if (converters.args == null) {
                    return converters.convertResult(((Constructor)this.raw).newInstance(arg0));
                }
                return converters.convertResult(((Constructor)this.raw).newInstance(converters.arg0.apply(arg0)));
            }

            public final T newInstance(Object arg0, Object arg1) {
                ParamsConverterList converters = this.prepare(2);
                if (converters.args == null) {
                    return converters.convertResult(((Constructor)this.raw).newInstance(arg0, arg1));
                }
                return converters.convertResult(((Constructor)this.raw).newInstance(converters.arg0.apply(arg0), converters.arg1.apply(arg1)));
            }

            public final T newInstance(Object arg0, Object arg1, Object arg2) {
                ParamsConverterList converters = this.prepare(3);
                if (converters.args == null) {
                    return converters.convertResult(((Constructor)this.raw).newInstance(arg0, arg1, arg2));
                }
                return converters.convertResult(((Constructor)this.raw).newInstance(converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2)));
            }

            public final T newInstance(Object arg0, Object arg1, Object arg2, Object arg3) {
                ParamsConverterList converters = this.prepare(4);
                if (converters.args == null) {
                    return converters.convertResult(((Constructor)this.raw).newInstance(arg0, arg1, arg2, arg3));
                }
                return converters.convertResult(((Constructor)this.raw).newInstance(converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2), converters.arg3.apply(arg3)));
            }

            public final T newInstance(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
                ParamsConverterList converters = this.prepare(5);
                if (converters.args == null) {
                    return converters.convertResult(((Constructor)this.raw).newInstance(arg0, arg1, arg2, arg3, arg4));
                }
                return converters.convertResult(((Constructor)this.raw).newInstance(converters.arg0.apply(arg0), converters.arg1.apply(arg1), converters.arg2.apply(arg2), converters.arg3.apply(arg3), converters.arg4.apply(arg4)));
            }
        }
    }

    public static class AbstractMethodConverter<M extends AbstractMethod<?>, T>
    extends AbstractParamsConverter<M, T, MethodDeclaration> {
        protected AbstractMethodConverter(M raw) {
            super(raw);
        }

        @Override
        protected MethodDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
            if (dec == null) {
                throw new IllegalArgumentException("ClassDeclaration is null");
            }
            Declaration mDec = ((AbstractMethod)this.raw).init((Class)owner, dec, name);
            if (mDec != null) {
                this.initConverters("method " + ((MethodDeclaration)mDec).name.toString(), ((MethodDeclaration)mDec).returnType, ((MethodDeclaration)mDec).parameters);
            }
            return mDec;
        }
    }

    public static abstract class AbstractParamsConverter<R extends TemplateElement<?>, T, D extends Declaration>
    extends TemplateElement<D> {
        public final R raw;
        protected ParamsConverterList.Supplier<T> converters = ParamsConverterList.Supplier.uninitialized();
        protected int argCount = -1;

        protected AbstractParamsConverter(R raw) {
            this.raw = raw;
        }

        @Override
        public void forceInitialization() {
            this.raw.forceInitialization();
            this.converters.get();
        }

        @Override
        protected void initElementName(String elementName) {
            super.initElementName(elementName);
            ((TemplateElement)this.raw).initElementName(elementName);
        }

        protected final void initConverters(String name, TypeDeclaration returnType, ParameterListDeclaration parameters) {
            this.argCount = parameters.parameters.length;
            this.converters = ParamsConverterList.Supplier.lazy(() -> {
                ParamsConverterList list = new ParamsConverterList(name, returnType, parameters);
                if (list.valid) {
                    this.converters = ParamsConverterList.Supplier.of(list);
                } else {
                    this.argCount = -1;
                    this.converters = ParamsConverterList.Supplier.unsupported("Method converters for " + this.getElementName() + " could not be initialized");
                }
                return this.converters;
            });
        }

        @Override
        protected void setOptional() {
            super.setOptional();
            ((TemplateElement)this.raw).setOptional();
        }

        @Override
        public boolean isAvailable() {
            return ((TemplateElement)this.raw).isAvailable();
        }

        protected final ParamsConverterList<T> prepare(int argCount) {
            if (this.argCount != argCount) {
                if (!this.converters.available()) {
                    ((TemplateElement)this.raw).failNotFound();
                    this.converters.get();
                }
                throw new IllegalArgumentException("Invalid number of arguments (" + this.argCount + " expected, but got " + argCount + ")");
            }
            return this.converters.get();
        }

        public R raw() {
            return this.raw;
        }
    }

    public static class AbstractFieldConverter<F extends AbstractField<?>, T>
    extends TemplateElement<FieldDeclaration> {
        public final F raw;
        protected LazyConverter<?, T> converter = LazyConverter.uninitialized();
        protected LazyConverter<T, ?> reverse = LazyConverter.uninitialized();

        protected AbstractFieldConverter(F raw) {
            this.raw = raw;
        }

        @Override
        public void forceInitialization() {
            ((AbstractField)this.raw).forceInitialization();
            if (!this.converter.isAvailable()) {
                throw new UnsupportedOperationException("Field get() converter for " + this.getElementName() + " was not found");
            }
            if (!this.reverse.isAvailable()) {
                throw new UnsupportedOperationException("Field set() converter for " + this.getElementName() + " was not found");
            }
        }

        @Override
        protected void initElementName(String elementName) {
            super.initElementName(elementName);
            ((AbstractField)this.raw).initElementName(elementName);
        }

        private void initFailConverter(NameDeclaration name, TypeDeclaration input, TypeDeclaration output) {
            this.initFail("Converter for field " + name.toString() + " not found: " + input.toString(true) + " -> " + output.toString(true));
        }

        @Override
        protected FieldDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
            if (dec == null) {
                throw new IllegalArgumentException("ClassDeclaration is null");
            }
            Declaration fDec = ((AbstractField)this.raw).init((Class)owner, dec, name);
            if (fDec != null) {
                if (this.isReadonly()) {
                    this.converter = LazyConverter.create(((FieldDeclaration)fDec).type, ((FieldDeclaration)fDec).type.cast);
                    this.reverse = LazyConverter.of(new DisabledConverter(((FieldDeclaration)fDec).type.cast, ((FieldDeclaration)fDec).type, "Field " + name + " is readonly"));
                } else {
                    this.converter = LazyConverter.create(((FieldDeclaration)fDec).type, ((FieldDeclaration)fDec).type.cast);
                    this.reverse = LazyConverter.create(((FieldDeclaration)fDec).type.cast, ((FieldDeclaration)fDec).type);
                }
                NameDeclaration fName = ((FieldDeclaration)fDec).name;
                this.converter.whenFailing((input, output) -> this.initFailConverter(fName, input, output));
                this.reverse.whenFailing((input, output) -> this.initFailConverter(fName, input, output));
            }
            return fDec;
        }

        @Override
        public boolean isAvailable() {
            return ((AbstractField)this.raw).isAvailable() && this.converter.isAvailable() && this.reverse.isAvailable();
        }

        @Override
        protected void setOptional() {
            super.setOptional();
            ((TemplateElement)this.raw).setOptional();
        }

        public TranslatorFieldAccessor<T> toFieldAccessor() {
            DuplexConverter<Object, Object> converter = this.isAvailable() ? DuplexConverter.pair(this.converter, this.reverse) : DuplexConverter.createNull(TypeDeclaration.fromClass(Object.class));
            return new TranslatorFieldAccessor(((AbstractField)this.raw).toFieldAccessor(), converter);
        }

        public F raw() {
            return this.raw;
        }
    }

    public static class AbstractMethod<T>
    extends TemplateElement<MethodDeclaration> {
        private MethodDeclaration method = null;
        public Invoker<T> invoker = InitInvoker.unavailableMethod();

        @Override
        protected final void failNotFound() {
            if (this.method == null) {
                throw new RuntimeException("Method " + this.getElementName() + " not found");
            }
        }

        @Override
        protected MethodDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
            if (dec == null) {
                throw new IllegalArgumentException("ClassDeclaration is null");
            }
            for (MethodDeclaration methodDec : dec.methods) {
                if (methodDec.constructor == null && methodDec.method == null && methodDec.body == null || !methodDec.name.real().equals(name)) continue;
                if (!methodDec.isResolved()) {
                    this.initFail("Method '" + name + "' has an unresolved declaration: " + methodDec);
                    return null;
                }
                this.method = methodDec;
                this.invoker = InitInvoker.forMethod((Object)this, "invoker", methodDec);
                return methodDec;
            }
            this.initFail("Method '" + name + "' not found in template for " + dec.type.typePath);
            return null;
        }

        @Override
        public void forceInitialization() {
            this.invoker.forceInitialization();
            this.failNotFound();
        }

        @Override
        protected void initElementName(String elementName) {
            super.initElementName(elementName);
            this.method = null;
            this.invoker = InitInvoker.unavailable("method", elementName);
        }

        @Override
        public boolean isAvailable() {
            return this.method != null;
        }

        public <R> MethodAccessor<R> toMethodAccessor() {
            FastMethod<T> fast = new FastMethod<T>();
            fast.init(this.method, this.invoker);
            return new SafeMethod(fast);
        }

        public java.lang.reflect.Method toJavaMethod() {
            return this.method == null ? null : this.method.method;
        }
    }

    public static class AbstractField<T>
    extends TemplateElement<FieldDeclaration> {
        protected final FastField<T> field = new FastField();

        @Override
        protected void failNotFound() {
            if (!this.field.isAvailable()) {
                throw new RuntimeException("Field " + this.getElementName() + " not found");
            }
        }

        @Override
        protected FieldDeclaration init(Class<?> owner, ClassDeclaration dec, String name) {
            if (dec == null) {
                throw new IllegalArgumentException("ClassDeclaration is null");
            }
            for (FieldDeclaration fieldDec : dec.fields) {
                if (fieldDec.field == null || !fieldDec.name.real().equals(name)) continue;
                if (!fieldDec.isResolved()) {
                    this.initFail("Field '" + name + "' has an unresolved declaration: " + fieldDec);
                    return null;
                }
                this.field.init(fieldDec.field);
                return fieldDec;
            }
            this.initFail("Field '" + name + "' not found in template for " + dec.type.typePath);
            return null;
        }

        @Override
        public void forceInitialization() {
            this.field.forceInitialization();
        }

        @Override
        protected void initElementName(String elementName) {
            super.initElementName(elementName);
            this.field.initUnavailable(elementName);
        }

        @Override
        public boolean isAvailable() {
            return this.field.getField() != null;
        }

        public FieldAccessor<T> toFieldAccessor() {
            if (this.isAvailable()) {
                return new SafeField<T>(this.field);
            }
            return new FieldAccessor<T>(){

                @Override
                public boolean isValid() {
                    return false;
                }

                private UnsupportedOperationException fail() {
                    return new UnsupportedOperationException("Field " + field.getDescription() + " is not available");
                }

                @Override
                public T get(Object instance) {
                    throw this.fail();
                }

                @Override
                public boolean set(Object instance, T value) {
                    throw this.fail();
                }

                @Override
                public T transfer(Object from, Object to) {
                    throw this.fail();
                }

                @Override
                public <K> TranslatorFieldAccessor<K> translate(DuplexConverter<?, K> converterPair) {
                    return new TranslatorFieldAccessor<K>(this, converterPair);
                }

                @Override
                public FieldAccessor<T> ignoreInvalid(T defaultValue) {
                    return new IgnoredFieldAccessor(defaultValue);
                }
            };
        }
    }

    public static abstract class TemplateElement<T extends Declaration>
    implements LazyInitializedObject,
    IgnoresRemapping {
        private boolean _optional = false;
        private boolean _readonly = false;
        protected boolean _hasClass = true;
        private String _elementName = "!!UNKNOWN!!";

        protected void initNoClass() {
            this._hasClass = false;
        }

        protected abstract T init(Class<?> var1, ClassDeclaration var2, String var3);

        protected void initElementName(String elementName) {
            this._elementName = elementName;
        }

        public final String getElementName() {
            return this._elementName;
        }

        public abstract boolean isAvailable();

        protected void failNotFound() {
        }

        protected final <V> V failGetSafe(RuntimeException ex, V def) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to get static field value", ex);
            return def;
        }

        protected final void failInvalidArgs(java.lang.Class<?>[] paramTypes, Object[] arguments) {
            if (paramTypes.length != arguments.length) {
                throw new IllegalArgumentException("Invalid number of argument specified! Expected " + paramTypes.length + " arguments, but got " + arguments.length);
            }
            for (int i = 0; i < paramTypes.length; ++i) {
                if (paramTypes[i].isPrimitive() && arguments[i] == null) {
                    throw new IllegalArgumentException("Null can not be assigned to primitive " + paramTypes[i].getName() + " argument [" + i + "]");
                }
                if (arguments[i] == null || BoxedType.tryBoxType(paramTypes[i]).isAssignableFrom(arguments[i].getClass())) continue;
                throw new IllegalArgumentException("Value of type " + MPLType.getName(arguments[i].getClass()) + " can not be assigned to " + MPLType.getName(paramTypes[i]) + " argument [" + i + "]");
            }
        }

        protected final void initFail(String message) {
            if (!this.isOptional()) {
                MountiplexUtil.LOGGER.warning(message);
            }
        }

        protected void setOptional() {
            this._optional = true;
        }

        protected void setReadonly() {
            this._readonly = true;
        }

        public boolean isOptional() {
            return this._optional;
        }

        public boolean isReadonly() {
            return this._readonly;
        }
    }

    public static final class DuplexHandleConverterReverse<H extends Handle>
    extends DuplexConverter<H, Object> {
        public final Class<H> handleClass;

        public DuplexHandleConverterReverse(DuplexHandleConverter<H> handleConverter) {
            super(handleConverter.output, handleConverter.input, handleConverter);
            this.handleClass = handleConverter.handleClass;
        }

        @Override
        public Object convertInput(H value) {
            return ((Handle)value).getRaw();
        }

        @Override
        public H convertOutput(Object value) {
            return this.handleClass.createHandle(value, false);
        }
    }

    public static final class DuplexHandleConverter<H extends Handle>
    extends DuplexConverter<Object, H> {
        public final Class<H> handleClass;

        public DuplexHandleConverter(Class<H> handleClass) {
            super(TypeDeclaration.fromClass(handleClass.getType()), TypeDeclaration.fromClass(handleClass.getHandleType()), null);
            this.handleClass = handleClass;
            this.reverse = new DuplexHandleConverterReverse(this);
        }

        @Override
        public H convertInput(Object value) {
            return this.handleClass.createHandle(value, false);
        }

        @Override
        public Object convertOutput(Handle value) {
            return value.getRaw();
        }
    }

    public static abstract class Handle
    implements IgnoresRemapping {
        public final boolean isInstanceOf(Class<?> type) {
            return this.isInstanceOf(type.getType());
        }

        public final boolean isInstanceOf(java.lang.Class<?> type) {
            return type != null && type.isAssignableFrom(this.getRaw().getClass());
        }

        public abstract Object getRaw();

        public static final Object getRaw(Handle handle) {
            return handle == null ? null : handle.getRaw();
        }

        public <T extends Handle> T cast(Class<T> type) {
            if (this.isInstanceOf(type)) {
                return type.createHandle(this.getRaw(), false);
            }
            throw new ClassCastException("Failed to cast handle of type " + MPLType.getName(this.getRaw().getClass()) + " to " + MPLType.getName(type.getType()));
        }

        public <T extends Handle> T tryCast(Class<T> type) {
            if (this.isInstanceOf(type)) {
                return type.createHandle(this.getRaw(), false);
            }
            return null;
        }

        public static Handle createHandle(final Object instance) {
            if (instance == null) {
                return null;
            }
            return new Handle(){

                @Override
                public Object getRaw() {
                    return instance;
                }
            };
        }

        public final int hashCode() {
            return this.getRaw().hashCode();
        }

        public final boolean equals(Object o) {
            if (this.getRaw() == null) {
                if (o == null) {
                    return true;
                }
                if (o instanceof Handle) {
                    return ((Handle)o).getRaw() == null;
                }
                return false;
            }
            if (this == o) {
                return true;
            }
            if (o instanceof Handle) {
                return ((Handle)o).getRaw().equals(this.getRaw());
            }
            return false;
        }

        public final String toString() {
            return this.getRaw().toString();
        }
    }

    public static class Class<H extends Handle>
    implements LazyInitializedObject,
    IgnoresRemapping {
        private boolean valid = false;
        private boolean optional = false;
        private String classPath = null;
        private java.lang.Class<?> classType = null;
        private java.lang.Class<H> handleType = null;
        private DuplexConverter<Object, H> handleConverter = null;
        private TemplateHandleBuilder<H> handleBuilder = null;
        private Invoker<H> handleBuilderMethod = null;
        private NullInstantiator<Object> instantiator = null;
        private TemplateElement<?>[] elements = new TemplateElement[0];
        private ClassFieldCopier<Object> fieldCopier = null;
        private ClassDeclaration classDec = null;

        public static <C extends Class<H>, H extends Handle> C create(java.lang.Class<C> classType) {
            return Class.create(classType, null);
        }

        public static <C extends Class<H>, H extends Handle> C create(java.lang.Class<C> classType, ClassDeclarationResolver classDeclarationResolver) {
            try {
                TemplateClassBuilder builder = new TemplateClassBuilder(classType, classDeclarationResolver);
                return builder.build();
            }
            catch (Throwable t) {
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize " + MPLType.getName(classType), t);
                return null;
            }
        }

        public final H createHandle(Object instance) {
            return this.createHandle(instance, false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public final H createHandle(Object instance, boolean allowNullInstance) {
            if (instance == null && !allowNullInstance) {
                return null;
            }
            if (this.handleBuilderMethod != null) {
                return (H)((Handle)this.handleBuilderMethod.invoke(null, instance));
            }
            if (this.handleBuilder == null) {
                Class clazz = this;
                synchronized (clazz) {
                    if (this.handleBuilder == null) {
                        TemplateHandleBuilder builder = new TemplateHandleBuilder(this);
                        builder.build();
                        if (this.handleBuilder == null) {
                            this.handleBuilder = builder;
                        }
                    }
                }
            }
            return this.handleBuilder.create(instance);
        }

        public final H newHandleNull() {
            return this.createHandle(this.newInstanceNull(), false);
        }

        public final Object newInstanceNull() {
            if (this.classType == null) {
                throw new IllegalStateException("Class " + this.classPath + " is not available");
            }
            return this.instantiator.create();
        }

        @Override
        public final void forceInitialization() {
            if (this.isAvailable()) {
                boolean success = true;
                for (TemplateElement<?> element : this.elements) {
                    if (element.isOptional() && !element.isAvailable()) continue;
                    try {
                        element.forceInitialization();
                    }
                    catch (Throwable t) {
                        MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize " + this.classPath + " " + element.getElementName(), t);
                        success = false;
                    }
                }
                if (this.getClass() != this.getSelfClassType()) {
                    for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
                        Object staticFieldValue;
                        if (!Modifier.isStatic(field.getModifiers())) continue;
                        try {
                            field.setAccessible(true);
                            staticFieldValue = field.get(null);
                            field.setAccessible(false);
                        }
                        catch (Throwable t) {
                            continue;
                        }
                        if (!(staticFieldValue instanceof LazyInitializedObject)) continue;
                        try {
                            ((LazyInitializedObject)staticFieldValue).forceInitialization();
                        }
                        catch (Throwable t) {
                            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize " + this.classPath + " field " + field.getName(), t);
                            success = false;
                        }
                    }
                }
                if (!success) {
                    throw new IllegalStateException("Not all members of template for " + this.classPath + " could be initialized!");
                }
            }
        }

        final void init(TemplateClassBuilder<?, H> builder) {
            this.handleType = builder.handleType;
            this.classPath = builder.instanceClassPath;
            this.classType = builder.instanceType;
            this.classDec = builder.classDec;
            this.optional = builder.isOptional;
            this.valid = this.classType != null && this.classDec != null;
            this.instantiator = NullInstantiator.of(this.classType);
            if (this.valid) {
                this.classDec.checkTemplateErrors();
            }
            if (this.classType != null && this.handleType != null) {
                this.handleConverter = new DuplexHandleConverter(this);
                Conversion.registerConverter(this.handleConverter);
            }
            if (this.valid) {
                this.classDec.getResolver().runBootstrap();
            }
            boolean fieldsSuccessful = true;
            ArrayList<TemplateElement> elementsList = new ArrayList<TemplateElement>();
            for (java.lang.reflect.Field templateFieldRef : this.getClass().getFields()) {
                String templateFieldName = templateFieldRef.getName();
                try {
                    Object templateField = templateFieldRef.get(this);
                    if (!(templateField instanceof TemplateElement)) continue;
                    TemplateElement element = (TemplateElement)templateField;
                    element.initElementName(this.classPath + "." + templateFieldName);
                    elementsList.add(element);
                    if (templateFieldRef.getAnnotation(Optional.class) != null) {
                        element.setOptional();
                    }
                    if (templateFieldRef.getAnnotation(Readonly.class) != null) {
                        element.setReadonly();
                    }
                    if (this.valid) {
                        Object result = element.init(this, this.classDec, templateFieldName);
                        if (result != null) {
                            if (!(result instanceof MethodDeclaration) || !TemplateHandleBuilder.isCreateHandleMethod((MethodDeclaration)result)) continue;
                            this.handleBuilderMethod = InitInvoker.proxy((Object)this, "handleBuilderMethod", ((StaticMethod)element).invoker);
                            continue;
                        }
                        if (element._optional) continue;
                        fieldsSuccessful = false;
                        continue;
                    }
                    element.initNoClass();
                }
                catch (Throwable t) {
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to initialize template field '" + templateFieldName + "' in " + this.classType.getName(), t);
                    fieldsSuccessful = false;
                }
            }
            this.valid &= fieldsSuccessful;
            this.elements = MountiplexUtil.toArray(elementsList, TemplateElement.class);
        }

        public final boolean isAvailable() {
            return this.classType != null;
        }

        public final boolean isValid() {
            return this.valid;
        }

        public final boolean isOptional() {
            return this.optional;
        }

        public java.lang.Class<?> getType() {
            return this.classType;
        }

        public java.lang.Class<H> getHandleType() {
            return this.handleType;
        }

        protected java.lang.Class<?> getSelfClassType() {
            return this.getClass();
        }

        public ClassDeclaration getClassDeclaration() {
            return this.classDec;
        }

        public DuplexConverter<Object, H> getHandleConverter() {
            return this.handleConverter;
        }

        public boolean isType(Object instance) {
            return instance != null && this.classType != null && this.classType.equals(instance.getClass());
        }

        public boolean isType(java.lang.Class<?> type) {
            return this.classType != null && this.classType.equals(type);
        }

        public boolean isHandleType(Handle handle) {
            return handle != null && this.isType(handle.getRaw());
        }

        public boolean isAssignableFrom(Object instance) {
            return instance != null && this.classType != null && this.classType.isAssignableFrom(instance.getClass());
        }

        public boolean isAssignableFrom(java.lang.Class<?> type) {
            return type != null && this.classType != null && this.classType.isAssignableFrom(type);
        }

        public void copyHandle(H handleFrom, H handleTo) {
            if (handleFrom == null || handleTo == null) {
                return;
            }
            this.copy(((Handle)handleFrom).getRaw(), ((Handle)handleTo).getRaw());
        }

        public void copy(Object instanceFrom, Object instanceTo) {
            if (this.fieldCopier == null) {
                this.fieldCopier = ClassFieldCopier.of(this.classType);
            }
            this.fieldCopier.copy(instanceFrom, instanceTo);
        }
    }
}

