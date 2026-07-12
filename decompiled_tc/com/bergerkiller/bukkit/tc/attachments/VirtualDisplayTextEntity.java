/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.bukkit.common.wrappers.DataWatcher$Prototype
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle
 *  com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle$TextDisplayHandle
 *  org.bukkit.Color
 */
package com.bergerkiller.bukkit.tc.attachments;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import org.bukkit.Color;

public class VirtualDisplayTextEntity
extends VirtualDisplayEntity {
    private ChatText text = null;
    private int styleFlags = 0;
    private int backgroundColor = 0x40000000;
    private double opacity = 1.0;
    private byte opacityByte = (byte)-1;
    public static final DataWatcher.Prototype TEXT_DISPLAY_METADATA = BASE_DISPLAY_METADATA.modify().setClientDefault(DisplayHandle.TextDisplayHandle.DATA_TEXT, (Object)ChatText.empty()).setClientDefault(DisplayHandle.TextDisplayHandle.DATA_LINE_WIDTH, (Object)200).setClientDefault(DisplayHandle.TextDisplayHandle.DATA_BACKGROUND_COLOR, (Object)0x40000000).setClientByteDefault(DisplayHandle.TextDisplayHandle.DATA_TEXT_OPACITY, -1).setClientByteDefault(DisplayHandle.TextDisplayHandle.DATA_STYLE_FLAGS, 0).setClientByteDefault(DisplayHandle.DATA_BILLBOARD_RENDER_CONSTRAINTS, 0).create();

    public VirtualDisplayTextEntity(AttachmentManager manager) {
        super(manager, TEXT_DISPLAY_ENTITY_TYPE, TEXT_DISPLAY_METADATA.create());
    }

    public double getOpacity() {
        return this.opacity;
    }

    public void setOpacity(double newOpacity) {
        byte newOpacityByte = (byte)(newOpacity * 255.0);
        if (this.opacityByte != newOpacityByte) {
            this.opacityByte = newOpacityByte;
            this.metadata.set(DisplayHandle.TextDisplayHandle.DATA_TEXT_OPACITY, (Object)newOpacityByte);
        }
    }

    public int getStyleFlags() {
        return this.styleFlags;
    }

    public void setStyleFlags(int newFlags) {
        if (this.styleFlags != newFlags) {
            this.styleFlags = newFlags;
            this.metadata.setByte(DisplayHandle.TextDisplayHandle.DATA_STYLE_FLAGS, newFlags);
        }
    }

    public void updateStyleFlags(int flagChanges, boolean set) {
        this.setStyleFlags(set ? this.styleFlags | flagChanges : this.styleFlags & ~flagChanges);
    }

    public void setBackgroundColor(Color color) {
        this.setBackgroundColor(color.asARGB());
    }

    public void setBackgroundColor(int colorRGB) {
        if (this.backgroundColor != colorRGB) {
            this.backgroundColor = colorRGB;
            this.metadata.set(DisplayHandle.TextDisplayHandle.DATA_BACKGROUND_COLOR, (Object)colorRGB);
        }
    }

    public ChatText getText() {
        return this.text;
    }

    public void setText(ChatText text) {
        if (!LogicUtil.bothNullOrEqual((Object)this.text, (Object)text)) {
            this.text = text;
            this.metadata.set(DisplayHandle.TextDisplayHandle.DATA_TEXT, (Object)text);
            this.syncMeta();
        }
    }
}

