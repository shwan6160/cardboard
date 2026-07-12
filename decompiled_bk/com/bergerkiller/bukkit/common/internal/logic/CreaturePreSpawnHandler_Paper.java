/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.AnnotationVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.FieldVisitor;
import com.bergerkiller.mountiplex.dep.org.objectweb.asm.MethodVisitor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreaturePreSpawnHandler_Paper
extends CreaturePreSpawnHandler {
    private final PreCreatureSpawnEventHandle handle = Template.Class.create(PreCreatureSpawnEventHandle.class);
    private final Listener listener;

    public CreaturePreSpawnHandler_Paper() throws Throwable {
        this.handle.forceInitialization();
        ExtendedClassWriter cw = ExtendedClassWriter.builder(Listener.class).setExactName(CreaturePreSpawnHandler_Paper.class.getName() + "$Listener").build();
        String preSpawnHandlerPaperDesc = MPLType.getDescriptor(CreaturePreSpawnHandler_Paper.class);
        FieldVisitor fv = cw.visitField(18, "main", preSpawnHandlerPaperDesc, null, null);
        fv.visitEnd();
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(" + preSpawnHandlerPaperDesc + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitFieldInsn(181, cw.getInternalName(), "main", preSpawnHandlerPaperDesc);
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        Class<?> preSpawnEventType = Class.forName("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");
        String preSpawnEventDesc = MPLType.getDescriptor(preSpawnEventType);
        mv = cw.visitMethod(1, "onCreaturePreSpawnEvent", "(" + preSpawnEventDesc + ")V", null, null);
        AnnotationVisitor av0 = mv.visitAnnotation(MPLType.getDescriptor(EventHandler.class), true);
        av0.visitEnd();
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitFieldInsn(180, cw.getInternalName(), "main", preSpawnHandlerPaperDesc);
        mv.visitVarInsn(25, 1);
        mv.visitMethodInsn(182, MPLType.getInternalName(CreaturePreSpawnHandler_Paper.class), "onCreaturePreSpawnEvent", "(Ljava/lang/Object;)V", false);
        mv.visitInsn(177);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        this.listener = (Listener)cw.generateInstance(new Class[]{CreaturePreSpawnHandler_Paper.class}, new Object[]{this});
        if (CommonPlugin.hasInstance()) {
            CommonPlugin.getInstance().register(this.listener);
        }
    }

    public void onCreaturePreSpawnEvent(Object event) {
        if (!this.handle.isCancelled(event)) {
            CreatureSpawnEvent.SpawnReason reason = this.handle.getReason(event);
            if (reason != CreatureSpawnEvent.SpawnReason.NATURAL && reason != CreatureSpawnEvent.SpawnReason.SPAWNER) {
                return;
            }
            Location at = this.handle.getLocation(event);
            EntityType entityType = this.handle.getEntityType(event);
            if (!CommonPlugin.getInstance().getEventFactory().handleCreaturePreSpawn(at, entityType, reason)) {
                this.handle.abort(event);
            }
        }
    }

    @Override
    public void onEventHasHandlers() {
    }

    @Override
    public void onWorldEnabled(World world) {
    }

    @Override
    public void onWorldDisabled(World world) {
    }

    @Template.ImportList(value={@Template.Import(value="org.bukkit.Location"), @Template.Import(value="org.bukkit.entity.EntityType")})
    @Template.InstanceType(value="com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent")
    public static abstract class PreCreatureSpawnEventHandle
    extends Template.Class<Template.Handle> {
        @Template.Generated(value="public static boolean isCancelled(PreCreatureSpawnEvent event) {\n#if exists com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent public boolean shouldAbortSpawn();\n    if (event.shouldAbortSpawn()) {\n        return true;\n    }\n           #endif\n    return event.isCancelled();\n}")
        public abstract boolean isCancelled(Object var1);

        @Template.Generated(value="public static void abort(PreCreatureSpawnEvent event) {\n#if exists com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent public void setShouldAbortSpawn(boolean shouldAbortSpawn);\n    event.setShouldAbortSpawn(true);\n#endif\n               event.setCancelled(true);\n}")
        public abstract void abort(Object var1);

        @Template.Generated(value="public static Location getLocation(PreCreatureSpawnEvent event) {\n    return event.getSpawnLocation();\n}")
        public abstract Location getLocation(Object var1);

        @Template.Generated(value="public static EntityType getEntityType(PreCreatureSpawnEvent event) {\n    return event.getType();\n}")
        public abstract EntityType getEntityType(Object var1);

        @Template.Generated(value="public static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason getReason(PreCreatureSpawnEvent event) {\n    return event.getReason();\n}")
        public abstract CreatureSpawnEvent.SpawnReason getReason(Object var1);
    }
}

