/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionRegistry;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionDialog;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import com.bergerkiller.bukkit.tc.controller.functions.ui.list.MapWidgetTransferFunctionList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class TransferFunctionList
implements TransferFunction,
Cloneable {
    public static final TransferFunction.Serializer<TransferFunctionList> SERIALIZER = new TransferFunction.Serializer<TransferFunctionList>(){

        @Override
        public String typeId() {
            return "LIST";
        }

        @Override
        public String title() {
            return "List Sequence";
        }

        @Override
        public TransferFunctionList createNew(TransferFunctionHost host) {
            return new TransferFunctionList();
        }

        @Override
        public TransferFunctionList load(TransferFunctionHost host, ConfigurationNode config) {
            TransferFunctionList list = new TransferFunctionList();
            TransferFunctionRegistry registry = host.getRegistry();
            for (ConfigurationNode functionConfig : config.getNodeList("functions")) {
                TransferFunction function = registry.load(host, functionConfig);
                FunctionMode mode = (FunctionMode)((Object)functionConfig.getOrDefault("functionMode", (Object)FunctionMode.ASSIGN));
                list.add(new Item(mode, function));
            }
            return list;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionList list) {
            if (!list.isEmpty()) {
                List effectConfigs = config.getList("functions");
                TransferFunctionRegistry registry = host.getRegistry();
                for (Item item : list.getItems()) {
                    ConfigurationNode functionConfig = registry.save(host, (TransferFunction)item.getFunction());
                    if (item.mode() != FunctionMode.ASSIGN) {
                        functionConfig.set("functionMode", (Object)item.mode());
                    }
                    effectConfigs.add(functionConfig);
                }
            }
        }
    };
    private final List<Item> items = new ArrayList<Item>();
    private int lastSelectedFunctionIndex = -1;
    private int lastScrollPosition = 0;

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public Item get(int index) {
        return this.items.get(index);
    }

    public void set(int index, Item item) {
        this.items.set(index, item);
    }

    public void add(TransferFunction function) {
        this.add(new Item(FunctionMode.ASSIGN, function));
    }

    public void add(Item item) {
        this.items.add(item);
    }

    public void add(int index, Item item) {
        this.items.add(index, item);
    }

    public void remove(int index) {
        this.items.remove(index);
    }

    public int indexOf(Item item) {
        return this.items.indexOf(item);
    }

    @Override
    public double map(double input) {
        for (Item item : this.items) {
            input = item.map(input);
        }
        return input;
    }

    @Override
    public boolean isPure() {
        for (int i = this.items.size() - 1; i >= 0; --i) {
            Item item = this.items.get(i);
            if (!item.getFunction().isPure()) {
                return false;
            }
            if (item.mode() == FunctionMode.ASSIGN) break;
        }
        return true;
    }

    public boolean isBooleanOutput(int index, BooleanSupplier isBooleanInput) {
        BooleanSupplier chain = isBooleanInput;
        int itemCount = this.items.size();
        for (int i = 0; i < itemCount; ++i) {
            Item item = this.items.get(i);
            if (item.mode.booleanMode() == FunctionBooleanMode.INPUT) {
                BooleanSupplier prev = chain;
                chain = () -> item.function.isBooleanOutput(prev);
            } else {
                boolean result = item.mode.booleanMode().asBool();
                chain = () -> result;
            }
            if (i == index) break;
        }
        return chain.getAsBoolean();
    }

    @Override
    public boolean isBooleanOutput(BooleanSupplier isBooleanInput) {
        return this.isBooleanOutput(this.items.size() - 1, isBooleanInput);
    }

    @Override
    public TransferFunctionList clone() {
        TransferFunctionList copy = new TransferFunctionList();
        for (Item item : this.items) {
            copy.items.add(item.clone());
        }
        return copy;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        byte color = widget.defaultColor((byte)30);
        view.drawLine(0, 3, 6, 3, color);
        view.drawLine(0, 5, 6, 5, color);
        view.drawLine(0, 7, 6, 7, color);
        view.drawLine(0, 9, 6, 9, color);
        view.draw(MapFont.MINECRAFT, 8, 3, color, (CharSequence)("[" + this.items.size() + (this.items.size() == 1 ? " step]" : " steps]")));
    }

    @Override
    public void openDialog(TransferFunction.Dialog dialog) {
        dialog.addWidget(new MapWidgetTransferFunctionList((MapWidgetTransferFunctionDialog)dialog, this){

            @Override
            public void onSelectedItemChanged() {
                TransferFunctionList.this.lastSelectedFunctionIndex = this.getSelectedItemIndex();
            }

            @Override
            public void onTick() {
                super.onTick();
                TransferFunctionList.this.lastScrollPosition = this.getVScroll();
            }
        }.setSelectedItemIndex(this.lastSelectedFunctionIndex).setVScroll(this.lastScrollPosition));
    }

    public static class Item
    extends TransferFunction.Holder<TransferFunction>
    implements Cloneable {
        private final FunctionMode mode;

        public Item(FunctionMode mode, TransferFunction function) {
            super(function, false);
            this.mode = mode;
        }

        public FunctionMode mode() {
            return this.mode;
        }

        public double map(double input) {
            return this.mode.apply(input, this.function.map(input));
        }

        public Item clone() {
            return new Item(this.mode, this.function.clone());
        }
    }

    public static enum FunctionMode {
        ASSIGN((i, fo) -> fo, FunctionBooleanMode.INPUT),
        MULTIPLY((i, fo) -> i * fo, FunctionBooleanMode.NEVER),
        DIVIDE((i, fo) -> i / fo, FunctionBooleanMode.NEVER),
        SUBTRACT((i, fo) -> i - fo, FunctionBooleanMode.NEVER),
        ADD((i, fo) -> i + fo, FunctionBooleanMode.NEVER),
        OR((i, fo) -> i != 0.0 || fo != 0.0 ? 1.0 : 0.0, FunctionBooleanMode.ALWAYS),
        AND((i, fo) -> i != 0.0 && fo != 0.0 ? 1.0 : 0.0, FunctionBooleanMode.ALWAYS);

        private final FunctionModeOperator operator;
        private final FunctionBooleanMode booleanMode;

        private FunctionMode(FunctionModeOperator operator, FunctionBooleanMode booleanMode) {
            this.operator = operator;
            this.booleanMode = booleanMode;
        }

        public FunctionBooleanMode booleanMode() {
            return this.booleanMode;
        }

        public double apply(double input, double functionOutput) {
            return this.operator.apply(input, functionOutput);
        }
    }

    public static enum FunctionBooleanMode {
        INPUT(false),
        ALWAYS(true),
        NEVER(false);

        private final boolean asBool;

        private FunctionBooleanMode(boolean asBool) {
            this.asBool = asBool;
        }

        public boolean asBool() {
            return this.asBool;
        }
    }

    @FunctionalInterface
    private static interface FunctionModeOperator {
        public double apply(double var1, double var3);
    }
}

