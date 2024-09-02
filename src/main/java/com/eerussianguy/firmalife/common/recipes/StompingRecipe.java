package com.eerussianguy.firmalife.common.recipes;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeSerializerImpl;
import net.dries007.tfc.common.recipes.SimpleItemRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class StompingRecipe extends SimpleItemRecipe
{
    public static final IndirectHashCollection<Item, StompingRecipe> CACHE = IndirectHashCollection.createForRecipe(StompingRecipe::getValidItems, FLRecipeTypes.STOMPING);

    private final ResourceLocation inputTexture;
    private final ResourceLocation outputTexture;
    private final SoundEvent sound;

    public StompingRecipe(ResourceLocation id, Ingredient ingredient, ItemStackProvider result, ResourceLocation inputTexture, ResourceLocation outputTexture, SoundEvent sound)
    {
        super(id, ingredient, result);
        this.inputTexture = inputTexture;
        this.outputTexture = outputTexture;
        this.sound = sound;
    }

    @Nullable
    public static StompingRecipe getRecipe(Level level, ItemStackInventory wrapper)
    {
        for (StompingRecipe recipe : CACHE.getAll(wrapper.getStack().getItem()))
        {
            if (recipe.matches(wrapper, level))
            {
                return recipe;
            }
        }
        return null;
    }

    public ResourceLocation getInputTexture()
    {
        return inputTexture;
    }

    public ResourceLocation getOutputTexture()
    {
        return outputTexture;
    }

    public SoundEvent getSound()
    {
        return sound;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.STOMPING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.STOMPING.get();
    }

    public static class StompingSerializer extends RecipeSerializerImpl<StompingRecipe>
    {
        @Override
        public StompingRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            final Ingredient ingredient = Ingredient.fromJson(JsonHelpers.get(json, "ingredient"));
            final ItemStackProvider result = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
            final ResourceLocation preTexture = FLHelpers.res(JsonHelpers.getAsString(json, "input_texture"));
            final ResourceLocation outTexture = FLHelpers.res(JsonHelpers.getAsString(json, "output_texture"));
            final SoundEvent sound = JsonHelpers.getRegistryEntry(json, "sound", ForgeRegistries.SOUND_EVENTS);
            return new StompingRecipe(id, ingredient, result, preTexture, outTexture, sound);
        }

        @Override
        public @Nullable StompingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final Ingredient ingredient = Ingredient.fromNetwork(buffer);
            final ItemStackProvider result = ItemStackProvider.fromNetwork(buffer);
            final ResourceLocation pre = buffer.readResourceLocation();
            final ResourceLocation post = buffer.readResourceLocation();
            final SoundEvent sound = buffer.readRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS);
            return new StompingRecipe(id, ingredient, result, pre, post, sound);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StompingRecipe recipe)
        {
            recipe.getIngredient().toNetwork(buffer);
            recipe.getResult().toNetwork(buffer);
            buffer.writeResourceLocation(recipe.inputTexture);
            buffer.writeResourceLocation(recipe.outputTexture);
            buffer.writeRegistryIdUnsafe(ForgeRegistries.SOUND_EVENTS, recipe.sound);
        }
    }
}
