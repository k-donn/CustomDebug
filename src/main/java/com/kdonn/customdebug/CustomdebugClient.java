package com.kdonn.customdebug;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CustomdebugClient implements Consumer<NetworkEvent> {
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static int capturedBeeCount = -1;
	public static int xPos = 0;
	public static int yPos = 0;
	public static int zPos = 0;
	

	@Override
	public void accept(NetworkEvent event) {
		LOGGER.debug("accepted comms");
		NetworkEvent.Context context = event.getSource().get();
        if (event.getPayload() != null) {
            context.enqueueWork(() -> {
                gotHiveInfo(null, event.getPayload());
            });
        }
        context.setPacketHandled(true);
	}
	
	private void gotHiveInfo(Object context, FriendlyByteBuf buffer) {
		LOGGER.debug("received hive");
		int honeyLevel, beeCount, currX, currY, currZ;
		LocalPlayer player = Minecraft.getInstance().player;
        List<String> beeNames = new ArrayList<>();
        
        currX = buffer.readInt();
        currY = buffer.readInt();
        currZ = buffer.readInt();
        
        honeyLevel = buffer.readInt();
        beeCount = buffer.readInt();

        for (int i=0; i<beeCount; i++) {
            String beeName=buffer.readUtf();
            beeNames.add(beeName);
        }
        
        capturedBeeCount = beeCount;
        xPos = currX;
        yPos = currY;
        zPos = currZ;
    }

}
