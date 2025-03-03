package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.block.MeatsoupCauldronBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBlock.class)
public abstract class CauldronBlockMixin {

	@Shadow
	@Final
	public static IntegerProperty LEVEL;

	@Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
	protected void biomancy_onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci) {
		if (!worldIn.isRemote && entityIn instanceof ItemEntity) {
			int waterLevel = state.get(LEVEL);
			if (waterLevel == 0) {
				ItemStack stack = ((ItemEntity) entityIn).getItem();
				Item item = stack.getItem();
				if (item.isIn(ModTags.Items.RAW_MEATS)) {
					int amount = Math.min(stack.getCount(), 5);
					((ItemEntity) entityIn).getItem().grow(-amount);

					BlockState meatState = ModBlocks.MEATSOUP_CAULDRON.get().getDefaultState().with(MeatsoupCauldronBlock.LEVEL, amount);
					worldIn.setBlockState(pos, meatState, Constants.BlockFlags.BLOCK_UPDATE);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1f, 0.5f);

					ci.cancel();
				}
			}
		}
	}

}
