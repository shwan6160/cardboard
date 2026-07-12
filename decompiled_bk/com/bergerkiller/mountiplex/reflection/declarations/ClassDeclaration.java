/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.ConstructorDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldLCSResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodMatchResolver;
import com.bergerkiller.mountiplex.reflection.declarations.ModifierDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParserGroups;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.ClassDeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class ClassDeclaration
extends Declaration {
    public final ModifierDeclaration modifiers;
    public final TypeDeclaration base;
    public final TypeDeclaration type;
    public final ClassDeclaration[] subclasses;
    public final ConstructorDeclaration[] constructors;
    public final MethodDeclaration[] methods;
    public final FieldDeclaration[] fields;
    public final String code;
    public final List<String> codeImports;
    public final boolean is_interface;

    public ClassDeclaration(ClassResolver resolver, Class<?> type) {
        super(resolver.clone());
        Class<?> superType = type.getSuperclass();
        this.is_interface = type.isInterface();
        this.base = superType == null ? null : TypeDeclaration.fromClass(superType);
        this.type = TypeDeclaration.fromClass(type);
        this.modifiers = new ModifierDeclaration(this.getResolver(), type.getModifiers());
        this.code = "";
        this.codeImports = Collections.emptyList();
        this.getResolver().setDeclaredClass(type);
        LinkedList<ConstructorDeclaration> constructors = new LinkedList<ConstructorDeclaration>();
        LinkedList methods = new LinkedList();
        LinkedList<FieldDeclaration> fields = new LinkedList<FieldDeclaration>();
        LinkedList<ClassDeclaration> classes = new LinkedList<ClassDeclaration>();
        if (type.isEnum()) {
            for (Object obj : type.getEnumConstants()) {
                fields.add(new FieldDeclaration(this.getResolver(), (Enum)obj));
            }
        }
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            constructors.add(new ConstructorDeclaration(this.getResolver(), constructor));
        }
        for (Field field : type.getDeclaredFields()) {
            fields.add(new FieldDeclaration(this.getResolver(), field));
        }
        ReflectionUtil.getDeclaredMethods(type).filter(ReflectionUtil.createDuplicateMethodFilter()).map(m -> new MethodDeclaration(this.getResolver(), (Method)m)).forEachOrdered(methods::add);
        for (Class<?> clazz : type.getDeclaredClasses()) {
            classes.add(new ClassDeclaration(this.getResolver(), clazz));
        }
        this.constructors = constructors.toArray(new ConstructorDeclaration[constructors.size()]);
        this.methods = methods.toArray(new MethodDeclaration[methods.size()]);
        this.fields = fields.toArray(new FieldDeclaration[fields.size()]);
        this.subclasses = classes.toArray(new ClassDeclaration[classes.size()]);
    }

    public ClassDeclaration(ClassResolver resolver, String declaration) {
        this(resolver, StringBuffer.of(declaration));
    }

    public ClassDeclaration(ClassResolver resolver, StringBuffer declaration) {
        super(resolver.clone(), declaration);
        this.modifiers = this.nextModifier();
        if (!this.isValid()) {
            this.code = "";
            this.codeImports = Collections.emptyList();
            this.base = null;
            this.type = null;
            this.subclasses = new ClassDeclaration[0];
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            this.is_interface = false;
            return;
        }
        StringBuffer postfix = this.getPostfix();
        this.is_interface = postfix.startsWith("interface ");
        if (!this.is_interface && !postfix.startsWith("class ")) {
            this.base = null;
            this.type = null;
            this.code = "";
            this.codeImports = Collections.emptyList();
            this.subclasses = new ClassDeclaration[0];
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            this.setInvalid();
            return;
        }
        this.setPostfix(postfix.substring(this.is_interface ? 10 : 6));
        this.type = this.nextType();
        if (!this.isValid()) {
            this.base = null;
            this.code = "";
            this.codeImports = Collections.emptyList();
            this.subclasses = new ClassDeclaration[0];
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            return;
        }
        this.getResolver().setDeclaredClass(this.type.type, this.type.typePath);
        postfix = this.getPostfix();
        if (postfix.startsWith("extends ")) {
            this.setPostfix(postfix.substring(8));
            this.base = this.nextType();
        } else {
            this.base = null;
        }
        postfix = this.getPostfix();
        boolean foundClassStart = false;
        int startIdx = -1;
        for (int cidx = 0; cidx < postfix.length(); ++cidx) {
            char c = postfix.charAt(cidx);
            if (c == '{') {
                foundClassStart = true;
                continue;
            }
            if (!foundClassStart || MountiplexUtil.containsChar(c, ParserStringBuffer.WHITESPACE_CHARACTERS)) continue;
            startIdx = cidx;
            break;
        }
        if (startIdx == -1) {
            this.code = "";
            this.codeImports = Collections.emptyList();
            this.subclasses = new ClassDeclaration[0];
            this.constructors = new ConstructorDeclaration[0];
            this.methods = new MethodDeclaration[0];
            this.fields = new FieldDeclaration[0];
            this.setInvalid();
            return;
        }
        this.getParserPostfix().trimWhitespace(startIdx);
        ParserContext parserContext = new ParserContext();
        LinkedList<ClassDeclaration> classes = new LinkedList<ClassDeclaration>();
        LinkedList<ConstructorDeclaration> constructors = new LinkedList<ConstructorDeclaration>();
        LinkedList<MethodDeclaration> methods = new LinkedList<MethodDeclaration>();
        LinkedList<FieldDeclaration> fields = new LinkedList<FieldDeclaration>();
        while ((postfix = this.getPostfix()) != null && postfix.length() > 0) {
            if (parserContext.runParsers(DeclarationParserGroups.CLASS)) continue;
            if (postfix.charAt(0) == '}') {
                this.getParserPostfix().trimWhitespace(1);
                break;
            }
            ClassDeclaration cldec = new ClassDeclaration(this.getResolver(), postfix);
            if (cldec.isValid()) {
                classes.add(cldec);
                this.setPostfix(cldec.getPostfix());
                this.getParserPostfix().trimWhitespace(0);
                continue;
            }
            Declaration dec = this.getParserPostfix().detectMemberDeclaration(this.getResolver());
            if (dec instanceof MethodDeclaration) {
                methods.add((MethodDeclaration)dec);
                continue;
            }
            if (dec instanceof ConstructorDeclaration) {
                constructors.add((ConstructorDeclaration)dec);
                continue;
            }
            if (!(dec instanceof FieldDeclaration)) break;
            fields.add((FieldDeclaration)dec);
        }
        this.code = parserContext.codeStr.toString();
        this.codeImports = parserContext.codeImports.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(parserContext.codeImports);
        this.subclasses = classes.toArray(new ClassDeclaration[classes.size()]);
        this.constructors = constructors.toArray(new ConstructorDeclaration[constructors.size()]);
        this.methods = methods.toArray(new MethodDeclaration[methods.size()]);
        this.fields = fields.toArray(new FieldDeclaration[fields.size()]);
        if (this.type.isResolved()) {
            this.resolveFields();
            this.resolveMethods();
            this.resolveConstructors();
        }
    }

    private void resolveFields() {
        int i;
        Field[] realRefFields;
        try {
            realRefFields = this.type.type.getDeclaredFields();
        }
        catch (Throwable t) {
            if (this.getResolver().getLogErrors()) {
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to get declared fields of " + this.type.typePath, t);
            }
            return;
        }
        FieldDeclaration[] realFields = new FieldDeclaration[realRefFields.length];
        for (int i2 = 0; i2 < realFields.length; ++i2) {
            try {
                realFields[i2] = new FieldDeclaration(this.getResolver(), realRefFields[i2]);
                continue;
            }
            catch (Throwable t) {
                if (!this.getResolver().getLogErrors()) continue;
                MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to read field " + realRefFields[i2], t);
            }
        }
        FieldDeclaration[] nameResolvedFields = new FieldDeclaration[this.fields.length];
        for (i = 0; i < nameResolvedFields.length; ++i) {
            nameResolvedFields[i] = this.fields[i].resolveName();
        }
        FieldLCSResolver.resolve(nameResolvedFields, realFields);
        for (i = 0; i < nameResolvedFields.length; ++i) {
            this.fields[i].copyFieldFrom(nameResolvedFields[i]);
        }
    }

    private void resolveMethods() {
        MethodMatchResolver.match(this.type.type, this.getResolver(), this.methods);
    }

    private void resolveConstructors() {
        int i;
        Constructor<?>[] realRefConstructors = this.type.type.getDeclaredConstructors();
        Declaration[] realConstructors = new ConstructorDeclaration[realRefConstructors.length];
        for (i = 0; i < realConstructors.length; ++i) {
            try {
                realConstructors[i] = new ConstructorDeclaration(this.getResolver(), realRefConstructors[i]);
                continue;
            }
            catch (Throwable t) {
                if (!this.getResolver().getLogErrors()) continue;
                MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to read constructor " + realRefConstructors[i], t);
            }
        }
        for (i = 0; i < this.constructors.length; ++i) {
            ConstructorDeclaration constructor = this.constructors[i];
            boolean found = false;
            for (int j = 0; j < realConstructors.length; ++j) {
                if (!realConstructors[j].match(constructor)) continue;
                constructor.constructor = realConstructors[j].constructor;
                found = true;
                break;
            }
            if (found || constructor.modifiers.isOptional()) continue;
            FieldLCSResolver.logAlternatives((String)"constructor", (Declaration[])realConstructors, (Declaration)constructor, (boolean)false);
        }
    }

    public MethodDeclaration findMethod(MethodDeclaration declaration) {
        for (MethodDeclaration mDec : this.methods) {
            if (!mDec.match(declaration)) continue;
            return mDec;
        }
        Remapping.MethodRemapping remapping = this.getResolver().getRemappings().find(declaration);
        if (remapping != null) {
            return remapping.declaration;
        }
        return null;
    }

    public String resolveMethodAlias(Method method) {
        if (Modifier.isPrivate(method.getModifiers())) {
            if (!this.type.type.equals(method.getDeclaringClass())) {
                return null;
            }
            for (MethodDeclaration mDec : this.methods) {
                if (mDec.method == null || !mDec.method.equals(method)) continue;
                return mDec.name.real();
            }
            return null;
        }
        Class<?>[] mParams = method.getParameterTypes();
        for (MethodDeclaration mDec : this.methods) {
            if (mDec.method == null || Modifier.isPrivate(mDec.method.getModifiers()) || !MPLType.getName(mDec.method).equals(MPLType.getName(method))) continue;
            boolean paramsMatch = true;
            Class<?>[] mDecParams = mDec.method.getParameterTypes();
            if (mDecParams.length != mParams.length) continue;
            for (int i = 0; i < mParams.length; ++i) {
                if (mParams[i].equals(mDecParams[i])) continue;
                paramsMatch = false;
                break;
            }
            if (!paramsMatch) continue;
            return mDec.name.real();
        }
        return null;
    }

    @Override
    public boolean isResolved() {
        return this.type.isResolved();
    }

    @Override
    public double similarity(Declaration other) {
        return 0.0;
    }

    @Override
    public boolean match(Declaration declaration) {
        if (declaration instanceof ClassDeclaration) {
            return ((ClassDeclaration)declaration).type.match(this.type);
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public String toString(boolean identity) {
        String str = this.modifiers.toString(identity);
        if (str.length() > 0) {
            str = str + " ";
        }
        str = str + (this.is_interface ? "interface " : "class ");
        str = this.type == null ? str + "<nulltype>" : str + this.type.toString(identity);
        if (this.base != null) {
            str = str + " extends " + this.base.toString(identity);
        }
        str = str + " {\n";
        for (FieldDeclaration fieldDeclaration : this.fields) {
            str = str + "    " + fieldDeclaration.toString(identity) + "\n";
        }
        for (Declaration declaration : this.constructors) {
            str = str + "    " + ((ConstructorDeclaration)declaration).toString(identity) + "\n";
        }
        for (Declaration declaration : this.methods) {
            str = str + "    " + ((MethodDeclaration)declaration).toString(identity) + "\n";
        }
        if (this.subclasses.length > 0) {
            void var6_15;
            void var6_13;
            str = str + "\n";
            String subclassesStr = "";
            Object[] objectArray = this.subclasses;
            int n = objectArray.length;
            boolean bl = false;
            while (var6_13 < n) {
                ClassDeclaration cDec = objectArray[var6_13];
                subclassesStr = subclassesStr + cDec.toString(identity);
                subclassesStr = subclassesStr + "\n";
                ++var6_13;
            }
            objectArray = subclassesStr.split("\\r?\\n", -1);
            n = objectArray.length;
            boolean bl2 = false;
            while (var6_15 < n) {
                Object line = objectArray[var6_15];
                str = ((String)line).length() > 0 ? str + "    " + (String)line + "\n" : str + "\n";
                ++var6_15;
            }
        }
        str = str + "}";
        return str;
    }

    @Override
    public String getTemplateLogIdentity() {
        return "class " + this.type.typeName;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
    }

    private class ParserContext
    extends Declaration.BaseDeclarationParserContext
    implements ClassDeclarationParserContext {
        public StringBuilder codeStr;
        public List<String> codeImports;

        public ParserContext() {
            super(ClassDeclaration.this);
            this.codeStr = new StringBuilder();
            this.codeImports = new ArrayList<String>();
        }

        @Override
        public void appendCode(String code) {
            this.codeStr.append(SourceDeclaration.trimIndentation(code));
        }

        @Override
        public void addCodeImport(String importPath) {
            this.codeImports.add(importPath);
        }
    }
}

