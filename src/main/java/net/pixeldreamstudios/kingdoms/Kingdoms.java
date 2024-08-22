package net.pixeldreamstudios.kingdoms;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
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
	private static final int MAX_PLAYERS_PER_TEAM = 50;

	@Override
	public void onInitialize() {
		LOGGER.info("And you could have it all, my empire of dirt...");

		KingdomCommands.init();
		registerRespawnHandler();
		serverHandleFirstJoin();
		registerKingDeathListener();
		registerPersistentTeamMembership();
		registerFriendlyFireListener();
	}

	private void serverHandleFirstJoin() {
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			if (TEAM_COMPONENT_COMPONENT_KEY.get(handler.getPlayer()).getTeam() == NO_KINGDOM) {
				// send client packet to open screen
				ServerPlayNetworking.send(handler.getPlayer(), NetworkingConstants.JOIN_KINGDOM_SCREEN_PACKET, PacketByteBufs.empty());
			}
		}));
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
				if (canJoinTeam(team)) {
					addPlayerToTeam(player, team);
					player.setRespawnPosition(player.level().dimension(), new BlockPos(-1467, 99, -1399), 0, true, true);
					player.teleportTo(-1467, 99, -1399);
					server.tell(new TickTask(0, () -> player.displayClientMessage(Component.literal("Joined the Thesium Kingdom"), true)));
				} else {
					player.displayClientMessage(Component.literal("This team is full!"), true);
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
				if (canJoinTeam(team)) {
					addPlayerToTeam(player, team);
					player.setRespawnPosition(player.level().dimension(), new BlockPos(375, 167, 394), 0, true, true);
					player.teleportTo(375, 167, 394);
					server.tell(new TickTask(0, () -> player.displayClientMessage(Component.literal("Joined the Krul'ath Kingdom"), true)));
				} else {
					player.displayClientMessage(Component.literal("This team is full!"), true);
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
	private void registerPersistentTeamMembership() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();
			TeamComponent teamComponent = TEAM_COMPONENT_COMPONENT_KEY.get(player);
			if (teamComponent.getTeam() != NO_KINGDOM) {
				ServerScoreboard scoreboard = server.getScoreboard();
				PlayerTeam team = teamComponent.getTeam() == THESIUM_KINGDOM ? scoreboard.getPlayerTeam("thesium_team") : scoreboard.getPlayerTeam("krulath_team");
				if (team != null) {
					team.getPlayers().add(player.getScoreboardName());
				}
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayer player = handler.getPlayer();
			TeamComponent teamComponent = TEAM_COMPONENT_COMPONENT_KEY.get(player);
		});
	}
	private void registerFriendlyFireListener() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (entity instanceof Player) {
				Player target = (Player) entity;
				if (arePlayersOnSameTeam(player, target)) {
					return InteractionResult.FAIL; // Cancel the attack
				}
			}
			return InteractionResult.PASS;
		});
	}
	private boolean canJoinTeam(PlayerTeam team) {
		return team.getPlayers().size() < MAX_PLAYERS_PER_TEAM;
	}
	private void addPlayerToTeam(Player player, PlayerTeam team) {
		team.getPlayers().add(player.getScoreboardName());
	}
	private boolean arePlayersOnSameTeam(Player attacker, Player target) {
		return attacker.getTeam() != null && attacker.getTeam().equals(target.getTeam());
	}
}
