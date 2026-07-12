/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentSelector;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public interface AttachmentSelection<T>
extends Iterable<T> {
    public static final AttachmentSelection<Attachment> NONE = AttachmentSelection.none(Attachment.class);

    public static <T> AttachmentSelection<T> none(Class<T> typeFilter) {
        final AttachmentSelector<T> selector = AttachmentSelector.none(typeFilter);
        return new AttachmentSelection<T>(){

            @Override
            public AttachmentSelector<T> selector() {
                return selector;
            }

            @Override
            public List<String> names() {
                return Collections.emptyList();
            }

            @Override
            public List<T> values() {
                return Collections.emptyList();
            }

            @Override
            public boolean sync() {
                return false;
            }
        };
    }

    public AttachmentSelector<T> selector();

    public List<String> names();

    public List<T> values();

    public boolean sync();

    @Override
    default public Iterator<T> iterator() {
        return this.values().iterator();
    }

    @Override
    default public void forEach(Consumer<? super T> action) {
        this.values().forEach(action);
    }
}

