package net.pixeldreamstudios.kingdoms.cardinalcomponents;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.pixeldreamstudios.kingdoms.Kingdoms;

public class Components implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(Kingdoms.TEAM_COMPONENT_COMPONENT_KEY, player -> new KingdomComponent(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
