/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.processing;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.processing.CommandContainer;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.processing.CommandContainerVisitor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

@SupportedAnnotationTypes(value={"com.bergerkiller.bukkit.common.dep.cloud.annotations.processing.CommandContainer"})
public final class CommandContainerProcessor
extends AbstractProcessor {
    public static final String PATH = "META-INF/commands/org.incendo.cloud.annotations.processing.CommandContainer";

    @Override
    public boolean process(@NonNull Set<? extends TypeElement> annotations, @NonNull RoundEnvironment roundEnv) {
        ArrayList<String> validTypes = new ArrayList<String>();
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(CommandContainer.class);
        if (elements.isEmpty()) {
            return false;
        }
        for (Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("@Command found on unsupported element type '%s' (%s)", element.getKind().name(), element.getSimpleName().toString()), element);
                return false;
            }
            element.accept(new CommandContainerVisitor(this.processingEnv, validTypes), null);
        }
        for (String string : validTypes) {
            this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("Found valid @CommandContainer-annotated class: %s", string));
        }
        this.writeCommandFile(validTypes);
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    private void writeCommandFile(@NonNull List<String> types) {
        try (BufferedWriter writer = new BufferedWriter(this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", PATH, new Element[0]).openWriter());){
            for (String t : types) {
                writer.write(t);
                writer.newLine();
            }
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

