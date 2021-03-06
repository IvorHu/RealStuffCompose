package com.ivor.realstuff.ui.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ivor.realstuff.model.Stuff
import com.ivor.realstuff.ui.widget.StaggeredVerticalGrid
import com.ivor.realstuff.util.AutoLoadMore
import com.ivor.realstuff.util.HomeCategory
import com.ivor.realstuff.util.NetworkImage
import com.ivor.realstuff.util.supportWideScreen

private const val LOAD_MORE_THRESHOLD = 0.6F

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    selectArticle: (String) -> Unit,
    viewImage: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    // TODO: 2021/7/19 modify refresh condition
    if (state.stuffs.isEmpty()) {
        LaunchedEffect(state.tab) {
            viewModel.refresh()
        }
    }

    Column {
        Spacer(
            Modifier.statusBarsHeight()
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .supportWideScreen()
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = state.loading),
                onRefresh = {
                    viewModel.refresh()
                }
            ) {
                ListContent(state, viewModel, selectArticle, viewImage)
            }
        }
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
        BottomTab(state, viewModel)
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun BottomTab(state: HomeViewState, viewModel: HomeViewModel) {
    TabRow(
        selectedTabIndex = state.tab.ordinal,
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
    ) {
        HomeCategory.values().forEach { tab ->
            val colorText = if (state.tab == tab) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            }
            Tab(
                selected = state.tab == tab,
                onClick = {
                    viewModel.updateTab(tab)
                },
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = tab.title,
                    color = colorText,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.paddingFromBaseline(top = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun ListContent(
    state: HomeViewState,
    viewModel: HomeViewModel,
    selectArticle: (String) -> Unit,
    viewImage: (String) -> Unit,
) {
    when (state.tab) {
        HomeCategory.Image -> ImageList(state, viewModel, viewImage)
        HomeCategory.ANDROID -> ArticleList(state, viewModel, selectArticle)
        HomeCategory.IOS -> ArticleList(state, viewModel, selectArticle)
        HomeCategory.WEB -> ArticleList(state, viewModel, selectArticle)
    }
}

@Composable
private fun ImageList(state: HomeViewState, viewModel: HomeViewModel, viewImage: (String) -> Unit) {
    val scrollState = viewModel.scrollState as ScrollState
    Column(
        modifier = Modifier
            .verticalScroll(state = scrollState)
    ) {
        StaggeredVerticalGrid(
            maxColumnWidth = 220.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            state.stuffs.map { stuff ->
                ImageStuff(stuff, viewImage)
            }
        }
    }
    scrollState.AutoLoadMore(shouldLoadMore = { value, maxValue ->
        value > maxValue * LOAD_MORE_THRESHOLD && maxValue > value
    }) {
        viewModel.fetch()
    }
}

@Composable
private fun ArticleList(
    state: HomeViewState,
    viewModel: HomeViewModel,
    selectArticle: (String) -> Unit,
) {
    val scrollState = viewModel.scrollState as LazyListState

    LazyColumn(state = scrollState) {
        items(state.stuffs, key = { it.id }) { stuff: Stuff ->
            ArticleStuff(stuff, selectArticle)
        }
    }
    scrollState.AutoLoadMore(shouldLoadMore = { lastIndex: Int, totalNumber: Int ->
        lastIndex > totalNumber * LOAD_MORE_THRESHOLD && totalNumber > lastIndex
    }) {
        viewModel.fetch()
    }
}

@Composable
private fun ImageStuff(stuff: Stuff, viewImage: (String) -> Unit) {
    Surface(modifier = Modifier
        .clickable {
            viewImage(stuff.id)
        }
        .padding(4.dp)) {
        Column {
            NetworkImage(
                url = stuff.url,
                contentDescription = stuff.desc,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 123.dp, max = 390.dp),
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stuff.desc,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(8.dp),
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun ArticleStuff(stuff: Stuff, selectArticle: (String) -> Unit) {
    Column(modifier = Modifier
        .clickable {
            selectArticle(stuff.url)
        }
        .padding(horizontal = 16.dp)) {
        Text(
            text = stuff.title,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stuff.desc,
            style = MaterialTheme.typography.body1,
            maxLines = 3,
            lineHeight = 20.sp,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = stuff.author, style = MaterialTheme.typography.caption)
                Text(text = stuff.publishedAt, style = MaterialTheme.typography.caption)
            }
        }
        Divider()
    }
}

@Preview("ImageStuff")
@Composable
private fun ImageStuffPreview() {
    val imageStuff = Stuff(
        id = "5e95923f808d6d2fe6b56ed8",
        author = "",
        category = "",
        createdAt = "2020-05-24 08:00:00",
        desc = "???????????????????????????????????????????????????????????????????????????",
        images = emptyList(),
        likeCounts = 5,
        publishedAt = "2020-05-24 08:00:00",
        stars = 1,
        title = "???95???",
        type = "",
        url = "http://gank.io/images/dc75cbde1d98448183e2f9514b4d1320",
        views = 8200,
    )
    ImageStuff(stuff = imageStuff) {}
}

@Preview("ArticleStuff", showBackground = true)
@Composable
private fun ArticleStuffPreview() {
    val stuff = Stuff(
        id = "6075ce2aee6ba981da2af445",
        author = "24k-?????????",
        category = "GanHuo",
        createdAt = "2021-04-14 01:00:26",
        desc = "?????????????????????ReadIT????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????",
        images = emptyList(),
        likeCounts = 5,
        publishedAt = "2021-04-14 01:00:26",
        stars = 1,
        title = "?????????????????????ReadIT???????????????RN????????????",
        type = "Android",
        url = "https://github.com/xiaobinwu/readIt",
        views = 96,
    )
    ArticleStuff(stuff) {}
}