/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Remapping;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedCodeInvoker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ClassResolver {
    private static final List<String> default_imports = Arrays.asList("java.lang.*", "java.util.*");
    private static final Runnable[] default_bootstrap = new Runnable[0];
    public static final ClassResolver DEFAULT = new ClassResolver().immutable();
    private final ArrayList<String> imports;
    private final ArrayList<String> manualImports;
    private final List<Requirement> requirements;
    private final Remapping.Lookup remappings;
    private VariablesMap variables;
    private String classDeclarationResolverName;
    private String packagePath;
    private String declaredClassName;
    private Class<?> declaredClass;
    private ClassLoader classLoader;
    private boolean logErrors;
    private boolean isGenerating;
    private Runnable[] bootstrap;

    private ClassResolver(ClassResolver src) {
        this.classDeclarationResolverName = src.classDeclarationResolverName;
        this.variables = src.variables;
        this.imports = new ArrayList<String>(src.imports);
        this.manualImports = new ArrayList<String>(src.manualImports);
        this.requirements = new ArrayList<Requirement>(src.requirements);
        this.remappings = src.remappings.clone();
        this.packagePath = src.packagePath;
        this.declaredClassName = src.declaredClassName;
        this.declaredClass = src.declaredClass;
        this.classLoader = src.classLoader;
        this.logErrors = src.logErrors;
        this.isGenerating = src.isGenerating;
        this.bootstrap = src.bootstrap;
    }

    public ClassResolver() {
        this.classDeclarationResolverName = "null";
        this.variables = VariablesMap.EMPTY;
        this.imports = new ArrayList();
        this.manualImports = new ArrayList();
        this.requirements = new ArrayList<Requirement>();
        this.remappings = Remapping.createLookup();
        this.packagePath = "";
        this.declaredClassName = null;
        this.declaredClass = null;
        this.classLoader = ClassResolver.class.getClassLoader();
        this.logErrors = true;
        this.isGenerating = false;
        this.bootstrap = default_bootstrap;
        this.regenImports();
    }

    public ClassResolver(String packagePath) {
        this.classDeclarationResolverName = "null";
        this.variables = VariablesMap.EMPTY;
        this.imports = new ArrayList();
        this.manualImports = new ArrayList();
        this.requirements = new ArrayList<Requirement>();
        this.remappings = Remapping.createLookup();
        this.packagePath = "";
        this.logErrors = true;
        this.isGenerating = false;
        this.bootstrap = default_bootstrap;
        this.declaredClassName = null;
        this.declaredClass = null;
        this.classLoader = ClassResolver.class.getClassLoader();
        this.setPackage(packagePath);
    }

    public boolean getLogErrors() {
        return this.logErrors;
    }

    public void setLogErrors(boolean log) {
        this.logErrors = log;
    }

    public boolean isGenerating() {
        return this.isGenerating;
    }

    public void setGenerating(boolean generating) {
        this.isGenerating = generating;
    }

    public ClassResolver clone() {
        return new ClassResolver(this);
    }

    public ClassResolver immutable() {
        return new ImmutableClassResolver(this);
    }

    public void setClassLoader(ClassLoader loader) {
        this.classLoader = loader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setDeclaredClassName(String typeName) {
        ResolveResult result = this.resolve(typeName);
        this.setDeclaredClass(result.classType, result.classPath);
    }

    public void setDeclaredClass(Class<?> type) {
        if (type != null) {
            this.setDeclaredClass(type, MPLType.getName(type));
        }
    }

    public void setDeclaredClass(Class<?> type, String typeName) {
        this.declaredClass = type;
        this.declaredClassName = typeName;
        String packageName = MountiplexUtil.getPackagePathFromClassPath(typeName);
        if (!packageName.isEmpty()) {
            this.packagePath = packageName;
        }
        this.regenImports();
    }

    public Class<?> getDeclaredClass() {
        return this.declaredClass;
    }

    public String getDeclaredClassName() {
        return this.declaredClassName;
    }

    public void setPackage(String path) {
        this.setPackage(path, true);
    }

    public void setPackage(String path, boolean reset) {
        this.packagePath = path;
        if (reset) {
            this.declaredClass = null;
            this.declaredClassName = null;
            this.manualImports.clear();
        }
        this.regenImports();
    }

    public String getPackage() {
        return this.packagePath;
    }

    public boolean hasPackage() {
        return !this.packagePath.isEmpty();
    }

    public void setClassDeclarationResolverName(String name) {
        this.classDeclarationResolverName = name;
    }

    public String getClassDeclarationResolverName() {
        return this.classDeclarationResolverName;
    }

    public List<Runnable> getBootstrap() {
        return Arrays.asList(this.bootstrap);
    }

    public void runBootstrap() {
        for (int i = 0; i < this.bootstrap.length; ++i) {
            try {
                this.bootstrap[i].run();
                continue;
            }
            catch (Throwable t) {
                throw new BootstrapException(this.bootstrap[i], t);
            }
        }
    }

    public void addBootstrap(Runnable runnable) {
        this.bootstrap = Arrays.copyOf(this.bootstrap, this.bootstrap.length + 1);
        this.bootstrap[this.bootstrap.length - 1] = new BootstrapRunnable(runnable);
    }

    public void addBootstrap(String code) {
        this.bootstrap = Arrays.copyOf(this.bootstrap, this.bootstrap.length + 1);
        this.bootstrap[this.bootstrap.length - 1] = new BootstrapCode(this, code);
    }

    public Stream<String> getAllImports() {
        return Stream.concat(this.manualImports.stream(), default_imports.stream());
    }

    public void addImport(String path) {
        this.manualImports.add(path);
        this.regenImports();
    }

    public void addImports(Collection<String> paths) {
        this.manualImports.addAll(paths);
        this.regenImports();
    }

    public String saveDeclaration() {
        return this.variables.getDeclaration();
    }

    public void setVariable(String name, String value) {
        this.variables = this.variables.modify(m -> m.put(name, value));
    }

    public String getVariable(String name, String def) {
        String value = this.variables.get(name);
        return value != null ? value : def;
    }

    public void includeSourceDetails(ClassResolver from) {
        this.remappings.assign(from.remappings);
        this.requirements.clear();
        this.requirements.addAll(from.requirements);
        this.variables = from.variables;
        this.bootstrap = from.bootstrap;
    }

    public void setAllVariables(Map<String, String> variables) {
        this.variables = this.variables.modify(m -> m.putAll(variables));
    }

    public void setAllVariables(ClassDeclarationResolver resolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("Resolver cannot be null");
        }
        if (this.declaredClassName == null || this.declaredClassName.isEmpty() || this.declaredClass == null) {
            throw new IllegalStateException("Class Resolver has no declared Class");
        }
        this.variables = this.variables.modify(m -> resolver.resolveClassVariables(this.declaredClassName, this.declaredClass, (Map<String, String>)m));
        ClassResolver classResolver = resolver.getRootClassResolver(this.declaredClassName, this.declaredClass);
        for (Remapping remapping : classResolver.getRemappings().getAllStoredRemappings()) {
            this.remappings.addRemapping(remapping);
        }
    }

    public Map<String, String> getAllVariables() {
        return this.variables.getAll();
    }

    public boolean evaluateExpression(String expression) {
        boolean prevExpressionResult = false;
        int prevExpressionStart = 0;
        boolean compareAnd = false;
        for (int cIdx = 0; cIdx < expression.length() - 1; ++cIdx) {
            boolean nextCompareAnd;
            char c = expression.charAt(cIdx);
            if (c == '&' && expression.charAt(cIdx + 1) == '&') {
                nextCompareAnd = true;
            } else {
                if (c != '|' || expression.charAt(cIdx + 1) != '|') continue;
                nextCompareAnd = false;
            }
            prevExpressionResult = compareAnd ? (prevExpressionResult &= this.evaluateExpression_part(expression.substring(prevExpressionStart, cIdx))) : (prevExpressionResult |= this.evaluateExpression_part(expression.substring(prevExpressionStart, cIdx)));
            compareAnd = nextCompareAnd;
            prevExpressionStart = ++cIdx + 1;
        }
        if (compareAnd) {
            return prevExpressionResult && this.evaluateExpression_part(expression.substring(prevExpressionStart));
        }
        return prevExpressionResult || this.evaluateExpression_part(expression.substring(prevExpressionStart));
    }

    private boolean evaluateExpression_part(String expression) {
        String value2;
        expression = expression.trim();
        String varName = null;
        for (int cIdx = 0; cIdx < expression.length(); ++cIdx) {
            char c = expression.charAt(cIdx);
            if (c != ' ' && (Character.isLetter(c) || c == '_' || c == '!')) continue;
            varName = expression.substring(0, cIdx);
            expression = expression.substring(cIdx).trim();
            break;
        }
        if (varName == null) {
            varName = expression;
            expression = "";
        }
        boolean inverted = false;
        while (varName.startsWith("!")) {
            inverted = !inverted;
            varName = varName.substring(1);
        }
        if (varName.equals("classexists") || varName.equals("methodexists") || varName.equals("fieldexists") || varName.equals("exists")) {
            String classPath;
            int signatureStart = expression.indexOf(32);
            if (signatureStart == -1) {
                classPath = expression;
                signatureStart = expression.length();
            } else {
                classPath = expression.substring(0, signatureStart);
                ++signatureStart;
            }
            while (classPath.endsWith(";")) {
                classPath = classPath.substring(0, classPath.length() - 1);
            }
            Class<?> declaredClass = Resolver.loadClass(classPath, false, this.classLoader);
            if (declaredClass == null) {
                return inverted;
            }
            String signatureStr = expression.substring(signatureStart).trim();
            if (signatureStr.isEmpty()) {
                return !inverted;
            }
            ClassResolver resolver = new ClassResolver(this);
            resolver.setDeclaredClass(declaredClass, classPath);
            resolver.setLogErrors(false);
            Declaration declaration = Declaration.parseDeclaration(resolver, signatureStr);
            if (declaration == null && this.getLogErrors()) {
                MountiplexUtil.LOGGER.warning("Failed to parse declaration to check existance: " + signatureStr);
                return inverted;
            }
            if (!declaration.isResolved()) {
                return inverted;
            }
            return declaration.discover() != null != inverted;
        }
        if (varName.equals("assignable")) {
            String secondClassPath;
            String firstClassPath;
            int secondClassStart = expression.indexOf(32);
            if (secondClassStart == -1) {
                firstClassPath = expression;
                secondClassPath = Object.class.getName();
            } else {
                firstClassPath = expression.substring(0, secondClassStart);
                secondClassPath = expression.substring(secondClassStart + 1).trim();
            }
            firstClassPath = this.resolvePath(firstClassPath);
            secondClassPath = this.resolvePath(secondClassPath);
            Class<?> firstClass = Resolver.loadClass(firstClassPath, false, this.classLoader);
            Class<?> secondClass = Resolver.loadClass(secondClassPath, false, this.classLoader);
            if (firstClass == null || secondClass == null) {
                return !inverted;
            }
            return firstClass.isAssignableFrom(secondClass) != inverted;
        }
        String value1 = this.variables.get(varName);
        if (value1 == null) {
            if (varName.equals("1") || varName.equalsIgnoreCase("true")) {
                return !inverted;
            }
            return inverted;
        }
        int logicEndIdx = expression.indexOf(32);
        if (logicEndIdx == -1) {
            return !inverted;
        }
        String operand = expression.substring(0, logicEndIdx);
        return TextValueSequence.evaluateText(value1, operand, value2 = expression.substring(logicEndIdx + 1).trim()) != inverted;
    }

    public String resolvePath(String name) {
        return this.resolve((String)name).classPath;
    }

    public Class<?> resolveClass(String name) {
        return this.resolve((String)name).classType;
    }

    public ResolveResult resolve(String name) {
        String classPath;
        if (name.length() == 1) {
            return new ResolveResult(name, Object.class);
        }
        if (name.endsWith("[]")) {
            int arrayLevels = 0;
            do {
                ++arrayLevels;
            } while ((name = name.substring(0, name.length() - 2)).endsWith("[]"));
            ResolveResult componentResult = this.resolve(name);
            StringBuilder newPath = new StringBuilder(componentResult.classPath);
            for (int i = 0; i < arrayLevels; ++i) {
                newPath.append("[]");
            }
            if (componentResult.classType != null) {
                return new ResolveResult(newPath.toString(), MountiplexUtil.getArrayType(componentResult.classType, arrayLevels));
            }
            return new ResolveResult(newPath.toString(), null);
        }
        Class<?> byAbsoluteName = Resolver.loadClass(name, false, this.classLoader);
        if (byAbsoluteName != null) {
            return new ResolveResult(name, byAbsoluteName);
        }
        String bestImport = null;
        String dotName = "." + name;
        for (String imp : this.imports) {
            Class<?> byImport;
            if (imp.endsWith(".*") || imp.endsWith("$*")) {
                classPath = imp.substring(0, imp.length() - 1) + name;
            } else {
                if (!imp.endsWith(dotName)) continue;
                classPath = imp;
                bestImport = imp;
            }
            if ((byImport = Resolver.loadClass(classPath, false, this.classLoader)) == null) continue;
            return new ResolveResult(classPath, byImport);
        }
        if (!(this.packagePath.isEmpty() || Character.isLowerCase(name.charAt(0)) && name.contains("."))) {
            classPath = this.packagePath + "." + name;
            Class<?> byPackage = Resolver.loadClass(classPath, false, this.classLoader);
            if (byPackage != null) {
                return new ResolveResult(classPath, byPackage);
            }
        } else {
            classPath = name;
        }
        if (bestImport != null) {
            classPath = bestImport;
        }
        return new ResolveResult(classPath, null);
    }

    public String resolvePath(Class<?> type) {
        if (type.isArray()) {
            return this.resolvePath(type.getComponentType()) + "[]";
        }
        return MPLType.getName(type).replace('$', '.');
    }

    public String resolveName(Class<?> type) {
        if (type == null) {
            return "NULL";
        }
        if (type.isArray()) {
            return this.resolveName(type.getComponentType()) + "[]";
        }
        String name = MPLType.getName(type).replace('$', '.');
        for (String imp : this.imports) {
            if (ClassResolver.equalsIgnoreDollarSign(imp, name)) {
                return type.getSimpleName();
            }
            if (!imp.endsWith(".*") && !imp.endsWith("$*")) continue;
            int imp_len = imp.length() - 1;
            if (name.length() < imp_len || !ClassResolver.equalsIgnoreDollarSign(name, imp, imp_len)) continue;
            return name.substring(imp_len);
        }
        return name;
    }

    private static boolean equalsIgnoreDollarSign(String a, String b) {
        int len = a.length();
        return len == b.length() && ClassResolver.equalsIgnoreDollarSign(a, b, len);
    }

    private static boolean equalsIgnoreDollarSign(String a, String b, int len) {
        for (int i = 0; i < len; ++i) {
            char cb;
            char ca = a.charAt(i);
            if (ca == (cb = b.charAt(i)) || (ca == '.' || ca == '$') && (cb == '.' || cb == '$')) continue;
            return false;
        }
        return true;
    }

    public void storeRequirement(Requirement declaration) {
        this.requirements.add(0, declaration);
    }

    public List<Requirement> getRequirements() {
        return this.requirements;
    }

    public void storeRemapping(Remapping remapping) {
        this.remappings.addRemapping(remapping);
    }

    public Remapping.Lookup getRemappings() {
        return this.remappings;
    }

    private void regenImports() {
        this.imports.clear();
        this.imports.addAll(this.manualImports);
        Collections.reverse(this.imports);
        if (this.declaredClassName != null) {
            this.imports.add(this.declaredClassName + "$*");
        }
        if (this.packagePath != null && !this.packagePath.isEmpty()) {
            this.imports.add(this.packagePath + ".*");
        }
        this.imports.addAll(default_imports);
    }

    private static class VariablesMap {
        private final Map<String, String> _map;
        private String _decl;
        public static final VariablesMap EMPTY = new VariablesMap();

        private VariablesMap() {
            this._map = Collections.emptyMap();
            this._decl = "";
        }

        public VariablesMap(Map<String, String> map) {
            this._map = map;
            this._decl = null;
        }

        public Map<String, String> getAll() {
            return Collections.unmodifiableMap(this._map);
        }

        public String get(String key) {
            return this._map.get(key);
        }

        public VariablesMap modify(Consumer<Map<String, String>> modifier) {
            HashMap<String, String> new_map = new HashMap<String, String>(this._map);
            modifier.accept(new_map);
            return new VariablesMap(new_map);
        }

        public String getDeclaration() {
            if (this._decl == null) {
                StringBuilder result = new StringBuilder();
                for (Map.Entry<String, String> entry : this._map.entrySet()) {
                    result.append("#set ").append(entry.getKey()).append(' ').append(entry.getValue()).append('\n');
                }
                this._decl = result.toString();
            }
            return this._decl;
        }
    }

    private static class ImmutableClassResolver
    extends ClassResolver {
        public ImmutableClassResolver(ClassResolver resolver) {
            super(resolver);
        }

        @Override
        public void setDeclaredClass(Class<?> type) {
            throw new UnsupportedOperationException("Class Resolver is immutable");
        }

        @Override
        public void setPackage(String path) {
            throw new UnsupportedOperationException("Class Resolver is immutable");
        }

        @Override
        public void addImport(String path) {
            throw new UnsupportedOperationException("Class Resolver is immutable");
        }

        @Override
        public void setVariable(String name, String value) {
            throw new UnsupportedOperationException("Class Resolver is immutable");
        }

        @Override
        public void storeRequirement(Requirement declaration) {
            throw new UnsupportedOperationException("Class Resolver is immutable");
        }

        @Override
        public void storeRemapping(Remapping remapping) {
            throw new UnsupportedOperationException("Class Resolver is immutable");
        }
    }

    public static class ResolveResult {
        public final String classPath;
        public final Class<?> classType;

        public ResolveResult(String classPath, Class<?> classType) {
            this.classPath = classPath;
            this.classType = classType;
        }

        public boolean success() {
            return this.classType != null;
        }
    }

    private static class BootstrapException
    extends RuntimeException {
        private static final long serialVersionUID = -1827332615527781142L;

        public BootstrapException(Runnable runnable, Throwable cause) {
            super("Failed to bootstrap " + runnable, cause);
        }
    }

    private static class BootstrapRunnable
    implements Runnable {
        private final Runnable runnable;
        private boolean needsExecuting = true;

        public BootstrapRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public synchronized void run() {
            if (this.needsExecuting) {
                this.runnable.run();
                this.needsExecuting = false;
            }
        }

        public String toString() {
            return "RUNNABLE{" + this.runnable + "}";
        }
    }

    private static class BootstrapCode
    implements Runnable {
        private final ClassResolver resolver;
        private final String code;
        private boolean needsExecuting = true;

        public BootstrapCode(ClassResolver resolver, String code) {
            this.resolver = resolver;
            this.code = code;
        }

        @Override
        public void run() {
            if (this.needsExecuting) {
                MethodDeclaration decl = new MethodDeclaration(this.resolver, "public static void run() {\n" + this.code + "\n}");
                GeneratedCodeInvoker invoker = GeneratedCodeInvoker.create(decl);
                invoker.invoke(null);
                this.needsExecuting = false;
            }
        }

        public String toString() {
            return "CODE" + this.code;
        }
    }
}

