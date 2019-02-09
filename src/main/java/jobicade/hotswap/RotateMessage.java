package jobicade.hotswap;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RotateMessage implements IMessage {
    private int rows;
    private boolean wholeRow;
    private boolean valid = true;

    public RotateMessage() {}

    public RotateMessage(int rows, boolean wholeRow) {
        this.rows = rows;
        this.wholeRow = wholeRow;
    }

    public int getNumRows() {
        return rows;
    }

    public boolean isWholeRow() {
        return wholeRow;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        valid = buf.isReadable(2);
        if(valid) {
            rows = buf.readByte();
            wholeRow = buf.readBoolean();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(rows);
        buf.writeBoolean(wholeRow);
    }
}
