package honziggi.branik_mod.energy.gui

import honziggi.branik_mod.energy.menu.FermentationKegMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class FermentationKegScreen (
    menu: FermentationKegMenu, inventory: Inventory, title: Component
): AbstractContainerScreen<FermentationKegMenu>(menu,inventory,title){

    private val texture = ResourceLocation("branik_mod", "textures/gui/fermentation_keg_gui.png")

    init {
        imageWidth = 176
        imageHeight = 166
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(graphics)
        super.render(graphics, mouseX, mouseY, delta)
        renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight)

        // Water tank
        val maxHeight = 48
        val waterFill = (menu.getWaterAmount().toFloat() / menu.getTankCapacity() * maxHeight).toInt()
        graphics.fill(leftPos + 7, topPos + 30 + (maxHeight - waterFill), leftPos + 45, topPos + 78, 0xFF4B00FF.toInt())

        // Beer tank
        val beerFill = (menu.getBeerAmount().toFloat() / menu.getTankCapacity() * maxHeight).toInt()
        graphics.fill(leftPos + 128, topPos + 30 + (maxHeight - beerFill), leftPos + 166, topPos + 78, 0xFFFFFF00.toInt())

        // Bubbles
        if (menu.isFermenting()) {
            val bubbles = (System.currentTimeMillis() / 200 % 4).toInt()
            for (i in 0 until bubbles) {
                val y = topPos + 32 - i * 6
                graphics.fill(leftPos + 94, y, leftPos + 100, y + 6, 0xFFFF0000.toInt())
            }
        }
        println("Water: ${menu.getWaterAmount()}, Beer: ${menu.getBeerAmount()}, Fermenting: ${menu.isFermenting()}")

    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
    }

}