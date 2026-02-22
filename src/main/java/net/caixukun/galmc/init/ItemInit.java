package net.caixukun.galmc.init;

import net.caixukun.galmc.Galmc_api;
import net.caixukun.galmc.ui.OpenUI;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Galmc_api.MODID);
    public static final RegistryObject<Item> GUI_TEST = ITEMS.register("open_cg", OpenUI::new);
}
