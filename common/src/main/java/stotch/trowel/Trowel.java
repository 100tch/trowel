package stotch.trowel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class Trowel {

	public static final String MOD_ID = "trowel";

	public static final Item TROWEL = new TrowelItem(new Item.Properties());

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
