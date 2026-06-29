package com.maxxed.fishingmaxxed

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.hypot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { FishingApp() }
    }
}

private enum class AppTheme { MASCULINE, FEMININE }
private enum class Page { CAPTURE, JOURNAL, RULES, LEADERBOARD, ABOUT }
private val Page.label: String get() = name.lowercase().replaceFirstChar { it.uppercase() }

@Composable
private fun FishingApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("fishing_settings", Context.MODE_PRIVATE) }
    var theme by rememberSaveable { mutableStateOf(runCatching { AppTheme.valueOf(prefs.getString("theme", "MASCULINE")!!) }.getOrDefault(AppTheme.MASCULINE)) }
    var page by rememberSaveable { mutableStateOf(Page.CAPTURE) }
    val store = remember { JournalStore(context) }
    var records by remember { mutableStateOf(store.load()) }
    val colors = if (theme == AppTheme.MASCULINE) darkColorScheme(primary = Color(0xFF55C2C3), secondary = Color(0xFFFF8377), background = Color(0xFF0E171B), surface = Color(0xFF17242A))
    else darkColorScheme(primary = Color(0xFFE58EB4), secondary = Color(0xFF65C7C2), background = Color(0xFF19131A), surface = Color(0xFF281E28))
    MaterialTheme(colorScheme = colors) {
        Scaffold(
            topBar = { Header(theme) { theme = it; prefs.edit().putString("theme", it.name).apply() } },
            bottomBar = { NavigationBar { Page.entries.forEach { item -> NavigationBarItem(selected = page == item, onClick = { page = item }, icon = { Icon(pageIcon(item), item.name) }, label = { Text(item.label) }) } } }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (page) {
                    Page.CAPTURE -> CaptureScreen(records) { record -> records = listOf(record) + records; store.save(records) }
                    Page.JOURNAL -> JournalScreen(records, onDelete = { id -> records = records.filterNot { it.id == id }; store.save(records) }, onUpdate = { changed -> records = records.map { if (it.id == changed.id) changed else it }; store.save(records) })
                    Page.RULES -> RulesScreen()
                    Page.LEADERBOARD -> LeaderboardScreen(records)
                    Page.ABOUT -> AboutScreen()
                }
            }
        }
    }
}

