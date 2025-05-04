package honziggi.branik_mod.fluids

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.FluidType.Properties
import java.util.function.Consumer

class BranikFluidsProperties(translationKey: String, private val tint: Int)
    : FluidType(Properties.create().descriptionId(translationKey)) {

    override fun initializeClient(consumer: Consumer<IClientFluidTypeExtensions>) {
        consumer.accept(object : IClientFluidTypeExtensions {

            override fun getStillTexture(): ResourceLocation = ResourceLocation("minecraft:block/water_still")
            override fun getFlowingTexture(): ResourceLocation = ResourceLocation("minecraft:block/water_flow")
            override fun getTintColor(): Int = tint
        })
    }
}