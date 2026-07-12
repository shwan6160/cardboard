/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui.list;

import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetScroller;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionList;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionDialog;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import com.bergerkiller.bukkit.tc.controller.functions.ui.list.MapWidgetTransferFunctionListItem;

public class MapWidgetTransferFunctionList
extends MapWidgetScroller {
    private final MapWidgetTransferFunctionDialog dialog;
    private final TransferFunctionList list;
    private int onOpenSelectedIndex = -1;

    public MapWidgetTransferFunctionList(MapWidgetTransferFunctionDialog dialog, TransferFunctionList list) {
        this.dialog = dialog;
        this.list = list;
        this.setBounds(5, 9, dialog.getWidth() - 10, dialog.getHeight() - 20);
        this.setScrollPadding(10);
    }

    public void onSelectedItemChanged() {
    }

    public int getSelectedItemIndex() {
        MapWidget w = this.display.getFocusedWidget();
        if (w instanceof MapWidgetTransferFunctionListItem) {
            return this.list.indexOf(((MapWidgetTransferFunctionListItem)w).getItem());
        }
        return -1;
    }

    public MapWidgetTransferFunctionList setSelectedItemIndex(int index) {
        this.onOpenSelectedIndex = index;
        return this;
    }

    @Override
    public void onAttached() {
        int index = 0;
        for (TransferFunctionList.Item listItem : this.list.getItems()) {
            MapWidgetTransferFunctionListItem item = this.createItem(listItem);
            this.calcBounds(item, index++);
            this.addContainerWidget(item);
        }
        this.addInitialItemPlaceholder();
        if (this.onOpenSelectedIndex != -1 && this.onOpenSelectedIndex < this.getContainer().getWidgetCount()) {
            this.getContainer().getWidget(this.onOpenSelectedIndex).focus();
        }
        super.onAttached();
    }

    private MapWidgetTransferFunctionListItem createItem(TransferFunctionList.Item listItem) {
        MapWidgetTransferFunctionListItem item = new MapWidgetTransferFunctionListItem(this.dialog.getHost(), listItem, () -> {
            int index = this.list.indexOf(listItem);
            if (index == -1) return false;
            if (!this.list.isBooleanOutput(index - 1, this.dialog::isBooleanInput)) return false;
            return true;
        }){

            @Override
            public void onMoveUp() {
                int currIndex = MapWidgetTransferFunctionList.this.list.indexOf(this.getItem());
                if (currIndex != -1 && currIndex > 0) {
                    MapWidgetTransferFunctionList.this.list.remove(currIndex);
                    MapWidgetTransferFunctionList.this.list.add(currIndex - 1, this.getItem());
                    MapWidgetTransferFunctionList.this.recalcBounds();
                    MapWidgetTransferFunctionList.this.dialog.markChanged();
                }
            }

            @Override
            public void onMoveDown() {
                int currIndex = MapWidgetTransferFunctionList.this.list.indexOf(this.getItem());
                if (currIndex != -1 && currIndex < MapWidgetTransferFunctionList.this.list.size() - 1) {
                    MapWidgetTransferFunctionList.this.list.remove(currIndex);
                    MapWidgetTransferFunctionList.this.list.add(currIndex + 1, this.getItem());
                    MapWidgetTransferFunctionList.this.recalcBounds();
                    MapWidgetTransferFunctionList.this.dialog.markChanged();
                }
            }

            @Override
            public void onFunctionModeChanged(TransferFunctionList.Item oldItem, TransferFunctionList.Item newItem) {
                int index = MapWidgetTransferFunctionList.this.list.indexOf(oldItem);
                if (index != -1) {
                    MapWidgetTransferFunctionList.this.list.set(index, newItem);
                    MapWidgetTransferFunctionList.this.dialog.markChanged();
                }
            }

            @Override
            public void onFocus() {
                super.onFocus();
                MapWidgetTransferFunctionList.this.onSelectedItemChanged();
            }
        };
        item.addButton(MapWidgetTransferFunctionItem.ButtonIcon.CONFIGURE, item::configure).addButton(MapWidgetTransferFunctionItem.ButtonIcon.MOVE, item::startMove).addButton(MapWidgetTransferFunctionItem.ButtonIcon.ADD, () -> this.addNewItem(this.list.indexOf(item.getItem()))).addButton(MapWidgetTransferFunctionItem.ButtonIcon.REMOVE, () -> {
            int itemIndex = this.list.indexOf(item.getItem());
            if (itemIndex != -1) {
                this.list.remove(itemIndex);
                this.getContainer().removeWidget((MapWidget)item);
                this.addInitialItemPlaceholder();
                this.recalcBounds();
                this.dialog.markChanged();
                if (itemIndex >= this.list.size()) {
                    itemIndex = this.list.size() - 1;
                }
                boolean found = false;
                if (itemIndex >= 0) {
                    TransferFunctionList.Item newSelListItem = this.list.get(itemIndex);
                    for (MapWidget w : this.getContainer().getWidgets()) {
                        if (((MapWidgetTransferFunctionListItem)w).getItem() != newSelListItem) continue;
                        w.focus();
                        found = true;
                        break;
                    }
                }
                if (!found && this.getContainer().getWidgetCount() > 0) {
                    this.getContainer().getWidget(0).focus();
                }
            }
        });
        return item;
    }

    private void addNewItem(int index) {
        this.dialog.createNew(newFunction -> {
            int newItemIndex = index;
            if (newItemIndex == -1) {
                newItemIndex = this.list.size();
            } else if (newItemIndex < this.list.size()) {
                ++newItemIndex;
            }
            if (this.list.isEmpty()) {
                this.getContainer().clearWidgets();
            }
            TransferFunctionList.Item newListItem = new TransferFunctionList.Item(TransferFunctionList.FunctionMode.ASSIGN, (TransferFunction)newFunction);
            this.list.add(newItemIndex, newListItem);
            MapWidgetTransferFunctionListItem newItem = this.addContainerWidget(this.createItem(newListItem));
            this.recalcBounds();
            newItem.focus();
            this.dialog.markChanged();
        });
    }

    private void addInitialItemPlaceholder() {
        if (this.list.isEmpty()) {
            this.getContainer().clearWidgets();
            this.addContainerWidget(new MapWidgetButton(){

                public void onActivate() {
                    MapWidgetTransferFunctionList.this.addNewItem(0);
                }
            }.setText("Set Function").setBounds(0, 0, this.getWidth(), 13)).focus();
        }
    }

    private void recalcBounds() {
        if (!this.list.isEmpty()) {
            for (MapWidget w : this.getContainer().getWidgets()) {
                MapWidgetTransferFunctionListItem item = (MapWidgetTransferFunctionListItem)w;
                this.calcBounds(w, this.list.indexOf(item.getItem()));
            }
        }
        super.recalculateContainerSize();
    }

    private void calcBounds(MapWidget widget, int index) {
        if (index == -1) {
            throw new IllegalArgumentException("Index is -1");
        }
        widget.setBounds(0, 14 * index, this.getWidth(), 15);
    }
}

