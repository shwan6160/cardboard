/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserRegistry;

public interface ParserContributor {
    public <C> void contribute(ParserRegistry<C> var1);
}

