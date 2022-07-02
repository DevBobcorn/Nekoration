package com.devbobcorn.nekoration.utils;

import java.util.Random;

import com.devbobcorn.nekoration.Nekoration;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import vazkii.patchouli.api.PatchouliAPI;

public class TradeHelper {
    public static class BrochureForEmeralds implements VillagerTrades.ItemListing {
        private final int villagerXp;

        public BrochureForEmeralds(int xp) {
            this.villagerXp = xp;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack itemstack = PatchouliAPI.get().getBookStack(new ResourceLocation(Nekoration.MODID, "neko_brochure"));

            int j = random.nextInt(3) + 6;

            return new MerchantOffer(new ItemStack(Items.EMERALD, j), new ItemStack(Items.BOOK), itemstack, 12, this.villagerXp, 0.2F);
        }
    }
}
