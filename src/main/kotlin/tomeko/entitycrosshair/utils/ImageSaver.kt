@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.utils

//? if forge {
/*import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.utils.Notifications
*///?} else {
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
//?}
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.util.Base64
import java.util.concurrent.TimeUnit
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
    //? if forge {
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

private const val OSASCRIPT_TIMEOUT_SECONDS = 5L

private fun notifyUser(message: String) {
    //? if forge {
    //Notifications.INSTANCE.send(Constants.MOD_NAME, message)
    //?} else {
    Notifications.send(Constants.MOD_NAME, message)
    //?}
}

private fun jsString(s: String): String = buildString {
    append('"')
    for (c in s) {
        when {
            c == '\\' -> append("\\\\")
            c == '"' -> append("\\\"")
            c.code !in 0x20..0x7E -> append("\\u%04x".format(c.code))
            else -> append(c)
        }
    }
    append('"')
}

private fun runOsascript(script: String): String? {
    return try {
        val process = ProcessBuilder("/usr/bin/osascript", "-l", "JavaScript", "-e", script)
            .redirectErrorStream(true)
            .start()
        if (!process.waitFor(OSASCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            return null
        }
        val output = process.inputStream.readBytes().toString(Charsets.UTF_8).trim()
        if (process.exitValue() == 0) output else null
    } catch (_: Exception) {
        null
    }
}

private fun macGetImageFromClipboard(): BufferedImage? {
    val fileUrl = runOsascript(
        """
        ObjC.import('AppKit');
        var s = $.NSPasteboard.generalPasteboard.stringForType('public.file-url');
        s.isNil() ? '' : ObjC.unwrap(s)
        """.trimIndent()
    )
    if (!fileUrl.isNullOrBlank() && fileUrl.startsWith("file:")) {
        try {
            ImageIO.read(File(URI(fileUrl)))?.let { return it }
        } catch (_: Exception) {
        }
    }

    val tmp = try {
        File.createTempFile("crosshair-clipboard", ".png")
    } catch (_: Exception) {
        return null
    }
    try {
        val result = runOsascript(
            """
            ObjC.import('AppKit');
            var pb = $.NSPasteboard.generalPasteboard;
            var png = pb.dataForType($.NSPasteboardTypePNG);
            if (png.isNil()) {
              var tiff = pb.dataForType($.NSPasteboardTypeTIFF);
              if (!tiff.isNil()) {
                var rep = $.NSBitmapImageRep.imageRepWithData(tiff);
                png = rep.representationUsingTypeProperties(4, $({}));
              }
            }
            (!png.isNil() && png.writeToFileAtomically(${jsString(tmp.absolutePath)}, true)) ? 'ok' : 'none'
            """.trimIndent()
        )
        if (result?.endsWith("ok") != true || tmp.length() == 0L) return null
        return ImageIO.read(tmp)
    } catch (_: Exception) {
        return null
    } finally {
        tmp.delete()
    }
}

private fun macCopyImageToClipboard(image: Image): Boolean {
    val tmp = try {
        File.createTempFile("crosshair-clipboard", ".png")
    } catch (_: Exception) {
        return false
    }
    try {
        ImageIO.write(image.toBufferedImage(), "png", tmp)
        val result = runOsascript(
            """
            ObjC.import('AppKit');
            var img = $.NSImage.alloc.initWithContentsOfFile(${jsString(tmp.absolutePath)});
            var pb = $.NSPasteboard.generalPasteboard;
            pb.clearContents();
            pb.writeObjects($.NSArray.arrayWithObject(img)) ? 'ok' : 'fail'
            """.trimIndent()
        )
        return result?.endsWith("ok") == true
    } catch (_: Exception) {
        return false
    } finally {
        tmp.delete()
    }
}

private fun awtGetImageFromClipboard(): BufferedImage? {
    val contents: Transferable = try {
        Toolkit.getDefaultToolkit().systemClipboard.getContents(null) ?: return null
    } catch (_: Exception) {
        return null
    }

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

fun getImageFromClipboard(): BufferedImage? =
    if (Debug.isMac) macGetImageFromClipboard() else awtGetImageFromClipboard()

fun copyToClipboard(image: Image?) {
    image ?: return
    if (Debug.isMac) {
        if (macCopyImageToClipboard(image)) {
            notifyUser("Crosshair has been copied to clipboard.")
        } else {
            notifyUser("Failed to copy crosshair to clipboard.")
        }
        return
    }

    val transferable = object : Transferable {
        override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
        override fun isDataFlavorSupported(flavor: DataFlavor?) = flavor == DataFlavor.imageFlavor
        override fun getTransferData(flavor: DataFlavor?): Any {
            if (flavor != DataFlavor.imageFlavor) throw UnsupportedFlavorException(flavor)
            return image
        }
    }
    try {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(transferable, null)
        notifyUser("Crosshair has been copied to clipboard.")
    } catch (e: Exception) {
        notifyUser("Failed to copy crosshair to clipboard: ${e.message}")
    }
}