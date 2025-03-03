package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(new ModLootTableProvider(generator));

		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		ModBlockTagsProvider blockTags = new ModBlockTagsProvider(generator, existingFileHelper);
		generator.addProvider(blockTags);
		generator.addProvider(new ModItemTagsProvider(generator, blockTags, existingFileHelper));

		generator.addProvider(new ModRecipeProvider(generator));
	}

	public static class ModLootTableProvider extends LootTableProvider {
		private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> lootTables = ImmutableList.of(
//            Pair.of(FishingLootTables::new, LootParameterSets.FISHING),
//            Pair.of(ChestLootTables::new, LootParameterSets.CHEST),
				Pair.of(ModEntityLootTables::new, LootParameterSets.ENTITY),
				Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK)
//            Pair.of(PiglinBarteringAddition::new, LootParameterSets.field_237453_h_),
//            Pair.of(GiftLootTables::new, LootParameterSets.GIFT)
		);

		public ModLootTableProvider(DataGenerator dataGeneratorIn) {
			super(dataGeneratorIn);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
			return lootTables;
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
			// do nothing, we can remove the override when all lootTables are present
		}

		@Override
		public String getName() {
			return StringUtils.capitalize(BiomancyMod.MOD_ID) + " " + super.getName();
		}
	}
}