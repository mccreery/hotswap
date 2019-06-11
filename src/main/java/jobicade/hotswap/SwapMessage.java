package jobicade.hotswap;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SwapMessage implements IMessage {
    private int slot;
    private boolean valid = true;

    public SwapMessage() {}

    public SwapMessage(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        valid = buf.isReadable();
        if(valid) { this.slot = buf.readByte(); }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slot);
    }
}
