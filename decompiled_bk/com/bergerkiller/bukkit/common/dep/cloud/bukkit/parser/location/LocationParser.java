/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.BlockCommandSender
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.util.Vector
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCaptionKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandContextKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationCoordinate;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationCoordinateParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationCoordinateType;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.caption.CaptionVariable;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.exception.parsing.ParserException;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.IntegerParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.type.range.Range;
import java.util.List;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class LocationParser<C>
implements ArgumentParser<C, Location>,
BlockingSuggestionProvider.Strings<C> {
    private static final Range<Integer> SUGGESTION_RANGE = Range.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
    private final LocationCoordinateParser<C> locationCoordinateParser = new LocationCoordinateParser();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, Location> locationParser() {
        return ParserDescriptor.of(new LocationParser<C>(), Location.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, Location> locationComponent() {
        return CommandComponent.builder().parser(LocationParser.locationParser());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Location> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.remainingTokens() < 3) {
            return ArgumentParseResult.failure(new LocationParseException(commandContext, LocationParseException.FailureReason.WRONG_FORMAT, commandInput.remainingInput()));
        }
        LocationCoordinate[] coordinates = new LocationCoordinate[3];
        for (int i = 0; i < 3; ++i) {
            if (commandInput.peekString().isEmpty()) {
                return ArgumentParseResult.failure(new LocationParseException(commandContext, LocationParseException.FailureReason.WRONG_FORMAT, commandInput.remainingInput()));
            }
            ArgumentParseResult<@NonNull LocationCoordinate> coordinate = this.locationCoordinateParser.parse(commandContext, commandInput);
            if (coordinate.failure().isPresent()) {
                return ArgumentParseResult.failure(coordinate.failure().get());
            }
            coordinates[i] = coordinate.parsedValue().orElseThrow(NullPointerException::new);
        }
        CommandSender bukkitSender = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
        Location originalLocation = bukkitSender instanceof BlockCommandSender ? ((BlockCommandSender)bukkitSender).getBlock().getLocation() : (bukkitSender instanceof Entity ? ((Entity)bukkitSender).getLocation() : (Bukkit.getWorlds().isEmpty() ? new Location(null, 0.0, 0.0, 0.0) : new Location((World)Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0)));
        if (coordinates[0].type() == LocationCoordinateType.LOCAL != (coordinates[1].type() == LocationCoordinateType.LOCAL) || coordinates[0].type() == LocationCoordinateType.LOCAL != (coordinates[2].type() == LocationCoordinateType.LOCAL)) {
            return ArgumentParseResult.failure(new LocationParseException(commandContext, LocationParseException.FailureReason.MIXED_LOCAL_ABSOLUTE, ""));
        }
        if (coordinates[0].type() == LocationCoordinateType.ABSOLUTE) {
            originalLocation.setX(coordinates[0].coordinate());
        } else if (coordinates[0].type() == LocationCoordinateType.RELATIVE) {
            originalLocation.add(coordinates[0].coordinate(), 0.0, 0.0);
        }
        if (coordinates[1].type() == LocationCoordinateType.ABSOLUTE) {
            originalLocation.setY(coordinates[1].coordinate());
        } else if (coordinates[1].type() == LocationCoordinateType.RELATIVE) {
            originalLocation.add(0.0, coordinates[1].coordinate(), 0.0);
        }
        if (coordinates[2].type() == LocationCoordinateType.ABSOLUTE) {
            originalLocation.setZ(coordinates[2].coordinate());
        } else if (coordinates[2].type() == LocationCoordinateType.RELATIVE) {
            originalLocation.add(0.0, 0.0, coordinates[2].coordinate());
        } else {
            Vector declaredPos = new Vector(coordinates[0].coordinate(), coordinates[1].coordinate(), coordinates[2].coordinate());
            return ArgumentParseResult.success(LocationParser.toLocalSpace(originalLocation, declaredPos));
        }
        return ArgumentParseResult.success(originalLocation);
    }

    static @NonNull Location toLocalSpace(@NonNull Location originalLocation, @NonNull Vector declaredPos) {
        double cosYaw = Math.cos(LocationParser.toRadians(originalLocation.getYaw() + 90.0f));
        double sinYaw = Math.sin(LocationParser.toRadians(originalLocation.getYaw() + 90.0f));
        double cosPitch = Math.cos(LocationParser.toRadians(-originalLocation.getPitch()));
        double sinPitch = Math.sin(LocationParser.toRadians(-originalLocation.getPitch()));
        double cosNegYaw = Math.cos(LocationParser.toRadians(-originalLocation.getPitch() + 90.0f));
        double sinNegYaw = Math.sin(LocationParser.toRadians(-originalLocation.getPitch() + 90.0f));
        Vector zModifier = new Vector(cosYaw * cosPitch, sinPitch, sinYaw * cosPitch);
        Vector yModifier = new Vector(cosYaw * cosNegYaw, sinNegYaw, sinYaw * cosNegYaw);
        Vector xModifier = zModifier.crossProduct(yModifier).multiply(-1);
        double xOffset = LocationParser.dotProduct(declaredPos, xModifier.getX(), yModifier.getX(), zModifier.getX());
        double yOffset = LocationParser.dotProduct(declaredPos, xModifier.getY(), yModifier.getY(), zModifier.getY());
        double zOffset = LocationParser.dotProduct(declaredPos, xModifier.getZ(), yModifier.getZ(), zModifier.getZ());
        return originalLocation.add(xOffset, yOffset, zOffset);
    }

    private static double dotProduct(Vector location, double x, double y, double z) {
        return location.getX() * x + location.getY() * y + location.getZ() * z;
    }

    private static float toRadians(float degrees) {
        return degrees * (float)Math.PI / 180.0f;
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        return LocationParser.getSuggestions(3, commandContext, input);
    }

    static <C> @NonNull List<@NonNull String> getSuggestions(int components, @NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        CommandInput inputCopy = input.copy();
        int idx = input.cursor();
        for (int i = 0; i < components; ++i) {
            ArgumentParseResult<LocationCoordinate> coordinateResult;
            idx = input.cursor();
            if (!input.hasRemainingInput(true) || (coordinateResult = new LocationCoordinateParser<C>().parse(commandContext, input)).failure().isPresent()) break;
        }
        input.cursor(idx);
        if (input.hasRemainingInput() && (input.peek() == '~' || input.peek() == '^')) {
            input.read();
        }
        String prefix = inputCopy.difference(input, true);
        return IntegerParser.getSuggestions(SUGGESTION_RANGE, input).stream().map(string -> prefix + string).collect(Collectors.toList());
    }

    static class LocationParseException
    extends ParserException {
        protected LocationParseException(@NonNull CommandContext<?> context, @NonNull FailureReason reason, @NonNull String input) {
            super(LocationParser.class, context, reason.caption(), CaptionVariable.of("input", input));
        }

        public static enum FailureReason {
            WRONG_FORMAT(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT),
            MIXED_LOCAL_ABSOLUTE(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE);

            private final Caption caption;

            private FailureReason(Caption caption) {
                this.caption = caption;
            }

            public @NonNull Caption caption() {
                return this.caption;
            }
        }
    }
}

