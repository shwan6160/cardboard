/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers;

import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.DeclarationParserContext;

public interface DeclarationParser {
    public boolean detect(ParserStringBuffer var1, DeclarationParserContext var2);

    public void parse(ParserStringBuffer var1, DeclarationParserContext var2);
}

