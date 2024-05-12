package ru.filimonov.hpa.widgets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import java.net.URI

@Composable
fun HpaImagePicker(
    modifier: Modifier = Modifier,
    onPickImage: (URI) -> Unit,
    painter: Painter,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    onPickImage(URI(uri.toString()))
                }
            }
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = modifier.clickable {
                launcher.launch("image/*")
            },
        )
    }
}
