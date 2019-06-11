package jobicade.hotswap;

import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=HotSwap.MODID, name="HotSwap", version="0.2.1",
    acceptedMinecraftVersions="[1.11,1.13)",
    acceptableRemoteVersions="*")
public class HotSwap {
    public static final String MODID = "hotswap";
    public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SidedProxy(clientSide="jobicade.hotswap.ClientProxy", serverSide="jobicade.hotswap.CommonProxy")
    private static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NET_WRAPPER.registerMessage(HotSwap::onRotateServer, RotateMessage.class, 0, Side.SERVER);
        proxy.init();
    }

    /**
     * Performs a rotation on the player's inventory on the client and notifies the server.
     * No operation if called on the server.
     *
     * @param rows The number of rows to rotate: down is positive, up is negative.
     * @param wholeRow {@code true} to rotate all columns, or {@code false} to rotate
     * the column containing the selected hotbar slot only.
     */
    public static void rotate(int rows, boolean wholeRow) {
        proxy.rotate(rows, wholeRow);
    }

    private static IMessage onRotateServer(RotateMessage message, MessageContext context) {
        if(message.isValid()) {
            EntityPlayerMP player = context.getServerHandler().player;
            rotateLocal(player, message.getNumRows(), message.isWholeRow());
        }
        return null;
    }

    /**
     * Performs a rotation on a player's inventory on either side without sending any messages.
     *
     * @param player The player to rotate.
     * @param rows The number of rows to rotate: down is positive, up is negative.
     * @param wholeRow {@code true} to rotate all columns, or {@code false} to rotate
     * the column containing the selected hotbar slot only.
     */
    public static void rotateLocal(EntityPlayer player, int rows, boolean wholeRow) {
        if(!player.isSpectator()) {
            if(wholeRow) {
                Collections.rotate(player.inventory.mainInventory, rows * 9);

                for(int i = 0; i < 9; i++) {
                    player.inventory.getStackInSlot(i).setAnimationsToGo(5);
                }
            } else {
                Collections.rotate(new StepList<>(player.inventory.mainInventory,
                        player.inventory.currentItem, 9), rows);
                player.inventory.getCurrentItem().setAnimationsToGo(5);
            }
        }
    }
}
