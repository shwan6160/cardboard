/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control.sequencer;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.control.effect.ScheduledEffectLoop;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerConfigurationMenu;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerEffectGroup;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.SequencerType;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionBoolean;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConstant;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionSingleConfigItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.plugin.java.JavaPlugin;

public class MapWidgetSequencerEffect
extends MapWidget {
    private static final byte BG_COLOR_DEFAULT = MapColorPalette.getColor((int)86, (int)88, (int)97);
    private static final byte BG_COLOR_FOCUSED = MapColorPalette.getColor((int)180, (int)177, (int)172);
    private static final byte EFFECT_COLOR_DEFAULT = MapColorPalette.getColor((int)220, (int)220, (int)220);
    private static final byte EFFECT_COLOR_FOCUSED = MapColorPalette.getColor((int)247, (int)233, (int)163);
    public static final MapTexture TEXTURE_ATLAS = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/sequencer_icons.png");
    public static final int HEIGHT = 11;
    private final ConfigurationNode config;
    private final SequencerType type;
    private final List<Button> buttons = new ArrayList<Button>();
    private boolean focusOnActivate = false;

    public MapWidgetSequencerEffect(SequencerType type, AttachmentSelector<Attachment.EffectAttachment> effectSelector) {
        this(type.createConfig(effectSelector));
    }

    public MapWidgetSequencerEffect(final ConfigurationNode config) {
        this.setFocusable(true);
        this.setClipParent(true);
        this.config = config;
        this.type = SequencerType.fromConfig(config);
        this.buttons.add(new Button(Icon.PREVIEW, "Preview", () -> {
            ScheduledEffectLoop effectLoop = this.type.createEffectLoop(this.getConfig().getNode("config"), this.getMenu().createEffectSink(this.getEffectSelector()));
            this.getMenu().getPreviewEffectLoopPlayer().play(effectLoop.asEffectLoop(this.getGroup().getDuration()));
        }));
        this.buttons.add(new Button(this.type.icon(false), this.type.icon(true), "Configure " + this.type.name().toLowerCase(Locale.ENGLISH), () -> {
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            this.type.openConfigurationDialog(new SequencerType.OpenDialogArguments(this.getMenu(), this.getConfig().getNode("config"), this.getGroup().getDuration(), this.getMenu().createEffectSink(this.getEffectSelector())));
        }));
        this.buttons.add(new Button(Icon.EFFECT_NAME, "Effect", () -> {
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            this.getMenu().addWidget((MapWidget)new MapWidgetAttachmentSelector<Attachment.EffectAttachment>(this, this.getEffectSelector()){
                final /* synthetic */ MapWidgetSequencerEffect this$0;
                {
                    this.this$0 = this$0;
                    super(selector);
                }

                @Override
                public List<String> getAttachmentNames(AttachmentSelector<Attachment.EffectAttachment> allSelector) {
                    return this.this$0.getMenu().getEffectNames(allSelector);
                }

                @Override
                public void onSelected(AttachmentSelector<Attachment.EffectAttachment> selection) {
                    selection.writeToConfig(config, "effect");
                    this.this$0.invalidate();
                }
            }.setTitle("Set Effect to play"));
        }));
        this.buttons.add(new Button(Icon.SETTINGS, "Settings", () -> {
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            this.getMenu().addWidget((MapWidget)new ConfigureDialog());
        }));
        this.buttons.add(new Button(Icon.DELETE, "Delete", () -> {
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            this.getMenu().addWidget((MapWidget)new ConfirmEffectDeleteDialog(){

                @Override
                public void onConfirmDelete() {
                    if (MapWidgetSequencerEffect.this.display != null) {
                        MapWidgetSequencerEffect.this.display.playSound(SoundEffect.EXTINGUISH);
                    }
                    MapWidgetSequencerEffect.this.remove();
                }

                @Override
                public void close() {
                    super.close();
                    MapWidgetSequencerEffect.this.focus();
                }
            });
        }));
    }

    public ConfigurationNode getConfig() {
        return this.config;
    }

    public MapWidgetSequencerEffect focusOnActivate() {
        this.focusOnActivate = true;
        return this;
    }

    public void remove() {
        if (this.getParent() instanceof MapWidgetSequencerEffectGroup) {
            ((MapWidgetSequencerEffectGroup)this.getParent()).removeEffect(this);
        }
    }

    public AttachmentSelector<Attachment.EffectAttachment> getEffectSelector() {
        return AttachmentSelector.readFromConfig(this.config, "effect").withType(Attachment.EffectAttachment.class).excludingSelf();
    }

    private MapWidgetSequencerEffectGroup getGroup() {
        for (MapWidget w = this.getParent(); w != null; w = w.getParent()) {
            if (!(w instanceof MapWidgetSequencerEffectGroup)) continue;
            return (MapWidgetSequencerEffectGroup)w;
        }
        throw new IllegalStateException("Effect not added to a effect group widget");
    }

    private MapWidgetSequencerConfigurationMenu getMenu() {
        for (MapWidget w = this.getParent(); w != null; w = w.getParent()) {
            if (!(w instanceof MapWidgetSequencerConfigurationMenu)) continue;
            return (MapWidgetSequencerConfigurationMenu)w;
        }
        throw new IllegalStateException("Effect not added to a effect group list widget");
    }

    private int getSelButtonIndex() {
        return Math.min(this.buttons.size() - 1, this.getMenu().effectSelButtonIndex);
    }

    private void setSelButtonIndex(int newIndex) {
        if (newIndex >= 0 && newIndex < this.buttons.size()) {
            this.getMenu().effectSelButtonIndex = newIndex;
            this.invalidate();
        }
    }

    public void onAttached() {
        super.onAttached();
        if (this.focusOnActivate) {
            this.focusOnActivate = false;
            this.focus();
        }
    }

    public void onDraw() {
        boolean focused = this.isFocused();
        this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
        this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, focused ? BG_COLOR_FOCUSED : BG_COLOR_DEFAULT);
        AttachmentSelector<Attachment.EffectAttachment> selector = this.getEffectSelector();
        String name = selector.nameFilter().orElseGet(() -> selector.strategy() == AttachmentSelector.SearchStrategy.NONE ? "<None>" : "<Any>");
        this.view.getView(1, 1, this.getWidth() - 2, this.getHeight() - 2).draw(MapFont.MINECRAFT, 1, 1, focused ? EFFECT_COLOR_FOCUSED : EFFECT_COLOR_DEFAULT, (CharSequence)name);
        if (focused) {
            int selButtonIndex = this.getSelButtonIndex();
            int x = this.getWidth() - 1;
            for (int i = this.buttons.size() - 1; i >= 0; --i) {
                Button b = this.buttons.get(i);
                this.view.draw((MapCanvas)b.icon(i == selButtonIndex), x -= b.width() + 1, 2);
            }
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (this.isFocused()) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.setSelButtonIndex(this.getSelButtonIndex() - 1);
                return;
            }
            if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.setSelButtonIndex(this.getSelButtonIndex() + 1);
                return;
            }
            if (event.getKey() == MapPlayerInput.Key.ENTER) {
                this.buttons.get((int)this.getSelButtonIndex()).action.run();
                return;
            }
        }
        super.onKeyPressed(event);
    }

    private static class Button {
        public final MapTexture iconDefault;
        public final MapTexture iconFocused;
        public final String title;
        public final Runnable action;

        public Button(Icon icon, String title, Runnable action) {
            this(icon.image(false), icon.image(true), title, action);
        }

        public Button(MapTexture iconDefault, MapTexture iconFocused, String title, Runnable action) {
            this.iconDefault = iconDefault;
            this.iconFocused = iconFocused;
            this.title = title;
            this.action = action;
        }

        public int width() {
            return this.iconDefault.getWidth();
        }

        public int height() {
            return this.iconDefault.getHeight();
        }

        public MapTexture icon(boolean focused) {
            return focused ? this.iconFocused : this.iconDefault;
        }
    }

    public static enum Icon {
        PREVIEW,
        EFFECT_NAME,
        SETTINGS,
        DELETE,
        MIDI,
        SIMPLE;

        private final MapTexture unfocusedImage = TEXTURE_ATLAS.getView(this.ordinal() * 7, 0, 7, 7).clone();
        private final MapTexture focusedImage = TEXTURE_ATLAS.getView(this.ordinal() * 7, 7, 7, 7).clone();

        public int width() {
            return this.unfocusedImage.getWidth();
        }

        public int height() {
            return this.unfocusedImage.getHeight();
        }

        public MapTexture image(boolean focused) {
            return focused ? this.focusedImage : this.unfocusedImage;
        }
    }

    private class ConfigureDialog
    extends MapWidgetMenu {
        public ConfigureDialog() {
            this.setPositionAbsolute(true);
            this.setBounds(14, 21, 100, 95);
            this.setBackgroundColor(MapColorPalette.getColor((int)72, (int)108, (int)152));
            this.labelColor = (byte)119;
        }

        @Override
        public void onAttached() {
            TransferFunctionHost host = MapWidgetSequencerEffect.this.getMenu().getTransferFunctionHost();
            this.addLabel(5, 5, "Active");
            (this.addWidget(new MapWidgetTransferFunctionSingleConfigItem(host, MapWidgetSequencerEffect.this.config, "active", () -> false){

                @Override
                public TransferFunction createDefault() {
                    return TransferFunctionBoolean.TRUE;
                }
            })).setBounds(5, 11, this.getWidth() - 10, 15);
            this.addLabel(5, 28, "Volume");
            (this.addWidget(new MapWidgetTransferFunctionSingleConfigItem(host, MapWidgetSequencerEffect.this.config, "volume", () -> false){

                @Override
                public TransferFunction createDefault() {
                    return TransferFunctionConstant.of(1.0);
                }
            })).setBounds(5, 34, this.getWidth() - 10, 15);
            this.addLabel(5, 51, "Pitch");
            (this.addWidget(new MapWidgetTransferFunctionSingleConfigItem(host, MapWidgetSequencerEffect.this.config, "pitch", () -> false){

                @Override
                public TransferFunction createDefault() {
                    return TransferFunctionConstant.of(1.0);
                }
            })).setBounds(5, 57, this.getWidth() - 10, 15);
            this.addLabel(5, 74, "Stop After (s)");
            ((AutoStopDelayNumberBox)this.addWidget(new AutoStopDelayNumberBox())).setBounds(5, 80, this.getWidth() - 10, 11);
            super.onAttached();
        }
    }

    public static enum HeaderIcon {
        CONFIGURE(0, 35),
        ADD(35, 7),
        SYNC(42, 31),
        ASYNC(73, 31),
        AUTOPLAY(104, 40),
        PLAY(144, 23),
        STOP(167, 23);

        private final MapTexture defaultImage;
        private final MapTexture focusedImage;
        private final MapTexture disabledImage;

        private HeaderIcon(int x, int w) {
            this.defaultImage = TEXTURE_ATLAS.getView(x, 14, w, 7).clone();
            this.focusedImage = TEXTURE_ATLAS.getView(x, 21, w, 7).clone();
            this.disabledImage = TEXTURE_ATLAS.getView(x, 28, w, 7).clone();
        }

        public int getWidth() {
            return this.defaultImage.getWidth();
        }

        public int getHeight() {
            return this.defaultImage.getHeight();
        }

        public MapTexture getIcon(boolean enabled, boolean focused) {
            return enabled ? (focused ? this.focusedImage : this.defaultImage) : this.disabledImage;
        }
    }

    private static class ConfirmEffectDeleteDialog
    extends MapWidgetMenu {
        public ConfirmEffectDeleteDialog() {
            this.setPositionAbsolute(true);
            this.setBounds(15, 36, 98, 58);
            this.setBackgroundColor(MapColorPalette.getColor((int)135, (int)33, (int)33));
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.addWidget(new MapWidgetText().setText("Are you sure you\nwant to delete\nthis effect?").setBounds(5, 5, 80, 30));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                }
            }.setText("No").setBounds(10, 40, 36, 13));
            this.addWidget(new MapWidgetButton(){

                public void onActivate() {
                    this.close();
                    this.onConfirmDelete();
                }
            }.setText("Yes").setBounds(52, 40, 36, 13));
        }

        public void onConfirmDelete() {
        }
    }

    private class AutoStopDelayNumberBox
    extends MapWidgetNumberBox {
        public AutoStopDelayNumberBox() {
            this.setRange(0.0, 3600.0);
        }

        @Override
        public void onAttached() {
            Double value = (Double)MapWidgetSequencerEffect.this.config.getOrDefault("stopAfter", Double.class, null);
            if (value != null) {
                this.setInitialValue(value);
                this.setTextOverride(null);
            } else {
                this.setInitialValue(0.0);
                this.setTextOverride("Not Set");
            }
            super.onAttached();
        }

        @Override
        public void onResetValue() {
            MapWidgetSequencerEffect.this.config.remove("stopAfter");
            this.setTextOverride("Not Set");
        }

        @Override
        public void onValueChanged() {
            MapWidgetSequencerEffect.this.config.set("stopAfter", (Object)this.getValue());
            this.setTextOverride(null);
        }
    }
}

