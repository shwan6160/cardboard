/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.world.level.BaseSpawner")
public abstract class BaseSpawnerHandle
extends Template.Handle {
    public static final BaseSpawnerClass T = Template.Class.create(BaseSpawnerClass.class, Common.TEMPLATE_RESOLVER);

    public static BaseSpawnerHandle createHandle(Object handleInstance) {
        return (BaseSpawnerHandle)T.createHandle(handleInstance);
    }

    public abstract void onTick(World var1, IntVector3 var2);

    public abstract IdentifierHandle getMobName();

    public abstract void setMobName(IdentifierHandle var1);

    public abstract int getSpawnDelay();

    public abstract void setSpawnDelay(int var1);

    public abstract int getMinSpawnDelay();

    public abstract void setMinSpawnDelay(int var1);

    public abstract int getMaxSpawnDelay();

    public abstract void setMaxSpawnDelay(int var1);

    public abstract int getSpawnCount();

    public abstract void setSpawnCount(int var1);

    public abstract Entity getEntity();

    public abstract void setEntity(Entity var1);

    public abstract int getMaxNearbyEntities();

    public abstract void setMaxNearbyEntities(int var1);

    public abstract int getRequiredPlayerRange();

    public abstract void setRequiredPlayerRange(int var1);

    public abstract int getSpawnRange();

    public abstract void setSpawnRange(int var1);

    public static final class BaseSpawnerClass
    extends Template.Class<BaseSpawnerHandle> {
        public final Template.Field.Integer spawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer minSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer maxSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer spawnCount = new Template.Field.Integer();
        public final Template.Field.Converted<Entity> entity = new Template.Field.Converted();
        public final Template.Field.Integer maxNearbyEntities = new Template.Field.Integer();
        public final Template.Field.Integer requiredPlayerRange = new Template.Field.Integer();
        public final Template.Field.Integer spawnRange = new Template.Field.Integer();
        public final Template.Method.Converted<Void> onTick = new Template.Method.Converted();
        public final Template.Method.Converted<IdentifierHandle> getMobName = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setMobName = new Template.Method.Converted();
    }
}

