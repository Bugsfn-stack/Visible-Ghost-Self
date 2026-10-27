package com.joel4848.visibleghosts.mixin;

import com.joel4848.visibleghosts.config.ModConfig;
import com.joel4848.visibleghosts.network.ClientNetworking;
import com.joel4848.visibleghosts.network.VisibleGhostsPackets.OverrideState;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Replaces vanilla's very faint invisibility alpha (0x27FFFFFF) with the
 * configured ghost transparency and a pale blue tint.
 *
 * This code path is only reached by entities that are being drawn as
 * "translucent invisible", which MixinInvisibilityEntity limits to the players
 * the current mode wants shown (plus vanilla teammates/spectator view).
 */
@Mixin(LivingEntityRenderer.class)
public class MixinInvisibilityAlpha {

    @ModifyConstant(
            method = "render*",
            constant = @Constant(intValue = 654311423),
            require = 0
    )
    private int modifyInvisibilityAlpha(int constant) {
        OverrideState override = ClientNetworking.getCurrentOverride();

        boolean shouldShowTransparent = switch (override) {
            case VISIBLE   -> true;   // Server forces visible.
            case INVISIBLE -> false;  // Server forces invisible; keep vanilla alpha.
            case NONE      -> true;
        };

        if (!shouldShowTransparent) {
            return constant; // Vanilla value unchanged.
        }

        int alpha = ModConfig.getInstance().getGhostTransparency();
        return ColorHelper.getArgb(alpha, 185, 225, 255); // pale ghostly blue tint
    }
}
