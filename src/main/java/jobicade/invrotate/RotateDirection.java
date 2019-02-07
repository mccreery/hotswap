package jobicade.invrotate;

import java.util.Collections;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid=InventoryRotate.MODID, value={Side.CLIENT})
public enum RotateDirection {
    UP(-1, new KeyBinding("key.invrotate.rotateUp", Keyboard.KEY_K, "key.categories.misc"), new KeyBinding("key.invrotate.rotateRowUp", Keyboard.KEY_L, "key.categories.misc")),
    DOWN(1, new KeyBinding("key.invrotate.rotateDown", Keyboard.KEY_J, "key.categories.misc"), new KeyBinding("key.invrotate.rotateRowDown", Keyboard.KEY_H, "key.categories.misc"));

    private final int rowOffset;
    private final KeyBinding keyBinding, keyBindingRow;

    private RotateDirection(int offset, KeyBinding keyBinding, KeyBinding keyBindingRow) {
        this.rowOffset = offset;
        this.keyBinding = keyBinding;
        this.keyBindingRow = keyBindingRow;
    }

    public void rotate(InventoryPlayer inventory, boolean wholeRow) {
        if(wholeRow) {
            Collections.rotate(inventory.mainInventory, rowOffset * 9);
        } else {
            Collections.rotate(new NthSubList<>(inventory.mainInventory, inventory.currentItem, 9), rowOffset);
        }
    }

    private void rotate(boolean wholeRow) {
        rotate(Minecraft.getMinecraft().player.inventory, wholeRow);
        InventoryRotate.NET_WRAPPER.sendToServer(new RotateMessage(this, wholeRow));
    }

    private void onKeyInput() {
        if(keyBindingRow.isPressed()) {
            rotate(true);
        } else if(keyBinding.isPressed()) {
            rotate(false);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(KeyInputEvent event) {
        if(Minecraft.getMinecraft().inGameHasFocus) {
            for(RotateDirection direction : values()) {
                direction.onKeyInput();
            }
        }
    }

    public static void registerKeyBindings() {
        for(RotateDirection direction : values()) {
            ClientRegistry.registerKeyBinding(direction.keyBinding);
            ClientRegistry.registerKeyBinding(direction.keyBindingRow);
        }
    }
}
