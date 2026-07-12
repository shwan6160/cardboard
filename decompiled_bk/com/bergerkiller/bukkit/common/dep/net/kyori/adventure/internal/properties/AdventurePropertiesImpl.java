/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.properties;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.properties.AdventureProperties;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Services;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

final class AdventurePropertiesImpl {
    private static final String FILESYSTEM_DIRECTORY_NAME = "config";
    private static final String FILESYSTEM_FILE_NAME = "adventure.properties";
    private static final Properties PROPERTIES = new Properties();

    private static void print(Throwable ex) {
        ex.printStackTrace();
    }

    private AdventurePropertiesImpl() {
    }

    @VisibleForTesting
    @NotNull
    static String systemPropertyName(String name) {
        return String.join((CharSequence)".", "net", "kyori", "adventure", name);
    }

    static <T> @NotNull AdventureProperties.Property<T> property(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue, boolean allowProviderDefaultOverride) {
        return new PropertyImpl<T>(name, parser, defaultValue, allowProviderDefaultOverride);
    }

    static {
        Path path = Optional.ofNullable(System.getProperty(AdventurePropertiesImpl.systemPropertyName(FILESYSTEM_DIRECTORY_NAME))).map(x$0 -> Paths.get(x$0, new String[0])).orElseGet(() -> Paths.get(FILESYSTEM_DIRECTORY_NAME, FILESYSTEM_FILE_NAME));
        if (Files.isRegularFile(path, new LinkOption[0])) {
            try (InputStream is = Files.newInputStream(path, new OpenOption[0]);){
                PROPERTIES.load(is);
            }
            catch (IOException e) {
                AdventurePropertiesImpl.print(e);
            }
        }
    }

    private static final class PropertyImpl<T>
    implements AdventureProperties.Property<T> {
        private final String name;
        private final Function<String, T> parser;
        @Nullable
        private final T defaultValue;
        private final boolean allowProviderDefaultOverride;
        private boolean valueCalculated;
        @Nullable
        private T value;

        PropertyImpl(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue, boolean allowProviderDefaultOverride) {
            this.name = name;
            this.parser = parser;
            this.defaultValue = defaultValue;
            this.allowProviderDefaultOverride = allowProviderDefaultOverride;
        }

        @Override
        @Nullable
        public T value() {
            if (!this.valueCalculated) {
                String property = AdventurePropertiesImpl.systemPropertyName(this.name);
                String value = System.getProperty(property, PROPERTIES.getProperty(this.name));
                if (value != null) {
                    this.value = this.parser.apply(value);
                }
                if (this.value == null) {
                    this.value = this.allowProviderDefaultOverride ? Providers.DEFAULT_PROVIDER.map(provider -> provider.overrideDefault(this, this.defaultValue)).orElse(this.defaultValue) : this.defaultValue;
                }
                this.valueCalculated = true;
            }
            return this.value;
        }

        public boolean equals(@Nullable Object that) {
            return this == that;
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }

    static final class Providers {
        @NotNull
        static final Optional<AdventureProperties.DefaultOverrideProvider> DEFAULT_PROVIDER = Services.service(AdventureProperties.DefaultOverrideProvider.class);

        Providers() {
        }
    }
}

