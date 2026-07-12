/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class MapWidgetModelStoreSelect
extends MapWidget {
    private final TrainCarts traincarts;
    private SavedAttachmentModel selected = null;
    private final MapWidgetSubmitText submitText;
    private final MapWidgetModelName modelName;
    private final MapWidgetDropDownListButton dropDownListButton;
    private int listNumRows = 7;

    public MapWidgetModelStoreSelect(TrainCarts traincarts) {
        this.traincarts = traincarts;
        this.setRetainChildWidgets(true);
        this.submitText = (MapWidgetSubmitText)this.addWidget((MapWidget)new MapWidgetSubmitText(){

            public void onAccept(String text) {
                this.display.playSound(SoundEffect.CLICK_WOOD);
                MapWidgetModelStoreSelect.this.setSelectedModelCheckChanged(text);
            }

            public void onCancel() {
            }
        });
        this.submitText.setDescription("Enter Model Name");
        this.modelName = (MapWidgetModelName)this.addWidget(new MapWidgetModelName(){

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                MapWidgetModelStoreSelect.this.submitText.activate();
            }
        });
        this.dropDownListButton = (MapWidgetDropDownListButton)this.addWidget(new MapWidgetDropDownListButton(){

            public void onActivate() {
                MapWidgetModelStoreSelect.this.openDropDownModelList();
            }
        });
    }

    public abstract void onSelectedModelChanged(SavedAttachmentModel var1);

    public MapWidgetModelStoreSelect setSelectedModelName(String modelName) {
        return this.setSelectedModel(this.traincarts.getSavedAttachmentModels().getModelOrNone(modelName));
    }

    public MapWidgetModelStoreSelect setSelectedModel(SavedAttachmentModel model) {
        this.selected = model;
        this.modelName.invalidate();
        return this;
    }

    public MapWidgetModelStoreSelect setListNumRows(int num) {
        this.listNumRows = num;
        return this;
    }

    public SavedAttachmentModel getSelectedModel() {
        return this.selected;
    }

    public void openDropDownModelList() {
        int listYPos = this.getAbsoluteY() + this.getHeight() - 1;
        int listHeight = this.listNumRows * 8 + 4;
        this.addWidget(new MapWidgetDropDownList(this.listNumRows){

            @Override
            public void onModelSelected(SavedAttachmentModel selectedModel) {
                MapWidgetModelStoreSelect.this.setSelectedModelCheckChanged(this.getSelectedModel());
            }

            @Override
            public void onClosed() {
                this.display.playSound(SoundEffect.PISTON_CONTRACT);
                this.removeWidget();
                MapWidgetModelStoreSelect.this.dropDownListButton.focus();
            }
        }.setBounds(this.getAbsoluteX(), listYPos, this.getWidth(), listHeight));
        this.display.playSound(SoundEffect.PISTON_EXTEND);
    }

    public void onBoundsChanged() {
        this.modelName.setBounds(0, 0, this.getWidth() - this.getHeight(), this.getHeight());
        this.dropDownListButton.setBounds(this.getWidth() - this.getHeight(), 0, this.getHeight(), this.getHeight());
    }

    private boolean setSelectedModelCheckChanged(String name) {
        if (name == null || name.trim().isEmpty()) {
            return this.setSelectedModelCheckChanged((SavedAttachmentModel)null);
        }
        return this.setSelectedModelCheckChanged(this.traincarts.getSavedAttachmentModels().getModelOrNone(name));
    }

    private boolean setSelectedModelCheckChanged(SavedAttachmentModel newModel) {
        if (LogicUtil.bothNullOrEqual((Object)this.selected, (Object)newModel)) {
            return false;
        }
        this.selected = newModel;
        this.modelName.invalidate();
        this.onSelectedModelChanged(newModel);
        return true;
    }

    private class MapWidgetDropDownListButton
    extends MapWidgetBordered {
        public MapWidgetDropDownListButton() {
            this.setFocusable(true);
        }

        public void onDraw() {
            this.drawBackground(false, this.isFocused());
            int t_w = this.getWidth() - 7;
            this.drawUpsideDownTriangleFrom(3, this.getHeight() - 3 - t_w / 2, t_w, this.isFocused() ? (byte)122 : 119);
        }

        private void drawUpsideDownTriangleFrom(int tl_x, int tl_y, int w, byte color) {
            while (w > 0) {
                this.view.drawLine(tl_x, tl_y, tl_x + w, tl_y, color);
                w -= 2;
                ++tl_x;
                ++tl_y;
            }
        }
    }

    private class MapWidgetModelName
    extends MapWidgetBordered {
        public MapWidgetModelName() {
            this.setFocusable(true);
        }

        public void onDraw() {
            this.drawBackground(true, this.isFocused());
            if (MapWidgetModelStoreSelect.this.selected != null) {
                this.view.draw(MapFont.MINECRAFT, 3, 3, MapWidgetModelStoreSelect.this.selected.isNone() ? (byte)18 : (this.isFocused() ? (byte)122 : 119), (CharSequence)MapWidgetModelStoreSelect.this.selected.getName());
            } else {
                this.view.draw(MapFont.MINECRAFT, 3, 3, MapColorPalette.getColor((int)80, (int)64, (int)64), (CharSequence)"-- not set --");
            }
        }
    }

    private abstract class MapWidgetDropDownList
    extends MapWidgetBordered {
        public static final int ROW_HEIGHT = 8;
        public static final int PADDING = 4;
        private final int numberOfRows;
        private List<SavedAttachmentModel> models;
        private int selectedIndex;
        private int scrollOffset;

        public MapWidgetDropDownList(int numberOfRows) {
            this.setPositionAbsolute(true);
            this.numberOfRows = numberOfRows;
            this.setFocusable(true);
            this.setDepthOffset(2);
        }

        public abstract void onModelSelected(SavedAttachmentModel var1);

        public abstract void onClosed();

        public SavedAttachmentModel getSelectedModel() {
            return this.models.isEmpty() ? null : this.models.get(this.selectedIndex);
        }

        public void onAttached() {
            this.models = MapWidgetModelStoreSelect.this.traincarts.getSavedAttachmentModels().getAll();
            if (MapWidgetModelStoreSelect.this.selected == null) {
                this.selectedIndex = 0;
            } else {
                this.selectedIndex = this.models.indexOf(MapWidgetModelStoreSelect.this.selected);
                if (this.selectedIndex == -1) {
                    this.models = new ArrayList<SavedAttachmentModel>(this.models);
                    this.models.add(MapWidgetModelStoreSelect.this.selected);
                    this.models.sort(Comparator.comparing(SavedAttachmentModel::getName));
                    this.selectedIndex = this.models.indexOf(MapWidgetModelStoreSelect.this.selected);
                }
            }
            this.scrollToSelection();
            this.activate();
        }

        public void onDraw() {
            this.drawBackground(false, false);
            byte selectedBGColor = MapColorPalette.getColor((int)140, (int)140, (int)140);
            byte selectedColor = 122;
            byte unselectedColor = MapColorPalette.getColor((int)190, (int)190, (int)190);
            for (int i = 0; i < this.numberOfRows; ++i) {
                int index = this.scrollOffset + i;
                if (index >= this.models.size()) continue;
                byte color = unselectedColor;
                if (index == this.selectedIndex) {
                    color = selectedColor;
                    this.view.fillRectangle(2, 2 + i * 8, this.getWidth() - 4, 8, selectedBGColor);
                }
                this.view.draw(MapFont.MINECRAFT, 2, 2 + i * 8, color, (CharSequence)this.models.get(index).getName());
            }
        }

        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.UP) {
                if (this.selectedIndex > 0) {
                    this.selectedIndex = Math.max(0, this.selectedIndex - 1);
                    this.scrollToSelection();
                    this.invalidate();
                }
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                int maxIndex = this.models.size() - 1;
                if (this.selectedIndex < maxIndex) {
                    this.selectedIndex = Math.min(maxIndex, this.selectedIndex + 1);
                    this.scrollToSelection();
                    this.invalidate();
                }
            } else if (event.getKey() == MapPlayerInput.Key.BACK) {
                this.onClosed();
            } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
                this.onModelSelected(MapWidgetModelStoreSelect.this.selected);
                this.onClosed();
            }
        }

        private void scrollToSelection() {
            if (this.models.size() < this.numberOfRows) {
                this.scrollOffset = 0;
            } else if (this.selectedIndex < this.scrollOffset) {
                this.scrollOffset = this.selectedIndex;
            } else if (this.selectedIndex >= this.scrollOffset + this.numberOfRows) {
                this.scrollOffset = this.selectedIndex - this.numberOfRows + 1;
            }
        }
    }

    private static class MapWidgetBordered
    extends MapWidget {
        private MapWidgetBordered() {
        }

        public void drawBackground(boolean invertedBorderColors, boolean focused) {
            byte color_edge = focused ? (byte)122 : 119;
            byte color_inner_tl = MapColorPalette.getColor((int)143, (int)143, (int)143);
            byte color_inner_br = MapColorPalette.getColor((int)66, (int)66, (int)66);
            byte color_inner = MapColorPalette.getColor((int)112, (int)112, (int)112);
            if (invertedBorderColors) {
                byte b = color_inner_tl;
                color_inner_tl = color_inner_br;
                color_inner_br = b;
            }
            this.view.drawLine(1, 0, this.getWidth() - 2, 0, color_edge);
            this.view.drawLine(1, this.getHeight() - 1, this.getWidth() - 2, this.getHeight() - 1, color_edge);
            this.view.drawLine(0, 1, 0, this.getHeight() - 2, color_edge);
            this.view.drawLine(this.getWidth() - 1, 1, this.getWidth() - 1, this.getHeight() - 2, color_edge);
            this.view.drawLine(1, 1, 1, this.getHeight() - 2, color_inner_tl);
            this.view.drawLine(1, 1, this.getWidth() - 2, 1, color_inner_tl);
            this.view.drawLine(this.getWidth() - 2, 1, 1, this.getHeight() - 2, color_inner_tl);
            this.view.drawLine(this.getWidth() - 2, 2, this.getWidth() - 2, this.getHeight() - 2, color_inner_br);
            this.view.drawLine(2, this.getHeight() - 2, this.getWidth() - 2, this.getHeight() - 2, color_inner_br);
            this.view.fillRectangle(2, 2, this.getWidth() - 4, this.getHeight() - 4, color_inner);
        }
    }
}

