/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.cache;

import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.util.Collection;
import java.util.List;
import org.bukkit.block.Block;

@Deprecated
public class RailMemberCache {
    public static MinecartMember<?> find(OfflineBlock railBlock) {
        List<MinecartMember<?>> members = RailLookup.findMembersOnRail(railBlock);
        if (members.isEmpty()) {
            return null;
        }
        return (MinecartMember)members.iterator().next();
    }

    @Deprecated
    public static MinecartMember<?> find(Block railBlock) {
        return RailMemberCache.find(OfflineBlock.of((Block)railBlock));
    }

    public static Collection<MinecartMember<?>> findAll(OfflineBlock railBlock) {
        return RailLookup.findMembersOnRail(railBlock);
    }

    @Deprecated
    public static Collection<MinecartMember<?>> findAll(Block railBlock) {
        return RailMemberCache.findAll(OfflineBlock.of((Block)railBlock));
    }

    public static void remove(MinecartMember<?> member) {
        RailLookup.removeMemberFromAll(member);
    }
}

