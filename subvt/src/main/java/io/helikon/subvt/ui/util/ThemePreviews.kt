package io.helikon.subvt.ui.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class ThemePreviews
