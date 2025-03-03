package com.github.elenterius.biomancy.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Based on FurnaceZoneContents class from author TGG <br>
 * link: https://github.com/TheGreyGhost/MinecraftByExample/blob/1-16-3-final/src/main/java/minecraftbyexample/mbe31_inventory_furnace/FurnaceZoneContents.java"
 */
public class SimpleInventory implements IInventory {

	private final ItemStackHandler itemStackHandler;
	private final LazyOptional<IItemHandler> optionalItemStackHandler;

	private Predicate<PlayerEntity> canPlayerAccessInventory = x -> true;
	private Notify markDirtyNotifier = () -> {};
	private Consumer<PlayerEntity> onOpenInventory = (player) -> {};
	private Consumer<PlayerEntity> onCloseInventory = (player) -> {};

	SimpleInventory(int slotAmount) {
		itemStackHandler = new ItemStackHandler(slotAmount);
		optionalItemStackHandler = LazyOptional.of(() -> itemStackHandler);
	}

	SimpleInventory(int slotAmount, Function<ItemStackHandler, LazyOptional<IItemHandler>> func) {
		itemStackHandler = new ItemStackHandler(slotAmount);
		optionalItemStackHandler = func.apply(itemStackHandler);
	}

	SimpleInventory(ItemStackHandler itemStackHandlerIn, LazyOptional<IItemHandler> optionalItemStackHandlerIn, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		itemStackHandler = itemStackHandlerIn;
		optionalItemStackHandler = optionalItemStackHandlerIn;
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, Function<ItemStackHandler, LazyOptional<IItemHandler>> func, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount, func);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	SimpleInventory(int slotAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		this(slotAmount);
		this.canPlayerAccessInventory = canPlayerAccessInventory;
		this.markDirtyNotifier = markDirtyNotifier;
	}

	public static SimpleInventory createServerContents(int slotAmount, Function<ItemStackHandler, LazyOptional<IItemHandler>> func, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory(slotAmount, func, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static SimpleInventory createServerContents(int slotAmount, Predicate<PlayerEntity> canPlayerAccessInventory, Notify markDirtyNotifier) {
		return new SimpleInventory(slotAmount, canPlayerAccessInventory, markDirtyNotifier);
	}

	public static SimpleInventory createClientContents(int slotAmount) {
		return new SimpleInventory(slotAmount);
	}

	public LazyOptional<IItemHandler> getOptionalItemStackHandler() {
		return optionalItemStackHandler;
	}

	public CompoundNBT serializeNBT() {
		return itemStackHandler.serializeNBT();
	}

	public void deserializeNBT(CompoundNBT nbt) {
		itemStackHandler.deserializeNBT(nbt);
	}

	@Override
	public void markDirty() {
		markDirtyNotifier.invoke();
	}

	@Override
	public void openInventory(PlayerEntity player) {
		onOpenInventory.accept(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		onCloseInventory.accept(player);
	}

	public void setCanPlayerAccessInventory(Predicate<PlayerEntity> predicate) {
		canPlayerAccessInventory = predicate;
	}

	public void setMarkDirtyNotifier(Notify callback) {
		markDirtyNotifier = callback;
	}

	public void setOpenInventoryConsumer(Consumer<PlayerEntity> consumer) {
		onOpenInventory = consumer;
	}

	public void setCloseInventoryConsumer(Consumer<PlayerEntity> consumer) {
		onCloseInventory = consumer;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return canPlayerAccessInventory.test(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return itemStackHandler.isItemValid(index, stack);
	}

	public ItemStack insertItemStack(int index, ItemStack insertStack) {
		return itemStackHandler.insertItem(index, insertStack, false);
	}

	public boolean doesItemStackFit(int index, ItemStack insertStack) {
		ItemStack remainder = itemStackHandler.insertItem(index, insertStack, true);
		return remainder.isEmpty();
	}

	@Override
	public int getSizeInventory() {
		return itemStackHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemStackHandler.getSlots(); ++i) {
			if (!itemStackHandler.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return itemStackHandler.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (count < 0) throw new IllegalArgumentException("count should be >= 0:" + count);
		return itemStackHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		int maxPossibleItemStackSize = itemStackHandler.getSlotLimit(index);
		return itemStackHandler.extractItem(index, maxPossibleItemStackSize, false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		itemStackHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clear() {
		for (int i = 0; i < itemStackHandler.getSlots(); ++i) {
			itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public ItemStackHandler getItemStackHandler() {
		return itemStackHandler;
	}

}
