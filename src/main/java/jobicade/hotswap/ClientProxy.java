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
    private final KeyBinding rowDown = new RegisterOrderKeyBinding("key.hotswap.rotateRowDown", Keyboard.KEY_H, "key.categories.hotswap");
    private final KeyBinding currentDown = new RegisterOrderKeyBinding("key.hotswap.rotateDown", Keyboard.KEY_J, "key.categories.hotswap");
    private final KeyBinding currentUp = new RegisterOrderKeyBinding("key.hotswap.rotateUp", Keyboard.KEY_K, "key.categories.hotswap");
    private final KeyBinding rowUp = new RegisterOrderKeyBinding("key.hotswap.rotateRowUp", Keyboard.KEY_L, "key.categories.hotswap");

    private final ModifierKeyBinding currentScroll = new ModifierKeyBinding("key.hotswap.rotate", Keyboard.KEY_LMENU, "key.categories.hotswap", "hotswap.mouseWheel");
    private final ModifierKeyBinding rowScroll = new ModifierKeyBinding("key.hotswap.rotateRow", KeyModifier.CONTROL, Keyboard.KEY_LMENU, "key.categories.hotswap", "hotswap.mouseWheel");
    private final ModifierKeyBinding swap = new ModifierKeyBinding("key.hotswap.swap", Keyboard.KEY_LMENU, "key.categories.hotswap", "hotswap.slot");

    private InvTweaksSuppressor suppressor;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(rowDown);
        ClientRegistry.registerKeyBinding(currentDown);
        ClientRegistry.registerKeyBinding(currentUp);
        ClientRegistry.registerKeyBinding(rowUp);

        ClientRegistry.registerKeyBinding(currentScroll);
        ClientRegistry.registerKeyBinding(rowScroll);
        ClientRegistry.registerKeyBinding(swap);

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
            if(rowUp.isPressed()) {
                rotate(-1, true);
            } else if(rowDown.isPressed()) {
                rotate(1, true);
            } else if(currentUp.isPressed()) {
                rotate(-1, false);
            } else if(currentDown.isPressed()) {
                rotate(1, false);
            }

            if(swap.isKeyDown()) {
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
            if(rowScroll.overrides(currentScroll)) {
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
        if(currentScroll.isKeyDown()) {
            rotate(Integer.signum(event.getDwheel()), false);
            event.setCanceled(true);
            return true;
        } else {
            return false;
        }
    }

    private boolean tryScrollRow(MouseEvent event) {
        if(rowScroll.isKeyDown()) {
            rotate(Integer.signum(event.getDwheel()), true);
            event.setCanceled(true);
            return true;
        } else {
            return false;
        }
    }
}
