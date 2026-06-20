package com.example

import android.os.Bundle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.ChannelRepository
import com.example.ui.ChannelViewModel
import com.example.ui.ChannelViewModelFactory
import com.example.ui.screens.MainContainer

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF), // Pure white accent/highlights
    onPrimary = Color(0xFF0F1115),
    secondary = Color(0xFFE2E8F0),
    background = Color(0xFF0F1115),
    surface = Color(0xFF161920),
    onBackground = Color(0xFFECEFF4),
    onSurface = Color(0xFFECEFF4),
    outline = Color(0xFF232834)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E293B),
    onPrimary = Color.White,
    secondary = Color(0xFF475569),
    background = Color(0xFFF2F4F7),
    surface = Color.White,
    onBackground = Color(0xFF2E3440),
    onSurface = Color(0xFF2E3440),
    outline = Color(0xFFD8DEE9)
)

@Composable
fun KurdTVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ChannelRepository(database.channelDao())
        val factory = ChannelViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[ChannelViewModel::class.java]

        setContent {
            KurdTVTheme(darkTheme = true) { // Enforce dark theme for TV streams for cozy viewing
                MainContainer(viewModel = viewModel)
            }
        }
    }
}
