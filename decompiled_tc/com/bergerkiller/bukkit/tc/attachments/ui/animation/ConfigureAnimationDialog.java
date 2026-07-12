/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.attachments.ui.animation;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationMovementControl;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.AnimationMenu;
import java.util.function.Consumer;

public class ConfigureAnimationDialog
extends MapWidgetMenu {
    private final AnimationMenu menu;

    public ConfigureAnimationDialog(AnimationMenu menu) {
        this.setBackgroundColor((byte)62);
        this.setBounds(14, 18, 88, 80);
        this.menu = menu;
    }

    @Override
    public void onAttached() {
        super.onAttached();
        (this.addWidget(new MapWidgetBlinkyButton(){

            public void onAttached() {
                super.onAttached();
                this.updateIcon();
            }

            @Override
            public void onClick() {
                ConfigureAnimationDialog.this.updateOptions(opt -> opt.setLooped(!opt.isLooped()));
                this.updateIcon();
            }

            private void updateIcon() {
                this.setIcon(ConfigureAnimationDialog.this.getOptions().isLooped() ? "attachments/anim_config_loop_on.png" : "attachments/anim_config_loop_off.png");
                this.setTooltip(ConfigureAnimationDialog.this.getOptions().isLooped() ? "Looped: YES" : "Looped: NO");
            }
        })).setClickSound((ResourceKey<SoundEffect>)SoundEffect.CLICK_WOOD).setPosition(11, 7);
        (this.addWidget(new MapWidgetBlinkyButton(){

            public void onAttached() {
                super.onAttached();
                this.updateIcon();
            }

            @Override
            public void onClick() {
                ConfigureAnimationDialog.this.updateOptions(opt -> opt.setAutoPlay(!opt.isAutoPlay()));
                this.updateIcon();
            }

            private void updateIcon() {
                this.setIcon(ConfigureAnimationDialog.this.getOptions().isAutoPlay() ? "attachments/anim_config_autoplay_on.png" : "attachments/anim_config_autoplay_off.png");
                this.setTooltip(ConfigureAnimationDialog.this.getOptions().isAutoPlay() ? "Autoplay: YES" : "Autoplay: NO");
            }
        })).setClickSound((ResourceKey<SoundEffect>)SoundEffect.CLICK_WOOD).setPosition(36, 7);
        (this.addWidget(new MapWidgetBlinkyButton(){

            public void onAttached() {
                super.onAttached();
                this.updateIcon();
            }

            @Override
            public void onClick() {
                ConfigureAnimationDialog.this.updateOptions(opt -> {
                    AnimationMovementControl[] move = AnimationMovementControl.values();
                    opt.setMovementControl(move[(opt.getMovementControl().ordinal() + 1) % move.length]);
                });
                this.updateIcon();
            }

            private void updateIcon() {
                switch (ConfigureAnimationDialog.this.getOptions().getMovementControl()) {
                    case OFF: {
                        this.setIcon("attachments/anim_config_movecontrol_off.png");
                        this.setTooltip("Movement-Control:\nNO");
                        break;
                    }
                    case REVERSIBLE: {
                        this.setIcon("attachments/anim_config_movecontrol_reversible.png");
                        this.setTooltip("Movement-Control:\nREVERSIBLE");
                        break;
                    }
                    case FORWARD_ONLY: {
                        this.setIcon("attachments/anim_config_movecontrol_forward_only.png");
                        this.setTooltip("Movement-Control:\nFORWARD-ONLY");
                    }
                }
            }
        })).setClickSound((ResourceKey<SoundEffect>)SoundEffect.CLICK_WOOD).setPosition(61, 7);
        byte lblColor = MapColorPalette.getColor((int)152, (int)89, (int)36);
        ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setColor(lblColor).setText("Speed").setPosition(13, 29);
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setValue(ConfigureAnimationDialog.this.getOptions().getSpeed());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Animation Speed";
            }

            @Override
            public void onActivate() {
                this.setValue(1.0);
            }

            @Override
            public void onValueChanged() {
                if (ConfigureAnimationDialog.this.getOptions().getSpeed() != this.getValue()) {
                    ConfigureAnimationDialog.this.updateOptions(opt -> opt.setSpeed(this.getValue()));
                }
            }
        })).setBounds(4, 38, 80, 11);
        ((MapWidgetText)this.addWidget((MapWidget)new MapWidgetText())).setColor(lblColor).setText("Delay").setPosition(13, 54);
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                super.onAttached();
                this.setValue(ConfigureAnimationDialog.this.getOptions().getDelay());
            }

            @Override
            public String getAcceptedPropertyName() {
                return "Animation Delay";
            }

            @Override
            public void onValueChanged() {
                if (ConfigureAnimationDialog.this.getOptions().getDelay() != this.getValue()) {
                    ConfigureAnimationDialog.this.updateOptions(opt -> opt.setDelay(this.getValue()));
                }
            }
        })).setBounds(4, 63, 80, 11);
    }

    private AnimationOptions getOptions() {
        return this.menu.getAnimation().getOptions();
    }

    private void updateOptions(Consumer<AnimationOptions> func) {
        Animation anim = this.menu.getAnimation().clone();
        func.accept(anim.getOptions());
        this.menu.setAnimation(anim);
        this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
        this.menu.playAnimation(opt -> {
            boolean looped = anim.getOptions().isLooped();
            opt.setSpeed(1.0);
            opt.setLooped(looped);
            opt.setReset(!looped);
            opt.setMovementControl(anim.getOptions().getMovementControl());
        });
    }
}

