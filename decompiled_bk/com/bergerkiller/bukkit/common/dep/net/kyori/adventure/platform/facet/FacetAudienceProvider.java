/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.ForwardingAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.permission.PermissionChecker;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.AudienceProvider;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetAudience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetPointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointered;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointers;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.renderer.ComponentRenderer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.TriState;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public abstract class FacetAudienceProvider<V, A extends FacetAudience<V>>
implements AudienceProvider,
ForwardingAudience {
    protected static final Locale DEFAULT_LOCALE = Locale.US;
    protected final ComponentRenderer<Pointered> componentRenderer;
    private final Audience console;
    private final Audience player;
    protected final Map<V, A> viewers;
    private final Map<UUID, A> players;
    private final Set<A> consoles;
    private A empty;
    private volatile boolean closed;

    protected FacetAudienceProvider(@NotNull ComponentRenderer<Pointered> componentRenderer) {
        this.componentRenderer = Objects.requireNonNull(componentRenderer, "component renderer");
        this.viewers = new ConcurrentHashMap<V, A>();
        this.players = new ConcurrentHashMap<UUID, A>();
        this.consoles = new CopyOnWriteArraySet<A>();
        this.console = new ForwardingAudience(){

            @Override
            @NotNull
            public Iterable<? extends Audience> audiences() {
                return FacetAudienceProvider.this.consoles;
            }

            @Override
            @NotNull
            public Pointers pointers() {
                if (FacetAudienceProvider.this.consoles.size() == 1) {
                    return ((FacetAudience)FacetAudienceProvider.this.consoles.iterator().next()).pointers();
                }
                return Pointers.empty();
            }
        };
        this.player = Audience.audience(this.players.values());
        this.closed = false;
    }

    public void addViewer(@NotNull V viewer) {
        if (this.closed) {
            return;
        }
        FacetAudience audience = this.viewers.computeIfAbsent((FacetAudience)Objects.requireNonNull(viewer, "viewer"), (Function<FacetAudience, A>)((Function<Object, FacetAudience>)v -> this.createAudience(Collections.singletonList(v))));
        FacetPointers.Type type = audience.getOrDefault(FacetPointers.TYPE, FacetPointers.Type.OTHER);
        if (type == FacetPointers.Type.PLAYER) {
            @Nullable UUID id = audience.getOrDefault(Identity.UUID, null);
            if (id != null) {
                this.players.putIfAbsent(id, audience);
            }
        } else if (type == FacetPointers.Type.CONSOLE) {
            this.consoles.add(audience);
        }
    }

    public void removeViewer(@NotNull V viewer) {
        FacetAudience audience = (FacetAudience)this.viewers.remove(viewer);
        if (audience == null) {
            return;
        }
        FacetPointers.Type type = audience.getOrDefault(FacetPointers.TYPE, FacetPointers.Type.OTHER);
        if (type == FacetPointers.Type.PLAYER) {
            @Nullable UUID id = audience.getOrDefault(Identity.UUID, null);
            if (id != null) {
                this.players.remove(id);
            }
        } else if (type == FacetPointers.Type.CONSOLE) {
            this.consoles.remove(audience);
        }
        audience.close();
    }

    public void refreshViewer(@NotNull V viewer) {
        FacetAudience audience = (FacetAudience)this.viewers.get(viewer);
        if (audience != null) {
            audience.refresh();
        }
    }

    @NotNull
    protected abstract A createAudience(@NotNull Collection<V> var1);

    @Override
    @NotNull
    public Iterable<? extends Audience> audiences() {
        return this.viewers.values();
    }

    @Override
    @NotNull
    public Audience all() {
        return this;
    }

    @Override
    @NotNull
    public Audience console() {
        return this.console;
    }

    @Override
    @NotNull
    public Audience players() {
        return this.player;
    }

    @Override
    @NotNull
    public Audience player(@NotNull UUID playerId) {
        return (Audience)this.players.getOrDefault(playerId, this.empty());
    }

    @NotNull
    private A empty() {
        if (this.empty == null) {
            this.empty = this.createAudience(Collections.emptyList());
        }
        return this.empty;
    }

    @NotNull
    public Audience filter(@NotNull Predicate<V> predicate) {
        return Audience.audience(FacetAudienceProvider.filter(this.viewers.entrySet(), entry -> predicate.test(entry.getKey()), Map.Entry::getValue));
    }

    @NotNull
    private Audience filterPointers(@NotNull Predicate<Pointered> predicate) {
        return Audience.audience(FacetAudienceProvider.filter(this.viewers.entrySet(), entry -> predicate.test((Pointered)entry.getValue()), Map.Entry::getValue));
    }

    @Override
    @NotNull
    public Audience permission(@NotNull String permission) {
        return this.filterPointers(pointers -> pointers.get(PermissionChecker.POINTER).orElse(PermissionChecker.always(TriState.FALSE)).test(permission));
    }

    @Override
    @NotNull
    public Audience world(@NotNull Key world) {
        return this.filterPointers(pointers -> world.equals(pointers.getOrDefault(FacetPointers.WORLD, null)));
    }

    @Override
    @NotNull
    public Audience server(@NotNull String serverName) {
        return this.filterPointers(pointers -> serverName.equals(pointers.getOrDefault(FacetPointers.SERVER, null)));
    }

    @Override
    public void close() {
        this.closed = true;
        for (V viewer : this.viewers.keySet()) {
            this.removeViewer(viewer);
        }
    }

    @NotNull
    private static <T, V> Iterable<V> filter(final @NotNull Iterable<T> input, final @NotNull Predicate<T> filter, final @NotNull Function<T, V> transformer) {
        return new Iterable<V>(){

            @Override
            @NotNull
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    private final Iterator<T> parent;
                    private V next;
                    {
                        this.parent = input.iterator();
                        this.populate();
                    }

                    private void populate() {
                        this.next = null;
                        while (this.parent.hasNext()) {
                            Object next = this.parent.next();
                            if (!filter.test(next)) continue;
                            this.next = transformer.apply(next);
                            return;
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return this.next != null;
                    }

                    @Override
                    public V next() {
                        if (this.next == null) {
                            throw new NoSuchElementException();
                        }
                        Object next = this.next;
                        this.populate();
                        return next;
                    }
                };
            }

            @Override
            public void forEach(Consumer<? super V> action) {
                for (Object each : input) {
                    if (!filter.test(each)) continue;
                    action.accept(transformer.apply(each));
                }
            }
        };
    }
}

