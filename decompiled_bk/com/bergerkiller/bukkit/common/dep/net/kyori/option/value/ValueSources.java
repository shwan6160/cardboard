/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.option.value;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.Option;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.value.ValueSource;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

final class ValueSources {
    static final ValueSource ENVIRONMENT = new EnvironmentVariable("");
    static final ValueSource SYSTEM_PROPERTIES = new SystemProperty("");

    private ValueSources() {
    }

    static final class EnvironmentVariable
    implements ValueSource {
        private static final Pattern ENVIRONMENT_SUBST_PATTERN = Pattern.compile("[:\\-/]");
        private static final String ENVIRONMENT_VAR_SEPARATOR = "_";
        private final String prefix;

        EnvironmentVariable(String prefix) {
            this.prefix = prefix.isEmpty() ? "" : prefix.toUpperCase(Locale.ROOT) + ENVIRONMENT_VAR_SEPARATOR;
        }

        @Override
        public <T> @Nullable T value(Option<T> option) {
            StringBuffer buf = new StringBuffer(option.id().length() + this.prefix.length());
            buf.append(this.prefix);
            Matcher match = ENVIRONMENT_SUBST_PATTERN.matcher(option.id());
            while (match.find()) {
                match.appendReplacement(buf, ENVIRONMENT_VAR_SEPARATOR);
            }
            match.appendTail(buf);
            String value = System.getenv(buf.toString().toUpperCase(Locale.ROOT));
            if (value == null) {
                return null;
            }
            return option.valueType().parse(value);
        }
    }

    static final class SystemProperty
    implements ValueSource {
        private static final Pattern SYSTEM_PROP_SUBST_PATTERN = Pattern.compile("[:/]");
        private static final String SYSTEM_PROPERTY_SEPARATOR = ".";
        private final String prefix;

        SystemProperty(String prefix) {
            this.prefix = prefix.isEmpty() ? "" : prefix + SYSTEM_PROPERTY_SEPARATOR;
        }

        @Override
        public <T> @Nullable T value(Option<T> option) {
            StringBuffer buf = new StringBuffer(option.id().length() + this.prefix.length());
            buf.append(this.prefix);
            Matcher match = SYSTEM_PROP_SUBST_PATTERN.matcher(option.id());
            while (match.find()) {
                match.appendReplacement(buf, SYSTEM_PROPERTY_SEPARATOR);
            }
            match.appendTail(buf);
            String value = System.getProperty(buf.toString());
            if (value == null) {
                return null;
            }
            return option.valueType().parse(value);
        }
    }
}

