package com.eerussianguy.firmalife.common.blockentities;

import java.util.function.Predicate;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.capabilities.wine.WineCapability;
import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import com.eerussianguy.firmalife.common.container.BarrelPressContainer;
import com.eerussianguy.firmalife.common.container.BeehiveContainer;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

public class BarrelPressBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void tick(Level level, BlockPos pos, BlockState state, BarrelPressBlockEntity press)
    {
        press.checkForLastTickSync();

        final boolean doneWorking = level.getGameTime() - press.lastPushed > TIME;
        if (!press.didAction && doneWorking)
        {
            press.didAction = true;
            press.squish();
        }
        if (!doneWorking && level.isClientSide && level.getGameTime() % 2 == 0)
        {
            final RandomSource rand = level.random;
            final ItemStack item = press.inventory.getStackInSlot(rand.nextInt(SLOTS));
            if (!item.isEmpty())
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + rand.nextFloat(), pos.getY() + 0.2f, pos.getZ() + rand.nextFloat(), rand.nextFloat(), 0.125f + 0.5f * rand.nextFloat(), rand.nextFloat());
        }
        if (press.needsSlotUpdate && press.hasOutput() && press.output != null)
        {
            if (press.inventory.getStackInSlot(SLOT_WINE_OUT).isEmpty())
            {
                final ItemStack current = press.inventory.getStackInSlot(SLOT_WINE_IN);
                final ItemStack newStack = press.tryFillWine(level, pos, current);
                if (!newStack.isEmpty())
                {
                    press.inventory.setStackInSlot(SLOT_WINE_OUT, newStack);
                }
            }
        }
    }


    @Nullable
    private static Item getFilledWine(Item item)
    {
        if (item == FLItems.EMPTY_HEMATITIC_WINE_BOTTLE.get())
            return FLItems.HEMATITIC_WINE_BOTTLE.get();
        if (item == FLItems.EMPTY_OLIVINE_WINE_BOTTLE.get())
            return FLItems.OLIVINE_WINE_BOTTLE.get();
        if (item == FLItems.EMPTY_VOLCANIC_WINE_BOTTLE.get())
            return FLItems.VOLCANIC_WINE_BOTTLE.get();
        return null;
    }

    public static final int SLOTS = 9;
    public static final int SLOT_GRAPES = 0;
    public static final int SLOT_WINE_IN = 5;
    public static final int SLOT_WINE_OUT = 6;
    public static final int SLOT_CORK = 7;
    public static final int SLOT_LABEL = 8;
    public static final long TIME = 40L;

    private boolean didAction = false;
    private boolean needsSlotUpdate = false;
    private long lastPushed = 0L;
    @Nullable private WineOutput output = null;

    public BarrelPressBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BARREL_PRESS.get(), pos, state, defaultInventory(SLOTS), FLHelpers.blockEntityName("barrel_press"));
    }

    public InteractionResult push()
    {
        assert level != null;
        if (level.getGameTime() - lastPushed > TIME)
        {
            didAction = false;
            lastPushed = level.getGameTime();
            markForSync();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void squish()
    {
        final ItemStack grapes = inventory.getStackInSlot(SLOT_GRAPES);
        final int servings = grapes.getCount() / 4;
        if (servings == 0 || level == null)
            return;
        final WineType wine = getWineType();
        if (wine == null)
            return;
        final ItemStack cork = inventory.extractItem(SLOT_CORK, 1, false);
        if (cork.isEmpty())
            return;
        final var climate = KoppenClimateClassification.classify(Climate.getAverageTemperature(level, worldPosition), Climate.getRainfall(level, worldPosition));

        String labelText = null;
        final ItemStack label = inventory.extractItem(SLOT_LABEL, 1, false);
        if (label.hasCustomHoverName())
            labelText = label.getHoverName().getString();

        output = new WineOutput(wine, climate, servings, labelText);

        for (int i = SLOT_GRAPES; i < SLOT_WINE_IN; i++)
        {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        markForSync();
    }

    @Nullable
    public WineType getWineType()
    {
        final ItemStack grapes = inventory.getStackInSlot(SLOT_GRAPES);
        if (FoodCapability.isRotten(grapes))
            return null;
        final Item redGrapes = FLItems.FOODS.get(FLFood.SMASHED_RED_GRAPES).get();
        if (Helpers.isItem(grapes, redGrapes))
        {
            return WineType.RED;
        }
        else if (Helpers.isItem(grapes, FLItems.FOODS.get(FLFood.SMASHED_WHITE_GRAPES).get()))
        {
            if (hasAtLeastThisMuchOfThisInOtherSlots(s -> s.getItem() == redGrapes && FoodCapability.hasTrait(s, FLFoodTraits.FERMENTED) && !FoodCapability.isRotten(s), 1))
            {
                return WineType.ROSE;
            }
            if (hasAtLeastThisMuchOfThisInOtherSlots(s -> Helpers.isItem(s, FLTags.Items.SWEETENER), 2))
            {
                return WineType.DESSERT;
            }
            return WineType.WHITE;
        }
        return null;
    }

    public ItemStack tryFillWine(Level level, BlockPos pos, ItemStack current)
    {
        if (output == null || !hasOutput())
            return ItemStack.EMPTY;
        final Item newWine = getFilledWine(current.getItem());
        if (Helpers.isItem(current, FLTags.Items.EMPTY_WINE_BOTTLES) && newWine != null)
        {
            final ItemStack bottle = newWine.getDefaultInstance();
            bottle.getCapability(WineCapability.CAPABILITY).ifPresent(cap -> {
                cap.setCreationDate(Calendars.get(level).getTicks());
                cap.setWineType(output.wine);
                cap.setClimate(output.koppen);
                if (output.label != null)
                    cap.setLabelText(output.label);
            });

            current.shrink(1);
            output.servings--;
            inventory.setStackInSlot(SLOT_WINE_IN, ItemStack.EMPTY);
            if (output.servings <= 0)
                output = null;

            Helpers.playSound(level, pos, SoundEvents.BOTTLE_FILL);
            return bottle;
        }
        return ItemStack.EMPTY;
    }

    private boolean hasAtLeastThisMuchOfThisInOtherSlots(Predicate<ItemStack> test, int count)
    {
        int found = 0;
        for (int i = SLOT_GRAPES + 1; i <= SLOT_WINE_IN - 1; i++)
        {
            if (test.test(inventory.getStackInSlot(i)))
            {
                found++;
                if (found >= count)
                    return true;
            }
        }
        return found >= count;
    }

    @Nullable
    public WineOutput getOutput()
    {
        return output;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return slot == SLOT_GRAPES ? 16 : (slot == SLOT_CORK || slot == SLOT_LABEL) ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_GRAPES && Helpers.isItem(stack, FLTags.Items.CAN_BE_PRESSED_LIKE_GRAPES))
        {
            return stack.getCapability(FoodCapability.CAPABILITY).map(cap -> cap.hasTrait(FLFoodTraits.FERMENTED)).orElse(false);
        }
        if (slot == SLOT_WINE_OUT)
            return false; // only code can add an item here
        if (slot == SLOT_WINE_IN)
            return Helpers.isItem(stack, FLTags.Items.EMPTY_WINE_BOTTLES);
        if (slot == SLOT_CORK)
            return Helpers.isItem(stack, FLItems.CORK.get());
        if (slot == SLOT_LABEL)
            return Helpers.isItem(stack, FLItems.BOTTLE_LABEL.get());
        return true;
    }

    public boolean hasOutput()
    {
        if (output != null && output.servings <= 0)
        {
            output = null;
            return false;
        }
        return output != null;
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putLong("pushed", this.lastPushed);
        tag.putBoolean("didAction", didAction);
        if (output != null)
            output.save(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
        this.lastPushed = tag.getLong("pushed");
        this.didAction = tag.getBoolean("didAction");
        if (tag.contains("output", Tag.TAG_COMPOUND))
            output = new WineOutput(tag);

        needsSlotUpdate = true;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
    }

    public float sinceWeLastTouched(float partialTick)
    {
        assert level != null;
        return (level.getGameTime() - lastPushed) + partialTick;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return BarrelPressContainer.create(this, inv, windowID);
    }

    public static class WineOutput
    {
        private WineType wine = WineType.RED;
        private KoppenClimateClassification koppen = KoppenClimateClassification.TEMPERATE;
        private int servings = 0;
        @Nullable private String label;

        public WineOutput(WineType type, KoppenClimateClassification koppen, int servings, @Nullable String label)
        {
            this.wine = type;
            this.koppen = koppen;
            this.servings = servings;
            this.label = label;
        }

        public WineOutput(CompoundTag tag)
        {
            if (tag.contains("output", Tag.TAG_COMPOUND))
            {
                final CompoundTag outputTag = tag.getCompound("output");
                wine = WineType.VALUES[outputTag.getInt("wineType")];
                koppen = WineType.KOPPEN_VALUES[outputTag.getInt("climate")];
                servings = outputTag.getInt("servings");
                label = outputTag.contains("label", Tag.TAG_STRING) ? outputTag.getString("label") : null;
            }
        }

        public CompoundTag save(CompoundTag tag)
        {
            final CompoundTag outputTag = new CompoundTag();
            outputTag.putInt("wineType", wine.ordinal());
            outputTag.putInt("climate", koppen.ordinal());
            outputTag.putInt("servings", servings);
            if (label != null)
                outputTag.putString("label", label);
            tag.put("output", outputTag);
            return tag;
        }

        @Nullable
        public String getLabel()
        {
            return label;
        }

        public int getServings()
        {
            return servings;
        }

        public WineType getType()
        {
            return wine;
        }

        public KoppenClimateClassification getClimate()
        {
            return koppen;
        }
    }
}
