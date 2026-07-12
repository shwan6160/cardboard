/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.FlagDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface FlagAssembler {
    public @NonNull CommandFlag<?> assembleFlag(@NonNull FlagDescriptor var1);
}

