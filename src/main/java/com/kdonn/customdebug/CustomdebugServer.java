package com.kdonn.customdebug;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

public class CustomdebugServer {
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();
	
	// Define mod id in a common place for everything to reference
	public static final String MODID = "customdebug";
	
    public static final ResourceLocation S2CPacketIdentifier = new ResourceLocation(MODID, "s2c");

    public static void sendBeehiveInfo(Player player, BlockPos blockPos, int honeyLevel, ListTag bees) {
    	LOGGER.debug("send hive info");
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        
        buf.writeInt(honeyLevel);

        if (bees == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(bees.size());
            for (int i=0; i<bees.size(); i++) {
                CompoundTag tag = bees.getCompound(i).getCompound("EntityData");
                if (tag != null && tag.contains("CustomName", 8)) {
                    String beeName = tag.getString("CustomName");
                    buf.writeUtf(beeName);
                } else {
                    buf.writeUtf("");
                }
            }
        }
        ((ServerPlayer)player).connection.getConnection().send(
            NetworkDirection.PLAY_TO_CLIENT.buildPacket(Pair.of(buf, buf.writerIndex()), S2CPacketIdentifier).getThis()
        );
    }
}
