package com.kdonn.customdebug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import com.mojang.datafixers.optics.Wander;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("customdebug")
public class Main {
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public Main() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		// some preinit code
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// do something when the server starts
	}

	// You can use EventBusSubscriber to automatically subscribe events on the
	// contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// register a new block here
			LOGGER.info("HELLO from Register Block");
		}
	}

	@SubscribeEvent
	public void renderOverlayEvent(RenderGameOverlayEvent.Text event) {
		ArrayList<String> right = event.getRight();

		Minecraft mc = Minecraft.getInstance();

		if (mc.crosshairPickEntity != null) {
			Entity entity = mc.crosshairPickEntity;
			BlockPos pos = entity.blockPosition();

			right.add("distance: " + (entity.distanceTo(mc.player)));

			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) entity;

				right.add("health: " + (livingEntity.getHealth()) + "/" + (livingEntity.getMaxHealth()));
				right.add("air supply: " + (livingEntity.getAirSupply()) + "/" + (livingEntity.getMaxAirSupply()));
				right.add("jump boost power: " + livingEntity.getJumpBoostPower());

				if (livingEntity instanceof Mob) {
					Mob mob = (Mob) livingEntity;

					right.add("left handed: " + mob.isLeftHanded());

					if (mob instanceof PathfinderMob) {
						PathfinderMob pfMob = (PathfinderMob) mob;

						right.add("speed: " + pfMob.getSpeed());

						if (pfMob instanceof AbstractGolem) {
							AbstractGolem abGolem = (AbstractGolem)mob;

							if (abGolem instanceof IronGolem) {
								IronGolem ironGolem = (IronGolem)abGolem;

								right.add("is player created: " + ironGolem.isPlayerCreated());
							}

							if (abGolem instanceof SnowGolem) {
								SnowGolem snowGolem = (SnowGolem)abGolem;

								right.add("has pumpkin: " + snowGolem.hasPumpkin());
							}
						}

						if (pfMob instanceof AgeableMob) {
							AgeableMob agMob = (AgeableMob) pfMob;

							right.add("age: " + agMob.getAge());
							right.add("can breed: " + agMob.canBreed());

							if (agMob instanceof AbstractVillager) {
								AbstractVillager abVillager = (AbstractVillager) agMob;

								right.add("unhappy level:" + abVillager.getUnhappyCounter());
								right.add("inventory size: " + abVillager.getInventory().getContainerSize());

								// Villager and WanderingTrader specific props not implemented
							}

							if (agMob instanceof Animal) {
								Animal animal = (Animal) agMob;

								right.add("in love: " + animal.isInLove());

								if (animal instanceof AbstractHorse) {
									AbstractHorse abHorse = (AbstractHorse) animal;

									right.add("temper: " + abHorse.getTemper());
									right.add("is tamed: " + abHorse.isTamed());

									// Horse, SkeletonHorse, ZombieHorse specific props not implemented
									// AbstractChestedHorse, Donkey, Mule, LLama specific props not implemented
								}

								if (animal instanceof Bee) {
									Bee bee = (Bee) animal;

									right.add("has nectar: " + bee.hasNectar());
								}

								if (animal instanceof Axolotl) {
									Axolotl axolotl = (Axolotl) animal;

									right.add("is playing dead: " + axolotl.isPlayingDead());
								}

								if (animal instanceof Chicken) {
									Chicken chicken = (Chicken) animal;

									right.add("egg time: " + chicken.eggTime);
								}

								// Cow not implemented
								// Fox not implemented

								if (animal instanceof Goat) {
									Goat goat = (Goat) animal;

									right.add("is screaming goat: " + goat.isScreamingGoat());
								}

								// Ocelot not implemented

								if (animal instanceof Panda) {
									Panda panda = (Panda) animal;

									// This is its own method separate from AbstractVillager unHappyCounter method
									right.add("unhappy counter: " + panda.getUnhappyCounter());
								}

								if (animal instanceof Pig) {
									Pig pig = (Pig) animal;

									right.add("steering speed: " + pig.getSteeringSpeed());
								}

								// Panda not implemented
								// PolarBear not implemented
								// Rabbit not implemented

								if (animal instanceof Sheep) {
									Sheep sheep = (Sheep) animal;

									DyeColor woolColorObj = sheep.getColor();

									right.add("wool color: " + woolColorObj.getName());
								}

								if (animal instanceof Turtle) {
									Turtle turtle = (Turtle)animal;

									right.add("has egg: " + turtle.hasEgg());
								}

								// Hoglin not implemented

								if (animal instanceof Strider) {
									Strider strider = (Strider) animal;

									right.add("move speed: " + strider.getMoveSpeed());
									right.add("steerig speed: " + strider.getSteeringSpeed());
								}

								if (animal instanceof TamableAnimal) {
									TamableAnimal tmblAnimal = (TamableAnimal) animal;

									right.add("is tame: " + tmblAnimal.isTame());

									// There isn't an interface to reference so this is only solution to get color
									if (tmblAnimal instanceof Cat) {
										Cat cat = (Cat) tmblAnimal;

										DyeColor collarColorObj = cat.getCollarColor();

										right.add("collar color: " + collarColorObj.getName());
									}

									if (tmblAnimal instanceof Wolf) {
										Wolf wolf = (Wolf) tmblAnimal;

										DyeColor collarColorObj = wolf.getCollarColor();

										right.add("collar color: " + collarColorObj.getName());
									}

									if (tmblAnimal instanceof ShoulderRidingEntity) {
										ShoulderRidingEntity shoulderEntity = (ShoulderRidingEntity) tmblAnimal;

										right.add("can sit on shoulder: " + shoulderEntity.canSitOnShoulder());

										if (shoulderEntity instanceof Parrot) {
											Parrot parrot = (Parrot) shoulderEntity;

											right.add("is partying: " + parrot.isPartyParrot());
											right.add("flap speed: " + parrot.flapSpeed);
										}
									}

								}

							}
						}
					
						if (pfMob instanceof WaterAnimal) {
							WaterAnimal wAnimal = (WaterAnimal)pfMob;

							if (wAnimal instanceof AbstractFish) {
								AbstractFish abFish = (AbstractFish)wAnimal;

								right.add("from bucket: " + abFish.fromBucket());

								if (abFish instanceof AbstractSchoolingFish) {
									AbstractSchoolingFish abSchoolingFish = (AbstractSchoolingFish)abFish;

									right.add("is follower" + abSchoolingFish.isFollower());
									right.add("max school size" + abSchoolingFish.getMaxSchoolSize());
								}
							}
							
							if (wAnimal instanceof Dolphin) {
								Dolphin dolphin = (Dolphin) wAnimal;

								right.add("moistness: " + dolphin.getMoistnessLevel());
							}

							
						}
					}
				}
			}
		}
	}
}
