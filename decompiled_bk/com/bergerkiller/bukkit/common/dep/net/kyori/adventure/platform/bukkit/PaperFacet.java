/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.Title
 *  com.destroystokyo.paper.Title$Builder
 *  net.md_5.bungee.api.chat.BaseComponent
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.CraftBukkitFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.SpigotFacet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.FacetBase;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Knob;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.destroystokyo.paper.Title;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PaperFacet<V extends CommandSender>
extends FacetBase<V> {
    private static final boolean SUPPORTED = Knob.isEnabled("paper", true);
    static final Class<?> NATIVE_COMPONENT_CLASS = MinecraftReflection.findClass(String.join((CharSequence)".", "net", "kyori", "adventure", "text", "Component"));
    private static final MethodHandle PAPER_ADVENTURE_AS_VANILLA = PaperFacet.findAsVanillaMethod();
    private static final Class<?> NATIVE_GSON_COMPONENT_SERIALIZER_CLASS = MinecraftReflection.findClass(String.join((CharSequence)".", "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializer"));
    private static final Class<?> NATIVE_GSON_COMPONENT_SERIALIZER_IMPL_CLASS = MinecraftReflection.findClass(String.join((CharSequence)".", "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializerImpl"));
    private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_GSON_GETTER = MinecraftReflection.findStaticMethod(NATIVE_GSON_COMPONENT_SERIALIZER_CLASS, "gson", NATIVE_GSON_COMPONENT_SERIALIZER_CLASS, new Class[0]);
    private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD = PaperFacet.findNativeDeserializeMethod();

    @Nullable
    private static MethodHandle findAsVanillaMethod() {
        try {
            Class<?> paperAdventure = MinecraftReflection.findClass("io.papermc.paper.adventure.PaperAdventure");
            Method method = paperAdventure.getDeclaredMethod("asVanilla", NATIVE_COMPONENT_CLASS);
            return MinecraftReflection.lookup().unreflect(method);
        }
        catch (IllegalAccessException | NoSuchMethodException | NullPointerException e) {
            return null;
        }
    }

    @Nullable
    private static MethodHandle findNativeDeserializeMethod() {
        try {
            Method method = NATIVE_GSON_COMPONENT_SERIALIZER_IMPL_CLASS.getDeclaredMethod("deserialize", String.class);
            method.setAccessible(true);
            return MinecraftReflection.lookup().unreflect(method);
        }
        catch (IllegalAccessException | NoSuchMethodException | NullPointerException e) {
            return null;
        }
    }

    protected PaperFacet(@Nullable Class<? extends V> viewerClass) {
        super(viewerClass);
    }

    @Override
    public boolean isSupported() {
        return super.isSupported() && SUPPORTED;
    }

    static class TabList
    extends CraftBukkitFacet.TabList {
        private static final boolean SUPPORTED = MinecraftReflection.hasField(CLASS_CRAFT_PLAYER, NATIVE_COMPONENT_CLASS, "playerListHeader") && MinecraftReflection.hasField(CLASS_CRAFT_PLAYER, NATIVE_COMPONENT_CLASS, "playerListFooter");
        private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD_BOUND = TabList.createBoundNativeDeserializeMethodHandle();

        TabList() {
        }

        @Nullable
        private static MethodHandle createBoundNativeDeserializeMethodHandle() {
            if (SUPPORTED) {
                try {
                    return NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD.bindTo(NATIVE_GSON_COMPONENT_SERIALIZER_GSON_GETTER.invoke());
                }
                catch (Throwable throwable) {
                    Knob.logError(throwable, "Failed to access native GsonComponentSerializer", new Object[0]);
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean isSupported() {
            return SUPPORTED && super.isSupported() && (CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER != null && CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER != null || PAPER_ADVENTURE_AS_VANILLA != null);
        }

        @Override
        protected Object create117Packet(Player viewer, @Nullable Object header, @Nullable Object footer) throws Throwable {
            if (CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER == null && CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER == null) {
                return CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(PAPER_ADVENTURE_AS_VANILLA.invoke(header == null ? this.createMessage(viewer, (Component)Component.empty()) : header), PAPER_ADVENTURE_AS_VANILLA.invoke(footer == null ? this.createMessage(viewer, (Component)Component.empty()) : footer));
            }
            Object packet = CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(null, null);
            CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER.invoke(packet, header == null ? this.createMessage(viewer, (Component)Component.empty()) : header);
            CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER.invoke(packet, footer == null ? this.createMessage(viewer, (Component)Component.empty()) : footer);
            return packet;
        }

        @Override
        @Nullable
        public Object createMessage(@NotNull Player viewer, @NotNull Component message) {
            try {
                return NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD_BOUND.invoke((String)GsonComponentSerializer.gson().serialize(message));
            }
            catch (Throwable throwable) {
                Knob.logError(throwable, "Failed to create native Component message", new Object[0]);
                return null;
            }
        }
    }

    static class Title
    extends SpigotFacet.Message<Player>
    implements Facet.Title<Player, BaseComponent[], Title.Builder, com.destroystokyo.paper.Title> {
        private static final boolean SUPPORTED = MinecraftReflection.hasClass("com.destroystokyo.paper.Title");

        protected Title() {
            super(Player.class);
        }

        @Override
        public boolean isSupported() {
            return super.isSupported() && SUPPORTED;
        }

        @Override
        public // Could not load outer class - annotation placement on inner may be incorrect
         @NotNull Title.Builder createTitleCollection() {
            return com.destroystokyo.paper.Title.builder();
        }

        @Override
        public void contributeTitle(// Could not load outer class - annotation placement on inner may be incorrect
         @NotNull Title.Builder coll, BaseComponent @NotNull [] title) {
            coll.title(title);
        }

        @Override
        public void contributeSubtitle(// Could not load outer class - annotation placement on inner may be incorrect
         @NotNull Title.Builder coll, BaseComponent @NotNull [] subtitle) {
            coll.subtitle(subtitle);
        }

        @Override
        public void contributeTimes(// Could not load outer class - annotation placement on inner may be incorrect
         @NotNull Title.Builder coll, int inTicks, int stayTicks, int outTicks) {
            if (inTicks > -1) {
                coll.fadeIn(inTicks);
            }
            if (stayTicks > -1) {
                coll.stay(stayTicks);
            }
            if (outTicks > -1) {
                coll.fadeOut(outTicks);
            }
        }

        @Override
        @Nullable
        public com.destroystokyo.paper.Title completeTitle(// Could not load outer class - annotation placement on inner may be incorrect
         @NotNull Title.Builder coll) {
            return coll.build();
        }

        @Override
        public void showTitle(@NotNull Player viewer, @NotNull com.destroystokyo.paper.Title title) {
            viewer.sendTitle(title);
        }

        @Override
        public void clearTitle(@NotNull Player viewer) {
            viewer.hideTitle();
        }

        @Override
        public void resetTitle(@NotNull Player viewer) {
            viewer.resetTitle();
        }
    }
}

