/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.brigadier.context;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ContextChain<S> {
    private final List<CommandContext<S>> modifiers;
    private final CommandContext<S> executable;
    private ContextChain<S> nextStageCache = null;

    public ContextChain(List<CommandContext<S>> modifiers, CommandContext<S> executable) {
        if (executable.getCommand() == null) {
            throw new IllegalArgumentException("Last command in chain must be executable");
        }
        this.modifiers = modifiers;
        this.executable = executable;
    }

    public static <S> Optional<ContextChain<S>> tryFlatten(CommandContext<S> rootContext) {
        ArrayList<CommandContext<S>> modifiers = new ArrayList<CommandContext<S>>();
        CommandContext<S> current = rootContext;
        while (true) {
            CommandContext<S> child;
            if ((child = current.getChild()) == null) {
                if (current.getCommand() == null) {
                    return Optional.empty();
                }
                return Optional.of(new ContextChain<S>(modifiers, current));
            }
            modifiers.add(current);
            current = child;
        }
    }

    public static <S> Collection<S> runModifier(CommandContext<S> modifier, S source, ResultConsumer<S> resultConsumer, boolean forkedMode) throws CommandSyntaxException {
        RedirectModifier<S> sourceModifier = modifier.getRedirectModifier();
        if (sourceModifier == null) {
            return Collections.singleton(source);
        }
        CommandContext<S> contextToUse = modifier.copyFor(source);
        try {
            return sourceModifier.apply(contextToUse);
        }
        catch (CommandSyntaxException ex) {
            resultConsumer.onCommandComplete(contextToUse, false, 0);
            if (forkedMode) {
                return Collections.emptyList();
            }
            throw ex;
        }
    }

    public static <S> int runExecutable(CommandContext<S> executable, S source, ResultConsumer<S> resultConsumer, boolean forkedMode) throws CommandSyntaxException {
        CommandContext<S> contextToUse = executable.copyFor(source);
        try {
            int result = executable.getCommand().run(contextToUse);
            resultConsumer.onCommandComplete(contextToUse, true, result);
            return forkedMode ? 1 : result;
        }
        catch (CommandSyntaxException ex) {
            resultConsumer.onCommandComplete(contextToUse, false, 0);
            if (forkedMode) {
                return 0;
            }
            throw ex;
        }
    }

    public int executeAll(S source, ResultConsumer<S> resultConsumer) throws CommandSyntaxException {
        if (this.modifiers.isEmpty()) {
            return ContextChain.runExecutable(this.executable, source, resultConsumer, false);
        }
        boolean forkedMode = false;
        List<S> currentSources = Collections.singletonList(source);
        for (CommandContext<S> modifier : this.modifiers) {
            forkedMode |= modifier.isForked();
            ArrayList<S> nextSources = new ArrayList<S>();
            for (S sourceToRun : currentSources) {
                nextSources.addAll(ContextChain.runModifier(modifier, sourceToRun, resultConsumer, forkedMode));
            }
            if (nextSources.isEmpty()) {
                return 0;
            }
            currentSources = nextSources;
        }
        int result = 0;
        for (S executionSource : currentSources) {
            result += ContextChain.runExecutable(this.executable, executionSource, resultConsumer, forkedMode);
        }
        return result;
    }

    public Stage getStage() {
        return this.modifiers.isEmpty() ? Stage.EXECUTE : Stage.MODIFY;
    }

    public CommandContext<S> getTopContext() {
        if (this.modifiers.isEmpty()) {
            return this.executable;
        }
        return this.modifiers.get(0);
    }

    public ContextChain<S> nextStage() {
        int modifierCount = this.modifiers.size();
        if (modifierCount == 0) {
            return null;
        }
        if (this.nextStageCache == null) {
            this.nextStageCache = new ContextChain<S>(this.modifiers.subList(1, modifierCount), this.executable);
        }
        return this.nextStageCache;
    }

    public static enum Stage {
        MODIFY,
        EXECUTE;

    }
}

