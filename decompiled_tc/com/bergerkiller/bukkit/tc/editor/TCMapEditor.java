/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapFont$Alignment
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapSessionMode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  org.bukkit.Effect
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.editor;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapSessionMode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.editor.EditedSign;
import com.bergerkiller.bukkit.tc.editor.MapControl;
import com.bergerkiller.bukkit.tc.editor.RailsTexture;
import com.bergerkiller.bukkit.tc.editor.TCMapControl;
import java.util.ArrayList;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TCMapEditor
extends MapDisplay {
    Player owner;
    MapTexture background;
    EditedSign sign = new EditedSign();
    ArrayList<MapControl> controls = new ArrayList();
    int selectedIndex = 0;
    RailsTexture texture;
    Block railsBlock = null;

    public Block getRailsBlock() {
        return this.railsBlock;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void addControl(MapControl control) {
        this.controls.add(control);
        if (this.controls.size() == 1) {
            control.setSelected(true);
            this.selectedIndex = 0;
        }
        control.bind(this);
    }

    public void onAttached() {
        this.setGlobal(false);
        this.setSessionMode(MapSessionMode.VIEWING);
        this.setReceiveInputWhenHolding(true);
        this.owner = (Player)this.getOwners().get(0);
        this.texture = new RailsTexture();
        ArrayList<Block> signBlocks = new ArrayList<Block>();
        BlockLocation searchLocation = this.getCommonMapItem().getCustomData().getBlockLocation("selected");
        if (searchLocation != null) {
            Block searchBlock;
            this.railsBlock = searchBlock = searchLocation.getBlock();
            if (((Boolean)MaterialUtil.ISSIGN.get(searchBlock)).booleanValue()) {
                signBlocks.add(searchBlock);
                this.railsBlock = Util.getRailsFromSign(searchBlock);
            } else {
                Util.getSignsFromRails(signBlocks, searchBlock);
            }
        }
        if (!signBlocks.isEmpty()) {
            this.sign.load(BlockUtil.getSign((Block)((Block)signBlocks.get(0))));
        }
        this.background = this.loadTexture("com/bergerkiller/bukkit/tc/textures/background.png");
        this.getLayer().setBlendMode(MapBlendMode.NONE);
        this.getLayer().draw((MapCanvas)this.background, 0, 0);
        if (this.sign.isValid()) {
            this.getLayer(1).setBlendMode(MapBlendMode.NONE);
            this.getLayer(1).setAlignment(MapFont.Alignment.MIDDLE);
            this.getLayer(1).draw(MapFont.MINECRAFT, 64, 5, MapColorPalette.getColor((int)255, (int)0, (int)0), (CharSequence)this.sign.getName());
            this.sign.initEditor(this);
        } else {
            this.getLayer(1).setBlendMode(MapBlendMode.NONE);
            this.getLayer(1).setAlignment(MapFont.Alignment.MIDDLE);
            this.getLayer(1).draw(MapFont.MINECRAFT, 64, 5, MapColorPalette.getColor((int)255, (int)0, (int)0), (CharSequence)"No sign selected");
        }
    }

    public void playClick() {
        this.owner.getWorld().playEffect(this.owner.getLocation(), Effect.CLICK2, 0);
    }

    public void onDetached() {
        TCMapControl.updateMapItem(this.owner, this.getMapItem(), false);
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.BACK) {
            TCMapControl.updateMapItem(event.getPlayer(), this.getMapItem(), false);
        }
        if (this.controls.size() > 1) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.controls.get(this.selectedIndex).setSelected(false);
                if (--this.selectedIndex < 0) {
                    this.selectedIndex = this.controls.size() - 1;
                }
                this.controls.get(this.selectedIndex).setSelected(true);
                this.playClick();
            } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.controls.get(this.selectedIndex).setSelected(false);
                if (++this.selectedIndex >= this.controls.size()) {
                    this.selectedIndex = 0;
                }
                this.controls.get(this.selectedIndex).setSelected(true);
                this.playClick();
            }
        }
        if (this.controls.size() > 0 && (event.getKey() == MapPlayerInput.Key.DOWN || event.getKey() == MapPlayerInput.Key.UP || event.getKey() == MapPlayerInput.Key.ENTER)) {
            this.controls.get(this.selectedIndex).onKeyPressed(event);
            this.playClick();
        }
    }

    public void onTick() {
        for (MapControl control : this.controls) {
            control.onTick();
        }
    }
}

