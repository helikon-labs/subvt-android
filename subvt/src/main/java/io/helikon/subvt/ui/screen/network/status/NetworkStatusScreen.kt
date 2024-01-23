package io.helikon.subvt.ui.screen.network.status

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.helikon.subvt.R
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.theme.Green

@Composable
fun NetworkStatusScreen(
    modifier: Modifier = Modifier,
    viewModel: NetworkStatusViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    viewModel.subscribe()
                } else if (event == Lifecycle.Event.ON_STOP) {
                    viewModel.unsubscribe()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .background(
                        Color.Transparent,
                        RoundedCornerShape(
                            0.dp,
                            0.dp,
                            dimensionResource(id = R.dimen.common_panel_border_radius),
                            dimensionResource(id = R.dimen.common_panel_border_radius),
                        ),
                    )
                    .zIndex(10f),
        ) {
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, 24.dp)
                        .statusBarsPadding(),
            )
            Row(
                modifier =
                    Modifier
                        .padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = stringResource(id = R.string.network_status_title),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier =
                            Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Green),
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
            Spacer(
                modifier =
                    Modifier
                        .padding(0.dp, dimensionResource(id = R.dimen.common_panel_padding)),
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.common_padding), 0.dp)
                    .zIndex(5f)
                    .verticalScroll(rememberScrollState()),
            Arrangement.spacedBy(
                dimensionResource(id = R.dimen.common_panel_padding),
            ),
        ) {
            Column(modifier = Modifier.padding(0.dp).alpha(0f)) {
                Spacer(
                    modifier =
                        Modifier
                            .padding(0.dp, 24.dp)
                            .statusBarsPadding(),
                )
                Text(
                    text = "dummy",
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(
                    modifier =
                        Modifier
                            .padding(0.dp, dimensionResource(id = R.dimen.common_padding)),
                )
            }
            // content begins here
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.common_panel_padding),
                    ),
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(128.dp)
                            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(dimensionResource(id = R.dimen.common_padding))
                            .noRippleClickable {
                                // no-op
                            },
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.network_status_active_validators),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(R.drawable.arrow_right_small),
                                contentDescription = "",
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = (networkStatus?.activeValidatorCount ?: 0).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = dimensionResource(id = R.dimen.network_status_panel_content_font_size).value.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(128.dp)
                            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(dimensionResource(id = R.dimen.common_padding))
                            .noRippleClickable {
                                // no-op
                            },
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = R.string.network_status_inactive_validators),
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = dimensionResource(id = R.dimen.network_status_panel_title_font_size).value.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Image(
                                painter = painterResource(R.drawable.arrow_right_small),
                                contentDescription = "",
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = (networkStatus?.inactiveValidatorCount ?: 0).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = dimensionResource(id = R.dimen.network_status_panel_content_font_size).value.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.common_panel_padding),
                    ),
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(128.dp)
                            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(dimensionResource(id = R.dimen.common_padding))
                            .noRippleClickable {
                                // no-op
                            },
                ) {
                }
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(128.dp)
                            .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.common_panel_border_radius)))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(dimensionResource(id = R.dimen.common_padding))
                            .noRippleClickable {
                                // no-op
                            },
                ) {
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding().padding(0.dp, 48.dp))
        }
    }
}
