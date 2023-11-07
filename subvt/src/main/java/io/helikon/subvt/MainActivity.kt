package io.helikon.subvt

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            SubVTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Introduction()
                }
            }
        }
    }
}

@Composable
fun Introduction(modifier: Modifier = Modifier) {
    var launched by remember { mutableStateOf(false) }
    val offsetAnimation by animateDpAsState(
        if (launched) {
            dimensionResource(id = R.dimen.introduction_icon_volume_end_offset)
        } else {
            dimensionResource(id = R.dimen.introduction_icon_volume_start_offset)
        },
        animationSpec = tween(
            durationMillis = integerResource(id = R.integer.introduction_icon_volume_anim_duration_ms),
            delayMillis = integerResource(id = R.integer.introduction_icon_volume_anim_delay_ms),
            easing = EaseOutBack,
        ),
        label = "offset_animation"
    )
    LaunchedEffect(true) {
        launched = true
    }
    Box {
        Image(
            painter = painterResource(id = R.drawable.icon_volume),
            contentDescription = stringResource(id = R.string.introduction_icon_volume_description),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(offsetAnimation, -offsetAnimation),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .safeContentPadding(),
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_title_margin_top)))
            Text(
                text = stringResource(R.string.introduction_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_subtitle_margin_top)))
            Text(
                modifier = Modifier.width(dimensionResource(id = R.dimen.introduction_subtitle_width)),
                text = stringResource(R.string.introduction_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.action_button_height))
                    .width(dimensionResource(id = R.dimen.action_button_width)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.action_button_border_radius)),
            ) {
                Text(
                    text = stringResource(R.string.introduction_get_started),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.introduction_action_button_margin_bottom)))
        }
    }
}

@ThemePreviews
@Composable
fun IntroductionPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            Introduction()
        }
    }
}
