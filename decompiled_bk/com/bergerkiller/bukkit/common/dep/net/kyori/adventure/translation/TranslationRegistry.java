/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.MessageFormatTranslationStore;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.TranslationStore;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.Translator;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface TranslationRegistry
extends Translator,
TranslationStore.StringBased<MessageFormat> {
    @Deprecated
    public static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");

    @Deprecated
    @NotNull
    public static TranslationRegistry create(Key name) {
        return new MessageFormatTranslationStore(Objects.requireNonNull(name, "name"));
    }

    @Override
    @Deprecated
    public boolean contains(@NotNull String var1);

    @Override
    @Deprecated
    @Nullable
    public MessageFormat translate(@NotNull String var1, @NotNull Locale var2);

    @Override
    @Deprecated
    public void defaultLocale(@NotNull Locale var1);

    @Override
    @Deprecated
    public void register(@NotNull String var1, @NotNull Locale var2, @NotNull MessageFormat var3);

    @Override
    @Deprecated
    default public void registerAll(@NotNull Locale locale, @NotNull Map<String, MessageFormat> formats) {
        this.registerAll(locale, formats.keySet(), formats::get);
    }

    @Override
    @Deprecated
    default public void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
            this.registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    @Deprecated
    default public void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
        this.registerAll(locale, bundle.keySet(), (String key) -> {
            String format = bundle.getString((String)key);
            return new MessageFormat(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
        });
    }

    @Override
    @Deprecated
    default public void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, MessageFormat> function) {
        IllegalArgumentException firstError = null;
        int errorCount = 0;
        for (String key : keys) {
            try {
                this.register(key, locale, function.apply(key));
            }
            catch (IllegalArgumentException e) {
                if (firstError == null) {
                    firstError = e;
                }
                ++errorCount;
            }
        }
        if (firstError != null) {
            if (errorCount == 1) {
                throw firstError;
            }
            if (errorCount > 1) {
                throw new IllegalArgumentException(String.format("Invalid key (and %d more)", errorCount - 1), firstError);
            }
        }
    }

    @Override
    @Deprecated
    public void unregister(@NotNull String var1);
}

