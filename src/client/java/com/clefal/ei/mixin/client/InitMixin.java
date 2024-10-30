package com.clefal.ei.mixin.client;

import com.clefal.ei.detector.ChatDetector;
import com.wynntils.core.WynntilsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WynntilsMod.class)
public class InitMixin {
	@Inject(at = @At("RETURN"), method = "init", remap = false)
	private static void inject(CallbackInfo info) {
		WynntilsMod.registerEventListener(new ChatDetector());
	}
}