package com.example.gradka.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.domain.PRODUCTS
import com.example.gradka.domain.Product
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AddListScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
) {
    val colors = LocalAppColors.current

    var title by remember { mutableStateOf("") }
    var itemInput by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(listOf<String>()) }
    var titleFocused by remember { mutableStateOf(false) }
    var itemFocused by remember { mutableStateOf(false) }

    val titleFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(200)
        runCatching { titleFocusRequester.requestFocus() }
    }

    val suggestions: List<Product> = remember(itemInput) {
        if (itemInput.isBlank()) emptyList()
        else PRODUCTS
            .filter {
                it.name.contains(itemInput, ignoreCase = true) ||
                    it.subtitle.contains(itemInput, ignoreCase = true)
            }
            .take(5)
    }

    val showSuggestions = itemFocused && itemInput.isNotBlank()

    fun addItem(name: String) {
        if (name.isNotBlank() && !items.contains(name)) {
            items = items + name.trim()
        }
        itemInput = ""
    }

    fun hueForItem(name: String): Float? =
        PRODUCTS.find { it.name.equals(name, ignoreCase = true) }?.hue

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bg)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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
                Column {
                    Text(
                        text = "Новый список",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.02).em,
                            color = colors.ink,
                        ),
                    )
                    Text(
                        text = "Добавьте название и продукты",
                        style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 120.dp),
            ) {

                // ── НАЗВАНИЕ ──
                Text(
                    text = "НАЗВАНИЕ",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.08.em,
                        color = colors.ink3,
                    ),
                    modifier = Modifier.padding(bottom = 6.dp),
                )
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
                    cursorBrush = SolidColor(colors.ink),
                    singleLine = true,
                    decorationBox = { inner ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface)
                                .border(
                                    1.5.dp,
                                    if (titleFocused) colors.ink else colors.line,
                                    RoundedCornerShape(12.dp),
                                )
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (title.isEmpty()) {
                                    Text(
                                        text = "Например: Еженедельная закупка",
                                        style = TextStyle(fontSize = 15.sp, color = colors.ink3),
                                    )
                                }
                                inner()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleFocusRequester)
                        .onFocusChanged { titleFocused = it.isFocused },
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── ПРОДУКТЫ header ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "ПРОДУКТЫ",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em,
                            color = colors.ink3,
                        ),
                    )
                    if (items.isNotEmpty()) {
                        Text(
                            text = "${items.size} позиций",
                            style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                        )
                    }
                }

                // ── Search input ──
                BasicTextField(
                    value = itemInput,
                    onValueChange = { itemInput = it },
                    textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
                    cursorBrush = SolidColor(colors.ink),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addItem(itemInput) }),
                    decorationBox = { inner ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface)
                                .border(
                                    1.5.dp,
                                    if (itemFocused) colors.ink else colors.line,
                                    RoundedCornerShape(12.dp),
                                )
                                .padding(start = 12.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            SearchIcon(tint = colors.ink3, size = 18.dp)
                            Box(modifier = Modifier.weight(1f)) {
                                if (itemInput.isEmpty()) {
                                    Text(
                                        text = "Найти или добавить продукт...",
                                        style = TextStyle(fontSize = 15.sp, color = colors.ink3),
                                    )
                                }
                                inner()
                            }
                            AnimatedVisibility(
                                visible = itemInput.isNotBlank(),
                                enter = fadeIn(tween(100)),
                                exit = fadeOut(tween(100)),
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    // Clear
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(colors.surface2)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                            ) { itemInput = "" },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CloseIcon(tint = colors.ink3, size = 12.dp)
                                    }
                                    // Add custom
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(colors.ink)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                            ) { addItem(itemInput) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        PlusIcon(tint = colors.bg, size = 18.dp)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { itemFocused = it.isFocused },
                )

                // ── Suggestions dropdown ──
                AnimatedVisibility(
                    visible = showSuggestions && suggestions.isNotEmpty(),
                    enter = fadeIn(tween(120)),
                    exit = fadeOut(tween(100)),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .background(colors.surface),
                    ) {
                        suggestions.forEachIndexed { index, product ->
                            val alreadyAdded = items.contains(product.name)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { if (!alreadyAdded) addItem(product.name) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                ProductPlaceholder(hue = product.hue, size = 40.dp, label = "")
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (alreadyAdded) colors.ink3 else colors.ink,
                                        ),
                                    )
                                    Text(
                                        text = "${product.subtitle} · ${product.unit}",
                                        style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                                        modifier = Modifier.padding(top = 1.dp),
                                    )
                                }
                                if (alreadyAdded) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(colors.accentSoft),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CheckIcon(tint = colors.accent, size = 16.dp)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(colors.surface2),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        PlusIcon(tint = colors.ink, size = 16.dp)
                                    }
                                }
                            }
                            if (index < suggestions.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(colors.line),
                                )
                            }
                        }
                    }
                }

                // ── Items list ──
                if (items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .background(colors.surface),
                    ) {
                        items.forEachIndexed { index, item ->
                            val productHue = hueForItem(item)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (productHue != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(hueColor(productHue, 0.28f, 0.93f)),
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colors.surface2),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(colors.ink3),
                                        )
                                    }
                                }
                                Text(
                                    text = item,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.ink,
                                    ),
                                    modifier = Modifier.weight(1f),
                                )
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(colors.surface2)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                        ) {
                                            items = items.toMutableList().also { list ->
                                                list.removeAt(index)
                                            }
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CloseIcon(tint = colors.ink3, size = 14.dp)
                                }
                            }
                            if (index < items.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(colors.line),
                                )
                            }
                        }
                    }
                }

                // ── Empty hint ──
                if (items.isEmpty() && !showSuggestions) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentSoft)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "🥬", style = TextStyle(fontSize = 16.sp))
                        Text(
                            text = "Начните вводить название — мы подберём из каталога",
                            style = TextStyle(fontSize = 12.sp, color = colors.accentDeep),
                        )
                    }
                }
            }

            // ── Bottom CTA ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bg)
                    .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
            ) {
                val canSave = title.isNotBlank()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (canSave) colors.ink else colors.surface2)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            if (canSave) {
                                vm.addNote(title.trim(), items.joinToString("\n"))
                                onBack()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (canSave) CheckIcon(tint = colors.bg, size = 18.dp)
                        Text(
                            text = if (items.isEmpty()) "Создать пустой список"
                            else "Создать список · ${items.size}",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (canSave) colors.bg else colors.ink3,
                            ),
                        )
                    }
                }
            }
        }
    }
}
