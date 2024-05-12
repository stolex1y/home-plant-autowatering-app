package ru.filimonov.hpa.widgets

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.max

@Composable
fun HpaTextWithCaption(
    modifier: Modifier = Modifier,
    caption: String,
    text: String?,
    default: String = "—",
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    val fullText = "$caption: ${text ?: default}"
    Text(
        modifier = modifier,
        text = fullText,
        style = style,
        minLines = minLines,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
