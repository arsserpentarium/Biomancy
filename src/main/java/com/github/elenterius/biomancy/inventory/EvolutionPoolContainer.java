package com.github.elenterius.biomancy.inventory;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModContainerTypes;
import com.github.elenterius.biomancy.tileentity.EvolutionPoolTileEntity;
import com.github.elenterius.biomancy.tileentity.state.EvolutionPoolStateData;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.logging.log4j.MarkerManager;

public class EvolutionPoolContainer extends Container {

	protected final SimpleInventory fuelInventory;
	protected final SimpleInventory inputInventory;
	protected final SimpleInventory outputInventory;
	private final EvolutionPoolStateData stateData;
	private final World world;

	private EvolutionPoolContainer(int screenId, PlayerInventory playerInventory, SimpleInventory fuelInventory, SimpleInventory inputInventory, SimpleInventory outputInventory, EvolutionPoolStateData stateData) {
		super(ModContainerTypes.EVOLUTION_POOL.get(), screenId);
		this.fuelInventory = fuelInventory;
		this.inputInventory = inputInventory;
		this.outputInventory = outputInventory;
		this.stateData = stateData;
		world = playerInventory.player.world;

		trackIntArray(stateData);

		PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);

		final int HOT_BAR_SIZE = 9;
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;

		// Add the players hotbar
		for (int idx = 0; idx < HOT_BAR_SIZE; idx++) { //hotbar
			addSlot(new SlotItemHandler(playerInventoryForge, idx, 8 + SLOT_X_SPACING * idx, 142));
		}

