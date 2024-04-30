//package net.pixeldreamstudios.kingdoms.mixin;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.entity.EntityRenderer;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.entity.Entity;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//
//@Mixin(EntityRenderer.class)
//public class PlayerNameTagRendererMixin<T extends Entity> {
//    PlayerNameTagRendererMixin() {}
//
//    @Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V`", at = @At("HEAD"))
//    protected void renderNameTag(T entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
//        Component oldComponent = component;
//        component = Component.literal("KOKOS" + oldComponent.getString());
//    }
//}