private fun pageIcon(page: Page) = when (page) {
    Page.CAPTURE -> Icons.Default.PhotoCamera
    Page.JOURNAL -> Icons.Default.Book
    Page.RULES -> Icons.Default.Gavel
    Page.LEADERBOARD -> Icons.Default.EmojiEvents
    Page.ABOUT -> Icons.Default.Info
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun Header(theme: AppTheme, onTheme: (AppTheme) -> Unit) {
    TopAppBar(title = { Column { Text("Fishing Maxxed"); Text("Private, on-device catch journal", style = MaterialTheme.typography.labelSmall) } }, actions = {
        TextButton(onClick = { onTheme(if (theme == AppTheme.MASCULINE) AppTheme.FEMININE else AppTheme.MASCULINE) }) { Text(if (theme == AppTheme.MASCULINE) "Masculine" else "Feminine") }
    })
}

@Composable
private fun CaptureScreen(existing: List<CatchRecord>, onSave: (CatchRecord) -> Unit) {
    val context = LocalContext.current
    var cameraAllowed by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { cameraAllowed = it }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var referenceLength by rememberSaveable { mutableStateOf("6.00") }
    var fishA by remember { mutableStateOf(Point(.18f, .52f)) }; var fishB by remember { mutableStateOf(Point(.82f, .52f)) }
    var refA by remember { mutableStateOf(Point(.18f, .78f)) }; var refB by remember { mutableStateOf(Point(.42f, .78f)) }
    val photoMeasurement = MeasurementCalculator.calculate(MeasurementInput(fishA, fishB, refA, refB, referenceLength.toDoubleOrNull() ?: 0.0))
    var query by rememberSaveable { mutableStateOf("") }; var selected by remember { mutableStateOf<Species?>(null) }
    var notes by rememberSaveable { mutableStateOf("") }; var method by rememberSaveable { mutableStateOf("rod and reel") }
    var weight by rememberSaveable { mutableStateOf("") }; var manualLength by rememberSaveable { mutableStateOf("") }
    val manualMeasurement = manualLength.toDoubleOrNull()?.takeIf { it > 0.0 }?.let { Measurement(it, 0.5, Confidence.LOW) }
    val measurement = if (photoFile != null) photoMeasurement else manualMeasurement
    var region by rememberSaveable { mutableStateOf("Sacramento Valley") }; var status by rememberSaveable { mutableStateOf(CatchStatus.LOCAL_CATCH) }
    var latitude by remember { mutableStateOf<Double?>(null) }; var longitude by remember { mutableStateOf<Double?>(null) }
    var boundaryAmbiguous by remember { mutableStateOf(false) }
    var locationAllowed by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) }
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        locationAllowed = result.values.any { it }; if (locationAllowed) lastLocation(context)?.let { location -> latitude = location.first; longitude = location.second; RegionResolver.resolve(location.first, location.second).let { match -> boundaryAmbiguous = match.ambiguous; match.region?.let { region = it } } }
    }
    LaunchedEffect(locationAllowed) { if (locationAllowed && latitude == null) lastLocation(context)?.let { location -> latitude = location.first; longitude = location.second; RegionResolver.resolve(location.first, location.second).let { match -> boundaryAmbiguous = match.ambiguous; match.region?.let { region = it } } } }
    val rule = BundledRules.engine.evaluate(RuleRequest(region.takeIf { latitude != null }, selected?.id, LocalDate.now(), measurement?.length, method, existing.count { it.status == CatchStatus.KEEPER }, boundaryAmbiguous))
    LaunchedEffect(rule.decision) { if (!rule.maySetKeeper && status == CatchStatus.KEEPER) status = CatchStatus.LOCAL_CATCH }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Capture and measure", style = MaterialTheme.typography.headlineSmall)
        Text("Place a known-size reference beside the fish in the same plane. Drag both endpoint pairs after capture.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!cameraAllowed) PermissionPanel("Camera access is needed only to photograph a catch.", { cameraPermission.launch(Manifest.permission.CAMERA) }, { openSettings(context) })
        else if (photoFile == null) {
            CameraPreview { imageCapture = it }
            Button(onClick = {
                val file = File(context.filesDir.resolve("catch_photos").apply { mkdirs() }, "catch-${System.currentTimeMillis()}.jpg")
                val capture = imageCapture ?: return@Button
                capture.takePicture(ImageCapture.OutputFileOptions.Builder(file).build(), ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) { photoFile = file; message = null }
                    override fun onError(exception: ImageCaptureException) { message = "Capture failed: ${exception.message}" }
                })
            }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(8.dp)); Text("Take photo") }
        } else {
            MeasurementEditor(photoFile!!, fishA, fishB, refA, refB) { index, point -> when(index) { 0 -> fishA = point; 1 -> fishB = point; 2 -> refA = point; else -> refB = point } }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(referenceLength, { referenceLength = it }, label = { Text("Reference inches") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { photoFile?.delete(); photoFile = null }, modifier = Modifier.align(Alignment.CenterVertically)) { Text("Retake") }
            }
            if (measurement == null) Text("Measurement unavailable: spread both handle pairs and enter a valid reference.", color = MaterialTheme.colorScheme.error)
            else Text("${measurement.length.inchesLabel()} | ${measurement.confidence.label} confidence | expected uncertainty +/- ${measurement.uncertainty.inchesLabel()}", style = MaterialTheme.typography.titleMedium)
        }
        ManualLengthField(photoFile == null, manualLength) { manualLength = it }
        HorizontalDivider()
        Text("Confirm species", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(query, { query = it; selected = null }, label = { Text("Search common or scientific name") }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth())
        SpeciesCatalog.search(query).take(5).forEach { species ->
            ListItem(headlineContent = { Text(species.commonName) }, supportingContent = { Text(species.subtitle) }, trailingContent = { RadioButton(selected == species, { selected = species; query = species.commonName }) }, modifier = Modifier.clickable { selected = species; query = species.commonName })
        }
        Text(if (selected == null) "No species confirmed. Record will be Unverified." else "Confirmed by user: ${selected!!.commonName}", color = if (selected == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        OutlinedTextField(weight, { weight = it }, label = { Text("Optional weight, pounds") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(method, { method = it.lowercase() }, label = { Text("Method or gear") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(region, { region = it }, label = { Text("Broad public region") }, modifier = Modifier.fillMaxWidth())
        if (!locationAllowed) OutlinedButton(onClick = { locationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }) { Icon(Icons.Default.LocationOn, null); Text(" Save private location") }
        else Text(if (latitude == null) "Location permission granted; no last known fix available." else "Exact coordinates stored privately and excluded from default export.")
        Text(rule.decision.label, color = if (rule.decision == RuleDecision.RELEASE_REQUIRED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.titleMedium)
        Text(rule.summary)
        Text("Location classification: ${rule.rule?.origin?.label ?: "Unknown"}")
        Column { CatchStatus.entries.chunked(2).forEach { choices -> Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { choices.forEach { option ->
            FilterChip(selected = status == option, onClick = { status = option }, enabled = option != CatchStatus.KEEPER || rule.maySetKeeper, label = { Text(option.label) })
        } } } }
        val canSave = photoFile != null || manualMeasurement != null || selected != null || notes.isNotBlank()
        Button(onClick = {
            val finalStatus = if (selected == null || measurement == null) CatchStatus.UNVERIFIED else status
            val record = CatchRecord(speciesId = selected?.id, speciesName = selected?.commonName ?: "Unknown species", speciesConfirmed = selected != null,
                lengthInches = measurement?.length, uncertaintyInches = measurement?.uncertainty, confidence = measurement?.confidence, weightPounds = weight.toDoubleOrNull(),
                notes = notes, method = method, status = finalStatus, photoPath = photoFile?.absolutePath, latitude = latitude, longitude = longitude,
                publicRegion = region.ifBlank { "Region withheld" }, origin = rule.rule?.origin ?: Origin.UNKNOWN, ruleDecision = rule.decision, ruleSummary = rule.summary)
            onSave(record); photoFile = null; selected = null; query = ""; notes = ""; weight = ""; manualLength = ""; message = "Catch saved locally."
        }, enabled = canSave, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Save catch") }
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

@Composable private fun PermissionPanel(text: String, request: () -> Unit, settings: () -> Unit) {
    Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(text); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button(onClick = request) { Text("Allow") }; OutlinedButton(onClick = settings) { Text("Settings") } } } }
}

@Composable private fun ManualLengthField(enabled: Boolean, value: String, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            enabled = enabled,
            label = { Text("Manual length, inches") },
            supportingText = { Text(if (enabled) "Optional fallback when you cannot capture a usable measurement photo." else "Photo measurement is used for this catch.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable private fun CameraPreview(onReady: (ImageCapture) -> Unit) {
    val context = LocalContext.current; val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.ui.viewinterop.AndroidView(factory = { ctx ->
        PreviewView(ctx).also { view ->
            val future = ProcessCameraProvider.getInstance(ctx)
            future.addListener({
                val provider = future.get(); val capture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(view.surfaceProvider) }
                runCatching { provider.unbindAll(); provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture); onReady(capture) }
            }, ContextCompat.getMainExecutor(ctx))
        }
    }, modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f))
}

@Composable private fun MeasurementEditor(file: File, fishA: Point, fishB: Point, refA: Point, refB: Point, onMove: (Int, Point) -> Unit) {
    val bitmap = remember(file.path) { BitmapFactory.decodeFile(file.path)?.asImageBitmap() }
    Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f).background(Color.Black)) {
        bitmap?.let { Image(it, null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit) }
        Canvas(Modifier.fillMaxSize().pointerInput(fishA, fishB, refA, refB) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(); val change = event.changes.firstOrNull() ?: continue
                    if (change.pressed) {
                        val normalized = Point((change.position.x / size.width).coerceIn(0f, 1f), (change.position.y / size.height).coerceIn(0f, 1f))
                        val points = listOf(fishA, fishB, refA, refB)
                        val nearest = points.indices.minBy { i -> hypot(points[i].x - normalized.x, points[i].y - normalized.y) }
                        onMove(nearest, normalized); change.consume()
                    }
                }
            }
        }) {
            fun offset(p: Point) = Offset(p.x * size.width, p.y * size.height)
            drawLine(Color(0xFF55E2E3), offset(fishA), offset(fishB), 6f); drawLine(Color.Yellow, offset(refA), offset(refB), 6f)
            listOf(fishA, fishB).forEach { drawCircle(Color(0xFF55E2E3), 18f, offset(it), style = Stroke(7f)) }
            listOf(refA, refB).forEach { drawCircle(Color.Yellow, 18f, offset(it), style = Stroke(7f)) }
        }
    }
    Text("Cyan: fish axis. Yellow: known reference.", style = MaterialTheme.typography.labelMedium)
}

