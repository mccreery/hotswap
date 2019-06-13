package jobicade.hotswap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class ClientProxy extends CommonProxy {
    private final KeyBinding CURRENT_UP = new RegisterOrderKeyBinding("key.hotswap.rotateUp", Keyboard.KEY_K, "key.categories.hotswap");
    private final KeyBinding CURRENT_DOWN = new RegisterOrderKeyBinding("key.hotswap.rotateDown", Keyboard.KEY_J, "key.categories.hotswap");
    private final KeyBinding ROW_UP = new RegisterOrderKeyBinding("key.hotswap.rotateRowUp", Keyboard.KEY_L, "key.categories.hotswap");
    private final KeyBinding ROW_DOWN = new RegisterOrderKeyBinding("key.hotswap.rotateRowDown", Keyboard.KEY_H, "key.categories.hotswap");

    private final ModifierKeyBinding CURRENT_SCROLL = new ModifierKeyBinding("key.hotswap.rotate", Keyboard.KEY_LMENU, "key.categories.hotswap", "hotswap.mouseWheel");
    private final ModifierKeyBinding ROW_SCROLL = new ModifierKeyBinding("key.hotswap.rotateRow", KeyModifier.CONTROL, Keyboard.KEY_LMENU, "key.categories.hotswap", "hotswap.mouseWheel");
    private final ModifierKeyBinding SWAP = new ModifierKeyBinding("key.hotswap.swap", Keyboard.KEY_LMENU, "key.categories.hotswap", "hotswap.slot");

    private InvTweaksSuppressor suppressor;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(ROW_DOWN);
        ClientRegistry.registerKeyBinding(CURRENT_DOWN);
        ClientRegistry.registerKeyBinding(CURRENT_UP);
        ClientRegistry.registerKeyBinding(ROW_UP);

        ClientRegistry.registerKeyBinding(CURRENT_SCROLL);
        ClientRegistry.registerKeyBinding(ROW_SCROLL);
        ClientRegistry.registerKeyBinding(SWAP);

        if(Loader.isModLoaded("inventorytweaks")) {
            suppressor = new InvTweaksSuppressor();
            suppressor.init();
        }
    }

    @Override
    public void rotate(int rows, boolean wholeRow) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        trySuppressInvTweaks();
        if(HotSwap.rotateLocal(player, rows, wholeRow)) {
            HotSwap.NET_WRAPPER.sendToServer(new RotateMessage(rows, wholeRow));
        }
    }

    @Override
    public void swap(int slot) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        trySuppressInvTweaks();
        if(HotSwap.swapLocal(player, slot)) {
            HotSwap.NET_WRAPPER.sendToServer(new SwapMessage(slot));
        }
    }

    private void trySuppressInvTweaks() {
        if(suppressor != null) {
            suppressor.suppressInvTweaks();
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if(Minecraft.getMinecraft().inGameHasFocus) {
            if(ROW_UP.isPressed()) {
                rotate(-1, true);
            } else if(ROW_DOWN.isPressed()) {
                rotate(1, true);
            } else if(CURRENT_UP.isPressed()) {
                rotate(-1, false);
            } else if(CURRENT_DOWN.isPressed()) {
                rotate(1, false);
            }

            if(SWAP.isKeyDown()) {
                KeyBinding[] keyBindsHotbar = Minecraft.getMinecraft().gameSettings.keyBindsHotbar;

                for(int i = 0; i < keyBindsHotbar.length; i++) {
                    if(keyBindsHotbar[i].isPressed()) {
                        swap(i);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        if(event.isCancelable() && !event.isCanceled() && event.getDwheel() != 0) {
            if(ROW_SCROLL.overrides(CURRENT_SCROLL)) {
                if(!tryScrollRow(event)) {
                    tryScrollCurrent(event);
                }
            } else {
                if(!tryScrollCurrent(event)) {
                    tryScrollRow(event);
                }
            }
        }
    }

    private boolean tryScrollCurrent(MouseEvent event) {
        if(CURRENT_SCROLL.isKeyDown()) {
            rotate(Integer.signum(event.getDwheel()), false);
            event.setCanceled(true);
            return true;
        } else {
            return false;
        }
    }

    private boolean tryScrollRow(MouseEvent event) {
        if(ROW_SCROLL.isKeyDown()) {
            rotate(Integer.signum(event.getDwheel()), true);
            event.setCanceled(true);
            return true;
        } else {
            return false;
        }
    }
}
