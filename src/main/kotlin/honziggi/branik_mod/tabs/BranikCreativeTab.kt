package com.honziggi.branik_mod

import honziggi.branik_mod.blocks.BranikBlocks
import honziggi.branik_mod.fluids.BranikFluids
import honziggi.branik_mod.items.BranikItems
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object BranikCreativeTab {

    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "branik_mod")

    val BRANIK_TAB: RegistryObject<CreativeModeTab> = CREATIVE_MODE_TABS.register("branik_tab") {
        CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.literal("Branik"))
            .icon { BranikItems.BRANIK2L_12.get().defaultInstance }
            .displayItems { _, output ->

                BranikItems.ALL_BRANIK_ITEMS.forEach { output.accept(it.get()) }
                BranikBlocks.ALL_BRANIK_BLOCKS.forEach{ output.accept(it.get()) }
                BranikFluids.beerBuckets.forEach {output.accept(it.get())}

            }
            .build()
    }

    fun register(eventBus: IEventBus) {
        CREATIVE_MODE_TABS.register(eventBus)
    }
}
