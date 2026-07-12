/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapFont$Alignment
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CartAttachmentText
extends CartAttachment
implements Attachment.TextDisplayAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "TEXT";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/text.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentText();
        }

        @Override
        public void createAppearanceTab(MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            final MapWidgetSubmitText textBox = new MapWidgetSubmitText(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setDescription("Enter text");
                }

                public void onAccept(String text) {
                    attachment.getConfig().set("text", (Object)text);
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", attachment);
                    attachment.resetIcon();
                }
            };
            tab.addWidget((MapWidget)textBox);
            ((MapWidgetText)tab.addWidget((MapWidget)new MapWidgetText().setText("Current Text:"))).setBounds(0, 10, 100, 16);
            (tab.addWidget((MapWidget)new MapWidgetText(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onTick() {
                    this.setText("\"" + (String)attachment.getConfig().get("text", (Object)"") + "\"");
                }
            })).setAlignment(MapFont.Alignment.MIDDLE).setBounds(0, 30, 100, 16);
            (tab.addWidget((MapWidget)new MapWidgetButton(this){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                }

                public void onAttached() {
                    this.setText("Edit Text");
                }

                public void onActivate() {
                    textBox.activate();
                }
            })).setBounds(0, 60, 100, 16);
        }
    };
    private VirtualEntity entity;
    private ChatText text = ChatText.empty();

    @Override
    public void onAttached() {
        super.onAttached();
        this.entity = new VirtualEntity(this.getManager());
        this.entity.setEntityType(EntityType.ARMOR_STAND);
        this.entity.getMetaData().set(EntityHandle.DATA_FLAGS, (Object)32);
        this.entity.getMetaData().set(EntityHandle.DATA_NO_GRAVITY, (Object)true);
        this.entity.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, (Object)true);
        this.entity.setRelativeOffset(0.0, -1.6, 0.0);
    }

    @Override
    public void onLoad(ConfigurationNode config) {
        super.onLoad(config);
        String text = (String)this.getConfig().get("text", (Object)" ");
        if (text.length() == 0) {
            text = " ";
        }
        this.loadText(ChatText.fromMessage((String)text));
    }

    @Override
    public void onTick() {
    }

    @Override
    public ChatText getDisplayedText() {
        return null;
    }

    @Override
    public void setDisplayedText(ChatText text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        this.loadText(text);
        this.getConfig().set("text", (Object)text.getMessage());
    }

    private void loadText(ChatText text) {
        if (!this.text.equals((Object)text)) {
            this.text = text;
            this.entity.getMetaData().set(EntityHandle.DATA_CUSTOM_NAME, (Object)this.text);
            this.entity.syncMetadata();
        }
    }

    @Override
    public boolean containsEntityId(int entityId) {
        return this.entity != null && this.entity.getEntityId() == entityId;
    }

    @Override
    public int getMountEntityId() {
        return this.entity.getEntityId();
    }

    @Override
    public void onTransformChanged(Matrix4x4 transform) {
        this.entity.updatePosition(transform);
    }

    @Override
    public void onMove(boolean absolute) {
        this.entity.syncPosition(absolute);
    }

    @Override
    @Deprecated
    public void makeVisible(Player player) {
        this.makeVisible(this.getManager().asAttachmentViewer(player));
    }

    @Override
    @Deprecated
    public void makeHidden(Player player) {
        this.makeHidden(this.getManager().asAttachmentViewer(player));
    }

    @Override
    public void makeVisible(AttachmentViewer viewer) {
        this.entity.spawn(viewer, new Vector(0.0, 0.0, 0.0));
    }

    @Override
    public void makeHidden(AttachmentViewer viewer) {
        this.entity.destroy(viewer);
    }
}

