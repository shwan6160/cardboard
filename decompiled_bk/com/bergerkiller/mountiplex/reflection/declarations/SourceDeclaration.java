/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourcePreprocessor;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParserGroups;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.SourceDeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class SourceDeclaration
extends Declaration {
    public final ClassDeclaration[] classes;

    private SourceDeclaration(ClassResolver resolver, ClassLoader classLoader, File sourceDirectory, StringBuffer declaration) {
        super(resolver, SourceDeclaration.preprocess(declaration));
        StringBuffer postfix;
        ParserContext parserContext = new ParserContext(classLoader, sourceDirectory);
        this.getParserPostfix().trimWhitespace(0);
        while ((postfix = this.getPostfix()) != null && postfix.length() > 0) {
            if (parserContext.runParsers(DeclarationParserGroups.SOURCE)) continue;
            ClassDeclaration cDec = this.nextClass();
            if (cDec.isValid()) {
                parserContext.classes.add(cDec);
                continue;
            }
            MountiplexUtil.LOGGER.warning("Invalid class declaration parsed:\n" + cDec);
            MountiplexUtil.LOGGER.warning("Source: " + cDec._initialDeclaration);
            this.setInvalid();
            this.classes = new ClassDeclaration[0];
            return;
        }
        this.classes = parserContext.getClasses();
    }

    public static StringBuffer preprocess(StringBuffer declaration) {
        return StringBuffer.of(SourceDeclaration.preprocess(declaration.toString()));
    }

    public static String preprocess(String declaration) {
        return SourceDeclaration.preprocess(declaration, new ClassResolver());
    }

    public static String preprocess(String declaration, ClassResolver resolver) {
        return new SourcePreprocessor(resolver).preprocess(declaration);
    }

    public static String trimIndentation(String text) {
        String[] lines = text.split("\\r?\\n", -1);
        int minIndent = 20;
        block0: for (String line : lines) {
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) == ' ') continue;
                if (i >= minIndent) continue block0;
                minIndent = i;
                continue block0;
            }
        }
        StringBuilder result = new StringBuilder(text.length());
        for (String line : lines) {
            if (line.length() >= minIndent) {
                line = line.substring(minIndent);
            }
            result.append(line).append('\n');
        }
        return result.toString();
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public boolean match(Declaration declaration) {
        return false;
    }

    @Override
    public String toString(boolean identity) {
        String pkg = this.getResolver().getPackage();
        String str = "";
        if (pkg.length() > 0) {
            str = str + "package " + pkg + ";\n\n";
        }
        for (String imp : this.getResolver().getAllImports().collect(Collectors.toList())) {
            str = str + "import " + imp + ";\n";
        }
        str = str + "\n";
        for (ClassDeclaration c : this.classes) {
            str = str + c.toString(identity) + "\n\n";
        }
        return str;
    }

    @Override
    protected void debugString(StringBuilder str, String indent) {
    }

    @Override
    public double similarity(Declaration other) {
        return 0.0;
    }

    public static SourceDeclaration parse(ClassResolver resolver, String source) {
        return new SourceDeclaration(resolver, null, null, StringBuffer.of(source));
    }

    public static SourceDeclaration parse(String source) {
        return new SourceDeclaration(new ClassResolver(), null, null, StringBuffer.of(source));
    }

    private static String saveVars(Map<String, String> variables) {
        if (variables == null || variables.isEmpty()) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            str.append("#set ").append(entry.getKey()).append(' ').append(entry.getValue()).append('\n');
        }
        return str.toString();
    }

    public static SourceDeclaration parseFromResources(ClassLoader classLoader, String sourceInclude, Map<String, String> variables) {
        return new SourceDeclaration(new ClassResolver(), classLoader, null, StringBuffer.of(SourceDeclaration.saveVars(variables) + "\n#include " + sourceInclude));
    }

    public static SourceDeclaration loadFromDisk(File sourceDirectory, String sourceInclude, Map<String, String> variables, boolean isGenerating) {
        ClassResolver resolver = new ClassResolver();
        resolver.setGenerating(isGenerating);
        return new SourceDeclaration(resolver, null, sourceDirectory, StringBuffer.of(SourceDeclaration.saveVars(variables) + "\n#include " + sourceInclude));
    }

    public static SourceDeclaration loadFromDisk(File sourceDirectory, String sourceInclude, Map<String, String> variables) {
        return new SourceDeclaration(new ClassResolver(), null, sourceDirectory, StringBuffer.of(SourceDeclaration.saveVars(variables) + "\n#include " + sourceInclude));
    }

    public static SourceDeclaration parseFromResources(ClassLoader classLoader, String sourceInclude) {
        return new SourceDeclaration(new ClassResolver(), classLoader, null, StringBuffer.of("#include " + sourceInclude));
    }

    public static SourceDeclaration loadFromDisk(File sourceDirectory, String sourceInclude) {
        return new SourceDeclaration(new ClassResolver(), null, sourceDirectory, StringBuffer.of("#include " + sourceInclude));
    }

    private class ParserContext
    extends Declaration.BaseDeclarationParserContext
    implements SourceDeclarationParserContext {
        private ClassLoader classLoader;
        private final File currentDirectory;
        private String templateFile = "";
        private LinkedList<ClassDeclaration> classes = new LinkedList();

        public ParserContext(ClassLoader classLoader, File currentDirectory) {
            this.classLoader = classLoader;
            this.currentDirectory = currentDirectory;
        }

        public ClassDeclaration[] getClasses() {
            return this.classes.toArray(new ClassDeclaration[this.classes.size()]);
        }

        @Override
        public ClassLoader getClassLoader() {
            if (this.classLoader == null) {
                this.classLoader = SourceDeclaration.class.getClassLoader();
            }
            return this.classLoader;
        }

        @Override
        public File getCurrentDirectory() {
            return this.currentDirectory;
        }

        @Override
        public void includeSource(StringBuffer subSource) {
            SourceDeclaration inclSource = new SourceDeclaration(this.getResolver().clone(), this.classLoader, this.currentDirectory, subSource);
            this.getResolver().includeSourceDetails(inclSource.getResolver());
            this.classes.addAll(Arrays.asList(inclSource.classes));
        }

        @Override
        public void setCurrentTemplateFile(String path) {
            this.templateFile = path;
        }

        @Override
        public String getCurrentTemplateFile() {
            return this.templateFile;
        }
    }
}

