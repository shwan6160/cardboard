/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldLCSResolver;
import com.bergerkiller.mountiplex.reflection.declarations.ModifierDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.NameDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastConvertedField;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.MethodBodyBuilder;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class FieldDeclaration
extends Declaration {
    public final ModifierDeclaration modifiers;
    public final NameDeclaration name;
    public final TypeDeclaration type;
    public boolean isEnum;
    public Field field;

    public FieldDeclaration(ClassResolver resolver, Enum<?> enumValue) {
        super(resolver);
        this.isEnum = true;
        this.field = null;
        this.modifiers = new ModifierDeclaration(resolver, 25);
        this.name = new NameDeclaration(resolver, enumValue.name(), null);
        this.type = TypeDeclaration.fromType(resolver, enumValue.getClass());
    }

    public FieldDeclaration(ClassResolver resolver, Field field) {
        super(resolver);
        TypeDeclaration fieldType;
        String name = MPLType.getName(field);
        String alias = Resolver.resolveFieldAlias(field, name);
        if (name.equals(alias)) {
            alias = null;
        }
        try {
            fieldType = TypeDeclaration.fromType(resolver, field.getGenericType());
        }
        catch (TypeNotPresentException | GenericSignatureFormatError | MalformedParameterizedTypeException ex) {
            fieldType = TypeDeclaration.fromType(resolver, field.getType());
        }
        this.isEnum = field.isEnumConstant();
        this.field = field;
        this.modifiers = new ModifierDeclaration(resolver, field.getModifiers());
        this.type = fieldType;
        this.name = new NameDeclaration(resolver, name, alias);
    }

    public FieldDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    public FieldDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver, declaration);
        this.getParserPostfix().trimWhitespace(0);
        if (this.getPostfix().startsWith("enum ")) {
            this.getParserPostfix().trimWhitespace(5);
            this.setPostfix(this.getPostfix().prepend("public static final "));
            this.isEnum = true;
            this.modifiers = this.nextModifier();
            this.type = this.nextType();
            this.name = this.nextName();
        } else {
            this.isEnum = false;
            this.field = null;
            this.modifiers = this.nextModifier();
            this.type = this.nextType();
            this.name = this.nextName();
        }
    }

    private FieldDeclaration(FieldDeclaration original, NameDeclaration newName) {
        super(original.getResolver());
        this.modifiers = original.modifiers;
        this.name = newName;
        this.type = original.type;
        this.isEnum = original.isEnum;
        this.field = original.field;
    }

    public final void copyFieldFrom(FieldDeclaration other) {
        if (this != other) {
            this.field = other.field;
            this.isEnum = other.isEnum;
        }
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof FieldDeclaration)) {
            return 0.0;
        }
        FieldDeclaration f = (FieldDeclaration)other;
        return 0.1 * this.modifiers.similarity(f.modifiers) + 0.3 * this.name.similarity(f.name) + 0.6 * this.type.similarity(f.type);
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof FieldDeclaration) {
            FieldDeclaration field = (FieldDeclaration)declaration;
            return (this.isEnum == field.isEnum || this.modifiers.match(field.modifiers)) && this.name.match(field.name) && this.type.match(field.type);
        }
        return false;
    }

    public boolean matchSignature(Declaration declaration) {
        if (declaration instanceof FieldDeclaration) {
            FieldDeclaration field = (FieldDeclaration)declaration;
            return (this.isEnum == field.isEnum || this.modifiers.match(field.modifiers)) && this.type.match(field.type);
        }
        return false;
    }

    @Override
    public String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        if (this.isEnum) {
            return "enum " + this.type.toString(identity) + " " + this.name.toString(identity);
        }
        String m = this.modifiers.toString(identity);
        String t = this.type.toString(identity);
        String n = this.name.toString(identity);
        if (m.length() > 0) {
            return m + " " + t + " " + n + ";";
        }
        return t + " " + n + ";";
    }

    @Override
    public boolean isResolved() {
        return this.modifiers.isResolved() && this.type.isResolved() && this.name.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Field {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.modifiers.debugString(str, indent + "  ");
        this.type.debugString(str, indent + "  ");
        this.name.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }

    @Override
    public void modifyBodyRequirement(Requirement requirement, StringBuilder body, String instanceName, String requirementName, int instanceStartIdx, int nameEndIdx) {
        TypeDeclaration fieldType = this.type;
        if (fieldType.cast != null) {
            fieldType = fieldType.cast;
        }
        int setOperationValueStartIdx = -1;
        for (int i = nameEndIdx; i < body.length(); ++i) {
            char c = body.charAt(i);
            if (c == ' ') continue;
            if (c != '=' || i + 1 < body.length() && body.charAt(i + 1) == '=') break;
            for (setOperationValueStartIdx = i + 1; setOperationValueStartIdx < body.length() && body.charAt(setOperationValueStartIdx) == ' '; ++setOperationValueStartIdx) {
            }
            break;
        }
        Class<?> fieldDeclaringClass = this.field == null ? null : this.field.getDeclaringClass();
        int modifiers = 0;
        if (fieldDeclaringClass != null && this.type.cast == null && Resolver.isPublic(fieldDeclaringClass)) {
            modifiers = this.field.getModifiers();
        }
        if (setOperationValueStartIdx != -1) {
            char c;
            int setOperationValueEndIdx;
            if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers)) {
                MethodBodyBuilder replacement = new MethodBodyBuilder();
                if (Modifier.isStatic(modifiers)) {
                    replacement.appendAccessibleTypeName(fieldDeclaringClass);
                } else {
                    replacement.append(instanceName);
                }
                replacement.append('.');
                replacement.append("MPL_NOREMAP$");
                replacement.append(MPLType.getName(this.field));
                body.replace(instanceStartIdx, nameEndIdx, replacement.toString());
                return;
            }
            int parenthesesCtr = 0;
            for (setOperationValueEndIdx = setOperationValueStartIdx; setOperationValueEndIdx < body.length() && (c = body.charAt(setOperationValueEndIdx)) != ';'; ++setOperationValueEndIdx) {
                if (c == '(') {
                    ++parenthesesCtr;
                    continue;
                }
                if (c == ')' && --parenthesesCtr < 0) break;
            }
            String valueName = body.substring(setOperationValueStartIdx, setOperationValueEndIdx);
            MethodBodyBuilder replacement = new MethodBodyBuilder();
            replacement.append("this.").append(requirementName).append(".set");
            if (fieldType.isPrimitive) {
                replacement.append(BoxedType.getBoxedType(fieldType.type).getSimpleName());
            }
            replacement.append('(');
            replacement.append(instanceName);
            replacement.append(", ");
            replacement.append(valueName);
            replacement.append(')');
            requirement.setProperty("generateFastField");
            body.replace(instanceStartIdx, setOperationValueEndIdx, replacement.toString());
        } else {
            MethodBodyBuilder replacement = new MethodBodyBuilder();
            if (Modifier.isPublic(modifiers)) {
                if (Modifier.isStatic(modifiers)) {
                    replacement.append(ReflectionUtil.getAccessibleTypeName(fieldDeclaringClass));
                } else {
                    replacement.append(instanceName);
                }
                replacement.append('.');
                replacement.append("MPL_NOREMAP$");
                replacement.append(MPLType.getName(this.field));
            } else {
                if (fieldType.isPrimitive) {
                    replacement.append("this.").append(requirementName).append(".get");
                    replacement.append(BoxedType.getBoxedType(fieldType.type).getSimpleName());
                } else {
                    replacement.appendAccessibleTypeCast(fieldType.type);
                    replacement.append("this.").append(requirementName).append(".get");
                }
                replacement.append('(');
                replacement.append(instanceName);
                replacement.append(')');
                requirement.setProperty("generateFastField");
            }
            body.replace(instanceStartIdx, nameEndIdx, replacement.toString());
        }
    }

    @Override
    public void addAsRequirement(ExtendedClassWriter<?> writer, Requirement requirement, String name) throws CannotCompileException, NotFoundException {
        if (!requirement.hasProperty("generateFastField")) {
            return;
        }
        if (this.type.cast != null) {
            DuplexConverter<Object, Object> converter = Conversion.findDuplex(this.type, this.type.cast);
            if (converter == null) {
                throw new RuntimeException("Failed to find converter from " + this.type.toString(true) + " <> " + this.type.cast.toString(true));
            }
            FastField f = new FastField();
            f.init(this.field);
            FastConvertedField<Object> cf = new FastConvertedField<Object>(f, converter);
            writer.visitSingletonField(name, FastConvertedField.class, cf);
        } else {
            FastField f = new FastField();
            f.init(this.field);
            writer.visitSingletonField(name, FastField.class, f);
        }
    }

    @Override
    public FieldDeclaration discover() {
        if (!this.isValid() || !this.isResolved()) {
            return null;
        }
        try {
            FieldDeclaration nameResolved = this.resolveName();
            if (nameResolved.field == null) {
                Field javaField = MPLType.getDeclaredField(this.getResolver().getDeclaredClass(), nameResolved.name.value());
                FieldDeclaration realField = new FieldDeclaration(this.getResolver(), javaField);
                if (!nameResolved.match(realField)) {
                    return null;
                }
                if (this.modifiers.isPublic() && !Modifier.isPublic(realField.field.getModifiers())) {
                    return null;
                }
                nameResolved.copyFieldFrom(realField);
            } else if (this.modifiers.isPublic() && !Modifier.isPublic(nameResolved.field.getModifiers())) {
                return null;
            }
            this.copyFieldFrom(nameResolved);
            return nameResolved;
        }
        catch (NoSuchFieldException nameResolved) {
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to discover field", t);
        }
        return null;
    }

    @Override
    public void discoverAlternatives() {
        Class<?> declaringClass = this.getResolver().getDeclaredClass();
        if (declaringClass == null) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Declaration could not be found inside: ??" + this.getResolver().getDeclaredClassName() + "??");
            MountiplexUtil.LOGGER.log(Level.SEVERE, "Declaration: " + this.toString());
            return;
        }
        Declaration[] alternatives = this.modifiers.isStatic() ? (FieldDeclaration[])ReflectionUtil.getAllStaticFields(declaringClass).map(f -> new FieldDeclaration(this.getResolver(), (Field)f)).toArray(FieldDeclaration[]::new) : (FieldDeclaration[])ReflectionUtil.getAllNonStaticFields(declaringClass).map(f -> new FieldDeclaration(this.getResolver(), (Field)f)).toArray(FieldDeclaration[]::new);
        FieldDeclaration.sortSimilarity((Declaration)this, (Declaration[])alternatives);
        FieldLCSResolver.logAlternatives((String)"field", (Declaration[])alternatives, (Declaration)this.resolveName(), (boolean)true);
    }

    public FieldDeclaration resolveName() {
        if (!this.isResolved()) {
            return this;
        }
        Remapping.FieldRemapping remapping = this.getResolver().getRemappings().find(this);
        if (remapping != null) {
            FieldDeclaration remappedSelf = new FieldDeclaration(this, this.name.rename(remapping.declaration.name));
            remappedSelf.field = remapping.field;
            remappedSelf.isEnum = remapping.field.isEnumConstant();
            return remappedSelf;
        }
        String resolvedName = Resolver.resolveFieldName(this.getResolver().getDeclaredClass(), this.name.value());
        if (resolvedName != null && !resolvedName.equals(this.name.value())) {
            return new FieldDeclaration(this, this.name.rename(resolvedName));
        }
        return this;
    }

    protected String getAccessedName() {
        return this.field != null ? MPLType.getName(this.field) : this.name.value();
    }
}

