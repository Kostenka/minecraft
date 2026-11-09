package tw.kinmen.directionhelper.mixin.client;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Unique
    private boolean directionhelper$fakedSneak = false;

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void directionhelper$beforeUse(LocalPlayer player, InteractionHand hand, BlockHitResult hit,
                                          CallbackInfoReturnable<InteractionResult> cir) {
        directionhelper$fakedSneak = false;
        if (player.isShiftKeyDown()) return;
        if (player.getItemInHand(hand).isEmpty()) return;
        if (!(player.level().getBlockState(hit.getBlockPos()).getBlock() instanceof NoteBlock)) return;

        Input in = player.input.keyPresses;
        player.connection.send(new ServerboundPlayerInputPacket(
                new Input(in.forward(), in.backward(), in.left(), in.right(), in.jump(), true, in.sprint())));
        directionhelper$fakedSneak = true;
    }

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void directionhelper$afterUse(LocalPlayer player, InteractionHand hand, BlockHitResult hit,
                                         CallbackInfoReturnable<InteractionResult> cir) {
        if (directionhelper$fakedSneak) {
            directionhelper$fakedSneak = false;
            player.connection.send(new ServerboundPlayerInputPacket(player.input.keyPresses));
        }
    }
}
