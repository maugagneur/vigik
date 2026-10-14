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
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.data.apes.APES_API_BASE_URL
import com.kidor.vigik.data.apes.ApesDateHelper
import com.kidor.vigik.data.apes.AvailabilityPreset
import com.kidor.vigik.data.apes.DEFAULT_PRESET_END_TIME
import com.kidor.vigik.data.apes.DEFAULT_PRESET_START_TIME
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.MemberAvailability
import com.kidor.vigik.data.apes.model.SlotOut
import com.kidor.vigik.data.apes.model.VoiceMember
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions
import java.time.Instant
import java.util.Locale

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
internal const val PRESET_SETTINGS_BUTTON_TEST_TAG = "preset_settings_button"
internal const val SOURCE_LABEL_TEST_TAG = "source_label"

private const val STATUS_CONFIRMED = 100
private const val STATUS_PROBABLE_THRESHOLD = 66
private const val STATUS_POSSIBLE_THRESHOLD = 33
private const val STATUS_UNAVAILABLE = 0
private const val HIGH_QUORUM_THRESHOLD = 2

private val LiveGreenColor = Color(0xFF22C55E)

// Quorum colors (for evening slot chips)
private val HighQuorumColor = Color(0xFF16A34A)
private val MediumQuorumColor = Color(0xFFEAB308)
private val ZeroQuorumColor = Color(0xFF94A3B8)

// Member availability status colors
private val StatusAvailableColor = Color(0xFF16A34A)
private val StatusProbableColor = Color(0xFFEAB308)
private val StatusPossibleColor = Color(0xFFF97316)
private val StatusUnknownColor = Color(0xFF94A3B8)

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
            contentWindowInsets = WindowInsets(0.dp),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pullRefresh(pullRefreshState)
            ) {
                ApesContent(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(all = MaterialTheme.dimensions.commonSpaceSmall),
                    onAction = viewModel::handleAction
                )

                PullRefreshIndicator(
                    refreshing = state.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun ApesContent(
    state: RestApiViewState,
    modifier: Modifier = Modifier,
    onAction: (RestApiViewAction) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceMedium)
    ) {
        // Live Voice Section
        LiveVoiceCard(voiceData = state.voiceData)

        // Evening Radar / Timeline Section
        EveningTimelineCard(availabilityData = state.availabilityData)

        // User Quick Availability Declaration
        UserAvailabilityCard(
            userToken = state.userToken,
            meData = state.meData,
            isSubmitting = state.isSubmittingPreset,
            presetStartTime = state.presetStartTime,
            presetEndTime = state.presetEndTime,
            onSaveToken = { token -> onAction(RestApiViewAction.SaveUserToken(token)) },
            onClearToken = { onAction(RestApiViewAction.ClearUserToken) },
            onApplyPreset = { preset -> onAction(RestApiViewAction.ApplyPreset(preset)) },
            onSavePresetTimes = { startTime, endTime ->
                onAction(RestApiViewAction.SavePresetTimes(startTime, endTime))
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer Source
        Text(
            text = stringResource(id = R.string.rest_api_source_label, APES_API_BASE_URL),
            color = MaterialTheme.colorScheme.outline,
            fontSize = MaterialTheme.dimensions.textSizeSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SOURCE_LABEL_TEST_TAG)
        )
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
                .padding(MaterialTheme.dimensions.commonSpaceMedium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceSmall)
        ) {
            val members = voiceData?.members.orEmpty()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Headset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.apes_voice_section_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                val hasMembers = members.isNotEmpty()
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (hasMembers) LiveGreenColor else MaterialTheme.colorScheme.outline)
                )
            }

            if (members.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.apes_voice_empty_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.testTag(VOICE_EMPTY_TEST_TAG)
                )
            } else {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.apes_voice_active_label,
                        count = members.size,
                        members.size
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = LiveGreenColor,
                    fontWeight = FontWeight.SemiBold
                )

                val referenceInstant = remember(voiceData) {
                    ApesDateHelper.parseInstant(voiceData?.now) ?: Instant.now()
                }

                Column(
                    modifier = Modifier.testTag(VOICE_MEMBERS_TEST_TAG),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall)
                ) {
                    members.forEach { member ->
                        key(member.id ?: member.displayName) {
                            VoiceMemberRow(member = member, referenceInstant = referenceInstant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceMemberRow(member: VoiceMember, referenceInstant: Instant) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberAvatar(
            name = member.displayName,
            avatarUrl = member.avatarUrl
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceSmall))

        Text(
            text = member.displayName.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        val durationStr = ApesDateHelper.formatDurationSince(member.since, referenceInstant)
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
                .padding(MaterialTheme.dimensions.commonSpaceMedium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceSmall)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.apes_evening_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val eveningDate = remember { ApesDateHelper.getCurrentEveningDate() }
            val slots = availabilityData?.slots.orEmpty()
            val members = availabilityData?.members.orEmpty()

            val eveningSlots = remember(slots, eveningDate) {
                slots.mapIndexed { index, slot -> index to slot }
                    .filter { (_, slot) -> ApesDateHelper.isSlotInEvening(slot.local, eveningDate) }
            }

            val (peakCount, peakSlotHour) = remember(eveningSlots, members) {
                findPeakEveningSlot(eveningSlots, members)
            }

            EveningPeakSection(peakCount = peakCount, peakSlotHour = peakSlotHour)

            if (eveningSlots.isNotEmpty()) {
                EveningSlotsBar(eveningSlots = eveningSlots, members = members)
            }

            if (members.isNotEmpty()) {
                MembersAvailabilitySection(members = members, eveningSlots = eveningSlots)
            }
        }
    }
}

@Composable
private fun EveningPeakSection(peakCount: Int, peakSlotHour: String) {
    if (peakCount > 0) {
        Text(
            text = pluralStringResource(
                id = R.plurals.apes_evening_peak_label,
                count = peakCount,
                peakCount,
                peakSlotHour
            ),
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
}

@Composable
private fun EveningSlotsBar(
    eveningSlots: List<Pair<Int, SlotOut>>,
    members: List<MemberAvailability>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .testTag(EVENING_SLOTS_TEST_TAG),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXXSmall)
    ) {
        eveningSlots.forEach { (index, slot) ->
            EveningSlotChip(
                hourLabel = ApesDateHelper.formatSlotHour(slot.local),
                playerCount = countAvailableMembers(members, index)
            )
        }
    }
}

@Composable
private fun MembersAvailabilitySection(
    members: List<MemberAvailability>,
    eveningSlots: List<Pair<Int, SlotOut>>
) {
    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceXXSmall))
    Text(
        text = stringResource(id = R.string.apes_members_section_title),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
    )

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

