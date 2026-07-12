/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.SignRedstoneMode;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirection;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import java.util.Locale;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class SignActionHeader {
    private boolean is_converted = false;
    private boolean is_empty = false;
    private String rc_name = "";
    private SignRedstoneMode redstoneMode = SignRedstoneMode.ON;
    private SignActionMode mode = SignActionMode.NONE;
    private String directions_str = null;
    private String modeText = "";
    private RailPiece rail_enter_dirs_rail = null;
    private BlockFace rail_enter_dirs_fwd = null;
    private RailEnterDirection[] rail_enter_dirs = null;

    public boolean isValid() {
        return this.mode != SignActionMode.NONE;
    }

    @Deprecated
    public boolean isLegacyConverted() {
        return this.is_converted;
    }

    public boolean isEmpty() {
        return this.is_empty;
    }

    public boolean isInverted() {
        return this.redstoneMode.isInverted();
    }

    public boolean isAlwaysOn() {
        return this.redstoneMode == SignRedstoneMode.ALWAYS;
    }

    public boolean isAlwaysOff() {
        return this.redstoneMode == SignRedstoneMode.NEVER;
    }

    public boolean onPowerRising() {
        return this.redstoneMode.isRisingPulse();
    }

    public boolean onPowerFalling() {
        return this.redstoneMode.isFallingPulse();
    }

    public SignRedstoneMode getRedstoneMode() {
        return this.redstoneMode;
    }

    public void setRedstoneMode(SignRedstoneMode mode) {
        this.redstoneMode = mode;
    }

    public SignActionMode getMode() {
        return this.mode;
    }

    public String getModeText() {
        return this.modeText;
    }

    public void setMode(SignActionMode mode) {
        this.mode = mode;
    }

    public String getRemoteName() {
        return this.rc_name;
    }

    public void setRemoteName(String name) {
        this.rc_name = name;
    }

    public boolean isMode(SignActionMode mode) {
        return this.mode == mode;
    }

    public boolean isTrain() {
        return this.mode == SignActionMode.TRAIN;
    }

    public boolean isCart() {
        return this.mode == SignActionMode.CART;
    }

    public boolean isRC() {
        return this.mode == SignActionMode.RCTRAIN;
    }

    @Deprecated
    public boolean hasDirections() {
        return this.directions_str != null;
    }

    @Deprecated
    public Direction[] getDirections() {
        return this.directions_str == null ? null : Direction.parseAll(this.directions_str);
    }

    public boolean hasEnterDirections() {
        return this.directions_str != null;
    }

    public RailEnterDirection[] getEnterDirections(RailPiece rail, BlockFace forwardDirection) {
        if (this.directions_str == null) {
            return null;
        }
        if (this.rail_enter_dirs_rail == rail && this.rail_enter_dirs_fwd == forwardDirection) {
            return this.rail_enter_dirs;
        }
        this.rail_enter_dirs_rail = rail;
        this.rail_enter_dirs_fwd = forwardDirection;
        this.rail_enter_dirs = RailEnterDirection.parseAll(rail, forwardDirection, this.directions_str);
        return this.rail_enter_dirs;
    }

    public void setEnterDirectionsText(String text) {
        this.directions_str = text;
        this.rail_enter_dirs_rail = null;
        this.rail_enter_dirs_fwd = null;
        this.rail_enter_dirs = null;
    }

    public void setEnterDirections(RailEnterDirection[] directions) {
        if (directions == null) {
            this.setEnterDirectionsText(null);
        } else if (directions.length == 0) {
            this.setEnterDirectionsText("");
        } else if (directions.length == 1) {
            this.setEnterDirectionsText(directions[0].name());
        } else {
            StringBuilder str = new StringBuilder(directions.length * 2);
            for (RailEnterDirection dir : directions) {
                str.append(dir.name());
            }
            this.setEnterDirectionsText(str.toString());
        }
    }

    @Deprecated
    public void setDirections(Direction[] directions) {
        if (directions == null) {
            this.setEnterDirectionsText(null);
        } else if (directions.length == 0) {
            this.setEnterDirectionsText("");
        } else if (directions.length == 1) {
            Direction d = directions[0];
            this.setEnterDirectionsText(this.isValidDirection(d) ? d.aliases()[0] : "");
        } else {
            StringBuilder str = new StringBuilder();
            for (Direction d : directions) {
                if (!this.isValidDirection(d)) continue;
                str.append(d.aliases()[0]);
            }
            this.setEnterDirectionsText(str.toString());
        }
    }

    private boolean isValidDirection(Direction direction) {
        return direction != Direction.NONE && direction != Direction.CONTINUE && direction != Direction.REVERSE;
    }

    @Deprecated
    public BlockFace[] getFaces(BlockFace absoluteDirection) {
        if (this.directions_str == null) {
            return FaceUtil.AXIS;
        }
        return RailEnterDirection.toFacesOnly(this.getEnterDirections(RailPiece.NONE, absoluteDirection));
    }

    public SignActionType getRedstoneAction(boolean newPowerState) {
        return this.redstoneMode.getRedstoneAction(newPowerState);
    }

    public boolean isActionFiltered(SignActionType type) {
        if (type == SignActionType.NONE) {
            return false;
        }
        if (!(this.redstoneMode.isRespondingToRedstone() || type != SignActionType.REDSTONE_ON && type != SignActionType.REDSTONE_OFF)) {
            return true;
        }
        return (this.redstoneMode.isRisingPulse() || this.redstoneMode.isFallingPulse()) && !type.isRedstone();
    }

    public String toString() {
        if (!this.isValid()) {
            return "";
        }
        String prefix = "[" + this.redstoneMode.getPattern();
        String postfix = "";
        if (this.directions_str != null) {
            postfix = postfix + ":" + this.directions_str;
        }
        postfix = postfix + "]";
        if (this.mode == SignActionMode.TRAIN) {
            return prefix + "train" + postfix;
        }
        if (this.mode == SignActionMode.CART) {
            return prefix + "cart" + postfix;
        }
        if (this.mode == SignActionMode.RCTRAIN) {
            postfix = this.rc_name + "]";
            if (postfix.length() + prefix.length() >= 10) {
                return prefix + "t " + postfix;
            }
            return prefix + "train " + postfix;
        }
        return prefix + "?" + postfix;
    }

    public static SignActionHeader parseFromEvent(SignActionEvent event) {
        return SignActionHeader.parse(event.getLine(0));
    }

    public static SignActionHeader parseFromEvent(SignChangeEvent event) {
        return SignActionHeader.parse(Util.getCleanLine(event, 0));
    }

    public static SignActionHeader parseFromSign(Sign sign) {
        return SignActionHeader.parse(Util.getCleanLine(sign, 0));
    }

    public static SignActionHeader parse(String line) {
        boolean validEnd;
        SignActionHeader header = new SignActionHeader();
        if (line == null || line.isEmpty()) {
            header.mode = SignActionMode.NONE;
            header.is_empty = true;
            return header;
        }
        boolean validStart = line.charAt(0) == '[';
        boolean bl = validEnd = line.charAt(line.length() - 1) == ']';
        if (TCConfig.allowParenthesesFormat) {
            validStart |= line.charAt(0) == '(';
            validEnd |= line.charAt(line.length() - 1) == ')';
        }
        if (TCConfig.parseOldSigns && !validStart && !validEnd) {
            String s = line.toLowerCase(Locale.ENGLISH);
            if (s.startsWith("!") || s.startsWith("+")) {
                s = s.substring(1);
            }
            if (s.startsWith("train") || s.startsWith("t ") || s.startsWith("cart")) {
                header.is_converted = true;
                line = String.format("[%s]", line);
                validStart = true;
                validEnd = true;
            }
        }
        if (!validStart || !validEnd) {
            header.mode = SignActionMode.NONE;
            return header;
        }
        SignRedstoneMode.ParseResult redstoneParseResult = SignRedstoneMode.parse(line, 1);
        header.setRedstoneMode(redstoneParseResult.mode);
        int idx = redstoneParseResult.endIndex;
        String token = line.substring(idx, line.length() - 1).toLowerCase(Locale.ENGLISH);
        String after_token = "";
        header.modeText = token;
        if (token.startsWith("train ") && token.length() > 6) {
            header.mode = SignActionMode.RCTRAIN;
            after_token = line.substring(idx + 6, line.length() - 1);
        } else if (token.startsWith("t ") && token.length() > 2) {
            header.mode = SignActionMode.RCTRAIN;
            after_token = line.substring(idx + 2, line.length() - 1);
        } else if (token.startsWith("train")) {
            header.mode = SignActionMode.TRAIN;
            after_token = line.substring(idx + 5, line.length() - 1);
        } else if (token.startsWith("cart")) {
            header.mode = SignActionMode.CART;
            after_token = line.substring(idx + 4, line.length() - 1);
        } else {
            header.mode = SignActionMode.NONE;
            return header;
        }
        if (header.mode == SignActionMode.RCTRAIN) {
            header.rc_name = after_token;
        } else if (after_token.startsWith(":")) {
            header.directions_str = after_token = after_token.substring(1);
        }
        return header;
    }
}

