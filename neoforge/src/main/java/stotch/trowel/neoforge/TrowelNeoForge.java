package stotch.trowel.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import stotch.trowel.Trowel;

@Mod(Trowel.MOD_ID)
public final class TrowelNeoForge {

	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Trowel.MOD_ID);

	private static final DeferredHolder<Item, Item> TROWEL = ITEMS.register("trowel", () -> Trowel.TROWEL);


	public TrowelNeoForge(IEventBus modEventBus) {
		ITEMS.register(modEventBus);
		modEventBus.addListener(this::onBuildCreativeTab);
	}

	private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.insertAfter(
				new ItemStack(Items.BRUSH),
				new ItemStack(TROWEL.get()),
				CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
			);
		}
	}
}
