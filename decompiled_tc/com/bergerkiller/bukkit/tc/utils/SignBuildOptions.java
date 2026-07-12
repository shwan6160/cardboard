/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.permissions.IPermissionEnum
 *  com.bergerkiller.bukkit.common.permissions.PermissionEnum
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.permissions.IPermissionEnum;
import com.bergerkiller.bukkit.common.permissions.PermissionEnum;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignBuildOptions {
    private boolean showBuildMessage = true;
    private String permission = null;
    private String name = null;
    private String helpURL = null;
    private String helpAlt = null;
    private String description = null;

    protected SignBuildOptions() {
    }

    public SignBuildOptions setPermission(PermissionEnum permission) {
        return this.setPermission(permission == null ? null : permission.getName());
    }

    public SignBuildOptions setPermission(IPermissionEnum permission) {
        return this.setPermission(permission == null ? null : permission.getName());
    }

    public SignBuildOptions setPermission(String permissionNode) {
        this.permission = permissionNode;
        return this;
    }

    public SignBuildOptions setName(String name) {
        this.name = name;
        return this;
    }

    public SignBuildOptions setDescription(String description) {
        this.description = description;
        return this;
    }

    public SignBuildOptions setTraincartsWIKIHelp(String page) {
        return this.setHelpURL("https://wiki.traincarts.net/index.php/" + page, "Click here to visit the Traincarts WIKI for help with this sign");
    }

    public SignBuildOptions setMinecraftWIKIHelp(String page) {
        return this.setHelpURL("https://minecraft.wiki/w/" + page, "Click here to visit the Minecraft WIKI for help with this sign");
    }

    public SignBuildOptions setHelpURL(String url) {
        this.helpURL = url;
        return this;
    }

    public SignBuildOptions setHelpURL(String url, String alt) {
        this.helpURL = url;
        this.helpAlt = alt;
        return this;
    }

    public SignBuildOptions setShowBuildMessage(boolean show) {
        this.showBuildMessage = show;
        return this;
    }

    public boolean checkBuildPermission(Player player) {
        if (this.permission != null && !CommonUtil.hasPermission((CommandSender)player, (String)this.permission)) {
            Localization.SIGN_NO_PERMISSION.message((CommandSender)player, new String[]{(String)LogicUtil.fixNull((Object)this.name, (Object)"")});
            return false;
        }
        return true;
    }

    public void showBuildMessage(Player player) {
        if (!this.showBuildMessage) {
            return;
        }
        if (this.name != null) {
            ChatText message = ChatText.fromMessage((String)(ChatColor.YELLOW + "You built a "));
            if (this.helpURL != null) {
                message.appendClickableURL(ChatColor.WHITE.toString() + ChatColor.UNDERLINE.toString() + this.name, this.helpURL, this.helpAlt);
            } else {
                message.append(ChatColor.WHITE.toString() + this.name);
            }
            message.append(ChatColor.YELLOW + "!");
            message.sendTo(player);
        }
        if (this.description != null) {
            player.sendMessage(ChatColor.GREEN + "This sign can " + this.description + ".");
        }
    }

    public boolean handle(Player player) {
        if (this.checkBuildPermission(player)) {
            this.showBuildMessage(player);
            return true;
        }
        return false;
    }

    public boolean handle(SignChangeActionEvent event) {
        if (!this.checkBuildPermission(event.getPlayer())) {
            return false;
        }
        if (event.isInteractive()) {
            this.showBuildMessage(event.getPlayer());
        }
        return true;
    }

    public static SignBuildOptions create() {
        return new SignBuildOptions();
    }
}

