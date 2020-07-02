package me.jiahuan.stf.server

import android.graphics.Rect

class ScreenInfo(
    /**
     * 手机的屏幕物理尺寸，可能被裁剪
     */
    val contentRect: Rect,
    /**
     * 视频流尺寸，可能裁剪
     */
    val unlockedVideoSize: Size,
    /**
     * 设备方向 (0, 1, 2 or 3)
     */
    val deviceRotation: Int,
    /**
     * 选定视频方向 (-1: disabled, 0: normal, 1: 90° CCW, 2: 180°, 3: 90° CW)
     */
    private val lockedVideoOrientation: Int
) {

    companion object {
        private fun computeVideoSize(w: Int, h: Int, maxSize: Int): Size {
            var contentWidth = w
            var contentHeight = h
            contentWidth = contentWidth and 7.inv()
            contentHeight = contentHeight and 7.inv()
            if (maxSize > 0) {
                val portrait = contentHeight > contentWidth
                var major = if (portrait) contentHeight else contentWidth
                var minor = if (portrait) contentWidth else contentHeight
                if (major > maxSize) {
                    val minorExact = minor * maxSize / major
                    minor = minorExact + 4 and 7.inv()
                    major = maxSize
                }
                contentWidth = if (portrait) minor else major
                contentHeight = if (portrait) major else minor
            }
            return Size(contentWidth, contentHeight)
        }

        fun computeScreenInfo(displayInfo: DisplayInfo, maxSize: Int, lockedVideoOrientation: Int): ScreenInfo {
            val rotation = displayInfo.rotation
            val deviceSize = displayInfo.size
            val contentRect = Rect(0, 0, deviceSize.width, deviceSize.height)
            return ScreenInfo(contentRect, computeVideoSize(contentRect.width(), contentRect.height(), maxSize), rotation, lockedVideoOrientation)
        }
    }

    val videoSize: Size
        get() = if (videoRotation % 2 == 0) {
            unlockedVideoSize
        } else unlockedVideoSize.rotate()

    val videoRotation: Int
        get() = if (lockedVideoOrientation == -1) {
            0
        } else (deviceRotation + 4 - lockedVideoOrientation) % 4

    override fun toString(): String {
        return "ScreenInfo(contentRect=$contentRect, unlockedVideoSize=$unlockedVideoSize, deviceRotation=$deviceRotation, lockedVideoOrientation=$lockedVideoOrientation)"
    }
}