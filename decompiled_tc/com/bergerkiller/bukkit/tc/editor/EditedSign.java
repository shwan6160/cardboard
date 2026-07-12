/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 */
package com.bergerkiller.bukkit.tc.editor;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.SignActionHeader;
import com.bergerkiller.bukkit.tc.SignRedstoneMode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.editor.MapControl;
import com.bergerkiller.bukkit.tc.editor.MapRailsControl;
import com.bergerkiller.bukkit.tc.editor.TCMapEditor;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import java.util.Locale;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class EditedSign {
    private Sign _sign;
    private SignActionHeader _header;

    public void load(Sign sign) {
        this._sign = sign;
        this._header = SignActionHeader.parseFromSign(sign);
    }

    public boolean isValid() {
        return this._sign != null && this._header != null;
    }

    public void save() {
        MinecartMember<?> member;
        this._sign.setLine(0, this._header.toString());
        this._sign.update();
        Block rails = Util.getRailsFromSign(this._sign.getBlock());
        if (rails != null && (member = MinecartMemberStore.getAt(rails)) != null) {
            member.getSignTracker().update();
        }
    }

    public String getName() {
        return "Unknown Sign";
    }

    public void setMode(SignActionMode mode) {
        this._header.setMode(mode);
        this.save();
    }

    public SignActionMode getMode() {
        return this._header.getMode();
    }

    public Direction[] getDirections() {
        return this._header.getDirections();
    }

    public void setDirections(Direction[] directions) {
        this._header.setDirections(directions);
        this.save();
    }

    public void setRedstoneMode(SignRedstoneMode mode) {
        this._header.setRedstoneMode(mode);
        this.save();
    }

    public SignRedstoneMode getRedstoneMode() {
        return this._header.getRedstoneMode();
    }

    public void initEditor(final TCMapEditor editor) {
        editor.addControl(new MapControl(this){
            private final SignRedstoneMode[] modes = new SignRedstoneMode[]{SignRedstoneMode.ON, SignRedstoneMode.OFF, SignRedstoneMode.ALWAYS, SignRedstoneMode.PULSE_ON, SignRedstoneMode.PULSE_OFF, SignRedstoneMode.PULSE_ALWAYS};
            final /* synthetic */ EditedSign this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onInit() {
                this.setLocation(5, 20);
                this.setBackground(this.display.loadTexture("com/bergerkiller/bukkit/tc/textures/redstone/bg.png"));
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.DOWN || event.getKey() == MapPlayerInput.Key.ENTER) {
                    this.this$0.setRedstoneMode((SignRedstoneMode)((Object)EditedSign.nextElement((Object[])this.modes, (Object)this.this$0.getRedstoneMode(), 1)));
                    this.draw();
                } else if (event.getKey() == MapPlayerInput.Key.UP) {
                    this.this$0.setRedstoneMode((SignRedstoneMode)((Object)EditedSign.nextElement((Object[])this.modes, (Object)this.this$0.getRedstoneMode(), -1)));
                    this.draw();
                }
            }

            @Override
            public void onDraw() {
                MapTexture texture = editor.loadTexture("com/bergerkiller/bukkit/tc/textures/redstone/" + this.this$0.getRedstoneMode().name().toLowerCase(Locale.ENGLISH) + ".png");
                this.display.getLayer(2).setBlendMode(MapBlendMode.NONE);
                this.display.getLayer(2).draw((MapCanvas)texture, this.x, this.y);
            }
        });
        editor.addControl(new MapControl(this){
            private final SignActionMode[] modes = new SignActionMode[]{SignActionMode.CART, SignActionMode.TRAIN, SignActionMode.RCTRAIN};
            final /* synthetic */ EditedSign this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onInit() {
                this.setLocation(40, 20);
                this.setBackground(this.display.loadTexture("com/bergerkiller/bukkit/tc/textures/modes/bg.png"));
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.DOWN || event.getKey() == MapPlayerInput.Key.ENTER) {
                    this.this$0.setMode((SignActionMode)((Object)EditedSign.nextElement((Object[])this.modes, (Object)this.this$0.getMode(), 1)));
                    this.draw();
                } else if (event.getKey() == MapPlayerInput.Key.UP) {
                    this.this$0.setMode((SignActionMode)((Object)EditedSign.nextElement((Object[])this.modes, (Object)this.this$0.getMode(), -1)));
                    this.draw();
                }
            }

            @Override
            public void onDraw() {
                MapTexture texture = editor.loadTexture("com/bergerkiller/bukkit/tc/textures/modes/" + this.this$0.getMode().name().toLowerCase(Locale.ENGLISH) + ".png");
                this.display.getLayer(2).setBlendMode(MapBlendMode.NONE);
                this.display.getLayer(2).draw((MapCanvas)texture, this.x, this.y);
            }
        });
        editor.addControl(new MapRailsControl(this){
            final /* synthetic */ EditedSign this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onInit() {
                this.setLocation(80, 20);
                if (editor.getRailsBlock() != null) {
                    for (RailType type : RailType.values()) {
                        if (!type.isRail(editor.getRailsBlock())) continue;
                        this.setRails(type, editor.getRailsBlock());
                        break;
                    }
                }
                super.onInit();
            }
        });
    }

    private static <T> T nextElement(T[] elements, T value, int n) {
        int i;
        for (i = 0; i < elements.length && elements[i] != value; ++i) {
        }
        i += n;
        while (i >= elements.length) {
            i -= elements.length;
        }
        while (i < 0) {
            i += elements.length;
        }
        return elements[i];
    }
}