/**
 * Finds the peak evening slot with the highest number of available members.
 *
 * @param eveningSlots The list of evening slot indexes and their metadata.
 * @param members      The list of members and their availability statuses.
 * @return A pair containing the maximum count of available members and the formatted hour of the peak slot.
 */
private fun findPeakEveningSlot(
    eveningSlots: List<Pair<Int, SlotOut>>,
    members: List<MemberAvailability>
): Pair<Int, String> {
    var peakCount = 0
    var peakSlotHour = ""
    eveningSlots.forEach { (index, slot) ->
        val count = countAvailableMembers(members, index)
        if (count > peakCount) {
            peakCount = count
            peakSlotHour = ApesDateHelper.formatSlotHour(slot.local)
        }
    }
    return peakCount to peakSlotHour
}

private fun countAvailableMembers(members: List<MemberAvailability>, slotIndex: Int): Int {
    return members.count { member ->
        val status = member.statuses?.getOrNull(slotIndex)
        status != null && status >= STATUS_PROBABLE_THRESHOLD
    }
}

@Composable
private fun EveningSlotChip(hourLabel: String, playerCount: Int) {
    val bgColor = when {
        playerCount >= HIGH_QUORUM_THRESHOLD -> HighQuorumColor.copy(alpha = 0.2f)
        playerCount > 0 -> MediumQuorumColor.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        playerCount >= HIGH_QUORUM_THRESHOLD -> HighQuorumColor
        playerCount > 0 -> MediumQuorumColor
        else -> ZeroQuorumColor
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
        MemberAvatar(
            name = member.displayName,
            avatarUrl = member.avatarUrl
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceSmall))

        Text(
            text = member.displayName.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Availability indicator for this evening
        val maxStatusInEvening = eveningSlots.maxOfOrNull { (index, _) ->
            member.statuses?.getOrNull(index) ?: -1
        } ?: -1

        val (statusTextRes, statusColor) = when {
            maxStatusInEvening == STATUS_CONFIRMED -> R.string.apes_status_available to StatusAvailableColor
            maxStatusInEvening >= STATUS_PROBABLE_THRESHOLD -> R.string.apes_status_probable to StatusProbableColor
            maxStatusInEvening >= STATUS_POSSIBLE_THRESHOLD -> R.string.apes_status_possible to StatusPossibleColor
            maxStatusInEvening == STATUS_UNAVAILABLE -> R.string.apes_status_unavailable to MaterialTheme.colorScheme.error
            else -> R.string.apes_status_unknown to StatusUnknownColor
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
 * Card for personal API token input and quick presets.
 */
@Composable
@VisibleForTesting
internal fun UserAvailabilityCard(
    userToken: String?,
    meData: MeOut?,
    isSubmitting: Boolean,
    presetStartTime: String = DEFAULT_PRESET_START_TIME,
    presetEndTime: String = DEFAULT_PRESET_END_TIME,
    onSaveToken: (String) -> Unit,
    onClearToken: () -> Unit,
    onApplyPreset: (AvailabilityPreset) -> Unit,
    onSavePresetTimes: (String, String) -> Unit = { _, _ -> }
) {
    var showTimeRangeDialog by remember { mutableStateOf(false) }

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
                .padding(MaterialTheme.dimensions.commonSpaceMedium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceSmall)
        ) {
            val isConnected = !userToken.isNullOrBlank() && meData != null

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.apes_dispo_section_title),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isConnected) {
                    IconButton(
                        onClick = { showTimeRangeDialog = true },
                        modifier = Modifier.testTag(PRESET_SETTINGS_BUTTON_TEST_TAG)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.apes_preset_settings_button_description),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            if (isConnected) {
                // Authenticated state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.apes_token_connected_as, meData.displayName.orEmpty()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusAvailableColor,
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
                        // Preset 1: Available tonight
                        PresetButton(
                            text = stringResource(
                                id = R.string.apes_preset_present_button,
                                presetStartTime,
                                presetEndTime
                            ),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            testTag = PRESET_PRESENT_BUTTON_TEST_TAG,
                            onClick = { onApplyPreset(AvailabilityPreset.PRESENT_TONIGHT) }
                        )

                        // Preset 2: Not available tonight
                        PresetButton(
                            text = stringResource(id = R.string.apes_preset_absent_button),
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            testTag = PRESET_ABSENT_BUTTON_TEST_TAG,
                            onClick = { onApplyPreset(AvailabilityPreset.ABSENT_TONIGHT) }
                        )

                        // Preset 3: Remove availability
                        PresetButton(
                            text = stringResource(id = R.string.apes_preset_clear_button),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            testTag = PRESET_CLEAR_BUTTON_TEST_TAG,
                            onClick = { onApplyPreset(AvailabilityPreset.CLEAR) }
                        )
                    }
                }
            } else {
                // Token entry section
                var tokenInput by remember { mutableStateOf("") }

                Text(
                    text = stringResource(id = R.string.apes_token_help_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

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

                Button(
                    onClick = { onSaveToken(tokenInput.trim()) },
                    enabled = tokenInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TOKEN_SAVE_BUTTON_TEST_TAG)
                ) {
                    Text(text = stringResource(id = R.string.apes_token_save_button))
                }
            }
        }
    }

    if (showTimeRangeDialog) {
        TimeRangePickerDialog(
            initialStartTime = presetStartTime,
            initialEndTime = presetEndTime,
            onDismiss = { showTimeRangeDialog = false },
            onConfirm = { startTime, endTime ->
                showTimeRangeDialog = false
                onSavePresetTimes(startTime, endTime)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRangePickerDialog(
    initialStartTime: String,
    initialEndTime: String,
    onDismiss: () -> Unit,
    onConfirm: (startTime: String, endTime: String) -> Unit
) {
    val (startHour, startMinute) = remember(initialStartTime) {
        parseHourMinute(initialStartTime)
    }
    val (endHour, endMinute) = remember(initialEndTime) {
        parseHourMinute(initialEndTime)
    }

    val startPickerState = rememberTimePickerState(
        initialHour = startHour,
        initialMinute = startMinute,
        is24Hour = true
    )
    val endPickerState = rememberTimePickerState(
        initialHour = endHour,
        initialMinute = endMinute,
        is24Hour = true
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.apes_preset_settings_dialog_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceMedium)
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.apes_preset_settings_start_tab,
                                startPickerState.formattedTime
                            )
                        )
                    }
                    SegmentedButton(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.apes_preset_settings_end_tab,
                                endPickerState.formattedTime
                            )
                        )
                    }
                }

                if (selectedTabIndex == 0) {
                    TimePicker(state = startPickerState)
                } else {
                    TimePicker(state = endPickerState)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(startPickerState.formattedTime, endPickerState.formattedTime)
                }
            ) {
                Text(text = stringResource(id = R.string.apes_preset_settings_save).uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.apes_preset_settings_cancel).uppercase())
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private val TimePickerState.formattedTime: String
    get() = String.format(Locale.ROOT, "%02d:%02d", hour, minute)

