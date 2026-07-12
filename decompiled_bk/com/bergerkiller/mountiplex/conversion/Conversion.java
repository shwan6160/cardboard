/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.conversion;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.ConverterProvider;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.conversion.annotations.ProviderMethod;
import com.bergerkiller.mountiplex.conversion.builtin.ArrayConversion;
import com.bergerkiller.mountiplex.conversion.builtin.BooleanConversion;
import com.bergerkiller.mountiplex.conversion.builtin.CollectionConversion;
import com.bergerkiller.mountiplex.conversion.builtin.EnumConversion;
import com.bergerkiller.mountiplex.conversion.builtin.MapConversion;
import com.bergerkiller.mountiplex.conversion.builtin.NumberConversion;
import com.bergerkiller.mountiplex.conversion.builtin.StreamConversion;
import com.bergerkiller.mountiplex.conversion.builtin.ToStringConversion;
import com.bergerkiller.mountiplex.conversion.builtin.VoidTypeConverter;
import com.bergerkiller.mountiplex.conversion.type.AnnotatedConverter;
import com.bergerkiller.mountiplex.conversion.type.AnnotatedProvider;
import com.bergerkiller.mountiplex.conversion.type.CastingConverter;
import com.bergerkiller.mountiplex.conversion.type.ChainConverter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.FieldDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.InputTypeMap;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Conversion {
    private static final DeferLock deferLock = new DeferLock();
    private static final Map<TypeTuple, Converter<Object, Object>> converters = new HashMap<TypeTuple, Converter<Object, Object>>();
    private static final ArrayList<ConverterProvider> providers = new ArrayList();

    public static void registerConverter(Converter<?, ?> converter) {
        try {
            Conversion.verifyConverter(converter);
            Conversion.registerConverterImpl(converter);
            if (converter instanceof DuplexConverter) {
                Conversion.registerConverterImpl(((DuplexConverter)converter).reverse());
            }
        }
        catch (InvalidConverterException ex) {
            MountiplexUtil.LOGGER.warning(ex.getMessage() + ": " + converter);
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "An error occurred registering " + converter, t);
        }
    }

    public static void registerProvider(ConverterProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider is null");
        }
        deferLock.schedule(() -> {
            providers.add(provider);
            OutputConverterList.resetAll();
            OutputConverterTree.resetAll();
            converters.clear();
        });
    }

    public static void registerConverters(Class<?> converterListClass) {
        for (Method method : converterListClass.getDeclaredMethods()) {
            MethodDeclaration m;
            if (method.getAnnotation(ConverterMethod.class) != null) {
                try {
                    TypeDeclaration input = AnnotatedConverter.parseType(method, true);
                    TypeDeclaration output = AnnotatedConverter.parseType(method, false);
                    if (input == null || output == null) continue;
                    MethodDeclaration methodDec = new MethodDeclaration(ClassResolver.DEFAULT, method);
                    if (input.hasTypeVariables() || output.hasTypeVariables()) {
                        Conversion.registerProvider(new AnnotatedConverter.GenericProvider(methodDec, input, output));
                    } else {
                        Conversion.registerConverter(new AnnotatedConverter(methodDec, input, output));
                    }
                }
                catch (Throwable t) {
                    m = new MethodDeclaration(ClassResolver.DEFAULT, method);
                    MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to register static converter method " + m.toString(), t);
                }
            }
            if (method.getAnnotation(ProviderMethod.class) == null) continue;
            try {
                Conversion.registerProvider(new AnnotatedProvider(method));
            }
            catch (Throwable t) {
                m = new MethodDeclaration(ClassResolver.DEFAULT, method);
                MountiplexUtil.LOGGER.log(Level.SEVERE, "Failed to register static provider method " + m.toString(), t);
            }
        }
        for (AccessibleObject accessibleObject : converterListClass.getDeclaredFields()) {
            FieldDeclaration f;
            if (!Modifier.isStatic(((Field)accessibleObject).getModifiers())) continue;
            if (Converter.class.isAssignableFrom(((Field)accessibleObject).getType())) {
                Converter converter = null;
                try {
                    ((Field)accessibleObject).setAccessible(true);
                    converter = (Converter)((Field)accessibleObject).get(null);
                }
                catch (Throwable t) {
                    f = new FieldDeclaration(ClassResolver.DEFAULT, (Field)accessibleObject);
                    MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to register static converter field " + f.toString(), t);
                    continue;
                }
                Conversion.registerConverter(converter);
            }
            if (!ConverterProvider.class.isAssignableFrom(((Field)accessibleObject).getType())) continue;
            ConverterProvider provider = null;
            try {
                ((Field)accessibleObject).setAccessible(true);
                provider = (ConverterProvider)((Field)accessibleObject).get(null);
            }
            catch (Throwable t) {
                f = new FieldDeclaration(ClassResolver.DEFAULT, (Field)accessibleObject);
                MountiplexUtil.LOGGER.log(Level.WARNING, "Failed to register static provider field " + f.toString(), t);
                continue;
            }
            Conversion.registerProvider(provider);
        }
    }

    public static <T> InputConverter<T> find(Class<T> output) {
        return Conversion.find(TypeDeclaration.fromClass(output));
    }

    public static <I, O> Converter<I, O> find(Class<I> input, Class<O> output) {
        return Conversion.find(TypeDeclaration.fromClass(input), TypeDeclaration.fromClass(output));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static InputConverter<?> find(TypeDeclaration output) {
        deferLock.lock();
        try {
            OutputConverterTree tree = OutputConverterTree.getIfExists(output);
            if (tree != null) {
                InputConverter<Object> inputConverter = tree.converter;
                return inputConverter;
            }
        }
        finally {
            deferLock.unlock();
        }
        Conversion.initType(output);
        deferLock.lock();
        try {
            InputConverter<Object> inputConverter = OutputConverterTree.get((TypeDeclaration)output).converter;
            return inputConverter;
        }
        finally {
            deferLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Converter<Object, Object> find(TypeDeclaration input, TypeDeclaration output) {
        Converter<Object, Object> converter;
        OutputConverterTree.NodeConverterProvider node;
        TypeTuple key;
        try {
            key = new TypeTuple(input, output);
        }
        catch (RuntimeException ex) {
            if (input == null) {
                throw new IllegalArgumentException("Input type is null");
            }
            if (output == null) {
                throw new IllegalArgumentException("Output type is null");
            }
            throw ex;
        }
        OutputConverterTree outputTree = null;
        OutputConverterTree.Node inputNode = null;
        deferLock.lock();
        try {
            NullConverter result = converters.get(key);
            if (result != null) {
                NullConverter nullConverter = result;
                return nullConverter;
            }
            if (input.isInstanceOf(output)) {
                result = new NullConverter(input, output);
                converters.put(key, result);
                NullConverter nullConverter = result;
                return nullConverter;
            }
            outputTree = OutputConverterTree.getIfExists(output);
            if (outputTree != null) {
                inputNode = outputTree.findInMapping(input);
            }
        }
        finally {
            deferLock.unlock();
        }
        if (outputTree == null) {
            Conversion.initType(output);
        }
        if (inputNode == null) {
            Conversion.initType(input);
        }
        deferLock.lock();
        try {
            Converter<Object, Object> result = converters.get(key);
            if (result != null) {
                Converter<Object, Object> converter2 = result;
                return converter2;
            }
            if (outputTree == null) {
                outputTree = OutputConverterTree.get(output);
            }
            if ((node = outputTree.discoverConverter(input)) == null) {
                result = null;
                return result;
            }
            if (node.isToConverterLockSafe()) {
                converter = node.toConverter(input, output);
                if (converter == null) {
                    Converter<Object, Object> converter3 = null;
                    return converter3;
                }
                converters.put(key, converter);
                if (converter instanceof DuplexConverter) {
                    converters.put(key.reverse(), ((DuplexConverter)converter).reverse());
                }
                Converter<Object, Object> converter4 = converter;
                return converter4;
            }
        }
        finally {
            deferLock.unlock();
        }
        converter = node.toConverter(input, output);
        if (converter == null) {
            return null;
        }
        deferLock.lock();
        try {
            Converter<Object, Object> existing = converters.putIfAbsent(key, converter);
            if (existing != null) {
                Converter<Object, Object> converter5 = existing;
                return converter5;
            }
            if (converter instanceof DuplexConverter) {
                converters.put(key.reverse(), ((DuplexConverter)converter).reverse());
            }
        }
        finally {
            deferLock.unlock();
        }
        return converter;
    }

    public static <I, O> DuplexConverter<I, O> findDuplex(Class<I> inputType, Class<O> outputType) {
        return DuplexConverter.pair(Conversion.find(inputType, outputType), Conversion.find(outputType, inputType));
    }

    public static DuplexConverter<Object, Object> findDuplex(TypeDeclaration input, TypeDeclaration output) {
        return DuplexConverter.pair(Conversion.find(input, output), Conversion.find(output, input));
    }

    public static void debugTree(Class<?> input, Class<?> output) {
        Conversion.debugTree(TypeDeclaration.fromClass(input), TypeDeclaration.fromClass(output));
    }

    public static void debugTree(TypeDeclaration input, TypeDeclaration output) {
        OutputConverterTree tree = OutputConverterTree.get(output);
        StringBuilder str = new StringBuilder();
        String header = "====== Converting " + input + " -> " + output + " ======";
        str.append(header).append('\n');
        tree.root.debugPrint((OutputConverterTree.Node)tree.mapping.get(input), str, 2);
        for (int i = 0; i < header.length(); ++i) {
            str.append('=');
        }
        str.append('\n');
        System.out.println(str);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void debugExportConverterTree(String filePath) {
        try (OutputStreamWriter logFile = new OutputStreamWriter(new FileOutputStream(filePath));){
            deferLock.lock();
            try {
                for (Converter<Object, Object> converter : converters.values()) {
                    logFile.write(converter.toString());
                    logFile.write("\r\n\r\n");
                }
            }
            finally {
                deferLock.unlock();
            }
        }
        catch (Throwable t) {
            MountiplexUtil.LOGGER.log(Level.SEVERE, "[Debug] Failed to export converter tree", t);
        }
    }

    private static void registerConverterImpl(Converter<?, ?> converter) {
        deferLock.schedule(() -> {
            int num_typeVariables;
            Conversion.addConverterToMapping(converter);
            if (converter.output.genericTypes.length == 0 && (num_typeVariables = converter.output.type.getTypeParameters().length) > 0) {
                Object[] genericTypes = new TypeDeclaration[num_typeVariables];
                Arrays.fill(genericTypes, TypeDeclaration.ANY);
                TypeDeclaration any_output = converter.output.setGenericTypes((TypeDeclaration[])genericTypes);
                Conversion.addConverterToMapping(new ChainConverter(Arrays.asList(converter, new NullConverter(converter.output, any_output))));
            }
        });
    }

    private static void addConverterToMapping(Converter<?, ?> converter) {
        OutputConverterList.get(converter.output).addConverter(converter);
        OutputConverterTree.resetTypeChange(converter.input);
        OutputConverterTree.resetTypeChange(converter.output);
        converters.put(new TypeTuple(converter), converter);
    }

    private static boolean initType(TypeDeclaration type) {
        if (type.type != null && Template.Handle.class.isAssignableFrom(type.type)) {
            Resolver.initializeClass(type.type);
            return true;
        }
        return false;
    }

    private static void initTypeAndProcessPending(TypeDeclaration type) {
        if (Conversion.initType(type)) {
            deferLock.processPending();
        }
    }

    private static void verifyConverter(Converter<?, ?> converter) throws InvalidConverterException {
        if (converter == null) {
            throw new InvalidConverterException("Converter is null");
        }
        if (!converter.input.isValid()) {
            throw new InvalidConverterException("Converter has invalid input");
        }
        if (!converter.output.isValid()) {
            throw new InvalidConverterException("Converter has invalid output");
        }
        if (!converter.input.isResolved()) {
            throw new InvalidConverterException("Converter has unresolved input");
        }
        if (!converter.output.isResolved()) {
            throw new InvalidConverterException("Converter has unresolved output");
        }
    }

    static {
        for (Class<?> unboxedType : BoxedType.getUnboxedTypes()) {
            Class<?> boxedType = BoxedType.getBoxedType(unboxedType);
            Conversion.registerConverter(new NullConverter(unboxedType, boxedType));
            Conversion.registerConverter(new NullConverter(boxedType, unboxedType));
        }
        NumberConversion.register();
        ToStringConversion.register();
        EnumConversion.register();
        CollectionConversion.register();
        MapConversion.register();
        ArrayConversion.register();
        VoidTypeConverter.register();
        BooleanConversion.register();
        StreamConversion.register();
        MountiplexUtil.registerUnloader(new Runnable(){

            @Override
            public void run() {
                OutputConverterTree.trees = new HashMap(0);
                OutputConverterList.mapping = new HashMap(0);
            }
        });
    }

    private static final class DeferLock
    extends ReentrantLock {
        private static final long serialVersionUID = 3302331820484906636L;
        private final ArrayList<Runnable> pending = new ArrayList();
        private volatile boolean hasPending = false;

        private DeferLock() {
        }

        @Override
        public void lock() {
            super.lock();
            this.processPending();
        }

        @Override
        public void unlock() {
            try {
                this.processPending();
            }
            finally {
                super.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void schedule(Runnable runnable) {
            if (super.tryLock()) {
                try {
                    this.processPending();
                    runnable.run();
                    this.processPending();
                }
                finally {
                    super.unlock();
                }
            }
            ArrayList<Runnable> arrayList = this.pending;
            synchronized (arrayList) {
                this.pending.add(runnable);
                this.hasPending = true;
            }
            if (super.tryLock()) {
                try {
                    this.processPending();
                }
                finally {
                    super.unlock();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void processPending() {
            while (this.hasPending) {
                ArrayList<Runnable> pendingCopy;
                ArrayList<Runnable> arrayList = this.pending;
                synchronized (arrayList) {
                    if (!this.hasPending) {
                        return;
                    }
                    pendingCopy = new ArrayList<Runnable>(this.pending);
                    this.pending.clear();
                    this.hasPending = false;
                }
                pendingCopy.forEach(Runnable::run);
            }
        }
    }

    private static class InvalidConverterException
    extends IllegalStateException {
        private static final long serialVersionUID = 2555039929011231357L;

        public InvalidConverterException(String message) {
            super(message);
        }
    }

    private static final class OutputConverterTree {
        private static HashMap<TypeDeclaration, OutputConverterTree> trees = new HashMap();
        private final Node root;
        private final InputTypeMap<Node> mapping = new InputTypeMap();
        private final ArrayList<Node> lastNodes = new ArrayList();
        private final ArrayList<Node> nextNodes = new ArrayList();
        private Converter<?, Object> nullConverter = null;
        private boolean nullConverterSearched = false;
        private boolean isReset = false;
        public final InputConverter<Object> converter;
        private int stepStuckCounter = 0;

        public OutputConverterTree(TypeDeclaration output) {
            this.root = new Node(null, new NullConverter(output, output));
            this.reset();
            this.converter = new InputConverter<Object>(output){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Converter<Object, Object> getConverter(TypeDeclaration input) {
                    NodeConverterProvider converterProvider;
                    deferLock.lock();
                    try {
                        converterProvider = this.discoverConverter(input);
                        if (converterProvider == null) {
                            Converter<Object, Object> converter = null;
                            return converter;
                        }
                        if (converterProvider.isToConverterLockSafe()) {
                            Converter<Object, Object> converter = converterProvider.toConverter(input, this.output);
                            return converter;
                        }
                    }
                    finally {
                        deferLock.unlock();
                    }
                    return converterProvider.toConverter(input, this.output);
                }

                @Override
                public Converter<?, Object> getNullConverter() {
                    deferLock.lock();
                    try {
                        if (!nullConverterSearched) {
                            nullConverterSearched = true;
                            nullConverter = null;
                            for (Converter converter : OutputConverterList.get(this.output).getConverters()) {
                                if (!converter.acceptsNullInput()) continue;
                                nullConverter = converter;
                                break;
                            }
                        }
                        Converter converter = nullConverter;
                        return converter;
                    }
                    finally {
                        deferLock.unlock();
                    }
                }
            };
        }

        public final NodeConverterProvider discoverConverter(TypeDeclaration input) {
            if (input.type == null) {
                throw new IllegalArgumentException("Unresolved input type: " + input.toString());
            }
            if (input.type.equals(Object.class)) {
                return (ignoredInput, ignoredOutput) -> this.converter;
            }
            while (!this.generateDeeper()) {
            }
            NodeConverterProvider foundProvider = this.findInMapping(input);
            if (foundProvider == null && this.converter.output.isInstanceOf(input)) {
                foundProvider = CastingConverter::new;
            }
            return foundProvider;
        }

        public boolean generateDeeper() {
            this.stepStuckCounter = 0;
            this.isReset = false;
            while (!this.nextNodes.isEmpty()) {
                this.lastNodes.clear();
                this.lastNodes.addAll(this.nextNodes);
                this.nextNodes.clear();
                for (Node nextNode : this.lastNodes) {
                    nextNode.step();
                    if (this.isReset) {
                        return false;
                    }
                    this.nextNodes.addAll(nextNode.children);
                }
                if (!this.nextNodes.isEmpty()) continue;
                this.lastNodes.clear();
                this.lastNodes.trimToSize();
                this.nextNodes.trimToSize();
            }
            return true;
        }

        private Node findInMapping(TypeDeclaration input) {
            return (Node)this.mapping.get(input);
        }

        private static final Converter<Object, Object> resolveInput(Converter<?, ?> converter, TypeDeclaration input) {
            Converter<Object, Object> forInput;
            if (converter instanceof InputConverter && (forInput = ((InputConverter)converter).getConverter(input)) != null) {
                return forInput;
            }
            return converter;
        }

        public final void reset() {
            this.nullConverterSearched = false;
            this.mapping.clear();
            this.mapping.put(this.root.converter.input, this.root);
            this.lastNodes.clear();
            this.nextNodes.clear();
            this.nextNodes.add(this.root);
            this.isReset = true;
        }

        public static void resetTypeChange(TypeDeclaration type) {
            for (OutputConverterTree tree : trees.values()) {
                if (!tree.mapping.containsKey(type)) continue;
                tree.reset();
            }
        }

        public static void resetAll() {
            for (OutputConverterTree tree : trees.values()) {
                tree.reset();
            }
        }

        public static OutputConverterTree getIfExists(TypeDeclaration output) {
            return trees.get(output);
        }

        public static OutputConverterTree get(TypeDeclaration output) {
            return trees.computeIfAbsent(output, OutputConverterTree::new);
        }

        private final class Node
        implements NodeConverterProvider {
            private static final int MAX_STEPS = 10000;
            public final Node previous;
            public final Converter<Object, Object> converter;
            public final ArrayList<Node> children = new ArrayList();
            public final int cost;

            public Node(Node previous, Converter<?, ?> converter) {
                this.converter = converter;
                this.previous = previous;
                this.cost = previous == null ? 0 : previous.cost + converter.getCost() + 1;
            }

            @Override
            public Converter<Object, Object> toConverter(TypeDeclaration input, TypeDeclaration output) {
                InputConverter inputConv;
                if (this.converter instanceof InputConverter && !(inputConv = (InputConverter)this.converter).canConvert(input)) {
                    if (output.isInstanceOf(input)) {
                        return new CastingConverter<Object>(input, output);
                    }
                    return null;
                }
                if (this == OutputConverterTree.this.root || this.previous == OutputConverterTree.this.root) {
                    return OutputConverterTree.resolveInput(this.converter, input);
                }
                TypeDeclaration currInput = input;
                ArrayList converters = new ArrayList();
                Node node = this;
                do {
                    Converter nextConv = OutputConverterTree.resolveInput(node.converter, currInput);
                    converters.add(nextConv);
                    currInput = nextConv.output;
                } while ((node = node.previous) != OutputConverterTree.this.root);
                return new ChainConverter<Object, Object>(converters);
            }

            @Override
            public boolean isToConverterLockSafe() {
                Node node = this;
                do {
                    if (!(node.converter instanceof InputConverter)) continue;
                    return false;
                } while ((node = node.previous) != OutputConverterTree.this.root);
                return true;
            }

            public final void step() {
                if (OutputConverterTree.this.stepStuckCounter >= 10010) {
                    return;
                }
                for (Converter<?, ?> next : OutputConverterList.get(this.converter.input).getConverters()) {
                    Node n = new Node(this, next);
                    Node old = (Node)OutputConverterTree.this.mapping.get(next.input);
                    if (old != null && old.cost <= n.cost) continue;
                    OutputConverterTree.this.mapping.put(next.input, n);
                    this.children.add(n);
                    ++OutputConverterTree.this.stepStuckCounter;
                    if (OutputConverterTree.this.stepStuckCounter == 10000) {
                        MountiplexUtil.LOGGER.severe("Cyclical Conversion step() detected!");
                        if (this.previous != null) {
                            MountiplexUtil.LOGGER.severe("Previous: " + this.previous.converter);
                        }
                    }
                    if (OutputConverterTree.this.stepStuckCounter < 10000) continue;
                    MountiplexUtil.LOGGER.severe("Next [" + n.cost + "]: " + n.converter);
                }
            }

            public final void debugPrint(Node highlighted, StringBuilder str, int indent) {
                StringBuilder childStr = new StringBuilder();
                boolean found = false;
                Node n = highlighted;
                while (n != null) {
                    if (n == this) {
                        found = true;
                        break;
                    }
                    n = n.previous;
                }
                for (Node child : this.children) {
                    child.debugPrint(highlighted, childStr, indent + 1);
                }
                if (found) {
                    for (int i = 1; i < indent; ++i) {
                        str.append("  ");
                    }
                    str.append(">>");
                } else {
                    for (int i = 0; i < indent; ++i) {
                        str.append("  ");
                    }
                }
                str.append(this.converter.input.toString());
                if (found) {
                    str.append("<<");
                }
                str.append('\n');
                str.append((CharSequence)childStr);
            }
        }

        @FunctionalInterface
        private static interface NodeConverterProvider {
            public Converter<Object, Object> toConverter(TypeDeclaration var1, TypeDeclaration var2);

            default public boolean isToConverterLockSafe() {
                return true;
            }
        }
    }

    private static final class TypeTuple {
        public final TypeDeclaration t1;
        public final TypeDeclaration t2;
        private final int hashcode;

        public TypeTuple(Converter<?, ?> converter) {
            this(converter.input, converter.output);
        }

        public TypeTuple(TypeDeclaration t1, TypeDeclaration t2) {
            this.t1 = t1.getBoxedType();
            this.t2 = t2.getBoxedType();
            this.hashcode = 961 + 31 * this.t1.hashCode() + this.t2.hashCode();
        }

        public final TypeTuple reverse() {
            return new TypeTuple(this.t2, this.t1);
        }

        public final boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof TypeTuple) {
                TypeTuple tuple = (TypeTuple)other;
                return tuple.t1.equals(this.t1) && tuple.t2.equals(this.t2);
            }
            return false;
        }

        public final int hashCode() {
            return this.hashcode;
        }
    }

    private static final class OutputConverterList {
        private static Map<TypeDeclaration, OutputConverterList> mapping = new HashMap<TypeDeclaration, OutputConverterList>();
        private final TypeDeclaration output;
        private final HashMap<TypeDeclaration, Converter<?, ?>> single = new HashMap();
        private final LinkedHashMap<TypeDeclaration, Converter<?, ?>> converters = new LinkedHashMap();
        private final ArrayList<OutputConverterList> parents = new ArrayList();
        private final HashSet<OutputConverterList> children = new HashSet();
        private boolean regen;

        public OutputConverterList(TypeDeclaration output) {
            this.output = output;
            this.reset();
        }

        public final void addConverter(Converter<?, ?> converter) {
            this.single.put(converter.input, converter);
            this.reset();
        }

        public final void makeParent(OutputConverterList parent) {
            if (this.children.add(parent)) {
                parent.parents.add(this);
                this.reset();
            }
        }

        public final void reset() {
            this.regen = true;
            this.converters.clear();
            for (OutputConverterList child : this.children) {
                child.reset();
            }
        }

        public final Collection<Converter<?, ?>> getConverters() {
            this.genConverters();
            return this.converters.values();
        }

        private final void genConverters() {
            if (this.regen) {
                this.regen = false;
                this.converters.putAll(this.single);
                ArrayList tmp = new ArrayList();
                for (ConverterProvider provider : providers) {
                    provider.getConverters(this.output, tmp);
                    if (tmp.isEmpty()) continue;
                    for (Converter<?, ?> converter : tmp) {
                        if (this.converters.putIfAbsent(converter.input, converter) != null) continue;
                        try {
                            Conversion.verifyConverter(converter);
                        }
                        catch (InvalidConverterException ex) {
                            this.converters.remove(converter.input);
                            MountiplexUtil.LOGGER.warning(ex.getMessage() + ": " + converter);
                        }
                        catch (Throwable t) {
                            this.converters.remove(converter.input);
                            MountiplexUtil.LOGGER.log(Level.SEVERE, "An error occurred registering " + converter, t);
                        }
                    }
                    tmp.clear();
                }
                for (OutputConverterList parent : this.parents) {
                    parent.genConverters();
                    for (Converter<?, ?> converter : parent.converters.values()) {
                        this.converters.putIfAbsent(converter.input, converter);
                    }
                }
            }
        }

        public static OutputConverterList get(TypeDeclaration output) {
            if (!output.isResolved()) {
                throw new IllegalArgumentException("Requested type is not resolved: " + output);
            }
            OutputConverterList list = mapping.get(output);
            if (list == null) {
                list = new OutputConverterList(output);
                mapping.put(output, list);
                Class<?> superType = output.type.getSuperclass();
                if (superType != null) {
                    list.makeParent(OutputConverterList.get(TypeDeclaration.fromClass(superType)));
                }
                for (Class<?> iif : output.type.getInterfaces()) {
                    list.makeParent(OutputConverterList.get(TypeDeclaration.fromClass(iif)));
                }
            }
            return list;
        }

        public static void resetAll() {
            for (OutputConverterList list : mapping.values()) {
                list.reset();
            }
        }
    }
}

