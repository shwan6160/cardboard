/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.bukkit.configuration.serialization.ConfigurationSerialization
 *  org.yaml.snakeyaml.DumperOptions
 *  org.yaml.snakeyaml.LoaderOptions
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.constructor.BaseConstructor
 *  org.yaml.snakeyaml.constructor.Construct
 *  org.yaml.snakeyaml.constructor.SafeConstructor
 *  org.yaml.snakeyaml.error.YAMLException
 *  org.yaml.snakeyaml.nodes.Node
 *  org.yaml.snakeyaml.nodes.Tag
 *  org.yaml.snakeyaml.representer.Representer
 *  org.yaml.snakeyaml.resolver.Resolver
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.config.HeaderBuilder;
import com.bergerkiller.bukkit.common.config.NodeBuilder;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializer;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerUtils;
import com.bergerkiller.bukkit.common.internal.proxy.PlayerProfile_1_8_to_1_18;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

public class YamlDeserializer {
    private final Yaml yaml;
    private final PreParser preParser;
    private final MappingConstructorFactory mappingFactory = new MappingConstructorFactory();
    public static final YamlDeserializer INSTANCE = new YamlDeserializer();

    public YamlDeserializer() {
        YamlConstructorFactory ctorFactory = new YamlConstructorFactory(this.mappingFactory);
        Yaml yaml = null;
        Class<?> loaderOptionsType = CommonUtil.getClass("org.yaml.snakeyaml.LoaderOptions");
        if (loaderOptionsType != null && SafeMethod.contains(loaderOptionsType, "setNestingDepthLimit", Integer.TYPE)) {
            try {
                Constructor constr = Yaml.class.getConstructor(BaseConstructor.class, Representer.class, DumperOptions.class, LoaderOptions.class, Resolver.class);
                Representer representer = new Representer(new DumperOptions());
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(representer.getDefaultFlowStyle());
                dumperOptions.setDefaultScalarStyle(representer.getDefaultScalarStyle());
                dumperOptions.setAllowReadOnlyProperties(representer.getPropertyUtils().isAllowReadOnlyProperties());
                dumperOptions.setTimeZone(representer.getTimeZone());
                LoaderOptions loaderOptions = new LoaderOptions();
                LoaderOptions.class.getMethod("setNestingDepthLimit", Integer.TYPE).invoke((Object)loaderOptions, Integer.MAX_VALUE);
                LoaderOptions.class.getMethod("setCodePointLimit", Integer.TYPE).invoke((Object)loaderOptions, Integer.MAX_VALUE);
                LoaderOptions.class.getMethod("setMaxAliasesForCollections", Integer.TYPE).invoke((Object)loaderOptions, Integer.MAX_VALUE);
                Resolver resolver = new Resolver();
                SafeConstructor yamlConstructor = ctorFactory.create(loaderOptions);
                yaml = (Yaml)constr.newInstance(yamlConstructor, representer, dumperOptions, loaderOptions, resolver);
            }
            catch (Throwable t) {
                Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to disable SnakeYAML document size limits", t);
                SafeConstructor yamlConstructor = ctorFactory.create();
                yaml = new Yaml((BaseConstructor)yamlConstructor);
            }
        } else {
            SafeConstructor yamlConstructor = ctorFactory.create();
            yaml = new Yaml((BaseConstructor)yamlConstructor);
        }
        this.yaml = yaml;
        this.preParser = new PreParser();
    }

    public Object deserializeMapping(Map<?, ?> mapping) {
        Map mappingUnsafe = (Map)LogicUtil.unsafeCast(mapping);
        boolean operatingOnCopy = false;
        for (Map.Entry entry : mappingUnsafe.entrySet()) {
            Object newValue;
            Object value = entry.getValue();
            if (!(value instanceof Map) || value == (newValue = this.deserializeMapping((Map)value))) continue;
            if (!operatingOnCopy) {
                if (!(mapping instanceof ImmutableMap)) {
                    try {
                        entry.setValue(newValue);
                        continue;
                    }
                    catch (UnsupportedOperationException unsupportedOperationException) {
                        // empty catch block
                    }
                }
                mapping = new LinkedHashMap(mapping);
                mappingUnsafe = (Map)LogicUtil.unsafeCast(mapping);
                operatingOnCopy = true;
            }
            mappingUnsafe.put(entry.getKey(), newValue);
        }
        return this.mappingFactory.construct(mapping);
    }

