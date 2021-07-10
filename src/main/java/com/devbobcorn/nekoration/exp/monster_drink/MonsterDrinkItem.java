package com.devbobcorn.nekoration.exp.monster_drink;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

/**
 * User: The Grey Ghost Date: 30/12/2014
 *
 * Item with variants, illustrating two different ways of changing the rendering
 * to suit the variant. The bottle has two types of information: Bottle flavor
 * (LIME, LEMON, CHERRY, ORANGE), stored in the "flavor" NBT tag Bottle
 * fullness (EMPTY, 25%, 50%, 75%, 100%), stored in the "fullness" NBT tag The
 * FULLNESS is used to select the model name: each different fullness has a
 * different layer1 texture for the liquid level This is done using a custom
 * PropertyOverride "fullness" which is used in the
 * monster_drink.json to select the correct model. The
 * custom PropertyOverride is attached to the Item in StartupClientOnly. The
 * FLAVOR is used to select the color of the contents (layer1 render) via
 * IItemColor
 */
public class MonsterDrinkItem extends Item {
	static private final int MAXIMUM_NUMBER_OF_BOTTLES = 1; // maximum stack size

	public MonsterDrinkItem() {
		super(new Item.Properties().stacksTo(MAXIMUM_NUMBER_OF_BOTTLES).tab(ItemGroup.TAB_BREWING));
	}

