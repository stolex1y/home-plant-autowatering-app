package ru.filimonov.hpa.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.ui.widgets.R

@Composable
fun HpaScaffold(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    snackbarHost: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
        snackbarHost = snackbarHost,
        modifier = Modifier
            .consumeWindowInsets(WindowInsets.navigationBars)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HpaScaffold(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    snackbarHost: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    title: String? = null,
    onNavigateUp: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit = {},
) {
    HpaScaffold(
        contentAlignment = contentAlignment,
        snackbarHost = snackbarHost,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = content,
        modifier = modifier,
        topBar = {
            if (title != null || actions != null || onNavigateUp != null) {
                val titleText = title ?: ""
                CenterAlignedTopAppBar(
                    modifier = Modifier.absolutePadding(left = 8.dp, right = 8.dp),
                    title = {
                        Text(
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = titleText
                        )
                    },
                    navigationIcon = {
                        if (onNavigateUp != null) {
                            IconButton(
                                modifier = Modifier.absolutePadding(0.dp),
                                onClick = onNavigateUp
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.back),
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    actions = {
                        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                            actions?.invoke(this)
                        }
                    }
                )
            }
        }
    )
}

@Composable
@Preview(showSystemUi = true)
private fun HpaScaffoldWithTitlePreview() {
    MaterialTheme {
        HpaScaffold(
            title = stringResource(id = R.string.title),
            modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars),

            ) {
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

@Composable
@Preview(showSystemUi = true)
private fun HpaScaffoldWithTopBarPreview() {
    MaterialTheme {
        HpaScaffold(
            title = stringResource(id = R.string.title),
            onNavigateUp = {},
            actions = {
                HpaActionButton(
                    contentDescription = 0,
                    onClick = {},
                    icon = ru.filimonov.hpa.ui.common.R.drawable.settings
                )
                HpaActionButton(
                    contentDescription = 0,
                    onClick = {},
                    icon = ru.filimonov.hpa.ui.common.R.drawable.settings
                )
            }
        ) {
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
