package io.helikon.subvt.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import io.helikon.subvt.R
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.theme.SubVTTheme
import io.helikon.subvt.ui.util.ThemePreviews

data class Tab(
    val title: String,
    val activeImageResourceId: Int,
    val inactiveImageResourceId: Int,
    val content: @Composable () -> Unit,
)

@Composable
fun TabContainer(
    tab: Tab,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(75.dp)
                .noRippleClickable { onSelect() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = if (isSelected) tab.activeImageResourceId else tab.inactiveImageResourceId),
            contentDescription = "",
        )
        Text(
            modifier = Modifier.offset(0.dp, (-2).dp),
            text = tab.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
        )
    }
}

@Composable
fun TabLayout(
    modifier: Modifier = Modifier,
    tabs: Array<Tab>,
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            for (i in tabs.indices) {
                val tab = tabs[i]
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surface,
                            )
                            .zIndex(if (i == selectedTabIndex) 10f else 1f),
                ) {
                    tab.content()
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                        .zIndex(12f)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                            MaterialTheme.colorScheme.surface,
                                        ),
                                ),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .padding(
                            vertical = 0.dp,
                            horizontal = dimensionResource(id = R.dimen.common_padding),
                        )
                        .navigationBarsPadding()
                        .align(Alignment.BottomCenter)
                        .offset(x = 0.dp, y = -dimensionResource(id = R.dimen.common_padding))
                        .zIndex(15f),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier =
                        Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(
                                dimensionResource(id = R.dimen.common_padding),
                                12.dp,
                            )
                            .fillMaxWidth(),
                ) {
                    for (i in tabs.indices) {
                        TabContainer(tabs[i], isSelected = selectedTabIndex == i) {
                            if (selectedTabIndex != i) {
                                selectedTabIndex = i
                            }
                        }
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun TabLayoutPreview() {
    SubVTTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            val tabs =
                arrayOf(
                    Tab(
                        title = stringResource(id = R.string.network_tab_title),
                        activeImageResourceId = R.drawable.tab_icon_network_active,
                        inactiveImageResourceId = R.drawable.tab_icon_network_inactive,
                        content = {
                            Text("network")
                        },
                    ),
                    Tab(
                        title = stringResource(id = R.string.my_validators_tab_title),
                        activeImageResourceId = R.drawable.tab_icon_network_active,
                        inactiveImageResourceId = R.drawable.tab_icon_network_inactive,
                        content = {
                            Text("my vals")
                        },
                    ),
                    Tab(
                        title = stringResource(id = R.string.notifications_tab_title),
                        activeImageResourceId = R.drawable.tab_icon_network_active,
                        inactiveImageResourceId = R.drawable.tab_icon_network_inactive,
                        content = {
                            Text("notifs")
                        },
                    ),
                    Tab(
                        title = stringResource(id = R.string.network_reports_tab_title),
                        activeImageResourceId = R.drawable.tab_icon_network_active,
                        inactiveImageResourceId = R.drawable.tab_icon_network_inactive,
                        content = {
                            Text("network reports")
                        },
                    ),
                )
            TabLayout(tabs = tabs)
        }
    }
}
