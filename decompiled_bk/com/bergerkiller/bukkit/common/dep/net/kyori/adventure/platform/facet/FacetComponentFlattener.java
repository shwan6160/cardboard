/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.facet.Facet;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.TranslatableComponent;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.flattener.ComponentFlattener;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.GlobalTranslator;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.TranslationRegistry;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class FacetComponentFlattener {
    private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");

    private FacetComponentFlattener() {
    }

    public static <V> ComponentFlattener get(V instance, Collection<? extends Translator<V>> candidates) {
        Translator translator = Facet.of(candidates, instance);
        ComponentFlattener.Builder flattenerBuilder = (ComponentFlattener.Builder)ComponentFlattener.basic().toBuilder();
        flattenerBuilder.complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
            String key = translatable.key();
            for (com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.Translator translator2 : GlobalTranslator.translator().sources()) {
                if (!(translator2 instanceof TranslationRegistry) || !((TranslationRegistry)translator2).contains(key)) continue;
                consumer.accept(GlobalTranslator.render(translatable, Locale.getDefault()));
                return;
            }
            @NotNull String translated = translator == null ? key : translator.valueOrDefault(instance, key);
            Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
            List<Component> args = translatable.args();
            int argPosition = 0;
            int lastIdx = 0;
            while (matcher.find()) {
                int idx;
                if (lastIdx < matcher.start()) {
                    consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
                }
                lastIdx = matcher.end();
                @Nullable String argIdx = matcher.group(1);
                if (argIdx != null) {
                    try {
                        int idx2 = Integer.parseInt(argIdx) - 1;
                        if (idx2 >= args.size()) continue;
                        consumer.accept(args.get(idx2));
                    }
                    catch (NumberFormatException idx2) {}
                    continue;
                }
                if ((idx = argPosition++) >= args.size()) continue;
                consumer.accept(args.get(idx));
            }
            if (lastIdx < translated.length()) {
                consumer.accept(Component.text(translated.substring(lastIdx)));
            }
        });
        return (ComponentFlattener)flattenerBuilder.build();
    }

    public static interface Translator<V>
    extends Facet<V> {
        @NotNull
        public String valueOrDefault(@NotNull V var1, @NotNull String var2);
    }
}

