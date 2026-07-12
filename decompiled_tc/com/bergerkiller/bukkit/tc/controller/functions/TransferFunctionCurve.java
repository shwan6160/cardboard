/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.Arrays;
import java.util.List;

public class TransferFunctionCurve
implements TransferFunction,
Cloneable {
    public static final TransferFunction.Serializer<TransferFunctionCurve> SERIALIZER = new TransferFunction.Serializer<TransferFunctionCurve>(){

        @Override
        public String typeId() {
            return "CURVE_GRAPH";
        }

        @Override
        public String title() {
            return "Curve Graph";
        }

        @Override
        public TransferFunctionCurve createNew(TransferFunctionHost host) {
            return TransferFunctionCurve.empty();
        }

        @Override
        public TransferFunctionCurve load(TransferFunctionHost host, ConfigurationNode config) {
            TransferFunctionCurve curve = TransferFunctionCurve.empty();
            for (String value : config.getList("values", String.class)) {
                int inputEnd;
                int sep = value.indexOf(61);
                if (sep == -1) continue;
                int outputStart = sep + 1;
                for (inputEnd = sep; inputEnd > 0 && value.charAt(inputEnd) == ' '; --inputEnd) {
                }
                while (outputStart < value.length() && value.charAt(outputStart) == ' ') {
                    ++outputStart;
                }
                String inputTxt = value.substring(0, inputEnd).trim();
                String outputTxt = value.substring(outputStart).trim();
                try {
                    double input = Double.parseDouble(inputTxt);
                    double output = Double.parseDouble(outputTxt);
                    curve.add(input, output);
                }
                catch (NumberFormatException numberFormatException) {}
            }
            return curve;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionCurve curve) {
            if (!curve.isEmpty()) {
                List values = config.getList("values", String.class);
                for (int i = 0; i < curve.size(); ++i) {
                    values.add(curve.getInput(i) + " = " + curve.getOutput(i));
                }
            }
        }
    };
    private double[] v;
    private double previousInput = Double.NaN;
    private boolean inputIncreasing = false;

    public static TransferFunctionCurve empty() {
        return new TransferFunctionCurve(new double[0]);
    }

    public static Builder builder() {
        return new Builder();
    }

    private TransferFunctionCurve(double[] v) {
        this.v = v;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    public boolean isEmpty() {
        return this.v.length == 0;
    }

    public int size() {
        return this.v.length >> 1;
    }

    public double getInput(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        return this.v[index];
    }

    public double getOutput(int index) {
        int len = this.v.length >> 1;
        if (index < 0 || index >= len) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        return this.v[index + len];
    }

    public void removeAt(int index) {
        int len = this.v.length >> 1;
        if (index < 0 || index >= len) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        if (len == 1) {
            this.v = new double[0];
            return;
        }
        double[] new_v = new double[len - 1 << 1];
        System.arraycopy(this.v, 0, new_v, 0, index);
        System.arraycopy(this.v, index + 1, new_v, index, len - index - 1);
        System.arraycopy(this.v, len, new_v, len - 1, index);
        System.arraycopy(this.v, len + index + 1, new_v, len + index - 1, len - index - 1);
        this.v = new_v;
    }

    public int add(double input, double output) {
        int len = this.v.length >> 1;
        if (len == 0) {
            this.v = new double[]{input, output};
            return 0;
        }
        int index = Arrays.binarySearch(this.v, 0, len, input);
        if (index < 0) {
            index = -index - 1;
            this.insertAt(index, input, output);
            return index;
        }
        if (index > 0 && this.v[index - 1] == input) {
            --index;
        } else if (index >= len - 1 || this.v[index + 1] != input) {
            this.insertAt(index, input, this.v[index]);
            ++len;
        }
        double dv0 = Math.abs(this.v[index + len] - output);
        double dv1 = Math.abs(this.v[index + len + 1] - output);
        if (dv0 == dv1) {
            if (index > 0) {
                dv0 = Math.abs(this.v[index + len - 1] - output);
            }
            if (index < len - 1) {
                dv1 = Math.abs(this.v[index + len + 1] - output);
            }
        }
        if (dv0 < dv1) {
            this.v[index + len] = output;
            return index;
        }
        this.v[index + len + 1] = output;
        return index + 1;
    }

    private void insertAt(int index, double input, double output) {
        int len = this.v.length >> 1;
        double[] new_v = new double[len + 1 << 1];
        System.arraycopy(this.v, 0, new_v, 0, index);
        new_v[index] = input;
        System.arraycopy(this.v, index, new_v, index + 1, len - index);
        System.arraycopy(this.v, len, new_v, len + 1, index);
        new_v[index + len + 1] = output;
        System.arraycopy(this.v, len + index, new_v, len + index + 2, len - index);
        this.v = new_v;
    }

    public boolean updateAt(int index, double input, double output) {
        double preceding;
        int len = this.v.length >> 1;
        if (index < 0 || index >= len) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        if (input < this.v[index] && index > 0) {
            double preceding2 = this.v[index - 1];
            if (input <= preceding2) {
                input = preceding2;
                if (index > 1 && this.v[index - 2] == preceding2) {
                    return false;
                }
            }
        } else if (input > this.v[index] && index < len - 1 && input >= (preceding = this.v[index + 1])) {
            input = preceding;
            if (index < len - 2 && this.v[index + 2] == preceding) {
                return false;
            }
        }
        this.v[index] = input;
        this.v[index + len] = output;
        return true;
    }

    @Override
    public double map(double input) {
        double previous = this.previousInput;
        this.previousInput = input;
        if (Double.isNaN(previous) || input < previous) {
            this.inputIncreasing = false;
        } else if (input > previous) {
            this.inputIncreasing = true;
        }
        int len = this.v.length >> 1;
        if (len == 0) {
            return input;
        }
        if (len == 1) {
            return this.v[1];
        }
        int index = Arrays.binarySearch(this.v, 0, len, input);
        if (index >= 0) {
            if (index > 0 && this.v[index - 1] == input) {
                if (this.inputIncreasing) {
                    --index;
                }
            } else if (index < len - 1 && this.v[index + 1] == input && !this.inputIncreasing) {
                ++index;
            }
            return this.v[index + len];
        }
        if ((index = -index - 1) == 0) {
            return this.v[len];
        }
        if (index == len) {
            return this.v[2 * len - 1];
        }
        double input_t0 = this.v[index - 1];
        double input_t1 = this.v[index];
        double theta = (input_t1 - input) / (input_t1 - input_t0);
        return this.v[len + index - 1] * (1.0 - theta) + this.v[len + index] * theta;
    }

    @Override
    public boolean isPure() {
        return true;
    }

    public void forEach(EntryConsumer consumer) {
        int len = this.v.length >> 1;
        for (int i = 0; i < len; ++i) {
            consumer.accept(this.v[i], this.v[i + len]);
        }
    }

    @Override
    public TransferFunctionCurve clone() {
        return new TransferFunctionCurve((double[])this.v.clone());
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 0, 3, (byte)30, (CharSequence)"Curve");
    }

    @Override
    public void openDialog(TransferFunction.Dialog dialog) {
        dialog.addWidget(new MapWidgetButton(){

            public void onActivate() {
            }
        }.setText("Click").setBounds(5, 5, 80, 13));
    }

    public static class Builder {
        private final TransferFunctionCurve curve = TransferFunctionCurve.empty();

        public Builder add(double input, double output) {
            this.curve.add(input, output);
            return this;
        }

        public TransferFunctionCurve build() {
            return this.curve;
        }
    }

    @FunctionalInterface
    public static interface EntryConsumer {
        public void accept(double var1, double var3);
    }
}

