package honziggi.branik_mod.items

import honziggi.branik_mod.blocks.BranikBlocks
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.effect.MobEffects.BLINDNESS
import net.minecraft.world.effect.MobEffects.CONFUSION
import net.minecraft.world.effect.MobEffects.DAMAGE_BOOST
import net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE
import net.minecraft.world.effect.MobEffects.REGENERATION
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.UUID

object BranikItems {
    val ITEMS: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, "branik_mod")

    private fun craftItem(name: String): RegistryObject<Item> =
        ITEMS.register(name) { Item(Item.Properties()) }

    private fun rawBranik(name: String): RegistryObject<Item> =
        ITEMS.register(name) {
            Item(Item.Properties().food(
                FoodProperties.Builder()
                    .nutrition(1).saturationMod(1f)
                    .alwaysEat().effect({ MobEffectInstance(MobEffects.HARM, 200)},1f)
                    .build()
            ))
        }

    private fun brewedBranik(name: String, vararg effects: MobEffectInstance) = ITEMS.register(name) {
        val food = FoodProperties.Builder()
            .nutrition(2+effects.size).saturationMod(0.5f + effects.size * 0.1f)
            .alwaysEat().apply { effects.forEach { effect({it}, 1f) } }
            .build()

        Item(Item.Properties().food(food).stacksTo(16))
    }

    val BRANIK_BOTTLE = craftItem("branik_bottle")
    val TWO_ELCO_BOTTLE = craftItem("two_elco_bottle")
    val BRANIK_CAP = craftItem("branik_cap")
    val BRANIK_LABEL = craftItem("branik_label")
    val HOPS = craftItem("hops")
    val GRAINS = craftItem("grains")
    val MALT = craftItem("malt")

    val HOPS_SEEDS = ITEMS.register("hops_seeds") {
        ItemNameBlockItem(BranikBlocks.HOPS_CROP.get(), Item.Properties())
    }

    val BRANIK_11 = brewedBranik("branik_11",
        MobEffectInstance(CONFUSION, 100),
        MobEffectInstance(DAMAGE_BOOST, 1200)
    )
    val BRANIK_12 = brewedBranik("branik_12",
        MobEffectInstance(BLINDNESS, 60),
        MobEffectInstance(CONFUSION, 100),
        MobEffectInstance(DAMAGE_BOOST, 1200),
        MobEffectInstance(REGENERATION, 200)
    )
    val BRANIK_14 = brewedBranik("branik_14",
        MobEffectInstance(CONFUSION, 200, 1)
    )
    val BRANIK_18 = brewedBranik("branik_18",
        MobEffectInstance(CONFUSION, 200, 1)
    )

    val BRANIK2L_11 = brewedBranik("branik2l_11",
        MobEffectInstance(BLINDNESS, 60),
        MobEffectInstance(CONFUSION, 200, 1),
        MobEffectInstance(DAMAGE_BOOST, 1800),
        MobEffectInstance(DAMAGE_RESISTANCE, 1200),
        MobEffectInstance(REGENERATION, 300)
    )
    val BRANIK2L_12 = brewedBranik("branik2l_12",
        MobEffectInstance(BLINDNESS, 100),
        MobEffectInstance(CONFUSION, 400, 1),
        MobEffectInstance(DAMAGE_BOOST, 1800, 1),
        MobEffectInstance(DAMAGE_RESISTANCE, 1800, 1),
        MobEffectInstance(REGENERATION, 600)
    )
    val BRANIK2L_14 = brewedBranik("branik2l_14",
        MobEffectInstance(BLINDNESS, 100),
        MobEffectInstance(CONFUSION, 400, 1),
        MobEffectInstance(DAMAGE_BOOST, 1800, 1),
        MobEffectInstance(DAMAGE_RESISTANCE, 1800, 1),
        MobEffectInstance(REGENERATION, 600)
    )
    val BRANIK2L_18 = brewedBranik("branik2l_18",
        MobEffectInstance(BLINDNESS, 100),
        MobEffectInstance(CONFUSION, 400, 1),
        MobEffectInstance(DAMAGE_BOOST, 1800, 1),
        MobEffectInstance(DAMAGE_RESISTANCE, 1800, 1),
        MobEffectInstance(REGENERATION, 600)
    )




    val playerLastDrinkTime: MutableMap<UUID, Long> = mutableMapOf()

    val ALL_BRANIK_ITEMS = listOf(
        BRANIK_BOTTLE, TWO_ELCO_BOTTLE, BRANIK_CAP, BRANIK_LABEL,
        BRANIK_11, BRANIK_12, BRANIK_14, BRANIK_18, BRANIK2L_11, BRANIK2L_12, BRANIK2L_14, BRANIK2L_18,
        HOPS, HOPS_SEEDS, GRAINS, MALT
    )

    fun registerItems(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}