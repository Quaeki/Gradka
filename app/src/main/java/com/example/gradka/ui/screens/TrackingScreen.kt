package com.example.gradka.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// Fixed route: warehouse → delivery address
private val WAREHOUSE = Point(55.758, 37.642)
private val DELIVERY  = Point(55.771, 37.582)

private val ROUTE_POINTS = listOf(
    Point(55.758, 37.642),
    Point(55.761, 37.635),
    Point(55.764, 37.621),
    Point(55.767, 37.610),
    Point(55.769, 37.601),
    Point(55.771, 37.582),
)

@SuppressLint("UnrememberedMutableState")
@Composable
fun TrackingScreen(onBack: () -> Unit) {
    val colors = LocalAppColors.current

    var courierIndex by remember { mutableIntStateOf((ROUTE_POINTS.size * 0.3f).toInt()) }
    var etaMinutes by remember { mutableIntStateOf(31) }
    val progress by derivedStateOf { courierIndex.toFloat() / (ROUTE_POINTS.size - 1) }

    var mapRef by remember { mutableStateOf<MapView?>(null) }
    var courierPin by remember { mutableStateOf<PlacemarkMapObject?>(null) }

    // Animate courier along route
    LaunchedEffect(Unit) {
        while (isActive && courierIndex < ROUTE_POINTS.size - 1) {
            delay(800)
            courierIndex++
            courierPin?.geometry = ROUTE_POINTS[courierIndex]
            val remaining = ((1f - progress) * 31f).toInt().coerceAtLeast(0)
            etaMinutes = remaining
        }
    }

    val steps = listOf(
        Triple("Принят",   "19:02", 0),
        Triple("Собран",   "19:18", 1),
        Triple("В пути",   "19:24", 2),
        Triple("У дверей", "~19:55", 3),
    )
    val currentStep = 2

    Column(modifier = Modifier.fillMaxSize().background(colors.bg).verticalScroll(rememberScrollState())) {

        // Map area
        Box(modifier = Modifier.fillMaxWidth().height(330.dp)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).also { mv ->
                        mapRef = mv
                        mv.onStart()
                        val map = mv.map
                        map.isScrollGesturesEnabled = false
                        map.isZoomGesturesEnabled = false

                        // Fit route in view
                        val midLat = (WAREHOUSE.latitude + DELIVERY.latitude) / 2
                        val midLon = (WAREHOUSE.longitude + DELIVERY.longitude) / 2
                        map.move(CameraPosition(Point(midLat, midLon), 13f, 0f, 0f))

                        val objects: MapObjectCollection = map.mapObjects

                        // Route polyline
                        val polyline = Polyline(ROUTE_POINTS)
                        objects.addPolyline(polyline).apply {
                            setStrokeColor(android.graphics.Color.parseColor("#5A8848"))
                            strokeWidth = 4f
                            setDashLength(12f)
                            setGapLength(8f)
                        }

                        // Warehouse marker
                        objects.addPlacemark(WAREHOUSE)

                        // Destination marker
                        objects.addPlacemark(DELIVERY)

                        // Courier marker
                        val cp = objects.addPlacemark(ROUTE_POINTS[courierIndex])
                        courierPin = cp
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            // Back button overlay
            Box(
                modifier = Modifier
                    .padding(14.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onBack,
                    )
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center,
            ) { BackIcon(tint = colors.ink) }

            // ETA chip overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .statusBarsPadding()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ETA", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.06.em, color = colors.ink3))
                    Text(
                        "$etaMinutes мин",
                        style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                    )
                }
            }

            // Progress bar at bottom of map
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(colors.line2),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(colors.accent),
                )
            }
        }

        // Bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(colors.bg)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.line2)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "В ПУТИ",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.08.em, color = colors.accentDeep),
                    )
                    Text(
                        text = "Прибудет через $etaMinutes мин",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                        ),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text(
                    text = "~19:55",
                    style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = colors.accentDeep),
                )
            }

            // Courier card
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.accentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("АП", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.accentDeep))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Александр П.", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                    Text("Курьер · электровелосипед", style = TextStyle(fontSize = 11.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.accent),
                    contentAlignment = Alignment.Center,
                ) { PhoneCallIcon(tint = Color.White) }
            }

            // Timeline
            Column(modifier = Modifier.padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                steps.forEachIndexed { i, (title, time, step) ->
                    val isDone = step <= currentStep
                    val isActive = step == currentStep
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) colors.accent else if (isActive) colors.accentSoft else colors.surface)
                                    .border(2.dp, if (isDone || isActive) colors.accent else colors.line2, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isDone && !isActive) CheckIcon(tint = Color.White, size = 12.dp)
                                else if (isActive) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors.accent))
                                }
                            }
                            if (i < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(18.dp)
                                        .background(if (isDone) colors.accent else colors.line2),
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f).padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (step > currentStep) colors.ink3 else colors.ink,
                                    ),
                                )
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(colors.accentSoft)
                                            .padding(horizontal = 8.dp, vertical = 3.dp),
                                    ) {
                                        Text("СЕЙЧАС", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.accentDeep))
                                    }
                                }
                            }
                            Text(
                                text = time,
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp, color = colors.ink3),
                            )
                        }
                    }
                }
            }

            // Route info chips
            Row(
                modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(Triple("2.4 км", "РАССТОЯНИЕ", Modifier.weight(1f)),
                       Triple("18 км/ч", "СКОРОСТЬ", Modifier.weight(1f)),
                       Triple("#24809", "ЗАКАЗ", Modifier.weight(1f))).forEach { (v, l, mod) ->
                    Column(
                        modifier = mod
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surface2)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Text(v, style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.ink))
                        Text(l, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.06.em, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapRef?.onStop() }
    }
}
