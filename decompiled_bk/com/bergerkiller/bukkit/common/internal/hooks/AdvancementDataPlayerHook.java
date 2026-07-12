/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.advancement.Advancement
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.InputTypeMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@ClassHook.HookPackage(value="net.minecraft.server")
@ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.advancements.AdvancementHolder"), @ClassHook.HookImport(value="net.minecraft.advancements.Advancement")})
@ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class AdvancementDataPlayerHook
extends ClassHook<AdvancementDataPlayerHook> {
    private static Optional<String> LOGIC_FAILURE = Optional.empty();
    private static final Handler HANDLER = LogicUtil.tryCreate(() -> {
        if (!CommonCapabilities.HAS_ADVANCEMENTS) {
            return null;
        }
        return new Handler();
    }, err -> {
        Logging.LOGGER.log(Level.WARNING, "Failed to hook advancement updates", (Throwable)err);
        LOGIC_FAILURE = Optional.of(err.getMessage());
        return null;
    });
    private Player player;

    public static Optional<String> getAdvancementsInitFailure() {
        return LOGIC_FAILURE;
    }

    @ClassHook.HookMethodCondition(value="version < 1.18")
    @ClassHook.HookMethod(value="public boolean grantCriteria(Advancement advancement, String s)")
    public boolean award_pre_1_18(Object rawAdvancement, String s) {
        Object bukkitAdvancement = CraftMagicNumbersHandle.advancementHandleToBukkit(rawAdvancement);
        return this.fireEvent(bukkitAdvancement, s) && ((AdvancementDataPlayerHook)this.base).award_pre_1_18(rawAdvancement, s);
    }

    @ClassHook.HookMethodCondition(value="version >= 1.18 && version < 1.20.2")
    @ClassHook.HookMethod(value="public boolean award(Advancement advancement, String s)")
    public boolean award_1_18_to_1_20_1(Object rawAdvancement, String s) {
        Object bukkitAdvancement = CraftMagicNumbersHandle.advancementHandleToBukkit(rawAdvancement);
        return this.fireEvent(bukkitAdvancement, s) && ((AdvancementDataPlayerHook)this.base).award_1_18_to_1_20_1(rawAdvancement, s);
    }

    @ClassHook.HookMethodCondition(value="version >= 1.20.2")
    @ClassHook.HookMethod(value="public boolean award(AdvancementHolder advancement, String s)")
    public boolean award_1_20_2(Object rawAdvancementHolder, String s) {
        Object bukkitAdvancement = CraftMagicNumbersHandle.advancementHandleToBukkit(rawAdvancementHolder);
        return this.fireEvent(bukkitAdvancement, s) && ((AdvancementDataPlayerHook)this.base).award_1_20_2(rawAdvancementHolder, s);
    }

    private boolean fireEvent(Object bukkitAdvancement, String criteria) {
        PlayerAdvancementProgressEvent event = new PlayerAdvancementProgressEvent(this.player, (Advancement)bukkitAdvancement, criteria);
        Bukkit.getPluginManager().callEvent((Event)event);
        return !event.isCancelled();
    }

    public static void hook(Player player) {
        if (HANDLER == null) {
            return;
        }
        if (!CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList())) {
            return;
        }
        HANDLER.hook(player);
    }

    public static void unhook(Player player) {
        if (HANDLER == null) {
            return;
        }
        HANDLER.unhook(player);
    }

    private static class Handler {
        private final HandlerLogic logic = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
        private final InputTypeMap<CriterionDataSwapper> criterionTriggerPlayerDataFields = new InputTypeMap();

        public Handler() throws Throwable {
            this.logic.forceInitialization();
            this.registerCritereonFields();
        }

        private void registerCritereonFields() throws Throwable {
            if (CommonBootstrap.evaluateMCVersion(">=", "26.2")) {
                return;
            }
            try {
                CommonUtil.getClass("net.minecraft.server.PlayerAdvancements").getDeclaredField("criterionData");
                return;
            }
            catch (Throwable throwable) {
                if (CommonBootstrap.evaluateMCVersion(">=", "1.15")) {
                    if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
                        this.registerCritereonField("SimpleCriterionTrigger", "players");
                    } else {
                        this.registerCritereonField("SimpleCriterionTrigger", "a");
                    }
                    return;
                }
                this.registerCritereonField("BredAnimalsTrigger", "b");
                this.registerCritereonField("BrewedPotionTrigger", "b");
                this.registerCritereonField("ChangeDimensionTrigger", "b");
                this.registerCritereonField("ConstructBeaconTrigger", "b");
                this.registerCritereonField("ConsumeItemTrigger", "b");
                this.registerCritereonField("CuredZombieVillagerTrigger", "b");
                this.registerCritereonField("EffectsChangedTrigger", "b");
                this.registerCritereonField("EnchantedItemTrigger", "b");
                this.registerCritereonField("EnterBlockTrigger", "b");
                this.registerCritereonField("EntityHurtPlayerTrigger", "b");
                this.registerCritereonField("InventoryChangeTrigger", "b");
                this.registerCritereonField("ItemDurabilityTrigger", "b");
                this.registerCritereonField("KilledTrigger", "a");
                this.registerCritereonField("LevitationTrigger", "b");
                this.registerCritereonField("CriterionTriggerLocation", "b");
                this.registerCritereonField("CriterionTriggerNetherTravel", "b");
                this.registerCritereonField("CriterionTriggerPlacedBlock", "b");
                this.registerCritereonField("PlayerHurtEntityTrigger", "b");
                this.registerCritereonField("RecipeUnlockedTrigger", "b");
                this.registerCritereonField("SummonedEntityTrigger", "b");
                this.registerCritereonField("TameAnimalTrigger", "b");
                this.registerCritereonField("CriterionTriggerTick", "b");
                this.registerCritereonField("UsedEnderEyeTrigger", "b");
                this.registerCritereonField("UsedTotemTrigger", "b");
                this.registerCritereonField("TradeTrigger", "b");
                if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                    this.registerCritereonField("ChanneledLightningTrigger", "b");
                    this.registerCritereonField("FilledBucketTrigger", "b");
                    this.registerCritereonField("FishingRodHookedTrigger", "b");
                }
                if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
                    this.registerCritereonField("ShotCrossbowTrigger", "b");
                    this.registerCritereonField("CriterionTriggerKilledByCrossbow", "b");
                }
                return;
            }
        }

        private void registerCritereonField(String criterionClassName, String fieldName) throws Throwable {
            List criterionPlayerDataModifiers;
            String fullCriterionClassName = CommonBootstrap.evaluateMCVersion(">=", "26.2") ? "net.minecraft.advancements.triggers." + criterionClassName : (CommonBootstrap.evaluateMCVersion(">=", "1.21.11") ? "net.minecraft.advancements.criterion." + criterionClassName : "net.minecraft.advancements.critereon." + criterionClassName);
            Class<?> type = CommonUtil.getClass(fullCriterionClassName);
            if (type == null) {
                throw new IllegalStateException("Failed to find criterion: " + criterionClassName);
            }
            Field refMapField = Resolver.resolveAndGetDeclaredField(type, fieldName);
            if (!Map.class.isAssignableFrom(refMapField.getType())) {
                throw new IllegalStateException("Criterion field of " + criterionClassName + " field " + fieldName + " is invalid: " + refMapField.getType());
            }
            FastField mapField = new FastField(refMapField);
            Class<?> valueClassType = CommonUtil.getClass(fullCriterionClassName + "$a");
            if (valueClassType != null) {
                Class<?> adpType = CommonUtil.getClass("net.minecraft.server.PlayerAdvancements");
                criterionPlayerDataModifiers = Stream.of(valueClassType.getDeclaredFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).filter(f -> f.getType() == adpType).map(FastField::new).map(ff -> ff::set).collect(Collectors.toList());
            } else {
                criterionPlayerDataModifiers = Collections.emptyList();
            }
            this.criterionTriggerPlayerDataFields.put(type, (criterion, oldPlayerAdvancements, newPlayerAdvancements) -> {
                Map map = (Map)mapField.get(criterion);
                Object currPlayerData = map.remove(oldPlayerAdvancements);
                if (currPlayerData != null) {
                    for (BiConsumer modifier : criterionPlayerDataModifiers) {
                        modifier.accept(currPlayerData, newPlayerAdvancements);
                    }
                    map.put(newPlayerAdvancements, currPlayerData);
                }
            });
        }

        public void hook(Player player) {
            Object playerHandle = HandleConversion.toEntityHandle((Entity)player);
            Object currAdvancements = this.logic.getAdvancements(playerHandle);
            if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) == null) {
                AdvancementDataPlayerHook hook = new AdvancementDataPlayerHook();
                hook.player = player;
                Object hookedAdvancements = hook.hook(currAdvancements);
                this.logic.setAdvancements(playerHandle, hookedAdvancements);
                this.swapInCriterionTriggers(currAdvancements, hookedAdvancements);
            }
        }

        public void unhook(Player player) {
            Object playerHandle = HandleConversion.toEntityHandle((Entity)player);
            Object currAdvancements = this.logic.getAdvancements(playerHandle);
            if (ClassHook.get(currAdvancements, AdvancementDataPlayerHook.class) != null) {
                Object unhookedAdvancements = ClassHook.unhook(currAdvancements);
                this.logic.setAdvancements(playerHandle, unhookedAdvancements);
                this.swapInCriterionTriggers(currAdvancements, unhookedAdvancements);
            }
        }

        private void swapInCriterionTriggers(Object oldPlayerAdvancements, Object newPlayerAdvancements) {
            if (this.criterionTriggerPlayerDataFields.values().isEmpty()) {
                return;
            }
            for (Object criterionTrigger : this.logic.getCriterionTriggers()) {
                for (CriterionDataSwapper swapper : this.criterionTriggerPlayerDataFields.getAll(criterionTrigger.getClass())) {
                    swapper.swap(criterionTrigger, oldPlayerAdvancements, newPlayerAdvancements);
                }
            }
        }
    }

    @Template.Optional
    @Template.InstanceType(value="net.minecraft.server.PlayerAdvancements")
    @Template.Import(value="net.minecraft.server.level.ServerPlayer")
    @Template.Require(declaring="net.minecraft.server.level.ServerPlayer", value="#select version >=\n#case 1.17:    private final net.minecraft.server.PlayerAdvancements advancements;\n#case 1.14:    private final net.minecraft.server.PlayerAdvancements advancements:advancementDataPlayer;\n#case 1.13.1:  private final net.minecraft.server.PlayerAdvancements advancements:cf;\n#case 1.13:    private final net.minecraft.server.PlayerAdvancements advancements:cg;\n#case else:    private final net.minecraft.server.PlayerAdvancements advancements:bY;\n#endselect")
    public static abstract class HandlerLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static Object getAdvancements(ServerPlayer nmsEntityPlayer) {\n    return nmsEntityPlayer#advancements;\n}")
        public abstract Object getAdvancements(Object var1);

        @Template.Generated(value="public static void setAdvancements(ServerPlayer nmsEntityPlayer, PlayerAdvancements newAdvancements) {\n    nmsEntityPlayer#advancements = newAdvancements;\n}")
        public abstract void setAdvancements(Object var1, Object var2);

        @Template.Generated(value="public static Iterable<Object> getCriterionTriggers() {\n#if version >= 1.20.3\n    return net.minecraft.core.registries.BuiltInRegistries.TRIGGER_TYPES;\n#elseif version >= 1.18\n    return net.minecraft.advancements.CriteriaTriggers.all();\n#else\n               return net.minecraft.advancements.CriteriaTriggers.a();\n#endif\n           }")
        public abstract Iterable<Object> getCriterionTriggers();
    }

    @FunctionalInterface
    private static interface CriterionDataSwapper {
        public void swap(Object var1, Object var2, Object var3);
    }
}

