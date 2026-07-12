/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.declarations.parsers;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParser;
import com.bergerkiller.mountiplex.reflection.declarations.parsers.DeclarationParserTypes;
import java.util.Arrays;
import java.util.stream.Stream;

public class DeclarationParserGroups {
    public static final DeclarationParser[] BASE = new DeclarationParser[]{DeclarationParserTypes.COMMENT, DeclarationParserTypes.BOOTSTRAP, DeclarationParserTypes.RESOLVER, DeclarationParserTypes.SET_VARIABLE, DeclarationParserTypes.REQUIREMENT, DeclarationParserTypes.REMAPPING, DeclarationParserTypes.ERROR, DeclarationParserTypes.WARNING};
    public static final DeclarationParser[] SOURCE = (DeclarationParser[])Stream.concat(Arrays.stream(BASE), Stream.of(DeclarationParserTypes.PACKAGE, DeclarationParserTypes.IMPORT, DeclarationParserTypes.INCLUDE, DeclarationParserTypes.SET_PATH)).toArray(DeclarationParser[]::new);
    public static final DeclarationParser[] CLASS = (DeclarationParser[])Stream.concat(MountiplexUtil.toStream(DeclarationParserTypes.CODE_BLOCK), Arrays.stream(BASE)).toArray(DeclarationParser[]::new);
}

