/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook_Fallback;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook_Impl_26_1;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook_Impl_Default;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook_Impl_Paper_1_21_2;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import java.util.logging.Level;
import org.bukkit.Material;

public abstract class BlockDataWrapperHook
implements LibraryComponent {
    public static BlockDataWrapperHook INSTANCE = ((LibraryComponentSelector)LibraryComponentSelector.forModule(BlockDataWrapperHook.class).runFirst(CommonBootstrap::initServer)).setDefaultComponent(BlockDataWrapperHook_Fallback::new).addWhen("Paper 1.21.2", a -> CommonBootstrap.evaluateMCVersion(">=", "1.21.2") && CommonBootstrap.evaluateMCVersion("<=", "1.21.11") && CommonBootstrap.isPaperServer(), BlockDataWrapperHook_Impl_Paper_1_21_2::new).addVersionOption("1.8", "1.21.11", BlockDataWrapperHook_Impl_Default::new).addVersionOption("26.1", null, BlockDataWrapperHook_Impl_26_1::new).update();

    public static void init() {
    }

    public static void disableHook() {
        try {
            INSTANCE.disable();
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to disable BlockData hook", t);
        }
        INSTANCE = new BlockDataWrapperHook_Fallback();
    }

    public void hook(Object nmsIBlockData, Object accessor, BlockData blockData) {
        this.setAccessor(nmsIBlockData, this.hook(accessor, blockData));
    }

    public final void unhook(Object nmsIBlockData) {
        Object accessor;
        try {
            accessor = this.getAccessor(nmsIBlockData);
            if (!(accessor instanceof Accessor)) {
                return;
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read BlockData accessor field", t);
            return;
        }
        try {
            this.setAccessor(nmsIBlockData, ((Accessor)accessor).bkcGetOriginalValue());
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to unhook BlockData accessor", t);
        }
    }

    @Override
    public void disable() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void enable() throws Throwable {
        this.baseEnable();
        Object airBlockData = BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(Material.AIR)).getBlockData().getRaw();
        Object currentAccessor = this.getAccessor(airBlockData);
        try {
            Object hooked = this.hook(currentAccessor, BlockData.AIR);
            this.setAccessor(airBlockData, hooked);
            if (this.getAccessor(airBlockData) != hooked) {
                Thread.yield();
                if (this.getAccessor(airBlockData) != hooked) {
                    throw new IllegalStateException("Output of getAccessor() did not change after setting accessor");
                }
            }
        }
        finally {
            this.setAccessor(airBlockData, currentAccessor);
        }
    }

    protected abstract void baseEnable() throws Throwable;

    public abstract Object getAccessor(Object var1);

    protected abstract void setAccessor(Object var1, Object var2);

    protected abstract Object hook(Object var1, BlockData var2);

    public static interface Accessor {
        public Object bkcGetOriginalValue();

        public BlockData bkcGetBlockData();
    }
}

