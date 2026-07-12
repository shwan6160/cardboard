/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser.standard;

import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.caption.StandardCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class CharacterParser<C>
implements ArgumentParser<C, Character> {
    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, Character> characterParser() {
        return ParserDescriptor.of(new CharacterParser<C>(), Character.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, Character> characterComponent() {
        return CommandComponent.builder().parser(CharacterParser.characterParser());
    }

    @Override
    public @NonNull ArgumentParseResult<Character> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.peekString().length() != 1) {
            return ArgumentParseResult.failure(new CharParseException(commandInput.peekString(), commandContext));
        }
        return ArgumentParseResult.success(Character.valueOf(commandInput.read()));
    }

    @API(status=API.Status.STABLE)
    public static final class CharParseException
    extends ParserException {
        private final String input;

        public CharParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(CharacterParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String input() {
            return this.input;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CharParseException that = (CharParseException)o;
            return this.input.equals(that.input);
        }

        public int hashCode() {
            return Objects.hash(this.input);
        }
    }
}

