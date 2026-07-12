/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.bergerkiller.bukkit.common.server.test;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_14;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_16;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_17;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_18;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_18_2;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_19_3;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_20_2;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_21_2;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_1_8;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory_26_1;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executor;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class TestServerFactory {
    public static void initTestServer() {
        if (CommonServerBase.SERVER_CLASS == null) {
            throw new UnsupportedOperationException("Unable to detect Bukkit server main class during test");
        }
        TestServerFactory factory = CommonBootstrap.evaluateMCVersion(">=", "26.1") ? new TestServerFactory_26_1() : (CommonBootstrap.evaluateMCVersion(">=", "1.21.2") ? new TestServerFactory_1_21_2() : (CommonBootstrap.evaluateMCVersion(">=", "1.20.2") ? new TestServerFactory_1_20_2() : (CommonBootstrap.evaluateMCVersion(">=", "1.19.3") ? new TestServerFactory_1_19_3() : (CommonBootstrap.evaluateMCVersion(">=", "1.18.2") ? new TestServerFactory_1_18_2() : (CommonBootstrap.evaluateMCVersion(">=", "1.18") ? new TestServerFactory_1_18() : (CommonBootstrap.evaluateMCVersion(">=", "1.17") ? new TestServerFactory_1_17() : (CommonBootstrap.evaluateMCVersion(">=", "1.16") ? new TestServerFactory_1_16() : (CommonBootstrap.evaluateMCVersion(">=", "1.14") ? new TestServerFactory_1_14() : new TestServerFactory_1_8()))))))));
        ServerEnvironment env = new ServerEnvironment();
        try {
            env.CB_ROOT = factory.detectCBRoot();
            env.NMS_ROOT = factory.detectNMSRoot();
            ((TestServerFactory)factory).init(env);
        }
        catch (Throwable t) {
            Logging.LOGGER.severe("Failed to initialize server under test");
            Logging.LOGGER.severe("Detected server class under test: " + CommonServerBase.SERVER_CLASS);
            Logging.LOGGER.severe("Detected CB_ROOT: " + env.CB_ROOT);
            Logging.LOGGER.severe("Detected NMS_ROOT: " + env.NMS_ROOT);
            throw new UnsupportedOperationException("An error occurred while trying to initialize the test server", t);
        }
        TestServerFactory.init_spigotConfig();
        System.gc();
    }

    protected String detectCBRoot() throws Throwable {
        return TestServerFactory.getPackagePath(CommonServerBase.SERVER_CLASS);
    }

    protected String detectNMSRoot() throws Throwable {
        return "net.minecraft.server";
    }

    protected abstract void init(ServerEnvironment var1) throws Throwable;

    private static void init_spigotConfig() {
        Class<?> spigotConfigType = CommonUtil.getClass("org.spigotmc.SpigotConfig");
        if (spigotConfigType != null) {
            String yamlType = DefaultSpigotYamlConfiguration.class.getName();
            TestServerFactory.createFromCode(spigotConfigType, "SpigotConfig.config = new " + yamlType + "();\nSpigotConfig.config.set(\"world-settings.default.verbose\", Boolean.FALSE);\nreturn null;\n");
        }
    }

    protected static void setField(Object instance, Class<?> declaringClass, String name, Object value) {
        String realName = Resolver.resolveFieldName(declaringClass, name);
        try {
            Field field = declaringClass.getDeclaredField(realName);
            field.setAccessible(true);
            field.set(instance, value);
        }
        catch (Throwable ex) {
            throw new RuntimeException("Failed to set field " + TestServerFactory.fieldAliasStr(name, realName), ex);
        }
    }

    protected static void setField(Object instance, String name, Object value) {
        String realName2;
        Field f = null;
        for (Class<?> t = instance.getClass(); t != null && f == null; t = t.getSuperclass()) {
            try {
                realName2 = Resolver.resolveFieldName(t, name);
                f = t.getDeclaredField(realName2);
                f.setAccessible(true);
                continue;
            }
            catch (Throwable realName2) {
                // empty catch block
            }
        }
        if (f == null) {
            realName2 = Resolver.resolveFieldName(instance.getClass(), name);
            throw new RuntimeException("Field " + TestServerFactory.fieldAliasStr(name, realName2) + " not found in " + instance.getClass().getName());
        }
        try {
            f.set(instance, value);
        }
        catch (Throwable ex) {
            throw new RuntimeException("Failed to set field " + name, ex);
        }
    }

    protected static Class<?> resolveClass(String name) {
        return TestServerFactory.resolveClass(name, true);
    }

    protected static Class<?> resolveClass(String name, boolean initialize) {
        String resolvedType = Resolver.resolveClassPath(name);
        try {
            return Class.forName(resolvedType, initialize, TestServerFactory.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException("Failed to find class " + name + " (" + resolvedType + ")", ex);
        }
    }

    protected static Object getStaticField(Class<?> type, String name) {
        String realName = Resolver.resolveFieldName(type, name);
        try {
            Field f = type.getDeclaredField(realName);
            f.setAccessible(true);
            return f.get(null);
        }
        catch (Throwable ex) {
            throw new RuntimeException("Failed to get field " + TestServerFactory.fieldAliasStr(name, realName), ex);
        }
    }

    private static String fieldAliasStr(String name, String realName) {
        return name.equals(realName) ? name : name + ":" + realName;
    }

    protected static Object createFromCode(Class<?> type, String code) {
        return TestServerFactory.compileCode(type, "public static Object create() {" + code + "}").invoke(null);
    }

    protected static Object createFromCode(Class<?> type, String code, Object ... args) {
        StringBuilder body = new StringBuilder();
        body.append("public static Object create(");
        for (int i = 0; i < args.length; ++i) {
            if (i > 0) {
                body.append(", ");
            }
            Class<Object> argType = args[i] == null ? Object.class : args[i].getClass();
            body.append(argType.getName()).append(" arg").append(i);
        }
        body.append(") {").append(code).append("}");
        return TestServerFactory.compileCode(type, body.toString()).invokeVA(null, args);
    }

    protected static FastMethod<Object> compileCode(Class<?> type, String code) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(type);
        MethodDeclaration dec = new MethodDeclaration(resolver, code);
        FastMethod<Object> m = new FastMethod<Object>();
        m.init(dec);
        return m;
    }

    protected static Executor newThreadExecutor() {
        return runnable -> {
            Thread t = new Thread(runnable, "TestServerWorkerThread");
            t.setDaemon(true);
            t.start();
        };
    }

    protected static Object construct(Class<?> type, Object ... parameters) {
        try {
            for (Constructor<?> constructor : type.getDeclaredConstructors()) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length != parameters.length) continue;
                boolean suitable = true;
                for (int i = 0; i < paramTypes.length; ++i) {
                    if (parameters[i] == null) {
                        if (!parameters[i].getClass().isPrimitive()) continue;
                        suitable = false;
                        break;
                    }
                    Class<?> paramTypeFixed = LogicUtil.tryBoxType(paramTypes[i]);
                    if (paramTypeFixed.isAssignableFrom(parameters[i].getClass())) continue;
                    suitable = false;
                    break;
                }
                if (!suitable) continue;
                constructor.setAccessible(true);
                return constructor.newInstance(parameters);
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to construct " + type.getSimpleName(), t);
        }
        throw new RuntimeException("Constructor not found in " + type.getSimpleName());
    }

    protected static String getPackagePath(Class<?> type) {
        return type.getPackage().getName();
    }

    protected static final class ServerEnvironment {
        public String CB_ROOT = "FAIL";
        public String NMS_ROOT = "FAIL";
        public Object mc_server = null;
        public Class<?> mc_server_type = null;
        public Object registries = null;
        public Object featureFlagSet = null;
        public Object resourcePackRepository = null;
        public Object resourceManager = null;
        public List<?> tagDataPackRegistries = null;

        protected ServerEnvironment() {
        }
    }

    public static class DefaultSpigotYamlConfiguration
    extends YamlConfiguration {
        public DefaultSpigotYamlConfiguration() {
            this.set("world-settings.default.verbose", false);
        }

        public void save(File file) {
        }

        public void save(String file) {
        }
    }

    public static class ServerHook
    extends ClassHook<ServerHook> {
        @ClassHook.HookMethod(value="public String getVersion()")
        public String getVersion() {
            return "DUMMY_SERVER";
        }

        @ClassHook.HookMethod(value="public String getBukkitVersion()")
        public String getBukkitVersion() {
            return "DUMMY_BUKKIT";
        }
    }
}

