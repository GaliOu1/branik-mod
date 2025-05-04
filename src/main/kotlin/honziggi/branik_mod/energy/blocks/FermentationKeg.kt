package honziggi.branik_mod.energy.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.network.NetworkHooks

class FermentationKeg : Block(Properties.copy(Blocks.IRON_BLOCK).strength(2.0f)), EntityBlock {

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    }

    override fun use(
        state: BlockState, level: Level, pos: BlockPos,
        player: Player, hand: InteractionHand, hit: BlockHitResult
    ): InteractionResult {
        val stack = player.getItemInHand(hand)
        val blockEntity = level.getBlockEntity(pos)

        if (blockEntity is FermentationKegEntity) {
            // Fill with water
            if (stack.item == Items.WATER_BUCKET) {
                val filled = blockEntity.waterTank.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0 && !player.isCreative) {
                    player.setItemInHand(hand, ItemStack(Items.BUCKET))
                }
                return InteractionResult.SUCCESS
            }

            // Extract beer into bucket
            val beerBucket = honziggi.branik_mod.fluids.BranikFluids.BRANIK11.bucket.get()
            if (stack.item == Items.BUCKET && blockEntity.beerTank.fluid.amount >= 1000) {
                if (!player.isCreative) {
                    stack.shrink(1)
                    player.addItem(ItemStack(beerBucket))
                }
                blockEntity.beerTank.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                return InteractionResult.SUCCESS
            }

            // Open GUI if not holding buckets
            if (!level.isClientSide) {
                NetworkHooks.openScreen(player as ServerPlayer, blockEntity) { buf ->
                    buf.writeBlockPos(pos)
                }
            }
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }


    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return FermentationKegEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!level.isClientSide) {
            BlockEntityTicker { lvl, pos, st, be ->
                if (be is FermentationKegEntity) {
                    be.tickServer()
                }
            }
        } else null
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }
}
