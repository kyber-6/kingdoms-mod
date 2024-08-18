package net.pixeldreamstudios.kingdoms;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
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

		serverHandleFirstJoin();
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
				player.getTabListDisplayName().getStyle().applyFormat(ChatFormatting.DARK_PURPLE);
				player.setRespawnPosition(player.level().dimension(), new BlockPos(-1467, 99, -1399), 0 ,true, true);
				player.teleportTo(-1467, 99, -1399);
				server.tell(new TickTask(0, () -> player.displayClientMessage(Component.literal("Joined the Thesium Kingdom"), true)));
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.JOINED_KRULATH_PACKET, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				TEAM_COMPONENT_COMPONENT_KEY.get(player).setTeam(KRULATH_KINGDOM);
				player.getTabListDisplayName().getStyle().applyFormat(ChatFormatting.GOLD);
				player.setRespawnPosition(player.level().dimension(), new BlockPos(375, 167, 394), 0 ,true, true);
				player.teleportTo(375, 167, 394);
				server.tell(new TickTask(0, () -> player.displayClientMessage(Component.literal("Joined the Krul'ath Kingdom"), true)));
			});
		});
	}
}