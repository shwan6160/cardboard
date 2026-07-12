/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers.context;

import com.bergerkiller.mountiplex.reflection.declarations.parsers.context.DeclarationParserContext;
import com.bergerkiller.mountiplex.reflection.util.StringBuffer;
import java.io.File;

public interface SourceDeclarationParserContext
extends DeclarationParserContext {
    public ClassLoader getClassLoader();

    public File getCurrentDirectory();

    public void includeSource(StringBuffer var1);

    public void setCurrentTemplateFile(String var1);

    public String getCurrentTemplateFile();
}

