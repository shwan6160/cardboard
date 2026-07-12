/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.debug.types;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.debug.DebugToolUtil;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTrackWalkerType;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class DebugToolTypeRails
extends DebugToolTrackWalkerType {
    @Override
    public String getIdentifier() {
        return "Rails";
    }

    @Override
    public String getTitle() {
        return "Rail path tool";
    }

    @Override
    public String getDescription() {
        return "Display the positions on the rails along which trains move";
    }

    @Override
    public String getInstructions() {
        return "Right-click rails to see the path a train would take on it";
    }

    @Override
    public void onBlockInteract(TrainCarts plugin, Player player, TrackWalkingPoint walker, CommonItemStack item, boolean isRightClick) {
        player.sendMessage(ChatColor.YELLOW + "Checking for rails from path [" + MathUtil.round((double)walker.state.position().posX, (int)3) + "/" + MathUtil.round((double)walker.state.position().posY, (int)3) + "/" + MathUtil.round((double)walker.state.position().posZ, (int)3) + "]");
        int lim = 10000;
        AtomicInteger signShowLimit = new AtomicInteger(20);
        if (player.isSneaking()) {
            if (walker.moveFull()) {
                for (RailPath.Point point : walker.currentRailPath.getPoints()) {
                    Util.spawnDustParticle(point.getLocation(walker.state.railBlock()), 0.1, 0.1, 1.0);
                }
                do {
                    this.showSigns(player, walker, signShowLimit);
                    DebugToolUtil.showParticle(walker.state.railBlock().getLocation().add(0.5, 0.5, 0.5));
                } while (walker.moveFull() && --lim > 0);
            }
        } else {
            RailPiece lastRailPiece = null;
            int segmentCounter = 0;
            double[][] colors = new double[][]{{1.0, 0.0, 0.0}, {0.5, 0.0, 0.0}};
            while (walker.move(0.3) && --lim > 0) {
                if (lastRailPiece == null || !lastRailPiece.equals(walker.state.railPiece())) {
                    lastRailPiece = walker.state.railPiece();
                    ++segmentCounter;
                    this.showSigns(player, walker, signShowLimit);
                }
                Location loc = walker.state.positionLocation();
                double[] color = colors[segmentCounter % colors.length];
                Util.spawnDustParticle(loc, color[0], color[1], color[2]);
            }
        }
    }

    private void showSigns(Player player, TrackWalkingPoint walker, AtomicInteger limit) {
        if (limit.get() <= 0) {
            return;
        }
        for (RailLookup.TrackedSign sign : walker.state.railSigns()) {
            SignAction action = sign.getAction();
            if (action == null) continue;
            limit.decrementAndGet();
            String name = action.getClass().getSimpleName();
            int signActionStart = name.toLowerCase(Locale.ENGLISH).indexOf("signaction");
            if (signActionStart != -1) {
                name = name.substring(0, signActionStart) + name.substring(signActionStart + 10);
            }
            BlockFace face = walker.state.enterFace();
            SignActionEvent event = sign.createEvent(SignActionType.NONE);
            ChatColor color = event.isWatchedDirection(walker.state.enterFace()) ? (action.overrideFacing() ? ChatColor.DARK_GREEN : ChatColor.GREEN) : (action.overrideFacing() ? ChatColor.DARK_RED : ChatColor.RED);
            BlockFace signDir = sign.getFacing().getOppositeFace();
            String dirName = DebugToolTypeRails.formatDirection(face.name().toLowerCase(Locale.ENGLISH), color);
            for (Direction dir : new Direction[]{Direction.LEFT, Direction.FORWARD, Direction.BACKWARD, Direction.RIGHT}) {
                if (dir.getDirection(signDir) != face) continue;
                dirName = dirName + ChatColor.WHITE + "/" + DebugToolTypeRails.formatDirection(dir.name().toLowerCase(Locale.ENGLISH), color);
                break;
            }
            dirName = ChatColor.GRAY + "\u2514\u2518" + ChatColor.BLUE + "\u2192" + ChatColor.WHITE + "[" + dirName + ChatColor.WHITE + "]";
            String coord = ChatColor.WHITE + "- [" + sign.signBlock.getX() + "/" + sign.signBlock.getY() + "/" + sign.sign.getZ() + "] ";
            ChatText text = ChatText.fromMessage((String)(coord + color + name + dirName));
            text.setHoverText(DebugToolTypeRails.createHoverTextForSign((String[])LogicUtil.appendArray((Object[])sign.sign.getLines(), (Object[])sign.getExtraLines())));
            text.sendTo(player);
        }
    }

    private static String formatDirection(String name, ChatColor color) {
        return color.toString() + ChatColor.UNDERLINE + name.charAt(0) + ChatColor.RESET + color + name.substring(1);
    }

    private static ChatText createHoverTextForSign(String[] lines) {
        int len;
        for (len = lines.length; len > 0 && lines[len - 1].isEmpty(); --len) {
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                str.append('\n');
            }
            str.append(lines[i]);
        }
        return ChatText.fromMessage((String)str.toString());
    }
}