private fun parseHourMinute(time: String): Pair<Int, Int> =
    time.substringBefore(':').toInt() to time.substringAfter(':').toInt()

@Composable
private fun PresetButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text = text)
    }
}

/**
 * Avatar with image loading and fallback to initials.
 */
@Composable
private fun MemberAvatar(
    name: String?,
    avatarUrl: String?,
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
    val initials = remember(name) { name?.trim()?.take(2)?.uppercase().orEmpty() }

    Box(
        modifier = modifier
            .size(32.dp)
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

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun ApesContentWithoutTokenPreview() {
    ApesContent(
        state = RestApiViewState()
    )
}

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun ApesContentWithTokenPreview() {
    ApesContent(
        state = RestApiViewState(
            userToken = "dummy_token",
            meData = MeOut(
                id = "user_1",
                displayName = "Player One"
            )
        )
    )
}

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun ApesContentWithLiveVoiceAndEveningPreview() {
    val eveningDate = ApesDateHelper.getCurrentEveningDate()
    val eveningDateStr = ApesDateHelper.formatDate(eveningDate)
    val slots = listOf(
        SlotOut(local = "${eveningDateStr}T20:30"),
        SlotOut(local = "${eveningDateStr}T21:00"),
        SlotOut(local = "${eveningDateStr}T21:30"),
        SlotOut(local = "${eveningDateStr}T22:00")
    )
    val members = listOf(
        MemberAvailability(
            id = "1",
            displayName = "Alice",
            statuses = listOf(STATUS_CONFIRMED, STATUS_CONFIRMED, STATUS_CONFIRMED, STATUS_PROBABLE_THRESHOLD)
        ),
        MemberAvailability(
            id = "2",
            displayName = "Bob",
            statuses = listOf(
                STATUS_PROBABLE_THRESHOLD,
                STATUS_PROBABLE_THRESHOLD,
                STATUS_POSSIBLE_THRESHOLD,
                STATUS_UNAVAILABLE
            )
        ),
        MemberAvailability(
            id = "3",
            displayName = "Charlie",
            statuses = listOf(STATUS_POSSIBLE_THRESHOLD, STATUS_POSSIBLE_THRESHOLD, STATUS_UNAVAILABLE, STATUS_UNAVAILABLE)
        )
    )
    val voiceMembers = listOf(
        VoiceMember(
            id = "1",
            displayName = "Alice",
            since = Instant.now().minusSeconds(2700).toString()
        ),
        VoiceMember(
            id = "2",
            displayName = "Bob",
            since = Instant.now().minusSeconds(900).toString()
        )
    )

    ApesContent(
        state = RestApiViewState(
            voiceData = VoiceOut(members = voiceMembers),
            availabilityData = AvailabilityOut(slots = slots, members = members),
            userToken = "dummy_token",
            meData = MeOut(
                id = "1",
                displayName = "Alice"
            )
        )
    )
}
