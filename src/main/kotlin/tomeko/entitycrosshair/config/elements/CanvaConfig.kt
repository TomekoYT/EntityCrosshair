package tomeko.entitycrosshair.config.elements

import cc.polyfrost.oneconfig.config.core.OneColor

abstract class CanvaConfig {
    abstract var drawerMap: HashMap<Int, Int>

    abstract var drawer: Drawer

    abstract var newCrosshairs: ArrayList<CrosshairEntry>

    abstract var penColor: OneColor

    abstract var canvaSize: Int

    abstract var newCurrentCrosshair: CrosshairEntry
}