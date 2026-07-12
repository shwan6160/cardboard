/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import org.checkerframework.checker.nullness.qual.NonNull;

final class CommandContainerVisitor
implements ElementVisitor<Void, Void> {
    private static final Collection<String> PERMITTED_CONSTRUCTOR_PARAMETER_TYPES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser")));
    private final ProcessingEnvironment processingEnvironment;
    private final Collection<String> validTypes;
    private final Map<Element, List<String>> errorsByElement = new HashMap<Element, List<String>>();
    private boolean suitableConstructorFound;

    CommandContainerVisitor(@NonNull ProcessingEnvironment processingEnvironment, @NonNull Collection<@NonNull String> validTypes) {
        this.processingEnvironment = processingEnvironment;
        this.validTypes = validTypes;
        this.suitableConstructorFound = false;
    }

    @Override
    public Void visit(Element e) {
        return this.visit(e, null);
    }

    @Override
    public Void visit(Element e, Void unused) {
        return null;
    }

    @Override
    public Void visitPackage(PackageElement e, Void unused) {
        return null;
    }

    @Override
    public Void visitType(TypeElement e, Void unused) {
        if (!e.getModifiers().contains((Object)Modifier.PUBLIC)) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@CommandContainer-annotated class must be public (%s)", e.getSimpleName()), e);
            return null;
        }
        for (Element element : e.getEnclosedElements()) {
            if (element.getKind() != ElementKind.CONSTRUCTOR) continue;
            element.accept(this, null);
            if (!this.suitableConstructorFound) continue;
            break;
        }
        if (!this.suitableConstructorFound) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@CommandContainer-annotated class must have a suitable constructor (%s)", e.getSimpleName()), e);
            for (Map.Entry entry : this.errorsByElement.entrySet()) {
                this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, "Constructor not suitable: " + String.join((CharSequence)", ", (Iterable)entry.getValue()), (Element)entry.getKey());
            }
            return null;
        }
        this.validTypes.add(e.asType().toString());
        return null;
    }

    @Override
    public Void visitVariable(VariableElement e, Void unused) {
        return null;
    }

    private void logError(Element element, String err) {
        this.errorsByElement.computeIfAbsent(element, $ -> new ArrayList()).add(err);
    }

    private void logInvalidParam(VariableElement param) {
        String allowedParams;
        List errors = this.errorsByElement.computeIfAbsent(param, $ -> new ArrayList());
        if (!errors.contains(allowedParams = "Recognized parameter types: " + PERMITTED_CONSTRUCTOR_PARAMETER_TYPES)) {
            errors.add(allowedParams);
        }
        errors.add("Parameter '" + param + "' is not a recognized type");
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Void unused) {
        if (!e.getModifiers().contains((Object)Modifier.PUBLIC)) {
            this.logError(e, "Is not public.");
            return null;
        }
        boolean illegalParams = false;
        for (VariableElement variableElement : e.getParameters()) {
            boolean isDeclared = variableElement.asType() instanceof DeclaredType;
            if (!isDeclared) {
                this.logInvalidParam(variableElement);
                illegalParams = true;
                continue;
            }
            DeclaredType type = (DeclaredType)variableElement.asType();
            Element typeElement = type.asElement();
            String typeName = typeElement.asType().toString().split("<")[0];
            if (PERMITTED_CONSTRUCTOR_PARAMETER_TYPES.contains(typeName)) continue;
            this.logInvalidParam(variableElement);
            illegalParams = true;
        }
        if (illegalParams) {
            return null;
        }
        this.suitableConstructorFound = true;
        return null;
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, Void unused) {
        return null;
    }

    @Override
    public Void visitUnknown(Element e, Void unused) {
        return null;
    }
}

