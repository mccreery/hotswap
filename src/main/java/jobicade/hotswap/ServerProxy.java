package jobicade.hotswap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class ServerProxy extends HotSwapProxy {
    @Override
    public void init() {}

    @Override
    public void rotateAndNotify(int rows, boolean wholeRow) {}

    @Override
    public IMessage onRotateServer(RotateMessage message, MessageContext context) {
        if (message.isValid()) {
            EntityPlayerMP player = context.getServerHandler().player;
            rotate(player, message.getNumRows(), message.isWholeRow());
        }
        return null;
    }
}
