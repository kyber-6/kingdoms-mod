package net.pixeldreamstudios.kingdoms;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.pixeldreamstudios.kingdoms.networking.NetworkingConstants;
import net.pixeldreamstudios.kingdoms.screen.JoinKingdomScreen;

public class KingdomsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        clientHandleFirstJoin();
    }

    private void clientHandleFirstJoin() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.JOIN_KINGDOM_SCREEN_PACKET, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                client.setScreen(new JoinKingdomScreen());
            });
        });
    }
}
