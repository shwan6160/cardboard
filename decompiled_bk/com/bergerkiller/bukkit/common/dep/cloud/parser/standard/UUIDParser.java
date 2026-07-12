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
import java.util.UUID;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public final class UUIDParser<C>
implements ArgumentParser<C, UUID> {
    @API(status=API.Status.STABLE)
    public static <C> @NonNull ParserDescriptor<C, UUID> uuidParser() {
        return ParserDescriptor.of(new UUIDParser<C>(), UUID.class);
    }

    @API(status=API.Status.STABLE)
    public static <C> @NonNull CommandComponent.Builder<C, UUID> uuidComponent() {
        return CommandComponent.builder().parser(UUIDParser.uuidParser());
    }

    @Override
    public @NonNull ArgumentParseResult<UUID> parse(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        String input = commandInput.readString();
        try {
            UUID uuid = UUID.fromString(input);
            return ArgumentParseResult.success(uuid);
        }
        catch (IllegalArgumentException e) {
            return ArgumentParseResult.failure(new UUIDParseException(input, commandContext));
        }
    }

    @API(status=API.Status.STABLE)
    public static final class UUIDParseException
    extends ParserException {
        private final String input;

        public UUIDParseException(@NonNull String input, @NonNull CommandContext<?> context) {
            super(UUIDParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID, CaptionVariable.of("input", input));
            this.input = input;
        }

        public String input() {
            return this.input;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UUIDParseException that = (UUIDParseException)o;
            return this.input.equals(that.input);
        }

        public int hashCode() {
            return Objects.hash(this.input);
        }
    }
}

