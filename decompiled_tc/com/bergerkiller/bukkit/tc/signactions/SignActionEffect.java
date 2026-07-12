/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentNameLookup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SignActionEffect
extends TrainCartsSignAction {
    public SignActionEffect() {
        super("effect");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isPowered()) {
            return;
        }
        if (info.isTrainSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER) && info.hasGroup()) {
            EffectAction.parse(info).run(info.getGroup().getAttachments().getNameLookup());
        } else if (info.isCartSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.MEMBER_ENTER) && info.hasMember()) {
            EffectAction.parse(info).run(info.getMember().getAttachments().getNameLookup());
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            EffectAction action = EffectAction.parse(info);
            for (MinecartGroup group : info.getRCTrainGroups()) {
                action.run(group.getAttachments().getNameLookup());
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_EFFECT).setName(event.isCartSign() ? "cart effect player" : "train effect player").setTraincartsWIKIHelp("TrainCarts/Signs/Effect");
        if (event.isTrainSign()) {
            opt.setDescription("play effects configured in attachments of all carts of the train");
        } else if (event.isCartSign()) {
            opt.setDescription("play effects configured in attachments of the cart");
        } else if (event.isRCSign()) {
            opt.setDescription("remotely play effects configured in attachments of all carts of the train");
        }
        return opt.handle(event);
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    private static class EffectAction {
        public final Consumer<Attachment.EffectAttachment> action;
        public final List<String> effects;

        public static EffectAction parse(SignActionEvent event) {
            return new EffectAction(event);
        }

        private EffectAction(SignActionEvent event) {
            String[] args = StringUtil.getAfter((String)event.getLine(1), (String)" ").trim().split(" ", -1);
            double speed = 1.0;
            double volume = 1.0;
            boolean decodedSpeed = false;
            boolean stop = false;
            for (String arg : args) {
                if (arg.equalsIgnoreCase("stop")) {
                    stop = true;
                    break;
                }
                if (!ParseUtil.isNumeric((String)arg)) continue;
                if (decodedSpeed) {
                    volume = ParseUtil.parseDouble((String)arg, (double)1.0);
                    continue;
                }
                decodedSpeed = true;
                speed = ParseUtil.parseDouble((String)arg, (double)1.0);
            }
            if (stop) {
                this.action = Attachment.EffectAttachment::stopEffect;
            } else {
                Attachment.EffectAttachment.EffectOptions opt = Attachment.EffectAttachment.EffectOptions.of(volume, speed);
                this.action = e -> e.playEffect(opt);
            }
            this.effects = Stream.of(event.getLine(2), event.getLine(3)).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        }

        public void run(AttachmentNameLookup lookup) {
            for (String effect : this.effects) {
                lookup.getOfType(effect, Attachment.EffectAttachment.class).forEach(this.action);
            }
        }
    }
}

