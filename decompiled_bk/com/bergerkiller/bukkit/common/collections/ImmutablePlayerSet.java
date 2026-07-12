/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.collections.ImmutableCachedSet;
import com.bergerkiller.bukkit.common.internal.CommonListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public final class ImmutablePlayerSet
extends ImmutableCachedSet<Player> {
    public static final ImmutablePlayerSet EMPTY = new ImmutablePlayerSet();

    private ImmutablePlayerSet() {
    }

    private ImmutablePlayerSet(ImmutablePlayerSet emptyRoot, Set<Player> values, int hashCode) {
        super(emptyRoot, values, hashCode);
    }

    protected ImmutablePlayerSet createNew(Set<Player> values, int hashCode) {
        return new ImmutablePlayerSet(this, values, hashCode);
    }

    @Override
    public Iterator<Player> iterator() {
        return super.iterator();
    }

    @Override
    public Stream<Player> stream() {
        return super.stream();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean contains(Player player) {
        return super.contains(player);
    }

    @Override
    public boolean containsAll(Collection<Player> players) {
        return super.containsAll(players);
    }

    public ImmutablePlayerSet remove(Player player) {
        return (ImmutablePlayerSet)super.remove(player);
    }

    public ImmutablePlayerSet addAll(Iterable<Player> players) {
        return (ImmutablePlayerSet)super.addAll(players);
    }

    public ImmutablePlayerSet add(Player player) {
        return (ImmutablePlayerSet)super.add(player);
    }

    public ImmutablePlayerSet removeAll(Iterable<Player> players) {
        return (ImmutablePlayerSet)super.removeAll(players);
    }

    public ImmutablePlayerSet addOrRemove(Player value, boolean add) {
        return (ImmutablePlayerSet)super.addOrRemove(value, add);
    }

    @Override
    public int size() {
        return super.size();
    }

    public ImmutablePlayerSet clear() {
        return EMPTY;
    }

    public static ImmutablePlayerSet get(Player player) {
        return EMPTY.add(player);
    }

    public static ImmutablePlayerSet get(Player ... players) {
        if (players.length == 0) {
            return EMPTY;
        }
        return EMPTY.addAll((Iterable)Arrays.asList(players));
    }

    public static ImmutablePlayerSet get(Iterable<Player> players) {
        return EMPTY.addAll((Iterable)players);
    }

    static {
        CommonListener.registerImmutablePlayerSet(EMPTY);
    }
}

