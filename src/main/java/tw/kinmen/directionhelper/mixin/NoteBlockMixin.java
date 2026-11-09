package tw.kinmen.directionhelper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 手上拿著任何東西時，右鍵音階盒不會調音，而是照常使用手上的物品（例如放方塊）。
 * 只有空手才會調音。
 */
@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void directionhelper$onlyEmptyHandTunes(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hit,
                                                   CallbackInfoReturnable<InteractionResult> cir) {
        if (!stack.isEmpty()) {
            // PASS = 不處理方塊互動，交給手上的物品（放方塊等）
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
