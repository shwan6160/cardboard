/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.dep.cloud.type.Either
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.commands.argument;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.type.Either;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.commands.parsers.DirectionParser;
import com.bergerkiller.bukkit.tc.commands.parsers.FormattedSpeedParser;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import java.util.concurrent.CompletableFuture;
import org.bukkit.command.CommandSender;

public final class DirectionOrFormattedSpeed {
    private final Direction direction;
    private final FormattedSpeed formattedSpeed;

    public DirectionOrFormattedSpeed(Direction direction) {
        this.direction = direction;
        this.formattedSpeed = null;
    }

    public DirectionOrFormattedSpeed(FormattedSpeed formattedSpeed) {
        this.direction = null;
        this.formattedSpeed = formattedSpeed;
    }

    public static ParserDescriptor<CommandSender, DirectionOrFormattedSpeed> directionOrFormattedSpeedParser() {
        return ArgumentParser.firstOf(FormattedSpeedParser.formattedSpeedParser(false), DirectionParser.directionParser()).mapSuccess(DirectionOrFormattedSpeed.class, (context, either) -> CompletableFuture.completedFuture(DirectionOrFormattedSpeed.of((Either<FormattedSpeed, Direction>)either)));
    }

    public static DirectionOrFormattedSpeed of(Either<FormattedSpeed, Direction> either) {
        return (DirectionOrFormattedSpeed)either.mapEither(DirectionOrFormattedSpeed::new, DirectionOrFormattedSpeed::new);
    }

    public boolean hasDirection() {
        return this.direction != null;
    }

    public boolean hasFormattedSpeed() {
        return this.formattedSpeed != null;
    }

    public Direction getDirection() {
        if (this.direction == null) {
            throw new UnsupportedOperationException("Argument has no direction");
        }
        return this.direction;
    }

    public FormattedSpeed getFormattedSpeed() {
        if (this.formattedSpeed == null) {
            throw new UnsupportedOperationException("Argument has no formatted speed");
        }
        return this.formattedSpeed;
    }
}

