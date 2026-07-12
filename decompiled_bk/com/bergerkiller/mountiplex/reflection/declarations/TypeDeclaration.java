/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeDeclaration
extends Declaration {
    public static final TypeDeclaration INVALID = new TypeDeclaration(ClassResolver.DEFAULT, (Type)null);
    public static final TypeDeclaration OBJECT = new TypeDeclaration(ClassResolver.DEFAULT, (Type)((Object)Object.class));
    public static final TypeDeclaration ENUM = new TypeDeclaration(ClassResolver.DEFAULT, (Type)((Object)Enum.class));
    public static final TypeDeclaration ANY = TypeDeclaration.parse("?");
    public final boolean isWildcard;
    public final boolean isPrimitive;
    private final TypeDeclaration boxed;
    public final String variableName;
    public final String typeName;
    public final String typePath;
    public final Class<?> type;
    public final TypeDeclaration[] genericTypes;
    public final TypeDeclaration cast;
    private TypeDeclaration[] superTypes = null;

    public TypeDeclaration(ClassResolver resolver, Type type) {
        super(resolver);
        this.cast = null;
        if (type == null) {
            this.isWildcard = false;
            this.variableName = null;
            this.type = null;
            this.typeName = "NULL";
            this.typePath = "NULL";
            this.isPrimitive = false;
            this.boxed = this;
            this.genericTypes = new TypeDeclaration[0];
            this.setInvalid();
            return;
        }
        this.isWildcard = type instanceof WildcardType;
        if (this.isWildcard) {
            type = ((WildcardType)type).getUpperBounds()[0];
        }
        int arrayLevels = 0;
        while (type instanceof GenericArrayType) {
            type = ((GenericArrayType)type).getGenericComponentType();
            ++arrayLevels;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Type[] params = ptype.getActualTypeArguments();
            this.type = MountiplexUtil.getArrayType((Class)ptype.getRawType(), arrayLevels);
            this.typePath = resolver.resolvePath(this.type);
            this.typeName = resolver.resolveName(this.type);
            this.isPrimitive = false;
            this.boxed = this;
            this.genericTypes = new TypeDeclaration[params.length];
            this.variableName = null;
            for (int i = 0; i < params.length; ++i) {
                this.genericTypes[i] = new TypeDeclaration(resolver, params[i]);
            }
        } else if (type instanceof Class) {
            this.type = MountiplexUtil.getArrayType((Class)type, arrayLevels);
            this.typePath = resolver.resolvePath(this.type);
            this.typeName = resolver.resolveName(this.type);
            this.isPrimitive = this.type.isPrimitive();
            this.boxed = this.isPrimitive ? TypeDeclaration.fromClass(BoxedType.getBoxedType(this.type)) : this;
            this.genericTypes = new TypeDeclaration[0];
            this.variableName = null;
        } else if (type instanceof TypeVariable) {
            Class<Object> bound;
            TypeVariable vtype;
            block13: {
                vtype = (TypeVariable)type;
                Type varType = vtype.getBounds()[0];
                while (true) {
                    if (varType instanceof Class) {
                        bound = (Class<Object>)((Object)varType);
                        break block13;
                    }
                    if (varType instanceof ParameterizedType) {
                        varType = ((ParameterizedType)varType).getRawType();
                        continue;
                    }
                    if (!(varType instanceof TypeVariable)) break;
                    varType = ((TypeVariable)varType).getBounds()[0];
                }
                bound = Object.class;
            }
            this.type = MountiplexUtil.getArrayType(bound, arrayLevels);
            this.typePath = resolver.resolvePath(this.type);
            this.typeName = resolver.resolveName(this.type);
            this.isPrimitive = false;
            this.boxed = this;
            this.genericTypes = new TypeDeclaration[0];
            this.variableName = vtype.getName();
        } else {
            MountiplexUtil.LOGGER.warning("Unsupported type in TypeDeclaration: " + type.getClass());
            this.type = null;
            this.typePath = "";
            this.typeName = "";
            this.isPrimitive = false;
            this.boxed = this;
            this.genericTypes = new TypeDeclaration[0];
            this.variableName = null;
            this.setInvalid();
        }
    }

    private TypeDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver, declaration);
        char c;
        int cidx;
        if (declaration == null) {
            this.typeName = "";
            this.typePath = "";
            this.type = null;
            this.isPrimitive = false;
            this.boxed = this;
            this.genericTypes = new TypeDeclaration[0];
            this.isWildcard = false;
            this.variableName = null;
            this.cast = null;
            this.setInvalid();
            return;
        }
        String rawType = null;
        String typeVarName = null;
        StringBuffer postfix = StringBuffer.EMPTY;
        int startIdx = -1;
        boolean anyType = false;
        boolean foundExtends = false;
        TypeDeclaration castType = null;
        for (int cidx2 = 0; cidx2 < declaration.length(); ++cidx2) {
            int validNameChar;
            int c2 = declaration.charAt(cidx2);
            if (startIdx == -1) {
                if (c2 == 32) continue;
                if (c2 == 63) {
                    anyType = true;
                    continue;
                }
                if (c2 == 41 && castType != null) continue;
            }
            int n = validNameChar = !MountiplexUtil.containsChar((char)c2, ParserStringBuffer.INVALID_NAME_CHARACTERS) ? 1 : 0;
            if (startIdx == -1) {
                if (c2 == 40) {
                    int cast_end = declaration.indexOf(')', cidx2 + 1);
                    if (cast_end == -1 || !(castType = new TypeDeclaration(resolver, declaration.substring(cidx2 + 1, cast_end))).isValid()) break;
                    cidx2 = cast_end;
                    continue;
                }
                if (validNameChar != 0) {
                    startIdx = cidx2;
                } else {
                    postfix = declaration.substring(cidx2);
                    break;
                }
            }
            if (validNameChar == 0 && rawType == null) {
                rawType = declaration.substringToString(startIdx, cidx2);
                if (anyType && !foundExtends) {
                    startIdx = -1;
                    if (!rawType.equals("extends")) break;
                    foundExtends = true;
                    rawType = null;
                }
            }
            if (rawType == null || c2 == 32) continue;
            if (declaration.substring(cidx2).startsWith("extends ")) {
                if (rawType.length() > 1) {
                    postfix = declaration.substring(cidx2);
                    break;
                }
                typeVarName = rawType;
                foundExtends = true;
                rawType = null;
                startIdx = -1;
                cidx2 += 7;
                continue;
            }
            postfix = declaration.substring(cidx2);
            break;
        }
        this.isWildcard = anyType;
        if (startIdx == -1) {
            if (this.isWildcard) {
                this.setPostfix(postfix);
                this.typeName = "";
                this.typePath = "java.lang.Object";
                this.type = Object.class;
                this.isPrimitive = false;
                this.boxed = this;
                this.variableName = typeVarName;
                this.genericTypes = new TypeDeclaration[0];
                this.cast = castType;
            } else {
                this.setInvalid();
                this.typeName = "";
                this.typePath = "";
                this.type = null;
                this.isPrimitive = false;
                this.boxed = this;
                this.variableName = typeVarName;
                this.genericTypes = new TypeDeclaration[0];
                this.cast = castType;
            }
            return;
        }
        if (rawType == null) {
            rawType = declaration.substringToString(startIdx);
            postfix = StringBuffer.EMPTY;
        }
        if (rawType != null && rawType.equals("enum")) {
            this.setInvalid();
            this.typeName = "";
            this.typePath = "";
            this.type = null;
            this.variableName = typeVarName;
            this.isPrimitive = false;
            this.boxed = this;
            this.genericTypes = new TypeDeclaration[0];
            this.cast = castType;
            return;
        }
        if (rawType != null && rawType.length() == 1 && typeVarName == null) {
            typeVarName = rawType;
            rawType = "Object";
            if (resolver.getDeclaredClass() != null) {
                block1: for (TypeVariable<Class<?>> tVar : resolver.getDeclaredClass().getTypeParameters()) {
                    if (!typeVarName.equals(tVar.getName())) continue;
                    for (Type bound : tVar.getBounds()) {
                        TypeDeclaration tDec = TypeDeclaration.fromType(resolver, bound);
                        if (!tDec.isValid() || !tDec.isResolved()) continue;
                        rawType = tDec.typePath;
                        break block1;
                    }
                }
            }
        }
        this.variableName = typeVarName;
        if (postfix.length() > 0 && postfix.charAt(0) == '<') {
            TypeDeclaration gen;
            LinkedList<TypeDeclaration> types = new LinkedList<TypeDeclaration>();
            do {
                if (!(gen = new TypeDeclaration(resolver, postfix.substring(1))).isValid()) {
                    this.setInvalid();
                    this.typeName = "";
                    this.typePath = "";
                    this.type = null;
                    this.isPrimitive = false;
                    this.boxed = this;
                    this.genericTypes = new TypeDeclaration[0];
                    this.cast = null;
                    return;
                }
                types.add(gen);
            } while ((postfix = gen.getPostfix()).length() > 0 && postfix.charAt(0) == ',');
            for (cidx = 0; cidx < postfix.length(); ++cidx) {
                c = postfix.charAt(cidx);
                if (c == ' ') continue;
                if (c == '>') {
                    postfix = postfix.substring(cidx + 1);
                    break;
                }
                postfix = postfix.substring(cidx);
                break;
            }
            this.genericTypes = types.toArray(new TypeDeclaration[types.size()]);
        } else {
            this.genericTypes = new TypeDeclaration[0];
        }
        int arrayEnd = -1;
        for (cidx = 0; cidx < postfix.length(); ++cidx) {
            c = postfix.charAt(cidx);
            if (c == '[' || c == ']') {
                rawType = rawType + c;
                continue;
            }
            if (c == ' ') continue;
            arrayEnd = cidx;
            break;
        }
        if (arrayEnd == -1) {
            this.setPostfix(StringBuffer.EMPTY);
        } else {
            this.setPostfix(postfix.substring(arrayEnd));
        }
        ClassResolver.ResolveResult resolveResult = resolver.resolve(rawType);
        this.cast = castType;
        this.type = resolveResult.classType;
        this.typePath = resolveResult.classPath;
        this.typeName = rawType;
        this.isPrimitive = this.type != null && this.type.isPrimitive();
        this.boxed = this.isPrimitive ? TypeDeclaration.fromClass(BoxedType.getBoxedType(this.type)) : this;
    }

    private TypeDeclaration(TypeDeclaration mainType, TypeDeclaration[] genericTypes) {
        super(mainType.getResolver());
        this.cast = mainType.cast;
        this.isWildcard = mainType.isWildcard;
        this.typeName = mainType.typeName;
        this.typePath = mainType.typePath;
        this.type = mainType.type;
        this.isPrimitive = mainType.isPrimitive;
        this.boxed = mainType.isPrimitive ? mainType.boxed : this;
        this.variableName = mainType.variableName;
        this.genericTypes = genericTypes;
    }

    public TypeDeclaration getSuperType() {
        return this.resolveSuperType(Resolver.getMeta(this.type).superType);
    }

    public TypeDeclaration[] getSuperTypes() {
        if (this.superTypes == null) {
            ArrayList<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
            TypeDeclaration superType = this.getSuperType();
            if (superType != null) {
                types.add(superType);
                types.addAll(Arrays.asList(superType.getSuperTypes()));
            }
            this.addInterfaces(types);
            this.superTypes = types.toArray(new TypeDeclaration[types.size()]);
        }
        return this.superTypes;
    }

    private void addInterfaces(ArrayList<TypeDeclaration> types) {
        if (this.type != null) {
            for (TypeDeclaration iif : Resolver.getMeta(this.type).interfaces) {
                if (types.contains(iif = this.resolveSuperType(iif))) continue;
                types.add(iif);
                iif.addInterfaces(types);
            }
        }
    }

    public boolean isArray() {
        return this.type != null && this.type.isArray();
    }

    public TypeDeclaration exposed() {
        return this.cast == null ? this : this.cast;
    }

    public TypeDeclaration getComponentType() {
        if (this.type != null && this.type.isArray()) {
            TypeDeclaration componentType = new TypeDeclaration(this.getResolver(), this.type.getComponentType());
            componentType = componentType.setGenericTypes(this.genericTypes);
            return componentType;
        }
        return null;
    }

    public TypeDeclaration getBoxedType() {
        return this.boxed;
    }

    public boolean hasTypeVariables() {
        if (this.variableName != null) {
            return true;
        }
        if (this.genericTypes.length == 0) {
            return false;
        }
        for (int i = 0; i < this.genericTypes.length; ++i) {
            if (!this.genericTypes[i].hasTypeVariables()) continue;
            return true;
        }
        return false;
    }

    public boolean canDownCast() {
        return this.cast == null || this.cast.isAssignableFrom(this);
    }

    public boolean canUpCast() {
        return this.cast == null || this.isAssignableFrom(this.cast);
    }

    public boolean isAssignableFrom(Object value) {
        return value != null && this.type.isAssignableFrom(value.getClass());
    }

    public boolean isAssignableFrom(TypeDeclaration other) {
        return other != null && other.isInstanceOf(this);
    }

    public boolean isInstanceOf(Class<?> otherType) {
        return otherType != null && this.type != null && otherType.isAssignableFrom(this.type);
    }

    public boolean isInstanceOf(TypeDeclaration other) {
        TypeDeclaration selfType;
        if (other == null || other.type == null || this.type == null || !other.type.isAssignableFrom(this.type)) {
            return false;
        }
        if (other.genericTypes.length == 0) {
            return true;
        }
        TypeDeclaration typeDeclaration = selfType = this.type.equals(other.type) ? this : this.castAsSuperType(other.type);
        if (selfType == null || other.genericTypes.length != selfType.genericTypes.length) {
            return false;
        }
        for (int i = 0; i < selfType.genericTypes.length; ++i) {
            TypeDeclaration s = selfType.genericTypes[i];
            TypeDeclaration t = other.genericTypes[i];
            if (t.type == null) {
                return false;
            }
            if (!(t.isWildcard ? !s.isInstanceOf(t) : (t.variableName == null || !s.isInstanceOf(t)) && !t.equals(s))) continue;
            return false;
        }
        return true;
    }

    public TypeDeclaration castAsType(Class<?> classType) {
        if (classType.equals(this.type)) {
            return this;
        }
        if (classType.isAssignableFrom(this.type)) {
            return this.castAsSuperType(classType);
        }
        return null;
    }

    private TypeDeclaration castAsSuperType(Class<?> classType) {
        for (TypeDeclaration type : this.getSuperTypes()) {
            if (!type.type.equals(classType)) continue;
            return type;
        }
        return null;
    }

    public TypeDeclaration setGenericTypes(TypeDeclaration ... genericTypes) {
        return new TypeDeclaration(this, genericTypes);
    }

    public TypeDeclaration getGenericType(int index) {
        return index >= 0 && index < this.genericTypes.length ? this.genericTypes[index] : OBJECT;
    }

    private final TypeDeclaration resolveSuperType(TypeDeclaration superType) {
        if (superType == null) {
            return null;
        }
        if (superType.genericTypes.length > 0 && this.genericTypes.length > 0) {
            TypeDeclaration[] newTypeParams = (TypeDeclaration[])superType.genericTypes.clone();
            TypeVariable<Class<?>>[] params = this.type.getTypeParameters();
            if (params.length == this.genericTypes.length) {
                boolean sameParamTypeCount = params.length == superType.genericTypes.length;
                block0: for (int i = 0; i < superType.genericTypes.length; ++i) {
                    String name = superType.genericTypes[i].variableName;
                    if (name == null) continue;
                    if (sameParamTypeCount && params[i].getName().equals(name)) {
                        newTypeParams[i] = this.genericTypes[i];
                        continue;
                    }
                    for (int j = 0; j < params.length; ++j) {
                        if (!params[j].getName().equals(name)) continue;
                        newTypeParams[i] = this.genericTypes[j];
                        continue block0;
                    }
                }
            }
            return superType.setGenericTypes(newTypeParams);
        }
        return superType;
    }

    @Override
    public double similarity(Declaration other) {
        double genericSimilarity;
        double mainTypeSimilarity;
        if (!(other instanceof TypeDeclaration)) {
            return 0.0;
        }
        TypeDeclaration t = (TypeDeclaration)other;
        if (!t.isValid() || !this.isValid()) {
            return 0.0;
        }
        if (!t.isResolved() || !this.isResolved()) {
            mainTypeSimilarity = MountiplexUtil.similarity(this.typePath, t.typePath);
        } else if (this.type.equals(t.type)) {
            mainTypeSimilarity = 1.0;
        } else {
            Class<?> s;
            Class<?> t2;
            Class<?> t1 = BoxedType.tryBoxType(this.type);
            if (t1.isAssignableFrom(t2 = BoxedType.tryBoxType(t.type))) {
                int n = 1;
                s = t2;
                while ((s = s.getSuperclass()) != null && !s.equals(t1)) {
                    ++n;
                }
                mainTypeSimilarity = 1.0 / (double)n;
            } else if (t2.isAssignableFrom(t1)) {
                int n = 1;
                s = t1;
                while ((s = s.getSuperclass()) != null && !s.equals(t2)) {
                    ++n;
                }
                mainTypeSimilarity = 1.0 / (double)n;
            } else {
                Collection t1s = ReflectionUtil.getAllClassesAndInterfaces(t1).filter(c -> c != Object.class).collect(Collectors.toCollection(ArrayList::new));
                Collection t2s = ReflectionUtil.getAllClassesAndInterfaces(t2).filter(c -> c != Object.class).collect(Collectors.toCollection(ArrayList::new));
                long numberOverlap = t1s.stream().filter(t2s::contains).count();
                if (numberOverlap == 0L) {
                    mainTypeSimilarity = 0.0;
                } else {
                    long totalSuperTypes = Stream.concat(t1s.stream(), t2s.stream()).distinct().count();
                    mainTypeSimilarity = (double)numberOverlap / (double)totalSuperTypes;
                }
            }
        }
        if (this.genericTypes.length > 0 && t.genericTypes.length > 0) {
            if (this.genericTypes.length == t.genericTypes.length) {
                genericSimilarity = 0.0;
                for (int i = 0; i < this.genericTypes.length; ++i) {
                    genericSimilarity += this.genericTypes[i].similarity(t.genericTypes[i]);
                }
                genericSimilarity /= (double)this.genericTypes.length;
            } else {
                genericSimilarity = 0.0;
            }
        } else {
            genericSimilarity = this.genericTypes.length == 0 && t.genericTypes.length == 0 ? 1.0 : 0.5;
        }
        return 0.75 * mainTypeSimilarity + 0.25 * genericSimilarity;
    }

    @Override
    public final boolean match(Declaration declaration) {
        if (!(declaration instanceof TypeDeclaration)) {
            return false;
        }
        TypeDeclaration type = (TypeDeclaration)declaration;
        if (this.type == null || type.type == null) {
            return false;
        }
        if (this.isWildcard != type.isWildcard) {
            return false;
        }
        if (!this.type.equals(type.type)) {
            return false;
        }
        if (this.genericTypes.length != 0 && type.genericTypes.length != 0) {
            if (this.genericTypes.length != type.genericTypes.length) {
                return false;
            }
            for (int i = 0; i < this.genericTypes.length; ++i) {
                if (this.genericTypes[i].match(type.genericTypes[i])) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public final String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        String typeInfo = identity ? (this.type == null ? this.typePath : MPLType.getName(this.type)) : this.typeName;
        int arrIdx = typeInfo.indexOf(91);
        String arrPart = "";
        if (arrIdx != -1) {
            arrPart = typeInfo.substring(arrIdx);
            typeInfo = typeInfo.substring(0, arrIdx);
        }
        if (this.type == null) {
            typeInfo = "??" + typeInfo + "??";
        }
        String str = "";
        if (this.cast != null && !identity) {
            str = str + "(" + this.cast.toString(identity) + ") ";
        }
        str = this.isWildcard ? (typeInfo.length() == 0 || this.type == Object.class ? str + "?" : str + "? extends " + typeInfo) : (this.variableName != null ? (typeInfo.length() == 0 || this.type == Object.class ? str + this.variableName : str + this.variableName + " extends " + typeInfo) : str + typeInfo);
        if (this.genericTypes.length > 0) {
            str = str + "<";
            boolean first = true;
            for (TypeDeclaration genericType : this.genericTypes) {
                if (first) {
                    first = false;
                } else {
                    str = str + ", ";
                }
                str = str + genericType.toString(identity);
            }
            str = str + ">";
        }
        str = str + arrPart;
        return str;
    }

    @Override
    public boolean isResolved() {
        if (this.type == null) {
            return false;
        }
        if (this.cast != null && !this.cast.isResolved()) {
            return false;
        }
        for (int i = 0; i < this.genericTypes.length; ++i) {
            if (this.genericTypes[i].isResolved()) continue;
            return false;
        }
        return true;
    }

    public boolean isBuiltin() {
        return TypeDeclaration.isBuiltin(this.type);
    }

    private static boolean isBuiltin(Class<?> type) {
        if (type != null) {
            if (type.isPrimitive()) {
                return true;
            }
            if (type.isArray()) {
                return TypeDeclaration.isBuiltin(type.getComponentType());
            }
            String path = type.getName();
            if (path.startsWith("java.lang.")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Type {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        str.append(indent).append("  typeName=").append(this.typeName).append('\n');
        str.append(indent).append("  typePath=").append(this.typePath).append('\n');
        str.append(indent).append("  type=").append(this.type).append('\n');
        str.append(indent).append("  isWildcard=").append(this.isWildcard).append('\n');
        for (TypeDeclaration t : this.genericTypes) {
            t.debugString(str, indent + "  ");
        }
        str.append(indent).append("}\n");
    }

    public static TypeDeclaration fromClass(Class<?> classType) {
        return Resolver.getMeta(classType).typeDec;
    }

    public static TypeDeclaration createGeneric(Class<?> baseType, Class<?> ... genericTypes) {
        TypeDeclaration[] gen = new TypeDeclaration[genericTypes.length];
        for (int i = 0; i < gen.length; ++i) {
            gen[i] = TypeDeclaration.fromClass(genericTypes[i]);
        }
        return TypeDeclaration.createGeneric(baseType, gen);
    }

    public static TypeDeclaration createGeneric(Class<?> baseType, TypeDeclaration ... genericTypes) {
        return TypeDeclaration.fromClass(baseType).setGenericTypes(genericTypes);
    }

    public static TypeDeclaration createArray(Class<?> componentType) {
        return TypeDeclaration.fromClass(MountiplexUtil.getArrayType(componentType));
    }

    public static TypeDeclaration createArray(TypeDeclaration componentType) {
        TypeDeclaration result = TypeDeclaration.createArray(componentType.type);
        result = result.setGenericTypes(componentType.genericTypes);
        return result;
    }

    public static TypeDeclaration fromType(ClassResolver classResolver, Type type) {
        return new TypeDeclaration(classResolver, type);
    }

    public static TypeDeclaration fromType(Type type) {
        if (type instanceof Class) {
            return TypeDeclaration.fromClass((Class)type);
        }
        return new TypeDeclaration(ClassResolver.DEFAULT, type);
    }

    public static TypeDeclaration parse(StringBuffer declaration) {
        return new TypeDeclaration(ClassResolver.DEFAULT, declaration);
    }

    public static TypeDeclaration parse(ClassResolver classResolver, StringBuffer declaration) {
        return new TypeDeclaration(classResolver, declaration);
    }

    public static TypeDeclaration parse(String declaration) {
        return new TypeDeclaration(ClassResolver.DEFAULT, StringBuffer.of(declaration));
    }

    public static TypeDeclaration parse(ClassResolver classResolver, String declaration) {
        return new TypeDeclaration(classResolver, StringBuffer.of(declaration));
    }
}

