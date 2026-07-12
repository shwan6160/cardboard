/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameter
 *  com.bergerkiller.bukkit.common.dep.typetoken.TypeToken
 */
package com.bergerkiller.bukkit.tc.commands.annotations;

import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserParameter;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.PARAMETER})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface SavedModelImplicitlyCreated {
    public static final ParserParameter<Boolean> PARAM = new ParserParameter("savedmodel.implicitlycreated", TypeToken.get(Boolean.class));
}

