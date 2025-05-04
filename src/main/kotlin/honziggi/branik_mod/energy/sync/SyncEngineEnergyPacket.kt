package honziggi.branik_mod.energy.sync

import honziggi.branik_mod.energy.blocks.BranikEngineEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class SyncEngineEnergyPacket(
    val pos: BlockPos,
    val energy: Int
) {
    companion object {
        fun encode(packet: SyncEngineEnergyPacket, buf: FriendlyByteBuf) {
            buf.writeBlockPos(packet.pos)
            buf.writeInt(packet.energy)
        }

        fun decode(buf: FriendlyByteBuf): SyncEngineEnergyPacket {
            return SyncEngineEnergyPacket(buf.readBlockPos(), buf.readInt())
        }

        fun handle(packet: SyncEngineEnergyPacket, context: Supplier<NetworkEvent.Context>) {
            val ctx = context.get()
            ctx.enqueueWork {
                val level = Minecraft.getInstance().level
                val be = level?.getBlockEntity(packet.pos)
                if (be is BranikEngineEntity) {
                    be.clientEnergyStored = packet.energy

                    println("CLIENT SYNC → BranikEngine at ${packet.pos}: $packet.energy FE")

                }
            }
            ctx.packetHandled = true
        }
    }
}

