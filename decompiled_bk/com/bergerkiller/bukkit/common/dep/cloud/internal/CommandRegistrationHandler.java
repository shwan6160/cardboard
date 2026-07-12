/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.internal;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@API(status=API.Status.STABLE)
public interface CommandRegistrationHandler<C> {
    public static <C> @NonNull CommandRegistrationHandler<C> nullCommandRegistrationHandler() {
        return new NullCommandRegistrationHandler();
    }

    public boolean registerCommand(@NonNull Command<C> var1);

    @API(status=API.Status.STABLE)
    default public void unregisterRootCommand(@NonNull CommandComponent<C> rootCommand) {
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    public static final class NullCommandRegistrationHandler<C>
    implements CommandRegistrationHandler<C> {
        private NullCommandRegistrationHandler() {
        }

        @Override
        public boolean registerCommand(@NonNull Command<C> command) {
            return true;
        }

        @Override
        public void unregisterRootCommand(@NonNull CommandComponent<C> rootCommand) {
        }
    }
}

