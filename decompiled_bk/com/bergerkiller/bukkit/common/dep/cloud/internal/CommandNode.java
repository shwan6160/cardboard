/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.MonotonicNonNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.internal;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import com.bergerkiller.bukkit.common.dep.cloud.key.SimpleMutableCloudKeyContainer;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class CommandNode<C> {
    public static final CloudKey<Set<Type>> META_KEY_SENDER_TYPES = CloudKey.cloudKey("senderTypes", new TypeToken<Set<Type>>(){});
    public static final CloudKey<Map<Type, Permission>> META_KEY_ACCESS = CloudKey.cloudKey("access", new TypeToken<Map<Type, Permission>>(){});
    private final SimpleMutableCloudKeyContainer nodeMeta = new SimpleMutableCloudKeyContainer(new HashMap());
    private final List<CommandNode<C>> children = new LinkedList<CommandNode<C>>();
    private final CommandComponent<C> component;
    private CommandNode<C> parent;
    private Command<C> command;

    public CommandNode(@Nullable CommandComponent<C> component) {
        this.component = component;
    }

    public @NonNull List<@NonNull CommandNode<C>> children() {
        return Collections.unmodifiableList(this.children);
    }

    public @NonNull CommandNode<C> addChild(@NonNull CommandComponent<C> component) {
        CommandNode<C> node = new CommandNode<C>(component);
        this.children.add(node);
        return node;
    }

    public @Nullable CommandNode<C> getChild(@NonNull CommandComponent<C> component) {
        for (CommandNode<C> child : this.children) {
            if (!component.equals(child.component())) continue;
            return child;
        }
        return null;
    }

    public boolean removeChild(@NonNull CommandNode<C> child) {
        return this.children.remove(child);
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public @NonNull SimpleMutableCloudKeyContainer nodeMeta() {
        return this.nodeMeta;
    }

    public @MonotonicNonNull CommandComponent<C> component() {
        return this.component;
    }

    public @MonotonicNonNull Command<C> command() {
        return this.command;
    }

    public void command(@NonNull Command<C> command) {
        if (this.command != null) {
            throw new IllegalStateException("Cannot replace owning command");
        }
        this.command = command;
    }

    public @Nullable CommandNode<C> parent() {
        return this.parent;
    }

    public void parent(@Nullable CommandNode<C> parent) {
        this.parent = parent;
    }

    public void sortChildren() {
        this.children.sort(Comparator.comparing(CommandNode::component));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CommandNode node = (CommandNode)o;
        return Objects.equals(this.component(), node.component());
    }

    public int hashCode() {
        return Objects.hash(this.component());
    }

    public String toString() {
        return "Node{value=" + this.component + '}';
    }
}

