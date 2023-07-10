package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
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
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kidor.vigik.R
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.ui.compose.AppTheme
import timber.log.Timber

/**
 * Diablo IV event tracker widget.
 */
class DiabloWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Provide composable when data is ready
        provideContent {
            val state = DiabloWidgetStateHelper.getState(currentState())

            Timber.d("current state = $state")

            val backgroundDrawable = when (state.data.worldBoss) {
                Diablo4WorldBoss.ASHAVA -> R.drawable.widget_ashava_rounded
                Diablo4WorldBoss.AVARICE -> R.drawable.widget_avarice_rounded
                Diablo4WorldBoss.WANDERING_DEATH -> R.drawable.widget_wandering_death_rounded
                Diablo4WorldBoss.UNKNOWN -> R.drawable.shape_rectangle_round_white
            }

            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .background(ImageProvider(resId = backgroundDrawable))
                    .appWidgetBackground()
                    .clickable(onClick = actionRunCallback<DiabloWidgetRefreshAction>()),
                contentAlignment = Alignment.Center
            ) {
                if (state.data.worldBoss == Diablo4WorldBoss.UNKNOWN) {
                    NoData(context = context, isLoading = state.isLoading)
                } else {
                    WidgetBody(context = context, state = state)
                }
            }
        }
    }
}

@Composable
private fun NoData(context: Context, isLoading: Boolean) {
    Column(
        modifier = GlanceModifier.fillMaxSize().padding(AppTheme.dimensions.commonSpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            provider = ImageProvider(resId = R.drawable.d4_monster),
            contentDescription = "Unknown world boss",
            modifier = GlanceModifier.fillMaxHeight().defaultWeight()
        )
        Text(
            text = context.getString(Diablo4WorldBoss.UNKNOWN.resId),
            style = TextStyle(
                color = ColorProvider(color = Color.Black),
                fontSize = 14.sp
            ),
            maxLines = 1
        )
    }
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(AppTheme.dimensions.commonSpaceSmall),
        horizontalAlignment = Alignment.End
    ) {
        LoadingImage(isLoading = isLoading, color = Color.Black)
    }
}

@Composable
private fun WidgetBody(context: Context, state: DiabloWidgetState) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(AppTheme.dimensions.commonSpaceSmall)
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = context.getString(state.data.worldBoss.resId),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .padding(end = AppTheme.dimensions.commonSpaceSmall),
                style = TextStyle(
                    color = ColorProvider(color = Color.White),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start
                ),
                maxLines = 2
            )
            LoadingImage(isLoading = state.isLoading, color = Color.White)
        }
        Spacer(modifier = GlanceModifier.fillMaxHeight().defaultWeight())
        Text(
            text = context.getString(R.string.widget_diablo_boos_spawn_label),
            style = TextStyle(
                color = ColorProvider(color = Color.White),
                fontSize = 10.sp,
                textAlign = TextAlign.Start
            ),
            maxLines = 1
        )
        Text(
            text = state.data.spawnDate,
            style = TextStyle(
                color = ColorProvider(color = Color.White),
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun LoadingImage(isLoading: Boolean, color: Color) {
    if (isLoading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = GlanceModifier.size(24.dp),
                color = ColorProvider(color = color)
            )
        }
    } else {
        Image(
            provider = ImageProvider(resId = R.drawable.ic_refresh),
            contentDescription = "Refresh",
            modifier = GlanceModifier.size(24.dp),
            colorFilter = ColorFilter.tint(colorProvider = ColorProvider(color = color))
        )
    }
}
