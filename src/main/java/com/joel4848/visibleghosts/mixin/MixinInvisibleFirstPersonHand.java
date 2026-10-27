package com.joel4848.visibleghosts.mixin;

import com.joel4848.visibleghosts.config.ModConfig;
import com.joel4848.visibleghosts.network.ClientNetworking;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla skips drawing your first-person hand while you are invisible.
 * When the current mode/override wants your own invisible player shown,
 * pretend the check says "not invisible" so the fist is drawn again.
 */
@Mixin(HeldItemRenderer.class)
public class MixinInvisibleFirstPersonHand {

    @WrapOperation(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "isInvisible()Z"),
            require = 0
    )
    private boolean visibleghosts$showOwnHand(Entity entity, Operation<Boolean> original) {
        boolean invisible = original.call(entity);
        if (!invisible) {
            return false;
        }

        boolean show = switch (ClientNetworking.getCurrentOverride()) {
            case VISIBLE   -> true;
            case INVISIBLE -> false;
            case NONE      -> ModConfig.getInstance().shouldShow(true);
        };
        return !show;
    }
}
