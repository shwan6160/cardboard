/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.assembler;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.ArgumentDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface ArgumentAssembler<C> {
    public @NonNull CommandComponent<C> assembleArgument(@NonNull SyntaxFragment var1, @NonNull ArgumentDescriptor var2);
}

