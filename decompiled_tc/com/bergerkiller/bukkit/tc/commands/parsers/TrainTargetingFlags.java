/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.cloud.CloudLocalizedException
 *  com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.Command$Builder
 *  com.bergerkiller.bukkit.common.dep.cloud.annotations.BuilderModifier
 *  com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.WorldParser
 *  com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext
 *  com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider$Strings
 *  com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.localization.ILocalizationEnum
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.jetbrains.annotations.NotNull
 */
package com.bergerkiller.bukkit.tc.commands.parsers;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.cloud.CloudLocalizedException;
import com.bergerkiller.bukkit.common.cloud.parsers.QuotedArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.BuilderModifier;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.WorldParser;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.flag.CommandFlag;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.localization.ILocalizationEnum;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.commands.parsers.NearPosition;
import com.bergerkiller.bukkit.tc.commands.suggestions.TrainNameSuggestionProvider;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.exception.command.NoTrainNearbyException;
import com.bergerkiller.bukkit.tc.exception.command.NoTrainSelectedException;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class TrainTargetingFlags
implements BuilderModifier<CommandTargetTrain, CommandSender> {
    public static final TrainTargetingFlags INSTANCE = new TrainTargetingFlags();
    private final CommandFlag<TrainProperties> flagTrain = CommandFlag.builder((String)"train").withComponent(CommandComponent.builder((String)"train_name", TrainTargetingFlags.trainFlagParser())).build();
    private final CommandFlag<CartSelectorResult> flagCart = CommandFlag.builder((String)"cart").withComponent(CommandComponent.builder((String)"cart_uuid", TrainTargetingFlags.cartFlagParser())).build();
    private final CommandFlag<World> flagWorld = CommandFlag.builder((String)"world").withComponent(CommandComponent.builder((String)"world_name", (ParserDescriptor)WorldParser.worldParser())).build();
    private final CommandFlag<NearPosition> flagNear = CommandFlag.builder((String)"near").withComponent(CommandComponent.builder((String)"where", NearPosition.nearParser())).build();
    private final CommandFlag<Void> flagNearest = CommandFlag.builder((String)"nearest").build();

    private TrainTargetingFlags() {
    }

    public boolean isTrainTargetingFlag(CommandFlag<?> flag) {
        return flag == this.flagTrain || flag == this.flagCart || flag == this.flagWorld || flag == this.flagNear || flag == this.flagNearest;
    }

    public Command.Builder<? extends CommandSender> modifyBuilder(@NonNull CommandTargetTrain annotation, Command.Builder<CommandSender> builder) {
        builder = builder.flag(this.flagTrain).flag(this.flagCart);
        builder = builder.flag(this.flagWorld).flag(this.flagNear).flag(this.flagNearest);
        return builder;
    }

    public CartProperties findCartProperties(CommandContext<CommandSender> context) {
        BlockLocation loc;
        TrainProperties trainProperties = null;
        CartProperties cartProperties = null;
        if (context.flags().hasFlag(this.flagTrain.name()) && !(trainProperties = (TrainProperties)context.flags().get(this.flagTrain.name())).isEmpty()) {
            cartProperties = trainProperties.get(0);
        }
        World atWorld = (World)context.flags().getValue(this.flagWorld.name(), null);
        if (context.flags().hasFlag(this.flagNear.name()) || context.flags().hasFlag(this.flagNearest.name())) {
            Optional<MemberResult> result;
            NearPosition near;
            Permission.COMMAND_TARGET_NEAR.handle((CommandSender)context.sender());
            if (context.flags().hasFlag(this.flagNear.name())) {
                near = (NearPosition)context.flags().get(this.flagNear.name());
            } else {
                ArgumentParseResult<NearPosition> parseResult = NearPosition.parseNearest(context);
                if (parseResult.failure().isPresent()) {
                    Throwable t = (Throwable)parseResult.failure().get();
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException)t;
                    }
                    return null;
                }
                near = (NearPosition)parseResult.parsedValue().get();
            }
            if (atWorld != null) {
                near.at.setWorld(atWorld);
            }
            List nearby = WorldUtil.getNearbyEntities((Location)near.at, (double)near.radius, (double)near.radius, (double)near.radius);
            double distanceSquaredMax = near.radius * near.radius;
            Stream<MemberResult> nearbyMembers = nearby.stream().map(MinecartMemberStore::getFromEntity).filter(Objects::nonNull).map(member -> new MemberResult((MinecartMember<?>)member, near.at)).filter(r -> r.distanceSquared <= distanceSquaredMax);
            if (trainProperties != null) {
                TrainProperties inTrain = trainProperties;
                nearbyMembers = nearbyMembers.filter(r -> r.member.getProperties().getTrainProperties() == inTrain);
            }
            if ((result = nearbyMembers.sorted().findFirst()).isPresent()) {
                cartProperties = result.get().member.getProperties();
                trainProperties = cartProperties.getTrainProperties();
            } else {
                throw new NoTrainNearbyException();
            }
        }
        if (cartProperties == null && trainProperties == null && context.sender() instanceof Player && (cartProperties = ((TrainCartsPlayer)context.inject(TrainCartsPlayer.class).get()).getEditedCart()) != null) {
            trainProperties = cartProperties.getTrainProperties();
        }
        if (context.flags().hasFlag(this.flagCart.name())) {
            CartSelectorResult cartSelector = (CartSelectorResult)context.flags().get(this.flagCart.name());
            if (cartSelector.cart_result != null) {
                if (trainProperties != null && trainProperties != cartSelector.cart_result.getTrainProperties()) {
                    throw new CloudLocalizedException(context, (ILocalizationEnum)Localization.COMMAND_CART_NOT_FOUND_IN_TRAIN, new String[]{"uuid=" + cartSelector.cart_result.getUUID().toString()});
                }
                cartProperties = cartSelector.cart_result;
                trainProperties = cartProperties.getTrainProperties();
            } else {
                int indexInCart;
                if (trainProperties == null) {
                    throw new NoTrainSelectedException();
                }
                int n = indexInCart = cartSelector.index_in_train < 0 ? trainProperties.size() + cartSelector.index_in_train : cartSelector.index_in_train;
                if (indexInCart >= 0 && indexInCart < trainProperties.size()) {
                    MinecartGroup group = trainProperties.getHolder();
                    cartProperties = group == null ? trainProperties.get(indexInCart) : ((MinecartMember)group.get(indexInCart)).getProperties();
                } else {
                    throw new CloudLocalizedException(context, (ILocalizationEnum)Localization.COMMAND_CART_NOT_FOUND_IN_TRAIN, new String[]{"index=" + cartSelector.index_in_train});
                }
            }
        }
        if (cartProperties != null && atWorld != null && ((loc = cartProperties.getLocation()) == null || loc.getWorld() != atWorld)) {
            throw new NoTrainSelectedException();
        }
        if (cartProperties == null) {
            throw new NoTrainSelectedException();
        }
        return cartProperties;
    }

    @NotNull
    private static ParserDescriptor<CommandSender, CartSelectorResult> cartFlagParser() {
        return ParserDescriptor.of((ArgumentParser)new CartFlagParser(), CartSelectorResult.class);
    }

    @NotNull
    private static ParserDescriptor<CommandSender, TrainProperties> trainFlagParser() {
        return new TrainFlagParser().createDescriptor(TrainProperties.class);
    }

    private static class MemberResult
    implements Comparable<MemberResult> {
        public final MinecartMember<?> member;
        public final double distanceSquared;

        public MemberResult(MinecartMember<?> member, Location at) {
            this.member = member;
            this.distanceSquared = ((CommonMinecart)member.getEntity()).loc.distanceSquared(at);
        }

        @Override
        public int compareTo(MemberResult o) {
            return Double.compare(this.distanceSquared, o.distanceSquared);
        }
    }

    private static class CartSelectorResult {
        public final CartProperties cart_result;
        public final int index_in_train;

        public CartSelectorResult(int index) {
            this.cart_result = null;
            this.index_in_train = index;
        }

        public CartSelectorResult(CartProperties result) {
            this.cart_result = result;
            this.index_in_train = Integer.MAX_VALUE;
        }
    }

    private static class CartFlagParser
    implements ArgumentParser<CommandSender, CartSelectorResult>,
    BlockingSuggestionProvider.Strings<CommandSender> {
        private CartFlagParser() {
        }

        public @NonNull ArgumentParseResult<@NonNull CartSelectorResult> parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
            String uuidName = commandInput.readString();
            if (uuidName.equalsIgnoreCase("head")) {
                return ArgumentParseResult.success((Object)new CartSelectorResult(0));
            }
            if (uuidName.equalsIgnoreCase("tail")) {
                return ArgumentParseResult.success((Object)new CartSelectorResult(-1));
            }
            int numCart = ParseUtil.parseInt((String)uuidName, (int)Integer.MIN_VALUE);
            if (numCart != Integer.MIN_VALUE) {
                return ArgumentParseResult.success((Object)new CartSelectorResult(numCart));
            }
            try {
                UUID uuid = UUID.fromString(uuidName);
                CartProperties prop = CartPropertiesStore.getByUUID(uuid);
                if (prop == null) {
                    return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_CART_NOT_FOUND_BY_UUID, new String[]{uuid.toString()}));
                }
                return ArgumentParseResult.success((Object)new CartSelectorResult(prop));
            }
            catch (IllegalArgumentException ex) {
                return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_CART_NOT_FOUND_BY_UUID, new String[]{uuidName}));
            }
        }

        public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<CommandSender> commandContext, @NonNull CommandInput input) {
            if (input.isEmpty()) {
                return Stream.concat(Stream.of("<uuid>", "head", "tail"), IntStream.range(0, 10).mapToObj(Integer::toString)).collect(Collectors.toList());
            }
            return IntStream.range(0, 10).mapToObj(Integer::toString).map(o -> input + o).collect(Collectors.toList());
        }
    }

    private static class TrainFlagParser
    implements QuotedArgumentParser<CommandSender, TrainProperties> {
        private final TrainNameSuggestionProvider suggestionProvider = new TrainNameSuggestionProvider();

        private TrainFlagParser() {
        }

        public @NonNull ArgumentParseResult<@NonNull TrainProperties> parseQuotedString(@NonNull CommandContext<@NonNull CommandSender> commandContext, String inputString) {
            TrainProperties properties = TrainPropertiesStore.get(inputString);
            if (properties == null) {
                properties = TrainPropertiesStore.getRelaxed(inputString);
            }
            if (properties == null) {
                return ArgumentParseResult.failure((Throwable)new CloudLocalizedException(commandContext, (ILocalizationEnum)Localization.COMMAND_TRAIN_NOT_FOUND, new String[]{inputString}));
            }
            commandContext.set("trainProperties", (Object)properties);
            return ArgumentParseResult.success((Object)properties);
        }

        public @NonNull SuggestionProvider<CommandSender> suggestionProvider() {
            return this.suggestionProvider;
        }
    }
}

