package com.github.elenterius.biomancy.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class NonNestingSlot extends Slot {

	public NonNestingSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		//only allow empty item inventories
		return HandlerBehaviors.EMPTY_ITEM_INVENTORY_PREDICATE.test(stack);
	}

}
