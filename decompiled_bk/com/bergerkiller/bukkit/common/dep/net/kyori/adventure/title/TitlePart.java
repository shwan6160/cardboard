/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.title.Title;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface TitlePart<T> {
    public static final TitlePart<Component> TITLE = new TitlePart<Component>(){

        public String toString() {
            return "TitlePart.TITLE";
        }
    };
    public static final TitlePart<Component> SUBTITLE = new TitlePart<Component>(){

        public String toString() {
            return "TitlePart.SUBTITLE";
        }
    };
    public static final TitlePart<Title.Times> TIMES = new TitlePart<Title.Times>(){

        public String toString() {
            return "TitlePart.TIMES";
        }
    };
}

