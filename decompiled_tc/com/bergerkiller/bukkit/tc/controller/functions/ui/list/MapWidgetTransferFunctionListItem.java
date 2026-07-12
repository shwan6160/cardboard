/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui.list;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionList;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionDialog;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.function.BooleanSupplier;
import org.bukkit.block.BlockFace;

public class MapWidgetTransferFunctionListItem
extends MapWidgetTransferFunctionItem {
    private static final int MODE_WIDTH = 9;
    private static final byte COLOR_MODE_TEXT_DEFAULT = MapColorPalette.getColor((int)64, (int)64, (int)64);
    private static final byte COLOR_MODE_TEXT_SELECTED = 34;
    private static final byte COLOR_MODE_BG_DEFAULT = MapColorPalette.getColor((int)180, (int)152, (int)138);
    private static final byte COLOR_MODE_BG_FOCUSED = MapColorPalette.getColor((int)209, (int)177, (int)161);
    private static final byte COLOR_MODE_BG_SELECTED = MapColorPalette.getColor((int)64, (int)64, (int)255);
    private TransferFunctionList.Item item;
    private final MapWidgetArrow modeUpArrow = new MapWidgetArrow(BlockFace.SOUTH);
    private final MapWidgetArrow modeDownArrow = new MapWidgetArrow(BlockFace.NORTH);

    public MapWidgetTransferFunctionListItem(TransferFunctionHost host, TransferFunctionList.Item item, BooleanSupplier isBooleanInput) {
        super(host, item, isBooleanInput);
        this.item = item;
    }

    public void onFunctionModeChanged(TransferFunctionList.Item oldItem, TransferFunctionList.Item newItem) {
    }

    public TransferFunctionList.Item getItem() {
        return this.item;
    }

    public void startMove() {
        MapWidgetTransferFunctionDialog dialog = this.getCurrentDialog();
        if (dialog != null) {
            dialog.setExitOnBack(false);
        }
        this.moving = true;
        this.invalidate();
    }

    private void updateFunctionMode(int incr) {
        TransferFunctionList.FunctionMode[] values = TransferFunctionList.FunctionMode.values();
        int newIndex = this.item.mode().ordinal() + incr;
        if (newIndex >= 0 && newIndex < values.length) {
            TransferFunctionList.Item oldItem = this.item;
            this.item = new TransferFunctionList.Item(values[newIndex], (TransferFunction)this.item.getFunction());
            this.onFunctionModeChanged(oldItem, this.item);
            this.invalidate();
        }
    }

    private boolean functionModeValid(int incr) {
        TransferFunctionList.FunctionMode[] values = TransferFunctionList.FunctionMode.values();
        int newIndex = this.item.mode().ordinal() + incr;
        return newIndex >= 0 && newIndex < values.length;
    }

    @Override
    public MapWidgetTransferFunctionItem addButton(MapWidgetTransferFunctionItem.ButtonIcon icon, Runnable action) {
        super.addButton(icon, action);
        return this;
    }

    @Override
    protected void setSelectedButton(int index) {
        if (index < -1) {
            index = -1;
        } else if (index >= this.buttons.size()) {
            index = this.buttons.size() - 1;
        }
        if (this.selButtonIdx == index) {
            return;
        }
        if (index == -1) {
            this.addWidget(this.modeUpArrow.setEnabled(this.functionModeValid(-1)).setPosition(0, -this.modeUpArrow.getHeight() + 1));
            this.addWidget(this.modeDownArrow.setEnabled(this.functionModeValid(1)).setPosition(0, this.getHeight() - 1));
        } else if (this.selButtonIdx == -1) {
            this.removeWidget(this.modeUpArrow);
            this.removeWidget(this.modeDownArrow);
        }
        this.selButtonIdx = index;
        this.invalidate();
    }

    @Override
    public void onDraw() {
        this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
        this.view.fillRectangle(1, 1, 9, this.getHeight() - 2, this.moving ? COLOR_BG_MOVING : (this.isFocused() ? (this.selButtonIdx == -1 ? COLOR_MODE_BG_SELECTED : COLOR_MODE_BG_FOCUSED) : COLOR_MODE_BG_DEFAULT));
        this.view.drawLine(10, 1, 10, this.getHeight() - 2, (byte)119);
        this.view.fillRectangle(11, 1, this.getWidth() - 3 - 9, this.getHeight() - 2, this.moving ? COLOR_BG_MOVING : (this.isFocused() ? COLOR_BG_FOCUSED : COLOR_BG_DEFAULT));
        byte by = this.isFocused() && this.selButtonIdx == -1 ? 34 : COLOR_MODE_TEXT_DEFAULT;
        int baseY = (this.getHeight() - 1) / 2;
        this.view.drawLine(6, baseY - 1, 8, baseY - 1, by);
        this.view.drawLine(6, baseY + 1, 8, baseY + 1, by);
        if (this.item.mode() != TransferFunctionList.FunctionMode.ASSIGN) {
            switch (this.item.mode()) {
                case ADD: {
                    MapCanvas iconView = this.view.getView(2, baseY - 3, 4, 6);
                    iconView.drawLine(0, 3, 2, 3, by);
                    iconView.drawPixel(1, 2, by);
                    iconView.drawPixel(1, 4, by);
                    break;
                }
                case SUBTRACT: {
                    MapCanvas iconView = this.view.getView(2, baseY - 3, 4, 6);
                    iconView.drawLine(0, 3, 2, 3, by);
                    break;
                }
                case MULTIPLY: {
                    MapCanvas iconView = this.view.getView(2, baseY - 3, 4, 6);
                    iconView.drawLine(0, 1, 0, 2, by);
                    iconView.drawLine(2, 1, 2, 2, by);
                    iconView.drawPixel(1, 3, by);
                    iconView.drawLine(0, 4, 0, 5, by);
                    iconView.drawLine(2, 4, 2, 5, by);
                    break;
                }
                case DIVIDE: {
                    MapCanvas iconView = this.view.getView(2, baseY - 3, 4, 6);
                    iconView.drawPixel(3, 0, by);
                    iconView.drawLine(2, 1, 2, 2, by);
                    iconView.drawLine(1, 3, 1, 4, by);
                    iconView.drawPixel(0, 5, by);
                    break;
                }
                case OR: {
                    MapCanvas iconView = this.view.getView(2, baseY - 3, 4, 7);
                    iconView.drawLine(1, 0, 1, 6, by);
                    break;
                }
                case AND: {
                    MapCanvas iconView = this.view.getView(1, baseY - 3, 5, 7);
                    iconView.drawLine(1, 0, 2, 0, by);
                    iconView.drawLine(0, 1, 0, 2, by);
                    iconView.drawLine(0, 4, 0, 5, by);
                    iconView.drawLine(1, 6, 2, 6, by);
                    iconView.drawPixel(3, 1, by);
                    iconView.drawPixel(2, 2, by);
                    iconView.drawPixel(1, 3, by);
                    iconView.drawPixel(2, 4, by);
                    iconView.drawPixel(3, 5, by);
                    iconView.drawPixel(4, 4, by);
                    iconView.drawPixel(4, 6, by);
                    break;
                }
            }
        }
        if (!this.isActivated()) {
            MapCanvas previewView = this.view.getView(12, 1, this.getWidth() - 2, this.getHeight() - 2);
            this.getFunction().drawPreview(this, previewView);
            this.drawUI();
        }
    }

    @Override
    protected void updateInlineDialogBounds(MapWidgetTransferFunctionItem.InlineDialog dialog) {
        dialog.setBounds(11, 1, this.getWidth() - 9 - 4, this.getHeight() - 2);
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        if (!this.moving && event.getKey() == MapPlayerInput.Key.ENTER && this.selButtonIdx == -1 && this.isFocused()) {
            this.setSelectedButton(0);
        } else if (!this.moving && this.selButtonIdx == -1) {
            if (event.getKey() == MapPlayerInput.Key.ENTER || event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.setSelectedButton(0);
            } else if (event.getKey() == MapPlayerInput.Key.UP) {
                this.updateFunctionMode(-1);
                this.modeUpArrow.setEnabled(this.functionModeValid(-1));
                this.modeDownArrow.setEnabled(this.functionModeValid(1));
                this.modeUpArrow.sendFocus();
                this.modeDownArrow.stopFocus();
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                this.updateFunctionMode(1);
                this.modeUpArrow.setEnabled(this.functionModeValid(-1));
                this.modeDownArrow.setEnabled(this.functionModeValid(1));
                this.modeDownArrow.sendFocus();
                this.modeUpArrow.stopFocus();
            }
        } else {
            super.onKeyPressed(event);
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (!this.moving && this.selButtonIdx == -1) {
            if (event.getKey() == MapPlayerInput.Key.UP) {
                this.modeUpArrow.stopFocus();
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                this.modeDownArrow.stopFocus();
            }
        }
        super.onKeyReleased(event);
    }
}

