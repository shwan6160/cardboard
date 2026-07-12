/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.ui.AnimationFramesImportExport;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetToggleButton;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.AnimationNodeClipboard;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.ConfigureAnimationDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.ConfigureAnimationNodeDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.ConfirmAnimationDeleteDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.MapWidgetAnimationView;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.entity.Player;

public class AnimationMenu
extends MapWidgetMenu
implements AnimationFramesImportExport {
    private PlaybackMode playbackMode = PlaybackMode.ENTIRE_ANIMATION;
    private boolean playForAll = false;
    private final MapWidgetSelectionBox animSelectionBox = new MapWidgetSelectionBox(){

        @Override
        public void onAttached() {
            super.onAttached();
            for (String name : AnimationMenu.this.getAnimRootConfig().getKeys()) {
                this.addItem(name);
            }
            if (!this.getItems().isEmpty()) {
                this.setSelectedItem(this.getItems().get(0));
            }
            this.onSelectedItemChanged();
        }

        @Override
        public void onSelectedItemChanged() {
            boolean menuEnabled = !this.getItems().isEmpty();
            AnimationMenu.this.animView.setAnimation(AnimationMenu.this.loadAnimation());
            AnimationMenu.this.animDelete.setEnabled(menuEnabled);
            AnimationMenu.this.animConfig.setEnabled(menuEnabled);
            AnimationMenu.this.animPlayFwd.setEnabled(menuEnabled);
            AnimationMenu.this.animPlayRev.setEnabled(menuEnabled);
        }

        @Override
        public void onActivate() {
            if (!this.getItems().isEmpty()) {
                AnimationMenu.this.animRenameBox.activate();
            }
        }
    };
    private final MapWidgetAnimationView animView = new MapWidgetAnimationView(){

        @Override
        public void onAnimationChanged(Animation animation) {
            boolean is_name_change;
            if (animation == null) {
                return;
            }
            String old_name = AnimationMenu.this.animSelectionBox.getSelectedItem();
            String new_name = animation.getOptions().getName();
            boolean bl = is_name_change = old_name != null && !old_name.equals(new_name);
            if (is_name_change) {
                AnimationMenu.this.getAnimRootConfig().remove(old_name);
            }
            animation.saveToParentConfig(AnimationMenu.this.getAnimRootConfig());
            if (is_name_change) {
                AnimationMenu.this.animSelectionBox.addItem(new_name);
                AnimationMenu.this.animSelectionBox.setSelectedItem(new_name);
                AnimationMenu.this.animSelectionBox.removeItem(old_name);
            } else if (!AnimationMenu.this.animSelectionBox.getItems().contains(new_name)) {
                AnimationMenu.this.animSelectionBox.addItem(new_name);
                AnimationMenu.this.animSelectionBox.setSelectedItem(new_name);
            }
            AnimationMenu.this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
        }

        @Override
        public void onSelectionActivated() {
            List<AnimationNode> nodes = this.getSelectedNodes();
            if (!nodes.isEmpty()) {
                (this.addWidget((MapWidget)new ConfigureAnimationNodeDialog(nodes){

                    @Override
                    public void onChanged() {
                        this.updateAnimationNodes(this.getNodes());
                    }

                    @Override
                    public void onMultiSelect() {
                        this.startMultiSelect();
                    }

                    @Override
                    public void onReorder() {
                        this.startReordering();
                    }

                    @Override
                    public void onDuplicate() {
                        this.duplicateAnimationNodes();
                    }

                    @Override
                    public void onCopy() {
                        AnimationMenu.this.copyAnimationNodes();
                    }

                    @Override
                    public void onPaste() {
                        AnimationMenu.this.pasteAnimationNodes();
                    }

                    @Override
                    public void onDelete() {
                        AnimationMenu.this.deleteAnimationNodes();
                    }

                    public void onDeactivate() {
                        super.onDeactivate();
                        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
                    }
                })).setAttachment(AnimationMenu.this.attachment);
            }
        }

        @Override
        public void onSelectionChanged() {
            AnimationNode node = this.getSelectedNode();
            if (node != null) {
                AnimationMenu.this.previewAnimationNode(this.getSelectedIndex(), node);
            }
        }

        @Override
        public void onPlayAnimation(boolean reverse, boolean looped) {
            AnimationMenu.this.playAnimation(reverse, looped);
        }

        @Override
        public void onReorder(int offset) {
            AnimationMenu.this.moveAnimationNodes(offset);
        }
    };
    private final MapWidgetSubmitText animNameBox = new MapWidgetSubmitText(){

        public void onAttached() {
            super.onAttached();
            this.setDescription("Enter the animation name");
        }

        public void onAccept(String text) {
            AnimationMenu.this.createAnimation(text);
        }
    };
    private final MapWidgetSubmitText animRenameBox = new MapWidgetSubmitText(){

        public void onAttached() {
            super.onAttached();
            this.setDescription("Enter the new animation name");
        }

        public void onAccept(String text) {
            AnimationMenu.this.renameAnimation(text);
        }
    };
    private final MapWidgetBlinkyButton animDelete = new MapWidgetBlinkyButton(){

        @Override
        public void onClick() {
            AnimationMenu.this.addWidget((MapWidget)new ConfirmAnimationDeleteDialog(){

                @Override
                public void onConfirmDelete() {
                    AnimationMenu.this.deleteAnimation();
                }
            });
        }
    };
    private final MapWidgetBlinkyButton animConfig = new MapWidgetBlinkyButton(){

        @Override
        public void onClick() {
            ConfigureAnimationDialog dialog = new ConfigureAnimationDialog(AnimationMenu.this);
            dialog.setAttachment(AnimationMenu.this.attachment);
            AnimationMenu.this.addWidget((MapWidget)dialog);
        }
    };
    private final MapWidgetBlinkyButton animPlayRev = new MapWidgetBlinkyButton(){

        public void onAttached() {
            super.onAttached();
            this.setRepeatClickEnabled(true);
        }

        @Override
        public void onClick() {
            AnimationMenu.this.playAnimation(true, false);
        }

        @Override
        public void onClickHold() {
            AnimationMenu.this.playAnimation(true, true);
        }

        @Override
        public void onClickHoldRelease() {
            this.onClick();
        }
    };
    private final MapWidgetBlinkyButton animPlayFwd = new MapWidgetBlinkyButton(){

        public void onAttached() {
            super.onAttached();
            this.setRepeatClickEnabled(true);
        }

        @Override
        public void onClick() {
            AnimationMenu.this.playAnimation(false, false);
        }

        @Override
        public void onClickHold() {
            AnimationMenu.this.playAnimation(false, true);
        }

        @Override
        public void onClickHoldRelease() {
            this.onClick();
        }
    };
    private final MapWidgetBlinkyButton animPlayOpt = new MapWidgetBlinkyButton(){

        @Override
        public void onClick() {
            AnimationMenu.this.addWidget((MapWidget)new MapWidgetPlaybackOptionsMenu());
        }
    };

    public AnimationMenu() {
        this.setBounds(5, 15, 118, 108);
        this.setBackgroundColor((byte)126);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.playbackMode = (PlaybackMode)((Object)((AttachmentEditor)this.display).getProperties().get("tcAnimPlaybackMode", PlaybackMode.class));
        if (this.playbackMode == null) {
            this.playbackMode = PlaybackMode.ENTIRE_ANIMATION;
        }
        this.playForAll = (Boolean)((AttachmentEditor)this.display).getProperties().get("tcAnimPlayForAll", (Object)false);
        int top_menu_x = 3;
        int top_menu_y = 3;
        this.addWidget(this.animSelectionBox.setBounds(top_menu_x, top_menu_y, this.getWidth() - 6, 11));
        top_menu_x = 8;
        this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                AnimationMenu.this.animNameBox.activate();
            }
        }.setTooltip("New animation").setIcon("attachments/anim_new.png").setPosition(top_menu_x, top_menu_y += 13));
        this.addWidget(this.animDelete.setTooltip("Delete animation").setIcon("attachments/anim_delete.png").setPosition(top_menu_x += 17, top_menu_y));
        this.addWidget(this.animConfig.setTooltip("Configure").setIcon("attachments/anim_config.png").setPosition(top_menu_x += 17, top_menu_y));
        top_menu_x += 17;
        this.addWidget(this.animPlayRev.setTooltip("Play in reverse").setIcon("attachments/anim_play_rev.png").setPosition(top_menu_x += 5, top_menu_y));
        this.addWidget(this.animPlayFwd.setTooltip("Play forwards").setIcon("attachments/anim_play_fwd.png").setPosition(top_menu_x += 17, top_menu_y));
        this.addWidget(this.animPlayOpt.setTooltip("Playback options").setIcon("attachments/anim_play_opt.png").setPosition(top_menu_x += 17, top_menu_y));
        top_menu_x = 3;
        this.addWidget((MapWidget)this.animNameBox);
        this.addWidget((MapWidget)this.animRenameBox);
        this.addWidget(this.animView.setBounds(top_menu_x, top_menu_y += 18, this.getWidth() - 2 * top_menu_x, 67));
    }

    public void playAnimation(boolean reverse, boolean looped) {
        this.playAnimation(opt -> {
            opt.setSpeed(reverse ? -1.0 : 1.0);
            opt.setLooped(looped);
            opt.setReset(!looped);
        });
    }

    public void playAnimation(Consumer<AnimationOptions> optionFunc) {
        if (this.playForAll) {
            for (MinecartMember<?> member : this.attachment.getMembersUsingAttachment()) {
                AnimationOptions options = new AnimationOptions(this.animSelectionBox.getSelectedItem());
                optionFunc.accept(options);
                this.playbackMode.applyOptions(options, this.animView::getSelectedScene);
                member.playNamedAnimation(options);
            }
        } else {
            for (Attachment liveAttachment : this.attachment.getAttachments()) {
                AnimationOptions options = new AnimationOptions(this.animSelectionBox.getSelectedItem());
                optionFunc.accept(options);
                this.playbackMode.applyOptions(options, this.animView::getSelectedScene);
                liveAttachment.playNamedAnimation(options);
            }
        }
    }

    public void deleteAnimation() {
        String item = this.animSelectionBox.getSelectedItem();
        this.animSelectionBox.removeItem(item);
        this.getAnimRootConfig().remove(item);
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
    }

    public void renameAnimation(String newName) {
        if (this.animSelectionBox.getItems().contains(newName)) {
            return;
        }
        Animation anim = this.getAnimation().clone();
        anim.getOptions().setName(newName);
        this.setAnimation(anim);
    }

    public void createAnimation(String name) {
        if (this.animSelectionBox.getItems().contains(name)) {
            this.animSelectionBox.setSelectedItem(name);
            return;
        }
        Animation newAnimation = new Animation(name, "t=0.25 x=0.0 y=0.0 z=0.0 yaw=0.0 pitch=0.0 roll=0.0", "t=0.25 x=0.0 y=0.0 z=0.0 yaw=90.0 pitch=0.0 roll=0.0", "t=0.25 x=0.0 y=0.0 z=0.0 yaw=180.0 pitch=0.0 roll=0.0", "t=0.25 x=0.0 y=0.0 z=0.0 yaw=270.0 pitch=0.0 roll=0.0");
        newAnimation.saveToParentConfig(this.getAnimRootConfig());
        this.animSelectionBox.addItem(name);
        this.animSelectionBox.setSelectedItem(name);
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
    }

    public void copyAnimationNodes() {
        for (Player player : this.display.getOwners()) {
            if (!this.display.isControlling(player)) continue;
            AnimationNodeClipboard.of(player).store(this.animView.getSelectedNodes());
            if (CommonCapabilities.KEYED_EFFECTS) {
                this.display.playSound(SoundEffect.fromName((String)"block.note_block.hat"));
                continue;
            }
            this.display.playSound(SoundEffect.fromName((String)"note.hat"));
        }
    }

    public void pasteAnimationNodes() {
        for (Player player : this.display.getOwners()) {
            List<AnimationNode> contents;
            if (!this.display.isControlling(player) || (contents = AnimationNodeClipboard.of(player).contents()).isEmpty()) continue;
            this.insertNewAnimationNodes(contents);
            break;
        }
    }

    public void duplicateAnimationNodes() {
        this.animView.duplicateAnimationNodes();
    }

    public void insertNewAnimationNodes(List<AnimationNode> nodes) {
        this.animView.insertNewAnimationNodes(nodes);
    }

    public void deleteAnimationNodes() {
        Animation old_anim = this.animView.getAnimation();
        if (old_anim == null || old_anim.getNodeCount() <= 1) {
            return;
        }
        int start = this.animView.getSelectionStart();
        int end = this.animView.getSelectionEnd();
        int count = end - start + 1;
        ArrayList<AnimationNode> tmp = new ArrayList<AnimationNode>(Arrays.asList(old_anim.getNodeArray()));
        for (int n = 0; n < count && !tmp.isEmpty(); ++n) {
            tmp.remove(start);
        }
        AnimationNode[] new_nodes = (AnimationNode[])LogicUtil.toArray(tmp, AnimationNode.class);
        Animation replacement = new Animation(old_anim.getOptions().getName(), new_nodes);
        replacement.setOptions(old_anim.getOptions().clone());
        this.setAnimation(replacement);
        this.animView.setSelectedItemRange(0);
    }

    public void updateAnimationNodes(List<AnimationNode> nodes) {
        this.animView.updateAnimationNodes(nodes);
    }

    public void updateAnimationNodes(List<AnimationNode> nodes, boolean replaceAllNodes) {
        this.animView.updateAnimationNodes(nodes, replaceAllNodes);
    }

    public void moveAnimationNodes(int offset) {
        int n;
        Animation old_anim = this.animView.getAnimation();
        if (old_anim == null) {
            return;
        }
        int start = this.animView.getSelectionStart();
        int end = this.animView.getSelectionEnd();
        int count = end - start + 1;
        AnimationNode[] old_nodes = old_anim.getNodeArray();
        ArrayList<AnimationNode> tmp = new ArrayList<AnimationNode>(Arrays.asList(old_nodes));
        for (n = 0; n < count; ++n) {
            tmp.remove(start);
        }
        for (n = 0; n < count; ++n) {
            tmp.add(start + offset + n, old_nodes[start + n]);
        }
        AnimationNode[] new_nodes = (AnimationNode[])LogicUtil.toArray(tmp, AnimationNode.class);
        Animation replacement = new Animation(old_anim.getOptions().getName(), new_nodes);
        replacement.setOptions(old_anim.getOptions().clone());
        this.setAnimation(replacement);
        this.animView.setSelectedIndex(this.animView.getSelectedIndex() + offset);
    }

    public void previewAnimationNode(int index, AnimationNode node) {
        for (Attachment liveAttachment : this.attachment.getAttachments()) {
            AnimationNode[] nodes = node.isActive() ? new AnimationNode[]{node} : new AnimationNode[]{new AnimationNode(node.getPosition(), node.getRotationVector(), true, 0.5), new AnimationNode(node.getPosition(), node.getRotationVector(), false, 0.5)};
            Animation anim_preview = new Animation("DUMMY_DO_NOT_USE", nodes);
            anim_preview.getOptions().setReset(true);
            anim_preview.getOptions().setLooped(true);
            liveAttachment.startAnimation(anim_preview);
        }
    }

    public Animation getAnimation() {
        return this.animView.getAnimation();
    }

    public Animation loadAnimation() {
        String item = this.animSelectionBox.getSelectedItem();
        return item == null ? null : Animation.loadFromConfig(this.getAnimRootConfig().getNode(item));
    }

    public void setAnimation(Animation animation) {
        this.animView.setAnimation(animation);
    }

    public ConfigurationNode getAnimRootConfig() {
        return this.attachment.getConfig().getNode("animations");
    }

    public MapWidgetAttachmentNode getAttachment() {
        return this.attachment;
    }

    @Override
    public String getAnimationName() {
        return this.animSelectionBox.getSelectedItem();
    }

    @Override
    public List<AnimationNode> exportAnimationFrames() {
        return this.animView.exportAnimationFrames();
    }

    @Override
    public void importAnimationFrames(List<AnimationNode> frames, boolean insert) {
        this.animView.importAnimationFrames(frames, insert);
    }

    private static enum PlaybackMode {
        ENTIRE_ANIMATION("Entire animation"){

            @Override
            public void applyOptions(AnimationOptions options, Supplier<String> sceneGetter) {
                options.resetScene();
            }
        }
        ,
        SCENE("Current scene"){

            @Override
            public void applyOptions(AnimationOptions options, Supplier<String> sceneGetter) {
                String scene = sceneGetter.get();
                options.setScene(scene, scene);
            }
        }
        ,
        BEGIN_TO_SCENE("Begin-to-scene"){

            @Override
            public void applyOptions(AnimationOptions options, Supplier<String> sceneGetter) {
                options.setScene(null, sceneGetter.get());
            }
        }
        ,
        SCENE_TO_END("Scene-to-end"){

            @Override
            public void applyOptions(AnimationOptions options, Supplier<String> sceneGetter) {
                options.setScene(sceneGetter.get(), null);
            }
        };

        private final String _desc;

        private PlaybackMode(String description) {
            this._desc = description;
        }

        public String description() {
            return this._desc;
        }

        public abstract void applyOptions(AnimationOptions var1, Supplier<String> var2);
    }

    private class MapWidgetPlaybackOptionsMenu
    extends MapWidgetMenu {
        public MapWidgetPlaybackOptionsMenu() {
            this.setBounds(8, 31, 100, 64);
            this.setBackgroundColor((byte)62);
        }

        @Override
        public void onAttached() {
            ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setFont(MapFont.MINECRAFT).setText("Playback mode:").setColor((byte)119).setPosition(6, 5);
            (this.addWidget((MapWidget)new MapWidgetToggleButton<PlaybackMode>(){

                @Override
                public void onSelectionChanged() {
                    AnimationMenu.this.playbackMode = (PlaybackMode)((Object)this.getSelectedOption());
                    ((AttachmentEditor)this.display).getProperties().set("tcAnimPlaybackMode", (Object)AnimationMenu.this.playbackMode);
                    this.display.playSound(SoundEffect.CLICK);
                }
            })).addOptions(PlaybackMode::description, PlaybackMode.class).setSelectedOption(AnimationMenu.this.playbackMode).setBounds(5, 15, 90, 13);
            ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setFont(MapFont.MINECRAFT).setText("Play for:").setColor((byte)119).setPosition(6, 36);
            (this.addWidget((MapWidget)new MapWidgetToggleButton<Boolean>(){

                @Override
                public void onSelectionChanged() {
                    AnimationMenu.this.playForAll = (Boolean)this.getSelectedOption();
                    ((AttachmentEditor)this.display).getProperties().set("tcAnimPlayForAll", (Object)AnimationMenu.this.playForAll);
                    this.display.playSound(SoundEffect.CLICK);
                }
            })).addOptions(opt -> opt != false ? "All attachments" : "This attachment", true, false).setSelectedOption(AnimationMenu.this.playForAll).setBounds(5, 46, 90, 13);
            super.onAttached();
        }
    }
}

