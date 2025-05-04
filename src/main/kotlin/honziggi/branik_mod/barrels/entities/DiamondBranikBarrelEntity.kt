package honziggi.branik_mod.barrels.entities

import honziggi.branik_mod.fluids.BranikFluids
import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.fluids.FluidStack

class DiamondBranikBarrelEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractBranikBarrelEntity(
    pos,
    state,
    inputFluid = { FluidStack(BranikFluids.BRANIK14.source.get(), 16000) },
    outputFluid = { FluidStack(BranikFluids.BRANIK18.source.get(), 8000) },
    inputBranik = "Branik °18",
    outputBranik = "Branik Cocaine",
    inputBucket = BranikFluids.BRANIK14.bucket.get(),
    outputBucket = BranikFluids.BRANIK18.bucket.get(),
    blockEntityType = BlockEntities.DIAMOND_BRANIK_BARREL_ENTITY.get(),
    requiredInputAmount = 16000,
    outputAmount = 8000,
    agingTimeTicks = 20 * 120  // 120 seconds
) {
    override fun createMenu(
        id: Int,
        inventory: Inventory,
        player: Player
    ): AbstractContainerMenu {
        throw UnsupportedOperationException("This block does not have a GUI")
    }

    override fun getDisplayName(): Component
            = Component.translatable("block.branik_mod.diamond_branik_barrel")
}
