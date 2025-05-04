package honziggi.branik_mod.energy.blocks

import honziggi.branik_mod.energy.menu.BranikEngineMenu
import honziggi.branik_mod.energy.menu.FermentationKegMenu
import honziggi.branik_mod.fluids.BranikFluids
import honziggi.branik_mod.items.BranikItems
import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class FermentationKegEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockEntities.FERMENTATION_KEG_ENTITY.get(), pos, state), MenuProvider {

    companion object {
        const val INPUT_SLOT = 0
        const val WATER_USAGE = 100
        const val FERMENT_TIME = 100 // 5 seconds (20 ticks per second)
    }

    // 1 slot: accepts only malt
    val itemHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return stack.item == BranikItems.MALT.get()
        }
    }
    private val itemCap = LazyOptional.of { itemHandler }

    // Tanks
    val waterTank = object : FluidTank(16000) {
        override fun isFluidValid(stack: FluidStack): Boolean {
            return stack.fluid == Fluids.WATER
        }
    }
    val beerTank: FluidTank by lazy {
        object : FluidTank(16000) {
            override fun isFluidValid(stack: FluidStack): Boolean {
                return stack.fluid == BranikFluids.BRANIK11
            }
        }
    }
    var dataSlot: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> waterTank.fluidAmount
                1 -> beerTank.fluidAmount
                2 -> if (fermentProgress > 0) 1 else 0
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                2 -> fermentProgress = if (value > 0) 1 else 0
            }
        }

        override fun getCount(): Int = 3
    }

    private val fluidCap = LazyOptional.of { CombinedTankHandler() }

    // Progress timer
    var fermentProgress = 0

    fun tickServer() {
        val maltStack = itemHandler.getStackInSlot(INPUT_SLOT)

        val hasWater = waterTank.fluid.amount >= WATER_USAGE
        val hasMalt = !maltStack.isEmpty

        if (hasMalt && hasWater) {
            fermentProgress++
            if (fermentProgress >= FERMENT_TIME) {
                // Finish fermentation
                maltStack.shrink(1)
                waterTank.drain(WATER_USAGE, IFluidHandler.FluidAction.EXECUTE)
                beerTank.fill(FluidStack(BranikFluids.BRANIK11.source.get(), 100), IFluidHandler.FluidAction.EXECUTE)

                fermentProgress = 0
                setChanged()
            }
        } else {
            fermentProgress = 0
        }
        println("Water: ${waterTank.fluid.amount}, Beer: ${beerTank.fluid.amount}, Malt: ${maltStack.count}, Fermenting: ${fermentProgress > 0}")
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val itemCapType = CapabilityManager.get(object : CapabilityToken<IItemHandler>() {})
        val fluidCapType = CapabilityManager.get(object : CapabilityToken<IFluidHandler>() {})

        return when (cap) {
            itemCapType -> itemCap.cast()
            fluidCapType -> fluidCap.cast()
            else -> super.getCapability(cap, side)
        }
    }
    override fun createMenu(
        id: Int,
        inventory: Inventory,
        player: Player
    ): AbstractContainerMenu {
        return FermentationKegMenu(id,inventory, this, dataSlot)
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Items", itemHandler.serializeNBT())
        tag.put("WaterTank", waterTank.writeToNBT(CompoundTag()))
        tag.put("BeerTank", beerTank.writeToNBT(CompoundTag()))
        tag.putInt("Progress", fermentProgress)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        itemHandler.deserializeNBT(tag.getCompound("Items"))
        waterTank.readFromNBT(tag.getCompound("WaterTank"))
        beerTank.readFromNBT(tag.getCompound("BeerTank"))
        fermentProgress = tag.getInt("Progress")
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemCap.invalidate()
        fluidCap.invalidate()
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.branik_mod.fermentation_keg")
    }

    // Combined tank handler for Forge Fluid capability
    inner class CombinedTankHandler : IFluidHandler {
        override fun getTanks() = 2

        override fun getFluidInTank(tank: Int) = when (tank) {
            0 -> waterTank.fluid
            1 -> beerTank.fluid
            else -> FluidStack.EMPTY
        }

        override fun getTankCapacity(tank: Int) = when (tank) {
            0 -> waterTank.capacity
            1 -> beerTank.capacity
            else -> 0
        }

        override fun isFluidValid(tank: Int, stack: FluidStack) = when (tank) {
            0 -> waterTank.isFluidValid(stack)
            1 -> beerTank.isFluidValid(stack)
            else -> false
        }

        override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
            return waterTank.fill(resource, action) // Only waterTank can be filled
        }

        override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
            return beerTank.drain(resource, action) // Only beerTank can be drained
        }

        override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
            return beerTank.drain(maxDrain, action)
        }
    }
}
