package com.ivor.realstuff.util

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LazyListState.AutoLoadMore(
    shouldLoadMore: (lastIndex: Int, totalNumber: Int) -> Boolean,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            shouldLoadMore(lastVisibleItemIndex, totalItemsNumber)
        }
    }

    ListenLoadMore(loadMore, onLoadMore)
}

@Composable
fun ScrollState.AutoLoadMore(
    shouldLoadMore: (lastIndex: Int, totalNumber: Int) -> Boolean,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            shouldLoadMore(value, maxValue)
        }
    }

    ListenLoadMore(loadMore, onLoadMore)
}

@Composable
private fun ListenLoadMore(
    loadMore: State<Boolean>,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    onLoadMore()
                }
            }
    }
}
