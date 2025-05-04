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

class WoodenBranikBarrelEntity(
    pos: BlockPos,
    state: BlockState
) : AbstractBranikBarrelEntity(
    pos,
    state,
    inputFluid = { FluidStack(BranikFluids.BRANIK11.source.get(), 16000) },
    outputFluid = { FluidStack(BranikFluids.BRANIK12.source.get(), 8000) },
    inputBranik = "Branik °11",
    outputBranik = "Branik °12",
    inputBucket = BranikFluids.BRANIK11.bucket.get(),
    outputBucket = BranikFluids.BRANIK12.bucket.get(),
    blockEntityType = BlockEntities.WOODEN_BRANIK_BARREL_ENTITY.get(),
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
    = Component.translatable("block.branik_mod.wooden_branik_barrel")



}
