package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;

import net.minecraft.tags.ITag.INamedTag;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.Comparator;

import static net.minecraft.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public final Logger LOGGER = BiomancyMod.LOGGER;
	public final Marker logMarker = MarkerManager.getMarker("ModItemTagsProvider");

	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}
	
	protected final ItemComparator ITEM_ID_COMPARATOR = new ItemComparator();

	static class ItemComparator implements Comparator<IItemProvider> {
		@Override
		public int compare(IItemProvider a, IItemProvider b) {
			ResourceLocation idA = Registry.ITEM.getKey(a.asItem());
			ResourceLocation idB = Registry.ITEM.getKey(b.asItem());
			return idA.compareTo(idB);
		}
	}

    private static void addOptionalItemsTo(Builder<Item> builder, String... itemKeys) {
        for (String itemKey : itemKeys) {
            builder.addOptional(new ResourceLocation(itemKey));
        }
    }

    private static void addOptionalTagsTo(Builder<Item> builder, String... tagKeys) {
        for (String tagKey : tagKeys) {
            builder.addOptionalTag(new ResourceLocation(tagKey));
        }
    }
	
	@Override
	protected void registerTags() {
		LOGGER.info(logMarker, "registering item tags...");
		
		Builder<Item> cookedMeats = getOrCreateBuilder(ModTags.Items.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT).addOptional(new ResourceLocation("rats:cooked_rat"));

		//addOptionalTagsTo( cookedMeats ,"forge:bread");
		
		Builder<Item> rawMeats = getOrCreateBuilder(ModTags.Items.RAW_MEATS)
				.add(BEEF, PORKCHOP, CHICKEN, COD, SALMON, RABBIT, MUTTON, TROPICAL_FISH, PUFFERFISH, ModItems.FLESH_LUMP.get(),
				ModItems.OCULUS.get(), ModItems.OCULUS_KEY.get(), ModItems.STOMACH.get(), ModItems.ARTIFICIAL_STOMACH.get());

		addOptionalItemsTo( rawMeats, "createfa:ground_chicken","createfa:ground_beef","circus:clown", "rats:raw_rat", "evilcraft:flesh_humanoid", "evilcraft:flesh_werewolf");
		
		
		
		Builder<Item> sSugars = getOrCreateBuilder(ModTags.Items.SUGARS)
				.add(SUGAR, COOKIE, CAKE, HONEYCOMB, HONEY_BLOCK, HONEYCOMB_BLOCK, HONEY_BOTTLE, SWEET_BERRIES, COCOA_BEANS);

		addOptionalItemsTo( sSugars ,"create:sweet_roll", "create:chocolate_glazed_berries", 
				"create:honeyed_apple", "create:bar_of_chocolate", "createaddition:chocolate_cake");
		
		//addOptionalTagsTo( sSugars ,"#forge:fruits");
		
		getOrCreateBuilder(ModTags.Items.POOR_BIOMASS)
				.addTag(ItemTags.FLOWERS).addTag(Tags.Items.SEEDS).addTag(ItemTags.LEAVES)
				.add(SWEET_BERRIES, SUGAR_CANE, KELP, DRIED_KELP, GRASS, SEAGRASS, VINE, FERN, BAMBOO, ModItems.SKIN_CHUNK.get()).addOptional(new ResourceLocation("rats:contaminated_food"));
		
		Builder<Item> avgBiomass = getOrCreateBuilder(ModTags.Items.AVERAGE_BIOMASS)
				.addTag(ItemTags.SAPLINGS).addTag(Tags.Items.CROPS).addTag(Tags.Items.MUSHROOMS)
				.add(COOKIE, CACTUS, APPLE, CHORUS_FRUIT, MELON_SLICE, SPIDER_EYE, WARPED_FUNGUS,
						NETHER_SPROUTS, WEEPING_VINES, TWISTING_VINES, LARGE_FERN, TALL_GRASS, WARPED_ROOTS, CRIMSON_ROOTS, CRIMSON_FUNGUS);

		addOptionalItemsTo( avgBiomass, "createfa:cheese", "createfa:mixed_egg", "createfa:fries", "rats:cheese", 
				"rats:string_cheese", "rats:potato_kinishes");

		//addOptionalTagsTo( avgBiomass ,"#forge:carbs");
		
		Builder<Item> goodBiomass = getOrCreateBuilder(ModTags.Items.GOOD_BIOMASS)
				.add(BREAD, MUSHROOM_STEM, SUSPICIOUS_STEW, COCOA_BEANS, BAKED_POTATO, HONEYCOMB, HONEY_BOTTLE, MELON, PUMPKIN, DRIED_KELP_BLOCK,
						SEA_PICKLE, LILY_PAD, CARVED_PUMPKIN, WARPED_WART_BLOCK, NETHER_WART_BLOCK, RED_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK,
						SHROOMLIGHT, MUSHROOM_STEM);

		addOptionalItemsTo( goodBiomass ,"create:bar_of_chocolate", "createfa:hamburger", "createfa:schnitzel", "createfa:meatballs", 
				"createfa:chicken_nuggets", "rats:blue_cheese");
		
		addOptionalTagsTo( goodBiomass ,"forge:bread");
		
		Builder<Item> spbBiomass =getOrCreateBuilder(ModTags.Items.SUPERB_BIOMASS)
				.add(CAKE, PUMPKIN_PIE, RABBIT_STEW, BEETROOT_SOUP, POISONOUS_POTATO, HAY_BLOCK);

		addOptionalItemsTo( spbBiomass , "create:sweet_roll", "create:chocolate_glazed_berries", 
				"create:honeyed_apple", "createfa:cheeseburger", "rats:assorted_vegetables", "rats:rat_burger", 
				"rats:potato_pancake", "rats:confit_byaldi", "createaddition:chocolate_cake");
		
		Builder<Item> builder = getOrCreateBuilder(ModTags.Items.BIOMASS);
//		ComposterBlock.CHANCES.keySet().stream().sorted(ITEM_ID_COMPARATOR).map(IItemProvider::asItem).forEach(builder::addItemEntry);
		builder.addOptionalTag(ModTags.Items.POOR_BIOMASS.getName());
		builder.addOptionalTag(ModTags.Items.AVERAGE_BIOMASS.getName());
		builder.addOptionalTag(ModTags.Items.GOOD_BIOMASS.getName());
		builder.addOptionalTag(ModTags.Items.SUPERB_BIOMASS.getName());

		getOrCreateBuilder(ModTags.Items.STOMACHS)
				.add(ModItems.ARTIFICIAL_STOMACH.get(), ModItems.STOMACH.get());

		LOGGER.info(logMarker, "registering secretion item tags...");
		getOrCreateBuilder(ModTags.Items.OXIDES)
				.add(NETHERITE_SCRAP, TURTLE_EGG, BONE_MEAL)
				.addTag(Tags.Items.INGOTS_IRON).addTag(Tags.Items.NUGGETS_IRON)
				.addTag(Tags.Items.INGOTS_GOLD).addTag(Tags.Items.NUGGETS_GOLD)
				.addTag(Tags.Items.INGOTS_NETHERITE)
				.addTag(Tags.Items.EGGS).addTag(Tags.Items.BONES);

		getOrCreateBuilder(ModTags.Items.SILICATES)
				.add(ANDESITE, DIORITE, GRANITE, REDSTONE, KELP, DRIED_KELP, NAUTILUS_SHELL, SHULKER_SHELL, GLOWSTONE_DUST)
				.addTag(Tags.Items.GEMS_EMERALD).addTag(Tags.Items.GEMS_LAPIS).addTag(Tags.Items.GEMS_QUARTZ).addTag(Tags.Items.GEMS_PRISMARINE)
				.addOptional(new ResourceLocation("minecraft", "amethyst_shard"));

		getOrCreateBuilder(ModTags.Items.KERATINS)
				.add(SCUTE, LEAD, PHANTOM_MEMBRANE, RABBIT_HIDE, PRISMARINE_SHARD)
				.addTag(Tags.Items.STRING).addTag(ItemTags.WOOL).addTag(ItemTags.CARPETS)
				.addTag(Tags.Items.FEATHERS).addTag(Tags.Items.LEATHER)
				.addOptional(new ResourceLocation("minecraft", "goat_horn"));

		getOrCreateBuilder(ModTags.Items.HORMONES)
				.add(INK_SAC, RABBIT_FOOT, SPIDER_EYE, GHAST_TEAR, FERMENTED_SPIDER_EYE, HONEYCOMB, SLIME_BALL)
				.addOptional(new ResourceLocation("minecraft", "glow_ink_sac"));
	}


	
	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}
}
