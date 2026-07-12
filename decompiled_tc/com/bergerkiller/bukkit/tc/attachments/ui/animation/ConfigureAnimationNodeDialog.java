/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.ui.animation;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.AnimationNodeClipboard;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ConfigureAnimationNodeDialog
extends MapWidgetMenu {
    private final AnimationNode _average;
    private List<Node> _nodes;
    private MapWidgetSubmitText sceneMarkerSubmit = null;

    public ConfigureAnimationNodeDialog(List<AnimationNode> nodes) {
        this.setBackgroundColor((byte)30);
        this._average = AnimationNode.average(nodes);
        this._nodes = nodes.stream().map(x$0 -> new Node((AnimationNode)x$0)).collect(Collectors.toList());
    }

    public void onChanged() {
    }

    public void onDuplicate() {
    }

    public void onCopy() {
    }

    public void onPaste() {
    }

    public void onReorder() {
    }

    public void onDelete() {
    }

    public void onMultiSelect() {
    }

    public AnimationNode getAverage() {
        return this._average;
    }

    public List<AnimationNode> getNodes() {
        return this._nodes.stream().map(n -> n.node).collect(Collectors.toList());
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.setBounds(5 - this.parent.getX(), 15 - this.parent.getY(), 105, 88);
        int slider_width = 72;
        int x_offset = 31;
        int y_offset = 4;
        int y_step = 10;
        int mtmpx = x_offset - 25;
        int mtmpx_step = 12;
        final MapWidgetSceneBlinkyButton sceneMarkerButton = (MapWidgetSceneBlinkyButton)this.addWidget(new MapWidgetSceneBlinkyButton());
        sceneMarkerButton.setTooltip("Scene marker").setPosition(mtmpx, y_offset);
        this.sceneMarkerSubmit = (MapWidgetSubmitText)this.addWidget((MapWidget)new MapWidgetSubmitText(this){
            final /* synthetic */ ConfigureAnimationNodeDialog this$0;
            {
                this.this$0 = this$0;
            }

            public void onAttached() {
                super.onAttached();
                this.setDescription("Enter a scene start marker name\nPut empty space to remove");
            }

            public ChatText getTitle() {
                return ChatText.fromMessage((String)"Enter marker name");
            }

            public void onAccept(String text) {
                this.this$0.updateScene(text);
                sceneMarkerButton.updateIcon();
            }
        });
        this.addWidget(new MapWidgetBlinkyButton(){

            public void onAttached() {
                super.onAttached();
                this.updateView();
            }

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.ACTIVE, this.isCurrentlyActive() ? 0.0 : 1.0);
                this.updateView();
            }

            private void updateView() {
                boolean active = this.isCurrentlyActive();
                this.setIcon(active ? "attachments/anim_node_active.png" : "attachments/anim_node_inactive.png");
                this.setTooltip(active ? "Active" : "Inactive");
            }

            private boolean isCurrentlyActive() {
                if (ConfigureAnimationNodeDialog.this._nodes.size() == 1) {
                    return ((Node)((ConfigureAnimationNodeDialog)ConfigureAnimationNodeDialog.this)._nodes.get((int)0)).node.isActive();
                }
                int num_active = 0;
                for (Node n : ConfigureAnimationNodeDialog.this._nodes) {
                    if (!n.node.isActive()) continue;
                    ++num_active;
                }
                return num_active >= ConfigureAnimationNodeDialog.this._nodes.size() >> 1;
            }
        }.setPosition(mtmpx += 12, y_offset));
        (this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.onMultiSelect();
                ConfigureAnimationNodeDialog.this.close();
            }
        })).setTooltip("Multi-select").setIcon("attachments/anim_node_multiselect.png").setPosition(mtmpx += 12, y_offset);
        (this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.onReorder();
                ConfigureAnimationNodeDialog.this.close();
            }
        })).setTooltip("Change order").setIcon("attachments/anim_node_reorder.png").setPosition(mtmpx += 12, y_offset);
        (this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onActivate() {
                this.onClick();
            }

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.onCopy();
                ConfigureAnimationNodeDialog.this.close();
            }
        })).setTooltip("Copy to Clipboard").setIcon("attachments/anim_node_copy.png").setPosition(mtmpx += 12, y_offset);
        (this.addWidget(new MapWidgetBlinkyButton(){

            public void onAttached() {
                super.onAttached();
                boolean hasClipboard = false;
                for (Player player : this.display.getOwners()) {
                    if (!this.display.isControlling(player)) continue;
                    hasClipboard |= AnimationNodeClipboard.hasClipboard(player);
                }
                this.setEnabled(hasClipboard);
            }

            @Override
            public void onActivate() {
                this.onClick();
            }

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.onPaste();
                ConfigureAnimationNodeDialog.this.close();
            }
        })).setTooltip("Paste from Clipboard").setIcon("attachments/anim_node_paste.png").setPosition(mtmpx += 12, y_offset);
        final MapWidget duplicateButton = (this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onActivate() {
                this.onClick();
            }

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.onDuplicate();
                ConfigureAnimationNodeDialog.this.close();
            }
        })).setTooltip("Duplicate").setIcon("attachments/anim_node_duplicate.png").setPosition(mtmpx += 12, y_offset);
        (this.addWidget(new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                ConfigureAnimationNodeDialog.this.onDelete();
                ConfigureAnimationNodeDialog.this.close();
            }
        })).setTooltip("Delete").setIcon("attachments/anim_node_delete.png").setPosition(mtmpx += 12, y_offset);
        (this.addWidget(new MapWidgetNumberBox(this){
            final /* synthetic */ ConfigureAnimationNodeDialog this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onAttached() {
                super.onAttached();
                this.setInitialValue(this.this$0.getAverage().getDuration());
            }

            @Override
            public void onValueChanged() {
                this.this$0.updateNode(ChangeMode.DURATION, this.getValue());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Delta Time";
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.UP) {
                    duplicateButton.focus();
                } else {
                    super.onKeyPressed(event);
                }
            }
        })).setBounds(x_offset, y_offset += 12, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Delta T");
        MapWidget posXWidget = (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setInitialValue(ConfigureAnimationNodeDialog.this.getAverage().getPosition().getX());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Position X-Coordinate";
            }

            @Override
            public void onValueChanged() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.POS_X, this.getValue());
            }
        })).setBounds(x_offset, y_offset += y_step, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Pos.X");
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setInitialValue(ConfigureAnimationNodeDialog.this.getAverage().getPosition().getY());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Position Y-Coordinate";
            }

            @Override
            public void onValueChanged() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.POS_Y, this.getValue());
            }
        })).setBounds(x_offset, y_offset += y_step, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Pos.Y");
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setInitialValue(ConfigureAnimationNodeDialog.this.getAverage().getPosition().getZ());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Position Z-Coordinate";
            }

            @Override
            public void onValueChanged() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.POS_Z, this.getValue());
            }
        })).setBounds(x_offset, y_offset += y_step, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Pos.Z");
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setIncrement(0.1);
                this.setInitialValue(ConfigureAnimationNodeDialog.this.getAverage().getRotationVector().getX());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Rotation Pitch";
            }

            @Override
            public void onValueChanged() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.ROT_X, this.getValue());
            }
        })).setBounds(x_offset, y_offset += y_step, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Pitch");
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setIncrement(0.1);
                this.setInitialValue(ConfigureAnimationNodeDialog.this.getAverage().getRotationVector().getY());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Rotation Yaw";
            }

            @Override
            public void onValueChanged() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.ROT_Y, this.getValue());
            }
        })).setBounds(x_offset, y_offset += y_step, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Yaw");
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setIncrement(0.1);
                this.setInitialValue(ConfigureAnimationNodeDialog.this.getAverage().getRotationVector().getZ());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Rotation Roll";
            }

            @Override
            public void onValueChanged() {
                ConfigureAnimationNodeDialog.this.updateNode(ChangeMode.ROT_Z, this.getValue());
            }
        })).setBounds(x_offset, y_offset += y_step, slider_width, 9);
        this.addLabel(5, y_offset + 3, "Roll");
        y_offset += y_step;
        int initialFocusedIndex = this.attachment.getEditorOption("animNodeSelectedOption", -1);
        if (initialFocusedIndex >= 0 && initialFocusedIndex < this.getWidgetCount()) {
            this.getWidget(initialFocusedIndex).focus();
        } else {
            posXWidget.focus();
        }
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        int index;
        super.onKeyPressed(event);
        if (this.display != null && (index = this.getWidgets().indexOf(this.display.getFocusedWidget())) != -1) {
            this.attachment.setEditorOption("animNodeSelectedOption", -1, index);
        }
    }

    private void updateNode(ChangeMode mode, double new_value) {
        for (Node n : this._nodes) {
            n.update(mode, new_value);
        }
        this.onChanged();
    }

    private void updateScene(String newSceneName) {
        for (int i = 0; i < this._nodes.size(); ++i) {
            if (i == 0) {
                this._nodes.get(i).updateScene(newSceneName);
                continue;
            }
            this._nodes.get(i).updateScene(null);
        }
        this.onChanged();
    }

    private static enum ChangeMode {
        POS_X,
        POS_Y,
        POS_Z,
        ROT_X,
        ROT_Y,
        ROT_Z,
        DURATION,
        ACTIVE;

    }

    private class MapWidgetSceneBlinkyButton
    extends MapWidgetBlinkyButton {
        private MapWidgetSceneBlinkyButton() {
        }

        public void onAttached() {
            this.updateIcon();
        }

        @Override
        public void onClick() {
            ConfigureAnimationNodeDialog.this.sceneMarkerSubmit.activate();
        }

        public void updateIcon() {
            if (!ConfigureAnimationNodeDialog.this._nodes.isEmpty() && ((Node)((ConfigureAnimationNodeDialog)ConfigureAnimationNodeDialog.this)._nodes.get((int)0)).node.hasSceneMarker()) {
                this.setIcon("attachments/anim_node_scene_set.png");
            } else {
                this.setIcon("attachments/anim_node_scene.png");
            }
        }
    }

    private class Node {
        public final AnimationNode original;
        public AnimationNode node;

        public Node(AnimationNode node) {
            this.original = node.clone();
            this.node = node;
        }

        public void updateScene(String newSceneName) {
            this.node = this.node.setSceneMarker(newSceneName);
        }

        public void update(ChangeMode mode, double new_value) {
            Vector pos = this.node.getPosition().clone();
            Vector rot = this.node.getRotationVector().clone();
            boolean active = this.node.isActive();
            double duration = this.node.getDuration();
            if (ConfigureAnimationNodeDialog.this._nodes.size() > 1) {
                Vector opos = this.original.getPosition();
                Vector orot = this.original.getRotationVector();
                Vector apos = ConfigureAnimationNodeDialog.this.getAverage().getPosition();
                Vector arot = ConfigureAnimationNodeDialog.this.getAverage().getRotationVector();
                switch (mode.ordinal()) {
                    case 0: {
                        pos.setX(opos.getX() + new_value - apos.getX());
                        break;
                    }
                    case 1: {
                        pos.setY(opos.getY() + new_value - apos.getY());
                        break;
                    }
                    case 2: {
                        pos.setZ(opos.getZ() + new_value - apos.getZ());
                        break;
                    }
                    case 3: {
                        rot.setX(orot.getX() + new_value - arot.getX());
                        break;
                    }
                    case 4: {
                        rot.setY(orot.getY() + new_value - arot.getY());
                        break;
                    }
                    case 5: {
                        rot.setZ(orot.getZ() + new_value - arot.getZ());
                        break;
                    }
                    case 6: {
                        duration = this.original.getDuration() + new_value - ConfigureAnimationNodeDialog.this.getAverage().getDuration();
                        break;
                    }
                    case 7: {
                        active = new_value != 0.0;
                    }
                }
            } else {
                switch (mode.ordinal()) {
                    case 0: {
                        pos.setX(new_value);
                        break;
                    }
                    case 1: {
                        pos.setY(new_value);
                        break;
                    }
                    case 2: {
                        pos.setZ(new_value);
                        break;
                    }
                    case 3: {
                        rot.setX(new_value);
                        break;
                    }
                    case 4: {
                        rot.setY(new_value);
                        break;
                    }
                    case 5: {
                        rot.setZ(new_value);
                        break;
                    }
                    case 6: {
                        duration = new_value;
                        break;
                    }
                    case 7: {
                        active = new_value != 0.0;
                    }
                }
            }
            this.node = new AnimationNode(pos, rot, active, duration, this.node.getSceneMarker());
        }
    }
}

