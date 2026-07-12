/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.dep.javassist.CannotCompileException;
import com.bergerkiller.mountiplex.dep.javassist.NotFoundException;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.ConstructorDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ModifierDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.NameDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.ParameterListDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Requirement;
import com.bergerkiller.mountiplex.reflection.declarations.TemplateError;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.DeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public abstract class Declaration {
    private final ParserStringBuffer _postfix = new ParserStringBuffer();
    private String _longDeclare = null;
    protected final StringBuffer _initialDeclaration;
    private final ClassResolver _resolver;
    private List<String> _errors = Collections.emptyList();
    private List<String> _warnings = Collections.emptyList();
    private boolean _loggedErrorsAndWarnings = true;

    public Declaration(ClassResolver resolver) {
        this._resolver = resolver;
        this._initialDeclaration = StringBuffer.EMPTY;
        this._postfix.set(StringBuffer.EMPTY);
    }

    public Declaration(ClassResolver resolver, String initialDeclaration) {
        this(resolver, StringBuffer.of(initialDeclaration));
    }

    public Declaration(ClassResolver resolver, StringBuffer initialDeclaration) {
        this._resolver = resolver;
        this._initialDeclaration = initialDeclaration;
        this._postfix.set(initialDeclaration);
    }

    public final ClassResolver getResolver() {
        return this._resolver;
    }

    protected final ModifierDeclaration nextModifier() {
        return this.updatePostfix(new ModifierDeclaration(this._resolver, this._postfix.get()));
    }

    protected final NameDeclaration nextName() {
        return this.updatePostfix(new NameDeclaration(this._resolver, this._postfix.get()));
    }

    protected final NameDeclaration nextName(int optionalIdx) {
        return this.updatePostfix(new NameDeclaration(this._resolver, this._postfix.get(), optionalIdx));
    }

    protected final TypeDeclaration nextType() {
        return this.updatePostfix(TypeDeclaration.parse(this._resolver, this._postfix.get()));
    }

    protected final ParameterDeclaration nextParameter(int paramIdx) {
        return this.updatePostfix(new ParameterDeclaration(this._resolver, this._postfix.get(), paramIdx));
    }

    protected final ParameterListDeclaration nextParameterList() {
        return this.updatePostfix(new ParameterListDeclaration(this._resolver, this._postfix.get()));
    }

    protected final ClassDeclaration nextClass() {
        return this.updatePostfix(new ClassDeclaration(this._resolver.clone(), this._postfix.get()));
    }

    protected final <T extends Declaration> T updatePostfix(T lastDeclaration) {
        this._postfix.set(lastDeclaration.getPostfix());
        return lastDeclaration;
    }

    public final StringBuffer getPostfix() {
        return this._postfix.get();
    }

    protected final ParserStringBuffer getParserPostfix() {
        return this._postfix;
    }

    protected final void setPostfix(StringBuffer postfix) {
        this._postfix.set(postfix);
    }

    public final boolean isValid() {
        return !this._postfix.isNull();
    }

    protected final void setInvalid() {
        this._postfix.set(null);
    }

    public abstract boolean isResolved();

    public final List<String> getTemplateWarnings() {
        return this._warnings;
    }

    public final List<String> getTemplateErrors() {
        return this._errors;
    }

    public final void checkTemplateErrors() {
        if (!this._loggedErrorsAndWarnings) {
            this._loggedErrorsAndWarnings = true;
            if (!this._warnings.isEmpty()) {
                if (this._warnings.size() > 1) {
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Warnings in template for declaring class: " + this._resolver.getDeclaredClassName());
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Multiple template warnings for " + this.getTemplateLogIdentity() + ":");
                    for (String warning : this._warnings) {
                        MountiplexUtil.LOGGER.log(Level.WARNING, "  - " + warning);
                    }
                } else {
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Warning in template for declaring class: " + this._resolver.getDeclaredClassName());
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Template warning for " + this.getTemplateLogIdentity() + ": " + this._warnings.get(0));
                }
            }
            if (!this._errors.isEmpty()) {
                if (this._errors.size() > 1) {
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Errors in template for declaring class: " + this._resolver.getDeclaredClassName());
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Multiple template errors for " + this.getTemplateLogIdentity() + ":");
                    for (String error : this._errors) {
                        MountiplexUtil.LOGGER.log(Level.SEVERE, "  - " + error);
                    }
                } else {
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Error in template for declaring class: " + this._resolver.getDeclaredClassName());
                }
            }
        }
        if (!this._errors.isEmpty()) {
            if (this._errors.size() > 1) {
                throw new TemplateError("Multiple template errors for " + this.getTemplateLogIdentity());
            }
            throw new TemplateError("Template error for " + this.getTemplateLogIdentity() + ": " + this._errors.get(0));
        }
    }

    protected String getTemplateLogIdentity() {
        return this.toString(false);
    }

    public abstract boolean match(Declaration var1);

    public abstract String toString(boolean var1);

    public final String toString() {
        return this.toString(false);
    }

    public String debugString() {
        StringBuilder str = new StringBuilder();
        this.debugString(str, "");
        return str.toString();
    }

    protected abstract void debugString(StringBuilder var1, String var2);

    public abstract double similarity(Declaration var1);

    public Declaration discover() {
        if (!this.isValid() || !this.isResolved()) {
            return null;
        }
        return this;
    }

    public void discoverAlternatives() {
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Declaration) {
            Declaration d = (Declaration)other;
            if (d._longDeclare == null) {
                d._longDeclare = d.toString(true);
            }
            if (this._longDeclare == null) {
                this._longDeclare = this.toString(true);
            }
            return d._longDeclare.equals(this._longDeclare);
        }
        return false;
    }

    public final int hashCode() {
        if (this._longDeclare == null) {
            this._longDeclare = this.toString(true);
        }
        return this._longDeclare.hashCode();
    }

    public void modifyBodyRequirement(Requirement requirement, StringBuilder body, String instanceName, String requirementName, int instanceStartIdx, int nameEndIdx) {
        throw new UnsupportedOperationException("Declaration " + this.toString() + " can not be added as requirement");
    }

    public void addAsRequirement(ExtendedClassWriter<?> writer, Requirement requirement, String name) throws CannotCompileException, NotFoundException {
        throw new UnsupportedOperationException("Declaration " + this.toString() + " can not be added as requirement");
    }

    public static <T extends Declaration> void sortSimilarity(T compare, List<T> list) {
        Collections.sort(list, Declaration.createSimilarityComparator(compare));
    }

    public static <T extends Declaration> void sortSimilarity(T compare, T[] array) {
        Arrays.sort(array, Declaration.createSimilarityComparator(compare));
    }

    private static <T extends Declaration> Comparator<T> createSimilarityComparator(T compare) {
        return (o1, o2) -> {
            double s2;
            double s1 = compare.similarity((Declaration)o1);
            if (s1 == (s2 = compare.similarity((Declaration)o2))) {
                return 0;
            }
            if (s1 > s2) {
                return -1;
            }
            return 1;
        };
    }

    public static Declaration parseDeclaration(ClassResolver resolver, String declaration) {
        return Declaration.parseDeclaration(resolver, StringBuffer.of(declaration));
    }

    public static Declaration parseDeclaration(ClassResolver resolver, StringBuffer declaration) {
        MethodDeclaration mdec = new MethodDeclaration(resolver, declaration);
        if (mdec.isValid()) {
            return mdec;
        }
        ConstructorDeclaration cdec = new ConstructorDeclaration(resolver, declaration);
        if (cdec.isValid()) {
            return cdec;
        }
        FieldDeclaration fdec = new FieldDeclaration(resolver, declaration);
        if (fdec.isValid()) {
            return fdec;
        }
        return null;
    }

    protected class BaseDeclarationParserContext
    implements DeclarationParserContext {
        protected BaseDeclarationParserContext() {
        }

        @Override
        public ClassResolver getResolver() {
            return Declaration.this._resolver;
        }

        @Override
        public ParserStringBuffer getBuffer() {
            return Declaration.this._postfix;
        }

        @Override
        public void addWarning(String warning) {
            if (Declaration.this._warnings.isEmpty()) {
                Declaration.this._warnings = new ArrayList();
            }
            Declaration.this._warnings.add(warning);
            Declaration.this._loggedErrorsAndWarnings = false;
        }

        @Override
        public void addError(String error) {
            if (Declaration.this._errors.isEmpty()) {
                Declaration.this._errors = new ArrayList();
            }
            Declaration.this._errors.add(error);
            Declaration.this._loggedErrorsAndWarnings = false;
        }
    }
}

