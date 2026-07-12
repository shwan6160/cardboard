/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.attachments.control.sequencer;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerType;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetScroller;

public abstract class MapWidgetSequencerTypeSelector
extends MapWidgetMenu {
    private static final byte ITEM_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
    private static final byte ITEM_BG_FOCUS = MapColorPalette.getColor((int)255, (int)252, (int)245);
    private static final int ROW_HEIGHT = 11;

    public MapWidgetSequencerTypeSelector() {
        this.setPositionAbsolute(true);
        this.setBounds(10, 20, 108, 98);
        this.setBackgroundColor(MapColorPalette.getColor((int)72, (int)108, (int)152));
        this.labelColor = (byte)119;
    }

    public abstract void onSelected(SequencerType var1);

    @Override
    public void onAttached() {
        this.addLabel(5, 5, "Set Sequencer to add");
        MapWidgetScroller scroller = (MapWidgetScroller)this.addWidget(new MapWidgetScroller());
        scroller.setScrollPadding(10).setBounds(5, 12, this.getWidth() - 10, this.getHeight() - 17);
        int y = 0;
        for (SequencerType type : SequencerType.all()) {
            Item item = new Item(type);
            item.setBounds(0, y, scroller.getWidth(), 11);
            scroller.addContainerWidget(item);
            y += 10;
        }
        super.onAttached();
    }

    private class Item
    extends MapWidget {
        private final SequencerType type;

        public Item(SequencerType type) {
            this.type = type;
            this.setFocusable(true);
        }

        public void onActivate() {
            this.display.playSound(SoundEffect.CLICK);
            MapWidgetSequencerTypeSelector.this.close();
            MapWidgetSequencerTypeSelector.this.onSelected(this.type);
        }

        public void onDraw() {
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
            this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isFocused() ? ITEM_BG_FOCUS : ITEM_BG_DEFAULT);
            this.view.draw(MapFont.MINECRAFT, 2, 2, this.isFocused() ? (byte)50 : 119, (CharSequence)this.type.name());
        }
    }
}

