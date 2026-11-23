package org.polyfrost.keyframe.standalone.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.polyfrost.keyframe.standalone.playback.Keyframe;
import org.polyfrost.keyframe.standalone.playback.ScreenReplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "org.polyfrost.oneconfig.internal.OneConfig", remap = false)
abstract class Mixin_OneConfig_ScreenState {
	// OneConfig hides HUDs while a screen is open, and the editor screen stays open through preview and export
	@ModifyExpressionValue(
		method = "prepareHud",
		at = @At(value = "INVOKE", target = "Lorg/polyfrost/oneconfig/api/platform/v1/ScreenPlatform;current()Ljava/lang/Object;"),
		require = 0
	)
	private static Object keyframe$recordedScreen(Object screen) {
		return Keyframe.INSTANCE.getPreviewing() ? ScreenReplay.INSTANCE.getPreviewed() : screen;
	}
}
