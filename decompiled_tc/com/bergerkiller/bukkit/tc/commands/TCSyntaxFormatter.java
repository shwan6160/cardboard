/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.CommandManager
 *  com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent
 *  com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent$ComponentType
 *  com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlagParser
 *  com.bergerkiller.bukkit.common.dep.cloud.permission.Permission
 *  com.bergerkiller.bukkit.common.dep.cloud.syntax.CommandSyntaxFormatter
 *  com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.tc.commands;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandNode;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlagParser;
import com.bergerkiller.bukkit.common.dep.cloud.permission.Permission;
import com.bergerkiller.bukkit.common.dep.cloud.syntax.CommandSyntaxFormatter;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.tc.commands.parsers.TrainTargetingFlags;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TCSyntaxFormatter<C>
implements CommandSyntaxFormatter<C> {
    private final CommandManager<C> manager;

    public TCSyntaxFormatter(@NonNull CommandManager<C> manager) {
        this.manager = manager;
    }

    public final @NonNull String apply(@Nullable C sender, @NonNull List<@NonNull CommandComponent<C>> commandComponents, @Nullable CommandNode<C> node) {
        return this.apply(commandComponents, node, (CommandNode<C> n) -> {
            if (sender == null) {
                return true;
            }
            Map accessMap = (Map)n.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
            for (Map.Entry entry : accessMap.entrySet()) {
                if (!GenericTypeReflector.isSuperType((Type)((Type)entry.getKey()), sender.getClass()) || !this.manager.testPermission(sender, (Permission)entry.getValue()).allowed()) continue;
                return true;
            }
            return false;
        });
    }

    private @NonNull String apply(@NonNull List<@NonNull CommandComponent<C>> commandComponents, @Nullable CommandNode<C> node, @NonNull Predicate<@NonNull CommandNode<C>> filter) {
        FormattingInstance formattingInstance = this.createInstance();
        Iterator<CommandComponent<C>> iterator = commandComponents.iterator();
        while (iterator.hasNext()) {
            CommandComponent<C> commandComponent = iterator.next();
            if (commandComponent.type() == CommandComponent.ComponentType.LITERAL) {
                formattingInstance.appendLiteral(commandComponent);
            } else if (commandComponent.parser() instanceof AggregateParser) {
                AggregateParser aggregateParser = (AggregateParser)commandComponent.parser();
                formattingInstance.appendAggregate(commandComponent, aggregateParser);
            } else if (commandComponent.type() == CommandComponent.ComponentType.FLAG) {
                formattingInstance.appendFlag((CommandFlagParser)commandComponent.parser());
            } else if (commandComponent.required()) {
                formattingInstance.appendRequired(commandComponent);
            } else {
                formattingInstance.appendOptional(commandComponent);
            }
            if (!iterator.hasNext()) continue;
            formattingInstance.appendBlankSpace();
        }
        CommandNode tail = node;
        while (tail != null && !tail.isLeaf() && filter.test(tail)) {
            if (tail.children().size() > 1) {
                formattingInstance.appendBlankSpace();
                Iterator childIterator = tail.children().stream().filter(filter).iterator();
                while (childIterator.hasNext()) {
                    CommandNode child = (CommandNode)childIterator.next();
                    if (child.component() == null) continue;
                    switch (child.component().type()) {
                        case LITERAL: {
                            formattingInstance.appendName(child.component().name());
                            break;
                        }
                        case REQUIRED_VARIABLE: {
                            formattingInstance.appendRequired(child.component());
                            break;
                        }
                        case OPTIONAL_VARIABLE: {
                            formattingInstance.appendOptional(child.component());
                            break;
                        }
                    }
                    if (!childIterator.hasNext()) continue;
                    formattingInstance.appendPipe();
                }
                break;
            }
            if (!filter.test((CommandNode)tail.children().get(0))) break;
            CommandComponent component = ((CommandNode)tail.children().get(0)).component();
            if (component.parser() instanceof AggregateParser) {
                AggregateParser aggregateParser = (AggregateParser)component.parser();
                formattingInstance.appendBlankSpace();
                formattingInstance.appendAggregate(component, aggregateParser);
            } else if (component.type() == CommandComponent.ComponentType.FLAG) {
                formattingInstance.appendBlankSpace();
                formattingInstance.appendFlag((CommandFlagParser)component.parser());
            } else if (component.type() == CommandComponent.ComponentType.LITERAL) {
                formattingInstance.appendBlankSpace();
                formattingInstance.appendLiteral(component);
            } else {
                formattingInstance.appendBlankSpace();
                if (component.required()) {
                    formattingInstance.appendRequired(component);
                } else {
                    formattingInstance.appendOptional(component);
                }
            }
            tail = (CommandNode)tail.children().get(0);
        }
        return formattingInstance.toString();
    }

    protected @NonNull FormattingInstance createInstance() {
        return new FormattingInstance();
    }

    static {
        new FormattingInstance();
    }

    public static class FormattingInstance {
        private final StringBuilder builder = new StringBuilder();

        protected FormattingInstance() {
        }

        public final @NonNull String toString() {
            return this.builder.toString();
        }

        public void appendLiteral(@NonNull CommandComponent<?> literal) {
            this.appendName(literal.name());
        }

        @API(status=API.Status.STABLE)
        public void appendAggregate(@NonNull CommandComponent<?> component, @NonNull AggregateParser<?, ?> parser) {
            String prefix = component.required() ? this.requiredPrefix() : this.optionalPrefix();
            String suffix = component.required() ? this.requiredSuffix() : this.optionalSuffix();
            this.builder.append(prefix);
            Iterator innerComponents = parser.components().iterator();
            while (innerComponents.hasNext()) {
                CommandComponent innerComponent = (CommandComponent)innerComponents.next();
                this.builder.append(prefix);
                this.appendName(innerComponent.name());
                this.builder.append(suffix);
                if (!innerComponents.hasNext()) continue;
                this.builder.append(' ');
            }
            this.builder.append(suffix);
        }

        public void appendFlag(@NonNull CommandFlagParser<?> flagParser) {
            Iterator flagIterator = flagParser.flags().stream().filter(this::isFlagVisible).iterator();
            if (!flagIterator.hasNext()) {
                return;
            }
            this.builder.append(this.optionalPrefix());
            while (flagIterator.hasNext()) {
                CommandFlag flag = (CommandFlag)flagIterator.next();
                this.appendName(String.format("--%s", flag.name()));
                if (flag.commandComponent() != null) {
                    this.builder.append(' ');
                    this.builder.append(this.optionalPrefix());
                    this.appendName(flag.commandComponent().name());
                    this.builder.append(this.optionalSuffix());
                }
                if (!flagIterator.hasNext()) continue;
                this.appendBlankSpace();
                this.appendPipe();
                this.appendBlankSpace();
            }
            this.builder.append(this.optionalSuffix());
        }

        public boolean isFlagVisible(CommandFlag<?> flag) {
            return !TrainTargetingFlags.INSTANCE.isTrainTargetingFlag(flag);
        }

        public void appendRequired(@NonNull CommandComponent<?> argument) {
            this.builder.append(this.requiredPrefix());
            this.appendName(argument.name());
            this.builder.append(this.requiredSuffix());
        }

        public void appendOptional(@NonNull CommandComponent<?> argument) {
            this.builder.append(this.optionalPrefix());
            this.appendName(argument.name());
            this.builder.append(this.optionalSuffix());
        }

        public void appendPipe() {
            this.builder.append("|");
        }

        public void appendName(@NonNull String name) {
            this.builder.append(name);
        }

        public @NonNull String requiredPrefix() {
            return "<";
        }

        public @NonNull String requiredSuffix() {
            return ">";
        }

        public @NonNull String optionalPrefix() {
            return "[";
        }

        public @NonNull String optionalSuffix() {
            return "]";
        }

        public void appendBlankSpace() {
            this.builder.append(' ');
        }
    }
}

