package tech.briangardner.composeplayground

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.selection.Toggleable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.ripple.ripple
import androidx.ui.res.imageResource
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
                ListScreen(state)
            }
        }
    }
}

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

@Composable
fun ListScreen(state: MutableState<MutableList<Tweet>>) {
    val (scaffoldState, onScaffoldStateChange) = state {
        ScaffoldState(
            drawerState = DrawerState.Closed,
            isDrawerGesturesEnabled = true
        )
    }

    val context = ContextAmbient.current
    Scaffold(
        scaffoldState = scaffoldState,
        topAppBar = {
            TweetBar {
                if (scaffoldState.drawerState == DrawerState.Closed) {
                    onScaffoldStateChange(ScaffoldState(DrawerState.Opened))
                } else {
                    onScaffoldStateChange(ScaffoldState(DrawerState.Closed))
                }
            }
        },
        bottomAppBar = { fabConfiguration ->
            TweetBottomBar(
                fabConfiguration,
                homeListener = {
                    Toast.makeText(context, "Clicked on home", Toast.LENGTH_SHORT).show()
                },
                searchListener = {
                    Toast.makeText(context, "Clicked on search", Toast.LENGTH_SHORT).show()
                },
                notificationListener = {
                    Toast.makeText(context, "Clicked on notifications", Toast.LENGTH_SHORT).show()
                },
                messageListener = {
                    Toast.makeText(context, "Clicked on messages", Toast.LENGTH_SHORT).show()
                }
            )
        },
        floatingActionButton = {
            AddTweetButton()
        },
        floatingActionButtonPosition = Scaffold.FabPosition.End,
        drawerContent = {
            TweetNavigation()
        },
        bodyContent= {
            TweetList(state)
        }
    )
}

@Composable
fun TweetNavigation() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Profile")
        Text("Lists")
        Text("Topics")
        Text("Bookmarks")
        Text("Moments")
        Text("Settings and privacy")
        Text("Help center")
    }
}

@Composable
fun TweetBottomBar(
    fabConfiguration: BottomAppBar.FabConfiguration?,
    homeListener: () -> Unit,
    searchListener: () -> Unit,
    notificationListener: () -> Unit,
    messageListener: () -> Unit
) {
    BottomAppBar(
        fabConfiguration = fabConfiguration,
        cutoutShape = CircleShape
    ) {
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = homeListener
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_home)
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = searchListener
        ) {            Icon(
                asset = vectorResource(id = R.drawable.ic_search)
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = notificationListener
        ) {            Icon(
                asset = vectorResource(id = R.drawable.ic_notifications)
            )
        }
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = messageListener
        ) {            Icon(
                asset = vectorResource(id = R.drawable.ic_message)
            )
        }
        // This box ensures that a FAB with an EndDocked position will not overlap the IconButtons
//        Box(
//            modifier = Modifier.weight(1f)
//        )
    }
}

@Composable
fun TweetBar(navIconClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Tweetish")
        },
        navigationIcon = {
            IconButton(onClick = navIconClick) {
                Icon(asset = vectorResource(id = R.drawable.ic_nav_drawer))
            }
        }
    )
}

// region tweet list
@Composable
fun TweetList(state: MutableState<MutableList<Tweet>>) {
    val list = state.value
    AdapterList(
        data = list
    ) { tweet ->
        val index = list.indexOf(tweet)
        val commentClick: (() -> Unit) = {
            // grab the new tweet
            val oldTweet = list[index]
            val newCount = oldTweet.commentCount + 1
            val newTweet = oldTweet.copy(
                commentCount = newCount
            )
            list[index] = newTweet
        }
        val retweetToggle: ((Boolean) -> Unit) = { retweet ->
            val oldTweet = list[index]
            val retweetCount = if (retweet) {
                oldTweet.retweetCount + 1
            } else {
                oldTweet.retweetCount - 1
            }
            val newTweet = oldTweet.copy(
                retweeted = retweet,
                retweetCount = retweetCount
            )
            list[index] = newTweet
        }
        val likeToggle: ((Boolean) -> Unit) = { liked ->
            val oldTweet = list[index]
            val likeCount = if (liked) {
                oldTweet.likeCount + 1
            } else {
                oldTweet.likeCount - 1
            }
            val newTweet = oldTweet.copy(
                liked = liked,
                likeCount = likeCount
            )
            list[index] = newTweet
        }
        TweetView(
            tweet = tweet,
            commentClick = commentClick,
            retweetToggle = retweetToggle,
            likedToggle = likeToggle
        )
    }
}
// endregion

// region single tweet view
@Composable
fun TweetView(
    tweet: Tweet,
    commentClick: () -> Unit,
    retweetToggle: (Boolean) -> Unit,
    likedToggle: (Boolean) -> Unit
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
                onRetweetChanged = retweetToggle,
                liked = tweet.liked,
                likeCount = tweet.likeCount,
                onLikeChanged = likedToggle
            )
        }
    }
}
// endregion

// region user info row
@Composable
fun UserInfoRow(name: String, handle: String, time: String) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        DisplayName(name = name)
        Handle(handle = handle)
        PostTime(time = time)
    }
}

