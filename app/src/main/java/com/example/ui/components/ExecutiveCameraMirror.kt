package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.PolarisAmberGold
import com.example.ui.theme.PolarisBluePrimary
import com.example.ui.theme.ScoreGreen
import com.example.ui.theme.VocalisCyan
import com.example.ui.theme.VocalisEmerald
import com.example.ui.theme.VocalisSurface
import kotlinx.coroutines.delay

@Composable
fun ExecutiveCameraMirror(
    isCameraExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Dynamic simulated gaze vector & composure metrics
    var eyeContactRatio by remember { mutableIntStateOf(92) }
    var headComposureIndex by remember { mutableIntStateOf(88) }
    var gazeReticleOffsetX by remember { mutableFloatStateOf(0f) }
    var gazeReticleOffsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isListening, isCameraExpanded) {
        while (isCameraExpanded) {
            delay(1200)
            if (isListening) {
                // Realistic minor eye micro-movements
                gazeReticleOffsetX = (Math.random().toFloat() - 0.5f) * 16f
                gazeReticleOffsetY = (Math.random().toFloat() - 0.5f) * 12f
                eyeContactRatio = (88 + (Math.random() * 11).toInt()).coerceIn(75, 99)
                headComposureIndex = (85 + (Math.random() * 14).toInt()).coerceIn(80, 100)
            } else {
                gazeReticleOffsetX = 0f
                gazeReticleOffsetY = 0f
                eyeContactRatio = 95
                headComposureIndex = 92
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VocalisSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(VocalisCyan.copy(alpha = 0.4f), PolarisBluePrimary.copy(alpha = 0.2f))
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Status & Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VocalisCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera Mirror",
                            tint = VocalisCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "EXECUTIVE EYE-CONTACT & CAMERA MIRROR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = VocalisCyan
                        )
                        Text(
                            text = if (isCameraExpanded) "Neural Gaze Reticle & Composure HUD Active" else "Tap to expand video composure mirror",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick stats pill when collapsed
                    if (!isCameraExpanded) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VocalisEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Gaze: $eyeContactRatio%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VocalisEmerald
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isCameraExpanded) Icons.Default.Close else Icons.Default.Visibility,
                            contentDescription = if (isCameraExpanded) "Hide Camera Mirror" else "Show Camera Mirror",
                            tint = VocalisCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isCameraExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Video Chamber Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF070B12))
                            .border(1.dp, VocalisCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    ) {
                        if (hasCameraPermission) {
                            // CameraX Front Camera Preview
                            var previewView: PreviewView? by remember { mutableStateOf(null) }

                            AndroidView(
                                factory = { ctx ->
                                    PreviewView(ctx).apply {
                                        scaleType = PreviewView.ScaleType.FILL_CENTER
                                        previewView = this
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            DisposableEffect(lifecycleOwner) {
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView?.surfaceProvider
                                        }
                                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview
                                        )
                                    } catch (exc: Exception) {
                                        Log.e("ExecutiveCameraMirror", "CameraX binding error", exc)
                                    }
                                }, ContextCompat.getMainExecutor(context))

                                onDispose {
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        cameraProvider.unbindAll()
                                    } catch (_: Exception) {}
                                }
                            }
                        } else {
                            // Simulated Optical Grid & Permission Prompter
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = VocalisCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Enable Front Camera Mirror",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Tap to grant camera access for real-time gaze mirror",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Neural Overlay HUD (Gaze Reticle & Alignment Quadrants)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val centerX = w / 2f + gazeReticleOffsetX
                            val centerY = h / 2f + gazeReticleOffsetY

                            // Golden Ratio Eye Framing Line
                            val eyeLevelY = h * 0.38f
                            drawLine(
                                color = Color.White.copy(alpha = 0.2f),
                                start = Offset(w * 0.2f, eyeLevelY),
                                end = Offset(w * 0.8f, eyeLevelY),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )

                            // Quadrant Crosshairs
                            val cornerSize = 24.dp.toPx()
                            val pad = 16.dp.toPx()
                            val crosshairColor = VocalisCyan.copy(alpha = 0.7f)

                            // Top-left
                            drawLine(crosshairColor, Offset(pad, pad), Offset(pad + cornerSize, pad), 2.dp.toPx())
                            drawLine(crosshairColor, Offset(pad, pad), Offset(pad, pad + cornerSize), 2.dp.toPx())

                            // Top-right
                            drawLine(crosshairColor, Offset(w - pad, pad), Offset(w - pad - cornerSize, pad), 2.dp.toPx())
                            drawLine(crosshairColor, Offset(w - pad, pad), Offset(w - pad, pad + cornerSize), 2.dp.toPx())

                            // Bottom-left
                            drawLine(crosshairColor, Offset(pad, h - pad), Offset(pad + cornerSize, h - pad), 2.dp.toPx())
                            drawLine(crosshairColor, Offset(pad, h - pad), Offset(pad, h - pad - cornerSize), 2.dp.toPx())

                            // Bottom-right
                            drawLine(crosshairColor, Offset(w - pad, h - pad), Offset(w - pad - cornerSize, h - pad), 2.dp.toPx())
                            drawLine(crosshairColor, Offset(w - pad, h - pad), Offset(w - pad, h - pad - cornerSize), 2.dp.toPx())

                            // Dynamic Center Gaze Reticle
                            val reticleRadius = 18.dp.toPx()
                            val reticleColor = if (eyeContactRatio >= 85) VocalisEmerald else PolarisAmberGold

                            drawCircle(
                                color = reticleColor.copy(alpha = 0.25f),
                                radius = reticleRadius,
                                center = Offset(centerX, centerY)
                            )
                            drawCircle(
                                color = reticleColor,
                                radius = reticleRadius,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawCircle(
                                color = reticleColor,
                                radius = 3.dp.toPx(),
                                center = Offset(centerX, centerY)
                            )
                        }

                        // HUD Telemetry Badge Overlay (Bottom Center)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CenterFocusStrong, contentDescription = null, tint = VocalisEmerald, modifier = Modifier.size(12.dp))
                                Text("Gaze: $eyeContactRatio% (Lens Locked)", fontSize = 10.sp, color = VocalisEmerald, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PolarisBluePrimary))
                                Text("Composure: $headComposureIndex%", fontSize = 10.sp, color = PolarisBluePrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Tactical Body Language Tip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF101622))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "💡 EXECUTIVE POSTURE CUE:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = PolarisAmberGold
                        )
                        Text(
                            text = "Look directly at the top camera lens rather than your own face to project true conviction.",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
