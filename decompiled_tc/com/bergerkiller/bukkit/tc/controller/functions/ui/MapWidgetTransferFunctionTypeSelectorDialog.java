/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetScroller;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;

public abstract class MapWidgetTransferFunctionTypeSelectorDialog
extends MapWidgetMenu {
    private static final byte ITEM_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
    private static final byte ITEM_BG_FOCUS = MapColorPalette.getColor((int)255, (int)252, (int)245);
    private static final int ROW_HEIGHT = 11;
    private final TransferFunctionHost host;

    public MapWidgetTransferFunctionTypeSelectorDialog(TransferFunctionHost host) {
        this.host = host;
        this.setBounds(20, 25, 88, 88);
        this.setPositionAbsolute(true);
        this.setBackgroundColor(MapColorPalette.getColor((int)164, (int)168, (int)184));
    }

    public abstract void onSelected(TransferFunction var1);

    @Override
    public void onAttached() {
        (this.addWidget(new MapWidgetScroller(){

            @Override
            public void onAttached() {
                int y = 0;
                boolean addedInput = false;
                for (TransferFunction.Serializer<?> serializer : MapWidgetTransferFunctionTypeSelectorDialog.this.host.getRegistry().all()) {
                    if (!serializer.isListed(MapWidgetTransferFunctionTypeSelectorDialog.this.host)) continue;
                    if (serializer.isInput()) {
                        if (addedInput) continue;
                        addedInput = true;
                    }
                    this.addContainerWidget(new Item(serializer).setBounds(0, y, this.getWidth(), 12));
                    y += 11;
                }
                super.onAttached();
            }
        })).setScrollPadding(5).setBounds(4, 4, this.getWidth() - 8, this.getHeight() - 8);
        super.onAttached();
    }

    private class Item
    extends MapWidget {
        private final TransferFunction.Serializer<?> serializer;

        public Item(TransferFunction.Serializer<?> serializer) {
            this.serializer = serializer;
            this.setFocusable(true);
        }

        public void onActivate() {
            MapWidgetTransferFunctionTypeSelectorDialog.this.close();
            MapWidgetTransferFunctionTypeSelectorDialog.this.onSelected((TransferFunction)this.serializer.createNew(MapWidgetTransferFunctionTypeSelectorDialog.this.host));
        }

        public void onDraw() {
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
            this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isFocused() ? ITEM_BG_FOCUS : ITEM_BG_DEFAULT);
            this.view.draw(MapFont.MINECRAFT, 2, 2, this.isFocused() ? (byte)50 : 119, (CharSequence)(this.serializer.isInput() ? "Input" : this.serializer.title()));
        }
    }
}

