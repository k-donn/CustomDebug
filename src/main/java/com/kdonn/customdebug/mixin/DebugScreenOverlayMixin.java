package com.kdonn.customdebug.mixin;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.kdonn.customdebug.CustomdebugClient;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
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
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
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
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.DyeColor;
import static net.minecraft.world.level.block.BeehiveBlock.HONEY_LEVEL;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraft.network.protocol.game.DebugPackets;
@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin extends GuiComponent {
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();

	/**
	 * Add more information into the right-hand menu of the F3 debug screen
	 * 
	 * @param cir		The mixin object to return the strings we get
	 * @param i			Runtime max memory
	 * @param j			Runtime total memory
	 * @param k			Runtime free memory
	 * @param l			Runtime used memory
	 * @param list		The list of strings to render
	 * @param entity	The entity under the crosshair, null if none
	 */
	@Inject(remap = false, method = "getSystemInformation", at = @At(value = "RETURN", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	public void getCustomSystemInformation(CallbackInfoReturnable<List<String>> cir, long i, long j, long k, long l, List<String> list, Entity entity) {
		Minecraft mc = Minecraft.getInstance();

		HitResult hitResult = mc.hitResult;

		LocalPlayer player = mc.player;

		// DebugScreenOverlay currObj = (DebugScreenOverlay)(Object)this;

		if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();

				BlockState blockState = mc.level.getBlockState(blockPos);

				BlockEntity blockEntity = player.level.getBlockEntity(blockPos);

				if (blockState.hasBlockEntity()) {
					if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {

						int pos = list.indexOf(ChatFormatting.UNDERLINE + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());

						boolean alreadyCappedPos = blockPos.getX() == CustomdebugClient.xPos &&
								blockPos.getY() == CustomdebugClient.yPos &&
								blockPos.getZ() == CustomdebugClient.zPos;

						boolean hasBeeInfo = CustomdebugClient.capturedBeeCount != -1 && alreadyCappedPos;

						String prompt = "<RIGHT CLICK TO GET>";

						list.add(pos + 2, "bee count: " + (hasBeeInfo ? CustomdebugClient.capturedBeeCount : prompt));     	
						list.add(pos + 3, "is full: " + (hasBeeInfo ? CustomdebugClient.capturedBeeCount == 3: prompt ));
						list.add(pos + 4, "is empty: " + (hasBeeInfo ? CustomdebugClient.capturedBeeCount == 0 : prompt));
						list.add(pos + 6, "is sedated: " + beehiveBlockEntity.isSedated());
					}
				}
			}
		}



		if (entity != null) {
			list.add("distance: " + (entity.distanceTo(mc.player)));

			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) entity;

				list.add("health: " + (livingEntity.getHealth()) + "/" + (livingEntity.getMaxHealth()));
				list.add("air supply: " + (livingEntity.getAirSupply()) + "/" + (livingEntity.getMaxAirSupply()));
				list.add("jump boost power: " + livingEntity.getJumpBoostPower());

				if (livingEntity instanceof Mob mob) {

					list.add("left handed: " + mob.isLeftHanded());

					if (mob instanceof PathfinderMob pfMob) {

						list.add("speed: " + pfMob.getSpeed());

						if (pfMob instanceof AbstractGolem abGolem) {

							if (abGolem instanceof IronGolem ironGolem) {
								list.add("is player created: " + ironGolem.isPlayerCreated());
							}

							if (abGolem instanceof SnowGolem snowGolem) {
								list.add("has pumpkin: " + snowGolem.hasPumpkin());
							}
						}

						if (pfMob instanceof AgeableMob agMob) {
							list.add("age: " + agMob.getAge());
							list.add("can breed: " + agMob.canBreed());

							if (agMob instanceof AbstractVillager abVillager) {
								list.add("unhappy level:" + abVillager.getUnhappyCounter());
								list.add("inventory size: " + abVillager.getInventory().getContainerSize());

								// Villager and WanderingTrader specific props not implemented
							}

							if (agMob instanceof Animal animal) {
								list.add("in love: " + animal.isInLove());

								if (animal instanceof AbstractHorse abHorse) {
									list.add("temper: " + abHorse.getTemper());
									list.add("is tamed: " + abHorse.isTamed());

									// Horse, SkeletonHorse, ZombieHorse specific props not implemented
									// AbstractChestedHorse, Donkey, Mule, LLama specific props not implemented
								}

								if (animal instanceof Bee bee) {
									list.add("has nectar: " + bee.hasNectar());
								}

								if (animal instanceof Axolotl axolotl) {
									list.add("is playing dead: " + axolotl.isPlayingDead());
								}

								if (animal instanceof Chicken chicken) {
									list.add("egg time: " + chicken.eggTime);
								}

								// Cow not implemented
								// Fox not implemented

								if (animal instanceof Goat goat) {
									list.add("is screaming goat: " + goat.isScreamingGoat());
								}

								// Ocelot not implemented

								if (animal instanceof Panda panda) {
									// This is its own method separate from AbstractVillager unHappyCounter method
									list.add("unhappy counter: " + panda.getUnhappyCounter());
								}

								if (animal instanceof Pig pig) {
									list.add("steering speed: " + pig.getSteeringSpeed());
								}

								// Panda not implemented
								// PolarBear not implemented
								// Rabbit not implemented

								if (animal instanceof Sheep sheep) {
									DyeColor woolColorObj = sheep.getColor();

									list.add("wool color: " + woolColorObj.getName());
								}

								if (animal instanceof Turtle turtle) {
									list.add("has egg: " + turtle.hasEgg());
								}

								// Hoglin not implemented

								if (animal instanceof Strider strider) {
									list.add("move speed: " + strider.getMoveSpeed());
									list.add("steerig speed: " + strider.getSteeringSpeed());
								}

								if (animal instanceof TamableAnimal tmblAnimal) {
									list.add("is tame: " + tmblAnimal.isTame());

									// There isn't an interface to reference so this is only solution to get color
									if (tmblAnimal instanceof Cat cat) {
										DyeColor collarColorObj = cat.getCollarColor();

										list.add("collar color: " + collarColorObj.getName());
									}

									if (tmblAnimal instanceof Wolf wolf) {
										DyeColor collarColorObj = wolf.getCollarColor();

										list.add("collar color: " + collarColorObj.getName());
									}

									if (tmblAnimal instanceof ShoulderRidingEntity shoulderEntity) {
										list.add("can sit on shoulder: " + shoulderEntity.canSitOnShoulder());

										if (shoulderEntity instanceof Parrot parrot) {
											list.add("is partying: " + parrot.isPartyParrot());
											list.add("flap speed: " + parrot.flapSpeed);
										}
									}

								}

							}
						}

						if (pfMob instanceof WaterAnimal wAnimal) {
							if (wAnimal instanceof Dolphin dolphin) {
								list.add("moistness: " + dolphin.getMoistnessLevel());
							}

							if (wAnimal instanceof AbstractFish abFish) {
								list.add("from bucket: " + abFish.fromBucket());

								if (abFish instanceof AbstractSchoolingFish abSchoolingFish) {
									list.add("is follower" + abSchoolingFish.isFollower());
									list.add("max school size" + abSchoolingFish.getMaxSchoolSize());
								}
							}

						}
					}
				}
			}
		}

		cir.setReturnValue(list);
	}
}
