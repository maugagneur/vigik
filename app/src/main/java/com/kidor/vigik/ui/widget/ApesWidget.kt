@file:Suppress("MagicNumber")

package com.kidor.vigik.ui.widget

import android.content.Context
import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
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

/**
 * 4x1 consultative widget for APES (Sync Jeux).
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

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .appWidgetBackground()
                    .background(ColorProvider(WIDGET_BACKGROUND_COLOR))
                    .cornerRadius(MaterialTheme.dimensions.commonSpaceMedium)
                    .clickable(actionStartActivity(openAppIntent)),
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

                    Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceSmall))

                    // Middle: Evening availability summary
                    EveningSection(
                        summary = state.data.eveningSummary,
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceSmall))

                    // Right: Refresh button
                    RefreshButton(isLoading = state.isLoading)
                }
            }
        }
    }
}

@Composable
private fun VoiceSection(
    voiceCount: Int,
    voiceMembers: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(resId = R.drawable.ic_headset),
            contentDescription = "Salon vocal",
            modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
            colorFilter = ColorFilter.tint(
                ColorProvider(if (voiceCount > 0) ACCENT_GREEN_COLOR else TEXT_SECONDARY_COLOR)
            )
        )
        Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (voiceCount > 0) "$voiceCount en vocal" else "Vocal vide",
                style = TextStyle(
                    color = ColorProvider(if (voiceCount > 0) ACCENT_GREEN_COLOR else TEXT_PRIMARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeSmall,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            if (voiceMembers.isNotEmpty()) {
                Text(
                    text = voiceMembers,
                    style = TextStyle(
                        color = ColorProvider(TEXT_SECONDARY_COLOR),
                        fontSize = MaterialTheme.dimensions.textSizeXXSmall
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EveningSection(
    summary: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        modifier = modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = ImageProvider(resId = R.drawable.ic_calendar),
            contentDescription = "Planning ce soir",
            modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
            colorFilter = ColorFilter.tint(ColorProvider(TEXT_SECONDARY_COLOR))
        )
        Spacer(modifier = GlanceModifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ce soir",
                style = TextStyle(
                    color = ColorProvider(TEXT_PRIMARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeSmall,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Text(
                text = summary.ifEmpty { "Consulter" },
                style = TextStyle(
                    color = ColorProvider(TEXT_SECONDARY_COLOR),
                    fontSize = MaterialTheme.dimensions.textSizeXXSmall,
                    textAlign = TextAlign.Start
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RefreshButton(isLoading: Boolean) {
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
                contentDescription = "Actualiser",
                modifier = GlanceModifier.size(MaterialTheme.dimensions.commonSpaceLarge),
                colorFilter = ColorFilter.tint(ColorProvider(TEXT_PRIMARY_COLOR))
            )
        }
    }
}
