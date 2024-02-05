package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import io.helikon.subvt.R
import io.helikon.subvt.ui.component.BezierCurve
import io.helikon.subvt.ui.component.BezierCurveStyle
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.util.ThemePreviews
import kotlin.math.sin

@Composable
fun BlockWave(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    blockNumber: Long?,
) {
    val progress = remember { Animatable(0.0f) }
    var frequency by remember { mutableFloatStateOf(0.0f) }
    var speed by remember { mutableIntStateOf(0) }
    LaunchedEffect(blockNumber) {
        progress.snapTo(0.0f)
        frequency = (Math.random() * 0.5 + 0.85).toFloat()
        speed = (Math.random() * 130 + 200).toInt()
        progress.animateTo(
            1.0f,
            animationSpec =
                tween(
                    durationMillis = 6000,
                    easing = LinearEasing,
                ),
        )
    }
    val heightFluctuation = sin(Math.PI * 2 * 4 * progress.value).toFloat() / 60
    val height = progress.value + heightFluctuation
    val offset = (progress.value * speed).toInt()
    val amplitude =
        sin(Math.PI * height).toFloat() * 0.035f / (frequency * frequency / 1.2f)
    val points = mutableListOf<Float>()
    for (i in offset..(120 + offset)) {
        points.add(sin(Math.PI * 2 * frequency / 120 * i).toFloat() * amplitude + height)
    }
    Box(
        modifier =
            modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color.blockWaveViewBg(isDark = isDark))
                .size(dimensionResource(id = R.dimen.network_status_block_wave_size)),
    ) {
        BezierCurve(
            modifier = Modifier.fillMaxSize(),
            points = points,
            maxPoint = 1.0f,
            minPoint = 0.0f,
            style =
                BezierCurveStyle.Fill(
                    brush =
                        Brush.horizontalGradient(
                            listOf(
                                Color.green(),
                                Color.blue(),
                            ),
                        ),
                ),
            visibleRatio = 1.0f,
        )
    }
}

@ThemePreviews
@Composable
fun BlockWavePreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        BlockWave(
            isDark = isSystemInDarkTheme(),
            blockNumber = 12_345_678,
        )
    }
}