@Composable private fun JournalScreen(records: List<CatchRecord>, onDelete: (String) -> Unit, onUpdate: (CatchRecord) -> Unit) {
    val context = LocalContext.current; var filter by rememberSaveable { mutableStateOf("") }
    var statusFilter by rememberSaveable { mutableStateOf<CatchStatus?>(null) }
    val shown = records.filter { record ->
        val matchesText = filter.isBlank() || record.speciesName.contains(filter, true) || record.status.name.contains(filter, true)
        val matchesStatus = statusFilter == null || record.status == statusFilter
        matchesText && matchesStatus
    }
    val summary = CatchAnalytics.summarize(records)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Catch journal", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f)); IconButton(onClick = { shareCsv(context, JournalStore.exportCsv(records)) }) { Icon(Icons.Default.Share, "Export") } }
        Text("Default export uses broad region and never includes exact coordinates.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("Catches", summary.total.toString(), Modifier.weight(1f))
            StatCard("Measured", summary.measured.toString(), Modifier.weight(1f))
            StatCard("Released", summary.released.toString(), Modifier.weight(1f))
            StatCard("Check", summary.unverified.toString(), Modifier.weight(1f))
        }
        OutlinedTextField(filter, { filter = it }, label = { Text("Filter species or status") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            FilterChip(selected = statusFilter == null, onClick = { statusFilter = null }, label = { Text("All") })
            listOf(CatchStatus.LOCAL_CATCH, CatchStatus.RELEASED, CatchStatus.UNVERIFIED).forEach { choice ->
                FilterChip(selected = statusFilter == choice, onClick = { statusFilter = choice }, label = { Text(choice.label) })
            }
        }
        if (shown.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No catches yet") }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(shown, key = { it.id }) { record -> RecordCard(record, onDelete, onUpdate) } }
    }
}

@Composable private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable private fun RecordCard(record: CatchRecord, onDelete: (String) -> Unit, onUpdate: (CatchRecord) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var editedNotes by remember(record.id, record.notes) { mutableStateOf(record.notes) }
    var editedStatus by remember(record.id, record.status) { mutableStateOf(record.status) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(record.speciesName, style = MaterialTheme.typography.titleMedium); Text(record.status.label) }; Text(record.lengthInches?.inchesLabel() ?: "Not measured") }
        Text("${date(record.createdAt)} | ${record.publicRegion}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (expanded) {
            record.photoPath?.let { path -> remember(path) { BitmapFactory.decodeFile(path)?.asImageBitmap() }?.let { photo -> Image(photo, "Catch photo", Modifier.fillMaxWidth().heightIn(max = 260.dp), contentScale = ContentScale.Fit) } }
            OutlinedTextField(editedNotes, { editedNotes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf(CatchStatus.LOCAL_CATCH, CatchStatus.RELEASED, CatchStatus.UNVERIFIED).forEach { choice -> FilterChip(editedStatus == choice, { editedStatus = choice }, label = { Text(choice.label) }) } }
            Text("Classification: ${record.origin.label}"); Text(record.ruleDecision.label); Text(record.ruleSummary); Text("Exact coordinates: private", style = MaterialTheme.typography.labelMedium)
            Row { TextButton(onClick = { onUpdate(record.copy(notes = editedNotes, status = editedStatus)) }) { Icon(Icons.Default.Save, null); Text("Save changes") }; TextButton(onClick = { onDelete(record.id) }) { Icon(Icons.Default.Delete, null); Text("Delete") } }
        }
    } }
}

