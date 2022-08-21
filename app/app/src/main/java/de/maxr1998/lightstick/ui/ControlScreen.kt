package de.maxr1998.lightstick.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.maxr1998.lightstick.MainViewModel
import de.maxr1998.lightstick.R
import de.maxr1998.lightstick.ui.model.Effect
import de.maxr1998.lightstick.ui.model.MemberColor

@Composable
fun ControlScreen(mainViewModel: MainViewModel = viewModel()) {
    Box {
        LazyVerticalGrid(
            modifier = Modifier.align(Alignment.Center),
            columns = GridCells.Fixed(@Suppress("MagicNumber") 3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            userScrollEnabled = false,
        ) {
            items(MemberColor.values()) { memberColor ->
                ColorItem(color = memberColor.color) { color ->
                    mainViewModel.setColor(color)
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter),
        ) {
            items(Effect.values()) { effect ->
                EffectItem(effect = effect) {
                    mainViewModel.launchEffect(effect)
                }
            }
        }

        Text(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            text = stringResource(R.string.message_connected),
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
private fun ColorItem(
    color: Color,
    onClick: (Color) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .requiredSize(72.dp)
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .background(color)
            .clickable {
                onClick(color)
            },
    )
}

@Composable
private fun EffectItem(
    effect: Effect,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = Modifier
            .padding(16.dp)
            .requiredSize(56.dp)
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .background(Color.White),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(effect.icon),
            contentDescription = stringResource(R.string.effect_icon_desc, effect.effectName),
        )
    }
}