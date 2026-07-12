/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationCoordinate;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationCoordinateType;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.DoubleParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class LocationCoordinateParser<C>
implements ArgumentParser<C, LocationCoordinate> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull LocationCoordinate> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        double coordinate;
        LocationCoordinateType locationCoordinateType;
        String input = commandInput.skipWhitespace().peekString();
        if (commandInput.peek() == '^') {
            locationCoordinateType = LocationCoordinateType.LOCAL;
            commandInput.moveCursor(1);
        } else if (commandInput.peek() == '~') {
            locationCoordinateType = LocationCoordinateType.RELATIVE;
            commandInput.moveCursor(1);
        } else {
            locationCoordinateType = LocationCoordinateType.ABSOLUTE;
        }
        try {
            boolean empty = commandInput.peekString().isEmpty() || commandInput.peek() == ' ';
            double d = coordinate = empty ? 0.0 : commandInput.readDouble();
            if (commandInput.hasRemainingInput()) {
                commandInput.skipWhitespace();
            }
        }
        catch (Exception e) {
            return ArgumentParseResult.failure(new DoubleParser.DoubleParseException(input, new DoubleParser(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), commandContext));
        }
        return ArgumentParseResult.success(LocationCoordinate.of(locationCoordinateType, coordinate));
    }
}

