/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.Declaration;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.MacroParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.DeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.util.logging.Level;

abstract class MacroDeclarationParser
extends MacroParser {
    private final String typeName;

    public MacroDeclarationParser(String macro, String typeName) {
        super(macro);
        this.typeName = typeName;
    }

    public abstract void runMacro(Declaration var1, DeclarationParserContext var2);

    @Override
    public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
        super.parse(buffer, context);
        int declaringClassEnd = buffer.get().indexOf(' ');
        if (declaringClassEnd == -1) {
            buffer.set(StringBuffer.EMPTY);
            return;
        }
        String declaringClassName = buffer.get().substringToString(0, declaringClassEnd);
        buffer.trimWhitespace(declaringClassEnd);
        ClassResolver resolver = context.getResolver().clone();
        resolver.setDeclaredClassName(declaringClassName);
        Declaration dec = buffer.detectMemberDeclaration(resolver);
        if (dec == null) {
            String remainder = buffer.trimLine();
            if (context.getResolver().getLogErrors()) {
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Declaration invalid for " + this.typeName + ": " + declaringClassName);
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Declaration: " + remainder);
            }
            return;
        }
        if (context.getResolver().isGenerating()) {
            return;
        }
        this.runMacro(dec, context);
    }
}

