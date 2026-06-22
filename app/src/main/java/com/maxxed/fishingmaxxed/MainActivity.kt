package com.maxxed.fishingmaxxed

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val prefs = remember { getSharedPreferences("fishing_theme", Context.MODE_PRIVATE) }
            var mode by rememberSaveable { mutableStateOf(runCatching { FishingThemeMode.valueOf(prefs.getString("fishing_mode", FishingThemeMode.MASCULINE.name)!!) }.getOrDefault(FishingThemeMode.MASCULINE)) }
            LaunchedEffect(mode) { prefs.edit().putString("fishing_mode", mode.name).apply() }
            FishingTheme(mode) { FishingScreen(mode) { mode = it } }
        }
    }
}

private enum class FishingThemeMode { MASCULINE, FEMININE }

@Composable
private fun FishingTheme(mode: FishingThemeMode, content: @Composable () -> Unit) {
    val scheme = if (mode == FishingThemeMode.MASCULINE) {
        androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF4AC2C8),
            secondary = Color(0xFFEF6D62),
            background = Color(0xFF101519),
            surface = Color(0xFF182028),
            onSurface = Color(0xFFF7FAFC),
            onSurfaceVariant = Color(0xFFB6C2CC)
        )
    } else {
        androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF4AC2C8),
            secondary = Color(0xFFF17C8A),
            background = Color(0xFF15141A),
            surface = Color(0xFF201B24),
            onSurface = Color(0xFFF7FAFC),
            onSurfaceVariant = Color(0xFFD1C0CE)
        )
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
private fun FishingScreen(mode: FishingThemeMode, onModeChange: (FishingThemeMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("FISHING MAXXED", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text("Private catch journal", style = MaterialTheme.typography.headlineMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemePill("Masc", mode == FishingThemeMode.MASCULINE) { onModeChange(FishingThemeMode.MASCULINE) }
                ThemePill("Fem", mode == FishingThemeMode.FEMININE) { onModeChange(FishingThemeMode.FEMININE) }
            }
        }
        FishingHero(mode)
        FishingStatusRow()
        FishingJournalCard()
    }
}

@Composable
private fun ThemePill(text: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun FishingHero(mode: FishingThemeMode) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Catch capture", style = MaterialTheme.typography.titleLarge)
                Notice("Release required", MaterialTheme.colorScheme.secondary)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.78f)
                    .background(if (mode == FishingThemeMode.MASCULINE) Color(0xFF102028) else Color(0xFF261D28), RoundedCornerShape(8.dp))
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    drawRect(Color(0x332E6E77))
                    drawLine(Color(0xFF4AC2C8), Offset(size.width * 0.18f, size.height * 0.55f), Offset(size.width * 0.82f, size.height * 0.48f), 8f)
                    drawCircle(Color(0xFF4AC2C8), 18f, Offset(size.width * 0.18f, size.height * 0.55f))
                    drawCircle(Color(0xFF4AC2C8), 18f, Offset(size.width * 0.82f, size.height * 0.48f))
                    drawOval(if (mode == FishingThemeMode.MASCULINE) Color(0x664AC2C8) else Color(0x66F17C8A), topLeft = Offset(size.width * 0.24f, size.height * 0.32f), size = androidx.compose.ui.geometry.Size(size.width * 0.46f, size.height * 0.22f))
                    drawRect(Color(0x88FFFFFF), topLeft = Offset(size.width * 0.08f, size.height * 0.18f), size = androidx.compose.ui.geometry.Size(size.width * 0.13f, size.height * 0.3f), style = Stroke(width = 5f))
                }
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("21.75 in", color = Color.White, style = MaterialTheme.typography.headlineLarge)
                    Text("Confidence medium  |  Uncertainty ±0.5 in", color = Color(0xFFD8E2EA))
                    Text("Prototype rules - verify with the official agency", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FishingStatusRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        StatusCard(Modifier.weight(1f), "Species", "Suggested bass")
        StatusCard(Modifier.weight(1f), "Region", "CA Demo Zone")
        StatusCard(Modifier.weight(1f), "Privacy", "Coords hidden")
    }
}

@Composable
private fun StatusCard(modifier: Modifier, label: String, value: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp)) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(value)
        }
    }
}

@Composable
private fun FishingJournalCard() {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("My Leaderboard", style = MaterialTheme.typography.titleLarge)
            JournalEntry("Released", "Spotted bass", "21.75 in", MaterialTheme.colorScheme.secondary)
            JournalEntry("Catch", "Trout", "15.25 in", MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun JournalEntry(state: String, species: String, length: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(species)
            Text(length, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Notice(state, accent)
    }
}

@Composable
private fun Notice(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.16f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, fontSize = 12.sp)
    }
}

@Preview(device = Devices.PIXEL_4)
@Composable
private fun FishingMascPreview() {
    FishingTheme(FishingThemeMode.MASCULINE) { FishingScreen(FishingThemeMode.MASCULINE) {} }
}

@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun FishingFemPreview() {
    FishingTheme(FishingThemeMode.FEMININE) { FishingScreen(FishingThemeMode.FEMININE) {} }
}
