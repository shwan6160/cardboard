/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.renderer;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.BlockNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.EntityNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.KeybindComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.NBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.ScoreComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.SelectorComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.StorageNBTComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TextComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslatableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.VirtualComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.renderer.ComponentRenderer;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractComponentRenderer<C>
implements ComponentRenderer<C> {
    @Override
    @NotNull
    public Component render(@NotNull Component component, @NotNull C context) {
        if (component instanceof VirtualComponent) {
            component = this.renderVirtual((VirtualComponent)component, context);
        }
        if (component instanceof TextComponent) {
            return this.renderText((TextComponent)component, context);
        }
        if (component instanceof TranslatableComponent) {
            return this.renderTranslatable((TranslatableComponent)component, context);
        }
        if (component instanceof KeybindComponent) {
            return this.renderKeybind((KeybindComponent)component, context);
        }
        if (component instanceof ScoreComponent) {
            return this.renderScore((ScoreComponent)component, context);
        }
        if (component instanceof SelectorComponent) {
            return this.renderSelector((SelectorComponent)component, context);
        }
        if (component instanceof NBTComponent) {
            if (component instanceof BlockNBTComponent) {
                return this.renderBlockNbt((BlockNBTComponent)component, context);
            }
            if (component instanceof EntityNBTComponent) {
                return this.renderEntityNbt((EntityNBTComponent)component, context);
            }
            if (component instanceof StorageNBTComponent) {
                return this.renderStorageNbt((StorageNBTComponent)component, context);
            }
        }
        return component;
    }

    @NotNull
    protected abstract Component renderBlockNbt(@NotNull BlockNBTComponent var1, @NotNull C var2);

    @NotNull
    protected abstract Component renderEntityNbt(@NotNull EntityNBTComponent var1, @NotNull C var2);

    @NotNull
    protected abstract Component renderStorageNbt(@NotNull StorageNBTComponent var1, @NotNull C var2);

    @NotNull
    protected abstract Component renderKeybind(@NotNull KeybindComponent var1, @NotNull C var2);

    @NotNull
    protected abstract Component renderScore(@NotNull ScoreComponent var1, @NotNull C var2);

    @NotNull
    protected abstract Component renderSelector(@NotNull SelectorComponent var1, @NotNull C var2);

    @NotNull
    protected abstract Component renderText(@NotNull TextComponent var1, @NotNull C var2);

    @NotNull
    protected Component renderVirtual(@NotNull VirtualComponent component, @NotNull C context) {
        return component;
    }

    @NotNull
    protected abstract Component renderTranslatable(@NotNull TranslatableComponent var1, @NotNull C var2);
}

