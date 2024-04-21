package com.eerussianguy.firmalife.common.recipes;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltips;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class BowlPotRecipe extends PotRecipe
{
    public static final OutputType OUTPUT_TYPE = nbt -> {
        ItemStack stack = ItemStack.of(nbt.getCompound("item"));
        return new BowlOutput(stack);
    };
    private final ItemStack itemOutput;
    private final FoodData food;

    public BowlPotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, int duration, float minTemp, ItemStack itemOutput, FoodData food)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
        this.itemOutput = FoodCapability.setStackNonDecaying(itemOutput);
        this.food = food;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return itemOutput;
    }

    @Override
    public Output getOutput(PotBlockEntity.PotInventory inv)
    {
        final ItemStack item = itemOutput.copy();
        final IFood cap = Helpers.getCapability(item, FoodCapability.CAPABILITY);
        if (cap instanceof FoodHandler.Dynamic dynamic)
        {
            dynamic.setCreationDate(FoodCapability.getRoundedCreationDate());
            dynamic.setFood(food);
        }
        return new BowlOutput(item);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.BOWL_POT.get();
    }

    public record BowlOutput(ItemStack stack) implements PotRecipe.Output
    {
        @Override
        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        @Override
        public int getFluidColor()
        {
            return TFCFluids.ALPHA_MASK | 0x24b1d1;
        }

        @Override
        public InteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            if (Helpers.isItem(clickedWith.getItem(), TFCTags.Items.SOUP_BOWLS) && !stack.isEmpty())
            {
                // set the internal bowl to the one we clicked with
                stack.getCapability(FoodCapability.CAPABILITY)
                    .filter(food -> food instanceof DynamicBowlHandler)
                    .ifPresent(food -> ((DynamicBowlHandler) food).setBowl(clickedWith.copyWithCount(1)));

                // take the player's bowl, give a soup
                clickedWith.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, stack.split(1));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        @Override
        public void write(CompoundTag nbt)
        {
            nbt.put("item", stack.save(new CompoundTag()));
        }

        @Override
        public OutputType getType()
        {
            return BowlPotRecipe.OUTPUT_TYPE;
        }

        @Override
        public BlockEntityTooltip getTooltip()
        {
            return ((level, blockState, blockPos, blockEntity, tooltip) -> {
                final List<Component> text = new ArrayList<>();
                BlockEntityTooltips.itemWithCount(tooltip, this.stack);
                FoodCapability.addTooltipInfo(this.stack, text);
                text.forEach(tooltip);
            });
        }
    }

    public static class Serializer extends PotRecipe.Serializer<BowlPotRecipe>
    {
        @Override
        protected BowlPotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            return new BowlPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, JsonHelpers.getItemStack(json, "item_output"), FoodData.read(json.getAsJsonObject("food")));
        }

        @Override
        protected BowlPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            return new BowlPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, buffer.readItem(), FoodData.decode(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BowlPotRecipe recipe)
        {
            super.toNetwork(buffer, recipe);
            buffer.writeItem(recipe.itemOutput);
            recipe.food.encode(buffer);
        }
    }
}
