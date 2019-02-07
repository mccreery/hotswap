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
    UP(-9, new KeyBinding("key.invrotate.rotateUp", Keyboard.KEY_K, "key.categories.misc")),
    DOWN(9, new KeyBinding("key.invrotate.rotateDown", Keyboard.KEY_J, "key.categories.misc"));

    private final int offset;
    private final KeyBinding keyBinding;

    private RotateDirection(int offset, KeyBinding keyBinding) {
        this.offset = offset;
        this.keyBinding = keyBinding;
    }

    public void rotate(InventoryPlayer inventory, boolean wholeRow) {
        Collections.rotate(inventory.mainInventory, offset);
    }

    private void onKeyInput() {
        if(keyBinding.isPressed()) {
            boolean wholeRow = false;

            rotate(Minecraft.getMinecraft().player.inventory, wholeRow);
            InventoryRotate.NET_WRAPPER.sendToServer(new RotateMessage(this, wholeRow));
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
        }
    }
}
