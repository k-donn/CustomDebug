package com.kdonn.customdebug;

import static net.minecraft.world.level.block.BeehiveBlock.HONEY_LEVEL;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BeehiveClick {
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();

	@SubscribeEvent
	public void onUseStick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		BlockPos blockPos = event.getPos();
        Player playerEntity = event.getEntity();
        BlockState blockState = level.getBlockState(blockPos);
        
		 if (!level.isClientSide()) {
			 LOGGER.debug("click event");
			 
			 Item item = event.getItemStack().getItem();
			 
			 if (item != Items.SHEARS && item != Items.GLASS_BOTTLE) {
				 ListTag listTag = null;
				 BlockEntity blockEntity = level.getBlockEntity(blockPos);
				 
				 if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
					 listTag = beehiveBlockEntity.writeBees();
				 } else {
					 return;
				 }
				 
				 int honey = 0;
				 if (blockState.hasProperty(HONEY_LEVEL)) {
					 honey = blockState.getValue(HONEY_LEVEL);
				 }
				 
				 CustomdebugServer.sendBeehiveInfo(playerEntity, blockPos, honey, listTag);
				 
				 event.setResult(Event.Result.ALLOW);
			 }
		 }
	}
}
