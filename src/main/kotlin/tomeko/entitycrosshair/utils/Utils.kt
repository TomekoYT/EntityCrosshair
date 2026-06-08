@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.utils

import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.utils.*
import tomeko.entitycrosshair.config.elements.CanvaConfig
import tomeko.entitycrosshair.config.elements.CrosshairEntry
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

fun notify(message: String) = Notifications.INSTANCE.send(Constants.MOD_NAME, message)

fun posToIndex(x: Int, y: Int): Int =
    x + y * 32

fun indexToPos(index: Int): Pos =
    Pos(index % 32, index / 32)

fun export(image: BufferedImage?, name: String): String {
    image ?: return ""
    val path = Constants.CACHES_PATH + name + ".png"
    OneImage(image).save(path)
    return path
}

fun save(canvaConfig: CanvaConfig, image: OneImage?) {
    image ?: return
    val base64 = toBase64(image.image)
    canvaConfig.newCrosshairs.forEach {
        if (it.img == base64) {
            it.loadFrom(canvaConfig.newCurrentCrosshair)
            return
        }
    }
    val entry = CrosshairEntry(canvaConfig)
    entry.loadFrom(canvaConfig.newCurrentCrosshair)
    entry.img = base64
    canvaConfig.newCrosshairs.add(entry)
}

fun toBufferedImage(string: String): BufferedImage? {
    val bytes = Base64.getDecoder().decode(string)
    return ImageIO.read(ByteArrayInputStream(bytes))
}

fun toBase64(image: BufferedImage): String {
    val byteOut = ByteArrayOutputStream()
    ImageIO.write(image, "png", byteOut)
    val encoded = Base64.getEncoder().encodeToString(byteOut.toByteArray())
    byteOut.close()
    return encoded
}

fun copy(image: Image?) {
    image ?: return
    IOUtils.copyImageToClipboard(image)
    notify("Crosshair has been copied to clipboard.")
}