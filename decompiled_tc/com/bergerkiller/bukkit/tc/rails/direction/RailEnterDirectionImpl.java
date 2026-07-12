/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.rails.direction;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirection;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirectionToFace;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import org.bukkit.block.BlockFace;

class RailEnterDirectionImpl {
    public static final RailEnterDirection[] NONE = new RailEnterDirection[0];
    public static final RailEnterDirection[] ALL = new RailEnterDirection[FaceUtil.BLOCK_SIDES.length];
    private static final Map<String, DirectionEnterDirection> DIRECTION_BY_NAME;
    private static final Map<Character, Direction> DIRECTION_BY_CHAR;

    RailEnterDirectionImpl() {
    }

    public static RailEnterDirection[] parseAll(RailPiece rail, BlockFace forwardDirection, String text) {
        forwardDirection = Util.snapFace(forwardDirection);
        DirectionEnterDirection dir = DIRECTION_BY_NAME.get(text);
        if (dir != null) {
            return dir.get(forwardDirection);
        }
        dir = DIRECTION_BY_NAME.get(text.toLowerCase(Locale.ENGLISH));
        if (dir != null) {
            return dir.get(forwardDirection);
        }
        DirectionList result = new DirectionList(text);
        for (RailJunction junction : rail.getJunctions()) {
            String name = junction.name();
            int nameLen = name.length();
            if (nameLen == 0 || nameLen == 1 && DIRECTION_BY_CHAR.containsKey(Character.valueOf(name.charAt(0)))) continue;
            result.matchJunction(name, junction);
        }
        result.finish(forwardDirection);
        return result.toArray();
    }

    static {
        for (int i = 0; i < ALL.length; ++i) {
            RailEnterDirectionImpl.ALL[i] = RailEnterDirection.toFace(FaceUtil.BLOCK_SIDES[i]);
        }
        DIRECTION_BY_NAME = new HashMap<String, DirectionEnterDirection>();
        DIRECTION_BY_CHAR = new HashMap<Character, Direction>();
        for (Direction direction : Direction.values()) {
            DirectionEnterDirection enterDirection;
            if (direction == Direction.NONE) continue;
            if (direction.isAbsolute()) {
                RailEnterDirection[] constant = RailEnterDirectionToFace.arrayFromFace(direction.getDirection(BlockFace.DOWN));
                enterDirection = a -> constant;
            } else {
                Direction d = direction;
                enterDirection = a -> RailEnterDirectionToFace.arrayFromFace(d.getDirection(a));
            }
            for (String name : direction.aliases()) {
                String name_lower = name.toLowerCase(Locale.ENGLISH);
                String name_upper = name.toUpperCase(Locale.ENGLISH);
                DIRECTION_BY_NAME.put(name_lower, enterDirection);
                DIRECTION_BY_NAME.put(name_upper, enterDirection);
                if (name.length() != 1) continue;
                DIRECTION_BY_CHAR.put(Character.valueOf(name_lower.charAt(0)), direction);
                DIRECTION_BY_CHAR.put(Character.valueOf(name_upper.charAt(0)), direction);
            }
        }
        DIRECTION_BY_NAME.put("*", a -> ALL);
        DIRECTION_BY_NAME.put("all", a -> ALL);
        DIRECTION_BY_NAME.put("ALL", a -> ALL);
        DIRECTION_BY_NAME.put("", a -> NONE);
    }

    @FunctionalInterface
    private static interface DirectionEnterDirection {
        public RailEnterDirection[] get(BlockFace var1);
    }

    private static class DirectionList {
        final LinkedList<DirectionToken> list = new LinkedList();

        public DirectionList(String text) {
            this.list.add(new DirectionToken(text));
        }

        public void matchJunction(String name, RailJunction junction) {
            ListIterator<DirectionToken> iter = this.list.listIterator();
            while (iter.hasNext()) {
                DirectionToken token = (DirectionToken)iter.next();
                int index = token.text.indexOf(name);
                if (index == -1) continue;
                int len = name.length();
                if (index == 0) {
                    if (token.text.length() == len) {
                        token.text = "";
                        token.direction = RailEnterDirection.fromJunction(junction);
                        continue;
                    }
                    token.text = token.text.substring(index + len);
                    iter.previous();
                    iter.add(new DirectionToken(RailEnterDirection.fromJunction(junction)));
                    continue;
                }
                iter.add(new DirectionToken(RailEnterDirection.fromJunction(junction)));
                if (index + len < token.text.length()) {
                    iter.add(new DirectionToken(token.text.substring(index + len)));
                    iter.previous();
                }
                token.text = token.text.substring(0, index);
                iter.previous();
                iter.previous();
            }
        }

        public void finish(BlockFace forwardDirection) {
            ListIterator<DirectionToken> iter = this.list.listIterator();
            while (iter.hasNext()) {
                DirectionToken token = (DirectionToken)iter.next();
                if (token.text.isEmpty()) continue;
                int len = token.text.length();
                boolean first = true;
                for (int ch_idx = 0; ch_idx < len; ++ch_idx) {
                    Direction ch_dir = (Direction)((Object)DIRECTION_BY_CHAR.get(Character.valueOf(token.text.charAt(ch_idx))));
                    if (ch_dir == null) continue;
                    for (RailEnterDirection enterDir : RailEnterDirectionToFace.arrayFromFace(ch_dir.getDirection(forwardDirection))) {
                        if (first) {
                            first = false;
                            token.direction = enterDir;
                            continue;
                        }
                        iter.add(new DirectionToken(enterDir));
                    }
                }
                token.text = "";
                if (!first) continue;
                iter.remove();
            }
        }

        public RailEnterDirection[] toArray() {
            RailEnterDirection[] result = new RailEnterDirection[this.list.size()];
            int i = -1;
            for (DirectionToken token : this.list) {
                result[++i] = token.direction;
            }
            return result;
        }
    }

    private static class DirectionToken {
        String text;
        RailEnterDirection direction;

        public DirectionToken(String text) {
            this.text = text;
            this.direction = null;
        }

        public DirectionToken(RailEnterDirection direction) {
            this.text = "";
            this.direction = direction;
        }
    }
}

