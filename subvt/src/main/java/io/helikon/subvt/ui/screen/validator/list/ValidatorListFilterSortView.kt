package io.helikon.subvt.ui.screen.validator.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.helikon.subvt.R
import io.helikon.subvt.data.extension.ValidatorFilterOption
import io.helikon.subvt.data.extension.ValidatorSortOption
import io.helikon.subvt.ui.modifier.noRippleClickable
import io.helikon.subvt.ui.style.Color
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.util.ThemePreviews

@Composable
private fun ValidatorListFilterSortOptionView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    text: String,
    isChecked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier =
                Modifier.noRippleClickable {
                    onClick()
                },
            painter =
                painterResource(
                    if (isChecked) {
                        R.drawable.checkbox_checked
                    } else {
                        R.drawable.checkbox_unchecked
                    },
                ),
            contentDescription = "",
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_padding)))
        Text(
            text = text,
            style = Font.light(12.sp),
            color = Color.text(isDark),
        )
    }
}

@Composable
fun ValidatorListFilterSortView(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    isActiveValidatorList: Boolean,
    sortOption: ValidatorSortOption,
    filterOptions: Set<ValidatorFilterOption>,
    onSelectSortOption: (ValidatorSortOption) -> Unit,
    onSelectFilterOption: (ValidatorFilterOption) -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.networkSelectionOverlayBg(isDark))
                .noRippleClickable { onClose() },
    ) {
        Spacer(
            modifier =
                Modifier
                    .padding(0.dp, dimensionResource(id = R.dimen.common_content_margin_top))
                    .statusBarsPadding(),
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.network_selector_button_height)))
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.common_padding), 0.dp),
        ) {
            Spacer(modifier = Modifier.weight(1.0f))
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .background(
                            color = Color.networkSelectorOpenBg(isDark),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .noRippleClickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.filter_icon),
                    contentDescription = "",
                )
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.common_padding)))
        Row(modifier = Modifier.padding(dimensionResource(id = R.dimen.common_padding), 0.dp)) {
            Spacer(modifier = Modifier.weight(1.0f))
            Column(
                modifier =
                    Modifier
                        .noRippleClickable { /* no-op */ }
                        .background(
                            color = Color.networkSelectorOpenBg(isDark),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(dimensionResource(id = R.dimen.common_padding))
                        .width(216.dp),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.common_padding)),
            ) {
                Text(
                    text = stringResource(id = R.string.validator_list_sort_by),
                    style = Font.light(12.sp),
                    color = Color.text(isDark),
                )
                // sort by identity
                ValidatorListFilterSortOptionView(
                    text = stringResource(id = R.string.validator_list_sort_by_id_address),
                    isChecked = sortOption == ValidatorSortOption.IDENTITY,
                    onClick = {
                        onSelectSortOption(ValidatorSortOption.IDENTITY)
                    },
                )
                // sort by active stake
                if (isActiveValidatorList) {
                    ValidatorListFilterSortOptionView(
                        text = stringResource(id = R.string.validator_list_sort_by_active_stake),
                        isChecked = sortOption == ValidatorSortOption.STAKE_DESCENDING,
                        onClick = {
                            onSelectSortOption(ValidatorSortOption.STAKE_DESCENDING)
                        },
                    )
                }
                // sort by nomination total
                ValidatorListFilterSortOptionView(
                    text = stringResource(id = R.string.validator_list_sort_by_nomination_total),
                    isChecked = sortOption == ValidatorSortOption.NOMINATION_DESCENDING,
                    onClick = {
                        onSelectSortOption(ValidatorSortOption.NOMINATION_DESCENDING)
                    },
                )
                // separator
                Box(
                    modifier =
                        Modifier.alpha(0.15f).height(1.dp)
                            .background(color = Color.text(isDark)),
                )
                Text(
                    text = stringResource(id = R.string.validator_list_filter),
                    style = Font.light(12.sp),
                    color = Color.text(isDark),
                )
                // filter with identity
                ValidatorListFilterSortOptionView(
                    text = stringResource(id = R.string.validator_list_filter_identity),
                    isChecked = filterOptions.contains(ValidatorFilterOption.HAS_IDENTITY),
                    onClick = {
                        onSelectFilterOption(ValidatorFilterOption.HAS_IDENTITY)
                    },
                )
                // filter 1KV
                ValidatorListFilterSortOptionView(
                    text = stringResource(id = R.string.validator_list_filter_onekv),
                    isChecked = filterOptions.contains(ValidatorFilterOption.IS_ONEKV),
                    onClick = {
                        onSelectFilterOption(ValidatorFilterOption.IS_ONEKV)
                    },
                )
                // para validator
                ValidatorListFilterSortOptionView(
                    text = stringResource(id = R.string.validator_list_filter_paravalidator),
                    isChecked = filterOptions.contains(ValidatorFilterOption.IS_PARA_VALIDATOR),
                    onClick = {
                        onSelectFilterOption(ValidatorFilterOption.IS_PARA_VALIDATOR)
                    },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun ValidatorListFilterSortViewPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        ValidatorListFilterSortView(
            modifier = Modifier,
            isDark = isDark,
            isActiveValidatorList = true,
            sortOption = ValidatorSortOption.NOMINATION_DESCENDING,
            filterOptions = setOf(ValidatorFilterOption.IS_ONEKV),
            onSelectSortOption = {},
            onSelectFilterOption = {},
            onClose = {},
        )
    }
}
