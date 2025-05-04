package honziggi.branik_mod.energy.menu

import honziggi.branik_mod.energy.blocks.BranikEngineIIEntity
import honziggi.branik_mod.registry.BranikMenus
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ContainerData

class BranikEngineIIMenu(
    windowId: Int,
    playerInventory: Inventory,
    blockEntity: BranikEngineIIEntity,
    data: ContainerData
): AbstractBranikEngineMenu<BranikEngineIIEntity>(
    BranikMenus.BRANIK_ENGINE_II_MENU.get(),
    windowId,
    playerInventory,
    blockEntity,
    data
) {
    companion object {
        fun fromNetwork(id: Int, inv: Inventory, extraData: FriendlyByteBuf?): BranikEngineIIMenu {
            requireNotNull(extraData) {"Missing extraData for BranikEngineIIMenu"}

            val pos = extraData.readBlockPos()
            val energyStored = extraData.readInt()

            val be = inv.player.level().getBlockEntity(pos) as? BranikEngineIIEntity
                ?: throw IllegalStateException("Missing or wrong block entity at: $pos")

            return BranikEngineIIMenu(id, inv, be, be.dataSlot).apply {
                this.clientEnergyStored = energyStored
            }
        }
    }
}