package jobicade.invrotate;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RotateMessage implements IMessage {
    private RotateDirection direction;
    private boolean wholeRow;

    public RotateMessage() {}

    public RotateMessage(RotateDirection direction, boolean wholeRow) {
        this.direction = direction;
        this.wholeRow = wholeRow;
    }

    public RotateDirection getDirection() {
        return direction;
    }

    public boolean isWholeRow() {
        return wholeRow;
    }

    public boolean isValid() {
        return direction != null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        direction = readEnumValueSafe(buf, RotateDirection.class);

        if(direction != null && buf.isReadable()) {
            wholeRow = buf.readBoolean();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuf = buf instanceof PacketBuffer ?
            (PacketBuffer)buf : new PacketBuffer(buf);

        packetBuf.writeEnumValue(direction);
        packetBuf.writeBoolean(wholeRow);
    }

    /**
     * Reads an enum value from a buffer, without throwing exceptions.
     *
     * @param buf The byte buffer to attempt to read from.
     * @param enumClass The class of enum.
     * @return An enum value from the given class, or {@code null}
     * if a valid, in-range varint could not be read.
     * @see PacketBuffer#readEnumValue(Class)
     */
    private static <T extends Enum<T>> T readEnumValueSafe(ByteBuf buf, Class<T> enumClass) {
        int i = 0;

        for(int shift = 0;; shift += 7) {
            // Invalid varint
            if(!buf.isReadable() || shift >= 32) {
                return null;
            }
            byte currentByte = buf.readByte();

            i |= (currentByte & 0x7f) << shift;
            // Terminated varint
            if((currentByte & 0x80) != 0x80) {
                break;
            }
        }

        T[] enumValues = enumClass.getEnumConstants();
        if(i >= 0 && i < enumValues.length) {
            return enumValues[i];
        } else {
            // Varint out of enum range
            return null;
        }
    }
}
