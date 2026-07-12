/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers.context;

import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.ParserStringBuffer;

public interface DeclarationParserContext {
    public ClassResolver getResolver();

    public ParserStringBuffer getBuffer();

    public void addWarning(String var1);

    public void addError(String var1);

    default public boolean runParsers(DeclarationParser[] parsers) {
        ParserStringBuffer buffer = this.getBuffer();
        if (buffer.isNull()) {
            return false;
        }
        for (DeclarationParser parser : parsers) {
            if (!parser.detect(buffer, this)) continue;
            parser.parse(buffer, this);
            return true;
        }
        return false;
    }
}

