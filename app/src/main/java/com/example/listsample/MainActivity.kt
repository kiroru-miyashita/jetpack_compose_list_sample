package com.example.listsample

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listsample.ui.theme.ListSampleTheme
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListSampleTheme {
                InfiniteList()
            }
        }
    }
}

const val ReloadLimit = 20
const val InitialLoadCount = 50

data class Post(val nickname: String, val caption: String)

@Composable
fun InfiniteList() {
    val listItems = remember { mutableStateOf(createPostData(0, InitialLoadCount)) }
    val isLoading = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        listItems.value.forEach {
            item {
                ItemCard(post = it, modifier = Modifier.padding(bottom = 8.dp, start = 32.dp, end = 32.dp))
            }
        }
    }

    if (isLoading.value) {
        CircularProgressIndicator()
    }

    listState.OnBottomReached {
        val endCount = listItems.value.count()
        isLoading.value = true
        // 今回は通信処理を行わないが、実際には通信を行う想定として遅延処理を実行
        Handler().postDelayed({
            isLoading.value = false
            listItems.value += createPostData(endCount, ReloadLimit)
        },2000)
    }
}

@Composable
fun ItemCard(modifier: Modifier = Modifier, post: Post) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(all = 16.dp)) {
            Image(
                painter = painterResource(androidx.core.R.drawable.notify_panel_notification_icon_bg),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = post.nickname,
                    color = MaterialTheme.colors.secondaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
fun LazyListState.OnBottomReached(
    loadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?:
                return@derivedStateOf true

            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}

private fun createPostData(offset: Int, limit: Int): List<Post> {
    val names = listOf("Alice", "Jhon", "Smith", "Taro", "Jun", "Debit", "Mike", "Kebin", "Dain", "Kein")
    return (offset + 1..offset + limit).toList().map {
        val random = (0..9).random()
        Post(nickname = names[random], caption = "これは${it}番目のキャプションです。") // フェッチしたpostDataを直接返してもよい。
    }
}

@Preview
@Composable
fun DefaultPreview() {
    ListSampleTheme {
    }
}