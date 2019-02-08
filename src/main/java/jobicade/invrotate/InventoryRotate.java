package jobicade.invrotate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=InventoryRotate.MODID, name="Inventory Rotate", version="0.1")
public class InventoryRotate {
    public static final String MODID = "invrotate";
    public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void init(FMLInitializationEvent event) {
        RotateDirection.registerKeyBindings();
        NET_WRAPPER.registerMessage(InventoryRotate::onRotateServer, RotateMessage.class, 0, Side.SERVER);
    }

    private static IMessage onRotateServer(RotateMessage message, MessageContext context) {
        if(message.isValid()) {
            EntityPlayerMP player = context.getServerHandler().player;
            message.getDirection().rotate(player, message.isWholeRow());
        }
        return null;
    }
}
