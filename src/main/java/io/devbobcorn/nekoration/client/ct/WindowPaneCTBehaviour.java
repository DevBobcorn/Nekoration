package io.devbobcorn.nekoration.client.ct;

import io.devbobcorn.nekoration.blocks.WindowPaneBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class WindowPaneCTBehaviour extends WindowCTBehaviour {

    @Override
    protected boolean supportsSourceBlock(BlockState state) {
        return state.getBlock() instanceof WindowPaneBlock;
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        // Pane multipart geometry can render quads on directions that vanilla face-culling
        // considers hidden, especially at corners. Build CT context for all directions so
        // those quads still receive connected tile indices.
        return true;
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        // Pane side quads on negative horizontal axes have mirrored UV orientation
        // compared to the positive faces; flip CT U mapping to keep atlas order consistent.
        return face == Direction.NORTH || face == Direction.WEST;
    }
}
