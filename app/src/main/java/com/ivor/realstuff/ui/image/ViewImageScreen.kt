package com.ivor.realstuff.ui.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ivor.realstuff.model.Stuff
import com.ivor.realstuff.util.NetworkImage

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ViewImageScreen(viewModel: ImageViewModel, startId: String) {
    val startIndex by viewModel.queryImageIndex(startId).collectAsState(initial = -1)
    val images by viewModel.imagesFlow.collectAsState(initial = emptyList())
    val pagerState = rememberPagerState(pageCount = images.size)
    val showDescription by viewModel.showDescriptionFlow.collectAsState()

    if (startIndex >= 0) {
        LaunchedEffect(key1 = startId) {
            pagerState.scrollToPage(startIndex)
        }
    }

    HorizontalPager(
        modifier = Modifier
            .fillMaxSize(), state = pagerState
    ) { page ->
        ImageDetail(images[page], showDescription) {
            viewModel.toggleShowDescription()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ImageDetail(image: Stuff, showDescription: Boolean, toggleDescription: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { toggleDescription() },
    ) {
        NetworkImage(url = image.url)
        AnimatedVisibility(
            visible = showDescription,
            modifier = Modifier.align(Alignment.BottomStart),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = image.desc,
                    style = MaterialTheme.typography.subtitle2.copy(color = Color.White),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black.copy(alpha = 0.3F))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                )
            }
        }
    }
}
