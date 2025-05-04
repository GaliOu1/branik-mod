package honziggi.branik_mod.energy.menu

import honziggi.branik_mod.energy.blocks.FermentationKegEntity
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

class FermentationKegMenu(
    id: Int,
    playerInventory: Inventory,
    val blockEntity: FermentationKegEntity,
    val data: ContainerData
) : AbstractContainerMenu(BranikMenus.FERMENTATION_KEG_MENU.get(), id) {

    private val access = ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.blockPos)
    private val itemHandler: IItemHandler = blockEntity.itemHandler

    init {
        addDataSlots(data)
        // Input Slot (malt) - center bottom of red box
        addSlot(SlotItemHandler(itemHandler, FermentationKegEntity.INPUT_SLOT, 90, 70))

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
    }

    override fun stillValid(player: Player): Boolean {
        return stillValid(access, player, blockEntity.blockState.block)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var stack = ItemStack.EMPTY
        val slot = slots[index]
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
    fun getWaterAmount(): Int = blockEntity.waterTank.fluidAmount
    fun getBeerAmount(): Int = blockEntity.beerTank.fluidAmount
    fun getTankCapacity(): Int = 16000
    fun isFermenting(): Boolean = blockEntity.fermentProgress > 0

    companion object {
        fun fromNetwork(id: Int, inv: Inventory, extraData: FriendlyByteBuf?): FermentationKegMenu {
            requireNotNull(extraData) { "Missing extraData for FermentationKegMenu!" }
            val pos = extraData.readBlockPos()
            val be = inv.player.level().getBlockEntity(pos) as? FermentationKegEntity
                ?: throw IllegalStateException("Wrong block entity at $pos")

            val data = be.dataSlot
            return FermentationKegMenu(id, inv, be, data)
        }
    }
}
