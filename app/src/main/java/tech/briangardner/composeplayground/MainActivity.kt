package tech.briangardner.composeplayground

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.*
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tweet = Tweet(
            displayName = "Brian Gardner",
            handle = "@BrianGardnerDev",
            time = 1,
            content = "This is a test tweet to see how things get rendered in the preview",
            commentCount = 100,
            retweetCount = 10,
            likeCount = 1000
        )
        setContent {
            MaterialTheme {
                TweetView(tweet)
            }
        }
    }
}

// State class for the Tweet data
data class Tweet(
    val displayName: String,
    val handle: String,
    val time: Long,
    val content: String,
    val commentCount: Int,
    val retweetCount: Int,
    val likeCount: Int
)

@Composable
fun TweetView(tweet: Tweet) {
    Column {
        UserInfoRow(
            name = tweet.displayName,
            handle = tweet.handle,
            time = tweet.time
        )
        TweetContent(content = tweet.content)
        ActionRow(
            commentCount = tweet.commentCount,
            retweetCount = tweet.retweetCount,
            likeCount = tweet.likeCount
        )
    }
}

// region information about the user's profile
@Composable
fun UserInfoRow(name: String, handle: String, time: Long) {
    Row(
        modifier = Spacing(8.dp)
    ) {
        DisplayName(name)
        Handle(handle)
        PostTime(time)
    }
}

@Composable
fun DisplayName(name: String) {
    Text(
        text=name,
        modifier = Spacing(0.dp, 0.dp, 8.dp, 0.dp),
        style = TextStyle(
            color = Color.Black, // TODO update this with an appropriate theme color
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun Handle(handle: String) {
    Text(
        text=handle,
        modifier = Spacing(0.dp, 0.dp, 8.dp, 0.dp),
        style = TextStyle(
            color = Color.DarkGray, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        )
    )
}

@Composable
fun PostTime(time: Long) {
    // TODO: Add conversion for tweet time
    Text(
        text="7m",
        style = TextStyle(
            color = Color.DarkGray, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        )
    )
}
// endregion

// region tweet contents
@Composable
fun TweetContent(content: String) {
    return Text(
        text = content,
        style = TextStyle(
            color = Color.Black, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        ),
        modifier = Spacing(8.dp)
    )
}
// endregion

// region actions row
@Composable
fun ActionRow(
    commentCount: Int,
    retweetCount: Int,
    likeCount: Int
) {
    val context = +ambient(ContextAmbient)
    Row(
        modifier = Spacing(8.dp),
        mainAxisSize = LayoutSize.Expand,
        mainAxisAlignment = MainAxisAlignment.SpaceAround
    ) {
        Comment(commentCount) { Toast.makeText(context, "Clicked on comment", Toast.LENGTH_SHORT).show() }
        Retweet(retweetCount){  Toast.makeText(context, "Clicked on retweet", Toast.LENGTH_SHORT).show() }
        Like(likeCount) { Toast.makeText(context, "Clicked on like", Toast.LENGTH_SHORT).show() }
        Share { Toast.makeText(context, "Clicked on share", Toast.LENGTH_SHORT).show() }
    }
}

@Composable
fun Comment(count: Int, onClick : (() -> Unit)) {
    InteractionImage(iconId = R.drawable.ic_comment, count = count, onClick = onClick)
}

@Composable
fun Retweet(count: Int, onClick : (() -> Unit)) {
    InteractionImage(iconId = R.drawable.ic_retweet, count = count, onClick = onClick)
}

@Composable
fun Like(count: Int, onClick : (() -> Unit)) {
    InteractionImage(iconId = R.drawable.ic_like, count = count, onClick = onClick)
}

@Composable
fun InteractionImage(
    @DrawableRes iconId: Int,
    count: Int,
    onClick : (() -> Unit)
    ) {
    val icon = +vectorResource(iconId)
    Clickable(onClick = onClick) {
        Row {
            Container(
                expanded = true,
                height = 24.dp,
                width = 24.dp
            ) {
                DrawVector(
                    vectorImage = icon,
                    tintColor = Color.LightGray
                )
            }
            if (count > 0) {
                Text(
                    "$count",
                    modifier = Spacing(8.dp, 0.dp, 0.dp, 0.dp),
                    style = TextStyle(
                        color = Color.LightGray,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
fun Share(onClick : (() -> Unit)) {
    val icon = +vectorResource(R.drawable.ic_share)
    Clickable(onClick = onClick) {
        Container(
            expanded = true,
            height = 24.dp,
            width = 24.dp
        ) {
            DrawVector(
                vectorImage = icon,
                tintColor = Color.LightGray
            )
        }
    }
}
// endregion

@Preview
@Composable
fun TwitterPreview() {
    val tweet = Tweet(
        displayName = "Brian Gardner",
        handle = "@BrianGardnerDev",
        time = 1,
        content = "This is a test tweet to see how things get rendered in the preview",
        commentCount = 100,
        retweetCount = 10,
        likeCount = 1000
    )
    MaterialTheme {
        TweetView(tweet)
    }
}
