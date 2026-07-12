/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.block.SignSide;
import com.bergerkiller.bukkit.common.block.SignSideLineAccessor;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

public interface SignLineAccessor {
    public String getLine(SignSide var1, int var2);

    public void setLine(SignSide var1, int var2, String var3);

    public String[] getLines(SignSide var1);

    public String getFrontLine(int var1);

    public void setFrontLine(int var1, String var2);

    public String[] getFrontLines();

    public ChatText getFormattedFrontLine(int var1);

    public void setFormattedFrontLine(int var1, ChatText var2);

    public ChatText[] getFormattedFrontLines();

    public String getBackLine(int var1);

    public void setBackLine(int var1, String var2);

    public String[] getBackLines();

    public ChatText getFormattedBackLine(int var1);

    public void setFormattedBackLine(int var1, ChatText var2);

    public ChatText[] getFormattedBackLines();

    default public SignSideLineAccessor accessLinesOfSide(final SignSide side) {
        return new SignSideLineAccessor(){
            final /* synthetic */ SignLineAccessor this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public SignSide getSide() {
                return side;
            }

            @Override
            public String[] getLines() {
                return this.this$0.getLines(side);
            }

            @Override
            public String getLine(int index) {
                return this.this$0.getLine(side, index);
            }

            @Override
            public void setLine(int index, String text) {
                this.this$0.setLine(side, index, text);
            }

            @Override
            public ChatText[] getFormattedLines() {
                return side.isFront() ? this.this$0.getFormattedFrontLines() : this.this$0.getFormattedBackLines();
            }

            @Override
            public ChatText getFormattedLine(int index) {
                return side.isFront() ? this.this$0.getFormattedFrontLine(index) : this.this$0.getFormattedBackLine(index);
            }

            @Override
            public void setFormattedLine(int index, ChatText text) {
                if (side.isFront()) {
                    this.this$0.setFormattedFrontLine(index, text);
                } else {
                    this.this$0.setFormattedBackLine(index, text);
                }
            }
        };
    }

    public static SignLineAccessor compose(final SignSideLineAccessor frontLines, final SignSideLineAccessor backLines) {
        return new SignLineAccessor(){

            @Override
            public String getLine(SignSide side, int index) {
                return side.selectFront(frontLines, backLines).getLine(index);
            }

            @Override
            public void setLine(SignSide side, int index, String text) {
                side.selectFront(frontLines, backLines).setLine(index, text);
            }

            @Override
            public String[] getLines(SignSide side) {
                return side.selectFront(frontLines, backLines).getLines();
            }

            @Override
            public String getFrontLine(int index) {
                return frontLines.getLine(index);
            }

            @Override
            public void setFrontLine(int index, String text) {
                frontLines.setLine(index, text);
            }

            @Override
            public String[] getFrontLines() {
                return frontLines.getLines();
            }

            @Override
            public ChatText getFormattedFrontLine(int index) {
                return frontLines.getFormattedLine(index);
            }

            @Override
            public void setFormattedFrontLine(int index, ChatText text) {
                frontLines.setFormattedLine(index, text);
            }

            @Override
            public ChatText[] getFormattedFrontLines() {
                return frontLines.getFormattedLines();
            }

            @Override
            public String getBackLine(int index) {
                return backLines.getLine(index);
            }

            @Override
            public void setBackLine(int index, String text) {
                backLines.setLine(index, text);
            }

            @Override
            public String[] getBackLines() {
                return backLines.getLines();
            }

            @Override
            public ChatText getFormattedBackLine(int index) {
                return backLines.getFormattedLine(index);
            }

            @Override
            public void setFormattedBackLine(int index, ChatText text) {
                backLines.setFormattedLine(index, text);
            }

            @Override
            public ChatText[] getFormattedBackLines() {
                return backLines.getFormattedLines();
            }
        };
    }
}

