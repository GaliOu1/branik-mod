package honziggi.branik_mod.barrels.blocks

import honziggi.branik_mod.barrels.entities.DiamondBranikBarrelEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class DiamondBranikBarrel: AbstractBranikBarrel() {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return DiamondBranikBarrelEntity(pos, state)
    }
}