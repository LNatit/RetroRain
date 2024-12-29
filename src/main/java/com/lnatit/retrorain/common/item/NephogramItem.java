package com.lnatit.retrorain.common.item;

import com.lnatit.retrorain.RetroRain;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class NephogramItem extends Item {
    public NephogramItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!player.isCreative())
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        if (level.isClientSide()) {
            RetroRain.RetroRainClient.openNephogram(level, player);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
    }
}
