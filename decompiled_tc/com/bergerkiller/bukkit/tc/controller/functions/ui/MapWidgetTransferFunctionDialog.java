/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionTypeSelectorDialog;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class MapWidgetTransferFunctionDialog
extends MapWidgetMenu
implements TransferFunction.Dialog {
    private final TransferFunctionHost host;
    private TransferFunction.Holder<TransferFunction> root;
    private TransferFunctionNav nav;
    private TransferFunction.Holder<?> prev;

    public MapWidgetTransferFunctionDialog(TransferFunctionHost host, TransferFunction rootFunction, BooleanSupplier isBooleanInput) {
        this.setBackgroundColor(MapColorPalette.getColor((int)72, (int)108, (int)152));
        this.setBounds(7, 17, 114, 105);
        this.setPositionAbsolute(true);
        this.host = host;
        this.root = TransferFunction.Holder.of(rootFunction);
        this.nav = new TransferFunctionNav(null, this.root, isBooleanInput);
        this.prev = null;
    }

    public abstract void onChanged(TransferFunction var1);

    @Override
    public void markChanged() {
        this.onChanged(this.root.getFunction());
    }

    @Override
    public TransferFunctionHost getHost() {
        return this.host;
    }

    @Override
    public void finish() {
        if (this.nav.parent != null) {
            this.navigate(this.nav.parent);
        } else {
            this.close();
        }
    }

    @Override
    public MapWidget getWidget() {
        return this;
    }

    @Override
    public void setFunction(TransferFunction newFunction) {
        if (this.nav != null) {
            if (this.nav.function.getFunction() == newFunction) {
                this.markChanged();
                return;
            }
            this.nav.function.setFunction(newFunction);
            this.navigate(this.nav);
            this.markChanged();
        }
    }

    @Override
    public boolean isBooleanInput() {
        return this.nav != null && this.nav.isBooleanInput.getAsBoolean();
    }

    @Override
    public boolean isPreviousFunction(TransferFunction.Holder<?> functionHolder) {
        return this.prev != null && this.prev.isSame(functionHolder);
    }

    public void navigate(TransferFunction.Holder<TransferFunction> function, BooleanSupplier isBooleanInput) {
        if (this.nav != null) {
            if (this.nav.function == function) {
                return;
            }
            if (this.nav.parent != null && this.nav.parent.function == function) {
                this.navigate(this.nav.parent);
                return;
            }
        }
        this.navigate(new TransferFunctionNav(this.nav, function, isBooleanInput));
    }

    private void navigate(TransferFunctionNav nav) {
        if (nav.function.getFunction().openDialogMode() != TransferFunction.DialogMode.WINDOW) {
            throw new IllegalArgumentException("Cannot navigate: function dialog mode is " + (Object)((Object)nav.function.getFunction().openDialogMode()));
        }
        if (this.nav != nav) {
            this.prev = this.nav == null ? null : this.nav.function;
        }
        this.nav = nav;
        this.clearWidgets();
        if (this.nav != null && this.getDisplay() != null) {
            this.deactivate();
            try {
                this.nav.function.getFunction().openDialog(this);
            }
            catch (Throwable t) {
                this.display.getPlugin().getLogger().log(Level.SEVERE, "Failed to open function dialog", t);
                this.close();
                return;
            }
            if (nav.parent != null && this.getWidgetCount() == 0) {
                this.display.playSound(SoundEffect.EXTINGUISH);
                this.navigate(nav.parent);
            } else {
                this.activate();
            }
        }
    }

    public void createNew(final Consumer<TransferFunction> action) {
        this.addWidget((MapWidget)new MapWidgetTransferFunctionTypeSelectorDialog(this, this.host){
            final /* synthetic */ MapWidgetTransferFunctionDialog this$0;
            {
                this.this$0 = this$0;
                super(host);
            }

            @Override
            public void onSelected(TransferFunction function) {
                action.accept(function);
            }
        });
    }

    @Override
    public void onAttached() {
        this.navigate(this.nav);
        super.onAttached();
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        if (this.exitOnBack && event.getKey() == MapPlayerInput.Key.BACK && this.isActivated() && this.nav.parent != null) {
            this.navigate(this.nav.parent);
            this.display.playSound(SoundEffect.CLICK, 1.0f, 0.6f);
            return;
        }
        super.onKeyPressed(event);
    }

    private static class TransferFunctionNav {
        public final TransferFunctionNav parent;
        public final TransferFunction.Holder<TransferFunction> function;
        public final BooleanSupplier isBooleanInput;
        public final int depth;

        public TransferFunctionNav(TransferFunctionNav parent, TransferFunction.Holder<TransferFunction> function, BooleanSupplier isBooleanInput) {
            this.parent = parent;
            this.function = function;
            this.isBooleanInput = isBooleanInput;
            this.depth = parent != null ? parent.depth + 1 : 0;
        }
    }
}

