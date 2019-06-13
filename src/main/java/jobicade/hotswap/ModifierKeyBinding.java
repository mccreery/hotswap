package jobicade.hotswap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

/**
 * A keybinding for mouse scroll with a prefix/modifier.
 * Modifier bindings (ctrl, alt, shift) are expanded to allow both sides.
 */
public class ModifierKeyBinding extends RegisterOrderKeyBinding {
    private final String suffixKey;

    public ModifierKeyBinding(String description, int keyCode, String category, String suffixKey) {
        super(description, new ModifierConflictContext(suffixKey), keyCode, category);
        this.suffixKey = suffixKey;
    }

    public ModifierKeyBinding(String description, KeyModifier keyModifier, int keyCode, String category, String suffixKey) {
        super(description, new ModifierConflictContext(suffixKey), keyModifier, keyCode, category);
        this.suffixKey = suffixKey;
    }

    public boolean overrides(KeyBinding other) {
        return getKeyModifier() != KeyModifier.NONE && other.getKeyModifier() == KeyModifier.NONE;
    }

    @Override
    public boolean isKeyDown() {
        KeyModifier modifier = getModifier();

        if(modifier != null) {
            /*
             * Spoof IN_GAME so that a modifier of NONE doesn't return false
             * because our conflict context doesn't conflict with IN_GAME.
             * Also enforces the L/R equality for modifier keys.
             */
            return getKeyConflictContext().isActive()
                    && getKeyModifier().isActive(KeyConflictContext.IN_GAME)
                    && modifier.isActive(KeyConflictContext.IN_GAME);
        } else {
            return super.isKeyDown();
        }
    }

    @Override
    public String getDisplayName() {
        KeyModifier modifier = getModifier();

        if(modifier != null) {
            String tail = getLocalizedComboName(modifier, I18n.format(suffixKey));
            return getLocalizedComboName(getKeyModifier(), tail);
        } else if(getKeyCode() == Keyboard.KEY_NONE) {
            return super.getDisplayName();
        } else {
            return I18n.format("hotswap.plus", super.getDisplayName(), I18n.format(suffixKey));
        }
    }

    /**
     * Checks the corresponding modifier for the bound keyCode.
     * @return The corresponding modifier for the bound keyCode, or {@code null}.
     */
    private KeyModifier getModifier() {
        for(KeyModifier modifier : KeyModifier.MODIFIER_VALUES) {
            if(modifier.matches(getKeyCode())) {
                return modifier;
            }
        }
        return null;
    }

    /**
     * Localizes the key modifier with any tail, e.g. "CTRL + Test"
     */
    private String getLocalizedComboName(KeyModifier modifier, String tail) {
        switch(modifier) {
            case CONTROL:
                String localizationFormatKey = Minecraft.IS_RUNNING_ON_MAC
                        ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
                return I18n.format(localizationFormatKey, tail);
            case SHIFT:
                return I18n.format("forge.controlsgui.shift", tail);
            case ALT:
                return I18n.format("forge.controlsgui.alt", tail);
            default:
                return tail;
        }
    }

    /**
     * A conflict context which conflicts only with contexts with the same suffix.
     */
    private static class ModifierConflictContext implements IKeyConflictContext {
        private final String suffixKey;

        public ModifierConflictContext(String suffixKey) {
            this.suffixKey = suffixKey;
        }

        @Override
        public boolean isActive() {
            return KeyConflictContext.IN_GAME.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return other instanceof ModifierConflictContext
                    && ((ModifierConflictContext)other).suffixKey.equals(suffixKey);
        }
    };
}
