package honziggi.branik_mod.energy.blocks

import honziggi.branik_mod.energy.menu.MaltingMachineMenu
import honziggi.branik_mod.items.BranikItems
import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class MaltingMachineEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockEntities.MALTING_MACHINE_ENTITY.get(), pos, state), MenuProvider {

    companion object {
        const val FUEL_SLOT = 0
        const val INPUT_SLOT1 = 1
        const val INPUT_SLOT2 = 2
        const val INPUT_SLOT3 = 3
        const val INPUT_SLOT4 = 4
        const val OUTPUT_SLOT1 = 5
        const val OUTPUT_SLOT2 = 6
        const val OUTPUT_SLOT3 = 7
        const val OUTPUT_SLOT4 = 8
    }

    val itemHandler = object : ItemStackHandler(9) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            // Check if the slot is FUEL_SLOT
            if (slot == FUEL_SLOT) {
                // Use Minecraft's built-in method to check if the item is burnable (like in a furnace)
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0
            }

            // For other slots (input/output), you can leave your previous validation
            if (slot in listOf(5, 6, 7, 8)) return false

            // Accept grains for input
            val itemId = BuiltInRegistries.ITEM.getKey(stack.item).toString()
            return itemId == "branik_mod:grains"
        }
    }

    private val itemCap = LazyOptional.of { itemHandler }

    var burnTime = 0
    var burnDuration = 0
    var temperature = 20 // starts at 20°C
    var maltingProgress = 0
    var maltingTotalTime = 400

    var dataSlot: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> burnTime
                1 -> burnDuration
                2 -> temperature
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> burnTime = value
                1 -> burnDuration = value
                2 -> temperature = value
            }
        }

        override fun getCount(): Int = 3
    }

    fun tickServer() {
        if (burnTime > 0) {
            burnTime--
        }

        if (burnTime > 0 && temperature < 120) {
            // burning: raise temperature
            if (level!!.gameTime % 20L == 0L) { // every second
                temperature = (temperature + 2).coerceAtMost(120)
            }
        } else if (burnTime <= 0 && temperature > 60) {
            // no burn, but above 60°C: cooling slowly
            if (level!!.gameTime % 20L == 0L) {
                temperature = (temperature - 1).coerceAtLeast(20)
            }
        } else if (burnTime <= 0 && temperature <= 60) {
            // low temperature, try to burn fuel
            tryConsumeFuel()
        } else if (burnTime <= 0 && temperature > 20) {
            // cooling normally
            if (level!!.gameTime % 20L == 0L) {
                temperature = (temperature - 1).coerceAtLeast(20)
            }
        }

        setChanged()

        // --- Malting logic ---
        if (canMalt()) {
            maltingProgress++
            if (maltingProgress >= maltingTotalTime) {
                finishMalting()
                maltingProgress = 0
            }
        } else {
            maltingProgress = 0
        }

        setChanged()
    }

    private fun tryConsumeFuel() {
        val fuelStack = itemHandler.getStackInSlot(FUEL_SLOT)
        if (!fuelStack.isEmpty) {
            val fuelValue = ForgeHooks.getBurnTime(fuelStack, RecipeType.SMELTING)

            if (fuelValue > 0) {
                burnDuration = fuelValue
                burnTime = fuelValue
                fuelStack.shrink(1)
                setChanged()
            }
        }
    }

    private fun canMalt(): Boolean {
        val inputSlots = listOf(INPUT_SLOT1, INPUT_SLOT2, INPUT_SLOT3, INPUT_SLOT4)
        val inputs = inputSlots.map { itemHandler.getStackInSlot(it) }

        val outputSlots = listOf(OUTPUT_SLOT1, OUTPUT_SLOT2, OUTPUT_SLOT3, OUTPUT_SLOT4)
        val outputs = outputSlots.map { itemHandler.getStackInSlot(it) }

        // Check if all inputs have items, and if outputs can accept items
        return inputs.all { it.item == BranikItems.GRAINS.get() } &&
                outputs.any { it.isEmpty || (it.item == BranikItems.MALT.get() && it.count < it.maxStackSize) } &&
                temperature > 80
    }

    private fun finishMalting() {
        val inputSlots = listOf(INPUT_SLOT1, INPUT_SLOT2, INPUT_SLOT3, INPUT_SLOT4)
        val inputs = inputSlots.map { itemHandler.getStackInSlot(it) }

        val outputSlots = listOf(OUTPUT_SLOT1, OUTPUT_SLOT2, OUTPUT_SLOT3, OUTPUT_SLOT4)
        val outputs = outputSlots.map { itemHandler.getStackInSlot(it) }

        // Process the malting: move grains to malt and reduce input
        for ((input, output) in inputs.zip(outputs)) {
            if (!input.isEmpty && (output.isEmpty || (output.item == BranikItems.MALT.get() && output.count < output.maxStackSize))) {
                if (output.isEmpty) {
                    itemHandler.setStackInSlot(outputSlots[inputs.indexOf(input)], ItemStack(BranikItems.MALT.get()))
                } else {
                    output.grow(1)
                }
                input.shrink(1)
            }
        }
    }

    override fun <T> getCapability(cap: Capability<T>, side: net.minecraft.core.Direction?): LazyOptional<T> {
        val itemCapType = CapabilityManager.get(object : CapabilityToken<IItemHandler>() {})
        return when (cap) {
            itemCapType -> itemCap.cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Items", itemHandler.serializeNBT())
        tag.putInt("BurnTime", burnTime)
        tag.putInt("BurnDuration", burnDuration)
        tag.putInt("Temperature", temperature)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        itemHandler.deserializeNBT(tag.getCompound("Items"))
        burnTime = tag.getInt("BurnTime")
        burnDuration = tag.getInt("BurnDuration")
        temperature = tag.getInt("Temperature")
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemCap.invalidate()
    }

    override fun getDisplayName() = net.minecraft.network.chat.Component.translatable("block.branik_mod.malting_machine")

    override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return MaltingMachineMenu(id, inventory, this, dataSlot)
    }
}
