/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.bukkit.plugin.java.JavaPlugin;

public class MapWidgetTransferFunctionItem
extends MapWidget {
    public static final int HEIGHT = 15;
    protected static final byte COLOR_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
    protected static final byte COLOR_BG_FOCUSED = MapColorPalette.getColor((int)255, (int)252, (int)245);
    protected static final byte COLOR_BG_MOVING = MapColorPalette.getColor((int)247, (int)233, (int)163);
    protected final List<Button> buttons = new ArrayList<Button>();
    protected final TransferFunctionHost host;
    protected final TransferFunction.Holder<TransferFunction> function;
    protected final BooleanSupplier isBooleanInput;
    protected boolean moving;
    protected int selButtonIdx = 0;

    public MapWidgetTransferFunctionItem(TransferFunctionHost host, TransferFunction.Holder<TransferFunction> function, BooleanSupplier isBooleanInput) {
        this.host = host;
        this.function = function.withChangeListener(this::onChangedInternal);
        this.isBooleanInput = isBooleanInput;
        this.setFocusable(true);
        this.setSize(104, 15);
    }

    protected void onChangedInternal(TransferFunction.Holder<TransferFunction> function) {
    }

    public void onMoveUp() {
    }

    public void onMoveDown() {
    }

    public TransferFunction getFunction() {
        return this.function.getFunction();
    }

    public boolean isDefault() {
        return this.function.isDefault();
    }

    public byte defaultColor(byte color) {
        return this.isDefault() ? TransferFunction.DEFAULT_FUNCTION_COLOR : color;
    }

    public MapWidgetTransferFunctionItem addConfigureButton() {
        return this.addButton(ButtonIcon.CONFIGURE, this::configure);
    }

    public void updateButtons(Consumer<MapWidgetTransferFunctionItem> addActions) {
        ButtonIcon prevSelectedIcon = null;
        if (this.selButtonIdx >= 0 && this.selButtonIdx < this.buttons.size()) {
            prevSelectedIcon = this.buttons.get((int)this.selButtonIdx).icon;
        }
        this.buttons.clear();
        addActions.accept(this);
        this.invalidate();
        if (prevSelectedIcon != null) {
            for (int i = 0; i < this.buttons.size(); ++i) {
                if (this.buttons.get((int)i).icon != prevSelectedIcon) continue;
                this.selButtonIdx = i;
                return;
            }
        }
        if (this.selButtonIdx >= this.buttons.size()) {
            this.selButtonIdx = this.buttons.size() - 1;
        }
    }

    public MapWidgetTransferFunctionItem addButton(ButtonIcon icon, Runnable action) {
        this.buttons.add(new Button(icon, action));
        return this;
    }

    public void configure() {
        if (this.getFunction().openDialogMode() == TransferFunction.DialogMode.NONE) {
            this.display.playSound(SoundEffect.EXTINGUISH);
            return;
        }
        if (this.getFunction().openDialogMode() == TransferFunction.DialogMode.INLINE) {
            InlineDialog inlineDialog = new InlineDialog();
            this.updateInlineDialogBounds(inlineDialog);
            this.addWidget(inlineDialog);
            this.getFunction().openDialog(inlineDialog);
            if (inlineDialog.getWidgetCount() == 0) {
                inlineDialog.finish();
                this.display.playSound(SoundEffect.EXTINGUISH);
            } else {
                this.activate();
            }
            return;
        }
        MapWidgetTransferFunctionDialog dialog = this.getCurrentDialog();
        if (dialog != null) {
            dialog.navigate(this.function, this.isBooleanInput);
        } else {
            dialog = new MapWidgetTransferFunctionDialog(this.host, this.function.getFunction(), this.isBooleanInput){

                @Override
                public void onChanged(TransferFunction function) {
                    MapWidgetTransferFunctionItem.this.function.setFunction(function);
                    MapWidgetTransferFunctionItem.this.invalidate();
                }
            };
            this.getParent().addWidget((MapWidget)dialog);
        }
    }

    public boolean isMoving() {
        return this.moving;
    }

    protected void setSelectedButton(int index) {
        if (index < 0) {
            index = 0;
        } else if (index >= this.buttons.size()) {
            index = this.buttons.size() - 1;
        }
        if (this.selButtonIdx != index) {
            this.selButtonIdx = index;
            this.invalidate();
        }
    }

    protected MapWidgetTransferFunctionDialog getCurrentDialog() {
        for (MapWidget w = this.getParent(); w != null; w = w.getParent()) {
            if (!(w instanceof MapWidgetTransferFunctionDialog)) continue;
            return (MapWidgetTransferFunctionDialog)w;
        }
        return null;
    }

    public void onFocus() {
        this.selButtonIdx = 0;
    }

    public void onDraw() {
        this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
        this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.moving ? COLOR_BG_MOVING : (this.isFocused() ? COLOR_BG_FOCUSED : COLOR_BG_DEFAULT));
        if (!this.isActivated()) {
            MapCanvas previewView = this.view.getView(2, 1, this.getWidth() - 2, this.getHeight() - 2);
            this.getFunction().drawPreview(this, previewView);
            this.drawUI();
        }
    }

    protected void updateInlineDialogBounds(InlineDialog dialog) {
        dialog.setBounds(1, 1, this.getWidth() - 2, this.getHeight() - 2);
    }

    protected void drawUI() {
        if (!this.moving && this.isFocused() && !this.buttons.isEmpty()) {
            int x_icon_step = this.buttons.get((int)0).icon.width() + 1;
            int x = this.getWidth() - this.buttons.size() * x_icon_step - 1;
            int i = 0;
            for (Button b : this.buttons) {
                this.view.draw((MapCanvas)b.icon.icon(this.selButtonIdx == i), x, 2);
                ++i;
                x += x_icon_step;
            }
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (this.moving) {
            if (event.getKey() == MapPlayerInput.Key.UP) {
                this.onMoveUp();
            } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
                this.onMoveDown();
            } else if (event.getKey() == MapPlayerInput.Key.BACK || event.getKey() == MapPlayerInput.Key.ENTER) {
                this.moving = false;
                MapWidgetTransferFunctionDialog dialog = this.getCurrentDialog();
                if (dialog != null) {
                    dialog.setExitOnBack(true);
                }
                this.invalidate();
            }
        } else if (event.getKey() == MapPlayerInput.Key.LEFT && this.isFocused()) {
            this.setSelectedButton(this.selButtonIdx - 1);
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT && this.isFocused()) {
            this.setSelectedButton(this.selButtonIdx + 1);
        } else if (event.getKey() == MapPlayerInput.Key.ENTER && this.selButtonIdx >= 0 && !this.buttons.isEmpty() && this.isFocused()) {
            this.buttons.get((int)this.selButtonIdx).action.run();
        } else {
            super.onKeyPressed(event);
        }
    }

    public static enum ButtonIcon {
        CONFIGURE("Configure"),
        MOVE("Change order"),
        REMOVE("Remove operation"),
        ADD("Add new operation"),
        INITIALIZE("Set operation");

        private final MapTexture icon_selected;
        private final MapTexture icon_default;
        private final String tooltip;

        private ButtonIcon(String tooltip) {
            MapTexture atlas = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/transfer_function_item_buttons.png");
            this.icon_selected = atlas.getView(this.ordinal() * atlas.getHeight(), 0, atlas.getHeight(), atlas.getHeight()).clone();
            this.icon_default = this.icon_selected.clone();
            this.icon_default.setBlendMode(MapBlendMode.SUBTRACT);
            this.icon_default.fill(MapColorPalette.getColor((int)20, (int)20, (int)64));
            this.tooltip = tooltip;
        }

        public int width() {
            return this.icon_selected.getWidth();
        }

        public MapTexture icon(boolean selected) {
            return selected ? this.icon_selected : this.icon_default;
        }

        public String tooltip() {
            return this.tooltip;
        }
    }

    private static class Button {
        public final ButtonIcon icon;
        public Runnable action;

        public Button(ButtonIcon icon, Runnable action) {
            this.icon = icon;
            this.action = action;
        }
    }

    protected class InlineDialog
    extends MapWidget
    implements TransferFunction.Dialog {
        protected InlineDialog() {
        }

        @Override
        public MapWidget getWidget() {
            return this;
        }

        @Override
        public TransferFunctionHost getHost() {
            return MapWidgetTransferFunctionItem.this.host;
        }

        @Override
        public void setFunction(TransferFunction function) {
            MapWidgetTransferFunctionItem.this.function.setFunction(function);
        }

        @Override
        public boolean isBooleanInput() {
            return MapWidgetTransferFunctionItem.this.isBooleanInput.getAsBoolean();
        }

        @Override
        public boolean isPreviousFunction(TransferFunction.Holder<?> functionHolder) {
            return MapWidgetTransferFunctionItem.this.function.isSame(functionHolder);
        }

        @Override
        public void markChanged() {
            this.setFunction(MapWidgetTransferFunctionItem.this.function.getFunction());
            MapWidgetTransferFunctionDialog dialog = MapWidgetTransferFunctionItem.this.getCurrentDialog();
            if (dialog != null) {
                dialog.markChanged();
            }
        }

        @Override
        public void finish() {
            this.removeWidget();
            MapWidgetTransferFunctionItem.this.focus();
        }

        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.BACK) {
                this.finish();
            } else {
                super.onKeyPressed(event);
            }
        }
    }
}

