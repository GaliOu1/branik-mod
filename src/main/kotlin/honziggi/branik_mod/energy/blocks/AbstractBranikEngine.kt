package honziggi.branik_mod.energy.blocks

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

abstract class AbstractBranikEngine(
    properties: Properties
) : Block(properties.noOcclusion()), EntityBlock {

    init {
        registerDefaultState(this.defaultBlockState()
            .setValue(BranikEngine.Companion.FACING, Direction.NORTH)
            .setValue(LIT, false))
    }
    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val LIT: BooleanProperty = BlockStateProperties.LIT
    }

    abstract fun createEngineEntity(pos: BlockPos, state: BlockState): BlockEntity
    abstract fun getEngineTicker(): BlockEntityTicker<*>?

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is MenuProvider && player is ServerPlayer) {
                NetworkHooks.openScreen(player, blockEntity) { buf ->
                    buf.writeBlockPos(pos)
                    if (blockEntity is AbstractBranikEngineEntity) {
                        buf.writeInt(blockEntity.energyStorage.energyStored)
                    } else {
                        buf.writeInt(0)
                    }
                }
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = createEngineEntity(pos,state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        @Suppress("UNCHECKED_CAST")
        return getEngineTicker() as? BlockEntityTicker<T>
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.opposite)
            .setValue(LIT, false)
    }
}
