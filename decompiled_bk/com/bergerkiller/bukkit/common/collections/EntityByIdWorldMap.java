/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.bukkit.common.collections;

import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class EntityByIdWorldMap {
    private final IntHashMap<EntitySlot> entitiesById = new IntHashMap();
    private final Set<EntitySlot> slots = new HashSet<EntitySlot>();
    private int markCounter = 0;

    public synchronized Entity get(World world, int entityId) {
        EntitySlot slot = this.entitiesById.get(entityId);
        while (slot != null) {
            if (slot.world == world) {
                return slot.entity;
            }
            slot = slot.next;
        }
        return null;
    }

    public synchronized void add(World world, Entity entity) {
        this.addAndGetSlot(world, entity);
    }

    private EntitySlot addAndGetSlot(World world, Entity entity) {
        int id = entity.getEntityId();
        EntitySlot slot = this.entitiesById.get(id);
        if (slot == null) {
            slot = new EntitySlot(world, entity, id);
            this.entitiesById.put(id, slot);
            this.slots.add(slot);
            return slot;
        }
        while (true) {
            if (slot.world == world) {
                slot.entity = entity;
                return slot;
            }
            EntitySlot next = slot.next;
            if (next == null) break;
            slot = next;
        }
        slot = slot.next = new EntitySlot(world, entity, id);
        this.slots.add(slot);
        return slot;
    }

    public synchronized void remove(World world, Entity entity) {
        int id = entity.getEntityId();
        EntitySlot slot = this.entitiesById.remove(id);
        if (slot != null) {
            if (slot.world == world && slot.entity == entity) {
                this.slots.remove(slot);
                if (slot.next != null) {
                    this.entitiesById.put(id, slot.next);
                }
                return;
            }
            this.entitiesById.put(id, slot);
            EntitySlot next = slot;
            while ((next = next.next) != null) {
                if (next.world != world || next.entity != entity) continue;
                this.slots.remove(next);
                slot.next = next.next;
                break;
            }
        }
    }

    public synchronized void clear(World world) {
        Iterator<EntitySlot> iter = this.slots.iterator();
        while (iter.hasNext()) {
            EntitySlot slot = iter.next();
            if (slot.world != world) continue;
            iter.remove();
            this.removeSlotFromMap(slot);
        }
    }

    public synchronized void sync(World world) {
        int mark = ++this.markCounter;
        for (Entity entity : WorldUtil.getEntities(world)) {
            this.addAndGetSlot((World)world, (Entity)entity).mark = mark;
        }
        Iterator<EntitySlot> iter = this.slots.iterator();
        while (iter.hasNext()) {
            EntitySlot slot = iter.next();
            if (slot.world != world || slot.mark == mark) continue;
            iter.remove();
            this.removeSlotFromMap(slot);
        }
    }

    private void removeSlotFromMap(EntitySlot slot) {
        EntitySlot root = this.entitiesById.remove(slot.entityId);
        if (root == slot) {
            if (root.next != null) {
                this.entitiesById.put(slot.entityId, root.next);
            }
        } else {
            EntitySlot next;
            this.entitiesById.put(slot.entityId, root);
            while ((next = root.next) != null) {
                if (next == slot) {
                    root.next = slot.next;
                    break;
                }
                root = next;
            }
        }
    }

    private static final class EntitySlot {
        public final World world;
        public Entity entity;
        public final int entityId;
        public int mark;
        public EntitySlot next;

        public EntitySlot(World world, Entity entity, int entityId) {
            this.world = world;
            this.entity = entity;
            this.entityId = entityId;
            this.mark = 0;
            this.next = null;
        }
    }
}

