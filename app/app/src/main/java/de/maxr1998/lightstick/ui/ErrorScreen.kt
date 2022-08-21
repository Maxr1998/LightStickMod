package de.maxr1998.lightstick.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.maxr1998.lightstick.R
import de.maxr1998.lightstick.ui.model.ErrorState

@Preview
@Composable
fun ErrorScreen(error: ErrorState = ErrorState.NONE) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Text(
                text = stringResource(R.string.error_header),
                style = MaterialTheme.typography.h1,
            )
            Text(
                text = stringResource(R.string.error_message, error.name),
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}