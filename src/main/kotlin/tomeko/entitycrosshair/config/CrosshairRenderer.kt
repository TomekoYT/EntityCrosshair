//? if = 1.8.9 {
/*@file:Suppress("UnstableAPIUsage")
*///?}

package tomeko.entitycrosshair.config

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.libs.universal.UResolution
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.GlStateManager as GL
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import tomeko.entitycrosshair.mixins.GuiIngameAccessor
import tomeko.entitycrosshair.mixins.MinecraftAccessor
import tomeko.entitycrosshair.utils.Constants
import java.awt.image.BufferedImage
*///?} else {
import com.mojang.blaze3d.platform.NativeImage
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.EntityHitResult
import tomeko.entitycrosshair.utils.Constants
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
//?}
import kotlin.math.ceil

object CrosshairRenderer {
    //? if = 1.8.9 {
    /*private var drawingImage = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB)
    *///?} else {
    private val mc = Minecraft.getInstance()
    //?}

    //? if >= 26.1 {
    fun register() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "crosshair")
        ) { guiGraphicsExtractor, _ ->
            if (EntityCrosshairConfig.enabled) render(guiGraphicsExtractor)
        }
    }
    //?}

    //? if = 1.8.9 {
    /*var defaultTextureLocation: ResourceLocation? =
        mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_default", DynamicTexture(Constants.MIN_CANVAS_SIZE, Constants.MIN_CANVAS_SIZE))
    var entityTextureLocation: ResourceLocation? =
        mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_entity", DynamicTexture(Constants.MIN_CANVAS_SIZE, Constants.MIN_CANVAS_SIZE))
    *///?} else {
    private var defaultTextureLocation: Identifier? = null
    private var entityTextureLocation: Identifier? = null
    //?}

    //? if = 1.8.9 {
    /*private var whiteTexture = DynamicTexture(Constants.MIN_CANVAS_SIZE, Constants.MIN_CANVAS_SIZE)
    private var whiteTextureLocation = mc.textureManager.getDynamicTextureLocation(Constants.MOD_ID, whiteTexture)
    private var vanilla = DynamicTexture(Constants.MIN_CANVAS_SIZE, Constants.MIN_CANVAS_SIZE)
    private var vanillaLocation = mc.textureManager.getDynamicTextureLocation(Constants.MOD_ID, vanilla)
      *///?} else {
    private var defaultTextureSize = Constants.MIN_CANVAS_SIZE
    private var entityTextureSize = Constants.MIN_CANVAS_SIZE
    //?}

    fun updateDefaultTexture(
        //? if = 1.8.9 {
        /*image: OneImage
        *///?} else {
        pngBytes: ByteArray
        //?}
    ) {
        //? if = 1.8.9 {
        /*drawingImage = image.image
        val texture = DynamicTexture(drawingImage)
        defaultTextureLocation = mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_default", texture)
        updateWhiteTexture()
        *///?} else {
        val (loc, size) = uploadTexture("${Constants.MOD_ID}_default", pngBytes) ?: return
        defaultTextureLocation = loc
        defaultTextureSize = size
        //?}
    }

    fun updateEntityTexture(
        //? if = 1.8.9 {
        /*image: OneImage
        *///?} else {
        pngBytes: ByteArray
        //?}
    ) {
        //? if = 1.8.9 {
        /*drawingImage = image.image
        val texture = DynamicTexture(drawingImage)
        entityTextureLocation = mc.textureManager.getDynamicTextureLocation("${Constants.MOD_ID}_entity", texture)
        updateWhiteTexture()
        *///?} else {
        val (loc, size) = uploadTexture("${Constants.MOD_ID}_entity", pngBytes) ?: return
        entityTextureLocation = loc
        entityTextureSize = size
        //?}
    }

    //? if = 1.8.9 {
    /*fun updateWhiteTexture() {
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
            val textureSize = Constants.MIN_CANVAS_SIZE + 1 - configSize % 2
            val autoScaledSize = Constants.MIN_CANVAS_SIZE + 1 - configSize % 2
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
    *///?} else {
    private fun uploadTexture(name: String, pngBytes: ByteArray): Pair<Identifier, Int>? {
        val nativeImage = try {
            NativeImage.read(ByteArrayInputStream(pngBytes))
        } catch (_: Exception) {
            return null
        }
        val texture = DynamicTexture({ name }, nativeImage)
        val location = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)
        mc.textureManager.register(location, texture)
        return location to nativeImage.width
    }

    private fun shouldShow(): Boolean {
        val player = mc.player ?: return false
        val debugScreenShowing =
        //? if >= 26.2 {
                /*mc.gui.hud.debugOverlay.showDebugScreen()
                *///?} else {
            mc.gui.debugOverlay.showDebugScreen()
        //?}
        val screen =
        //? if >= 26.2 {
                /*mc.gui.screen()
                *///?} else {
            mc.screen
        //?}

        if (!EntityCrosshairConfig.showInGuis && screen != null) return false
        if (!EntityCrosshairConfig.showInThirdPerson && !mc.options.cameraType.isFirstPerson) return false
        if (EntityCrosshairConfig.showInSpectator && player.isSpectator) return true
        if (EntityCrosshairConfig.showInDebug && debugScreenShowing) return true
        if (debugScreenShowing) return false
        if (player.isSpectator && !EntityCrosshairConfig.showInSpectator) return false
        return true
    }
    //?}

    private fun lookingAtEntity(): Boolean {
        //? if = 1.8.9 {
        /*val entity = mc.objectMouseOver?.entityHit ?: return false
        *///?} else {
        val hit = mc.hitResult as? EntityHitResult ?: return false
        val entity: Entity = hit.entity
        //?}
        return !entity.isInvisible
    }

    //? if >= 26.1 {
    private fun render(guiGraphics: GuiGraphicsExtractor) {
        if (!shouldShow()) return

        val entityMode = lookingAtEntity()
        val crosshair = if (entityMode) EntityCrosshairConfig.entitySet.current else EntityCrosshairConfig.generalSet.current
        val textureLocation = (if (entityMode) entityTextureLocation else defaultTextureLocation) ?: return
        val textureSize = if (entityMode) entityTextureSize else defaultTextureSize
        if (textureSize <= 0) return

        val window = mc.window
        val scale = crosshair.scale / 100f
        val autoScaledSize = Constants.MIN_CANVAS_SIZE + 1 - textureSize % 2
        val size = ceil(autoScaledSize * scale).toInt().coerceAtLeast(1)
        val textureScale = size.toFloat() / textureSize
        val translation = ceil((if (crosshair.centered) -autoScaledSize / 2f else -7f) * scale)

        val pose = guiGraphics.pose()
        pose.pushMatrix()
        pose.translate(
            (window.guiScaledWidth / 2f) + crosshair.offsetX,
            (window.guiScaledHeight / 2f) + crosshair.offsetY
        )
        pose.rotate(crosshair.rotation.toFloat())
        pose.translate(translation, translation)
        pose.scale(textureScale, textureScale)

        drawTexture(guiGraphics, textureLocation, 0, 0, textureSize, textureSize, textureSize, textureSize)

        pose.popMatrix()
    }

    private fun drawTexture(
        guiGraphics: GuiGraphicsExtractor,
        texture: Identifier,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int,
    ) {
        guiGraphics.blit(
            net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            texture,
            x, y,
            0f, 0f,
            width, height,
            textureWidth, textureHeight,
        )
    }

    fun BufferedImage.toPngBytes(): ByteArray {
        val out = java.io.ByteArrayOutputStream()
        javax.imageio.ImageIO.write(this, "png", out)
        return out.toByteArray()
    }
    //?}
}