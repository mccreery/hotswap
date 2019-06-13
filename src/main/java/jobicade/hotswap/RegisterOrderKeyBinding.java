package jobicade.hotswap;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

/**
 * A key binding which compares in register order
 * for other register order bindings in the same category.
 */
public class RegisterOrderKeyBinding extends KeyBinding {
    private int cachedIndex = -1;

    public RegisterOrderKeyBinding(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }

    public RegisterOrderKeyBinding(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
        super(description, keyConflictContext, keyCode, category);
    }

    public RegisterOrderKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
    }

    @Override
    public int compareTo(KeyBinding other) {
        if(other instanceof RegisterOrderKeyBinding && getKeyCategory().equals(other.getKeyCategory())) {
            return Integer.compare(getRegisterOrder(), ((RegisterOrderKeyBinding)other).getRegisterOrder());
        } else {
            return super.compareTo(other);
        }
    }

    private int getRegisterOrder() {
        if(cachedIndex == -1) {
            cachedIndex = ArrayUtils.indexOf(Minecraft.getMinecraft().gameSettings.keyBindings, this);
        }
        return cachedIndex;
    }
}
