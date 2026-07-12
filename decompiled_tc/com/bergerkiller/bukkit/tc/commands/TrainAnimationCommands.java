/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Hastebin$DownloadResult
 *  com.bergerkiller.bukkit.common.Hastebin$UploadResult
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding
 *  com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.Hastebin;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Flag;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import com.bergerkiller.bukkit.tc.attachments.ui.AnimationFramesImportExport;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandRequiresPermission;
import com.bergerkiller.bukkit.tc.utils.QuoteEscapedString;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(value="train animation")
public class TrainAnimationCommands {
    private AnimationFramesImportExport tryAccessAnimationMenu(Player player) {
        MapDisplay display = MapDisplay.getHeldDisplay((Player)player, AttachmentEditor.class);
        if (display == null && (display = MapDisplay.getHeldDisplay((Player)player)) == null) {
            player.sendMessage(ChatColor.RED + "You do not have an editor menu open");
            return null;
        }
        MapWidget focused = display.getFocusedWidget();
        if (!(focused instanceof AnimationFramesImportExport)) {
            focused = display.getActivatedWidget();
        }
        if (!(focused instanceof AnimationFramesImportExport)) {
            player.sendMessage(ChatColor.RED + "Train attachment animation menu is not open!");
            return null;
        }
        AnimationFramesImportExport menu = (AnimationFramesImportExport)focused;
        if (menu.getAnimationName() == null) {
            player.sendMessage(ChatColor.RED + "No animation is selected yet, please create one!");
            return null;
        }
        return menu;
    }

    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="export")
    @CommandDescription(value="Exports the train animation frames to a hastebin server")
    private void commandTrainAnimationExport(TrainCarts plugin, final Player player) {
        AnimationFramesImportExport menu = this.tryAccessAnimationMenu(player);
        if (menu == null) {
            return;
        }
        List<AnimationNode> nodes = menu.exportAnimationFrames();
        if (nodes.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Animation is empty");
            return;
        }
        final String animationName = menu.getAnimationName();
        ConfigurationNode tmp = new ConfigurationNode();
        tmp.set("nodes", nodes.stream().map(AnimationNode::serializeToString).collect(Collectors.toList()));
        String config = Pattern.compile("\r?\n").splitAsStream(tmp.toString()).skip(1L).map(String::trim).collect(Collectors.joining("\n"));
        TCConfig.hastebin.upload(config).thenAccept(new Consumer<Hastebin.UploadResult>(){
            final /* synthetic */ TrainAnimationCommands this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void accept(Hastebin.UploadResult t) {
                if (t.success()) {
                    player.sendMessage(ChatColor.GREEN + "Animation '" + ChatColor.YELLOW + animationName + ChatColor.GREEN + "' exported: " + ChatColor.WHITE + ChatColor.UNDERLINE + t.url());
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to export animation '" + animationName + "': " + t.error());
                }
            }
        });
    }

    @CommandRequiresPermission(value=Permission.COMMAND_GIVE_EDITOR)
    @Command(value="import <url>")
    @CommandDescription(value="Imports train attachment animation frames from an online hastebin server by url")
    private void commandTrainAnimationImport(final TrainCarts plugin, final Player player, @Greedy @FlagYielding @Argument(value="url", description="The URL to a Hastebin-hosted paste to download from") String url, final @Flag(value="insert") boolean insert) {
        if (this.tryAccessAnimationMenu(player) == null) {
            return;
        }
        TCConfig.hastebin.download(url).thenAccept(new Consumer<Hastebin.DownloadResult>(){
            final /* synthetic */ TrainAnimationCommands this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void accept(Hastebin.DownloadResult result) {
                List<AnimationNode> frames;
                if (!result.success()) {
                    Localization.COMMAND_IMPORT_ERROR.message((CommandSender)player, new String[]{result.error()});
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(result.contentInputStream(), StandardCharsets.UTF_8));){
                    frames = reader.lines().map(String::trim).map(s -> s.startsWith("-") ? s.substring(1).trim() : s).map(s -> {
                        QuoteEscapedString q = QuoteEscapedString.tryParseQuoted(s);
                        return q.isQuoteEscaped() ? q.getUnescaped() : s;
                    }).map(AnimationNode::parseFromString).filter(AnimationNode::hasValidDuration).collect(Collectors.toList());
                }
                catch (Throwable t) {
                    plugin.getLogger().log(Level.WARNING, "Failed to import animation", t);
                    Localization.COMMAND_IMPORT_ERROR.message((CommandSender)player, new String[]{t.getMessage()});
                    return;
                }
                if (frames.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "No animation frames could be read from the provided url");
                    return;
                }
                AnimationFramesImportExport menu = this.this$0.tryAccessAnimationMenu(player);
                if (menu == null) {
                    return;
                }
                String animationName = menu.getAnimationName();
                menu.importAnimationFrames(frames, insert);
                player.sendMessage(ChatColor.GREEN + "Imported " + ChatColor.WHITE + frames.size() + ChatColor.GREEN + " frames into animation '" + ChatColor.YELLOW + animationName + ChatColor.GREEN + "'!");
            }
        });
    }
}

