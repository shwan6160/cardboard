/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.mountiplex.reflection.ClassHook;

public class LegacyContainerAnvilHook
extends ClassHook<LegacyContainerAnvilHook> {
    public Runnable textChangeCallback = null;

    @ClassHook.HookMethod(value="public void a(String s)")
    public void onTextChange(String newText) {
        ((LegacyContainerAnvilHook)this.base).onTextChange(newText);
        if (this.textChangeCallback != null) {
            this.textChangeCallback.run();
        }
    }
}

