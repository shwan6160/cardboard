/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.helper;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.helper.AttachmentUpdateTransformHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelperMethods {
    private static final ChatColor[] GLOW_COLORS = new ChatColor[]{ChatColor.DARK_RED, ChatColor.DARK_GREEN, ChatColor.DARK_BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.YELLOW, ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE, ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.BLACK, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.WHITE};
    private static final AttachmentUpdateTransformHelper updateTransformHelper = AttachmentUpdateTransformHelper.createSimple();

    @Deprecated
    public static void updatePositions(Attachment startAttachment, Matrix4x4 transform) {
        updateTransformHelper.startAndFinish(startAttachment, transform);
    }

    public static void makeHiddenRecursive(Attachment root, boolean active, AttachmentViewer viewer) {
        active &= root.isActive();
        for (Attachment child : root.getChildren()) {
            HelperMethods.makeHiddenRecursive(child, active, viewer);
        }
        if (active || !root.isHiddenWhenInactive()) {
            root.makeHidden(viewer);
        }
    }

    public static void makeVisibleRecursive(Attachment root, boolean active, AttachmentViewer viewer) {
        if ((active &= root.isActive()) || !root.isHiddenWhenInactive()) {
            root.makeVisible(viewer);
        }
        for (Attachment child : root.getChildren()) {
            HelperMethods.makeVisibleRecursive(child, active, viewer);
        }
    }

    public static boolean hasInactiveParent(Attachment attachment) {
        for (Attachment parent = attachment.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.isActive()) continue;
            return true;
        }
        return false;
    }

    public static void updateActiveRecursive(Attachment attachment, boolean active, Collection<Player> viewers) {
        attachment.onActiveChanged(active);
        if (attachment.isHiddenWhenInactive()) {
            if (active) {
                for (Player viewer : viewers) {
                    attachment.makeVisible(viewer);
                }
            } else {
                for (Player viewer : viewers) {
                    attachment.makeHidden(viewer);
                }
            }
            attachment.getInternalState().last_transform = null;
        }
        for (Attachment child : attachment.getChildren()) {
            if (!child.isActive()) continue;
            HelperMethods.updateActiveRecursive(child, active, viewers);
        }
    }

    public static void perform_onTick(Attachment attachment) {
        attachment.onTick();
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.perform_onTick(child);
        }
    }

    public static void perform_onMove(Attachment attachment, boolean absolute) {
        attachment.onMove(absolute);
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.perform_onMove(child, absolute);
        }
    }

    public static void perform_onAttached(Attachment attachment) {
        HelperMethods.perform_onAttached_single(attachment);
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.perform_onAttached(child);
        }
    }

    public static void perform_onAttached_single(Attachment attachment) {
        attachment.getInternalState().attached = true;
        attachment.onAttached();
        attachment.onLoad(attachment.getConfig());
        if (attachment.isFocused()) {
            attachment.onFocus();
        }
    }

    public static void perform_onDetached(Attachment attachment) {
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.perform_onDetached(child);
        }
        HelperMethods.perform_onDetached_single(attachment);
    }

    public static void perform_onDetached_single(Attachment attachment) {
        attachment.onDetached();
        attachment.getInternalState().attached = false;
        attachment.getInternalState().reset();
    }

    public static Attachment findAttachmentWithEntityId(Attachment root, int entityId) {
        if (root.containsEntityId(entityId)) {
            return root;
        }
        for (Attachment child : root.getChildren()) {
            Attachment att = HelperMethods.findAttachmentWithEntityId(child, entityId);
            if (att == null) continue;
            return att;
        }
        return null;
    }

    public static boolean playAnimationRecursive(Attachment attachment, AnimationOptions options) {
        if (HelperMethods.playStoredAnimationRecursive(attachment, options)) {
            return true;
        }
        Animation defaultAnimation = TCConfig.defaultAnimations.get(options.getName());
        if (defaultAnimation != null) {
            attachment.startAnimation(defaultAnimation.clone().applyOptions(options));
            return true;
        }
        return false;
    }

    public static boolean playAnimation(Attachment attachment, AnimationOptions options) {
        if (HelperMethods.playStoredAnimation(attachment, options)) {
            return true;
        }
        Animation defaultAnimation = TCConfig.defaultAnimations.get(options.getName());
        if (defaultAnimation != null) {
            attachment.startAnimation(defaultAnimation.clone().applyOptions(options));
            return true;
        }
        return false;
    }

    public static void setFocusedRecursive(Attachment attachment, boolean focused) {
        attachment.setFocused(focused);
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.setFocusedRecursive(child, focused);
        }
    }

    public static ChatColor getFocusGlowColor(Attachment attachment) {
        Attachment parent;
        while (true) {
            if ((parent = attachment.getParent()) == null) {
                return ChatColor.WHITE;
            }
            if (!parent.isFocused()) break;
            attachment = parent;
        }
        return GLOW_COLORS[parent.getChildren().indexOf(attachment) & 0xF];
    }

    public static void addAnimationNamesToSetRecursive(Set<String> out_names, Attachment attachment) {
        out_names.addAll(attachment.getAnimationNames());
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.addAnimationNamesToSetRecursive(out_names, child);
        }
    }

    public static void addAnimationScenesToSetRecursive(Set<String> out_names, String animationName, Attachment attachment) {
        out_names.addAll(attachment.getAnimationScenes(animationName));
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.addAnimationScenesToSetRecursive(out_names, animationName, child);
        }
    }

    private static boolean playStoredAnimation(Attachment attachment, AnimationOptions options) {
        Animation anim = attachment.getInternalState().animations.get(options.getName());
        if (anim != null) {
            attachment.startAnimation(anim.clone().applyOptions(options));
            return true;
        }
        return false;
    }

    private static boolean playStoredAnimationRecursive(Attachment attachment, AnimationOptions options) {
        boolean found = HelperMethods.playStoredAnimation(attachment, options);
        for (Attachment child : attachment.getChildren()) {
            found |= HelperMethods.playStoredAnimationRecursive(child, options);
        }
        return found;
    }

    public static List<Attachment> listAllAttachments(Attachment root) {
        if (root == null) {
            return Collections.emptyList();
        }
        ArrayList<Attachment> result = new ArrayList<Attachment>(16);
        HelperMethods.addAttachments(root, result);
        return Collections.unmodifiableList(result);
    }

    private static void addAttachments(Attachment attachment, List<Attachment> dest) {
        dest.add(attachment);
        for (Attachment child : attachment.getChildren()) {
            HelperMethods.addAttachments(child, dest);
        }
    }
}

