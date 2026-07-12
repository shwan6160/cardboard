/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.common.returnsreceiver.qual.This
 */
package com.bergerkiller.bukkit.common.dep.cloud.parser;

import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.FlagYielding;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Greedy;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Liberal;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Quoted;
import com.bergerkiller.bukkit.common.dep.cloud.annotation.specifier.Range;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserContributor;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserRegistry;
import com.bergerkiller.bukkit.common.dep.cloud.parser.StandardParameters;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.BooleanParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.ByteParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.CharacterParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.DoubleParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.DurationParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.EnumParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.FloatParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.IntegerParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.LongParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.ShortParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.StringArrayParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.StringParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.UUIDParser;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.bergerkiller.bukkit.common.dep.typetoken.AnnotatedTypeMap;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status=API.Status.STABLE)
public final class StandardParserRegistry<C>
implements ParserRegistry<C> {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAPPINGS = new HashMap<Class<?>, Class<?>>(){
        {
            this.put(Character.TYPE, Character.class);
            this.put(Integer.TYPE, Integer.class);
            this.put(Short.TYPE, Short.class);
            this.put(Byte.TYPE, Byte.class);
            this.put(Float.TYPE, Float.class);
            this.put(Double.TYPE, Double.class);
            this.put(Long.TYPE, Long.class);
            this.put(Boolean.TYPE, Boolean.class);
        }
    };
    private final Map<String, Function<ParserParameters, ArgumentParser<C, ?>>> namedParsers = new HashMap();
    private final Map<AnnotatedType, Function<ParserParameters, ArgumentParser<C, ?>>> parserSuppliers = new AnnotatedTypeMap();
    private final Map<Class<? extends Annotation>, ParserRegistry.AnnotationMapper<?>> annotationMappers = new HashMap();
    private final Map<String, SuggestionProvider<C>> namedSuggestionProviders = new HashMap<String, SuggestionProvider<C>>();

    public StandardParserRegistry() {
        this.registerAnnotationMapper(Range.class, (ParserRegistry.AnnotationMapper)new RangeMapper());
        this.registerAnnotationMapper(Greedy.class, (ParserRegistry.AnnotationMapper)new GreedyMapper());
        this.registerAnnotationMapper(Quoted.class, (quoted, typeToken) -> ParserParameters.single(StandardParameters.QUOTED, true));
        this.registerAnnotationMapper(Liberal.class, (liberal, typeToken) -> ParserParameters.single(StandardParameters.LIBERAL, true));
        this.registerAnnotationMapper(FlagYielding.class, (flagYielding, typeToken) -> ParserParameters.single(StandardParameters.FLAG_YIELDING, true));
        this.registerParserSupplier((TypeToken)TypeToken.get(Byte.class), (T options) -> new ByteParser((Byte)((Number)options.get(StandardParameters.RANGE_MIN, (byte)-128)), (Byte)((Number)options.get(StandardParameters.RANGE_MAX, (byte)127))));
        this.registerParserSupplier((TypeToken)TypeToken.get(Short.class), (T options) -> new ShortParser((Short)((Number)options.get(StandardParameters.RANGE_MIN, (short)Short.MIN_VALUE)), (Short)((Number)options.get(StandardParameters.RANGE_MAX, (short)Short.MAX_VALUE))));
        this.registerParserSupplier((TypeToken)TypeToken.get(Integer.class), (T options) -> new IntegerParser((Integer)((Number)options.get(StandardParameters.RANGE_MIN, Integer.MIN_VALUE)), (Integer)((Number)options.get(StandardParameters.RANGE_MAX, Integer.MAX_VALUE))));
        this.registerParserSupplier((TypeToken)TypeToken.get(Long.class), (T options) -> new LongParser((Long)((Number)options.get(StandardParameters.RANGE_MIN, Long.MIN_VALUE)), (Long)((Number)options.get(StandardParameters.RANGE_MAX, Long.MAX_VALUE))));
        this.registerParserSupplier((TypeToken)TypeToken.get(Float.class), (T options) -> new FloatParser(((Float)((Number)options.get(StandardParameters.RANGE_MIN, Float.valueOf(Float.NEGATIVE_INFINITY)))).floatValue(), ((Float)((Number)options.get(StandardParameters.RANGE_MAX, Float.valueOf(Float.POSITIVE_INFINITY)))).floatValue()));
        this.registerParserSupplier((TypeToken)TypeToken.get(Double.class), (T options) -> new DoubleParser((Double)((Number)options.get(StandardParameters.RANGE_MIN, Double.NEGATIVE_INFINITY)), (Double)((Number)options.get(StandardParameters.RANGE_MAX, Double.POSITIVE_INFINITY))));
        this.registerParserSupplier((TypeToken)TypeToken.get(Character.class), (T options) -> new CharacterParser());
        this.registerParserSupplier((TypeToken)TypeToken.get(String[].class), (T options) -> new StringArrayParser(options.get(StandardParameters.FLAG_YIELDING, false)));
        this.registerParserSupplier((TypeToken)TypeToken.get(String.class), (T options) -> {
            boolean greedy = options.get(StandardParameters.GREEDY, false);
            boolean greedyFlagAware = options.get(StandardParameters.FLAG_YIELDING, false);
            boolean quoted = options.get(StandardParameters.QUOTED, false);
            if (greedyFlagAware && quoted) {
                throw new IllegalArgumentException("Don't know whether to create GREEDY_FLAG_YIELDING or QUOTED StringArgument.StringParser, both specified.");
            }
            if (greedy && quoted) {
                throw new IllegalArgumentException("Don't know whether to create GREEDY or QUOTED StringArgument.StringParser, both specified.");
            }
            StringParser.StringMode stringMode = greedyFlagAware ? StringParser.StringMode.GREEDY_FLAG_YIELDING : (greedy ? StringParser.StringMode.GREEDY : (quoted ? StringParser.StringMode.QUOTED : StringParser.StringMode.SINGLE));
            return new StringParser(stringMode);
        });
        this.registerParserSupplier((TypeToken)TypeToken.get(Boolean.class), (T options) -> {
            boolean liberal = options.get(StandardParameters.LIBERAL, false);
            return new BooleanParser(liberal);
        });
        this.registerParser(UUIDParser.uuidParser());
        this.registerParser(DurationParser.durationParser());
        ServiceLoader<ParserContributor> loader = ServiceLoader.load(ParserContributor.class, ParserContributor.class.getClassLoader());
        loader.iterator().forEachRemaining(contributor -> contributor.contribute(this));
    }

    private static boolean isPrimitive(@NonNull TypeToken<?> type) {
        return GenericTypeReflector.erase(type.getType()).isPrimitive();
    }

    @Override
    public <T> @This StandardParserRegistry<C> registerParserSupplier(@NonNull TypeToken<T> type, @NonNull Function<@NonNull ParserParameters, @NonNull ArgumentParser<C, ?>> supplier) {
        this.parserSuppliers.put(type.getAnnotatedType(), supplier);
        return this;
    }

    @Override
    public @This StandardParserRegistry<C> registerNamedParserSupplier(@NonNull String name, @NonNull Function<@NonNull ParserParameters, @NonNull ArgumentParser<C, ?>> supplier) {
        this.namedParsers.put(name, supplier);
        return this;
    }

    @Override
    public <A extends Annotation> @This StandardParserRegistry<C> registerAnnotationMapper(@NonNull Class<A> annotation, @NonNull ParserRegistry.AnnotationMapper<A> mapper) {
        this.annotationMappers.put(annotation, mapper);
        return this;
    }

    @Override
    public @NonNull ParserParameters parseAnnotations(@NonNull TypeToken<?> parsingType, @NonNull Collection<? extends @NonNull Annotation> annotations) {
        ParserParameters parserParameters = new ParserParameters();
        annotations.forEach(annotation -> {
            ParserRegistry.AnnotationMapper<?> mapper = this.annotationMappers.get(annotation.annotationType());
            if (mapper == null) {
                return;
            }
            ParserParameters parserParametersCasted = mapper.mapAnnotation(annotation, parsingType);
            parserParameters.merge(parserParametersCasted);
        });
        return parserParameters;
    }

    @Override
    public <T> @NonNull Optional<ArgumentParser<C, T>> createParser(@NonNull TypeToken<T> type, @NonNull ParserParameters parserParameters) {
        TypeToken<Object> actualType = GenericTypeReflector.erase(type.getType()).isPrimitive() ? TypeToken.get(PRIMITIVE_MAPPINGS.get(GenericTypeReflector.erase(type.getType()))) : type;
        Function<ParserParameters, ArgumentParser<C, ?>> producer = this.parserSuppliers.get(actualType.getAnnotatedType());
        if (producer == null) {
            if (GenericTypeReflector.isSuperType(Enum.class, actualType.getType())) {
                EnumParser enumArgument = new EnumParser(GenericTypeReflector.erase(actualType.getType()));
                return Optional.of(enumArgument);
            }
            return Optional.empty();
        }
        ArgumentParser<C, ?> parser = producer.apply(parserParameters);
        return Optional.of(parser);
    }

    @Override
    public <T> @NonNull Optional<ArgumentParser<C, T>> createParser(@NonNull String name, @NonNull ParserParameters parserParameters) {
        Function<ParserParameters, ArgumentParser<C, ?>> producer = this.namedParsers.get(name);
        if (producer == null) {
            return Optional.empty();
        }
        ArgumentParser<C, ?> parser = producer.apply(parserParameters);
        return Optional.of(parser);
    }

    @Override
    public void registerSuggestionProvider(@NonNull String name, @NonNull SuggestionProvider<C> suggestionProvider) {
        this.namedSuggestionProviders.put(name.toLowerCase(Locale.ENGLISH), suggestionProvider);
    }

    @Override
    public @NonNull Optional<SuggestionProvider<C>> getSuggestionProvider(@NonNull String name) {
        SuggestionProvider<C> suggestionProvider = this.namedSuggestionProviders.get(name.toLowerCase(Locale.ENGLISH));
        return Optional.ofNullable(suggestionProvider);
    }

    private static final class RangeMapper
    implements ParserRegistry.AnnotationMapper<Range> {
        private RangeMapper() {
        }

        @Override
        public @NonNull ParserParameters mapAnnotation(@NonNull Range range, @NonNull TypeToken<?> type) {
            Class clazz = StandardParserRegistry.isPrimitive(type) ? (Class)PRIMITIVE_MAPPINGS.get(GenericTypeReflector.erase(type.getType())) : GenericTypeReflector.erase(type.getType());
            if (!Number.class.isAssignableFrom(clazz)) {
                return ParserParameters.empty();
            }
            Number min = null;
            Number max = null;
            if (clazz.equals(Byte.class)) {
                if (!range.min().isEmpty()) {
                    min = Byte.parseByte(range.min());
                }
                if (!range.max().isEmpty()) {
                    max = Byte.parseByte(range.max());
                }
            } else if (clazz.equals(Short.class)) {
                if (!range.min().isEmpty()) {
                    min = Short.parseShort(range.min());
                }
                if (!range.max().isEmpty()) {
                    max = Short.parseShort(range.max());
                }
            } else if (clazz.equals(Integer.class)) {
                if (!range.min().isEmpty()) {
                    min = Integer.parseInt(range.min());
                }
                if (!range.max().isEmpty()) {
                    max = Integer.parseInt(range.max());
                }
            } else if (clazz.equals(Long.class)) {
                if (!range.min().isEmpty()) {
                    min = Long.parseLong(range.min());
                }
                if (!range.max().isEmpty()) {
                    max = Long.parseLong(range.max());
                }
            } else if (clazz.equals(Float.class)) {
                if (!range.min().isEmpty()) {
                    min = Float.valueOf(Float.parseFloat(range.min()));
                }
                if (!range.max().isEmpty()) {
                    max = Float.valueOf(Float.parseFloat(range.max()));
                }
            } else if (clazz.equals(Double.class)) {
                if (!range.min().isEmpty()) {
                    min = Double.parseDouble(range.min());
                }
                if (!range.max().isEmpty()) {
                    max = Double.parseDouble(range.max());
                }
            }
            ParserParameters parserParameters = new ParserParameters();
            if (min != null) {
                parserParameters.store(StandardParameters.RANGE_MIN, min);
            }
            if (max != null) {
                parserParameters.store(StandardParameters.RANGE_MAX, max);
            }
            return parserParameters;
        }
    }

    private static final class GreedyMapper
    implements ParserRegistry.AnnotationMapper<Greedy> {
        private GreedyMapper() {
        }

        @Override
        public @NonNull ParserParameters mapAnnotation(@NonNull Greedy greedy, @NonNull TypeToken<?> typeToken) {
            return ParserParameters.single(StandardParameters.GREEDY, true);
        }
    }
}

