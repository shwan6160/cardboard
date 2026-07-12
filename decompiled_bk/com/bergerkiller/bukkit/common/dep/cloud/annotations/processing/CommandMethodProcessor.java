/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.processing;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.processing.CommandMethodVisitor;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes(value={"com.bergerkiller.bukkit.common.dep.cloud.annotations.Command"})
public final class CommandMethodProcessor
extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Command.class);
        if (elements.isEmpty()) {
            return false;
        }
        for (Element element : elements) {
            if (element.getKind() != ElementKind.METHOD) continue;
            element.accept(new CommandMethodVisitor(this.processingEnv), null);
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}

