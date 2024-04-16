package ru.filimonov.hpa.widgets

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.ui.widgets.R

@Composable
fun HpaScaffold(
    contentAlignment: Alignment = Alignment.TopStart,
    snackbarHost: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar,
    ) {
        Box(
            contentAlignment = contentAlignment,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun HpaScaffold(
    contentAlignment: Alignment = Alignment.TopStart,
    snackbarHost: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    @StringRes title: Int? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    HpaScaffold(
        contentAlignment = contentAlignment,
        snackbarHost = snackbarHost,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = content,
        modifier = modifier,
        topBar = {
            if (title != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(title)
                )
            }
        }
    )
}

@Composable
@Preview
fun HpaScaffoldPreview() {
    MaterialTheme {
        HpaScaffold(title = R.string.title) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = ru.filimonov.hpa.ui.common.R.drawable.settings),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
