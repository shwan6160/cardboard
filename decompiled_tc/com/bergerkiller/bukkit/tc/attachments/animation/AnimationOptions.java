/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.google.common.base.Objects
 */
package com.bergerkiller.bukkit.tc.attachments.animation;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationMovementControl;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.google.common.base.Objects;
import java.util.Locale;

public class AnimationOptions
implements Cloneable {
    private String _name;
    private String _sceneBegin;
    private String _sceneEnd;
    private boolean _hasSceneOption;
    private double _speed;
    private double _delay;
    private boolean _looped;
    private boolean _hasLoopOption;
    private boolean _reset;
    private boolean _queue;
    private AnimationMovementControl _movementControl;
    private boolean _hasMovementControlOption;
    private boolean _autoplay;

    protected AnimationOptions(AnimationOptions source) {
        this._name = source._name;
        this._sceneBegin = source._sceneBegin;
        this._sceneEnd = source._sceneEnd;
        this._hasSceneOption = source._hasSceneOption;
        this._speed = source._speed;
        this._delay = source._delay;
        this._looped = source._looped;
        this._hasLoopOption = source._hasLoopOption;
        this._reset = source._reset;
        this._queue = source._queue;
        this._movementControl = source._movementControl;
        this._hasMovementControlOption = source._hasMovementControlOption;
        this._autoplay = source._autoplay;
    }

    public AnimationOptions() {
        this("");
    }

    public AnimationOptions(String name) {
        this._name = name;
        this._sceneBegin = null;
        this._sceneEnd = null;
        this._hasSceneOption = false;
        this._speed = 1.0;
        this._delay = 0.0;
        this._looped = false;
        this._hasLoopOption = false;
        this._reset = false;
        this._queue = false;
        this._hasMovementControlOption = false;
        this._movementControl = AnimationMovementControl.OFF;
        this._autoplay = false;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public void setScene(String scene) {
        this._sceneBegin = scene;
        this._sceneEnd = scene;
        this._hasSceneOption = true;
    }

    public void setScene(String sceneBegin, String sceneEnd) {
        this._sceneBegin = sceneBegin;
        this._sceneEnd = sceneEnd;
        this._hasSceneOption = true;
    }

    public void resetScene() {
        this._sceneBegin = null;
        this._sceneEnd = null;
        this._hasSceneOption = false;
    }

    public String getSceneBegin() {
        return this._sceneBegin;
    }

    public String getSceneEnd() {
        return this._sceneEnd;
    }

    public boolean isSingleScene() {
        return this._sceneBegin != null && this._sceneBegin.equals(this._sceneEnd);
    }

    public boolean hasSceneOption() {
        return this._hasSceneOption;
    }

    public double getSpeed() {
        return this._speed;
    }

    public void setSpeed(double speed) {
        this._speed = speed;
    }

    public boolean isReversed() {
        return this._speed < 0.0;
    }

    public double getDelay() {
        return this._delay;
    }

    public void setDelay(double delay) {
        this._delay = delay;
    }

    public void setLooped(boolean looped) {
        this._looped = looped;
        this._hasLoopOption = true;
    }

    public void resetLooped() {
        this._hasLoopOption = false;
    }

    public boolean hasLoopOption() {
        return this._hasLoopOption;
    }

    public boolean isLooped() {
        return this._looped;
    }

    public boolean isAutoPlay() {
        return this._autoplay;
    }

    public void setAutoPlay(boolean autoplay) {
        this._autoplay = autoplay;
    }

    public void setReset(boolean reset) {
        this._reset = reset;
    }

    public boolean getReset() {
        return this._reset;
    }

    public void setQueue(boolean queue) {
        this._queue = queue;
    }

    public boolean getQueue() {
        return this._queue;
    }

    public boolean hasMovementControlledOption() {
        return this._hasMovementControlOption;
    }

    public boolean isMovementControlled() {
        return this._movementControl != AnimationMovementControl.OFF;
    }

    public AnimationMovementControl getMovementControl() {
        return this._movementControl;
    }

    public void setMovementControl(AnimationMovementControl control) {
        this._movementControl = control;
        this._hasMovementControlOption = true;
    }

    public void setMovementControlled(boolean controlled) {
        this.setMovementControl(controlled ? AnimationMovementControl.REVERSIBLE : AnimationMovementControl.OFF);
    }

    public void clearMovementControlled() {
        this._movementControl = AnimationMovementControl.OFF;
        this._hasMovementControlOption = false;
    }

    public void apply(AnimationOptions options) {
        this.setDelay(this.getDelay() + this.getSpeed() * options.getSpeed() * options.getDelay());
        this.setSpeed(this.getSpeed() * options.getSpeed());
        if (options.hasLoopOption()) {
            this.setLooped(options.isLooped());
        }
        if (options.hasMovementControlledOption()) {
            this.setMovementControl(options.getMovementControl());
        }
        if (options.isAutoPlay()) {
            this.setAutoPlay(true);
        }
        if (options.hasSceneOption()) {
            this.setScene(options.getSceneBegin(), options.getSceneEnd());
        }
        this.setReset(options.getReset());
        this.setQueue(options.getQueue());
    }

    public void loadFromConfig(ConfigurationNode config) {
        this._speed = config.contains("speed") ? (Double)config.get("speed", (Object)1.0) : 1.0;
        this._delay = config.contains("delay") ? (Double)config.get("delay", (Object)0.0) : 0.0;
        this._hasLoopOption = config.contains("looped");
        this._looped = this._hasLoopOption ? (Boolean)config.get("looped", (Object)false) : false;
        if (config.contains("movementControl")) {
            this.setMovementControl((AnimationMovementControl)((Object)config.get("movementControl", (Object)AnimationMovementControl.OFF)));
        } else if (config.contains("movementControlled")) {
            this.setMovementControlled((Boolean)config.get("movementControlled", (Object)false));
        } else {
            this.clearMovementControlled();
        }
        this._autoplay = config.contains("autoplay") && (Boolean)config.get("autoplay", (Object)false) != false;
    }

    public void saveToConfig(ConfigurationNode config) {
        if (this._speed == 1.0) {
            config.remove("speed");
        } else {
            config.set("speed", (Object)this._speed);
        }
        if (this._delay == 0.0) {
            config.remove("delay");
        } else {
            config.set("delay", (Object)this._delay);
        }
        if (this._hasLoopOption) {
            config.set("looped", (Object)this._looped);
        } else {
            config.remove("looped");
        }
        config.remove("movementControlled");
        if (this._hasMovementControlOption) {
            config.set("movementControl", (Object)this._movementControl);
        } else {
            config.remove("movementControl");
        }
        if (this._autoplay) {
            config.set("autoplay", (Object)true);
        } else {
            config.remove("autoplay");
        }
    }

    public void loadFromSign(SignActionEvent info) {
        String mode_line = info.getLine(1).toLowerCase(Locale.ENGLISH).trim();
        for (String part : mode_line.split(" ")) {
            if (LogicUtil.contains((Object)part, (Object[])new String[]{"noloop", "unlooped", "ul", "nl"})) {
                this.setLooped(false);
                continue;
            }
            if (LogicUtil.contains((Object)part, (Object[])new String[]{"loop", "looped", "l"})) {
                this.setLooped(true);
                continue;
            }
            if (LogicUtil.contains((Object)part, (Object[])new String[]{"reset", "rst", "r"})) {
                this.setReset(true);
                continue;
            }
            if (LogicUtil.contains((Object)part, (Object[])new String[]{"queue", "que", "q"})) {
                this.setQueue(true);
                continue;
            }
            if (!LogicUtil.contains((Object)part, (Object[])new String[]{"move", "mv", "m"})) continue;
            this.setMovementControlled(true);
        }
        String nameAndScenes = info.getLine(2).trim();
        int sceneStart = nameAndScenes.indexOf(91);
        if (sceneStart != -1 && nameAndScenes.endsWith("]")) {
            this.setName(nameAndScenes.substring(0, sceneStart).trim());
            int sceneSplitIdx = nameAndScenes.indexOf(58, sceneStart + 1);
            if (sceneSplitIdx == -1) {
                this.setScene(nameAndScenes.substring(sceneStart + 1, nameAndScenes.length() - 1).trim());
            } else {
                String begin = nameAndScenes.substring(sceneStart + 1, sceneSplitIdx).trim();
                String end = nameAndScenes.substring(sceneSplitIdx + 1, nameAndScenes.length() - 1).trim();
                this.setScene(begin, end);
            }
        } else {
            this.setName(nameAndScenes);
        }
        if (!info.getLine(3).isEmpty()) {
            String[] parts = info.getLine(3).split(" ");
            if (parts.length >= 1) {
                this.setSpeed(ParseUtil.parseDouble((String)parts[0], (double)1.0));
            }
            if (parts.length >= 2) {
                this.setDelay(ParseUtil.parseDouble((String)parts[1], (double)0.0));
            }
        }
    }

    public String getCommandSuccessMessage() {
        String name = this.getName();
        if (this.getSceneBegin() != null || this.getSceneEnd() != null) {
            name = name + " [";
            name = Objects.equal((Object)this.getSceneBegin(), (Object)this.getSceneEnd()) ? name + this.getSceneBegin() : (this.getSceneBegin() == null ? name + ".. > " + this.getSceneEnd() : (this.getSceneEnd() == null ? name + this.getSceneBegin() + " > .." : name + this.getSceneBegin() + " > " + this.getSceneEnd()));
            name = name + "]";
        }
        if (this.hasLoopOption()) {
            name = this.isLooped() ? name + " (looped)" : name + " (not looped)";
        }
        if (this.hasMovementControlledOption()) {
            switch (this.getMovementControl()) {
                case REVERSIBLE: {
                    name = name + " (movement reversible)";
                    break;
                }
                case FORWARD_ONLY: {
                    name = name + " (movement forward-only)";
                    break;
                }
                case OFF: {
                    name = name + " (not movement controlled)";
                }
            }
        }
        if (this._reset) {
            name = name + " (reset)";
        } else if (this._queue) {
            name = name + " (queue)";
        }
        return Localization.COMMAND_ANIMATE_SUCCESS.get(name, Double.toString(this.getSpeed()), Double.toString(this.getDelay()));
    }

    public String getCommandFailureMessage() {
        return Localization.COMMAND_ANIMATE_FAILURE.get(this.getName());
    }

    public AnimationOptions clone() {
        return new AnimationOptions(this);
    }
}

