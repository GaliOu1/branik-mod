package honziggi.branik_mod.registry

import honziggi.branik_mod.BranikMod
import honziggi.branik_mod.energy.menu.BranikEngineIIIMenu
import honziggi.branik_mod.energy.menu.BranikEngineIIMenu
import honziggi.branik_mod.energy.menu.BranikEngineMenu
import honziggi.branik_mod.energy.menu.FermentationKegMenu
import honziggi.branik_mod.energy.menu.MaltingMachineMenu
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject

object BranikMenus {

    val MENUS: DeferredRegister<MenuType<*>> =
        DeferredRegister.create(Registries.MENU, BranikMod.ID)

    private inline fun <reified T : AbstractContainerMenu> registerMenu(
        name: String,
        noinline factory: (id:Int, inv: Inventory, data: FriendlyByteBuf?) -> T
    ): RegistryObject<MenuType<T>> {
        return MENUS.register(name) {
            IForgeMenuType.create(factory)
        }
    }
    val BRANIK_ENGINE_MENU = registerMenu("branik_engine_menu", BranikEngineMenu::fromNetwork)
    val BRANIK_ENGINE_II_MENU = registerMenu("branik_engine_ii_menu", BranikEngineIIMenu::fromNetwork)
    val BRANIK_ENGINE_III_MENU = registerMenu("branik_engine_iii_menu", BranikEngineIIIMenu::fromNetwork)

    val MALTING_MACHINE_MENU = registerMenu("malting_machine_menu", MaltingMachineMenu::fromNetwork)
    val FERMENTATION_KEG_MENU = registerMenu("fermentation_keg_menu", FermentationKegMenu::fromNetwork)


    fun register(eventBus: IEventBus) {
        MENUS.register(eventBus)
    }
}

