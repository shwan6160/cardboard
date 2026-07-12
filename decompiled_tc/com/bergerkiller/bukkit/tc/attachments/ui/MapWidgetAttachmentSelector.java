/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetScroller;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class MapWidgetAttachmentSelector<T>
extends MapWidgetMenu {
    private static final byte ITEM_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
    private static final byte ITEM_BG_FOCUS = MapColorPalette.getColor((int)255, (int)252, (int)245);
    private static final int ROW_HEIGHT = 11;
    private final boolean allowNone;
    private AttachmentSelector<T> allSelector;
    private MapWidgetScroller scroller;
    private String title = "Set Attachment Name";
    private String anyItemText = null;

    public MapWidgetAttachmentSelector(AttachmentSelector<T> selector) {
        this(selector, false);
    }

    public MapWidgetAttachmentSelector(AttachmentSelector<T> selector, boolean allowNone) {
        this.setPositionAbsolute(true);
        this.setBounds(10, 20, 108, 98);
        this.setBackgroundColor(MapColorPalette.getColor((int)72, (int)108, (int)152));
        this.labelColor = (byte)119;
        this.allowNone = allowNone;
        this.allSelector = selector.withSelectAll();
        if (!allowNone && this.allSelector.strategy() == AttachmentSelector.SearchStrategy.NONE) {
            this.allSelector = this.allSelector.withStrategy(AttachmentSelector.SearchStrategy.CHILDREN);
        }
    }

    public abstract List<String> getAttachmentNames(AttachmentSelector<T> var1);

    public abstract void onSelected(AttachmentSelector<T> var1);

    public MapWidgetAttachmentSelector<T> includeAny(String text) {
        this.anyItemText = text;
        return this;
    }

    public MapWidgetAttachmentSelector<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public void onAttached() {
        this.addLabel(5, 5, this.title);
        this.scroller = (MapWidgetScroller)this.addWidget(new MapWidgetScroller());
        this.scroller.setScrollPadding(10).setBounds(5, 12, this.getWidth() - 10, this.getHeight() - 17);
        this.loadItems();
        ((SearchStrategyWidget)this.addWidget(new SearchStrategyWidget())).setPosition(this.getWidth() - 16, 4);
        super.onAttached();
    }

    private void loadItems() {
        this.scroller.getContainer().clearWidgets();
        if (this.allSelector.strategy() == AttachmentSelector.SearchStrategy.NONE) {
            return;
        }
        List items = this.getAttachmentNames(this.allSelector).stream().sorted().distinct().map(x$0 -> new NameItem((String)x$0)).collect(Collectors.toCollection(ArrayList::new));
        items.add(new SelectNameItem());
        if (this.anyItemText != null) {
            items.add(new AnyNameItem());
        }
        int y = 0;
        for (MapWidget item : items) {
            item.setBounds(0, y, this.scroller.getWidth(), 11);
            this.scroller.addContainerWidget(item);
            y += 10;
        }
    }

    private AttachmentSelector.SearchStrategy getNextStrategy(AttachmentSelector.SearchStrategy current) {
        AttachmentSelector.SearchStrategy[] strategies = AttachmentSelector.SearchStrategy.values();
        int index = current.ordinal();
        index = (index + 1) % strategies.length;
        AttachmentSelector.SearchStrategy next = strategies[index];
        if (!this.allowNone && next == AttachmentSelector.SearchStrategy.NONE) {
            index = (index + 1) % strategies.length;
            next = strategies[index];
        }
        return next;
    }

    private class SearchStrategyWidget
    extends MapWidget {
        private final MapWidgetTooltip tooltip = new MapWidgetTooltip();

        public SearchStrategyWidget() {
            this.setFocusable(true);
            this.setSize(11, 7);
            this.tooltip.setText(MapWidgetAttachmentSelector.this.allSelector.strategy().getCaption());
        }

        public void onAttached() {
            super.onAttached();
            if (MapWidgetAttachmentSelector.this.allSelector.strategy() == AttachmentSelector.SearchStrategy.NONE) {
                this.focus();
            }
        }

        public void onActivate() {
            MapWidgetAttachmentSelector.this.allSelector = MapWidgetAttachmentSelector.this.allSelector.withStrategy(MapWidgetAttachmentSelector.this.getNextStrategy(MapWidgetAttachmentSelector.this.allSelector.strategy()));
            this.tooltip.setText(MapWidgetAttachmentSelector.this.allSelector.strategy().getCaption());
            this.invalidate();
            MapWidgetAttachmentSelector.this.loadItems();
            this.display.playSound(SoundEffect.CLICK_WOOD);
        }

        public void onFocus() {
            this.addWidget(this.tooltip);
        }

        public void onBlur() {
            this.removeWidget(this.tooltip);
        }

        public void onDraw() {
            this.view.draw((MapCanvas)MapWidgetAttachmentSelector.this.allSelector.strategy().getIcon(this.isFocused()), 0, 0);
        }
    }

    private class SelectNameItem
    extends MapWidget {
        public SelectNameItem() {
            this.setFocusable(true);
        }

        public void onActivate() {
            MapWidget parent = MapWidgetAttachmentSelector.this.getParent();
            MapWidgetAttachmentSelector.this.close();
            parent.addWidget((MapWidget)new MapWidgetSubmitText(){

                public void onAttached() {
                    this.setDescription("Set Name");
                    this.activate();
                }

                public void onAccept(String text) {
                    if (this.parent.getDisplay() != null) {
                        this.parent.getDisplay().playSound(SoundEffect.CLICK);
                    }
                    MapWidgetAttachmentSelector.this.onSelected(MapWidgetAttachmentSelector.this.allSelector.withName(text.trim()));
                }
            });
        }

        public void onDraw() {
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
            this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isFocused() ? ITEM_BG_FOCUS : ITEM_BG_DEFAULT);
            this.view.draw(MapFont.MINECRAFT, 2, 2, this.isFocused() ? (byte)50 : 119, (CharSequence)"<Set Name>");
        }
    }

    private class AnyNameItem
    extends MapWidget {
        public AnyNameItem() {
            this.setFocusable(true);
        }

        public void onActivate() {
            this.display.playSound(SoundEffect.CLICK);
            MapWidgetAttachmentSelector.this.close();
            MapWidgetAttachmentSelector.this.onSelected(MapWidgetAttachmentSelector.this.allSelector);
        }

        public void onDraw() {
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
            this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isFocused() ? ITEM_BG_FOCUS : ITEM_BG_DEFAULT);
            this.view.draw(MapFont.MINECRAFT, 2, 2, this.isFocused() ? (byte)50 : 119, (CharSequence)MapWidgetAttachmentSelector.this.anyItemText);
        }
    }

    private class NameItem
    extends MapWidget {
        private final String name;

        public NameItem(String name) {
            this.name = name;
            this.setFocusable(true);
        }

        public void onActivate() {
            this.display.playSound(SoundEffect.CLICK);
            MapWidgetAttachmentSelector.this.close();
            MapWidgetAttachmentSelector.this.onSelected(MapWidgetAttachmentSelector.this.allSelector.withName(this.name));
        }

        public void onDraw() {
            this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
            this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isFocused() ? ITEM_BG_FOCUS : ITEM_BG_DEFAULT);
            this.view.draw(MapFont.MINECRAFT, 2, 2, this.isFocused() ? (byte)50 : 119, (CharSequence)this.name);
        }
    }
}

