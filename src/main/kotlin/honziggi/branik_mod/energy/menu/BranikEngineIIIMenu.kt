package honziggi.branik_mod.energy.menu

import honziggi.branik_mod.energy.blocks.BranikEngineIIIEntity
import honziggi.branik_mod.registry.BranikMenus
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ContainerData

class BranikEngineIIIMenu(
    windowId: Int,
    playerInventory: Inventory,
    blockEntity: BranikEngineIIIEntity,
    data: ContainerData
): AbstractBranikEngineMenu<BranikEngineIIIEntity>(
    BranikMenus.BRANIK_ENGINE_III_MENU.get(),
    windowId,
    playerInventory,
    blockEntity,
    data
) {
    companion object {
        fun fromNetwork(id: Int, inv: Inventory, extraData: FriendlyByteBuf?): BranikEngineIIIMenu {
            requireNotNull(extraData) {"Missing extraData for BranikEngineIIIMenu"}

            val pos = extraData.readBlockPos()
            val energyStored = extraData.readInt()

            val be = inv.player.level().getBlockEntity(pos) as? BranikEngineIIIEntity
                ?: throw IllegalStateException("Missing or wrong block entity at: $pos")

            return BranikEngineIIIMenu(id, inv, be, be.dataSlot).apply {
                this.clientEnergyStored = energyStored
            }
        }
    }
}