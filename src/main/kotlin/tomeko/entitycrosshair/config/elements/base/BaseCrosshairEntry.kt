package tomeko.entitycrosshair.config.elements.base

interface BaseCrosshairEntry {
    var img: String
    var scale: Int
    var rotation: Int
    var offsetX: Int
    var offsetY: Int
    var centered: Boolean

    fun loadFrom(entry: BaseCrosshairEntry) {
        this.img = entry.img
        this.scale = entry.scale
        this.rotation = entry.rotation
        this.offsetX = entry.offsetX
        this.offsetY = entry.offsetY
        this.centered = entry.centered
    }
}