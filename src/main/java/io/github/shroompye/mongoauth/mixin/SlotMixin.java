package io.github.shroompye.mongoauth.mixin;

import io.github.shroompye.mongoauth.MongoAuth;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Inject(method = "canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void canTakeItems(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!MongoAuth.playerCache.getOrCreate(player.getUuid()).authenticated()) {
            ((ServerPlayerEntity)player).networkHandler.sendPacket(
                    new ScreenHandlerSlotUpdateS2CPacket(
                            -2,
                            ((PlayerEntityAccessor)player).getInventory().selectedSlot,
                            ((PlayerEntityAccessor)player).getInventory().getStack(((PlayerEntityAccessor)player).getInventory().selectedSlot))
            );
            //((ServerPlayerEntity)player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, ((PlayerEntityAccessor)player).getInventory().getStack(-1)));
            cir.setReturnValue(false);
        }
    }
}