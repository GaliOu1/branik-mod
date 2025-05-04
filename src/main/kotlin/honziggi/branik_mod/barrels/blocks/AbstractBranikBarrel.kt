package honziggi.branik_mod.barrels.blocks

import honziggi.branik_mod.barrels.entities.AbstractBranikBarrelEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
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
import net.minecraft.world.phys.BlockHitResult

abstract class AbstractBranikBarrel(properties: Properties) :
    Block(properties), EntityBlock {

    constructor() : this(Properties.copy(Blocks.BARREL)) // Default barrel properties

    abstract fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return createBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

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
            if (blockEntity is AbstractBranikBarrelEntity) {
                return blockEntity.handlePlayerInteraction(player, hand)
            }
        }
        return InteractionResult.SUCCESS
    }


    override fun <T : BlockEntity?> getTicker(
        level: Level, state: BlockState, type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!level.isClientSide) {
            BlockEntityTicker { lvl, pos, st, be ->
                if (be is AbstractBranikBarrelEntity) {
                    be.tickServer()
                }
            }
        } else null
    }
}