package com.kidor.vigik.ui.emoji

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_EMOJI = "❔"

/**
 * Business logic of Emoji screen.
 */
@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val preferences: DataStore<Preferences>
) : BaseViewModel<EmojiViewAction, EmojiViewState, Nothing>() {

    init {
        viewModelScope.launch {
            val emoji: String = preferences.data.firstOrNull()?.get(PreferencesKeys.EMOJI_PICKED) ?: DEFAULT_EMOJI
            _viewState.value = EmojiViewState.SelectedEmoji(emoji)
        }
    }

    override fun handleAction(viewAction: EmojiViewAction) {
        when (viewAction) {
            is EmojiViewAction.ChangeSelectedEmoji -> {
                viewModelScope.launch {
                    // Save picked Emoji into persistent storage
                    preferences.edit { it[PreferencesKeys.EMOJI_PICKED] = viewAction.emoji }
                }
                _viewState.value = EmojiViewState.SelectedEmoji(viewAction.emoji)
            }
            EmojiViewAction.ClickOnEmoji -> {
                _viewState.value = EmojiViewState.EmojiPicker
            }
        }
    }
}