	/**
	 * Returns the color (flavor) of the contents, or a default if the itemstack
	 * is invalid
	 * 
	 * @param stack
	 * @return
	 */
	public static EnumBottleFlavor getFlavor(ItemStack stack) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		return EnumBottleFlavor.fromNBT(compoundNBT, NBT_TAG_NAME_FLAVOR);
	}

	/**
	 * Returns the fullness of the contents, or a default if the itemstack is
	 * invalid
	 * 
	 * @param stack
	 * @return
	 */
	public static EnumBottleFullness getFullness(ItemStack stack) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		return EnumBottleFullness.fromNBT(compoundNBT, NBT_TAG_NAME_FULLNESS);
	}

	/**
	 * gets the fullness property override, used in
	 * monster_drink.json to select which model should be
	 * rendered
	 * 
	 * @param itemStack
	 * @param world
	 * @param livingEntity
	 * @return
	 */
	public static float getFullnessPropertyOverride(ItemStack itemStack, @Nullable World world,
			@Nullable LivingEntity livingEntity) {
		EnumBottleFullness enumBottleFullness = getFullness(itemStack);
		return enumBottleFullness.getPropertyOverrideValue();
	}

	/**
	 * Sets the flavor of the contents
	 *
	 * @param stack
	 *            the stack
	 * @param enumBottleFlavor
	 *            the new flavor of the contents
	 */
	public static void setFlavor(ItemStack stack, EnumBottleFlavor enumBottleFlavor) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		enumBottleFlavor.putIntoNBT(compoundNBT, NBT_TAG_NAME_FLAVOR);
	}

	/**
	 * Sets the flavor of the contents
	 *
	 * @param stack
	 *            the stack
	 * @param enumBottleFullness
	 *            the new fullness of the contents
	 */
	public static void setFullness(ItemStack stack, EnumBottleFullness enumBottleFullness) {
		CompoundNBT compoundNBT = stack.getOrCreateTag();
		enumBottleFullness.putIntoNBT(compoundNBT, NBT_TAG_NAME_FULLNESS);
	}

	// add a subitem for each item that we want to appear in the creative tab
	// in this case - a full bottle of each flavor
	@Override
	public void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.allowdedIn(tab)) {
			for (EnumBottleFlavor flavor : EnumBottleFlavor.values()) {
				ItemStack subItemStack = new ItemStack(this, 1);
				setFlavor(subItemStack, flavor);
				setFullness(subItemStack, EnumBottleFullness.FULL);
				subItems.add(subItemStack);
			}
		}
	}

	// what animation to use when the player holds the "use" button
	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.DRINK;
	}

	// how long the drinking will last for, in ticks (1 tick = 1/20 second)
	@Override
	public int getUseDuration(ItemStack stack) {
		final int TICKS_PER_SECOND = 20;
		final int DRINK_DURATION_SECONDS = 2;
		return DRINK_DURATION_SECONDS * TICKS_PER_SECOND;
	}

	// called when the player starts holding right click;
	// --> start drinking the liquid (if the bottle isn't already empty)
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemStackHeld = playerIn.getItemInHand(hand);
		EnumBottleFullness fullness = getFullness(itemStackHeld);
		if (fullness == EnumBottleFullness.EMPTY)
			return new ActionResult<>(ActionResultType.FAIL, itemStackHeld);

		playerIn.startUsingItem(hand);
		return new ActionResult<>(ActionResultType.PASS, itemStackHeld);
	}

	// called when the player has held down right button for the full item use
	// duration
	// --> decrease the bottle fullness by one step
	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		EnumBottleFullness fullness = getFullness(stack);
		fullness = fullness.decreaseFullnessByOneStep();
		fullness.putIntoNBT(stack.getTag(), NBT_TAG_NAME_FULLNESS);
		return stack;
	}

	@Override
	// Make a unique name for each flavor (lime, orange, etc) so we can name them
	// individually
	// The fullness information is added separately in getItemStackDisplayName()
	public String getDescriptionId(ItemStack stack) {
		EnumBottleFlavor flavor = getFlavor(stack);
		return super.getDescriptionId(stack) + "." + flavor.getSerializedName();
	}

	// change the displayed stack name depending on the fullness
	// the flavor is already incorporated into the translationKey
	@Override
	public ITextComponent getName(ItemStack stack) {
		String fullnessText = getFullness(stack).getDescription();
		return new TranslationTextComponent(this.getDescriptionId(stack), fullnessText);
		// the entry in the lang file contains a %s which inserts our fullnessText into
		// the description
	}

	public static final String NBT_TAG_NAME_FLAVOR = "color";
	public static final String NBT_TAG_NAME_FULLNESS = "fullness";

	// create a new enum for the fullness of our liquid contents, with some
	// supporting methods to convert to & from NBT, and to get
	// human-readable names for a description.
	public enum EnumBottleFullness implements IStringSerializable {
		EMPTY(0, "0pc", "empty"), ONE_QUARTER(1, "25pc", "nearly empty"), ONE_HALF(2, "50pc",
				"half full"), THREE_QUARTERS(3, "75pc", "mostly full"), FULL(4, "100pc", "full");

		@Override
		public String toString() {
			return this.description;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		public float getPropertyOverrideValue() {
			return nbtID;
		}

		public EnumBottleFullness decreaseFullnessByOneStep() {
			if (nbtID == 0)
				return this;
			for (EnumBottleFullness fullness : EnumBottleFullness.values()) {
				if (fullness.nbtID == nbtID - 1)
					return fullness;
			}
			// error... return default
			return this;
		}

		/**
		 * Read the NBT tag for bottle fullness and create the corresponding Enum from
		 * it. Do not trust NBT values! They can be set to illegal values by factors
		 * outside your code's control. Always check them for correctness (within range,
		 * logical consistency with other tags, etc)
		 * 
		 * @param compoundNBT
		 * @return
		 */
		public static EnumBottleFullness fromNBT(CompoundNBT compoundNBT, String tagname) {
			byte fullnessID = 0; // default in case of error
			if (compoundNBT != null && compoundNBT.contains(tagname)) {
				fullnessID = compoundNBT.getByte(tagname);
			}
			Optional<EnumBottleFullness> fullness = getFullnessFromID(fullnessID);
			return fullness.orElse(FULL);
		}

		/**
		 * Write this enum to NBT
		 * 
		 * @param compoundNBT
		 * @param tagname
		 */
		public void putIntoNBT(CompoundNBT compoundNBT, String tagname) {
			compoundNBT.putByte(tagname, nbtID);
		}

		private final byte nbtID;
		private final String name;
		private final String description;

		EnumBottleFullness(int i_NBT_ID, String i_name, String i_description) {
			this.nbtID = (byte) i_NBT_ID;
			this.name = i_name;
			this.description = i_description;
		}

		private static Optional<EnumBottleFullness> getFullnessFromID(byte ID) {
			for (EnumBottleFullness fullness : EnumBottleFullness.values()) {
				if (fullness.nbtID == ID)
					return Optional.of(fullness);
			}
			return Optional.empty();
		}
	}

	// create a new enum for the color of our liquid contents, with some supporting
	// methods to convert to & from NBT, to get
	// human-readable names, and to provide a rendering color
	public enum EnumBottleFlavor implements IStringSerializable {
		LEMON(0, "lemon", Color.YELLOW), LIME(1, "lime", Color.GREEN), CHERRY(2, "cherry", Color.RED), ORANGE(3,
				"orange", Color.ORANGE);

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public Color getRenderColor() {
			return renderColor;
		}

		/**
		 * Read the NBT tag for bottle flavor and create the corresponding Enum from
		 * it. Do not trust NBT values! They can be set to illegal values by factors
		 * outside your code's control. Always check them for correctness (within range,
		 * logical consistency with other tags, etc)
		 * 
		 * @param compoundNBT
		 * @return
		 */
		public static EnumBottleFlavor fromNBT(CompoundNBT compoundNBT, String tagname) {
			byte flavorID = 0; // default in case of error
			if (compoundNBT != null && compoundNBT.contains(tagname)) {
				flavorID = compoundNBT.getByte(tagname);
			}
			Optional<EnumBottleFlavor> flavor = getFlavorFromID(flavorID);
			return flavor.orElse(LEMON); // default is lemon
		}

		/**
		 * Write this enum to NBT
		 * 
		 * @param compoundNBT
		 * @param tagname
		 */
		public void putIntoNBT(CompoundNBT compoundNBT, String tagname) {
			compoundNBT.putByte(tagname, nbtID);
		}

		private final byte nbtID;
		private final String name;
		private final Color renderColor;

		EnumBottleFlavor(int i_NBT_ID, String i_name, Color i_renderColor) {
			this.nbtID = (byte) i_NBT_ID;
			this.name = i_name;
			this.renderColor = i_renderColor;
		}

		private static Optional<EnumBottleFlavor> getFlavorFromID(byte ID) {
			for (EnumBottleFlavor flavor : EnumBottleFlavor.values()) {
				if (flavor.nbtID == ID)
					return Optional.of(flavor);
			}
			return Optional.empty();
		}
	}
}