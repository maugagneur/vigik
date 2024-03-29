package com.kidor.vigik.ui.paging

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.kidor.vigik.R
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to Paging.
 */
@Composable
fun PagingScreen(
    viewModel: PagingViewModel = hiltViewModel()
) {
    val images = viewModel.pagingDataFlow().collectAsLazyPagingItems()
    ShowData(images)
//    when (images.loadState.refresh) {
//        is LoadState.Error -> ErrorView()
//        LoadState.Loading -> LoadingView()
//        is LoadState.NotLoading -> ShowData(images)
//    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
private fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.paging_error_message),
            color = MaterialTheme.colorScheme.error,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun ShowData(images: LazyPagingItems<GeneratedImage>) {
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
