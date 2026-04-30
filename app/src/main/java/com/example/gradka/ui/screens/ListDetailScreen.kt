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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gradka.AppViewModel
import com.example.gradka.data.PRODUCTS
import com.example.gradka.domain.Note
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun ListDetailScreen(
    noteId: Int,
    vm: AppViewModel,
    onBack: () -> Unit,
) {
    val colors = LocalAppColors.current
    val notes by vm.notes.collectAsState()
    val note = notes.find { it.id == noteId }

    // If note was deleted or not found — go back
    if (note == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val hue = ((note.id.toLong() * 137L) % 360L).toFloat()
        .let { if (it < 0f) it + 360f else it }

    // Local mutable state synced from note
    var items by remember(note.content) {
        mutableStateOf(
            note.content.lines().filter { it.isNotBlank() }.toMutableList()
        )
    }
    var checked by remember { mutableStateOf(setOf<Int>()) }
    var itemInput by remember { mutableStateOf("") }
    var inputFocused by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val suggestions: List<String> = remember(itemInput) {
        if (itemInput.isBlank()) emptyList()
        else PRODUCTS
            .filter {
                it.name.contains(itemInput, ignoreCase = true) &&
                    !items.contains(it.name)
            }
            .take(4)
            .map { it.name }
    }

    fun saveItems(newItems: List<String>) {
        items = newItems.toMutableList()
        vm.editNote(note.copy(content = newItems.joinToString("\n")))
    }

    fun addItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !items.contains(trimmed)) {
            saveItems(items + trimmed)
        }
        itemInput = ""
    }

    fun removeItem(index: Int) {
        val updated = items.toMutableList().also { it.removeAt(index) }
        saveItems(updated)
        checked = checked.filter { it != index }.map { if (it > index) it - 1 else it }.toSet()
    }

    // ── Delete dialog ──
    if (showDeleteDialog) {
        Dialog(
            onDismissRequest = { showDeleteDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.ink.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { showDeleteDialog = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(colors.surface)
                        .navigationBarsPadding()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.line2),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(colors.surface2),
                        contentAlignment = Alignment.Center,
                    ) {
                        TrashIcon(tint = colors.danger)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Удалить список?",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 20.sp,
                            fontWeight = FontWeight.Medium, color = colors.ink,
                        ),
                    )
                    Text(
                        text = "«${note.title}» будет удалён навсегда",
                        style = TextStyle(fontSize = 14.sp, color = colors.ink3, lineHeight = (14 * 1.4).sp),
                        modifier = Modifier.padding(top = 6.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.danger)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                vm.deleteNote(note.id)
                                onBack()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Удалить",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.bg),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surface2)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { showDeleteDialog = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Отмена",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                        )
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
        ) {

            // ── Header ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
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

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surface2)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { showDeleteDialog = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        TrashIcon(tint = colors.danger, size = 18.dp)
                    }
                }
            }

            // ── Title block ──
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 6.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(hueColor(hue, 0.28f, 0.93f)),
                    )
                    Column {
                        Text(
                            text = note.title,
                            style = TextStyle(
                                fontFamily = FrauncesFontFamily, fontSize = 24.sp,
                                fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em,
                                color = colors.ink,
                            ),
                        )
                        val doneCount = checked.size
                        val total = items.size
                        Text(
                            text = if (total == 0) "Пустой список"
                            else if (doneCount == 0) "$total товаров"
                            else "$doneCount из $total отмечено",
                            style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }

            // ── Items ──
            if (items.isEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentSoft)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "🥬", style = TextStyle(fontSize = 16.sp))
                        Text(
                            text = "Список пуст — добавьте первый товар",
                            style = TextStyle(fontSize = 13.sp, color = colors.accentDeep),
                        )
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(16.dp))
                            .background(colors.surface),
                    ) {
                        items.forEachIndexed { index, item ->
                            val isChecked = checked.contains(index)
                            val productHue = PRODUCTS.find {
                                it.name.equals(item, ignoreCase = true)
                            }?.hue

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) {
                                        checked = if (isChecked) checked - index else checked + index
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Checkbox
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isChecked) colors.accent else colors.bg,
                                        )
                                        .border(
                                            1.5.dp,
                                            if (isChecked) colors.accent else colors.line2,
                                            CircleShape,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (isChecked) CheckIcon(tint = colors.bg, size = 13.dp)
                                }

                                // Color chip if from catalog
                                if (productHue != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(hueColor(productHue, 0.28f, 0.93f)),
                                    )
                                }

                                Text(
                                    text = item,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = if (isChecked) colors.ink3 else colors.ink,
                                        textDecoration = if (isChecked) TextDecoration.LineThrough else null,
                                    ),
                                    modifier = Modifier.weight(1f),
                                )

                                // Delete item
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                        ) { removeItem(index) },
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
            }

            // ── Add item input ──
            item {
                Spacer(modifier = Modifier.height(12.dp))
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
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(colors.surface)
                                .border(
                                    1.5.dp,
                                    if (inputFocused) colors.ink else colors.line,
                                    RoundedCornerShape(13.dp),
                                )
                                .padding(start = 12.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            SearchIcon(tint = colors.ink3, size = 18.dp)
                            Box(modifier = Modifier.weight(1f)) {
                                if (itemInput.isEmpty()) {
                                    Text(
                                        text = "Добавить продукт...",
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
                                    ) { CloseIcon(tint = colors.ink3, size = 12.dp) }
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
                                    ) { PlusIcon(tint = colors.bg, size = 18.dp) }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { inputFocused = it.isFocused },
                )
            }

            // ── Suggestions ──
            if (inputFocused && suggestions.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .background(colors.surface),
                    ) {
                        suggestions.forEachIndexed { index, name ->
                            val product = PRODUCTS.find { it.name == name }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { addItem(name) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (product != null) {
                                    ProductPlaceholder(hue = product.hue, size = 38.dp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                                    )
                                    if (product != null) {
                                        Text(
                                            text = "${product.subtitle} · ${product.unit}",
                                            style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                                            modifier = Modifier.padding(top = 1.dp),
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colors.surface2),
                                    contentAlignment = Alignment.Center,
                                ) { PlusIcon(tint = colors.ink, size = 15.dp) }
                            }
                            if (index < suggestions.size - 1) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                            }
                        }
                    }
                }
            }

            // ── Clear checked ──
            if (checked.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, colors.line, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                val toRemove = checked.sortedDescending()
                                val updated = items.toMutableList()
                                toRemove.forEach { updated.removeAt(it) }
                                saveItems(updated)
                                checked = emptySet()
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CloseIcon(tint = colors.danger, size = 16.dp)
                            Text(
                                text = "Удалить отмеченные (${checked.size})",
                                style = TextStyle(fontSize = 14.sp, color = colors.danger),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── TrashIcon ──
@Composable
fun TrashIcon(tint: androidx.compose.ui.graphics.Color, size: androidx.compose.ui.unit.Dp = 22.dp) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(s * 0.208f, s * 0.292f)
            lineTo(s * 0.792f, s * 0.292f)
            lineTo(s * 0.708f, s * 0.833f)
            cubicTo(s * 0.708f, s * 0.875f, s * 0.667f, s * 0.875f, s * 0.667f, s * 0.875f)
            lineTo(s * 0.333f, s * 0.875f)
            cubicTo(s * 0.333f, s * 0.875f, s * 0.292f, s * 0.875f, s * 0.292f, s * 0.833f)
            close()
        }
        drawPath(
            path = path, color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.6.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
            ),
        )
        drawLine(tint, androidx.compose.ui.geometry.Offset(s * 0.125f, s * 0.292f), androidx.compose.ui.geometry.Offset(s * 0.875f, s * 0.292f), 1.6.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(tint, androidx.compose.ui.geometry.Offset(s * 0.375f, s * 0.125f), androidx.compose.ui.geometry.Offset(s * 0.625f, s * 0.125f), 1.6.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(tint, androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.417f), androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.75f), 1.6.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(tint, androidx.compose.ui.geometry.Offset(s * 0.375f, s * 0.417f), androidx.compose.ui.geometry.Offset(s * 0.35f, s * 0.75f), 1.6.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(tint, androidx.compose.ui.geometry.Offset(s * 0.625f, s * 0.417f), androidx.compose.ui.geometry.Offset(s * 0.65f, s * 0.75f), 1.6.dp.toPx(), androidx.compose.ui.graphics.StrokeCap.Round)
    }
}
