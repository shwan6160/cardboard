/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.data;

import java.util.function.Predicate;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BlockPredicate
extends Predicate<Block> {
    public @NonNull BlockPredicate loadChunks();
}

