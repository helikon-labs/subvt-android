package io.helikon.subvt.ui.screen.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.helikon.subvt.R
import io.helikon.subvt.ui.modifier.NoRippleInteractionSource
import io.helikon.subvt.ui.style.Font
import io.helikon.subvt.ui.theme.Color
import io.helikon.subvt.ui.util.ThemePreviews
import kotlinx.coroutines.launch

private const val PAGE_COUNT = 4

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit,
) {
    OnboardingScreenContent(
        modifier = modifier,
        onComplete = {
            viewModel.completeOnboarding()
            onComplete()
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreenContent(
    modifier: Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    onComplete: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()

    BackHandler {
        if (pagerState.currentPage > 0) {
            scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }
    }

    Box(
        modifier
            .navigationBarsPadding()
            .statusBarsPadding(),
    ) {
        HorizontalPager(
            state = pagerState,
        ) { pageIndex ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(0.dp),
            ) {
                Spacer(
                    modifier =
                        Modifier
                            .height(dimensionResource(id = R.dimen.onboarding_content_margin_top)),
                )
                Row(
                    Modifier.padding(
                        dimensionResource(id = R.dimen.onboarding_content_padding_horizontal),
                        0.dp,
                    ),
                ) {
                    Text(
                        text = (pageIndex + 1).toString(),
                        style = Font.semiBold(14.sp),
                        color = Color.text(isDark),
                        textAlign = TextAlign.Left,
                    )
                    Text(
                        text = "/".plus(PAGE_COUNT),
                        style = Font.light(14.sp, 20.sp),
                        color = Color.text(isDark),
                        textAlign = TextAlign.Left,
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.onboarding_title_margin_top)))
                Text(
                    text =
                        stringResource(
                            when (pageIndex) {
                                0 -> R.string.onboarding_1_title
                                1 -> R.string.onboarding_2_title
                                2 -> R.string.onboarding_3_title
                                else -> R.string.onboarding_4_title
                            },
                        ),
                    style = Font.semiBold(22.sp),
                    color = Color.text(isDark),
                    modifier =
                        Modifier.padding(
                            dimensionResource(id = R.dimen.onboarding_content_padding_horizontal),
                            0.dp,
                        ),
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.onboarding_description_margin_top)))
                Text(
                    text =
                        stringResource(
                            when (pageIndex) {
                                0 -> R.string.onboarding_1_description
                                1 -> R.string.onboarding_2_description
                                2 -> R.string.onboarding_3_description
                                else -> R.string.onboarding_4_description
                            },
                        ),
                    style = Font.light(14.sp, 20.sp),
                    color = Color.text(isDark),
                    modifier =
                        Modifier.padding(
                            dimensionResource(id = R.dimen.onboarding_content_padding_horizontal),
                            0.dp,
                        ),
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.onboarding_image_margin_top)))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        contentScale =
                            when (pageIndex) {
                                2 -> ContentScale.Crop
                                else -> ContentScale.Fit
                            },
                        painter =
                            painterResource(
                                id =
                                    when (pageIndex) {
                                        0 -> R.drawable.onboarding_step_1
                                        1 -> R.drawable.onboarding_step_2
                                        2 -> R.drawable.onboarding_step_3
                                        else -> R.drawable.onboarding_step_4
                                    },
                            ),
                        contentDescription = stringResource(id = R.string.onboarding_1_title),
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimensionResource(id = R.dimen.onboarding_content_margin_bottom)),
        ) {
            TextButton(
                colors =
                    ButtonDefaults.buttonColors(
                        Color.transparent(),
                    ),
                modifier = Modifier.background(Color.transparent()),
                onClick = onComplete,
                interactionSource = NoRippleInteractionSource(),
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_skip),
                    style = Font.semiBold(16.sp),
                    color = Color.text(isDark),
                )
            }
            Row {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) {
                            Color.actionButtonBg()
                        } else {
                            Color.onboardingPageIndicatorBg(
                                isDark,
                            )
                        }
                    Box(
                        modifier =
                            Modifier
                                .padding(
                                    dimensionResource(id = R.dimen.onboarding_page_indicator_padding),
                                    0.dp,
                                )
                                .clip(CircleShape)
                                .background(color)
                                .size(dimensionResource(id = R.dimen.onboarding_page_indicator_size)),
                    )
                }
            }
            TextButton(
                colors =
                    ButtonDefaults.buttonColors(
                        Color.transparent(),
                    ),
                onClick = {
                    if (pagerState.currentPage < (PAGE_COUNT - 1)) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onComplete()
                    }
                },
                interactionSource = NoRippleInteractionSource(),
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_next),
                    style = Font.light(16.sp),
                    color = Color.text(isDark),
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun OnboardingScreenContentPreview(isDark: Boolean = isSystemInDarkTheme()) {
    Surface(
        color = Color.bg(isDark),
    ) {
        OnboardingScreenContent(Modifier, onComplete = {})
    }
}
