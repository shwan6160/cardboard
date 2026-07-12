/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.scoreboard.DisplaySlot
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.ClassDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.ClassDeclarationResolver;
import java.util.Map;
import org.bukkit.scoreboard.DisplaySlot;

public class ScoreboardDisplaySlotConversion {
    private static final ConversionLogic LOGIC = Template.Class.create(ConversionLogic.class, new ClassDeclarationResolver(){

        @Override
        public ClassDeclaration resolveClassDeclaration(String classPath, Class<?> classType) {
            return null;
        }

        @Override
        public void resolveClassVariables(String classPath, Class<?> classType, Map<String, String> variables) {
            variables.put("version", CommonBootstrap.initCommonServer().getMinecraftVersion());
        }
    });

    public static void init() {
        LOGIC.forceInitialization();
    }

    @ConverterMethod
    public static DisplaySlot idToDisplaySlot(int id) {
        return LOGIC.idToSlot(id);
    }

    @ConverterMethod
    public static int displaySlotToId(DisplaySlot slot) {
        return LOGIC.slotToId(slot);
    }

    @ConverterMethod(input="net.minecraft.world.scores.DisplaySlot", optional=true)
    public static DisplaySlot handleToDisplaySlot(Object nmsDisplaySlot) {
        return LOGIC.handleToSlot(nmsDisplaySlot);
    }

    @ConverterMethod(output="net.minecraft.world.scores.DisplaySlot", optional=true)
    public static Object displaySlotToHandle(DisplaySlot slot) {
        return LOGIC.slotToHandle(slot);
    }

    @Template.ImportList(value={@Template.Import(value="org.bukkit.scoreboard.DisplaySlot"), @Template.Import(value="org.bukkit.craftbukkit.scoreboard.CraftScoreboardTranslations")})
    public static abstract class ConversionLogic
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static DisplaySlot idToSlot(int id) {\n#if version >= 1.20.2\n    net.minecraft.world.scores.DisplaySlot nmsSlot;\n    nmsSlot = (net.minecraft.world.scores.DisplaySlot) net.minecraft.world.scores.DisplaySlot.BY_ID.apply(id);\n    return CraftScoreboardTranslations.toBukkitSlot(nmsSlot);\n#else\n               return CraftScoreboardTranslations.toBukkitSlot(id);\n#endif\n           }")
        public abstract DisplaySlot idToSlot(int var1);

        @Template.Generated(value="public static int slotToId(DisplaySlot slot) {\n#if version >= 1.20.2\n    return CraftScoreboardTranslations.fromBukkitSlot(slot).id();\n#else\n               return CraftScoreboardTranslations.fromBukkitSlot(slot);\n#endif\n           }")
        public abstract int slotToId(DisplaySlot var1);

        @Template.Generated(value="public static DisplaySlot handleToSlot(Object handle) {\n#if version >= 1.20.2\n    return CraftScoreboardTranslations.toBukkitSlot((net.minecraft.world.scores.DisplaySlot) handle);\n#else\n               throw new UnsupportedOperationException(\"No DisplaySlot handle class exists on this version of Minecraft\");\n#endif\n           }")
        public abstract DisplaySlot handleToSlot(Object var1);

        @Template.Generated(value="public static Object slotToHandle(DisplaySlot slot) {\n#if version >= 1.20.2\n    return CraftScoreboardTranslations.fromBukkitSlot(slot);\n#else\n               throw new UnsupportedOperationException(\"No DisplaySlot handle class exists on this version of Minecraft\");\n#endif\n           }")
        public abstract Object slotToHandle(DisplaySlot var1);
    }
}

