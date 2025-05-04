package honziggi.branik_mod.fluids

import honziggi.branik_mod.BranikMod
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.ForgeFlowingFluid
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

data class FluidData(
    val type: RegistryObject<FluidType>,
    val source: RegistryObject<ForgeFlowingFluid.Source>,
    val flowing: RegistryObject<ForgeFlowingFluid.Flowing>,
    val bucket: RegistryObject<BucketItem>,
    val block: RegistryObject<LiquidBlock>
)


object BranikFluids {
    val FLUID_TYPES: DeferredRegister<FluidType> = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, BranikMod.ID)
    val FLUIDS: DeferredRegister<Fluid> = DeferredRegister.create(ForgeRegistries.FLUIDS, BranikMod.ID)
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BranikMod.ID)
    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BranikMod.ID)

    private val  _fluids = mutableMapOf<String, FluidData>()
    fun getFluid(name: String) : FluidData = _fluids[name]!!

    private val amber = 0xFFc68e17.toInt()
    private val butterscotch = 0xFFFFD17F.toInt()
    private val chocolate = 0xFF5D3A1A.toInt()
    private val red50 = 0xFF304F.toInt()
    private val uranium = 0xFFB4FF00.toInt()

    private fun registerFluid(name: String, translationKey: String, tint: Int): FluidData {

        val type: RegistryObject<FluidType> = FLUID_TYPES.register(name)
        { BranikFluidsProperties(translationKey, tint) }

        lateinit var source: RegistryObject<ForgeFlowingFluid.Source>
        lateinit var flowing: RegistryObject<ForgeFlowingFluid.Flowing>

        val bucket = ITEMS.register("${name}_bucket") {
            BucketItem({ source.get() }, Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))
        }

        val block = BLOCKS.register("${name}_block") {
            LiquidBlock({ source.get() }, BlockBehaviour.Properties.copy(Blocks.WATER))
        }

        source = FLUIDS.register(name) {
            ForgeFlowingFluid.Source(
                ForgeFlowingFluid.Properties(type, { source.get() }, { flowing.get() })
                    .bucket(bucket)
                    .block(block)
            )
        }

        flowing = FLUIDS.register("flowing_$name") {
            ForgeFlowingFluid.Flowing(
                ForgeFlowingFluid.Properties(type, { source.get() }, { flowing.get() })
            )
        }

        return FluidData(type, source, flowing, bucket, block)
    }

    val BRANIK11: FluidData by lazy {
        registerFluid("branik11", "block.branik_mod.branik11", butterscotch).also { _fluids["branik11"] = it }
    }
    val BRANIK12: FluidData by lazy {
        registerFluid("branik12", "block.branik_mod.branik12", amber).also { _fluids["branik12"] = it }
    }
    val BRANIK14: FluidData by lazy {
        registerFluid("branik14", "block.branik_mod.branik14", chocolate).also { _fluids["branik14"] = it }
    }
    val BRANIK18: FluidData by lazy {
        registerFluid("branik18", "block.branik_mod.branik18", red50).also { _fluids["branik18"] = it }
    }

    val beerBuckets: List<RegistryObject<BucketItem>> by lazy {
        listOf(BRANIK11.bucket, BRANIK12.bucket, BRANIK14.bucket, BRANIK18.bucket)
    }

    fun register(eventBus: IEventBus) {
        BRANIK11
        BRANIK12
        BRANIK14
        BRANIK18

        FLUID_TYPES.register(eventBus)
        FLUIDS.register(eventBus)
        ITEMS.register(eventBus)
        BLOCKS.register(eventBus)
    }
}