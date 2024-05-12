package ru.filimonov.hpa.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

@Composable
fun HpaCard(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .then(modifier)
    ) {
        Column(
            Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            title()
            content()
        }
    }
}

@Composable
fun HpaCardWithPhoto(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    painter: Painter,
    photoModifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    HpaCard(
        modifier = modifier,
        title = {
            Text(
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = titleStyle,
                textAlign = TextAlign.Center,
                text = title
            )
        },
        content = {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.FillHeight,
                modifier = modifier
                    .then(photoModifier)
                    .clip(MaterialTheme.shapes.medium),
            )
        },
    )
}

@Composable
fun HpaCardWithText(
    modifier: Modifier = Modifier,
    title: String,
    content: String?,
    defaultContent: String = "—",
    minTitleLines: Int = 1,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    contentStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    HpaCard(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                minLines = minTitleLines,
                overflow = TextOverflow.Ellipsis,
                style = titleStyle,
                text = title,
                textAlign = TextAlign.Center,
            )
        },
        content = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center, style = contentStyle, text = content ?: defaultContent
            )
        },
    )
}

@Composable
@Preview
private fun HpaCardPreview() {
    MaterialTheme {
        HpaCard(
            title = { Text(text = "Название карточки") },
            content = { Text(text = "Содержимое карточки") }
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun HpaCardWithPhotoPreview() {
    MaterialTheme {
        HpaCardWithPhoto(
            modifier = Modifier.height(150.dp),
            title = "Название карточки",
            painter = painterResource(id = ru.filimonov.hpa.ui.common.R.drawable.edit)
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun HpaCardWithTextPreview() {
    MaterialTheme {
        HpaCardWithText(
            title = "Название карточки",
            content = "Содержимое карточки"
        )
    }
}
