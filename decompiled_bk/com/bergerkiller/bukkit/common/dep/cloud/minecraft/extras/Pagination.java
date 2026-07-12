/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.minecraft.extras;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import org.checkerframework.checker.nullness.qual.NonNull;

final class Pagination<T> {
    private final BiFunction<Integer, Integer, List<Component>> headerRenderer;
    private final BiFunction<T, Boolean, Component> rowRenderer;
    private final BiFunction<Integer, Integer, Component> footerRenderer;
    private final BiFunction<Integer, Integer, Component> outOfRangeRenderer;

    Pagination(@NonNull BiFunction<Integer, Integer, List<Component>> headerRenderer, @NonNull BiFunction<T, Boolean, Component> rowRenderer, @NonNull BiFunction<Integer, Integer, Component> footerRenderer, @NonNull BiFunction<Integer, Integer, Component> outOfRangeRenderer) {
        this.headerRenderer = headerRenderer;
        this.rowRenderer = rowRenderer;
        this.footerRenderer = footerRenderer;
        this.outOfRangeRenderer = outOfRangeRenderer;
    }

    @NonNull List<Component> render(@NonNull List<T> content, int page, int itemsPerPage) {
        int pages = (int)Math.ceil((double)content.size() / ((double)itemsPerPage * 1.0));
        if (page < 1 || page > pages) {
            return Collections.singletonList(this.outOfRangeRenderer.apply(page, pages));
        }
        ArrayList<Component> renderedContent = new ArrayList<Component>((Collection)this.headerRenderer.apply(page, pages));
        int start = itemsPerPage * (page - 1);
        int maxIndex = start + itemsPerPage;
        for (int index = start; index < maxIndex && index <= content.size() - 1; ++index) {
            renderedContent.add(this.rowRenderer.apply(content.get(index), index == maxIndex - 1));
        }
        renderedContent.add(this.footerRenderer.apply(page, pages));
        return Collections.unmodifiableList(renderedContent);
    }
}

