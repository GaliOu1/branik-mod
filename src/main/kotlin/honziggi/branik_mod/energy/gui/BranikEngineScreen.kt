package honziggi.branik_mod.energy.gui

import honziggi.branik_mod.energy.menu.BranikEngineMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class BranikEngineScreen (
    menu: BranikEngineMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<BranikEngineMenu>(menu, inventory, title) {

    private val texture = ResourceLocation("branik_mod", "textures/gui/branik_engine_gui.png")

    init {
        imageWidth = 176
        imageHeight = 166
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(graphics)
        super.render(graphics, mouseX, mouseY, delta)
        renderTooltip(graphics, mouseX, mouseY)

        val stored = menu.blockEntity.clientEnergyStored
        val capacity = menu.blockEntity.energyStorage.maxEnergyStored

        //FE stored
        println("GUI client: stored=$stored")
        graphics.drawString(font, "Energy: $stored/$capacity FE", leftPos + 5, topPos + 6, 0xFFFFFF)

        //FE per tick generation
        val fePerTick = menu.data.get(2)
        val color = if (fePerTick > 0) 0x00FF00 else 0xFFFFFF
        graphics.drawString(font, "Generation: $fePerTick FE/t", leftPos + 8, topPos + 16, color)

        // Tooltip for energy bar
        val barX = leftPos + 140
        val barY = topPos + 35
        val barWidth = 6
        val barHeight = 36

        if (mouseX in barX..(barX + barWidth) && mouseY in barY..(barY + barHeight)) {
            val capacity = menu.blockEntity.energyStorage.maxEnergyStored
            graphics.renderTooltip(font, Component.literal("Stored Energy: $stored / $capacity FE"), mouseX, mouseY)
        }
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight)

        // Fire animation
        val burn = menu.getBurnProgressScaled(13)
        if (burn > 0) {
            graphics.blit(
                texture,
                leftPos + 45,
                topPos + 27 + 12 - burn,
                176,
                13 - burn,
                14,
                burn + 1
            )
        }
        // Energy bar with outline
        val stored = menu.blockEntity.clientEnergyStored
        val capacity = menu.blockEntity.energyStorage.maxEnergyStored

        if (capacity > 0 && stored > 0) {
            val fullHeight = 36 // total height of energy bar
            val barHeight = (stored.toFloat() / capacity.toFloat() * fullHeight).toInt()

            val barXStart = leftPos + 140 // position near output slots
            val barYStart = topPos + 35
            val barWidth = 6

            val barYEnd = barYStart + fullHeight

            // --- 1. Draw the dark gray outline ---
            graphics.fill(
                barXStart - 1, barYStart - 1,
                barXStart + barWidth + 1, barYEnd + 1,
                0xFF333333.toInt()
            )

            // --- 2. Draw empty background (black inside the outline) ---
            graphics.fill(
                barXStart, barYStart,
                barXStart + barWidth, barYEnd,
                0xFF000000.toInt()
            )

            // --- 3. Draw the aquamarine filled part ---
            graphics.fill(
                barXStart, barYEnd - barHeight,
                barXStart + barWidth, barYEnd,
                0xFF7FFFD4.toInt()
            )
        }

    }
    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
    }
}