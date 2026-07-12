/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionBoolean;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConstant;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionSingleItem;
import com.bergerkiller.bukkit.tc.controller.functions.ui.conditional.MapWidgetTransferFunctionConditionalHysteresis;
import com.bergerkiller.bukkit.tc.controller.functions.ui.conditional.MapWidgetTransferFunctionConditionalOperator;
import com.bergerkiller.bukkit.tc.utils.CachedBooleanSupplier;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class TransferFunctionConditional
implements TransferFunction {
    public static final TransferFunction.Serializer<TransferFunctionConditional> SERIALIZER = new TransferFunction.Serializer<TransferFunctionConditional>(){

        @Override
        public String typeId() {
            return "CONDITIONAL";
        }

        @Override
        public String title() {
            return "Conditional";
        }

        @Override
        public TransferFunctionConditional createNew(TransferFunctionHost host) {
            TransferFunctionConditional conditional = new TransferFunctionConditional();
            conditional.setOperator(Operator.GREATER_THAN);
            conditional.setRightInput(TransferFunctionConstant.zero());
            conditional.setTrueOutput(TransferFunctionBoolean.TRUE);
            conditional.setFalseOutput(TransferFunctionBoolean.FALSE);
            return conditional;
        }

        @Override
        public TransferFunctionConditional load(TransferFunctionHost host, ConfigurationNode config) {
            TransferFunctionConditional conditional = new TransferFunctionConditional();
            if (config.isNode("left")) {
                conditional.setLeftInput(host.loadFunction(config.getNode("left")));
            }
            if (config.isNode("right")) {
                conditional.setRightInput(host.loadFunction(config.getNode("right")));
            }
            conditional.setOperator((Operator)((Object)config.getOrDefault("operator", (Object)conditional.operator)));
            conditional.setHysteresis((Double)config.getOrDefault("hysteresis", (Object)0.0));
            if (config.isNode("falseOutput")) {
                conditional.setFalseOutput(host.loadFunction(config.getNode("falseOutput")));
            }
            if (config.isNode("trueOutput")) {
                conditional.setTrueOutput(host.loadFunction(config.getNode("trueOutput")));
            }
            return conditional;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionConditional conditional) {
            if (!conditional.leftInput.isDefault()) {
                config.set("left", (Object)host.saveFunction((TransferFunction)conditional.leftInput.getFunction()));
            }
            if (!conditional.rightInput.isDefault()) {
                config.set("right", (Object)host.saveFunction((TransferFunction)conditional.rightInput.getFunction()));
            }
            config.set("operator", (Object)conditional.operator);
            config.set("hysteresis", conditional.hysteresis != 0.0 ? Double.valueOf(conditional.hysteresis) : null);
            if (!conditional.falseOutput.isDefault()) {
                config.set("falseOutput", (Object)host.saveFunction((TransferFunction)conditional.falseOutput.getFunction()));
            }
            if (!conditional.trueOutput.isDefault()) {
                config.set("trueOutput", (Object)host.saveFunction((TransferFunction)conditional.trueOutput.getFunction()));
            }
        }
    };
    private final TransferFunction.Holder<TransferFunction> leftInput = TransferFunction.Holder.of(TransferFunction.identity(), true);
    private final TransferFunction.Holder<TransferFunction> rightInput = TransferFunction.Holder.of(TransferFunction.identity(), true);
    private Operator operator = Operator.GREATER_THAN;
    private double hysteresis = 0.0;
    private Boolean hysteresisLastState = null;
    private final TransferFunction.Holder<TransferFunction> falseOutput = TransferFunction.Holder.of(TransferFunction.identity(), true);
    private final TransferFunction.Holder<TransferFunction> trueOutput = TransferFunction.Holder.of(TransferFunction.identity(), true);
    private Boolean leftWasBooleanOnOpen = null;

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public double map(double input) {
        boolean result;
        if (this.operator == Operator.BOOL) {
            result = this.leftInput.getFunction().map(input) != 0.0;
        } else if (this.hysteresis == 0.0) {
            result = this.operator.compare(this.leftInput.getFunction().map(input), this.rightInput.getFunction().map(input));
        } else {
            if (this.hysteresisLastState == null) {
                this.hysteresisLastState = this.hysteresis < 0.0;
            }
            result = this.operator.compareWithHysteresis(this.hysteresisLastState, this.leftInput.getFunction().map(input), this.rightInput.getFunction().map(input), Math.abs(this.hysteresis));
        }
        this.hysteresisLastState = result;
        return (result ? this.trueOutput : this.falseOutput).getFunction().map(input);
    }

    @Override
    public boolean isBooleanOutput(BooleanSupplier isBooleanInput) {
        isBooleanInput = CachedBooleanSupplier.of(isBooleanInput);
        return this.trueOutput.getFunction().isBooleanOutput(isBooleanInput) && this.falseOutput.getFunction().isBooleanOutput(isBooleanInput);
    }

    @Override
    public boolean isPure() {
        return this.leftInput.getFunction().isPure() && (this.operator == Operator.BOOL || this.rightInput.getFunction().isPure()) && this.falseOutput.getFunction().isPure() && this.trueOutput.getFunction().isPure();
    }

    public void setLeftInput(TransferFunction input) {
        this.leftInput.setFunction(input);
    }

    public void setRightInput(TransferFunction input) {
        this.rightInput.setFunction(input);
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void setHysteresis(double hysteresis) {
        this.hysteresis = hysteresis;
    }

    public void setFalseOutput(TransferFunction output) {
        this.falseOutput.setFunction(output);
    }

    public void setTrueOutput(TransferFunction output) {
        this.trueOutput.setFunction(output);
    }

    @Override
    public TransferFunctionConditional clone() {
        TransferFunctionConditional copy = new TransferFunctionConditional();
        copy.leftInput.setFunction(this.leftInput.getFunction().clone());
        copy.rightInput.setFunction(this.rightInput.getFunction().clone());
        copy.operator = this.operator;
        copy.falseOutput.setFunction(this.falseOutput.getFunction().clone());
        copy.trueOutput.setFunction(this.trueOutput.getFunction().clone());
        return copy;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 2, 3, (byte)30, (CharSequence)"Conditional [Y:N]");
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        CachedBooleanSupplier isBooleanInput = CachedBooleanSupplier.of(dialog::isBooleanInput);
        final Consumer<Boolean> autoToggleOperator = isBool -> {
            Operator newOperator = isBool != false ? Operator.BOOL : Operator.GREATER_EQUAL_THAN;
            boolean found = false;
            for (MapWidget w : dialog.getWidget().getWidgets()) {
                if (!(w instanceof MapWidgetTransferFunctionConditionalOperator)) continue;
                ((MapWidgetTransferFunctionConditionalOperator)w).setOperator(newOperator);
                found = true;
            }
            if (!found) {
                this.setOperator(newOperator);
                dialog.markChanged();
            }
        };
        boolean leftSideIsBoolean = this.leftInput.getFunction().isBooleanOutput(isBooleanInput);
        if (this.leftWasBooleanOnOpen != null && leftSideIsBoolean != this.leftWasBooleanOnOpen && dialog.isPreviousFunction(this.leftInput)) {
            autoToggleOperator.accept(leftSideIsBoolean);
        }
        this.leftWasBooleanOnOpen = leftSideIsBoolean;
        final Runnable focusOperatorWidget = () -> {
            for (MapWidget w : dialog.getWidget().getWidgets()) {
                if (!(w instanceof MapWidgetTransferFunctionConditionalOperator)) continue;
                w.focus();
                break;
            }
        };
        dialog.addLabel(39, 3, (byte)18, "CONDITION");
        dialog.addWidget(new MapWidgetTransferFunctionSingleItem(this, dialog.getHost(), this.leftInput, isBooleanInput){
            final /* synthetic */ TransferFunctionConditional this$0;
            {
                this.this$0 = this$0;
                super(host, function, isBooleanInput);
            }

            @Override
            public void onChanged(TransferFunction.Holder<TransferFunction> function) {
                boolean leftSideIsBoolean = function.getFunction().isBooleanOutput(this.isBooleanInput);
                if (leftSideIsBoolean != this.this$0.leftWasBooleanOnOpen) {
                    autoToggleOperator.accept(leftSideIsBoolean);
                    this.this$0.leftWasBooleanOnOpen = leftSideIsBoolean;
                }
                dialog.markChanged();
            }

            public void onAttached() {
                super.onAttached();
                if (dialog.isPreviousFunction(this.this$0.leftInput)) {
                    this.focus();
                }
            }

            @Override
            public TransferFunction createDefault() {
                return TransferFunction.identity();
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.DOWN && this.isFocused()) {
                    focusOperatorWidget.run();
                } else {
                    super.onKeyPressed(event);
                }
            }
        }).setBounds(5, 9, dialog.getWidth() - 10, 15);
        MapWidgetTransferFunctionConditionalHysteresis hysteresisWidget = dialog.addWidget(new MapWidgetTransferFunctionConditionalHysteresis(this, this.hysteresis){
            final /* synthetic */ TransferFunctionConditional this$0;
            {
                this.this$0 = this$0;
                super(hysteresis);
            }

            @Override
            public void onHysteresisChanged(double hysteresis) {
                this.this$0.setHysteresis(hysteresis);
                dialog.markChanged();
            }
        });
        hysteresisWidget.setBounds(dialog.getWidth() - 55, 26, 50, 13);
        MapWidgetTransferFunctionSingleItem rightInputWidget = dialog.addWidget(new MapWidgetTransferFunctionSingleItem(this, dialog.getHost(), this.rightInput, isBooleanInput){
            final /* synthetic */ TransferFunctionConditional this$0;
            {
                this.this$0 = this$0;
                super(host, function, isBooleanInput);
            }

            @Override
            public void onChanged(TransferFunction.Holder<TransferFunction> function) {
                dialog.markChanged();
            }

            @Override
            public TransferFunction createDefault() {
                return TransferFunction.identity();
            }

            public void onAttached() {
                super.onAttached();
                if (dialog.isPreviousFunction(this.this$0.rightInput)) {
                    this.focus();
                }
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.UP && this.isFocused()) {
                    focusOperatorWidget.run();
                } else {
                    super.onKeyPressed(event);
                }
            }
        });
        rightInputWidget.setBounds(5, 41, dialog.getWidth() - 10, 15);
        final Runnable operatorChangeHandler = () -> {
            hysteresisWidget.setVisible(this.operator != Operator.BOOL);
            rightInputWidget.setVisible(this.operator != Operator.BOOL);
        };
        operatorChangeHandler.run();
        dialog.addWidget(new MapWidgetTransferFunctionConditionalOperator(this, this.operator){
            final /* synthetic */ TransferFunctionConditional this$0;
            {
                this.this$0 = this$0;
                super(operator);
            }

            @Override
            public void onOperatorChanged(Operator operator) {
                this.this$0.operator = operator;
                operatorChangeHandler.run();
                dialog.markChanged();
            }
        }).setBounds(5, 26, 21, 13);
        dialog.addLabel(44, dialog.getHeight() - 44, (byte)18, "RESULT");
        dialog.addLabel(3, dialog.getHeight() - 33, (byte)18, "Y");
        dialog.addWidget(new MapWidgetTransferFunctionSingleItem(this, dialog.getHost(), this.trueOutput, isBooleanInput){
            final /* synthetic */ TransferFunctionConditional this$0;
            {
                this.this$0 = this$0;
                super(host, function, isBooleanInput);
            }

            @Override
            public void onChanged(TransferFunction.Holder<TransferFunction> function) {
                dialog.markChanged();
            }

            public void onAttached() {
                super.onAttached();
                if (dialog.isPreviousFunction(this.this$0.trueOutput)) {
                    this.focus();
                }
            }

            @Override
            public TransferFunction createDefault() {
                return TransferFunction.identity();
            }
        }).setBounds(7, dialog.getHeight() - 38, dialog.getWidth() - 12, 15);
        dialog.addLabel(3, dialog.getHeight() - 16, (byte)18, "N");
        dialog.addWidget(new MapWidgetTransferFunctionSingleItem(this, dialog.getHost(), this.falseOutput, isBooleanInput){
            final /* synthetic */ TransferFunctionConditional this$0;
            {
                this.this$0 = this$0;
                super(host, function, isBooleanInput);
            }

            @Override
            public void onChanged(TransferFunction.Holder<TransferFunction> function) {
                dialog.markChanged();
            }

            public void onAttached() {
                super.onAttached();
                if (dialog.isPreviousFunction(this.this$0.falseOutput)) {
                    this.focus();
                }
            }

            @Override
            public TransferFunction createDefault() {
                return TransferFunction.identity();
            }
        }).setBounds(7, dialog.getHeight() - 21, dialog.getWidth() - 12, 15);
    }

    public static enum Operator {
        EQUAL("==", (l, r) -> l == r, (l, r, h) -> l == r, (l, r, h) -> Math.abs(l - r) > h),
        NOT_EQUAL("!=", (l, r) -> l != r, (l, r, h) -> Math.abs(l - r) > h, (l, r, h) -> l == r),
        GREATER_THAN(">", (l, r) -> l > r, (l, r, h) -> l - r > h, (l, r, h) -> r - l >= h),
        GREATER_EQUAL_THAN(">=", (l, r) -> l >= r, (l, r, h) -> l - r >= h, (l, r, h) -> r - l > h),
        LESSER_THAN("<", (l, r) -> l < r, (l, r, h) -> r - l > h, (l, r, h) -> l - r >= h),
        LESSER_EQUAL_THAN("<=", (l, r) -> l <= r, (l, r, h) -> r - l >= h, (l, r, h) -> l - r > h),
        BOOL("!=0", (l, r) -> l != 0.0, (l, r, h) -> l != 0.0, (l, r, h) -> l == 0.0);

        private final String title;
        private final DoubleComparator comparator;
        private final DoubleHysteresisComparator trueHysteresisComparator;
        private final DoubleHysteresisComparator falseHysteresisComparator;

        private Operator(String title, DoubleComparator comparator, DoubleHysteresisComparator trueHysteresisComparator, DoubleHysteresisComparator falseHysteresisComparator) {
            this.title = title;
            this.comparator = comparator;
            this.trueHysteresisComparator = trueHysteresisComparator;
            this.falseHysteresisComparator = falseHysteresisComparator;
        }

        public String title() {
            return this.title;
        }

        public boolean compare(double left, double right) {
            return this.comparator.compare(left, right);
        }

        public boolean compareWithHysteresis(boolean wasTrue, double left, double right, double hysteresis) {
            if (wasTrue) {
                return !this.falseHysteresisComparator.compare(left, right, hysteresis);
            }
            return this.trueHysteresisComparator.compare(left, right, hysteresis);
        }

        public boolean hasRightHandSide() {
            return this != BOOL;
        }
    }

    @FunctionalInterface
    private static interface DoubleHysteresisComparator {
        public boolean compare(double var1, double var3, double var5);
    }

    @FunctionalInterface
    private static interface DoubleComparator {
        public boolean compare(double var1, double var3);
    }
}

