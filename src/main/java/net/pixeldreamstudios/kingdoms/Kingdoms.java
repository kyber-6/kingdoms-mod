package net.pixeldreamstudios.kingdoms;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.PlayerTeam;
import net.pixeldreamstudios.kingdoms.cardinalcomponents.TeamComponent;
import net.pixeldreamstudios.kingdoms.command.KingdomCommands;
import net.pixeldreamstudios.kingdoms.networking.NetworkingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kingdoms implements ModInitializer {
	public static final String MOD_ID = "kingdoms";
	public static final ComponentKey<TeamComponent> TEAM_COMPONENT_COMPONENT_KEY = ComponentRegistry.getOrCreate(new ResourceLocation(MOD_ID, "team"), TeamComponent.class);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int NO_KINGDOM = 0, THESIUM_KINGDOM = 1, KRULATH_KINGDOM = 2;
	private static final int MAX_PLAYERS_PER_TEAM = 50; // Maximum number of players per team

	@Override
	public void onInitialize() {
		LOGGER.info("And you could have it all, my empire of dirt...");

		KingdomCommands.init();
		registerRespawnHandler();
		serverHandleFirstJoin();
		registerKingDeathListener();
		registerFriendlyFireListener(); // Register the friendly fire listener
		registerBedInteractionListener(); // Register the bed interaction listener
	}

	private void serverHandleFirstJoin() {
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			if (TEAM_COMPONENT_COMPONENT_KEY.get(handler.getPlayer()).getTeam() == NO_KINGDOM) {
				// send client packet to open screen
				ServerPlayNetworking.send(handler.getPlayer(), NetworkingConstants.JOIN_KINGDOM_SCREEN_PACKET, PacketByteBufs.empty());
			}
		}));

		// receive client packet after the player has selected kingdom
		ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.JOINED_THESIUM_PACKET, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				TEAM_COMPONENT_COMPONENT_KEY.get(player).setTeam(THESIUM_KINGDOM);

				ServerScoreboard scoreboard = server.getScoreboard();
				PlayerTeam team = scoreboard.getPlayerTeam("thesium_team");

				if (team == null) {
					team = scoreboard.addPlayerTeam("thesium_team");
					team.setDisplayName(Component.literal("Thesium Kingdom"));
					team.setColor(ChatFormatting.DARK_PURPLE);
				}

				if (team.getPlayers().size() < MAX_PLAYERS_PER_TEAM) {
					scoreboard.addPlayerToTeam(player.getScoreboardName(), team);

					// Delay the teleportation to the next tick
					server.execute(() -> {
						BlockPos respawnPos = new BlockPos(-1467, 99, -1399);
						player.setRespawnPosition(player.level().dimension(), respawnPos, 0, true, true);
						player.teleportTo(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
						player.displayClientMessage(Component.literal("Joined the Thesium Kingdom"), true);
						LOGGER.info("Player {} successfully teleported to {}", player.getName().getString(), respawnPos);
					});
				} else {
					player.displayClientMessage(Component.literal("This team is full!").withStyle(ChatFormatting.RED), true);
				}
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.JOINED_KRULATH_PACKET, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				TEAM_COMPONENT_COMPONENT_KEY.get(player).setTeam(KRULATH_KINGDOM);

				ServerScoreboard scoreboard = server.getScoreboard();
				PlayerTeam team = scoreboard.getPlayerTeam("krulath_team");

				if (team == null) {
					team = scoreboard.addPlayerTeam("krulath_team");
					team.setDisplayName(Component.literal("Krul'ath Kingdom"));
					team.setColor(ChatFormatting.GOLD);
				}

				if (team.getPlayers().size() < MAX_PLAYERS_PER_TEAM) {
					scoreboard.addPlayerToTeam(player.getScoreboardName(), team);

					// Delay the teleportation to the next tick
					server.execute(() -> {
						BlockPos respawnPos = new BlockPos(375, 167, 394);
						player.setRespawnPosition(player.level().dimension(), respawnPos, 0, true, true);
						player.teleportTo(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
						player.displayClientMessage(Component.literal("Joined the Krul'ath Kingdom"), true);
						LOGGER.info("Player {} successfully teleported to {}", player.getName().getString(), respawnPos);
					});
				} else {
					player.displayClientMessage(Component.literal("This team is full!").withStyle(ChatFormatting.RED), true);
				}
			});
		});
	}

	private void registerRespawnHandler() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			TeamComponent teamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(newPlayer);

			// Enforce Spectator mode if the team is not allowed to respawn
			if (!teamComponent.isTeamRespawnAllowed()) {
				newPlayer.setGameMode(GameType.SPECTATOR);
				newPlayer.sendSystemMessage(Component.literal("Your king is dead. You can no longer respawn!").withStyle(ChatFormatting.RED));
			}

			// Force set the respawn position based on the team, overriding any bed interactions
			BlockPos respawnPos;
			if (teamComponent.getTeam() == THESIUM_KINGDOM) {
				respawnPos = new BlockPos(-1467, 99, -1399);
			} else if (teamComponent.getTeam() == KRULATH_KINGDOM) {
				respawnPos = new BlockPos(375, 167, 394);
			} else {
				respawnPos = null;
			}

			if (respawnPos != null) {
				newPlayer.setRespawnPosition(newPlayer.level().dimension(), respawnPos, 0, true, true);
				newPlayer.teleportTo(respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
				LOGGER.info("Player {} respawned and teleported to {}", newPlayer.getName().getString(), respawnPos);
			}
		});
	}

	private void registerKingDeathListener() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			TeamComponent teamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(oldPlayer);

			if (teamComponent.isKing()) {
				// Ensure the king is placed into Spectator mode
				newPlayer.setGameMode(GameType.SPECTATOR);
				newPlayer.sendSystemMessage(Component.literal("As the king, you can no longer respawn!").withStyle(ChatFormatting.RED));

				// Mark the entire team as unable to respawn, meaning they now have only one life
				oldPlayer.getServer().getPlayerList().getPlayers().forEach(player -> {
					TeamComponent playerTeamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(player);
					if (playerTeamComponent.getTeam() == teamComponent.getTeam()) {
						playerTeamComponent.setTeamRespawnAllowed(false);
					}
				});

				// Broadcast to the server that the king has fallen
				Component deathMessage = Component.literal("The King of the ")
						.append(teamComponent.getTeam() == Kingdoms.THESIUM_KINGDOM ? "Thesium" : "Krul'ath")
						.append(" Kingdom has fallen! Members of this kingdom now only have one life left.")
						.withStyle(ChatFormatting.RED);

				oldPlayer.getServer().getPlayerList().broadcastSystemMessage(deathMessage, false);
			}
		});
	}

	private void registerFriendlyFireListener() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (entity instanceof Player) {
				Player target = (Player) entity;
				if (arePlayersOnSameTeam((Player) player, target)) {
					return InteractionResult.FAIL; // Cancel the attack if on the same team
				}
			}
			return InteractionResult.PASS; // Allow the attack if not on the same team
		});
	}

	private void registerBedInteractionListener() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			BlockState blockState = world.getBlockState(hitResult.getBlockPos());

			// Check if the block is any type of bed
			if (blockState.is(Blocks.WHITE_BED) ||
					blockState.is(Blocks.ORANGE_BED) ||
					blockState.is(Blocks.MAGENTA_BED) ||
					blockState.is(Blocks.LIGHT_BLUE_BED) ||
					blockState.is(Blocks.YELLOW_BED) ||
					blockState.is(Blocks.LIME_BED) ||
					blockState.is(Blocks.PINK_BED) ||
					blockState.is(Blocks.GRAY_BED) ||
					blockState.is(Blocks.LIGHT_GRAY_BED) ||
					blockState.is(Blocks.CYAN_BED) ||
					blockState.is(Blocks.PURPLE_BED) ||
					blockState.is(Blocks.BLUE_BED) ||
					blockState.is(Blocks.BROWN_BED) ||
					blockState.is(Blocks.GREEN_BED) ||
					blockState.is(Blocks.RED_BED) ||
					blockState.is(Blocks.BLACK_BED)) {

				player.sendSystemMessage(Component.literal("Setting spawn with beds is disabled in this world!").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL; // Cancel the bed interaction
			}

			return InteractionResult.PASS;
		});
	}

	private boolean arePlayersOnSameTeam(Player player1, Player player2) {
		return player1.getTeam() != null && player1.getTeam().equals(player2.getTeam());
	}
}
