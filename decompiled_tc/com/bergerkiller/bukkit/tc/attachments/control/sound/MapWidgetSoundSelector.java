/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.PacketUtil
 *  com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCustomSoundPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundStopSoundPacketHandle
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundCategorySelector;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundNameSelector;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundCustomSoundPacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundStopSoundPacketHandle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class MapWidgetSoundSelector
extends MapWidget {
    private final MapWidgetSoundNameSelector name = new MapWidgetSoundNameSelector(){

        @Override
        public void onSoundChanged(ResourceKey<SoundEffect> sound) {
            MapWidgetSoundSelector.this.playSound(this.display);
            MapWidgetSoundSelector.this.onSoundChanged(sound);
        }

        public void onFocus() {
            MapWidgetSoundSelector.this.playSound(this.display);
        }

        public void onBlur() {
            MapWidgetSoundSelector.this.stopSound(this.display);
        }

        public void onDetached() {
            super.onDetached();
            MapWidgetSoundSelector.this.stopSound(this.display);
        }
    };
    private final MapWidgetSoundCategorySelector category = new MapWidgetSoundCategorySelector(){

        @Override
        public void onCategoryChanged(String categoryName) {
            MapWidgetSoundSelector.this.onCategoryChanged(categoryName);
        }
    };
    private Mode mode = Mode.FIRST_PERSPECTIVE;
    private ResourceKey<SoundEffect> lastPreviewedSound = null;
    private String lastPreviewedCategory = "master";

    public abstract void onSoundChanged(ResourceKey<SoundEffect> var1);

    public abstract void onCategoryChanged(String var1);

    public MapWidgetSoundSelector setMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            this.setVisible(mode != Mode.NONE);
            this.invalidate();
        }
        return this;
    }

    public MapWidgetSoundSelector setSoundPath(String soundPath) {
        ResourceKey key = soundPath == null ? null : SoundEffect.fromName((String)soundPath);
        return this.setSound((ResourceKey<SoundEffect>)key);
    }

    public String getSoundPath() {
        return this.name.getSound() == null ? null : this.name.getSound().getPath();
    }

    public MapWidgetSoundSelector setSound(ResourceKey<SoundEffect> sound) {
        this.name.setSound(sound);
        return this;
    }

    public ResourceKey<SoundEffect> getSound() {
        return this.name.getSound();
    }

    public MapWidgetSoundSelector setCategory(String categoryName) {
        this.category.setCategory(categoryName);
        return this;
    }

    public String getCategory() {
        return this.category.getCategory();
    }

    public void onAttached() {
        this.onBoundsChanged();
        this.addWidget(this.name);
        this.addWidget(this.category);
    }

    public void onBoundsChanged() {
        int modeSpace = 7;
        this.name.setBounds(modeSpace, 0, this.getWidth() - this.category.getWidth() - 1 - modeSpace, this.getHeight());
        this.category.setBounds(this.name.getX() + this.name.getWidth() + 1, 0, this.category.getWidth(), this.getHeight());
    }

    public void onDraw() {
        switch (this.mode.ordinal()) {
            case 1: {
                this.draw1p(0, 3, (byte)18);
                break;
            }
            case 2: {
                this.draw3p(0, 3, (byte)18);
                break;
            }
            case 3: {
                this.draw1p(0, 0, (byte)18);
                this.draw3p(0, 6, (byte)18);
            }
        }
    }

    private void draw1p(int x, int y, byte color) {
        this.view.drawPixel(x, y + 1, color);
        this.view.drawLine(x + 1, y, x + 1, y + 4, color);
        this.view.draw(MapFont.TINY, x + 3, y, color, (CharSequence)"p");
    }

    private void draw3p(int x, int y, byte color) {
        this.view.drawPixel(x, y, color);
        this.view.drawPixel(x, y + 2, color);
        this.view.drawPixel(x, y + 4, color);
        this.view.drawLine(x + 1, y, x + 1, y + 4, color);
        this.view.draw(MapFont.TINY, x + 3, y, color, (CharSequence)"p");
    }

    private void playSound(MapDisplay display) {
        this.stopSound(display);
        this.lastPreviewedSound = this.getSound();
        this.lastPreviewedCategory = this.getCategory();
        if (this.lastPreviewedSound != null) {
            for (Player player : display.getOwners()) {
                ClientboundCustomSoundPacketHandle packet = ClientboundCustomSoundPacketHandle.createNew(this.lastPreviewedSound, (String)this.lastPreviewedCategory, (Location)player.getLocation(), (float)1.0f, (float)1.0f);
                PacketUtil.sendPacket((Player)player, (PacketHandle)packet);
            }
        }
    }

    private void stopSound(MapDisplay display) {
        if (Common.hasCapability((String)"Common:Sound:StopSoundPacket")) {
            this.stopSoundImpl(display);
        }
    }

    private void stopSoundImpl(MapDisplay display) {
        if (this.lastPreviewedSound != null) {
            ClientboundStopSoundPacketHandle packet = ClientboundStopSoundPacketHandle.createNew(this.lastPreviewedSound, (String)this.lastPreviewedCategory);
            for (Player player : display.getOwners()) {
                PacketUtil.sendPacket((Player)player, (PacketHandle)packet);
            }
            this.lastPreviewedSound = null;
        }
    }

    public static enum Mode {
        NONE,
        FIRST_PERSPECTIVE,
        THIRD_PERSPECTIVE,
        ALL_PERSPECTIVE;

    }
}

