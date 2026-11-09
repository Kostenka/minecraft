package tw.kinmen.directionhelper.client;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;

/** 找出方塊「東西／訊號出去」的那一面。不是這幾種方塊就回傳 null。 */
public final class OutputDirection {
    private OutputDirection() {}

    public static Direction of(BlockState state) {
        // 投擲器 DropperBlock 繼承 DispenserBlock，所以一起處理
        if (state.getBlock() instanceof DispenserBlock) {
            return state.getValue(DispenserBlock.FACING);
        }
        // 偵測器：FACING 是「臉」（偵測那面），紅點輸出在反方向
        if (state.getBlock() instanceof ObserverBlock) {
            return state.getValue(ObserverBlock.FACING).getOpposite();
        }
        // 漏斗：FACING 就是細管子指的方向（下或四個水平方向）
        if (state.getBlock() instanceof HopperBlock) {
            return state.getValue(HopperBlock.FACING);
        }
        // 合成器：正面就是吐東西的那面
        if (state.getBlock() instanceof CrafterBlock) {
            return state.getValue(CrafterBlock.ORIENTATION).front();
        }
        return null;
    }

    public static String blockName(BlockState state) {
        String id = state.getBlock().getDescriptionId();
        if (id.endsWith("dropper")) return "投擲器";
        if (id.endsWith("dispenser")) return "發射器";
        if (id.endsWith("observer")) return "偵測器";
        if (id.endsWith("hopper")) return "漏斗";
        if (id.endsWith("crafter")) return "合成器";
        return "方塊";
    }

    public static String absName(Direction d) {
        return switch (d) {
            case UP -> "上 ↑";
            case DOWN -> "下 ↓";
            case NORTH -> "北";
            case SOUTH -> "南";
            case EAST -> "東";
            case WEST -> "西";
        };
    }

    /** 相對於玩家面向的說法：遠離妳／朝向妳／左邊／右邊 */
    public static String relativeName(Direction d, Direction playerFacing) {
        if (d.getAxis().isVertical()) return "";
        if (d == playerFacing) return "（往前，遠離妳）";
        if (d == playerFacing.getOpposite()) return "（朝向妳）";
        if (d == playerFacing.getClockWise()) return "（妳的右邊 →）";
        return "（妳的左邊 ←）";
    }
}
