package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.MasonBeetleItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MasonBeetleRecipe extends SpecialRecipe {
	public MasonBeetleRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int blocks = 0, beetles = 0;
		ItemStack blockStack = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof MasonBeetleItem) {
					blockStack = ((MasonBeetleItem) stack.getItem()).getBlockItemStack(stack);
					if (++beetles > 1) return false;
				}
				else if (!(stack.getItem() instanceof BlockItem) || stack.getItem().isFood() || ++blocks > 1) return false;
			}
		}

		return (beetles == 1 && blocks == 1 && blockStack.isEmpty()) || (beetles == 1 && blocks == 0 && !blockStack.isEmpty());
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack beetleStack = ItemStack.EMPTY;
		ItemStack blockStack = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof MasonBeetleItem) {
					beetleStack = stack;
				}
				else {
					blockStack = stack;
				}
			}
		}

		if (beetleStack.isEmpty()) return ItemStack.EMPTY;

		if (!blockStack.isEmpty()) {
			blockStack = blockStack.copy();
			blockStack.setCount(1);
			return ((MasonBeetleItem) beetleStack.getItem()).setBlockItemStack(beetleStack.copy(), blockStack);
		}
		else {
			return ((MasonBeetleItem) beetleStack.getItem()).getBlockItemStack(beetleStack);
		}
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CRAFTING_SPECIAL_BOOMLING_GRENADE.get();
	}
}
