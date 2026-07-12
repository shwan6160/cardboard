/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionIdentity;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionRegistry;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;

public interface TransferFunction
extends DoubleUnaryOperator,
Cloneable {
    public static final byte DEFAULT_FUNCTION_COLOR = MapColorPalette.getColor((int)100, (int)100, (int)100);

    public Serializer<? extends TransferFunction> getSerializer();

    public static TransferFunctionRegistry getRegistry() {
        return TransferFunctionRegistry.INSTANCE;
    }

    public static TransferFunction identity() {
        return TransferFunctionIdentity.INSTANCE;
    }

    public double map(double var1);

    default public boolean isBooleanOutput(BooleanSupplier isBooleanInput) {
        return false;
    }

    default public boolean isPure() {
        return false;
    }

    @Override
    default public double applyAsDouble(double v) {
        return this.map(v);
    }

    public TransferFunction clone();

    public void drawPreview(MapWidgetTransferFunctionItem var1, MapCanvas var2);

    public void openDialog(Dialog var1);

    default public DialogMode openDialogMode() {
        return DialogMode.WINDOW;
    }

    public static enum DialogMode {
        NONE,
        INLINE,
        WINDOW;

    }

    public static interface Serializer<T extends TransferFunction> {
        public static final String TYPE_FIELD = "type";

        public String typeId();

        public String title();

        default public boolean isListed(TransferFunctionHost host) {
            return true;
        }

        default public boolean isInput() {
            return false;
        }

        public T createNew(TransferFunctionHost var1);

        public T load(TransferFunctionHost var1, ConfigurationNode var2);

        public void save(TransferFunctionHost var1, ConfigurationNode var2, T var3);
    }

    public static class Holder<T extends TransferFunction> {
        protected T function;
        protected boolean isDefault = false;

        protected Holder(T function, boolean isDefault) {
            this.function = function;
            this.isDefault = isDefault;
        }

        public T getFunction() {
            return this.function;
        }

        public final void setFunction(T function) {
            this.setFunction(function, false);
        }

        public void setFunction(T function, boolean isDefault) {
            this.function = function;
            this.isDefault = isDefault;
        }

        public boolean isIdentity() {
            return this.getFunction() == TransferFunction.identity();
        }

        public boolean isDefault() {
            return this.isDefault;
        }

        public Holder<T> withChangeListener(final Consumer<Holder<T>> onChanged) {
            final Holder orig = this;
            return new Holder<T>(this, (TransferFunction)this.function, this.isDefault){
                final /* synthetic */ Holder this$0;
                {
                    this.this$0 = this$0;
                    super(function, isDefault);
                }

                @Override
                public void setFunction(T function, boolean isDefault) {
                    super.setFunction(function, isDefault);
                    orig.setFunction(function, isDefault);
                    onChanged.accept(this);
                }

                @Override
                protected Holder<?> rootHolder() {
                    return orig.rootHolder();
                }
            };
        }

        public final boolean isSame(Holder<?> other) {
            return this.rootHolder() == other.rootHolder();
        }

        protected Holder<?> rootHolder() {
            return this;
        }

        public static <T extends TransferFunction> Holder<T> of(T function) {
            return new Holder<T>(function, false);
        }

        public static <T extends TransferFunction> Holder<T> of(T function, boolean isDefault) {
            return new Holder<T>(function, isDefault);
        }
    }

    public static interface Dialog {
        public MapWidget getWidget();

        public TransferFunctionHost getHost();

        public void setFunction(TransferFunction var1);

        public boolean isBooleanInput();

        public boolean isPreviousFunction(Holder<?> var1);

        public void markChanged();

        public void finish();

        default public <T extends MapWidget> T addWidget(T widget) {
            return (T)this.getWidget().addWidget(widget);
        }

        default public int getWidth() {
            return this.getWidget().getWidth();
        }

        default public int getHeight() {
            return this.getWidget().getHeight();
        }

        default public void addLabel(int x, int y, byte color, String text) {
            MapWidgetText label = new MapWidgetText();
            label.setFont(MapFont.TINY);
            label.setText(text);
            label.setPosition(x, y);
            label.setColor(color);
            this.addWidget(label);
        }

        default public Dialog wrapWidget(final MapWidget widget) {
            final Dialog original = this;
            return new Dialog(){
                final /* synthetic */ Dialog this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public MapWidget getWidget() {
                    return widget;
                }

                @Override
                public TransferFunctionHost getHost() {
                    return original.getHost();
                }

                @Override
                public void setFunction(TransferFunction function) {
                    original.setFunction(function);
                }

                @Override
                public boolean isBooleanInput() {
                    return original.isBooleanInput();
                }

                @Override
                public boolean isPreviousFunction(Holder<?> functionHolder) {
                    return original.isPreviousFunction(functionHolder);
                }

                @Override
                public void markChanged() {
                    original.markChanged();
                }

                @Override
                public void finish() {
                    original.finish();
                }
            };
        }
    }
}

