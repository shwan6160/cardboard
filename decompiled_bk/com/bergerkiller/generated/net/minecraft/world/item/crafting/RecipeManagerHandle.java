/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.item.crafting;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.RecipeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.item.crafting.RecipeManager")
public abstract class RecipeManagerHandle
extends Template.Handle {
    public static final RecipeManagerClass T = Template.Class.create(RecipeManagerClass.class, Common.TEMPLATE_RESOLVER);

    public static RecipeManagerHandle createHandle(Object handleInstance) {
        return (RecipeManagerHandle)T.createHandle(handleInstance);
    }

    public static Iterable<RecipeHandle> getRecipes() {
        return RecipeManagerHandle.T.getRecipes.invoke();
    }

    public static final class RecipeManagerClass
    extends Template.Class<RecipeManagerHandle> {
        public final Template.StaticMethod.Converted<Iterable<RecipeHandle>> getRecipes = new Template.StaticMethod.Converted();
    }
}

