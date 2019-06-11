package jobicade.hotswap;

import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class HotSwapProxy {
    public abstract void init();
    public abstract void rotateAndNotify(int rows, boolean wholeRow);
    public abstract IMessage onRotateServer(RotateMessage message, MessageContext context);
    public abstract void trySuppressInvTweaks();

    protected static final void rotate(EntityPlayer player, int rows, boolean wholeRow) {
        if(!player.isSpectator()) {
            if(wholeRow) {
                Collections.rotate(player.inventory.mainInventory, rows * 9);

                for(int i = 0; i < 9; i++) {
                    player.inventory.getStackInSlot(i).setAnimationsToGo(5);
                }
            } else {
                Collections.rotate(new StepList<>(player.inventory.mainInventory, player.inventory.currentItem, 9), rows);
                player.inventory.getCurrentItem().setAnimationsToGo(5);
            }
        }
    }
}
