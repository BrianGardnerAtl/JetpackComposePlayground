package tech.briangardner.composeplayground

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.Clip
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.vector.DrawVector
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.res.vectorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tweetList = generateFakeTweetList()
        val state = mutableStateOf(tweetList, StructurallyEqual)
        setContent {
            MaterialTheme {
                TweetList(state = state)
            }
        }
    }
}

// State class for the Tweet data
@Model
data class Tweet(
    val displayName: String,
    val handle: String,
    val time: String,
    val content: String,
    val commentCount: Int,
    val retweeted: Boolean,
    val retweetCount: Int,
    val liked: Boolean,
    val likeCount: Int
)

// region tweet list view
@Composable
fun TweetList(state: MutableState<MutableList<Tweet>>) {
    val tweetList = state.value
    AdapterList(
        data = tweetList,
        modifier = LayoutPadding(8.dp)
    ) { tweet ->
        val index = tweetList.indexOf(tweet)
        Log.d("TEST", "itemCallback for $index")
        val commentClick: (() -> Unit) = {
            Log.d("TEST", "Clicked on comment: $index")
            val newCount = tweet.commentCount + 1
            val newTweet = tweet.copy(
                commentCount = newCount
            )
            tweetList[index] = newTweet
            state.value = tweetList
        }
        val retweetToggle: ((Boolean) -> Unit) = { retweet ->
            Log.d("TEST", "Clicked on retweet: $index")
            val retweetCount = if (retweet) {
                tweet.retweetCount + 1
            } else {
                tweet.retweetCount - 1
            }
            val newTweet = tweet.copy(
                retweeted = retweet,
                retweetCount = retweetCount
            )
            tweetList[index] = newTweet
            state.value = tweetList
        }
        val likeToggle: ((Boolean) -> Unit) = { liked ->
            Log.d("TEST", "Clicked on like: $index")
            val likeCount = if (liked) {
                tweet.likeCount + 1
            } else {
                tweet.likeCount - 1
            }
            val newTweet = tweet.copy(
                liked = liked,
                likeCount = likeCount
            )
            tweetList[index] = newTweet
            state.value = tweetList
        }
        TweetView(
            tweet = tweet,
            commentClick = commentClick,
            retweetToggle = retweetToggle,
            likeToggle = likeToggle
        )
    }
}
// endregion

// region individual tweet view
@Composable
fun TweetView(
    tweet: Tweet,
    commentClick: () -> Unit,
    retweetToggle: (Boolean) -> Unit,
    likeToggle: (Boolean) -> Unit
) {
    Row {
        ProfileImage()
        Column {
            UserInfoRow(
                name = tweet.displayName,
                handle = tweet.handle,
                time = tweet.time
            )
            TweetContent(content = tweet.content)
            ActionRow(
                commentCount = tweet.commentCount,
                commentClick = commentClick,
                retweeted = tweet.retweeted,
                retweetCount = tweet.retweetCount,
                retweetToggle = retweetToggle,
                liked = tweet.liked,
                likeCount = tweet.likeCount,
                likeToggle = likeToggle
            )
        }
    }
}
// endregion

// region information about the user's profile
@Composable
fun UserInfoRow(name: String, handle: String, time: String) {
    Row(
        modifier = LayoutPadding(8.dp)
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
        modifier = LayoutPadding(0.dp, 0.dp, 8.dp, 0.dp),
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
        modifier = LayoutPadding(0.dp, 0.dp, 8.dp, 0.dp),
        style = TextStyle(
            color = Color.DarkGray, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        )
    )
}

@Composable
fun PostTime(time: String) {
    // TODO: Add conversion for tweet time
    Text(
        text=time,
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
        modifier = LayoutPadding(8.dp)
    )
}
// endregion

// region actions row
@Composable
fun ActionRow(
    commentCount: Int,
    commentClick: (() -> Unit),
    retweeted: Boolean,
    retweetCount: Int,
    retweetToggle: ((Boolean) -> Unit),
    liked: Boolean,
    likeCount: Int,
    likeToggle: ((Boolean) -> Unit)
) {
    val context = ContextAmbient.current
    Row(
        modifier = LayoutWidth.Fill + LayoutPadding(8.dp),
        arrangement = Arrangement.SpaceAround
    ) {
        Comment(commentCount, commentClick)
        Retweet(retweetCount, retweeted, retweetToggle)
        Like(likeCount, liked, likeToggle)
        Share { Toast.makeText(context, "Clicked on share", Toast.LENGTH_SHORT).show() }
    }
}

