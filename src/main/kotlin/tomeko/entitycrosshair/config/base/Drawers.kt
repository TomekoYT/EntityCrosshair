@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config.base

import cc.polyfrost.oneconfig.images.OneImage
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.entity.EntityCrosshairEntry
import tomeko.entitycrosshair.config.general.GeneralCrosshairEntry
import tomeko.entitycrosshair.utils.toBase64

val EntityDrawer = CrosshairDrawer(
    canvaConfig = EntityCrosshairConfig.entityCanvaConfig,
    entryFactory = ::EntityCrosshairEntry,
    saveAction = { saveCrosshair(EntityCrosshairConfig.entityCanvaConfig, ::EntityCrosshairEntry, it) },
    updateTextureAction = CrosshairRenderer::updateEntityTexture,
)

val GeneralDrawer = CrosshairDrawer(
    canvaConfig = EntityCrosshairConfig.generalCanvaConfig,
    entryFactory = ::GeneralCrosshairEntry,
    saveAction = { saveCrosshair(EntityCrosshairConfig.generalCanvaConfig, ::GeneralCrosshairEntry, it) },
    updateTextureAction = CrosshairRenderer::updateDefaultTexture,
)

fun <T : CrosshairEntry> saveCrosshair(canvaConfig: CanvaConfig<T>, entryFactory: () -> T, image: OneImage?) {
    image ?: return
    val base64 = toBase64(image.image)
    for (existing in canvaConfig.newCrosshairs) {
        if (existing.img == base64) {
            existing.loadFrom(canvaConfig.newCurrentCrosshair)
            return
        }
    }
    val entry = entryFactory()
    entry.loadFrom(canvaConfig.newCurrentCrosshair)
    entry.img = base64
    canvaConfig.newCrosshairs.add(entry)
}