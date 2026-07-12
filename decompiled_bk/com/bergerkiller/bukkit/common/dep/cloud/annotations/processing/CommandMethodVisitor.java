/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.processing;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.ArgumentMode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxParserImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import org.checkerframework.checker.nullness.qual.NonNull;

class CommandMethodVisitor
implements ElementVisitor<Void, Void> {
    private final ProcessingEnvironment processingEnvironment;
    private final SyntaxParserImpl syntaxParser;

    CommandMethodVisitor(@NonNull ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
        this.syntaxParser = new SyntaxParserImpl();
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
        return null;
    }

    @Override
    public Void visitVariable(VariableElement e, Void unused) {
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Void unused) {
        Command[] commands;
        if (!e.getModifiers().contains((Object)Modifier.PUBLIC)) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("@Command annotated methods should be public (%s)", e.getSimpleName()), e);
        }
        if (e.getModifiers().contains((Object)Modifier.STATIC)) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@Command annotated methods should be non-static (%s)", e.getSimpleName()), e);
        }
        if (e.getReturnType().toString().equals("Void")) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@Command annotated methods should return void (%s)", e.getSimpleName()), e);
        }
        for (Command command : commands = (Command[])e.getAnnotationsByType(Command.class)) {
            List annotatedArgumentNames = e.getParameters().stream().map(parameter -> parameter.getAnnotation(Argument.class)).filter(Objects::nonNull).map(Argument::value).filter(name -> !name.equals("__INFERRED_ARGUMENT_NAME__")).collect(Collectors.toList());
            ArrayList parameterArgumentNames = new ArrayList(annotatedArgumentNames);
            e.getParameters().stream().filter(parameter -> {
                Argument argument = parameter.getAnnotation(Argument.class);
                return argument == null || "__INFERRED_ARGUMENT_NAME__".equals(argument.value());
            }).map(parameter -> parameter.getSimpleName().toString()).forEach(parameterArgumentNames::add);
            ArrayList<String> parsedArgumentNames = new ArrayList<String>(parameterArgumentNames.size());
            List<SyntaxFragment> syntaxFragments = this.syntaxParser.parseSyntax(null, command.value());
            boolean foundOptional = false;
            for (SyntaxFragment fragment : syntaxFragments) {
                if (fragment.argumentMode() == ArgumentMode.LITERAL) continue;
                if (!parameterArgumentNames.contains(fragment.major())) {
                    this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@Argument(\"%s\") is missing from @Command (%s)", fragment.major(), e.getSimpleName()), e);
                }
                if (fragment.argumentMode() == ArgumentMode.REQUIRED) {
                    if (foundOptional) {
                        this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Required argument '%s' cannot succeed an optional argument (%s)", fragment.major(), e.getSimpleName()), e);
                    }
                } else {
                    foundOptional = true;
                }
                parsedArgumentNames.add(fragment.major());
            }
            for (String argument : annotatedArgumentNames) {
                if (parsedArgumentNames.contains(argument)) continue;
                this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Argument '%s' is missing from the @Command syntax (%s)", argument, e.getSimpleName()), e);
            }
        }
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

