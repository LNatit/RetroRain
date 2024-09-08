package com.lnatit.retrorain.common.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.lnatit.retrorain.RetroRain.MOD_ID;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<NephogramItem> NEPHOGRAM = ITEMS.register("nephogram", NephogramItem::new);
}