@Composable
fun DisplayName(name: String) {
    Text(
        text = name,
        modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
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
        text = handle,
        modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
        style = TextStyle(
            color = Color.DarkGray, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        )
    )
}

@Composable
fun PostTime(time: String) {
    Text(
        text = time,
        style = TextStyle(
            color = Color.DarkGray, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        )
    )
}
// endregion

// region tweet content
@Composable
fun TweetContent(content: String) {
    return Text(
        text = content,
        style = TextStyle(
            color = Color.Black, // TODO update this with an appropriate theme color
            fontSize = 12.sp
        ),
        modifier = Modifier.padding(8.dp)
    )
}
// endregion

// region action row
@Composable
fun ActionRow(
    commentCount: Int,
    commentClick: (() -> Unit),
    retweeted: Boolean,
    retweetCount: Int,
    onRetweetChanged: (Boolean) -> Unit,
    liked: Boolean,
    likeCount: Int,
    onLikeChanged: (Boolean) -> Unit
) {
    val context = ContextAmbient.current
    Row(
        modifier = Modifier.fillMaxWidth()
                           .padding(8.dp),
        arrangement = Arrangement.SpaceAround
    ) {
        Comment(commentCount, commentClick)
        Retweet(retweetCount, retweeted, onRetweetChanged)
        Like(likeCount, liked, onLikeChanged)
        Share {
            Toast.makeText(context, "Clicked on share", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Comment(count: Int, onClick : () -> Unit) {
    Clickable(
        onClick = onClick,
        modifier = Modifier.ripple()) {
        val icon = vectorResource(R.drawable.ic_comment)
        Row {
            Icon(
                asset = icon,
                modifier = Modifier.preferredSize(24.dp),
                tint = Color.LightGray
            )
            if (count > 0) {
                Text(
                    text = "$count",
                    modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
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
fun Retweet(count: Int, retweeted: Boolean, onValueChange: (Boolean) -> Unit) {
    ToggleImage(
        iconId = R.drawable.ic_retweet,
        count = count,
        checked = retweeted,
        selectedColor = Color.Green,
        onValueChange = onValueChange
    )
}

@Composable
fun Like(count: Int, liked: Boolean, onValueChange: (Boolean) -> Unit) {
    ToggleImage(
        iconId = R.drawable.ic_like,
        count = count,
        checked = liked,
        selectedColor = Color.Red,
        onValueChange = onValueChange
    )
}

@Composable
fun ToggleImage(
    @DrawableRes iconId: Int,
    count: Int,
    checked: Boolean,
    selectedColor: Color,
    onValueChange: ((Boolean) -> Unit)
) {
    val icon = vectorResource(iconId)
    val color = if (checked) {
        selectedColor
    } else {
        Color.LightGray
    }
    Toggleable(
        value = checked,
        onValueChange = onValueChange,
        modifier = Modifier.ripple()
    ) {
        Row {
            Icon(
                asset = icon,
                modifier = Modifier.preferredSize(24.dp),
                tint = color
            )
            if (count > 0) {
                Text(
                    text = "$count",
                    modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
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
fun Share(onClick : () -> Unit) {
    val icon = vectorResource(R.drawable.ic_share)
    IconButton(
        onClick = onClick,
        modifier = Modifier.preferredSize(24.dp)
    ) {
        Icon(
            asset = icon,
            modifier = Modifier.preferredSize(24.dp),
            tint = Color.LightGray
        )
    }
}
// endregion

// region profile image
@Composable
fun ProfileImage() {
    val defaultPhoto = vectorResource(id = R.drawable.ic_profile_photo_default)
    Surface(
        color = Color.DarkGray,
        shape = CircleShape,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            asset = defaultPhoto,
            modifier = Modifier.preferredSize(36.dp),
            tint = Color.LightGray
        )
    }
}
// endregion

// region add tweet
@Composable
fun AddTweetButton() {
    val icon = imageResource(R.drawable.ic_add)
    val context = ContextAmbient.current
    FloatingActionButton(
        onClick = {
            Toast.makeText(context, "Clicked on FAB", Toast.LENGTH_SHORT).show()
        }
    ) {
        Icon(
            asset = icon,
            modifier = Modifier.preferredSize(48.dp)
        )
    }
}
// endregion

// region preview functions
@Preview
@Composable
fun TwitterPreview() {
    val commentClick: (() -> Unit) = {
    }
    val retweetToggle: ((Boolean) -> Unit) = { _ ->
    }
    val likeToggle: ((Boolean) -> Unit) = { _ ->
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
            likedToggle = likeToggle
        )
    }
}

@Preview
@Composable
fun TweetProfileImagePreview() {
    MaterialTheme {
        ProfileImage()
    }
}

@Preview
@Composable
fun TweetListPreview() {
    val tweetList = generateFakeTweetList()
    val state = mutableStateOf(tweetList)
    TweetList(state = state)
}

@Preview
@Composable
fun AddTweetPreview() {
    AddTweetButton()
}

@Preview
@Composable
fun TweetBarPreview() {
    TweetBar {
    }
}
// endregion

// region generator functions
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
// endregion