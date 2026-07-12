/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.state;

import com.bergerkiller.bukkit.common.dep.cloud.state.State;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface Stateful<S extends State> {
    public @NonNull S state();

    public boolean transitionIfPossible(@NonNull S var1, @NonNull S var2);

    default public void requireState(@NonNull S expected) {
        if (this.state().equals(expected)) {
            return;
        }
        throw new IllegalStateException(String.format("This operation requires the command manager to be in state '%s', but it is in '%s'", expected, this.state()));
    }

    default public void transitionOrThrow(@NonNull S in, @NonNull S out) {
        if (this.transitionIfPossible(in, out)) {
            return;
        }
        throw new IllegalStateException(String.format("The current state is '%s' but we expected a state of '%s' or '%s'", this.state(), in, out));
    }
}

