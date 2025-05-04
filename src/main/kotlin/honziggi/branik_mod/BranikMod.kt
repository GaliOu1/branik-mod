package honziggi.branik_mod

import com.honziggi.branik_mod.BranikCreativeTab
import honziggi.branik_mod.registry.BranikMenus
import honziggi.branik_mod.blocks.BranikBlocks
import honziggi.branik_mod.energy.gui.BranikEngineScreen
import honziggi.branik_mod.energy.gui.MaltingMachineScreen
import honziggi.branik_mod.energy.gui.FermentationKegScreen
import honziggi.branik_mod.fluids.BranikFluids
import honziggi.branik_mod.registry.BlockEntities
import honziggi.branik_mod.items.BranikItems
import honziggi.branik_mod.network.NetworkHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(BranikMod.ID)
object BranikMod {
    const val ID = "branik_mod"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        // Register the KDeferredRegister to the mod-specific event bus
        BranikFluids.register(MOD_BUS)
        BranikBlocks.register(MOD_BUS)
        BranikItems.registerItems(MOD_BUS)
        BranikMenus.register(MOD_BUS)
        BlockEntities.register(MOD_BUS)
        BranikCreativeTab.register(MOD_BUS)
        LOGGER.info("BRANIK_ENGINE_II present: ${BranikBlocks.BRANIK_ENGINE_II.isPresent}")
        LOGGER.info("BRANIK_ENGINE_II_ENTITY present: ${BlockEntities.BRANIK_ENGINE_II_ENTITY.isPresent}")


        LOGGER.log(Level.INFO, "All items, block, and menus registered")

        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            })

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
        event.enqueueWork {
            MenuScreens.register(BranikMenus.BRANIK_ENGINE_MENU.get(), ::BranikEngineScreen)
            MenuScreens.register(BranikMenus.MALTING_MACHINE_MENU.get(), ::MaltingMachineScreen)
            MenuScreens.register(BranikMenus.FERMENTATION_KEG_MENU.get(), ::FermentationKegScreen)
            NetworkHandler.register()
        }
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }
}