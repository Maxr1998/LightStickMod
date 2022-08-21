package de.maxr1998.lightstick.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import de.maxr1998.lightstick.MainViewModel
import de.maxr1998.lightstick.ui.model.ErrorState
import de.maxr1998.lightstick.ui.theme.LSCTheme

@Composable
fun ComposeApp() {
    LSCTheme {
        val systemUiController = rememberSystemUiController()

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.White,
                darkIcons = true,
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background,
        ) {
            AppContent()
        }
    }
}

@Composable
private fun AppContent(mainViewModel: MainViewModel = viewModel()) {
    val error by mainViewModel.error
    val connected by mainViewModel.connected
    when (error) {
        ErrorState.NONE -> if (connected) {
            ControlScreen()
        } else {
            ConnectScreen()
        }
        else -> ErrorScreen(error = error)
    }
}