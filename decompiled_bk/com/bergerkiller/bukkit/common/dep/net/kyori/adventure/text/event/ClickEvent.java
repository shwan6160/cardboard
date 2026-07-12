/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.audience.Audience;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.builder.AbstractBuilder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.dialog.DialogLike;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.internal.Internals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Key;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Keyed;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.nbt.api.BinaryTagHolder;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickCallback;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickCallbackInternals;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.ClickCallbackOptionsImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.event.PayloadImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.StyleBuilderApplicable;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Index;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClickEvent
implements Examinable,
StyleBuilderApplicable {
    private final Action action;
    private final Payload payload;

    @NotNull
    public static ClickEvent openUrl(@NotNull String url) {
        return new ClickEvent(Action.OPEN_URL, Payload.string(url));
    }

    @NotNull
    public static ClickEvent openUrl(@NotNull URL url) {
        return ClickEvent.openUrl(url.toExternalForm());
    }

    @NotNull
    public static ClickEvent openFile(@NotNull String file) {
        return new ClickEvent(Action.OPEN_FILE, Payload.string(file));
    }

    @NotNull
    public static ClickEvent runCommand(@NotNull String command) {
        return new ClickEvent(Action.RUN_COMMAND, Payload.string(command));
    }

    @NotNull
    public static ClickEvent suggestCommand(@NotNull String command) {
        return new ClickEvent(Action.SUGGEST_COMMAND, Payload.string(command));
    }

    @Deprecated
    @NotNull
    public static ClickEvent changePage(@NotNull String page) {
        Objects.requireNonNull(page, "page");
        return new ClickEvent(Action.CHANGE_PAGE, Payload.integer(Integer.parseInt(page)));
    }

    @NotNull
    public static ClickEvent changePage(int page) {
        return new ClickEvent(Action.CHANGE_PAGE, Payload.integer(page));
    }

    @NotNull
    public static ClickEvent copyToClipboard(@NotNull String text) {
        return new ClickEvent(Action.COPY_TO_CLIPBOARD, Payload.string(text));
    }

    @NotNull
    public static ClickEvent callback(@NotNull ClickCallback<Audience> function) {
        return ClickCallbackInternals.PROVIDER.create(Objects.requireNonNull(function, "function"), ClickCallbackOptionsImpl.DEFAULT);
    }

    @NotNull
    public static ClickEvent callback(@NotNull ClickCallback<Audience> function, @NotNull ClickCallback.Options options) {
        return ClickCallbackInternals.PROVIDER.create(Objects.requireNonNull(function, "function"), Objects.requireNonNull(options, "options"));
    }

    @NotNull
    public static ClickEvent callback(@NotNull ClickCallback<Audience> function, @NotNull @NotNull Consumer<@NotNull ClickCallback.Options.Builder> optionsBuilder) {
        return ClickCallbackInternals.PROVIDER.create(Objects.requireNonNull(function, "function"), (ClickCallback.Options)AbstractBuilder.configureAndBuild(ClickCallback.Options.builder(), Objects.requireNonNull(optionsBuilder, "optionsBuilder")));
    }

    @NotNull
    public static ClickEvent showDialog(@NotNull DialogLike dialog) {
        Objects.requireNonNull(dialog, "dialog");
        return new ClickEvent(Action.SHOW_DIALOG, Payload.dialog(dialog));
    }

    @Deprecated
    @NotNull
    public static ClickEvent custom(@NotNull Key key, @NotNull String data) {
        return ClickEvent.custom(key, BinaryTagHolder.binaryTagHolder(data));
    }

    @NotNull
    public static ClickEvent custom(@NotNull Key key, @NotNull BinaryTagHolder nbt) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(nbt, "nbt");
        return new ClickEvent(Action.CUSTOM, Payload.custom(key, nbt));
    }

    @Deprecated
    @NotNull
    public static ClickEvent clickEvent(@NotNull Action action, @NotNull String value) {
        if (action == Action.CHANGE_PAGE) {
            return ClickEvent.changePage(value);
        }
        if (!action.payloadType().equals(Payload.Text.class)) {
            throw new IllegalArgumentException("Action " + (Object)((Object)action) + " does not support string payloads");
        }
        return new ClickEvent(action, Payload.string(value));
    }

    @NotNull
    public static ClickEvent clickEvent(@NotNull Action action, @NotNull Payload payload) {
        return new ClickEvent(action, payload);
    }

    private ClickEvent(@NotNull Action action, @NotNull Payload payload) {
        if (!action.supports(payload)) {
            throw new IllegalArgumentException("Action " + (Object)((Object)action) + " does not support payload " + payload);
        }
        this.action = Objects.requireNonNull(action, "action");
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    @NotNull
    public Action action() {
        return this.action;
    }

    @Deprecated
    @NotNull
    public String value() {
        if (this.payload instanceof Payload.Text) {
            return ((Payload.Text)this.payload).value();
        }
        if (this.action == Action.CHANGE_PAGE) {
            return String.valueOf(((Payload.Int)this.payload).integer());
        }
        throw new IllegalStateException("Payload is not a string payload, is " + this.payload);
    }

    @NotNull
    public Payload payload() {
        return this.payload;
    }

    @Override
    public void styleApply( @NotNull Style.Builder style) {
        style.clickEvent(this);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        ClickEvent that = (ClickEvent)other;
        return this.action == that.action && Objects.equals(this.payload, that.payload);
    }

    public int hashCode() {
        int result = this.action.hashCode();
        result = 31 * result + this.payload.hashCode();
        return result;
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("action", (Object)this.action), ExaminableProperty.of("payload", this.payload));
    }

    public String toString() {
        return Internals.toString(this);
    }

    public static enum Action {
        OPEN_URL("open_url", true, Payload.Text.class),
        OPEN_FILE("open_file", false, Payload.Text.class),
        RUN_COMMAND("run_command", true, Payload.Text.class),
        SUGGEST_COMMAND("suggest_command", true, Payload.Text.class),
        CHANGE_PAGE("change_page", true, Payload.Int.class),
        COPY_TO_CLIPBOARD("copy_to_clipboard", true, Payload.Text.class),
        SHOW_DIALOG("show_dialog", false, Payload.Dialog.class),
        CUSTOM("custom", true, Payload.Custom.class);

        public static final Index<String, Action> NAMES;
        private final String name;
        private final boolean readable;
        private final Class<? extends Payload> payloadType;

        private Action(@NotNull String name, boolean readable, Class<? extends Payload> payloadType) {
            this.name = name;
            this.readable = readable;
            this.payloadType = payloadType;
        }

        public boolean readable() {
            return this.readable;
        }

        public boolean supports(@NotNull Payload payload) {
            Objects.requireNonNull(payload, "payload");
            return this.payloadType.isAssignableFrom(payload.getClass());
        }

        @NotNull
        public Class<? extends Payload> payloadType() {
            return this.payloadType;
        }

        @NotNull
        public String toString() {
            return this.name;
        }

        static {
            NAMES = Index.create(Action.class, constant -> constant.name);
        }
    }

    public static interface Payload
    extends Examinable {
        public static @NotNull Text string(@NotNull String value) {
            Objects.requireNonNull(value, "value");
            return new PayloadImpl.TextImpl(value);
        }

        public static @NotNull Int integer(int integer) {
            return new PayloadImpl.IntImpl(integer);
        }

        public static @NotNull Dialog dialog(@NotNull DialogLike dialog) {
            Objects.requireNonNull(dialog, "dialog");
            return new PayloadImpl.DialogImpl(dialog);
        }

        @Deprecated
        public static @NotNull Custom custom(@NotNull Key key, @NotNull String data) {
            return Payload.custom(key, BinaryTagHolder.binaryTagHolder(data));
        }

        public static @NotNull Custom custom(@NotNull Key key, @NotNull BinaryTagHolder nbt) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(nbt, "nbt");
            return new PayloadImpl.CustomImpl(key, nbt);
        }

        public static interface Custom
        extends Payload,
        Keyed {
            @Deprecated
            @NotNull
            public String data();

            @NotNull
            public BinaryTagHolder nbt();
        }

        public static interface Dialog
        extends Payload {
            @NotNull
            public DialogLike dialog();
        }

        public static interface Int
        extends Payload {
            public int integer();
        }

        public static interface Text
        extends Payload {
            @NotNull
            public String value();
        }
    }
}

