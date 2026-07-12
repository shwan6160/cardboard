/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Hastebin$UploadResult
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus.general;

import com.bergerkiller.bukkit.common.Hastebin;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.commands.Commands;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class ModelStorageTypeSelectionDialog
extends MapWidgetMenu {
    private final boolean load;
    private MapWidgetSubmitText textWidget;
    private MapWidgetButton modelStoreButton;
    private Consumer<String> textAccept = t -> {};

    protected ModelStorageTypeSelectionDialog(boolean load) {
        this.load = load;
        this.playSoundWhenBackClosed = true;
        this.setBounds(9, 17, 100, 69);
        this.setBackgroundColor(MapColorPalette.getColor((int)183, (int)188, (int)79));
    }

    protected TrainCarts getTrainCarts() {
        return (TrainCarts)this.display.getPlugin();
    }

    protected TrainCartsPlayer getPlayerOwner() {
        return this.getTrainCarts().getPlayer((Player)this.display.getOwners().get(0));
    }

    protected void askText(String description, Consumer<String> accept) {
        this.textAccept = accept;
        this.textWidget.setDescription(description);
        this.textWidget.activate();
    }

    public abstract void useClipboard();

    public abstract void usePasteServer();

    public abstract void useModelStore();

    @Override
    public void onAttached() {
        super.onAttached();
        this.textWidget = (MapWidgetSubmitText)this.addWidget((MapWidget)new MapWidgetSubmitText(){

            public void onAccept(String text) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.display.getPlugin(), () -> ModelStorageTypeSelectionDialog.this.textAccept.accept(text));
            }
        });
        this.addWidget(new MapWidgetText().setText(this.load ? "Where to load\nattachments from?" : "Where to save\nattachments to?").setBounds(5, 5, 80, 20));
        this.addWidget(new MapWidgetButton(){

            public void onActivate() {
                ModelStorageTypeSelectionDialog.this.useClipboard();
            }
        }.setText("Clipboard").setBounds(5, 24, 90, 12).setEnabled(!this.load || this.getPlayerOwner().getModelClipboard() != null));
        this.addWidget(new MapWidgetButton(){

            public void onActivate() {
                ModelStorageTypeSelectionDialog.this.usePasteServer();
            }
        }.setText("Paste Server").setBounds(5, 38, 90, 12));
        this.modelStoreButton = (MapWidgetButton)this.addWidget((MapWidget)new MapWidgetButton(){

            public void onActivate() {
                if (Permission.COMMAND_MODEL_CONFIG_LIST.has((CommandSender)this.display.getOwners().get(0))) {
                    ModelStorageTypeSelectionDialog.this.useModelStore();
                } else {
                    this.setEnabled(false);
                }
            }
        });
        this.modelStoreButton.setText("Model Store").setBounds(5, 52, 90, 12);
        this.modelStoreButton.setEnabled(Permission.COMMAND_MODEL_CONFIG_LIST.has((CommandSender)this.display.getOwners().get(0)));
    }

    public static abstract class SaveDialog
    extends ModelStorageTypeSelectionDialog {
        private final ConfigurationNode attachmentConfig;

        protected SaveDialog(ConfigurationNode attachmentConfig) {
            super(false);
            this.attachmentConfig = attachmentConfig.clone();
        }

        public abstract void onExported();

        @Override
        public void useClipboard() {
            this.getPlayerOwner().setModelClipboard(this.attachmentConfig);
            Localization.ATTACHMENTS_SAVE_CLIPBOARD.message((CommandSender)this.display.getOwners().get(0), new String[0]);
            this.close();
            this.onExported();
        }

        @Override
        public void usePasteServer() {
            final Player player = (Player)this.display.getOwners().get(0);
            TCConfig.hastebin.upload(this.attachmentConfig.toString()).thenAccept(new Consumer<Hastebin.UploadResult>(){
                final /* synthetic */ SaveDialog this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void accept(Hastebin.UploadResult t) {
                    if (t.success()) {
                        Localization.ATTACHMENTS_SAVE_PASTE_SERVER.message((CommandSender)player, new String[]{t.url()});
                        this.this$0.close();
                        this.this$0.onExported();
                    } else {
                        player.sendMessage(ChatColor.RED + "Failed to export attachment: " + t.error());
                    }
                }
            });
        }

        @Override
        public void useModelStore() {
            Player player = (Player)this.display.getOwners().get(0);
            this.askText("Enter Model Name", name -> {
                SavedAttachmentModel model = this.getTrainCarts().getSavedAttachmentModels().getModelOrNone((String)name);
                if (model.hasPermission((CommandSender)player)) {
                    try {
                        boolean isNewConfig = model.isNone();
                        this.getTrainCarts().getSavedAttachmentModels().setConfigAsPlayer((String)name, this.attachmentConfig, (CommandSender)player);
                        Localization.ATTACHMENTS_SAVE_MODEL_STORE.message((CommandSender)player, new String[]{name});
                        this.close();
                        this.onExported();
                    }
                    catch (IllegalNameException e) {
                        Localization.COMMAND_INPUT_NAME_INVALID.message((CommandSender)player, new String[]{name});
                    }
                } else {
                    Localization.COMMAND_MODEL_CONFIG_CLAIMED.message((CommandSender)player, new String[]{name});
                }
            });
        }
    }

    public static abstract class LoadDialog
    extends ModelStorageTypeSelectionDialog {
        protected LoadDialog() {
            super(true);
        }

        public abstract void onConfigLoaded(ConfigurationNode var1);

        @Override
        public void useClipboard() {
            if (this.getPlayerOwner().getModelClipboard() != null) {
                Localization.ATTACHMENTS_LOAD_CLIPBOARD.message((CommandSender)this.display.getOwners().get(0), new String[0]);
                this.display.playSound(SoundEffect.CLICK_WOOD);
                this.onConfigLoaded(this.getPlayerOwner().getModelClipboard().clone());
                this.close();
            } else {
                this.display.playSound(SoundEffect.EXTINGUISH);
                this.close();
            }
        }

        @Override
        public void usePasteServer() {
            this.askText("Enter Paste URL", url -> Commands.importModel(this.getTrainCarts(), (CommandSender)this.display.getOwners().get(0), url, config -> {
                Localization.ATTACHMENTS_LOAD_PASTE_SERVER.message((CommandSender)this.display.getOwners().get(0), new String[0]);
                this.display.playSound(SoundEffect.CLICK_WOOD);
                this.onConfigLoaded((ConfigurationNode)config);
                this.close();
            }));
        }

        @Override
        public void useModelStore() {
            this.askText("Enter Model Name", name -> {
                SavedAttachmentModel model = this.getTrainCarts().getSavedAttachmentModels().getModel((String)name);
                if (model != null) {
                    Localization.ATTACHMENTS_LOAD_MODEL_STORE.message((CommandSender)this.display.getOwners().get(0), new String[]{name});
                    this.display.playSound(SoundEffect.CLICK_WOOD);
                    this.onConfigLoaded(model.getConfig().clone());
                    this.close();
                } else {
                    Localization.COMMAND_MODEL_CONFIG_NOTFOUND.message((CommandSender)this.display.getOwners().get(0), new String[]{name});
                    this.display.playSound(SoundEffect.EXTINGUISH);
                }
            });
        }
    }
}

