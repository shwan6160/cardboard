/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.brigadier;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.type.tuple.Pair;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.tree.CommandNode;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public final class CloudBrigadierCommand<C, S>
implements Command<S> {
    private final CommandManager<C> commandManager;
    private final CloudBrigadierManager<C, S> brigadierManager;
    private final Function<String, String> inputMapper;

    public CloudBrigadierCommand(@NonNull CommandManager<C> commandManager, @NonNull CloudBrigadierManager<C, S> brigadierManager) {
        this.commandManager = commandManager;
        this.brigadierManager = brigadierManager;
        this.inputMapper = Function.identity();
    }

    public CloudBrigadierCommand(@NonNull CommandManager<C> commandManager, @NonNull CloudBrigadierManager<C, S> brigadierManager, @NonNull Function<String, String> inputMapper) {
        this.commandManager = commandManager;
        this.brigadierManager = brigadierManager;
        this.inputMapper = inputMapper;
    }

    @Override
    public int run(@NonNull CommandContext<S> ctx) {
        Object source = ctx.getSource();
        String input = this.inputMapper.apply(ctx.getInput().substring(CloudBrigadierCommand.parsedNodes(ctx.getLastChild()).get(0).second().getStart()));
        C sender = this.brigadierManager.senderMapper().map(source);
        this.commandManager.commandExecutor().executeCommand(sender, input, cloudContext -> cloudContext.store("_cloud_brigadier_native_sender", source));
        return 1;
    }

    public static <S> List<Pair<CommandNode<S>, StringRange>> parsedNodes(CommandContext<S> commandContext) {
        try {
            Method getNodesMethod = commandContext.getClass().getDeclaredMethod("getNodes", new Class[0]);
            Object nodes = getNodesMethod.invoke(commandContext, new Object[0]);
            if (nodes instanceof List) {
                return ParsedCommandNodeHandler.toPairList((List)nodes);
            }
            if (nodes instanceof Map) {
                return ((Map)nodes).entrySet().stream().map(entry -> Pair.of((CommandNode)entry.getKey(), (StringRange)entry.getValue())).collect(Collectors.toList());
            }
            throw new IllegalStateException();
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final class ParsedCommandNodeHandler {
        private ParsedCommandNodeHandler() {
        }

        private static <S> List<Pair<CommandNode<S>, StringRange>> toPairList(List<?> nodes) {
            return nodes.stream().map(n -> Pair.of(n.getNode(), n.getRange())).collect(Collectors.toList());
        }
    }
}

