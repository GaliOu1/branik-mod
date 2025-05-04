package honziggi.branik_mod.energy.menu

import honziggi.branik_mod.registry.BranikMenus
import honziggi.branik_mod.energy.blocks.BranikEngineEntity
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class BranikEngineMenu (
    windowId: Int,
    playerInventory: Inventory,
    val blockEntity: BranikEngineEntity,
    val data: ContainerData
) : AbstractContainerMenu(BranikMenus.BRANIK_ENGINE_MENU.get(), windowId) {

    var clientEnergyStored = blockEntity.clientEnergyStored
    private val level: Level = playerInventory.player.level()
    private val itemHandler: IItemHandler = blockEntity.itemHandler
    private val access = ContainerLevelAccess.create(level, blockEntity.blockPos)

    init {
        // Input (fuel) slot at the left
        addSlot(SlotItemHandler(itemHandler, 0, 44, 46))

        addSlot(SlotItemHandler(itemHandler, 1, 98, 35))
        addSlot(SlotItemHandler(itemHandler, 2, 98, 53))
        addSlot(SlotItemHandler(itemHandler, 3, 116, 35))
        addSlot(SlotItemHandler(itemHandler, 4, 116, 53))


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


    override fun stillValid(p0: Player): Boolean {
        return stillValid(access, p0, blockEntity.blockState.block)
    }

    fun getBurnProgressScaled(pixels: Int): Int {
        val burnTime = data.get(0)
        val maxBurnTime = data.get(3)

        return if (maxBurnTime > 0 && burnTime > 0) {
            burnTime * pixels / maxBurnTime
        } else 0
    }

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

    companion object {
        fun fromNetwork(id: Int, inv: Inventory, extraData: FriendlyByteBuf?): BranikEngineMenu {
            requireNotNull(extraData) {"Missing extraData for BranikEngineMenu"}

            val pos = extraData.readBlockPos()
            val energyStored = extraData.readInt()

            val be = inv.player.level().getBlockEntity(pos) as? BranikEngineEntity
                ?: throw IllegalStateException("Missing or wrong block entity at $pos")

            return BranikEngineMenu(id, inv, be, be.dataSlot).apply {
                this.clientEnergyStored = energyStored
            }
        }
    }
}