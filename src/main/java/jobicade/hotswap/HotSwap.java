package jobicade.hotswap;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=HotSwap.MODID, name="HotSwap", version="0.2",
    acceptedMinecraftVersions="[1.11,1.13)",
    acceptableRemoteVersions="*")
public class HotSwap {
    public static final String MODID = "hotswap";
    public static final SimpleNetworkWrapper NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SidedProxy(clientSide="jobicade.hotswap.ClientProxy", serverSide="jobicade.hotswap.ServerProxy")
    public static HotSwapProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NET_WRAPPER.registerMessage(proxy::onRotateServer, RotateMessage.class, 0, Side.SERVER);
        proxy.init();
    }
}
