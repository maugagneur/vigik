package com.kidor.vigik.ui.paging

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.SingletonImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.kidor.vigik.ui.theme.dimensions
import timber.log.Timber

private const val COLUMN_ITEM_COUNT = 2f

/**
 * View that display the section dedicated to Paging.
 */
@Composable
fun PagingScreen(
    viewModel: PagingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val images = viewModel.images().collectAsLazyPagingItems()
    var screenSize by remember { mutableStateOf(Size.Zero) }

    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxHeight()
            .onSizeChanged { screenSize = it.toSize() },
        columns = StaggeredGridCells.Fixed(COLUMN_ITEM_COUNT.toInt()),
    ) {
        items(images.itemCount) { index ->
            val url = images[index]?.imageUrl
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .build(),
                imageLoader = SingletonImageLoader.get(LocalContext.current),
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        Timber.e(state.result.throwable, "Error when loading image")
                    }
                }
            )

            val imageWidth = screenSize.width / COLUMN_ITEM_COUNT
            Image(
                painter = painter,
                contentDescription = "$url",
                modifier = Modifier.width(imageWidth.dp),
                contentScale = ContentScale.FillWidth
            )

            painter.state.collectAsState().let { state ->
                if (state.value is AsyncImagePainter.State.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.dimensions.commonSpaceMedium),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
