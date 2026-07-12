/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.bukkit.tc.events;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.PowerState;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.utils.FakeSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeActionEvent
extends SignActionEvent {
    private final Cancellable event;
    private final Player player;
    private final boolean interactive;

    public SignChangeActionEvent(SignChangeEvent event, boolean interactive) {
        this((Cancellable)event, event.getPlayer(), new TrackedChangingSign(event), interactive);
    }

    public SignChangeActionEvent(SignChangeEvent event) {
        this((Cancellable)event, event.getPlayer(), new TrackedChangingSign(event), true);
    }

    public SignChangeActionEvent(Player player, RailLookup.TrackedSign sign, boolean interactive) {
        this(new MockCancellable(), player, sign, interactive);
    }

    public SignChangeActionEvent(Player player, RailLookup.TrackedSign sign) {
        this(new MockCancellable(), player, sign, true);
    }

    protected SignChangeActionEvent(SignChangeActionEvent event) {
        this(event.event, event.player, event.getTrackedSign(), event.interactive);
    }

    protected SignChangeActionEvent(Cancellable event, Player player, RailLookup.TrackedSign sign, boolean interactive) {
        super(sign);
        this.event = event;
        this.player = player;
        this.interactive = interactive;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isInteractive() {
        return this.interactive;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        super.setCancelled(cancelled);
        this.event.setCancelled(cancelled);
    }

    private static class TrackedChangingSign
    extends RailLookup.TrackedRealSign {
        private final SignChangeEvent event;
        private final boolean front;

        public TrackedChangingSign(final SignChangeEvent event) {
            super(FakeSign.create(event.getBlock()), event.getBlock(), RailPiece.NONE);
            this.front = BlockUtil.isChangingFrontLines((SignChangeEvent)event);
            ((FakeSign)this.sign).setHandler(new FakeSign.HandlerSignFallback(this, this.signBlock){
                final /* synthetic */ TrackedChangingSign this$0;
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

    private static class MockCancellable
    implements Cancellable {
        private boolean cancelled = false;

        private MockCancellable() {
        }

        public boolean isCancelled() {
            return this.cancelled;
        }

        public void setCancelled(boolean b) {
            this.cancelled = b;
        }
    }
}

