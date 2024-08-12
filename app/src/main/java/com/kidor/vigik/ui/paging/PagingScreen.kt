package com.kidor.vigik.ui.paging

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kidor.vigik.ui.compose.AppTheme
import timber.log.Timber

private const val IMAGE_HEIGHT_RATIO = 0.5f

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
        columns = StaggeredGridCells.Fixed(2),
    ) {
        items(images.itemCount) { index ->
            val url = images[index]?.imageUrl
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .size(coil.size.Size.ORIGINAL)
                    .build(),
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        Timber.e(state.result.throwable, "Error when loading image")
                    }
                }
            )

            var isImageLoading by remember { mutableStateOf(false) }
            isImageLoading = painter.state is AsyncImagePainter.State.Loading

            // We have to specify the height without what the painter will not draw its content as it is in a lazy
            // column witch has an unbounded height constraint.
            val imageHeight = screenSize.width * (images[index]?.ratio ?: 1f) * IMAGE_HEIGHT_RATIO
            Image(
                painter = painter,
                contentDescription = "$url",
                modifier = Modifier
                    .height(imageHeight.dp)
                    .width(screenSize.width.dp),
                contentScale = ContentScale.FillBounds
            )

            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .apply { if (imageHeight > 0) height(imageHeight.dp) }
                        .padding(AppTheme.dimensions.commonSpaceMedium),
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
