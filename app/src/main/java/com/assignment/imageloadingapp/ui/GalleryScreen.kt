package com.assignment.imageloadingapp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.assignment.ImageUtil
import com.assignment.caching.CustomCaching
import com.assignment.imageloadingapp.CustomCachingAsync
import com.assignment.imageloadingapp.CustomImageLoader
import com.assignment.imageloadingapp.R
import com.assignment.imageloadingapp.data.UnsplashPhoto
import com.assignment.imageloadingapp.data.UnsplashPhotoUrls
import com.assignment.imageloadingapp.data.UnsplashUser
import com.assignment.imageloadingapp.viewmodel.GalleryViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.State
import com.assignment.exp.ImageLoader

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onPhotoClick: (UnsplashPhoto) -> Unit,
    onUpClick: () -> Unit,
) {
    GalleryScreen(
        plantPictures = viewModel.plantPictures,
        onPhotoClick = onPhotoClick,
        onUpClick = onUpClick,
        onPullToRefresh = viewModel::refreshData,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryScreen(
    plantPictures: Flow<PagingData<UnsplashPhoto>>,
    onPhotoClick: (UnsplashPhoto) -> Unit = {},
    onUpClick: () -> Unit = {},
    onPullToRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            GalleryTopBar(onUpClick = onUpClick)
        },
    ) { padding ->

        val pullToRefreshState = rememberPullToRefreshState()

        if (pullToRefreshState.isRefreshing) {
            onPullToRefresh()
        }

        val pagingItems: LazyPagingItems<UnsplashPhoto> =
            plantPictures.collectAsLazyPagingItems()

        LaunchedEffect(pagingItems.loadState) {
            when (pagingItems.loadState.refresh) {
                is LoadState.Loading -> Unit
                is LoadState.Error, is LoadState.NotLoading -> {
                    pullToRefreshState.endRefresh()
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.card_side_margin))
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it.id + it.urls.thumb }
                ) { index ->
                    val photo = pagingItems[index] ?: return@items
                    PhotoListItem(photo = photo) {
                        onPhotoClick(photo)
                    }
                }
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryTopBar(
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.gallery_title))
        },
        modifier = modifier.statusBarsPadding(),
    )
}

@Composable
fun PhotoListItem(photo: UnsplashPhoto, onClick: () -> Unit) {
    ImageListItem(name = photo.user.name, imageUrl = photo.urls.thumb, onClick = onClick)
}

@Composable
fun ImageListItem(name: String, imageUrl: String, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_bottom_margin))
    ) {
        Column(Modifier.fillMaxWidth()) {
            var btmp: ImageBitmap? = null
            LaunchedEffect(Unit) {
                btmp = CustomImageLoader.getInstance(context).getBitmap(imageUrl,name)?.asImageBitmap()
            }
            btmp?.let {
                Image(
                    bitmap = it,
                    contentDescription = stringResource(R.string.item_image_description),
                    Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.plant_item_image_height)),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.margin_normal))
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun loadNetworkImage(
    url: String,
    id: String,
    imageLoader: ImageLoader = CustomImageLoader.getInstance(LocalContext.current)
): State<Result<Bitmap>> {

    // Creates a State<T> with Result.Loading as initial value
    // If either `url` or `imageRepository` changes, the running producer
    // will cancel and will be re-launched with the new inputs.
    return produceState<Result<Bitmap>>(initialValue = Result.Loading, url, imageLoader) {

        // In a coroutine, can make suspend calls
        val bitmap = imageLoader.getBitmap(url,id)

        // Update State with either an Error or Success result.
        // This will trigger a recomposition where this State is read
        value = if (bitmap == null) {
            Result.Error
        } else {
            Result.Success(bitmap)
        }
    }
}

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    object Error : Result<Nothing>()
    class Success<T>(t: T?) : Result<T>()
}