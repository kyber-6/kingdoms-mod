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
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.level.GameType;
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

	@Override
	public void onInitialize() {
		LOGGER.info("And you could have it all, my empire of dirt...");

		KingdomCommands.init();
		registerRespawnHandler();
		serverHandleFirstJoin();
		registerKingDeathListener();
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

				scoreboard.addPlayerToTeam(player.getScoreboardName(), team);

				player.setRespawnPosition(player.level().dimension(), new BlockPos(-1467, 99, -1399), 0, true, true);
				player.teleportTo(-1467, 99, -1399);
				server.tell(new TickTask(0, () -> player.displayClientMessage(Component.literal("Joined the Thesium Kingdom"), true)));
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

				scoreboard.addPlayerToTeam(player.getScoreboardName(), team);

				player.setRespawnPosition(player.level().dimension(), new BlockPos(375, 167, 394), 0, true, true);
				player.teleportTo(375, 167, 394);
				server.tell(new TickTask(0, () -> player.displayClientMessage(Component.literal("Joined the Krul'ath Kingdom"), true)));
			});
		});
	}
	private void registerKingDeathListener() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			TeamComponent teamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(oldPlayer);

			if (teamComponent.isKing()) {
				teamComponent.setTeamRespawnAllowed(false);

				Component deathMessage = Component.literal("The King of the ")
						.append(teamComponent.getTeam() == Kingdoms.THESIUM_KINGDOM ? "Thesium" : "Krul'ath")
						.append(" Kingdom has fallen! Members of this kingdom, including the king, can no longer respawn.")
						.withStyle(ChatFormatting.RED);

				oldPlayer.getServer().getPlayerList().broadcastSystemMessage(deathMessage, false);

				newPlayer.setGameMode(GameType.SPECTATOR);
				newPlayer.sendSystemMessage(Component.literal("As the king, you can no longer respawn!").withStyle(ChatFormatting.RED));
			}
		});
	}
	private void registerRespawnHandler() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			TeamComponent teamComponent = Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(newPlayer);

			if (!teamComponent.isTeamRespawnAllowed()) {
				newPlayer.setGameMode(GameType.SPECTATOR);
				newPlayer.sendSystemMessage(Component.literal("Your king is dead. You can no longer respawn!").withStyle(ChatFormatting.RED));
			}
		});
	}

}
