package io.helikon.subvt.ui.screen.network.status.panel

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.ui.component.BezierCurve
import io.helikon.subvt.ui.component.BezierCurveStyle
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

private const val COUNT_OFFSET = 100

@Composable
fun ValidatorCountChart(
    modifier: Modifier = Modifier,
    validatorCountHistory: List<Int>,
) {
    var isLaunched by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(validatorCountHistory) {
        isLaunched = true
    }
    val visibleRatio by animateFloatAsState(
        targetValue =
            if (isLaunched) {
                1.0f
            } else {
                0.0f
            },
        animationSpec =
            tween(durationMillis = 250, easing = LinearEasing),
        label = "",
    )
    val mid = validatorCountHistory.average()
    val min = mid - COUNT_OFFSET
    val max = mid + COUNT_OFFSET
    val brush = Brush.horizontalGradient(listOf(Color.blue(), Color.green()))
    BezierCurve(
        modifier = modifier.alpha(visibleRatio),
        points = validatorCountHistory.map { it.toFloat() },
        minPoint = min.toFloat(),
        maxPoint = max.toFloat(),
        style =
            BezierCurveStyle.CurveStroke(
                brush = brush,
                stroke = Stroke(width = 8.dp.value),
            ),
        visibleRatio = visibleRatio,
    )
}

@Composable
fun ValidatorCountPanel(
    modifier: Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    title: String,
    validatorCountHistory: List<Int>,
    validatorCount: Int?,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .height(128.dp)
                .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                .background(Color.panelBg(isDark))
                .noRippleClickable {
                    onClick()
                },
    ) {
        if (validatorCountHistory.isNotEmpty()) {
            ValidatorCountChart(
                modifier =
                    Modifier
                        .fillMaxSize(),
                validatorCountHistory = validatorCountHistory,
            )
            ValidatorCountChart(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .offset(0.dp, 5.dp)
                        .blur(5.dp)
                        .alpha(0.4f),
                validatorCountHistory = validatorCountHistory,
            )
        }
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.common_padding)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = Font.light(dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp),
                    color = Color.text(isDark),
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.arrow_right_small),
                    contentDescription = "",
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text =
                    if (validatorCount == null) {
                        "-"
                    } else {
                        "$validatorCount"
                    },
                style = Font.medium(28.sp),
                color = Color.text(isDark),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ValidatorCountPanelPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorCountPanel(
            modifier = Modifier,
            title = stringResource(id = R.string.network_status_active_validators),
            validatorCountHistory =
                listOf(
                    1180,
                    1180,
                    1187,
                    1182,
                    1193,
                    1186,
                    1194,
                    1195,
                    1197,
                    1178,
                    1177,
                    1179,
                    1185,
                    1173,
                    1175,
                    1177,
                ),
            validatorCount = 123,
        ) {
            // no-op
        }
    }
}