@Composable private fun RulesScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Regulation verification", style = MaterialTheme.typography.headlineSmall)
        Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Unable to verify - check official regulations", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary); Text("The bundled data is a limited demonstration dataset and is not authorized to approve Keeper status. It does not claim daily-current California or worldwide coverage.") } }
        Text("Evaluation inputs", style = MaterialTheme.typography.titleLarge)
        listOf("Coordinates and boundary ambiguity", "Species and user confirmation", "Catch date and season", "Measured size or slot", "Local bag history", "Method and gear", "Closures", "Dataset effective dates and support status").forEach { ListItem(headlineContent = { Text(it) }, leadingContent = { Icon(Icons.Default.CheckCircle, null) }) }
        Text("Source metadata", style = MaterialTheme.typography.titleLarge)
        Text("California Department of Fish and Wildlife regulations portal\nhttps://wildlife.ca.gov/Regulations\nDataset: bundled-demo-v1 | Checked: 2026-06-21 | Authorization: unsupported")
        Text("Always consult the official agency. App history may be incomplete and cannot establish your legal bag count.", color = MaterialTheme.colorScheme.error)
    }
}

@Composable private fun LeaderboardScreen(records: List<CatchRecord>) {
    val ranked = CatchAnalytics.localLeaderboard(records)
    val summary = CatchAnalytics.summarize(records)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("My Leaderboard", style = MaterialTheme.typography.headlineSmall); Text("Only records stored on this device. No global ranking.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (summary.measured > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("Measured", summary.measured.toString(), Modifier.weight(1f))
                StatCard("Best", summary.bestLengthInches?.inchesLabel() ?: "-", Modifier.weight(1f))
                StatCard("Average", summary.averageLengthInches?.inchesLabel() ?: "-", Modifier.weight(1f))
            }
        }
        if (ranked.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Measured catches will appear here.") }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { items(ranked.withIndex().toList()) { (index, record) ->
            ListItem(headlineContent = { Text("${index + 1}. ${record.speciesName}") }, supportingContent = { Text(record.status.label) }, trailingContent = { Text(record.lengthInches?.inchesLabel().orEmpty()) }, colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth())
        } }
    }
}

