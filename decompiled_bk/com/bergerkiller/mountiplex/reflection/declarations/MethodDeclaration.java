/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.CtMethod;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldLCSResolver;
import com.bergerkiller.mountiplex.reflection.declarations.ModifierDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.NameDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParserGroups;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.MethodBodyBuilder;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.asm.javassist.MPLCtNewMethod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;

public class MethodDeclaration
extends Declaration {
    public Method method;
    public Constructor<Object> constructor;
    public final boolean isRecordFieldChanger;
    public final ModifierDeclaration modifiers;
    public final TypeDeclaration returnType;
    public final NameDeclaration name;
    public final ParameterListDeclaration parameters;
    public final String body;
    public final Requirement[] bodyRequirements;

    public MethodDeclaration(ClassResolver resolver, Constructor<?> constructor) {
        super(resolver);
        try {
            this.method = null;
            this.constructor = constructor;
            this.isRecordFieldChanger = false;
            this.modifiers = new ModifierDeclaration(resolver, constructor.getModifiers());
            this.returnType = TypeDeclaration.fromClass(constructor.getDeclaringClass());
            this.name = new NameDeclaration(resolver, "<init>", null);
            this.parameters = new ParameterListDeclaration(resolver, constructor.getGenericParameterTypes());
            this.body = null;
            this.bodyRequirements = new Requirement[0];
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to read details of " + MethodDeclaration.toDebugString(constructor), t);
        }
    }

    public MethodDeclaration(ClassResolver resolver, Method method) {
        super(resolver);
        String name = MPLType.getName(method);
        String alias = Resolver.resolveMethodAlias(method, name);
        if (name.equals(alias)) {
            alias = null;
        }
        try {
            Type[] genericParameterTypes;
            Class<?> returnType;
            this.method = method;
            this.constructor = null;
            this.isRecordFieldChanger = false;
            this.modifiers = new ModifierDeclaration(resolver, method.getModifiers() & 0xFFFFFFBF);
            this.name = new NameDeclaration(resolver, name, alias);
            try {
                returnType = method.getGenericReturnType();
                genericParameterTypes = method.getGenericParameterTypes();
            }
            catch (TypeNotPresentException | GenericSignatureFormatError | MalformedParameterizedTypeException err) {
                returnType = method.getReturnType();
                genericParameterTypes = method.getParameterTypes();
                MountiplexUtil.LOGGER.warning("Generic type information about method " + MethodDeclaration.toDebugString(method) + " is corrupt: " + err.getMessage() + " - using a non-generic fallback representation");
            }
            this.returnType = TypeDeclaration.fromType(resolver, returnType);
            this.parameters = new ParameterListDeclaration(resolver, genericParameterTypes);
            this.body = null;
            this.bodyRequirements = new Requirement[0];
        }
        catch (Throwable t) {
            throw new IllegalStateException("Failed to read details of " + MethodDeclaration.toDebugString(method), t);
        }
    }

    public MethodDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    public MethodDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver, declaration);
        this.method = null;
        this.constructor = null;
        this.modifiers = this.nextModifier();
        Declaration.BaseDeclarationParserContext parserContext = new Declaration.BaseDeclarationParserContext();
        this.getParserPostfix().trimGenericTypes();
        this.returnType = this.nextType();
        this.name = this.nextName();
        this.parameters = this.nextParameterList();
        this.isRecordFieldChanger = this.name.value().equals("<record_changer>");
        this.getParserPostfix().trimWhitespace(0);
        StringBuffer postfix = this.getPostfix();
        if (postfix != null && postfix.startsWith("{")) {
            StringBuilder bodyBuilder = new StringBuilder();
            int curlyBrackets = 0;
            boolean done = false;
            block0: while (true) {
                int cIdx;
                boolean inString = false;
                for (cIdx = 0; cIdx < postfix.length(); ++cIdx) {
                    char c = postfix.charAt(cIdx);
                    bodyBuilder.append(c);
                    if (c == '\n') {
                        ++cIdx;
                        while (cIdx < postfix.length() && postfix.charAt(cIdx) == ' ') {
                            bodyBuilder.append(' ');
                            ++cIdx;
                        }
                        break;
                    }
                    if (c == '\"') {
                        inString = !inString;
                        continue;
                    }
                    if (inString) continue;
                    if (c == '{') {
                        ++curlyBrackets;
                        continue;
                    }
                    if (c != '}' || --curlyBrackets != 0) continue;
                    done = true;
                    ++cIdx;
                    break;
                }
                postfix = postfix.substring(cIdx);
                this.setPostfix(postfix);
                if (done || this.getPostfix().length() == 0) break;
                while (true) {
                    if (!parserContext.runParsers(DeclarationParserGroups.BASE)) continue block0;
                    postfix = this.getPostfix();
                }
                break;
            }
            int lastIndent = bodyBuilder.lastIndexOf("\n");
            if (lastIndent != -1) {
                for (int lastIndentEnd = lastIndent + 1; lastIndentEnd < bodyBuilder.length() && bodyBuilder.charAt(lastIndentEnd) == ' '; lastIndentEnd += 2) {
                    bodyBuilder.insert(0, ' ');
                }
            }
            this.bodyRequirements = !this.getResolver().isGenerating() ? this.processRequirements(bodyBuilder) : new Requirement[0];
            this.body = SourceDeclaration.trimIndentation(bodyBuilder.toString());
        } else {
            this.body = null;
            this.bodyRequirements = new Requirement[0];
            if (postfix != null && postfix.startsWith(";")) {
                this.setPostfix(postfix.substring(1));
            }
        }
        this.getParserPostfix().trimWhitespace(0);
        if (this.getPostfix() != null) {
            this.setPostfix(this.getPostfix().prepend("\n"));
        }
    }

    private MethodDeclaration(MethodDeclaration original, NameDeclaration newName) {
        super(original.getResolver());
        this.method = original.method;
        this.constructor = original.constructor;
        this.isRecordFieldChanger = original.isRecordFieldChanger;
        this.modifiers = original.modifiers;
        this.returnType = original.returnType;
        this.name = newName;
        this.parameters = original.parameters;
        this.body = original.body;
        this.bodyRequirements = original.bodyRequirements;
    }

    private MethodDeclaration(MethodDeclaration original, ParameterListDeclaration parameters) {
        super(original.getResolver());
        this.method = original.method;
        this.constructor = original.constructor;
        this.isRecordFieldChanger = original.isRecordFieldChanger;
        this.modifiers = original.modifiers;
        this.returnType = original.returnType;
        this.name = original.name;
        this.parameters = parameters;
        this.body = original.body;
        this.bodyRequirements = original.bodyRequirements;
    }

    @Override
    public double similarity(Declaration other) {
        if (!(other instanceof MethodDeclaration)) {
            return 0.0;
        }
        MethodDeclaration m = (MethodDeclaration)other;
        return 0.1 * this.modifiers.similarity(m.modifiers) + 0.3 * this.name.similarity(m.name) + 0.3 * this.returnType.similarity(m.returnType) + 0.3 * this.parameters.similarity(m.parameters);
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration)declaration;
            if (!(this.name.match(method.name) && this.returnType.match(method.returnType) && this.parameters.match(method.parameters))) {
                return false;
            }
            return this.modifiers.isStatic() == method.modifiers.isStatic();
        }
        return false;
    }

    public boolean matchSignature(Declaration declaration) {
        if (declaration instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration)declaration;
            return this.modifiers.match(method.modifiers) && this.returnType.match(method.returnType) && this.parameters.match(method.parameters);
        }
        return false;
    }

    public Class<?> getDeclaringClass() {
        if (this.body == null && this.method != null) {
            return this.method.getDeclaringClass();
        }
        if (this.body == null && this.constructor != null) {
            return this.constructor.getDeclaringClass();
        }
        return this.getResolver().getDeclaredClass();
    }

    public String getASMInvokeDescriptor() {
        Class[] params;
        if (this.modifiers.isStatic()) {
            params = new Class[this.parameters.parameters.length];
            for (int i = 0; i < this.parameters.parameters.length; ++i) {
                params[i] = MethodDeclaration.getAccessibleType(this.parameters.parameters[i].type);
            }
        } else {
            params = new Class[this.parameters.parameters.length + 1];
            params[0] = MethodDeclaration.getAccessibleType(this.getDeclaringClass());
            for (int i = 0; i < this.parameters.parameters.length; ++i) {
                params[i + 1] = MethodDeclaration.getAccessibleType(this.parameters.parameters[i].type);
            }
        }
        return MPLType.getMethodDescriptor(this.returnType.type, params);
    }

    private static Class<?> getAccessibleType(TypeDeclaration type) {
        if (type == null) {
            throw new IllegalArgumentException("Input type is null");
        }
        if (!type.isResolved()) {
            throw new IllegalArgumentException("Input type " + type + " was not resolved");
        }
        return MethodDeclaration.getAccessibleType(type.type);
    }

    private static Class<?> getAccessibleType(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Input type is null");
        }
        return Resolver.getMeta(type).isPublic ? type : Object.class;
    }

    @Override
    public String toString(boolean identity) {
        if (!this.isValid()) {
            return "??[" + this._initialDeclaration + "]??";
        }
        String m = this.modifiers.toString(identity);
        String t = this.returnType.toString(identity);
        String n = this.name.toString(identity);
        String p = this.parameters.toString(identity);
        if (m.length() > 0) {
            return m + " " + t + " " + n + p + ";";
        }
        return t + " " + n + p + ";";
    }

    @Override
    public boolean isResolved() {
        return this.modifiers.isResolved() && this.returnType.isResolved() && this.name.isResolved() && this.parameters.isResolved();
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
        str.append(indent).append("Method {\n");
        str.append(indent).append("  declaration=").append(this._initialDeclaration).append('\n');
        str.append(indent).append("  postfix=").append(this.getPostfix()).append('\n');
        this.modifiers.debugString(str, indent + "  ");
        this.returnType.debugString(str, indent + "  ");
        this.name.debugString(str, indent + "  ");
        this.parameters.debugString(str, indent + "  ");
        str.append(indent).append("}\n");
    }

    @Override
    public void modifyBodyRequirement(Requirement requirement, StringBuilder body, String instanceName, String requirementName, int instanceStartIdx, int nameEndIdx) {
        int firstOpenIndex;
        Class<?> methodDeclaringClass = this.getDeclaringClass();
        boolean canCallDirectly = false;
        if (methodDeclaringClass != null && Resolver.isPublic(methodDeclaringClass)) {
            if (this.method != null) {
                canCallDirectly = Modifier.isPublic(this.method.getModifiers());
            } else if (this.constructor != null) {
                canCallDirectly = Modifier.isPublic(this.constructor.getModifiers());
            }
        }
        if (canCallDirectly) {
            if (this.returnType.cast != null) {
                canCallDirectly = false;
            } else {
                for (ParameterDeclaration param : this.parameters.parameters) {
                    if (param.type.cast == null) continue;
                    canCallDirectly = false;
                    break;
                }
            }
        }
        if (canCallDirectly) {
            MethodBodyBuilder replacement = new MethodBodyBuilder();
            if (this.method != null) {
                if (this.modifiers.isStatic()) {
                    replacement.append(MPLType.getName(this.method.getDeclaringClass()));
                } else {
                    replacement.append(instanceName);
                }
                replacement.append('.');
                replacement.append("MPL_NOREMAP$");
                replacement.append(MPLType.getName(this.method));
            } else if (this.constructor != null) {
                replacement.append("new ");
                replacement.append("MPL_NOREMAP$");
                replacement.append(MPLType.getName(this.constructor.getDeclaringClass()));
            }
            body.replace(instanceStartIdx, nameEndIdx, replacement.toString());
            return;
        }
        for (firstOpenIndex = nameEndIdx; firstOpenIndex < body.length(); ++firstOpenIndex) {
            char c = body.charAt(firstOpenIndex);
            if (c == ' ') {
                continue;
            }
            if (c == '(') break;
            firstOpenIndex = -1;
            break;
        }
        if (firstOpenIndex == -1) {
            if (this.getResolver().getLogErrors()) {
                MountiplexUtil.LOGGER.warning("Requirement refers to method but is used as field");
                MountiplexUtil.LOGGER.warning("Method body: " + instanceName + "#" + requirementName);
            }
        } else {
            MethodBodyBuilder replacement = new MethodBodyBuilder();
            replacement.append("this.").append(requirementName);
            replacement.append('(');
            replacement.append(instanceName);
            for (int i = firstOpenIndex + 1; i < body.length(); ++i) {
                char c = body.charAt(i);
                if (c == ' ') continue;
                if (c == ')') break;
                replacement.append(", ");
                break;
            }
            body.replace(instanceStartIdx, firstOpenIndex + 1, replacement.toString());
        }
        requirement.setProperty("generateMethod");
    }

    @Override
    public void addAsRequirement(ExtendedClassWriter<?> writer, Requirement requirement, String name) throws CannotCompileException, NotFoundException {
        ParameterDeclaration param;
        int i;
        boolean isVarArgsInvoke;
        if (!requirement.hasProperty("generateMethod")) {
            return;
        }
        MethodBodyBuilder methodBody = new MethodBodyBuilder();
        methodBody.append("private final ");
        methodBody.append(ReflectionUtil.getAccessibleTypeName(this.returnType.exposed().type));
        methodBody.append(' ').append(name).append('(');
        methodBody.append("Object instance");
        for (int i2 = 0; i2 < this.parameters.parameters.length; ++i2) {
            ParameterDeclaration param2 = this.parameters.parameters[i2];
            methodBody.append(", ");
            methodBody.appendAccessibleTypeName(param2.type.exposed().type);
            methodBody.append(' ').append(param2.name.real());
            if ((param2.type.cast == null || param2.type.cast.type == Object.class) && !param2.type.isPrimitive) continue;
            methodBody.append("_conv_input");
        }
        methodBody.append(") {\n");
        String methodFieldName = name + "_method";
        FastMethod fastMethod = new FastMethod();
        fastMethod.init(this);
        writer.visitSingletonField(methodFieldName, FastMethod.class, fastMethod);
        boolean bl = isVarArgsInvoke = this.parameters.parameters.length > 5;
        if (isVarArgsInvoke) {
            methodBody.append("  Object[] ").append(name).append("_input_args");
            methodBody.append(" = new Object[").append(this.parameters.parameters.length).append("];\n");
        }
        for (i = 0; i < this.parameters.parameters.length; ++i) {
            boolean hasConversion;
            param = this.parameters.parameters[i];
            boolean bl2 = hasConversion = param.type.cast != null && param.type.cast.type != Object.class;
            if (isVarArgsInvoke) {
                methodBody.append("  ").appendFieldName(name, "_input_args").append('[').append(i).append("] = ");
            } else if (hasConversion || param.type.isPrimitive) {
                methodBody.append("  Object ").append(param.name.real()).append(" = ");
            }
            if (param.type.cast == null && param.type.isPrimitive) {
                methodBody.appendBoxPrimitive(param.type.type, param.name.real(), "_conv_input").appendEnd();
                continue;
            }
            if (!hasConversion) {
                if (!isVarArgsInvoke) continue;
                methodBody.append(param.name.real()).appendEnd();
                continue;
            }
            String converterFieldName = name + "_conv_" + param.name.real();
            Converter<Object, Object> converter = Conversion.find(param.type.cast, param.type);
            if (converter == null) {
                throw new RuntimeException("Failed to find converter for parameter " + param.name.real() + " of method " + name + " (" + param.type.cast.toString(true) + " -> " + param.type.toString(true) + ")");
            }
            writer.visitSingletonField(converterFieldName, Converter.class, converter);
            methodBody.append("this.").append(converterFieldName);
            methodBody.append(".convertInput(");
            if (param.type.cast.isPrimitive) {
                methodBody.appendBoxPrimitive(param.type.cast.type, param.name.real(), "_conv_input");
            } else {
                methodBody.appendFieldName(param.name.real(), "_conv_input");
            }
            methodBody.append(')').appendEnd();
        }
        if (!this.returnType.type.equals(Void.TYPE)) {
            methodBody.append("  ");
            if (this.returnType.cast != null && this.returnType.cast.type == Object.class) {
                methodBody.append("Object ");
                methodBody.appendFieldName(name, "_return").append(" = ");
            } else if (this.returnType.cast != null) {
                methodBody.append("Object ");
                methodBody.appendFieldName(name, "_return_conv_input").append(" = ");
            } else if (this.returnType.isPrimitive) {
                Class<?> boxedType = BoxedType.getBoxedType(this.returnType.type);
                methodBody.appendTypeName(boxedType).append(' ');
                methodBody.appendFieldName(name, "_return").append(" = ");
                methodBody.appendTypeCast(boxedType);
            } else {
                methodBody.appendAccessibleTypeName(this.returnType.type).append(' ');
                methodBody.appendFieldName(name, "_return").append(" = ");
                methodBody.appendAccessibleTypeCast(this.returnType.type);
            }
        }
        methodBody.append("this.").append(methodFieldName);
        if (isVarArgsInvoke) {
            methodBody.append(".invokeVA(instance, ").appendFieldName(name, "_input_args").append(')').appendEnd();
        } else {
            methodBody.append(".invoke(instance");
            for (i = 0; i < this.parameters.parameters.length; ++i) {
                param = this.parameters.parameters[i];
                methodBody.append(", ").append(param.name.real());
            }
            methodBody.append(')').appendEnd();
        }
        if (this.returnType.cast != null && this.returnType.cast.type != Object.class) {
            String converterFieldName = name + "_conv_return";
            Converter<Object, Object> converter = Conversion.find(this.returnType, this.returnType.cast);
            if (converter == null) {
                throw new RuntimeException("Failed to find converter for return value  of method " + name + " (" + this.returnType.toString(true) + " -> " + this.returnType.cast.toString(true) + ")");
            }
            writer.visitSingletonField(converterFieldName, Converter.class, converter);
            Class<?> rType = this.returnType.cast.isPrimitive ? BoxedType.getBoxedType(this.returnType.cast.type) : this.returnType.cast.type;
            methodBody.append("  ").appendAccessibleTypeName(rType).append(' ').appendFieldName(name, "_return").append(" = ").appendAccessibleTypeCast(rType).append("this.").append(converterFieldName).append(".convertInput(").appendFieldName(name, "_return_conv_input").append(')').appendEnd();
        }
        if (!this.returnType.type.equals(Void.TYPE)) {
            methodBody.append("  return ");
            methodBody.appendFieldName(name, "_return");
            if (this.returnType.exposed().isPrimitive) {
                methodBody.appendUnboxPrimitive(this.returnType.exposed().type);
            }
            methodBody.appendEnd();
        }
        methodBody.append("}");
        writer.addJavassist(invokerClass -> {
            try {
                CtMethod method = MPLCtNewMethod.make(methodBody.toString(), invokerClass);
                invokerClass.addMethod(method);
            }
            catch (CannotCompileException ex) {
                MountiplexUtil.LOGGER.severe("Failed to compile method: " + methodBody.toString());
                throw ex;
            }
        });
    }

    @Override
    public MethodDeclaration discover() {
        if (!this.isValid() || !this.isResolved()) {
            return null;
        }
        if (this.body != null) {
            return this;
        }
        if (this.getResolver().getDeclaredClass() == null) {
            return null;
        }
        TypeDeclaration typeDec = TypeDeclaration.parse(this.getResolver().getDeclaredClassName());
        if (this.name.isAliasOnly()) {
            MethodDeclaration result;
            TypeDeclaration[] cDec = Resolver.resolveClassDeclaration(this.getResolver().getDeclaredClassName(), this.getResolver().getDeclaredClass());
            if (cDec != null && (result = cDec.findMethod(this)) != null) {
                return result;
            }
            if (typeDec != null) {
                for (TypeDeclaration superType : typeDec.getSuperTypes()) {
                    MethodDeclaration result2;
                    ClassDeclaration cDec2 = Resolver.resolveClassDeclaration(superType.typePath, superType.type);
                    if (cDec2 == null || (result2 = cDec2.findMethod(this)) == null) continue;
                    return result2;
                }
            }
        }
        if (this.isRecordFieldChanger) {
            ParameterListDeclaration newParams = this.parameters.renameParameters(param -> Resolver.resolveMethodName(this.getResolver().getDeclaredClass(), param.name.value(), new Class[0]));
            return new MethodDeclaration(this, newParams);
        }
        MethodDeclaration nameResolved = this.resolveName();
        if (nameResolved.name.value().equals("<init>")) {
            try {
                this.constructor = this.getResolver().getDeclaredClass().getDeclaredConstructor(nameResolved.parameters.toParamArray());
                return this;
            }
            catch (NoSuchMethodException | SecurityException result) {}
        } else {
            if (nameResolved.method != null) {
                return nameResolved;
            }
            try {
                Method method = MPLType.getDeclaredMethod(this.getResolver().getDeclaredClass(), nameResolved.name.value(), nameResolved.parameters.toParamArray());
                MethodDeclaration result = new MethodDeclaration(this.getResolver(), method);
                if (result.match(nameResolved) && this.checkPublic(method)) {
                    nameResolved.method = method;
                    return nameResolved;
                }
            }
            catch (NoSuchMethodException | SecurityException method) {
                // empty catch block
            }
            ClassDeclaration cDec = new ClassDeclaration(ClassResolver.DEFAULT, this.getResolver().getDeclaredClass());
            MethodDeclaration result = cDec.findMethod(nameResolved);
            if (result != null && this.checkPublic(result.method)) {
                this.method = result.method;
                return this;
            }
            for (TypeDeclaration superType : typeDec.getSuperTypes()) {
                ClassDeclaration cDec3 = new ClassDeclaration(ClassResolver.DEFAULT, superType.type);
                MethodDeclaration result3 = cDec3.findMethod(nameResolved);
                if (result3 == null || !this.checkPublic(result3.method)) continue;
                this.method = result3.method;
                return this;
            }
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
        Declaration[] alternatives = this.modifiers.isStatic() ? (MethodDeclaration[])ReflectionUtil.getAllMethods(declaringClass).filter(m -> Modifier.isStatic(m.getModifiers())).map(m -> new MethodDeclaration(this.getResolver(), (Method)m)).toArray(MethodDeclaration[]::new) : (MethodDeclaration[])ReflectionUtil.getAllMethods(declaringClass).filter(m -> !Modifier.isStatic(m.getModifiers())).filter(ReflectionUtil.createDuplicateMethodFilter()).map(m -> new MethodDeclaration(this.getResolver(), (Method)m)).toArray(MethodDeclaration[]::new);
        MethodDeclaration.sortSimilarity((Declaration)this, (Declaration[])alternatives);
        FieldLCSResolver.logAlternatives((String)"method", (Declaration[])alternatives, (Declaration)this, (boolean)true);
    }

    @Override
    public String getTemplateLogIdentity() {
        StringBuilder str = new StringBuilder();
        if (this.modifiers.isStatic()) {
            str.append("static ");
        }
        str.append("method ");
        str.append(this.returnType.exposed().typeName);
        str.append(' ');
        str.append(this.name.toString());
        str.append('(');
        boolean first = true;
        for (ParameterDeclaration param : this.parameters.parameters) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(param.name.toString());
        }
        str.append(')');
        return str.toString();
    }

    private boolean checkPublic(Method method) {
        return !this.modifiers.isPublic() || Modifier.isPublic(method.getModifiers());
    }

    private Requirement[] processRequirements(StringBuilder body) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (int seek = 1; seek < body.length(); ++seek) {
            int instanceStartIdx;
            int instanceEndIdx;
            if (body.charAt(seek) != '#' || seek >= 1 && body.charAt(seek - 1) == '#' || seek + 1 < body.length() && body.charAt(seek + 1) == '#') continue;
            int nameEndIdx = -1;
            boolean isMethod = false;
            boolean isField = false;
            for (int i = seek + 1; i < body.length(); ++i) {
                char c = body.charAt(i);
                if (c == '(') {
                    nameEndIdx = i;
                    isMethod = true;
                    break;
                }
                if (Character.isLetterOrDigit(c) || c == '_' || c == '$') continue;
                nameEndIdx = i;
                isField = true;
                break;
            }
            String name = body.substring(seek + 1, nameEndIdx);
            Object foundRequirement = null;
            for (Object req : result) {
                if (!((Requirement)req).name.equals(name)) continue;
                foundRequirement = req;
                break;
            }
            if (foundRequirement == null) {
                Object req;
                Declaration parsedDeclaration = null;
                req = this.getResolver().getRequirements().iterator();
                while (req.hasNext()) {
                    Requirement req2 = (Requirement)req.next();
                    if (!req2.name.equals(name)) continue;
                    if (isMethod && req2.declaration instanceof MethodDeclaration) {
                        parsedDeclaration = req2.declaration;
                        break;
                    }
                    if (!isField || !(req2.declaration instanceof FieldDeclaration)) continue;
                    parsedDeclaration = req2.declaration;
                    break;
                }
                if (parsedDeclaration == null) continue;
                String declaredClassName = parsedDeclaration.getResolver().getDeclaredClassName();
                if (parsedDeclaration.getResolver().getDeclaredClass() == null) {
                    if (!this.getResolver().getLogErrors()) continue;
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Requirement declaration declaring Class not found: ??" + declaredClassName + "??");
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Declaration: " + parsedDeclaration.toString());
                    continue;
                }
                if (!parsedDeclaration.isResolved()) {
                    if (!this.getResolver().getLogErrors()) continue;
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Requirement declaration could not be resolved for: " + declaredClassName);
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Declaration: " + parsedDeclaration.toString());
                    continue;
                }
                Declaration foundDeclaration = parsedDeclaration.discover();
                if (foundDeclaration == null) {
                    if (!this.getResolver().getLogErrors()) continue;
                    parsedDeclaration.discoverAlternatives();
                    continue;
                }
                foundRequirement = new Requirement(name, foundDeclaration);
                result.add(foundRequirement);
            }
            boolean isStatic = false;
            if (((Requirement)foundRequirement).declaration instanceof MethodDeclaration) {
                MethodDeclaration mDec = (MethodDeclaration)((Requirement)foundRequirement).declaration;
                boolean bl = isStatic = mDec.constructor != null || mDec.modifiers.isStatic();
            }
            if (((Requirement)foundRequirement).declaration instanceof FieldDeclaration && ((FieldDeclaration)((Requirement)foundRequirement).declaration).modifiers.isStatic()) {
                isStatic = true;
            }
            if (isStatic && !Character.isLetterOrDigit(body.charAt(instanceEndIdx))) {
                instanceStartIdx = ++instanceEndIdx;
            } else {
                for (instanceEndIdx = seek - 1; instanceEndIdx > 0 && body.charAt(instanceEndIdx) == ' '; --instanceEndIdx) {
                }
                int parenthesesCtr = 0;
                for (instanceStartIdx = instanceEndIdx++; instanceStartIdx >= 0; --instanceStartIdx) {
                    char c = body.charAt(instanceStartIdx);
                    if (c == ')') {
                        ++parenthesesCtr;
                        continue;
                    }
                    if (c == '(' ? --parenthesesCtr < 0 : !Character.isLetterOrDigit(c) && c != '.' && c != '$' && c != '_' && parenthesesCtr == 0) break;
                }
                ++instanceStartIdx;
            }
            String instanceName = isStatic ? "null" : body.substring(instanceStartIdx, instanceEndIdx);
            ((Requirement)foundRequirement).declaration.modifyBodyRequirement((Requirement)foundRequirement, body, instanceName, name, instanceStartIdx, nameEndIdx);
        }
        return result.toArray(new Requirement[result.size()]);
    }

    public MethodDeclaration resolveName() {
        if (!this.isResolved() || this.getResolver().getDeclaredClass() == null || this.body != null || this.name.value().equals("<init>") || this.isRecordFieldChanger) {
            return this;
        }
        Remapping.MethodRemapping remapping = this.getResolver().getRemappings().find(this);
        if (remapping != null) {
            MethodDeclaration remappedSelf = new MethodDeclaration(this, this.name.rename(remapping.declaration.name));
            remappedSelf.method = remapping.method;
            return remappedSelf;
        }
        String resolvedName = Resolver.resolveMethodName(this.getResolver().getDeclaredClass(), this.name.value(), this.parameters.toParamArray());
        if (resolvedName != null && !resolvedName.equals(this.name.value())) {
            return new MethodDeclaration(this, this.name.rename(resolvedName));
        }
        return this;
    }

    public MethodDeclaration setAlias(String alias) {
        if (alias.equals(this.name.alias())) {
            return this;
        }
        return new MethodDeclaration(this, new NameDeclaration(this.getResolver(), this.name.value(), alias));
    }

    protected String getAccessedName() {
        return this.method != null ? MPLType.getName(this.method) : this.name.value();
    }

    private static String toDebugString(Executable executable) {
        if (executable == null) {
            return "<null>";
        }
        try {
            return executable.toGenericString();
        }
        catch (Throwable throwable) {
            try {
                return executable.toString();
            }
            catch (Throwable throwable2) {
                StringBuilder str = new StringBuilder();
                if (executable instanceof Method) {
                    try {
                        str.append(((Method)executable).getReturnType());
                        str.append(' ');
                    }
                    catch (Throwable throwable3) {
                        // empty catch block
                    }
                }
                try {
                    str.append(executable.getDeclaringClass());
                    str.append('.');
                }
                catch (Throwable throwable4) {
                    // empty catch block
                }
                try {
                    str.append(executable.getName());
                }
                catch (Throwable throwable5) {
                    // empty catch block
                }
                str.append('(');
                try {
                    Class<?>[] params = executable.getParameterTypes();
                    for (int i = 0; i < params.length; ++i) {
                        if (i > 0) {
                            str.append(", ");
                        }
                        str.append(params[i]);
                    }
                }
                catch (Throwable t) {
                    str.append("???");
                }
                str.append(')');
                return str.toString();
            }
        }
    }
}

