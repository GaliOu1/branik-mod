package honziggi.branik_mod.energy.menu

import honziggi.branik_mod.energy.blocks.MaltingMachineEntity
import honziggi.branik_mod.registry.BranikMenus
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class MaltingMachineMenu(
    id: Int,
    playerInventory: Inventory,
    val blockEntity: MaltingMachineEntity,
    val data: ContainerData
) : AbstractContainerMenu(BranikMenus.MALTING_MACHINE_MENU.get(), id) {

    private val access = ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.blockPos)
    private val itemHandler: IItemHandler = blockEntity.itemHandler

    override fun quickMoveStack(p0: Player, index: Int): ItemStack {
        var stack = ItemStack.EMPTY
        val slot = this.slots[index]
        if (slot.hasItem()) {
            val current = slot.item
            stack = current.copy()
            if (index == 0) {
                if (!moveItemStackTo(current, 1, slots.size, true)) return ItemStack.EMPTY
                slot.onQuickCraft(current, stack)
            } else {
                if (!moveItemStackTo(current, 0, 1, false)) return ItemStack.EMPTY
            }

            if (current.isEmpty) slot.set(ItemStack.EMPTY)
            else slot.setChanged()
        }
        return stack
    }

    init {
        // Fuel Slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.FUEL_SLOT, 80, 64))

        // Input Slots (4 slots)
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.INPUT_SLOT1, 26, 24))  // 1st input slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.INPUT_SLOT2, 26, 42))  // 2nd input slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.INPUT_SLOT3, 44, 24))  // 3rd input slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.INPUT_SLOT4, 44, 42))  // 4th input slot

        // Output Slots (4 slots)
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.OUTPUT_SLOT1, 116, 24))  // 1st output slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.OUTPUT_SLOT2, 116, 42))  // 2nd output slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.OUTPUT_SLOT3, 134, 24))  // 3rd output slot
        addSlot(SlotItemHandler(itemHandler, MaltingMachineEntity.OUTPUT_SLOT4, 134, 42))  // 4th output slot

        // Player inventory (9x3)
        for (row in 0 until 3) {
            for (col in 0 until 9) {
                addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }

        // Hotbar
        for (col in 0 until 9) {
            addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }

        addDataSlots(data)
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(access, player, blockEntity.blockState.block)
    }

    fun getBurnProgressScaled(pixels: Int): Int {
        val burnTime = data.get(0)
        val burnDuration = data.get(1)
        return if (burnDuration == 0 || burnTime == 0) 0 else burnTime * pixels / burnDuration
    }
    fun getMaltingProgressScaled(pixels: Int): Int {
        val progress = blockEntity.maltingProgress
        val total = blockEntity.maltingTotalTime
        return if (total == 0 || progress == 0) 0 else progress * pixels / total
    }

    fun getTemperature(): Int {
        return data.get(2)
    }

    companion object {
        fun fromNetwork(id: Int, inv: Inventory, extraData: FriendlyByteBuf?): MaltingMachineMenu {
            requireNotNull(extraData) { "Missing extraData for MaltingMachineMenu!" }

            val pos = extraData.readBlockPos()
            val be = inv.player.level().getBlockEntity(pos) as? MaltingMachineEntity
                ?: throw IllegalStateException("Wrong block entity at $pos")

            val data = be.dataSlot

            return MaltingMachineMenu(id, inv, be, data)
        }
    }
}
