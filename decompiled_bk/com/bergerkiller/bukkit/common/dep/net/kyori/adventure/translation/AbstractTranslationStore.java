/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.TranslationLocales;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.translation.TranslationStore;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.TriState;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTranslationStore<T>
implements Examinable,
TranslationStore<T> {
    @NotNull
    private final Key name;
    private final Map<String, Translation> translations = new ConcurrentHashMap<String, Translation>();
    @NotNull
    private volatile Locale defaultLocale = Locale.US;

    protected AbstractTranslationStore(@NotNull Key name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    @Nullable
    protected T translationValue(@NotNull String key, @NotNull Locale locale) {
        Translation translation = this.translations.get(Objects.requireNonNull(key, "key"));
        if (translation == null) {
            return null;
        }
        return (T)translation.translate(Objects.requireNonNull(locale, "locale"));
    }

    @Override
    public final boolean contains(@NotNull String key) {
        return this.translations.containsKey(key);
    }

    @Override
    public final boolean contains(@NotNull String key, @NotNull Locale locale) {
        Translation translation = this.translations.get(Objects.requireNonNull(key, "key"));
        if (translation == null) {
            return false;
        }
        return translation.translations.get(Objects.requireNonNull(locale, "locale")) != null;
    }

    @Override
    public final boolean canTranslate(@NotNull String key, @NotNull Locale locale) {
        Translation translation = this.translations.get(Objects.requireNonNull(key, "key"));
        if (translation == null) {
            return false;
        }
        return translation.translate(Objects.requireNonNull(locale, "locale")) != null;
    }

    @Override
    public final void defaultLocale(@NotNull Locale locale) {
        this.defaultLocale = Objects.requireNonNull(locale, "locale");
    }

    @Override
    public final void register(@NotNull String key, @NotNull Locale locale, @NotNull T translation) {
        this.translations.computeIfAbsent(key, x$0 -> new Translation((String)x$0)).register(locale, translation);
    }

    @Override
    public final void registerAll(@NotNull Locale locale, @NotNull Map<String, T> translations) {
        this.registerAll(locale, translations.keySet(), translations::get);
    }

    @Override
    public final void registerAll(@NotNull Locale locale, @NotNull Set<String> keys, Function<String, T> function) {
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
    public final void unregister(@NotNull String key) {
        this.translations.remove(key);
    }

    @Override
    @NotNull
    public final Key name() {
        return this.name;
    }

    @Override
    @NotNull
    public final TriState hasAnyTranslations() {
        return TriState.byBoolean(!this.translations.isEmpty());
    }

    @Override
    @NotNull
    public final Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("translations", this.translations));
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractTranslationStore)) {
            return false;
        }
        AbstractTranslationStore that = (AbstractTranslationStore)other;
        return this.name.equals(that.name);
    }

    public final int hashCode() {
        return this.name.hashCode();
    }

    @NotNull
    public final String toString() {
        return Internals.toString(this);
    }

    private final class Translation
    implements Examinable {
        private final String key;
        private final Map<Locale, T> translations;

        private Translation(String key) {
            this.key = Objects.requireNonNull(key, "key");
            this.translations = new ConcurrentHashMap();
        }

        @Nullable
        private T translate(@NotNull Locale locale) {
            Object format = this.translations.get(Objects.requireNonNull(locale, "locale"));
            if (format == null && (format = this.translations.get(new Locale(locale.getLanguage()))) == null && (format = this.translations.get(AbstractTranslationStore.this.defaultLocale)) == null) {
                format = this.translations.get(TranslationLocales.global());
            }
            return format;
        }

        private void register(@NotNull Locale locale, @NotNull T translation) {
            if (this.translations.putIfAbsent(Objects.requireNonNull(locale, "locale"), Objects.requireNonNull(translation, "translation")) != null) {
                throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", this.key, locale));
            }
        }

        @Override
        @NotNull
        public Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(ExaminableProperty.of("key", this.key), ExaminableProperty.of("translations", this.translations));
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Translation)) {
                return false;
            }
            Translation that = (Translation)other;
            return this.key.equals(that.key) && this.translations.equals(that.translations);
        }

        public int hashCode() {
            return Objects.hash(this.key, this.translations);
        }

        public String toString() {
            return Internals.toString(this);
        }
    }

    public static abstract class StringBased<T>
    extends AbstractTranslationStore<T>
    implements TranslationStore.StringBased<T> {
        private static final Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("'");

        protected StringBased(@NotNull Key name) {
            super(name);
        }

        @NotNull
        protected abstract T parse(@NotNull String var1, @NotNull Locale var2);

        @Override
        public final void registerAll(@NotNull Locale locale, @NotNull Path path, boolean escapeSingleQuotes) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
                this.registerAll(locale, new PropertyResourceBundle(reader), escapeSingleQuotes);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        @Override
        public final void registerAll(@NotNull Locale locale, @NotNull ResourceBundle bundle, boolean escapeSingleQuotes) {
            this.registerAll(locale, bundle.keySet(), key -> {
                String format = bundle.getString((String)key);
                return this.parse(escapeSingleQuotes ? SINGLE_QUOTE_PATTERN.matcher(format).replaceAll("''") : format, locale);
            });
        }
    }
}

