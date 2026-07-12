/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 */
package com.bergerkiller.bukkit.tc.controller.functions.inputs;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInput;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import com.bergerkiller.bukkit.tc.controller.functions.ui.inputs.MapWidgetInputFilterExpression;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.api.IDoubleProperty;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TransferFunctionInputProperty
extends TransferFunctionInput {
    public static final TransferFunction.Serializer<TransferFunctionInputProperty> SERIALIZER = new TransferFunction.Serializer<TransferFunctionInputProperty>(){

        @Override
        public String typeId() {
            return "INPUT-PROPERTY";
        }

        @Override
        public String title() {
            return "In: Property";
        }

        @Override
        public boolean isInput() {
            return true;
        }

        @Override
        public TransferFunctionInputProperty createNew(TransferFunctionHost host) {
            TransferFunctionInputProperty propertyInput = new TransferFunctionInputProperty(StandardProperties.SPEEDLIMIT);
            propertyInput.updateSource(host);
            return propertyInput;
        }

        @Override
        public TransferFunctionInputProperty load(TransferFunctionHost host, ConfigurationNode config) {
            IProperty<Object> property = host.getTrainCarts().getPropertyRegistry().byListedName().get(config.getOrDefault("property", (Object)""));
            TransferFunctionInputProperty propertyInput = new TransferFunctionInputProperty(property);
            propertyInput.property.load(config);
            propertyInput.updateSource(host);
            return propertyInput;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionInputProperty function) {
            config.set("property", (Object)((TransferFunctionInputProperty)function).property.name);
            if (function.property.exists()) {
                function.property.save(config);
            }
        }
    };
    private ListedProperty<?> property;

    public TransferFunctionInputProperty(IProperty<?> property) {
        this(ListedProperty.of(property));
    }

    private TransferFunctionInputProperty(ListedProperty<?> property) {
        if (property == null) {
            throw new IllegalArgumentException("Listed Property cannot be null");
        }
        this.property = property;
    }

    public IProperty<?> getProperty() {
        return this.property.property;
    }

    public void setProperty(IProperty<?> property) {
        this.property = ListedProperty.of(property);
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public TransferFunctionInput.ReferencedSource createSource(TransferFunctionHost host) {
        CartProperties properties;
        if (this.property.canCreateSource() && (properties = host.getCartProperties()) != null) {
            return this.property.createSource(properties);
        }
        return TransferFunctionInput.ReferencedSource.NONE;
    }

    @Override
    public boolean isBooleanOutput() {
        return this.property.isBooleanOutput();
    }

    @Override
    protected TransferFunctionInput cloneInput() {
        return new TransferFunctionInputProperty(this.property);
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 0, 3, (byte)30, (CharSequence)"Property [input]");
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        super.openDialog(dialog);
        dialog.addWidget(new MapWidgetSelectionBox(this){
            private List<ListedProperty<?>> properties = Collections.emptyList();
            private boolean loading = false;
            final /* synthetic */ TransferFunctionInputProperty this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onAttached() {
                this.properties = TrainCarts.plugin.getPropertyRegistry().byListedName().entrySet().stream().map(e -> ListedProperty.of((String)e.getKey(), (IProperty)e.getValue())).filter(ListedProperty::canCreateSource).sorted().collect(Collectors.toList());
                this.loading = true;
                for (ListedProperty<?> listedProperty : this.properties) {
                    this.addItem(listedProperty.name);
                    if (listedProperty.property != ((TransferFunctionInputProperty)this.this$0).property.property) continue;
                    this.setSelectedIndex(this.getItemCount() - 1);
                }
                super.onAttached();
                this.loading = false;
            }

            @Override
            public void onSelectedItemChanged() {
                if (!this.loading && this.getSelectedIndex() >= 0 && this.getSelectedIndex() < this.properties.size()) {
                    this.this$0.setProperty((IProperty<?>)this.properties.get((int)this.getSelectedIndex()).property);
                    for (MapWidget w : dialog.getWidget().getWidgets()) {
                        if (!(w instanceof PropertyOptionsWidget)) continue;
                        ((PropertyOptionsWidget)w).update();
                        break;
                    }
                    this.this$0.updateSource(dialog.getHost());
                    dialog.markChanged();
                }
            }
        }).setBounds(4, 18, dialog.getWidth() - 8, 11);
        dialog.addWidget(new PropertyOptionsWidget(dialog).setBounds(0, 31, dialog.getWidth(), dialog.getHeight() - 31));
    }

    private static class ListedProperty<P extends IProperty<?>>
    implements Comparable<ListedProperty<?>>,
    Cloneable {
        public final String name;
        public final P property;
        private final BiFunction<CartProperties, ListedProperty<P>, TransferFunctionInput.ReferencedSource> sourceCreator;

        public <LP extends ListedProperty<P>> ListedProperty(String listedName, P property, BiFunction<CartProperties, LP, TransferFunctionInput.ReferencedSource> sourceCreator) {
            this.name = listedName;
            this.property = property;
            this.sourceCreator = sourceCreator;
        }

        public boolean exists() {
            return this.property != null;
        }

        public boolean canCreateSource() {
            return this.sourceCreator != null;
        }

        public TransferFunctionInput.ReferencedSource createSource(CartProperties properties) {
            return this.sourceCreator.apply(properties, this);
        }

        public boolean isBooleanOutput() {
            return this.exists() && this.property.getDefault() instanceof Boolean;
        }

        public void load(ConfigurationNode config) {
        }

        public void save(ConfigurationNode config) {
        }

        public void addWidgets(TransferFunction.Dialog dialog, TransferFunctionInputProperty function) {
        }

        @Override
        public int compareTo(ListedProperty<?> listedProperty) {
            return this.name.compareTo(listedProperty.name);
        }

        public ListedProperty<P> clone() {
            return this;
        }

        public static ListedProperty<?> of(IProperty<?> property) {
            return ListedProperty.of(property == null ? "" : property.getListedName(), property);
        }

        public static ListedProperty<?> of(String name, IProperty<?> property) {
            if (property == null) {
                return new ListedProperty<Object>(name, null, null);
            }
            if (!property.isListed()) {
                return new ListedProperty(name, property, null);
            }
            if (property instanceof IDoubleProperty) {
                return new ListedProperty<IDoubleProperty>(name, (IDoubleProperty)property, PropertySourceDouble::new);
            }
            if (property.getDefault() instanceof Double) {
                return new ListedProperty(name, property, PropertySourceDoubleBoxed::new);
            }
            if (property.getDefault() instanceof Boolean) {
                return new ListedProperty(name, property, PropertySourceBool::new);
            }
            if (property instanceof IStringSetProperty) {
                return new ListedPropertyStringSet(name, (IStringSetProperty)property);
            }
            return new ListedProperty(name, property, null);
        }
    }

    private class PropertyOptionsWidget
    extends MapWidget {
        public final TransferFunction.Dialog dialog;

        public PropertyOptionsWidget(TransferFunction.Dialog dialog) {
            this.dialog = dialog.wrapWidget(this);
        }

        public void onAttached() {
            this.update();
        }

        public void update() {
            if (this.display != null) {
                this.clearWidgets();
                TransferFunctionInputProperty.this.property.addWidgets(this.dialog, TransferFunctionInputProperty.this);
            }
        }
    }

    private static class PropertySourceDouble
    extends TransferFunctionInput.ReferencedSource {
        public final CartProperties properties;
        public final IDoubleProperty property;

        public PropertySourceDouble(CartProperties properties, ListedProperty<IDoubleProperty> property) {
            this.properties = properties;
            this.property = (IDoubleProperty)property.property;
        }

        @Override
        public void onTick() {
            this.value = this.property.getDouble(this.properties);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof PropertySourceDouble && ((PropertySourceDouble)o).property == this.property;
        }
    }

    private static class PropertySourceDoubleBoxed
    extends TransferFunctionInput.ReferencedSource {
        public final CartProperties properties;
        public final IProperty<Double> property;

        public PropertySourceDoubleBoxed(CartProperties properties, ListedProperty<IProperty<Double>> property) {
            this.properties = properties;
            this.property = property.property;
        }

        @Override
        public void onTick() {
            this.value = this.property.get(this.properties);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof PropertySourceDoubleBoxed && ((PropertySourceDoubleBoxed)o).property == this.property;
        }
    }

    private static class PropertySourceBool
    extends TransferFunctionInput.ReferencedSource {
        public final CartProperties properties;
        public final IProperty<Boolean> property;

        public PropertySourceBool(CartProperties properties, ListedProperty<IProperty<Boolean>> property) {
            this.properties = properties;
            this.property = property.property;
        }

        @Override
        public void onTick() {
            this.value = this.property.get(this.properties) != false ? 1.0 : 0.0;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof PropertySourceBool && ((PropertySourceBool)o).property == this.property;
        }
    }

    private static class PropertyStringSet
    extends TransferFunctionInput.ReferencedSource {
        public final CartProperties properties;
        public final IStringSetProperty property;
        public final boolean train;
        public final String expression;
        private Set<String> previousResult = null;

        public PropertyStringSet(CartProperties properties, ListedPropertyStringSet property) {
            this.properties = properties;
            this.property = (IStringSetProperty)property.property;
            this.train = property.train;
            this.expression = property.expression;
        }

        @Override
        public void onTick() {
            IProperties props = this.properties;
            if (this.train && (props = this.properties.getTrainProperties()) == null) {
                this.value = 0.0;
                return;
            }
            Set<String> allValues = props.get(this.property);
            if (this.previousResult != allValues) {
                this.previousResult = allValues;
                this.value = Util.matchText(allValues, this.expression) ? 1.0 : 0.0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PropertyStringSet) {
                PropertyStringSet other = (PropertyStringSet)o;
                return this.property == other.property && this.expression.equals(other.expression) && this.train == other.train;
            }
            return false;
        }
    }

    private static class ListedPropertyStringSet
    extends ListedProperty<IStringSetProperty> {
        public boolean train = false;
        public String expression = "";

        public ListedPropertyStringSet(String listedName, IStringSetProperty property) {
            super(listedName, property, PropertyStringSet::new);
        }

        @Override
        public boolean isBooleanOutput() {
            return true;
        }

        @Override
        public void load(ConfigurationNode config) {
            this.train = (Boolean)config.getOrDefault("ofTrain", (Object)false);
            this.expression = (String)config.getOrDefault("expression", (Object)"");
        }

        @Override
        public void save(ConfigurationNode config) {
            config.set("ofTrain", (Object)this.train);
            config.set("expression", (Object)this.expression);
        }

        @Override
        public void addWidgets(final TransferFunction.Dialog dialog, final TransferFunctionInputProperty function) {
            dialog.addLabel(11, 3, (byte)18, "Check " + ((IStringSetProperty)this.property).getListedName() + " of:");
            dialog.addWidget(new MapWidgetButton(this){
                final /* synthetic */ ListedPropertyStringSet this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.updateText();
                    super.onAttached();
                }

                public void onActivate() {
                    this.this$0.train = !this.this$0.train;
                    function.updateSource(dialog.getHost());
                    dialog.markChanged();
                    this.updateText();
                }

                private void updateText() {
                    this.setText(this.this$0.train ? "TRAIN" : "CART");
                }
            }).setBounds(11, 10, dialog.getWidth() - 22, 12);
            dialog.addLabel(11, 26, (byte)18, "Filter Expression:");
            dialog.addWidget(new MapWidgetInputFilterExpression(this){
                final /* synthetic */ ListedPropertyStringSet this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void onChanged(String expression) {
                    this.this$0.expression = expression;
                    function.updateSource(dialog.getHost());
                    dialog.markChanged();
                }
            }).setExpression(this.expression).setBounds(11, 33, dialog.getWidth() - 22, 12);
        }

        @Override
        public ListedPropertyStringSet clone() {
            ListedPropertyStringSet clone = new ListedPropertyStringSet(this.name, (IStringSetProperty)this.property);
            clone.train = this.train;
            clone.expression = this.expression;
            return clone;
        }
    }
}

