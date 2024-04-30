package net.pixeldreamstudios.kingdoms.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.pixeldreamstudios.kingdoms.Kingdoms;
import net.pixeldreamstudios.kingdoms.networking.NetworkingConstants;

@Environment(EnvType.CLIENT)
public class JoinKingdomScreen extends Screen {
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Kingdoms.MOD_ID, "textures/gui/kingdom.png");

    public JoinKingdomScreen() {
        // The parameter is the title of the screen,
        // which will be narrated when you enter the screen.
        super(Component.literal("Join Kingdom Screen"));
    }

    public Button button1;
    public Button button2;

    @Override
    protected void init() {
        super.init();
        button1 = Button.builder(Component.translatable("team."+ Kingdoms.MOD_ID +  ".thesium"), button -> {
                    ClientPlayNetworking.send(NetworkingConstants.JOINED_THESIUM_PACKET, PacketByteBufs.empty());
                    this.onClose();
                })
                .bounds(width / 2 - 205, height / 2, 200, 20)
                .tooltip(Tooltip.create(Component.translatable("join." + Kingdoms.MOD_ID + ".thesium")))
                .build();
        button2 = Button.builder(Component.translatable("team."+ Kingdoms.MOD_ID +  ".krulath"), button -> {
                    ClientPlayNetworking.send(NetworkingConstants.JOINED_KRULATH_PACKET, PacketByteBufs.empty());
                    this.onClose();
                })
                .bounds(width / 2 + 5, height /2 , 200, 20)
                .tooltip(Tooltip.create(Component.translatable("join." + Kingdoms.MOD_ID + ".krulath")))
                .build();

        addRenderableWidget(button1);
        addRenderableWidget(button2);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
