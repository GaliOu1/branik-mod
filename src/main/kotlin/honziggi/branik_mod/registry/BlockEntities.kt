package honziggi.branik_mod.registry

import honziggi.branik_mod.BranikMod
import honziggi.branik_mod.barrels.entities.DiamondBranikBarrelEntity
import honziggi.branik_mod.barrels.entities.GoldenBranikBarrelEntity
import honziggi.branik_mod.barrels.entities.IronBranikBarrelEntity
import honziggi.branik_mod.barrels.entities.WoodenBranikBarrelEntity
import honziggi.branik_mod.blocks.BranikBlocks
import honziggi.branik_mod.energy.blocks.BranikEngineEntity
import honziggi.branik_mod.energy.blocks.BranikEngineIIEntity
import honziggi.branik_mod.energy.blocks.BranikEngineIIIEntity
import honziggi.branik_mod.energy.blocks.FermentationKegEntity
import honziggi.branik_mod.energy.blocks.MaltingMachineEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object BlockEntities {
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BranikMod.ID)

    private inline fun <T : BlockEntity> registerEntity(
        name: String,
        block: RegistryObject<out Block>,
        noinline factory: (pos: BlockPos, state: BlockState) -> T
    ): RegistryObject<BlockEntityType<T>> {
        return BLOCK_ENTITIES.register(name) {
            BlockEntityType.Builder.of(factory, block.get()).build(null)
        }
    }

    val BRANIK_ENGINE_ENTITY = registerEntity("branik_engine_entity", BranikBlocks.BRANIK_ENGINE, ::BranikEngineEntity)
    val BRANIK_ENGINE_II_ENTITY = registerEntity("branik_engine_ii_entity", BranikBlocks.BRANIK_ENGINE_II, ::BranikEngineIIEntity)
    val BRANIK_ENGINE_III_ENTITY = registerEntity("branik_engine_iii_entity", BranikBlocks.BRANIK_ENGINE_III, ::BranikEngineIIIEntity)
    val MALTING_MACHINE_ENTITY = registerEntity("malting_machine_entity", BranikBlocks.MALTING_MACHINE, ::MaltingMachineEntity)
    val FERMENTATION_KEG_ENTITY = registerEntity("fermentation_keg_entity", BranikBlocks.FERMENTATION_KEG, ::FermentationKegEntity)

    val WOODEN_BRANIK_BARREL_ENTITY = registerEntity("wooden_barrel_entity", BranikBlocks.WOODEN_BRANIK_BARREL, ::WoodenBranikBarrelEntity)
    val IRON_BRANIK_BARREL_ENTITY = registerEntity("iron_branik_barrel_entity", BranikBlocks.IRON_BRANIK_BARREL, ::IronBranikBarrelEntity)
    val GOLDEN_BRANIK_BARREL_ENTITY = registerEntity("golden_branik_barrel_entity", BranikBlocks.GOLDEN_BRANIK_BARREL, ::GoldenBranikBarrelEntity)
    val DIAMOND_BRANIK_BARREL_ENTITY = registerEntity("diamond_branik_barrel_entity", BranikBlocks.DIAMOND_BRANIK_BARREL, ::DiamondBranikBarrelEntity)


    fun register(eventBus: IEventBus) {
        BLOCK_ENTITIES.register(eventBus)
    }
}
