/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.SignEditTextEvent
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.events.SignEditTextEvent;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.PowerState;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.utils.FakeSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;

public class SignBuildEvent
extends SignChangeActionEvent {
    private static final HandlerList handlers = new HandlerList();
    private final SignAction action;

    public SignBuildEvent(Player player, RailLookup.TrackedSign sign, boolean interactive) {
        super(player, sign, interactive);
        this.action = SignAction.getSignAction(this);
    }

    public SignBuildEvent(Player player, RailLookup.TrackedSign sign, boolean interactive, SignAction action) {
        super(player, sign, interactive);
        this.action = action;
    }

    @Deprecated
    public SignBuildEvent(SignChangeEvent event, boolean interactive) {
        super(event, interactive);
        this.action = SignAction.getSignAction(this);
    }

    @Deprecated
    public SignBuildEvent(SignChangeActionEvent event) {
        super(event);
        this.action = SignAction.getSignAction(event);
    }

    protected SignBuildEvent(Cancellable event, Player player, RailLookup.TrackedSign sign, boolean interactive) {
        super(event, player, sign, interactive);
        this.action = SignAction.getSignAction(this);
    }

    public boolean hasRegisteredAction() {
        return this.action != null;
    }

    public SignAction getRegisteredAction() {
        return this.action;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static class BKCLSignEditBuildEvent
    extends SignBuildEvent {
        public static SignBuildEvent create(SignEditTextEvent event, boolean interactive) {
            return new BKCLSignEditBuildEvent(event, interactive);
        }

        private BKCLSignEditBuildEvent(SignEditTextEvent event, boolean interactive) {
            super((Cancellable)event, event.getPlayer(), new TrackedEditedSign(event), interactive);
        }

        private static class TrackedEditedSign
        extends RailLookup.TrackedRealSign {
            private final SignEditTextEvent event;
            private final boolean front;

            public TrackedEditedSign(final SignEditTextEvent event) {
                super(FakeSign.create(event.getBlock()), event.getBlock(), RailPiece.NONE);
                this.front = event.getSide().isFront();
                ((FakeSign)this.sign).setHandler(new FakeSign.HandlerSignFallback(this, this.signBlock){
                    final /* synthetic */ TrackedEditedSign this$0;
                    {
                        this.this$0 = this$0;
                        super(signBlock);
                    }

                    @Override
                    public String getFrontLine(int index) {
                        return this.this$0.front ? event.getLine(index) : super.getFrontLine(index);
                    }

                    @Override
                    public void setFrontLine(int index, String text) {
                        if (this.this$0.front) {
                            event.setLine(index, text);
                        } else {
                            super.setFrontLine(index, text);
                        }
                    }

                    @Override
                    public String getBackLine(int index) {
                        return this.this$0.front ? super.getBackLine(index) : event.getLine(index);
                    }

                    @Override
                    public void setBackLine(int index, String text) {
                        if (this.this$0.front) {
                            super.setBackLine(index, text);
                        } else {
                            event.setLine(index, text);
                        }
                    }
                });
                this.rail = null;
                this.event = event;
            }

            @Override
            public boolean isFrontText() {
                return this.front;
            }

            @Override
            public boolean verify() {
                return false;
            }

            @Override
            public boolean isRemoved() {
                return (Boolean)MaterialUtil.ISSIGN.get(this.event.getBlock()) == false;
            }

            @Override
            public BlockFace getFacing() {
                return BlockUtil.getFacing((Block)this.event.getBlock());
            }

            @Override
            public Block getAttachedBlock() {
                return BlockUtil.getAttachedBlock((Block)this.event.getBlock());
            }

            @Override
            public String[] getExtraLines() {
                return new String[0];
            }

            @Override
            public PowerState getPower(BlockFace from) {
                return PowerState.get(this.signBlock, from, this.getAction() != null ? PowerState.Options.SIGN_CONNECT_WIRE : PowerState.Options.SIGN);
            }

            @Override
            public String getLine(int index) throws IndexOutOfBoundsException {
                return this.event.getLine(index);
            }

            @Override
            public void setLine(int index, String line) throws IndexOutOfBoundsException {
                this.event.setLine(index, line);
            }

            @Override
            public Object getUniqueKey() {
                return this;
            }
        }
    }
}

