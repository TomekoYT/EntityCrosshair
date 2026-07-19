package tomeko.entitycrosshair.config.base

import cc.polyfrost.oneconfig.config.core.OneColor

interface CanvaConfig<T : CrosshairEntry> {
    var drawerMap: HashMap<Int, Int>
    var newCrosshairs: ArrayList<T>
    var penColor: OneColor
    var canvaSize: Int
    var newCurrentCrosshair: T
}
