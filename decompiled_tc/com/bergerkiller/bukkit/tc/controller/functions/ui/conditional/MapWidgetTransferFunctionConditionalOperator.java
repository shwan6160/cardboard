/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui.conditional;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConditional;
import org.bukkit.block.BlockFace;

public abstract class MapWidgetTransferFunctionConditionalOperator
extends MapWidget {
    private static final byte COLOR_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
    private static final byte COLOR_BG_FOCUSED = MapColorPalette.getColor((int)255, (int)252, (int)245);
    private static final byte COLOR_BG_ACTIVATED = MapColorPalette.getColor((int)247, (int)233, (int)163);
    private final MapWidgetArrow opUpArrow = new MapWidgetArrow(BlockFace.SOUTH);
    private final MapWidgetArrow opDownArrow = new MapWidgetArrow(BlockFace.NORTH);
    private final Connector aboveConnector = new Connector();
    private final Connector belowConnector = new Connector();
    private TransferFunctionConditional.Operator operator;

    public MapWidgetTransferFunctionConditionalOperator(TransferFunctionConditional.Operator operator) {
        this.operator = operator;
        this.setFocusable(true);
        this.setRetainChildWidgets(true);
        this.addWidget(this.aboveConnector);
        this.addWidget(this.belowConnector);
        this.belowConnector.setVisible(operator != TransferFunctionConditional.Operator.BOOL);
    }

    public abstract void onOperatorChanged(TransferFunctionConditional.Operator var1);

    public void setOperator(TransferFunctionConditional.Operator operator) {
        if (this.operator != operator) {
            this.operator = operator;
            this.belowConnector.setVisible(operator != TransferFunctionConditional.Operator.BOOL);
            this.onOperatorChanged(operator);
            this.invalidate();
        }
    }

    private void updateOperator(int incr) {
        TransferFunctionConditional.Operator[] values = TransferFunctionConditional.Operator.values();
        int newIndex = this.operator.ordinal() + incr;
        if (newIndex >= 0 && newIndex < values.length) {
            this.setOperator(values[newIndex]);
        }
    }

    private boolean opValid(int incr) {
        TransferFunctionConditional.Operator[] values = TransferFunctionConditional.Operator.values();
        int newIndex = this.operator.ordinal() + incr;
        return newIndex >= 0 && newIndex < values.length;
    }

    public void onBoundsChanged() {
        this.aboveConnector.setPosition((this.getWidth() - this.aboveConnector.getWidth()) / 2, -this.aboveConnector.getHeight());
        this.belowConnector.setPosition((this.getWidth() - this.belowConnector.getWidth()) / 2, this.getHeight());
    }

    public void onActivate() {
        this.addWidget(this.opUpArrow.setEnabled(this.opValid(-1)).setPosition((this.getWidth() - this.opUpArrow.getWidth()) / 2, -this.opUpArrow.getHeight() - 1));
        this.addWidget(this.opDownArrow.setEnabled(this.opValid(1)).setPosition((this.getWidth() - this.opDownArrow.getWidth()) / 2, this.getHeight() + 1));
        super.onActivate();
    }

    public void onDeactivate() {
        this.removeWidget(this.opUpArrow);
        this.removeWidget(this.opDownArrow);
        super.onDeactivate();
    }

    public void onDraw() {
        this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
        this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isActivated() ? COLOR_BG_ACTIVATED : (this.isFocused() ? COLOR_BG_FOCUSED : COLOR_BG_DEFAULT));
        byte color = this.isFocused() ? (byte)50 : 119;
        int textWidth = (int)this.view.calcFontSize(MapFont.MINECRAFT, (CharSequence)this.operator.title()).getWidth();
        this.view.draw(MapFont.MINECRAFT, (this.getWidth() - textWidth + 1) / 2, 3, color, (CharSequence)this.operator.title());
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (!this.isActivated()) {
            super.onKeyPressed(event);
            return;
        }
        if (event.getKey() == MapPlayerInput.Key.UP) {
            this.updateOperator(-1);
            this.opUpArrow.setEnabled(this.opValid(-1));
            this.opDownArrow.setEnabled(this.opValid(1));
            this.opUpArrow.sendFocus();
            this.opDownArrow.stopFocus();
        } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
            this.updateOperator(1);
            this.opUpArrow.setEnabled(this.opValid(-1));
            this.opDownArrow.setEnabled(this.opValid(1));
            this.opDownArrow.sendFocus();
            this.opUpArrow.stopFocus();
        } else {
            this.focus();
            if (event.getKey() == MapPlayerInput.Key.LEFT || event.getKey() == MapPlayerInput.Key.RIGHT) {
                super.onKeyPressed(event);
            }
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (this.isActivated()) {
            if (event.getKey() == MapPlayerInput.Key.UP) {
                this.opUpArrow.stopFocus();
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                this.opDownArrow.stopFocus();
            }
        }
        super.onKeyReleased(event);
    }

    private static class Connector
    extends MapWidget {
        public Connector() {
            this.setSize(3, 2);
            this.setDepthOffset(-1);
        }

        public void onDraw() {
            this.view.drawLine(0, 0, 0, this.getHeight() - 1, (byte)119);
            this.view.drawLine(2, 0, 2, this.getHeight() - 1, (byte)119);
        }
    }
}

