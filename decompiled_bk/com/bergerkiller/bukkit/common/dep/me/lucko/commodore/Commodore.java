/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.dep.me.lucko.commodore;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Objects;
import java.util.function.Predicate;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public interface Commodore {
    public void register(Command var1, LiteralCommandNode<?> var2, Predicate<? super Player> var3);

    default public void register(Command command, LiteralArgumentBuilder<?> argumentBuilder, Predicate<? super Player> permissionTest) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(argumentBuilder, "argumentBuilder");
        Objects.requireNonNull(permissionTest, "permissionTest");
        this.register(command, (LiteralCommandNode<?>)argumentBuilder.build(), permissionTest);
    }

    default public void register(Command command, LiteralCommandNode<?> node) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(node, "node");
        this.register(command, node, arg_0 -> ((Command)command).testPermissionSilent(arg_0));
    }

    default public void register(Command command, LiteralArgumentBuilder<?> argumentBuilder) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(argumentBuilder, "argumentBuilder");
        this.register(command, (LiteralCommandNode<?>)argumentBuilder.build());
    }

    public void register(LiteralCommandNode<?> var1);

    default public void register(LiteralArgumentBuilder<?> argumentBuilder) {
        Objects.requireNonNull(argumentBuilder, "argumentBuilder");
        this.register((LiteralCommandNode<?>)argumentBuilder.build());
    }
}

