/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers;

import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.DeclarationParserContext;

abstract class MacroParser
implements DeclarationParser {
    private final String prefix;

    public MacroParser(String macro) {
        this.prefix = macro + " ";
    }

    @Override
    public boolean detect(ParserStringBuffer buffer, DeclarationParserContext context) {
        return buffer.startsWith(this.prefix);
    }

    @Override
    public void parse(ParserStringBuffer buffer, DeclarationParserContext context) {
        buffer.trimWhitespace(this.prefix.length());
    }
}

