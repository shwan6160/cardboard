/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ConstructorDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ModifierDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TemplateHandleBuilder;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

public class TemplateGenerator {
    private ClassDeclaration rootClassDec = null;
    private File rootDir = null;
    private String path = "";
    private StringBuilder builder = new StringBuilder();
    private Map<String, String> imports = new TreeMap<String, String>();
    private Set<String> codeImports = new TreeSet<String>();
    private int indent = 0;
    private Map<TypeDeclaration, TemplateGenerator> generatorPool = null;

    public void setClass(ClassDeclaration classDec) {
        this.rootClassDec = classDec;
    }

    public ClassDeclaration getClassType() {
        return this.rootClassDec;
    }

    public void setPool(Map<TypeDeclaration, TemplateGenerator> generatorPool) {
        this.generatorPool = generatorPool;
    }

    public void setRootDirectory(File rootDir) {
        this.rootDir = rootDir;
    }

    public void setPath(String path) {
        this.path = path.replace('/', '.');
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void generate() {
        block9: {
            Collection<String> allImports;
            this.builder = new StringBuilder();
            this.imports = new TreeMap<String, String>();
            this.imports.put("Template", Template.class.getName());
            this.indent = 0;
            String packagePath = this.path;
            this.addClass(this.rootClassDec);
            String resultStr = this.builder.toString();
            this.builder.setLength(0);
            this.addLine("package " + packagePath);
            this.addLine();
            String classRoot = this.path + "." + this.handleName(this.rootClassDec);
            if (this.codeImports.isEmpty()) {
                allImports = this.imports.values();
            } else {
                allImports = new TreeSet<String>(this.codeImports);
                allImports.addAll(this.imports.values());
            }
            for (String importPath : allImports) {
                if (importPath.startsWith(classRoot) || importPath.startsWith(this.path) && !importPath.substring(this.path.length() + 1).contains(".")) continue;
                this.addLine("import " + importPath);
            }
            this.builder.append(resultStr);
            String templateContents = this.builder.toString();
            try {
                File sourceFileDir = new File(this.rootDir, packagePath.replace('.', File.separatorChar));
                sourceFileDir.mkdirs();
                File sourceFile = new File(sourceFileDir, this.handleName(this.rootClassDec) + ".java");
                boolean changed = true;
                if (sourceFile.exists()) {
                    String originalContents = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
                    boolean bl = changed = !originalContents.equals(templateContents);
                }
                if (!changed) break block9;
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile));){
                    writer.write(templateContents);
                }
            }
            catch (Throwable t) {
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to generate template class " + classRoot, t);
            }
        }
    }

    private String findClassName(ClassDeclaration root, String path, TypeDeclaration type) {
        String new_path = path + "." + this.handleName(root);
        if (root.type.equals(type)) {
            return new_path;
        }
        for (ClassDeclaration sub : root.subclasses) {
            String result = this.findClassName(sub, new_path, type);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    /*
     * WARNING - void declaration
     */
    private void addClass(ClassDeclaration classDec) {
        boolean bl;
        void var8_30;
        String fName;
        String typeStr;
        String primTypeStr;
        if (classDec.type.typePath.equals("")) {
            return;
        }
        String extendedHandleType = "Template.Handle";
        TypeDeclaration baseType = classDec.base;
        if (baseType != null && this.generatorPool != null) {
            TemplateGenerator baseGen = this.generatorPool.get(baseType);
            if (baseGen != null) {
                Object[] path = baseGen.findClassName(baseGen.rootClassDec, baseGen.path, classDec.base);
                if (path != null) {
                    extendedHandleType = this.resolveImport((String)path);
                } else {
                    MountiplexUtil.LOGGER.severe("Failed to find super type template class: " + baseType.typePath);
                    MountiplexUtil.LOGGER.severe("With super template generator: " + baseGen.path);
                    MountiplexUtil.LOGGER.severe("At template " + classDec.type.typePath);
                }
            } else {
                MountiplexUtil.LOGGER.severe("Super type has no template: " + baseType.typePath);
                MountiplexUtil.LOGGER.severe("At template " + classDec.type.typePath);
            }
        }
        String classHeadStatic = "";
        if (classDec != this.rootClassDec) {
            classHeadStatic = "static ";
        }
        this.addLine();
        this.addComment("Instance wrapper handle for type <b>" + classDec.type.typePath + "</b>.\nTo access members without creating a handle type, use the static {@link #T} member.\nNew handles can be created from raw instances using {@link #createHandle(Object)}.");
        this.populateModifiers(classDec.modifiers);
        this.addLine("@Template.InstanceType(\"" + classDec.type.typePath + "\")");
        this.addLine("public abstract " + classHeadStatic + "class " + this.handleName(classDec) + " extends " + extendedHandleType + " {");
        this.addComment("@see " + this.className(classDec));
        this.addLine("public static final " + this.className(classDec) + " T = Template.Class.create(" + this.className(classDec) + ".class, " + classDec.getResolver().getClassDeclarationResolverName() + ")");
        for (FieldDeclaration fieldDeclaration : classDec.fields) {
            if (!fieldDeclaration.isEnum || fieldDeclaration.modifiers.isOptional()) continue;
            String typeStr2 = this.getFieldTypeStr(fieldDeclaration);
            String fName2 = fieldDeclaration.name.real();
            this.populateModifiers(fieldDeclaration.modifiers);
            this.addLine("public static final " + typeStr2 + " " + fName2 + " = T." + fName2 + ".getSafe()");
        }
        for (FieldDeclaration fieldDeclaration : classDec.fields) {
            if (fieldDeclaration.modifiers.isUnknown() || !fieldDeclaration.modifiers.isStatic() || !fieldDeclaration.modifiers.isFinal() || fieldDeclaration.isEnum || fieldDeclaration.modifiers.isOptional()) continue;
            primTypeStr = this.getPrimFieldType(fieldDeclaration);
            typeStr = this.getFieldTypeStr(fieldDeclaration);
            fName = fieldDeclaration.name.real();
            this.populateModifiers(fieldDeclaration.modifiers);
            this.addLine("public static final " + typeStr + " " + fName + " = T." + fName + ".get" + primTypeStr + "Safe()");
        }
        this.addLine("/* ============================================================================== */");
        this.addLine();
        this.addLine("public static " + this.handleName(classDec) + " createHandle(Object handleInstance) {");
        this.addLine("return T.createHandle(handleInstance)");
        this.addLine("}");
        for (Declaration declaration : classDec.constructors) {
            if (((ConstructorDeclaration)declaration).modifiers.isOptional()) continue;
            this.populateModifiers(((ConstructorDeclaration)declaration).modifiers);
            String cHeader = "public static final " + this.getExposedTypeStr(((ConstructorDeclaration)declaration).type) + " createNew";
            this.addLine(cHeader + this.getParamsBody(((ConstructorDeclaration)declaration).parameters) + " {");
            if (((ConstructorDeclaration)declaration).parameters.parameters.length <= 5) {
                this.addLine("return T." + ((ConstructorDeclaration)declaration).getName() + ".newInstance(" + this.getArgsBody(((ConstructorDeclaration)declaration).parameters) + ")");
            } else {
                this.addLine("return T." + ((ConstructorDeclaration)declaration).getName() + ".newInstanceVA(" + this.getArgsBody(((ConstructorDeclaration)declaration).parameters) + ")");
            }
            this.addLine("}");
        }
        this.addLine("/* ============================================================================== */");
        this.addLine();
        for (Declaration declaration : classDec.fields) {
            if (((FieldDeclaration)declaration).modifiers.isUnknown() || !((FieldDeclaration)declaration).modifiers.isStatic() || ((FieldDeclaration)declaration).modifiers.isFinal() || ((FieldDeclaration)declaration).isEnum || ((FieldDeclaration)declaration).modifiers.isOptional()) continue;
            primTypeStr = this.getPrimFieldType((FieldDeclaration)declaration);
            typeStr = this.getFieldTypeStr((FieldDeclaration)declaration);
            fName = ((FieldDeclaration)declaration).name.real();
            this.populateModifiers(((FieldDeclaration)declaration).modifiers);
            this.addLine("public static " + typeStr + " " + fName + "() {");
            this.addLine("return T." + ((FieldDeclaration)declaration).name.real() + ".get" + primTypeStr + "()");
            this.addLine("}");
            if (((FieldDeclaration)declaration).modifiers.isReadonly()) continue;
            this.populateModifiers(((FieldDeclaration)declaration).modifiers);
            this.addLine("public static void " + fName + "_set(" + typeStr + " value) {");
            this.addLine("T." + ((FieldDeclaration)declaration).name.real() + ".set" + primTypeStr + "(value)");
            this.addLine("}");
        }
        for (Declaration declaration : classDec.methods) {
            if (((MethodDeclaration)declaration).modifiers.isUnknown() || !((MethodDeclaration)declaration).modifiers.isStatic() || ((MethodDeclaration)declaration).modifiers.isOptional() || TemplateHandleBuilder.isCreateHandleMethod((MethodDeclaration)declaration)) continue;
            this.addStaticMethodBody((MethodDeclaration)declaration);
        }
        for (Declaration declaration : classDec.methods) {
            if (((MethodDeclaration)declaration).modifiers.isUnknown() || ((MethodDeclaration)declaration).modifiers.isStatic() || ((MethodDeclaration)declaration).modifiers.isOptional()) continue;
            this.addAbstractMethodBody((MethodDeclaration)declaration);
        }
        this.codeImports.addAll(classDec.codeImports);
        if (classDec.code.length() > 0) {
            for (String string : classDec.code.split("\n")) {
                if (string.trim().length() > 0) {
                    for (int i = 0; i < this.indent; ++i) {
                        this.builder.append("    ");
                    }
                    this.builder.append(string);
                }
                this.builder.append('\n');
            }
        }
        for (FieldDeclaration fieldDeclaration : classDec.fields) {
            if (fieldDeclaration.modifiers.isUnknown() || fieldDeclaration.modifiers.isStatic() || fieldDeclaration.isEnum || fieldDeclaration.modifiers.isOptional()) continue;
            String typeStr3 = this.getFieldTypeStr(fieldDeclaration);
            this.populateModifiers(fieldDeclaration.modifiers);
            this.addLine("public abstract " + typeStr3 + " " + TemplateGenerator.getGetterName(fieldDeclaration) + "()");
            if (fieldDeclaration.modifiers.isReadonly()) continue;
            this.populateModifiers(fieldDeclaration.modifiers);
            this.addLine("public abstract void " + TemplateGenerator.getSetterName(fieldDeclaration) + "(" + typeStr3 + " value)");
        }
        this.addComment("Stores class members for <b>" + classDec.type.typePath + "</b>.\nMethods, fields, and constructors can be used without using Handle Objects.");
        this.addLine("public static final class " + this.className(classDec) + " extends Template.Class<" + this.handleName(classDec) + "> {");
        boolean hasEnumFields = false;
        FieldDeclaration[] fieldDeclarationArray = classDec.fields;
        int n = fieldDeclarationArray.length;
        boolean bl2 = false;
        while (var8_30 < n) {
            FieldDeclaration fDec = fieldDeclarationArray[var8_30];
            if (!fDec.modifiers.isUnknown() && fDec.isEnum) {
                String fieldTypeStr = "Template.EnumConstant";
                if (this.hasConversion(fDec)) {
                    fieldTypeStr = fieldTypeStr + ".Converted";
                    fieldTypeStr = fieldTypeStr + "<" + this.getTypeStr(fDec.type.cast, true) + ">";
                } else {
                    fieldTypeStr = fieldTypeStr + "<" + this.getFieldTypeStr(fDec) + ">";
                }
                this.populateModifiers(fDec.modifiers);
                this.addLine("public final " + fieldTypeStr + " " + fDec.name.real() + " = new " + fieldTypeStr + "()");
                hasEnumFields = true;
            }
            ++var8_30;
        }
        if (hasEnumFields) {
            this.addLine();
        }
        boolean hasConstructors = false;
        for (ConstructorDeclaration cDec : classDec.constructors) {
            String constrTypeStr = "Template.Constructor";
            if (this.hasConversion(cDec)) {
                constrTypeStr = constrTypeStr + ".Converted";
            }
            constrTypeStr = constrTypeStr + "<" + this.getExposedTypeStr(cDec.type) + ">";
            this.populateModifiers(cDec.modifiers);
            this.addLine("public final " + constrTypeStr + " " + cDec.getName() + " = new " + constrTypeStr + "()");
            hasConstructors = true;
        }
        if (hasConstructors) {
            this.addLine();
        }
        boolean hasStaticFields = false;
        for (FieldDeclaration fDec : classDec.fields) {
            if (fDec.modifiers.isUnknown() || !fDec.modifiers.isStatic() || fDec.isEnum) continue;
            String fieldTypeStr = "Template.StaticField";
            if (this.hasConversion(fDec)) {
                fieldTypeStr = fieldTypeStr + ".Converted";
                fieldTypeStr = fieldTypeStr + "<" + this.getTypeStr(fDec.type.cast, true) + ">";
            } else {
                String primTypeStr2 = this.getPrimFieldType(fDec);
                fieldTypeStr = !primTypeStr2.isEmpty() ? fieldTypeStr + "." + primTypeStr2 : fieldTypeStr + "<" + this.getFieldTypeStr(fDec) + ">";
            }
            this.populateModifiers(fDec.modifiers);
            this.addLine("public final " + fieldTypeStr + " " + fDec.name.real() + " = new " + fieldTypeStr + "()");
            hasStaticFields = true;
        }
        if (hasStaticFields) {
            this.addLine();
        }
        boolean bl3 = false;
        for (FieldDeclaration fDec : classDec.fields) {
            if (fDec.modifiers.isUnknown() || fDec.modifiers.isStatic() || fDec.isEnum) continue;
            String fieldTypeStr = "Template.Field";
            if (this.hasConversion(fDec)) {
                fieldTypeStr = fieldTypeStr + ".Converted";
                fieldTypeStr = fieldTypeStr + "<" + this.getTypeStr(fDec.type.cast, true) + ">";
            } else {
                String primTypeStr3 = this.getPrimFieldType(fDec);
                fieldTypeStr = !primTypeStr3.isEmpty() ? fieldTypeStr + "." + primTypeStr3 : fieldTypeStr + "<" + this.getFieldTypeStr(fDec) + ">";
            }
            this.populateModifiers(fDec.modifiers);
            this.addLine("public final " + fieldTypeStr + " " + fDec.name.real() + " = new " + fieldTypeStr + "()");
            bl = true;
        }
        if (bl) {
            this.addLine();
        }
        boolean hasStaticMethods = false;
        for (MethodDeclaration mDec : classDec.methods) {
            if (mDec.modifiers.isUnknown() || !mDec.modifiers.isStatic()) continue;
            String methodTypeStr = "Template.StaticMethod" + this.getMethodAppend(mDec);
            this.populateModifiers(mDec.modifiers);
            this.addLine("public final " + methodTypeStr + " " + mDec.name.real() + " = new " + methodTypeStr + "()");
            hasStaticMethods = true;
        }
        if (hasStaticMethods) {
            this.addLine();
        }
        boolean hasLocalMethods = false;
        for (MethodDeclaration mDec : classDec.methods) {
            if (mDec.modifiers.isUnknown() || mDec.modifiers.isStatic()) continue;
            String methodTypeStr = "Template.Method" + this.getMethodAppend(mDec);
            this.populateModifiers(mDec.modifiers);
            this.addLine("public final " + methodTypeStr + " " + mDec.name.real() + " = new " + methodTypeStr + "()");
            hasLocalMethods = true;
        }
        if (hasLocalMethods) {
            this.addLine();
        }
        this.addLine("}");
        for (ClassDeclaration classDeclaration : classDec.subclasses) {
            this.addClass(classDeclaration);
        }
        this.addLine("}");
    }

    public static String getGetterName(FieldDeclaration fDec) {
        String fName = TemplateGenerator.getPropertyName(fDec);
        if (fName.length() > 2 && fName.startsWith("Is") && Character.isUpperCase(fName.charAt(2))) {
            return fName.substring(0, 1).toLowerCase(Locale.ENGLISH) + fName.substring(1);
        }
        if (Boolean.TYPE.equals(fDec.type.exposed().type)) {
            return "is" + fName;
        }
        return "get" + fName;
    }

    public static String getSetterName(FieldDeclaration fDec) {
        return "set" + TemplateGenerator.getPropertyName(fDec);
    }

    private void populateModifiers(ModifierDeclaration dec) {
        if (dec.isRawtype()) {
            this.addLine("@SuppressWarnings(\"rawtypes\")");
        }
        if (dec.isOptional()) {
            this.addLine("@Template.Optional");
        }
        if (dec.isReadonly()) {
            this.addLine("@Template.Readonly");
        }
    }

    private String getParamsBody(ParameterListDeclaration parameters) {
        String paramsStr = "(";
        for (int i = 0; i < parameters.parameters.length; ++i) {
            if (i > 0) {
                paramsStr = paramsStr + ", ";
            }
            paramsStr = paramsStr + this.getExposedTypeStr(parameters.parameters[i].type);
            paramsStr = paramsStr + " " + parameters.parameters[i].name.real();
        }
        return paramsStr + ")";
    }

    private String getArgsBody(ParameterListDeclaration parameters) {
        String argsStr = "";
        for (int i = 0; i < parameters.parameters.length; ++i) {
            if (i > 0) {
                argsStr = argsStr + ", ";
            }
            argsStr = argsStr + parameters.parameters[i].name.real();
        }
        return argsStr;
    }

    private void addAbstractMethodBody(MethodDeclaration mDec) {
        this.addLine("public abstract " + this.getExposedTypeStr(mDec.returnType) + " " + mDec.name.real() + this.getParamsBody(mDec.parameters));
    }

    private void addStaticMethodBody(MethodDeclaration mDec) {
        this.addLine("public static " + this.getExposedTypeStr(mDec.returnType) + " " + mDec.name.real() + this.getParamsBody(mDec.parameters) + " {");
        String bodyStr = "";
        if (!Void.TYPE.equals(mDec.returnType.exposed().type)) {
            bodyStr = bodyStr + "return ";
        }
        bodyStr = this.hasConversion(mDec) ? (mDec.parameters.parameters.length <= 5 ? bodyStr + "T." + mDec.name.real() + ".invoke(" : bodyStr + "T." + mDec.name.real() + ".invokeVA(") : (mDec.parameters.parameters.length == 0 ? bodyStr + "T." + mDec.name.real() + ".invoker.invoke(null" : (mDec.parameters.parameters.length <= 5 ? bodyStr + "T." + mDec.name.real() + ".invoker.invoke(null," : bodyStr + "T." + mDec.name.real() + ".invoker.invokeVA(null,"));
        bodyStr = bodyStr + this.getArgsBody(mDec.parameters);
        bodyStr = bodyStr + ")";
        this.populateModifiers(mDec.modifiers);
        this.addLine(bodyStr);
        this.addLine("}");
    }

    private boolean hasConversion(FieldDeclaration fDec) {
        return fDec.type.cast != null && (!fDec.modifiers.isReadonly() || fDec.type.type != Object.class);
    }

    private boolean hasConversion(ConstructorDeclaration cDec) {
        if (cDec.type.cast != null && cDec.type.cast.type != Object.class) {
            return true;
        }
        for (int i = 0; i < cDec.parameters.parameters.length; ++i) {
            if (cDec.parameters.parameters[i].type.cast == null) continue;
            return true;
        }
        return false;
    }

    private boolean hasConversion(MethodDeclaration mDec) {
        if (mDec.returnType.cast != null && mDec.returnType.cast.type != Object.class) {
            return true;
        }
        for (int i = 0; i < mDec.parameters.parameters.length; ++i) {
            if (mDec.parameters.parameters[i].type.cast == null) continue;
            return true;
        }
        return false;
    }

    private String getMethodAppend(MethodDeclaration mDec) {
        String app = "";
        if (this.hasConversion(mDec)) {
            app = app + ".Converted";
        }
        TypeDeclaration returnType = mDec.returnType.cast != null ? mDec.returnType.cast : mDec.returnType;
        app = returnType.type != null && returnType.type.isPrimitive() ? app + "<" + BoxedType.getBoxedType(returnType.type).getSimpleName() + ">" : app + "<" + this.getTypeStr(returnType, true) + ">";
        return app;
    }

    private String getExposedTypeStr(TypeDeclaration type) {
        return this.getTypeStr(type.exposed(), false);
    }

    private String getFieldTypeStr(FieldDeclaration fDec) {
        return this.getExposedTypeStr(fDec.type);
    }

    private String resolveImport(String typePath) {
        String fullType;
        String importPath = typePath;
        while (importPath.endsWith("[]")) {
            importPath = importPath.substring(0, importPath.length() - 2);
        }
        String typeName = typePath.substring(typePath.lastIndexOf(46) + 1);
        String importName = importPath.substring(importPath.indexOf(46) + 1);
        String oldImport = this.imports.get(importName);
        if (oldImport != null) {
            fullType = oldImport.equals(importPath) ? typeName : typePath;
        } else {
            this.imports.put(importName, importPath);
            fullType = typeName;
        }
        return fullType;
    }

    private String getTypeStr(TypeDeclaration type, boolean isGenericType) {
        if (type.isArray()) {
            return this.getTypeStr(type.getComponentType(), false) + "[]";
        }
        String fullType = isGenericType && type.isPrimitive ? BoxedType.getBoxedType(type.type).getSimpleName() : (type.isBuiltin() ? type.typeName : this.resolveImport(type.typePath));
        if (type.isWildcard) {
            fullType = fullType.length() > 0 ? "? extends " + fullType : "?";
        }
        if (type.genericTypes.length > 0) {
            fullType = fullType + "<";
            boolean first = true;
            for (TypeDeclaration gen : type.genericTypes) {
                if (first) {
                    first = false;
                } else {
                    fullType = fullType + ", ";
                }
                fullType = fullType + this.getTypeStr(gen, true);
            }
            fullType = fullType + ">";
        }
        return fullType;
    }

    private static String getPropertyName(FieldDeclaration fDec) {
        String name = fDec.name.real();
        if (name.isEmpty()) {
            return "UNKNOWN";
        }
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    private String getPrimFieldType(FieldDeclaration fDec) {
        Class<?> fType = fDec.type.exposed().type;
        if (fType != null) {
            if (fType.equals(Byte.TYPE)) {
                return "Byte";
            }
            if (fType.equals(Short.TYPE)) {
                return "Short";
            }
            if (fType.equals(Integer.TYPE)) {
                return "Integer";
            }
            if (fType.equals(Long.TYPE)) {
                return "Long";
            }
            if (fType.equals(Float.TYPE)) {
                return "Float";
            }
            if (fType.equals(Double.TYPE)) {
                return "Double";
            }
            if (fType.equals(Character.TYPE)) {
                return "Character";
            }
            if (fType.equals(Boolean.TYPE)) {
                return "Boolean";
            }
        }
        return "";
    }

    private String handleName(ClassDeclaration classDec) {
        return TemplateGenerator.filterTypeName(classDec.type.typeName) + "Handle";
    }

    private String className(ClassDeclaration classDec) {
        return TemplateGenerator.filterTypeName(classDec.type.typeName) + "Class";
    }

    private static String filterTypeName(String name) {
        int idx = name.lastIndexOf(46);
        if (idx != -1) {
            return name.substring(idx + 1);
        }
        return name;
    }

    private void addComment(String commentText) {
        String[] lines = commentText.split("\\r?\\n");
        if (lines.length == 1) {
            this.addRawLine("/** " + lines[0] + " */");
        } else {
            this.addRawLine("/**");
            for (String line : lines) {
                this.addRawLine(" * " + line);
            }
            this.addRawLine(" */");
        }
    }

    private void addLine() {
        this.builder.append('\n');
    }

    private void addLine(String line) {
        if (line.endsWith("}")) {
            this.indent(-1);
        }
        for (int i = 0; i < this.indent; ++i) {
            this.builder.append("    ");
        }
        this.builder.append(line);
        if (line.endsWith("{")) {
            this.indent(1);
        } else if (line.endsWith("}")) {
            this.builder.append('\n');
        } else if (!(line.startsWith("//") || line.startsWith("/*") || line.startsWith("@"))) {
            this.builder.append(';');
        }
        this.builder.append('\n');
    }

    private void addRawLine(String line) {
        for (int i = 0; i < this.indent; ++i) {
            this.builder.append("    ");
        }
        this.builder.append(line);
        this.builder.append('\n');
    }

    private void indent(int indent) {
        this.indent += indent;
    }
}

