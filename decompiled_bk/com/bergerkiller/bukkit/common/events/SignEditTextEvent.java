/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.block.BlockEvent
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.block.SignLineAccessor;
import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.block.SignSideLineAccessor;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class SignEditTextEvent
extends BlockEvent
implements Cancellable,
SignSideLineAccessor {
    private static final HandlerList handlers = new HandlerList();
    private final Sign sign;
    private final Player player;
    private final EditReason editReason;
    private final SignSide signSide;
    private final SignLineAccessor lineAccessor;
    private boolean cancelled = false;

    public SignEditTextEvent(@NotNull Player player, @NotNull Block signBlock, @NotNull Sign sign, @NonNull EditReason editReason, @NotNull SignSide signSide, @NonNull SignLineAccessor lineAccessor) {
        super(signBlock);
        this.sign = sign;
        this.player = player;
        this.editReason = editReason;
        this.signSide = signSide;
        this.lineAccessor = lineAccessor;
    }

    public Sign getSign() {
        return this.sign;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public SignSide getSide() {
        return this.signSide;
    }

    public EditReason getEditReason() {
        return this.editReason;
    }

    public SignLineAccessor getAllSideLines() {
        return this.lineAccessor;
    }

    @Override
    public String[] getLines() {
        return this.lineAccessor.getLines(this.signSide);
    }

    @Override
    public String getLine(int index) {
        return this.lineAccessor.getLine(this.signSide, index);
    }

    @Override
    public void setLine(int index, String text) {
        this.lineAccessor.setLine(this.signSide, index, text);
    }

    @Override
    public ChatText[] getFormattedLines() {
        return this.signSide.isFront() ? this.lineAccessor.getFormattedFrontLines() : this.lineAccessor.getFormattedBackLines();
    }

    @Override
    public ChatText getFormattedLine(int index) {
        return this.signSide.isFront() ? this.lineAccessor.getFormattedFrontLine(index) : this.lineAccessor.getFormattedBackLine(index);
    }

    @Override
    public void setFormattedLine(int index, ChatText text) {
        if (this.signSide.isFront()) {
            this.lineAccessor.setFormattedFrontLine(index, text);
        } else {
            this.lineAccessor.setFormattedBackLine(index, text);
        }
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static enum EditReason {
        PLACE,
        EDIT,
        CTRL_PICK_PLACE;

    }
}

