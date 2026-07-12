/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationParser
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.standard.DoubleParser
 *  com.bergerkiller.bukkit.common.dep.typetoken.TypeToken
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationParser;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.aggregate.AggregateParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.DoubleParser;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.util.LinkedList;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class NearPosition {
    public final Location at;
    public final double radius;

    public NearPosition(Location at, double radius) {
        this.at = at;
        this.radius = radius;
    }

    public String toString() {
        return "near{world=" + this.at.getWorld().getName() + ", x=" + this.at.getX() + ", y=" + this.at.getY() + ", z=" + this.at.getZ() + ", radius=" + this.radius + "}";
    }

    public static ArgumentParseResult<NearPosition> parseNearest(CommandContext<CommandSender> commandContext) {
        LinkedList<String> atSenderQueue = new LinkedList<String>();
        atSenderQueue.add("~");
        atSenderQueue.add("~");
        atSenderQueue.add("~");
        ArgumentParseResult locationResult = new LocationParser().parse(commandContext, CommandInput.of(atSenderQueue));
        if (locationResult.failure().isPresent()) {
            return ArgumentParseResult.failure((Throwable)((Throwable)locationResult.failure().get()));
        }
        return ArgumentParseResult.success((Object)new NearPosition((Location)locationResult.parsedValue().get(), 128.0));
    }

    public static ParserDescriptor<CommandSender, NearPosition> nearParser() {
        return AggregateParser.pairBuilder((String)"location", (ParserDescriptor)LocationParser.locationParser(), (String)"radius", (ParserDescriptor)DoubleParser.doubleParser((double)0.0)).withMapper(TypeToken.get(NearPosition.class), (context, location, radius) -> ArgumentParseResult.successFuture((Object)new NearPosition((Location)location, (double)radius))).build();
    }
}

