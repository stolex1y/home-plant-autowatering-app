package ru.filimonov.hpa.widgets

import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HpaLoading(
    modifier: Modifier = Modifier.width(64.dp),
    strokeWidth: Dp = 6.dp
) {
    CircularProgressIndicator(
        modifier = modifier,
        strokeWidth = strokeWidth,
    )
}

@Composable
@Preview(showBackground = true)
private fun HpaLoadingPreview() {
    MaterialTheme {
        HpaLoading()
    }
}
