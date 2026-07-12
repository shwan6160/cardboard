/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.api.Via
 *  com.viaversion.viaversion.api.ViaAPI
 *  com.viaversion.viaversion.api.protocol.version.ProtocolVersion
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public class PlayerGameInfoSupplier_ViaVersion
implements Function<Player, PlayerGameInfo> {
    private final ViaAPI<Player> api = Via.getAPI();
    private final Entry[] entries = new Entry[ProtocolVersion.getProtocols().stream().mapToInt(ProtocolVersion::getVersion).max().getAsInt() + 1];

    public PlayerGameInfoSupplier_ViaVersion() {
        ProtocolVersion.getProtocols().stream().map(Entry::new).filter(e -> e.protocolVersion >= 0).filter(e -> TextValueSequence.evaluate(e.minimum, ">=", TextValueSequence.parse("1.8"))).forEach(e -> {
            this.entries[e.protocolVersion] = e;
        });
        boolean initial = true;
        for (int i = 0; i < this.entries.length; ++i) {
            if (initial && this.entries[i] != null) {
                initial = false;
                Arrays.fill(this.entries, 0, i, this.entries[i]);
                continue;
            }
            if (initial || this.entries[i] != null) continue;
            this.entries[i] = this.entries[i - 1];
        }
    }

    @Override
    public PlayerGameInfo apply(Player player) {
        int protocolVersion = this.api.getPlayerVersion((Object)player);
        try {
            return this.entries[protocolVersion];
        }
        catch (IndexOutOfBoundsException ex) {
            return this.entries[this.entries.length - 1];
        }
    }

    private static class Entry
    implements PlayerGameInfo {
        public final int protocolVersion;
        public final TextValueSequence minimum;
        public final TextValueSequence maximum;

        public Entry(ProtocolVersion version) {
            this.protocolVersion = version.getVersion();
            if (version.isVersionWildcard()) {
                String str = version.getName().substring(0, version.getName().length() - 2);
                this.minimum = TextValueSequence.parse(str);
                this.maximum = TextValueSequence.parse(str + ".9");
            } else if (version.isRange()) {
                List list = version.getIncludedVersions().stream().map(TextValueSequence::parse).sorted().collect(Collectors.toList());
                this.minimum = (TextValueSequence)list.get(0);
                this.maximum = (TextValueSequence)list.get(list.size() - 1);
            } else {
                this.minimum = this.maximum = TextValueSequence.parse(version.getName());
            }
        }

        @Override
        public String version() {
            return this.maximum.toString();
        }

        @Override
        public boolean evaluateVersion(String operand, TextValueSequence rightSide) {
            int second;
            int len = operand.length();
            if (len == 0 || len > 2) {
                return false;
            }
            char first = operand.charAt(0);
            int n = second = len == 2 ? (int)operand.charAt(1) : 32;
            if (first == '>') {
                int comp = this.minimum.compareTo(rightSide);
                if (second == 61) {
                    return comp >= 0;
                }
                return comp > 0;
            }
            if (first == '<') {
                int comp = this.maximum.compareTo(rightSide);
                if (second == 61) {
                    return comp <= 0;
                }
                return comp < 0;
            }
            if (first == '=' && second == 61) {
                if (this.minimum == this.maximum) {
                    return this.minimum.equals(rightSide);
                }
                return this.minimum.compareTo(rightSide) >= 0 && this.maximum.compareTo(rightSide) <= 0;
            }
            if (first == '!' && second == 61) {
                if (this.minimum == this.maximum) {
                    return !this.minimum.equals(rightSide);
                }
                return this.minimum.compareTo(rightSide) < 0 || this.maximum.compareTo(rightSide) > 0;
            }
            return false;
        }
    }
}

