package jobicade.invrotate;

import java.util.Collections;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
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

    public void rotate(EntityPlayer player, boolean wholeRow) {
        if(!player.isSpectator()) {
            if(wholeRow) {
                Collections.rotate(player.inventory.mainInventory, rowOffset * 9);

                for(int i = 0; i < 9; i++) {
                    player.inventory.getStackInSlot(i).setAnimationsToGo(5);
                }
            } else {
                Collections.rotate(new NthSubList<>(player.inventory.mainInventory, player.inventory.currentItem, 9), rowOffset);
                player.inventory.getCurrentItem().setAnimationsToGo(5);
            }
        }
    }

    private void rotate(boolean wholeRow) {
        rotate(Minecraft.getMinecraft().player, wholeRow);
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

    @SubscribeEvent
    public static void onMouseInput(MouseEvent event) {
        if(event.isCancelable() && !Minecraft.getMinecraft().player.isSpectator() && GuiScreen.isAltKeyDown() && event.getDwheel() != 0) {
            (event.getDwheel() > 0 ? UP : DOWN).rotate(GuiScreen.isCtrlKeyDown());
            event.setCanceled(true);
        }
    }

    public static void registerKeyBindings() {
        for(RotateDirection direction : values()) {
            ClientRegistry.registerKeyBinding(direction.keyBinding);
            ClientRegistry.registerKeyBinding(direction.keyBindingRow);
        }
    }
}
