/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.block.InputDialogAnvil;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class MapWidgetAnvil
extends MapWidget {
    public final Button LEFT_BUTTON = new Button(this, 0, i -> i.LEFT_BUTTON);
    public final Button MIDDLE_BUTTON = new Button(this, 1, i -> i.MIDDLE_BUTTON);
    public final Button RIGHT_BUTTON = new Button(this, 2, i -> i.RIGHT_BUTTON);
    private final Set<Player> _openFor = new HashSet<Player>();
    private final Map<Player, InputDialogAnvil> _openDialogs = new HashMap<Player, InputDialogAnvil>();
    private boolean _isOpen = false;
    private String _text = "";

    public MapWidgetAnvil() {
        this.setBounds(0, 0, 0, 0);
        this.setFocusable(true);
    }

    public String getText() {
        return this._text;
    }

    public ChatText getTitle() {
        return null;
    }

    public MapWidgetAnvil openFor(Collection<Player> players) {
        this._openFor.clear();
        this._openFor.addAll(players);
        return this;
    }

    public void onClick(Button button) {
    }

    public void onTextChanged() {
    }

    public void onClose() {
    }

    @Override
    public void onActivate() {
        super.onActivate();
        this.closeAllDialogs();
        this._text = "";
        this.openDialogForAll();
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        this.closeAllDialogs();
    }

    private void openDialogForAll() {
        for (Player player : this._openFor.isEmpty() ? this.display.getViewers() : this._openFor) {
            if (this._openFor.isEmpty() && !this.display.isControlling(player)) continue;
            WidgetInputDialogAnvil dialog = new WidgetInputDialogAnvil(player);
            this.LEFT_BUTTON.applyAll(dialog);
            this.MIDDLE_BUTTON.applyAll(dialog);
            this.RIGHT_BUTTON.applyAll(dialog);
            this._openDialogs.put(player, dialog);
            dialog.open();
        }
        if (this._openDialogs.isEmpty()) {
            this.closeAllDialogs();
            this.deactivate();
            return;
        }
        this._isOpen = true;
    }

    private void closeAllDialogs() {
        ArrayList<InputDialogAnvil> dialogs = new ArrayList<InputDialogAnvil>(this._openDialogs.values());
        dialogs.forEach(InputDialogAnvil::close);
        this._openDialogs.clear();
        if (this._isOpen) {
            this._isOpen = false;
            this.onClose();
        }
    }

    private Button buttonFromAnvilDialog(InputDialogAnvil dialog, InputDialogAnvil.Button button) {
        if (this.LEFT_BUTTON._buttonAccessor.apply(dialog) == button) {
            return this.LEFT_BUTTON;
        }
        if (this.MIDDLE_BUTTON._buttonAccessor.apply(dialog) == button) {
            return this.MIDDLE_BUTTON;
        }
        if (this.RIGHT_BUTTON._buttonAccessor.apply(dialog) == button) {
            return this.RIGHT_BUTTON;
        }
        return null;
    }

    public static class Button {
        private final MapWidgetAnvil _owner;
        private final int _index;
        private final Function<InputDialogAnvil, InputDialogAnvil.Button> _buttonAccessor;
        private Material _material = Material.AIR;
        private String _title = "";
        private String _description = "";

        public Button(MapWidgetAnvil owner, int index, Function<InputDialogAnvil, InputDialogAnvil.Button> buttonAccessor) {
            this._owner = owner;
            this._index = index;
            this._buttonAccessor = buttonAccessor;
        }

        public int getIndex() {
            return this._index;
        }

        public String getTitle() {
            return this._title;
        }

        public void setTitle(String title) {
            this._title = title;
            this.forAllDialogs(b -> b.setTitle(title));
        }

        public String getDescription() {
            return this._description;
        }

        public void setDescription(String description) {
            this._description = description;
            this.forAllDialogs(b -> b.setDescription(description));
        }

        public Material getMaterial() {
            return this._material;
        }

        public void setMaterial(Material material) {
            this._material = material;
            this.forAllDialogs(b -> b.setMaterial(material));
        }

        public void setItemMaterial(ItemStack material) {
            this.setMaterial(material == null ? null : material.getType());
        }

        private void forAllDialogs(Consumer<InputDialogAnvil.Button> action) {
            for (InputDialogAnvil dialog : this._owner._openDialogs.values()) {
                action.accept(this._buttonAccessor.apply(dialog));
            }
        }

        private void applyAll(InputDialogAnvil dialog) {
            InputDialogAnvil.Button dialogButton = this._buttonAccessor.apply(dialog);
            dialogButton.setTitle(this.getTitle());
            dialogButton.setDescription(this.getDescription());
            dialogButton.setMaterial(this.getMaterial());
        }

        public String toString() {
            if (this.getDescription() == null || this.getDescription().isEmpty()) {
                return "Button[" + this.getIndex() + "]";
            }
            return this.getDescription();
        }
    }

    private class WidgetInputDialogAnvil
    extends InputDialogAnvil {
        public WidgetInputDialogAnvil(Player player) {
            super((Plugin)MapWidgetAnvil.this.getDisplay().getPlugin(), player);
        }

        @Override
        public ChatText getTitle() {
            return MapWidgetAnvil.this.getTitle();
        }

        @Override
        public void onTextChanged() {
            MapWidgetAnvil.this._text = this.getText();
            MapWidgetAnvil.this.LEFT_BUTTON._title = this.getText();
            MapWidgetAnvil.this.onTextChanged();
        }

        @Override
        public void onClick(InputDialogAnvil.Button button) {
            Button widgetButton = MapWidgetAnvil.this.buttonFromAnvilDialog(this, button);
            if (widgetButton != null) {
                MapWidgetAnvil.this.onClick(widgetButton);
            }
        }

        @Override
        public void onClose() {
            MapWidgetAnvil.this._openDialogs.remove(this.getPlayer());
            MapWidgetAnvil.this.closeAllDialogs();
        }
    }
}

