package honziggi.branik_mod.barrels.entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import honziggi.branik_mod.fluids.BranikFluids

abstract class AbstractBranikBarrelEntity(
    pos: BlockPos,
    state: BlockState,
    private val inputFluid: () -> FluidStack,
    private val outputFluid: () -> FluidStack,
    private val inputBranik: String = "branik",
    private val outputBranik: String = "branik",
    private val inputBucket: Item = BranikFluids.BRANIK11.bucket.get(),
    private val outputBucket: Item = BranikFluids.BRANIK11.bucket.get(),
    private val blockEntityType: BlockEntityType<*>,
    private val requiredInputAmount: Int = 16000,
    private val outputAmount: Int = 8000,
    val agingTimeTicks: Int = 20 * 60 // 60 seconds
) : BlockEntity(blockEntityType, pos, state), MenuProvider {

    val inputTank = object : FluidTank(requiredInputAmount) {
        override fun isFluidValid(stack: FluidStack): Boolean {
            return stack.fluid == inputFluid().fluid
        }
    }

    val outputTank = object : FluidTank(requiredInputAmount) {
        override fun isFluidValid(stack: FluidStack): Boolean {
            return stack.fluid == outputFluid().fluid
        }
    }

    fun handlePlayerInteraction(player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        val isEmptyBucket = stack.`is`(Items.BUCKET)
        val isInputBucket = stack.`is`(inputBucket)
        val isCreative = player.isCreative

        when {
            isInputBucket && inputTank.fluid.amount <= inputTank.capacity - 1000 -> {
                val filled = inputTank.fill(FluidStack(inputFluid().fluid, 1000), IFluidHandler.FluidAction.EXECUTE)
                if (filled == 1000 && !isCreative) {
                    stack.shrink(1)
                    player.addItem(ItemStack(Items.BUCKET))
                }
                return InteractionResult.SUCCESS
            }

            isEmptyBucket && outputTank.fluid.amount >= 1000 -> {
                val drained = outputTank.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                if (!drained.isEmpty && !isCreative) {
                    stack.shrink(1)
                    player.addItem(ItemStack(outputBucket))
                }
                return InteractionResult.SUCCESS
            }

            stack.isEmpty -> {
                val fluidIn = inputTank.fluid.amount
                val fluidOut = outputTank.fluid.amount
                val remainingTime = (agingTimeTicks - agingProgress) / 20

                val message = when {
                    fluidOut > 0 -> "$fluidOut mb $outputBranik"
                    agingProgress > 0 -> "Maturing in progress: time left $remainingTime seconds"
                    fluidIn > 0 -> "$fluidIn mb $inputBranik"
                    else -> "Barrel is empty"
                }

                player.displayClientMessage(Component.literal(message), true)
                return InteractionResult.SUCCESS
            }
        }

        return InteractionResult.PASS
    }



    private val fluidCap = LazyOptional.of { CombinedTankHandler() }
    var agingProgress = 0

    fun tickServer() {
        if (inputTank.fluidAmount >= requiredInputAmount && outputTank.fluidAmount <= (requiredInputAmount - outputAmount)) {
            agingProgress++

            if (agingProgress >= agingTimeTicks) {
                inputTank.drain(requiredInputAmount, IFluidHandler.FluidAction.EXECUTE)
                outputTank.fill(outputFluid(), IFluidHandler.FluidAction.EXECUTE)

                agingProgress = 0
                setChanged()
            }
        } else {
            agingProgress = 0
        }
    }

    fun getAgingProgressScaled(pixels: Int): Int {
        return if (agingTimeTicks == 0) 0 else agingProgress * pixels / agingTimeTicks
    }

    override fun <T> getCapability(cap: Capability<T>, side: net.minecraft.core.Direction?): LazyOptional<T> {
        val fluidCapType = CapabilityManager.get(object : CapabilityToken<IFluidHandler>() {})
        return when (cap) {
            fluidCapType -> fluidCap.cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        fluidCap.invalidate()
    }

    // Combined capability for Forge fluid pipes
    inner class CombinedTankHandler : IFluidHandler {
        override fun getTanks() = 2

        override fun getFluidInTank(tank: Int) = when (tank) {
            0 -> inputTank.fluid
            1 -> outputTank.fluid
            else -> FluidStack.EMPTY
        }

        override fun getTankCapacity(tank: Int) = when (tank) {
            0 -> inputTank.capacity
            1 -> outputTank.capacity
            else -> 0
        }

        override fun isFluidValid(tank: Int, stack: FluidStack) = when (tank) {
            0 -> inputTank.isFluidValid(stack)
            1 -> outputTank.isFluidValid(stack)
            else -> false
        }

        override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
            return inputTank.fill(resource, action)
        }

        override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
            return outputTank.drain(resource, action)
        }

        override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack {
            return outputTank.drain(maxDrain, action)
        }
    }

    abstract override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu

    abstract override fun getDisplayName(): Component
}
