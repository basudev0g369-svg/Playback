package org.polyfrost.keyframe.standalone.mixin.ornithe;

import net.minecraft.entity.living.LivingEntity;
import org.polyfrost.keyframe.standalone.playback.Puppet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class Mixin_LivingEntity_Puppet {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
	private void keyframe$afterBaseTick(CallbackInfo ci) {
		Puppet.afterBaseTick((LivingEntity) (Object) this);
	}
}
