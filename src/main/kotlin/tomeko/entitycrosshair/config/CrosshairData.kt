package tomeko.entitycrosshair.config

//? if >= 26.1 {
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import tomeko.entitycrosshair.utils.Constants

@Serializable
data class CrosshairEntry(
    var img: String = "",
    var rotation: Int = 0,
    var offsetX: Int = 0,
    var offsetY: Int = 0,
    var centered: Boolean = true,
) {
    fun loadFrom(entry: CrosshairEntry) {
        this.img = entry.img
        this.rotation = entry.rotation
        this.offsetX = entry.offsetX
        this.offsetY = entry.offsetY
        this.centered = entry.centered
    }

    fun copyTransformFrom(entry: CrosshairEntry): CrosshairEntry = copy(
        rotation = entry.rotation,
        offsetX = entry.offsetX,
        offsetY = entry.offsetY,
        centered = entry.centered,
    )
}

object DefaultCrosshairs {
    const val GENERAL =
        "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAHklEQVR42mNgGH7gPxAMQc0k2wQDyDaji4+G9pADAJoJM827QMwQAAAAAElFTkSuQmCC"
    const val ENTITY =
        "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAALklEQVR42mNgGH7gPxCQrAGmCZkmyiBkRbjYtPEjPmfT3nay/UxRaFMUz0MDAAARS2Od/Bu+BgAAAABJRU5ErkJggg=="
}

@Serializable
data class CrosshairSetData(
    var presets: MutableList<CrosshairEntry> = mutableListOf(),
    var current: CrosshairEntry = CrosshairEntry(),
    var canvasSize: Int = Constants.MIN_CANVAS_SIZE,
    var penColorArgb: Int = -1,
) {
    companion object {
        val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        fun default(defaultImg: String) = CrosshairSetData(
            presets = mutableListOf(),
            current = CrosshairEntry(img = defaultImg),
        )

        fun decode(raw: String, defaultImg: String): CrosshairSetData {
            if (raw.isBlank()) return default(defaultImg)
            return try {
                json.decodeFromString(raw)
            } catch (_: Exception) {
                default(defaultImg)
            }
        }
    }

    fun encode(): String = json.encodeToString(this)
}
//?}