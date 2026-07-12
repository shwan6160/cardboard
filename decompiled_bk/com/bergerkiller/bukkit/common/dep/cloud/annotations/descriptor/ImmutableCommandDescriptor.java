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
package com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor;

import com.bergerkiller.bukkit.common.dep.cloud.annotations.SyntaxFragment;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.CommandDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.annotations.descriptor.Descriptor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Generated;

@API(status=API.Status.STABLE, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
@Generated(from="CommandDescriptor", generator="Immutables")
public final class ImmutableCommandDescriptor
implements CommandDescriptor {
    private final @NonNull Method method;
    private final @NonNull String name;
    private final @NonNull List<SyntaxFragment> syntax;
    private final @NonNull String commandToken;
    private final @NonNull Class<?> requiredSender;

    private ImmutableCommandDescriptor(@NonNull Method method, @NonNull String name, Iterable<? extends SyntaxFragment> syntax, @NonNull String commandToken, @NonNull Class<?> requiredSender) {
        this.method = Objects.requireNonNull(method, "method");
        this.name = Objects.requireNonNull(name, "name");
        this.syntax = ImmutableCommandDescriptor.createUnmodifiableList(false, ImmutableCommandDescriptor.createSafeList(syntax, true, false));
        this.commandToken = Objects.requireNonNull(commandToken, "commandToken");
        this.requiredSender = Objects.requireNonNull(requiredSender, "requiredSender");
    }

    private ImmutableCommandDescriptor(Builder builder) {
        this.method = builder.method;
        this.syntax = builder.syntax == null ? Collections.emptyList() : ImmutableCommandDescriptor.createUnmodifiableList(true, builder.syntax);
        this.commandToken = builder.commandToken;
        this.requiredSender = builder.requiredSender;
        this.name = builder.name != null ? builder.name : Objects.requireNonNull(CommandDescriptor.super.name(), "name");
    }

    private ImmutableCommandDescriptor(ImmutableCommandDescriptor original, @NonNull Method method, @NonNull String name, @NonNull List<SyntaxFragment> syntax, @NonNull String commandToken, @NonNull Class<?> requiredSender) {
        this.method = method;
        this.name = name;
        this.syntax = syntax;
        this.commandToken = commandToken;
        this.requiredSender = requiredSender;
    }

    @Override
    public @NonNull Method method() {
        return this.method;
    }

    @Override
    public @NonNull String name() {
        return this.name;
    }

    @Override
    public @NonNull List<SyntaxFragment> syntax() {
        return this.syntax;
    }

    @Override
    public @NonNull String commandToken() {
        return this.commandToken;
    }

    @Override
    public @NonNull Class<?> requiredSender() {
        return this.requiredSender;
    }

    public final ImmutableCommandDescriptor withMethod(@NonNull Method value) {
        if (this.method == value) {
            return this;
        }
        @NonNull Method newValue = Objects.requireNonNull(value, "method");
        return new ImmutableCommandDescriptor(this, newValue, this.name, this.syntax, this.commandToken, this.requiredSender);
    }

    public final ImmutableCommandDescriptor withName(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "name");
        if (this.name.equals(newValue)) {
            return this;
        }
        return new ImmutableCommandDescriptor(this, this.method, newValue, this.syntax, this.commandToken, this.requiredSender);
    }

    public final ImmutableCommandDescriptor withSyntax(SyntaxFragment ... elements) {
        @NonNull List<SyntaxFragment> newValue = ImmutableCommandDescriptor.createUnmodifiableList(false, ImmutableCommandDescriptor.createSafeList(Arrays.asList(elements), true, false));
        return new ImmutableCommandDescriptor(this, this.method, this.name, newValue, this.commandToken, this.requiredSender);
    }

    public final ImmutableCommandDescriptor withSyntax(Iterable<? extends SyntaxFragment> elements) {
        if (this.syntax == elements) {
            return this;
        }
        @NonNull List<SyntaxFragment> newValue = ImmutableCommandDescriptor.createUnmodifiableList(false, ImmutableCommandDescriptor.createSafeList(elements, true, false));
        return new ImmutableCommandDescriptor(this, this.method, this.name, newValue, this.commandToken, this.requiredSender);
    }

    public final ImmutableCommandDescriptor withCommandToken(@NonNull String value) {
        @NonNull String newValue = Objects.requireNonNull(value, "commandToken");
        if (this.commandToken.equals(newValue)) {
            return this;
        }
        return new ImmutableCommandDescriptor(this, this.method, this.name, this.syntax, newValue, this.requiredSender);
    }

    public final ImmutableCommandDescriptor withRequiredSender(@NonNull Class<?> value) {
        if (this.requiredSender == value) {
            return this;
        }
        @NonNull Class<?> newValue = Objects.requireNonNull(value, "requiredSender");
        return new ImmutableCommandDescriptor(this, this.method, this.name, this.syntax, this.commandToken, newValue);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableCommandDescriptor && this.equalTo(0, (ImmutableCommandDescriptor)another);
    }

    private boolean equalTo(int synthetic, ImmutableCommandDescriptor another) {
        return this.method.equals(another.method) && this.name.equals(another.name) && this.syntax.equals(another.syntax) && this.commandToken.equals(another.commandToken) && this.requiredSender.equals(another.requiredSender);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + this.method.hashCode();
        h += (h << 5) + this.name.hashCode();
        h += (h << 5) + this.syntax.hashCode();
        h += (h << 5) + this.commandToken.hashCode();
        h += (h << 5) + this.requiredSender.hashCode();
        return h;
    }

    public String toString() {
        return "CommandDescriptor{method=" + this.method + ", name=" + this.name + ", syntax=" + this.syntax + ", commandToken=" + this.commandToken + ", requiredSender=" + this.requiredSender + "}";
    }

    public static ImmutableCommandDescriptor of(@NonNull Method method, @NonNull String name, @NonNull List<SyntaxFragment> syntax, @NonNull String commandToken, @NonNull Class<?> requiredSender) {
        return ImmutableCommandDescriptor.of(method, name, syntax, commandToken, requiredSender);
    }

    public static ImmutableCommandDescriptor of(@NonNull Method method, @NonNull String name, Iterable<? extends SyntaxFragment> syntax, @NonNull String commandToken, @NonNull Class<?> requiredSender) {
        return new ImmutableCommandDescriptor(method, name, syntax, commandToken, requiredSender);
    }

    public static ImmutableCommandDescriptor copyOf(CommandDescriptor instance) {
        if (instance instanceof ImmutableCommandDescriptor) {
            return (ImmutableCommandDescriptor)instance;
        }
        return ImmutableCommandDescriptor.builder().from(instance).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
        ArrayList<T> list;
        if (iterable instanceof Collection) {
            int size = ((Collection)iterable).size();
            if (size == 0) {
                return Collections.emptyList();
            }
            list = new ArrayList(size);
        } else {
            list = new ArrayList<T>();
        }
        for (T element : iterable) {
            if (skipNulls && element == null) continue;
            if (checkNulls) {
                Objects.requireNonNull(element, "element");
            }
            list.add(element);
        }
        return list;
    }

    private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
        switch (list.size()) {
            case 0: {
                return Collections.emptyList();
            }
            case 1: {
                return Collections.singletonList(list.get(0));
            }
        }
        if (clone) {
            return Collections.unmodifiableList(new ArrayList<T>(list));
        }
        if (list instanceof ArrayList) {
            ((ArrayList)list).trimToSize();
        }
        return Collections.unmodifiableList(list);
    }

    @Generated(from="CommandDescriptor", generator="Immutables")
    public static final class Builder {
        private static final long INIT_BIT_METHOD = 1L;
        private static final long INIT_BIT_COMMAND_TOKEN = 2L;
        private static final long INIT_BIT_REQUIRED_SENDER = 4L;
        private long initBits = 7L;
        private @NonNull Method method;
        private @NonNull String name;
        private List<SyntaxFragment> syntax = null;
        private @NonNull String commandToken;
        private @NonNull Class<?> requiredSender;

        private Builder() {
        }

        @CanIgnoreReturnValue
        public final Builder from(CommandDescriptor instance) {
            Objects.requireNonNull(instance, "instance");
            this.from((short)0, instance);
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder from(Descriptor instance) {
            Objects.requireNonNull(instance, "instance");
            this.from((short)0, instance);
            return this;
        }

        private void from(short _unused, Object object) {
            Descriptor instance;
            long bits = 0L;
            if (object instanceof CommandDescriptor) {
                instance = (CommandDescriptor)object;
                if ((bits & 1L) == 0L) {
                    this.name(instance.name());
                    bits |= 1L;
                }
                this.addAllSyntax(instance.syntax());
                this.commandToken(instance.commandToken());
                this.method(instance.method());
                this.requiredSender(instance.requiredSender());
            }
            if (object instanceof Descriptor) {
                instance = (Descriptor)object;
                if ((bits & 1L) == 0L) {
                    this.name(instance.name());
                    bits |= 1L;
                }
            }
        }

        @CanIgnoreReturnValue
        public final Builder method(@NonNull Method method) {
            this.method = Objects.requireNonNull(method, "method");
            this.initBits &= 0xFFFFFFFFFFFFFFFEL;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder name(@NonNull String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder addSyntax(SyntaxFragment element) {
            if (this.syntax == null) {
                this.syntax = new ArrayList<SyntaxFragment>();
            }
            this.syntax.add(Objects.requireNonNull(element, "syntax element"));
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder addSyntax(SyntaxFragment ... elements) {
            if (this.syntax == null) {
                this.syntax = new ArrayList<SyntaxFragment>();
            }
            for (SyntaxFragment element : elements) {
                this.syntax.add(Objects.requireNonNull(element, "syntax element"));
            }
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder syntax(Iterable<? extends SyntaxFragment> elements) {
            this.syntax = new ArrayList<SyntaxFragment>();
            return this.addAllSyntax(elements);
        }

        @CanIgnoreReturnValue
        public final Builder addAllSyntax(Iterable<? extends SyntaxFragment> elements) {
            Objects.requireNonNull(elements, "syntax element");
            if (this.syntax == null) {
                this.syntax = new ArrayList<SyntaxFragment>();
            }
            for (SyntaxFragment syntaxFragment : elements) {
                this.syntax.add(Objects.requireNonNull(syntaxFragment, "syntax element"));
            }
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder commandToken(@NonNull String commandToken) {
            this.commandToken = Objects.requireNonNull(commandToken, "commandToken");
            this.initBits &= 0xFFFFFFFFFFFFFFFDL;
            return this;
        }

        @CanIgnoreReturnValue
        public final Builder requiredSender(@NonNull Class<?> requiredSender) {
            this.requiredSender = Objects.requireNonNull(requiredSender, "requiredSender");
            this.initBits &= 0xFFFFFFFFFFFFFFFBL;
            return this;
        }

        public ImmutableCommandDescriptor build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            }
            return new ImmutableCommandDescriptor(this);
        }

        private String formatRequiredAttributesMessage() {
            ArrayList<String> attributes = new ArrayList<String>();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("method");
            }
            if ((this.initBits & 2L) != 0L) {
                attributes.add("commandToken");
            }
            if ((this.initBits & 4L) != 0L) {
                attributes.add("requiredSender");
            }
            return "Cannot build CommandDescriptor, some of required attributes are not set " + attributes;
        }
    }
}

