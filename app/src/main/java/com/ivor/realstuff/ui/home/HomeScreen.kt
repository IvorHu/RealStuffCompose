package com.ivor.realstuff.ui.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun HomeScreen() {
    val viewModel: HomeViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    // TODO: 2021/7/19 modify refresh condition
    if (state.stuffs.isEmpty()) {
        LaunchedEffect(state.tab) {
            viewModel.refresh()
        }
    }

    Column {
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
                ListContent(state, viewModel)
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
private fun ListContent(state: HomeViewState, viewModel: HomeViewModel) {
    when (state.tab) {
        HomeCategory.Image -> ImageList(state, viewModel)
        HomeCategory.ANDROID -> ArticleList(state, viewModel)
        HomeCategory.IOS -> ArticleList(state, viewModel)
        HomeCategory.WEB -> ArticleList(state, viewModel)
    }
}

@Composable
private fun ImageList(state: HomeViewState, viewModel: HomeViewModel) {
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
                ImageStuff(stuff)
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
private fun ArticleList(state: HomeViewState, viewModel: HomeViewModel) {
    val scrollState = viewModel.scrollState as LazyListState

    LazyColumn(state = scrollState) {
        items(state.stuffs, key = { it.id }) { stuff: Stuff ->
            ArticleStuff(stuff)
        }
    }
    scrollState.AutoLoadMore(shouldLoadMore = { lastIndex: Int, totalNumber: Int ->
        lastIndex > totalNumber * LOAD_MORE_THRESHOLD && totalNumber > lastIndex
    }) {
        viewModel.fetch()
    }
}

@Composable
private fun ImageStuff(stuff: Stuff) {
    Surface(modifier = Modifier
        .clickable {
            // TODO: 2021/7/27 view image
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
            Text(
                text = stuff.desc,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(8.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun ArticleStuff(stuff: Stuff) {
    Column(modifier = Modifier
        .clickable {
            // TODO: 2021/7/27 view article
        }
        .padding(horizontal = 16.dp)) {
        Text(
            text = stuff.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stuff.desc,
            fontSize = 14.sp,
            maxLines = 3,
            lineHeight = 20.sp,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Text(text = stuff.author, fontSize = 12.sp)
            Text(text = stuff.publishedAt, fontSize = 12.sp)
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
        desc = "这世界总有人在笨拙地爱着你，想把全部的温柔都给你。",
        images = emptyList(),
        likeCounts = 5,
        publishedAt = "2020-05-24 08:00:00",
        stars = 1,
        title = "第95期",
        type = "",
        url = "http://gank.io/images/dc75cbde1d98448183e2f9514b4d1320",
        views = 8200,
    )
    ImageStuff(stuff = imageStuff)
}

@Preview("ArticleStuff", showBackground = true)
@Composable
private fun ArticleStuffPreview() {
    val stuff = Stuff(
        id = "6075ce2aee6ba981da2af445",
        author = "24k-小清新",
        category = "GanHuo",
        createdAt = "2021-04-14 01:00:26",
        desc = "一款轻阅读应用ReadIT，支持功能：优质文章推送、评论点赞、计划制定、计划闹钟、天气预报、收藏文章、深浅两套主题、多语言切换、极光推送等功能。后续还会继续集成功能，目前文章数也比较少。前后端均自主研发，借鉴市面较好的种子工程，目前正准备上应用市场。",
        images = emptyList(),
        likeCounts = 5,
        publishedAt = "2021-04-14 01:00:26",
        stars = 1,
        title = "一款轻阅读应用ReadIT，记录我的RN躺坑之旅",
        type = "Android",
        url = "https://github.com/xiaobinwu/readIt",
        views = 96,
    )
    ArticleStuff(stuff)
}