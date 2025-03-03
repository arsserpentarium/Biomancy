package com.github.elenterius.biomancy.recipe;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModReagents;
import com.github.elenterius.biomancy.init.ModRecipes;
import com.github.elenterius.biomancy.item.AccessKeyItem;
import com.github.elenterius.biomancy.reagent.Reagent;
import com.github.elenterius.biomancy.util.UserAuthorization;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class AddUserToAccessKeyRecipe extends SpecialRecipe {

	public AddUserToAccessKeyRecipe(ResourceLocation idIn) {
		super(idIn);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int dna = 0, key = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof AccessKeyItem) {
					if (++key > 1) return false;
				}
				else if (stack.getItem() == ModItems.REAGENT.get()) {
					Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
					if (reagent == null || reagent != ModReagents.BLOOD_SAMPLE.get() || ++dna > 1) return false;
				}
			}
		}

		return dna == 1 && key == 1;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack key = ItemStack.EMPTY;
		ItemStack reagentStack = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof AccessKeyItem) {
					key = stack;
				}
				else if (stack.getItem() == ModItems.REAGENT.get()) {
					Reagent temp = Reagent.deserialize(stack.getOrCreateTag());
					if (temp != null && temp == ModReagents.BLOOD_SAMPLE.get()) {
						reagentStack = stack;
					}
				}
			}
		}

		if (key.isEmpty() || reagentStack.isEmpty()) return ItemStack.EMPTY;
		ItemStack stack = key.copy();

		CompoundNBT reagentNbt = reagentStack.getOrCreateTag();
		if (reagentNbt.contains(Reagent.NBT_KEY_DATA)) {
			CompoundNBT entityNBT = reagentNbt.getCompound(Reagent.NBT_KEY_DATA);
			if (entityNBT.contains("EntityUUID")) {
				UUID userUUID = entityNBT.getUniqueId("EntityUUID");

				CompoundNBT keyNbt = stack.getOrCreateChildTag(AccessKeyItem.NBT_KEY);
				ListNBT nbtList = keyNbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
				CompoundNBT nbtEntry = new CompoundNBT();
				nbtEntry.putUniqueId("UserUUID", userUUID);
				UserAuthorization.AuthorityLevel.USER.serialize(nbtEntry);
				nbtList.add(nbtEntry);
				keyNbt.put("UserList", nbtList);

				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.CRAFTING_SPECIAL_ADD_USER_TO_KEY.get();
	}

}
