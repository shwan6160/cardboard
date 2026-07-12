/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.mountiplex.reflection.ReflectionUtil
 *  com.bergerkiller.mountiplex.reflection.util.BoxedType
 *  com.bergerkiller.mountiplex.reflection.util.FastMethod
 *  org.bukkit.command.CommandSender
 */
package com.bergerkiller.bukkit.tc.properties.registry;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorCondition;
import com.bergerkiller.bukkit.tc.commands.selector.TCSelectorHandlerRegistry;
import com.bergerkiller.bukkit.tc.exception.command.NoPermissionForAnyPropertiesException;
import com.bergerkiller.bukkit.tc.exception.command.NoPermissionForPropertyException;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyRegistry;
import com.bergerkiller.bukkit.tc.properties.api.IPropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyInvalidInputException;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorCondition;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyInputContext;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;

public final class TCPropertyRegistry
implements IPropertyRegistry,
LibraryComponent {
    private static final Pattern LITERALS_PATTERN = Pattern.compile("([\\w\\s]+)\\|?");
    private final TrainCarts plugin;
    private final CloudSimpleHandler commands;
    private final Map<IProperty<Object>, PropertyDetails<Object>> properties = new HashMap<IProperty<Object>, PropertyDetails<Object>>();
    private final Map<String, IProperty<Object>> propertiesByListedName = new HashMap<String, IProperty<Object>>();
    private Collection<IProperty<Object>> cachedPropertiesAll = null;
    private Map<String, IProperty<Object>> cachedPropertiesByListedName = null;
    private final Map<String, PropertyParserElement<?>> parsersByName = new HashMap();
    private final Map<String, PropertyParserElement<?>> parsersByPreProcessedName = new HashMap();
    private final List<PropertyParserElement<?>> parsersWithComplexRegex = new ArrayList();
    private final List<IProperty<?>> pendingProperties = new ArrayList();
    private IProperty<?> currentPropertyBeingParsed = null;

    public TCPropertyRegistry(TrainCarts plugin, CloudSimpleHandler commands) {
        this.plugin = plugin;
        this.commands = commands;
    }

    public void enable() {
        this.commands.getParser().registerBuilderModifier(PropertyCheckPermission.class, (annot, builder) -> {
            IProperty<?> property = this.currentlyParsedProperty();
            String propertyName = annot.value();
            return builder.prependHandler(context -> {
                CommandSender sender = (CommandSender)context.sender();
                if (!Permission.COMMAND_PROPERTIES.has(sender) && !Permission.COMMAND_GLOBALPROPERTIES.has(sender)) {
                    throw new NoPermissionForAnyPropertiesException();
                }
                if (!property.hasPermission(sender, propertyName)) {
                    throw new NoPermissionForPropertyException(propertyName);
                }
            });
        });
        this.pendingProperties.forEach(this::parsePropertyAnnotations);
        this.pendingProperties.clear();
    }

    public void disable() {
    }

    private IProperty<?> currentlyParsedProperty() {
        if (this.currentPropertyBeingParsed == null) {
            throw new IllegalStateException("No property is being parsed right now");
        }
        return this.currentPropertyBeingParsed;
    }

    @Override
    public void register(IProperty<?> property) {
        PropertyDetails details = (PropertyDetails)CommonUtil.unsafeCast(this.createDetails(property));
        PropertyDetails<Object> previous = this.properties.put(details.property, details);
        this.invalidateCachedCollections();
        if (previous != null) {
            this.onPropertyRemoved(previous);
        }
        details.parsers.forEach(this::registerParser);
        details.conditions.forEach(this::registerCondition);
        if (details.property.isListed()) {
            this.propertiesByListedName.put(details.listedName, details.property);
        }
        if (this.commands.isEnabled()) {
            this.parsePropertyAnnotations(property);
        } else {
            this.pendingProperties.add(property);
        }
    }

    private void parsePropertyAnnotations(IProperty<?> property) {
        this.currentPropertyBeingParsed = property;
        try {
            this.commands.annotations(property);
        }
        finally {
            this.currentPropertyBeingParsed = null;
        }
    }

    @Override
    public void unregister(IProperty<?> property) {
        PropertyDetails<Object> removed = this.properties.remove(property);
        if (removed != null) {
            this.onPropertyRemoved(removed);
            this.invalidateCachedCollections();
        }
    }

    private void invalidateCachedCollections() {
        this.cachedPropertiesAll = null;
        this.cachedPropertiesByListedName = null;
    }

    private void onPropertyRemoved(PropertyDetails<Object> details) {
        details.parsers.forEach(this::unregisterParser);
        details.conditions.forEach(this::unregisterCondition);
        if (details.property.isListed()) {
            this.propertiesByListedName.remove(details.listedName, details.property);
        }
    }

    @Override
    public <T> Optional<IPropertyParser<T>> findParser(String name) {
        RegistryPropertyParser search = new RegistryPropertyParser(this.plugin, name);
        return this.findParserElement(search) ? Optional.of(search) : Optional.empty();
    }

    @Override
    public Collection<IProperty<Object>> all() {
        Collection<IProperty<Object>> all = this.cachedPropertiesAll;
        if (all == null) {
            this.cachedPropertiesAll = all = Collections.unmodifiableCollection(new ArrayList<IProperty<Object>>(this.properties.keySet()));
        }
        return all;
    }

    @Override
    public Map<String, IProperty<Object>> byListedName() {
        Map<String, IProperty<Object>> byListedName = this.cachedPropertiesByListedName;
        if (byListedName == null) {
            this.cachedPropertiesByListedName = byListedName = Collections.unmodifiableMap(new HashMap<String, IProperty<Object>>(this.propertiesByListedName));
        }
        return byListedName;
    }

    private void registerCondition(PropertySelectorConditionElement<?> condition) {
        TCSelectorHandlerRegistry registry = (TCSelectorHandlerRegistry)this.plugin.getSelectorHandlerRegistry();
        for (String name : condition.names) {
            registry.registerCondition(name, condition);
        }
    }

    private void unregisterCondition(PropertySelectorConditionElement<?> condition) {
        TCSelectorHandlerRegistry registry = (TCSelectorHandlerRegistry)this.plugin.getSelectorHandlerRegistry();
        for (String name : condition.names) {
            registry.unregisterCondition(name);
        }
    }

    private <T> void registerParser(PropertyParserElement<T> parser) {
        List<String> literals = TCPropertyRegistry.findPatternLiterals(parser.options.value());
        if (literals.isEmpty()) {
            this.parsersWithComplexRegex.add(parser);
        } else {
            Map<String, PropertyParserElement<?>> literalMap = parser.options.preProcess() ? this.parsersByPreProcessedName : this.parsersByName;
            literals.forEach(literal -> literalMap.put((String)literal, parser));
        }
    }

    private <T> void unregisterParser(PropertyParserElement<T> parser) {
        List<String> literals = TCPropertyRegistry.findPatternLiterals(parser.options.value());
        if (literals.isEmpty()) {
            this.parsersWithComplexRegex.remove(parser);
        } else {
            Map<String, PropertyParserElement<?>> literalMap = parser.options.preProcess() ? this.parsersByPreProcessedName : this.parsersByName;
            for (String literal : literals) {
                PropertyParserElement<?> removed = literalMap.remove(literal);
                if (removed == parser || removed == null) continue;
                literalMap.put(literal, removed);
            }
        }
    }

    private <T> boolean findParserElement(RegistryPropertyParser<T> parser) {
        PropertyParserElement result = (PropertyParserElement)CommonUtil.unsafeCast(this.parsersByName.get(parser.name));
        if (result != null && result.match(parser)) {
            return true;
        }
        result = (PropertyParserElement)CommonUtil.unsafeCast(this.parsersByPreProcessedName.get(parser.namePreProcessed));
        if (result != null && result.match(parser)) {
            return true;
        }
        for (PropertyParserElement<?> complexParserElementRaw : this.parsersWithComplexRegex) {
            result = (PropertyParserElement)CommonUtil.unsafeCast(complexParserElementRaw);
            if (!result.match(parser)) continue;
            return true;
        }
        return false;
    }

    private <T> PropertyDetails<T> createDetails(IProperty<T> property) {
        List parsers = ReflectionUtil.getAllMethods(property.getClass()).map(method -> {
            PropertyParser parser = method.getAnnotation(PropertyParser.class);
            if (parser == null) {
                return null;
            }
            try {
                return new PropertyParserElement(property, parser, (Method)method);
            }
            catch (PatternSyntaxException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid syntax of property parser " + method.toGenericString(), ex);
                return null;
            }
            catch (ParserIncorrectSignatureException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid method signature of property parser", ex);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List conditions = ReflectionUtil.getAllMethods(property.getClass()).map(method -> {
            PropertySelectorCondition[] options = (PropertySelectorCondition[])method.getAnnotationsByType(PropertySelectorCondition.class);
            if (options.length == 0) {
                return null;
            }
            try {
                return new PropertySelectorConditionElement(property, options, (Method)method);
            }
            catch (SelectorConditionIncorrectSignatureException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Invalid method signature of property selector condition", ex);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return new PropertyDetails<T>(property, parsers, conditions);
    }

    public static List<String> findPatternLiterals(String pattern) {
        if (pattern.startsWith("(") && pattern.endsWith(")")) {
            pattern = pattern.substring(1, pattern.length() - 1);
        }
        Matcher matcher = LITERALS_PATTERN.matcher(pattern);
        int expectedStart = 0;
        int endIndex = pattern.length();
        ArrayList<String> literals = new ArrayList<String>();
        while (matcher.find() && matcher.start() == expectedStart) {
            literals.add(matcher.group(1));
            if (matcher.end() == endIndex) {
                return literals;
            }
            expectedStart = matcher.end();
        }
        return Collections.emptyList();
    }

    private static class PropertyDetails<T> {
        public final IProperty<T> property;
        public final String listedName;
        public final List<PropertyParserElement<T>> parsers;
        public final List<PropertySelectorConditionElement<T>> conditions;

        public PropertyDetails(IProperty<T> property, List<PropertyParserElement<T>> parsers, List<PropertySelectorConditionElement<T>> conditions) {
            this.property = property;
            this.listedName = property.getListedName();
            this.parsers = parsers;
            this.conditions = conditions;
        }
    }

    private static class RegistryPropertyParser<T>
    implements IPropertyParser<T> {
        public final TrainCarts plugin;
        public final String name;
        public final String namePreProcessed;
        public PropertyParserElement<T> parser;
        public MatchResult matchResult;

        public RegistryPropertyParser(TrainCarts plugin, String name) {
            this.plugin = plugin;
            this.name = name;
            this.namePreProcessed = name.trim().toLowerCase(Locale.ENGLISH);
            this.parser = null;
            this.matchResult = null;
        }

        @Override
        public IProperty<T> getProperty() {
            return this.parser.property;
        }

        @Override
        public String getName() {
            return this.parser.options.preProcess() ? this.namePreProcessed : this.name;
        }

        @Override
        public boolean isInputPreProcessed() {
            return this.parser.options.preProcess();
        }

        @Override
        public boolean isProcessedPerCart() {
            return this.parser.options.processPerCart();
        }

        @Override
        public PropertyParseResult<T> parse(IProperties properties, PropertyInputContext inputContext) {
            IProperty<T> property = this.getProperty();
            String name = this.getName();
            try {
                Object value;
                if (this.parser.inputIsString) {
                    value = this.parser.method.invoke(property, (Object)inputContext.input());
                } else {
                    T currentValue;
                    if (properties != null) {
                        try {
                            currentValue = properties.get(property);
                        }
                        catch (Throwable t) {
                            this.plugin.getLogger().log(Level.SEVERE, "Failed to read property value of '" + this.name + "'", t);
                            currentValue = property.getDefault();
                        }
                    } else {
                        currentValue = property.getDefault();
                    }
                    PropertyParseContext<T> context = new PropertyParseContext<T>(this.plugin, properties, currentValue, name, inputContext, this.matchResult);
                    value = this.parser.method.invoke(property, context);
                }
                return PropertyParseResult.success(inputContext, property, name, value);
            }
            catch (PropertyInvalidInputException ex) {
                return PropertyParseResult.failInvalidInput(inputContext, property, name, Localization.PROPERTY_INVALID_INPUT.get(name, inputContext.input(), ex.getMessage()));
            }
            catch (Throwable t) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to parse property '" + this.name + "'", t);
                return PropertyParseResult.failError(inputContext, property, this.name);
            }
        }
    }

    public static class PropertySelectorConditionElement<T>
    implements IPropertySelectorCondition {
        public final IProperty<T> property;
        public final String[] names;
        private final ArgumentAdapter[] argumentAdapters;
        private final ReturnAdapter returnAdapter;
        private final FastMethod<Object> method;

        public PropertySelectorConditionElement(IProperty<T> property, PropertySelectorCondition[] options, Method method) throws SelectorConditionIncorrectSignatureException {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new SelectorConditionIncorrectSignatureException(method, "Must not be a static method");
            }
            if (method.getReturnType() == Void.TYPE) {
                throw new SelectorConditionIncorrectSignatureException(method, "Method should return a value, but return type is void");
            }
            Class<?>[] argTypes = method.getParameterTypes();
            this.argumentAdapters = new ArgumentAdapter[argTypes.length];
            for (int i = 0; i < argTypes.length; ++i) {
                Class<Object> argType = argTypes[i];
                if (argType.isAssignableFrom(TrainProperties.class)) {
                    this.argumentAdapters[i] = (properties, condition) -> properties;
                    continue;
                }
                if (argType.isAssignableFrom(SelectorCondition.class)) {
                    this.argumentAdapters[i] = (properties, condition) -> condition;
                    continue;
                }
                throw new SelectorConditionIncorrectSignatureException(method, "Method parameter #" + (i + 1) + " has incompatible type " + argType.getName());
            }
            Class<?> returnType = BoxedType.getBoxedType(method.getReturnType());
            if (returnType == null) {
                returnType = method.getReturnType();
            }
            if (returnType == Boolean.class) {
                this.returnAdapter = (condition, value) -> (Boolean)value;
            } else if (returnType == String.class) {
                this.returnAdapter = (condition, value) -> condition.matchesText((String)value);
            } else if (returnType == Float.class || returnType == Double.class) {
                this.returnAdapter = (condition, value) -> condition.matchesNumber(((Number)value).doubleValue());
            } else if (Number.class.isAssignableFrom(returnType)) {
                this.returnAdapter = (condition, value) -> condition.matchesNumber(((Number)value).longValue());
            } else {
                throw new SelectorConditionIncorrectSignatureException(method, "Method has incompatible return type " + returnType.getName());
            }
            this.property = property;
            this.names = (String[])Stream.of(options).map(PropertySelectorCondition::value).toArray(String[]::new);
            this.method = new FastMethod();
            this.method.init(method);
        }

        @Override
        public boolean matches(CommandSender sender, TrainProperties properties, SelectorCondition condition) {
            Object[] args = new Object[this.argumentAdapters.length];
            for (int i = 0; i < args.length; ++i) {
                args[i] = this.argumentAdapters[i].adapt(properties, condition);
            }
            Object result = this.method.invokeVA(this.property, args);
            return this.returnAdapter.adapt(condition, result);
        }

        @FunctionalInterface
        private static interface ArgumentAdapter {
            public Object adapt(TrainProperties var1, SelectorCondition var2);
        }

        @FunctionalInterface
        private static interface ReturnAdapter {
            public boolean adapt(SelectorCondition var1, Object var2);
        }
    }

    public static class PropertyParserElement<T> {
        public final IProperty<T> property;
        public final FastMethod<T> method;
        public final PropertyParser options;
        public final boolean inputIsString;
        private final Pattern pattern;

        public PropertyParserElement(IProperty<T> property, PropertyParser options, Method method) throws PatternSyntaxException, ParserIncorrectSignatureException {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new ParserIncorrectSignatureException(method, "Must not be a static method");
            }
            if (method.getParameterCount() != 1) {
                throw new ParserIncorrectSignatureException(method, "Parameter count should be 1");
            }
            if (method.getReturnType() == Void.TYPE) {
                throw new ParserIncorrectSignatureException(method, "Method should return a value, but return type is void");
            }
            Class<PropertyParseContext> paramType = method.getParameterTypes()[0];
            this.inputIsString = paramType.equals(String.class);
            if (!this.inputIsString && !paramType.isAssignableFrom(PropertyParseContext.class)) {
                throw new ParserIncorrectSignatureException(method, "First argument should be PropertyParseContext or String");
            }
            this.property = property;
            this.options = options;
            this.pattern = Pattern.compile(PropertyParserElement.anchorRegex(options.value()));
            this.method = new FastMethod();
            this.method.init(method);
        }

        public boolean match(RegistryPropertyParser<T> parser) {
            Matcher matcher = this.pattern.matcher(this.options.preProcess() ? parser.namePreProcessed : parser.name);
            if (matcher.find()) {
                parser.parser = this;
                parser.matchResult = matcher;
                return true;
            }
            return false;
        }

        private static String anchorRegex(String expression) {
            if (!expression.startsWith("^")) {
                expression = "^" + expression;
            }
            if (!expression.endsWith("$")) {
                expression = expression + "$";
            }
            return expression;
        }
    }

    public static class SelectorConditionIncorrectSignatureException
    extends Exception {
        private static final long serialVersionUID = 9081453776342069335L;

        public SelectorConditionIncorrectSignatureException(Method method, String reason) {
            super("Method " + method.toGenericString() + " has invalid signature for a selector condition: " + reason);
        }
    }

    public static class ParserIncorrectSignatureException
    extends Exception {
        private static final long serialVersionUID = -1679698260727072778L;

        public ParserIncorrectSignatureException(Method method, String reason) {
            super("Method " + method.toGenericString() + " has invalid signature for a parser: " + reason);
        }
    }
}

