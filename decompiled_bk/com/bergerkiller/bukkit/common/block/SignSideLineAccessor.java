/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import org.bukkit.event.block.SignChangeEvent;

public interface SignSideLineAccessor {
    public SignSide getSide();

    public String[] getLines();

    public String getLine(int var1);

    public void setLine(int var1, String var2);

    public ChatText[] getFormattedLines();

    public ChatText getFormattedLine(int var1);

    public void setFormattedLine(int var1, ChatText var2);

    public static SignSideLineAccessor ofChangeEvent(final SignChangeEvent event) {
        final SignSide side = SignSide.byFront(BlockUtil.isChangingFrontLines(event));
        return new SignSideLineAccessor(){

            @Override
            public SignSide getSide() {
                return side;
            }

            @Override
            public String[] getLines() {
                return event.getLines();
            }

            @Override
            public String getLine(int index) {
                return event.getLine(index);
            }

            @Override
            public void setLine(int index, String text) {
                event.setLine(index, text);
            }

            @Override
            public ChatText[] getFormattedLines() {
                return LogicUtil.mapArray(event.getLines(), ChatText.class, ChatText::fromMessage);
            }

            @Override
            public ChatText getFormattedLine(int index) {
                return ChatText.fromMessage(event.getLine(index));
            }

            @Override
            public void setFormattedLine(int index, ChatText text) {
                event.setLine(index, text.getMessage());
            }
        };
    }
}

