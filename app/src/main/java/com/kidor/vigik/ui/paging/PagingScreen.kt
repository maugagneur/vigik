package com.kidor.vigik.ui.paging

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to Paging.
 */
@Composable
fun PagingScreen(
    viewModel: PagingViewModel = hiltViewModel()
) {
    val images = viewModel.pagingDataFlow().collectAsLazyPagingItems()
    var screenSize by remember { mutableStateOf(Size.Zero) }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .onSizeChanged { screenSize = it.toSize() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(images.itemCount) { index ->
            val painter = rememberAsyncImagePainter(model = images[index]?.imageUrl)

            var isImageLoading by remember { mutableStateOf(false) }
            isImageLoading = painter.state is AsyncImagePainter.State.Loading

            // We have to specify the height without what the painter will not draw its content as it is in a lazy
            // column witch has an unbounded height constraint.
            val imageHeight = screenSize.width * (images[index]?.ratio ?: 1f) / 2
            Image(
                painter = painter,
                contentDescription = "${images[index]?.imageUrl}",
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
