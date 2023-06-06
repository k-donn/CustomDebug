package com.kdonn.customdebug;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("customdebug")
public class Main {
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();

	// Define mod id in a common place for everything to reference
	public static final String MODID = "customdebug";

	// Create a Deferred Register to hold Blocks which will all be registered under the "customdebug" namespace
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	// Create a Deferred Register to hold Items which will all be registered under the "customdebug" namespace
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	
	public static final ResourceLocation C2SPacketIdentifier = new ResourceLocation(MODID, "c2s");    
    public static final ResourceLocation S2CPacketIdentifier = new ResourceLocation(MODID, "s2c");
    
    EventNetworkChannel channel;

	public Main() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		channel = NetworkRegistry.newEventChannel(
	            S2CPacketIdentifier,
	            () -> "",
	            (a) -> true,
	            (a) -> true
	        );
		
		if (FMLEnvironment.dist.isClient()) {
			channel.addListener(new CustomdebugClient());
		}
		MinecraftForge.EVENT_BUS.register(new BeehiveClick());
	}


	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			// Some client setup code
			LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
		}
	}
	
}
