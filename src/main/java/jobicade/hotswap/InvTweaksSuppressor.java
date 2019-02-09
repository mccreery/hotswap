package jobicade.hotswap;

import static net.minecraftforge.fml.common.eventhandler.EventPriority.LOWEST;
import static net.minecraftforge.fml.common.gameevent.TickEvent.Phase.START;

import invtweaks.InvTweaks;
import invtweaks.InvTweaksConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class InvTweaksSuppressor {
    private boolean enablePending;

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority=LOWEST)
    public void onTick(ClientTickEvent event) {
        if(event.phase == START && enablePending) {
            setAutoRefill(true);
            enablePending = false;
        }
    }

    public void suppressInvTweaks() {
        if(getAutoRefill()) {
            setAutoRefill(false);
            enablePending = true;
        }
    }

    private boolean getAutoRefill() {
        return InvTweaksConfig.VALUE_TRUE.equals(InvTweaks.getConfigManager().getConfig().getProperty(InvTweaksConfig.PROP_ENABLE_AUTO_REFILL));
    }

    private void setAutoRefill(boolean enabled) {
        InvTweaks.getConfigManager().getConfig().setProperty(InvTweaksConfig.PROP_ENABLE_AUTO_REFILL, enabled ? InvTweaksConfig.VALUE_TRUE : InvTweaksConfig.VALUE_FALSE);
    }
}
