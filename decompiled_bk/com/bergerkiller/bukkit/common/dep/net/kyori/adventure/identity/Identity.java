/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.Identified;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.IdentityImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.identity.NilIdentity;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.pointer.Pointer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public interface Identity
extends Examinable,
Identified {
    public static final Pointer<String> NAME = Pointer.pointer(String.class, Key.key("adventure", "name"));
    public static final Pointer<UUID> UUID = Pointer.pointer(UUID.class, Key.key("adventure", "uuid"));
    public static final Pointer<Component> DISPLAY_NAME = Pointer.pointer(Component.class, Key.key("adventure", "display_name"));
    public static final Pointer<Locale> LOCALE = Pointer.pointer(Locale.class, Key.key("adventure", "locale"));

    @NotNull
    public static Identity nil() {
        return NilIdentity.INSTANCE;
    }

    @NotNull
    public static Identity identity(@NotNull UUID uuid) {
        if (uuid.equals(NilIdentity.NIL_UUID)) {
            return NilIdentity.INSTANCE;
        }
        return new IdentityImpl(uuid);
    }

    @NotNull
    public UUID uuid();

    @Override
    @NotNull
    default public Identity identity() {
        return this;
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("uuid", this.uuid()));
    }
}

