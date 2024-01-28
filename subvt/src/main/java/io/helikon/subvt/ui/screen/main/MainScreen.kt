package io.helikon.subvt.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.helikon.subvt.R
import io.helikon.subvt.ui.screen.network.status.NetworkStatusScreen
import io.helikon.subvt.ui.screen.notification.list.NotificationsScreen
import io.helikon.subvt.ui.screen.report.network.NetworkReportsScreen
import io.helikon.subvt.ui.screen.validator.my.MyValidatorsScreen

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    MainScreenContent(modifier)
}

@Composable
private fun MainScreenContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        val tabs =
            arrayOf(
                Tab(
                    title = stringResource(id = R.string.network_tab_title),
                    activeImageResourceId = R.drawable.tab_icon_network_active,
                    inactiveImageResourceId = R.drawable.tab_icon_network_inactive,
                    content = {
                        NetworkStatusScreen()
                    },
                ),
                Tab(
                    title = stringResource(id = R.string.my_validators_tab_title),
                    activeImageResourceId = R.drawable.tab_icon_my_validators_active,
                    inactiveImageResourceId = R.drawable.tab_icon_my_validators_inactive,
                    content = {
                        MyValidatorsScreen()
                    },
                ),
                Tab(
                    title = stringResource(id = R.string.notifications_tab_title),
                    activeImageResourceId = R.drawable.tab_icon_notifications_active,
                    inactiveImageResourceId = R.drawable.tab_icon_notifications_inactive,
                    content = {
                        NotificationsScreen()
                    },
                ),
                Tab(
                    title = stringResource(id = R.string.network_reports_tab_title),
                    activeImageResourceId = R.drawable.tab_icon_network_reports_active,
                    inactiveImageResourceId = R.drawable.tab_icon_network_reports_inactive,
                    content = {
                        NetworkReportsScreen()
                    },
                ),
            )
        TabLayout(tabs = tabs)
    }
}
