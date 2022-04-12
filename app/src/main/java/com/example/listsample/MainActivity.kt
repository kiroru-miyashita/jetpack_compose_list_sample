package com.example.listsample

import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listsample.ui.theme.ListSampleTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

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

data class Post(val nickname: String, val caption: String)

const val ReloadLimit = 25
const val MaxCount = 200

@Composable
fun InfiniteList() {
    var listItem = remember { mutableStateOf(createListItem(end = 0, count = ReloadLimit)) }
    Surface(color = MaterialTheme.colors.background) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            listItem.value.forEach {
                item {
                    ItemCard(post = it, modifier = Modifier.padding(bottom = 8.dp, start = 32.dp, end = 32.dp))
                }
            }

            // アイテムの上限数も設定可能
//            val isLast= listItem.value.count() >= MaxCount
//            if (isLast.not()) {
                item {
                    Loading(
                        modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)) {
                        val endCount = listItem.value.count()
                        listItem.value = listItem.value + createListItem(endCount, ReloadLimit)
                    }
                }
//            }
        }
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

fun createListItem(end: Int, count: Int): List<Post> {
    val names = listOf("Alice", "Jhon", "Smith", "Taro", "Jun", "Debit", "Mike", "Kebin", "Dain", "Kein")
    return (end + 1..end + count).toList().map {
        val random = (0..9).random()
        Post(nickname = names[random], caption = "これは${it}番目のキャプションです。") // フェッチしたpostDataを直接返してもよい。
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier, onLaunch: () -> Unit) {
    CircularProgressIndicator(modifier = modifier)

    LaunchedEffect(key1 = true) {
        onLaunch()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    ListSampleTheme {
    }
}