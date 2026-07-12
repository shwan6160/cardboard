/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.ArgumentMode;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class SyntaxFragment {
    private final String major;
    private final List<String> minor;
    private final ArgumentMode argumentMode;

    public SyntaxFragment(@NonNull String major, @NonNull List<@NonNull String> minor, @NonNull ArgumentMode argumentMode) {
        this.major = major;
        this.minor = minor;
        this.argumentMode = argumentMode;
    }

    public @NonNull String major() {
        return this.major;
    }

    public @NonNull List<@NonNull String> minor() {
        return this.minor;
    }

    public @NonNull ArgumentMode argumentMode() {
        return this.argumentMode;
    }
}

