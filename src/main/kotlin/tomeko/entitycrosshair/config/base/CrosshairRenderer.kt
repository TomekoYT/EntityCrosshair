@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config.base

import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.mixins.GuiIngameAccessor
import tomeko.entitycrosshair.mixins.MinecraftAccessor
import tomeko.entitycrosshair.utils.Constants
import java.awt.image.BufferedImage
import kotlin.math.ceil
import net.minecraft.client.renderer.GlStateManager as GL

object CrosshairRenderer {
    private var drawingImage = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB)

    var defaultTextureLocation: ResourceLocation? =
        mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_default", DynamicTexture(15, 15))
    var entityTextureLocation: ResourceLocation? =
        mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_entity", DynamicTexture(15, 15))

    private var whiteTexture = DynamicTexture(15, 15)
    private var whiteTextureLocation = mc.textureManager.getDynamicTextureLocation(Constants.MOD_ID, whiteTexture)
    private var vanilla = DynamicTexture(15, 15)
    private var vanillaLocation = mc.textureManager.getDynamicTextureLocation(Constants.MOD_ID, vanilla)

    fun updateDefaultTexture(image: OneImage) {
        drawingImage = image.image
        val texture = DynamicTexture(drawingImage)
        defaultTextureLocation = mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_default", texture)
        updateWhiteTexture()
    }

    fun updateEntityTexture(image: OneImage) {
        drawingImage = image.image
        val texture = DynamicTexture(drawingImage)
        entityTextureLocation = mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_entity", texture)
        updateWhiteTexture()
    }

    fun updateWhiteTexture() {
        whiteTexture = DynamicTexture(drawingImage.width, drawingImage.height)
        for (posY in 0..<drawingImage.height) {
            for (posX in 0..<drawingImage.width) {
                val color = drawingImage.getRGB(posX, posY)
                if (color shr 24 == 0) continue
                whiteTexture.textureData[posX + posY * drawingImage.width] = -1
            }
        }
        whiteTexture.updateDynamicTexture()
        whiteTextureLocation = mc.textureManager.getDynamicTextureLocation(Constants.MOD_ID, whiteTexture)
    }

    fun updateVanilla() {
        val icon = TextureUtil.readBufferedImage(mc.resourceManager.getResource(Gui.icons).inputStream)
        val size = icon.width * 16 / 256
        vanilla = DynamicTexture(size, size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                icon.getRGB(x, y).let {
                    if (it != -16777216) vanilla.textureData[x + y * size] = it
                }
            }
        }
        vanilla.updateDynamicTexture()
        vanillaLocation = mc.textureManager.getDynamicTextureLocation(Constants.MOD_ID, vanilla)
    }

    @SubscribeEvent
    fun onPackSwitch(event: TextureStitchEvent) {
        updateVanilla()
    }

    fun drawCrosshair(entityRenderer: EntityRenderer) {
        val parent = RenderGameOverlayEvent((mc as MinecraftAccessor).timer.renderPartialTicks, ScaledResolution(mc))
        MinecraftForge.EVENT_BUS.post(RenderGameOverlayEvent.Pre(parent, RenderGameOverlayEvent.ElementType.CROSSHAIRS))
        if ((mc.ingameGUI as? GuiIngameAccessor)?.shouldShowCrosshair() == true) {
            entityRenderer.setupOverlayRendering()
            GL.pushMatrix()
            GL.tryBlendFuncSeparate(770, 771, 1, 0)
            GL.enableBlend()
            GL.enableAlpha()

            GL11.glColor4f(1f, 1f, 1f, 1f)

            val activeTexture = if (lookingAtEntity()) entityTextureLocation else defaultTextureLocation

            mc.textureManager.bindTexture(activeTexture)

            val mcScale = UResolution.scaleFactor.toFloat()
            GL.scale(1 / mcScale, 1 / mcScale, 1f)
            val crosshair = if (lookingAtEntity()) EntityCrosshairConfig.entityCanvaConfig.newCurrentCrosshair else EntityCrosshairConfig.generalCanvaConfig.newCurrentCrosshair
            GL.translate(crosshair.offsetX.toFloat(), crosshair.offsetY.toFloat(), 0f)
            GL.translate((UResolution.windowWidth / 2).toFloat(), (UResolution.windowHeight / 2).toFloat(), 0f)
            GL.rotate(crosshair.rotation.toFloat(), 0f, 0f, 1f)
            val configSize = if (lookingAtEntity()) EntityCrosshairConfig.entityCanvaConfig.canvaSize else EntityCrosshairConfig.generalCanvaConfig.canvaSize
            val scale = crosshair.scale / 100f
            val textureSize = 16
            val autoScaledSize = if (configSize % 2 == 0) 16 else 15
            val size = ceil(autoScaledSize * mcScale * scale).toInt()
            val translation = ceil((if (crosshair.centered) -autoScaledSize / 2f else -7f) * mcScale * scale)
            GL.translate(translation, translation, 0f)
            Gui.drawScaledCustomSizeModalRect(
                0,
                0,
                0f,
                0f,
                textureSize,
                textureSize,
                size,
                size,
                textureSize.toFloat(),
                textureSize.toFloat()
            )
            GL11.glColor4f(1f, 1f, 1f, 1f)
            GL.disableBlend()
            GL.popMatrix()
        }
        MinecraftForge.EVENT_BUS.post(
            RenderGameOverlayEvent.Post(
                parent,
                RenderGameOverlayEvent.ElementType.CROSSHAIRS
            )
        )
    }

    private fun lookingAtEntity(): Boolean {
        val entity = mc.objectMouseOver?.entityHit ?: return false
        return !entity.isInvisible
    }
}