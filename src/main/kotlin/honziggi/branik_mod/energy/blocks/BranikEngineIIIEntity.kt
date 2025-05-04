package honziggi.branik_mod.energy.blocks

import honziggi.branik_mod.energy.menu.BranikEngineIIIMenu
import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.translatable
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState

class BranikEngineIIIEntity(
    pos: BlockPos,
    state: BlockState,
    override val validItems: List<String> = listOf(
        "branik_mod:branik_11", "branik_mod:branik2l_11",
        "branik_mod:branik_12", "branik_mod:branik2l_12",
        "branik_mod:branik_14", "branik_mod:branik2l_14",
        "branik_mod:branik_18", "branik_mod:branik2l_18")

) : AbstractBranikEngineEntity(
        BlockEntities.BRANIK_ENGINE_III_ENTITY.get(),
        pos,
        state,
        capacity = 10000,
        maxInput = 100,
        maxOutput = 200
    ) {

    override fun getEnergyPerTickFor(item: Item): Int = when (BuiltInRegistries.ITEM.getKey(item).toString()) {
        "branik_mod:branik_11", "branik_mod:branik2l_11" -> 10
        "branik_mod:branik_12", "branik_mod:branik2l_12" -> 15
        "branik_mod:branik_14", "branik_mod:branik2l_14" -> 20
        "branik_mod:branik_18", "branik_mod:branik2l_18" -> 30
        else -> 0
    }

    override fun getItemEnergy(item: Item): Int = when (BuiltInRegistries.ITEM.getKey(item).toString()) {
        "branik_mod:branik_11" -> 500
        "branik_mod:branik_12" -> 1000
        "branik_mod:branik2l_11", "branik_mod:branik_14" -> 2000
        "branik_mod:branik2l_12", "branik_mod:branik_18" -> 4000
        "branik_mod:branik2l_14" -> 8000
        "branik_mod:branik2l_18" -> 16000
        else -> 0
    }
    override fun createMenu(
        id: Int,
        inventory: Inventory,
        player: Player
    ): AbstractContainerMenu {
        return BranikEngineIIIMenu (id, inventory, this, dataSlot)
    }

    override fun getDisplayName(): Component {
        return translatable("block.branik_mod.branik_engine_iii")
    }
}



