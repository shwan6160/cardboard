/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapFont$Alignment
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class MapWidgetNameSet
extends MapWidget {
    private static final int ROW_HEIGHT = 11;
    private static final int HSCROLL_DELAY_TICKS = 60;
    private static final int HSCROLL_HOLD_TICKS = 30;
    private static final int HSCROLL_PIXEL_STEPS = 2;
    private final List<ListItem> items = new ArrayList<ListItem>();
    private final Set<String> uniqueItemNames = new LinkedHashSet<String>();
    private int scrollOffset = 0;
    private int selectedIndex = 0;
    private int horScrollTicks = 0;
    private int numTicksOfNoScroll = 0;
    private String newNameDialogTitle = "Add a new item";
    private String newNameText = "+++ NEW +++";
    MapWidgetSubmitText newItemDialog;

    public MapWidgetNameSet() {
        this.setFocusable(true);
    }

    public abstract void onItemAdded(String var1);

    public abstract void onItemRemoved(String var1);

    public MapWidgetNameSet addItem(String item) {
        if (this.uniqueItemNames.add(item)) {
            this.items.add(new ListItem(item));
            this.invalidate();
        }
        return this;
    }

    public MapWidgetNameSet setItems(Collection<String> items) {
        this.items.clear();
        this.uniqueItemNames.clear();
        for (String item : items) {
            this.addItem(item);
        }
        return this;
    }

    public Set<String> getItems() {
        return Collections.unmodifiableSet(this.uniqueItemNames);
    }

    public MapWidgetNameSet setNewItemDescription(String title) {
        this.newNameDialogTitle = title;
        if (this.newItemDialog != null) {
            this.newItemDialog.setDescription(title);
        }
        return this;
    }

    public MapWidgetNameSet setNewItemText(String text) {
        this.newNameText = text;
        this.invalidate();
        return this;
    }

    public void onAttached() {
        this.newItemDialog = (this.addWidget((MapWidget)new MapWidgetSubmitText(){

            public void onAccept(String text) {
                if ((text = text.trim()).isEmpty()) {
                    this.onCancel();
                    return;
                }
                ListItem newItem = new ListItem(text);
                if (MapWidgetNameSet.this.items.contains(newItem)) {
                    MapWidgetNameSet.this.addWidget((MapWidget)new ItemAlreadyAddedDialog());
                } else {
                    MapWidgetNameSet.this.uniqueItemNames.add(newItem.name);
                    MapWidgetNameSet.this.items.add(newItem);
                    MapWidgetNameSet.this.selectedIndex = MapWidgetNameSet.this.items.size();
                    MapWidgetNameSet.this.scrollToSelection();
                    MapWidgetNameSet.this.invalidate();
                    MapWidgetNameSet.this.onItemAdded(newItem.name);
                }
            }
        })).setDescription(this.newNameDialogTitle);
    }

    public void onDraw() {
        int numVisibleItems = this.calcNumItems();
        byte gridColor = this.isFocused() ? (byte)122 : 119;
        this.view.drawRectangle(0, 0, this.getWidth(), numVisibleItems * 11, gridColor);
        for (int i = 0; i < numVisibleItems; ++i) {
            boolean isSelected;
            int index = this.scrollOffset + i;
            boolean isNewIcon = index >= this.items.size();
            boolean bl = isSelected = index == this.selectedIndex && this.isActivated();
            byte bgColor = isNewIcon ? (isSelected ? MapColorPalette.getColor((int)0, (int)160, (int)0) : MapColorPalette.getColor((int)0, (int)64, (int)0)) : (isSelected ? MapColorPalette.getColor((int)128, (int)128, (int)128) : ((index & 1) == 1 ? MapColorPalette.getColor((int)32, (int)32, (int)32) : MapColorPalette.getColor((int)64, (int)64, (int)64)));
            this.view.fillRectangle(1, i * 11 + 1, this.getWidth() - 2, 10, bgColor);
            this.view.drawLine(1, (i + 1) * 11, this.getWidth() - 2, (i + 1) * 11, gridColor);
            if (isNewIcon) {
                this.view.setAlignment(MapFont.Alignment.MIDDLE);
                this.view.draw(MapFont.MINECRAFT, this.getWidth() / 2, i * 11 + 2, (byte)18, (CharSequence)this.newNameText);
                break;
            }
            ListItem listItem = this.items.get(index);
            this.view.setAlignment(MapFont.Alignment.LEFT);
            this.view.getView(2, i * 11 + 2, this.getWidth() - 3, 8).draw(MapFont.MINECRAFT, -listItem.horOffset, 0, (byte)34, (CharSequence)listItem.name);
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (!this.isActivated() || event.getKey() == MapPlayerInput.Key.BACK) {
            super.onKeyPressed(event);
        } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
            if (this.selectedIndex < this.items.size()) {
                this.addWidget((MapWidget)new ConfirmItemDeleteDialog(){

                    @Override
                    public void onConfirmDelete() {
                        this.invalidate();
                        String nameRemoved = ((ListItem)((MapWidgetNameSet)MapWidgetNameSet.this).items.remove((int)((MapWidgetNameSet)MapWidgetNameSet.this).selectedIndex)).name;
                        MapWidgetNameSet.this.uniqueItemNames.remove(nameRemoved);
                        MapWidgetNameSet.this.onItemRemoved(nameRemoved);
                    }
                });
            } else {
                this.newItemDialog.activate();
            }
        } else if (event.getKey() == MapPlayerInput.Key.UP) {
            if (this.selectedIndex > 0) {
                --this.selectedIndex;
                this.scrollToSelection();
                this.invalidate();
            }
        } else if (event.getKey() == MapPlayerInput.Key.DOWN && this.selectedIndex < this.items.size()) {
            ++this.selectedIndex;
            this.scrollToSelection();
            this.invalidate();
        }
    }

    public void onTick() {
        if (this.numTicksOfNoScroll > 0) {
            if (++this.numTicksOfNoScroll > 30) {
                this.resetHScroll();
            }
        } else if (++this.horScrollTicks >= 60) {
            int numVisibleItems = this.calcNumItems();
            int textViewWidth = this.getWidth() - 3;
            boolean scrolled = false;
            for (int i = 0; i < numVisibleItems; ++i) {
                int index = this.scrollOffset + i;
                if (index >= this.items.size()) continue;
                scrolled |= this.items.get(index).scrollLeft(this.view, textViewWidth);
            }
            if (scrolled) {
                this.invalidate();
            } else {
                this.numTicksOfNoScroll = 1;
            }
        }
    }

    private void resetHScroll() {
        if (this.horScrollTicks > 60) {
            boolean changed = false;
            for (ListItem item : this.items) {
                if (item.horOffset <= 0) continue;
                item.horOffset = 0;
                changed = true;
            }
            if (changed) {
                this.invalidate();
            }
        }
        this.horScrollTicks = 0;
        this.numTicksOfNoScroll = 0;
    }

    private void scrollToSelection() {
        int numItems = this.calcNumItems();
        if (this.selectedIndex < this.scrollOffset) {
            this.scrollOffset = this.selectedIndex;
        } else if (this.selectedIndex - numItems + 1 > this.scrollOffset) {
            this.scrollOffset = this.selectedIndex - numItems + 1;
        }
    }

    private int calcNumItems() {
        return (this.getHeight() - 1) / 11;
    }

    private static class ListItem {
        public final String name;
        private int width = -1;
        public int horOffset = 0;

        public ListItem(String name) {
            this.name = name;
        }

        public boolean scrollLeft(MapCanvas view, int textViewWidth) {
            int cutOff = this.getWidth(view) - textViewWidth - this.horOffset;
            if (cutOff > 0) {
                this.horOffset += Math.min(cutOff, 2);
                return true;
            }
            return false;
        }

        public int getWidth(MapCanvas view) {
            int w = this.width;
            if (this.width == -1) {
                this.width = w = view.calcFontSize((MapFont)MapFont.MINECRAFT, (CharSequence)this.name).width;
            }
            return w;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object o) {
            return ((ListItem)o).name.equals(this.name);
        }
    }

    private static class ConfirmItemDeleteDialog
    extends MapWidgetMenu {
        public ConfirmItemDeleteDialog() {
            this.setBackgroundColor(MapColorPalette.getColor((int)135, (int)33, (int)33));
            this.setSize(90, 40);
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.setPosition((this.parent.getWidth() - this.getWidth()) / 2, (this.parent.getHeight() - this.getHeight()) / 2);
            this.addWidget(new MapWidgetText().setText("Delete this item?").setBounds(5, 5, 80, 30));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                }
            }.setText("No").setBounds(6, 21, 36, 13));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                    this.onConfirmDelete();
                }
            }.setText("Yes").setBounds(48, 21, 36, 13));
        }

        public void onConfirmDelete() {
        }
    }

    private static class ItemAlreadyAddedDialog
    extends MapWidgetMenu {
        public ItemAlreadyAddedDialog() {
            this.setBackgroundColor(MapColorPalette.getColor((int)135, (int)33, (int)33));
            this.setSize(90, 46);
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.setPosition((this.parent.getWidth() - this.getWidth()) / 2, (this.parent.getHeight() - this.getHeight()) / 2);
            this.addWidget(new MapWidgetText().setText("This item was\nalready added!").setBounds(5, 5, 80, 30));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                }
            }.setText("OK").setBounds(27, 27, 36, 13));
        }

        public void onConfirmDelete() {
        }
    }
}

