/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.chat.ChatType;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ChatTypeImpl
implements ChatType {
    private final Key key;

    ChatTypeImpl(@NotNull Key key) {
        this.key = key;
    }

    @Override
    @NotNull
    public Key key() {
        return this.key;
    }

    public String toString() {
        return Internals.toString(this);
    }

    static final class BoundImpl
    implements ChatType.Bound {
        private final ChatType chatType;
        private final Component name;
        @Nullable
        private final Component target;

        BoundImpl(ChatType chatType, Component name, @Nullable Component target) {
            this.chatType = chatType;
            this.name = name;
            this.target = target;
        }

        @Override
        @NotNull
        public ChatType type() {
            return this.chatType;
        }

        @Override
        @NotNull
        public Component name() {
            return this.name;
        }

        @Override
        @Nullable
        public Component target() {
            return this.target;
        }

        public String toString() {
            return Internals.toString(this);
        }
    }
}

