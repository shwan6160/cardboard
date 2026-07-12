/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Generated
 */
package com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.AnnotationParser;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.DescriptionMapper;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.ParameterNameExtractor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.extractor.StandardArgumentExtractor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="StandardArgumentExtractor", generator="Immutables")
public final class ImmutableStandardArgumentExtractor
extends StandardArgumentExtractor {
    private final @NonNull AnnotationParser<?> annotationParser;
    private final @NonNull ParameterNameExtractor parameterNameExtractor;
    private final @NonNull DescriptionMapper descriptionMapper;
    private static final byte STAGE_INITIALIZING = -1;
    private static final byte STAGE_UNINITIALIZED = 0;
    private static final byte STAGE_INITIALIZED = 1;
    private volatile transient InitShim initShim = new InitShim();

    private ImmutableStandardArgumentExtractor(@NonNull AnnotationParser<?> annotationParser, @NonNull ParameterNameExtractor parameterNameExtractor, @NonNull DescriptionMapper descriptionMapper) {
        this.annotationParser = Objects.requireNonNull(annotationParser, "annotationParser");
        this.parameterNameExtractor = Objects.requireNonNull(parameterNameExtractor, "parameterNameExtractor");
        this.descriptionMapper = Objects.requireNonNull(descriptionMapper, "descriptionMapper");
        this.initShim = null;
    }

    private ImmutableStandardArgumentExtractor(Builder builder) {
        this.annotationParser = builder.annotationParser;
        if (builder.parameterNameExtractor != null) {
            this.initShim.parameterNameExtractor(builder.parameterNameExtractor);
        }
        if (builder.descriptionMapper != null) {
            this.initShim.descriptionMapper(builder.descriptionMapper);
        }
        this.parameterNameExtractor = this.initShim.parameterNameExtractor();
        this.descriptionMapper = this.initShim.descriptionMapper();
        this.initShim = null;
    }

    private ImmutableStandardArgumentExtractor(ImmutableStandardArgumentExtractor original, @NonNull AnnotationParser<?> annotationParser, @NonNull ParameterNameExtractor parameterNameExtractor, @NonNull DescriptionMapper descriptionMapper) {
        this.annotationParser = annotationParser;
        this.parameterNameExtractor = parameterNameExtractor;
        this.descriptionMapper = descriptionMapper;
        this.initShim = null;
    }

    @Override
    public @NonNull AnnotationParser<?> annotationParser() {
        return this.annotationParser;
    }

    @Override
    public @NonNull ParameterNameExtractor parameterNameExtractor() {
        InitShim shim = this.initShim;
        return shim != null ? shim.parameterNameExtractor() : this.parameterNameExtractor;
    }

    @Override
    public @NonNull DescriptionMapper descriptionMapper() {
        InitShim shim = this.initShim;
        return shim != null ? shim.descriptionMapper() : this.descriptionMapper;
    }

    public final ImmutableStandardArgumentExtractor withAnnotationParser(@NonNull AnnotationParser<?> value) {
        if (this.annotationParser == value) {
            return this;
        }
        @NonNull AnnotationParser<?> newValue = Objects.requireNonNull(value, "annotationParser");
        return new ImmutableStandardArgumentExtractor(this, newValue, this.parameterNameExtractor, this.descriptionMapper);
    }

    public final ImmutableStandardArgumentExtractor withParameterNameExtractor(@NonNull ParameterNameExtractor value) {
        if (this.parameterNameExtractor == value) {
            return this;
        }
        @NonNull ParameterNameExtractor newValue = Objects.requireNonNull(value, "parameterNameExtractor");
        return new ImmutableStandardArgumentExtractor(this, this.annotationParser, newValue, this.descriptionMapper);
    }

    public final ImmutableStandardArgumentExtractor withDescriptionMapper(@NonNull DescriptionMapper value) {
        if (this.descriptionMapper == value) {
            return this;
        }
        @NonNull DescriptionMapper newValue = Objects.requireNonNull(value, "descriptionMapper");
        return new ImmutableStandardArgumentExtractor(this, this.annotationParser, this.parameterNameExtractor, newValue);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableStandardArgumentExtractor && this.equalTo(0, (ImmutableStandardArgumentExtractor)another);
    }

    private boolean equalTo(int synthetic, ImmutableStandardArgumentExtractor another) {
        return this.annotationParser.equals(another.annotationParser) && this.parameterNameExtractor.equals(another.parameterNameExtractor) && this.descriptionMapper.equals(another.descriptionMapper);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.annotationParser.hashCode();
        h += (h << 5) + this.parameterNameExtractor.hashCode();
        h += (h << 5) + this.descriptionMapper.hashCode();
        return h;
    }

    public String toString() {
        return "StandardArgumentExtractor{annotationParser=" + this.annotationParser + ", parameterNameExtractor=" + this.parameterNameExtractor + ", descriptionMapper=" + this.descriptionMapper + "}";
    }

    public static ImmutableStandardArgumentExtractor of(@NonNull AnnotationParser<?> annotationParser, @NonNull ParameterNameExtractor parameterNameExtractor, @NonNull DescriptionMapper descriptionMapper) {
        return new ImmutableStandardArgumentExtractor(annotationParser, parameterNameExtractor, descriptionMapper);
    }

    public static ImmutableStandardArgumentExtractor copyOf(StandardArgumentExtractor instance) {
        if (instance instanceof ImmutableStandardArgumentExtractor) {
            return (ImmutableStandardArgumentExtractor)instance;
        }
        return ImmutableStandardArgumentExtractor.builder().from(instance).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Generated(from="StandardArgumentExtractor", generator="Immutables")
    public static final class Builder {
        private static final long INIT_BIT_ANNOTATION_PARSER = 1L;
        private long initBits = 1L;
        private @NonNull AnnotationParser<?> annotationParser;
        private @NonNull ParameterNameExtractor parameterNameExtractor;
        private @NonNull DescriptionMapper descriptionMapper;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public final Builder from(StandardArgumentExtractor instance) {
            Objects.requireNonNull(instance, "instance");
            this.annotationParser(instance.annotationParser());
            this.parameterNameExtractor(instance.parameterNameExtractor());
            this.descriptionMapper(instance.descriptionMapper());
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder annotationParser(@NonNull AnnotationParser<?> annotationParser) {
            this.annotationParser = Objects.requireNonNull(annotationParser, "annotationParser");
            this.initBits &= 0xFFFFFFFFFFFFFFFEL;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder parameterNameExtractor(@NonNull ParameterNameExtractor parameterNameExtractor) {
            this.parameterNameExtractor = Objects.requireNonNull(parameterNameExtractor, "parameterNameExtractor");
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder descriptionMapper(@NonNull DescriptionMapper descriptionMapper) {
            this.descriptionMapper = Objects.requireNonNull(descriptionMapper, "descriptionMapper");
            return this;
        }

        public ImmutableStandardArgumentExtractor build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            }
            return new ImmutableStandardArgumentExtractor(this);
        }

        private String formatRequiredAttributesMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("annotationParser");
            }
            return "Cannot build StandardArgumentExtractor, some of required attributes are not set " + attributes;
        }
    }

    @Generated(from="StandardArgumentExtractor", generator="Immutables")
    private final class InitShim {
        private byte parameterNameExtractorBuildStage = 0;
        private @NonNull ParameterNameExtractor parameterNameExtractor;
        private byte descriptionMapperBuildStage = 0;
        private @NonNull DescriptionMapper descriptionMapper;

        private InitShim() {
        }

        @NonNull ParameterNameExtractor parameterNameExtractor() {
            if (this.parameterNameExtractorBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.parameterNameExtractorBuildStage == 0) {
                this.parameterNameExtractorBuildStage = (byte)-1;
                this.parameterNameExtractor = Objects.requireNonNull(ImmutableStandardArgumentExtractor.super.parameterNameExtractor(), "parameterNameExtractor");
                this.parameterNameExtractorBuildStage = 1;
            }
            return this.parameterNameExtractor;
        }

        void parameterNameExtractor(@NonNull ParameterNameExtractor parameterNameExtractor) {
            this.parameterNameExtractor = parameterNameExtractor;
            this.parameterNameExtractorBuildStage = 1;
        }

        @NonNull DescriptionMapper descriptionMapper() {
            if (this.descriptionMapperBuildStage == -1) {
                throw new IllegalStateException(this.formatInitCycleMessage());
            }
            if (this.descriptionMapperBuildStage == 0) {
                this.descriptionMapperBuildStage = (byte)-1;
                this.descriptionMapper = Objects.requireNonNull(ImmutableStandardArgumentExtractor.super.descriptionMapper(), "descriptionMapper");
                this.descriptionMapperBuildStage = 1;
            }
            return this.descriptionMapper;
        }

        void descriptionMapper(@NonNull DescriptionMapper descriptionMapper) {
            this.descriptionMapper = descriptionMapper;
            this.descriptionMapperBuildStage = 1;
        }

        private String formatInitCycleMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if (this.parameterNameExtractorBuildStage == -1) {
                attributes.add("parameterNameExtractor");
            }
            if (this.descriptionMapperBuildStage == -1) {
                attributes.add("descriptionMapper");
            }
            return "Cannot build StandardArgumentExtractor, attribute initializers form cycle " + attributes;
        }
    }
}

