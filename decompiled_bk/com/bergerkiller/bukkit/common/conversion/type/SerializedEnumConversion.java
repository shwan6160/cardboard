/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class SerializedEnumConversion {
    public static void registerMinecraftEnumConversion() {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.21.5")) {
            SerializedEnumConversion.registerEnum("net.minecraft.world.scores.Team$Visibility", "getSerializedName");
            SerializedEnumConversion.registerEnum("net.minecraft.world.scores.Team$CollisionRule", "getSerializedName");
        }
    }

    private static void registerEnum(String nmsEnumClassName, String nameGetterFuncName) {
        Class<?> nmsEnumClass = Resolver.loadClass(nmsEnumClassName, true);
        if (nmsEnumClass == null) {
            Logging.LOGGER_REFLECTION.severe("Failed to find enum " + nmsEnumClassName + " to register it in conversion");
        }
        try {
            Conversion.registerConverter(new EnumDuplexConverter(nmsEnumClass, nameGetterFuncName));
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to register enum " + nmsEnumClassName + " for conversion", t);
        }
    }

    public static class EnumDuplexConverter
    extends DuplexConverter<String, Object> {
        private final FastMethod<String> nameGetter;
        private final Map<String, Object> byName;

        public EnumDuplexConverter(Class<?> enumType, String nameGetterFuncName) {
            super(String.class, enumType);
            if (!enumType.isEnum()) {
                throw new IllegalStateException("Input type " + enumType + " is not an enum");
            }
            try {
                Method getter = Resolver.resolveAndGetDeclaredMethod(enumType, nameGetterFuncName, new Class[0]);
                if (Modifier.isStatic(getter.getModifiers())) {
                    throw new IllegalStateException("Method " + nameGetterFuncName + " of " + enumType.getName() + " is static");
                }
                if (getter.getReturnType() != String.class) {
                    throw new IllegalStateException("Method " + nameGetterFuncName + " of " + enumType.getName() + " does not return String (returns " + getter.getReturnType().getName() + " instead)");
                }
                this.nameGetter = new FastMethod(getter);
                this.nameGetter.forceInitialization();
                ?[] constants = enumType.getEnumConstants();
                this.byName = new HashMap<String, Object>(constants.length * 3);
                for (Object value : constants) {
                    String key = this.nameGetter.invoke(value);
                    this.byName.put(key, value);
                    this.byName.put(key.toLowerCase(Locale.ENGLISH), value);
                    this.byName.put(key.toUpperCase(Locale.ENGLISH), value);
                }
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        @Override
        public Object convertInput(String value) {
            Object item = this.byName.get(value);
            if (item == null) {
                throw new IllegalArgumentException("Not a valid value for " + this.output + ": " + value);
            }
            return item;
        }

        @Override
        public String convertOutput(Object value) {
            return this.nameGetter.invoke(value);
        }
    }
}

