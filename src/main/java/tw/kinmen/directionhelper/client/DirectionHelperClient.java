package tw.kinmen.directionhelper.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 看著投擲器／發射器／偵測器／漏斗／合成器時：
 *  1. 在輸出那一面外面畫一支紅色粒子箭頭
 *  2. 在快捷列上方顯示文字「輸出方向：…」
 * 純客戶端，任何伺服器都能用。
 */
public class DirectionHelperClient implements ClientModInitializer {
    private static final DustParticleOptions ARROW = new DustParticleOptions(0xFF2A2A, 0.7f);
    private static final DustParticleOptions FACE = new DustParticleOptions(0x3AA0FF, 0.6f);

    private int tickCounter = 0;
    private BlockPos lastPos = null;
    private BlockState lastState = null;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(Minecraft mc) {
        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            lastPos = null;
            return;
        }
        BlockPos pos = hit.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        Direction out = OutputDirection.of(state);
        if (out == null) {
            lastPos = null;
            return;
        }

        tickCounter++;
        boolean changed = !pos.equals(lastPos) || state != lastState;
        if (changed || tickCounter % 20 == 0) {
            Component msg = Component.literal(OutputDirection.blockName(state) + " 輸出方向：")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(OutputDirection.absName(out)
                                    + OutputDirection.relativeName(out, mc.player.getDirection()))
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            mc.gui.setOverlayMessage(msg, false);
        }
        lastPos = pos;
        lastState = state;

        if (tickCounter % 3 == 0) {
            drawArrow(mc.level, pos, out, ARROW, mc.player.getDirection());
            // 偵測器另外用藍色小箭頭標出「偵測的那一面」
            if (state.getBlock() instanceof ObserverBlock) {
                drawShortMark(mc.level, pos, out.getOpposite(), FACE);
            }
        }
    }

    /** 在方塊 dir 那一面外面畫箭頭：一根軸 + 四個倒鉤（從任何角度都看得到） */
    private static void drawArrow(ClientLevel level, BlockPos pos, Direction dir, DustParticleOptions p,
                                  Direction playerFacing) {
        Vec3 c = Vec3.atCenterOf(pos);
        Vec3 d = Vec3.atLowerCornerOf(dir.getUnitVec3i());
        Vec3[] perps = perpendiculars(dir, playerFacing);

        // 軸：從面上 0.55 到 1.35
        for (double t = 0.55; t <= 1.35; t += 0.1) {
            spawn(level, p, c.add(d.scale(t)));
        }
        // 箭頭尖端的倒鉤
        Vec3 tip = c.add(d.scale(1.35));
        for (Vec3 u : perps) {
            for (double s = 0.07; s <= 0.3; s += 0.075) {
                spawn(level, p, tip.subtract(d.scale(s)).add(u.scale(s)));
            }
        }
    }

    private static void drawShortMark(ClientLevel level, BlockPos pos, Direction dir, DustParticleOptions p) {
        Vec3 c = Vec3.atCenterOf(pos);
        Vec3 d = Vec3.atLowerCornerOf(dir.getUnitVec3i());
        for (double t = 0.55; t <= 0.8; t += 0.08) {
            spawn(level, p, c.add(d.scale(t)));
        }
    }

    private static Vec3[] perpendiculars(Direction dir, Direction playerFacing) {
        Vec3 a, b;
        if (dir.getAxis().isVertical()) {
            // 上下箭頭：倒鉤往四個水平方向
            a = new Vec3(1, 0, 0);
            b = new Vec3(0, 0, 1);
        } else {
            // 水平箭頭：倒鉤往上下 + 左右
            a = new Vec3(0, 1, 0);
            b = Vec3.atLowerCornerOf(dir.getClockWise().getUnitVec3i());
        }
        return new Vec3[]{a, a.scale(-1), b, b.scale(-1)};
    }

    private static void spawn(ClientLevel level, DustParticleOptions p, Vec3 v) {
        level.addParticle(p, v.x, v.y, v.z, 0, 0, 0);
    }
}
