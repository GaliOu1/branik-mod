package honziggi.branik_mod.energy.gui

import honziggi.branik_mod.energy.menu.MaltingMachineMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class MaltingMachineScreen(
    menu: MaltingMachineMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<MaltingMachineMenu>(menu, inventory, title) {

    private val texture = ResourceLocation("branik_mod", "textures/gui/malting_machine_gui.png")

    init {
        imageWidth = 176
        imageHeight = 166
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(graphics)
        super.render(graphics, mouseX, mouseY, delta)
        renderTooltip(graphics, mouseX, mouseY)

        // Draw temperature text
        val temperature = menu.getTemperature()
        val text = "Temperature: $temperature/120 °C"
        graphics.drawString(font, text, leftPos + 55, topPos + 8, 0xFFFFFF)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight)

        // Fire animation
        val burn = menu.getBurnProgressScaled(13)
        if (burn > 0) {
            graphics.blit(
                texture,
                leftPos + 82,
                topPos + 46 + 13 - burn,
                176,
                13 - burn,
                14,
                burn + 1
            )
        }
        // Malting progress animation
        val progress = menu.getMaltingProgressScaled(24) // 24 pixels wide maximum
        if (progress > 0) {
            graphics.blit(
                texture,
                leftPos + 95, // X position where your purple arrow starts
                topPos + 35,  // Y position where your purple arrow is
                176,          // U position inside your texture (start drawing purple arrow)
                14,           // V position inside your texture
                progress + 1, // Width of how much to draw
                16            // Height of your arrow
            )
        }

        val temperature = menu.getTemperature()
        val maxTemperature = 120

        // Thermometer animation
        val thermoX = leftPos + 151
        val thermoY = topPos + 18
        val thermoWidth = 7
        val thermoHeight = 53

        val fillHeight = (temperature.toFloat() / maxTemperature.toFloat() * thermoHeight).toInt()

        // Background bar
        graphics.fill(thermoX, thermoY, thermoX + thermoWidth, thermoY + thermoHeight, 0xFF222222.toInt())

        // Filled mercury
        graphics.fill(thermoX, thermoY + thermoHeight - fillHeight, thermoX + thermoWidth, thermoY + thermoHeight, 0xFFFF4500.toInt())
    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
    }
}
