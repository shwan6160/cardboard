/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.YamlRepresenter
 *  org.yaml.snakeyaml.DumperOptions
 *  org.yaml.snakeyaml.DumperOptions$FlowStyle
 *  org.yaml.snakeyaml.DumperOptions$ScalarStyle
 *  org.yaml.snakeyaml.emitter.Emitable
 *  org.yaml.snakeyaml.emitter.Emitter
 *  org.yaml.snakeyaml.events.StreamEndEvent
 *  org.yaml.snakeyaml.events.StreamStartEvent
 *  org.yaml.snakeyaml.nodes.Node
 *  org.yaml.snakeyaml.representer.BaseRepresenter
 *  org.yaml.snakeyaml.representer.Represent
 *  org.yaml.snakeyaml.resolver.Resolver
 *  org.yaml.snakeyaml.serializer.Serializer
 */
package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.io.StringBuilderWriter;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.BaseRepresenter;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class YamlSerializer {
    private String indentStr;
    private String[] headerPrefixes = new String[]{"#> ", "# "};
    private final DumperOptions dumperOptions = new DumperOptions();
    private final Resolver resolver;
    private final YamlRepresenter representer;
    private final StringBuilder builder;
    private final StringBuilderWriter output;
    private final FastMethod<Void> resetMethod;
    private Emitter emitter;
    private Serializer serializer;
    private Object emitterStateStartValue;
    public static final YamlSerializer INSTANCE = new YamlSerializer();
    private static final HashSet<String> SPECIAL_WORDS = new HashSet<String>(Arrays.asList("", "inf", "Inf", "INF", "nan", "NaN", "NAN", "null", "Null", "NULL", "y", "Y", "yes", "Yes", "YES", "n", "N", "no", "No", "NO", "on", "On", "ON", "off", "Off", "OFF", "true", "True", "TRUE", "false", "False", "FALSE"));

    public YamlSerializer() {
        this.dumperOptions.setIndent(2);
        this.dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.indentStr = StringUtil.getFilledString(" ", this.dumperOptions.getIndent());
        this.resolver = new Resolver();
        this.representer = YamlSerializer.createRepresenter(this.dumperOptions);
        try {
            Field representersField = BaseRepresenter.class.getDeclaredField("representers");
            representersField.setAccessible(true);
            YamlRepresenter quotedRepresenter = YamlSerializer.createRepresenter(this.dumperOptions);
            quotedRepresenter.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
            Map quotedRepresentersByType = (Map)representersField.get(quotedRepresenter);
            Represent quotedStringRepresent = (Represent)quotedRepresentersByType.get(String.class);
            if (quotedStringRepresent == null) {
                throw new IllegalStateException("No String representer");
            }
            Map representersByType = (Map)representersField.get(this.representer);
            representersByType.computeIfPresent(String.class, (clsKey, defaultStringRepresent) -> data -> {
                if (YamlSerializer.stringMustBeQuoted((String)data)) {
                    return quotedStringRepresent.representData(data);
                }
                return defaultStringRepresent.representData(data);
            });
        }
        catch (Throwable t) {
            Logging.LOGGER_CONFIG.log(Level.WARNING, "[Yaml] Failed to enable escaping of special characters", t);
        }
        this.output = new StringBuilderWriter();
        this.resetMethod = new FastMethod();
        this.builder = this.output.getBuilder();
        this.emitter = new Emitter((Writer)this.output, this.dumperOptions);
        this.serializer = new Serializer((Emitable)this.emitter, this.resolver, this.dumperOptions, null);
        try {
            Field emitterEventField = Emitter.class.getDeclaredField("event");
            Field emitterStateField = Emitter.class.getDeclaredField("state");
            Method expectMethod = emitterStateField.getType().getDeclaredMethod("expect", new Class[0]);
            Field serializerClosedField = Serializer.class.getDeclaredField("closed");
            emitterEventField.setAccessible(true);
            emitterStateField.setAccessible(true);
            expectMethod.setAccessible(true);
            serializerClosedField.setAccessible(true);
            emitterEventField.set(this.emitter, new StreamStartEvent(null, null));
            Object startState = emitterStateField.get(this.emitter);
            expectMethod.invoke(startState, new Object[0]);
            emitterEventField.set(this.emitter, null);
            serializerClosedField.set(this.serializer, Boolean.FALSE);
            this.emitterStateStartValue = emitterStateField.get(this.emitter);
            if (this.emitterStateStartValue == null) {
                throw new IllegalStateException("Emitter state is null");
            }
            if (!this.emitterStateStartValue.getClass().getSimpleName().equals("ExpectFirstDocumentStart")) {
                throw new IllegalStateException("Emitter state after initialization is not ExpectFirstDocumentStart");
            }
            ClassResolver resolver = new ClassResolver();
            resolver.addImport(Emitter.class.getName() + "State");
            resolver.addImport(StreamEndEvent.class.getName());
            resolver.setDeclaredClass(Emitter.class);
            MethodDeclaration resetMethodDec = new MethodDeclaration(resolver, "public void reset(Object startState) {\n  #require Emitter private (Object) EmitterState state;\n  #require Emitter private int column;\n  #require Emitter private Integer indent;\n  instance.emit(new StreamEndEvent(null, null));\n  instance#state = startState;\n  instance#column = 0;\n  instance#indent = null;\n}");
            if (!resetMethodDec.isResolved()) {
                throw new IllegalStateException("Failed to resolve reset method: " + resetMethodDec);
            }
            this.resetMethod.init(resetMethodDec);
            this.resetMethod.forceInitialization();
        }
        catch (Throwable t) {
            throw new UnsupportedOperationException("This version of SnakeYAML is not supported", t);
        }
        AbstractMap<Object, Object> noop_map = new AbstractMap<Object, Object>(){

            @Override
            public Set<Map.Entry<Object, Object>> entrySet() {
                return Collections.emptySet();
            }

            @Override
            public boolean containsKey(Object o) {
                return false;
            }

            @Override
            public Object put(Object key, Object value) {
                return null;
            }

            @Override
            public void clear() {
            }
        };
        AbstractSet<Object> noop_set = new AbstractSet<Object>(){

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public boolean add(Object o) {
                return true;
            }

            @Override
            public Iterator<Object> iterator() {
                return Collections.emptyIterator();
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public void clear() {
            }
        };
        try {
            Field representedObjectsField = BaseRepresenter.class.getDeclaredField("representedObjects");
            representedObjectsField.setAccessible(true);
            representedObjectsField.set(this.representer, noop_map);
            representedObjectsField.setAccessible(false);
            Field serializerAnchorsField = Serializer.class.getDeclaredField("anchors");
            serializerAnchorsField.setAccessible(true);
            serializerAnchorsField.set(this.serializer, noop_map);
            serializerAnchorsField.setAccessible(false);
            Field serializerSerializedNodesField = Serializer.class.getDeclaredField("serializedNodes");
            serializerSerializedNodesField.setAccessible(true);
            serializerSerializedNodesField.set(this.serializer, noop_set);
            serializerSerializedNodesField.setAccessible(false);
        }
        catch (Throwable t) {
            Logging.LOGGER_CONFIG.log(Level.SEVERE, "Unhandled error disabling YAML anchors", t);
        }
    }

    private static YamlRepresenter createRepresenter(DumperOptions dumperOptions) {
        YamlRepresenter representer = new YamlRepresenter();
        representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());
        representer.setDefaultScalarStyle(dumperOptions.getDefaultScalarStyle());
        representer.getPropertyUtils().setAllowReadOnlyProperties(dumperOptions.isAllowReadOnlyProperties());
        representer.setTimeZone(dumperOptions.getTimeZone());
        return representer;
    }

    private String build() {
        String str = this.output.toString();
        this.builder.setLength(0);
        return str;
    }

    public void appendHeader(StringBuilder builder, String header, int indent) {
        if (header != null && !header.isEmpty()) {
            int i = 0;
            while (header.charAt(i) == '\n') {
                if (++i != header.length()) continue;
                builder.append(header);
                return;
            }
            String[] headerPrefixes = this.headerPrefixes;
            if (indent >= headerPrefixes.length) {
                String[] new_prefixes = new String[indent + 1];
                System.arraycopy(headerPrefixes, 0, new_prefixes, 0, headerPrefixes.length);
                StringBuilder headerBuilder = new StringBuilder(headerPrefixes[headerPrefixes.length - 1]);
                for (int h = headerPrefixes.length; h < new_prefixes.length; ++h) {
                    headerBuilder.insert(0, this.indentStr);
                    new_prefixes[h] = headerBuilder.toString();
                }
                headerPrefixes = new_prefixes;
                this.headerPrefixes = new_prefixes;
            }
            String headerPrefix = headerPrefixes[indent];
            if (i > 0) {
                builder.append(header).insert(i, headerPrefix);
            } else {
                builder.append(headerPrefix).append(header);
            }
            i += 2;
            while (i < builder.length()) {
                if (builder.charAt(i) == '\n') {
                    builder.insert(i + 1, headerPrefix);
                    i += 2;
                }
                ++i;
            }
            builder.append('\n');
        }
    }

    public synchronized String serialize(Object value) throws SerializeException {
        return this.serialize(value, "", 1);
    }

    public synchronized String serializeKey(String key, String header, int indent, boolean isListElement) {
        this.appendHeader(header, indent);
        this.appendIndent(indent);
        if (isListElement && indent > 0) {
            this.builder.setCharAt(this.builder.length() - 2, '-');
        }
        if (key.length() == 1 && key.charAt(0) == '*') {
            this.builder.append("*:\n");
        } else {
            try {
                this.appendKeyValue(key, 0, 0, true);
            }
            catch (SerializeException ex) {
                throw new RuntimeException("Unexpected error serializing key '" + key + "'", ex.getCause());
            }
            this.builder.setLength(this.builder.length() - 3);
            this.builder.append('\n');
        }
        return this.build();
    }

    public synchronized String serialize(Object value, String header, int indent) throws SerializeException {
        Map m;
        this.appendHeader(header, indent);
        if (value instanceof Map && (m = (Map)value).size() == 1) {
            Map.Entry entry = m.entrySet().iterator().next();
            this.appendKeyValue((String)entry.getKey(), entry.getValue(), indent, true);
            return this.build();
        }
        this.appendValue(value, indent, true);
        return this.build();
    }

    private void appendHeader(String header, int indent) {
        this.appendHeader(this.builder, header, indent);
    }

    private void appendKeyValue(String key, Object value, int indent, boolean indentFirstLine) throws SerializeException {
        if (!this.canWriteStringLiteral(key, true)) {
            this.appendValue(Collections.singletonMap(key, value), indent, indentFirstLine);
            return;
        }
        if (indentFirstLine) {
            this.appendIndent(indent);
        }
        if (value == null) {
            this.builder.append(key);
            this.builder.append(": ~\n");
            return;
        }
        if (value instanceof Number) {
            this.builder.append(key);
            this.builder.append(": ");
            Number valueNum = (Number)value;
            if (value instanceof Integer) {
                this.builder.append(valueNum.intValue());
            } else if (value instanceof Double) {
                this.builder.append(valueNum.doubleValue());
            } else {
                this.builder.append(value.toString());
            }
            this.builder.append('\n');
            return;
        }
        if (value instanceof String) {
            String valueStr = value.toString();
            if (this.canWriteStringLiteral(valueStr, false)) {
                this.builder.append(key);
                this.builder.append(": ");
                this.builder.append(valueStr);
                this.builder.append('\n');
                return;
            }
        } else if (value instanceof Boolean) {
            this.builder.append(key);
            if (((Boolean)value).booleanValue()) {
                this.builder.append(": true\n");
            } else {
                this.builder.append(": false\n");
            }
            return;
        }
        this.appendValue(Collections.singletonMap(key, value), indent, false);
    }

    private void appendValue(Object value, int indent, boolean indentFirstLine) throws SerializeException {
        Node node;
        try {
            node = this.representer.represent(value);
        }
        catch (Throwable t) {
            throw new SerializeException(value, t);
        }
        this.appendNode(node, indent, indentFirstLine);
    }

    private void appendIndent(int indent) {
        for (int i = 1; i < indent; ++i) {
            this.builder.append(this.indentStr);
        }
    }

    private void appendNode(Node node, int indent, boolean indentFirstLine) {
        int oldTrailingLength = this.builder.length();
        this.resetMethod.invoke(this.emitter, this.emitterStateStartValue);
        this.builder.setLength(oldTrailingLength);
        int valueInitialOffset = this.builder.length();
        try {
            if (indentFirstLine) {
                for (int i = 1; i < indent; ++i) {
                    this.builder.append(this.indentStr);
                }
            }
            this.serializer.serialize(node);
        }
        catch (IOException i) {
            // empty catch block
        }
        int newLength = this.builder.length();
        if (newLength == 0 || this.builder.charAt(newLength - 1) != '\n') {
            this.builder.append('\n');
        }
        for (int i = valueInitialOffset; i < this.builder.length() - 1; ++i) {
            char c = this.builder.charAt(i);
            if (c == '\u00a7' && StringUtil.isChatCode(this.builder.charAt(i + 1))) {
                this.builder.setCharAt(i, '&');
                ++i;
                continue;
            }
            if (c != '&' || !StringUtil.isChatCode(this.builder.charAt(i + 1))) continue;
            this.builder.insert(i, '&');
            ++i;
        }
        if (indent > 1) {
            String fullIndentStr = StringUtil.getFilledString(this.indentStr, indent - 1);
            int indentStart = Integer.MAX_VALUE;
            for (int i = valueInitialOffset; i < this.builder.length(); ++i) {
                char c = this.builder.charAt(i);
                if (c != '\n') continue;
                if (i > indentStart) {
                    this.builder.insert(indentStart, fullIndentStr);
                    i += fullIndentStr.length();
                }
                indentStart = i + 1;
            }
        }
    }

    private static boolean stringMustBeQuoted(String str) {
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (c != '[' && c != ']' && c != '{' && c != '}' && c != '&' && c != '$' && c != '\'' && c != '\u00a7') continue;
            return true;
        }
        return false;
    }

    private boolean canWriteStringLiteral(String str, boolean isKey) {
        if (SPECIAL_WORDS.contains(str)) {
            return false;
        }
        boolean hasDigits = false;
        boolean hasSpecial = false;
        boolean hasLetters = false;
        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                hasLetters = true;
                continue;
            }
            if (c == '_' || c == '-') {
                hasSpecial = true;
                continue;
            }
            if (isKey && c >= '0' && c <= '9') {
                hasDigits = true;
                continue;
            }
            return false;
        }
        if (hasSpecial && !hasLetters && hasDigits) {
            return false;
        }
        return !Character.isWhitespace(str.charAt(0)) && !Character.isWhitespace(str.charAt(len - 1));
    }

    public static final class SerializeException
    extends Exception {
        public SerializeException(Object value, Throwable cause) {
            super("Failed to serialize: " + value, cause);
        }
    }
}