@Composable private fun AboutScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("About Fishing Maxxed", style = MaterialTheme.typography.headlineSmall)
        Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Private by default", style = MaterialTheme.typography.titleLarge)
            Text("Fishing Maxxed stores catches on this device. Default exports use broad region and do not include exact coordinates.")
        } }
        Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Measurement disclaimer", style = MaterialTheme.typography.titleLarge)
            Text("Photo measurements are estimates based on your reference object, camera angle, and handle placement. They are for personal tracking, not official weighing or tournament judging.")
        } }
        Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Regulation disclaimer", style = MaterialTheme.typography.titleLarge)
            Text("The bundled regulation screen is not authoritative and cannot approve Keeper status. Always check official agency regulations before keeping, releasing, or transporting fish.")
        } }
        Text("Support", style = MaterialTheme.typography.titleLarge)
        Text("Email: support@techmaxxed.com")
        Text("Privacy: https://techmaxxed.com/privacy")
        Text("Terms: https://techmaxxed.com/terms")
        Text("Version 1.0.0 | Offline-first release candidate", style = MaterialTheme.typography.labelMedium)
    }
}

private fun openSettings(context: Context) = context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
@SuppressLint("MissingPermission")
private fun lastLocation(context: Context): Pair<Double, Double>? = runCatching {
    val manager = context.getSystemService(LocationManager::class.java)
    listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).mapNotNull { provider -> runCatching { manager.getLastKnownLocation(provider) }.getOrNull() }.maxByOrNull { it.time }?.let { it.latitude to it.longitude }
}.getOrNull()
private fun shareCsv(context: Context, csv: String) { context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/csv"; putExtra(Intent.EXTRA_SUBJECT, "Fishing Maxxed catch journal"); putExtra(Intent.EXTRA_TEXT, csv) }, "Export private-safe journal")) }
private fun date(epoch: Long) = DateTimeFormatter.ofPattern("MMM d, yyyy").format(Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()))