    private static Map<String, Object> safePutInMap(Map<String, Object> map, String key, Object value) {
        if (!(map instanceof ImmutableMap)) {
            try {
                map.put(key, value);
                return map;
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
        }
        map = new LinkedHashMap<String, Object>(map);
        map.put(key, value);
        return map;
    }

    public synchronized Output deserialize(String yamlString) throws YAMLException {
        return this.deserialize(new StringReader(yamlString));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized Output deserialize(Reader reader) throws YAMLException {
        try {
            this.preParser.open(reader);
            Output output = new Output();
            output.root = LogicUtil.tryCast(this.yaml.load((Reader)this.preParser), Map.class, Collections.emptyMap());
            output.headers = new HashMap<YamlPath, String>(this.preParser.headers);
            if (this.preParser.mainHeader.length() > 0) {
                output.headers.put(YamlPath.ROOT, this.preParser.mainHeader.toString());
            }
            output.indent = this.preParser.nodeBuilder.getIndent();
            if (output.indent == -1) {
                output.indent = 2;
            }
            Output output2 = output;
            return output2;
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException iOException) {}
        }
    }

    private static class MappingConstructorFactory {
        private final Map<String, Function<Map<String, Object>, ? extends Object>> custom_builders = new HashMap<String, Function<Map<String, Object>, ? extends Object>>();

        public MappingConstructorFactory() {
            ItemStackDeserializer itemStackDeserializer = ItemStackDeserializer.INSTANCE;
            this.register("org.bukkit.inventory.ItemStack", itemStackDeserializer);
            this.register("org.bukkit.inventory.ItemMeta", itemStackDeserializer.getBukkitMigrator().getItemMetaDeserializer());
            this.register("ItemMeta", itemStackDeserializer.getBukkitMigrator().getItemMetaDeserializer());
            if (CraftItemStackHandle.T.deserializeCustomModelData.isAvailable()) {
                this.register("CustomModelData", ItemStackDeserializerUtils::deserializeCustomModelData);
            }
            if (CommonCapabilities.HAS_BUKKIT_PLAYER_PROFILE) {
                try {
                    Class<?> craftPlayerProfileType = CommonUtil.getClass("org.bukkit.craftbukkit.profile.CraftPlayerProfile");
                    FastMethod deserializeMethod = new FastMethod(craftPlayerProfileType.getMethod("deserialize", Map.class));
                    deserializeMethod.forceInitialization();
                    Function<Map<String, Object>, Object> deserializePlayerProfile = map -> {
                        String newName;
                        Object name = map.get("name");
                        if (name instanceof String && (newName = MappingConstructorFactory.fixProfileName((String)name)) != name) {
                            map = YamlDeserializer.safePutInMap(map, "name", newName);
                        }
                        return deserializeMethod.invoke(null, map);
                    };
                    this.register("org.bukkit.profile.PlayerProfile", deserializePlayerProfile);
                    this.register("PlayerProfile", deserializePlayerProfile);
                }
                catch (Throwable t) {
                    Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to register player profile deserializer", t);
                }
            } else {
                this.register("org.bukkit.profile.PlayerProfile", PlayerProfile_1_8_to_1_18::new);
                this.register("PlayerProfile", PlayerProfile_1_8_to_1_18::new);
            }
        }

        private void register(String typeName, Function<Map<String, Object>, ? extends Object> builder) {
            this.custom_builders.put(typeName, builder);
        }

        private static String fixProfileName(String name) {
            int len = name.length();
            StringBuilder result = null;
            for (int i = 0; i < len; ++i) {
                char c = name.charAt(i);
                if (c <= ' ' || c >= '\u007f') {
                    if (result == null) {
                        result = new StringBuilder(len);
                        result.append(name, 0, i);
                    }
                    result.append('_');
                    continue;
                }
                if (result == null) continue;
                result.append(c);
            }
            if (result == null) {
                if (len > 16) {
                    result = new StringBuilder(name);
                } else {
                    return name;
                }
            }
            if ((len = result.length()) > 16) {
                result.delete(16, len);
            }
            return result.toString();
        }

        public Object construct(Map<?, ?> mapping) {
            String serialized_type = LogicUtil.applyIfNotNull(mapping.get("=="), Object::toString, null);
            if (serialized_type != null) {
                LinkedHashMap typed = (LinkedHashMap)LogicUtil.unsafeCast(mapping);
                for (Object key : mapping.keySet()) {
                    if (key instanceof String) continue;
                    typed = new LinkedHashMap(mapping.size());
                    for (Map.Entry<?, ?> entry : mapping.entrySet()) {
                        typed.put(entry.getKey().toString(), entry.getValue());
                    }
                }
                try {
                    Function<Map<String, Object>, ? extends Object> builder = this.custom_builders.get(serialized_type);
                    if (builder != null) {
                        return builder.apply(typed);
                    }
                    return ConfigurationSerialization.deserializeObject((Map)typed);
                }
                catch (IllegalArgumentException ex) {
                    throw new YAMLException("Could not deserialize object", (Throwable)ex);
                }
            }
            return mapping;
        }
    }

    private static class YamlConstructorFactory {
        private final MappingConstructorFactory mappingFactory;

        public YamlConstructorFactory(MappingConstructorFactory mappingFactory) {
            this.mappingFactory = mappingFactory;
        }

        public SafeConstructor create(LoaderOptions loaderOptions) {
            SafeConstructor ctor = new SafeConstructor(loaderOptions);
            this.initConstructor(ctor);
            return ctor;
        }

        public SafeConstructor create() {
            try {
                Constructor ctor_ctor = SafeConstructor.class.getConstructor(new Class[0]);
                SafeConstructor ctor = (SafeConstructor)ctor_ctor.newInstance(new Object[0]);
                this.initConstructor(ctor);
                return ctor;
            }
            catch (Throwable t) {
                return this.createWithDefaultOptions();
            }
        }

        private SafeConstructor createWithDefaultOptions() {
            return this.create(new LoaderOptions());
        }

        private void initConstructor(SafeConstructor ctor) {
            try {
                Map yamlConstructors = (Map)LogicUtil.unsafeCast(SafeField.get(ctor, "yamlConstructors", Map.class));
                yamlConstructors.computeIfPresent(Tag.MAP, (u, base) -> new ConstructCustomObject((Construct)base));
            }
            catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }

        private class ConstructCustomObject
        implements Construct {
            private final Construct mapConstructor;

            public ConstructCustomObject(Construct mapConstructor) {
                this.mapConstructor = mapConstructor;
            }

            public Object construct(Node node) {
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Unexpected referential mapping structure. Node: " + node);
                }
                return YamlConstructorFactory.this.mappingFactory.construct((Map)this.mapConstructor.construct(node));
            }

            public void construct2ndStep(Node node, Object object) {
                this.mapConstructor.construct2ndStep(node, object);
            }
        }
    }

