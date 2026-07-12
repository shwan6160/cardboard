/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class BundleUnwrapperIterator_1_19_4 {
    protected final BundleUnwrapperIterator_1_19_4 parent;
    protected Iterator<Object> packetIter;

    protected BundleUnwrapperIterator_1_19_4(BundleUnwrapperIterator_1_19_4 parent, Iterator<Object> packetIter) {
        this.parent = parent;
        this.packetIter = packetIter;
    }

    public static Iterable<Object> unwrap(Iterable<Object> packets) {
        return () -> new MainIterator(packets.iterator());
    }

    public static Iterator<Object> unwrap(Iterator<Object> packets) {
        return new MainIterator(packets);
    }

    private static class MainIterator
    extends BundleUnwrapperIterator_1_19_4
    implements Iterator<Object> {
        private BundleUnwrapperIterator_1_19_4 current = this;
        private Object next = null;

        public MainIterator(Iterator<Object> packetIter) {
            super(null, packetIter);
        }

        @Override
        public boolean hasNext() {
            return this.next != null || (this.next = this.genNextPacket()) != null;
        }

        @Override
        public Object next() {
            Object result = this.next;
            if (result != null) {
                this.next = null;
                return result;
            }
            result = this.genNextPacket();
            if (result != null) {
                return result;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void forEachRemaining(Consumer<? super Object> action) {
            Object packet;
            Object initial = this.next;
            if (initial != null) {
                this.next = null;
                action.accept(initial);
            }
            while ((packet = this.genNextPacket()) != null) {
                action.accept(packet);
            }
        }

        private Object genNextPacket() {
            BundleUnwrapperIterator_1_19_4 current = this.current;
            while (current != null) {
                Iterator<Object> currentIter = current.packetIter;
                if (currentIter.hasNext()) {
                    Object packet = currentIter.next();
                    Iterable<Object> bundle = PacketHandle.tryUnwrapBundlePacket(packet);
                    if (bundle != null) {
                        this.current = current = new BundleUnwrapperIterator_1_19_4(current, bundle.iterator());
                        continue;
                    }
                    return packet;
                }
                this.current = current = current.parent;
            }
            return null;
        }
    }
}

