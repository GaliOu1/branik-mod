package honziggi.branik_mod.energy.blocks

import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
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
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class BranikEngine : Block(
    Properties.copy(Blocks.IRON_BLOCK)
        .strength(3.5f)
        .noOcclusion()
), EntityBlock {
    init {
        registerDefaultState(this.defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false))
    }

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val LIT: BooleanProperty = BlockStateProperties.LIT
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
            if (blockEntity is BranikEngineEntity) {
                NetworkHooks.openScreen(pPlayer as ServerPlayer, blockEntity as MenuProvider) { buf ->
                    buf.writeBlockPos(pPos)
                    buf.writeInt(blockEntity.energyStorage.energyStored)
                }
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BranikEngineEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!level.isClientSide && type == BlockEntities.BRANIK_ENGINE_ENTITY.get()) {
            BlockEntityTicker { lvl, pos, st, be ->
                (be as? BranikEngineEntity)?.tickServer()
            }
        } else null
    }


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LIT)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.opposite)
            .setValue(LIT, false)
    }
}