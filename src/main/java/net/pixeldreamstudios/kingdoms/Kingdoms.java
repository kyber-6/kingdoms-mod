package net.pixeldreamstudios.kingdoms;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.pixeldreamstudios.kingdoms.cardinalcomponents.TeamComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Kingdoms implements ModInitializer {
	public static final String MOD_ID = "kingdoms";
	public static final ComponentKey<TeamComponent> TEAM_COMPONENT_COMPONENT_KEY = ComponentRegistry.getOrCreate(new ResourceLocation(MOD_ID, "team"), TeamComponent.class);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			ServerPlayer serverPlayer = handler.getPlayer();
			Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).setTeam(new Random().nextBoolean() ? "Knights of Crimson" : "Lords of Azure");
			serverPlayer.sendSystemMessage(Component.literal("You belong to the \"" + Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).getTeam() + "\" kingdom."));
		}));
	}
}