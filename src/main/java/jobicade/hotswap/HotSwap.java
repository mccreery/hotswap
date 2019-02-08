package jobicade.hotswap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/*
 * Dependencies commented out because Forge changed modid at the beginning
 * of 1.11, from "Forge" to "forge". The minimum version is somewhere in
 * Forge 1.10, so it is impossible to be compatible with both 1.10 and 1.11
 * while checking for Forge. There is also no point in checking the
 * dependency because all version of Forge starting at 1.11 satisfy it.
 */

@Mod(modid=HotSwap.MODID, name="HotSwap", version="0.1",
//    dependencies="required-after:forge@[12.18.1.2092,)", // minimum Forge/upper bound: EventBusSubscriber
    acceptedMinecraftVersions="[1.11,1.13)",
    acceptableRemoteVersions="*")
public class HotSwap {
    public static final String MODID = "hotswap";
    public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void init(FMLInitializationEvent event) {
        RotateDirection.registerKeyBindings();
        NET_WRAPPER.registerMessage(HotSwap::onRotateServer, RotateMessage.class, 0, Side.SERVER);
    }

    private static IMessage onRotateServer(RotateMessage message, MessageContext context) {
        if(message.isValid()) {
            EntityPlayerMP player = context.getServerHandler().player;
            message.getDirection().rotate(player, message.isWholeRow());
        }
        return null;
    }
}
