/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier.permission;

import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.permission.BrigadierPermissionChecker;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, since="2.0.0")
public final class BrigadierPermissionPredicate<C, S>
implements Predicate<S> {
    private final SenderMapper<S, C> senderMapper;
    private final BrigadierPermissionChecker<C> permissionChecker;
    private final CommandNode<?> node;

    public BrigadierPermissionPredicate(@NonNull SenderMapper<S, C> senderMapper, @NonNull BrigadierPermissionChecker<C> permissionChecker, @NonNull CommandNode<?> node) {
        this.senderMapper = senderMapper;
        this.permissionChecker = permissionChecker;
        this.node = node;
    }

    @Override
    public boolean test(@NonNull S source) {
        C cloudSender = this.senderMapper.map(source);
        Map accessMap = this.node.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
        for (Map.Entry entry : accessMap.entrySet()) {
            if (!GenericTypeReflector.isSuperType((Type)entry.getKey(), cloudSender.getClass()) || !this.permissionChecker.hasPermission(cloudSender, (Permission)entry.getValue())) continue;
            return true;
        }
        return false;
    }
}