    private static class PreParser
    extends Reader {
        private Reader baseReader;
        private HeaderBuilder headerBuilder = new HeaderBuilder();
        private final NodeBuilder nodeBuilder = new NodeBuilder(-1);
        private StringBuilder mainHeader = new StringBuilder();
        private Map<YamlPath, String> headers = new HashMap<YamlPath, String>();
        private StringBuilder currentLine = new StringBuilder();
        private int currentColumn = 0;

        private PreParser() {
        }

        public void open(Reader reader) {
            this.baseReader = reader;
            this.headerBuilder.clear();
            this.nodeBuilder.reset(-1);
            this.mainHeader.setLength(0);
            this.headers.clear();
            this.currentColumn = 0;
            this.currentLine.setLength(0);
        }

        @Override
        public int read() throws IOException {
            if (this.currentColumn == this.currentLine.length()) {
                if (this.readNextLine()) {
                    this.currentColumn = 0;
                } else {
                    this.currentColumn = this.currentLine.length();
                    return -1;
                }
            }
            return this.currentLine.charAt(this.currentColumn++);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int available;
            if (len == 0) {
                return 0;
            }
            int startLen = len;
            do {
                if ((available = this.currentLine.length() - this.currentColumn) == 0) {
                    if (this.readNextLine()) {
                        this.currentColumn = 0;
                        available = this.currentLine.length();
                    } else {
                        this.currentColumn = this.currentLine.length();
                        break;
                    }
                }
                if (len > available) {
                    this.currentLine.getChars(this.currentColumn, this.currentLine.length(), cbuf, off);
                    this.currentColumn = this.currentLine.length();
                    off += available;
                    continue;
                }
                this.currentLine.getChars(this.currentColumn, this.currentColumn + len, cbuf, off);
                this.currentColumn += len;
                return startLen;
            } while ((len -= available) > 0);
            return len == startLen ? -1 : startLen - len;
        }

        @Override
        public void close() throws IOException {
            this.baseReader.close();
        }

        private boolean readNextLine() throws IOException {
            int contentStart;
            int rawChar;
            this.currentLine.setLength(0);
            int numNewlineChars = 0;
            while ((rawChar = this.baseReader.read()) != -1) {
                char c = (char)rawChar;
                this.currentLine.append(c);
                if (c == '\r') {
                    ++numNewlineChars;
                    continue;
                }
                if (c != '\n') continue;
                ++numNewlineChars;
                break;
            }
            if (this.currentLine.length() == 0) {
                return false;
            }
            for (int i = 0; i < this.currentLine.length(); ++i) {
                char c = this.currentLine.charAt(i);
                if (c == '\t') {
                    this.currentLine.setCharAt(i, ' ');
                    if (this.nodeBuilder.getIndent() == -1) {
                        this.nodeBuilder.setIndent(2);
                    }
                    for (int n = 1; n < this.nodeBuilder.getIndent(); ++n) {
                        this.currentLine.insert(++i, ' ');
                    }
                    continue;
                }
                if (c != ' ') break;
            }
            for (contentStart = 0; contentStart < this.currentLine.length() && this.currentLine.charAt(contentStart) == ' '; ++contentStart) {
            }
            int contentEnd = this.currentLine.length() - numNewlineChars;
            int contentLen = contentEnd - contentStart;
            if (contentLen == 2 && this.currentLine.charAt(contentEnd - 2) == '*' && this.currentLine.charAt(contentEnd - 1) == ':') {
                this.currentLine.replace(contentEnd - 2, contentEnd, "'*':");
                contentEnd += 2;
                contentLen += 2;
            }
            if (contentStart == 0 && contentLen >= 2 && this.currentLine.charAt(0) == '#' && this.currentLine.charAt(1) == '>') {
                int headerStart = 2;
                if (headerStart < contentEnd && this.currentLine.charAt(2) == ' ') {
                    ++headerStart;
                }
                if (this.mainHeader.length() > 0) {
                    this.mainHeader.append('\n');
                }
                this.mainHeader.append(this.currentLine, headerStart, contentEnd);
                this.currentLine.setLength(1);
                this.currentLine.setCharAt(0, '\n');
                return true;
            }
            if (this.headerBuilder.handle(this.currentLine, contentStart, contentEnd)) {
                this.currentLine.setLength(1);
                this.currentLine.setCharAt(0, '\n');
                return true;
            }
            this.nodeBuilder.handle(this.currentLine, contentStart, contentEnd);
            if (this.headerBuilder.hasHeader()) {
                this.headers.put(this.nodeBuilder.getYamlPath(), this.headerBuilder.getHeader());
                this.headerBuilder.clear();
            }
            for (int i = contentStart; i < contentEnd - 1; ++i) {
                if (this.currentLine.charAt(i) != '&') continue;
                char following = this.currentLine.charAt(i + 1);
                if (following == '&') {
                    this.currentLine.deleteCharAt(i);
                    --contentEnd;
                    continue;
                }
                if (!StringUtil.isChatCode(following)) continue;
                this.currentLine.setCharAt(i, '\u00a7');
                ++i;
            }
            return true;
        }
    }

    public static class Output {
        public int indent;
        public Map<?, ?> root;
        public Map<YamlPath, String> headers;
    }
}

