package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;
import com.devbobcorn.nekoration.items.TweakItem;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.devbobcorn.nekoration.network.S2CUpdateCustomBlockData;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;

public class CustomBlock extends Block implements EntityBlock {
    public static final IntegerProperty LIGHT = BlockStateProperties.LEVEL;

    public CustomBlock(Properties settings) {
        super(settings);
    }
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(LIGHT);
    }

    @SuppressWarnings("resource")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        CustomBlockEntity te = (CustomBlockEntity)world.getBlockEntity(pos);

        // Both Sides Changes...
        if (item == Items.AIR){
            // Toggle arrow hint...
            te.showHint = !te.showHint;
        } else if (item instanceof TweakItem){
            // Pass and let the item apply the tweaks...
            return InteractionResult.PASS;
        } else if (item == ModItems.PALETTE.get()){
            // Dye me!
            CompoundTag nbt = itemStack.getTag();
            if (nbt != null) {
                byte a = nbt.getByte(PaletteItem.ACTIVE);
                int[] c = nbt.getIntArray(PaletteItem.COLORS);
                if (c.length > a) {
                    // So c[a] is the color we need...
                    te.color[0] = NekoColors.getRed(c[a]);
                    te.color[1] = NekoColors.getGreen(c[a]);
                    te.color[2] = NekoColors.getBlue(c[a]);
                }
            } else {
                te.color[0] = PaletteItem.DEFAULT_COLOR_SET[0].getRed();
                te.color[1] = PaletteItem.DEFAULT_COLOR_SET[0].getGreen();
                te.color[2] = PaletteItem.DEFAULT_COLOR_SET[0].getBlue();
            }
            te.retint = true;
        } else if (item == ModItems.PAW.get()) {
            // Undye me!
            te.retint = false;
        } else if (item instanceof BlockItem){
            if (((BlockItem)item).getBlock() instanceof CustomBlock)
                return InteractionResult.PASS;

            BlockState newState = ((BlockItem)item).getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, itemStack, hit));
            
            // getStateForPlacement() might get a null return value sometimes (The block is not placable
            // in this BlockPlaceContext), in this occasion, we'll use its default block state...
            if (newState == null)
                newState = ((BlockItem)item).getBlock().defaultBlockState();
            
            // If we still can't get a valid block state, pass...
            if (newState == null)
                return InteractionResult.PASS;

            if (te.displayState == newState)
                return InteractionResult.PASS;
            else {
                te.displayState = newState;
            }
        } else return InteractionResult.PASS;

        
        if (!te.getLevel().isClientSide) { // Tell clients to update this block entity...
            te.setChanged(); // client setChanged() will be called when they receive the packet below
            
            final S2CUpdateCustomBlockData packet = new S2CUpdateCustomBlockData(te.getBlockPos(), te.dir, te.offset, te.retint, te.showHint, te.color, te.displayState);
            ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        ItemStack stack = new ItemStack(this.asItem());
        return Collections.singletonList(stack);
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomBlockEntity(pos, state);
    }
}
