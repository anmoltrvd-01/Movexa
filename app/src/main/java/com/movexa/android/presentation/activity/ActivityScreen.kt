package com.movexa.android.presentation.activity

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.movexa.android.domain.model.ActivitySession.MapType
import com.movexa.android.domain.model.ActivityType
import com.movexa.android.domain.model.TrackingState
import com.movexa.android.ui.theme.MovexaMotion
import com.movexa.android.ui.theme.MovexaSpacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val session by viewModel.session.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    var showLocationRationale by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val coarseGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (!fineGranted && !coarseGranted) {
            // Permission denied logic
        }
    }

    if (showLocationRationale) {
        LocationRationaleDialog(
            onDismiss = { showLocationRationale = false },
            onConfirm = {
                showLocationRationale = false
                locationPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!hasFine) showLocationRationale = true
    }

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 280.dp,
        sheetShape = MaterialTheme.shapes.large,
        sheetContainerColor = colorScheme.surfaceContainerHigh,
        sheetDragHandle = {
            Box(
                Modifier
                    .padding(vertical = 16.dp)
                    .width(32.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(colorScheme.outlineVariant)
            )
        },
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MovexaSpacing.lg)
                    .padding(bottom = 40.dp)
            ) {
                // ── Activity Selection (M3 Expressive Button Group Style) ──
                AnimatedVisibility(visible = session.state == TrackingState.IDLE) {
                    Column {
                        Text("SELECT WORKOUT", style = MaterialTheme.typography.labelMedium, color = colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(MovexaSpacing.md))
                        ActivityTypeRow(session.type, viewModel::selectType)
                        Spacer(Modifier.height(MovexaSpacing.lg))
                    }
                }

                // ── Editorial Stat Moment: Live Stats ──
                if (session.state != TrackingState.IDLE) {
                    LiveStatsRow(session)
                    Spacer(Modifier.height(MovexaSpacing.lg))
                }

                // ── High-Touch Target Controls ──
                ControlButtons(session, viewModel)
                
                Spacer(Modifier.height(MovexaSpacing.xxl))
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            ActivityMap(session = session)

            // ── Floating Navigation Header ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(
                        onClick = { /* Back */ },
                        modifier = Modifier.size(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                    Spacer(Modifier.width(MovexaSpacing.md))
                    Surface(
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                        tonalElevation = 2.dp
                    ) {
                        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Search, null, tint = colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(MovexaSpacing.md))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
                                singleLine = true,
                                decorationBox = { inner ->
                                    if (searchQuery.isEmpty()) Text("Where to?", color = colorScheme.onSurfaceVariant)
                                    inner()
                                }
                            )
                        }
                    }
                }
            }

            // ── Floating Map Actions ──
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(MovexaSpacing.md)
            ) {
                MapActionFab(Icons.Rounded.Layers) { viewModel.cycleMapType() }
                MapActionFab(if (session.isFollowMode) Icons.Default.Navigation else Icons.Default.Explore) {
                    viewModel.toggleFollowMode()
                }
            }
        }
    }
}

@Composable
private fun LiveStatsRow(session: com.movexa.android.domain.model.ActivitySession) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        EditorialStat("DISTANCE", "%.2f".format(session.distanceKm), "KM", Modifier.weight(1f))
        EditorialStat("SPEED", "%.1f".format(session.currentSpeedKmh), "KM/H", Modifier.weight(1f))
        EditorialStat("PACE", session.paceFormatted, "/KM", Modifier.weight(1f))
    }
}

@Composable
private fun EditorialStat(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
        Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ControlButtons(session: com.movexa.android.domain.model.ActivitySession, viewModel: ActivityViewModel) {
    val colorScheme = MaterialTheme.colorScheme
    
    AnimatedContent(targetState = session.state, label = "controls") { state ->
        when (state) {
            TrackingState.IDLE -> {
                Button(
                    onClick = { viewModel.startActivity() },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text("START WORKOUT", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            TrackingState.ACTIVE -> {
                Row(horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.md)) {
                    FilledTonalButton(
                        onClick = { viewModel.pauseActivity() },
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Default.Pause, null)
                        Spacer(Modifier.width(8.dp))
                        Text("PAUSE")
                    }
                    Button(
                        onClick = { viewModel.stopActivity() },
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                    ) {
                        Icon(Icons.Default.Stop, null)
                        Spacer(Modifier.width(8.dp))
                        Text("FINISH")
                    }
                }
            }
            // ... add PAUSED and FINISHED states with similar M3 styling ...
            else -> { /* Fallback */ }
        }
    }
}

@Composable
private fun MapActionFab(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = MaterialTheme.shapes.medium,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
        )
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ActivityTypeRow(selected: ActivityType, onSelect: (ActivityType) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(MovexaSpacing.sm)) {
        items(ActivityType.entries.toTypedArray()) { type ->
            val isSelected = type == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(type) },
                label = { Text(type.label) },
                leadingIcon = { Text(type.emoji) },
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
private fun ActivityMap(session: com.movexa.android.domain.model.ActivitySession) {
    val colorScheme = MaterialTheme.colorScheme
    AndroidView(
        factory = { ctx ->
            TouchAwareMapView(ctx).apply {
                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                controller.setZoom(17.0)
            }
        },
        update = { map ->
            // Update route polyline with tertiary color (§6.6)
            if (session.routePoints.size >= 2) {
                val polyline = org.osmdroid.views.overlay.Polyline().apply {
                    setPoints(session.routePoints.map { org.osmdroid.util.GeoPoint(it.latitude, it.longitude) })
                    outlinePaint.color = "#64B5F6".toColorInt() // tertiary
                    outlinePaint.strokeWidth = 14f
                    outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                }
                map.overlays.add(polyline)
            }
            // ... add custom markers as per §6.6 ...
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun LocationRationaleDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Precision Tracking") },
        text = { Text("Movexa requires precise location to draw your route and calculate accurate pace.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Continue") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Later") } },
        shape = MaterialTheme.shapes.large
    )
}
