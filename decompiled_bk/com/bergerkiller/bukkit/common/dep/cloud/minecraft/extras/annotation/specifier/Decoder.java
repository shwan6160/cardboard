/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras.annotation.specifier;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;
import org.apiguardian.api.API;

@Documented
@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
@API(status=API.Status.STABLE, since="2.0.0")
public @interface Decoder {
    @API(status=API.Status.STABLE, since="2.0.0")
    public Class<? extends Provider> value();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static interface Provider {
        @API(status=API.Status.STABLE, since="2.0.0")
        public Function<String, ? extends Component> decoder(TypeToken<?> var1);
    }

    @Documented
    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @API(status=API.Status.STABLE, since="2.0.0")
    public static @interface Json {
        @API(status=API.Status.STABLE, since="2.0.0")
        public boolean downsampleColors() default false;
    }

    @Documented
    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @API(status=API.Status.STABLE, since="2.0.0")
    public static @interface Legacy {
        @API(status=API.Status.STABLE, since="2.0.0")
        public char value() default 38;
    }

    @Documented
    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    @API(status=API.Status.STABLE, since="2.0.0")
    public static @interface MiniMessage {
    }
}

