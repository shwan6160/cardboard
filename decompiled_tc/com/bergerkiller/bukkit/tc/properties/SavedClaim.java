/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.exception.command.InvalidClaimPlayerNameException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SavedClaim {
    public final UUID playerUUID;
    public final String playerName;

    public SavedClaim(OfflinePlayer player) {
        this.playerUUID = player.getUniqueId();
        this.playerName = player.getName();
    }

    public SavedClaim(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.playerName = null;
    }

    private SavedClaim(String config) throws IllegalArgumentException {
        config = config.trim();
        int name_end = config.lastIndexOf(32);
        if (name_end == -1) {
            this.playerName = null;
            this.playerUUID = UUID.fromString(config);
        } else {
            this.playerName = config.substring(0, name_end);
            this.playerUUID = UUID.fromString(config.substring(name_end + 1).trim());
        }
    }

    public String description() {
        if (this.playerName == null) {
            return "uuid=" + this.playerUUID.toString();
        }
        return this.playerName;
    }

    public String toString() {
        if (this.playerName == null) {
            return this.playerUUID.toString();
        }
        return this.playerName + " " + this.playerUUID.toString();
    }

    public int hashCode() {
        return this.playerUUID.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SavedClaim) {
            SavedClaim other = (SavedClaim)o;
            return other.playerUUID.equals(this.playerUUID);
        }
        return false;
    }

    public static boolean hasPermission(ConfigurationNode config, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Set<SavedClaim> claims = SavedClaim.loadClaims(config);
        if (claims.isEmpty()) {
            return true;
        }
        UUID playerUUID = ((Player)sender).getUniqueId();
        for (SavedClaim claim : claims) {
            if (!playerUUID.equals(claim.playerUUID)) continue;
            return true;
        }
        return false;
    }

    public static Set<SavedClaim> loadClaims(ConfigurationNode config) {
        if (!config.contains("claims")) {
            return Collections.emptySet();
        }
        List claim_strings = config.getList("claims", String.class);
        if (claim_strings == null || claim_strings.isEmpty()) {
            return Collections.emptySet();
        }
        HashSet<SavedClaim> claims = new HashSet<SavedClaim>(claim_strings.size());
        for (String claim_str : claim_strings) {
            try {
                claims.add(new SavedClaim(claim_str));
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        return Collections.unmodifiableSet(claims);
    }

    public static void saveClaims(ConfigurationNode config, Collection<SavedClaim> claims) {
        if (claims.isEmpty()) {
            config.remove("claims");
        } else {
            ArrayList<String> claim_strings = new ArrayList<String>(claims.size());
            for (SavedClaim claim : claims) {
                claim_strings.add(claim.toString());
            }
            config.set("claims", claim_strings);
        }
    }

    public static Set<SavedClaim> parseClaims(Set<SavedClaim> oldClaims, String[] players) {
        HashSet<SavedClaim> result = new HashSet<SavedClaim>(players.length);
        for (String playerArg : players) {
            try {
                UUID queryUUID = UUID.fromString(playerArg);
                OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(queryUUID);
                if (!(player instanceof Player || player.getName() != null && player.hasPlayedBefore())) {
                    player = null;
                }
                if (player != null) {
                    result.add(new SavedClaim(player));
                    continue;
                }
                boolean uuidMatchesOldClaim = false;
                for (SavedClaim oldClaim : oldClaims) {
                    if (!oldClaim.playerUUID.equals(queryUUID)) continue;
                    result.add(oldClaim);
                    uuidMatchesOldClaim = true;
                    break;
                }
                if (uuidMatchesOldClaim) continue;
                result.add(new SavedClaim(queryUUID));
            }
            catch (IllegalArgumentException ex) {
                boolean nameMatchesOldClaim = false;
                for (SavedClaim oldClaim : oldClaims) {
                    if (oldClaim.playerName == null || !oldClaim.playerName.equals(playerArg)) continue;
                    result.add(oldClaim);
                    nameMatchesOldClaim = true;
                    break;
                }
                if (nameMatchesOldClaim) continue;
                OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(playerArg);
                if (!(player instanceof Player || player.getName() != null && player.hasPlayedBefore())) {
                    throw new InvalidClaimPlayerNameException(playerArg);
                }
                result.add(new SavedClaim(player));
            }
        }
        return result;
    }

    public static void buildClaimList(MessageBuilder builder, Set<SavedClaim> claims) {
        if (claims.isEmpty()) {
            builder.red(new Object[]{"Not Claimed"});
        } else {
            builder.setSeparator(ChatColor.WHITE, ", ");
            for (SavedClaim claim : claims) {
                OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(claim.playerUUID);
                String name = player.getName();
                if (name == null) {
                    name = claim.playerName;
                    if (name == null) {
                        name = claim.playerUUID.toString();
                    }
                    builder.red(new Object[]{name});
                    continue;
                }
                if (player.isOnline()) {
                    builder.aqua(new Object[]{name});
                    continue;
                }
                builder.white(new Object[]{name});
            }
        }
    }
}

