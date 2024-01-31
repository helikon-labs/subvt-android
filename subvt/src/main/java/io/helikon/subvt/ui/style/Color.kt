package io.helikon.subvt.ui.style

import androidx.compose.ui.graphics.Color as ComposeColor

object Color {
    private fun color(
        isDark: Boolean,
        light: Long,
        dark: Long,
    ) = if (isDark) {
        ComposeColor(dark)
    } else {
        ComposeColor(light)
    }

    fun transparent() = ComposeColor(0x00000000)

    fun lightGray() = ComposeColor(0xFF748E9D)

    fun actionButtonBg() = ComposeColor(0xFF3A6DFF)

    fun actionButtonBgDisabled(isDark: Boolean) = color(isDark, 0xFF668EFF, 0xFF2A54BD)

    fun actionButtonBgPressed(isDark: Boolean) = color(isDark, 0xFF1D53EE, 0xFF1D53EE)

    fun actionButtonDisabledText() = ComposeColor(0x7AF5F5F5)

    fun actionButtonShadow() = ComposeColor(0xFF3A6DFF)

    fun actionButtonText() = ComposeColor(0xFFF5F5F5)

    fun addItemButtonText(isDark: Boolean) = color(isDark, 0xFF3B6EFF, 0xFF00E927)

    fun barChartBg(isDark: Boolean) = color(isDark, 0x33041A26, 0xCC041A26)

    fun bg(isDark: Boolean) = color(isDark, 0xFFF5F5F5, 0xFF041A25)

    fun bgMorphLeft(isDark: Boolean) = color(isDark, 0x7A2AFA4D, 0xFF3A6DFF)

    fun bgMorphLeftInactive() = ComposeColor(0x7AA8A8A8)

    fun bgMorphMiddle(isDark: Boolean) = color(isDark, 0x26F5F5F5, 0x26F5F5F5)

    fun bgMorphRight(isDark: Boolean) = color(isDark, 0x7A3A6DFF, 0xFF2AFA4D)

    fun bgMorphRightInactive() = ComposeColor(0x7A6D6D6D)

    fun bgMorphRightError(isDark: Boolean) = color(isDark, 0xFFFF002D, 0xFFFF002D)

    fun blockWaveViewBg(isDark: Boolean) = color(isDark, 0xFFF5F5F5, 0x19F5F5F5)

    fun blue() = ComposeColor(0xFF3B6FFF)

    fun black() = ComposeColor(0xFF000000)

    fun panelBg(isDark: Boolean) = color(isDark, 0xAAEAEAEA, 0x770E2938)

    fun enablePushNotifsButtonText(isDark: Boolean) = color(isDark, 0xFF3A6DFF, 0xFF00E927)

    fun green() = ComposeColor(0xFF02E62C)

    fun itemListSelectionIndicator() = ComposeColor(0xFF02E62C)

    fun itemListDivider(isDark: Boolean) = color(isDark, 0x260E2938, 0x26F5F5F5)

    fun networkButtonBg(isDark: Boolean) = color(isDark, 0xFFEAEAEA, 0xFF133547)

    fun networkButtonShadow(isDark: Boolean) = color(isDark, 0xFF3A6DFF, 0xFF01080D)

    fun networkSelectionOverlayBg(isDark: Boolean) = color(isDark, 0xE5F5F5F5, 0xE5041A25)

    fun networkSelectorClosedBg(isDark: Boolean) = color(isDark, 0x99F5F5F5, 0x59041A26)

    fun networkSelectorOpenBg(isDark: Boolean) = color(isDark, 0xFFEAEAEA, 0xFF143446)

    fun onboardingPageIndicatorBg(isDark: Boolean) = color(isDark, 0xFFC4C6C8, 0xFF3B4B54)

    fun popupOverlayBg(isDark: Boolean) = color(isDark, 0xE5F5F5F5, 0xE5041A26)

    fun progress() = ComposeColor(0xFF3B6EFF)

    fun remainingTime(isDark: Boolean) = color(isDark, 0xFF3C4C55, 0xFFA2A9AC)

    fun snackbarAction(isDark: Boolean) = color(isDark, 0xFF3A6DFF, 0xFF00E927)

    fun snackbarBg(isDark: Boolean) = color(isDark, 0xFFEAEAEA, 0xFF0E2938)

    fun statusActive() = ComposeColor(0xFF00E927)

    fun statusError() = ComposeColor(0xFFFF002D)

    fun statusIdle() = ComposeColor(0xFF888888)

    fun statusWaiting() = ComposeColor(0xFFE7B415)

    fun tabBarBg(isDark: Boolean) = color(isDark, 0xFFEEEEEE, 0xFF0E2938)

    fun tabBarItemTextActive(isDark: Boolean) = color(isDark, 0xFF3A6DFF, 0xFF00E927)

    fun tabBarItemTextInactive(isDark: Boolean) = color(isDark, 0xFF5D89A3, 0xFF5D89A3)

    fun text(isDark: Boolean) = color(isDark, 0xFF041A25, 0xFFF5F5F5)

    fun tooltipShadow() = ComposeColor(0xFF041A25)

    fun validatorActive(isDark: Boolean) = color(isDark, 0xFF3B6EFF, 0xFF00E927)

    fun validatorInactive() = ComposeColor(0xFFFF002E)

    fun validatorListSortViewBg(isDark: Boolean) = color(isDark, 0xFFEAEAEA, 0xFF143446)
}
