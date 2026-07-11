package org.cardboardpowered.mixin.world.level;

import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.bridge.world.level.LevelBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.storage.WritableLevelData;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelBridge {

    @Shadow public LevelChunk getChunkAt(BlockPos pos) {return null;}

    private CraftWorld world;

    // private CraftWorld bukkitBackup;

    public boolean captureBlockStates = false;
    public boolean captureTreeGeneration = false;
    public Map<BlockPos, CapturedBlockState> capturedBlockStates = new HashMap<>();

    @Shadow
    public abstract LevelEntityGetter<Entity> getEntities();

    @Shadow
    public ResourceKey<Level> dimension() {
        return null;
    }

    @Shadow public Holder<DimensionType> dimensionTypeRegistration() { return null; }

    public ResourceKey<DimensionType> getTypeKey() {
        return dimensionTypeRegistration().unwrapKey().orElse(null);
    }

    @Override
    public LevelEntityGetter<Entity> cb$get_entity_lookup() {
    	return getEntities();
    }
    
    @Override
    public Map<BlockPos, CapturedBlockState> getCapturedBlockStates_BF() {
        return capturedBlockStates;
    }

    @Override
    public boolean isCaptureBlockStates_BF() {
        return captureBlockStates;
    }
    
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(WritableLevelData a, ResourceKey<?> b, RegistryAccess rm, Holder<DimensionType> registryEntry, boolean f, boolean g, long h, int i, CallbackInfo ci) {

        if (!(((Level) (Object) this) instanceof ServerLevel)) {
            System.out.println("CLIENT WORLD!");
            return;
        }

        Level thiz = (Level) (Object) this;
        ServerLevel nms = ((ServerLevel) thiz);
    	CardboardMod.on_world_init_mc(nms);
    }
    
    public CraftWorld getWorld() {
        return world;
    }

    @Override
    public CraftWorld cardboard$getWorld() {
        return world;
    }
    
    public CraftWorld getCraftWorld() {
        return world;
    }

    @Override
    public void set_bukkit_world(CraftWorld world) {
        
    	// Reflection trickery as the bukkitWorld field is final in Paper
    	/*
    	try {
    		Field field = ((Level)(Object)this).getClass().getField("world");
    		field.setAccessible(true); // Bypasses private access check
    		field.set(((Level)(Object)this), world); // Injects the new value
    	} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	this.world = world;
    }

    private int cardboard$getPowerLevel(BlockState state) {
        if (state == null) return 0;
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER);
        }
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED) ? 15 : 0;
        }
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)) {
            net.minecraft.world.level.block.Block block = state.getBlock();
            if (block instanceof net.minecraft.world.level.block.RedstoneTorchBlock || block instanceof net.minecraft.world.level.block.RedstoneWallTorchBlock) {
                return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 15 : 0;
            }
        }
        return 0;
    }

    @Inject(at = @At("HEAD"), method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    public void setBlockState1(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if (((Level) (Object) this) instanceof ServerLevel) {
            BlockState oldState = ((Level)(Object)this).getBlockState(pos);
            int oldPower = cardboard$getPowerLevel(oldState);
            int newPower = cardboard$getPowerLevel(state);
            if (oldPower != newPower) {
                org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange((Level)(Object)this, pos, oldPower, newPower);
            }
        }
        // TODO 1.17ify: if (!ServerWorld.isOutOfBuildLimitVertically(blockposition)) {
            LevelChunk chunk = getChunkAt(pos);
            boolean captured = false;
            if (this.captureBlockStates && !this.capturedBlockStates.containsKey(pos)) {
                CapturedBlockState blockstate = CapturedBlockState.getTreeBlockState((Level)(Object)this, pos, flags);
                this.capturedBlockStates.put(pos.immutable(), blockstate);
                captured = true;
            }
        //}
    }

    @Override
    public void setCaptureBlockStates_BF(boolean b) {
        this.captureBlockStates = b;
    }
    
    public CraftServer getCraftServer() {
		return CraftServer.INSTANCE;
	}

}
