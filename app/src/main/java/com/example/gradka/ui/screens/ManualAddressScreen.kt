package com.example.gradka.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.domain.Address
import com.example.gradka.domain.AddressSuggestion
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

private enum class AddressLabel(val emoji: String, val label: String) {
    HOME("🏠", "Дом"),
    WORK("💼", "Работа"),
    OTHER("📍", "Другое"),
}

@Composable
fun ManualAddressScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()

    var step by remember { mutableIntStateOf(1) }
    var streetQuery by remember { mutableStateOf("") }
    var selectedSuggestion by remember { mutableStateOf<AddressSuggestion?>(null) }
    var suggests by remember { mutableStateOf(listOf<AddressSuggestion>()) }
    var showSuggests by remember { mutableStateOf(false) }
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    var streetFocused by remember { mutableStateOf(false) }

    // Step 2 fields
    var selectedLabel by remember { mutableStateOf(AddressLabel.HOME) }
    var apt by remember { mutableStateOf("") }
    var entrance by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var intercom by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(step) {
        if (step == 1) {
            delay(100)
            runCatching { focusRequester.requestFocus() }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bg)
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp, bottom = 16.dp)
                    .statusBarsPadding(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (step == 2) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { step = 1 },
                            contentAlignment = Alignment.Center,
                        ) { BackIcon(tint = colors.ink) }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onBack,
                                ),
                            contentAlignment = Alignment.Center,
                        ) { BackIcon(tint = colors.ink) }
                    }
                    Column {
                        Text(
                            text = if (step == 1) "Новый адрес" else selectedSuggestion?.title ?: "Детали адреса",
                            style = TextStyle(
                                fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                                fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                            ),
                        )
                        if (step == 1) {
                            Text(
                                text = "Введите улицу и дом",
                                style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }

                // Step progress bar
                Row(
                    modifier = Modifier.padding(top = 14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(1, 2).forEach { s ->
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (s == step) colors.ink else colors.line2),
                            )
                            Text(
                                text = if (s == 1) "Адрес" else "Детали",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = if (s == step) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (s == step) colors.ink else colors.ink3,
                                ),
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }

            if (step == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "УЛИЦА И ДОМ",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    // Search input
                    BasicTextField(
                        value = streetQuery,
                        onValueChange = { v ->
                            streetQuery = v
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
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.surface)
                                    .border(1.5.dp, if (streetFocused) colors.ink else colors.line, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                SearchIcon(tint = colors.ink3)
                                Box(modifier = Modifier.weight(1f)) {
                                    if (streetQuery.isEmpty()) Text("Введите улицу и дом", style = TextStyle(fontSize = 15.sp, color = colors.ink3))
                                    inner()
                                }
                                if (streetQuery.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(colors.ink3)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                            ) { streetQuery = ""; showSuggests = false },
                                        contentAlignment = Alignment.Center,
                                    ) { CloseIcon(tint = colors.bg, size = 10.dp) }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { streetFocused = it.isFocused },
                    )

                    // Suggest dropdown
                    AnimatedVisibility(visible = showSuggests, enter = fadeIn(), exit = fadeOut()) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
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
                                            selectedSuggestion = sug
                                            streetQuery = sug.fullText
                                            showSuggests = false
                                            step = 2
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
                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                                }
                            }
                        }
                    }

                    // Or divider
                    Row(
                        modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.line))
                        Text("или", style = TextStyle(fontSize = 13.sp, color = colors.ink3))
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(colors.line))
                    }

                    // Enter manually button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                selectedSuggestion = AddressSuggestion(
                                    title = streetQuery.ifBlank { "Ввод вручную" },
                                    subtitle = "",
                                    fullText = streetQuery,
                                    lat = 55.771,
                                    lon = 37.582,
                                )
                                step = 2
                            }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.surface2),
                            contentAlignment = Alignment.Center,
                        ) { EditIcon(tint = colors.ink) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ввести вручную", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                            Text("Заполнить форму самостоятельно", style = TextStyle(fontSize = 12.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                        }
                        ChevronIcon(tint = colors.ink3)
                    }

                    // Recent addresses
                    val recentAddresses by vm.addresses.collectAsState()
                    if (recentAddresses.isNotEmpty()) {
                        Text(
                            text = "НЕДАВНИЕ АДРЕСА",
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
                            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                        )
                        recentAddresses.take(3).forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) {
                                        selectedSuggestion = AddressSuggestion(
                                            title = addr.text,
                                            subtitle = "",
                                            fullText = addr.text,
                                            lat = 55.771,
                                            lon = 37.582,
                                        )
                                        streetQuery = addr.text
                                        step = 2
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                PinIcon(tint = colors.ink3, size = 16.dp)
                                Text(addr.text, style = TextStyle(fontSize = 14.sp, color = colors.ink), modifier = Modifier.weight(1f))
                                ChevronIcon(tint = colors.ink3)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp).navigationBarsPadding())
                }
            }

            if (step == 2) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 100.dp),
                ) {
                    // Label picker
                    Text(
                        text = "ТИП АДРЕСА",
                        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AddressLabel.entries.forEach { lbl ->
                            val active = lbl == selectedLabel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (active) colors.ink else colors.surface)
                                    .border(1.dp, if (active) colors.ink else colors.line, RoundedCornerShape(12.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { selectedLabel = lbl },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(lbl.emoji, style = TextStyle(fontSize = 16.sp))
                                    Text(
                                        lbl.label,
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (active) colors.bg else colors.ink,
                                        ),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Street field
                    FormFieldLabel("УЛИЦА И ДОМ *", colors)
                    ManualFormField(
                        value = selectedSuggestion?.title ?: streetQuery,
                        onValueChange = {},
                        placeholder = "Улица и дом",
                        colors = colors,
                        readOnly = true,
                        leadingIcon = { PinIcon(tint = colors.ink3, size = 16.dp) },
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Apt + Entrance
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("КВАРТИРА", colors)
                            ManualFormField(
                                value = apt,
                                onValueChange = { apt = it },
                                placeholder = "Кв.",
                                colors = colors,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("ПОДЪЕЗД", colors)
                            ManualFormField(
                                value = entrance,
                                onValueChange = { entrance = it },
                                placeholder = "Подъезд",
                                colors = colors,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Floor + Intercom
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("ЭТАЖ", colors)
                            ManualFormField(
                                value = floor,
                                onValueChange = { floor = it },
                                placeholder = "Этаж",
                                colors = colors,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormFieldLabel("ДОМОФОН", colors)
                            ManualFormField(
                                value = intercom,
                                onValueChange = { intercom = it },
                                placeholder = "Код",
                                colors = colors,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comment
                    FormFieldLabel("КОММЕНТАРИЙ КУРЬЕРУ", colors)
                    var commentFocused by remember { mutableStateOf(false) }
                    BasicTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        textStyle = TextStyle(fontSize = 14.sp, color = colors.ink),
                        cursorBrush = SolidColor(colors.ink),
                        decorationBox = { inner ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surface)
                                    .border(1.5.dp, if (commentFocused) colors.ink else colors.line, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                            ) {
                                if (comment.isEmpty()) Text("Позвоните за 10 минут…", style = TextStyle(fontSize = 14.sp, color = colors.ink3))
                                inner()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { commentFocused = it.isFocused },
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hint bubble
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentSoft)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("🚴", style = TextStyle(fontSize = 16.sp))
                        Text(
                            "Курьер может позвонить или написать, когда будет рядом",
                            style = TextStyle(fontSize = 12.sp, color = colors.accentDeep),
                        )
                    }
                }

                // Bottom CTA
                Box(
                    modifier = Modifier
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
                            .background(colors.ink)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                val sug = selectedSuggestion
                                val addressText = (sug?.title ?: streetQuery).trim()
                                if (addressText.isBlank()) return@clickable

                                val newAddr = Address(
                                    id = UUID.randomUUID().toString(),
                                    label = selectedLabel.label,
                                    text = buildString {
                                        append(addressText)
                                        if (apt.isNotBlank()) append(", кв. $apt")
                                    },
                                    note = buildString {
                                        if (entrance.isNotBlank()) append("Подъезд $entrance")
                                        if (floor.isNotBlank()) append(", этаж $floor")
                                        if (comment.isNotBlank()) append(". $comment")
                                    },
                                    primary = false,
                                )
                                vm.addAddress(newAddr)
                                onSaved()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            CheckIcon(tint = colors.bg, size = 18.dp)
                            Text("Сохранить адрес", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormFieldLabel(label: String, colors: AppColors, error: Boolean = false) {
    Text(
        text = label,
        style = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.08.em,
            color = if (error) colors.danger else colors.ink3,
        ),
        modifier = Modifier.padding(bottom = 6.dp),
    )
}

@Composable
private fun ManualFormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    colors: AppColors,
    hasError: Boolean = false,
    readOnly: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val borderColor = when {
        hasError -> colors.danger
        focused -> colors.ink
        else -> colors.line
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
        cursorBrush = SolidColor(colors.ink),
        singleLine = true,
        decorationBox = { inner ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(placeholder, style = TextStyle(fontSize = 15.sp, color = colors.ink3))
                    inner()
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
    )
}
