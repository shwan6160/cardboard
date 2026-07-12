/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE, since="2.0.0")
public final class SelectorUnsupportedException
extends ParserException {
    public SelectorUnsupportedException(@NonNull CommandContext<?> context, @NonNull Class<?> parser) {
        super(parser, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_SELECTOR_UNSUPPORTED, new CaptionVariable[0]);
    }
}

