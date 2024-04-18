package com.assignment.imageloadingapp.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.assignment.CustomImageLoader
import com.assignment.imageloadingapp.R
import com.assignment.imageloadingapp.data.pojo.UnsplashPhoto
import com.assignment.imageloadingapp.presentation.viewmodel.GalleryViewModel
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.assignment.imageloadingapp.presentation.ui.theme.Purple80
import com.assignment.imageloadingapp.presentation.ui.theme.PurpleGrey80

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    GalleryScreen(
        plantPictures = viewModel.plantPictures,
        onPullToRefresh = viewModel::refreshData,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryScreen(
    plantPictures: Flow<PagingData<UnsplashPhoto>>,
    onPullToRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            GalleryTopBar()
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
                    PhotoListItem(photo = photo)
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
fun PhotoListItem(photo: UnsplashPhoto) {
    ImageListItem(name = photo.user.name, imageUrl = photo.urls.thumb)
}

@Composable
fun ImageListItem(name: String, imageUrl: String) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.card_side_margin))
            .padding(bottom = dimensionResource(id = R.dimen.card_bottom_margin))
    ) {
        Column(Modifier.fillMaxWidth()) {
            var ib by remember { mutableStateOf<ImageBitmap?>(null) }
            CustomImageLoader.getInstance(context).displayImage(
                imageUrl,
                name,
                null
            ) {
                ib = it.asImageBitmap()
            }
            ib?.let {
                Image(
                    bitmap = it,
                    contentDescription = stringResource(R.string.item_image_description),
                    Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.plant_item_image_height)),
                    contentScale = ContentScale.Crop
                )
            } ?: kotlin.run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = PurpleGrey80)
                        .height(dimensionResource(id = R.dimen.plant_item_image_height))
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
