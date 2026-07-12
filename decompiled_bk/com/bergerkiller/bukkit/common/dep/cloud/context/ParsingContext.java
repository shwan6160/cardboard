/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.context;

import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import java.time.Duration;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.MAINTAINED)
public final class ParsingContext<C> {
    private final CommandComponent<@NonNull C> component;
    private @Nullable String consumed = null;
    private long startTime = -1L;
    private long endTime = -1L;
    private int consumedFrom = -1;
    private int consumedTo = -1;
    private boolean success;

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public ParsingContext(@NonNull CommandComponent<@NonNull C> component) {
        this.component = component;
    }

    public @NonNull CommandComponent<C> component() {
        return this.component;
    }

    public @NonNull Duration parseDuration() {
        if (this.startTime < 0L) {
            throw new IllegalStateException("No start time has been registered");
        }
        if (this.endTime < 0L) {
            throw new IllegalStateException("No end time has been registered");
        }
        return Duration.ofNanos(this.endTime - this.startTime);
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public void markStart() {
        this.startTime = System.nanoTime();
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public void markEnd() {
        this.endTime = System.nanoTime();
    }

    long startTime() {
        return this.startTime;
    }

    long endTime() {
        return this.endTime;
    }

    public boolean success() {
        return this.success;
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public void success(boolean success) {
        this.success = success;
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public void consumedInput(@NonNull CommandInput original, @NonNull CommandInput postParse) {
        if (this.consumed != null) {
            throw new IllegalStateException();
        }
        this.consumed = original.difference(postParse);
        this.consumedFrom = original.cursor();
        this.consumedTo = original.cursor() + this.consumed.length();
    }

    @API(status=API.Status.STABLE)
    public @NonNull String consumedInput() {
        return Objects.requireNonNull(this.consumed);
    }

    public @Nullable String exactAlias() {
        if (!this.success || this.component.type() != CommandComponent.ComponentType.LITERAL) {
            return null;
        }
        return this.consumed;
    }

    @API(status=API.Status.STABLE)
    public int consumedFrom() {
        return this.consumedFrom;
    }

    @API(status=API.Status.STABLE)
    public int consumedTo() {
        return this.consumedTo;
    }
}

