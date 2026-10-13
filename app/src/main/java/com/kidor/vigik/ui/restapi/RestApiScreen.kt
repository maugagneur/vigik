package com.kidor.vigik.ui.restapi

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.data.apes.APES_API_BASE_URL
import com.kidor.vigik.data.apes.ApesDateHelper
import com.kidor.vigik.data.apes.DispoPreset
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.MemberAvailability
import com.kidor.vigik.data.apes.model.SlotOut
import com.kidor.vigik.data.apes.model.VoiceMember
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions

internal const val VOICE_CARD_TEST_TAG = "voice_card"
internal const val VOICE_EMPTY_TEST_TAG = "voice_empty"
internal const val VOICE_MEMBERS_TEST_TAG = "voice_members"
internal const val EVENING_CARD_TEST_TAG = "evening_card"
internal const val EVENING_PEAK_TEST_TAG = "evening_peak"
internal const val EVENING_SLOTS_TEST_TAG = "evening_slots"
internal const val MEMBERS_AVAILABILITY_TEST_TAG = "members_availability"
internal const val TOKEN_CARD_TEST_TAG = "token_card"
internal const val TOKEN_INPUT_TEST_TAG = "token_input"
internal const val TOKEN_SAVE_BUTTON_TEST_TAG = "token_save_button"
internal const val TOKEN_LOGOUT_BUTTON_TEST_TAG = "token_logout_button"
internal const val PRESET_PRESENT_BUTTON_TEST_TAG = "preset_present_button"
internal const val PRESET_ABSENT_BUTTON_TEST_TAG = "preset_absent_button"
internal const val PRESET_CLEAR_BUTTON_TEST_TAG = "preset_clear_button"
internal const val SOURCE_LABEL_TEST_TAG = "source_label"

private val LiveGreenColor = Color(0xFF22C55E)
private val HighQuorumColor = Color(0xFF16A34A)
private val MediumQuorumColor = Color(0xFFEAB308)
private val LowQuorumColor = Color(0xFF94A3B8)

/**
 * View displaying the section dedicated to REST API (APES - Sync Jeux).
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RestApiScreen(
    viewModel: RestApiViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveViewState(viewModel) { state ->
        // Handle Snackbar messages
        LaunchedEffect(state.userMessage, state.errorMessage) {
            val message = state.userMessage ?: state.errorMessage
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                viewModel.handleAction(RestApiViewAction.DismissMessage)
            }
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.isRefreshing,
            onRefresh = { viewModel.handleAction(RestApiViewAction.RefreshData) }
        )

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pullRefresh(pullRefreshState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = MaterialTheme.dimensions.commonSpaceMedium)
                        .padding(vertical = MaterialTheme.dimensions.commonSpaceSmall),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceMedium)
                ) {
                    // Header Title
                    Text(
                        text = stringResource(id = R.string.rest_api_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // 1. Live Voice Section
                    LiveVoiceCard(voiceData = state.voiceData)

                    // 2. Evening Radar / Timeline Section
                    EveningTimelineCard(availabilityData = state.availabilityData)

                    // 3. User Quick Availability Declaration
                    UserAvailabilityCard(
                        userToken = state.userToken,
                        meData = state.meData,
                        isSubmitting = state.isSubmittingPreset,
                        onSaveToken = { token -> viewModel.handleAction(RestApiViewAction.SaveUserToken(token)) },
                        onClearToken = { viewModel.handleAction(RestApiViewAction.ClearUserToken) },
                        onApplyPreset = { preset -> viewModel.handleAction(RestApiViewAction.ApplyPreset(preset)) }
                    )

                    // Footer Source
                    Text(
                        text = stringResource(id = R.string.rest_api_source_label, APES_API_BASE_URL),
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = MaterialTheme.dimensions.textSizeSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.dimensions.commonSpaceMedium)
                            .testTag(SOURCE_LABEL_TEST_TAG)
                    )
                }

                PullRefreshIndicator(
                    refreshing = state.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

/**
 * Card displaying Discord live voice channel status.
 */
