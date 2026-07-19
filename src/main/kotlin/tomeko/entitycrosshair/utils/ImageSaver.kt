@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.utils

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.utils.IOUtils
import cc.polyfrost.oneconfig.utils.Notifications
import java.awt.Image
*///?} else {
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
//?}
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
//? if >= 26.1 {
import java.io.File
//?}
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
    //? if = 1.8.9 {
    /*val path = Constants.CACHES_PATH + name + ".png"
    OneImage(image).save(path)
    return path
    *///?} else {
    Constants.CACHES_FILE.mkdirs()
    val file = File(Constants.CACHES_PATH + name + ".png")
    ImageIO.write(image, "png", file)
    return file.absolutePath
    //?}
}

fun toBufferedImage(base64: String): BufferedImage? {
    if (base64.isBlank()) return null
    return try {
        val bytes = Base64.getDecoder().decode(base64)
        ImageIO.read(ByteArrayInputStream(bytes))
    } catch (_: Exception) {
        null
    }
}

fun toBase64(image: BufferedImage): String {
    val byteOut = ByteArrayOutputStream()
    ImageIO.write(image, "png", byteOut)
    val encoded = Base64.getEncoder().encodeToString(byteOut.toByteArray())
    byteOut.close()
    return encoded
}

//? if >= 26.1 {
fun getImageFromClipboard(): BufferedImage? {
    val contents: Transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null) ?: return null

    try {
        if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            val files = contents.getTransferData(DataFlavor.javaFileListFlavor)
            if (files is List<*> && files.isNotEmpty() && files[0] is File) {
                ImageIO.read(files[0] as File)?.let { return it }
            }
        }
    } catch (_: Exception) {
    }

    return try {
        if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            (contents.getTransferData(DataFlavor.imageFlavor) as? Image)?.toBufferedImage()
        } else null
    } catch (_: UnsupportedFlavorException) {
        null
    } catch (_: Exception) {
        null
    }
}
//?}

fun copyToClipboard(image: Image?) {
    image ?: return
    //? if = 1.8.9 {
    /*IOUtils.copyImageToClipboard(image)
    Notifications.INSTANCE.send(Constants.MOD_NAME, "Crosshair has been copied to clipboard.")
    *///?} else {
    val transferable = object : Transferable {
        override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
        override fun isDataFlavorSupported(flavor: DataFlavor?) = flavor == DataFlavor.imageFlavor
        override fun getTransferData(flavor: DataFlavor?): Any {
            if (flavor != DataFlavor.imageFlavor) throw UnsupportedFlavorException(flavor)
            return image
        }
    }
    Toolkit.getDefaultToolkit().systemClipboard.setContents(transferable, null)
    Notifications.send(Constants.MOD_NAME, "Crosshair has been copied to clipboard.")
    //?}
}
