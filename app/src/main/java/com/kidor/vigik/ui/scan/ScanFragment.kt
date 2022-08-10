package com.kidor.vigik.ui.scan

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.BaseFragment
import com.kidor.vigik.ui.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint

internal const val FLOATING_ACTION_BUTTON_TEST_TAG = "Floating action button"
internal const val TAG_DATA_TEXT_TEST_TAG = "Tag data text"
internal const val PROGRESS_BAR_TEST_TAG = "Progress bar"

/**
 * View that scan for a NFC tag.
 */
@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewAction, ScanViewState, ScanViewEvent, ScanViewModel>() {

    override val viewModel by viewModels<ScanViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_scan, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return if (menuItem.itemId == R.id.action_stop_scan) {
                        activity?.onBackPressed()
                        true
                    } else {
                        false
                    }
                }
            },
            viewLifecycleOwner,     // Tie the MenuProvider to the parent activity
            Lifecycle.State.RESUMED // Indicate that the menu should be add/visible on the RESUME Lifecycle.State
        )
    }

    @Composable
    override fun StateRender(viewState: ScanViewState) {
        when (viewState) {
            is ScanViewState.DisplayTag ->
                DisplayTagState(DisplayTagStateData(viewState) { action -> viewModel.handleAction(action) })
            ScanViewState.Loading -> LoadingState()
        }
    }

    @Composable
    override fun EventRender(viewEvent: ScanViewEvent) {
        when (viewEvent) {
            ScanViewEvent.SaveTagFailure -> promptMessage(R.string.save_tag_fail)
            ScanViewEvent.SaveTagSuccess -> promptMessage(R.string.save_tag_success)
        }
    }

    private fun promptMessage(@StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun DisplayTagState(@PreviewParameter(DisplayTagStateDataProvider::class) displayTagStateData: DisplayTagStateData) {
    Scaffold(
        floatingActionButton = {
            if (displayTagStateData.state.canBeSaved) {
                FloatingActionButton(
                    onClick = { displayTagStateData.onViewAction(ScanViewAction.SaveTag) },
                    modifier = Modifier.testTag(FLOATING_ACTION_BUTTON_TEST_TAG)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = displayTagStateData.state.tag.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimensions.commonSpaceSmall)
                        .testTag(TAG_DATA_TEXT_TEST_TAG),
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
            }
        }
    )
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun LoadingState() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag(PROGRESS_BAR_TEST_TAG),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
