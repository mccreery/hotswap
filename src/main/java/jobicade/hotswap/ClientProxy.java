package jobicade.hotswap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class ClientProxy extends HotSwapProxy {
    private final KeyBinding CURRENT_UP = new KeyBinding("key.hotswap.rotateUp", Keyboard.KEY_K, "key.categories.hotswap");
    private final KeyBinding CURRENT_DOWN = new KeyBinding("key.hotswap.rotateDown", Keyboard.KEY_J, "key.categories.hotswap");
    private final KeyBinding ROW_UP = new KeyBinding("key.hotswap.rotateRowUp", Keyboard.KEY_L, "key.categories.hotswap");
    private final KeyBinding ROW_DOWN = new KeyBinding("key.hotswap.rotateRowDown", Keyboard.KEY_H, "key.categories.hotswap");

    private InvTweaksSuppressor suppressor;

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(CURRENT_UP);
        ClientRegistry.registerKeyBinding(CURRENT_DOWN);
        ClientRegistry.registerKeyBinding(ROW_UP);
        ClientRegistry.registerKeyBinding(ROW_DOWN);

        if(Loader.isModLoaded("inventorytweaks")) {
            suppressor = new InvTweaksSuppressor();
            suppressor.init();
        }
    }

    @Override
    public void rotateAndNotify(int rows, boolean wholeRow) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        if(!player.isSpectator()) {
            trySuppressInvTweaks();
            rotate(player, rows, wholeRow);
            HotSwap.NET_WRAPPER.sendToServer(new RotateMessage(rows, wholeRow));
        }
    }

    @Override
    public IMessage onRotateServer(RotateMessage message, MessageContext context) {
        return null;
    }

    @Override
    public void trySuppressInvTweaks() {
        if(suppressor != null) {
            suppressor.suppressInvTweaks();
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if(Minecraft.getMinecraft().inGameHasFocus) {
            if(ROW_UP.isPressed()) {
                rotateAndNotify(-1, true);
            } else if(ROW_DOWN.isPressed()) {
                rotateAndNotify(1, true);
            } else if(CURRENT_UP.isPressed()) {
                rotateAndNotify(-1, false);
            } else if(CURRENT_DOWN.isPressed()) {
                rotateAndNotify(1, false);
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        if(event.isCancelable() && GuiScreen.isAltKeyDown() && event.getDwheel() != 0) {
            rotateAndNotify(Integer.signum(event.getDwheel()), GuiScreen.isCtrlKeyDown());
            event.setCanceled(true);
        }
    }
}
