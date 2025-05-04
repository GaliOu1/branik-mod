package honziggi.branik_mod.energy.blocks

import honziggi.branik_mod.energy.menu.BranikEngineMenu
import honziggi.branik_mod.energy.sync.SyncEngineEnergyPacket
import honziggi.branik_mod.network.NetworkHandler
import honziggi.branik_mod.registry.BlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.minecraftforge.network.PacketDistributor


class BranikEngineEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockEntities.BRANIK_ENGINE_ENTITY.get(), pos, state), MenuProvider {

    val energyStorage = EnergyStorage(20000, 10, 20)
    @JvmField
    var clientEnergyStored: Int = 0

    private val energyCap = LazyOptional.of { energyStorage }
    private val itemCap = LazyOptional.of { itemHandler }

    private var burnTime = 0
    private var maxBurnTime = 0
    private var lastEnergyPerTick = 0
    private var syncEnergyStored = 0

    val itemHandler = object : ItemStackHandler(5) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            if (slot != 0) return false

            val itemId = BuiltInRegistries.ITEM.getKey(stack.item).toString()
            return itemId == "branik_mod:branik_11" || itemId == "branik_mod:branik2l_11"
        }
    }

    var dataSlot: ContainerData = object : ContainerData {

        override fun get(p0: Int): Int {
            return when (p0) {
                0 -> burnTime
                1 -> energyStorage.energyStored
                2 -> getCurrentGeneration()
                3 -> maxBurnTime
                else -> 0
            }
        }

        override fun set(p0: Int, p1: Int) {
            when (p0) {
                0 -> burnTime = p1
                1 -> syncEnergyStored = p1
                2 -> lastEnergyPerTick = p1
                3 -> maxBurnTime = p1
            }
        }

        override fun getCount() = 4
    }


    fun tickServer() {
        if (burnTime > 0) {
            if (energyStorage.energyStored < energyStorage.maxEnergyStored){
                burnTime--
                energyStorage.receiveEnergy(lastEnergyPerTick, false)

                dataSlot.set(0, burnTime)
                dataSlot.set(1, energyStorage.energyStored)
                syncEnergyStored = energyStorage.energyStored

                if (burnTime == 0) {
                    lastEnergyPerTick = 0
                }

                setChanged()
            } else {
                print("Energy Full: ${energyStorage.energyStored}/${energyStorage.maxEnergyStored}")
            }
        }

        if (burnTime <= 0) {
            val stack = itemHandler.getStackInSlot(0)
            if (!stack.isEmpty) {
                val item = stack.item
                val energyPerTick = getEnergyPerTickFor(item)
                val totalEnergy = getItemEnergy(item)
                val duration = totalEnergy / energyPerTick

                if (energyPerTick > 0 && duration > 0) {
                    burnTime = duration
                    maxBurnTime = duration
                    lastEnergyPerTick = energyPerTick

                    stack.shrink(1)
                    tryOutputBeerBottle()

                    dataSlot.set(0, burnTime)
                    val scaled = (energyStorage.energyStored * 10000) / energyStorage.maxEnergyStored
                    dataSlot.set(1, scaled)
                    dataSlot.set(2, lastEnergyPerTick)
                    dataSlot.set(3, maxBurnTime)

                    setChanged()
                }
            }
            val lit = burnTime > 0
            val currentState = level!!.getBlockState(blockPos)
            if (currentState.getValue(BranikEngine.LIT) != lit) {
                level!!.setBlock(blockPos, currentState.setValue(BranikEngine.LIT, lit), 3)
            }
        }

        val packet = SyncEngineEnergyPacket(blockPos, energyStorage.energyStored)
        NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with {
            level!!.getChunkAt(blockPos)
        }, packet)
    }






    private fun tryOutputBeerBottle() {
        val outputItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation("branik_mod", "branik_bottle")).orElse(null)
        if (outputItem != null) {
            val output = ItemStack(outputItem)

            for (slot in 1..4) {
                val current = itemHandler.getStackInSlot(slot)
                if (current.isEmpty) {
                    itemHandler.setStackInSlot(slot, output.copy())
                    break
                } else if (ItemStack.isSameItemSameTags(current, output) && current.count < current.maxStackSize) {
                    current.grow(1)
                    break
                }
            }
        }
    }

    private fun getEnergyPerTickFor(item: Item): Int = when (BuiltInRegistries.ITEM.getKey(item).toString()) {
        "branik_mod:branik_11", "branik_mod:branik2l_11" -> 10
        else -> 0 // fallback
    }
    private fun getItemEnergy(item: Item): Int = when (BuiltInRegistries.ITEM.getKey(item).toString()) {
        "branik_mod:branik_11" -> 500
        "branik_mod:branik2l_11"-> 2000
        else -> 0
    }

    override fun <T> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val itemCapType = CapabilityManager.get(object : CapabilityToken<IItemHandler>() {})
        val energyCapType = CapabilityManager.get(object : CapabilityToken<IEnergyStorage>() {})

        return when {
            cap === itemCapType -> itemCap.cast()
            cap === energyCapType -> energyCap.cast()
            else -> super.getCapability(cap, side)
        }
    }

    fun getCurrentGeneration(): Int {
        return if (burnTime > 0) {
            lastEnergyPerTick
        } else 0
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Energy", energyStorage.serializeNBT())
        tag.putInt("BurnTime", burnTime)
        tag.putInt("MaxBurnTime", maxBurnTime)
        tag.putInt("LastFEPerTick", lastEnergyPerTick)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        val energyTag = tag.get("Energy") as CompoundTag
        println("LOAD hash=${this.hashCode()} | NBT energy=${energyTag.getInt("energy")}")

        itemHandler.deserializeNBT(tag.getCompound("Items"))
        energyStorage.deserializeNBT(tag.get("Energy") as CompoundTag)
        burnTime = tag.getInt("BurnTime")
        maxBurnTime = tag.getInt("MaxBurnTime")
        lastEnergyPerTick = tag.getInt("LastFEPerTick")
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemCap.invalidate()
        energyCap.invalidate()
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.branik_mod.branik_engine")
    }

    override fun createMenu(
        id: Int,
        inventory: Inventory,
        player: Player
    ): AbstractContainerMenu {
        return BranikEngineMenu(id, inventory, this, dataSlot)
    }

}
