package tech.briangardner.composeplayground

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.core.sp
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacing
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.res.imageResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Tweet()
            }
        }
    }
}

@Composable
fun Tweet() {
    Column {
        UserInfoRow(name = "Brian Gardner", handle = "@BrianGardnerDev", time = 1)
        TweetContent(content = "This is a test tweet to see how things get rendered in the preview")
        ActionRow()
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
fun ActionRow() {
    Row(
        modifier = Spacing(8.dp)
    ) {
        Comment()
        Retweet()
        Like()
        Share()
    }
}

@Composable
fun Comment() {
    val icon = imageResource(R.drawable.ic_comment)
    Button("Comment")
}

@Composable
fun Retweet() {
    val icon = imageResource(R.drawable.ic_retweet)
    Button("Retweet")
}

@Composable
fun Like() {
    Button("Like")
}

@Composable
fun Share() {
    val icon = imageResource(R.drawable.ic_share)
    Button("Share")
}
// endregion

@Preview
@Composable
fun TwitterPreview() {
    MaterialTheme {
        Tweet()
    }
}
