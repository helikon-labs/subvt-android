package io.helikon.subvt.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zedalpha.shadowgadgets.compose.clippedShadow
import io.helikon.subvt.R
import io.helikon.subvt.ui.style.Color
import kotlin.math.min

fun Modifier.scrollHeader(
    isDark: Boolean,
    scrolledRatio: Float,
): Modifier {
    return composed {
        val borderRadius = dimensionResource(id = R.dimen.common_panel_border_radius)
        val clipShape =
            remember {
                RoundedCornerShape(
                    0.dp,
                    0.dp,
                    borderRadius,
                    borderRadius,
                )
            }
        this.then(
            background(
                color =
                    Color
                        .headerSectionBg(isDark)
                        // is slightly transparent
                        .copy(alpha = min(0.93f, scrolledRatio)),
                shape = clipShape,
            )
                .fillMaxWidth()
                .zIndex(10f)
                .clippedShadow(
                    elevation = (10 * min(1.0f, scrolledRatio)).dp,
                    shape = clipShape,
                ),
        )
    }
}
