package net.pixeldreamstudios.kingdoms.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.pixeldreamstudios.kingdoms.Kingdoms;

@Environment(EnvType.CLIENT)
public class JoinKingdomScreen extends Screen {
    public JoinKingdomScreen() {
        // The parameter is the title of the screen,
        // which will be narrated when you enter the screen.
        super(Component.literal("My tutorial screen"));
    }

    public Button button1;
    public Button button2;

    @Override
    protected void init() {
        super.init();
        button1 = Button.builder(Component.translatable("team."+ Kingdoms.MOD_ID +  ".blue"), button -> {
                    System.out.println("You clicked button1!");
                })
                .bounds(width / 2 - 205, height / 2, 200, 20)
                .tooltip(Tooltip.create(Component.translatable("join." + Kingdoms.MOD_ID + ".blue")))
                .build();
        button2 = Button.builder(Component.translatable("team."+ Kingdoms.MOD_ID +  ".red"), button -> {
                    System.out.println("You clicked button2!");
                })
                .bounds(width / 2 + 5, height /2 , 200, 20)
                .tooltip(Tooltip.create(Component.translatable("join." + Kingdoms.MOD_ID + ".red")))
                .build();

        addRenderableWidget(button1);
        addRenderableWidget(button2);
    }
}