		// Add the players main inventory
		final int PLAYER_INVENTORY_POS_X = 8;
		final int PLAYER_INVENTORY_POS_Y = 84;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = HOT_BAR_SIZE + y * 9 + x;
				int posX = PLAYER_INVENTORY_POS_X + x * SLOT_X_SPACING;
				int posY = PLAYER_INVENTORY_POS_Y + y * SLOT_Y_SPACING;
				addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, posX, posY));
			}
		}

		int posX = 17;
		int posY = 17;
		addSlot(new Slot(fuelInventory, 0, posX, posY) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return EvolutionPoolTileEntity.VALID_FUEL_ITEM.test(stack);
			}
		});

		int inputPosX = posX + 18 * 2;
		addSlot(new Slot(inputInventory, 0, inputPosX, posY));
		addSlot(new Slot(inputInventory, 1, inputPosX + 18, posY));
		addSlot(new Slot(inputInventory, 2, inputPosX + 18 * 2, posY));
		addSlot(new Slot(inputInventory, 3, inputPosX, posY + 18));
		addSlot(new Slot(inputInventory, 4, inputPosX + 18, posY + 18));
		addSlot(new Slot(inputInventory, 5, inputPosX + 18 * 2, posY + 18));

		addSlot(new OutputSlot(outputInventory, 0, 125, 26));
	}

	public static EvolutionPoolContainer createServerContainer(int screenId, PlayerInventory playerInventory, SimpleInventory fuelInventory, SimpleInventory inputInventory, SimpleInventory outputInventory, EvolutionPoolStateData stateData) {
		return new EvolutionPoolContainer(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	public static EvolutionPoolContainer createClientContainer(int screenId, PlayerInventory playerInventory, PacketBuffer extraData) {
		SimpleInventory fuelInventory = SimpleInventory.createClientContents(EvolutionPoolTileEntity.FUEL_SLOTS);
		SimpleInventory inputInventory = SimpleInventory.createClientContents(EvolutionPoolTileEntity.INPUT_SLOTS);
		SimpleInventory outputInventory = SimpleInventory.createClientContents(EvolutionPoolTileEntity.OUTPUT_SLOTS);
		EvolutionPoolStateData stateData = new EvolutionPoolStateData();
		return new EvolutionPoolContainer(screenId, playerInventory, fuelInventory, inputInventory, outputInventory, stateData);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		//we don't check all three inventories because they all call the same method in the decomposer tile entity
		return inputInventory.isUsableByPlayer(playerIn);
	}

	public float getCraftingProgressNormalized() {
		if (stateData.timeForCompletion == 0) return 0f;
		return MathHelper.clamp(stateData.timeElapsed / (float) stateData.timeForCompletion, 0f, 1f);
	}

	public int getFuel() {
		return stateData.fuel;
	}

	public float getFuelNormalized() {
		return MathHelper.clamp(stateData.fuel / (float) EvolutionPoolTileEntity.MAX_FUEL, 0f, 1f);
	}

	/**
	 * copied from: https://github.com/TheGreyGhost/MinecraftByExample/blob/1-16-3-final/src/main/java/minecraftbyexample/mbe31_inventory_furnace/ContainerFurnace.java
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int sourceSlotIndex) {

		Slot sourceSlot = inventorySlots.get(sourceSlotIndex); // side-effect: throws error if the sourceSlotIndex is out of range (index < 0 || index >= size())
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		boolean successfulTransfer = false;
		SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);

		switch (sourceZone) {
			case OUTPUT_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, true);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, true);
				if (successfulTransfer) sourceSlot.onSlotChange(sourceStack, copyOfSourceStack);
				break;

			case INPUT_ZONE:
			case FUEL_ZONE:
				successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
				if (!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				break;

			case PLAYER_HOTBAR:
			case PLAYER_MAIN_INVENTORY:
				if (EvolutionPoolTileEntity.RECIPE_TYPE.getRecipeForItem(world, sourceStack).isPresent()) {
					successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, sourceStack, false);
				}
				if (!successfulTransfer && EvolutionPoolTileEntity.VALID_FUEL_ITEM.test(sourceStack)) {
					successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, sourceStack, true);
				}
				if (!successfulTransfer) {
					if (sourceZone == SlotZone.PLAYER_HOTBAR) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceStack, false);
					else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceStack, false);
				}
				break;

			default:
				throw new IllegalArgumentException("unexpected sourceZone:" + sourceZone);
		}

		if (!successfulTransfer) return ItemStack.EMPTY;

		if (sourceStack.isEmpty()) sourceSlot.putStack(ItemStack.EMPTY);
		else sourceSlot.onSlotChanged();

		if (sourceStack.getCount() == copyOfSourceStack.getCount()) {
			BiomancyMod.LOGGER.warn(MarkerManager.getMarker(getClass().getSimpleName()), "Stack transfer failed in an unexpected way!");
			return ItemStack.EMPTY; //transfer error
		}

		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	private boolean mergeInto(SlotZone destinationZone, ItemStack sourceStack, boolean fillFromEnd) {
		return mergeItemStack(sourceStack, destinationZone.firstIndex, destinationZone.lastIndexPlus1, fillFromEnd);
	}

	public String getFuelTranslationKey() {
		return TextUtil.getTranslationKey("tooltip", "mutagen");
	}

	private enum SlotZone {
		PLAYER_HOTBAR(0, 9),
		PLAYER_MAIN_INVENTORY(9, 3 * 9),
		FUEL_ZONE(9 + 3 * 9, EvolutionPoolTileEntity.FUEL_SLOTS),
		INPUT_ZONE(9 + 3 * 9 + EvolutionPoolTileEntity.FUEL_SLOTS, EvolutionPoolTileEntity.INPUT_SLOTS),
		OUTPUT_ZONE(9 + 3 * 9 + EvolutionPoolTileEntity.FUEL_SLOTS + EvolutionPoolTileEntity.INPUT_SLOTS, EvolutionPoolTileEntity.OUTPUT_SLOTS);

		public final int firstIndex;
		public final int slotCount;
		public final int lastIndexPlus1;

		SlotZone(int firstIndex, int numberOfSlots) {
			this.firstIndex = firstIndex;
			this.slotCount = numberOfSlots;
			this.lastIndexPlus1 = firstIndex + numberOfSlots;
		}

		public static SlotZone getZoneFromIndex(int slotIndex) {
			for (SlotZone slotZone : SlotZone.values()) {
				if (slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
			}
			throw new IndexOutOfBoundsException("Unexpected slotIndex");
		}
	}
}
