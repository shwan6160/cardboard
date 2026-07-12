/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.general.ModelStorageTypeSelectionDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.models.MapWidgetModelStoreSelect;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CartAttachmentModel
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "MODEL";
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/model.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentModel();
        }

        @Override
        public void createAppearanceTab(final MapWidgetTabView.Tab tab, final MapWidgetAttachmentNode attachment) {
            final TrainCarts traincarts = TrainCarts.plugin;
            ((MapWidgetText)tab.addWidget((MapWidget)new MapWidgetText().setText("Current Model:"))).setBounds(0, 3, 100, 16);
            final MapWidgetModelStoreSelect modelSelector = (MapWidgetModelStoreSelect)tab.addWidget((MapWidget)new MapWidgetModelStoreSelect(this, traincarts){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                    super(traincarts2);
                }

                public void onAttached() {
                    this.setSelectedModel(this.this$0.getModelOf(traincarts, attachment));
                }

                @Override
                public void onSelectedModelChanged(SavedAttachmentModel model) {
                    attachment.getConfig().set("modelName", (Object)(model == null ? null : model.getName()));
                    this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed", attachment);
                    for (MapWidget widget : tab.getWidgets()) {
                        if (!(widget instanceof ModelActionButton)) continue;
                        ((ModelActionButton)widget).updateEnabled();
                    }
                }
            });
            modelSelector.setBounds(0, 13, 100, 13);
            (tab.addWidget((MapWidget)new ModelActionButton(this, traincarts, attachment){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                    super(traincarts2, attachment2);
                }

                public void onActivate() {
                    final 2 selfButton = this;
                    final SavedAttachmentModel model = this.this$0.getModelOf(traincarts, attachment);
                    if (model == null) {
                        this.setEnabled(false);
                        return;
                    }
                    if (!this.checkPerm(model)) {
                        return;
                    }
                    this.display.playSound(SoundEffect.CLICK);
                    tab.addWidget(new ModelStorageTypeSelectionDialog.LoadDialog(this){
                        final /* synthetic */ 2 this$1;
                        {
                            this.this$1 = this$1;
                        }

                        @Override
                        public void onConfigLoaded(ConfigurationNode attachmentConfig) {
                            if (!this.this$1.checkPerm(model)) {
                                return;
                            }
                            try {
                                traincarts.getSavedAttachmentModels().setConfigAsPlayer(model.getName(), attachmentConfig, (CommandSender)this.getPlayerOwner().getOnlinePlayer());
                                modelSelector.setSelectedModel(model);
                            }
                            catch (IllegalNameException e) {
                                Localization.COMMAND_MODEL_CONFIG_INVALID_NAME.message((CommandSender)this.display.getOwners().get(0), new String[]{model.getName()});
                            }
                        }

                        @Override
                        public void close() {
                            super.close();
                            selfButton.focus();
                        }
                    }.setPosition(0, 5));
                }
            })).setText("Load").setBounds(0, 30, 49, 14);
            (tab.addWidget((MapWidget)new ModelActionButton(this, traincarts, attachment){
                final /* synthetic */ 1 this$0;
                {
                    this.this$0 = this$0;
                    super(traincarts2, attachment2);
                }

                public void onActivate() {
                    SavedAttachmentModel model = this.this$0.getModelOf(traincarts, attachment);
                    if (model == null) {
                        this.setEnabled(false);
                    } else if (this.checkPerm(model)) {
                        boolean isNewModel = model.isNone();
                        Player player = (Player)this.display.getOwners().get(0);
                        traincarts.getPlayer(player).editModel(model);
                        if (isNewModel) {
                            Localization.COMMAND_MODEL_CONFIG_EDIT_NEW.message((CommandSender)player, new String[]{model.getName()});
                        } else {
                            Localization.COMMAND_MODEL_CONFIG_EDIT_EXISTING.message((CommandSender)player, new String[]{model.getName()});
                        }
                    }
                }
            })).setText("Edit").setBounds(51, 30, 49, 14);
        }

        private SavedAttachmentModel getModelOf(TrainCarts traincarts, MapWidgetAttachmentNode attachment) {
            String modelName = (String)attachment.getConfig().getOrDefault("modelName", (Object)"");
            if (modelName.trim().isEmpty()) {
                return null;
            }
            return traincarts.getSavedAttachmentModels().getModelOrNone(modelName);
        }

        class ModelActionButton
        extends MapWidgetButton {
            private final TrainCarts traincarts;
            private final MapWidgetAttachmentNode attachment;

            public ModelActionButton(TrainCarts traincarts, MapWidgetAttachmentNode attachment) {
                this.traincarts = traincarts;
                this.attachment = attachment;
            }

            public void updateEnabled() {
                this.setEnabled(this.getModelOf(this.traincarts, this.attachment) != null);
            }

            public void onAttached() {
                this.updateEnabled();
            }

            protected boolean checkPerm(SavedAttachmentModel model) {
                Player editing = (Player)this.display.getOwners().get(0);
                if (model.hasPermission((CommandSender)editing)) {
                    return true;
                }
                Localization.COMMAND_MODEL_CONFIG_CLAIMED.message((CommandSender)editing, new String[]{model.getName()});
                this.display.playSound(SoundEffect.EXTINGUISH);
                return false;
            }
        }
    };

    @Override
    public void makeVisible(Player viewer) {
    }

    @Override
    public void makeHidden(Player viewer) {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onMove(boolean absolute) {
    }
}

