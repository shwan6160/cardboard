/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.events.map.MapStatusEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.particle.PhysicalMemberPreview;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import java.util.Collections;
import java.util.Random;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicalMenu
extends MapWidgetMenu {
    private static final int PREVIEW_OFFSET = 5;
    private static final int PREVIEW_HEIGHT = 10;
    private static final int NUMBERBOX_OFFSET = 27;
    private static final int NUMBERBOX_STEP = 21;
    private static final int NUMBERBOX_HEIGHT = 11;
    private final MapTexture wheelTexture;
    private PhysicalMemberPreview preview;
    private int ticksPreviewVisible = 0;

    public PhysicalMenu() {
        this.setBounds(5, 15, 118, 107);
        this.setBackgroundColor((byte)62);
        this.wheelTexture = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/wheel.png");
    }

    @Override
    public void onAttached() {
        super.onAttached();
        if (this.getAttachment().getEditor().getEditedCart() != null) {
            this.preview = new PhysicalMemberPreview(this.getAttachment().getEditor().getEditedCart(), () -> {
                if (this.ticksPreviewVisible > 0 && this.display != null) {
                    return this.display.getOwners();
                }
                return Collections.emptySet();
            });
        }
        byte lblColor = MapColorPalette.getColor((int)152, (int)89, (int)36);
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setRange(0.0, Double.POSITIVE_INFINITY);
                this.setValue((Double)PhysicalMenu.this.getConfig().get("cartLength", (Object)1.0));
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Cart Length";
            }

            @Override
            public void onResetValue() {
                this.setValue(0.98f);
            }

            @Override
            public void onValueChanged() {
                PhysicalMenu.this.getConfig().set("cartLength", (Object)this.getValue());
                PhysicalMenu.this.onChanged();
            }
        })).setBounds(10, 27, 100, 11);
        ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setColor(lblColor).setText("Cart Length").setPosition(20, 19);
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setRange(-100.0, Math.max(0.1, TCConfig.cartDistanceGapMax));
                this.setInitialValue((Double)PhysicalMenu.this.getConfig().getOrDefault("cartCouplerLength", (Object)(0.5 * TCConfig.cartDistanceGap)));
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Coupler Length";
            }

            @Override
            public void onResetValue() {
                this.setValue(0.5 * TCConfig.cartDistanceGap);
            }

            @Override
            public void onValueChanged() {
                if (this.getValue() == 0.5 * TCConfig.cartDistanceGap) {
                    PhysicalMenu.this.getConfig().remove("cartCouplerLength");
                } else {
                    PhysicalMenu.this.getConfig().set("cartCouplerLength", (Object)this.getValue());
                }
                PhysicalMenu.this.onChanged();
            }
        })).setBounds(10, 48, 100, 11);
        ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setColor(lblColor).setText("Coupler Length").setPosition(20, 40);
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setRange(0.0, Double.POSITIVE_INFINITY);
                this.setValue((Double)PhysicalMenu.this.getConfig().get("wheelDistance", (Object)0.0));
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Wheel Distance";
            }

            @Override
            public void onValueChanged() {
                PhysicalMenu.this.getConfig().set("wheelDistance", (Object)this.getValue());
                PhysicalMenu.this.onChanged();
            }
        })).setBounds(10, 69, 100, 11);
        ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setColor(lblColor).setText("Wheel Distance").setPosition(20, 61);
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setValue((Double)PhysicalMenu.this.getConfig().get("wheelCenter", (Object)0.0));
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Wheel Center Offset";
            }

            @Override
            public void onValueChanged() {
                PhysicalMenu.this.getConfig().set("wheelCenter", (Object)this.getValue());
                PhysicalMenu.this.onChanged();
            }
        })).setBounds(10, 90, 100, 11);
        ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setColor(lblColor).setText("Wheel Offset").setPosition(20, 82);
    }

    public void onDetached() {
        super.onDetached();
        if (this.preview != null) {
            this.preview.hide();
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.preview != null) {
            this.preview.update();
        }
        if (--this.ticksPreviewVisible < 0) {
            this.ticksPreviewVisible = 0;
        }
    }

    private void onChanged() {
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
        this.ticksPreviewVisible = 100;
    }

    public ConfigurationNode getConfig() {
        return this.attachment.getConfig().getNode("physical");
    }

    public MapWidgetAttachmentNode getAttachment() {
        return this.attachment;
    }

    public void onStatusChanged(MapStatusEvent event) {
        if (event.isName("changed")) {
            this.invalidate();
        }
    }

    public void onDraw() {
        super.onDraw();
        double cartLength = (Double)this.getConfig().get("cartLength", (Object)1.0);
        double wheelDistance = (Double)this.getConfig().get("wheelDistance", (Object)0.0);
        double wheelCenter = (Double)this.getConfig().get("wheelCenter", (Object)0.0);
        double cartLengthFactor = 20.0;
        double maxCartLength = 5.0;
        boolean isMaxScale = cartLength > maxCartLength;
        double scaleFactor = cartLengthFactor * (isMaxScale ? maxCartLength / cartLength : 1.0);
        cartLength *= scaleFactor;
        wheelDistance *= scaleFactor;
        wheelCenter *= scaleFactor;
        if (isMaxScale) {
            cartLength = cartLengthFactor * maxCartLength;
        }
        int hull_x = MathUtil.floor((double)(0.5 * (double)this.getWidth() - 0.5 * cartLength));
        int hull_y = 5;
        int hull_w = MathUtil.ceil((double)cartLength);
        int hull_h = 10;
        this.view.drawRectangle(hull_x, hull_y, hull_w, hull_h, (byte)119);
        Random rand = new Random(12345678L);
        for (int px = 1; px < hull_w - 1; ++px) {
            for (int py = 1; py < hull_h - 1; ++py) {
                byte color = (byte)(128 + rand.nextInt(40));
                this.view.drawPixel(hull_x + px, hull_y + py, MapColorPalette.getColor((byte)color, (byte)color, (byte)color));
            }
        }
        int wheel_x1 = MathUtil.floor((double)(0.5 * (double)this.getWidth() - 0.5 * wheelDistance + wheelCenter));
        int wheel_x2 = MathUtil.ceil((double)(0.5 * (double)this.getWidth() + 0.5 * wheelDistance + wheelCenter));
        int wheel_y = hull_y + hull_h - 1;
        this.drawWheel(MathUtil.clamp((int)wheel_x1, (int)hull_x, (int)(hull_x + hull_w - 1)), wheel_y);
        this.drawWheel(MathUtil.clamp((int)wheel_x2, (int)hull_x, (int)(hull_x + hull_w - 1)), wheel_y);
    }

    private final void drawWheel(int x, int y) {
        this.view.draw((MapCanvas)this.wheelTexture, x - MathUtil.floor((double)(0.5 * (double)this.wheelTexture.getWidth())), y - MathUtil.floor((double)(0.5 * (double)this.wheelTexture.getHeight())));
    }
}

