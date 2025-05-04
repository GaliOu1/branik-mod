package honziggi.branik_mod.energy.blocks

import honziggi.branik_mod.energy.menu.BranikEngineIIMenu
import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState

class BranikEngineIIEntity
    (pos: BlockPos,
     state: BlockState,
     override val validItems: List<String> = listOf(
        "branik_mod:branik_11", "branik_mod:branik2l_11",
        "branik_mod:branik_12", "branik_mod:branik2l_12",
        "branik_mod:branik_14", "branik_mod:branik2l_14")
    )

    : AbstractBranikEngineEntity(
        BlockEntities.BRANIK_ENGINE_II_ENTITY.get(),
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
        else -> 0
    }

    override fun getItemEnergy(item: Item): Int = when (BuiltInRegistries.ITEM.getKey(item).toString()) {
        "branik_mod:branik_11" -> 500
        "branik_mod:branik_12" -> 1000
        "branik_mod:branik2l_11", "branik_mod:branik_14" -> 2000
        "branik_mod:branik2l_12" -> 4000
        "branik_mod:branik2l_14" -> 8000
        else -> 0
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.branik_mod.branik_engine")
    }

        override fun createMenu(
        id : Int,
        inventory: Inventory,
        player : Player
    ): BranikEngineIIMenu {
        return BranikEngineIIMenu(id, inventory, this, dataSlot)
    }
}
