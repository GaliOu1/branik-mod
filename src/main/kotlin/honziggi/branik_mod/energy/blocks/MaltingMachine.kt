package honziggi.branik_mod.energy.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class MaltingMachine: Block(Properties.copy(Blocks.IRON_BLOCK)
), EntityBlock {

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    }

    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        if (!pLevel.isClientSide) {
            val blockEntity = pLevel.getBlockEntity(pPos)
            println("Block Entity at $pPos: $blockEntity")
            if (blockEntity is MenuProvider) {
                NetworkHooks.openScreen(pPlayer as ServerPlayer, blockEntity) {
                        buf -> buf.writeBlockPos(pPos)
                }
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return MaltingMachineEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!level.isClientSide) {
            BlockEntityTicker { lvl, pos, st, be ->
                if (be is MaltingMachineEntity) {
                    be.tickServer()
                }
            }
        } else null
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }
}