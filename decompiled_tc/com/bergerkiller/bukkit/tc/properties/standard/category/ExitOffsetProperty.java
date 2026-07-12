/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.Command
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.command.CommandSender
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.properties.standard.category;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Argument;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.CommandDescription;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.type.ExitOffset;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

public final class ExitOffsetProperty
implements ICartProperty<ExitOffset> {
    @CommandTargetTrain
    @PropertyCheckPermission(value="exitoffset")
    @Command(value="train exit offset <dx> <dy> <dz>")
    @CommandDescription(value="Sets an offset relative to the cart where players exit it")
    private void trainSetOffsetProperty(CommandSender sender, TrainProperties properties, @Argument(value="dx") double dx, @Argument(value="dy") double dy, @Argument(value="dz") double dz) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(dx, dy, dz, old.getYaw(), old.getPitch()));
        this.trainGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitoffset")
    @Command(value="train exit location <posX> <posY> <posZ>")
    @CommandDescription(value="Sets world coordinates where players are teleported to when exiting")
    private void trainSetLocationProperty(CommandSender sender, TrainProperties properties, @Argument(value="posX") double posX, @Argument(value="posY") double posY, @Argument(value="posZ") double posZ) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.createAbsolute(posX, posY, posZ, old.getYaw(), old.getPitch()));
        this.trainGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="train exit rotation <yaw> <pitch>")
    @CommandDescription(value="Sets the rotation of the player relative to the cart where players exit it")
    private void trainSetRotationProperty(CommandSender sender, TrainProperties properties, @Argument(value="yaw") float yaw, @Argument(value="pitch") float pitch) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), yaw, pitch));
        this.trainGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitoffset")
    @Command(value="cart exit location <posX> <posY> <posZ>")
    @CommandDescription(value="Sets world coordinates where players are teleported to when exiting")
    private void cartSetlocationProperty(CommandSender sender, CartProperties properties, @Argument(value="posX") double posX, @Argument(value="posY") double posY, @Argument(value="posZ") double posZ) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.createAbsolute(posX, posY, posZ, old.getYaw(), old.getPitch()));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitoffset")
    @Command(value="cart exit offset <dx> <dy> <dz>")
    @CommandDescription(value="Sets an offset relative to the cart where players exit it")
    private void cartSetOffsetProperty(CommandSender sender, CartProperties properties, @Argument(value="dx") double dx, @Argument(value="dy") double dy, @Argument(value="dz") double dz) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(dx, dy, dz, old.getYaw(), old.getPitch()));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="cart exit rotation <yaw> <pitch>")
    @CommandDescription(value="Sets the rotation of the player relative to the cart where players exit it")
    private void cartSetRotationProperty(CommandSender sender, CartProperties properties, @Argument(value="yaw") float yaw, @Argument(value="pitch") float pitch) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), yaw, pitch));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="cart exit yaw <yaw>")
    @CommandDescription(value="Sets the yaw rotation relative to the cart exiting players are positioned at")
    private void cartSetRotationYawProperty(CommandSender sender, CartProperties properties, @Argument(value="yaw") float yaw) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), yaw, old.getPitch()));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="cart exit yaw free")
    @CommandDescription(value="Sets the yaw orientation of the player after exiting remains as it was before")
    private void cartSetRotationYawFreeProperty(CommandSender sender, CartProperties properties) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), Float.NaN, old.getPitch()));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="cart exit pitch <pitch>")
    @CommandDescription(value="Sets the pitch rotation relative to the cart exiting players are positioned at")
    private void cartSetRotationPitchProperty(CommandSender sender, CartProperties properties, @Argument(value="pitch") float pitch) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), old.getYaw(), pitch));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="cart exit pitch free")
    @CommandDescription(value="Sets the pitch orientation of the player after exiting remains as it was before")
    private void cartSetRotationPitchFreeProperty(CommandSender sender, CartProperties properties) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), old.getYaw(), Float.NaN));
        this.cartGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="train exit yaw <yaw>")
    @CommandDescription(value="Sets the yaw rotation relative to the cart exiting players are positioned at")
    private void trainSetRotationYawProperty(CommandSender sender, TrainProperties properties, @Argument(value="yaw") float yaw) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), yaw, old.getPitch()));
        this.trainGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="train exit yaw free")
    @CommandDescription(value="Sets the yaw orientation of the player after exiting remains as it was before")
    private void trainSetRotationYawFreeProperty(CommandSender sender, TrainProperties properties) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), Float.NaN, old.getPitch()));
        this.trainGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="train exit pitch <pitch>")
    @CommandDescription(value="Sets the pitch rotation relative to the cart exiting players are positioned at")
    private void trainSetRotationPitchProperty(CommandSender sender, TrainProperties properties, @Argument(value="pitch") float pitch) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), old.getYaw(), pitch));
        this.trainGetProperty(sender, properties);
    }

    @CommandTargetTrain
    @PropertyCheckPermission(value="exitrotation")
    @Command(value="train exit pitch free")
    @CommandDescription(value="Sets the pitch orientation of the player after exiting remains as it was before")
    private void trainSetRotationPitchFreeProperty(CommandSender sender, TrainProperties properties) {
        ExitOffset old = properties.get(this);
        properties.set(this, ExitOffset.create(old.getPosition(), old.getYaw(), Float.NaN));
        this.trainGetProperty(sender, properties);
    }

    @Command(value="train exit")
    @CommandDescription(value="Displays the current exit offset and rotation set for the train")
    private void trainGetProperty(CommandSender sender, TrainProperties properties) {
        this.showProperty(sender, "Train", properties.get(this));
    }

    @Command(value="cart exit")
    @CommandDescription(value="Displays the current exit offset and rotation set for the cart")
    private void cartGetProperty(CommandSender sender, CartProperties properties) {
        this.showProperty(sender, "Cart", properties.get(this));
    }

    private void showProperty(CommandSender sender, String prefix, ExitOffset offset) {
        MessageBuilder builder = new MessageBuilder();
        if (offset.isAbsolute()) {
            builder.yellow(new Object[]{prefix + " exit coordinates are set to:"});
            builder.newLine().yellow(new Object[]{"  Location X: "}).white(new Object[]{offset.getX()});
            builder.newLine().yellow(new Object[]{"  Location Y: "}).white(new Object[]{offset.getY()});
            builder.newLine().yellow(new Object[]{"  Location Z: "}).white(new Object[]{offset.getZ()});
        } else {
            builder.yellow(new Object[]{prefix + " exit offset is set to:"});
            builder.newLine().yellow(new Object[]{"  Relative X: "}).white(new Object[]{offset.getX()});
            builder.newLine().yellow(new Object[]{"  Relative Y: "}).white(new Object[]{offset.getY()});
            builder.newLine().yellow(new Object[]{"  Relative Z: "}).white(new Object[]{offset.getZ()});
        }
        if (offset.hasLockedYaw()) {
            builder.newLine().yellow(new Object[]{"  Yaw: "}).white(new Object[]{Float.valueOf(offset.getYaw())});
        } else {
            builder.newLine().yellow(new Object[]{"  Yaw: "}).green(new Object[]{"Not set (free)"});
        }
        if (offset.hasLockedPitch()) {
            builder.newLine().yellow(new Object[]{"  Pitch: "}).white(new Object[]{Float.valueOf(offset.getPitch())});
        } else {
            builder.newLine().yellow(new Object[]{"  Pitch: "}).green(new Object[]{"Not set (free)"});
        }
        builder.send(sender);
    }

    @Override
    public String getPermissionName() {
        return "exit offset";
    }

    @PropertyParser(value="exitlocation", processPerCart=true)
    public ExitOffset parseLocation(PropertyParseContext<ExitOffset> context) {
        Vector vec = Util.parseVector(context.input(), null);
        if (vec == null) {
            throw new PropertyInvalidInputException("Not a vector");
        }
        return ExitOffset.createAbsolute(vec, context.current().getYaw(), context.current().getPitch());
    }

    @PropertyParser(value="exitoffset", processPerCart=true)
    public ExitOffset parseOffset(PropertyParseContext<ExitOffset> context) {
        Vector vec = Util.parseVector(context.input(), null);
        if (vec == null) {
            throw new PropertyInvalidInputException("Not a vector");
        }
        if (vec.length() > TCConfig.maxEjectDistance) {
            vec.normalize().multiply(TCConfig.maxEjectDistance);
        }
        return ExitOffset.create(vec, context.current().getYaw(), context.current().getPitch());
    }

    @PropertyParser(value="exityaw", processPerCart=true)
    public ExitOffset parseYaw(PropertyParseContext<ExitOffset> context) {
        return ExitOffset.create(context.current().isAbsolute(), context.current().getPosition(), context.inputFloatOrNaN(), context.current().getPitch());
    }

    @PropertyParser(value="exitpitch", processPerCart=true)
    public ExitOffset parsePitch(PropertyParseContext<ExitOffset> context) {
        return ExitOffset.create(context.current().isAbsolute(), context.current().getPosition(), context.current().getYaw(), context.inputFloatOrNaN());
    }

    @PropertyParser(value="exitrot|exitrotation", processPerCart=true)
    public ExitOffset parseRotation(PropertyParseContext<ExitOffset> context) {
        float new_pitch;
        float new_yaw;
        String[] angletext = Util.splitBySeparator(context.input());
        if (angletext.length == 2) {
            new_yaw = ParseUtil.parseFloat((String)angletext[0], (float)Float.NaN);
            new_pitch = ParseUtil.parseFloat((String)angletext[1], (float)Float.NaN);
        } else if (angletext.length == 1) {
            new_yaw = ParseUtil.parseFloat((String)angletext[0], (float)Float.NaN);
            new_pitch = Float.NaN;
        } else {
            new_yaw = Float.NaN;
            new_pitch = Float.NaN;
        }
        return ExitOffset.create(context.current().isAbsolute(), context.current().getPosition(), new_yaw, new_pitch);
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_EXIT_OFFSET.has(sender);
    }

    @Override
    public ExitOffset getDefault() {
        return ExitOffset.DEFAULT;
    }

    @Override
    public Optional<ExitOffset> readFromConfig(ConfigurationNode config) {
        if (config.contains("exitOffset") || config.contains("exitYaw") || config.contains("exitPitch")) {
            Vector absoluteCoords = (Vector)config.getOrDefault("exitLocation", Vector.class, null);
            Vector offset = absoluteCoords == null ? (Vector)config.getOrDefault("exitOffset", (Object)new Vector()) : null;
            float yaw = ((Float)config.getOrDefault("exitYaw", (Object)Float.valueOf(Float.NaN))).floatValue();
            float pitch = ((Float)config.getOrDefault("exitPitch", (Object)Float.valueOf(Float.NaN))).floatValue();
            if (!((Boolean)config.getOrDefault("exitYawLocked", (Object)false)).booleanValue()) {
                yaw = Float.NaN;
            }
            if (!((Boolean)config.getOrDefault("exitPitchLocked", (Object)false)).booleanValue()) {
                pitch = Float.NaN;
            }
            if (absoluteCoords != null) {
                return Optional.of(ExitOffset.createAbsolute(absoluteCoords, yaw, pitch));
            }
            return Optional.of(ExitOffset.create(offset, yaw, pitch));
        }
        return Optional.empty();
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<ExitOffset> value) {
        if (value.isPresent()) {
            ExitOffset data = value.get();
            if (data.isAbsolute()) {
                config.set("exitLocation", (Object)data.getPosition());
                config.remove("exitOffset");
            } else {
                config.set("exitOffset", (Object)data.getPosition());
                config.remove("exitLocation");
            }
            if (data.hasLockedYaw()) {
                config.set("exitYawLocked", (Object)true);
                config.set("exitYaw", (Object)Float.valueOf(data.getYaw()));
            } else {
                config.set("exitYawLocked", (Object)false);
                config.set("exitYaw", (Object)Float.valueOf(0.0f));
            }
            if (data.hasLockedPitch()) {
                config.set("exitPitchLocked", (Object)true);
                config.set("exitPitch", (Object)Float.valueOf(data.getPitch()));
            } else {
                config.set("exitPitchLocked", (Object)false);
                config.set("exitPitch", (Object)Float.valueOf(0.0f));
            }
        } else {
            config.remove("exitOffset");
            config.remove("exitLocation");
            config.remove("exitYaw");
            config.remove("exitYawLocked");
            config.remove("exitPitch");
            config.remove("exitPitchLocked");
        }
    }
}

