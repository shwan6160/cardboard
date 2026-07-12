/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.TeamProvider;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GlowColorTeamProvider {
    private final TeamProvider teamProvider;
    private final Map<ChatColor, TeamProvider.Team> teamsByColor = new EnumMap<ChatColor, TeamProvider.Team>(ChatColor.class);

    public GlowColorTeamProvider(TeamProvider teamProvider) {
        this.teamProvider = teamProvider;
        for (ChatColor color : ChatColor.values()) {
            if (color == ChatColor.WHITE) {
                this.teamsByColor.put(color, teamProvider.disabledTeam());
                continue;
            }
            if (!color.isColor()) continue;
            this.teamsByColor.put(color, teamProvider.buildTeam().color(color).prefix(ChatText.fromChatColor((ChatColor)color)).build());
        }
    }

    @Deprecated
    public void update(Player viewer, UUID entityUUID, ChatColor color) {
        this.update(AttachmentViewer.fallback(viewer), entityUUID, color);
    }

    public void update(AttachmentViewer viewer, UUID entityUUID, ChatColor color) {
        if (color == null) {
            this.reset(viewer, entityUUID);
        } else {
            TeamProvider.Team team = this.teamsByColor.get(color);
            if (team != null) {
                team.join(viewer, entityUUID);
            }
        }
    }

    public void update(AttachmentViewer viewer, Iterable<UUID> entityUUIDs, ChatColor color) {
        if (color == null) {
            this.reset(viewer, entityUUIDs);
        } else {
            TeamProvider.Team team = this.teamsByColor.get(color);
            if (team != null) {
                team.join(viewer, entityUUIDs);
            }
        }
    }

    public void reset(AttachmentViewer viewer, Iterable<UUID> entityUUIDs) {
        this.teamProvider.reset(viewer, entityUUIDs);
    }

    public void reset(AttachmentViewer viewer, UUID entityUUID) {
        this.teamProvider.reset(viewer, entityUUID);
    }

    public void reset(Player viewer, UUID entityUUID) {
        this.teamProvider.reset(viewer, entityUUID);
    }

    public void reset(Player viewer) {
        this.teamProvider.reset(viewer);
    }
}

