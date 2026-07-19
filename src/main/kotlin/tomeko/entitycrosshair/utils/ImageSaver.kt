@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.utils

import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.utils.IOUtils
import cc.polyfrost.oneconfig.utils.Notifications
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

fun Image.toBufferedImage(): BufferedImage {
    if (this is BufferedImage) return this
    val bufferedImage = BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val graphics2D = bufferedImage.createGraphics()
    graphics2D.drawImage(this, 0, 0, null)
    graphics2D.dispose()
    return bufferedImage
}

fun export(image: BufferedImage?, name: String): String {
    image ?: return ""
    val path = Constants.CACHES_PATH + name + ".png"
    OneImage(image).save(path)
    return path
}

fun toBufferedImage(base64: String): BufferedImage? {
    val bytes = Base64.getDecoder().decode(base64)
    return ImageIO.read(ByteArrayInputStream(bytes))
}

fun toBase64(image: BufferedImage): String {
    val byteOut = ByteArrayOutputStream()
    ImageIO.write(image, "png", byteOut)
    val encoded = Base64.getEncoder().encodeToString(byteOut.toByteArray())
    byteOut.close()
    return encoded
}

fun copyToClipboard(image: Image?) {
    image ?: return
    IOUtils.copyImageToClipboard(image)
    Notifications.INSTANCE.send(Constants.MOD_NAME, "Crosshair has been copied to clipboard.")
}
