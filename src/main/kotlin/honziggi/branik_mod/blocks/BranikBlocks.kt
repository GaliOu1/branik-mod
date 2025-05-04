package honziggi.branik_mod.blocks

import honziggi.branik_mod.BranikMod
import honziggi.branik_mod.barrels.blocks.DiamondBranikBarrel
import honziggi.branik_mod.barrels.blocks.GoldenBranikBarrel
import honziggi.branik_mod.barrels.blocks.IronBranikBarrel
import honziggi.branik_mod.barrels.blocks.WoodenBranikBarrel
import honziggi.branik_mod.blocks.crops.HopsCrop
import honziggi.branik_mod.energy.blocks.BranikEngine
import honziggi.branik_mod.energy.blocks.BranikEngineII
import honziggi.branik_mod.energy.blocks.BranikEngineIII
import honziggi.branik_mod.energy.blocks.FermentationKeg
import honziggi.branik_mod.energy.blocks.MaltingMachine
import honziggi.branik_mod.items.BranikItems
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object BranikBlocks {

    const val MODID = BranikMod.ID
    val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }
    private fun <T : Block> registerBlock(name: String, block: () -> T): RegistryObject<T>{
        val toReturn = BLOCKS.register(name, block)
        registerBlockItem(name, toReturn)
        return toReturn
    }
    private fun <T : Block> registerBlockItem(name: String, block: RegistryObject<T>): RegistryObject<Item> {
        return BranikItems.ITEMS.register(name) {
            BlockItem(block.get(), Item.Properties())
        }
    }

    val EMPTY_CASE = registerBlock("empty_case") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK_11_CASE = registerBlock("branik_11_case") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK_12_CASE = registerBlock("branik_12_case") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK_14_CASE = registerBlock("branik_14_case") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK_18_CASE = registerBlock("branik_18_case") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK2L_11_PACK = registerBlock("branik2l_11_pack") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK2L_12_PACK = registerBlock("branik2l_12_pack") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK2L_14_PACK = registerBlock("branik2l_14_pack") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }
    val BRANIK2L_18_PACK = registerBlock("branik2l_18_pack") {
        Block(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)) }


    val HOPS_CROP = registerBlock("hops_crop") {
        HopsCrop(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()) }


    val BRANIK_ENGINE = registerBlock("branik_engine") { BranikEngine() }
    val BRANIK_ENGINE_II = registerBlock("branik_engine_ii") { BranikEngineII() }
    val BRANIK_ENGINE_III = registerBlock("branik_engine_iii") { BranikEngineIII() }

    val MALTING_MACHINE = registerBlock("malting_machine") { MaltingMachine() }

    val FERMENTATION_KEG = registerBlock("fermentation_keg") { FermentationKeg() }

    val WOODEN_BRANIK_BARREL = registerBlock("wooden_branik_barrel") { WoodenBranikBarrel() }
    val IRON_BRANIK_BARREL = registerBlock("iron_branik_barrel") { IronBranikBarrel() }
    val GOLDEN_BRANIK_BARREL = registerBlock("golden_branik_barrel") { GoldenBranikBarrel() }
    val DIAMOND_BRANIK_BARREL = registerBlock("diamond_branik_barrel") { DiamondBranikBarrel() }



    val ALL_BRANIK_BLOCKS = listOf(
        EMPTY_CASE,
        BRANIK_11_CASE,BRANIK_12_CASE, BRANIK_14_CASE, BRANIK_18_CASE,
        BRANIK2L_11_PACK, BRANIK2L_12_PACK, BRANIK2L_14_PACK, BRANIK2L_18_PACK,
        HOPS_CROP,
        BRANIK_ENGINE, BRANIK_ENGINE_II, BRANIK_ENGINE_III,
        MALTING_MACHINE, FERMENTATION_KEG,
        WOODEN_BRANIK_BARREL, IRON_BRANIK_BARREL, GOLDEN_BRANIK_BARREL, DIAMOND_BRANIK_BARREL

    )
}