@Composable
fun Comment(count: Int, onClick : (() -> Unit)) {
    Clickable(onClick = onClick) {
        val icon = vectorResource(R.drawable.ic_comment)
        Row {
            Container(
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
                    text = "$count",
                    modifier = LayoutPadding(8.dp, 0.dp, 0.dp, 0.dp),
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.LightGray
                    )
                )
            }
        }
    }
}

@Composable
fun Retweet(count: Int, retweeted: Boolean, onValueChange : ((Boolean) -> Unit)) {
    ToggleImage(
        iconId = R.drawable.ic_retweet,
        count = count,
        checked = retweeted,
        onValueChange = onValueChange,
        selectedColor = Color.Green
    )
}

@Composable
fun Like(count: Int, liked: Boolean, onValueChange : ((Boolean) -> Unit)) {
    ToggleImage(
        iconId = R.drawable.ic_like,
        count = count,
        checked = liked,
        onValueChange = onValueChange,
        selectedColor = Color.Red
    )
}

@Composable
fun ToggleImage(
    @DrawableRes iconId: Int,
    count: Int,
    checked: Boolean,
    onValueChange: ((Boolean) -> Unit),
    selectedColor: Color
) {
    val icon = vectorResource(iconId)
    val color = if (checked) {
        selectedColor
    } else {
        Color.LightGray
    }
    Toggleable(value = checked, onValueChange = onValueChange) {
        Row {
            Container(
                expanded = true,
                height = 24.dp,
                width = 24.dp
            ) {
                DrawVector(
                    vectorImage = icon,
                    tintColor = color
                )
            }
            if (count > 0) {
                Text(
                    text = "$count",
                    modifier = LayoutPadding(8.dp, 0.dp, 0.dp, 0.dp),
                    style = TextStyle(
                        color = color,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
fun Share(onClick : (() -> Unit)) {
    val icon = vectorResource(R.drawable.ic_share)
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

// region profile image
@Composable
fun ProfileImage() {
    val defaultPhoto = vectorResource(id = R.drawable.ic_profile_photo_default)
    Container(modifier = LayoutPadding(8.dp)) {
        Clip(shape = CircleShape) {
            Surface(color = Color.DarkGray) {
                Container(
                    height = 36.dp,
                    width = 36.dp
                ) {
                    DrawVector(
                        vectorImage = defaultPhoto
                    )
                }
            }
        }
    }
}

// endregion

@Preview
@Composable
fun TwitterPreview() {
    val commentClick: (() -> Unit) = {
    }
    val retweetToggle: ((Boolean) -> Unit) = { retweet ->
    }
    val likeToggle: ((Boolean) -> Unit) = { liked ->
    }
    val tweet = Tweet(
        displayName = "Brian Gardner",
        handle = "@BrianGardnerDev",
        time = "7m",
        content = "This is a test tweet to see how things get rendered in the preview",
        commentCount = 100,
        retweeted = false,
        retweetCount = 10,
        liked = false,
        likeCount = 1000
    )
    MaterialTheme {
        TweetView(
            tweet = tweet,
            commentClick = commentClick,
            retweetToggle = retweetToggle,
            likeToggle = likeToggle
        )
    }
}

@Preview
@Composable
fun TweetListPreview() {
    val tweetList = generateFakeTweetList()
    val state = mutableStateOf(tweetList)
    TweetList(state = state)
}

fun generateFakeTweetList(): MutableList<Tweet> {
    return (0..100).map {
        Tweet(
            displayName = "Brian Gardner$it",
            handle = "@BrianGardnerDev$it",
            time = "7m",
            content = "This is a test tweet to see how things get rendered in the preview",
            commentCount = it*1,
            retweeted = it%2==0,
            retweetCount = it*10,
            liked = it%2==1,
            likeCount = it*100
        )
    }.toMutableList()
}
