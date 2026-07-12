/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.attachments.control.sequencer;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerConfigurationMenu;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerEffect;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerTypeSelector;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerMode;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerType;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConstant;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionSingleConfigItem;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MapWidgetSequencerEffectGroup
extends MapWidget {
    protected static final byte BACKGROUND_COLOR = MapColorPalette.getColor((int)54, (int)81, (int)114);
    private static final NumberFormat DURATION_FORMAT = Util.createNumberFormat(1, 4);
    private static final ConfigurationNode EMPTY_CONFIG = new ConfigurationNode();
    private static final int TOP_HEADER_HEIGHT = 8;
    private final MapWidgetSequencerConfigurationMenu menu;
    private final SequencerMode mode;
    private final List<MapWidgetSequencerEffect> effects = new ArrayList<MapWidgetSequencerEffect>();
    private Header header;
    private HeaderTitle headerTitle;
    private HeaderButton configureButton;
    private HeaderButton addEffectButton;
    private EffectLoop.Time duration;

    public MapWidgetSequencerEffectGroup(MapWidgetSequencerConfigurationMenu menu, SequencerMode mode) {
        this.menu = menu;
        this.mode = mode;
        this.duration = EffectLoop.Time.seconds((Double)this.readConfig().getOrDefault("duration", (Object)0.0));
        this.setClipParent(true);
        this.updateBounds();
        for (ConfigurationNode effectConfig : this.readConfig().getNodeList("effects")) {
            this.addEffect(new MapWidgetSequencerEffect(effectConfig));
        }
    }

    protected ConfigurationNode readConfig() {
        ConfigurationNode config = this.menu.getConfig().getNodeIfExists(this.mode.configKey());
        return config == null ? EMPTY_CONFIG : config;
    }

    protected ConfigurationNode writeConfig() {
        return this.menu.getConfig().getNode(this.mode.configKey());
    }

    public MapWidgetSequencerEffectGroup addEffect(MapWidgetSequencerEffect effect) {
        this.effects.add(effect);
        if (!effect.getConfig().hasParent()) {
            this.writeConfig().getNodeList("effects", false).add(effect.getConfig());
        }
        if (!this.duration.isZero()) {
            this.addEffectWidget(effect);
        }
        if (this.effects.size() == 1 && this.header != null) {
            this.header.invalidate();
        }
        this.updateBounds();
        return this;
    }

    public MapWidgetSequencerEffectGroup removeEffect(MapWidgetSequencerEffect effect) {
        int effectIndex = this.effects.indexOf((Object)effect);
        if (effectIndex != -1) {
            boolean wasFocused = effect.isFocused();
            this.effects.remove(effectIndex);
            effect.getConfig().remove();
            if (!this.duration.isZero()) {
                this.removeWidget(effect);
                for (int i = 0; i < this.effects.size(); ++i) {
                    this.effects.get(i).setBounds(0, 8 + 10 * i, this.getWidth(), 11);
                }
                this.updateBounds();
                if (wasFocused) {
                    if (effectIndex >= this.effects.size()) {
                        effectIndex = this.effects.size() - 1;
                    }
                    if (effectIndex != -1) {
                        this.effects.get(effectIndex).focus();
                    } else {
                        this.activate();
                    }
                }
            }
        }
        return this;
    }

    public MapWidgetSequencerEffectGroup setDuration(double duration) {
        return this.setDuration(EffectLoop.Time.seconds(Math.max(0.0, duration)));
    }

    public MapWidgetSequencerEffectGroup setDuration(EffectLoop.Time duration) {
        if (!this.duration.equals(duration)) {
            boolean effectsVisibleChanged = this.duration.isZero() != duration.isZero();
            this.duration = duration;
            this.writeConfig().set("duration", duration.isZero() ? null : Double.valueOf(duration.seconds));
            if (effectsVisibleChanged) {
                if (this.addEffectButton != null) {
                    this.addEffectButton.setEnabled(!duration.isZero());
                }
                for (MapWidgetSequencerEffect effect : this.effects) {
                    this.removeWidget(effect);
                }
                if (!duration.isZero()) {
                    this.effects.forEach(this::addEffectWidget);
                }
                if (this.header != null) {
                    this.header.invalidate();
                }
                this.updateBounds();
            }
            if (this.headerTitle != null) {
                this.headerTitle.invalidate();
            }
        }
        return this;
    }

    public EffectLoop.Time getDuration() {
        return this.duration;
    }

    private void addEffectWidget(MapWidgetSequencerEffect effect) {
        int index = this.effects.indexOf((Object)effect);
        effect.setBounds(0, 8 + 10 * index, this.getWidth(), 11);
        this.addWidget(effect);
    }

    private void updateBounds() {
        int newHeight = 8 + (this.duration.isZero() || this.effects.isEmpty() ? 0 : this.effects.size() * 10 + 1);
        boolean heightChanged = this.getHeight() != newHeight;
        this.setBounds(0, this.getY(), this.menu.getWidth(), newHeight);
        if (heightChanged) {
            this.menu.recalculateContainerSize();
        }
    }

    public void onAttached() {
        this.header = (Header)this.addWidget(new Header());
        this.header.setBounds(0, 0, this.getWidth(), 8);
        this.headerTitle = (HeaderTitle)this.header.addWidget(new HeaderTitle());
        this.headerTitle.setBounds(0, 0, this.getWidth() - 43, 7);
        this.configureButton = (HeaderButton)this.header.addWidget(new HeaderButton(MapWidgetSequencerEffect.HeaderIcon.CONFIGURE){

            @Override
            public void onActivate() {
                this.display.playSound(SoundEffect.PISTON_EXTEND);
                MapWidgetSequencerEffectGroup.this.menu.addWidget((MapWidget)new ConfigureDialog());
            }
        });
        this.configureButton.setPosition(this.getWidth() - 43, 0);
        this.addEffectButton = (HeaderButton)this.header.addWidget(new HeaderButton(MapWidgetSequencerEffect.HeaderIcon.ADD){

            @Override
            public void onActivate() {
                this.display.playSound(SoundEffect.PISTON_EXTEND);
                MapWidgetSequencerEffectGroup.this.menu.addWidget((MapWidget)new MapWidgetAttachmentSelector<Attachment.EffectAttachment>(AttachmentSelector.all(Attachment.EffectAttachment.class).excludingSelf()){

                    @Override
                    public List<String> getAttachmentNames(AttachmentSelector<Attachment.EffectAttachment> allSelector) {
                        return MapWidgetSequencerEffectGroup.this.menu.getEffectNames(allSelector);
                    }

                    @Override
                    public void onSelected(final AttachmentSelector<Attachment.EffectAttachment> effectSelector) {
                        MapWidgetSequencerEffectGroup.this.menu.addWidget((MapWidget)new MapWidgetSequencerTypeSelector(this){
                            final /* synthetic */ 1 this$2;
                            {
                                this.this$2 = this$2;
                            }

                            @Override
                            public void onSelected(SequencerType type) {
                                ((MapWidgetSequencerEffectGroup)this.this$2.MapWidgetSequencerEffectGroup.this).menu.effectSelButtonIndex = 0;
                                this.this$2.MapWidgetSequencerEffectGroup.this.addEffect(new MapWidgetSequencerEffect(type, effectSelector).focusOnActivate());
                            }
                        });
                    }
                }.setTitle("Set Effect to play"));
            }
        });
        this.addEffectButton.setEnabled(!this.duration.isZero());
        this.addEffectButton.setPosition(this.getWidth() - 7, 0);
    }

    private class Header
    extends MapWidget {
        public Header() {
            this.setClipParent(true);
        }

        public void onDraw() {
            if (MapWidgetSequencerEffectGroup.this.duration.isZero() || MapWidgetSequencerEffectGroup.this.effects.isEmpty()) {
                this.view.fillRectangle(2, 0, this.getWidth() - 2, this.getHeight() - 1, BACKGROUND_COLOR);
                this.view.drawLine(1, 1, 1, this.getHeight() - 3, BACKGROUND_COLOR);
            } else {
                this.view.fillRectangle(2, 0, this.getWidth() - 2, this.getHeight(), BACKGROUND_COLOR);
                this.view.drawLine(1, 1, 1, this.getHeight() - 1, BACKGROUND_COLOR);
                this.view.drawPixel(0, this.getHeight() - 1, BACKGROUND_COLOR);
            }
        }
    }

    private static abstract class HeaderButton
    extends MapWidget {
        private final MapWidgetSequencerEffect.HeaderIcon icon;

        public HeaderButton(MapWidgetSequencerEffect.HeaderIcon icon) {
            this.setFocusable(true);
            this.setClipParent(true);
            this.setSize(icon.getWidth(), icon.getHeight());
            this.icon = icon;
        }

        public abstract void onActivate();

        public void onDraw() {
            this.view.draw((MapCanvas)this.icon.getIcon(this.isEnabled(), this.isFocused()), 0, 0);
        }
    }

    private class HeaderTitle
    extends MapWidget {
        public HeaderTitle() {
            this.setClipParent(true);
        }

        public void onDraw() {
            byte textColor = MapWidgetSequencerEffectGroup.this.duration.isZero() ? MapColorPalette.getColor((int)72, (int)108, (int)152) : MapColorPalette.getColor((int)213, (int)201, (int)140);
            this.view.draw((MapCanvas)MapWidgetSequencerEffectGroup.this.mode.icon(), 2, 1, textColor);
            this.view.draw(MapFont.TINY, 11, 1, textColor, (CharSequence)MapWidgetSequencerEffectGroup.this.mode.title());
            this.view.draw(MapFont.TINY, 37, 1, textColor, (CharSequence)(MapWidgetSequencerEffectGroup.this.duration.isZero() ? "[OFF]" : DURATION_FORMAT.format(((MapWidgetSequencerEffectGroup)MapWidgetSequencerEffectGroup.this).duration.seconds) + "s"));
        }
    }

    private class ConfigureDialog
    extends MapWidgetMenu {
        public ConfigureDialog() {
            this.setPositionAbsolute(true);
            this.setBounds(14, 30, 100, 78);
            this.setBackgroundColor(MapColorPalette.getColor((int)72, (int)108, (int)152));
            this.labelColor = (byte)119;
        }

        @Override
        public void onAttached() {
            this.addLabel(5, 6, "Duration (s):");
            (this.addWidget(new MapWidgetNumberBox(){

                @Override
                public void onAttached() {
                    this.setRange(0.0, 100000.0);
                    this.setIncrement(0.01);
                    this.setInitialValue(((MapWidgetSequencerEffectGroup)MapWidgetSequencerEffectGroup.this).duration.seconds);
                    this.setTextOverride(MapWidgetSequencerEffectGroup.this.duration.isZero() ? "Off" : null);
                    super.onAttached();
                }

                @Override
                public void onValueChanged() {
                    MapWidgetSequencerEffectGroup.this.setDuration(this.getValue());
                    this.setTextOverride(this.getValue() > 0.0 ? null : "Off");
                }
            })).setBounds(5, 13, 66, 11);
            this.addLabel(5, 27, "Playback Speed:");
            (this.addWidget(new MapWidgetTransferFunctionSingleConfigItem(MapWidgetSequencerEffectGroup.this.menu.getTransferFunctionHost(), MapWidgetSequencerEffectGroup.this.writeConfig(), "speed", () -> false){

                @Override
                public TransferFunction createDefault() {
                    return TransferFunctionConstant.of(1.0);
                }
            })).setBounds(5, 34, this.getWidth() - 10, 15);
            this.addLabel(5, 53, "Interrupt Play:");
            (this.addWidget((MapWidget)new MapWidgetButton(){

                public void onAttached() {
                    this.updateText();
                    super.onAttached();
                }

                public void onActivate() {
                    MapWidgetSequencerEffectGroup.this.writeConfig().set("interrupt", (Object)((Boolean)MapWidgetSequencerEffectGroup.this.readConfig().getOrDefault("interrupt", (Object)false) == false ? 1 : 0));
                    this.updateText();
                }

                private void updateText() {
                    this.setText((Boolean)MapWidgetSequencerEffectGroup.this.readConfig().getOrDefault("interrupt", (Object)false) != false ? "Yes" : "No");
                }
            })).setBounds(5, 60, 52, 12);
            super.onAttached();
        }
    }
}