@Composable
@VisibleForTesting
internal fun LiveVoiceCard(voiceData: VoiceOut?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(VOICE_CARD_TEST_TAG),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimensions.commonSpaceMedium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Headset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
                    Text(
                        text = stringResource(id = R.string.apes_voice_section_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val hasMembers = !voiceData?.members.isNullOrEmpty()
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (hasMembers) LiveGreenColor else MaterialTheme.colorScheme.outline)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

            val members = voiceData?.members.orEmpty()
            if (members.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.apes_voice_empty_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.testTag(VOICE_EMPTY_TEST_TAG)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.apes_voice_active_label, members.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = LiveGreenColor,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

                Column(
                    modifier = Modifier.testTag(VOICE_MEMBERS_TEST_TAG),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall)
                ) {
                    members.forEach { member ->
                        VoiceMemberRow(member = member)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceMemberRow(member: VoiceMember) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberAvatar(name = member.displayName, avatarUrl = member.avatarUrl, size = 32.dp)

        Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceSmall))

        Text(
            text = member.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        val durationStr = ApesDateHelper.formatDurationSince(member.since)
        if (durationStr.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.apes_voice_since_label, durationStr),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * Card displaying evening timeline and group availability.
 */
@Composable
@VisibleForTesting
internal fun EveningTimelineCard(availabilityData: AvailabilityOut?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(EVENING_CARD_TEST_TAG),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimensions.commonSpaceMedium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
                Text(
                    text = stringResource(id = R.string.apes_evening_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

            val eveningDate = remember { ApesDateHelper.getCurrentEveningDate() }
            val slots = availabilityData?.slots.orEmpty()
            val members = availabilityData?.members.orEmpty()

            val eveningSlots = remember(slots, eveningDate) {
                slots.mapIndexed { index, slot -> index to slot }
                    .filter { (_, slot) -> ApesDateHelper.isSlotInEvening(slot.local, eveningDate) }
            }

            // Calculate peak
            var peakCount = 0
            var peakSlotHour = ""
            eveningSlots.forEach { (index, slot) ->
                val count = members.count { member ->
                    val status = member.statuses.getOrNull(index)
                    status != null && status >= 66
                }
                if (count > peakCount) {
                    peakCount = count
                    peakSlotHour = ApesDateHelper.formatSlotHour(slot.local)
                }
            }

            if (peakCount > 0) {
                Text(
                    text = stringResource(id = R.string.apes_evening_peak_label, peakCount, peakSlotHour),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HighQuorumColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(EVENING_PEAK_TEST_TAG)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.apes_evening_no_slots),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.testTag(EVENING_PEAK_TEST_TAG)
                )
            }

            // Horizontal Slots Bar
            if (eveningSlots.isNotEmpty()) {
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .testTag(EVENING_SLOTS_TEST_TAG),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXXSmall)
                ) {
                    eveningSlots.forEach { (index, slot) ->
                        val count = members.count { member ->
                            val status = member.statuses.getOrNull(index)
                            status != null && status >= 66
                        }
                        EveningSlotChip(
                            hourLabel = ApesDateHelper.formatSlotHour(slot.local),
                            playerCount = count
                        )
                    }
                }
            }

            // Group Members List
            if (members.isNotEmpty()) {
                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceMedium))
                Text(
                    text = stringResource(id = R.string.apes_members_section_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceXSmall))

                Column(
                    modifier = Modifier.testTag(MEMBERS_AVAILABILITY_TEST_TAG),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall)
                ) {
                    members.forEach { member ->
                        MemberAvailabilityRow(
                            member = member,
                            eveningSlots = eveningSlots
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EveningSlotChip(hourLabel: String, playerCount: Int) {
    val bgColor = when {
        playerCount >= 3 -> HighQuorumColor.copy(alpha = 0.2f)
        playerCount > 0 -> MediumQuorumColor.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        playerCount >= 3 -> HighQuorumColor
        playerCount > 0 -> MediumQuorumColor
        else -> LowQuorumColor
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hourLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$playerCount",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun MemberAvailabilityRow(
    member: MemberAvailability,
    eveningSlots: List<Pair<Int, SlotOut>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberAvatar(name = member.displayName, avatarUrl = member.avatarUrl, size = 28.dp)

        Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceSmall))

        Text(
            text = member.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Availability indicator for this evening
        val maxStatusInEvening = eveningSlots.maxOfOrNull { (index, _) ->
            member.statuses.getOrNull(index) ?: -1
        } ?: -1

        val (statusTextRes, statusColor) = when {
            maxStatusInEvening == 100 -> R.string.apes_status_available to HighQuorumColor
            maxStatusInEvening >= 66 -> R.string.apes_status_probable to MediumQuorumColor
            maxStatusInEvening >= 33 -> R.string.apes_status_possible to MediumQuorumColor
            maxStatusInEvening == 0 -> R.string.apes_status_unavailable to MaterialTheme.colorScheme.error
            else -> R.string.apes_status_unknown to LowQuorumColor
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = stringResource(id = statusTextRes),
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Card for personal API token input and quick presets (2.C).
 */
@Composable
@VisibleForTesting
internal fun UserAvailabilityCard(
    userToken: String?,
    meData: MeOut?,
    isSubmitting: Boolean,
    onSaveToken: (String) -> Unit,
    onClearToken: () -> Unit,
    onApplyPreset: (DispoPreset) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TOKEN_CARD_TEST_TAG),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimensions.commonSpaceMedium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceXSmall))
                Text(
                    text = stringResource(id = R.string.apes_dispo_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

            val isConnected = !userToken.isNullOrBlank() && meData != null

            if (!isConnected) {
                // Token entry section
                var tokenInput by remember { mutableStateOf("") }

                Text(
                    text = stringResource(id = R.string.apes_token_help_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text(stringResource(id = R.string.apes_token_label)) },
                    placeholder = { Text(stringResource(id = R.string.apes_token_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TOKEN_INPUT_TEST_TAG),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

                Button(
                    onClick = { onSaveToken(tokenInput.trim()) },
                    enabled = tokenInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TOKEN_SAVE_BUTTON_TEST_TAG)
                ) {
                    Text(text = stringResource(id = R.string.apes_token_save_button))
                }
            } else {
                // Authenticated state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.apes_token_connected_as, meData.displayName),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HighQuorumColor,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onClearToken,
                        modifier = Modifier.testTag(TOKEN_LOGOUT_BUTTON_TEST_TAG)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

                // The 3 user presets
                if (isSubmitting) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.dimensions.commonSpaceMedium),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceSmall)
                    ) {
                        // Preset 1: Présent ce soir
                        Button(
                            onClick = { onApplyPreset(DispoPreset.PRESENT_TONIGHT) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(PRESET_PRESENT_BUTTON_TEST_TAG),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = stringResource(id = R.string.apes_preset_present_button))
                        }

                        // Preset 2: Absent ce soir
                        OutlinedButton(
                            onClick = { onApplyPreset(DispoPreset.ABSENT_TONIGHT) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(PRESET_ABSENT_BUTTON_TEST_TAG)
                        ) {
                            Text(text = stringResource(id = R.string.apes_preset_absent_button))
                        }

                        // Preset 3: Effacer
                        OutlinedButton(
                            onClick = { onApplyPreset(DispoPreset.CLEAR) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(PRESET_CLEAR_BUTTON_TEST_TAG),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(text = stringResource(id = R.string.apes_preset_clear_button))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Avatar with image loading and fallback to initials.
 */
@Composable
private fun MemberAvatar(
    name: String,
    avatarUrl: String?,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    val fullUrl = remember(avatarUrl) {
        if (avatarUrl.isNullOrBlank()) {
            null
        } else if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) {
            avatarUrl
        } else {
            val normalized = if (avatarUrl.startsWith("/")) avatarUrl else "/$avatarUrl"
            "${APES_API_BASE_URL.removeSuffix("/")}$normalized"
        }
    }
    val initials = remember(name) { name.trim().take(2).uppercase() }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!fullUrl.isNullOrBlank()) {
            AsyncImage(
                model = fullUrl,
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
private fun RestApiScreenPreview() {
    RestApiScreen()
}
