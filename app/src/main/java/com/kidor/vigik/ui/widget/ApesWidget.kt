package com.kidor.vigik.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kidor.vigik.R
import com.kidor.vigik.navigation.AppScreen
import com.kidor.vigik.ui.MainActivity
import com.kidor.vigik.ui.theme.dimensions

private val WIDGET_BACKGROUND_COLOR = Color(0xFF1E2124)
private val TEXT_PRIMARY_COLOR = Color(0xFFFFFFFF)
private val TEXT_SECONDARY_COLOR = Color(0xFFB9BBBE)
private val ACCENT_GREEN_COLOR = Color(0xFF57F287)
private val APE_AVATAR_SIZE = 64.dp

/**
 * 4x1 consultative widget for APES.
 */
class ApesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = ApesWidgetStateHelper.getState(currentState())

            val openAppIntent = Intent(
                Intent.ACTION_VIEW,
                AppScreen.RestApiScreen.deeplinkPath.toUri(),
                context,
                MainActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            ApesWidgetContent(
                state = state,
                onWidgetClick = actionStartActivity(openAppIntent)
            )
        }
    }
}

/**
 * Composable displaying the content of [ApesWidget].
 *
 * @param state State data of the widget.
 * @param modifier Glance modifier to apply to the root container.
 * @param onWidgetClick Optional click action on the widget root.
 */
@Composable
internal fun ApesWidgetContent(
    state: ApesWidgetState,
    modifier: GlanceModifier = GlanceModifier,
    onWidgetClick: Action? = null
) {
    val contentModifier = GlanceModifier
        .fillMaxSize()
        .appWidgetBackground()
        .background(ColorProvider(WIDGET_BACKGROUND_COLOR))
        .cornerRadius(MaterialTheme.dimensions.commonSpaceMedium)
        .let { baseModifier ->
            if (onWidgetClick != null) {
                baseModifier.clickable(onWidgetClick)
            } else {
                baseModifier
            }
        }

    Box(
        modifier = modifier.then(contentModifier),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.dimensions.commonSpaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Voice section
            VoiceSection(
                voiceCount = state.data.voiceCount,
                voiceMembers = state.data.voiceMembers,
                modifier = GlanceModifier.defaultWeight()
            )

            Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceXSmall))

            // Center: Ape avatar
            ApeAvatar(voiceCount = state.data.voiceCount)

            Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceXSmall))

            // Middle: Evening availability summary
            EveningSection(
                summary = state.data.eveningSummary,
                modifier = GlanceModifier.defaultWeight()
            )

            // Right: Refresh button
            RefreshButton(isLoading = state.isLoading)
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 380, heightDp = 90)
@Composable
private fun ApesWidgetEmptyPreview() {
    ApesWidgetContent(
        state = ApesWidgetState(
            isLoading = true,
            data = ApesWidgetData(
                voiceCount = 0,
                voiceMembers = "",
                eveningSummary = ""
            )
        )
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 380, heightDp = 90)
@Composable
private fun ApesWidgetActivePreview() {
    ApesWidgetContent(
        state = ApesWidgetState(
            isLoading = false,
            data = ApesWidgetData(
                voiceCount = 3,
                voiceMembers = "Kidor, Player 2, xXx-DarkSasuke-xXx, Legolas38",
                eveningSummary = "3 dispos"
            )
        )
    )
}

@Composable
private fun VoiceSection(
    voiceCount: Int,
    voiceMembers: String,
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(resId = R.drawable.ic_headset),
            contentDescription = context.getString(R.string.widget_apes_voice_channel_content_description),
            modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
            colorFilter = ColorFilter.tint(
                ColorProvider(if (voiceCount > 0) ACCENT_GREEN_COLOR else TEXT_SECONDARY_COLOR)
            )
        )
        Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(R.string.widget_apes_voice_title),
                style = TextStyle(
                    color = ColorProvider(if (voiceCount > 0) ACCENT_GREEN_COLOR else TEXT_PRIMARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeSmall,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 3
            )
            Text(
                text = if (voiceCount > 0) {
                    voiceMembers
                } else {
                    context.getString(R.string.widget_apes_voice_empty)
                },
                style = TextStyle(
                    color = ColorProvider(TEXT_SECONDARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeXXSmall
                ),
                maxLines = 3
            )
        }
    }
}

@Composable
private fun ApeAvatar(
    voiceCount: Int,
    modifier: GlanceModifier = GlanceModifier
) {
    val avatarRes = if (voiceCount > 0) {
        R.drawable.widget_apes_avatar_active
    } else {
        R.drawable.widget_apes_avatar_empty
    }
    Image(
        provider = ImageProvider(resId = avatarRes),
        contentDescription = null,
        modifier = modifier.size(APE_AVATAR_SIZE)
    )
}

@Composable
private fun EveningSection(
    summary: String,
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(resId = R.drawable.ic_calendar),
            contentDescription = context.getString(R.string.widget_apes_evening_schedule_content_description),
            modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
            colorFilter = ColorFilter.tint(ColorProvider(TEXT_SECONDARY_COLOR))
        )
        Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
        Column(
            modifier = GlanceModifier.defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(R.string.widget_apes_evening_title),
                style = TextStyle(
                    color = ColorProvider(TEXT_PRIMARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeSmall,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 3
            )
            Text(
                text = summary.ifEmpty { context.getString(R.string.widget_apes_evening_action_check) },
                style = TextStyle(
                    color = ColorProvider(TEXT_SECONDARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeXXSmall,
                    textAlign = TextAlign.Start
                ),
                maxLines = 3
            )
        }
    }
}

@Composable
private fun RefreshButton(isLoading: Boolean) {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier
            .size(MaterialTheme.dimensions.commonSpaceXXLarge)
            .clickable(onClick = actionRunCallback<ApesWidgetRefreshAction>()),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
                color = ColorProvider(TEXT_PRIMARY_COLOR)
            )
        } else {
            Image(
                provider = ImageProvider(resId = R.drawable.ic_refresh),
                contentDescription = context.getString(R.string.widget_apes_refresh_content_description),
                modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
                colorFilter = ColorFilter.tint(ColorProvider(TEXT_PRIMARY_COLOR))
            )
        }
    }
}
