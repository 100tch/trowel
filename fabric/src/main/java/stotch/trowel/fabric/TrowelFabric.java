package stotch.trowel.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import stotch.trowel.Trowel;

public final class TrowelFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), Trowel.id("trowel"));
		Registry.register(BuiltInRegistries.ITEM, key, Trowel.TROWEL);

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
			content.addAfter(Items.BRUSH, Trowel.TROWEL);
		});
	}
}
