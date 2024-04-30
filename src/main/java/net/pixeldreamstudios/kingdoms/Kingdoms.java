package net.pixeldreamstudios.kingdoms;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.pixeldreamstudios.kingdoms.cardinalcomponents.TeamComponent;
import net.pixeldreamstudios.kingdoms.command.KingdomCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Kingdoms implements ModInitializer {
	public static final String MOD_ID = "kingdoms";
	public static final ComponentKey<TeamComponent> TEAM_COMPONENT_COMPONENT_KEY = ComponentRegistry.getOrCreate(new ResourceLocation(MOD_ID, "team"), TeamComponent.class);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int NO_TEAM = 0, BLUE_TEAM = 1, RED_TEAM = 2;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		KingdomCommands.init();

//		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
//			ServerPlayer serverPlayer = handler.getPlayer();
//			if (serverPlayer.level().isClientSide) {
//				Minecraft.getInstance().setScreen(new TutorialScreen());
//			}
////			Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).setTeam(new Random().nextBoolean() ? "Knights of Crimson" : "Lords of Azure");
////			serverPlayer.sendSystemMessage(Component.literal("You belong to the \"" + Kingdoms.TEAM_COMPONENT_COMPONENT_KEY.get(serverPlayer).getTeam() + "\" kingdom."));
//		}));
	}
}