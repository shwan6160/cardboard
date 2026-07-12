/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentAnchor;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetScroller;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSizeBox;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PositionMenu
extends MapWidgetMenu {
    private final MapWidgetScroller scroller = new MapWidgetScroller();

    public PositionMenu() {
        this.setBounds(5, 15, 118, 108);
        this.setPositionAbsolute(true);
        this.setBackgroundColor((byte)30);
        this.scroller.setBounds(5, 5, this.getWidth() - 7, this.getHeight() - 10);
        this.scroller.setScrollPadding(20);
        this.addWidget(this.scroller);
    }

    public int getSliderWidth() {
        return 86;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        Builder builder = new Builder();
        builder.addRow(menu -> new MapWidgetSelectionBox(this, (PositionMenu)((Object)menu)){
            final /* synthetic */ PositionMenu val$menu;
            final /* synthetic */ PositionMenu this$0;
            {
                this.val$menu = positionMenu;
                this.this$0 = this$0;
            }

            @Override
            public void onAttached() {
                super.onAttached();
                AttachmentAnchor current = this.getCurrentAnchor();
                AttachmentType attachmentType = this.val$menu.getMenuAttachmentType();
                boolean foundCurrent = false;
                for (AttachmentAnchor type : AttachmentAnchor.values()) {
                    if (!type.supports(AttachmentControllerMember.class, attachmentType)) continue;
                    this.addItem(type.getName());
                    if (!type.equals(current)) continue;
                    foundCurrent = true;
                }
                if (!foundCurrent) {
                    this.addItem(current.getName());
                }
                this.setSelectedItem(current.getName());
            }

            @Override
            public void onSelectedItemChanged() {
                AttachmentAnchor newAnchor = AttachmentAnchor.find(AttachmentControllerMember.class, this.val$menu.getMenuAttachmentType(), this.getSelectedItem());
                if (!this.getCurrentAnchor().equals(newAnchor)) {
                    this.val$menu.updatePositionConfigValue("anchor", newAnchor.getName());
                }
            }

            private AttachmentAnchor getCurrentAnchor() {
                String name = this.val$menu.getPositionConfigValue("anchor", AttachmentAnchor.DEFAULT.getName());
                return AttachmentAnchor.find(AttachmentControllerMember.class, this.val$menu.getMenuAttachmentType(), name);
            }
        }.setBounds(25, 0, menu.getSliderWidth(), 11)).addLabel(0, 3, "Anchor");
        builder.addPositionSlider("posX", "Pos.X", "Position X-Coordinate").setSpacingAbove(3);
        builder.addPositionSlider("posY", "Pos.Y", "Position Y-Coordinate");
        builder.addPositionSlider("posZ", "Pos.Z", "Position Z-Coordinate");
        builder.addRotationSlider("rotX", "Pitch", "Rotation Pitch");
        builder.addRotationSlider("rotY", "Yaw", "Rotation Yaw");
        builder.addRotationSlider("rotZ", "Roll", "Rotation Roll");
        this.getMenuAttachmentType().createPositionMenu(builder);
        Row prevRow = null;
        int yPos = 0;
        for (Row row : builder.getRows()) {
            if (prevRow != null) {
                yPos += Math.max(prevRow.spacingBelow, row.spacingAbove);
            }
            prevRow = row;
            MapWidget widget = row.creator.apply(this);
            int rowHeight = widget.getY() + widget.getHeight();
            widget.setPosition(widget.getX(), widget.getY() + yPos);
            this.scroller.addContainerWidget(widget);
            for (Row.Label label : row.labels) {
                MapWidgetText textWidget = new MapWidgetText();
                textWidget.setFont(MapFont.TINY);
                textWidget.setText(label.text);
                textWidget.setPosition(label.x, label.y + yPos);
                textWidget.setColor(MapColorPalette.getSpecular((byte)this.labelColor, (float)0.5f));
                this.scroller.addContainerWidget(textWidget);
            }
            yPos += rowHeight;
        }
    }

    protected AttachmentType getMenuAttachmentType() {
        return this.getAttachment().getType();
    }

    public <T> T getPositionConfigValue(String key, T def) {
        ConfigurationNode config = this.getPositionConfig();
        if (config.contains(key)) {
            return (T)config.getOrDefault(key, def);
        }
        return def;
    }

    public void updatePositionConfigValue(String key, Object value) {
        this.updatePositionConfig(config -> config.set(key, value));
    }

    public void updatePositionConfig(Consumer<ConfigurationNode> manipulator) {
        ConfigurationNode config = this.getPositionConfig();
        manipulator.accept(config);
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
    }

    public void updateConfigValue(String key, Object value) {
        this.updateConfig(config -> config.set(key, value));
    }

    public void updateConfig(Consumer<ConfigurationNode> manipulator) {
        ConfigurationNode config = this.getConfig();
        manipulator.accept(config);
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", this.attachment);
    }

    public ConfigurationNode getConfig() {
        return this.attachment.getConfig();
    }

    public final ConfigurationNode getPositionConfig() {
        return this.getConfig().getNode("position");
    }

    public MapWidgetAttachmentNode getAttachment() {
        return this.attachment;
    }

    public static class Builder {
        private final ArrayList<Row> rows = new ArrayList();

        public List<Row> getRows() {
            return this.rows;
        }

        public Row addRow(Function<PositionMenu, MapWidget> creator) {
            Row row = new Row(creator);
            this.rows.add(row);
            return row;
        }

        public Row addRow(int index, Function<PositionMenu, MapWidget> creator) {
            Row row = new Row(creator);
            this.rows.add(index, row);
            return row;
        }

        public Row addSizeBox() {
            return this.addRow(menu -> new MapWidgetSizeBox(this, (PositionMenu)((Object)menu)){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ Builder this$0;
                {
                    this.val$menu = positionMenu;
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    super.onAttached();
                    this.setInitialSize(this.val$menu.getPositionConfigValue("sizeX", 1.0), this.val$menu.getPositionConfigValue("sizeY", 1.0), this.val$menu.getPositionConfigValue("sizeZ", 1.0));
                }

                @Override
                public void onSizeChanged() {
                    this.val$menu.updatePositionConfig(config -> {
                        if (this.x.getValue() == 1.0 && this.y.getValue() == 1.0 && this.z.getValue() == 1.0) {
                            config.remove("sizeX");
                            config.remove("sizeY");
                            config.remove("sizeZ");
                        } else {
                            config.set("sizeX", (Object)this.x.getValue());
                            config.set("sizeY", (Object)this.y.getValue());
                            config.set("sizeZ", (Object)this.z.getValue());
                        }
                    });
                }
            }.setBounds(25, 0, menu.getSliderWidth(), 35)).addLabel(0, 3, "Size X").addLabel(0, 15, "Size Y").addLabel(0, 27, "Size Z").setSpacingAbove(3);
        }

        public Row addPositionSlider(String settingName, String shortName, String propertyName) {
            return this.addPositionSlider(settingName, shortName, propertyName, Double.NaN);
        }

        public Row addPositionSlider(String settingName, String shortName, String propertyName, double defaultValue) {
            return this.addRow(menu -> new MapWidgetNumberBox(this, (PositionMenu)((Object)menu), settingName, propertyName, defaultValue){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ String val$settingName;
                final /* synthetic */ String val$propertyName;
                final /* synthetic */ double val$defaultValue;
                final /* synthetic */ Builder this$0;
                {
                    this.val$menu = positionMenu;
                    this.val$settingName = string;
                    this.val$propertyName = string2;
                    this.val$defaultValue = d;
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setInitialValue(this.val$menu.getPositionConfigValue(this.val$settingName, 0.0));
                }

                @Override
                public String getAcceptedPropertyName() {
                    return this.val$propertyName;
                }

                @Override
                public void onValueChanged() {
                    if (this.getValue() == this.val$defaultValue) {
                        this.val$menu.updatePositionConfig(cfg -> cfg.remove(this.val$settingName));
                    } else {
                        this.val$menu.updatePositionConfigValue(this.val$settingName, this.getValue());
                    }
                }
            }.setBounds(25, 0, menu.getSliderWidth(), 11)).addLabel(0, 3, shortName);
        }

        public Row addRotationSlider(String settingName, String shortName, String propertyName) {
            return this.addRow(menu -> new RotationNumberBox(this, (PositionMenu)((Object)menu), settingName, propertyName){
                final /* synthetic */ PositionMenu val$menu;
                final /* synthetic */ String val$settingName;
                final /* synthetic */ String val$propertyName;
                final /* synthetic */ Builder this$0;
                {
                    this.val$menu = positionMenu;
                    this.val$settingName = string;
                    this.val$propertyName = string2;
                    this.this$0 = this$0;
                }

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setInitialValue(this.val$menu.getPositionConfigValue(this.val$settingName, 0.0));
                }

                @Override
                public String getAcceptedPropertyName() {
                    return this.val$propertyName;
                }

                @Override
                public void onValueChanged() {
                    this.val$menu.updatePositionConfigValue(this.val$settingName, this.getValue());
                }
            }.setBounds(25, 0, menu.getSliderWidth(), 11)).addLabel(0, 3, shortName);
        }
    }

    public static class Row {
        public final Function<PositionMenu, MapWidget> creator;
        public final List<Label> labels = new ArrayList<Label>();
        public int spacingAbove = 1;
        public int spacingBelow = 1;

        public Row(Function<PositionMenu, MapWidget> creator) {
            this.creator = creator;
        }

        public Row addLabel(int x, int y, String text) {
            this.labels.add(new Label(x, y, text));
            return this;
        }

        public Row setSpacingBelow(int spacing) {
            this.spacingBelow = spacing;
            return this;
        }

        public Row setSpacingAbove(int spacing) {
            this.spacingAbove = spacing;
            return this;
        }

        public static class Label {
            public final int x;
            public final int y;
            public final String text;

            public Label(int x, int y, String text) {
                this.x = x;
                this.y = y;
                this.text = text;
            }
        }
    }

    private static class RotationNumberBox
    extends MapWidgetNumberBox {
        public RotationNumberBox() {
            this.setIncrement(0.1);
        }

        @Override
        public void onResetSpecial(MapPlayerInput.Key key) {
            if (key == MapPlayerInput.Key.RIGHT) {
                this.setValue(MathUtil.wrapAngle((double)(this.getValue() + 90.0)));
            } else if (key == MapPlayerInput.Key.LEFT) {
                this.setValue(MathUtil.wrapAngle((double)(this.getValue() - 90.0)));
            } else {
                this.setValue(MathUtil.wrapAngle((double)(this.getValue() + 180.0)));
            }
        }
    }
}

