package ru.filimonov.hpa.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.filimonov.hpa.ui.widgets.R

@Composable
fun HpaFilledTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes text: Int,
    enabled: Boolean = true,
) {
    Button(
        enabled = enabled,
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
fun HpaOutlinedTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes text: Int,
    enabled: Boolean = true
) {
    OutlinedButton(
        enabled = enabled,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        ),
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
fun HpaActionButton(
    modifier: Modifier = Modifier,
    @StringRes contentDescription: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
    @DrawableRes icon: Int,
) {
    val iconAlpha = if (enabled)
        Modifier
    else
        Modifier.alpha(0.7f)
    IconButton(enabled = enabled, modifier = modifier, onClick = onClick) {
        Icon(
            modifier = Modifier
                .then(iconAlpha)
                .size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = contentDescription)
        )
    }
}

@Composable
fun HpaCircularIconButton(
    modifier: Modifier = Modifier,
    @StringRes contentDescription: Int? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    @DrawableRes icon: Int,
) {
    val iconAlpha = if (enabled)
        Modifier
    else
        Modifier.alpha(0.7f)
    Button(
        shape = CircleShape,
        modifier = iconAlpha.then(modifier),
        onClick = onClick,
        contentPadding = PaddingValues(16.dp),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = contentDescription?.run { stringResource(id = this) }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaCircularIconButtonPreview() {
    MaterialTheme {
        HpaCircularIconButton(
            onClick = {},
            contentDescription = 0,
            enabled = true,
            icon = ru.filimonov.hpa.ui.common.R.drawable.settings
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaActionButtonPreview() {
    MaterialTheme {
        HpaScaffold(
            title = "Название",
            actions = {
                HpaActionButton(
                    contentDescription = R.string.button,
                    onClick = { },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.settings
                )
                HpaActionButton(
                    contentDescription = R.string.button,
                    onClick = { },
                    icon = ru.filimonov.hpa.ui.common.R.drawable.settings
                )
            })
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaFilledTextButtonPreview() {
    MaterialTheme {
        HpaFilledTextButton(enabled = true, onClick = {}, text = R.string.button)
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaDisabledFilledTextButtonPreview() {
    MaterialTheme {
        HpaFilledTextButton(enabled = false, onClick = {}, text = R.string.button)
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaOutlinedTextButtonPreview() {
    MaterialTheme {
        HpaOutlinedTextButton(enabled = true, onClick = {}, text = R.string.button)
    }
}

@Composable
@Preview(showBackground = true)
private fun HpaDisabledOutlinedTextButtonPreview() {
    MaterialTheme {
        HpaOutlinedTextButton(enabled = false, onClick = {}, text = R.string.button)
    }
}
