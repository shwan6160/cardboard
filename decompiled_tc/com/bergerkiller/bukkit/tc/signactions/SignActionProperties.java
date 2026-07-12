/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyInputContext;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;

public class SignActionProperties
extends TrainCartsSignAction {
    private static PropertyParseResult.Reason parseAndSet(IProperties properties, SignActionEvent info, boolean conditional) {
        return properties.parseAndSet(info.getLine(2), PropertyInputContext.of(info.getLine(3)).signEvent(info).beforeSet(result -> {
            if (conditional && !result.getInputContext().hasParsedStatements()) {
                return PropertyParseResult.failSuppressed(result.getInputContext(), result.getProperty(), result.getName());
            }
            return result;
        })).getReason();
    }

    public SignActionProperties() {
        super("property");
    }

    @Override
    public void execute(SignActionEvent info) {
        PropertyParseResult.Reason result;
        if (!info.isPowered()) {
            return;
        }
        boolean isConditionalCart = false;
        boolean isConditionalTrain = false;
        if (!info.getHeader().onPowerFalling() && !info.getHeader().onPowerRising()) {
            if (info.isAction(SignActionType.REDSTONE_CHANGE)) {
                isConditionalCart = true;
                isConditionalTrain = true;
            } else if (info.isAction(SignActionType.MEMBER_UPDATE)) {
                isConditionalCart = true;
            } else if (info.isAction(SignActionType.GROUP_UPDATE)) {
                isConditionalTrain = true;
            }
        }
        if ((isConditionalCart || info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)) && info.isCartSign() && info.hasMember()) {
            result = SignActionProperties.parseAndSet(info.getMember().getProperties(), info, isConditionalCart);
        } else if ((isConditionalTrain || info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) && info.isTrainSign() && info.hasGroup()) {
            result = SignActionProperties.parseAndSet(info.getGroup().getProperties(), info, isConditionalTrain);
        } else if (info.isAction(SignActionType.REDSTONE_ON) && info.isRCSign()) {
            result = PropertyParseResult.Reason.NONE;
            for (TrainProperties prop : info.getRCTrainProperties()) {
                PropertyParseResult.Reason singleResult = SignActionProperties.parseAndSet(prop, info, false);
                if (singleResult == PropertyParseResult.Reason.NONE) continue;
                result = singleResult;
            }
        } else {
            return;
        }
        BlockFace facingInv = info.getFacing().getOppositeFace();
        Location effectLocation = info.getSign().getLocation().add(0.5, 0.5, 0.5).add(0.3 * (double)facingInv.getModX(), 0.0, 0.3 * (double)facingInv.getModZ());
        switch (result) {
            case PROPERTY_NOT_FOUND: {
                Util.spawnDustParticle(effectLocation, 0.0, 0.0, 0.0);
                WorldUtil.playSound((Location)effectLocation, (ResourceKey)SoundEffect.EXTINGUISH, (float)1.0f, (float)2.0f);
                break;
            }
            case INVALID_INPUT: {
                Util.spawnDustParticle(effectLocation, 255.0, 255.0, 0.0);
                WorldUtil.playSound((Location)effectLocation, (ResourceKey)SoundEffect.EXTINGUISH, (float)1.0f, (float)2.0f);
                break;
            }
            case ERROR: {
                Util.spawnDustParticle(effectLocation, 255.0, 0.0, 0.0);
                WorldUtil.playSound((Location)effectLocation, (ResourceKey)SoundEffect.EXTINGUISH, (float)1.0f, (float)2.0f);
                break;
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_PROPERTY).setName(event.isCartSign() ? "cart property setter" : "train property setter").setTraincartsWIKIHelp("TrainCarts/Signs/Property");
        if (!Permission.COMMAND_PROPERTIES.has((CommandSender)event.getPlayer()) && !Permission.COMMAND_GLOBALPROPERTIES.has((CommandSender)event.getPlayer())) {
            Localization.PROPERTY_NOPERM_ANY.message((CommandSender)event.getPlayer(), new String[0]);
            return false;
        }
        PropertyParseResult result = IPropertyRegistry.instance().parse(null, event.getLine(2), event.getLine(3));
        if (!result.hasPermission((CommandSender)event.getPlayer())) {
            Localization.PROPERTY_NOPERM.message((CommandSender)event.getPlayer(), new String[]{result.getName()});
            return false;
        }
        if (event.isTrainSign()) {
            opt.setDescription("set properties on the train above");
        } else if (event.isCartSign()) {
            opt.setDescription("set properties on the cart above");
        } else if (event.isRCSign()) {
            opt.setDescription("remotely set properties on the train specified");
        }
        if (!opt.handle(event)) {
            return false;
        }
        if (!result.isSuccessful()) {
            event.getPlayer().sendMessage(result.getMessage());
        }
        return true;
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }
}

