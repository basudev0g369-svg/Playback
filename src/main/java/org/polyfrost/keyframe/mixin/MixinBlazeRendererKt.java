package org.polyfrost.keyframe.mixin;

//? if >=26.3 {
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.polyfrost.keyframe.ui.GuiMeshCapture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(targets = "org.polyfrost.compose.minecraft.render.blaze.BlazeRendererKt", remap = false)
public abstract class MixinBlazeRendererKt {

    @Inject(method = "writeTexture", at = @At("HEAD"), require = 0)
    private static void keyframe$mirrorUpload(DynamicTexture texture, ByteBuffer data, int y, int w, int h, CallbackInfo ci) {
        GuiMeshCapture.mirror(texture, data, y, w, h);
    }

}
//?}
