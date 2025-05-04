package honziggi.branik_mod.blocks.crops

import honziggi.branik_mod.items.BranikItems
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty

class HopsCrop(properties: Properties): CropBlock(properties) {

    companion object{
        const val MAX_AGE = 7
        val AGE: IntegerProperty = BlockStateProperties.AGE_7
    }

    override fun getAgeProperty(): IntegerProperty = AGE
    override fun getMaxAge(): Int = MAX_AGE
    override fun getBaseSeedId(): ItemLike = BranikItems.HOPS_SEEDS.get()

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(AGE)
    }


}