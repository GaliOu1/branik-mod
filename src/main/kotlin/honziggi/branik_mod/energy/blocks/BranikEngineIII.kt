package honziggi.branik_mod.energy.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState

class BranikEngineIII
    : AbstractBranikEngine(Properties.copy(Blocks.IRON_BLOCK).strength(3f)) {

    override fun createEngineEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity {
        return BranikEngineIIIEntity(pos, state)
    }

    override fun getEngineTicker(): BlockEntityTicker<*>? {
        return BlockEntityTicker<BlockEntity> { level, pos, state, entity ->
            if (!level.isClientSide && entity is BranikEngineIIIEntity) {
                entity.tickServer()
            }
        }
    }

}