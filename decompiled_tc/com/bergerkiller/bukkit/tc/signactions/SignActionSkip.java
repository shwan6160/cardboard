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
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.SignSkipOptions;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.statements.Statement;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SignActionSkip
extends TrainCartsSignAction {
    public SignActionSkip() {
        super("skip");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isPowered()) {
            return;
        }
        List<String> statements = this.getStatements(info);
        if (info.isCartSign() && info.isAction(SignActionType.REDSTONE_CHANGE, SignActionType.MEMBER_ENTER)) {
            if (!info.hasRailedMember()) {
                return;
            }
            if (Statement.hasMultiple(info.getMember(), statements, info)) {
                info.getMember().getProperties().setSkipOptions(this.getOptions(info));
            }
        } else if (info.isTrainSign() && info.isAction(SignActionType.REDSTONE_CHANGE, SignActionType.GROUP_ENTER)) {
            if (!info.hasRailedMember()) {
                return;
            }
            if (Statement.hasMultiple(info.getGroup(), statements, info)) {
                info.getGroup().getProperties().setSkipOptions(this.getOptions(info));
            }
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            SignSkipOptions opt = this.getOptions(info);
            for (TrainProperties prop : info.getRCTrainProperties()) {
                if (!Statement.hasMultiple(prop.getHolder(), statements, info)) continue;
                prop.setSkipOptions(opt);
            }
        }
    }

    public List<String> getStatements(SignActionEvent info) {
        String line1 = info.getLine(2);
        String line2 = info.getLine(3);
        if (line2.isEmpty()) {
            if (line1.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singletonList(line1);
        }
        String[] extraLines = info.getExtraLinesBelow();
        if (extraLines.length > 0) {
            ArrayList<String> statements = new ArrayList<String>(extraLines.length + 2);
            if (!line1.isEmpty()) {
                statements.add(line1);
            }
            statements.add(line2);
            for (String line : extraLines) {
                if (line.isEmpty()) break;
                statements.add(line);
            }
            return statements;
        }
        if (line1.isEmpty()) {
            return Collections.singletonList(line2);
        }
        return Arrays.asList(line1, line2);
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions opt = SignBuildOptions.create().setPermission(Permission.BUILD_SKIPPER).setName(event.isCartSign() ? "cart skipper" : "train skipper").setTraincartsWIKIHelp("TrainCarts/Signs/Skip");
        if (event.isTrainSign()) {
            opt.setDescription("tell a train to skip upcoming signs");
        } else if (event.isCartSign()) {
            opt.setDescription("tell a cart to skip upcoming signs");
        } else if (event.isRCSign()) {
            opt.setDescription("tell a remote train to skip signs");
        }
        return opt.handle(event);
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    private SignSkipOptions getOptions(SignActionEvent info) {
        String[] args = info.getLine(1).toLowerCase(Locale.ENGLISH).split(" ");
        args = StringUtil.remove((String[])args, (int)0);
        int skipCtr = 1;
        int ignoreCtr = 0;
        String filter = "";
        skipCtr = 1;
        if (args.length >= 1) {
            if (args[0].equals("none") || args[0].equals("disable")) {
                skipCtr = 0;
            } else {
                if (!ParseUtil.isNumeric((String)args[0])) {
                    filter = args[0];
                    args = StringUtil.remove((String[])args, (int)0);
                }
                if (args.length >= 2) {
                    ignoreCtr = ParseUtil.parseInt((String)args[0], (int)0);
                }
                if (args.length >= 1) {
                    skipCtr = ParseUtil.parseInt((String)args[args.length - 1], (int)1);
                }
            }
        }
        return SignSkipOptions.create(ignoreCtr, skipCtr, filter);
    }
}

