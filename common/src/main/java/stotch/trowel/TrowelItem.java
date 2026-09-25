package stotch.trowel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

public class TrowelItem extends Item {

	private static final ThreadLocalRandom random = ThreadLocalRandom.current();

	public TrowelItem(Properties properties) {
		super(properties.durability(512).stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		Player player = context.getPlayer();
		ItemStack trowelStack = context.getItemInHand();
		BlockPos targetPos = context.getClickedPos().relative(context.getClickedFace());

		if (world.isClientSide || player == null) return InteractionResult.PASS;
		if (!world.getBlockState(targetPos).isAir()) return InteractionResult.FAIL;

		List<ItemStack> placeableBlocks = getPlaceableBlocksFromHotbar(player, targetPos, context);
		if (placeableBlocks.isEmpty()) return InteractionResult.FAIL;

		return place(placeableBlocks, player, context, world, targetPos, trowelStack);
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return repairCandidate.is(Items.IRON_INGOT) || super.isValidRepairItem(stack, repairCandidate);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
		tooltip.add(Component.translatable("itemTooltip.trowel.trowelTooltip").withStyle(ChatFormatting.GRAY));
	}

	private InteractionResult place(
		List<ItemStack> placeableBlocks,
		Player player,
		UseOnContext context,
		Level world,
		BlockPos targetPos,
		ItemStack trowelStack
	) {
		ItemStack blockStack = placeableBlocks.get(random.nextInt(placeableBlocks.size()));
		BlockPlaceContext placementContext = createPlacementContext(player, blockStack, targetPos, context);

		if (canPlaceBlock(placementContext, (BlockItem) blockStack.getItem())) {
			BlockState blockState = ((BlockItem) blockStack.getItem())
				.getBlock()
				.getStateForPlacement(placementContext);

			Direction facing = context.getClickedFace();
			if (blockState.getBlock() instanceof RotatedPillarBlock) {
				blockState = blockState.setValue(RotatedPillarBlock.AXIS, facing.getAxis());
			}

			InteractionResult result = blockStack.useOn(placementContext);
			if (result.consumesAction()) {
				if (!player.isCreative()) {
					trowelStack.hurtAndBreak(
						1,
						player,
						context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
					);
				}
				player.swing(context.getHand(), true);
				world.playSound(
					null,
					targetPos,
					blockState.getSoundType().getPlaceSound(),
					SoundSource.BLOCKS,
					1.0F,
					1.0F
				);
				return result;
			}
		}

		placeableBlocks.remove(blockStack);
		if (!placeableBlocks.isEmpty()) return place(placeableBlocks, player, context, world, targetPos, trowelStack);
		return InteractionResult.FAIL;
	}

	private List<ItemStack> getPlaceableBlocksFromHotbar(Player player, BlockPos targetPos, UseOnContext context) {
		List<ItemStack> placeableBlocks = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			Item item = stack.getItem();
			if (item instanceof BlockItem && !(item instanceof AirItem)) {
				placeableBlocks.add(stack);
			}
		}
		return placeableBlocks;
	}

	private boolean canPlaceBlock(BlockPlaceContext context, BlockItem blockItem) {
		BlockState blockState = blockItem.getBlock().getStateForPlacement(context);
		if (blockState == null) return false;

		CollisionContext shapeContext =
			context.getPlayer() == null ? CollisionContext.empty() : CollisionContext.of(context.getPlayer());

		return (
			context.canPlace() &&
			blockState.canSurvive(context.getLevel(), context.getClickedPos()) &&
			context.getLevel().isUnobstructed(blockState, context.getClickedPos(), shapeContext)
		);
	}

	private BlockPlaceContext createPlacementContext(
		Player player,
		ItemStack blockStack,
		BlockPos targetPos,
		UseOnContext context
	) {
		return new BlockPlaceContext(
			player,
			context.getHand(),
			blockStack,
			new BlockHitResult(
				context.getClickLocation(),
				context.getClickedFace(),
				context.getClickedPos(),
				context.isInside()
			)
		);
	}
}
