package com.example.gradka.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gradka.AppViewModel
import com.example.gradka.domain.Address
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectDragListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

private const val ADDRESS_PICKER_TITLE = "Выберите адрес на карте"
private const val ADDRESS_PICKER_SUBTITLE = "Перетащите пин или воспользуйтесь поиском"

@SuppressLint("MissingPermission")
@Composable
fun AddressScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onAddManual: () -> Unit,
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val addresses by vm.addresses.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var suggests by remember { mutableStateOf(listOf<com.example.gradka.domain.AddressSuggestion>()) }
    var showSuggests by remember { mutableStateOf(false) }
    var detectedAddress by remember { mutableStateOf<String?>(null) }
    var detectedCity by remember { mutableStateOf<String?>(null) }
    var apt by remember { mutableStateOf("") }
    var entrance by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var intercom by remember { mutableStateOf("") }
    var isGpsLoading by remember { mutableStateOf(false) }
    var confirmed by remember { mutableStateOf(false) }
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val mapCenter = remember { mutableStateOf(Point(55.771, 37.582)) }
    var mapRef by remember { mutableStateOf<MapView?>(null) }
    var pinRef by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    val selectedAddress = detectedAddress?.takeIf { it.isNotBlank() }
        ?: query.trim().takeIf { it.isNotBlank() }
    val canConfirmAddress = selectedAddress != null

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isGpsLoading = true
            val client = LocationServices.getFusedLocationProviderClient(context)
            client.lastLocation.addOnSuccessListener { loc ->
                isGpsLoading = false
                if (loc != null) {
                    val pt = Point(loc.latitude, loc.longitude)
                    mapCenter.value = pt
                    pinRef?.geometry = pt
                    mapRef?.map?.move(
                        CameraPosition(pt, 16f, 0f, 0f),
                        Animation(Animation.Type.SMOOTH, 0.5f), null
                    )
                    scope.launch {
                        val address = vm.reverseGeocode(pt.latitude, pt.longitude)
                            .takeIf { it.isNotBlank() }
                        detectedAddress = address
                        detectedCity = if (address != null) "Москва" else null
                    }
                }
            }.addOnFailureListener { isGpsLoading = false }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 10.dp)
                        .statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, onClick = onBack,
                            ),
                        contentAlignment = Alignment.Center,
                    ) { BackIcon(tint = colors.ink) }
                    Text(
                        text = "Адрес доставки",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                        ),
                    )
                }
            }

            // Search bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)) {
                    BasicTextField(
                        value = query,
                        onValueChange = { v ->
                            query = v
                            debounceJob?.cancel()
                            if (v.length >= 2) {
                                debounceJob = scope.launch {
                                    delay(250)
                                    runCatching {
                                        suggests = vm.suggestAddresses(v)
                                        showSuggests = suggests.isNotEmpty()
                                    }
                                }
                            } else {
                                showSuggests = false
                                suggests = emptyList()
                            }
                        },
                        textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
                        cursorBrush = SolidColor(colors.ink),
                        singleLine = true,
                        decorationBox = { inner ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surface2)
                                    .border(1.5.dp, if (query.isNotEmpty()) colors.ink else Color.Transparent, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                SearchIcon(tint = colors.ink3)
                                Box(modifier = Modifier.weight(1f)) {
                                    if (query.isEmpty()) Text("Поиск адреса…", style = TextStyle(fontSize = 15.sp, color = colors.ink3))
                                    inner()
                                }
                                if (query.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(colors.ink3)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                            ) { query = ""; showSuggests = false },
                                        contentAlignment = Alignment.Center,
                                    ) { CloseIcon(tint = colors.bg, size = 10.dp) }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Suggest dropdown
            if (showSuggests) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .background(colors.surface)
                            .padding(vertical = 4.dp),
                    ) {
                        suggests.forEachIndexed { i, sug ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) {
                                        query = sug.fullText
                                        showSuggests = false
                                        val pt = Point(sug.lat, sug.lon)
                                        mapCenter.value = pt
                                        pinRef?.geometry = pt
                                        mapRef?.map?.move(
                                            CameraPosition(pt, 16f, 0f, 0f),
                                            Animation(Animation.Type.SMOOTH, 0.4f), null
                                        )
                                        detectedAddress = sug.title.takeIf { it.isNotBlank() }
                                        detectedCity = sug.subtitle.takeIf { it.isNotBlank() }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 13.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                PinIcon(tint = colors.ink3, size = 16.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sug.title, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                                    if (sug.subtitle.isNotEmpty()) {
                                        Text(sug.subtitle, style = TextStyle(fontSize = 12.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                                ChevronIcon(tint = colors.ink3)
                            }
                            if (i < suggests.size - 1) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line).padding(horizontal = 14.dp))
                            }
                        }
                    }
                }
            }

            // Map
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                ) {
                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).also { mv ->
                                mapRef = mv
                                mv.onStart()
                                val map = mv.map
                                map.move(CameraPosition(mapCenter.value, 15f, 0f, 0f))
                                val pin = map.mapObjects.addPlacemark(mapCenter.value)
                                pin.isDraggable = true
                                pinRef = pin
                                pin.setDragListener(object : MapObjectDragListener {
                                    override fun onMapObjectDragStart(p0: com.yandex.mapkit.map.MapObject) {}
                                    override fun onMapObjectDrag(p0: com.yandex.mapkit.map.MapObject, p1: Point) {}
                                    override fun onMapObjectDragEnd(p0: com.yandex.mapkit.map.MapObject) {
                                        val pt = (p0 as PlacemarkMapObject).geometry
                                        mapCenter.value = pt
                                        scope.launch {
                                            detectedAddress = vm.reverseGeocode(pt.latitude, pt.longitude)
                                                .takeIf { it.isNotBlank() }
                                            detectedCity = null
                                        }
                                    }
                                })
                                map.addInputListener(object : InputListener {
                                    override fun onMapTap(m: Map, point: Point) {
                                        pin.geometry = point
                                        mapCenter.value = point
                                        scope.launch {
                                            detectedAddress = vm.reverseGeocode(point.latitude, point.longitude)
                                                .takeIf { it.isNotBlank() }
                                            detectedCity = null
                                        }
                                    }
                                    override fun onMapLongTap(m: Map, point: Point) {}
                                })
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    // GPS button
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.surface)
                            .border(1.dp, colors.line, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    isGpsLoading = true
                                    val client = LocationServices.getFusedLocationProviderClient(context)
                                    client.lastLocation.addOnSuccessListener { loc ->
                                        isGpsLoading = false
                                        if (loc != null) {
                                            val pt = Point(loc.latitude, loc.longitude)
                                            mapCenter.value = pt
                                            pinRef?.geometry = pt
                                            mapRef?.map?.move(
                                                CameraPosition(pt, 16f, 0f, 0f),
                                                Animation(Animation.Type.SMOOTH, 0.5f), null
                                            )
                                            scope.launch {
                                                val address = vm.reverseGeocode(pt.latitude, pt.longitude)
                                                    .takeIf { it.isNotBlank() }
                                                detectedAddress = address
                                                detectedCity = if (address != null) "Москва" else null
                                            }
                                        }
                                    }.addOnFailureListener { isGpsLoading = false }
                                } else {
                                    locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        DeliveryIcon(tint = colors.accent, size = 20.dp)
                    }

                    // Hint chip
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xBF1A1A17))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                    ) {
                        Text("Перетащите пин", style = TextStyle(fontSize = 11.sp, color = Color.White))
                    }
                }
            }

            // Detected address card
            item {
                Column(modifier = Modifier.padding(10.dp).padding(horizontal = 6.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                    ) {
                        Column {
                            Text(
                                selectedAddress ?: ADDRESS_PICKER_TITLE,
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                            )
                            Text(
                                detectedCity ?: ADDRESS_PICKER_SUBTITLE,
                                style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }

                    Text(
                        text = "ДЕТАЛИ",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AddressDetailField("Квартира", apt, { apt = it }, colors, Modifier.weight(1f))
                        AddressDetailField("Подъезд", entrance, { entrance = it }, colors, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AddressDetailField("Этаж", floor, { floor = it }, colors, Modifier.weight(1f))
                        AddressDetailField("Домофон", intercom, { intercom = it }, colors, Modifier.weight(1f))
                    }

                    Text(
                        text = "МОИ АДРЕСА",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
                        modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
                    )
                }
            }

            items(addresses) { addr ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .border(1.dp, if (addr.primary) colors.ink else colors.line, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { vm.setPrimaryAddress(addr.id) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (addr.primary) colors.accentSoft else colors.surface2),
                        contentAlignment = Alignment.Center,
                    ) { PinIcon(tint = if (addr.primary) colors.accent else colors.ink, size = 18.dp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(addr.label, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                            if (addr.primary) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(colors.ink)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text("ОСНОВНОЙ", style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.bg))
                                }
                            }
                        }
                        Text(addr.text, style = TextStyle(fontSize = 12.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colors.surface2)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { vm.deleteAddress(addr.id) },
                        contentAlignment = Alignment.Center,
                    ) { CloseIcon(tint = colors.ink3, size = 14.dp) }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(width = 1.dp, color = colors.line2, shape = RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onAddManual,
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PlusIcon(tint = colors.ink, size = 16.dp)
                        Text("Добавить адрес", style = TextStyle(fontSize = 14.sp, color = colors.ink))
                    }
                }
                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Bottom CTA
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colors.bg)
                .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when {
                            confirmed -> colors.accentSoft
                            canConfirmAddress -> colors.ink
                            else -> colors.ink3.copy(alpha = 0.35f)
                        }
                    )
                    .clickable(
                        enabled = canConfirmAddress && !confirmed,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        val addressText = selectedAddress ?: return@clickable
                        if (!confirmed) {
                            confirmed = true
                            val newAddr = Address(
                                id = UUID.randomUUID().toString(),
                                label = "Новый адрес",
                                text = buildString {
                                    append(addressText)
                                    if (apt.isNotBlank()) append(", кв. $apt")
                                },
                                note = buildString {
                                    if (entrance.isNotBlank()) append("Подъезд $entrance")
                                    if (floor.isNotBlank()) append(", этаж $floor")
                                },
                                primary = addresses.isEmpty(),
                            )
                            vm.addAddress(newAddr)
                            onSave()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (confirmed) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        CheckIcon(tint = colors.accentDeep, size = 18.dp)
                        Text("Адрес сохранён", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.accentDeep))
                    }
                } else {
                    Text(
                        "Подтвердить адрес",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.bg,
                        ),
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapRef?.onStop()
        }
    }
}

@Composable
private fun AddressDetailField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: AppColors,
    modifier: Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
        cursorBrush = SolidColor(colors.ink),
        singleLine = true,
        decorationBox = { inner ->
            Column(
                modifier = modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface2)
                    .padding(horizontal = 12.dp, vertical = 11.dp),
            ) {
                Text(label, style = TextStyle(fontSize = 10.sp, color = colors.ink3))
                Box(modifier = Modifier.padding(top = 4.dp)) {
                    if (value.isEmpty()) Text("—", style = TextStyle(fontSize = 14.sp, color = colors.ink3))
                    inner()
                }
            }
        },
        modifier = modifier,
    )
}
